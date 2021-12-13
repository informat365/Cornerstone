<style scoped>

.it-name-label{
  color:#333333;
  font-size:16px;
  font-weight: bold;
  text-align: left;
  display: flex;
  align-items: center;
}
.it-user-label{
  font-size:12px;
  color: #999999;
}

.it-description{
  margin-top:10px;
  font-size:14px;
  color:#666666;
  text-align: left;
}
.chart-container{
    width:400px;
    height: 400px;
    display: inline-block;
    text-align: center;
}
.chart-container-title{
    font-size:14px;
    text-align: center;
    margin-top:5px;
}
    .count-label{
        color:#999;
        font-size:13px;
    }
    .count-value{
        font-size:20px;
        font-weight: bold;
        color:#F84F84;
    }
    .count-value-big{
        font-size:50px;
        margin-top:20px;
    }
    .count-box{
        padding:20px;
        font-weight: bold;
        text-align: center;
    }
    .it-name{
        max-width:200px;
        display: inline-block;
    }
</style>
<i18n>
{
    "en": {
        "迭代信息": "Iteration",
        "创建":"Create",
        "燃尽图":"Burndown chart",
        "天视图":"Day",
        "周视图":"Week",
        "月视图":"Month",
        "延期":"Delayed",
        "完成":"Finished",
        "总数":"Total",
        "预期":"Expect",
        "实际":"Actual"
    },
    "zh_CN": {
        "迭代信息": "迭代信息",
        "创建":"创建",
        "燃尽图":"燃尽图",
        "天视图":"天视图",
        "周视图":"周视图",
        "月视图":"月视图",
        "延期":"延期",
        "完成":"完成",
        "总数":"总数",
        "预期":"预期",
        "实际":"实际"
    }
}
</i18n>
<template>
 <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('迭代信息')" width="1000" :footer-hide="true">
    <div >
        <Row>
              <Col span="12" class="it-name-label">
                <div class="it-name text-no-wrap">{{iterationInfo.name}} </div>
                <span style="color:#666666;font-size:12px;padding-left:5px">
                    <DataDictLabel style="margin-right:10px" type="ProjectIteration.status" :value="iterationInfo.status"/>{{iterationInfo.startDate|fmtDate}} ~  {{iterationInfo.endDate|fmtDate}}
                </span>
              </Col>
                <Col span="12" style="text-align:right">
                   <span  class="create-user-label">
                    <AvatarImage  size="small" :value="iterationInfo.createAccountImageId" :name="iterationInfo.createAccountName" type="label"/>
                     <span style="padding-left:10px;vertical-align: text-top;">{{iterationInfo.createTime|fmtDate}} {{$t('创建')}}</span>

                     </span>
                    <IconButton v-if="prjPerm('project_iteration_config')" @click="showEditIterationDialog(iterationInfo)"
                    icon="ios-settings-outline"></IconButton>
                </Col>
           </Row>

           <div class="it-description">
              {{iterationInfo.description}}
           </div>
        <div style="min-height:500px;height:600px">
             <Tabs value="stage" :animated="false" style="margin-top:15px">
                <TabPane label="阶段" name="stage">
                    <div style="text-align:right">
                         <RadioGroup v-model="stageViewType" style="margin-left:20px" >
                                <Radio label="Day" >{{$t('天视图')}}</Radio>
                                <Radio label="Week">{{$t('周视图')}}</Radio>
                                <Radio label="Month">{{$t('月视图')}}</Radio>
                        </RadioGroup>
                    </div>
                    <div style="margin-top:20px" class="scrollbox gantt-target hover-scroll"></div>

                </TabPane>
                <TabPane :label="$t('燃尽图')" name="burndown">
                    <div style="text-align:right">
                        <Select style="width: 100px;" v-model="burndownQuery.type" @on-change="loadBurnDownChartData">
                            <Option v-for="item in burndownTypeList" :value="item.value" :key="item.value">{{ item.name }}</Option>
                        </Select>
                        <Select style="width: 100px;" v-model="burndownQuery.objectType" @on-change="loadBurnDownChartData">
                            <Option v-for="item in objectTypeList" :value="item.objectType" :key="item.objectType">{{ item.name }}</Option>
                        </Select>
                    </div>
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

    </Modal>
</template>

<script>
import { graphic } from 'echarts/lib/export'
export default {
    mixins: [componentMixin],
    props:['iterationId'],
    data(){
        return {
            iterationInfo:{},
            rateList:[],
            stageViewType:'Month',
            chartOptions:{
                color:['#0094FB','#EC0023'],
                tooltip: {
                    trigger: 'axis'
                },
                toolbox: {

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
            burndownQuery:{
                type:1,
                objectType:0
            },
            burndownTypeList:[
                {value:1,name:'按任务'},
                {value:2,name:'按工作量'},
            ]
        }
    },
    watch:{
        "stageViewType":function(val){
            this.gantt_chart.change_view_mode(val);
        },
        "iterationId":function(val){
            this.loadData();
        }
    },
    computed:{
        objectTypeList(){
            let array =[{objectType:0,name:'全部'}]
            let list =app.moduleList;
            if(!!list){
               list= list.filter(k=>k.isStatusBased&&k.objectType>0)
            }
            if(!!list){
                return array.concat(list);
            }else{
                return array;
            }
        }
    },

    methods:{
        pageLoad(){
            this.loadData();
            this.loadBurnDownChartData();
            this.loadFinishRateData();
        },
        pageMessage(type){
            if(type=='iteration.edit'){
               this.loadData();
            }
            if(type=='iteration.delete'){
               this.showDialog=false;
            }
        },
        loadData(){
            app.invoke('BizAction.getProjectIterationInfoById',[app.token,this.args.id],(info)=>{
                this.iterationInfo=info;
                this.setupStageGannt(info.stepList)
            });
        },
        loadFinishRateData(){
            app.invoke('BizAction.getIterationFinishDelayRate',[app.token,this.args.id],(info)=>{
                this.rateList=info;
            });
        },
        loadBurnDownChartData(){
            this.chartOptions.series[0].data=[];
            this.chartOptions.series[1].data=[];
            this.chartOptions.xAxis.data=[];
            app.invoke('BizAction.getIterationBurnDownChart',[app.token,this.args.id,this.burndownQuery.type,this.burndownQuery.objectType],(list)=>{
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
        showEditIterationDialog(item){
            app.showDialog(IterationEditDialog,{
                item:item
            })
        },
        setupStageGannt(stages){
            var tasks = stages.map(function(item, i) {
                var task= {
                    start: new Date(item.startDate),
                    end: new Date(item.endDate),
                    name: item.name,
                    id: "stage " + i,
                    progress: 100
                }
                var today=new Date();
                if(today.getTime()>name.endDate){
                    task.custom_class = "bar-overdue";
                }
                return task;
            });
            this.$el.getElementsByClassName('gantt-target')[0].innerHTML="";
            this.gantt_chart= new FGantt(".gantt-target", tasks, {
                readonly:true,
                on_date_change: function(task, start, end) {
                    console.log(task, start, end);
                },
            });
            this.gantt_chart.change_view_mode('Month')
        }
  }
}
</script>
