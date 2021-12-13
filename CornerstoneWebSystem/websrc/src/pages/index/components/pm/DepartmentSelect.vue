<template>
    <div
        class="project-department-select"
        :class="{
            border:border,
        }"
        v-click-outside="onClickOutside"
        @mouseover="mouseOver=true"
        @mouseout="mouseOver=false"
        @click="onClickSelect">
        <div class="placeholder" v-show="!Array.isArray(departmentList) || departmentList.length === 0">
            {{ placeholder }}
        </div>
        <div
            class="selection" :class="{multiple}" v-show="Array.isArray(departmentList) && departmentList.length > 0">
            <template v-if="departmentList.length < 2 ">
                <template v-for="item in departmentList">
                    <template v-if="multiple">
                        <Tag
                            :key="item.id" closable @on-close="onRemoveDepartment(item.id)">{{item.label}}
                        </Tag>
                    </template>
                    <template v-else>
                        {{item.label}}
                    </template>
                </template>
            </template>
            <template v-else>
                {{ $t('已选择部门',[departmentList[0].label,departmentList.length]) }}
            </template>
        </div>
        <template v-if="showClear">
            <Icon class="icon" type="ios-close-circle" @click.stop.native="onClickClear" />
        </template>
        <template v-else>
            <Icon class="icon" type="ios-arrow-down" />
        </template>
        <div class="project-department-select-wrap" v-show="treeShow">
            <treeselect
                v-model="treeSelectedDepartment"
                :options="list"
                :clearable="multiple"
                :clear-all-text="$t('清除所有')"
                :clear-value-text="$t('清除')"
                :no-results-text="$t('找不到匹配的部门')"
                :search-prompt-text="$t('搜索部门')"
                :placeholder="placeholder"
                no-options-text=""
                value-format="object"
                :match-keys='["pinyin","label"]'
                :show-count="true"
                :always-open="true"
                :close-on-select="true"
                :default-expand-level="defaultExpandLevel"
                :max-height="200"
                :disable-branch-nodes="multiple !== true"
                value-consists-of="LEAF_PRIORITY"
                :multiple="multiple">
                    <label slot="option-label" slot-scope="{ node, shouldShowCount, count, labelClassName, countClassName }" :class="labelClassName">
                        <Icon v-if="node.raw.type==1" type="ios-people" />
                        <Icon v-if="node.raw.type==2" type="md-person" /> 
                        {{ node.label }}
                    <span v-if="shouldShowCount" :class="countClassName">({{ count }})</span>
                </label>
            </treeselect>
        </div>
    </div>
</template>

<i18n>
    {
    "en": {
    "选择部门": "Choose department",
    "清除所有":"Clear all",
    "清除":"Clear",
    "找不到匹配的部门":"No matching",
    "搜索部门":"Search department",
    "选择部门":"Choose department",
    "未设置标签":"No labels set",
    "已选择部门":"{0}etc,({1})departments",
    "确定":"OK"
    },
    "zh_CN": {
    "选择部门": "选择部门",
    "清除所有":"清除所有",
    "清除":"清除",
    "找不到匹配的部门":"找不到匹配的部门",
    "搜索部门":"搜索部门",
    "选择部门":"选择部门",
    "未设置标签":"未设置标签",
    "已选择部门":"{0}等,({1})部门",
    "确定":"确定"
    }
    }
