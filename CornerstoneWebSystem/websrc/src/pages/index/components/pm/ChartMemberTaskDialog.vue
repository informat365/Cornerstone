<style scoped>
    .page {
        min-height: calc(100vh - 51px);
        background-color: #f1f4f5;
        width: 100%;
        padding: 20px;
        text-align: center;
    }

    .chart-content {
        width: 1200px;
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

    .nodata {
        padding: 60px;
        font-size: 20px;
        color: #999;
        text-align: center;
    }

    .table-info {
        color: #999;
        text-align: center;
    }

    .table-count {
        background-color: #e8e8e8;
        color: #666;
        padding: 3px 5px;
        border-radius: 3px;
    }
</style>
<i18n>
    {
    "en": {
    "成员任务": "Member tasks",
    "条数据":"{0} Items",
    "名称":"Name",
    "项目":"Project",
    "责任人":"Owner",
    "创建人":"Creater",
    "时间":"Time",
    "状态":"Status",
    "待认领":"Unassigned",
    "暂无数据":"No Data",

    "待办":"Todo",
    "类型":"Type",
    "创建":"Creation",
    "全部":"All",
    "用户":"Member Account",
    "查询":"Query",
    "导出":"Export"
    },
    "zh_CN": {
    "成员任务": "成员任务",
    "条数据":"{0}条数据",
    "名称":"名称",
    "项目":"项目",
    "责任人":"责任人",
    "创建人":"创建人",
    "时间":"时间",
    "状态":"状态",
    "待认领":"待认领",
    "暂无数据":"暂无数据",

    "待办":"待办",
    "类型":"类型",
    "创建":"创建",
    "全部":"全部",
    "用户":"用户名",
    "查询":"查询",
    "导出":"导出"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog"
        v-model="showDialog"
        :closable="true"
        :mask-closable="false"
        :loading="false"
        :title="$t('成员任务')"
        class="fullscreen-modal"
        width="100%"
        :footer-hide="true">
        <div class="page">
            <div class="chart-content">
                <div class="chart-opt-bar">
                    <Form inline>
                        <FormItem>
                            <Select v-model="formItem.type" :placeholder="$t('类型')" style="width:150px">
                                <Option value="1">{{$t('待办')}}</Option>
                                <Option value="2">{{$t('创建')}}</Option>
                                <Option value="3">{{$t('全部')}}</Option>
                            </Select>
                        </FormItem>
                        <FormItem>
                            <CompanyUserSelect
                                v-model="formItem.accountIds"
                                clearable
                                multiple
                                style="min-width:150px;max-width: 450px;"
                                @on-change="loadData" />
                            <!-- <Input type="text" v-model="formItem.accountName" :placeholder="$t('用户')"></Input>-->
                        </FormItem>
                        <FormItem>
                            <Button
                                :disabled="!Array.isArray(formItem.accountIds) || formItem.accountIds.le === 0"
                                type="default"
                                @click="loadData()">
                                {{$t('查询')}}
                            </Button>
                        </FormItem>

                        <FormItem>
                            <Button
                                :disabled="!Array.isArray(formItem.accountIds) || formItem.accountIds.le === 0"
                                type="default"
                                @click="exportData()">
                                {{$t('导出')}}
                            </Button>
                        </FormItem>

                    </Form>
                </div>

                <div class="chart-chart">
                    <div class="table-info">
                        <span class="table-count">{{$t('条数据',[list.length])}}</span>
                    </div>
                    <table class="table-content table-color" style="table-layout:fixed">
                        <thead>
                        <tr>
                            <th>{{$t('名称')}}</th>
                            <th style="width:150px;">{{$t('项目')}}</th>
                            <th style="width:120px;">{{$t('责任人')}}</th>
                            <th style="width:100px;">{{$t('创建人')}}</th>
                            <th style="width:150px">{{$t('时间')}}</th>
                            <th style="width:90px;text-align:right">{{$t('状态')}}</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr
                            @click="showTaskInfo(item)"
                            v-for="item in list"
                            :key="'todo'+item.id"
                            class="table-row clickable">
                            <td class="text-no-wrap">
                                <TaskNameLabel
                                    :is-done="item.isFinish"
                                    :id="item.serialNo"
                                    :name="item.name"
                                    :object-type="item.objectTypeName" />

                            </td>
                            <td>
                                <div style="max-width:180px" class="text-no-wrap">
                                    {{item.projectName}}
                                </div>
                            </td>
                            <td class="text-no-wrap">
                                <TaskOwnerView :tips="false" :value="item" />
                            </td>

                            <td class="text-no-wrap">
                                <AvatarImage
                                    size="small"
                                    :name="item.createAccountName"
                                    :value="item.createAccountImageId"
                                    type="label"></AvatarImage>
                            </td>

                            <td>
                                <OverdueLabel v-if="!item.isFinish&&item.endDate" :value="item.endDate"></OverdueLabel>
                                <span v-if="item.isFinish||item.endDate==null">{{item.startDate|fmtDate}}<Icon type="md-arrow-round-forward" />{{item.endDate|fmtDate}}</span>
                            </td>
                            <td style="text-align:right">
                                <TaskStatus :label="item.statusName" :color="item.statusColor"></TaskStatus>
                            </td>


                        </tr>
                        </tbody>
                    </table>
                    <div class="table-nodata" v-if="list.length==0">
                        {{$t('暂无数据')}}
                    </div>
                </div>
            </div>
        </div>
    </Modal>
</template>

<script>

    export default {
        mixins: [componentMixin],
        data() {
            return {
                formItem: {
                    type: '1',
                    accountIds: null,
                },
                list: [],
            };
        },

        methods: {
            pageLoad() {
            },
            loadData() {
              /*  var type = 1;
                if (this.formItem.type === 'create') {
                    type = 2;
                }*/
                if (!Array.isArray(this.formItem.accountIds) || this.formItem.accountIds.le === 0) {
                    this.list = [];
                    return;
                }
                app.invoke('BizAction.getAccountsTaskList', [app.token, this.formItem.type, this.formItem.accountIds], list => {
                    this.list = list;
                });
            },
            showTaskInfo(item) {
                app.showDialog(TaskDialog, {
                    taskId: item.uuid,
                    taskList: this.list,
                    showTopBar: true,
                });
            },
            exportData() {
                var query = {
                    token: app.token,
                    type:this.formItem.type,
                    accountIds:this.formItem.accountIds
                };
                var queryString = JSON.stringify(query);
                var encoded = Base64.encode(queryString);
                window.open('/p/main/export_account_task_list?arg=' + encodeURIComponent(encoded));
            },
        },
    };
</script>
