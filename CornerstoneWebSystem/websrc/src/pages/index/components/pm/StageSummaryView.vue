<style scoped lang="less">

    .it-name-label {
        color: #333333;
        font-size: 16px;
        font-weight: bold;
        text-align: left;
        display: flex;
        align-items: center;
    }

    .it-user-label {
        font-size: 12px;
        color: #999999;
    }

    .it-description {
        margin-top: 10px;
        font-size: 14px;
        color: #666666;
        text-align: left;
    }

    .chart-container {
        width: 400px;
        height: 400px;
        display: inline-block;
        text-align: center;
    }

    .chart-container-title {
        font-size: 14px;
        text-align: center;
        margin-top: 5px;
    }

    .count-label {
        color: #999;
        font-size: 13px;
    }

    .count-value {
        font-size: 20px;
        font-weight: bold;
        color: #F84F84;
    }

    .count-value-big {
        font-size: 50px;
        margin-top: 20px;
    }

    .count-box {
        padding: 20px;
        font-weight: bold;
        text-align: center;
    }

    .it-name {
        max-width: 200px;
        display: inline-block;
    }

    .landmark-timeline2 {
        width: 100%;
        margin: 8px;
        background-color: #fff;
        padding-top: 10px;
    }

    .gantt-container {
        width: 100%;
        min-height: 400px;
        margin-top: 10px;
    }

    .stage-landmark-step /deep/ .ivu-steps-main .ivu-steps-title {
        cursor: pointer;
        background-color: #fff !important;
    }

    .ivu-steps /deep/ .stage-landmark-step .ivu-steps-head {
        background-color: #fff !important;
    }


</style>
<i18n>
    {
    "en": {
    "阶段信息": "Iteration",
    "创建":"Create",
    "燃尽图":"Burndown chart",
    "天视图":"Day",
    "周视图":"Week",
    "月视图":"Month",
    "季视图":"Quarter",
    "延期":"Delayed",
    "完成":"Finished",
    "总数":"Total",
    "预期":"Expect",
    "完成度":"Finish rate",
    "实际":"Actual"
    },
    "zh_CN": {
    "阶段信息": "阶段信息",
    "创建":"创建",
    "燃尽图":"燃尽图",
    "天视图":"天视图",
    "周视图":"周视图",
    "月视图":"月视图",
    "季视图":"季视图",
    "延期":"延期",
    "完成":"完成",
    "总数":"总数",
    "预期":"预期",
    "完成度":"完成度",
    "实际":"实际"
    }
    }
</i18n>
<template>
    <div>
        <div style="min-height:500px;height:600px">
            <Tabs value="stage" :animated="false" style="margin-top:15px">
                <TabPane label="阶段" name="stage">
                    <div style="text-align:right">
                        <RadioGroup v-model="stageViewType" style="margin-left:20px">
                            <Radio label="day">{{$t('天视图')}}</Radio>
                            <Radio label="week">{{$t('周视图')}}</Radio>
                            <Radio label="month">{{$t('月视图')}}</Radio>
                            <Radio label="quarter">{{$t('季视图')}}</Radio>
                        </RadioGroup>
                    </div>
                    <div class="landmark-timeline2">
                        <Steps :current="currentLandmark">
                            <Step class="stage-landmark-step" :key="'step_'+step.id" v-for="step in landmarkList"
                                  icon="ios-star" color="red"
                                  @click.native="showLandmarkInfo(step)"
                                  :title="step.landmarkEndDate|fmtDate" :content="step.landmarkName">
                            </Step>
                        </Steps>
                    </div>
                    <div ref="stageGantt" style="min-height: 700px;" class="gantt-container "></div>

                </TabPane>
                <TabPane :label="$t('燃尽图')" name="burndown">
                    <v-chart :autoresize="true" :options="chartOptions" style="width:100%;height:400px"/>
                </TabPane>
                <TabPane :label="$t('完成度')" name="stat">
                    <Row style="padding-bottom:30px">
                        <Col span="12" v-for="item in rateList" :key="'rate'+item.objectType">
                            <div class="count-box">
                                <div class="count-value-big numberfont">{{item.finishNum|fmtPercentStr(item.totalNum)}}
                                    <span v-if="item.delayNum>=0" class="count-value">{{$t('延期')}} {{item.delayNum|fmtPercentStr(item.totalNum)}}</span>
                                </div>
                                <div class="count-label">{{item.objectType|dataDict('Task.objectType')}}
                                    {{$t('总数')}}{{item.totalNum}}
                                    <template v-if="item.finishNum>=0"> / {{$t('完成')}}{{item.finishNum}}</template>
                                    <template v-if="item.delayNum>=0"> / {{$t('延期')}}{{item.delayNum}}</template>
                                </div>
                            </div>
                        </Col>
                    </Row>
                </TabPane>

            </Tabs>
        </div>


    </div>

