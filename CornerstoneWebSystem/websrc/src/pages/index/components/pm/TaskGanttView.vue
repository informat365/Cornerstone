<style scoped>
.page-content-top{
    width:100%;
    display: inline-block;
    padding:10px;
    padding-top:5px;
    padding-bottom:5px;
    text-align: left;
    font-weight: bold;
}
.location-total-label{
    font-size:12px;
    color:#999;
    line-height: 20px;
    text-align: right;
}
.selected-btn{
    color:#0097F7 !important;
}
</style>
<i18n>
{
    "en": {
    },
    "zh_CN": {
        "总数": "总数{0}，已完成{1}，未完成{2}",
        "天视图":"天视图",
        "周视图":"周视图",
        "月视图":"月视图",
        "未设置时间":"未设置时间",
        "天":"天"
    }
}
</i18n>
<template>
<div >
             <Row class="page-content-top">
               <Col span="18">
                <div style=";padding-left:10px;color:#999;padding-top:3px;font-size:12px">
                    {{$t('总数',[allCount,finishCount,allCount-finishCount])}}
                </div>
               </Col>
                <Col span="6" class="location-total-label">
                    <IconButton :class="{'selected-btn':viewType=='Day'}" @click="viewType='Day'" :title="$t('天视图')"></IconButton>
                    <IconButton :class="{'selected-btn':viewType=='Week'}" @click="viewType='Week'" :title="$t('周视图')"></IconButton>
                    <IconButton :class="{'selected-btn':viewType=='Month'}"  @click="viewType='Month'" :title="$t('月视图')"></IconButton>
                </Col>
            </Row>
    <div class="gantt-target tasklist"></div>

 </div>
</template>

<script>
export default {
    mixins: [componentMixin],
    props:['queryInfo','queryForm'],
    data(){
        return {
            title:"TaskGanttView",
            tableData:[],
            viewType:"Week",
            finishCount:0,
            allCount:0,
        }
    },
    watch:{
        viewType(val){
            if(this.ganttChart){
                this.ganttChart.change_view_mode(val)
            }
        }
    },
    methods:{
        pageMessage(type){
            if(type=='task.edit'){
                this.reloadData();
            }
        },
        loadData(){
            this.queryForm.pageIndex=1;
            this.queryForm.pageSize=1000;
            app.invoke( "BizAction.getTaskInfoList",[app.token,this.queryForm],info => {
                this.tableData=this.sortTask(info.list);
                this.buildView();
            });
        },
        reloadData(){
            this.queryForm.pageIndex=1;
            this.queryForm.pageSize=1000;
            app.invoke( "BizAction.getTaskInfoList",[app.token,this.queryForm],info => {
                this.tableData=this.sortTask(info.list);
                this.refreshView();
            });
        },
        sortTask(list){
            this.allCount=0;
            this.finishCount=0;
            var allTaskMap={};
            for(var i=0;i<list.length;i++){
                var t=list[i];
                this.allCount++;
                if(t.isFinish){
                    this.finishCount++;
                }
                if(t.parentId==0){
                    allTaskMap[t.id]={
                        task:t,
                        children:[],
                    };
                }
            }
            //
            for(var i=0;i<list.length;i++){
                var t=list[i];
                if(t.parentId>0){
                    if(allTaskMap[t.parentId]){
                       allTaskMap[t.parentId].children.push(t);
                    }else{
                        //父任务不存在
                         allTaskMap[t.id]={
                            task:t,
                            children:[],
                        };
                    }
                }
            }
            var finalList=[];
            //
            for(var k in allTaskMap){
                var t=allTaskMap[k];
                finalList.push(t.task);
                for(var i=0;i<t.children.length;i++){
                    finalList.push(t.children[i])
                }
            }
            return finalList;
        },
        buildView(){
            var tasks=this.getTasks();
            this.$el.getElementsByClassName('gantt-target')[0].innerHTML="";
            if(tasks.length>0){
                this.ganttChart= new Gantt(".gantt-target", tasks, {
                    on_click:(task)=>{
                        this.showTaskInfo(task.task)
                    },
                    on_date_change: (task, start, end)=>{
                        this.updateTaskStartDate(task.task,start,end);
                    }
                });
                this.ganttChart.change_view_mode('Week');
            }
        },
        refreshView(){
            this.ganttChart.remeberScrollPos();
            this.ganttChart.refresh(this.getTasks());
            this.ganttChart.resetScrollPos();
        },
        getTasks(){
            var tasks=[];
            for(var i=0;i<this.tableData.length;i++){
                var item=this.tableData[i];
                var progress=0;
                if(item.expectWorkTime>0){
                    progress=parseInt(item.workTime*100/item.expectWorkTime)
                }
                if(progress>100){
                    progress=100;
                }
                var startDate;
                if(item.startDate==null){
                    startDate=new Date();
                }else{
                    startDate=new Date(item.startDate);
                }
                var endDate;
                if(item.endDate==null){
                    endDate=new Date();
                }else{
                    endDate=new Date(item.endDate);
                }
                if(startDate.getTime()==endDate.getTime()){
                    endDate=new Date(startDate.getTime()+3600*23.9*1000);
                }
                var dependencies=null;
                if(item.parentId&&item.parentId>0){
                    dependencies="task"+item.parentId;
                }
                var dayDiff=(this.dateDiff(startDate,endDate)+1)+"天";
                if(item.startDate==null||item.endDate==null){
                    dayDiff="未设置时间";
                }
                //
                var name="【"+item.statusName+"】"+"【"+dayDiff+"】"+"#"+item.serialNo+" "+item.name;
                if(progress>0){
                    name="【"+item.statusName+"】"+"【"+dayDiff+"】"+"【"+item.workTime+"/"+item.expectWorkTime+"h】"+"#"+item.serialNo+" "+item.name;
                }

                var task= {
                    start: startDate,
                    end: endDate,
                    name: name,
                    id: "task" + item.id,
                    progress: progress,
                    task:item,
                    color:item.statusColor,
                    finish:item.isFinish,
                }
                if(dependencies){
                    task.dependencies=dependencies;
                }
                var today=new Date();
                if(today.getTime()>endDate.getTime()&&!item.isFinish){
                    task.custom_class = "bar-overdue";
                }
                tasks.push(task)
            }
            return tasks;
        },
        dateDiff:function(dt1,dt2){
            return Math.floor((Date.UTC(dt2.getFullYear(), dt2.getMonth(), dt2.getDate())
                     - Date.UTC(dt1.getFullYear(), dt1.getMonth(), dt1.getDate()) ) /(1000 * 60 * 60 * 24));
        },
        updateTaskStartDate(task,startDate,endDate){
            var obj={}
            obj.id=task.id;
            obj.startDate=startDate;
            obj.endDate=endDate;
            task.startDate=startDate;
            task.endDate=endDate;
            this.refreshView();
            app.invoke('BizAction.getTaskAssociateStat', [app.token, task.id], info => {
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
            // app.invoke( "BizAction.updateTask",[app.token,obj,['startDate','endDate']],
            //     info => {},
            //     ()=>{this.loadData()}
            // );
        },
        updateTaskTimeNative(obj,isManualUpdate){
            app.invoke("BizAction.updateTask", [app.token, obj, ['startDate', 'endDate'],isManualUpdate],
                info => {
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
        }
  }
}
</script>
