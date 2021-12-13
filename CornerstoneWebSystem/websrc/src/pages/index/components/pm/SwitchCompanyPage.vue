<style scoped>
.page{
    min-height: 400px;
    padding:100px;
    font-size:20px;
    font-weight: bold;
    color:#999;
    text-align: center;
}
</style>
<i18n>
{
    "en": {
        "正在切换": "Loading..."
    },
    "zh_CN": {
        "正在切换": "正在切换，请稍后"
    }
}
</i18n>
<template>
    <div class="page">
        {{$t('正在切换')}}
    </div>
</template>
<script>
export default {
  mixins: [componentMixin],
  data(){
        return {
        }
    },
    methods:{
        pageLoad(){
            app.invoke('BizAction.switchCompany',[app.token,this.args.id],()=>{
                this.reloadAppInfo();
            })
        },
        reloadAppInfo(){
            app.getLoginInfo(()=>{
                window.location.href="/"
                setTimeout(()=>{
                    window.location.reload();
                },1000)
            })
        }
    }
}
</script>