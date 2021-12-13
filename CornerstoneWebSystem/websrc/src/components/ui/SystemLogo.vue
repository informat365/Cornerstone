<style scoped>
    .head-logo{
        height: 20px;
        vertical-align: middle;
    }
     .head-logo-custom{
        height: 42px;
        vertical-align: middle;
    }
    .head-logo-custom-cs{
        position:absolute;
        bottom: 6px;
        right:25px;
        width:50px;
        background-color: rgba(255,255,255,0.8);
        filter: opacity(70%);
    }
    .system-logo{
        position: relative;
    }
</style>
<template>
    <div class="system-logo">
        <img class="head-logo" v-if="value==null" src="/image/logo.png"/>
        <img class="head-logo-custom" v-if="value" :src="imageURL"/>
    </div>
    
</template>
<script>
export default {
    name:"SystemLogo",
    props: ['value'],
    data (){
        return{
            
        }
    },
    computed:{
        imageURL:function(){
            return app.serverAddr+"/p/file/get_file/"+this.value+"?token="+app.token;
        }
    }
}
</script>