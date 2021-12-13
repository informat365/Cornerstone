<style
    lang="less" scoped>

    .weekly-report {

        /deep/ .ivu-modal-header {
            padding: 0;
        }

        &-header {
            position: relative;
            height: 50px;
            display: flex;
            align-items: center;
            align-content: center;
            justify-content: space-between;

            .chart-opt-bar {
                flex: 1;
                max-width: 1024px;
                min-width: 800px;
                opacity: 0;
                transition: opacity 0.3s;
                display: none;

                &.shown {
                    opacity: 1;
                    display: flex;
                }
            }

            .title {
                width: 100px;
                padding-left: 16px;
            }
        }
    }

    .nodata {
        padding: 60px;
        font-size: 16px;
        color: #999;
        text-align: center;
    }

    .numberfont {
        padding: 0 5px;
        color: #999;
    }

    .page {
        position: relative;
        height: calc(100vh - 51px);
        background-color: #f1f4f5;
        width: 100%;
        padding: 20px;
        overflow: auto;
    }

    .chart-content {
        max-width: 1024px;
        min-width: 800px;
        margin: 0 auto;
        background-color: #fff;
        border: 1px solid #eee;
        border-radius: 5px;
        padding: 0 20px;
    }

    .chart-opt-bar {
        padding: 15px 0;
        border-bottom: 1px solid #eee;
        display: flex;
        align-items: center;
        align-content: center;

        &-date {
            flex: 1;
            display: flex;
            align-items: center;
            align-content: center;
            justify-content: space-between;
            padding: 0 8px;

            .btn-date-group,
            .btn-prev-week,
            .btn-next-week {
                cursor: pointer;
                color: #009af4;
                user-select: none;
                display: flex;
                align-items: center;
                align-content: center;
            }

            .btn-next-week.disabled {
                cursor: not-allowed;
                color: #dcdee2;
            }

            .current-week {
                font-size: 18px;
                font-weight: bold;

                .ivu-date-picker {
                    width: 150px !important;
                    font-weight: 500 !important;
                    font-size: 14px;
                    margin-right: 5px;
                }
            }
        }
    }

    .chart-opt-summary {
        padding: 20px;

        &-title {
            font-size: 16px;
            font-weight: bold;
            text-align: center;
            padding-bottom: 10px;
        }

        &-detail {
            display: flex;
            align-items: center;
            align-content: center;
            justify-content: space-around;
            padding: 0 20px;
        }

        &-card {
            text-align: center;
            width: 240px;
            &.active{
                box-shadow: 1px 1px 2px 2px #cccccc;
            }

            &-title {
                font-size: 14px;
                font-weight: 500;
                color: #111;
            }

            &-number {
                padding-top: 5px;
                font-size: 32px;
            }

            &.finish {
                color: #0ab169;
            }

            &.delay {
                color: #ed4014;
            }

            &.create {
                color: #009af4;
            }
        }
    }

    .chart-opt-rank {
        display: flex;
        justify-content: space-between;

        &-list {
            width: 48%;
            padding: 0 10px;

            &-title {
                padding-bottom: 10px;
                font-size: 15px;
                font-weight: bold;
                border-bottom: solid 2px #eee;
            }

            &-wrap {
                position: relative;
                max-height: 360px;
                overflow: auto;
                overflow-scrolling: touch;
            }

            &-row {
                height: 40px;
                display: flex;
                align-items: center;
                align-content: center;

                &:hover {
                    background-color: #ebf7ff;
                }

                &-rank {
                    font-size: 15px;
                    font-weight: 500;
                    padding: 0 10px;
                }

                &-desc {
                    flex: 1;
                    text-align: right;
                    padding-right: 10px;
                }
            }

            &.delay {
                .chart-opt-rank-list-row-desc {
                    color: #ed4014;
                }
            }
        }
    }

    .chart-opt-delay-list {
        padding: 20px;

        &-title {
            padding-bottom: 10px;
            font-size: 16px;
            font-weight: bold;
            border-bottom: solid 3px #eee;
        }

        &-row {
            height: 40px;
            display: flex;
            align-items: center;
            align-content: center;

            &.account {
                position: relative;
                padding: 0 10px;

                &:before {
                    position: absolute;
                    top: 50%;
                    left: 0;
                    content: "";
                    background-color: #2391ff;
                    height: 12px;
                    width: 3px;
                    margin-top: -6px;
                    display: block;
                }
            }

            &.task {
                padding: 0 20px;
                cursor: pointer;
            }

            &.task:hover {
                background-color: #ebf7ff;
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

            &-date {
                color: #ed4014;
            }
        }
    }
</style>
<i18n>
    {
    "en": {
    "项目周报": "Project chart",
    "整体报告": "Project weekly summary stat",
    "项目": "Project",
    "部门名称": "Department Name",
    "上一周": "Last Week",
    "下一周": "Next Week",
    "本周": "This Week",
    "上个月": "Last Month",
    "下个月": "Next Month",
    "本月": "This Month",
    "自定义": "Custom",
    "周": "Week",
    "月": "Month",
    "完成任务数量": "Number of completed tasks",
    "逾期任务数量": "Number of delayed tasks",
    "新增任务数量": "Number of new tasks",
    "完成任务数量排行": "Rank the number of tasks completed",
    "逾期任务数量排行": "Rank the number of tasks delayed",
    "逾期的任务": "Delayed tasks",
    "完成的任务":"Finished tasks",
    "新增的任务":"Created tasks",
    "暂无数据记录":"No records",
    "完成任务":"{0} tasks has been completed",
    "延期任务":"{0} tasks has been delayed"
    },
    "zh_CN": {
    "项目周报":"项目报表",
    "整体报告":"整体报告",
    "项目":"项目",
    "部门名称":"部门名称",
    "上一周": "上一周",
    "下一周": "下一周",
    "本周": "本周",
    "上个月": "上个月",
    "下个月": "下个月",
    "本月": "本月",
    "自定义": "自定义",
    "周": "周",
    "月": "月",
    "完成任务数量":"完成任务数量",
    "逾期任务数量":"逾期任务数量",
    "新增任务数量":"新增任务数量",
    "完成任务数量排行":"完成任务数量排行",
    "逾期任务数量排行":"逾期任务数量排行",
    "逾期的任务":"逾期的任务",
    "完成的任务":"完成的任务",
    "新增的任务":"新增的任务",
    "暂无数据记录":"暂无数据记录",
    "完成任务":"完成{0}项任务",
    "延期任务":"有{0}项任务延期"
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
        class="fullscreen-modal weekly-report"
        width="100%"
        :footer-hide="true">
        <div
            class="weekly-report-header" slot="header">
            <div class="title">
                {{ $t('项目周报') }}
            </div>
            <div
                class="chart-opt-bar" :class="{ shown: showTopOptBar }">
                <Select
                    v-model="formItem.projectIdInList"
                    :placeholder="$t('项目')"
                    transfer
                    clearable
                    multiple
                    filterable
                    style="width:150px"
                    @on-change="loadData"
                    @on-clear="loadData">
                    <Option
                        v-for="item in projectList" :value="item.id" :key="'prj'+item.id">{{ item.name }}
                    </Option>
                </Select>
<!--                <ListSelect placeholder="项目"-->
<!--                            :multiple="true"-->
<!--                            :data-list="projectList"-->
<!--                            @on-change="loadData"-->
<!--                            v-model="formItem.projectIdInList"-->
<!--                            placement="bottom-start"></ListSelect>-->
                <DepartmentTreeSelect
                    v-model="formItem.departmentId" clearable style="width:200px;margin:0 20px" @on-change="loadData"/>
                <CompanyUserSelect
                    v-model="formItem.accountIds"
                    clearable
                    multiple
                    style="width: 150px;margin-right: 20px;"
                    @on-change="loadData"/>
                <div class="chart-opt-bar-date">
                    <div class="btn-date-group">
                        <RadioGroup v-model="datemode" type="button" size="small">
                            <Radio label="week">{{$t('周')}}</Radio>
                            <Radio label="month">{{$t('月')}}</Radio>
                            <Radio label="custom">{{$t('自定义')}}</Radio>
                        </RadioGroup>
                    </div>
                    <div v-if="datemode!='custom'"
                         @click="onClickPreWeek" class="btn-prev-week">
                        <Icon type="ios-arrow-dropleft-circle" size="18"/>
                        {{datemode=='week'?$t('上一周'):$t('上个月')}}
                    </div>
                    <div class="current-week" v-if="datemode!='custom'">
                        <span>{{ formItem.startDate | fmtDate }} - {{ formItem.endDate | fmtDate }}</span>
                        <span v-if="isCurrentWeek"> {{ datemode=='week'?$t('本周'):$t('本月')}}</span>
                    </div>
                    <div class="current-week" v-else>
                        <DatePicker type="datetime" @on-change="startDateChange" @on-ok="loadData" transfer confirm v-model="formItem.startDate"  :placeholder="$t('自定义')"/>
                        <DatePicker type="datetime" @on-change="endDateChange" @on-ok="loadData" transfer confirm v-model="formItem.endDate" :placeholder="$t('自定义')"/>
                    </div>
                    <div v-if="datemode!='custom'"
                         @click="onClickNextWeek" :class="{ disabled:isCurrentWeek }" class="btn-next-week">
                        {{datemode=='week'?$t('下一周'):$t('下个月')}}
                        <Icon type="ios-arrow-dropright-circle" size="18"/>
                    </div>
                </div>
            </div>
            <div class="title"></div>
        </div>
        <div
            class="page scrollbox" @scroll="onScroll">
            <div class="chart-content">
                <div class="chart-opt-bar">
<!--                    <Select-->
<!--                        v-model="formItem.projectId"-->
<!--                        :placeholder="$t('项目')"-->
<!--                        transfer-->
<!--                        clearable-->
<!--                        style="width:150px"-->
<!--                        @on-change="loadData"-->
<!--                        @on-clear="loadData">-->
<!--                        <Option-->
<!--                            v-for="item in projectList" :value="item.id" :key="'prj'+item.id">{{ item.name }}-->
<!--                        </Option>-->
<!--                    </Select>-->
                    <ListSelect placeholder="项目"
                                :multiple="true"
                                :data-list="projectList"
                                v-model="formItem.projectIdInList"
                                @on-change="loadData"
                                placement="bottom-start"></ListSelect>
                    <DepartmentTreeSelect
                        v-model="formItem.departmentId"
                        clearable
                        style="width:150px;margin:0 20px"
                        @on-change="loadData"/>
                    <CompanyUserSelect
                        v-model="formItem.accountIds"
                        clearable
                        multiple
                        style="width: 150px;margin-right: 20px;"
                        @on-change="loadData"/>
                    <div class="chart-opt-bar-date">
                        <div class="btn-date-group">
                            <RadioGroup v-model="datemode" type="button" size="small">
                                <Radio label="week">{{$t('周')}}</Radio>
                                <Radio label="month">{{$t('月')}}</Radio>
                                <Radio label="custom">{{$t('自定义')}}</Radio>
                            </RadioGroup>
                        </div>
                        <div v-if="datemode!='custom'"
                             @click="onClickPreWeek" class="btn-prev-week">
                            <Icon type="ios-arrow-dropleft-circle" size="18"/>
                            {{datemode=='week'?$t('上一周'):$t('上个月')}}
                        </div>
                        <div class="current-week" v-if="datemode!='custom'">
                            <span>{{ formItem.startDate | fmtDate }} - {{ formItem.endDate | fmtDate }}</span>
                            <span v-if="isCurrentWeek"> {{ datemode=='week'?$t('本周'):$t('本月')}}</span>
                        </div>
                        <div class="current-week" v-else>
                            <DatePicker type="datetime" @on-change="startDateChange" @on-ok="loadData" transfer confirm v-model="formItem.startDate"  :placeholder="$t('自定义')"/>
                            <DatePicker type="datetime" @on-change="endDateChange" @on-ok="loadData" transfer confirm v-model="formItem.endDate" :placeholder="$t('自定义')"/>
                        </div>
                        <div v-if="datemode!='custom'"
                             @click="onClickNextWeek" :class="{ disabled:isCurrentWeek }" class="btn-next-week">
                            {{datemode=='week'?$t('下一周'):$t('下个月')}}
                            <Icon type="ios-arrow-dropright-circle" size="18"/>
                        </div>
                    </div>
                </div>
                <div class="chart-opt-summary">
                    <div class="chart-opt-summary-title">
                        {{ $t('整体报告') }}
                    </div>
                    <div class="chart-opt-summary-detail">
                        <Card :class="{active:taskShowIdx==1}" class="chart-opt-summary-card finish" @click.native="showTaskList(1)">
                            <div class="chart-opt-summary-card-title">
                                {{ $t('完成任务数量') }}
                            </div>
                            <div class="chart-opt-summary-card-number">
                                {{ report.finishTaskNum }}
                            </div>
                        </Card>
                        <Card :class="{active:taskShowIdx==2}" class="chart-opt-summary-card delay" @click.native="showTaskList(2)">
                            <div class="chart-opt-summary-card-title">
                                {{ $t('逾期任务数量') }}
                            </div>
                            <div class="chart-opt-summary-card-number">
                                {{ report.delayTaskNum }}
                            </div>
                        </Card>
                        <Card :class="{active:taskShowIdx==3}" class="chart-opt-summary-card create" @click.native="showTaskList(3)">
                            <div class="chart-opt-summary-card-title">
                                {{ $t('新增任务数量') }}
                            </div>
                            <div class="chart-opt-summary-card-number">
                                {{ report.createTaskNum }}
                            </div>
                        </Card>
                    </div>
                </div>
                <div class="chart-opt-rank">
                    <div class="chart-opt-rank-list finish">
                        <div class="chart-opt-rank-list-title">
                            {{ $t('完成任务数量排行') }}
                        </div>
                        <div class="chart-opt-rank-list-wrap scrollbox">
                            <template v-for="(account,index) in report.finishTaskAccountList">
                                <div
                                    :key="account.accountId" class="chart-opt-rank-list-row">
                                    <div class="chart-opt-rank-list-row-rank">
                                        {{ index+1 }}
                                    </div>
                                    <AvatarImage
                                        class="chart-opt-rank-list-row-account"
                                        size="small"
                                        :name="account.accountName"
                                        v-model="account.accountImageId"
                                        type="label"/>
                                    <div class="chart-opt-rank-list-row-desc">
                                        {{ $t('完成任务',null,[account.num]) }}
                                    </div>
                                </div>
                            </template>
                            <template
                                v-if="!!report.finishTaskAccountList && report.finishTaskAccountList.length === 0">
                                <div class="nodata">{{ $t('暂无数据记录') }}</div>
                            </template>
                        </div>
                    </div>
                    <div class="chart-opt-rank-list delay">
                        <div class="chart-opt-rank-list-title">
                            {{ $t('逾期任务数量排行') }}
                        </div>
                        <div class="chart-opt-rank-list-wrap scrollbox">
                            <template v-for="(account,index) in report.delayTaskAccountList">
                                <div
                                    :key="account.accountId" class="chart-opt-rank-list-row">
                                    <div class="chart-opt-rank-list-row-rank">
                                        {{ index+1 }}
                                    </div>
                                    <AvatarImage
                                        class="chart-opt-rank-list-row-account"
                                        size="small"
                                        :name="account.accountName"
                                        v-model="account.accountImageId"
                                        type="label"/>
                                    <div class="chart-opt-rank-list-row-desc">
                                        {{ $t('延期任务',null,[account.num]) }}
                                    </div>
                                </div>
                            </template>
                            <template v-if="!!report.delayTaskAccountList && report.delayTaskAccountList.length === 0">
                                <div class="nodata">{{ $t('暂无数据记录') }}</div>
                            </template>
                        </div>
                    </div>
                </div>
                <div class="chart-opt-delay-list">
                    <div class="chart-opt-delay-list-title">
                        {{ taskShowIdx==2?$t('逾期的任务'):(taskShowIdx==1?$t('完成的任务'):$t('新增的任务')) }}
                    </div>
                    <div class="chart-opt-delay-list-wrap scrollbox">
                        <template v-for="(account) in taskShowList">
                            <div
                                :key="'da-'+account.accountId" class="chart-opt-delay-list-row account">
                                <AvatarImage
                                    size="small"
                                    :name="account.accountName"
                                    v-model="account.accountImageId"
                                    type="label"/>
                            </div>
                            <template v-for="item in account.taskList">
                                <div
                                    @click.stop="showTaskInfo(item)"
                                    :key="'dt-'+account.accountId+'-'+item.taskId"
                                    class="chart-opt-delay-list-row task">
                                    <Tag>{{ item.objectTypeName }}</Tag>
                                    <div class="chart-opt-delay-list-row-name">
                                        <span class="task-project-name">
                                            {{ item.projectName }}
                                        </span>
                                        <span class="task-no numberfont">#{{ item.taskSerialNo }}</span>
                                        <span class="task-name">
                                          {{ item.taskName }}
                                        </span>
                                    </div>
                                    <div class="chart-opt-delay-list-row-date">
                                        <Icon
                                            type="md-calendar" size="16" style="margin-right: 3px;"/>
                                        <span v-if="taskShowIdx==1">{{ item.finishDate | fmtDate }}</span>
                                        <span v-else-if="taskShowIdx==2">{{ item.endDate | fmtDate }}</span>
                                        <span v-else>{{ item.createDate | fmtDate }}</span>

                                    </div>
                                </div>
                            </template>
                        </template>
                        <template v-if="!!taskShowList && taskShowList.length === 0">
                            <div class="nodata">{{ $t('暂无数据记录') }}</div>
                        </template>
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
                    departmentId: null,
                    projectId: null,
                    accountIds: null,
                    startDate: null,
                    endDate: null,
                },
                loadTimeoutId: 0,
                report: {},
                accountDelayTaskList: null,
                accountFinishTaskList: null,
                accountCreataTaskList: null,
                projectList: [],
                scrollTop: 0,
                datemode: "week",
                taskShowIdx:2,
                taskShowList:[]
            };
        },
        watch: {
            datemode(val, oldVal) {
                let now = new Date();
                let year = now.getFullYear();
                let month = now.getMonth();
                let day = now.getDate();
                let dayOfWeek = now.getDay();
                if (val == 'week') {
                    let startDate = new Date(year, month, day);
                    startDate = new Date(startDate.getTime() - (dayOfWeek-1) * 24 * 3600 * 1000);
                    let endDate = new Date(startDate.getTime() + 6 * 24 * 3600 * 1000);
                    this.formItem.startDate = startDate;
                    this.formItem.endDate = endDate;
                } else if (val == 'month') {
                    let startDate = new Date(year, month, 1);
                    month+=1;
                    if (month >= 12) {
                        month = month - 12;
                        year = year + 1;
                    }
                    let endDate = new Date(year, month, 1);
                    endDate = new Date(endDate.getTime() - 1);
                    this.formItem.startDate = startDate;
                    this.formItem.endDate = endDate;
                }
            }
        },
        computed: {
            isCurrentWeek() {
                if (this.formItem.startDate === null || this.formItem.endDate === null) {
                    return false;
                }
                const now = new Date();
                return this.formItem.startDate.getTime() <= now.getTime() && now.getTime() <= this.formItem.endDate.getTime();
            },
            showTopOptBar() {
                return this.scrollTop > 80;
            },
        },
        created() {
            const now = new Date();
            const startDate = new Date(now.getFullYear(), now.getMonth(), now.getDate() - now.getDay() + 1);
            const endDate = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate() + 6, 23, 59, 59, 999);
            this.formItem.startDate = startDate;
            this.formItem.endDate = endDate;
        },
        methods: {
            pageLoad() {
                this.loadData();
                this.loadProjectList();
            },
            loadProjectList() {
                app.invoke('BizAction.getMyProjectList', [app.token], list => {
                    this.projectList = list;
                });
            },
            startDateChange(e){
                this.formItem.startDate = e;
            },
            endDateChange(e){
                this.formItem.endDate = e;
            },
            showTask(){
                if(this.taskShowIdx===1){
                    this.taskShowList = this.accountFinishTaskList;
                }else if(this.taskShowIdx===2){
                    this.taskShowList = this.accountDelayTaskList;
                }else{
                    this.taskShowList = this.accountCreateTaskList;
                }
            },
            showTaskList(idx){
                console.log("showTaskList--->",idx)
                this.taskShowIdx = idx;
                this.showTask();
            },
            onScroll(event) {
                this.scrollTop = event.target.scrollTop;
            },
            onClickPreWeek() {
                if(this.datemode=='week'){
                    this.formItem.startDate = new Date(this.formItem.startDate.getFullYear(), this.formItem.startDate.getMonth(), this.formItem.startDate.getDate() - 7);
                    this.formItem.endDate = new Date(this.formItem.endDate.getFullYear(), this.formItem.endDate.getMonth(), this.formItem.endDate.getDate() - 7);
                }else if(this.datemode=='month'){
                    let year = this.formItem.startDate.getFullYear();
                    let month = this.formItem.startDate.getMonth();
                    month-=1;
                    if (month < 0) {
                        month = month + 12;
                        year = year - 1;
                    }
                    let startDate = new Date(year, month, 1);
                    month+=1;
                    if (month >= 12) {
                        month = month - 12;
                        year = year + 1;
                    }
                    let endDate = new Date(year, month, 1);
                    endDate = new Date(endDate.getTime() - 1);
                    this.formItem.startDate = startDate;
                    this.formItem.endDate = endDate;
                }
                this.loadData();
            },
            onClickNextWeek() {
                if (this.isCurrentWeek) {
                    return;
                }
                if(this.datemode=='week'){
                    this.formItem.startDate = new Date(this.formItem.startDate.getFullYear(), this.formItem.startDate.getMonth(), this.formItem.startDate.getDate() + 7);
                    this.formItem.endDate = new Date(this.formItem.endDate.getFullYear(), this.formItem.endDate.getMonth(), this.formItem.endDate.getDate() + 7);
                }else if(this.datemode=='month'){
                    let year = this.formItem.startDate.getFullYear();
                    let month = this.formItem.startDate.getMonth();
                    month+=1;
                    if (month >= 12) {
                        month = month - 12;
                        year = year + 1;
                    }
                    let startDate = new Date(year, month, 1);
                    month+=1;
                    if (month >= 12) {
                        month = month - 12;
                        year = year + 1;
                    }
                    let endDate = new Date(year, month, 1);
                    endDate = new Date(endDate.getTime() - 1);
                    this.formItem.startDate = startDate;
                    this.formItem.endDate = endDate;
                }
                this.loadData();
            },
            loadData() {
                clearTimeout(this.loadTimeoutId);
                this.loadTimeoutId = setTimeout(() => {
                    app.invoke('BizAction.getCompanyTaskReport', [app.token, this.formItem], report => {
                        this.report = {
                            createTaskNum: report.createTaskNum || 0,
                            delayTaskNum: report.delayTaskNum || 0,
                            finishTaskNum: report.finishTaskNum || 0,
                            delayTaskAccountList: report.delayTaskAccountList || [],
                            finishTaskAccountList: report.finishTaskAccountList || [],
                            createTaskAccountList: report.createTaskAccountList || [],
                        };
                        //延期任务
                        if (!Array.isArray(report.delayTaskAccountList) || !Array.isArray(report.delayTaskList)) {
                            this.accountDelayTaskList = [];
                        }else{
                            const delayTaskObj = {};
                            report.delayTaskAccountList.forEach((account, index) => {
                                const key = 'dt_' + account.accountId;
                                delayTaskObj[key] = {
                                    accountId: account.accountId,
                                    accountImageId: account.accountImageId,
                                    accountName: account.accountName,
                                    taskList: [],
                                    index,
                                };
                            });
                            report.delayTaskList.forEach(item => {
                                const key = 'dt_' + item.accountId;
                                let accountTaskObj = delayTaskObj[key];
                                if (!accountTaskObj) {
                                    return;
                                }
                                if (!Array.isArray(accountTaskObj.taskList)) {
                                    accountTaskObj.taskList = [];
                                }
                                accountTaskObj.taskList.push(item);
                                delayTaskObj[key] = accountTaskObj;
                            });
                            const accountDelayTaskList = [];
                            Object.keys(delayTaskObj).forEach(key => {
                                const accountTaskObj = delayTaskObj[key];
                                if (!accountTaskObj) {
                                    return;
                                }
                                accountDelayTaskList.push(accountTaskObj);
                            });
                            accountDelayTaskList.sort((a, b) => a.index - b.index);
                            this.accountDelayTaskList = accountDelayTaskList;
                        }
                        //完成任务
                        if (!Array.isArray(report.finishTaskAccountList) || !Array.isArray(report.finishTaskList)) {
                            this.accountFinishTaskList = [];
                        }else{
                            const delayTaskObj = {};
                            report.finishTaskAccountList.forEach((account, index) => {
                                const key = 'dt_' + account.accountId;
                                delayTaskObj[key] = {
                                    accountId: account.accountId,
                                    accountImageId: account.accountImageId,
                                    accountName: account.accountName,
                                    taskList: [],
                                    index,
                                };
                            });
                            report.finishTaskList.forEach(item => {
                                const key = 'dt_' + item.accountId;
                                let accountTaskObj = delayTaskObj[key];
                                if (!accountTaskObj) {
                                    return;
                                }
                                if (!Array.isArray(accountTaskObj.taskList)) {
                                    accountTaskObj.taskList = [];
                                }
                                accountTaskObj.taskList.push(item);
                                delayTaskObj[key] = accountTaskObj;
                            });
                            const accountDelayTaskList = [];
                            Object.keys(delayTaskObj).forEach(key => {
                                const accountTaskObj = delayTaskObj[key];
                                if (!accountTaskObj) {
                                    return;
                                }
                                accountDelayTaskList.push(accountTaskObj);
                            });
                            accountDelayTaskList.sort((a, b) => a.index - b.index);
                            this.accountFinishTaskList = accountDelayTaskList;
                        }
                        //新增任务
                        if (!Array.isArray(report.createTaskAccountList) || !Array.isArray(report.createTaskList)) {
                            this.accountCreateTaskList = [];
                        }else{
                            const delayTaskObj = {};
                            report.createTaskAccountList.forEach((account, index) => {
                                const key = 'dt_' + account.accountId;
                                delayTaskObj[key] = {
                                    accountId: account.accountId,
                                    accountImageId: account.accountImageId,
                                    accountName: account.accountName,
                                    taskList: [],
                                    index,
                                };
                            });
                            report.createTaskList.forEach(item => {
                                const key = 'dt_' + item.accountId;
                                let accountTaskObj = delayTaskObj[key];
                                if (!accountTaskObj) {
                                    return;
                                }
                                if (!Array.isArray(accountTaskObj.taskList)) {
                                    accountTaskObj.taskList = [];
                                }
                                accountTaskObj.taskList.push(item);
                                delayTaskObj[key] = accountTaskObj;
                            });
                            const accountDelayTaskList = [];
                            Object.keys(delayTaskObj).forEach(key => {
                                const accountTaskObj = delayTaskObj[key];
                                if (!accountTaskObj) {
                                    return;
                                }
                                accountDelayTaskList.push(accountTaskObj);
                            });
                            accountDelayTaskList.sort((a, b) => a.index - b.index);
                            this.accountCreateTaskList = accountDelayTaskList;
                        }
                        this.showTask();
                    }, () => {
                        this.report = {
                            createTaskNum: 0,
                            delayTaskNum: 0,
                            finishTaskNum: 0,
                            delayTaskAccountList: [],
                            finishTaskAccountList: [],
                            createTaskAccountList: [],
                        };
                        this.accountDelayTaskList = [];
                        this.accountFinishTaskList = [];
                        this.accountCreateTaskList = [];
                        this.showTask();
                    });


                    this.taskShowList = this.accountDelayTaskList;

                }, 250);
            },
            showTaskInfo(item) {
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
