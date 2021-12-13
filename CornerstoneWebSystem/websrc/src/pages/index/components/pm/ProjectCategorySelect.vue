<template>
    <div
        class="project-category-select" :class="{
            border:border,
         }" v-click-outside="onClickOutside" @click="onClickUserSelect">
        <div class="placeholder" v-show="!Array.isArray(modelList) || modelList.length === 0">
            {{ placeholder || $t('所有分类', [this.objectTypeName || '']) }}
        </div>
        <div
            class="selection" :class="{multiple}" v-show="Array.isArray(modelList) && modelList.length > 0">
            <template v-if="modelList.length < 2 ">
                <template v-for="item in modelList">
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
                {{ $t('已选择分类',[modelList[0].label,modelList.length]) }}
            </template>
        </div>
        <template v-if="clearable && Array.isArray(modelList) && modelList.length > 0">
            <Icon class="icon" type="ios-close-circle" @click.stop.native="onClickClear" />
        </template>
        <template v-else>
            <Icon class="icon" type="ios-arrow-down" />
        </template>
        <div class="project-category-select-wrap" v-show="treeShow">
            <treeselect
                v-model="treeSelectedList"
                :options="treeData"
                :clearable="multiple"
                :clear-all-text="$t('清除所有')"
                :clear-value-text="$t('清除')"
                :no-results-text="$t('找不到匹配的分类')"
                :search-prompt-text="$t('搜索分类')"
                :placeholder="placeholder"
                no-options-text=""
                value-format="object"
                :match-keys='["name","label"]'
                :show-count="true"
                :always-open="true"
                :flat="true"
                :close-on-select="true"
                :default-expand-level="1"
                :max-height="200"
                :disable-branch-nodes="multiple !== true"
                value-consists-of="LEAF_PRIORITY"
                :multiple="multiple"
                @select="onNodeSelect">
                <label
                    slot="option-label"
                    slot-scope="{ node, shouldShowCount, count, labelClassName, countClassName }"
                    :class="labelClassName">
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
    "分类":"categories",
    "所有分类":"All {0}",
    "未分类":"None {}",
    "选择分类": "Choose category",
    "清除所有":"Clear all",
    "清除":"Clear",
    "找不到匹配的分类":"No matching",
    "搜索分类":"Search category",
    "选择分类":"Choose category",
    "未设置标签":"No labels set",
    "已选择分类":"{0}etc,({1})categories",
    "确定":"OK"
    },
    "zh_CN": {
    "分类":"分类",
    "所有分类":"所有{0}",
    "未分类":"未分类{0}",
    "选择分类": "选择分类",
    "清除所有":"清除所有",
    "清除":"清除",
    "找不到匹配的分类":"找不到匹配的分类",
    "搜索分类":"搜索分类",
    "选择分类":"选择分类",
    "未设置标签":"未设置标签",
    "已选择分类":"{0}等,({1})分类",
    "确定":"确定"
    }
    }
</i18n>
<script>
    import Treeselect from '@riophae/vue-treeselect';
    import '@riophae/vue-treeselect/dist/vue-treeselect.css';

    export default {
        name: 'ProjectCategorySelect',
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
            projectId: {
                type: Number,
                required: true,
            },
            objectType: {
                type: Number,
                required: true,
            },
            objectTypeName: {
                type: String,
            },
            placeholder: {
                type: String,
            },
            isSetCategory: {
                type: Boolean,
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
        },
        data() {
            return {
                treeShow: false,
                modelList: [],
                treeSelectedList: [],
                treeData: [],
                categoryList: [],
                changeTimeoutId: 0,
            };
        },
        watch: {
            projectId() {
                this.loadCategoryNodeList();
            },
            objectType() {
                this.loadCategoryNodeList();
            },
            modelList(val) {
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
                this.emitChange();
            },
            treeSelectedList(val) {
                if (!val) {
                    this.modelList = [];
                    return;
                }
                if (Array.isArray(val)) {
                    if (val.length === 0) {
                        this.modelList = [];
                        return;
                    }
                } else {
                    if (!this.multiple) {
                        this.modelList = [{
                            id: val.id,
                            label: val.label,
                        }];
                        return;
                    }
                }
                const modelList = [];
                val.forEach(item => {
                    modelList.push({
                        id: item.id,
                        label: item.label,
                    });
                });
                this.modelList = modelList;
            },
        },
        mounted() {
            this.loadCategoryNodeList();
        },
        methods: {
            emitChange() {
                clearTimeout(this.changeTimeoutId);
                this.changeTimeoutId = setTimeout(() => {
                    this.$emit('on-change');
                }, 100);
            },
            loadCategoryNodeList() {
                this.treeSelectedList = [];
                this.modelList = [];
                if (!this.projectId || !this.objectType > 0) {
                    return;
                }
                app.invoke('BizAction.getCategoryNodeList', [app.token, this.projectId, this.objectType], list => {
                    this.handleCategoryList(list);
                    this.categoryList = list;
                    // this.compModelList();
                    this.handleList();
                    this.$emit('on-category-loaded', list);
                });
            },
            handleCategoryList(list) {
                if (!Array.isArray(list)) {
                    return;
                }
                list.forEach(item => {
                    item.label = item.name;
                    if (!Array.isArray(item.children) || item.children.length === 0) {
                        delete item.children;
                        return;
                    }
                    this.handleCategoryList(item.children);
                });
            },
            onNodeSelect(node) {
                if (node.id === 0) {
                    this.$nextTick(() => {
                        this.treeSelectedList = [node];
                    });
                    return;
                }
                this.$nextTick(() => {
                    this.treeSelectedList = this.treeSelectedList.filter(item => item.id !== 0);
                });
            },
            onClickOutside() {
                this.treeShow = false;
            },
            handleList() {
                const allNode = {
                    children: [],
                    id: -1,
                    name: this.$t('所有分类', [this.objectTypeName]),
                    label: this.$t('所有分类', [this.objectTypeName]),
                };
                allNode.children.push({
                    id: 0,
                    name: this.$t('未分类', [this.objectTypeName]),
                    label: this.$t('未分类', [this.objectTypeName]),
                });
                if (Array.isArray(this.categoryList)) {
                    this.categoryList.forEach(item => {
                        allNode.children.push(item);
                    });
                }
                this.treeData = [allNode];
            },
            onRemoveUser(accountId) {
                const index = this.modelList.findIndex(item => item.id === accountId);
                if (index === -1) {
                    return;
                }
                this.modelList.splice(index, 1);
                const accountIds = [];
                this.modelList.forEach(item => {
                    accountIds.push(item.accountId);
                });
                this.modelValue = accountIds;
            },
            onClickUserSelect() {
                this.treeShow = true;
            },
            onClickClear() {
                this.modelList = [];
                if (this.multiple) {
                    this.treeSelectedList = null;
                } else {
                    this.treeSelectedList = null;
                }
            },
        },
    };
</script>
<style lang="less" scoped>
    .project-category-select {
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

