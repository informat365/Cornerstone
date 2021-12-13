<style scoped>
.page-content-top{
    width:100%;
    display: inline-block;
    padding:10px;
    padding-top:7px;
    padding-bottom:0px;
    text-align: left;
    font-weight: bold;
    color:#999;
    font-size:12px;
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
        border:1px solid #ccc;
        border-right:none;
        position: relative;
}
.right-opt{
    padding:0 5px;
    display: flex;
    flex-direction: row-reverse;
    align-items: center;
    font-weight: normal;
}
.opt-label{
    color:#999;
    font-size:12px;
    line-height: 12px;
    display: inline-block;
    vertical-align: middle;
    margin-left: 5px;
}
.opt-divider{
    width: 1px;
    display: inline-block;
    background: #A7A7A7;
    height: 10px;
    line-height: 10px;
    margin-left: 10px;
    margin-right: 5px;
    vertical-align: middle;
}

</style>
<i18n>
{

}
</i18n>
<template>
<div >
    <Row class="page-content-top">
        <Col span="6">
            <div style="display:flex;align-items:center">
                <span>总计{{tableData.length}}条数据</span>
            </div>
        </Col>
        <Col span="18" class="right-opt">
            <Select @on-change="eventLayoutChanged" v-model="eventLayout" size="small" style="width:100px; color:#999;">
                <Option v-for="item in eventLayoutList" :value="item.value" :key="item.value">{{ item.label }}</Option>
            </Select>

            <Select @on-change="zoomChanged" v-model="zoomLevel" size="small" style="width:100px;margin-right:5px">
                <Option v-for="item in zoomList" :value="item.value" :key="item.value">{{ item.label }}</Option>
            </Select>


             <i-Switch style="margin-right:5px" @on-change="toggleTimeRanges" v-model="showTimeRanges" size="small"/>
             <span class="opt-label">显示迭代周期：</span>

        </Col>
    </Row>
     <div ref="viewbox" class="calendar-view">


    </div>
 </div>
</template>

<script>

import Scheduler from '../../../../assets/bryntum/Scheduler/view/Scheduler.js';
import '../../../../assets/bryntum/Grid/column/TemplateColumn.js';
import Cn from '../../../../assets/bryntum/Scheduler/localization/Cn';
import LocaleManager from '../../../../assets/bryntum/Core/localization/LocaleManager';

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

