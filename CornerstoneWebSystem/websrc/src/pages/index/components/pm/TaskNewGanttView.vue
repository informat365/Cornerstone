<style scoped lang="less">
    .page-content-top {
        width: 100%;
        padding: 10px;
        padding-top: 5px;
        padding-bottom: 5px;
        text-align: left;
        font-weight: bold;
        font-size: 12px;
    }

    .location-total-label {
        font-size: 12px;
        color: #999;
        line-height: 20px;
        text-align: right;
    }

    .selected-btn {
        color: #0097F7 !important;

    }

    .gantt-container {
        height: calc(100vh - 130px);
        position: relative;
    }

    .opt-divider {
        width: 1px;
        display: inline-block;
        background: #A7A7A7;
        height: 10px;
        line-height: 10px;
        margin-left: 10px;
        margin-right: 5px;
        vertical-align: middle;
    }

    .opt-label {
        color: #999;
        font-size: 12px;
        line-height: 12px;
        display: inline-block;
        vertical-align: middle;
        margin-left: 5px;
    }

    .gantt-double-baseline /deep/ .gantt_data_area .gantt_bars_area .gantt_task_line {
        margin-top: -9px !important;
    }

    .gantt-double-baseline /deep/ .gantt_task_line {
        margin-bottom: 7px;
    }

    .gantt-double-baseline /deep/ .gantt_task_link .gantt_link_arrow {
        margin-top: -12px
    }

    .gantt-double-baseline /deep/ .gantt_side_content.gantt_right {
        bottom: 0;
    }

    .gantt-double-baseline /deep/ .baseline {
        position: absolute;
        border-radius: 2px;
        opacity: 0.6;
        margin-top: -7px;
        height: 12px;
        background: #ffd180;
        border: 1px solid rgb(255, 153, 0);
    }
</style>
<i18n>
    {
    "en": {
    "总计条数据":"total {0} items",
    "每页条":"{0} per page",
    "第页":"{0}/{1}",
    "责任人分组":"Owner group",
    "工作日":"Work day",
    "人力视图":"Resource view",
    "天":"Day",
    "周":"Week",
    "月":"Month",
    "季度":"Quarter",
    "重置排序":"Reset custom sort",
    "导出":"Export"
    },
    "zh_CN": {
    "总计条数据":"总计 {0} 条数据",
    "每页条":"每页 {0} 条",
    "第页":"第 {0}/{1} 页",
    "责任人分组":"责任人分组",
    "工作日":"工作日",
    "人力视图":"人力视图",
    "天":"天",
    "周":"周",
    "月":"月",
    "月":"月",
    "季度":"季度",
    "重置排序":"重置排序",
    "导出":"导出"
    }
    }
</i18n>
<template>
    <div>
        <Row class="page-content-top">
            <Col span="8">
                <div style="display:flex;align-items:center;color:#999">
                    <div style="">
                        {{$t('总计条数据',[pageQuery.total])}}
                    </div>
                    <Poptip transfer placement="bottom" v-model="pageSizePopuptipVisible">
                        <span style="margin-left:5px;cursor:pointer" class="vcenter pagespan">{{$t('每页条',[pageQuery.pageSize])}}</span>
                        <div slot="content">
                            <PopupTipItem
                                @click="selectPageSize(item)"
                                :name="item+''"
                                :selected="pageQuery.pageSize==item" v-for="item in [500,1000,1500]"
                                :key="'ps'+item"/>
                        </div>
                    </Poptip>

                    <IconButton
                        :disabled="pageQuery.pageIndex==1"
                        :size="15"
                        @click="changePage(-1)"
                        icon="md-arrow-round-back"></IconButton>
                    <span class="vcenter">{{$t('第页',[pageQuery.pageIndex,pageQuery.totalPage])}}</span>
                    <IconButton
                        :disabled="pageQuery.pageIndex==pageQuery.totalPage"
                        :size="15"
                        @click="changePage(1)"
                        icon="md-arrow-round-forward"></IconButton>
                    <span class="opt-divider"></span>
                    <TaskTableHeaderSelect
                        @change="onFieldsChange"
                        @on-reset-fields-order="onResetFieldsOrder"
                        @on-field-list-sort="onFieldListSort"
                        v-model="viewSetting.hideTableFieldList"
                        placement="bottom-end"
                        no-system-fields
                        :field-list="tableFieldList">
                        <IconButton slot="popup-name" icon="ios-settings-outline" size="16"/>
                    </TaskTableHeaderSelect>
                    <span class="opt-divider"></span>
                    <Icon style="cursor: pointer;" @click="clearSortCache" :title="$t('重置排序')" type="ios-trash"
                          size="16"></Icon>
                    <span class="opt-divider"></span>
                    <IconButton @click="exportMsProject" :title="$t('导出')"></IconButton>
                </div>
            </Col>
            <Col span="16" class="location-total-label">
                <span class="opt-label">{{$t('责任人分组')}}：</span>
                <i-Switch @on-change="toggleGroups" v-model="isGroupByUser" size="small"/>
                <span class="opt-divider"></span>


                <span class="opt-label">{{$t('人力视图')}}：</span>
                <i-Switch @on-change="toggleResourceLayout" v-model="showResource" size="small"/>
                <span class="opt-divider"></span>
                <IconButton :class="{'selected-btn':viewSetting.viewType=='day'}" @click="setViewType('day')"
                            :title="$t('天')"></IconButton>
                <IconButton :class="{'selected-btn':viewSetting.viewType=='week'}" @click="setViewType('week')"
                            :title="$t('周')"></IconButton>
                <IconButton :class="{'selected-btn':viewSetting.viewType=='month'}" @click="setViewType('month')"
                            :title="$t('月')"></IconButton>
                <IconButton :class="{'selected-btn':viewSetting.viewType=='quarter'}" @click="setViewType('quarter')"
                            :title="$t('季度')"></IconButton>
            </Col>
        </Row>
        <div class="gantt-container" :class="{'gantt-double-baseline':showDoubleBaselines}" ref="gantt"></div>

    </div>
