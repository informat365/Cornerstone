<template>
    <div class="version-task-list">
        <FlagTitle :label="flagTitle" size="large">
            <IconButton
                v-if="hasPermissionUpdate"
                slot="right"
                :size="16"
                icon="md-git-branch"
                title="添加发布内容"
                @click="onClickRelationTask"></IconButton>
        </FlagTitle>
        <div class="version-task-list-wrap">
            <div
                class="version-task-list-row action" v-if="hasPermissionUpdate">
                <div class="version-task-list-row-check">
                    <Checkbox
                        :indeterminate="indeterminate" :value="checkAll" @click.prevent.native="onHandleCheckAll">全选
                    </Checkbox>
                </div>
                <div class="version-task-list-row-actions" @click.stop>
                    <ActionDropdown
                        opera-text="操作"
                        :actions="batchActions"
                        :disabled="!hasPermissionUpdate"
                        :disabled-show-opera-text="false"
                        @on-click="onClickDropdown">
                        <Icon type="ios-arrow-down" size="16" slot="suffix-slot" />
                    </ActionDropdown>
                </div>
            </div>
            <template v-for="(item) in taskList">
                <div
                    :key="item.id" class="version-task-list-row task">
                    <div class="version-task-list-row-check" v-if="hasPermissionUpdate">
                        <Checkbox
                            :value="checkVersionTaskIds.indexOf(item.id) > -1"
                            :label="item.id"
                            @on-change="onTaskCheckChange(item.id)">
                            &nbsp;
                        </Checkbox>
                    </div>
                    <div class="version-task-list-row-detail" @click.stop="onShowTaskInfo(item)">
                        <Tag size="small">{{ item.objectTypeName }}</Tag>
                        <div class="version-task-list-row-name">
                            <span class="task-project-name">
                                {{ item.projectName }}
                            </span>
                            <span class="task-no numberfont">#{{ item.taskSerialNo }}</span>
                            <span class="task-name">
                              {{ item.taskName }}
                            </span>
                        </div>
                    </div>
                    <div class="version-task-list-row-actions">
                        <ActionDropdown
                            :opera-text="item.taskStatusName"
                            :actions="versionTaskActions"
                            :disabled="!hasPermissionUpdate"
                            @on-click="(name)=>{onClickDropdown(name,item.id)}">
                            <Icon type="ios-arrow-down" size="16" slot="suffix-slot" />
                        </ActionDropdown>
                    </div>
                </div>
            </template>
            <template v-if="Array.isEmpty(taskList)">
                <div class="nodata">暂无发布内容</div>
            </template>
        </div>
    </div>
</template>

