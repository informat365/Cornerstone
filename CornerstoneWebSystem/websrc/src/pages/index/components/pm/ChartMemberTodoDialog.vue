<style
        lang="less" scoped>

    .selected-btn {
        color: #0097F7 !important;

    }

    .gantt_tooltip {
        z-index: 9999 !important;
    }

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
                flex-wrap: nowrap;

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
        /*max-width: 1024px;*/
        width: 90%;
        /*min-width: 800px;*/
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
        flex-wrap: nowrap;

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
        width: 100%;

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
    "天": "Day",
    "季度": "Quarter",
    "季": "Quarter",
    "年": "Year",
    "类型": "Type",
    "全局人力视图":"Member todo list",
    "待办人力":"Todo list",
    "选择项目":"Choose project",
    "创建":"Create",
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
    "天": "天",
    "季度": "季度",
    "季": "季",
    "年": "年",
    "类型": "类型",
    "全局人力视图":"全局人力视图",
    "待办人力":"待办人力",
    "选择项目":"选择项目",
    "创建":"创建",
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
                {{ $t('全局人力视图') }}
            </div>
            <div
                    class="chart-opt-bar" :class="{ shown: showTopOptBar }">
               <!-- <Select
                        v-model="formItem.projectId"
                        :placeholder="$t('项目')"
                        transfer
                        clearable
                        style="width:150px"
                        @on-change="loadData"
                        @on-clear="loadData">
                    <Option
                            v-for="item in projectList" :value="item.id" :key="'prj'+item.id">{{ item.name }}
                    </Option>
                </Select>-->
<!--                <ListSelect placeholder="项目"-->
<!--                            :multiple="true"-->
<!--                            :data-list="projectList"-->
<!--                            @on-change="loadData"-->
<!--                            v-model="formItem.projectIdInList"-->
<!--                            placement="bottom-start"></ListSelect>-->
                <Select
                    v-model="formItem.projectIdInList"
                    :placeholder="$t('项目')"
                    transfer
                    multiple
                    clearable
                    filterable
                    style="width:150px"
                    @on-change="loadData"
                    @on-clear="loadData">
                    <Option
                        v-for="item in projectList" :value="item.id" :key="'prj'+item.id">{{ item.name }}
                    </Option>
                </Select>
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
                            <Radio label="quarter">{{$t('季')}}</Radio>
                            <Radio label="year">{{$t('年')}}</Radio>
                            <Radio label="custom">{{$t('自定义')}}</Radio>
                        </RadioGroup>
                    </div>
                    <div v-if="datemode!='custom'"
                         @click="onClickPreWeek" class="btn-prev-week">
                        <Icon type="ios-arrow-dropleft-circle" size="18"/>
                        {{'上一'+currentPeriodName}}
                    </div>
                    <div class="current-week" v-if="datemode!='custom'">
                        <span>{{ formItem.startDate | fmtDate }} - {{ formItem.endDate | fmtDate }}</span>
                        <span v-if="isCurrentWeek"> {{'本'+currentPeriodName}}</span>
                    </div>
                    <div class="current-week" v-else>
                        <DatePicker type="datetime" @on-change="startDateChange" @on-ok="loadData" transfer confirm v-model="formItem.startDate" :placeholder="$t('自定义')"/>
                        <DatePicker type="datetime" @on-change="endDateChange" @on-ok="loadData" transfer confirm v-model="formItem.endDate" :placeholder="$t('自定义')"/>
                    </div>
                    <div v-if="datemode!='custom'"
                         @click="onClickNextWeek" :class="{ disabled:isCurrentWeek }" class="btn-next-week">
                        {{'下一'+currentPeriodName}}
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
<!--                            v-model="formItem.projectId"-->
<!--                            :placeholder="$t('项目')"-->
<!--                            transfer-->
<!--                            clearable-->
<!--                            style="width:150px"-->
<!--                            @on-change="loadData"-->
<!--                            @on-clear="loadData">-->
<!--                        <Option-->
<!--                                v-for="item in projectList" :value="item.id" :key="'prj'+item.id">{{ item.name }}-->
<!--                        </Option>-->
<!--                    </Select>-->
                    <ListSelect placeholder="项目"
                                :multiple="true"
                                :data-list="projectList"
                                @on-change="loadData"
                                v-model="formItem.projectIdInList"
                                placement="bottom-start"></ListSelect>
                    <DepartmentTreeSelect
                            v-model="formItem.departmentId"
                            clearable
                            style="width:150px;margin:0 20px;"
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
                                <Radio label="quarter">{{$t('季')}}</Radio>
                                <Radio label="year">{{$t('年')}}</Radio>
                                <Radio label="custom">{{$t('自定义')}}</Radio>
                            </RadioGroup>
                        </div>
                        <div v-if="datemode!='custom'"
                             @click="onClickPreWeek" class="btn-prev-week">
                            <Icon type="ios-arrow-dropleft-circle" size="18"/>
                            {{'上一'+currentPeriodName}}
                        </div>
                        <div class="current-week" v-if="datemode!='custom'">
                            <span>{{ formItem.startDate | fmtDate }} - {{ formItem.endDate | fmtDate }}</span>
                            <span v-if="isCurrentWeek"> {{ '本'+currentPeriodName}}</span>
                        </div>
                        <div class="current-week" v-else>
                            <DatePicker type="datetime" @on-change="startDateChange" @on-ok="loadData" transfer confirm v-model="formItem.startDate" :placeholder="$t('自定义')"/>
                            <DatePicker type="datetime" @on-change="endDateChange" @on-ok="loadData" transfer confirm v-model="formItem.endDate" :placeholder="$t('自定义')"/>
                        </div>
                        <div v-if="datemode!='custom'"
                             @click="onClickNextWeek" :class="{ disabled:isCurrentWeek }" class="btn-next-week">
                            {{'下一'+currentPeriodName}}
                            <Icon type="ios-arrow-dropright-circle" size="18"/>
                        </div>
                    </div>

                </div>
                <div style="margin-top: 3px;">
                    <IconButton :class="{'selected-btn':viewType=='day'}" @click="setViewType('day')"
                                :title="$t('天')"></IconButton>
                    <IconButton :class="{'selected-btn':viewType=='week'}" @click="setViewType('week')"
                                :title="$t('周')"></IconButton>
                    <IconButton :class="{'selected-btn':viewType=='month'}" @click="setViewType('month')"
                                :title="$t('月')"></IconButton>
                    <IconButton :class="{'selected-btn':viewType=='quarter'}" @click="setViewType('quarter')"
                                :title="$t('季度')"></IconButton>

                    <Poptip transfer ref="selectPoptip" class="poptip-full" trigger="click">
                        <Button type="default" size="small">
                            {{$t('创建')}}
                        </Button>
