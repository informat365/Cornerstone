<style scoped>
.page{
  min-height: calc(100vh - 62px);
  background-color: #F1F4F5;
  width: 100%;
  padding:20px;
  text-align: center;
}
.chart-content{
    width:1200px;
    display: inline-block;
    background-color: #fff;
    border:1px solid #eee;
    border-radius: 5px;
    text-align: left;
}
.chart-opt-bar{
    padding:20px;
    border-bottom: 1px solid #eee;
}
.chart-chart{
    padding:20px;
}
</style>
<i18n>
{
    "en": {
        "代码提交统计": "Code stat",
        "项目":"Project",
        "迭代":"Iteration",
        "查询":"Query",
        "每日提交行数":"Daily line count",
        "累计提交行数":"Total line count",
        "作者提交分布":"Author",
        "暂无数据":"No Data",
        "数量":"Count"
    },
    "zh_CN": {
        "代码提交统计": "代码提交统计",
        "项目":"项目",
        "迭代":"迭代",
        "查询":"查询",
        "每日提交行数":"每日提交行数",
        "累计提交行数":"累计提交行数",
        "作者提交分布":"作者提交分布",
        "暂无数据":"暂无数据",
        "数量":"数量"
    }
}
</i18n>
<template>
 <Modal
        ref="dialog"  v-model="showDialog"
        :closable="true"
        :mask-closable="false"
        :loading="false" :title="$t('代码提交统计')" class="fullscreen-modal"
         width="100%"
         :footer-hide="true">
    <div class="page">
        <div class="chart-content">
            <div class="chart-opt-bar">
                 <Form inline>
                    <FormItem>
                       <Select  v-model="formItem.projectId" :placeholder="$t('项目')" filterable style="width:200px">
                            <Option v-for="item in projectList" :value="item.id" :key="'prj'+item.id">{{ item.name }}</Option>
                        </Select>
                    </FormItem>

                     <FormItem>
                       <Select clearable v-model="formItem.iterationId" :placeholder="$t('迭代')" style="width:150px">
                            <Option v-for="item in iterationList" :value="item.id" :key="'it'+item.id">{{ item.name }}</Option>
                        </Select>
                    </FormItem>

                    <FormItem>
                        <Button :disabled="formItem.projectId==null"  type="default" @click="loadChartData()">{{$t('查询')}}</Button>
                    </FormItem>

                 </Form>
            </div>
            <div class="chart-chart">
                    <div>{{$t('每日提交行数')}}</div>
                    <div class="chart-chart" v-show="dayChartOptions.xAxis.data.length>0">
                        <v-chart :autoresize="true" :options="dayChartOptions" style="width:100%;height:400px"/>
                    </div>
                    <div class="chart-nodata" v-if="dayChartOptions.xAxis.data.length==0">
                        {{$t('暂无数据')}}
                    </div>

                    <div>{{$t('累计提交行数')}}</div>
                    <div class="chart-chart" v-show="totalChartOptions.xAxis.data.length>0">
                        <v-chart :autoresize="true" :options="totalChartOptions" style="width:100%;height:400px"/>
                    </div>
                    <div class="chart-nodata" v-if="totalChartOptions.xAxis.data.length==0">
                        {{$t('暂无数据')}}
                    </div>

                     <div>{{$t('作者提交分布')}}</div>
                    <div class="chart-chart" v-show="authorChartOptions.xAxis.data.length>0">
                        <v-chart :autoresize="true" :options="authorChartOptions" style="width:100%;height:400px"/>
                    </div>
                    <div class="chart-nodata" v-if="authorChartOptions.xAxis.data.length==0">
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
    data(){
        return {
             dayChartOptions:{
                  color:['#0094FB','#CCCCCC'],
                tooltip: {
                    trigger: 'axis'
                },
                toolbox: {
                    feature: {
                        saveAsImage: {}
                    }
                },
                xAxis: {
                    data:[],
                    type: 'category',
                },
                yAxis: {
                    type: 'value'
                },
                legend: {
                    data:[this.$t('数量')]
                },
                series: [{
                    name: this.$t('数量'),
                    data: [],
                    type: 'line'
                }]
            },
            authorChartOptions:{
                color:['#0094FB','#CCCCCC'],
                tooltip: {
                    trigger: 'axis'
                },
                toolbox: {
                    feature: {
                        saveAsImage: {}
                    }
                },
                xAxis: {
                    data:[],
                    type: 'category',
                },
                yAxis: {
                    type: 'value'
                },
                legend: {
                    data:[this.$t('数量')]
                },
                series: [{
                    name: this.$t('数量'),
                    data: [],
                    type: 'bar',
                    barWidth : 30,
                    itemStyle: {
                        normal: {
                            color: function(params) {
                                return chartColorList[params.dataIndex]
                            }
                        },
                    },

                }]
            },
            totalChartOptions:{
                color:['#0094FB','#CCCCCC'],
                tooltip: {
                    trigger: 'axis'
                },
                toolbox: {
                    feature: {
                        saveAsImage: {}
                    }
                },
                xAxis: {
                    data:[],
                    type: 'category',
                },
                yAxis: {
                    type: 'value'
                },
                legend: {
                    data:[this.$t('数量')]
                },
                series: [{
                    name: this.$t('数量'),
                    data: [],
                    type: 'line'
                }]
            },
            projectList:[],
            iterationList:[],
            formItem:{
                projectId:null,
                iterationId:null,
            },
        }
    },
    watch:{
        "formItem.projectId":function(val){
            this.loadIterationList();
        },
        "formItem.iterationId":function(val){
            if(val){
                this.loadChartData();
            }
        }
    },
    methods:{
        pageLoad(){
            this.loadProjectList();
        },
        loadProjectList(){
            app.invoke("BizAction.getMyProjectList",[app.token],list => {
                this.projectList=list;
            });
        },
        loadIterationList(){
            if(this.formItem.projectId==null){
                this.iterationList=[];
                return;
            }
            app.invoke("BizAction.getProjectIterationInfoList",[app.token,this.formItem.projectId],list => {
                this.iterationList=list;
                this.formItem.iterationId=null;
            })
        },
        loadChartData(){
            this.totalChartOptions.series[0].data=[];
            this.totalChartOptions.xAxis.data=[];
            //
            this.dayChartOptions.series[0].data=[];
            this.dayChartOptions.xAxis.data=[];
            //
            this.authorChartOptions.xAxis.data=[];
            this.authorChartOptions.series[0].data=[];
            //
            if(!!this.formItem.iterationId&&this.formItem.iterationId>0){
                app.invoke('BizAction.getScmCommitStatInfo',[app.token,this.formItem.iterationId],(info)=>{
                    for(var i=0;i<info.totalList.length;i++){
                        var t = info.totalList[i];
                        this.totalChartOptions.xAxis.data.push(t.item);
                        this.totalChartOptions.series[0].data.push(t.values[0])
                    }
                    for(var i=0;i<info.dailyList.length;i++){
                        var t = info.dailyList[i];
                        this.dayChartOptions.xAxis.data.push(t.item);
                        this.dayChartOptions.series[0].data.push(t.values[0])
                    }
                    for(var i=0;i<info.authorList.length;i++){
                        var t = info.authorList[i];
                        this.authorChartOptions.xAxis.data.push(t.item);
                        this.authorChartOptions.series[0].data.push(t.values[0])
                    }
                });
            }else{
                if(!!this.formItem.projectId&&this.formItem.projectId>0){
                    app.invoke('BizAction.getScmCommitStatInfoByProjectId',[app.token,this.formItem.projectId],(info)=>{
                        for(var i=0;i<info.totalList.length;i++){
                            var t = info.totalList[i];
                            this.totalChartOptions.xAxis.data.push(t.item);
                            this.totalChartOptions.series[0].data.push(t.values[0])
                        }
                        for(var i=0;i<info.dailyList.length;i++){
                            var t = info.dailyList[i];
                            this.dayChartOptions.xAxis.data.push(t.item);
                            this.dayChartOptions.series[0].data.push(t.values[0])
                        }
                        for(var i=0;i<info.authorList.length;i++){
                            var t = info.authorList[i];
                            this.authorChartOptions.xAxis.data.push(t.item);
                            this.authorChartOptions.series[0].data.push(t.values[0])
                        }
                    });
                }
            }

        },
    }
}
</script>
