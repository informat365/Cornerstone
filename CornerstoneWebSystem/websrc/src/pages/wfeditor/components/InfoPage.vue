<style scoped>
.page{
    display: flex;
    align-items: center;
    background-color: #F1F4F5;
    padding:10px;
    flex-direction: column;
}
.page-form{
    width:700px;
    background-color: #fff;
    border:1px solid #eee;
    border-radius: 3px;
    padding:30px 20px;
}
.form-desc{
    font-size:12px;
    color:#666;
    margin-bottom:5px;
}
</style>
<i18n>
{
	"en": {
        "流程启用中": "流程启用中",
		"流程已禁用": "流程已禁用，将不能发起新的流程。原有流程将继续流转。",
		"将不能发起新的流程": "将不能发起新的流程",
		"原有流程将继续流转": "原有流程将继续流转",
		"流程名称": "流程名称",
		"输入名称": "输入名称",
		"颜色标识": "颜色标识",
		"在流程列表中使用此颜色来标识": "在流程列表中使用此颜色来标识",
		"描述": "描述",
		"流程描述": "流程描述",
		"分组": "分组",
		"分组名称": "分组名称",
		"通知": "通知",
		"流程流转时微信提醒负责人抄送人": "流程流转时微信提醒负责人，抄送人",
		"流程流转时邮件提醒负责人抄送人": "流程流转时邮件提醒负责人，抄送人",
		"允许撤回": "允许撤回",
		"开启后允许提交人在后续节点负责人尚未处理的情况下撤回流程": "开启后允许提交人在后续节点负责人尚未处理的情况下撤回流程",
		"允许打印": "允许打印",
		"开启后允许打印流程表单": "开启后允许打印流程表单",
		"数据标题": "数据标题",
		"将以下字段作为流程的标题显示在流程列表中": "将以下字段作为流程的标题显示在流程列表中",
		"禁用流程": "禁用流程",
		"启用流程": "启用流程",
		"确定要启用此流程吗": "确定要启用此流程吗？",
		"确定要禁用此流程吗": "确定要禁用此流程吗？禁用后将不能发起新的流程，原有流程不受影响。",
		"保存成功": "保存成功"
    },
	"zh_CN": {
		"流程启用中": "流程启用中",
		"流程已禁用": "流程已禁用，将不能发起新的流程。原有流程将继续流转。",
		"将不能发起新的流程": "将不能发起新的流程",
		"原有流程将继续流转": "原有流程将继续流转",
		"流程名称": "流程名称",
		"输入名称": "输入名称",
		"颜色标识": "颜色标识",
		"在流程列表中使用此颜色来标识": "在流程列表中使用此颜色来标识",
		"描述": "描述",
		"流程描述": "流程描述",
		"分组": "分组",
		"分组名称": "分组名称",
		"通知": "通知",
		"流程流转时微信提醒负责人抄送人": "流程流转时微信提醒负责人，抄送人",
		"流程流转时邮件提醒负责人抄送人": "流程流转时邮件提醒负责人，抄送人",
		"允许撤回": "允许撤回",
		"开启后允许提交人在后续节点负责人尚未处理的情况下撤回流程": "开启后允许提交人在后续节点负责人尚未处理的情况下撤回流程",
		"允许打印": "允许打印",
		"开启后允许打印流程表单": "开启后允许打印流程表单",
		"数据标题": "数据标题",
		"将以下字段作为流程的标题显示在流程列表中": "将以下字段作为流程的标题显示在流程列表中",
		"禁用流程": "禁用流程",
		"启用流程": "启用流程",
		"确定要启用此流程吗": "确定要启用此流程吗？",
		"确定要禁用此流程吗": "确定要禁用此流程吗？禁用后将不能发起新的流程，原有流程不受影响。",
		"保存成功": "保存成功"
	}
}
</i18n>
<template>
    <div class="page">
       <div class="page-form">
            <Alert v-if="formItem.status==1" type="info">{{$t('流程启用中')}}</Alert>
            <Alert v-if="formItem.status==2" type="error">{{$t('流程已禁用')}}</Alert>
            <Form ref="form" :model="formItem" :rules="formRule" :label-width="80">
                <FormItem :label="$t('流程名称')" prop="name">
                    <Input v-model.trim="formItem.name" :placeholder="$t('输入名称')"></Input>
                </FormItem>
                <FormItem :label="$t('颜色标识')" prop="color">
                    <div class="form-desc">{{$t('在流程列表中使用此颜色来标识')}}</div>
                    <ColorPicker :recommend="true" :colors="colorArray" v-model="formItem.color"/>
                </FormItem>
                <FormItem :label="$t('描述')" prop="remark">
                    <Input type="textarea" :rows="3" v-model.trim="formItem.remark" :placeholder="$t('流程描述')"></Input>
                </FormItem>
                
                <FormItem :label="$t('分组')" prop="group">
                    <Input style="width:150px" v-model.trim="formItem.group" :placeholder="$t('分组名称')"></Input>
                </FormItem>

                <FormItem :label="$t('通知')">
                    <div>
                        <div class="form-desc"> <i-Switch v-model="formItem.enableNotifyWechat"/> {{$t('流程流转时微信提醒负责人抄送人')}}</div>
                        <div class="form-desc"> <i-Switch v-model="formItem.enableNotifyEmail"/> {{$t('流程流转时邮件提醒负责人抄送人')}}</div>
                    </div>
                </FormItem>

                <FormItem :label="$t('允许撤回')">
                    <i-Switch v-model="formItem.enableCancel"/> 
                    <div class="form-desc">{{$t('开启后允许提交人在后续节点负责人尚未处理的情况下撤回流程')}}</div>
                </FormItem>

                 <FormItem :label="$t('允许打印')">
                    <i-Switch v-model="formItem.enablePrint"/> 
                    <div class="form-desc">{{$t('开启后允许打印流程表单')}}</div>
                </FormItem>

                <FormItem :label="$t('数据标题')">
                    <div class="form-desc">{{$t('将以下字段作为流程的标题显示在流程列表中')}}</div>
                    <Select multiple transfer v-model="formItem.titleFormFieldList">
                        <Option v-for="item in fieldList" :key="+'content-'+item.id" :value="item.id">{{item.name}}</Option>
                    </Select>
                </FormItem>

                <FormItem>
                    <Button v-if="formItem.status==1"  @click="disableWorkflow" type="error">{{$t('禁用流程')}}</Button>
                    <Button v-if="formItem.status==2"  @click="enableWorkflow" type="default">{{$t('启用流程')}}</Button>
                </FormItem>
                
            </Form>
       </div>
    </div>
