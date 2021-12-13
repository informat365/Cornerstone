<template>
    <Drawer class="drawer-detail" v-model="drawerShow" width="720" :mask="false" :mask-closable="false">
        <div slot="close"></div>
        <div class="drawer-detail-header" slot="header">
            <div class="drawer-detail-header-title">
                {{ detail && detail.name }}
            </div>
            <div class="drawer-detail-header-actions">
                <template v-if="perm('version_delete')">
                    <IconButton icon="ios-trash" @click="onDeleteVersion"></IconButton>
                    <div class="split-line"></div>
                </template>
                <IconButton icon="md-close" @click="onClickClose"></IconButton>
            </div>
        </div>
        <template v-if="detail">
            <div class="drawer-detail-body scrollbox">
                <Form class="drawer-version-form" label-position="left" :label-width="80">
                    <Row :gutter="20">
                        <Col :span="24">
                            <FormItem class="name">
                                <template v-if="!hasPermissionUpdate">
                                    <div class="value">
                                        {{ detail.name }}
                                    </div>
                                </template>
                                <template v-else>
                                    <Input
                                        :maxlength="20" v-model.trim="detail.name" @on-blur="onUpdateVersion('name')" />
                                </template>
                            </FormItem>
                        </Col>
                        <Col :span="12">
                            <FormItem label="版本状态" prop="status">
                                <VersionStatusSelect
                                    :disabled="!hasPermissionUpdate"
                                    style="width: 180px"
                                    :value="detail.status"
                                    @on-change="onVersionStatusChange">
                                    <Icon type="ios-arrow-down" size="16" slot="suffix-slot" />
                                </VersionStatusSelect>
                            </FormItem>
                        </Col>
                        <Col :span="12">
                            <FormItem label="版本进度">
                                <Progress :percent="progress" :stroke-width="8" />
                            </FormItem>
                        </Col>
                        <Col :span="12">
                            <FormItem label="开始时间" prop="startTime">
                                <template v-if="!hasPermissionUpdate">
                                    {{ detail.startTime | fmtDateTime }}
                                </template>
                                <template v-else>
                                    <DatePicker
                                        type="datetime"
                                        :options="startTimeOptions"
                                        style="width: 180px"
                                        confirm
                                        transfer
                                        v-model="detail.startTime"
                                        @on-clear="onVersionTimeChange('startTime')"
                                        @on-ok="onVersionTimeChange('startTime')"
                                        placeholder="请选择版本开始时间" />
                                </template>
                            </FormItem>
                        </Col>
                        <Col :span="12">
                            <FormItem
                                label="发布时间" prop="endTime">
                                <template v-if="!hasPermissionUpdate">
                                    {{ detail.endTime | fmtDateTime }}
                                </template>
                                <template v-else>
                                    <DatePicker
                                        type="datetime"
                                        :options="endTimeOptions"
                                        style="width: 180px"
                                        confirm
                                        transfer
                                        v-model="detail.endTime"
                                        @on-clear="onVersionTimeChange('endTime')"
                                        @on-ok="onVersionTimeChange('endTime')"
                                        placeholder="请选择版本发布时间" />
                                </template>
                            </FormItem>
                        </Col>
                    </Row>
                </Form>
                <div class="drawer-detail-remark">
                    <FlagTitle label="详细描述" size="large">
                        <div class="text-right" slot="right">
                            <IconButton
                                v-if="hasPermissionUpdate"
                                :size="16"
                                icon="md-checkbox"
                                title="编辑描述"
                                @click="remarkEdit=true"></IconButton>
                        </div>
                    </FlagTitle>
                    <div v-show="!remarkEdit" @dblclick="onDbClickUpdateRemark">
                        <RichtextLabel class="version-richtext-box" :value="detail.remark" placeholder="未设置详细描述" />
                    </div>
                    <div v-show="remarkEdit">
                        <RichtextEditor ref="descEditor" v-model="detail.remark" />
                        <div
                            style="margin-top:10px;text-align:right" v-if="hasPermissionUpdate">
                            <Button type="text" style="margin-right:10px" @click="onClickCancelEdit">取消</Button>
                            <Button type="default" @click="onClickSaveEdit">保存</Button>
                        </div>
                    </div>
                </div>
                <div class="drawer-detail-tasks">
                    <VersionTaskList
                        :version-id="detail.id"
                        @on-version-task-add="onVersionTaskChanged"
                        @on-version-task-delete="onVersionTaskChanged" />
                </div>
                <div class="drawer-detail-change-log">
                    <FlagTitle label="所有动态" size="large" />
                    <div>
                        <template v-for="item in changeLogList">
                            <TaskViewChangeLog :value="item" :key="'log_'+item.id" />
                        </template>
                        <div class="table-nodata" v-if="Array.isEmpty(changeLogList)">暂无变更记录</div>
                        <div
                            v-if="changeLogMore !== true" style="text-align:center;padding: 20px 10px 10px">
                            <Button
                                @click="loadChangeLogMore" type="text" icon="ios-refresh-circle-outline">
                                加载更多
                            </Button>
                        </div>
                    </div>
                </div>
            </div>
        </template>
    </Drawer>
