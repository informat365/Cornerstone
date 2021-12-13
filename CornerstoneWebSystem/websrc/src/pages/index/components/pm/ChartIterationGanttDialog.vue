<style scoped>
.page{
  min-height: calc(100vh - 51px);
  background-color: #F1F4F5;
  width: 100%;
  padding:20px;
  text-align: center;
}
.chart-content{
    width:calc(100vw - 40px);
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
.rate{
    font-size:18px;
    font-weight: bold;
    margin-right:10px;
    color:#0097F7;
}
.delay{
    font-size:18px;
    font-weight: bold;
    margin-right:10px;
    color:#F84F84;
}
</style>
<i18n>
{
    "en": {
        "迭代日历": "Iteration calendar",
        "项目":"Project",
        "迭代":"Iteration",
        "查询":"Query",
        "没有加入任何项目":"No Data",
        "周视图":"Week",
        "天视图":"Day",
        "月视图":"Month"
    },
    "zh_CN": {
        "迭代日历": "迭代日历",
        "项目":"项目",
        "迭代":"迭代",
        "查询":"查询",
        "没有加入任何项目":"没有加入任何项目",
        "周视图":"周视图",
        "天视图":"天视图",
        "月视图":"月视图"
    }
}
</i18n>
<template>
 <Modal
        ref="dialog"  v-model="showDialog" 
        :closable="true" 
        :mask-closable="false"
        :loading="false" :title="$t('迭代日历')" class="fullscreen-modal"
         width="100%" 
         :footer-hide="true">
    <div class="page">
        <div class="chart-content">
            <div class="chart-chart">
              

               <div style="text-align:right">
                         <RadioGroup v-model="stageViewType" style="margin-left:20px" >
                                <Radio label="Day" >{{$t('天视图')}}</Radio>
                                <Radio label="Week">{{$t('周视图')}}</Radio>
                                <Radio label="Month">{{$t('月视图')}}</Radio>
                        </RadioGroup>
                    </div>
                <div style="margin-top:20px" class="scrollbox gantt-target hover-scroll"></div>

            <div class="table-nodata" v-if="projectList!=null&&projectList.length==0">
                {{$t('没有加入任何项目')}}
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
            projectList:null,
            stageViewType:'Week',
        }
    },
    watch:{
        "stageViewType":function(val){
            this.gantt_chart.change_view_mode(val);
        },
    },
    methods:{
        pageLoad(){
            this.loadProjectList(); 
        },
        loadProjectList(){
            app.invoke("BizAction.getIterationProgressReportList",[app.token],list => {
                this.projectList=list;
                this.setupStageGannt(list);
            });
        },
        setupStageGannt(stages){
            var tasks = stages.map(function(item, i) {
                var task= {
                    start: new Date(item.startDate),
                    end: new Date(item.endDate),
                    name: item.projectName+"【"+item.iterationName+"】"
                        +"【"+formatDate(item.startDate)+"~"+formatDate(item.endDate)+"】",
                    id: "stage " + i,
                    progress: 0
                }
                var today=new Date();
                task.custom_class = "bar-color-"+(i%10);
                if(today.getTime()>item.endDate){
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
            this.gantt_chart.change_view_mode('Week')
        }
    }
}
</script>