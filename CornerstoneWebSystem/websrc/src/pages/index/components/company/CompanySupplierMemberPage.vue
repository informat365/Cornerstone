<style scoped lang="less">
    .opt-bar {
        background-color: #f1f4f5;
        margin-top: 0;
    }

    .opt-right {
        text-align: right;
    }

    .create-popup {
        position: fixed;
        width: 200px;
        background-color: #fff;
        box-shadow: 0 1px 6px rgba(0, 0, 0, .2);
        z-index: 999;
    }

    .create-popup-opt {
        position: absolute;
        top: 0;
        left: 0;
        width: 200px;
        padding-left: 20px;
        color: #999;
    }

    .nodata {
        font-size: 20px;
        color: #999;
        margin-top: 30px;
    }

    .wechat-label {
        display: inline-block;
        padding: .25em .6em;
        font-size: 12px;
        font-weight: 600;
        line-height: 1;
        text-align: center;
        white-space: nowrap;
        vertical-align: middle;
        border-radius: 1rem;
        color: #fff;
        background-color: #83db5e;
        margin-left: 5px;
    }

    .checkbox-col {
        width: 60px;
    }

    .operation-box {
        text-align: center;

        div {
            padding: 8px 0 !important;
            cursor: pointer;

            &:hover {
                background-color: #eee;
            }
        }
    }

    .vertical-center-modal {
        display: flex;
        align-items: center;
        justify-content: center;

        .ivu-modal {
            top: 0;
            min-height: 600px;
        }
    }

    .model-container {
        min-height: 500px;
    }
</style>

<i18n>
    {
    "en": {
    "姓名":"Name",
    "名称":"Name",
    "供应商":"Supplier",
    "工号":"Code",
    "入场开始时间":"Entry time start",
    "入场截止时间":"Entry time end",
    "离场开始时间":"Leave time start",
    "离场截止时间":"Leave time end ",
    "查询":"Query",
    "创建人员":"Create new member",
    "项目":"Project",
    "产品库":"Products",
    "科室":"Department",
    "小组":"Team",
    "手机号":"Mobile",
    "状态":"Status",
    "入场时间":"Entry time",
    "离场时间":"Leave time ",
    "创建时间":"Create time",
    "设置":"Setting"
    },
    "zh_CN": {
    "姓名":"姓名",
    "名称":"名称",
    "供应商":"供应商",
    "工号":"工号",
    "入场开始时间":"入场开始时间",
    "入场截止时间":"入场截止时间",
    "离场开始时间":"离场开始时间",
    "离场截止时间":"离场截止时间",
    "查询":"查询",
    "创建人员":"创建人员",
    "项目":"项目",
    "产品库":"产品库",
    "科室":"科室",
    "小组":"小组",
    "手机号":"手机号",
    "状态":"状态",
    "入场时间":"入场时间",
    "离场时间":"离场时间",
    "创建时间":"创建时间",
    "设置":"设置"
    }
    }
</i18n>

<template>
    <div class="page">
        <Row class="opt-bar opt-bar-light">
            <Col span="18" class="opt-left">
                <Form inline @submit.native.prevent>
                    <FormItem>
                        <Input
                                style="width:180px"
                                @on-change="loadData(true)"
                                clearable
                                type="text"
                                v-model="formItem.name"
                                :placeholder="$t('姓名')"></Input>
                    </FormItem>
                    <FormItem>
                        <Select
                                @on-change="loadData(true)"
                                clearable filterable
                                :placeholder="$t('供应商')"
                                v-model="formItem.supplierId"
                                style="width:180px">
                            <template v-for="item in supplierList">
                                <Option :value="item.id" :key="item.id">{{ item.name }}</Option>
                            </template>
                        </Select>
                    </FormItem>
                    <FormItem>
                        <DepartmentTreeSelect
                                v-model="formItem.departmentId" clearable style="width:180px;" @on-change="loadData"/>
                    </FormItem>
                    <FormItem>
                        <Input
                                style="width:180px"
                                @on-change="loadData(true)"
                                type="text"
                                clearable
                                v-model="formItem.code"
                                :placeholder="$t('工号')"></Input>
                    </FormItem>
                    <FormItem>
                        <ExDatePicker @on-change="loadData(true)"
                                      type="date" style="width:150px"
                                      clearable
                                      v-model="formItem.entryTimeStart"
                                      :placeholder="$t('入场开始时间')"></ExDatePicker>
                    </FormItem>
                    <FormItem>
                        <ExDatePicker @on-change="loadData(true)"
                                      type="date" style="width:150px"
                                      clearable
                                      v-model="formItem.entryTimeEnd"
                                      :day-end="true"
                                      :placeholder="$t('入场截止时间')"></ExDatePicker>
                    </FormItem>
                    <FormItem>
                        <ExDatePicker @on-change="loadData(true)"
                                      clearable
                                      type="date" style="width:150px"
                                      v-model="formItem.leaveTimeStart"
                                      :placeholder="$t('离场开始时间')"></ExDatePicker>
                    </FormItem>
                    <FormItem>
                        <ExDatePicker @on-change="loadData(true)"
                                      clearable
                                      type="date" style="width:150px"
                                      v-model="formItem.leaveTimeEnd"
                                      :day-end="true"
                                      :placeholder="$t('离场截止时间')"></ExDatePicker>
                    </FormItem>

                    <FormItem>
                        <Button style="margin: 0 5px;" @click="loadData(true)" type="default">{{$t('查询')}}</Button>
                        <Button style="margin: 0 5px;" @click="exportData" type="default">{{$t('导出')}}</Button>
                    </FormItem>
                </Form>

            </Col>
            <Col span="6" class="opt-right">
                <Form inline>
                    <FormItem>
                        <Button v-if="company.version==2&&perm('supplier_edit')"  @click="showEditDialog" type="default" icon="md-person-add">
                            {{$t('创建人员')}}
                        </Button>
                    </FormItem>
                </Form>
            </Col>
        </Row>


        <div style="padding:20px">
            <Table
                    class="workflow-table" :columns="columns" :data="tableData">
                <template slot="project" slot-scope="{ row }">
                    <Tag v-for="pro in row.projectList" :key="'prj_'+pro.id">{{pro.name}}</Tag>
                </template>
                <template slot="repository" slot-scope="{ row }">
                    <Tag v-for="pro in row.repositoryList" :key="'prj_'+pro.id">{{pro.name}}</Tag>
                </template>
                <template slot="department" slot-scope="{ row }">
                    <Tag v-for="pro in row.departmentList" :key="'prj_'+pro.id">{{pro.name}}</Tag>
                </template>
                <template slot="entryTime" slot-scope="{ row }">
                    {{row.entryTime|fmtDate}}
                </template>
                <template slot="leaveTime" slot-scope="{ row }">
                    {{row.leaveTime|fmtDate}}
                </template>
                <template slot="workStatus" slot-scope="{ row }">
                    <DataDictLabel type="SupplierMember.workStatus" v-model="row.workStatus"/>
                </template>
                <template slot="status" slot-scope="{ row }">
                    <DataDictLabel type="Common.status" v-model="row.status"/>
                </template>
                <template slot="position" slot-scope="{ row }">
                    <DataDictLabel type="SupplierMember.position" v-model="row.position"/>
                </template>
                <template slot="action" slot-scope="{ row }">
                    <Button @click="showEditDialog(row)" type="default">{{$t('设置')}}</Button>
                </template>
            </Table>
            <Row class="table-footer-bar">
                <Col span="8" class="table-footer-bar-left">
                    <slot name="footer-left"></slot> &nbsp;
                </Col>
                <Col span="16" class="table-footer-bar-right">
                    <Page :total="pageQuery.total"
                          :current="pageQuery.pageIndex"
                          :page-size="pageQuery.pageSize"
                          :page-size-opts="[10,15, 20, 50, 200]"
                          placement="top"
                          show-sizer
                          show-total
                          @on-page-size-change="changePageSize"
                          @on-change="changePage"></Page>
                </Col>
            </Row>
        </div>

    </div>
