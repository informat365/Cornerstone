
<style scoped>
    .page{
       padding:0;
    }
  
    
</style>
<template>
  <div class="page">  
     <div>
      <v-calendar :attributes="eventList" @dayclick="dayClicked" @update:frompage="pageChanged" style="width:100%"></v-calendar>
     </div>
     <div>
         <div @click="showTask(item)" class="todo-box" v-for="item in dayTaskList" :key="'t'+item.id">
                <div class="todo-item">
                    <TaskObjectType :name="item.objectTypeName"></TaskObjectType>
                    <div class="serial-no">#{{item.serialNo}}</div>
                    <TaskStatus style="margin-left:10px" :label="item.statusName" :color="item.statusColor"></TaskStatus>
                    <div class="owners">
                         <template v-if="item.ownerAccountList">
                                   <template v-if="item.ownerAccountList.length==0">
                                        待认领
                                        </template>
                                        <template v-if="item.ownerAccountList.length==1">
                                        <AvatarImage  size="small" :name="item.ownerAccountList[0].name" :value="item.ownerAccountList[0].imageId" type="label"></AvatarImage>
                                    </template>
                                    
                                    <template v-if="item.ownerAccountList.length>1">
                                        <AvatarImage  v-for="acc in item.ownerAccountList" :key="item.id+'_acc'+acc.id" size="small" :name="acc.name" :value="acc.imageId" type="tips"></AvatarImage>
                                    </template>
                            </template>
                    </div>
                    
                </div>
                <div class="todo-item" style="margin-top:5px">
                    <div class="task-name">{{item.name}}</div>
                </div>
            </div>
     </div>
  </div>
</template>

<script>
import Vue from 'vue';
import VCalendar from 'v-calendar';
import 'v-calendar/lib/v-calendar.min.css';
import { setupCalendar, Calendar} from 'v-calendar'
setupCalendar({
  firstDayOfWeek: 2,  // Monday,
  locale:"zh-CN"
});
Vue.component('v-calendar', Calendar);
export default {
    mixins:[componentMixin],
    data () {
        return {
            currentDate:new Date(),
            eventList:[],
            dayTaskList:[],
            taskList:[],
        }
    },
    methods:{
        pageLoad(){
            this.loadData();
        },
        loadData(){
            var query={
                monthDate:this.currentDate
            }
            var dayMap={};
            var reminderMap={};
            app.invoke("BizAction.getMyCalendarInfo",[app.token,query],info => { 
                this.taskList=info.taskList;
                for(var i=0;i<info.taskList.length;i++){
                    var task=info.taskList[i];
                    if(task.startDate!=null){
                        dayMap[task.startDate]=task.startDate;
                    }
                    if(task.endDate!=null){
                        dayMap[task.endDate]=task.endDate;
                    }
                }
                 this.eventList=[];
                 for(var k in dayMap){
                     var d=dayMap[k];
                     this.eventList.push({
                         key:'event-'+d,
                         highlight: {
                            backgroundColor: '#ff8080',
                        },
                        contentStyle: {
                            color: '#fafafa',
                        },
                         dates: new Date(d)
                     })
                 }
                 console.log(this.eventList)
            });
           
        },
        pageChanged(e){
            var date=new Date();
            date.setFullYear(e.year);
            date.setMonth(e.month);
            //
            this.currentDate=date;
        },
        dayClicked(e){
            var dateTime=e.dateTime;
            this.dayTaskList=[];
            for(var i=0;i<this.taskList.length;i++){
                var t=this.taskList[i];
                if(t.startDate==dateTime||t.endDate==dateTime){
                    this.dayTaskList.push(t);
                }
            }
        },
        showTask(item){
            app.loadPage('/m/task?id='+item.uuid)
        }
    }
}
</script>

