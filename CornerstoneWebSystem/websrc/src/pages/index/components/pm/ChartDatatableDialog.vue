<style scoped>
    .page {
        min-height: calc(100vh - 62px);
        background-color: #F1F4F5;
        width: 100%;
        padding: 20px;
        text-align: center;
    }

    .chart-content {
        width: 100%;
        display: inline-block;
        background-color: #fff;
        border: 1px solid #eee;
        border-radius: 5px;
        text-align: left;
    }

    .chart-opt-bar {
        padding: 20px;
        border-bottom: 1px solid #eee;
    }

    .chart-chart {
        padding: 20px;
    }
</style>
<i18n>
    {
    "en": {
    "数据表格": "Datatable-",
    "项目":"Project",
    "查询":"Query",
    "导出":"Export"
    },
    "zh_CN": {
    "数据表格": "数据表格-",
    "项目":"项目",
    "查询":"查询",
    "导出":"导出"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog"
        :closable="true"
        :mask-closable="false"
        :loading="false" :title="$t('数据表格')+datatable.name" class="fullscreen-modal"
        width="100%"
        :footer-hide="true">
        <div class="page">
            <div class="chart-content">
                <div class="chart-opt-bar" v-if="datatable.dataTableDefine.query">
                    <Form inline>
                        <template v-for="query in datatable.dataTableDefine.query">

                            <template v-if="query.type=='project'">
                                <FormItem :key="'q'+query.name">
                                    <Select clearable filterable v-model="formItem.projectId" :placeholder="$t('项目')"
                                            style="width:200px">
                                        <Option v-for="item in projectList" :value="item.id" :key="'prj'+item.id">{{
                                            item.name }}
                                        </Option>
                                    </Select>
                                </FormItem>
                                <FormItem :key="'ex'+query.name+'ex'">
                                    <Select clearable v-model="formItem.iterationId" :placeholder="$t('迭代')"
                                            style="width:150px">
                                        <Option v-for="item in iterationList" :value="item.id" :key="'it'+item.id">{{
                                            item.name }}
                                        </Option>
                                    </Select>
                                </FormItem>
                            </template>

                            <FormItem :key="'sd'+query.name">
                                <ExDatePicker v-if="query.type=='date'" type="date" style="width:130px"
                                              v-model="formQuery[query.name]" :placeholder="query.name"></ExDatePicker>

                                <Input v-if="query.type=='text'" type="text" v-model="formQuery[query.name]"
                                       :placeholder="query.name"></Input>

                                <Select v-if="query.type=='select'" clearable v-model="formQuery[query.name]"
                                        :placeholder="query.name" style="width:150px">
                                    <Option v-for="item in query.options" :value="item" :key="query.name+'q'+item">
                                        {{ item }}
                                    </Option>
                                </Select>

                            </FormItem>

                        </template>

                        <FormItem>
                            <Button type="default" @click="loadData()">{{$t('查询')}}</Button>
                        </FormItem>
                        <FormItem>
                            <Button :disabled="result.table==null" type="default" @click="exportData()">{{$t('导出')}}
                            </Button>
                        </FormItem>

                    </Form>
                </div>
            </div>
            <div v-if="result.messages&&result.messages.length>0" style="padding:5px 0;text-align:left">
                <Alert>
                    <div :key="'m'+msgIdx" v-for="(item,msgIdx) in result.messages">{{item}}</div>
                </Alert>
            </div>
            <div v-if="result.table" style="padding-top:5px">
                <Tabs type="card" value="sheet0" :animated="false">
                    <TabPane v-for="(sheet,sheetIdx) in result.table.sheets" :key="'s'+sheetIdx" :label="sheet.name"
                             :name="'sheet'+sheetIdx">
                        <DatatableView :data="sheet"></DatatableView>
                    </TabPane>
                </Tabs>
            </div>
        </div>
    </Modal>
</template>

<script>
    import {Base64} from 'js-base64';

    export default {
        mixins: [componentMixin],
        data() {
            return {
                projectList: [],
                iterationList: [],
                formItem: {
                    projectId: null,
                    iterationId: null,
                },
                formQuery: {},
                datatable: {
                    name: "",
                    dataTableDefine: {}
                },
                result: {}
            }
        },
        watch: {
            "formItem.projectId": function (val) {
                this.loadIterationList();
            }
        },
        methods: {
            pageLoad() {
                this.loadDatatable();
                this.loadProjectList();
            },
            loadDatatable() {
                app.invoke('DataTableAction.getDataTableById', [app.token, this.args.id], (info) => {
                    this.datatable = info;
                    info.dataTableDefine.query.forEach(item => {
                        if (item.value) {
                            this.formQuery[item.name] = item.value;
                        }
                    })
                })
            },
            loadData() {
                var query = {
                    projectId: this.formItem.projectId,
                    iterationId: this.formItem.iterationId,
                    parameters: this.formQuery
                }
                app.invoke('DataTableAction.runDataTable', [app.token, this.args.id, query], (info) => {
                    this.result = info;
                })
            },
            loadProjectList() {
                app.invoke("BizAction.getMyProjectList", [app.token], list => {
                    this.projectList = list;
                });
            },
            loadIterationList() {
                if (this.formItem.projectId == null) {
                    this.iterationList = [];
                    return;
                }
                app.invoke("BizAction.getProjectIterationInfoList", [app.token, this.formItem.projectId], list => {
                    this.iterationList = list;
                    this.formItem.iterationId = null;
                })
            },
            exportData() {
                var queryForm = {
                    projectId: this.formItem.projectId,
                    iterationId: this.formItem.iterationId,
                    parameters: this.formQuery
                }
                var query = {
                    token: app.token,
                    query: queryForm,
                    id: this.args.id
                }
                //
                var queryString = JSON.stringify(query);
                var encoded = Base64.encode(queryString);
                window.open('/p/main/export_data_table?arg=' + encodeURIComponent(encoded))
            }
        }
    }
</script>