<!--                        <IconButton  tips="快速创建" :size="20"></IconButton>-->
                        <div slot="content" style="width:270px;padding:20px">

                            <div class="row">
                                <Select transfer v-model="createFrom.projectId" :placeholder="$t('选择项目')" style="width:100%">
                                    <Option v-for="item in projectList" :value="item.id" :key="'prj'+item.id">{{ item.name }}</Option>
                                </Select>
                            </div>
                            <div class="row" style="margin-top: 5px;">
                                <Select transfer clearable v-model="createFrom.objectType" :placeholder="$t('类型')" style="width:100%">
                                    <template v-for="item in createModuleList">
                                        <Option :value="item.objectType" :key="'md'+item.id">{{ item.name }}</Option>
                                    </template>
                                </Select>
                            </div>

                            <div class="row" style="text-align:center;margin-top:20px">
                                <Button  :disabled="createFrom.objectType==null" @click="createTask" type="default">
                                    {{$t('创建')}}
                                </Button>
                            </div>

                        </div>
                    </Poptip>
                    <!-- <Poptip
                             :disabled="!formItem.projectId"
                             trigger="hover"
                             transfer
                             class="poptip-full">
                         <Button :disabled="!formItem.projectId"  type="default">{{$t('创建')}}</Button>
                         <div slot="content">
                             <div class="popup-box scrollbox">
                                 <div v-for="(moudle,idx) in moduleList" :key="idx" v-if="moudle.isStatusBased"
                                      @click="createTask(moudle.objectType)"
                                      class="popup-item-name">{{moudle.name}}
                                 </div>
                             </div>
                         </div>
                     </Poptip>-->
                </div>
                <div class="chart-opt-summary">
                    <div class="gantt-container" ref="todoGantt" style="min-height: 700px;">
                    </div>
                </div>
            </div>
        </div>
    </Modal>
</template>

