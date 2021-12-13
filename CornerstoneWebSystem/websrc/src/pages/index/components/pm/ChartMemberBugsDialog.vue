<style scoped>
    .page {
        min-height: calc(100vh - 51px);
        background-color: #f1f4f5;
        width: 100%;
        padding: 20px;
        text-align: center;
    }

    .chart-content {
        width: 1000px;
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
        padding: 20px 0;
    }

    .content-box {
        display: flex;
        align-items: center;
        padding: 10px;
        flex-direction: column;
    }

    .table-box {
        background-color: #fff;
        padding: 30px;
        padding-top: 10px;
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

    .nodata {
        padding: 60px;
        font-size: 20px;
        color: #999;
        text-align: center;
    }

    .create-account-select {
        position: relative;
        display: inline-block;
        color: #515a6e;
        width: 220px;
        font-size: 14px;
        cursor: pointer;
        background-color: #fff;
        border: 1px solid #dcdee2;
        outline: 0;
        -webkit-user-select: none;
        -moz-user-select: none;
        -ms-user-select: none;
        user-select: none;
        box-sizing: border-box;
        vertical-align: middle;
    }

    .create-account-select:hover {
        border-color: #57a3f3
    }


    .create-account-select .placeholder {
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

    .create-account-select .selection {
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
        padding-left: 4px;
        padding-right: 24px;
    }

    .create-account-select .icon {
        position: absolute;
        top: 50%;
        right: 8px;
        line-height: 1;
        transform: translateY(-50%);
        font-size: 14px;
        color: #808695;
    }
</style>
<i18n>
    {
    "en": {
    "成员缺陷统计": "Member bugs stat",
    "项目":"Project",
    "名称":"Name",
    "类型":"Type",
    "开始时间":"Start",
    "结束时间":"End",
    "查询":"Query",
    "暂无数据":"No Data",
    "成员":"Name",
    "返工率":"Rework rate",
    "缺陷率":"BUG rate",
    "总任务数":"Number of total task",
    "总缺陷数":"Number of total bug",
    "重新打开缺陷数":"Number of bug reopened",
    "总数据统计":"{0} items"
    },
    "zh_CN": {
    "成员缺陷统计": "成员缺陷统计",
    "项目":"项目",
    "开始时间":"开始时间",
    "结束时间":"结束时间",
    "查询":"查询",
    "暂无数据":"暂无数据",
    "成员":"成员",
    "成员":"成员",
    "返工率":"返工率",
    "缺陷率":"缺陷率",
    "总任务数":"总任务数",
    "总缺陷数":"总缺陷数",
    "重新打开缺陷数":"重新打开缺陷数",
    "总数据统计":"{0} 条数据"
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
        :title="$t('成员缺陷统计')"
        class="fullscreen-modal"
        width="100%"
        :footer-hide="true">
        <div class="page">
            <div class="chart-content">
                <div class="chart-opt-bar">
                    <Form inline>
                        <FormItem>
                            <Select
                                @on-change="loadChartData"
                                v-model="formItem.projectId"
                                clearable
                                filterable
                                :placeholder="$t('项目')"
                                style="width:200px">
                                <Option v-for="item in projectList" :value="item.id" :key="'prj'+item.id">
                                    {{ item.name }}
                                </Option>
                            </Select>
                        </FormItem>
                        <FormItem>
                            <CompanyUserSelect
                                @on-change="loadChartData"
                                v-model="formItem.accountIdList"
                                clearable
                                multiple
                                style="width: 180px" />
                        </FormItem>
                        <FormItem>
                            <ExDatePicker
                                clearable
                                type="date"
                                style="width:130px"
                                v-model="formItem.startTimeStart"
                                :placeholder="$t('开始时间')"></ExDatePicker>
                        </FormItem>
                        <FormItem>
                            <ExDatePicker
                                clearable
                                type="date"
                                style="width:130px"
                                :day-end="true"
                                v-model="formItem.startTimeEnd"
                                :placeholder="$t('结束时间')"></ExDatePicker>
                        </FormItem>

                        <FormItem>
                            <Button type="default" @click="loadChartData()">{{$t('查询')}}</Button>
                        </FormItem>
                    </Form>
                </div>
                <div class="chart-chart">
                    <div class="table-info">
                        <span class="table-count">{{$t('总数据统计',[pageQuery.total])}}</span>
                    </div>
                    <div v-if="list" class="table-box">
                        <table class="table-content " style="table-layout:fixed">
                            <thead>
                            <tr>
                                <th>{{$t('成员')}}</th>
                                <th class="text-center">{{$t('总任务数')}}</th>
                                <th class="text-center">{{$t('总缺陷数')}}</th>
                                <th class="text-center">{{$t('返工率')}}</th>
                                <th class="text-center">{{$t('缺陷率')}}</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr v-for="item in list" :key="item.accountId" class="table-row ">
                                <td class="text-no-wrap">
                                    {{item.accountName}}
                                </td>
                                <td class="text-no-wrap text-center">
                                    {{item.totalTaskNum}}
                                </td>
                                <td class="text-no-wrap text-center">
                                    {{item.totalBugNum}}
                                </td>
                                <td class="text-no-wrap text-center">
                                    {{ item | reopenRate }}
                                </td>
                                <td class="text-no-wrap text-center">
                                    {{ item | bugRate }}
                                </td>
                            </tr>
                            </tbody>
                        </table>
                        <div class="nodata" v-if="list.length==0">
                            {{$t('暂无数据')}}
                        </div>
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
                projectList: [],
                iterationList: [],
                formItem: {
                    projectId: null,
                    startTimeStart: null,
                    startTimeEnd: null,
                    createAccountName: null,
                    accountIdList: null,
                },
                pageQuery: {
                    totalHours: 0,
                    total: 0,
                    pageIndex: 1,
                    pageSize: 50,
                    totalPage: 1,
                },
                list: [],
            };
        },
        filters: {
            reopenRate(val) {
                if (val.reopenBugNum > 0 && val.totalBugNum > 0) {
                    return (val.reopenBugNum / val.totalBugNum * 100).toFixed(2).concat('%');
                }
                return '0%';
            },
            bugRate(val) {
                if (val.totalBugNum > 0 && val.totalTaskNum > 0) {
                    return (val.totalBugNum / val.totalTaskNum * 100).toFixed(2).concat('%');
                }
                return '0%';
            },
        },
        methods: {
            pageLoad() {
                this.loadProjectList();
                this.loadChartData();
            },
            loadProjectList() {
                app.invoke('BizAction.getMyProjectList', [app.token], list => {
                    this.projectList = list;
                });
            },
            loadChartData() {
                this.loadData(true);
            },
            loadData(resetPage) {
                if (resetPage) {
                    this.pageQuery.pageIndex = 1;
                }
                const query = copyObject(this.pageQuery, this.formItem);
               /* if (query.startTimeEnd) {
                    const startTimeEnd = new Date(query.startTimeEnd);
                    if (!Number.isNaN(startTimeEnd.getTime())) {
                        startTimeEnd.setDate(startTimeEnd.getDate() + 1);
                        query.startTimeEnd = startTimeEnd.getTime();
                    }
                }*/
                app.invoke('BizAction.getAccountBugStatList', [app.token, query], (list) => {
                    if (!Array.isArray(list)) {
                        this.list = [];
                        this.pageQuery.total = 0;
                        this.pageQuery.totalPage = 0;
                        return;
                    }
                    this.list = list;
                    this.pageQuery.total = list.length;
                });
            },
        },
    };
</script>
