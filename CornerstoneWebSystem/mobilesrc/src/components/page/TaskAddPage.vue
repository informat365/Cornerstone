<style scoped>
    .page{
       padding:0;
    }
    .mul-value{
        margin-left:5px;
    }

    .v-right /deep/ input{
        text-align: right;
    }
</style>
<template>
  <div class="page">  
        <group label-width="70px" label-margin-right="10px" label-align="left">
            <group-title slot="title">{{formItem.objectType|dataDict('Task.objectType')}}信息</group-title>
        
            <x-input title="名称" v-model.trim="formItem.name" placeholder="请输入名称"></x-input>
            <cell is-link v-if="hasField('ownerAccountName')" title="责任人" @click.native="editOwner()">
                    <div v-if="formItem.ownerAccountIdList">
                        <template v-if="formItem.ownerAccountIdList.length==0">
                            <span >待认领</span>
                         </template>
                        <template v-if="formItem.ownerAccountIdList.length==1">
                            <AvatarImage  size="small" :name="getMember(formItem.ownerAccountIdList[0]).accountName" :value="getMember(formItem.ownerAccountIdList[0]).accountImageId" type="label"></AvatarImage>
                        </template> 
                         <template v-if="formItem.ownerAccountIdList.length>1">
                             <AvatarImage  v-for="acc in formItem.ownerAccountIdList" :key="'_acc'+acc" size="small" :name="getMember(acc).accountName" :value="getMember(acc).accountImageId" type="tips"></AvatarImage>
                        </template>
                    </div>
            </cell>
            <popup-radio  v-if="hasField('statusName')" title="状态" :options="editInfo.statusList" v-model="formItem.status"></popup-radio>
            <popup-radio  v-if="hasField('priorityName')" title="优先级" :options="editInfo.priorityList" v-model="formItem.priority"></popup-radio>
            <popup-radio  v-if="hasField('iterationName')" title="迭代" :options="editInfo.iterationList" v-model="formItem.iterationId"></popup-radio>
            <popup-radio  v-if="hasField('subSystemName')" title="子系统" :options="editInfo.subSystemList" v-model="formItem.subSystemId"></popup-radio>
            <popup-radio  v-if="hasField('releaseName')" title="Release" :options="editInfo.releaseList" v-model="formItem.releaseId"></popup-radio>
            <popup-radio  v-if="hasField('stageName')" title="阶段" :options="editInfo.stageList" v-model="formItem.stageId"></popup-radio>


            <datetime  v-if="hasField('startDate')"  title="开始时间" v-model="formItem.startDate"></datetime>
            <datetime  v-if="hasField('endDate')"  title="截止时间" v-model="formItem.endDate"></datetime>
            <datetime  v-if="hasField('expectEndDate')"  title="计划截止时间" v-model="formItem.expectEndDate"></datetime>
            <x-input  class="v-right" v-if="hasField('expectWorkTime')"   type="number" title="预计工时" v-model.trim="formItem.expectWorkTime" ></x-input>


            
        </group>

        <group v-if="hasOtherField()" label-margin-right="10px" label-align="left">
            <group-title slot="title">其它信息</group-title>
            <template v-if="item.isSystemField==false" v-for="item in editInfo.fieldList">
                <x-input class="v-right" v-if="item.type==1" :title="item.name+(item.isRequired?'*':'')" v-model.trim="formItem.customFields['field_'+item.id]" placeholder="未设置"></x-input>
                <x-input class="v-right"  v-if="item.type==8"  type="number" :title="item.name+(item.isRequired?'*':'')" v-model.trim="formItem.customFields['field_'+item.id]" placeholder="未设置"></x-input>
                <datetime  v-if="item.type==7"  :title="item.name+(item.isRequired?'*':'')" v-model="formItem.customFields['field_'+item.id]"></datetime>

                 <cell @click.native="editCustomProp(item)" is-link v-if="item.type==3"  :title="item.name+(item.isRequired?'*':'')">
                    <span class="mul-value" v-for="vv in formItem.customFields['field_'+item.id]">{{vv}}</span>
                 </cell>
                 <cell @click.native="editCustomProp(item)" is-link v-if="item.type==4"  :title="item.name+(item.isRequired?'*':'')">
                    {{formItem.customFields['field_'+item.id]}}
                 </cell>

                 <cell @click.native="editCustomMember(item)" is-link v-if="item.type==6"  :title="item.name+(item.isRequired?'*':'')">
                        <div v-if="formItem.customFields['field_'+item.id]">
                        <template v-if="formItem.customFields['field_'+item.id].length==0">
                            <span>未设置</span>
                         </template>
                        <template v-if="formItem.customFields['field_'+item.id].length==1">
                            <AvatarImage  size="small" :name="getMember(formItem.customFields['field_'+item.id][0]).accountName" :value="getMember(formItem.customFields['field_'+item.id][0]).accountImageId" type="label"></AvatarImage>
                        </template> 
                         <template v-if="formItem.customFields['field_'+item.id].length>1">
                             <AvatarImage  v-for="acc in formItem.customFields['field_'+item.id]" :key="'_acc'+acc" size="small" :name="getMember(acc).accountName" :value="getMember(acc).accountImageId" type="tips"></AvatarImage>
                        </template>
                    </div>
                 </cell>
            </template>
         </group>

         <group label-width="60px" label-margin-right="10px" label-align="left">
            <group-title slot="title">详细描述</group-title>
            <x-textarea :autosize="true" v-model.trim="editInfo.content"></x-textarea>
         </group>

         <div style="padding:20px">
            <x-button @click.native="confirm" type="primary">确定</x-button>
        </div>

        <TaskPopupSelect v-model="showPopup"  @change="popupConfirm" 
                :multiple="popupMultiple"  
                :list="popupSelectList"
                :selected-list="popupSelectValue" ></TaskPopupSelect>
  </div>
