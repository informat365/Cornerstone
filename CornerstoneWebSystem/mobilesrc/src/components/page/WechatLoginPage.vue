<template>
  <div class="page">
    <div>
        <img class="logo" src="../../assets/image/logo.png">
    </div>
     <template v-if="loginSuccess==false">
            <div v-if="unbind==false">
                <div class="confirm-info">确认授权登录</div>
                <div><x-button @click.native="login" type="primary">登录</x-button></div>
            </div>
            <div v-if="unbind">
                <div class="confirm-info">您没有绑定微信账号，无法使用微信登录</div>
            </div>
    </template>
    <template v-if="loginSuccess==true">
        <div class="success-info">登录成功</div>
    </template>

  </div>
</template>

<script>
import { XButton } from 'vux'
export default {
    components: {
        XButton
    },
    mixins:[componentMixin],
    data () {
        return {
            unbind:false,
            state:null,
            loginSuccess:false,
        }
    },
    mounted(){
        this.state=this.$route.query.state;
        if(this.state==null){
            this.unbind=true;
        }
    },
    methods:{
        
        login:function(){
            var state=this.$route.query.state;
            app.invoke("BizAction.wxConfirmLogin",[state],(obj)=>{
                this.loginSuccess=true;
            })
        }
    }

}
</script>

<style scoped>
.confirm-info{
    font-size:18px;
    font-weight: bold;
    color:#666;
    margin-bottom: 80px;
    margin-top:20px;
}
.success-info{
    font-size:24px;
    font-weight: bold;
    color:#17A7ED;
    margin-top: 80px;
}
.logo {
  width: 130px;
  margin-top:40px;
}
.page{
    text-align: center;
    padding:20px;
}
</style>
