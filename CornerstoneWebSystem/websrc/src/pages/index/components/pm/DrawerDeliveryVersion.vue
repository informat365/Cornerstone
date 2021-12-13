<template>
    <Drawer class="drawer-detail" v-model="drawerShow" width="600" :mask="false" :mask-closable="false">
        <div slot="close"></div>
        <div class="drawer-detail-header" slot="header">
            <div class="drawer-detail-header-title">
                {{ detail && detail.name }}
            </div>
            <div class="drawer-detail-header-actions">
                <template v-if="prjPerm('delivery_delete')&&!projectDisabled">
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
                                    <Input :disabled="projectDisabled"
                                           :maxlength="20" v-model.trim="detail.name" @on-blur="onUpdateVersion('name')"/>
                                </template>
                            </FormItem>
                        </Col>
                        <Col :span="12">
                            <FormItem label="交付状态" prop="status">
                                <div v-if="!hasPermissionUpdate" class="delivery-status" :class="{'delivery-status-delivery':detail.status==2}">{{detail.status==2?'已交付':'未交付'}}</div>
                                <div v-else style="width: 80px; border-radius: 20px !important;" :class="'delivery-status-'+detail.status">
                                    <Select :disabled="projectDisabled" v-model="detail.status" @on-change="onVersionStatusChange($event)">
                                        <Option disabled label="未交付" :value="1" ><span class="delivery-status">未交付</span></Option>
                                        <Option :disabled="detail.status==2" label="已交付" :value="2"><span class="delivery-status delivery-status-delivery">已交付</span></Option>
                                    </Select>
                                </div>
                              <!--  <VersionStatusSelect
                                    :disabled="!hasPermissionUpdate"
                                    style="width: 180px"
                                    :value="detail.status"
                                    @on-change="onVersionStatusChange">
                                    <Icon type="ios-arrow-down" size="16" slot="suffix-slot"/>
                                </VersionStatusSelect>-->
                            </FormItem>
                        </Col>
                        <Col :span="12">
                            <FormItem
                                label="交付时间" prop="deliveryDate">
                                <template v-if="!hasPermissionUpdate">
                                    {{ detail.deliveryDate | fmtDateTime }}
                                </template>
                                <template v-else>
                                    <ExDatePicker
                                        type="datetime"
                                        style="width: 180px"
                                        confirm
                                        transfer
                                        :disabled="projectDisabled"
                                        v-model="detail.deliveryDate"
                                        @on-clear="onVersionTimeChange('deliveryDate')"
                                        @on-ok="onVersionTimeChange('deliveryDate')"
                                        placeholder="请选择版本交付时间"/>
                                </template>
                            </FormItem>
                        </Col>
                    </Row>
                </Form>
                <div class="drawer-detail-remark">
                    <FlagTitle label="交付备注" size="large">
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
                        <RichtextLabel class="version-richtext-box" :value="detail.remark" placeholder="未设置详细描述"/>
                    </div>
                    <div v-show="remarkEdit">
                        <Input :disabled="projectDisabled" type="textarea" :rows="1" v-model="detail.remark"/>
                        <div
                            style="margin-top:10px;text-align:right" v-if="hasPermissionUpdate">
                            <Button type="text" style="margin-right:10px" @click="onClickCancelEdit">取消</Button>
                            <Button type="default" @click="onClickSaveEdit">保存</Button>
                        </div>
                    </div>
                </div>
                <div class="drawer-detail-tasks">
                    <FlagTitle label="关联版本" size="large">
                        <div class="text-right" slot="right">
                            <IconButton
                                v-if="hasPermissionUpdate"
                                :size="16"
                                icon="md-checkbox"
                                title="编辑关联"
                                @click="vesionEdit=true"></IconButton>
                        </div>
                    </FlagTitle>
                    <table class="table-content table-color" v-if="!Array.isEmpty(detail.deliveryItems)">
                        <thead>
                        <tr>
                            <th style="width:100px;">{{$t('版本库')}}</th>
                            <th style="width:100px;">{{$t('版本')}}</th>
                            <th style="width:100px;">{{$t('版本状态')}}</th>
                            <th style="width:100px">{{$t('版本备注')}}</th>
                            <th v-if="vesionEdit"></th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr v-for="(item,idx) in detail.deliveryItems" :key="'version_'+item.id" class="table-row">
                            <td>
                                {{item.repositoryName}}
                            </td>
                            <td>
                                {{item.versionName}}
                            </td>
                            <td>
                                <DataDictLabel :disabled="projectDisabled" v-model="item.versionStatus" type="CompanyVersion.status"/>
                            </td>
                            <td>
                                <div v-html="item.versionRemark"></div>
                            </td>
                            <td v-if="vesionEdit&&!projectDisabled">
                                <IconButton
                                    v-if="hasPermissionUpdate"
                                    :size="16"
                                    icon="ios-trash"
                                    @click="deleteVersionItem(item,idx)"></IconButton>
                                <IconButton
                                    v-if="hasPermissionUpdate"
                                    :size="16"
                                    icon="ios-add"
                                    @click="addVersionItem"></IconButton>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <div class="drawer-detail-change-log">
                    <FlagTitle label="所有动态" size="large"/>
                    <div>
                        <template v-for="item in changeLogList">
                            <TaskViewChangeLog :value="item" :key="'log_'+item.id"/>
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
    import VueMixin from '../VersionRepository/vue.mixin';
    import FlagTitle from '../VersionRepository/components/FlagTitle';
    import VersionTaskList from '../VersionRepository/components/VersionTaskList';
    import ExDatePicker from "../../../../components/ui/ExDatePicker";
    import DataDictLabel from "../../../../components/ui/DataDictLabel";

    export default {
        name: 'DrawerVersion',
        components: {DataDictLabel, ExDatePicker, VersionTaskList, FlagTitle},
        mixins: [VueMixin, componentMixin],
        data() {
            return {
                drawerShow: false,
                remarkEdit: false,
                detailBak: {},
                changeLogQuery: {
                    pageIndex: 1,
                    pageSize: 20,
                    typeInList: [1200, 1201, 1202]
                },
                changeLogList: [],
                changeLogMore: false,
                detail: null,
                vesionEdit:false,
            };
        },
        computed: {
            progress() {
                if (!this.detail || this.detail.totalTaskNum === 0) {
                    return 0;
                }
            },
            hasPermissionUpdate() {
                return this.prjPerm('delivery_edit')&&!this.projectDisabled;
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
                this.request('BizAction.getDeliveryById', [versionId], (res) => {
                    if (Object.isEmpty(res)) {
                        return;
                    }
                    this.detailBak = {
                        ...res,
                    };
                    this.detail = {
                        ...res
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
                this.request('BizAction.updateDelivery', [{...this.detail}], () => {
                    app.toast('更新成功');
                    app.postMessage('delivery.edit');
                    this.detailBak = {
                        ...this.detail,
                    };
                    this.showModal = false;
                    this.loading = false;
                    this.remarkEdit =false;
                    this.loadData(this.detail.id);
                }, (code, message) => {
                    app.toast(`更新失败，${message || ''}`);
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
                    if (this.changeLogQuery.pageIndex > 1) {
                        this.changeLogList = [...this.changeLogList, ...list];
                    } else {
                        this.changeLogList = list;
                    }
                    if (list.length > 0) {
                        this.changeLogQuery.pageIndex += 1;
                    }
                    this.loading = false;
                }, (code, message) => {
                    app.toast(`加载变更记录失败，${message || ''}`);
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
                    app.invoke('BizAction.deleteDelivery', [app.token,this.detail.id], () => {
                        app.toast('删除版本成功');
                        app.postMessage('delivery.edit');
                        this.drawerShow = false;
                        this.loading = false;
                    }, (code, message) => {
                        app.toast(message || '删除版本失败');
                        this.loading = false;
                    });
                });
            },
            deleteVersionItem(item,idx) {
                app.confirm('确定要删除此关联版本吗？删除后不可恢复.', () => {
                    this.loading = true;
                    app.invoke('BizAction.deleteDeliveryItem', [app.token,item.id], () => {
                        app.toast('删除关联版本成功');
                        this.loading = false;
                        this.detail.deliveryItems.splice(idx,1);
                        this.versionEdit =false;
                    }, (code, message) => {
                        app.toast(message || '删除关联版本失败');
                        this.loading = false;
                    });
                });
            },
            addVersionItem(){
                let _this = this;
                app.showDialog(VersionRepositorySelectDialog,{
                    single:true,
                    callback:function (list) {
                        if(!!list&&!Array.isEmpty(list)){
                            let item ={
                                deliveryId:_this.detail.id,
                                versionId:list[0].id,
                                versionName:list[0].name,
                                repositoryId:list[0].repositoryId,
                                repositoryName:list[0].repositoryName,
                                versionStatus:list[0].status,
                                versionRemark:list[0].remark
                            };

                            app.invoke('BizAction.addDeliveryItem', [app.token,item], () => {
                                app.toast('关联版本成功');
                                if(Array.isEmpty(_this.detail.deliveryItems)){
                                    _this.detail.deliveryItems =[item];
                                }else{
                                    _this.detail.deliveryItems.push(item);
                                }
                                _this.versionEdit =false;
                            }, (code, message) => {
                                app.toast(message || '关联版本失败');
                                this.loading = false;
                            });
                        }
                        console.log(_this.detail.deliveryItems)
                    }
                });
            }

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

    .ivu-select /deep/ .ivu-select-selection{
        border: 0;
        background: transparent;
        outline: none;
        width: 100%;
        box-shadow: none;
        height: 30px;
    }
    .delivery-status{
        width: 55px;
        height: 32px;
        line-height: 32px;
        font-size: 10px;
        border:2px solid #00a8c6;
        color: #00a8c6;
        padding: 0 6px;
        border-radius: 16px !important;

        &-delivery{
            border:3px solid #0AB169 !important;
            color: #0AB169 !important;
        }
    }

    .delivery-status{

        &-1{
            border:2px solid #00a8c6 ;
            color: #00a8c6 ;
        }

        &-2{
            border:3px solid #0AB169 ;
            color: #0AB169 ;
        }
    }

</style>
<i18n>
    {
    "en": {
    "编辑交付版本": "Edit Delivery Version",
    "创建交付版本":"Create Delivery Version",
    "名称":"Name",
    "备注":"remark",
    "交付时间":"Delivery Date",
    "关联版本库":"Associate Company Version",
    "参与人":"Participants",
    "新增":"Add",
    "删除":"Delete",
    "版本库":"Company version repository name",
    "版本":"Company version name",
    "版本备注":"Company version remark",
    "保存成功":"Success",
    "创建成功":"Success",
    "保存":"Save",
    "创建":"Create"
    },
    "zh_CN": {
    "创建交付版本": "创建交付版本",
    "编辑交付版本":"编辑交付版本",
    "名称":"名称",
    "备注":"备注",
    "交付时间":"交付时间",
    "关联版本库":"关联版本库",
    "新增":"新增",
    "删除":"删除",
    "版本库":"版本库",
    "版本":"版本",
    "版本备注":"版本备注",
    "保存成功":"保存成功",
    "创建成功":"创建成功",
    "保存":"保存",
    "创建":"创建"
    }
    }
</i18n>
