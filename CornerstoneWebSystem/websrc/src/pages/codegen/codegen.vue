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
                window.location.href="/"
            }
        });
  },
  methods: {

  }
};
</script>