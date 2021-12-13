<style scoped>
    .spin-icon-load{
        animation: ani-demo-spin 1s linear infinite;
        color:#fff;
    }
    .loading-wrap{
        width:100vw;
        height: 100vh;
        background-color: transparent;
        z-index:999999;
        position: fixed;
        text-align: center;
        padding-top:0px;
        display: flex;
        align-items: center;
        justify-content: center;
        top:0;
        left:0;
    }
    .loading-box{
         background-color: rgba(0,0,0,0.4);
         border-radius: 14px;
         width:150px;
         display: inline-block;
         text-align:center;
         padding:20px;
         color:#fff;
    }
</style>
<template>
  <div class="loading-wrap">
      <div  class="loading-box">
             <Spin>
                <Icon type="ios-loading" size=60 class="spin-icon-load"></Icon>
                <div style="margin-top:15px;font-size:16px;color:#fff">{{args.message}}</div>
            </Spin>
        </div>
  </div>
</template>
<script>
    export default {
        name:"LoadingDialog",
        data () {
            return {
                showDialog: true,
                args:{
                    message:""
                }
            }
        },
        watch:{
            showDialog:function(val){
                if(val==false){
                    this.$el.parentElement.removeChild(this.$el)
                }
            }
        },
        created(){
        },
        methods: {
        }
    }
</script>