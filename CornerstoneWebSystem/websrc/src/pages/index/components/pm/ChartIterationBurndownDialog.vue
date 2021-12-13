<style scoped>
.page{
  min-height: calc(100vh - 51px);
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
        "燃尽图": "BurnDown chart",
        "项目":"Project",
        "迭代":"Iteration",
        "类型":"Type",
        "开始时间":"Start",
        "结束时间":"End",
        "查询":"Query",
        "暂无数据":"No Data",
        "预期":"expected",
        "实际":"actual"
    },
    "zh_CN": {
        "燃尽图": "燃尽图",
        "项目":"项目",
        "迭代":"迭代",
        "类型":"类型",
        "开始时间":"开始时间",
        "结束时间":"结束时间",
        "查询":"查询",
        "暂无数据":"暂无数据",
        "预期":"预期",
        "实际":"实际"
    }
}
</i18n>
<template>
 <Modal
        ref="dialog"  v-model="showDialog"
        :closable="true"
        :mask-closable="false"
        :loading="false" :title="$t('燃尽图')" class="fullscreen-modal"
         width="100%"
         :footer-hide="true">
    <div class="page">
        <div class="chart-content">
            <div class="chart-opt-bar">
                 <Form inline>
                    <FormItem>
                       <Select v-model="formItem.projectId" :placeholder="$t('项目')" filterable style="width:200px">
                            <Option v-for="item in projectList" :value="item.id" :key="'prj'+item.id">{{ item.name }}</Option>
                        </Select>
                    </FormItem>

                     <FormItem>
                       <Select v-model="formItem.iterationId" :placeholder="$t('迭代')" style="width:150px">
                            <Option v-for="item in iterationList" :value="item.id" :key="'it'+item.id">{{ item.name }}</Option>
                        </Select>
                    </FormItem>

                    <FormItem>
                        <Button :disabled="formItem.iterationId==null" type="default" @click="loadBurnDownChartData()">{{$t('查询')}}</Button>
                    </FormItem>

                 </Form>
            </div>
            <div class="chart-chart">
                <div class="chart-chart" v-show="chartOptions.series[0].data.length>0">
                    <v-chart :autoresize="true" :options="chartOptions" style="width:100%;height:400px"/>
                </div>
                <div class="chart-nodata" v-if="chartOptions.series[0].data.length==0">
                    {{$t('暂无数据')}}
                </div>
            </div>
        </div>
    </div>
</Modal>
</template>

<script>
import { graphic } from 'echarts/lib/export'
export default {
    mixins: [componentMixin],
    data(){
        return {
             chartOptions:{
                color:['#0094FB','#EC0023'],
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
                    data:[this.$t('预期'),this.$t("实际")]
                },
                series: [
                    {
                        name: '预期',
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
            projectList:[],
            iterationList:[],
            formItem:{
                projectId:null,
                iterationId:null,
            }
        }
    },
    watch:{
        "formItem.projectId":function(val){
            this.loadIterationList();
        },
        "formItem.iterationId":function(val){
            if(val){
                this.loadBurnDownChartData();
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
        loadBurnDownChartData(){
            this.chartOptions.series[0].data=[];
            this.chartOptions.series[1].data=[];
            this.chartOptions.xAxis.data=[];

            app.invoke('BizAction.getIterationBurnDownChart',[app.token,this.formItem.iterationId,1,0],(list)=>{
                for(var i=0;i<list.length;i++){
                    var t = list[i];
                    this.chartOptions.xAxis.data.push(formatDate(t.statDate));
                    this.chartOptions.series[0].data.push(t.expectUnfinishNum);
                    if(t.unfinishNum>=0){
                        this.chartOptions.series[1].data.push(t.unfinishNum);
                    }
                }

            });
        },
    }
}
</script>