</template>

<script>
    import {graphic} from 'echarts/lib/export'
    import "../../../../../public/js/dhtml_gantt/ext/dhtmlxgantt_tooltip.js";

    export default {
        name: "StageSummaryView",
        mixins: [componentMixin],
        props: ['iterationId'],
        data() {
            return {
                iterationInfo: {},
                rateList: [],
                stageViewType: 'week',
                chartOptions: {
                    color: ['#0094FB', '#EC0023'],
                    tooltip: {
                        trigger: 'axis'
                    },
                    toolbox: {},
                    xAxis: {
                        data: [],
                        type: 'category',
                    },
                    yAxis: {
                        type: 'value'
                    },
                    legend: {
                        data: [this.$t('预期'), this.$t("实际")]
                    },
                    series: [
                        {
                            name: this.$t('预期'),
                            data: [],
                            type: 'line',
                            areaStyle: {
                                color: new graphic.LinearGradient(0, 0, 0, 1, [{
                                    offset: 0,
                                    color: '#0094FB'
                                }, {
                                    offset: 1,
                                    color: '#CCCCCC'
                                }])
                            },
                        },
                        {
                            name: this.$t('实际'),
                            data: [],
                            type: 'line'
                        }
                    ]
                },
                landmarkQuery: {
                    projectId: app.projectId,
                    type: 2,
                    sortFields: ["create_time "]
                },
                landmarkList: [],
                ganttChart: null,
                stageList: [],
                currentLandmark: -1,
                ganttActive: false,
                stagemap: null,
                todayMarkerInterval:null
            }
        },
        watch: {
            "stageViewType": function (val) {
                if (this.ganttChart) {
                    this.ganttChart.config.scale_unit = val;
                    this.ganttChart.render();
                }
            }
        },
        created() {
            this.ganttChart = gantt;
        },
        beforeDestroy() {
            console.log("before destory stage")
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
            // if(typeof this.ganttChart.detachAllEvents === "function"){
            //     this.ganttChart.detachAllEvents();
            // }
            // if(typeof this.ganttChart.destructor === "function"){
            //     this.ganttChart.destructor();
            // }
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
                ganttChart.config.open_tree_initially = true;
                ganttChart.config.resource_property = "resources";
                ganttChart.config.work_time = false;
                ganttChart.templates.leftside_text = (start, end, task) => {
                    if (this.getTaskFitValue(task) === "left") {
                        return encodeHTML(task.text);
                    }
                    return "";
                };
                ganttChart.templates.rightside_text = (start, end, task) => {
                    if(task.type == gantt.config.types.milestone){
                        return task.text;
                    }
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
                ganttChart.init(this.$refs.stageGantt);
                ganttChart.attachEvent("onTaskDblClick", (id, e) => {
                    // console.log("onTaskClick---->");
                    if (id == null) {
                        return;
                    }
                    if (!this.ganttActive) {
                        console.log("stage gantt active:", this.ganttActive)
                        return;
                    }
                    let task = gantt.getTask(id)
                    if (task.id) {
                        this.$nextTick(() => {
                            this.showStageInfo(task.id)
                        })
                    }
                });
                ganttChart.attachEvent("onAfterTaskUpdate", (id, item) => {
                    console.log("onAfterTaskUpdate---->stage");
                    if (id == null) {
                        return;
                    }
                    if (!this.ganttActive) {
                        console.log("stage gantt active:", this.ganttActive)
                        return;
                    }
                    let task = ganttChart.getTask(id)
                    // console.log(task)
                    if (task.task == null) {
                        return;
                    }
                    var obj = {
                        changeRemark: null
                    };
                    obj.id = task.id;
                    obj.startDate = task.start_date.getTime();
                    obj.endDate = task.end_date.getTime() - 1;
                    task.startDate = obj.startDate;
                    task.endDate = obj.endDate - 1;
                    // console.log(task)
                    let _this = this;
                    this.debounceEvent("stage.gantt.updateTime", () => {
                        _this.$Modal.confirm({
                            render: (h) => {
                                return h('Input', {
                                    props: {
                                        value: obj.changeRemark,
                                        autofocus: true,
                                        placeholder: '请输入调整原因'
                                    },
                                    on: {
                                        input: (val) => {
                                            obj.changeRemark = val;
                                        }
                                    }
                                })
                            },
                            onOk: () => {
                                if (!!obj.changeRemark) {
                                    _this.updateStageWorkday(obj);
                                } else {
                                    app.toast("请输入调整原因");
                                    _this.loadData();
                                }
                            }, onCancel: () => {
                                _this.loadData();
                            }
                        })


                    })
                });
                ganttChart.attachEvent("onParse", () => {
                    console.log("stage gantt onParse-----:", this.ganttActive)
                    if (!this.ganttActive) {
                        console.log("stage gantt active-----:", this.ganttActive)
                        return;
                    }
                    // console.log("onParse---->");
                    ganttChart.render();
                    this.$forceUpdate();
                });
            },

            updateStageWorkday(stage) {
                //判断是否有后置阶段
                var hasAssociate = false;
                var ass = this.stagemap.get(stage.id)
                console.log(ass)
                if (!!ass && ass > 0) {
                    hasAssociate = true;
                }
                if (hasAssociate) {
                    app.confirm('该阶段关联有后置阶段，是否需要同步其开始截止时间?', () => {
                        this.updateStageWorkday0(stage, true);
                    }, () => {
                        this.updateStageWorkday0(stage, false);
                    }, '需要', '不需要');
                } else {
                    this.updateStageWorkday0(stage, false);
                }
            },
            updateStageWorkday0(stage, danamicupdate) {
                app.invoke('BizAction.updateStageWorkday', [app.token, stage, danamicupdate], (info) => {
                    app.toast('修改成功')
                    app.postMessage('stage.edit')
                    // this.loadData();
                }, error => {
                    this.loadData();
                })
            },
            pageLoad() {
                this.loadData();
                this.loadBurnDownChartData();
                this.loadFinishRateData();
                this.loadLandmarkList();
            },
            // pageMessage(type) {
            //     if (type == 'stage.edit') {
            //         this.loadData();
            //     }
            // },
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
                        var spanClass = '';
                        if (obj.task) {
                            if (obj.task.isFinish) {
                                spanClass = ' style=\'color:#ccc;text-decoration: line-through\' ';
                            }
                            return '<b' + spanClass + '>' + encodeHTML(obj.text) + '</b>';
                        }//#19BAD1
                        spanClass = ' style=\'color:#19BAD1\'';
                        return '<b' + spanClass + '>' + encodeHTML(obj.text) + '</b>';
                    },
                    sort(a, b) {
                        return b.id - a.id;
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


                return columns;
            },
            getTasks() {
                const tasks = [];
                const links = [];
                for (let i = 0; i < this.stageList.length; i++) {
                    const item = this.stageList[i];
                    if (!item.startDate && !item.endDate) {
                        continue;
                    }
                    let startDate;
                    let endDate;
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
                    // const dayTimes = 3600 * 24 * 1000;
                    // endDate = new Date(endDate.getTime() + dayTimes - 1);
                    const task = {
                        start_date: startDate,
                        end_date: endDate,
                        text: item.name,
                        id: item.id,
                        task: item,
                        parent: item.preId,
                    }
                    if (item.parentId > 0) {
                        links.push({
                            id: links.length,
                            source: item.preId,
                            target: item.id,
                            type: 1
                        })
                    }
                    tasks.push(task)
                }

                return {
                    data: tasks,
                    links: links
                };
            },
            setupResourceLayout() {
                var ganttChart = this.ganttChart;
                var layoutGantt =
                    {
                        cols: [
                            // {view: "grid", group: "grids", scrollY: "scrollVer"},
                            // {resizer: true, width: 1},
                            {view: "timeline", scrollX: "scrollHor", scrollY: "scrollVer"},
                            {view: "scrollbar", id: "scrollVer", group: "vertical"}
                        ],
                        gravity: 2
                    };
                ganttChart.config.layout.rows = [
                    layoutGantt,
                    {view: "scrollbar", id: "scrollHor"}
                ]

                //
            },
            loadData() {
                app.invoke('BizAction.getStageList', [app.token, {projectId: app.projectId}], (stageList) => {
                    this.stageList = stageList;
                    if (!Array.isEmpty(this.stageList)) {
                        this.stageList.sort((a, b) => {
                            return a.startDate - b.endDate;
                        })
                    }
                    //统计关联的后置阶段数
                    this.stagemap = new Map();
                    this.stageList.forEach(item => {
                        if (item.preId && item.preId > 0) {
                            var v = this.stagemap.get(item.preId);
                            if (!!v) {
                                this.stagemap.set(item.preId, v + 1)
                            } else {
                                this.stagemap.set(item.preId, 1)
                            }
                        }
                    });
                    this.buildView()
                });
            },
            loadLandmarkList() {
                app.invoke('BizAction.getStageAssociateList', [app.token, this.landmarkQuery], (info) => {
                    if (!Array.isEmpty(info)) {
                        info = info.filter(item=>item.landmarkId>0&&item.type===2)
                        info.sort((a, b) => {
                            return a.landmarkEndDate - b.landmarkEndDate;
                        })
                    }
                    this.landmarkList = info;
                    this.currentLandmark = this.landmarkList.length - 1;
                    // for (let i = 0; i < this.landmarkList.length; i++) {
                    //     if (this.landmarkList[i].stageStatus === 3) {
                    //         if (i > currentLandmark) {
                    //             currentLandmark = i;
                    //         }
                    //     }
                    // }
                });
            },
            loadFinishRateData() {
                app.invoke('BizAction.getProjectStageFinishDelayRate', [app.token, app.projectId], (info) => {
                    this.rateList = info;
                });
            },
            loadBurnDownChartData() {
                this.chartOptions.series[0].data = [];
                this.chartOptions.series[1].data = [];
                this.chartOptions.xAxis.data = [];
                app.invoke('BizAction.getProjectStageBurnDownChart', [app.token, app.projectId], (list) => {
                    for (var i = 0; i < list.length; i++) {
                        var t = list[i];
                        this.chartOptions.xAxis.data.push(formatDate(t.statDate));
                        this.chartOptions.series[0].data.push(t.expectUnfinishNum);
                        if (t.unfinishNum >= 0) {
                            this.chartOptions.series[1].data.push(t.unfinishNum);
                        }
                    }
                });
            },
            buildView(info) {
                console.log('buildView stage');
                this.ganttActive = true;
                const ganttChart = this.ganttChart;
                var tasks = this.getTasks();
                //todo 添加里程碑

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
                // ganttChart.render();
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
            showStageInfo(stageId) {
                this.debounceEvent("stage.gantt.showTime", () => {
                    app.showDialog(StageEditDialog, {
                        id: stageId,
                        projectId: app.projectId
                    })
                })
            },
            showTaskInfo(taskId) {
                this.debounceEvent("landmark.gantt.showTime", () => {
                    app.showDialog(TaskDialog, {
                        taskId: taskId
                    })
                })
            },
            showLandmarkInfo(item){
                app.showDialog(LandmarkInfoDialog, {
                    id: item.landmarkId||item.id,
                });
            }
        }
    }
</script>