</template>

<script>

    import "../../../../../public/js/dhtml_gantt/ext/dhtmlxgantt_tooltip.js";

    export default {
        mixins: [componentMixin],
        props: ['queryInfo', 'queryForm', "isProjectSet"],
        data() {
            return {
                title: "TaskNewGanttView",
                tableData: [],
                pageSizePopuptipVisible: false,
                pageQuery: {
                    total: 0,
                    pageIndex: 1,
                    pageSize: 500,
                    totalPage: 1,
                },
                viewSetting: {
                    tableSort: {
                        name: null,
                        sort: null,
                    },
                    viewType: "week",
                    hideTableFieldList: [],
                },
                definedFieldList: [
                    {
                        id: 1,
                        field: 'text',
                        name: '名称',
                    },
                    {
                        id: 2,
                        field: 'start_date',
                        name: '开始日期',
                    },
                    {
                        id: 7,
                        field: 'end_date',
                        name: '结束日期',
                    },
                    {
                        id: 3,
                        field: 'status',
                        name: '状态',
                    },
                    {
                        id: 4,
                        field: 'owner',
                        name: '责任人',
                    },
                    {
                        id: 5,
                        field: 'priority',
                        name: '优先级',
                    },
                    {
                        id: 6,
                        field: 'categories',
                        name: '分类',
                    },
                ],
                tableFieldList: [],
                showResource: false,
                showWorkday: false,
                isGroupByUser: false,
                todayMarkerInterval: null,
                newGanttActive: false,
                initQuery: null,
                projectFinishDisabled: false,
                isFieldSorting: false,
                isTimeUpdating: false,
                showDoubleBaselines: false
            }
        },
        watch: {
            "viewSetting.viewType": function (val) {
                if (this.ganttChart) {
                    this.ganttChart.config.scale_unit = val;
                    this.ganttChart.render();
                }
            }
        },
        beforeDestroy() {
            this.newGanttActive = false;
            // console.log("before destory---------task")
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
            this.ganttChart.detachEvent('onAfterTaskMove');
            this.ganttChart.detachEvent('onAfterSort');
            // if(typeof this.ganttChart.detachAllEvents === "function"){
            //     this.ganttChart.detachAllEvents();
            // }
            // if(typeof this.ganttChart.destructor === "function"){
            //     this.ganttChart.destructor();
            // }
            if (this.resourcesStore && typeof this.resourcesStore.destructor === "function") {
                this.resourcesStore.destructor();
            }
            // console.log(this.resourcesStore);
            //清空定时更新
            clearInterval(this.todayMarkerInterval);
        },
        created() {
            //2021年4月25日项目集显示双基线
            this.showDoubleBaselines = this.isProjectSet;
            this.ganttChart = gantt;
            Object.freeze(this.definedFieldList);
            this.loadViewSetting();
            if (Array.isArray(this.viewSetting.tableFieldList) && this.viewSetting.tableFieldList.length > 0) {
                this.tableFieldList = [...this.viewSetting.tableFieldList];
            } else {
                this.tableFieldList = [...this.definedFieldList];
            }
        },
        mounted() {
            let _this = this;
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
                var isFinish = (task && task.isFinish) || false;
                var overdue = "";
                var leftday = window.getLeftDays(end.getTime());
                var isPs = task && task.objectType == 1001;

                var diffDay = 0;
                if (task.finishTime) {
                    diffDay = window.dateDiff(new Date(task.finishTime), end);
                }
                if (!isPs) {
                    if (isFinish) {
                        if (diffDay < 0) {
                            overdue = "<br/><b>延期</b>:" + (diffDay * -1) + "天";
                        } else if (diffDay > 0) {
                            overdue = "<br/><b>提前</b>:" + (diffDay) + "天";
                        }
                    } else {
                        if (leftday < 0) {
                            overdue = "<br/><b>延期</b>:" + (leftday * -1) + "天";
                        }
                    }
                }
                var planDate = "";
                if (_this.showDoubleBaselines) {
                    planDate = ("</br><b>实际开始时间：</b>" + formatDate(start) +
                    "</br><b>实际完成时间:</b>" + (task.finishTime ? formatDate(task.finishTime) : ""));
                }

                let tip= "<b>任务:</b> " + task.text + "<br/><b>时长:</b> " + task.duration +
                    "天<br/><b>开始时间:</b>" + formatDate(start) + "<br/><b>截止时间:</b>" + formatDate(end) + overdue + planDate;
                return tip;
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
            ganttChart.config.sort = false;
            ganttChart.config.row_height = 30;
            //双基线
            if (this.showDoubleBaselines) {
                ganttChart.config.task_height = 16;
                ganttChart.config.row_height = 40;
            }

            ganttChart.config.min_column_width = 70;
            ganttChart.config.grid_width = 390;
            ganttChart.config.font_width_ratio = 7;
            ganttChart.config.resource_store = "resource";
            ganttChart.config.order_branch = true;
            ganttChart.config.open_tree_initially = true;
            ganttChart.config.resource_property = "resources";
            ganttChart.config.work_time = false;
            ganttChart.config.lightbox.sections = [
                {name: "description", height: 70, map_to: "text", type: "textarea", focus: true},
                {name: "time", height: 72, map_to: "auto", type: "duration"},
                {
                    name: "baseline", height: 72, map_to: {
                        start_date: "planned_start", end_date: "planned_end"
                    }, type: "duration"
                }
            ];
            ganttChart.locale.labels.section_baseline = "Planned";

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
                css: 'gantt_container ' + this.showDoubleBaselines ? 'gantt_double_baselines' : '',
                rows: [],
            };
            // ganttChart.plugins({
            //     tooltip: true
            // });
            this.setupResourceLayout();
            ganttChart.init(this.$refs.gantt);

            //双基线
            if (this.showDoubleBaselines) {
                ganttChart.attachEvent("onTaskLoading", function (task) {
                    task.planned_start = ganttChart.date.parseDate(task.start_date, "xml_date");
                    task.planned_end = ganttChart.date.parseDate(task.task && (task.task.finishTime || new Date()), "xml_date");
                    return true;
                });


                ganttChart.addTaskLayer(task => {
                    if (task.planned_start && task.planned_end) {
                        var sizes = ganttChart.getTaskPosition(task, task.planned_start, task.planned_end);
                        var el = document.createElement('div');
                        el.className = 'baseline';
                        el.style.left = sizes.left + 'px';
                        el.style.height = '12px';
                        el.style.width = sizes.width + 'px';
                        el.style.top = sizes.top + ganttChart.config.task_height + 13 + 'px';
                        // console.log("addTaskLayer----",sizes,el)
                        return el;
                    }
                    return false;
                })
            }

            /**
             * 监听双击任务事件
             * */
            ganttChart.attachEvent("onTaskDblClick", (id, e) => {
                if (id == null) {
                    return;
                }
                if (!this.newGanttActive) {
                    console.log("stage gantt active:", this.newGanttActive)
                    return;
                }
                let task = gantt.getTask(id)
                if (task.uuid) {
                    this.$nextTick(() => {
                        this.showTaskInfo(task.uuid)
                    })
                }
            });
            ganttChart.attachEvent("onBeforeTaskDrag", function (id, mode, e) {
                if (id == null) {
                    return false;
                }
                let task = gantt.getTask(id)
                if (task.task == null) {
                    return false;
                }
                if (task.task.isFinish) {
                    return false;
                }
                return true;
            });

            /**
             * 监听拖拽时间条事件
             * */
            ganttChart.attachEvent("onAfterTaskUpdate", (id, item) => {
                if (id == null) {
                    return;
                }
                if (!this.newGanttActive) {
                    console.log("task gantt active:", this.newGanttActive)
                    return;
                }
                let task = ganttChart.getTask(id)
                if (task.task == null) {
                    return;
                }
                this.updateTaskStartDate(task, task.start_date.getTime(), task.end_date.getTime())
            });
            ganttChart.attachEvent("onParse", () => {
                if (!this.newGanttActive) {

                    return;
                }
                ganttChart.render();
            });

            /**
             * 监听拖拽排序
             * */
            ganttChart.attachEvent("onAfterTaskMove", (taskId, parentId, listIdx) => {
                console.log("gantt move task:" + taskId + " pid:" + parentId + " idx:" + listIdx, " objectType:" + app.objectType)

                if (!this.newGanttActive) {
                    console.log("task gantt active:", this.newGanttActive)
                    return;
                }
                console.log("no field sorting,set up custom sort data")
                var currentGantt = this.queryForm.objectType == app.objectType && app.projectId == this.queryForm.projectId;
                if (!currentGantt) {
                    console.error("setupGanttSort object dismatch", app.objectType, this.queryForm.objectType)
                    return;
                }
                this.setupGanttSort(taskId, parentId, listIdx);
            });

            /**
             * 监听点击列名排序事件
             */
            ganttChart.attachEvent("onAfterSort", (sortFn, desc, a) => {
                if (!this.newGanttActive) {
                    console.log("task gantt active:", this.newGanttActive)
                    return;
                }
                var currentGantt = this.queryForm.objectType == app.objectType && app.projectId == this.queryForm.projectId;
                if (!currentGantt) {
                    return;
                }
                this.isFieldSorting = true;

                console.log("onAfterSort --->")

                if (null === sortFn) {
                    console.error("gantt sort : \r\n desc:" + desc, "queryForm:", this.queryForm.objectType, app.objectType)
                    //开启多个甘特图的情况下会多次触发
                    this.isFieldSorting = false;
                    this.debounceEvent(app.projectId + "_reload_gantt_" + app.objectType, () => {
                        this.reloadData();
                    });
                    this.setupGanttSortFunc(sortFn, null);
                } else {
                    if (this.hasQueryCondition()) {
                        return;
                    }
                    var tasks = this.getTasks().data;
                    // console.log(sortFn,tasks)
                    tasks.sort((a, b) => {
                        return sortFn(a, b)
                    })
                    if (desc) {
                        tasks.reverse();
                    }
                    this.setupGanttSortFunc(sortFn, desc);
                    // console.log(tasks.map(item=>item.id).join(" "))
                    var key = [app.projectId, "ganttSort", this.queryForm.objectType].join("_");
                    var gsort = [];
                    if (!!tasks && !Array.isEmpty(tasks)) {
                        tasks.forEach((item, idx) => {
                            if (item.id && item.task && item.task.parentId === 0) {
                                gsort.push(item.id)
                            }
                        })
                    }
                    app.saveObject(key, gsort);
                }
            });


        },
        methods: {
            clearSortCache() {
                let key = [app.projectId, "ganttSort", app.objectType].join("_");
                let key1 = [app.projectId, "ganttSortFunc", app.objectType].join("_");
                app.confirm('确认重置自定义的排序吗?', () => {
                    app.removeObject(key);
                    app.removeObject(key1);
                    app.toast("重置完成");
                    this.reloadData();
                });

            },
            pageMessage(type, content) {
                if (type == 'task.edit') {
                    if (content || this.isTimeUpdating) {
                        let _this = this;
                        this.loadData(() => {
                            setTimeout(() => {
                                _this.appGanttSortFunc(content);
                                _this.buildView();
                                _this.isTimeUpdating = false;
                            }, 30)
                        });
                    } else {
                        this.reloadData();
                    }
                }
                /*  if(type=='task.create'){
                      console.log("task.create event")
                      var currentGantt = this.queryForm.objectType == app.objectType && app.projectId == this.queryForm.projectId;
                      if(currentGantt){
                          setTimeout(()=>{this.appGanttSortFunc(content)},600)
                      }
                  }*/
            },
            computeShowColumns() {
                let list = this.tableFieldList;
                if (Array.isArray(this.viewSetting.hideTableFieldList)) {
                    list = this.tableFieldList.filter(item => this.viewSetting.hideTableFieldList.indexOf(item.id) === -1);
                }
                const columns = [];
                list.forEach(item => {
                    if (item.field === 'text') {
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
                                return a.id - b.id;
                            },
                        });
                    } else if (item.field === 'start_date') {
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
                                // console.log(a,b)
                                if (!a.task || !b.task) {
                                    return 0;
                                }
                                return a.start_date - b.start_date;
                            },
                        });
                    } else if (item.field === 'end_date') {
                        columns.push({
                            name: 'end_date',
                            resize: true,
                            label: '截止时间',
                            width: '160',
                            align: 'center',
                            template(obj) {
                                var end = formatDate(obj.end_date.getTime());
                                var isFinish = (obj.task && obj.task.isFinish) || false;
                                //项目集不显示延期信息
                                var isPs = obj.task && obj.task.objectType == 1001;
                                var diffDay = 0;
                                if (obj.task && obj.task.finishTime) {
                                    diffDay = window.dateDiff(new Date(obj.task.finishTime), new Date(obj.end_date));
                                }
                                var leftday = window.getLeftDays(obj.end_date);
                                if (isPs) {
                                    return end;
                                }
                                if (isFinish) {
                                    if (diffDay < 0) {
                                        return end + "[延期" + (diffDay * -1) + "天]";
                                    } else if (diffDay > 0) {
                                        return end + "[提前" + (diffDay) + "天]";
                                    }
                                } else {
                                    if (leftday < 0) {
                                        return end + "[延期" + (leftday * -1) + "天]";
                                    } else {
                                        return end;
                                    }
                                }
                                return end;

                                // if (isFinish && !isPs) {
                                //     if (diffDay < 0) {
                                //         return end + "[延期" + (diffDay * -1) + "天]";
                                //     } else if (diffDay > 0) {
                                //         return end + "[提前" + (diffDay) + "天]";
                                //     }
                                // } else {
                                //     if (leftday < 0 && !isPs) {
                                //         return end + "[延期" + (leftday * -1) + "天]";
                                //     } else {
                                //         return end;
                                //     }
                                // }

                            },
                            sort(a, b) {
                                if (!a.task || !b.task) {
                                    return 0;
                                }
                                return a.end_date - b.end_date;
                            },
                        });
                    } else if (item.field === 'status' && !this.isProjectSet) {
                        columns.push({
                            name: "status",
                            resize: true,
                            label: "状态",
                            width: '70',
                            align: "center",
                            template(obj) {
                                if (obj.task && obj.status && obj.status != 'undefined') {
                                    return "<span style='color:" + obj.color + "'>" + (obj.status || "") + "</span>"
                                }
                                return "";
                            },
                            sort(a, b) {
                                if (!a.task || !b.task) {
                                    return 0;
                                }
                                return a.task.statusType - b.task.statusType;
                            },
                        });
                    } else if (item.field === 'owner') {
                        columns.push({
                            name: 'owner',
                            resize: true,
                            label: '责任人',
                            width: '100',
                            align: 'center',
                            template(obj) {
                                if (obj.task) {
                                    return encodeHTML(obj.owner);
                                }
                                return '';
                            },
                            sort: false,
                        });
                    } else if (item.field === 'priority') {
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
                                return a.task.prioritySortWeight - b.task.prioritySortWeight;
                            },
                        });
                    } else if (item.field === 'categories') {
                        columns.push({
                            name: 'categories',
                            resize: true,
                            label: '分类',
                            min_width: 100,
                            width: '*',
                            align: 'center',
                            template: (object) => {
                                if (!Array.isArray(this.queryInfo.categoryList)) {
                                    return;
                                }
                                if (object.task) {
                                    if (!Array.isArray(object.task.categoryIdList) || object.task.categoryIdList.length === 0) {
                                        return '<span style="color:#ccc;">未设置</span>';
                                    }
                                    const html = [];
                                    html.push('<div>');
                                    object.task.categoryIdList.forEach(item => {
                                        const category = this.queryInfo.categoryList.find(_ => _.id === item);
                                        if (!category) {
                                            return;
                                        }
                                        html.push(`<span class="gantt-task-tag" style="background-color:${category.color + '55'}"><span style="color:${category.color}">${category.name}</span></span>`);
                                    });
                                    html.push('</div>');
                                    return html.join('');
                                }
                                return '分类';
                            },
                            sort: false,
                        });
                    }
                });
                return columns;
            },
            onFieldsChange() {
                this.saveViewSetting();
                this.ganttChart.config.columns = this.computeShowColumns();
                this.ganttChart.render();
            },
            onResetFieldsOrder() {
                this.tableFieldList = [...this.definedFieldList];
                this.saveViewSetting();
                this.ganttChart.config.columns = this.computeShowColumns();
                this.ganttChart.render();
            },
            onFieldListSort(val) {
                this.tableFieldList = val;
                this.viewSetting.tableFieldList = this.tableFieldList;
                this.saveViewSetting();
                this.ganttChart.config.columns = this.computeShowColumns();
                this.ganttChart.render();
            },
            changePage(delta) {
                var t = this.pageQuery.pageIndex + delta;
                if (t <= 0 || t > this.pageQuery.totalPage) {
                    return;
                }
                this.pageQuery.pageIndex = t;
                this.loadData();
            },
            exportMsProject() {
                app.toast('微软project文件即将生成，请稍后');
                this.ganttChart.exportToMSProject({
                    locale: "cn",
                    name: this.queryInfo.project.name + " gantt_mpp.xml"
                });
            },
            exportPng() {
                app.toast('图片即将生成，请稍后');
                // 中文会出现乱码情况
                this.ganttChart.exportToPNG({
                    locale: "cn",
                    name: this.queryInfo.project.name + " gantt.png"
                });
            },
            exportPdf() {
                app.toast('PDF即将生成，请稍后');
                // 中文会出现乱码情况
                this.ganttChart.exportToPDF({
                    locale: "cn",
                    name: this.queryInfo.project.name + " gantt.pdf"
                });
            },
            setupResourceLayout() {
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
                            name: "allocated", label: "对象数量", align: "center", width: 100, template: (resource) => {
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
            toggleGroups() {
                // console.log("toggleGroups--->")
                var ganttChart = this.ganttChart;
                if (this.isGroupByUser) {
                    var groups = this.resourcesStore.getItems().map(function (item) {
                        var group = ganttChart.copy(item);
                        group.group_id = group.id;
                        group.id = ganttChart.uid();
                        return group;
                    });
                    ganttChart.groupBy({
                        groups: groups,
                        relation_property: ganttChart.config.resource_property,
                        group_id: "group_id",
                        group_text: "text",
                        delimiter: ",",
                        default_group_label: "未分配"
                    });
                } else {
                    ganttChart.groupBy(false);
                }
            },
            toggleResourceLayout() {
                var ganttChart = this.ganttChart;
                this.setupResourceLayout();
                ganttChart.init(this.$refs.gantt)
            },
            toggleWorkday() {
                var ganttChart = this.ganttChart;
                ganttChart.config.work_time = this.showWorkday;
                ganttChart.render();
            },
            expandAll() {
                var ganttChart = this.ganttChart;
                ganttChart.eachTask(function (task) {
                    task.$open = true;
                });
                ganttChart.render();
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
            hasField(name) {
                if (this.queryInfo.fieldList) {
                    for (var i = 0; i < this.queryInfo.fieldList.length; i++) {
                        var f = this.queryInfo.fieldList[i];
                        if (f.field == name) {
                            return true;
                        }
                    }
                }
                return false;
            },
            setViewType(type) {
                this.viewSetting.viewType = type;
                this.saveViewSetting();
            },

            saveViewSetting() {
                var objectType = this.queryForm.objectType;
                var projectId = this.queryForm.projectId;
                this.viewSetting.pageSize = this.pageQuery.pageSize;
                app.saveObject('TaskPage.GanttView.viewSetting' + objectType + "." + projectId, this.viewSetting);
            },
            loadViewSetting: function () {
                var objectType = this.queryForm.objectType;
                var projectId = this.queryForm.projectId;
                var vc = app.loadObject('TaskPage.GanttView.viewSetting' + objectType + '.' + projectId);
                if (vc != null) {
                    if (vc.pageSize) {
                        this.pageQuery.pageSize = vc.pageSize;
                    }
                    if (vc.tableSort == null) {
                        vc.tableSort = {
                            name: null,
                            sort: null,
                        };
                    }
                    if (vc.viewType == null) {
                        vc.viewType = 'week';
                    }
                    this.viewSetting = vc;
                }
            },
            checkViewSetting() {
                let hasChange = true;
                if (this.queryForm.nameSort > 0) {
                    this.viewSetting.tableSort.name = 'name';
                    this.viewSetting.tableSort.sort = this.queryForm.nameSort;
                } else if (this.queryForm.ownerAccountNameSort > 0) {
                    this.viewSetting.tableSort.name = 'ownerAccountName';
                    this.viewSetting.tableSort.sort = this.queryForm.ownerAccountNameSort;
                } else if (this.queryForm.priorityNameSort > 0) {
                    this.viewSetting.tableSort.name = 'priorityName';
                    this.viewSetting.tableSort.sort = this.queryForm.priorityNameSort;
                } else if (this.queryForm.startDateSort > 0) {
                    this.viewSetting.tableSort.name = 'startDate';
                    this.viewSetting.tableSort.sort = this.queryForm.startDateSort;
                } else if (this.queryForm.endDateSort > 0) {
                    this.viewSetting.tableSort.name = 'endDate';
                    this.viewSetting.tableSort.sort = this.queryForm.endDateSort;
                } else {
                    hasChange = false;
                }
                // 重置掉已有排序,重新计算排序字段
                this.queryForm.nameSort = undefined;
                this.queryForm.ownerAccountNameSort = undefined;
                this.queryForm.priorityNameSort = undefined;
                this.queryForm.startDateSort = undefined;
                this.queryForm.endDateSort = undefined;
                if (hasChange) {
                    this.saveViewSetting();
                }
            },
            loadData(callback) {
                this.checkViewSetting();
                this.queryForm.pageIndex = this.pageQuery.pageIndex;
                this.queryForm.pageSize = this.pageQuery.pageSize;
                const query = Object.assign({}, this.queryForm);
                query.customFieldsSort = {};
                query.parentId = this.pageQuery.parentId;
                if (this.viewSetting.tableSort.name != null) {
                    query[this.viewSetting.tableSort.name + 'Sort'] = this.viewSetting.tableSort.sort;
                    this.$set(this.queryForm, this.viewSetting.tableSort.name + 'Sort', this.viewSetting.tableSort.sort);
                }
                this.projectFinishDisabled = this.isProjectDisabled(query.projectId);
                //开启多个甘特图后会丢失objectType
                query.objectType = app.objectType;
                query.projectId = app.projectId;
                app.invoke('BizAction.getTaskInfoListAndAssociateTasks', [app.token, query], info => {
                    this.tableData = info.list;
                    this.pageQuery.total = info.count;
                    if (info.count === 0) {
                        this.pageQuery.totalPage = 1;
                    } else {
                        this.pageQuery.totalPage = Math.ceil(info.count / this.pageQuery.pageSize);
                    }
                    if (this.queryForm.iterationId) {
                        this.loadIterationInfo(this.queryForm.iterationId);
                    } else {
                        this.buildView();
                    }
                    if (!!callback && Object.isFunction(callback)) {
                        callback.apply();
                    }
                });
            },
            selectPageSize(size) {
                this.pageSizePopuptipVisible = false;
                this.pageQuery.pageSize = size;
                this.pageQuery.pageIndex = 1;
                this.saveViewSetting();
                this.loadData();
            },
            loadIterationInfo(id) {
                app.invoke('BizAction.getProjectIterationInfoById', [app.token, id], (info) => {
                    this.buildView(info);
                });
            },
            reloadData() {
                if (this.isFieldSorting) {
                    //隐藏列名上的排序角标
                    var sortDiv = this.$el.getElementsByClassName("gantt_sort")[0];
                    if (sortDiv) {
                        sortDiv.click();
                    }
                }
                this.pageQuery.pageIndex = 1;
                this.loadData();
            },
            buildView(info) {
                console.log('buildView  task');
                this.newGanttActive = true;
                const ganttChart = this.ganttChart;
                //排序状态切换视图驻留的问题
                ganttChart.config.sort = true;
                var tasks = this.getTasks();
                if (info) {
                    var mil = this.getMilstone(info);
                    tasks.data = mil.data.concat(tasks.data);
                    tasks.links = mil.links.concat(tasks.links);
                }
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
                    if (!!today) {
                        today.start_date = new Date();
                        ganttChart.updateMarker(todayId);
                    }
                }, 1000 * 300);
                // console.log("render tasks--->", tasks.data.map(k => k.id).join(","))
                ganttChart.parse(tasks);
                this.resourcesStore.parse(this.getResource());
                ganttChart.render();
                // this.toggleGroups();
            },
            getResource() {
                var allUsers = [];
                var allUserList = [];
                for (var i = 0; i < this.tableData.length; i++) {
                    var item = this.tableData[i];
                    if (item.ownerAccountList) {
                        item.ownerAccountList.forEach((element) => {
                            if (allUsers[element.id] == null) {
                                var q = {
                                    id: element.id,
                                    text: element.name,
                                    taskCount: 1
                                };
                                allUsers[element.id] = q;
                                allUserList.push(q);
                            } else {
                                allUsers[element.id].taskCount++;
                            }
                        });
                    }
                }
                return allUserList;
            },
            getMilstone(info) {
                const ganttChart = this.ganttChart;
                const tasks = [];
                const links = [];
                for (let i = 0; i < info.stepList.length; i++) {
                    const t = info.stepList[i];
                    const task = {
                        start_date: new Date(t.startDate),
                        end_date: new Date(t.endDate),
                        text: t.name,
                        id: 'm' + t.id,
                        type: ganttChart.config.types.meeting,
                    };
                    tasks.push(task);
                }
                for (let i = 0; i < info.stepList.length - 1; i++) {
                    const t1 = info.stepList[i];
                    const t2 = info.stepList[i + 1];
                    links.push({
                        id: links.length,
                        source: 'm' + t1.id,
                        target: 'm' + t2.id,
                        type: 0,
                    });
                }
                return {
                    data: tasks,
                    links: links
                };
            },
            //有查询参数则不进行主动排序
            hasQueryCondition() {
                var obj = this.queryForm;
                var keys = ["filterId", "iterationId", "serialNo", "name", "ownerAccountId", "priorityInList", "categoryIdList"];
                var exist = false;
                if (!app.isEmptyOrNull(obj)) {
                    for (let i = 0; i < keys.length; i++) {
                        if (!app.isEmptyOrNull(obj[keys[i]])) {
                            exist = true;
                            break;
                        }
                    }
                    for (let key in obj) {
                        if (key.indexOf("Sort") > 0) {
                            if (!app.isEmptyOrNull(obj[key])) {
                                exist = true;
                                break;
                            }
                        }
                    }
                    if (!!obj.customFields) {
                        for (let key in obj.customFields) {
                            if (!app.isEmptyOrNull(obj.customFields[key])) {
                                exist = true;
                                break;
                            }
                        }
                    }

                }
                return exist;
            },
            setupGanttSort(taskId, parentId, tarIdx) {
                if (this.hasQueryCondition()) {
                    console.log("skip gantt auto sort --------------------------->")
                    return;
                }
                console.warn(app.objectType, this.queryForm.objectType, "   xxxx  ", app.projectId, this.queryForm.projectId)
                var key = [app.projectId, "ganttSort", app.objectType].join("_");
                if (parentId > 0) { //子对象移动
                    return;
                }
                var gsort = app.loadObject(key);
                if (!gsort) {
                    gsort = [];
                    var tasks = this.getTasks().data;
                    if (!!tasks && !Array.isEmpty(tasks)) {
                        tasks.forEach((item, idx) => {
                            if (item.id && item.task && item.task.parentId === 0) {
                                gsort.push(item.id)
                            }
                        })
                    }
                }
                if (gsort.length <= tarIdx) {
                    return;
                }
                var srcIdx = gsort.findIndex(item => item == taskId);
                if (srcIdx === tarIdx) {
                    return;
                }
                if (srcIdx > tarIdx) {
                    gsort.splice(tarIdx, 0, taskId);
                    gsort.splice(srcIdx + 1, 1);
                } else {
                    gsort.splice(tarIdx + 1, 0, taskId);
                    gsort.splice(srcIdx, 1);
                }
                app.saveObject(key, gsort);
            },
            /**
             * 保存排序函数
             * @param sortFunc
             * @param desc  是否倒序
             */
            setupGanttSortFunc(sortFunc, desc) {
                let key = [app.projectId, "ganttSortFunc", app.objectType].join("_");
                let key1 = [app.projectId, "ganttSortFuncDesc", app.objectType].join("_");
                if (!!sortFunc) {
                    app.saveObject(key, sortFunc.toString());
                    app.saveObject(key1, desc);
                } else {
                    app.removeObject(key);
                    app.removeObject(key1);
                }
            },
            appGanttSortFunc(taskId) {
                let key = [app.projectId, "ganttSortFunc", app.objectType].join("_");
                let key1 = [app.projectId, "ganttSortFuncDesc", app.objectType].join("_");
                let fn = app.loadObject(key);
                let desc = app.loadObject(key1);
                if (!!fn) {
                    let sortFunc = eval('(' + fn + ')');

                    this.ganttChart.callEvent("onAfterSort", [sortFunc, desc])
                    // console.log("appGanttSortFunc --->",taskId)
                }

            },
            appGanttSort() {
                if (app.objectType != this.queryForm.objectType) {
                    console.error("appGanttSort object dismatch", app.objectType, this.queryForm.objectType)
                    return;
                }

                if (this.hasQueryCondition()) {
                    console.log("skip gantt auto sort-------<")
                    return;
                }
                var key = [app.projectId, "ganttSort", this.queryForm.objectType].join("_");
                var gsort = app.loadObject(key);
                if (gsort && gsort.length > 0) {
                    const sortByObject = gsort.reduce(
                        (obj, item, index) => ({
                            ...obj,
                            [item]: index + 1
                        }), {}
                    );
                    this.tableData.sort((a, b) => {
                        return parseInt(sortByObject[b.id] || 0) - parseInt(sortByObject[a.id] || 0)
                    })
                }
            },
            getTasks() {
                const tasks = [];
                const links = [];
                this.appGanttSort();
                for (let i = this.tableData.length - 1; i >= 0; i--) {
                    const item = this.tableData[i];
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
                    // if (startDate.getTime() === endDate.getTime()) {
                    //     endDate = new Date(startDate.getTime() + (3600 * 24 * 1000 - 1));
                    // }
                    //TODO 2020-06-20 兼容当前后台返回结束时间为00:00:00.000的情况(默认结束时间为当天23:59:59.999)
                    const dayTimes = 3600 * 24 * 1000;
                    endDate = new Date(endDate.getTime() + dayTimes - 1);
                    const ownerList = [];
                    const ownerIdList = [];
                    if (item.ownerAccountList) {
                        item.ownerAccountList.forEach((element) => {
                            ownerList.push(element.name);
                            ownerIdList.push(element.id)
                        });
                    }
                    const task = {
                        start_date: startDate,
                        end_date: endDate,
                        text: item.name,
                        id: item.id,
                        serialNo: item.serialNo,
                        progress: item.progress / 100,
                        task: item,
                        resources: ownerIdList,
                        owner: ownerList.join(','),
                        //兼容项目集
                        color: item.customFields.statusColor || item.statusColor,
                        finish: item.isFinish,
                        isFinish: item.isFinish,
                        finishTime: item.finishTime,
                        objectType: item.objectType,
                        parent: item.parentId,
                        uuid: item.uuid,
                        status: item.statusName,
                    }
                    if (item.parentId > 0) {
                        links.push({
                            id: links.length,
                            source: item.parentId,
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
            dateDiff: function (dt1, dt2) {
                return Math.floor((Date.UTC(dt2.getFullYear(), dt2.getMonth(), dt2.getDate())
                    - Date.UTC(dt1.getFullYear(), dt1.getMonth(), dt1.getDate())) / (1000 * 60 * 60 * 24));
            },
            updateTaskStartDate(task, startDate, endDate) {
                if (this.projectFinishDisabled) {
                    app.toast("项目已完成，无法进行编辑");
                    this.reloadData()
                    return;
                }
                var _this = this;
                this.debounceEvent("gantt.updateTime", () => {
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
                        this.isTimeUpdating = true;
                        app.postMessage('task.edit')
                    },
                    () => {
                        this.reloadData()
                    }
                );
            },
            showTaskInfo(uuid) {
                this.debounceEvent("gantt.showTime", () => {
                    app.showDialog(TaskDialog, {
                        taskId: uuid
                    })
                })
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
            }
        }
    }
</script>
