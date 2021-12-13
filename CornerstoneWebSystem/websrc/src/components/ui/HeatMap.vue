<style scoped>
    .heatmap-box{
        overflow: auto;
    }
    .daybox{
        display: inline-block;
        width:48px;
        margin-right:5px;
    }
    .day-label{
        text-align: center;
        font-size:12px;
        color:#999;
    }
    .hourbox{
        width:12px;
        height: 12px;
        border:1px solid #fff;
        background-color: #BCD7EA;
        display: inline-block;
        float: left;
    }
    .hourbox-1{
        background-color: #DDE09D;
    }
    .hourbox-2{
        background-color: #7A9959;
    }
    .hourbox-3{
        background-color: #7A9959;
    }
    .hourbox-4{
        background-color: #6A7748;
    }
    .hourbox-5{
        background-color: #4A6135;
    }
    .tooltip-box{
        width:12px;
        height: 12px;
    }
</style>
<template>
   <div class="heatmap-box">
       <div class="daybox" v-for="day in dayList" :key="day.date">
           <div>
               <div class="hourbox" :class="'hourbox-'+hour.count" v-for="hour in day.hours" :key="hour.hour">
                  
                    <Tooltip>
                        <div class="tooltip-box">&nbsp;</div>
                        <div slot="content">
                            <div v-for="e in hour.events" :key="e.id">
                                {{e.createTime|fmtDateTime}} {{e.createAccountName}} {{getEventType(e.type)}}
                            </div>
                            <div v-if="hour.events.length==0">无活动</div>
                        </div>
                        
                    </Tooltip>
               </div>
           </div>
           <div class="day-label">
               {{day.date|fmtMonth}}
           </div>
       </div>
   </div>
</template>
<script>
export default {
    name:"HeatMap",
    props: ['eventList','free'],
    data (){
        return{
           dayList:[],
        }
    },
    mounted(){
        this.buildView();
    },
    watch:{
        eventList(val){
            this.buildView();
        }
    },
    methods:{
        buildView(){
            if(this.free){
                this.setupFreeTime(this.eventList);
            }else{
                this.setupFixedTime();
            }
            for(var i=0;i<this.eventList.length;i++){
                var t=this.eventList[i];
                var dayItem=this.getDayItem(t.createTime);
               
                if(dayItem){
                    var tdate=new Date(t.createTime);
                    var hour=tdate.getHours();
                    dayItem.hours[hour].count++;
                    if(dayItem.hours[hour].count>5){
                        dayItem.hours[hour].count=5;
                    }
                    dayItem.hours[hour].events.push(t);
                }
            }
        },
        getDayItem(date){
            var d=new Date(date);
            for(var i=0;i<this.dayList.length;i++){
                var t=this.dayList[i];
                var td=new Date(t.date);
                if(d.getDate()==td.getDate()){
                    return t;
                }
            }
            return null;
        },
       
        getEventType(type){
            var types={
                "1":"创建项目",
                "2":"编辑对象属性",
                "3":"上传附件",
                "4":"删除附件",
                "5":"新增关联对象",
                "6":"删除关联对象",
                "7":"新增子任务",

                "10":"新增项目成员",
                "11":"删除项目成员",
                "14":"新增迭代",
                "15":"删除迭代",
                "12":"创建对象",
                "13":"删除对象",
                "16":"新增Release",
                "17":"删除Release",
                "18":"新增子系统",
                "19":"删除子系统",
                "20":"提交代码",
                "201":"从回收站恢复",
                "110":"从回收站恢复",
                "120":"创建知识库页面",
                "121":"删除知识库页面",
                "122":"从回收站恢复知识库页面",
                "131":"创建主机",
                "132":"删除主机",
                "133":"从回收站恢复主机",
                "141":"归档了项目",
                "142":"删除了项目",
                "143":"重新打开了项目",
                "150":"登录主机",
                "160":"执行pipeline",
            }
            return types[type]
        },
        setupFixedTime(){
            this.dayList=[];
            for(var i=0;i<15;i++){
                var now=new Date();
                now.setDate(now.getDate()-i);
                var day={
                    date:now.getTime(),
                    hours:[],
                }
                for(var j=0;j<24;j++){
                    day.hours.push({
                        hour:j,
                        count:0,
                        events:[]
                    })
                }
                this.dayList.push(day)
            }
            this.dayList=this.dayList.reverse();
        },
        setupFreeTime(eventList){
            this.dayList=[];
            var minDate=Number.MAX_VALUE;
            var maxDate=0;
            eventList.forEach(element => {
                if(element.createTime<minDate){
                    minDate=element.createTime;
                }
                if(element.createTime>maxDate){
                    maxDate=element.createTime;
                }
            });
            //
            for(var i=0;i<30;i++){
                var now=new Date(maxDate);
                now.setDate(now.getDate()-i);
                
                var day={
                    date:now.getTime(),
                    hours:[],
                }
                for(var j=0;j<24;j++){
                    day.hours.push({
                        hour:j,
                        count:0,
                        events:[]
                    })
                }
                this.dayList.push(day)
                if(now.getTime()<minDate){
                    break;
                }
            }
        }
    }
}
</script>