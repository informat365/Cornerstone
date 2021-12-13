<style scoped>
.page {
    display: flex;
    align-items: center;
    background-color: #f1f4f5;
    padding: 10px;
    flex-direction: column;
}
.page-form {
    width: 700px;
    background-color: #fff;
    border: 1px solid #eee;
    border-radius: 3px;
    padding: 30px 20px;
}
.form-desc{
    font-size:14px;
    color:#666;
    margin-bottom:20px;
    text-align: center;
}
</style>
<i18n>
{
	"en": {
        "以下成员或者角色可以发起流程": "以下成员或者角色可以发起流程",
		"保存成功": "保存成功"
    },
	"zh_CN": {
		"以下成员或者角色可以发起流程": "以下成员或者角色可以发起流程",
		"保存成功": "保存成功"
	}
}
</i18n>
<template>
    <div class="page">
        <div class="page-form">
            <div class="form-desc">{{$t('以下成员或者角色可以发起流程')}}</div>
            <PermissionForm v-model="formItem"/>
        </div>
    </div>
</template>

<script>
import WorkflowPageMixin from './WorkflowPageMixin'
export default {
    mixins: [componentMixin,WorkflowPageMixin],
    data() {
        return {
            formItem: {
                userList:[],
                companyRoleList:[],
                projectRoleList:[],
                departmentList:[],
            },
            permissionDefine:null
        };
    },
    methods: {
        pageLoad(){
            app.currentPage=this;
            this.loadData();
            this.watchSaveProp('formItem');
        }, 
        loadData(){
            app.invoke('WorkflowAction.getWorkflowDefinePermissionById',[app.token,app.workflowDefine.id],(info)=>{
                var temp=info;
                if(temp.accountList!=null){
                    this.concatList(this.formItem.userList,(temp.accountList))
                }
                if(temp.companyRoleList!=null){
                    this.concatList(this.formItem.companyRoleList,(temp.companyRoleList))
                }
                if(temp.projectRoleList!=null){
                    this.concatList(this.formItem.projectRoleList,(temp.projectRoleList))
                }
                if(temp.departmentList!=null){
                    this.concatList(this.formItem.departmentList,(temp.departmentList))
                }
                this.permissionDefine=info;
                this.$nextTick(()=>{
                    this.isSaved=true;
                })
            });
        },
        concatList(list,addList){
            addList.forEach(item=>{
                list.push(item)
            })
        },
        confirm(callback){
            this.confirmForm(callback);
        },
        confirmForm(callback){
            this.permissionDefine.accountList=(this.formItem.userList);
            this.permissionDefine.companyRoleList=(this.formItem.companyRoleList);
            this.permissionDefine.projectRoleList=(this.formItem.projectRoleList);
            this.permissionDefine.departmentList=(this.formItem.departmentList);
            app.invoke('WorkflowAction.updateWorkflowDefinePermission',[app.token,this.permissionDefine],(info)=>{
                app.toast(this.$t('保存成功'));
                this.isSaved=true;
                if(callback){
                    callback();
                }
			})
        }
    }
};
</script>