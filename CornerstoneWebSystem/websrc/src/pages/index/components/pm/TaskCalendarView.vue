<style scoped>
.page-content-top{
    width:100%;
    display: inline-block;
    padding-top:5px;
    text-align: left;
    font-weight: bold;
}
.location-total-label{
    font-size:12px;
    color:#999;
    line-height: 20px;
    text-align: right;
}
 .calendar-view{
        width:100%;
        height:calc(100vh - 135px);
        background-color: #F1F4F5;
        display: flex;
        flex-direction: column;
        flex-grow: 1;
        padding-right:10px;
        padding-bottom:10px;
    }
</style>
<i18n>
{
    "en": {
         "总计": "Total {0} items"
    },
    "zh_CN": {
        "总计": "总计 {0} 条数据"
    }
}
</i18n>
<template>
<div >
    <Row class="page-content-top">
        <Col span="24">
            <div style="color:#999;padding-top:3px;font-size:12px">
                {{$t('总计',[tableData.length])}}
            </div>
        </Col>
    </Row>
     <div class="calendar-view">
      <calendar-view ref="calendar"
	    :show-date="showDate"
        :starting-day-of-week="1"
		@show-date-change="setShowDate"
        :events="eventList"
        @click-event="clickEvent"
        @drop-on-date="dropOnDate"
        @click-date="clickDate"
        :enableDragDrop="true"
		class="theme-default">
      </calendar-view>

    </div>

 </div>
</template>

<script>
export default {
    mixins: [componentMixin],
    props:['queryInfo','queryForm'],
    data(){
        return {
            title:"TaskCalandarView",
            tableData:[],
            finishCount:0,
            allCount:0,
            showDate: new Date(),
            eventList:[],
        }
    },
    methods:{
        pageMessage(type){
            if(type=='task.edit'){
                this.loadData();
            }
        },
        loadData(){
            this.queryForm.pageIndex=1;
            this.queryForm.pageSize=1000;
            app.invoke("BizAction.getTaskInfoList",[app.token,this.queryForm],info => {
                this.tableData=info.list;
                this.getTasks();
            });
        },
        getTasks(){
            this.eventList=[];
            for(var i=0;i<this.tableData.length;i++){
                    var task=this.tableData[i];
                    var event={
                        id:task.id,
                        title:"【"+task.statusName+"】"+task.name,
                        type:"",
                        uuid:task.uuid,
                        taskId:task.id,
                        taskType:task.objectType,
                        priorityColor:task.priorityColor,
                        classes:'calendar-event-'+((task.id%8)+1)
                    }
                    if(task.startDate!=null){
                        event.startDate=new Date(task.startDate);
                    }else{
                        if(event.endDate!=null){
                            event.startDate=new Date(event.endDate);
                        }else{
                            event.startDate=new Date();
                        }

                    }
                    if(task.endDate!=null){
                        event.endDate=new Date(task.endDate);
                    }else{
                        if(event.startDate!=null){
                            event.endDate=new Date(event.startDate);
                        }else{
                            event.endDate=new Date();
                        }
                    }
                    if(event.startDate.getTime()>event.endDate.getTime()){
                        event.endDate=event.startDate;
                    }
                    //
                    this.eventList.push(event);
            }
        },
        dateDiff:function(dt1,dt2){
            return Math.floor((Date.UTC(dt2.getFullYear(), dt2.getMonth(), dt2.getDate())
                     - Date.UTC(dt1.getFullYear(), dt1.getMonth(), dt1.getDate()) ) /(1000 * 60 * 60 * 24));
        },
        updateTaskStartDate(event,time){
            var obj={}
            obj.id=event.taskId;
            var diff=0;
            if(event.startDate!=null&&event.endDate!=null){
                diff=event.endDate.getTime()-event.startDate.getTime();
            }
            obj.startDate=time;
            obj.endDate=new Date(time.getTime()+diff);

            app.invoke('BizAction.getTaskAssociateStat', [app.token, event.taskId], info => {
                if (info.beforeCount > 0 || info.afterCount > 0||info.subCount>0) {
                    app.confirm('该对象关联有前后置对象或子对象，是否需要同步其开始截止时间?', () => {
                        this.updateTaskTimeNative(obj, false);
                    }, () => {
                        this.updateTaskTimeNative(obj,  true);
                    },'需要','不需要');
                }else{
                    this.updateTaskTimeNative(obj,  true);
                }
            }, (error) => {
                this.loadData();
            });
            // app.invoke("BizAction.updateTask",[app.token,obj,['startDate','endDate']],info => {
            //     app.postMessage('task.edit')
            // },()=>{this.loadData()})
        },
        updateTaskTimeNative(obj,isManualUpdate){
            app.invoke("BizAction.updateTask", [app.token, obj, ['startDate', 'endDate'],isManualUpdate],
                info => {
                    app.postMessage('task.edit')
                },
                () => {
                    this.loadData()
                }
            );
        },
        showTaskInfo(item){
            app.showDialog(TaskDialog,{
                taskId:item.uuid
            })
        },
        setShowDate(d) {
            this.showDate = d;
        },
        clickDate(e){
            app.showDialog(TaskCreateDialog,{
                projectId:this.queryInfo.project.id,
                type:this.queryForm.objectType,
                iterationId:this.queryForm.iterationId,
                startDate:e.day.getTime()
            })
        },
        clickEvent(e){
            app.showDialog(TaskDialog,{
                taskId:e.originalEvent.uuid
            })
        },
        dropOnDate(e,time){
            this.updateTaskStartDate(e.originalEvent,time)
        },
  }
}
</script>
