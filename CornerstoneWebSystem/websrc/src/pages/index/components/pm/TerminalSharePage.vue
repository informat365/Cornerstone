<style scoped>
.page{
    min-height: 400px;
}
.share-info{
  font-size:16px;
  color:#999;
  margin-top:40px;
  text-align: center;
}
</style>
<template>
    <div class="page">
         <div class="share-info">{{info.createAccountName}}在{{info.createTime|fmtDateTime}}分享的终端,{{info.type==1?"只读":"协作"}}模式</div>
    </div>
</template>

<script>

export default {
  mixins: [componentMixin],
  data(){
    return {
        info:{},
    }
  },
  methods:{
      pageLoad(){
        app.invoke("BizAction.getLoginMachineInfoByToken",[app.token,app.terminalToken],info => {
            this.info=info;
            app.showDialog(TerminalWindow,{
                info:info
            })
        });
      }
  }
}
</script>