</template>

<script>

import { Flexbox,XButton,TransferDom,Datetime,Group,GroupTitle,Cell,CellBox, PopupRadio,XInput,XTextarea,FlexboxItem,Popup} from 'vux'

export default {
    components: {Flexbox,XButton,XTextarea,Datetime,PopupRadio,XInput,CellBox,FlexboxItem,Group,GroupTitle,Cell, Popup},
    directives: {TransferDom},
    mixins:[componentMixin],
    data () {
        return {
           formItem:{
               name:null,
               priority:null,
               status:null,
               iterationId:null,
               stageId:null,
               projectId:null,
               objectType:null,
               customFields:{},
               ownerAccountIdList:[],
           },
           editInfo:{
               statusList:[],
               priorityList:[],
               iterationList:[],
               subSystemList:[],
               releaseList:[],
               stageList:[]
           },
            showPopup:false,
            popupSelectList:[],
            popupSelectValue:[],
            popupMultiple:false,
            currentEditProp:null,
            currsntCustomField:null,
        }
    },
    
    methods:{
        pageLoad(){
            this.formItem.projectId=this.args.projectId;
            this.formItem.iterationId=this.args.iterationId;
            this.formItem.objectType=this.args.type;
            this.loadEditInfo();
           
        },
        loadEditInfo(){
            app.invoke("BizAction.getEditTaskInfo",[app.token,this.args.projectId,this.args.type],info => {
                this.editInfo=info;
                //
                this.editInfo.priorityList.map(item=>{
                    item.key=item.id;
                    item.value=item.name;
                    if(item.isDefault){
                        this.formItem.priority=item.id;
                    }
                })
                this.editInfo.statusList.map(item=>{
                    item.key=item.id;
                    item.value=item.name;
                    if(item.type==1&&this.args.status==null){
                        this.formItem.status=item.id;
                    }
                })
                this.editInfo.iterationList.map(item=>{
                    item.key=item.id;
                    item.value=item.name;
                })
                 this.editInfo.subSystemList.map(item=>{
                    item.key=item.id;
                    item.value=item.name;
                })
                 this.editInfo.releaseList.map(item=>{
                    item.key=item.id;
                    item.value=item.name;
                })
                 this.editInfo.stageList.map(item=>{
                    item.key=item.id;
                    item.value=item.name;
                })

                //
                if(this.formItem.iterationId==null&&info.iterationList.length>0){
                    this.formItem.iterationId=info.iterationList[0].id;
                }
            });
        },
        hasOtherField(){
            if(this.editInfo==null||this.editInfo.fieldList==null){
                return false;
            }
            var result=false;
            for(var i=0;i<this.editInfo.fieldList.length;i++){
                var t=this.editInfo.fieldList[i];
                if(t.isSystemField==false){
                    return true;
                }
            }
            return false;
        },
        hasField(name){
                if(this.editInfo.fieldList){
                    for(var i=0;i<this.editInfo.fieldList.length;i++){
                        var f=this.editInfo.fieldList[i];
                        if(f.field==name&&f.isShow){
                            return true;
                        }
                    }
                }
                return false;
        },
        getMember(id){
            for(var i=0;i<this.editInfo.memberList.length;i++){
                var t=this.editInfo.memberList[i];
                if(t.accountId==id){
                    return t;
                }
            }
            return {};
        },
        editOwner(){
            this.editMember('ownerAccountIdList');
            for(var i=0;i<this.formItem.ownerAccountIdList.length;i++){
                this.popupSelectValue.push(this.formItem.ownerAccountIdList[i])
            }
        },
        editCustomMember(item){
            this.currsntCustomField=item;
            this.editMember('field_'+item.id);
        },
        
        editMember(prop){
            this.currentEditProp=prop
            this.showPopup=true;
            this.popupSelectList=[];
            this.popupMultiple=true;
            for(var i=0;i<this.editInfo.memberList.length;i++){
                var t=this.editInfo.memberList[i]
                 this.popupSelectList.push({
                     key:t.accountId,
                     value:t.accountName
                 })
            }
            this.popupSelectValue=[];
        },
        editCustomProp(item){
            this.currsntCustomField=item;
            this.currentEditProp="field_"+item.id;
            this.showPopup=true;
            this.popupSelectList=[];
            this.popupMultiple=item.type==3;
            for(var i=0;i<item.valueRange.length;i++){
                var t=item.valueRange[i]
                 this.popupSelectList.push({
                     key:t,
                     value:t
                 })
            }
            this.popupSelectValue=[];
            var customValue=this.formItem.customFields[this.currentEditProp];
            if(item.type==3&&customValue){
                for(var i=0;i<customValue.length;i++){
                    this.popupSelectValue.push(customValue[i])
                }
            }
            if(item.type==4&&customValue){
                this.popupSelectValue.push(customValue)
            }
        },
        popupConfirm(v){
            if(this.currentEditProp=='ownerAccountIdList'){
                this.formItem[this.currentEditProp]=v;
            }
            if(this.currentEditProp.indexOf('field_')!=-1){
                if(this.currsntCustomField.type==4){
                    if(v.length==1){
                        this.formItem.customFields[this.currentEditProp]=v[0];
                    }else{
                         this.formItem.customFields[this.currentEditProp]=null;
                    }
                }
                if(this.currsntCustomField.type==3){
                    this.formItem.customFields[this.currentEditProp]=v;
                }
                if(this.currsntCustomField.type==6){
                    this.formItem.customFields[this.currentEditProp]=v;
                }
            }
        },
        confirm(){
            app.invoke('BizAction.createTask',[app.token,this.formItem],(id)=>{
                app.toast(''+this.formItem.name+' 创建成功')
                app.postMessage('task.edit')
                app.popPage();
			})
        }
    }
}
</script>


