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
    "用户名":"Username",
    "名称":"Name",
    "曾用名1":"Old name",
    "曾用名2":"Old name",
    "机构代码":"Organizaion code",
    "状态":"Status",
    "查询":"Query",
    "实施方式":"service type",
    "跨境情况":"overland type",
    "关联外包类型":"out type",
    "中断或异常退出":"if interrupt or exit",
    "创建时间":"Create time",
    "设置":"Setting",
    "创建供应商":"Create new supplier"
    },
    "zh_CN": {
    "名称":"名称",
    "曾用名1":"曾用名1",
    "曾用名2":"曾用名2",
    "机构代码":"机构代码",
    "状态":"状态",
    "查询":"查询",
    "实施方式":"实施方式",
    "跨境情况":"跨境情况",
    "关联外包类型":"关联外包类型",
    "中断或异常退出":"中断或异常退出",
    "创建时间":"创建时间",
    "设置":"设置",
    "创建供应商":"创建供应商"
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
                                style="width:150px"
                                @on-change="loadData(true)"
                                type="text"
                                v-model="formItem.name"
                                :placeholder="$t('名称')"></Input>
                    </FormItem>
                    <FormItem>
                        <Input
                                style="width:150px"
                                @on-change="loadData(true)"
                                type="text"
                                v-model="formItem.oldName1"
                                :placeholder="$t('曾用名1')"></Input>
                    </FormItem>
                    <FormItem>
                        <Input
                                style="width:150px"
                                @on-change="loadData(true)"
                                type="text"
                                v-model="formItem.oldName2"
                                :placeholder="$t('曾用名2')"></Input>
                    </FormItem>
                    <FormItem>
                        <Input
                                style="width:150px"
                                @on-change="loadData(true)"
                                type="text"
                                v-model="formItem.code"
                                :placeholder="$t('机构代码')"></Input>
                    </FormItem>
                    <FormItem>
                        <Button style="margin: 0 5px;" @click="loadData(true)" type="default">{{$t('查询')}}</Button>
                    </FormItem>
                </Form>

            </Col>
            <Col span="6" class="opt-right">
                <Form inline>
                    <FormItem>
                        <Button v-if="company.version==2&&perm('supplier_edit')"  @click="showEditDialog" type="default" icon="md-person-add">
                            {{$t('创建供应商')}}
                        </Button>
                    </FormItem>
                </Form>
            </Col>
        </Row>


        <div style="padding:20px">
            <BizTable :fixed="true" @change="loadData" :page="pageQuery" :value="tableData">
                <template slot="thead">
                    <tr>
                        <th style="width:150px">{{$t('名称')}}</th>
                        <th style="width:150px">{{$t('曾用名1')}}</th>
                        <th style="width:150px">{{$t('曾用名2')}}</th>
                        <th style="width:100px">{{$t('机构代码')}}</th>
                        <th style="width:150px">{{$t('实施方式')}}</th>
                        <th style="width:150px">{{$t('跨境情况')}}</th>
                        <th style="width:150px">{{$t('关联外包类型')}}</th>
                        <th style="width:150px">{{$t('中断或异常退出')}}</th>
                        <th style="width:150px">{{$t('创建时间')}}</th>
                        <th style="width:120px;"></th>
                    </tr>
                </template>
                <template slot="tbody">
                    <tr v-for="(item,idx) in tableData" :key="item.id" class="table-row">
                        <td>
                            {{item.name}}
                        </td>
                        <td>
                            {{item.oldName1}}
                        </td>
                        <td>
                            {{item.oldName2}}
                        </td>
                        <td>
                            {{item.code}}
                        </td>
                        <td>
                            <DataDictLabel type="Supplier.service" :value="item.serviceType"/>
                        </td>
                        <td>
                            <DataDictLabel type="Supplier.overland" :value="item.overlandType"/>
                        </td>
                        <td>
                            <DataDictLabel type="Supplier.out" :value="item.outType"/>
                        </td>
                        <td>
                            <span v-if="item.exited">是</span>
                            <span v-else>否</span>
                        </td>
                        <td>{{item.createTime|fmtDateTime}}</td>
                        <td style="text-align:right">
                            <Button @click="showEditDialog(item)" type="default">{{$t('设置')}}</Button>
                        </td>
                    </tr>

                </template>
            </BizTable>
        </div>

    </div>
</template>

<script>
    import Treeselect from '@riophae/vue-treeselect';
    import '@riophae/vue-treeselect/dist/vue-treeselect.css';

    export default {
        mixins: [componentMixin],
        components: {Treeselect},
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
                projectRoleList: [],
                departmentList: [],
                tableData: [],
            };
        },
        methods: {
            pageLoad() {
                this.company = app.company;
                this.loadData();
            },
            pageMessage(type) {
                if (type == 'supplier.edit') {
                    console.log("-------> message  ",type)
                    this.loadData();
                }
            },
            loadData(resetPage) {
                if (resetPage) {
                    this.pageQuery.pageIndex = 1;
                }
                var query = copyObject(this.pageQuery, this.formItem);
                app.invoke('BizAction.getSupplierList', [app.token, query], (info) => {
                    this.tableData = info.list;
                    this.pageQuery.total = info.count;
                });
            },
            showEditDialog(item) {
                app.showDialog(CompanySupplierEditDialog, {
                    id: item.id,
                });
            },
        },
    };
</script>
