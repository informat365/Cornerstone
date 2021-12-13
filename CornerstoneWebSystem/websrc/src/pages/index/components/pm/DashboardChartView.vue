<style scoped>
    .table-box {
        padding: 20px;
        border-radius: 3px;
    }

    .table-info {
        font-weight: bold;
        color: #999;
        padding-left: 15px;
    }

    .report-card {
        display: block;
        width: 150px;
        margin-right: 10px;
        margin-bottom: 10px;
        cursor: pointer;
        text-align: center;
    }

    .report-card img {
        width: 100px;
        height: 100px;
    }

    .data-table-name {
        font-size: 14px;
    }

    .data-table-remark {
        font-size: 12px;
        color: #666;
    }

    .datatable-card {
        display: block;
        width: 150px;
        height: 100px;
        margin-right: 10px;
        margin-bottom: 10px;
        cursor: pointer;
        text-align: center;
        overflow: hidden;
        padding-top: 10px;
    }

    .card-box {
        display: flex;
        flex-wrap: wrap;
    }
</style>
<i18n>
    {
    "en": {
    "项目报表": "Project Charts",
    "全局报表": "Global Charts",
    "数据表格": "Data Table",
    "迭代燃尽图": "Burndown chart",
    "代码提交统计": "Code stat",
    "状态分布统计": "Status stat",
    "每日新增曲线": "Daily added",
    "每日完成曲线": "Daily finished",
    "累计数量曲线": "Total count",
    "成员工时列表": "Member Worktime",
    "成员缺陷统计": "Member bugs",
    "项目进度": "Project stat",
    "迭代进度": "Iteration stat",
    "迭代日历": "Iteration calander",
    "成员任务":"Member tasks",
    "项目工时列表":"Project Worktime",
    "项目报表":"Project Report",
    "项目数量统计":"Project count stat",
    "全局人力视图":"Total todo tasks",
    "工时统计":"Worktime stat"
    },
    "zh_CN": {
    "项目报表": "项目报表",
    "全局报表": "全局报表",
    "数据表格": "数据表格",
    "迭代燃尽图": "迭代燃尽图",
    "代码提交统计": "代码提交统计",
    "状态分布统计": "状态分布统计",
    "每日新增曲线": "每日新增曲线",
    "每日完成曲线": "每日完成曲线",
    "累计数量曲线": "累计数量曲线",
    "成员工时列表": "成员工时列表",
    "成员缺陷统计": "成员缺陷统计",
    "项目进度": "项目进度",
    "迭代进度": "迭代进度",
    "迭代日历": "迭代日历",
    "成员任务":"成员任务",
    "项目工时列表":"项目工时列表",
    "项目报表":"项目报表",
    "项目数量统计":"项目数量统计",
    "全局人力视图":"全局人力视图",
    "工时统计":"工时统计"
    }
    }
</i18n>
<template>
    <div class="table-box" style="padding:20px;">
        <template v-if="perm('chart_project_view')">
            <div class="info-section-row">
                <div class="info-section-header">
                    {{$t('项目报表')}}
                </div>

            </div>
            <div class="card-box" style="margin-top:20px">
                <Card
                    @click.native="showReport(item.id)"
                    class="report-card"
                    v-for="item in showProjectReportList"
                    :key="item.id">
                    <img :src="'/image/report_icon/'+item.id+'.png'">
                    <div>{{item.name}}</div>
                </Card>
            </div>
        </template>

        <template v-if="perm('chart_company_view')">
            <div class="info-section-row">
                <div class="info-section-header">
                    {{$t('全局报表')}}
                </div>
            </div>
            <div class="card-box" style="margin-top:20px">
                <Card
                    @click.native="showReport(item.id)"
                    class="report-card"
                    v-for="item in showGlobalReportList"
                    :key="item.id">
                    <img :src="'/image/report_icon/'+item.id+'.png'">
                    <div>{{item.name}}</div>
                </Card>
            </div>
        </template>

        <template v-if="perm('chart_data_table_view')&&datatableList.length>0">
            <div class="info-section-row">
                <div class="info-section-header">
                    {{$t('数据表格')}}
                </div>
            </div>
            <div class="card-box" style="margin-top:20px">
                <Card
                    @click.native="showDatatable(item.id)"
                    class="datatable-card"
                    v-for="item in datatableList"
                    :key="item.id">
                    <div class="data-table-name">{{item.name}}</div>
                    <div class="data-table-remark">{{item.remark}}</div>
                </Card>
            </div>
        </template>
    </div>
</template>

