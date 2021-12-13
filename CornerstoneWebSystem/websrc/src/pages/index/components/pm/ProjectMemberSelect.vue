<template>
    <div
        class="project-member-select"
        :class="{
            border:border,
         }"
        v-click-outside="onClickOutside"
        @mouseover="mouseOver=true"
        @mouseout="mouseOver=false"
        @click="onClickUserSelect">
        <div class="placeholder" v-show="!Array.isArray(memberList) || memberList.length === 0">
            {{ placeholder }}
        </div>
        <div
            class="selection" :class="{multiple}" v-show="Array.isArray(memberList) && memberList.length > 0">
            <template v-if="memberList.length < 2 ">
                <template v-for="item in memberList">
                    <template v-if="multiple">
                        <Tag
                            :key="item.id" closable @on-close="onRemoveUser(item.id)">{{item.label}}
                        </Tag>
                    </template>
                    <template v-else>
                        {{item.label}}
                    </template>
                </template>
            </template>
            <template v-else>
                {{ $t('已选择成员',[memberList[0].label,memberList.length]) }}
            </template>
        </div>
        <template v-if="showClear">
            <Icon class="icon" type="ios-close-circle" @click.stop.native="onClickClear" />
        </template>
        <template v-else>
            <Icon class="icon" type="ios-arrow-down" />
        </template>
        <div class="project-member-select-wrap" v-show="treeShow">
            <treeselect
                v-model="treeSelectedMembers"
                :options="treeData"
                :clearable="multiple"
                :clear-all-text="$t('清除所有')"
                :clear-value-text="$t('清除')"
                :no-results-text="$t('找不到匹配的成员')"
                :search-prompt-text="$t('搜索成员')"
                :placeholder="placeholder"
                no-options-text=""
                value-format="object"
                :match-keys='["pinyinName","label","userName"]'
                :show-count="true"
                :always-open="true"
                :close-on-select="true"
                :default-expand-level="defaultExpandLevel"
                :max-height="200"
                :disable-branch-nodes="multiple !== true"
                value-consists-of="LEAF_PRIORITY"
                :multiple="multiple">
                <label
                    slot="option-label"
                    slot-scope="{ node, shouldShowCount, count, labelClassName, countClassName }"
                    :class="labelClassName">
                    <Icon v-if="node.raw.type===1" type="ios-people" />
                    <Icon v-if="node.raw.type===2" type="md-person" />
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
    "选择成员": "Choose member",
    "清除所有":"Clear all",
    "清除":"Clear",
    "找不到匹配的成员":"No matching",
    "搜索成员":"Search member",
    "选择成员":"Choose member",
    "未设置标签":"No labels set",
    "已选择成员":"{0}etc,({1})members",
    "确定":"OK"
    },
    "zh_CN": {
    "选择成员": "选择成员",
    "清除所有":"清除所有",
    "清除":"清除",
    "找不到匹配的成员":"找不到匹配的成员",
    "搜索成员":"搜索成员",
    "选择成员":"选择成员",
    "未设置标签":"未设置标签",
    "已选择成员":"{0}等,({1})成员",
    "确定":"确定"
    }
    }
