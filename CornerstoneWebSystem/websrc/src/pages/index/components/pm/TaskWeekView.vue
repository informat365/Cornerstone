<style scoped>
    .week-container {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 20px;
    }

    .report-title-row {
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .week-report {
        width: 1000px;
        background-color: #fff;
        border: 1px solid #e5e5e5;
        border-radius: 3px;
        min-height: calc(100vh - 150px);
        text-align: left;
        position: relative;
        padding: 40px;
    }

    .report-title {
        text-align: center;
        font-size: 20px;
    }

    .report-section-title {
        margin-top: 25px;
        margin-bottom: 7px;
    }

    .info-value {
        font-size: 20px;
        color: #333;
        font-weight: bold;
        text-align: center;
        margin-bottom: 5px;
    }

    .info-label {
        color: #666;
        font-size: 14px;
        text-align: center;
    }
</style>
<i18n>
    {
    "en": {
    "周汇总":"{0} Summary",
    "周":"Week",
    "月":"Month",
    "本周计划":"This {0}",
    "本周未完成":"Unfinished",
    "本周已完成":"Finished",
    "本周超期":"Overdue",
    "下周计划":"Next {0}",
    "历史已超期":"All Overdue"
    },
    "zh_CN": {
    "周汇总":"{0}汇总",
    "周":"周",
    "月":"月",
    "本周计划":"本{0}计划",
    "本周未完成":"本{0}未完成",
    "本周已完成":"本{0}已完成",
    "本周超期":"本{0}超期",
    "下周计划":"下{0}计划",
    "历史已超期":"历史已超期"
    }
    }
</i18n>
<template>
    <div class="week-container">
        <div v-if="summaryInfo" class="week-report">
            <Row :gutter="20">
                <Col :span="6">
                    &nbsp;
                </Col>
                <Col :span="12">
                    <div class="report-title-row">
                        <IconButton @click="changePage(-1)" icon="md-arrow-round-back"></IconButton>
                        <h2 class="report-title">
                            {{summaryInfo.weekStart|fmtDate}} ~ {{summaryInfo.weekEnd|fmtDate}} {{ summaryLabel }} </h2>
                        <IconButton @click="changePage(1)" icon="md-arrow-round-forward"></IconButton>
                    </div>
                </Col>
                <Col :span="6" style="text-align: right;">
                    <RadioGroup v-model="summaryType" type="button" @on-change="onSummaryTypeChange">
                        <Radio :label="0">{{ $t('周') }}</Radio>
                        <Radio :label="1">{{ $t('月') }}</Radio>
                    </RadioGroup>
                </Col>
            </Row>

            <Row style="margin-top:30px">
                <Col span="6">
                    <div class="info-value">{{summaryInfo.thisWeekTasks.length}}</div>
                    <div class="info-label">{{ thisPlanLabel }}</div>
                </Col>
                <Col span="6">
                    <div class="info-value">{{unfinishedTask}}</div>
                    <div class="info-label">{{ thisUnFinishLabel }}</div>
                </Col>
                <Col span="6">
                    <div class="info-value">
                        {{summaryInfo.thisWeekTasks.length-unfinishedTask}}
                        / {{percent(summaryInfo.thisWeekTasks.length-unfinishedTask,summaryInfo.thisWeekTasks.length)}}
                    </div>
                    <div class="info-label">{{ thisFinishLabel }}</div>
                </Col>
                <Col span="6">
                    <div class="info-value">{{overdueTask}}</div>
                    <div class="info-label">{{ thisOverdueLabel }}</div>
                </Col>

            </Row>
            <h3 class="report-section-title">{{ thisPlanLabel }}（{{summaryInfo.thisWeekTasks.length}}）</h3>
            <TaskWeekViewTable
                :type="viewTableType" :query-info="queryInfo" :taskList="summaryInfo.thisWeekTasks"></TaskWeekViewTable>

            <h3 class="report-section-title">{{ nextPlanLabel }}（{{summaryInfo.nextWeekTasks.length}}）</h3>
            <TaskWeekViewTable
                :type="viewTableType" :query-info="queryInfo" :taskList="summaryInfo.nextWeekTasks"></TaskWeekViewTable>

            <h3 class="report-section-title">{{$t('历史已超期')}}（{{summaryInfo.dueTasks.length}}）</h3>
            <TaskWeekViewTable type="due" :query-info="queryInfo" :taskList="summaryInfo.dueTasks"></TaskWeekViewTable>
        </div>
    </div>
</template>

<script>
    export default {
        mixins: [componentMixin],
        props: ['queryInfo', 'queryForm'],
        data() {
            return {
                title: 'TaskWeekView',
                summaryInfo: null,
                page: 0,
                summaryType: 0,
            };
        },
        computed: {
            viewTableType(){
                return this.summaryType === 0 ? 'normal' : 'date';
            },
            summaryLabel() {
                if (this.summaryType === 0) {
                    return this.$t('周汇总', [this.$t('周')]);
                }
                return this.$t('周汇总', [this.$t('月')]);
            },
            thisOverdueLabel() {
                if (this.summaryType === 0) {
                    return this.$t('本周超期', [this.$t('周')]);
                }
                return this.$t('本周超期', [this.$t('月')]);
            },
            thisFinishLabel() {
                if (this.summaryType === 0) {
                    return this.$t('本周未完成', [this.$t('周')]);
                }
                return this.$t('本周未完成', [this.$t('月')]);
            },
            thisUnFinishLabel() {
                if (this.summaryType === 0) {
                    return this.$t('本周未完成', [this.$t('周')]);
                }
                return this.$t('本周未完成', [this.$t('月')]);
            },
            thisPlanLabel() {
                if (this.summaryType === 0) {
                    return this.$t('本周计划', [this.$t('周')]);
                }
                return this.$t('本周计划', [this.$t('月')]);
            },
            nextPlanLabel() {
                if (this.summaryType === 0) {
                    return this.$t('下周计划', [this.$t('周')]);
                }
                return this.$t('下周计划', [this.$t('月')]);
            },
            unfinishedTask() {
                var v = 0;
                for (var i = 0; i < this.summaryInfo.thisWeekTasks.length; i++) {
                    var t = this.summaryInfo.thisWeekTasks[i];
                    if (!t.isFinish) {
                        v++;
                    }
                }
                return v;
            },
            overdueTask() {
                var v = 0;
                for (var i = 0; i < this.summaryInfo.thisWeekTasks.length; i++) {
                    var t = this.summaryInfo.thisWeekTasks[i];
                    var leftDay = window.getLeftDays(t.endDate);
                    if (!t.isFinish && leftDay < 0) {
                        v++;
                    }
                }
                return v;
            },
        },
        methods: {
            pageMessage(type) {
                if (type === 'task.edit') {
                    this.loadData();
                }
            },
            percent(v1, v2) {
                if (v1 < 0) {
                    return '--';
                }
                if (v2 === 0) {
                    return '--';
                }
                return (v1 * 100 / v2).toFixed(1) + '%';
            },
            changePage(delta) {
                this.page += delta;
                this.loadData();
            },
            onSummaryTypeChange() {
                setTimeout(() => {
                    this.loadData();
                }, 0);
            },
            loadData() {
                app.invoke('BizAction.getTaskWeekInfo', [app.token, this.queryForm, this.page, this.summaryType], info => {
                    this.summaryInfo = info;
                });
            },
        },
    };
</script>
