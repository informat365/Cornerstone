<style scoped>
    .reminder-box{
        height:400px;
        padding:15px;
        text-align: left;
    }
    .rule-item{

    }
    .rule-item-span{
        padding:5px;
        align-items: center;
        justify-items: center;
        display: inline-flex;
        height: 100%;
    }
</style>
<i18n>
{
    "en": {
        "提醒设置": "Reminder Setting",
        "提醒规则":"Rule",
        "新增规则":"Add",
        "暂无规则":"No Data",
        "未设置":"none",
        "提醒对象":"Reminder user",
        "开始时间前":"Before start",
        "开始时间后":"After start",
        "截止时间前":"Before end",
        "截止时间后":"After end",
        "指定时间":"Appointed time",
        "分钟":"Minute",
        "小时":"Hour",
        "天":"Day",
        "保存":"Save",
        "备注信息":"Remark"
    },
    "zh_CN": {
        "提醒设置": "提醒设置",
        "提醒规则":"提醒规则",
        "新增规则":"新增规则",
        "暂无规则":"暂无规则",
        "未设置":"未设置",
        "提醒对象":"提醒对象",
        "开始时间前":"开始时间前",
        "开始时间后":"开始时间后",
        "截止时间前":"截止时间前",
        "截止时间后":"截止时间后",
        "指定时间":"指定时间",
        "分钟":"分钟",
        "小时":"小时",
        "天":"天",
        "保存":"保存",
        "备注信息":"备注信息"
    }
}
</i18n>
<template>
   <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('提醒设置')" width="500"  @on-ok="confirm">

<div class="reminder-box">
    <div  class="info-section-row">
         <div class="info-section-header">{{$t('提醒规则')}} </div>
        <IconButton v-if="!projectFinishDisabled" @click="addRule" icon="ios-add"  :title="$t('新增规则')"></IconButton>
    </div>

    <div style="padding:10px 0">
        <Row v-for="item in reminderRules" :key="item.id" class="rule-item" align="middle" type="flex">
            <Col span="10" class="rule-item-span">
                <Select :disabled="projectFinishDisabled" @on-change="ruleTypeChaged(item)" v-model="item.type" transfer >
                    <Option  :key="'t'+item.value" v-for="item in ruleTypes" :value="item.value">{{item.name}}</Option>
                </Select>
            </Col>
            <Col span="12" class="rule-item-span">
                <template v-if="item.type!=5">
                    <Select  v-model="item.value" transfer>
                        <Option  :key="'v_'+item" v-for="item in unitValue" :value="item">{{item}}</Option>
                    </Select>
                    <Select style="margin-left:5px" v-model="item.unit" transfer>
                        <Option  :key="item.value" v-for="item in unitArray" :value="item.value">{{item.name}}</Option>
                    </Select>
                </template>
                <ExDatePicker style="width:100%" v-if="item.type==5" type="datetime" v-model="item.value"></ExDatePicker>
            </Col>
            <Col span="2"  class="rule-item-span">
                <IconButton  :disabled="projectFinishDisabled" @click="removeRule(item)" icon="ios-trash"></IconButton>
            </Col>
        </Row>
        <div v-if="reminderRules.length==0" class="table-nodata">{{$t('暂无规则')}}</div>
    </div>

    <div  class="info-section-row">
         <div class="info-section-header">{{$t('提醒对象')}}</div>
    </div>

   <div style="padding:15px 0" v-if="editInfo">
        <MemberSelect   :placeholder="$t('未设置')"
                        :multiple="true"
                        :member-list="editInfo.memberList"
                        :value-list="reminderMemberInfos"
                        v-model="reminderMembers" placement="bottom-start"></MemberSelect>
   </div>
    <div  class="info-section-row">
        <div class="info-section-header">{{$t('备注信息')}} </div>
    </div>
    <Input style="width: 400px;margin-top: 8px;" type="textarea" :rows="2" v-model="remark"/>

 </div>
    <div slot="footer">
        <Row v-if="!projectFinishDisabled">
            <Col span="24" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{$t('保存')}}</Button></Col>
         </Row>

    </div>

 </Modal>
</template>

<script>
export default {
  mixins: [componentMixin],
  data(){
    return {
        ruleTypes:[
            {name:this.$t("开始时间前"),value:1},
            {name:this.$t("开始时间后"),value:2},
            {name:this.$t("截止时间前"),value:3},
            {name:this.$t("截止时间后"),value:4},
            {name:this.$t("指定时间"),value:5},
        ],
        unitArray:[
            {name:this.$t("分钟"),value:1},
            {name:this.$t("小时"),value:2},
            {name:this.$t("天"),value:3},
        ],
        unitValue:[

        ],
        remark:"",
        reminderRules:[],
        reminderMembers:[],
        reminderMemberInfos:[],
        task:{},
        editInfo:null,
        projectFinishDisabled:false
    }
  },
  methods:{
        pageLoad(){
            for(var i=0;i<60;i++){
                this.unitValue.push(i+1);
            }
            this.task=this.args.task;
            this.editInfo=this.args.editInfo;
            this.loadData();
            this.projectFinishDisabled = this.isProjectDisabled(this.task.projectId);
        },
        loadData(){
            app.invoke('BizAction.getTaskRemindInfo',[app.token,this.task.id],(info)=>{
                if(info!=null){
                    this.reminderRules=info.remindRules;
                    this.reminderMembers=info.remindAccountIdList;
                    this.reminderMemberInfos=info.remindAccountList;
                    this.remark = info.remark;
                }
                if(this.reminderRules.length==0){
                    this.addRule();
                }
            });
        },
        ruleTypeChaged(item){
            if(item.type==5){
                item.value=new Date().getTime();
            }else{
                item.value=1;
            }
        },
        removeRule(item){
            for(var i=0;i<this.reminderRules.length;i++){
                var t=this.reminderRules[i];
                if(t==item){
                    this.reminderRules.splice(i,1);
                    return;
                }
            }
        },
        addRule(){
            this.reminderRules.push({
                type:1,
                value:1,
                unit:1
            })
        },
        confirm(){
            var obj={
                taskId:this.task.id,
                remindRules:this.reminderRules,
                remindAccountIdList:this.reminderMembers,
                remark:this.remark
            }
            app.invoke('BizAction.updateTaskRemind',[app.token,obj],(info)=>{
                this.showDialog=false;
                if(this.args.callback){
                    this.args.callback();
                }
            });
        }
  }
}
</script>