</i18n>
<script>
    import Treeselect from '@riophae/vue-treeselect';
    import '@riophae/vue-treeselect/dist/vue-treeselect.css';

    export default {
        name: 'ProjectMemberSelect',
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
                    return this.$t('选择成员');
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
                memberList: [],
                treeSelectedMembers: [],
                treeData: [],
            };
        },
        watch: {
            value: {
                immediate: true,
                handler(value) {
                    if (!value || (Array.isArray(value) && value.length === 0)) {
                        this.treeSelectedMembers = [];
                    }
                },
            },
            memberList(val) {
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
            list: {
                immediate: true,
                handler(val) {
                    this.handleList(val);
                },
            },
            treeSelectedMembers(val) {
                if (!val) {
                    this.memberList = [];
                    return;
                }
                if (!this.multiple) {
                    this.memberList = [{
                        id: val.accountId,
                        label: val.label,
                        userName: val.userName,
                    }];
                    return;
                }
                if (!Array.isArray(val) || val.length === 0) {
                    this.memberList = [];
                    return;
                }
                const memberList = [];
                val.forEach(item => {
                    if (item.type !== 2) {
                        return;
                    }
                    memberList.push({
                        id: item.accountId,
                        label: item.label,
                        userName: item.userName,
                    });
                });
                this.memberList = memberList;
            },
        },
        computed: {
            showClear() {
                return this.clearable && this.mouseOver && Array.isArray(this.memberList) && this.memberList.length > 0;
            },
        },
        mounted() {
            this.compMemberList();
        },
        methods: {
            compMemberList() {
                if (!this.value || !Array.isArray(this.list) || this.list.length === 0) {
                    this.memberList = [];
                    return;
                }
                let idsList = [];
                if (Array.isArray(this.value)) {
                    idsList = this.value;
                } else {
                    idsList = [this.value];
                }
                const memberList = [];
                this.list.filter(item => idsList.indexOf(item.accountId) > -1).forEach(item => {
                    memberList.push({
                        id: item.accountId,
                        label: item.accountName,
                        userName: item.accountUserName,
                    });
                });
                this.memberList = memberList;
            },
            onClickOutside() {
                this.treeShow = false;
            },
            handleList(list) {
                const value = this.value || [];
                const treeSelectData = [];
                const treeData = [];
                let incId = 1;
                const defaultNode = {
                    children: [],
                    id: ++incId,
                    label: this.$t('未设置标签'),
                    title: this.$t('未设置标签'),
                    type: 1,
                };
                let tags = [];
                list.forEach(item => {
                    if (!Array.isArray(item.tag) || item.tag.length === 0) {
                        const node = {
                            accountId: item.accountId,
                            expand: true,
                            id: ++incId,
                            label: item.accountName,
                            pinyinName: item.accountPinyinName,
                            title: item.accountName,
                            type: 2,
                            userName: item.accountUserName,
                        };
                        defaultNode.children.push(node);
                        if (value.indexOf(node.accountId) > -1) {
                            treeSelectData.push(node);
                        }
                        return;
                    }
                    item.tag.forEach(tag => {
                        if (tags.indexOf(tag) > -1) {
                            return;
                        }
                        tags.push(tag);
                    });
                });
                tags.forEach((tag) => {
                    treeData.push({
                        children: [],
                        isDisabled: false,
                        expand: true,
                        id: ++incId,
                        label: tag,
                        title: tag,
                        type: 1,
                    });
                });
                treeData.forEach(treeNode => {
                    list.forEach(item => {
                        if (!Array.isArray(item.tag) || item.tag.length === 0) {
                            return;
                        }
                        if (item.tag.indexOf(treeNode.label) > -1) {
                            const node = {
                                accountId: item.accountId,
                                expand: true,
                                id: ++incId,
                                label: item.accountName,
                                pinyinName: item.accountPinyinName,
                                title: item.accountName,
                                type: 2,
                                userName: item.accountUserName,
                            };
                            treeNode.children.push(node);
                            if (value.indexOf(node.accountId) > -1) {
                                treeSelectData.push(node);
                            }
                        }
                    });
                });
                if (treeData.length === 0) {
                    this.defaultExpandLevel = 1;
                }
                treeData.unshift(defaultNode);
                this.treeSelectedMembers = treeSelectData;
                this.treeData = treeData;
            },
            onRemoveUser(accountId) {
                const index = this.memberList.findIndex(item => item.id === accountId);
                if (index === -1) {
                    return;
                }
                this.memberList.splice(index, 1);
                const accountIds = [];
                this.memberList.forEach(item => {
                    accountIds.push(item.accountId);
                });
                this.modelValue = accountIds;
            },
            onClickUserSelect() {
                this.treeShow = true;
            },
            onClickClear() {
                this.memberList = [];
                if (this.multiple) {
                    this.treeSelectedMembers = null;
                } else {
                    this.treeSelectedMembers = null;
                }
            },
        },
    };
</script>
<style lang="less" scoped>
    .project-member-select {
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