</template>

<script>
    import Treeselect from '@riophae/vue-treeselect';
    import '@riophae/vue-treeselect/dist/vue-treeselect.css';
    import DataDictLabel from "../../../../components/ui/DataDictLabel";

    export default {
        mixins: [componentMixin],
        components: {DataDictLabel, Treeselect},
        data() {
            return {
                company: {},
                formItem: {
                    accountName: null,
                    accountUserName: null,
                    roleName: null,
                    accountStatus: null,
                    departmentId: null,
                },
                pageQuery: {
                    pageIndex: 1,
                    pageSize: 20,
                },
                supplierList: [],
                departmentList: [],
                tableData: [],
                columns:[
                    {
                        title:this.$t('名称'),
                        key:'name',
                        width:120
                    },
                    {
                        title:this.$t('供应商'),
                        key:'supplierName',
                        width:120
                    },
                    {
                        title:this.$t('工号'),
                        key:'code',
                        width:120
                    },
                    {
                        title:this.$t('项目'),
                        slot:'project',
                        width:180
                    },
                    {
                        title:this.$t('系统'),
                        slot:'repository',
                        width:180
                    },
                    {
                        title:this.$t('科室'),
                        slot:'department',
                        width:180
                    },
                    {
                        title:this.$t('手机号'),
                        key:'mobile',
                        width:120
                    },
                    {
                        title:this.$t('状态'),
                        slot:'status',
                        width:80
                    },
                    {
                        title:this.$t('工作状态'),
                        slot:'workStatus',
                        width:100
                    },
                    {
                        title:this.$t('岗位'),
                        slot:'position',
                        width:80
                    },
                    {
                        title:this.$t('入场时间'),
                        slot:'entryTime',
                        width:150
                    },
                    {
                        title:this.$t('离场时间'),
                        slot:'leaveTime',
                        width:150
                    },
                    {
                        title:this.$t('操作'),
                        slot:'action',
                        width:100,
                        fixed:'right'
                    },
                ]
            };
        },
        methods: {
            pageLoad() {
                this.company = app.company;
                this.loadData();
                this.loadSupplierList();
            },
            pageMessage(type) {
                if (type == 'supplierMember.edit') {
                    this.loadData();
                }
            },
            loadData(resetPage) {
                if (resetPage) {
                    this.pageQuery.pageIndex = 1;
                }
                var query = copyObject(this.pageQuery, this.formItem);
                app.invoke('BizAction.getSupplierMemberList', [app.token, query], (info) => {
                    this.tableData = info.list;
                    this.pageQuery.total = info.count;
                });
            },
            loadSupplierList(){
                app.invoke('BizAction.getSupplierList', [app.token, {pageSize:-1}], (info) => {
                    this.supplierList = info.list;
                });
            },
            showEditDialog(item) {
                app.showDialog(CompanySupplierMemberEditDialog, {
                    id: item.id,
                });
            },
            exportData(){
                var query={
                    token:app.token,
                    query:this.formItem
                }
                //
                var queryString=JSON.stringify(query);
                var encoded=Base64.encode(queryString);
                window.open('/p/main/export_supplier_member?arg='+encodeURIComponent(encoded))
            },
            changePage: function (page) {
                this.pageQuery.pageIndex = page;
                this.loadData()
            },
            changePageSize: function (size) {
                this.pageQuery.pageSize = size;
                this.loadData()
            },
        },
    };
</script>
