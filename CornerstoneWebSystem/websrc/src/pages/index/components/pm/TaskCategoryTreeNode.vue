<style scoped>
    .tree-item-name {
        font-size: 13px;
        font-weight: bold;
        color: #808080;
        padding: 0 5px 0 15px;
        user-select: none;
        display: flex;
    }

    .tree-left {
        flex: 1;
    }

    .tree-right {
        width: 50px;
    }

    .tree-item-inner {
        height: 40px;
        display: flex;
        align-items: center;
        cursor: pointer;
    }

    .tree-item-label {
        flex: 1;
        margin-left: 5px;
    }

    .tree-item-children {
        padding-left: 20px;
    }

    .tree-item-name:hover {
        background-color: #f5f5f5;
    }

    .tree-opt {
        visibility: hidden;
        z-index: 999999;
        text-align: left;
    }

    .right-bar {
        float: right;
        text-align: right;
        padding-right: 0px;
    }

    .tree-item-name:hover .tree-opt {
        visibility: visible;
    }

    .opt-list {
        width: 150px;
        padding-top: 8px;
        padding-bottom: 8px;
    }

    .opt-btn {
        font-size: 14px;
        color: #666;
        padding: 10px 20px;
        font-weight: bold;
    }

    .opt-btn:hover {
        background-color: #f3f3f3;
    }

    .opt-icon {
        font-size: 18px;
        margin-right: 10px;
    }

    .node-color {
        width: 8px;
        height: 8px;
        border-radius: 50%;
        background-color: #0097f7;
    }

    .expand-icon {
        font-size: 16px;
        vertical-align: text-top;
        margin-right: 5px;
        width: 15px;
        display: inline-block;
    }

    .select-icon {
        color: #0097f7;
    }
</style>
<i18n>
    {
    "en": {
    "新增子分类": "Add category",
    "编辑分类":"Edit",
    "删除分类":"Delete",
    "确定要删除分类":"Are you sure you want to delete the category {0}?"
    },
    "zh_CN": {
    "新增子分类": "新增子分类",
    "编辑分类":"编辑分类",
    "删除分类":"删除分类",
    "确定要删除分类":"确定要删除分类【{0}】吗？"
    }
    }
</i18n>
<template>
    <div class="tree-item">
        <div class="tree-item-name ">
            <div
                class="tree-left text-no-wrap tree-item-inner"
                @click="toggleSelect"
                span="20">
                <template v-if="value.children!=null&&value.children.length>0">
                    <Icon
                        class="expand-icon"
                        @click.native.stop="expand=!expand"
                        v-if="expand"
                        type="ios-arrow-down" />
                    <Icon
                        class="expand-icon"
                        @click.native.stop="expand=!expand"
                        v-if="expand===false"
                        type="ios-arrow-forward" />

                </template>
                <div
                    class="expand-icon"
                    v-if="value.children==null||value.children.length===0"></div>
                <div
                    class="node-color"
                    :style="{backgroundColor:value.color}"></div>
                <div class="tree-item-label text-no-wrap" v-tooltip="value.name"> {{value.name}}</div>
            </div>
            <div class="tree-right tree-item-inner">
                <Icon class="select-icon" v-if="selected" type="md-checkmark" />
                <Poptip
                    v-if="canEditable"
                    ref="poptip"
                    transfer
                    class="tree-opt poptip-full"
                    trigger="click"
                    placement="bottom-start">
                    <IconButton icon="ios-more"></IconButton>
                    <div class="opt-list" slot="content">
                        <div class="opt-btn" @click.stop="showEditTagDialog()">
                            <Icon class="opt-icon" type="ios-add" />
                            {{$t('新增子分类')}}
                        </div>
                        <div
                            class="opt-btn"
                            @click.stop="showEditTagDialog(value)">
                            <Icon
                                class="opt-icon" type="ios-settings-outline" />
                            {{$t('编辑分类')}}
                        </div>
                        <div class="opt-btn" @click.stop="deleteCategory">
                            <Icon class="opt-icon" type="ios-trash-outline" />
                            {{$t('删除分类')}}
                        </div>
                    </div>
                </Poptip>
            </div>

        </div>
        <div class="tree-item-children">
            <template v-for="item in value.children">
                <TaskCategoryTreeNode
                    v-if="value.children&&expand"
                    ref="taskCategoryTreeNodeItem"
                    :permissionId="permissionId"
                    :edit="canEdit"
                    :change="change"
                    :key="'tctn_'+item.id"
                    :value="item"></TaskCategoryTreeNode>
            </template>

        </div>
    </div>
</template>
<script>
    import { TextOverflow, Tooltip } from '../../../../assets/vue.directives';

    export default {
        mixins: [componentMixin],
        name: 'TaskCategoryTreeNode',
        props: ['value', 'change', 'edit', 'permissionId'],
        directives: {
            TextOverflow,
            Tooltip,
        },
        data() {
            return {
                selected: false,
                canEdit: true,
                expand: false,
            };
        },
        watch: {
            value(val) {
                if (val) {
                    this.selected = val.selected;
                    //this.expand=val.expand
                }
            },
        },
        computed: {
            canEditable() {
                if (!this.canEdit) {
                    return false;
                }
                if (this.permissionId) {
                    return this.prjPerm(this.permissionId) && this.canEdit;
                }
                if (!this.value) {
                    return false;
                }
                return this.prjPerm('task_edit_category_' + this.value.objectType) && this.canEdit;
            },
        },
        mounted() {
            if (this.value && this.value.selected) {
                this.selected = this.value.selected;
            }

            if (this.edit === false) {
                this.canEdit = false;
            }
        },
        methods: {
            updateSelect() {
                this.selected = this.value.selected;
                // 获取到所有ref的值为taskCategoryTreeNodeItem组件
                if (!Array.isArray(this.$refs.taskCategoryTreeNodeItem)) {
                    return;
                }
                this.$refs.taskCategoryTreeNodeItem.forEach(treeNode => {
                    treeNode.updateSelect();
                });
                // if(this.value.children){
                //     for(var i=0;i<this.value.children.length;i++){
                //         var t=this.value.children[i];
                //         this.$refs['child-'+t.id][0].updateSelect();
                //     }
                // }
            },
            showEditTagDialog(item) {
                if (this.$refs.poptip) {
                    this.$refs.poptip.ok();
                }
                var args = {};
                app.showDialog(TaskCategoryEditDialog, {
                    item: item,
                    projectId: this.value.projectId,
                    objectType: this.value.objectType,
                    parentId: this.value.id,
                });
            },
            toggleSelect() {
                if (this.$refs.poptip) {
                    this.$refs.poptip.ok();
                }
                this.selected = !this.selected;
                this.value.selected = this.selected;
                this.$emit('input');
                if (this.change) {
                    this.change(this.value);
                }
            },
            deleteCategory() {
                if (this.$refs.poptip) {
                    this.$refs.poptip.ok();
                }
                app.confirm(this.$t('确定要删除分类', [this.value.name]), () => {
                    app.invoke('BizAction.deleteCategory', [app.token, this.value.id], info => {
                        app.postMessage('category.edit');
                    });
                });
            },
        },
    };
</script>
