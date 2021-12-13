<style lang="less" scoped>
    .table-box {
        background-color: #fff;
        padding: 15px 30px;
        box-shadow: 0px 2px 10px 0px rgba(225, 225, 225, 0.5);
        border: 1px solid rgba(216, 216, 216, 1);
    }

    .table-info {
        color: #999;
        text-align: center;
    }

    .table-count {
        background-color: #E8E8E8;
        color: #666;
        padding: 3px 5px;
        border-radius: 3px;
    }

    .nodata {
        padding: 60px;
        font-size: 20px;
        color: #999;
        text-align: center;
    }
</style>
<i18n>
    {
    "en": {
    "总计条数据":"total {0} items",
    "每页条":"{0} per page",
    "第页":"{0}/{1}",
    "保存": "保存",
    "基本信息": "基本信息",
    "表单设计": "表单设计",
    "工作流": "工作流",
    "填写权限": "填写权限",
    "数据权限": "数据权限",
    "流程设计": "流程设计",
    "当前页面没有保存是否保存": "当前页面没有保存，是否保存？"
    },
    "zh_CN": {
    "总计条数据":"总计 {0} 条数据",
    "每页条":"每页 {0} 条",
    "第页":"第 {0}/{1} 页",
    "保存": "保存",
    "基本信息": "基本信息",
    "表单设计": "表单设计",
    "工作流": "工作流",
    "填写权限": "填写权限",
    "数据权限": "数据权限",
    "流程设计": "流程设计",
    "当前页面没有保存是否保存": "当前页面没有保存，是否保存？"
    }
    }
</i18n>
<template>
    <div>
        <div style="padding:20px;">
            <Row>
                <Col span="8">&nbsp;
                </Col>
                <Col span="8">
                    <div class="table-info">
                        <IconButton :size="15" :disabled="pageQuery.pageIndex==1" @click="loadData(-1)"
                                    icon="md-arrow-round-back"></IconButton>
                        <span class="table-count">{{pageQuery.totalCount}}条数据 ，{{pageQuery.pageIndex}}/{{pageQuery.totalPage}}</span>
                        <IconButton :size="15" :disabled="pageQuery.pageIndex==pageQuery.totalPage"
                                    @click="loadData(1)"
                                    icon="md-arrow-round-forward"></IconButton>
                    </div>
                    &nbsp;
                </Col>
                <Col span="8" style="text-align:right">

                </Col>
            </Row>

            <div style=" padding: 0 5px">
                <div class="chart-opt-bar">
                    <Input
                        placeholder="系统名称"
                        style="width: 150px;"
                        type="text"
                        @on-change="loadData(0,true)"
                        v-model.trim="formItem.name"
                    />

                    <CompanyUserSelect
                        v-model="formItem.ownerAccountIds"
                        clearable
                        multiple
                        placeholder="系统经理"
                        style="width: 150px;margin-left: 10px"
                        @on-change="loadData(0,true)"
                    />
                    <Input
                        placeholder="业务经理"
                        style="width: 150px;margin-left: 10px;"
                        type="text"
                        @on-change="loadData(0,true)"
                        v-model.trim="formItem.businessLeader"
                    />
                    <CompanyDepartmentSelect
                        v-model="formItem.ownerDepartmentIds"
                        clearable
                        multiple
                        placeholder="开发科室"
                        style="width: 150px;margin-left: 10px"
                        @on-change="loadData(0,true)"
                    />
                    <!-- <DepartmentTreeSelect
                         style="width: 150px; margin-left: 10px"
                         v-model="formItem.departmentId"
                         multiple
                         placeholder="开发科室"
                         clearable
                         @on-change="loadData(true)"/>-->
                    <ExDatePicker clearable @on-change="loadData(0,true)" type="date"
                                  style="width: 150px; margin-left: 10px"
                                  :editable="false"
                                  v-model="formItem.releaseDateStart"
                                  placeholder="发布开始时间"></ExDatePicker>

                    <ExDatePicker clearable @on-change="loadData(0,true)" type="date" :day-end="true"
                                  style="width: 150px; margin-left: 10px"
                                  :editable="false"
                                  v-model="formItem.releaseDateEnd"
                                  placeholder="发布结束时间"></ExDatePicker>

                    <DataDictSelect
                        style="width: 150px; margin-left: 10px"
                        @change="loadData(0,true)"
                        clearable
                        placeholder="系统状态"
                        type="CompanyVersionRepository.status"
                        v-model="formItem.status"
                    ></DataDictSelect>

                    <Button
                        v-if="perm('version_repository_add')"
                        style="margin-left: 20px;"
                        @click.native="showCreateDialog">
                        创建
                    </Button>
                    <Button
                        type="success"
                        v-if="perm('version_repository_add')"
                        style="margin-left: 10px;"
                        @click.native="showImportDialog">
                        导入
                    </Button>
                    <Button
                        type="info"
                        v-if="perm('version_repository_add')"
                        style="margin-left: 10px;"
                        @click.native="exportRepository">
                        导出
                    </Button>
                </div>
                <div class="actions">

                </div>
            </div>
        </div>

        <div style="padding:20px">
            <Table
                class="workflow-table" :columns="columns" :data="repositoryList">
                <template slot="releaseDate" slot-scope="{ row }">
                    {{row.releaseDate|formatFullDate}}
                </template>
                <template slot="ownerAccount" slot-scope="{ row }">
                    <span v-if="row.ownerAccountList&&row.ownerAccountList.length>0">{{row.ownerAccountList.map(k=>k.name).join("、")}}</span>
                </template>
                <template slot="ownerDepartment" slot-scope="{ row }">
                    <span v-if="row.ownerDepartmentList&&row.ownerDepartmentList.length>0">{{row.ownerDepartmentList.map(k=>k.name).join("、")}}</span>
                </template>
                <template slot="status" slot-scope="{ row }">
                    <DataDictLabel type="CompanyVersionRepository.status" v-model="row.status"/>
                </template>
                <template slot="action" slot-scope="{ row }">
                    <Button style="margin-left: 10px;" v-if="perm('version_repository_add')" @click="showEditDialog(row,false)" type="default">{{$t('设置')}}</Button>
                    <Button style="margin-left: 10px;" v-else @click="showEditDialog(row,true)" type="default">{{$t('查看')}}</Button>
                    <Button style="margin-left: 5px;" @click="showVersionDialog(row)" type="default">{{$t('查看版本')}}</Button>
                </template>
            </Table>
            <Row class="table-footer-bar">
                <Col span="8" class="table-footer-bar-left">
                    <slot name="footer-left"></slot> &nbsp;
                </Col>
                <Col span="16" class="table-footer-bar-right">
                </Col>
            </Row>
        </div>
    </div>
