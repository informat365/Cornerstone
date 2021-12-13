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
        "以下成员或者角色可以查看流程数据": "以下成员或者角色可以查看流程数据",
		"保存成功": "保存成功"
    },
	"zh_CN": {
		"以下成员或者角色可以查看流程数据": "以下成员或者角色可以查看流程数据",
		"保存成功": "保存成功"
	}
}
</i18n>
<template>
    <div class="page">
        <div class="page-form">
            <div class="form-desc">{{$t('以下成员或者角色可以查看流程数据')}}</div>
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
                if(temp.dataAccountList!=null){
                    this.concatList(this.formItem.userList,temp.dataAccountList)
                }
                if(temp.dataCompanyRoleList!=null){
                    this.concatList(this.formItem.companyRoleList,temp.dataCompanyRoleList)
                }
                if(temp.dataProjectRoleList!=null){
                    this.concatList(this.formItem.projectRoleList,temp.dataProjectRoleList)
                }
                if(temp.dataDepartmentList!=null){
                    this.concatList(this.formItem.departmentList,temp.dataDepartmentList)
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
            this.permissionDefine.dataAccountList=(this.formItem.userList);
            this.permissionDefine.dataCompanyRoleList=(this.formItem.companyRoleList);
            this.permissionDefine.dataProjectRoleList=(this.formItem.projectRoleList);
            this.permissionDefine.dataDepartmentList=(this.formItem.departmentList);
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