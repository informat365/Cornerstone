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
        "以下成员或者角色可以填写问卷调查": "以下成员或者角色可以填写问卷调查",
		"保存成功": "保存成功"
    },
	"zh_CN": {
		"以下成员或者角色可以填写问卷调查": "以下成员或者角色可以填写问卷调查",
		"保存成功": "保存成功"
	}
}
</i18n>
<template>
    <div class="page">
        <div class="page-form">
            <div class="form-desc">{{$t('以下成员或者角色可以填写问卷调查')}}</div>
            <PermissionForm v-model="formItem"/>
        </div>
    </div>
</template>

<script>
import SurveysPageMixin from './SurveysPageMixin'
export default {
    mixins: [componentMixin,SurveysPageMixin],
    data() {
        return {
            formItem: {
                userList:[],
                companyRoleList:[],
                projectRoleList:[],
                departmentList:[],
            },
        };
    },
    methods: {
        pageLoad(){
            app.currentPage=this;
            this.loadData();
            this.watchSaveProp('formItem');
        }, 
        loadData(){
            app.invoke('SurveysAction.getSurveysDefineByUuid',[app.token,app.surveysDefine.uuid],(info)=>{
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
            var permissionDefine={
                id:app.surveysDefine.id
            }
            permissionDefine.accountList=(this.formItem.userList);
            permissionDefine.companyRoleList=(this.formItem.companyRoleList);
            permissionDefine.projectRoleList=(this.formItem.projectRoleList);
            permissionDefine.departmentList=(this.formItem.departmentList);
            app.invoke('SurveysAction.updateSurveysDefinePermission',[app.token,permissionDefine],(info)=>{
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