</template>
<script>


    import TaskOwnerView from "./TaskOwnerView";
    export default {
        components: {TaskOwnerView},
        mixins: [componentMixin],
        data() {
            return {
                companyUuid: null,
                repositoryList: [],
                pageSizePopuptipVisible: false,
                pageQuery: {
                    pageIndex: 1,
                    pageSize: 20,
                    totalCount: 0,
                    totalPage: 0,
                },
                formItem: {},
                columns: [
                    {
                        title: "系统名称",
                        key: 'name',
                        width: 250
                    },

                    {
                        title: "系统经理",
                        slot: 'ownerAccount',
                        width: 180
                    },
                    {
                        title: "开发科室",
                        slot: 'ownerDepartment',
                        width: 180
                    },
                    {
                        title: "业务经理",
                        key: 'businessLeader',
                        width: 180
                    },
                    {
                        title: "系统最新版本号",
                        key: 'latest',
                        width: 120
                    },
                    {
                        title: "系统发布时间",
                        slot: 'releaseDate',
                        width: 120
                    },
                    {
                        title: "系统状态",
                        slot: 'status',
			minWidth:100
                    },
                    {
                        title: this.$t('操作'),
                        slot: 'action',
                        width: 220,
                        fixed: 'right'
                    },
                ]
            };
        },
        computed: {
            noRepositoryData() {
                return Array.isArray(this.repositoryList) && this.repositoryList.length === 0 && this.pageQuery.pageIndex === 1 && !this.loading;
            },
        },
        mounted() {
            this.companyUuid = app.company.uuid;
            this.loadData(0,true);
            app.onMessage('AppEvent', (event) => {
                if (event.type === 'version.repository.edit') {
                    this.loadData(0,true);
                }
            });
        },
        methods: {
            loadData(pageAddition = 0, reload = false) {
                if (this.loading) {
                    return;
                }
                if (this.pageQuery.pageIndex + pageAddition < 1) {
                    return;
                }
                if (reload) {
                    this.pageQuery.pageIndex = 1;
                } else {
                    if (this.pageQuery.pageIndex + pageAddition < 1) {
                        return;
                    }
                    this.pageQuery.pageIndex += pageAddition;
                }
                this.loading = true;
                let query = Object.assign({}, this.formItem, this.pageQuery);
                app.invoke('BizAction.getCompanyVersionRepositoryList', [app.token, query], (res) => {
                    this.loading = false;
                    if (!res || !Array.isArray(res.list)) {
                        if (this.pageQuery.pageIndex === 1 || reload) {
                            this.repositoryList = [];
                            this.pageQuery.totalCount = 0;
                            this.pageQuery.totalPage = 0;
                        }
                        return;
                    }
                    this.repositoryList = res.list;
                    if (!Number.check(this.repositoryId, 1)) {
                        this.repositoryId = this.repositoryList[0].id;
                    }
                    this.pageQuery.totalCount = Number.parseIntWithDefault(res.count, 0);
                    this.pageQuery.totalPage = Math.ceil(this.pageQuery.totalCount / this.pageQuery.pageSize);
                }, (code, message) => {
                    app.toast(`加载数据失败，${message || ''}`);
                    this.loading = false;
                });
            },
            showCreateDialog() {
                app.showDialog(DashboardVersionRepositoryEditDialog)
            },
            showEditDialog(item,readonly) {
                app.showDialog(DashboardVersionRepositoryEditDialog,{id:item.id,readonly:readonly})
            },
            exportRepository(){
                var query={
                    token:app.token,
                    query:this.formItem
                }
                //
                var queryString=JSON.stringify(query);
                var encoded=Base64.encode(queryString);
                window.open('/p/main/export_company_version_repository?arg='+encodeURIComponent(encoded))
            },
            showVersionDialog(item){
                app.showDialog(DashboardVersionListDialog,{repositoryId:item.id})
            },
            showImportDialog(){
                app.showDialog(DashboardRepositoryImportDialog)
            }
        }
    };
</script>
