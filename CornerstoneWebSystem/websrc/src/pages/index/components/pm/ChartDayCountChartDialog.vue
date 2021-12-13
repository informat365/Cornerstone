<style scoped>
    .page {
        min-height: calc(100vh - 51px);
        background-color: #F1F4F5;
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

    .chart-item {
        width: 100%;
        padding: 20px;
    }
</style>
<i18n>
    {
    "en": {
    "累计数量": "Total count",
    "项目":"Project",
    "迭代":"Iteration",
    "类型":"Type",
    "开始时间":"Start",
    "结束时间":"End",
    "查询":"Query",
    "暂无数据":"No data",
    "数量统计":"Count stat"
    },
    "zh_CN": {
    "累计数量": "累计数量",
    "项目":"项目",
    "迭代":"迭代",
    "类型":"类型",
    "开始时间":"开始时间",
    "结束时间":"结束时间",
    "查询":"查询",
    "暂无数据":"暂无数据",
    "数量统计":"数量统计"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog"
        :closable="true"
        :mask-closable="false"
        :loading="false" :title="$t('数量统计')" class="fullscreen-modal"
        width="100%"
        :footer-hide="true">
        <div class="page">
            <div class="chart-content">
                <div class="chart-opt-bar">
                    <Form inline>
                        <FormItem>
                            <Select v-model="formItem.projectId" :placeholder="$t('项目')"
                                    filterable  style="width:200px">
                                <Option v-for="item in projectList" :value="item.id" :key="'prj'+item.id">{{ item.name}}
                                </Option>
                            </Select>
                        </FormItem>

                        <FormItem>
                            <Select clearable v-model="formItem.iterationId" :placeholder="$t('迭代')"
                                    style="width:150px">
                                <Option v-for="item in iterationList" :value="item.id" :key="'it'+item.id">{{ item.name
                                    }}
                                </Option>
                            </Select>
                        </FormItem>

                        <FormItem>
                            <Select clearable v-model="formItem.objectType" :placeholder="$t('类型')" style="width:150px">
                                <Option v-for="item in moduleList" v-if="item.objectType>0" :value="item.objectType"
                                        :key="'md'+item.id">{{ item.name }}
                                </Option>
                            </Select>
                        </FormItem>

                        <FormItem>
                            <ExDatePicker clearable type="date" style="width:130px" v-model="formItem.statDateStart"
                                          :placeholder="$t('开始时间')"></ExDatePicker>
                        </FormItem>

                        <FormItem>
                            <ExDatePicker clearable :day-end="true" type="date" style="width:130px" v-model="formItem.statDateEnd"
                                          :placeholder="$t('结束时间')"></ExDatePicker>
                        </FormItem>

                        <FormItem>
                            <Button :disabled="formItem.projectId==null" type="default" @click="loadChartData()">
                                {{$t('查询')}}
                            </Button>
                        </FormItem>

                    </Form>
                </div>

                <!-- 状态分布-->
                <div class="chart-item">
                    <Card>
                        <p slot="title">状态分布<span style="font-size: 12px;color: #C1C1C1">(需指定类型)</span></p>
                        <div class="chart-chart" v-show="statusDistributeChartOptions.series[0].data.length>0">
                            <v-chart :autoresize="true" :options="statusDistributeChartOptions"
                                     style="width:100%;height:400px"/>
                        </div>
                        <div class="chart-nodata" v-if="statusDistributeChartOptions.series[0].data.length==0">
                            {{$t('暂无数据')}}
                        </div>
                    </Card>
                </div>
                <!-- 每日新增-->
                <div class="chart-item">
                    <Card>
                        <p slot="title">每日新增</p>
                        <div class="chart-chart" v-show="dailyAddChartOptions.xAxis.data.length>0">
                            <v-chart :autoresize="true" :options="dailyAddChartOptions"
                                     style="width:100%;height:400px"/>
                        </div>
                        <div class="chart-nodata" v-if="dailyAddChartOptions.xAxis.data.length==0">
                            {{$t('暂无数据')}}
                        </div>
                    </Card>
                </div>
                <!-- 每日完成-->
                <div class="chart-item">
                    <Card>
                        <p slot="title">每日完成</p>
                        <div class="chart-chart" v-show="dailyFinishChartOptions.xAxis.data.length>0">
                            <v-chart :autoresize="true" :options="dailyFinishChartOptions"
                                     style="width:100%;height:400px"/>
                        </div>
                        <div class="chart-nodata" v-if="dailyFinishChartOptions.xAxis.data.length==0">
                            {{$t('暂无数据')}}
                        </div>
                    </Card>
                </div>
                <!-- 累计数量-->
                <div class="chart-item">
                    <Card>
                        <p slot="title">累计数量</p>
                        <div class="chart-chart" v-show="totalCountChartOptions.xAxis.data.length>0">
                            <v-chart :autoresize="true" :options="totalCountChartOptions"
                                     style="width:100%;height:400px"/>
                        </div>
                        <div class="chart-nodata" v-if="totalCountChartOptions.xAxis.data.length==0">
                            {{$t('暂无数据')}}
                        </div>
                    </Card>
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
                dailyFinishChartOptions: {
                    color: ['#0094FB', '#CCCCCC'],
                    tooltip: {
                        trigger: 'axis'
                    },
                    toolbox: {
                        feature: {
                            dataView: {readOnly: true},
                            saveAsImage: {}
                        }
                    },
                    xAxis: {
                        data: [],
                        type: 'category',
                    },
                    yAxis: {
                        type: 'value'
                    },
                    legend: {
                        data: ['数量']
                    },
                    series: [{
                        name: '数量',
                        data: [],
                        type: 'line'
                    }]
                },
                dailyAddChartOptions: {
                    color: ['#0094FB', '#CCCCCC'],
                    tooltip: {
                        trigger: 'axis'
                    },
                    toolbox: {
                        feature: {
                            dataView: {readOnly: true},
                            saveAsImage: {}
                        }
                    },
                    xAxis: {
                        data: [],
                        type: 'category',
                    },
                    yAxis: {
                        type: 'value'
                    },
                    legend: {
                        data: ['数量']
                    },
                    series: [{
                        name: '数量',
                        data: [],
                        type: 'line'
                    }]
                },
                statusDistributeChartOptions: {
                    tooltip: {
                        trigger: 'axis'
                    },
                    toolbox: {
                        feature: {
                            dataView: {readOnly: true},
                            saveAsImage: {}
                        }
                    },
                    legend: {
                        data: []
                    },
                    series: [{
                        name: '数量',
                        data: [],
                        type: 'pie',
                        radius: '55%',
                        itemStyle: {
                            emphasis: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        },
                        label: {
                            normal: {
                                formatter: '{b}:{c}: ({d}%)',
                                textStyle: {
                                    fontWeight: 'normal',
                                    fontSize: 12
                                }
                            }
                        }
                    }]
                },
                totalCountChartOptions: {
                    color: ['#0094FB', '#CCCCCC'],
                    tooltip: {
                        trigger: 'axis'
                    },
                    toolbox: {
                        feature: {
                            dataView: {readOnly: true},
                            saveAsImage: {}
                        }
                    },
                    xAxis: {
                        data: [],
                        type: 'category',
                    },
                    yAxis: {
                        type: 'value'
                    },
                    legend: {
                        data: ['数量']
                    },
                    series: [{
                        name: '数量',
                        data: [],
                        type: 'line'
                    }]
                },
                projectList: [],
                iterationList: [],
                moduleList: [],
                formItem: {
                    projectId: null,
                    iterationId: null,
                    objectType: null,
                    statDateStart: null,
                    statDateEnd: null,
                }
            }
        },
        watch: {
            "formItem.projectId": function (val) {
                this.loadIterationList();
                this.loadProjectModule();
            },
        },
        methods: {
            pageLoad() {
                this.loadProjectList();
            },
            loadProjectList() {
                app.invoke("BizAction.getMyProjectList", [app.token], list => {
                    this.projectList = list;
                });
            },
            loadProjectModule() {
                this.formItem.objectType = null;
                app.invoke('BizAction.getProjectModuleInfoList', [app.token, this.formItem.projectId], (list) => {
                    this.moduleList = list;
                })
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
            loadChartData() {
                //总量
                this.totalCountChartOptions.series[0].data = [];
                this.totalCountChartOptions.xAxis.data = [];
                app.invoke('BizAction.getTaskTotalNumList', [app.token, this.formItem], (list) => {
                    for (var i = 0; i < list.length; i++) {
                        var t = list[i];
                        this.totalCountChartOptions.xAxis.data.push(formatDate(t.statDate));
                        this.totalCountChartOptions.series[0].data.push(t.num);
                    }
                });
                //每日完成
                this.dailyFinishChartOptions.series[0].data = [];
                this.dailyFinishChartOptions.xAxis.data = [];
                app.invoke('BizAction.getTaskFinishList', [app.token, this.formItem], (list) => {
                    for (var i = 0; i < list.length; i++) {
                        var t = list[i];
                        this.dailyFinishChartOptions.xAxis.data.push(formatDate(t.statDate));
                        this.dailyFinishChartOptions.series[0].data.push(t.num);
                    }
                });
                //每日新增
                this.dailyAddChartOptions.series[0].data = [];
                this.dailyAddChartOptions.xAxis.data = [];
                app.invoke('BizAction.getTaskCreateDayDataList', [app.token, this.formItem], (list) => {
                    for (var i = 0; i < list.length; i++) {
                        var t = list[i];
                        this.dailyAddChartOptions.xAxis.data.push(formatDate(t.statDate));
                        this.dailyAddChartOptions.series[0].data.push(t.num);
                    }
                });
                //状态分布
                this.statusDistributeChartOptions.series[0].data = [];
                this.statusDistributeChartOptions.legend.data = []
                app.invoke('BizAction.getTaskCurrStatusDistributeList', [app.token, this.formItem], (list) => {
                    for (var i = 0; i < list.length; i++) {
                        var t = list[i];
                        //this.chartSettingsColors.push(t.statusColor);
                        this.statusDistributeChartOptions.legend.data.push(t.statusName)
                        this.statusDistributeChartOptions.series[0].data.push({
                            name: t.statusName,
                            value: t.num
                        })
                    }
                });
            },
        }
    }
</script>
