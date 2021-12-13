<style scoped>
    .main-view{
        display: flex;
        position: fixed;
        top:0;
        width:100%;
        height:100vh;
        z-index:1000;
        background-color: #fff;
        flex-direction: column;
    }
    .opt-view{
        height:48px;
        border-bottom: 1px solid #ccc;
    }
    .cal-view{
        flex:1;
    }
    .right-opt{
        display: flex;
        flex-direction: row-reverse;
        align-items: center;
        padding:5px;
        height:48px;
    }
    .left-opt{
        display: flex;
        align-items: center;
        padding:5px;
        height:48px;
    }
    .splite{
        height:18px;
        width:1px;
        background-color: #eee;
        margin:0 15px;
    }
    .mr{
        margin-right:5px;
    }
</style>
<i18n>
{
    
}
</i18n>
<template>
<div class="main-view">
    <div class="opt-view">
        <Row>
            <Col span="12" class="left-opt">
                <Select v-model="calendarId" style="width:150px" class="mr">
                    <Option v-for="item in calendarList" :value="item.id" :key="item.id">{{ item.name }}</Option>
                </Select>
                
                <IconButton v-if="calendar.createAccountId==user.id" icon="md-settings" 
                            :size="16" 
                            @click="showEditCalDialog"  
                            :tips="'编辑日历'"></IconButton>

                <IconButton icon="md-add" 
                            :size="18" 
                            @click="showAddCalDialog"  
                            :tips="'创建日历'"></IconButton>

            </Col>
            <Col span="12" class="right-opt">
                <IconButton icon="md-close" 
                            :size="16" 
                            @click="closeView"  
                            :tips="'关闭'"></IconButton>
                <div class="splite"></div>
                
                <DatePicker @on-change="monthChanged" v-model="selectedMonth" type="month" placeholder="选择月份" style="width:120px"></DatePicker>
                
                <Select class="mr" @on-change="eventLayoutChanged" v-model="eventLayout"  style="width:100px; color:#999;">
                    <Option v-for="item in eventLayoutList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                </Select>

                <Select class="mr" @on-change="zoomChanged" v-model="zoomLevel"  style="width:100px;margin-right:5px">
                    <Option v-for="item in zoomList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                </Select>

            </Col>
        </Row>
    </div>
    <div class="cal-view" ref="calbox">

    </div>
</div>
</template>

<script>
import Scheduler from '../../../../assets/bryntum/Scheduler/view/Scheduler.js';
import '../../../../assets/bryntum/Grid/column/TemplateColumn.js';
import Cn from '../../../../assets/bryntum/Scheduler/localization/Cn';
import LocaleManager from '../../../../assets/bryntum/Core/localization/LocaleManager';
import DateHelper from '../../../../assets/bryntum/Core/helper/DateHelper';

import '../../../../assets/bryntum/Scheduler/feature/TimeRanges';
import '../../../../assets/bryntum/Scheduler/feature/EventResize';
import '../../../../assets/bryntum/Scheduler/feature/HeaderZoom';
import '../../../../assets/bryntum/Scheduler/feature/Labels';
import '../../../../assets/bryntum/Scheduler/feature/NonWorkingTime';
import '../../../../assets/bryntum/Scheduler/feature/Summary';
import '../../../../assets/bryntum/Scheduler/column/ResourceInfoColumn';
import '../../../../assets/bryntum/Scheduler/feature/ResourceTimeRanges';

import '../../../../assets/bryntum/Core/adapter/widget/BryntumWidgetAdapter';

