<style >
    .mainbox{
        text-align: center;
        padding:100px;
    }
    .mainview{
        text-align: center;
        display: inline-block;
        width:300px;
    }
    .big-input .ivu-input{
        width: 100% !important;
        height: 50px !important;
        line-height: 1.5 !important;
        padding: 4px 17px !important;
        font-size: 14px !important;
    }
    .mainview-company-name{
        font-size:16px;
        margin-top:10px;
        margin-bottom: 10px;
        color:#666;
    }
</style>
<i18n>
{
  "en": {
  },
  "zh_CN": {
    "创建您的企业": "创建您的企业",
    "企业名称":"企业名称",
    "创建":"创建"
  }
}
</i18n>
<template>
<div class="mainbox">
    <div class="mainview">
            <div class="mainview-company-name">{{$t('创建您的企业')}}</div>
            <div style="margin-top:30px"><Input :maxlength="40" v-model.trim="formItem.name" class="big-input"  :placeholder="$t('企业名称')"/></div>
            <div style="margin-top:30px">
                
                <Button :disabled="formItem.name==''||creating" @click="createCompany" type="default" style="padding:10px 10px " long size="large">{{$t('创建')}}</Button>
                <div v-if="creating" style="padding:10px;">正在创建中...</div>
            </div>
    </div>
</div>
</template>

<script>
    export default {
        data () {
            return {
                formItem:{
                    name:''
                },
                creating:false
            }
        },
        mounted(){
            
        },
        methods:{
            createCompany(){
                this.creating=true;
                app.invoke('BizAction.createCompany',[app.token,this.formItem],(info)=>{
					app.getLoginInfo(()=>{
                        app.loadPage('/pm/index/dashboard')
                    });
			    },()=>{
                    this.creating=false;
                })
            }
        }
    }
</script>