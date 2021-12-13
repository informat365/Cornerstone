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
        "流程启用中": "问卷调查启用中",
		"流程已禁用": "问卷调查已禁用，不能继续填写",
        "输入名称":"输入名称",
		"名称": "名称",
		"允许匿名": "允许匿名",
		"允许重新编辑": "允许重新编辑",
		"开始时间": "开始时间",
		"结束时间": "结束时间",
		"二维码": "二维码",
		"未设置": "未设置",
		"禁用": "禁用",
		"启用": "启用",
		"确定要启用": "确定要启用此问卷调查吗？",
		"确定要禁用": "确定要禁用此问卷调查吗？",
		"保存成功": "保存成功"
    },
	"zh_CN": {
		"流程启用中": "问卷调查启用中",
		"流程已禁用": "问卷调查已禁用，不能继续填写",
        "输入名称":"输入名称",
		"名称": "名称",
		"允许匿名": "允许匿名",
		"允许重新编辑": "允许重新编辑",
		"开始时间": "开始时间",
		"结束时间": "结束时间",
		"二维码": "二维码",
		"未设置": "未设置",
		"禁用": "禁用",
		"启用": "启用",
		"确定要启用": "确定要启用此问卷调查吗？",
		"确定要禁用": "确定要禁用此问卷调查吗？",
		"保存成功": "保存成功",
        "编辑后生成新的记录":"编辑后生成新的记录"
	}
}
</i18n>
<template>
    <div class="page">
       <div class="page-form">
            <Alert v-if="formItem.status==1" type="info">{{$t('流程启用中')}}</Alert>
            <Alert v-if="formItem.status==2" type="error">{{$t('流程已禁用')}}</Alert>
            <Form ref="form" :model="formItem" :rules="formRule" :label-width="100">
                <FormItem :label="$t('名称')" prop="name">
                    <Input v-model.trim="formItem.name" :placeholder="$t('输入名称')"></Input>
                </FormItem>
              
                <FormItem :label="$t('允许匿名')" >
                    <i-Switch v-model="formItem.anonymous"></i-Switch>
                    <div class="form-desc">允许匿名的问卷调查不需要登录就可以提交</div>
                </FormItem>

                <FormItem :label="$t('允许重新编辑')" >
                    <i-Switch v-model="formItem.submitEdit"></i-Switch>
                    <div class="form-desc">提交后允许重新编辑修改</div>
                </FormItem>


                <FormItem :label="$t('开始时间')" prop="startTime">
                    <ExDatePicker type="datetime" v-model="formItem.startTime" :placeholder="$t('未设置')" ></ExDatePicker>
                </FormItem>

                <FormItem :label="$t('结束时间')" prop="endTime">
                    <ExDatePicker type="datetime" v-model="formItem.endTime" :placeholder="$t('未设置')" ></ExDatePicker>
                    <div class="form-desc">设置了开始时间或截止时间的问卷调查只允许在这个时间段内提交</div>
                </FormItem>


                 <FormItem :label="$t('二维码')" >
                    
                    <qrcode :value="shareURL" :options="{ size: 200 }"></qrcode>
                    <div><a target="_blank" :href="shareURL">{{shareURL}}</a></div>
                    <div class="form-desc">扫描二维码可以在手机上填写</div>
                </FormItem>


            

                <FormItem>
                    <Button v-if="formItem.status==1"  @click="disableSurveys" type="error">{{$t('禁用')}}</Button>
                    <Button v-if="formItem.status==2"  @click="enableSurveys" type="default">{{$t('启用')}}</Button>
                </FormItem>
                
            </Form>
       </div>
    </div>
</template>

<script>
import SurveysPageMixin from './SurveysPageMixin'
import qrcode from '@xkeshi/vue-qrcode';
export default {
    mixins: [componentMixin,SurveysPageMixin],
    components:{
        qrcode
    },
    data(){
        return { 
            formItem:{
                name:"",
                remark:"",
                status:1
            },
            formRule:{
                name:[vd.req,vd.name],
                remark:[vd.name],
                group:[vd.name],
            },
            shareURL:""
        }
    },
    methods:{
        pageLoad(){
            app.currentPage=this;
            this.loadData();
           
        }, 
        pageMessage(type){
            if(type=='surveys.template.status.edit'){
                this.loadData();
            }
        },
        loadData(){
            app.invoke('SurveysAction.getSurveysDefineByUuid',[app.token,app.surveysDefine.uuid],(info)=>{
                this.formItem = Object.assign({}, this.formItem, info)
                var host=app.getHost();
                this.shareURL=host+"/surveysform.html?uuid="+info.uuid
            });
        },
        enableSurveys(){
            app.confirm(this.$t('确定要启用'),()=>{
                app.invoke('SurveysAction.enableSurveysDefine',[app.token,app.surveysDefine.id],(info)=>{
                    app.postMessage('surveys.template.status.edit')
                });
            });
        },
        disableSurveys(){
            app.confirm(this.$t('确定要禁用'),()=>{
                app.invoke('SurveysAction.disableSurveysDefine',[app.token,app.surveysDefine.id],(info)=>{
                    app.postMessage('surveys.template.status.edit')
                });
            });
        },
        confirm(callback){
            this.$refs.form.validate((r)=>{
                if(r){
                    this.confirmForm(callback);
                }
            });
        },
        confirmForm(callback){
             app.invoke('SurveysAction.updateSurveysDefine',[app.token,this.formItem],(info)=>{
                app.toast(this.$t('保存成功'));
                this.isSaved=true;
                app.postMessage('surveys.template.edit');   
                if(callback){
                    callback();
                }
			})
        }
    }
}
</script>