</template>

<script>
    import VueMixin from '../vue.mixin';
    import FlagTitle from './FlagTitle';
    import VersionStatusSelect from './VersionStatusSelect';
    import VersionTaskList from './VersionTaskList';

    export default {
        name: 'DrawerVersion',
        components: { VersionTaskList, FlagTitle, VersionStatusSelect },
        mixins: [VueMixin, componentMixin],
        data() {
            return {
                drawerShow: false,
                remarkEdit: false,
                detailBak: {},
                changeLogQuery: {
                    pageIndex: 1,
                    pageSize: 20,
                    typeInList:[1100,1101,1102,1103,1104]
                },
                changeLogList: [],
                changeLogMore: false,
                detail: null,
                startTimeOptions: {
                    disabledDate: (date) => {
                        if (Date.isDate(this.detail.endTime)) {
                            return date.getTime() > this.detail.endTime.getTime();
                        }
                        return false;
                    },
                },
                endTimeOptions: {
                    disabledDate: (date) => {
                        if (Date.isDate(this.detail.startTime)) {
                            return date.getTime() < this.detail.startTime.getTime();
                        }
                        return false;
                    },
                },
            };
        },
        computed: {
            progress() {
                if (!this.detail || this.detail.totalTaskNum === 0) {
                    return 0;
                }
                return Number.parseFloat((this.detail.finishTaskNum / this.detail.totalTaskNum * 100).toFixed(1));
            },
            hasPermissionUpdate() {
                return this.perm('version_add');
            },
        },
        methods: {
            show(versionId) {
                this.drawerShow = true;
                this.detail = null;
                this.loadData(versionId);
            },
            loadData(versionId) {
                this.loading = true;
                this.request('BizAction.getCompanyVersionById', [versionId], (res) => {
                    if (Object.isEmpty(res)) {
                        return;
                    }
                    this.detailBak = {
                        ...res,
                    };
                    this.detail = {
                        ...res,
                        startTime: Date.parser(res.startTime),
                        endTime: Date.parser(res.endTime),
                    };
                    this.loading = false;
                    this.loadChangeLog(true);
                }, (code, message) => {
                    app.toast(message || '加载版本信息失败');
                    this.loading = false;
                });
            },
            onUpdateVersion(field) {
                if (this.detailBak[field] === this.detail[field]) {
                    return;
                }
                this.loading = true;
                this.request('BizAction.updateCompanyVersion', [{ ...this.detail }], () => {
                    app.toast('更新成功');
                    app.postMessage('version.item.edit');
                    this.detailBak = {
                        ...this.detail,
                    };
                    this.showModal = false;
                    this.loading = false;
                }, (code, message) => {
                    app.toast(`更新失败，${ message || '' }`);
                    this.loading = false;
                });
            },
            onVersionTaskChanged() {
                this.loadData(this.detail.id);
                app.postMessage('version.item.edit');
            },
            onVersionStatusChange(value) {
                this.detail.status = value;
                this.$nextTick(() => {
                    this.onUpdateVersion('status');
                });
            },
            onVersionTimeChange(field) {
                this.$nextTick(() => {
                    this.onUpdateVersion(field);
                });
            },
            onDbClickUpdateRemark() {
                if (!this.hasPermissionUpdate) {
                    return;
                }
                this.remarkEdit = true;
            },
            onClickCancelEdit() {
                this.remarkEdit = false;
                this.detail.remark = this.detailBak.remark;
            },
            onClickSaveEdit() {
                this.remarkEdit = false;
                this.$nextTick(() => {
                    this.onUpdateVersion('remark');
                });
            },
            loadChangeLog(reload) {
                if (this.loading) {
                    return;
                }
                if (reload) {
                    this.changeLogQuery.pageIndex = 1;
                }
                this.loading = true;
                app.invoke('BizAction.getChangeLogList', [app.token, {
                    ...this.changeLogQuery,
                    associatedId: this.detail.id,
                }], list => {
                    if (Array.isEmpty(list)) {
                        this.changeLogMore = true;
                        this.loading = false;
                        return;
                    }
                    list.forEach(item => {
                        if (!String.isEmpty(item.items)) {
                            item.items = JSON.parse(item.items);
                        } else {
                            item.items = {};
                        }
                    });
                    this.changeLogMore = list.length < this.changeLogQuery.pageSize;
                    if(this.changeLogQuery.pageIndex>1){
                        this.changeLogList = [...this.changeLogList, ...list];
                    }else{
                        this.changeLogList = list;
                    }
                    if (list.length > 0) {
                        this.changeLogQuery.pageIndex += 1;
                    }
                    this.loading = false;
                }, (code, message) => {
                    app.toast(`加载变更记录失败，${ message || '' }`);
                    this.loading = false;
                });
            },
            loadChangeLogMore() {
                this.loadChangeLog();
            },
            onClickClose() {
                this.drawerShow = false;
            },
            onDeleteVersion() {
                app.confirm('确定要删除此版本吗？删除后不可恢复.', () => {
                    this.loading = true;
                    this.request('BizAction.deleteCompanyVersion', [this.detail.id], () => {
                        app.toast('删除版本成功');
                        app.postMessage('version.item.edit');
                        this.drawerShow = false;
                        this.loading = false;
                    }, (code, message) => {
                        app.toast(message || '删除版本失败');
                        this.loading = false;
                    });
                });
            },
        },
    };
</script>

<style lang="less" scoped>
    .split-line {
        width: 1px;
        height: 12px;
        background-color: #a2a2a2;
        margin-left: 7px;
        margin-right: 7px;
    }

    .drawer-detail {
        /deep/ .ivu-drawer-header {
            border-bottom: 1px solid #ccc;
            background-color: #f7f7f7;
            height: 48px;
        }

        /deep/ .ivu-drawer-body {
            padding: 0;
            overflow: hidden;
        }

        &-header {
            display: flex;
            align-items: center;
            align-content: center;

            &-title {
                flex: 1;
                overflow: hidden;
                text-overflow: ellipsis;
                display: -webkit-box;
                -webkit-line-clamp: 1;
                -webkit-box-orient: vertical;
                font-weight: bold;
                font-size: 16px;
            }

            &-actions {
                display: flex;
                align-items: center;
                align-content: center;
            }
        }

        &-body {
            position: relative;
            height: 100%;
            width: 100%;
            overflow-x: auto;
            overflow-scrolling: touch;
            scroll-behavior: smooth;
            overscroll-behavior: contain;
            padding: 16px;
        }
    }
</style>