</i18n>
<script>
    import Treeselect from '@riophae/vue-treeselect';
    import '@riophae/vue-treeselect/dist/vue-treeselect.css';

    export default {
        name: 'ProjectDepartmentSelect',
        components: { Treeselect },
        directives: {
            clickOutside: {
                bind(el, binding) {
                    function documentHandler(e) {
                        if (el.contains(e.target)) {
                            return false;
                        }
                        if (binding.expression) {
                            binding.value(e);
                        }
                    }

                    el.__vueClickOutside__ = documentHandler;
                    document.addEventListener('click', documentHandler);
                },
                update() {

                },
                unbind(el) {
                    document.removeEventListener('click', el.__vueClickOutside__);
                    delete el.__vueClickOutside__;
                },
            },
        },
        props: {
            value: {
                type: [Number, Array],
            },
            placeholder: {
                type: String,
                default() {
                    return this.$t('选择部门');
                },
            },
            multiple: {
                type: Boolean,
                default: false,
            },
            border: {
                type: Boolean,
                default: true,
            },
            clearable: {
                type: Boolean,
                default: false,
            },
            list: {
                type: Array,
            },
        },
        data() {
            return {
                defaultExpandLevel: 0,
                mouseOver: false,
                treeShow: false,
                departmentList: [],
                treeSelectedDepartment: []
            };
        },
        watch: {
            value: {
                immediate: true,
                handler(value) {
                    if (!value || (Array.isArray(value) && value.length === 0)) {
                        this.treeSelectedDepartment = [];
                    }
                },
            },
            departmentList(val) {
                if (Array.isArray(val)) {
                    if (val.length > 0) {
                        const values = val.map(item => item.id);
                        if (this.multiple) {
                            this.$emit('input', values);
                        } else {
                            this.$emit('input', values[0]);
                        }
                    } else {
                        this.$emit('input', null);
                    }
                } else {
                    this.$emit('input', val);
                }
                setTimeout(() => {
                    this.$emit('on-change', val);
                });
            },
            // list: {
            //     immediate: true,
            //     handler(val) {
            //         this.handleList(val);
            //     },
            // },
            treeSelectedDepartment(val) {
                if (!val) {
                    this.departmentList = [];
                    return;
                }
                if (!this.multiple) {
                    this.departmentList = [{
                        id: val.id,
                        label: val.label,
                        level: val.level,
                    }];
                    return;
                }
                if (!Array.isArray(val) || val.length === 0) {
                    this.departmentList = [];
                    return;
                }
                const departmentList = [];
                val.forEach(item => {
                    if (item.type !== 1) {
                        return;
                    }
                    departmentList.push({
                        id: item.id,
                        label: item.label,
                        level: item.level,
                    });
                });
                this.departmentList = departmentList;
            },
        },
        computed: {
            showClear() {
                return this.clearable && this.mouseOver && Array.isArray(this.departmentList) && this.departmentList.length > 0;
            },
        },
        mounted() {
            this.compDepartmentList();
        },
        methods: {
            compDepartmentList() {
                if (!this.value || !Array.isArray(this.list) || this.list.length === 0) {
                    this.departmentList = [];
                    return;
                }
                let idsList = [];
                if (Array.isArray(this.value)) {
                    idsList = this.value;
                } else {
                    idsList = [this.value];
                }
                const departmentList = [];
                this.list.filter(item => idsList.indexOf(item.id) > -1).forEach(item => {
                    departmentList.push({
                        id: item.id,
                        label: item.label,
                    });
                });
                this.departmentList = departmentList;
            },
            onClickOutside() {
                this.treeShow = false;
            },
            handleList() {
                
            },
            onRemoveDepartment(accountId) {
                const index = this.departmentList.findIndex(item => item.id === accountId);
                if (index === -1) {
                    return;
                }
                this.departmentList.splice(index, 1);
                const accountIds = [];
                this.departmentList.forEach(item => {
                    accountIds.push(item.accountId);
                });
                this.modelValue = accountIds;
            },
            onClickSelect() {
                this.treeShow = true;
            },
            onClickClear() {
                this.departmentList = [];
                if (this.multiple) {
                    this.treeSelectedDepartment = null;
                } else {
                    this.treeSelectedDepartment = null;
                }
            },
        },
    };
</script>
<style lang="less" scoped>
    .project-department-select {
        position: relative;
        display: inline-block;
        width: 100%;
        color: #515a6e;
        font-size: 13px;
        cursor: pointer;
        background-color: #fff;
        outline: 0;
        -webkit-user-select: none;
        -moz-user-select: none;
        -ms-user-select: none;
        user-select: none;
        box-sizing: border-box;
        vertical-align: middle;

        .placeholder {
            display: block;
            height: 32px;
            line-height: 32px;
            font-size: 12px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            padding-left: 8px;
            padding-right: 24px;
            color: #c5c8ce;
        }

        .selection {
            position: relative;
            display: block;
            outline: 0;
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            user-select: none;
            cursor: pointer;
            background-color: #fff;
            min-height: 32px;
            max-height: 120px;
            overflow: auto;
            padding-top: 2px;
            padding-left: 4px;
            padding-right: 24px;

            &.multiple {
                padding-top: 2px;
            }
        }

        .icon {
            position: absolute;
            top: 50%;
            right: 8px;
            line-height: 1;
            transform: translateY(-50%);
            font-size: 14px;
            color: #808695;
        }

        &.border {
            .placeholder,
            .selection {
                border: 1px solid #dcdee2;
            }
        }

        &:hover {

            .placeholder,
            .selection {
                border-color: #57a3f3
            }
        }

        &-wrap {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            z-index: 101;

            /deep/ .vue-treeselect__control {
                height: 32px;
                border-radius: 0;

                .vue-treeselect__placeholder {
                    line-height: 32px;
                }

                .vue-treeselect__multi-value-item {
                    line-height: normal;
                }
            }

            /deep/ .vue-treeselect__menu-container {
                .vue-treeselect__menu {
                    &::-webkit-scrollbar {
                        width: 4px;
                        height: 6px;
                        background-color: transparent;
                        -ms-overflow-style: -ms-autohiding-scrollbar;
                    }

                    &::-webkit-scrollbar-track {
                        border-radius: 0;
                        background-color: transparent;
                    }

                    &::-webkit-scrollbar-thumb {
                        border-radius: 0;
                        background-color: #ddd;
                    }
                }
            }
        }
    }
</style>