</template>

<script>
import WorkflowPageMixin from './WorkflowPageMixin'
export default {
    mixins: [componentMixin,WorkflowPageMixin],
    data(){
        return { 
            formItem:{
                name:"",
                remark:"",
                status:1,
                group:"",
                color:"#333333"
            },
            formRule:{
                name:[vd.req,vd.name],
                remark:[vd.name],
                group:[vd.name],
            },
            workflowFormFieldList:[],
            fieldList:[],
            colorArray:[
                    "#2E94B9",
                    "#FFFDC0",
                    "#F0B775",
                    "#D25565",
                    "#F54EA2",
                    "#42218E",
                    "#5BE7C4",
                    "#525564",
            ],
        }
    },
    methods:{
        pageLoad(){
            app.currentPage=this;
            this.loadData();
           
        }, 
        pageMessage(type){
            if(type=='workflow.template.status.edit'){
                this.loadData();
            }
        },
        loadData(){
            app.invoke('WorkflowAction.getWorkflowDefineById',[app.token,app.workflowDefine.id],(info)=>{
                this.formItem = Object.assign({}, this.formItem, info)
                this.loadFormData();
            });
           
        },
        enableWorkflow(){
            app.confirm(this.$t('确定要启用此流程吗'),()=>{
                app.invoke('WorkflowAction.enableWorkflowDefine',[app.token,app.workflowDefine.id],(info)=>{
                    app.postMessage('workflow.template.status.edit')
                });
            });
        },
        disableWorkflow(){
            app.confirm(this.$t('确定要禁用此流程吗'),()=>{
                app.invoke('WorkflowAction.disableWorkflowDefine',[app.token,app.workflowDefine.id],(info)=>{
                    app.postMessage('workflow.template.status.edit')
                });
            });
        },
        loadFormData(){
            app.invoke('WorkflowAction.getWorkflowFormDefineById',[app.token,this.formItem.id],(info)=>{
                if(info.fieldList){
                    this.workflowFormFieldList=JSON.parse(info.fieldList)
                    this.setupFieldList();
                    this.watchSaveProp('formItem');
                }
            });
        },
        setupFieldList(){
            this.fieldList=[];
            for(var i=0;i<this.workflowFormFieldList.length;i++){
                var wf=this.workflowFormFieldList[i];
                var t=wf.type;
                if(t=='text-single'||
                    t=='system-value'
                    ||t=='user-select'
                    ||t=='radio'
                    ||t=='select'
                    ||t=='department-select'
                    ||t=='role-company-select'
                    ||t=='role-project-select'){
                    this.fieldList.push(wf);
                }
            } 
        },
        confirm(callback){
            this.$refs.form.validate((r)=>{
                if(r){
                    this.confirmForm(callback);
                }
            });
        },
        confirmForm(callback){
             app.invoke('WorkflowAction.updateWorkflowDefine',[app.token,this.formItem],(info)=>{
                app.toast(this.$t('保存成功'));
                this.isSaved=true;
                app.postMessage('workflow.template.edit');   
                if(callback){
                    callback();
                }
			})
        }
    }
}
</script>