//
export default {
    mixins: [componentMixin],
    props:['queryInfo','queryForm'],
    data(){
        return {
            title:"TaskSchedulerView",
            tableData:[],

            showTimeRanges:false,
            zoomLevel:10,
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
            taskRange:{}
        }
    },
    mounted(){
        this.$nextTick(()=>{
            this.buildView();
            this.getProjectInfo();
        })
    },
    beforeDestroy(){
        this.scheduler.destroy();
    },
    methods:{
        pageMessage(type){
            if(type=='task.edit'){
                this.loadData();
            }
        },
        exportPdf(){
            //this.scheduler.features.pdfExport.showExportDialog();
        },
        toggleTimeRanges(){
            this.scheduler.features.timeRanges.showHeaderElements=this.showTimeRanges;
        },
        zoomChanged(){
            this.scheduler.zoomLevel=this.zoomLevel;
            this.scheduler.startDate=this.taskRange.startDate;
            this.scheduler.endDate=this.taskRange.endDate;
        },
        eventLayoutChanged(){
             this.scheduler.eventLayout=this.eventLayout;
        },
        buildView(){
            var vm=this;

            const scheduler = new Scheduler({
                appendTo:this.$refs.viewbox,
                eventStyle : 'border',
                minHeight  : '20em',
                emptyText:"暂无数据",
                viewPreset : {
                    name              : 'dayAndWeek',
                    displayDateFormat : 'YYYY-mm-dd',
                    timeResolution    : {
                        unit      : 'day',
                        increment : 1
                    }
                },
                timeResolution :{
                    unit      : 'day',
                    increment : 1
                },
                eventBodyTemplate : data => `<div class="value"><div class="progress" style="width: ${data.progress}%"></div>${data.name}</div>`,
                rowHeight : 50,
                barMargin : 10,
                snap:true,
                fillTicks  : true,
                autoAdjustTimeAxis:false,
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
                    },
                    /*labels : {
                        left : {
                            renderer : ({ eventRecord }) => eventRecord.statusName + ' '
                        }
                    }*/
                },
                columns : [
                    {   type : 'resourceInfo',
                        imagePath : app.serverAddr+"/p/file/get_file/",
                        defaultImageName:"/image/common/account-placeholder.png",
                        text : '成员',
                        field : 'name',
                        width : 150,
                        showEventCount:true,
                        showRole:false
                    },
                ],
                listeners : {
                    eventdrop(e){
                        var startDate=e.context.startDate;
                        var endDate=e.context.endDate;
                        var newOwnerId=e.context.newResource.data.id;
                        var oldOwnerId=e.context.resourceRecord.data.id;
                        //
                        var idList=e.context.record.data.ownerAccountIdList;
                        for(var i=0;i<idList.length;i++){
                            var t=idList[i];
                            if(t==oldOwnerId){
                                idList.splice(i,1);
                                break;
                            }
                        }
                        //
                        if(newOwnerId!=-1){
                            idList.push(newOwnerId);
                        }
                        //
                        var id=e.context.record.data.id;
                        //
                        vm.updateTaskDrop(id,idList,startDate,endDate);
                    },
                    eventresizeend(e){
                        var startDate=e.eventRecord.data.startDate;
                        var endDate=e.eventRecord.data.endDate;
                        var id=e.eventRecord.id;
                        vm.updateTask(id,startDate,endDate);
                    },
                    beforeeventedit(e){
                        //
                        if(e.eventRecord.uuid){
                            app.showDialog(TaskDialog,{
                                taskId:e.eventRecord.uuid
                            })
                        }else{
                            var startDate=e.eventRecord.data.startDate;
                            var endDate=e.eventRecord.data.endDate;
                            var userId=e.resourceRecord.data.id;
                            //
                            app.showDialog(TaskCreateDialog,{
                                projectId:vm.queryInfo.project.id,
                                type:vm.queryForm.objectType,
                                iterationId:vm.queryForm.iterationId==null?0:vm.queryForm.iterationId,
                                startDate:startDate.getTime(),
                                endDate:endDate.getTime(),
                                ownerAccountIdList:[userId]
                            })
                        }
                        return false;
                    }
                }
            })

            this.scheduler=scheduler;
            scheduler.localeManager.locale = Cn;
            this.scheduler.zoomLevel=this.zoomLevel;
        },
        loadIterationInfo(id){
            app.invoke('BizAction.getProjectIterationInfoById',[app.token,id],(info)=>{
                var milstoneList=[];
                for(var i=0;i<info.stepList.length;i++){
                    var t=info.stepList[i];
                    var milstone= {
                        startDate:new Date(t.startDate),
                        endDate:new Date(t.endDate),
                        name: t.name,
                    }
                    milstoneList.push(milstone);
                }
                //
                this.scheduler.timeRanges=milstoneList;
            });
        },
        loadData(){
            this.queryForm.pageIndex=1;
            this.queryForm.pageSize=1000;
            app.invoke("BizAction.getTaskInfoList",[app.token,this.queryForm],info => {
                this.tableData=info.list;
                this.getTasks();
            });
            //
            if(this.queryForm.iterationId){
                this.loadIterationInfo(this.queryForm.iterationId)
            }else{
                this.scheduler.timeRanges=[];
            }
        },
        getProjectInfo(){
            app.invoke("BizAction.getProjectInfoByUuid",[app.token, app.projectUUID],info => {
                var memberList=info.memberList;
                var resourceList=[];
                memberList.forEach(e => {
                    var roles=[];
                    e.roleList.forEach(c=>{roles.push(c.name)})
                    resourceList.push({
                        id:e.accountId,
                        name:e.accountName,
                        image:e.accountImageId,
                        role:roles.join(",")
                    });
                });
                resourceList.push({
                        id:-1,
                        name:'未分配',
                        role:''
                })
                this.scheduler.resourceStore.data=resourceList;
            });
        },
        getTasks(){
            var eventList=[];
            var assignments=[];
            var dependencies=[];
            var startTime=Number.MAX_VALUE;
            var endTime=0;
            for(var i=0;i<this.tableData.length;i++){
                    var task=this.tableData[i];
                    var event={
                        id:task.id,
                        name:task.name,
                        uuid:task.uuid,
                        progress:task.progress,
                        statusName:task.statusName,
                        eventColor:task.statusColor,
                        ownerAccountIdList:task.ownerAccountIdList
                    }

                    if(task.parentId>0){
                        dependencies.push({
                            id:dependencies.length+1,
                            from:task.parentId,
                            to:task.id
                        })
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
                    if(event.startDate.getTime()==event.endDate.getTime()){
                        event.endDate=new Date(event.startDate.getTime()+3600*24*1000)
                    }
                    //
                    if(event.startDate.getTime()<startTime){
                        startTime=event.startDate.getTime();
                    }
                    if(event.endDate.getTime()>endTime){
                        endTime=event.endDate.getTime();
                    }
                    var now=new Date().getTime();
                    if(task.isFinish==false&&event.endDate.getTime()<now){
                        event.eventColor='red';
                        event.eventStyle='plain';
                        event.iconCls="b-fa b-fa-exclamation-triangle"
                    }
                    if(task.isFinish){
                        event.cls="b-event-finish"
                        event.iconCls='b-fa b-fa-check'
                    }
                    //
                    eventList.push(event);
                    //
                    if(Array.isArray(task.ownerAccountIdList)&&task.ownerAccountIdList.length>0){
                        task.ownerAccountIdList.forEach(e=>{
                            assignments.push({
                                resourceId:e,
                                eventId:task.id
                            })
                        })
                    }

                    if(!task.ownerAccountIdList||task.ownerAccountIdList.length===0){
                         assignments.push({
                            resourceId:-1,
                            eventId:task.id
                        })
                    }
            }
            //
            if(this.scheduler.startDate.getTime()>startTime){
                this.scheduler.startDate=new Date(startTime);
            }
            if(this.scheduler.endDate.getTime()<endTime){
                this.scheduler.endDate=new Date(endTime);
            }
            //
            this.taskRange={
                startDate:this.scheduler.startDate,
                endDate:this.scheduler.endDate
            }
            var resourceTimeRanges=[
                {id:1,
                resourceId:-1,
                startDate:this.scheduler.startDate,
                endDate:this.scheduler.endDate,
                cls : 'custom'}
            ]
            //
            this.scheduler.eventStore.data=eventList;
            this.scheduler.assignments=assignments;
            this.scheduler.resourceTimeRanges=resourceTimeRanges;
            //this.scheduler.dependencies=dependencies;
            //
        },
        dateDiff:function(dt1,dt2){
            return Math.floor((Date.UTC(dt2.getFullYear(), dt2.getMonth(), dt2.getDate())
                     - Date.UTC(dt1.getFullYear(), dt1.getMonth(), dt1.getDate()) ) /(1000 * 60 * 60 * 24));
        },
        updateTaskDrop(id,ownerIdList,startDate,endDate){
            var obj={}
            obj.id=id;
            obj.startDate=startDate;
            obj.endDate=endDate;
            obj.ownerAccountIdList=ownerIdList
            if(ownerIdList.length==1&&ownerIdList[0]==-1){
                obj.ownerAccountIdList=[]
            }
            app.invoke('BizAction.getTaskAssociateStat', [app.token, id], info => {
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
            // app.invoke("BizAction.updateTask",[app.token,obj,['startDate','endDate','ownerAccountIdList']],info => {
            //     app.postMessage('task.edit')
            // },()=>{this.loadData()})
        },
        updateTaskTimeNative(obj,isManualUpdate){
            app.invoke("BizAction.updateTask", [app.token, obj, ['startDate', 'endDate','ownerAccountIdList'],isManualUpdate],
                info => {
                    app.postMessage('task.edit')
                },
                () => {
                    this.loadData()
                }
            );
        },
        updateTask(id,startDate,endDate){
            var obj={}
            obj.id=id;
            obj.startDate=startDate;
            obj.endDate=endDate;
            app.invoke('BizAction.getTaskAssociateStat', [app.token, id], info => {
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
            app.invoke('BizAction.getTaskAssociateStat', [app.token, id], info => {
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
  }
}
</script>