<script>
    import VueMixin from '../vue.mixin';
    import ActionDropdown from './ActionDropdown';
    import FlagTitle from './FlagTitle';

    export default {
        name: 'VersionTaskList',
        components: { ActionDropdown, FlagTitle },
        mixins: [VueMixin, componentMixin],
        props: {
            versionId: {
                type: Number,
                default: 0,
                required: true,
            },
        },
        watch: {
            version: {
                immediate: true,
                handler() {
                    this.$nextTick(this.loadCompanyVersionTaskList);
                },
            },
        },
        computed: {
            flagTitle() {
                if (Array.isEmpty(this.taskList)) {
                    return '发布内容';
                }
                return `发布内容 (${ this.taskList.length }) 项`;
            },
            hasPermissionUpdate() {
                return this.perm('version_add');
            },
            indeterminate() {
                if (Array.isEmpty(this.taskList) || Array.isEmpty(this.checkVersionTaskIds)) {
                    return false;
                }
                return this.taskList.length !== this.checkVersionTaskIds.length;
            },
            checkAll() {
                if (Array.isEmpty(this.taskList) || Array.isEmpty(this.checkVersionTaskIds)) {
                    return false;
                }
                return this.taskList.length === this.checkVersionTaskIds.length;
            },
            batchActions() {
                return [
                    {
                        name: 'batchDelete',
                        label: '删除发布内容',
                        disabled: Array.isEmpty(this.checkVersionTaskIds),
                    },
                ];
            },
        },
        data() {
            return {
                taskList: [],
                checkVersionTaskIds: [],
                versionTaskActions: [
                    {
                        name: 'delete',
                        label: '删除发布内容',
                    },
                ],
            };
        },
        methods: {
            onClickRelationTask() {
                app.showDialog(TaskSelectDialog, {
                    repositoryVersionId:0,
                    callback: (list) => {
                        if (Array.isEmpty(list)) {
                            return;
                        }
                        const taskIds = [];
                        list.forEach(item => {
                            taskIds.push(item.id);
                        });
                        this.addCompanyVersionTaskList(taskIds);
                    },
                });
            },
            addCompanyVersionTaskList(taskIds) {
                this.request('BizAction.addCompanyVersionTaskList', [this.versionId, taskIds], (res) => {
                    this.loadCompanyVersionTaskList();
                    this.$emit('on-version-task-add');
                });
            },
            loadCompanyVersionTaskList() {
                this.loading = true;
                this.request('BizAction.getCompanyVersionTaskList', [{
                    versionId: this.versionId,
                    pageIndex: 1,
                    pageSize: 1000,
                }], (res) => {
                    if (Object.isEmpty(res)) {
                        return;
                    }
                    this.taskList = res.list;
                    this.loading = false;
                });
            },
            onClickDropdown(name, versionTaskId) {
                if (name === 'batchDelete') {
                    if (Array.isEmpty(this.checkVersionTaskIds)) {
                        return;
                    }
                    app.confirm('确定要删除选择的版本内容向吗？删除后不可恢复.', () => {
                        this.onConfirmDeleteVersionTask([
                            ...this.checkVersionTaskIds,
                        ]);
                    });
                    return;
                }
                if (name === 'delete') {
                    if (!Number.check(versionTaskId, 1)) {
                        return;
                    }
                    app.confirm('确定要删除此版本内容向吗？删除后不可恢复.', () => {
                        this.onConfirmDeleteVersionTask([
                            versionTaskId,
                        ]);
                    });
                }
            },
            onConfirmDeleteVersionTask(versionTaskIds) {
                if (Array.isEmpty(versionTaskIds)) {
                    return;
                }
                this.loading = true;
                this.request('BizAction.batchDeleteCompanyVersionTaskList', [
                    this.versionId,
                    versionTaskIds,
                ], () => {
                    app.toast('删除版本内容项成功');
                    this.taskList = this.taskList.filter(item => versionTaskIds.indexOf(item.id) === -1);
                    this.$emit('on-version-task-delete');
                    this.checkVersionTaskIds = [];
                    this.showModal = false;
                    this.loading = false;
                }, (code, message) => {
                    app.toast(message || '删除版本内容项失败');
                    this.loading = false;
                });
            },
            onTaskCheckChange(taskId) {
                const index = this.checkVersionTaskIds.indexOf(taskId);
                if (index > -1) {
                    this.checkVersionTaskIds.splice(index, 1);
                    return;
                }
                this.checkVersionTaskIds.push(taskId);
            },
            onHandleCheckAll() {
                if (this.indeterminate || this.checkAll) {
                    this.checkVersionTaskIds = [];
                    return;
                }
                const checkVersionTaskIds = [];
                if (Array.isEmpty(this.taskList)) {
                    return;
                }
                this.taskList.forEach(_ => {
                    checkVersionTaskIds.push(_.id);
                });
                this.checkVersionTaskIds = checkVersionTaskIds;
            },
            onShowTaskInfo(item) {
                app.showDialog(TaskDialog, {
                    taskId: item.taskUuid,
                    showTopBar: true,
                    callback: (res) => {
                        if (!res || !res.type) {
                            return;
                        }
                        if (res.type === 'goto') {
                            this.showDialog = false;
                        }
                    },
                });
            },
        },
    };
</script>

<style lang="less" scoped>

    .nodata {
        padding: 20px 40px;
        background-color: #f9f9f9;
        color: #94929d;
        text-align: center;
        width: 100%;
    }

    .version-task-list {
        /*position: relative;*/
        /*max-height: 500px;*/
        /*width: 100%;*/
        /*overflow: auto;*/
        /*overflow-scrolling: touch;*/
        /*scroll-behavior: smooth;*/

        &-wrap {
            border: solid 1px #efefef;
            border-radius: 4px;
        }

        &-row {
            height: 48px;
            display: flex;
            align-items: center;
            align-content: center;
            font-size: 12px;
            border-bottom: solid 1px #f3f3f3;

            &.action {
                height: 40px;
                padding: 0 10px;
                justify-content: space-between;
                border-bottom: solid 1px #efefef;
            }

            &.task {
                padding: 0 10px;
                cursor: pointer;
            }

            &.task:hover {
                background-color: #ebf7ff;
            }

            &-check {
            }

            &-detail {
                flex: 1;
                display: flex;
                align-items: center;
                align-content: center;
            }

            &-name {
                flex: 1;
                overflow: hidden;
                text-overflow: ellipsis;
                display: -webkit-box;
                -webkit-line-clamp: 1;
                -webkit-box-orient: vertical;
                padding: 0 5px;

                .task-project-name {
                    color: #0a6fc0;
                    font-weight: bold;
                }
            }

            &-actions {
                position: relative;
                height: 40px;
                line-height: 40px;
            }
        }
    }
</style>
