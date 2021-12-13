<style scoped>
    .calendar-view{

        display: flex;
        flex-direction: column;
        flex-grow: 1;
        flex:1;
    }
    .calendar-close{
        position: fixed;
        top:9px;
        right:0;
        width:200px;
        height: 50px;
        text-align: right;
        z-index:1001;
        padding-right:10px;
    }
    .calendar-option{
        position: fixed;
        top:17px;
        left:500px;
        width:400px;
        height: 50px;
        text-align: left;
        z-index:1001;
        color:#666;
    }
    .header{
      padding:10px;
    }
    .main-view{
        display: flex;
        position: fixed;
        top:0;
        width:100%;
        height:100vh;
        z-index:1000;
        background-color: #fff;
    }
    .side-view{
        width:250px;
        padding:15px;
    }

     .menu-item-group{
        font-size:12px;
        color:#999;
        padding:8px 0px;
    }
    .filter-tag{
        font-size:13px;
        padding:3px 10px;
        background-color: #EEEEEE;
        color:#777;
        display: inline-block;
        margin-right: 10px;
        margin-top:10px;
        border-radius: 13px;
        cursor: pointer;
        user-select: none;
        max-width: 200px;
        text-overflow:ellipsis;
        overflow: hidden;
        white-space: nowrap;
    }
    .filter-count{
        font-size:12px;
        margin-left: 5px;
        font-weight: bold;
    }
    .filter-tag-select{
        background-color: #009CF1;
        color:#fff;
    }

</style>
<i18n>
{
    "en": {
        "提醒": "Reminder",
        "订阅":"Subscribe"
    },
    "zh_CN": {
        "提醒": "提醒",
        "订阅":"订阅"
    }
}
</i18n>
<template>
<div class="main-view">

    <div class="side-view">
        <div class="menu-item-group">类型过滤</div>

        <div v-for="item in typeList" :key="'t'+item.value"
            :class="{'filter-tag-select':hasFilter(typeFilters,item.value)}"
            @click="toggleFilter(typeFilters,item.value)"
            class="filter-tag">{{item.value|dataDict('Task.objectType')}}</div>


        <div class="menu-item-group" style="margin-top:20px">项目过滤</div>

        <div v-for="item in projectList" :key="'p'+item.value"
            :class="{'filter-tag-select':hasFilter(projectFilters,item.value)}"
            @click="toggleFilter(projectFilters,item.value)"
            class="filter-tag">{{item.name}}</div>



    </div>
    <div class="calendar-view">
      <calendar-view ref="calendar"
	    :show-date="showDate"
        :starting-day-of-week="1"
		@show-date-change="setShowDate"
        :events="filteredEventList"
        @click-event="clickEvent"
        @click-date="clickDate"
        @drop-on-date="dropOnDate"
        :enableDragDrop="true"
		class="theme-default">
      </calendar-view>
    </div>
    <div class="calendar-close">
            <IconButton icon="md-star" @click="subscribeCal" :title="$t('订阅')" style="margin-right:20px"></IconButton>
            <IconButton icon="md-close" @click="closeView"></IconButton>
      </div>
</div>
</template>

<script>
//
export default {
    mixins: [componentMixin],
    data(){
        return {
            showDate: new Date(),
            eventList:[],
            clickDay:null,
            typeList:[],
            projectList:[],
            showRemind:true,
            typeFilters:[],
            projectFilters:[]
        }
    },
    mounted(){
        this.loadData();
    },
    computed:{
        filteredEventList(){
            var list=[];
            for(var i=0;i<this.eventList.length;i++){
                var t=this.eventList[i];
                if(t.taskType&&!this.hasFilter(this.typeFilters,t.taskType)){
                    continue;
                }

                if(t.projectId!=null&&!this.hasFilter(this.projectFilters,t.projectId)){
                    continue;
                }
                list.push(t);
            }
            return list;
        }
    },
    methods:{
        closeView(){
            this.$emit('popup-close')
        },
        hasFilter(which,id){
            for(var i=0;i<which.length;i++){
                if(which[i]==id){
                    return true;
                }
            }
            return false;
        },
        toggleFilter(which,id){
            for(var i=0;i<which.length;i++){
                if(which[i]==id){
                    which.splice(i,1);
                    return;
                }
            }
            which.push(id);
        },

        pageMessage(type){
            if(type=='remind.edit'||type=='task.edit'){
                this.loadData();
            }
        },
        subscribeCal(){
            app.showDialog(ProjectCalendarSubscribeDialog)
        },
        loadData(){
            var query={
                monthDate:this.showDate
            }
            app.invoke("BizAction.getMyCalendarInfo",[app.token,query],info => {
                this.eventList=[];
                var typeMap={};
                var projectMap={};
                for(var i=0;i<info.taskList.length;i++){
                    var task=info.taskList[i];
                    var event={
                        id:task.id,
                        title:"【"+task.statusName+"】"+task.name,
                        type:task.objectTypeName+"#"+task.serialNo,
                        uuid:task.uuid,
                        taskId:task.id,
                        taskType:task.objectType,
                        projectId:task.projectId,
                        priorityColor:task.priorityColor,
                        classes:'calendar-event-'+((task.id%8)+1)
                    }
                    typeMap[task.objectType]=true
                    projectMap[task.projectId]=task.projectName;
                    if(task.startDate!=null){
                        event.startDate=new Date(task.startDate);
                    }
                    if(task.endDate!=null){
                        event.endDate=new Date(task.endDate);
                    }
                    if(event.startDate!=null&&event.endDate==null){
                        event.endDate=event.startDate;
                    }
                    if(event.startDate==null&&event.endDate!=null){
                        event.startDate=event.endDate;
                    }
                    //
                    this.eventList.push(event);
                }
                this.typeList=[];
                for(var k in typeMap){
                    this.typeList.push({
                        value:k
                    });
                    this.typeFilters.push(k);
                }
                //
                this.projectList=[];
                for(var k in projectMap){
                    this.projectList.push({
                        value:k,
                        name:projectMap[k]
                    });
                    this.projectFilters.push(k);
                }
                //
                for(var i=0;i<info.remindList.length;i++){
                    var remind=info.remindList[i];
                    var event={
                        id:remind.id+"_"+i,
                        title:remind.name,
                        type:"reminder",
                        remindId:remind.id,
                        startDate:remind.remindTime,
                        endDate:remind.remindTime,
                    }
                    this.eventList.push(event);
                }

            });
        },
        setShowDate(d) {
            this.showDate = d;
        },
        clickEvent(e){
            if(e.originalEvent.type=='reminder'){
                app.showDialog(ReminderCreateDialog,{
                    id:e.originalEvent.remindId
                })
            }else{
                app.showDialog(TaskDialog,{
                    taskId:e.originalEvent.uuid
                })
            }
        },
        dropOnDate(e,time){
            if(e.originalEvent.remindId!=null){
                this.updateRemindDate(e.originalEvent.remindId,time);
            }
            if(e.originalEvent.taskId!=null){
                this.updateTaskStartDate(e.originalEvent,time)
            }
        },
        updateRemindDate(remindId,time){
            app.invoke("BizAction.updateRemindTime",[app.token,remindId,time],info => {
                app.postMessage('remind.edit')
            })
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
        clickDate(e){
            this.clickDay=e.day;
            app.showDialog(ReminderCreateDialog,{
                remindTime:this.clickDay,
            })
        }
  }
}
</script>
