<template>
     <router-view v-if="loginSuccess"></router-view>
</template>
<script>
export default {
    mixins:[componentMixin],
    data () {
        return {
            loginSuccess:false
        }
    },
    mounted(){
        if(this.$route.query["_token"]!=null){
            app.setCookie('token',this.$route.query["_token"])
        }
        app.getLoginInfo(()=>{
            this.loginSuccess=true
        })
    },
    methods:{
       
    }
}
</script>

<style>

</style>