//
LocaleManager.locale = Cn;
//
export default {
    mixins: [componentMixin],
    data(){
        return {
            calendarId:null,
            calendar:{},
            user:{},
            calendarList:[],
            zoomLevel:11,
            selectedMonth:null,
            zoomList:[
                {value:6,label:"月视图"},
                {value:8,label:"周视图(小)"},
                {value:10,label:"周视图(大)"},
                {value:11,label:"天视图"},
            ],
            eventLayout:'stack',
            eventLayoutList:[
                {value:'stack',label:"默认排列"},
                {value:'pack',label:"压缩排列"},
                {value:'mixed',label:"覆盖排列"},
            ],
            timeRange:{

            }
        }
    },  
    mounted(){
        this.user=app.account;
        this.$nextTick(()=>{
            this.buildView();
        })
        this.loadData();
        //
        app.onMessage('AppEvent',(event)=>{
            if(event.type=='calendar.edit'){
                this.loadCalenderList();
                this.loadCalendarInfo();
            }
            if(event.type=='calendar.delete'){
                this.calendarId=null;
                this.loadCalenderList();
            }
            if(event.type=='schedule.edit'){
                this.loadCalendarInfo();
            }
        });
    },
    computed:{
        
    },
    watch:{
        calendarId(val){
            this.loadCalendarInfo();
        }
    },
    methods:{
        closeView(){
            this.$emit('popup-close')
        },
        monthChanged(){
            this.computeTimeRange(this.selectedMonth);
            this.scheduler.startDate=this.timeRange.startDate;
            this.scheduler.endDate=this.timeRange.endDate;
        },
        computeTimeRange(date){
            var startDate=new Date(date.getTime());
            var endDate=new Date(startDate.getTime())
            endDate.setMonth(endDate.getMonth()+1);
            this.timeRange.startDate=startDate;
            this.timeRange.endDate=endDate;
        },
        zoomChanged(){
            this.scheduler.zoomLevel=this.zoomLevel;
        },
        eventLayoutChanged(){
             this.scheduler.eventLayout=this.eventLayout;
        },
        showEditCalDialog(){
             app.showDialog(CalendarEditDialog,{
                 id:this.calendarId
             })
        },
        showAddCalDialog(){
            app.showDialog(CalendarEditDialog)
            //app.showDialog(CalendarEventEditDialog)
        },
        loadData(){
            this.loadCalenderList();
        },
        loadCalendarInfo(){
            if(this.calendarId==null){
                return;
            }
            var query={
                calendarId:this.calendarId,
                startTimeStart:this.timeRange.startDate,
                startTimeEnd:this.timeRange.endDate
            }  
            app.invoke("BizAction.getCalendarScheduleList",[app.token,query],info => {
                var resources=[];
                this.calendar=info.calendar;
                info.calendar.memberInfos.forEach(e=>{
                    resources.push({
                        id:e.id,
                        name:e.name,
                        image:e.imageId,
                    })
                })
                //
                //
                var eventList=[];
                var assignments=[];
                info.list.forEach(item=>{
                    var event={
                        id:item.uuid,
                        scheduleId:item.id,
                        name:item.name,
                        eventColor:item.color,
                        startDate:new Date(item.startTime),
                        endDate:new Date(item.endTime)
                    }
                    if(item.repeat!=1){
                        event.iconCls="b-fa b-fa-sync"
                    }
                    eventList.push(event)
                    //
                    item.ownerAccountIdList.forEach(e=>{
                        assignments.push({
                            resourceId:e,
                            eventId:item.uuid
                        })
                    })
                })
                //
                //
                this.scheduler.resourceStore.data=resources;
                this.scheduler.eventStore.data=eventList;
                this.scheduler.assignments=assignments;
            });
        },
        loadCalenderList(){
            app.invoke("BizAction.getCalendarList",[app.token],list => {
                this.calendarList=list;
                if(list.length>0&&this.calendarId==null){
                    this.calendarId=list[0].id;
                }
            });
        },
        dragEvent(info){
            app.invoke("BizAction.dragCalendarSchedule",[app.token,info],obj => {
                this.loadCalendarInfo();    
            });
        },
        buildView(){
            var currentDate=new Date();
            currentDate.setDate(1);
            this.selectedMonth=currentDate;
            this.computeTimeRange(this.selectedMonth);
            var vm=this;
            //
            const scheduler = new Scheduler({
                appendTo:this.$refs.calbox,
                eventStyle : 'bordered',
                minHeight  : '20em',
                emptyText:"暂无数据",
                viewPreset : {
                    name              : 'dayAndWeek',
                    displayDateFormat : 'YYYY-mm-dd'
                },
                rowHeight : 60,
                barMargin : 5,
                snap:true,
                fillTicks  : false,
                autoAdjustTimeAxis:false,
                eventRenderer({ eventRecord }) {
                    return `           
                        <div>
                        <div style="font-size:12px">${DateHelper.format(eventRecord.startDate, 'LT')}</div>
                        <div style="font-weight:bold">${eventRecord.name}</div>
                        </div>
                    `;
                },
                features : {
                    resourceTimeRanges:true,
                    scheduleTooltip:false,
                    scheduleContextMenu:false,
                    headerContextMenu:false,
                    eventContextMenu:false,
                    stripe : true,
                    eventTooltip : false,
                    nonWorkingTime : true,
                    timeRanges : {
                        showHeaderElements  : false,
                        showCurrentTimeLine : true
                    },
                    summary : {
                         renderer({ element, events }) {
                            return `${events.length || ''}`;
                        }
                    }
                }, 
                columns : [
                    {   type : 'resourceInfo', 
                        imagePath : app.serverAddr+"/p/file/get_file/", 
                        defaultImageName:"/image/common/account-placeholder.png",
                        text : '参与人', 
                        field : 'name', 
                        width : 150,
                        showEventCount:true,
                        showRole:false
                    },
                ],
                listeners : {
                    timeaxischange(e){
                        vm.timeRange.startDate=e.config.startDate;
                        vm.timeRange.endDate=e.config.endDate;
                        vm.loadCalendarInfo();
                    },
                    eventdrop(e){
                        var info={};
                        info.startTime=e.context.startDate;
                        info.endTime=e.context.endDate;
                        info.newOwnerAccountId=e.context.newResource.data.id;
                        info.oldOwnerAccountId=e.context.resourceRecord.data.id;
                        info.id=e.context.record.data.scheduleId;
                        vm.dragEvent(info);
                    },
                    eventresizeend(e){
                        var info={}
                        info.startTime=e.eventRecord.data.startDate;
                        info.endTime=e.eventRecord.data.endDate;
                        info.id=e.eventRecord.scheduleId;
                        vm.dragEvent(info);
                    },
                    beforeeventedit(e){
                        //
                        if(e.eventRecord.scheduleId){
                            app.showDialog(CalendarEventEditDialog,{
                                id:e.eventRecord.scheduleId,
                                calendarId:vm.calendarId
                            })
                        }else{
                            var startDate=e.eventRecord.data.startDate;
                            var endDate=e.eventRecord.data.endDate;
                            var userId=e.resourceRecord.data.id;
                            app.showDialog(CalendarEventEditDialog,{
                                accountId:userId,
                                calendarId:vm.calendarId,
                                startDate:startDate,
                                endDate:endDate
                            })
                        }
                        return false;
                    }
                }
            })
            this.scheduler=scheduler;
            this.scheduler.zoomLevel=this.zoomLevel;
            this.scheduler.startDate=this.timeRange.startDate;
            this.scheduler.endDate=this.timeRange.endDate;
            scheduler.localeManager.locale = Cn;
        }
  }
}
</script>