<script>
    export default {
        mixins: [componentMixin],
        data() {

            return {
                showProjectReportList: [],
                showGlobalReportList: [],
                projectReportList: {
                    r0: { id: 0, name: this.$t('迭代燃尽图') },
                    r1: { id: 1, name: this.$t('代码提交统计') },
                    // r2: { id: 2, name: this.$t('状态分布统计') },
                    // r3: { id: 3, name: this.$t('每日新增曲线') },
                    // r4: { id: 4, name: this.$t('每日完成曲线') },
                    // r5: { id: 5, name: this.$t('累计数量曲线') },
                    r5: { id: 5, name: this.$t('项目数量统计') },
                    // r6: { id: 6, name: this.$t('成员工时列表') },
                    // r7: { id: 7, name: this.$t('项目工时列表') },
                    r7: { id: 7, name: this.$t('工时统计') },
                    r8: { id: 8, name: this.$t('项目报表') },
                    r9: { id: 9, name: this.$t('成员缺陷统计') },
                },
                globalReportList: {
                    r100: { id: 100, name: this.$t('项目进度') },
                    r101: { id: 101, name: this.$t('迭代进度') },
                    r102: { id: 102, name: this.$t('迭代日历') },
                    r103: { id: 103, name: this.$t('成员任务') },
                    r104: { id: 104, name: this.$t('全局人力视图') },
                },
                datatableList: [],
            };
        },
        created() {
            this.compReportList();
        },
        mounted() {
            this.loadDatatable();
        },
        methods: {
            compReportList() {
                const reportConfig = window.app.reportConfig || {};
                Object.freeze(this.projectReportList);
                Object.freeze(this.globalReportList);
                const showProjectReportList = [];
                const showGlobalReportList = [];
                if (Array.isArray(reportConfig.projectReport) && reportConfig.projectReport.length > 0) {
                    reportConfig.projectReport.forEach(id => {
                        const config = this.projectReportList['r' + id];
                        if (!config) {
                            return;
                        }
                        showProjectReportList.push({
                            ...config,
                        });
                    });
                } else {
                    Object.keys(this.projectReportList).forEach(key => {
                        const config = this.projectReportList[key];
                        if (!config) {
                            return;
                        }
                        showProjectReportList.push({
                            ...config,
                        });
                    });
                }
                if (Array.isArray(reportConfig.companyReport) && reportConfig.companyReport.length > 0) {
                    reportConfig.companyReport.forEach(id => {
                        const config = this.globalReportList['r' + id];
                        if (!config) {
                            return;
                        }
                        showGlobalReportList.push({
                            ...config,
                        });
                    });
                } else {
                    Object.keys(this.globalReportList).forEach(key => {
                        const config = this.globalReportList[key];
                        if (!config) {
                            return;
                        }
                        showGlobalReportList.push({
                            ...config,
                        });
                    });
                }
                this.showProjectReportList = showProjectReportList;
                this.showGlobalReportList = showGlobalReportList;
            },
            loadDatatable() {
                app.invoke('DataTableAction.getMyDataTableList', [app.token], (list) => {
                    this.datatableList = list;
                });
            },
            showDatatable(id) {
                app.showDialog(ChartDatatableDialog, {
                    id: id,
                });
            },
            showReport: function (id) {
                if (id === 0) {
                    app.showDialog(ChartIterationBurndownDialog);
                    return;
                }
                if (id === 1) {
                    app.showDialog(ChartProjectCodeDialog);
                    return;
                }
              /*  if (id === 2) {
                    app.showDialog(ChartDistributeChartDialog);
                    return;
                }
                if (id === 3) {
                    app.showDialog(ChartDayAddLineChartDialog);
                    return;
                }
                if (id === 4) {
                    app.showDialog(ChartDayFinishLineChartDialog);
                    return;
                }
                if (id === 5) {
                    app.showDialog(ChartDayTotalLineChartDialog);
                    return;
                }*/
                //2-5报表四合一
                if(id===5){
                    app.showDialog(ChartDayCountChartDialog);
                    return;
                }

               /* if (id === 6) {
                    app.showDialog(ChartDayWorktimeChartDialog);
                    return;
                }

                if (id === 7) {
                    app.showDialog(ChartProjectWorktimeDialog);
                    return;
                }*/
                //6-7报表二合一
                if(id===7){
                    app.showDialog(ChartWorktimeDialog);
                    return;
                }

                if (id === 8) {
                    app.showDialog(ChartProjectWeeklyDialog);
                    return;
                }
                if (id === 9) {
                    app.showDialog(ChartMemberBugsDialog);
                    return;
                }

                if (id === 100) {
                    app.showDialog(ChartProjectProgressDialog);
                    return;
                }

                if (id === 101) {
                    app.showDialog(ChartIterationProgressDialog);
                    return;
                }

                if (id === 102) {
                    app.showDialog(ChartIterationGanttDialog);
                    return;
                }

                if (id === 103) {
                    app.showDialog(ChartMemberTaskDialog);
                    return;
                }

                if (id === 104) {
                    app.showDialog(ChartMemberTodoDialog);
                    return;
                }

            },
        },
    };
</script>
