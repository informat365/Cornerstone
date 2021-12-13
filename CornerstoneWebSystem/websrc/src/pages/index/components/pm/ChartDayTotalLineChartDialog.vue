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
        "累计数量": "Total count",
        "项目":"Project",
        "迭代":"Iteration",
        "类型":"Type",
        "开始时间":"Start",
        "结束时间":"End",
        "查询":"Query",
        "暂无数据":"No Data"
    },
    "zh_CN": {
        "累计数量": "累计数量",
        "项目":"项目",
        "迭代":"迭代",
        "类型":"类型",
        "开始时间":"开始时间",
        "结束时间":"结束时间",
        "查询":"查询",
        "暂无数据":"暂无数据"
    }
}
</i18n>
<template>
 <Modal
        ref="dialog"  v-model="showDialog"
        :closable="true"
        :mask-closable="false"
        :loading="false" :title="$t('累计数量')" class="fullscreen-modal"
         width="100%"
         :footer-hide="true">
    <div class="page">
        <div class="chart-content">
            <div class="chart-opt-bar">
                 <Form inline>
                    <FormItem>
                       <Select v-model="formItem.projectId" :placeholder="$t('项目')" style="width:200px">
                            <Option v-for="item in projectList" :value="item.id" :key="'prj'+item.id">{{ item.name }}</Option>
                        </Select>
                    </FormItem>

                    <FormItem>
                       <Select clearable v-model="formItem.iterationId" :placeholder="$t('迭代')" style="width:150px">
                            <Option v-for="item in iterationList" :value="item.id" :key="'it'+item.id">{{ item.name }}</Option>
                        </Select>
                    </FormItem>

                     <FormItem>
                       <Select clearable v-model="formItem.objectType" :placeholder="$t('类型')" style="width:150px">
                            <Option v-for="item in moduleList" v-if="item.objectType>0" :value="item.objectType" :key="'md'+item.id">{{ item.name }}</Option>
                        </Select>
                    </FormItem>

                    <FormItem>
                        <ExDatePicker type="date" style="width:130px" v-model="formItem.statDateStart" :placeholder="$t('开始时间')"></ExDatePicker>
                    </FormItem>

                    <FormItem>
                        <ExDatePicker type="date" :day-end="true" style="width:130px" v-model="formItem.statDateEnd" :placeholder="$t('结束时间')"></ExDatePicker>
                    </FormItem>

                    <FormItem>
                        <Button :disabled="formItem.projectId==null" type="default" @click="loadChartData()">{{$t('查询')}}</Button>
                    </FormItem>

                 </Form>
            </div>
            <div class="chart-chart" v-show="chartOptions.xAxis.data.length>0">
                <v-chart :autoresize="true" :options="chartOptions" style="width:100%;height:400px"/>
            </div>
            <div class="chart-nodata" v-if="chartOptions.xAxis.data.length==0">
                {{$t('暂无数据')}}
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
            chartOptions:{
                 color:['#0094FB','#CCCCCC'],
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
                    data:[],
                    type: 'category',
                },
                yAxis: {
                    type: 'value'
                },
                legend: {
                    data:['数量']
                },
                series: [{
                    name: '数量',
                    data: [],
                    type: 'line'
                }]
            },
            projectList:[],
            iterationList:[],
            moduleList:[],
            formItem:{
                projectId:null,
                iterationId:null,
                objectType:null,
                statDateStart:null,
                statDateEnd:null,
            }
        }
    },
    watch:{
        "formItem.projectId":function(val){
            this.loadIterationList();
            this.loadProjectModule();
        },
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
        loadProjectModule(){
            this.formItem.objectType=null;
            app.invoke('BizAction.getProjectModuleInfoList',[app.token,this.formItem.projectId],(list)=>{
                this.moduleList=list;
            })
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
            this.chartOptions.series[0].data=[];
            this.chartOptions.xAxis.data=[];
            app.invoke('BizAction.getTaskTotalNumList',[app.token,this.formItem],(list)=>{
                for(var i=0;i<list.length;i++){
                    var t = list[i];
                    this.chartOptions.xAxis.data.push(formatDate(t.statDate));
                    this.chartOptions.series[0].data.push(t.num);
                }
            });
        },
    }
}
</script>
