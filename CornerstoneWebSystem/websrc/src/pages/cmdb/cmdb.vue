<style >

</style>
<template>
<div>
  <router-view v-if="login"></router-view>
</div>
   
</template>
<script>

export default {
 
  data() {
    return {
      login:false,
    }
  },
  mounted() {
        document.title=app.name
        var loading=document.getElementById('loading-box');
        if(loading){
            loading.parentNode.removeChild(loading);
        }
        
        app.getLoginInfo(()=>{
            this.login=true;
            if(app.company==null){
                app.loadPage('/main')
            }else{
                if(this.$route.fullPath=='/'){
                    app.loadPage('/pm/index/dashboard')
                }
            }
        });
  },
  methods: {

  }
};
</script>