<script>
    import ListSelect from "../../../../components/ui/ListSelect";

    import "../../../../../public/js/dhtml_gantt/ext/dhtmlxgantt_tooltip.js";

    export default {
        name: "ChartMemberTodoDialog",
        mixins: [componentMixin],
        components: {ListSelect},
        data() {
            return {
                formItem: {
                    departmentId: null,
                    projectId: null,
                    accountIds: null,
                    startDate: null,
                    endDate: null,
                },
                createFrom: {
                    projectId: null,
                    objectType: null
                },
                loadTimeoutId: 0,
                report: {},
                projectList: [],
                scrollTop: 0,
                datemode: "week",
                viewType: 'week',
                taskShowIdx: 2,
                taskShowList: [],
                accountTodoTaskList: [],
                ganttActive: false,
                moduleList: [],
                showResource: false,
                showWorkday: false,
                createModuleList: []
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
                    startDate = new Date(startDate.getTime() - (dayOfWeek - 1) * 24 * 3600 * 1000);
                    let endDate = new Date(startDate.getTime() + 6 * 24 * 3600 * 1000);
                    this.formItem.startDate = startDate;
                    this.formItem.endDate = endDate;
                } else if (val == 'month') {
                    let startDate = new Date(year, month, 1);
                    month += 1;
                    if (month >= 12) {
                        month = month - 12;
                        year = year + 1;
                    }
                    let endDate = new Date(year, month, 1);
                    endDate = new Date(endDate.getTime() - 1);
                    this.formItem.startDate = startDate;
                    this.formItem.endDate = endDate;
                } else if (val == 'quarter') {
                    let initMonth = 0;
                    if (month >= 3) {
                        initMonth = 3;
                    }
                    if (month >= 6) {
                        initMonth = 6;
                    }
                    if (month >= 9) {
                        initMonth = 9;
                    }
                    let startDate = new Date(year, initMonth, 1);
                    initMonth += 3;
                    if (initMonth >= 12) {
                        initMonth = initMonth - 12;
                        year = year + 1;
                    }
                    let endDate = new Date(year, initMonth, 1);
                    endDate = new Date(endDate.getTime() - 1);
                    this.formItem.startDate = startDate;
                    this.formItem.endDate = endDate;
                } else if (val == 'year') {
                    let startDate = new Date(year, 0, 1);
                    let endDate = new Date(year + 1, 0, 1);
                    endDate = new Date(endDate.getTime() - 1);
                    this.formItem.startDate = startDate;
                    this.formItem.endDate = endDate;
                }

                this.loadData();
            },
            'formItem.projectId'(val, oldVal) {
                this.loadProjectModule(this.formItem.projectId, 1);
            },
            'createFrom.projectId'(val, oldVal) {
                console.log("xxxx222")
                this.loadProjectModule(this.createFrom.projectId, 2);
            }
        },
        computed: {
            currentPeriodName() {
                if (this.datemode === 'week') {
                    return '周'
                } else if (this.datemode === 'month') {
                    return '月'
                } else if (this.datemode === 'quarter') {
                    return '季'
                } else if (this.datemode === 'year') {
                    return '年'
                }
            },
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
            this.ganttChart = gantt;

            const now = new Date();
            const startDate = new Date(now.getFullYear(), now.getMonth(), now.getDate() - now.getDay() + 1);
            const endDate = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate() + 6, 23, 59, 59, 999);
            this.formItem.startDate = startDate;
            this.formItem.endDate = endDate;
        },
        beforeDestroy() {
            console.log("before destory todo list")
            this.ganttActive = false;
            this.ganttChart.clearAll();
            if (typeof this.ganttChart.clearRedoStack === "function") {
                this.ganttChart.clearRedoStack();
            }
            if (typeof this.ganttChart.clearUndoStack === "function") {
                this.ganttChart.clearUndoStack();
            }
            //移除绑定事件
            this.ganttChart.detachEvent('onTaskDblClick');
            this.ganttChart.detachEvent('onBeforeTaskDrag');
            this.ganttChart.detachEvent('onAfterTaskUpdate');
            this.ganttChart.detachEvent('onParse');
            if (this.resourcesStore && typeof this.resourcesStore.destructor === "function") {
                this.resourcesStore.destructor();
            }
            console.log(this.resourcesStore);
            //清空定时更新
            clearInterval(this.todayMarkerInterval);
        },
        mounted() {
            try {
                this.initGantt();
                this.pageLoad();
            } catch (e) {
                console.error(e)
            }
        },
        methods: {

            initGantt() {
                var ganttChart = this.ganttChart;
                ganttChart.templates.task_text = function (start, end, task) {
                    return "#" + task.serialNo + "<b style='margin-left:5px'>" + encodeHTML(task.text) + "</b> ";
                };
                ganttChart.templates.scale_cell_class = function (date) {
                    if (date.getDay() == 0 || date.getDay() == 6) {
                        return "weekend";
                    }
                };

                // ganttChart.plugins({
                //     tooltip: true
                // });
                ganttChart.templates.tooltip_text = function (start, end, task) {
                    return "<b>任务:</b> " + task.text + "<br/><b>时长:</b> " + task.duration +
                        "天<br/><b>开始时间:</b>" + formatDate(start) + "<br/><b>截止时间:</b>" + formatDate(end);
                };

                ganttChart.templates.resource_cell_class = function (start_date, end_date, resource, tasks) {
                    var css = [];
                    css.push("resource_marker");
                    return css.join(" ");
                };
                ganttChart.templates.resource_cell_value = function (start_date, end_date, resource, tasks) {
                    return "<div>" + tasks.length + "</div>";
                };

                ganttChart.locale = {
                    date: {
                        month_full: ["一月", "二月", "三月", "四月", "五月", "六月", "七月",
                            "八月", "九月", "十月", "十一月", "十二月"],
                        month_short: ["一月", "二月", "三月", "四月", "五月", "六月", "七月",
                            "八月", "九月", "十月", "十一月", "十二月"],
                        day_full: ["周日", "周一", "周二", "周三", "周四", "周五", "周六"],
                        day_short: ["周日", "周一", "周二", "周三", "周四", "周五", "周六"],
                    },
                    labels: {
                        new_task: "新任务",
                        icon_save: "保存",
                        icon_cancel: "取消",
                        icon_details: "详细",
                        icon_edit: "编辑",
                        icon_delete: "删除",
                        confirm_closing: "",//Your changes will be lost, are you sure ?
                        confirm_deleting: "?",
                        section_description: "描述",
                        section_time: "时间段",
                        /* link confirmation */
                        confirm_link_deleting: "",
                        link_from: "起始",
                        link_to: "截止",
                        link_start: "开始",
                        link_end: "结束",
                        minutes: "分钟",
                        hours: "小时",
                        days: "天",
                        weeks: "周",
                        months: "月",
                        years: "年"
                    }
                }
                ganttChart.config.columns = this.computeShowColumns();
                ganttChart.config.scale_unit = 'week';
                ganttChart.config.drag_progress = false
                ganttChart.config.drag_links = false;
                ganttChart.config.sort = true;
                ganttChart.config.row_height = 30;
                ganttChart.config.min_column_width = 70;
                ganttChart.config.grid_width = 390;
                ganttChart.config.font_width_ratio = 7;
                ganttChart.config.resource_store = "resource";
                ganttChart.config.order_branch = true;
                ganttChart.config.open_tree_initially = false;
                ganttChart.config.resource_property = "resources";
                ganttChart.config.work_time = false;
                ganttChart.templates.leftside_text = (start, end, task) => {
                    if (this.getTaskFitValue(task) === "left") {
                        return encodeHTML(task.text);
                    }
                    return "";
                };
                ganttChart.templates.rightside_text = (start, end, task) => {
                    if (this.getTaskFitValue(task) === "right") {
                        return encodeHTML(task.text);
                    }
                    return "";
                };
                ganttChart.templates.task_text = (start, end, task) => {
                    if (this.getTaskFitValue(task) === "center") {
                        return encodeHTML(task.text);
                    }
                    return "";
                };
                //
                var resourcesStore = ganttChart.createDatastore({
                    name: ganttChart.config.resource_store,
                    type: 'treeDatastore',

                    initItem: function (item) {
                        item.parent = item.parent || ganttChart.config.root_id;
                        item[ganttChart.config.resource_property] = item.parent;
                        item.open = true;
                        return item;
                    },
                });
                this.resourcesStore = resourcesStore;
                //
                ganttChart.config.layout = {
                    css: 'gantt_container',
                    rows: [],
                };
                this.setupResourceLayout();
                ganttChart.init(this.$refs.todoGantt);
                ganttChart.attachEvent("onTaskDblClick", (id, e) => {
                    // console.log("onTaskClick---->");
                    if (id == null) {
                        return;
                    }
                    if (!this.ganttActive) {
                        console.log("todo gantt active:", this.ganttActive)
                        return;
                    }
                    let task = gantt.getTask(id)
                    console.log(task)
                    if (task.task) {
                        this.$nextTick(() => {
                            this.showTaskInfo(task.task)
                        })
                    }
                });
                ganttChart.attachEvent("onAfterTaskUpdate", (id, item) => {
                    console.log("onAfterTaskUpdate---->todo");
                    if (id == null) {
                        return;
                    }
                    if (!this.ganttActive) {
                        console.log("todo gantt active:", this.ganttActive)
                        return;
                    }
                    let task = ganttChart.getTask(id)
                    // console.log(task)
                    if (task.task == null) {
                        return;
                    }
                    this.updateTaskStartDate(task, task.start_date.getTime(), task.end_date.getTime());
                });
                ganttChart.attachEvent("onParse", () => {
                    if (!this.ganttActive) {
                        console.log("todo gantt active-----:", this.ganttActive)
                        return;
                    }
                    // console.log("onParse---->");
                    ganttChart.render();
                    this.$forceUpdate();
                });
            },
            getTaskFitValue(task) {
                var ganttChart = this.ganttChart;
                var taskStartPos = ganttChart.posFromDate(task.start_date),
                    taskEndPos = ganttChart.posFromDate(task.end_date);
                var width = taskEndPos - taskStartPos;
                var textWidth = (task.text || "").length * ganttChart.config.font_width_ratio;
                if (width < textWidth) {
                    var ganttLastDate = gantt.getState().max_date;
                    var ganttEndPos = gantt.posFromDate(ganttLastDate);
                    if (ganttEndPos - taskEndPos < textWidth) {
                        return "left"
                    } else {
                        return "right"
                    }
                } else {
                    return "center";
                }
            },
            computeShowColumns() {
                const columns = [];
                columns.push({
                    name: 'text',
                    resize: true,
                    label: '名称',
                    align: 'left',
                    tree: true,
                    min_width: 150,
                    width: '*',
                    template: function (obj) {
                        var spanClass = ' style=\'color:#19BAD1\'';
                        return '<b' + spanClass + '>' + encodeHTML(obj.text) + '</b>';
                    },
                    sort(a, b) {
                        return b.id - a.id;
                    },
                });
                columns.push({
                    name: 'projectName',
                    resize: true,
                    label: '项目',
                    align: 'left',
                    min_width: 150,
                    width: '*',
                    template: function (obj) {
                        var spanClass = ' style=\'color:#19BAD1\'';
                        var task= obj.task;
                        if(task.objectTypeName){
                            return '<span ' + spanClass + '>'+encodeHTML(task.objectTypeName)+'</span>#'+ '<b>' + encodeHTML(task.projectName) + '</b>';
                        }else{
                            return  '<b>' + encodeHTML(task.projectName) + '</b>';
                        }
                    },
                    sort(a, b) {
                        return b.projectId - a.projectId;
                    },
                });

                columns.push({
                    name: 'start_date',
                    resize: true,
                    label: '开始时间',
                    width: '160',
                    align: 'center',
                    template(obj) {
                        return formatDate(obj.start_date.getTime()) + '[' + obj.duration + ']';
                    },
                    sort(a, b) {
                        if (!a.task || !b.task) {
                            return 0;
                        }
                        return b.start_date - a.start_date;
                    },
                });

                columns.push({
                    name: 'end_date',
                    resize: true,
                    label: '截止时间',
                    width: '160',
                    align: 'center',
                    template(obj) {
                        return formatDate(obj.end_date.getTime());
                    },
                    sort(a, b) {
                        if (!a.task || !b.task) {
                            return 0;
                        }
                        return b.end_date - a.end_date;
                    },
                });
                columns.push({
                    name: 'num',
                    resize: true,
                    label: '待办数量',
                    width: '100',
                    align: 'center',
                    template(obj) {
                        return obj.num;
                    },
                    sort: false
                });
                columns.push({
                    name: 'owner',
                    resize: true,
                    label: '负责人',
                    width: '100',
                    align: 'center',
                    template(obj) {
                        return obj.accountName;
                    },
                    sort: false
                });
                columns.push({
                    name: "status",
                    resize: true,
                    label: "状态",
                    width: '70',
                    align: "center",
                    template(obj) {
                        if (obj.task && obj.task.status && obj.task.statusName != 'undefined') {
                            return "<span style='color:" + obj.task.statusColor + "'>" + (obj.task.statusName || "") + "</span>"
                        }
                        return "";
                    },
                    sort(a, b) {
                        if (!a.task || !b.task) {
                            return 0;
                        }
                        return b.task.status - a.task.status;
                    },
                });

                columns.push({
                    name: 'priority',
                    resize: true,
                    label: '优先级',
                    width: '80',
                    align: 'center',
                    template(obj) {
                        if (obj.task) {
                            return `<span style="color:${obj.task.priorityColor}">${obj.task.priorityName || ""}</span>`;
                        }
                        return '';
                    },
                    sort(a, b) {
                        if (!a.task || !b.task) {
                            return 0;
                        }
                        return b.task.priority - a.task.priority;
                    },
                });

                columns.push({
                    name: 'categories',
                    resize: true,
                    label: '分类',
                    min_width: 100,
                    width: '*',
                    align: 'center',
                    template: (object) => {
                        if (!Array.isArray(object.task.categoryList)) {
                            return;
                        }
                        if (object.task) {
                            if (!Array.isArray(object.task.categoryList) || object.task.categoryList.length === 0) {
                                return '<span style="color:#ccc;">未设置</span>';
                            }
                            const html = [];
                            html.push('<div>');
                            object.task.categoryList.forEach(item => {
                                // const category = this.queryInfo.categoryList.find(_ => _.id === item);
                                // if (!category) {
                                //     return;
                                // }
                                html.push(`<span class="gantt-task-tag" style="background-color:${item.color + '55'}"><span style="color:${item.color}">${item.name}</span></span>`);
                            });
                            html.push('</div>');
                            return html.join('');
                        }
                        return '分类';
                    },
                    sort: false,
                });


                return columns;
            },
            setViewType(type) {
                this.viewType = type;
                if (this.ganttChart) {
                    this.ganttChart.config.scale_unit = type;
                    this.ganttChart.render();
                }
            },
            getTasks() {
                const tasks = [];
                const links = [];
                var idx = 10000;
                for (let i = 0; i < this.accountTodoTaskList.length; i++) {
                    const accountItem = this.accountTodoTaskList[i];
                    let accountId = accountItem.accountId;
                    let taskList = accountItem.tasks;
                    taskList.unshift({
                        taskId: accountId,
                        accountName: accountItem.accountName,
                        name: accountItem.accountName,
                        startDate: accountItem.startDate,
                        endDate: accountItem.endDate,
                        num: accountItem.num,
                    })
                    for (let j = 0; j < taskList.length; j++) {
                        let item = taskList[j];
                        let startDate;
                        let endDate;
                        if (!item.startDate && !item.endDate) {
                            startDate = new Date();
                        }
                        if (item.startDate >= 0) {
                            startDate = new Date(item.startDate);
                        }
                        if (item.endDate >= 0) {
                            endDate = new Date(item.endDate);
                        }

                        if (startDate && !endDate) {
                            endDate = startDate;
                        }
                        if (!startDate && endDate) {
                            startDate = endDate;
                        }
                        const dayTimes = 3600 * 24 * 1000;
                        endDate = new Date(endDate.getTime() + dayTimes - 1);
                        const task = {
                            start_date: startDate,
                            end_date: endDate,
                            text: item.name,
                            id: item.uuid ? idx++ : item.taskId,
                            task: item,
                            projectId:item.projectId,
                            num: item.num || 1,
                            accountName: accountItem.accountName,
                            parent: item.accountId,
                        }
                        if (item.accountId > 0) {
                            links.push({
                                id: links.length,
                                source: item.accountId,
                                target: item.uuid ? idx++ : item.taskId,
                                type: 1
                            })
                        }
                        tasks.push(task)
                    }
                }

                return {
                    data: tasks,
                    links: links
                };
            },
            setupResourceLayout() {
                // var ganttChart = this.ganttChart;
                // var layoutGantt =
                //     {
                //         cols: [
                //             {view: "grid", group: "grids", scrollY: "scrollVer"},
                //             {resizer: true, width: 1},
                //             {view: "timeline", scrollX: "scrollHor", scrollY: "scrollVer"},
                //             {view: "scrollbar", id: "scrollVer", group: "vertical"}
                //         ],
                //         gravity: 2
                //     };
                // ganttChart.config.layout.rows = [
                //     layoutGantt,
                //     {view: "scrollbar", id: "scrollHor"}
                // ]
                var ganttChart = this.ganttChart;
                var resourceConfig = {
                    scale_height: 30,
                    subscales: [],
                    columns: [
                        {
                            name: "name", label: "责任人", tree: true, width: 150, template: (resource) => {
                                return resource.text;
                            }, resize: true
                        },
                        {
                            name: "allocated", label: "待办对象数量", align: "center", width: 100, template: (resource) => {
                                return resource.taskCount;
                            }, resize: true
                        },
                        {
                            name: "days", label: "时长(天)", align: "center", width: 100, template: (resource) => {
                                var tasks;

                                var store = ganttChart.getDatastore(ganttChart.config.resource_store),
                                    field = ganttChart.config.resource_property;
                                if (store.hasChild(resource.id)) {
                                    tasks = ganttChart.getTaskBy(field, store.getChildren(resource.id));
                                } else {
                                    tasks = ganttChart.getTaskBy(field, resource.id);
                                }
                                var totalDuration = 0;
                                for (var i = 0; i < tasks.length; i++) {
                                    totalDuration += tasks[i].duration;
                                }
                                return totalDuration;
                            }, resize: true
                        }
                    ]
                };
                //
                var layoutGantt =
                    {
                        cols: [
                            {view: "grid", group: "grids", scrollY: "scrollVer"},
                            {resizer: true, width: 1},
                            {view: "timeline", scrollX: "scrollHor", scrollY: "scrollVer"},
                            {view: "scrollbar", id: "scrollVer", group: "vertical"}
                        ],
                        gravity: 2
                    };
                var layoutResource =
                    {
                        config: resourceConfig,
                        cols: [
                            {view: "resourceGrid", group: "grids", width: 435, scrollY: "resourceVScroll"},
                            {resizer: true, width: 1},
                            {view: "resourceTimeline", scrollX: "scrollHor", scrollY: "resourceVScroll"},
                            {view: "scrollbar", id: "resourceVScroll", group: "vertical"}
                        ],
                        gravity: 1
                    };
                if (this.showResource) {
                    ganttChart.config.layout.rows = [
                        layoutGantt,
                        {resizer: true, width: 1},
                        layoutResource,
                        {view: "scrollbar", id: "scrollHor"}
                    ]
                } else {
                    ganttChart.config.layout.rows = [
                        layoutGantt,
                        {view: "scrollbar", id: "scrollHor"}
                    ]
                }
                //
            },
            buildView() {
                console.log('buildView todo task');
                this.ganttActive = true;
                const ganttChart = this.ganttChart;
                var tasks = this.getTasks();
                ganttChart.clearAll();
                //today marker
                const todayId = ganttChart.addMarker({
                    start_date: new Date(),
                    css: 'gantt_status_line today',
                    text: '今天',
                    title: '今天',
                });
                clearInterval(this.todayMarkerInterval);
                this.todayMarkerInterval = setInterval(function () {
                    const today = ganttChart.getMarker(todayId);
                    today.start_date = new Date();
                    ganttChart.updateMarker(todayId);
                }, 1000 * 300);

                ganttChart.parse(tasks);
                // this.resourcesStore.parse(this.getResource());
                ganttChart.render();
            },
            debounceEvent(key, callback) {
                //从甘特图切换到其他视图返回后，对同一事件会触发多次
                let showTime = Number(JSON.parse(window.sessionStorage.getItem(key) || '0'));
                let currentTime = new Date().getTime();
                if (currentTime - showTime > 1000) {
                    window.sessionStorage.setItem(key, JSON.stringify(currentTime));
                    if (callback) {
                        callback.apply(this, []);
                    }
                } else {
                    window.sessionStorage.setItem(key, JSON.stringify(currentTime));
                }
            },
            pageLoad() {
                this.loadData();
                this.loadProjectList();
            },
            loadProjectList() {
                app.invoke('BizAction.getMyProjectList', [app.token], list => {
                    this.projectList = list;
                });
            },
            startDateChange(e) {
                this.formItem.startDate = e;
            },
            endDateChange(e) {
                this.formItem.endDate = e;
            },
            showTask() {
                if (this.taskShowIdx === 1) {
                    this.taskShowList = this.accountFinishTaskList;
                } else if (this.taskShowIdx === 2) {
                    this.taskShowList = this.accountDelayTaskList;
                } else {
                    this.taskShowList = this.accountCreateTaskList;
                }
            },
            showTaskList(idx) {
                this.taskShowIdx = idx;
                this.showTask();
            },
            onScroll(event) {
                this.scrollTop = event.target.scrollTop;
            },
            onClickPreWeek() {
                if (this.datemode == 'week') {
                    this.formItem.startDate = new Date(this.formItem.startDate.getFullYear(), this.formItem.startDate.getMonth(), this.formItem.startDate.getDate() - 7);
                    this.formItem.endDate = new Date(this.formItem.endDate.getFullYear(), this.formItem.endDate.getMonth(), this.formItem.endDate.getDate() - 7);
                } else if (this.datemode == 'month') {
                    let year = this.formItem.startDate.getFullYear();
                    let month = this.formItem.startDate.getMonth();
                    month -= 1;
                    if (month < 0) {
                        month = month + 12;
                        year = year - 1;
                    }
                    let startDate = new Date(year, month, 1);
                    month += 1;
                    if (month >= 12) {
                        month = month - 12;
                        year = year + 1;
                    }
                    let endDate = new Date(year, month, 1);
                    endDate = new Date(endDate.getTime() - 1);
                    this.formItem.startDate = startDate;
                    this.formItem.endDate = endDate;
                } else if (this.datemode == 'quarter') {
                    let year = this.formItem.startDate.getFullYear();
                    let month = this.formItem.startDate.getMonth();
                    month -= 3;
                    if (month < 0) {
                        month = month + 12;
                        year = year - 1;
                    }
                    let startDate = new Date(year, month, 1);
                    month += 3;
                    if (month >= 12) {
                        month = month - 12;
                        year = year + 1;
                    }
                    let endDate = new Date(year, month, 1);
                    endDate = new Date(endDate.getTime() - 1);
                    this.formItem.startDate = startDate;
                    this.formItem.endDate = endDate;
                } else if (this.datemode == 'year') {
                    let year = this.formItem.startDate.getFullYear();
                    let startDate = new Date(year - 1, 0, 1);
                    let endDate = new Date(year, 0, 1);
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
                if (this.datemode == 'week') {
                    this.formItem.startDate = new Date(this.formItem.startDate.getFullYear(), this.formItem.startDate.getMonth(), this.formItem.startDate.getDate() + 7);
                    this.formItem.endDate = new Date(this.formItem.endDate.getFullYear(), this.formItem.endDate.getMonth(), this.formItem.endDate.getDate() + 7);
                } else if (this.datemode == 'month') {
                    let year = this.formItem.startDate.getFullYear();
                    let month = this.formItem.startDate.getMonth();
                    month += 1;
                    if (month >= 12) {
                        month = month - 12;
                        year = year + 1;
                    }
                    let startDate = new Date(year, month, 1);
                    month += 1;
                    if (month >= 12) {
                        month = month - 12;
                        year = year + 1;
                    }
                    let endDate = new Date(year, month, 1);
                    endDate = new Date(endDate.getTime() - 1);
                    this.formItem.startDate = startDate;
                    this.formItem.endDate = endDate;
                } else if (this.datemode == 'quarter') {
                    let year = this.formItem.startDate.getFullYear();
                    let month = this.formItem.startDate.getMonth();
                    month += 3;
                    if (month >= 12) {
                        month = month - 12;
                        year = year + 1;
                    }
                    let startDate = new Date(year, month, 1);
                    month += 3;
                    if (month >= 12) {
                        month = month - 12;
                        year = year + 1;
                    }
                    let endDate = new Date(year, month, 1);
                    endDate = new Date(endDate.getTime() - 1);
                    this.formItem.startDate = startDate;
                    this.formItem.endDate = endDate;
                } else if (this.datemode == 'year') {
                    let year = this.formItem.startDate.getFullYear();
                    let startDate = new Date(year + 1, 0, 1);
                    let endDate = new Date(year + 2, 0, 1);
                    endDate = new Date(endDate.getTime() - 1);
                    this.formItem.startDate = startDate;
                    this.formItem.endDate = endDate;
                }
                this.loadData();
            },
            loadData() {
                clearTimeout(this.loadTimeoutId);
                this.loadTimeoutId = setTimeout(() => {
                    app.invoke('BizAction.getCompanyMemberTodoTask', [app.token, this.formItem], res => {
                        this.accountTodoTaskList = res;
                        this.buildView();
                    }, (e) => {
                        console.log("error---->", e);
                    });
                }, 50);
            },
            loadProjectModule(projectId, type) {
                app.invoke('BizAction.getProjectModuleInfoList', [app.token, projectId], (list) => {
                    if (type === 1) {
                        this.moduleList = list;
                    } else {
                        this.createModuleList = list;
                    }
                });
            },
            updateTaskStartDate(task, startDate, endDate) {
                var _this = this;
                this.debounceEvent("goto.updateTime", () => {
                    var obj = {};
                    obj.id = task.id;
                    obj.startDate = startDate;
                    obj.endDate = endDate - 1;
                    task.startDate = startDate;
                    task.endDate = endDate - 1;
                    task.start_date = new Date(startDate);
                    task.end_date = new Date(endDate - 1);
                    // task.endDate = endDate;
                    app.invoke('BizAction.getTaskAssociateStat', [app.token, task.id], info => {
                        if (info.beforeCount > 0 || info.afterCount > 0 || info.subCount > 0) {
                            app.confirm('该对象关联有前后置对象或子对象，是否需要同步其开始截止时间?', () => {
                                this.updateTaskTimeNative(obj, false);
                            }, () => {
                                this.updateTaskTimeNative(obj, true);
                            }, '需要', '不需要');
                        } else {
                            this.updateTaskTimeNative(obj, true);
                        }
                    }, (error) => {
                        this.loadData();
                    });

                })
            },
            updateTaskTimeNative(obj, isManualUpdate) {
                app.invoke("BizAction.updateTask", [app.token, obj, ['startDate', 'endDate'], isManualUpdate],
                    info => {
                        app.postMessage('task.edit')
                    },
                    () => {
                        this.loadData()
                    }
                );
            },
            showTaskInfo(item) {
                console.log("todo show task info---> ", JSON.stringify(item))
                if (item.uuid) {
                    this.debounceEvent("todo.gantt.showTime", () => {
                        app.showDialog(TaskDialog, {
                            taskId: item.uuid,
                            showTopBar: true,
                            callback: (res) => {
                                if (res.type && res.type === 'goto') {
                                    this.showDialog = false;
                                }
                            },
                        });
                    });
                }
            },
            createTask() {
                let _this = this;
                app.showDialog(TaskCreateDialog, {
                    projectId: _this.createFrom.projectId,
                    type: _this.createFrom.objectType,
                    callback: function (taskId) {
                        if (_this.args.callback) {
                            _this.args.callback([{id: taskId}]);
                        }
                        _this.showDialog = false;
                    }
                });
            }
        },
    };
</script>
