<template>
  <div class="page">
    <div>
        <img class="logo" src="../../assets/image/logo.png">
    </div>
    <group>
         <x-input label-width="80px" title="用户名" placeholder="用户名/手机号码" v-model.trim="formItem.userName"></x-input>
         <x-input label-width="80px" title="密码" type="password"  v-model.trim="formItem.password"></x-input>
    </group>
    <div style="padding:20px">
        <x-button :disabled="this.formItem.userName==null ||formItem.password==null" @click.native="login" type="primary">登录</x-button>
    </div>
  </div>
</template>

<script>
import { XButton,XInput,Group } from 'vux'
export default {
    components: {XButton,XInput,Group},
    mixins:[componentMixin],
    data () {
        return {
            formItem:{
                userName:null,
                password:null
            }
        }
    },
    methods:{
        
        login:function(){
            app.invoke("BizAction.login",[this.formItem.userName,this.formItem.password],(token)=>{
                if(token.errCode!=0){
                    app.toast('登录失败：'+token.errMsg);
                    return;
                }
                app.setCookie('token',token.token)
                app.loadPage('/t/todo')
            })
        }
    }
}
</script>

<style scoped>

.logo {
  width: 130px;
  margin-top:40px;
}
.page{
    text-align: center;
}
</style>
