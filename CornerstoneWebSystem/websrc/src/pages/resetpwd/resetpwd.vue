<style >
        .app{
          text-align: center;
        }
        .main-box{
          display: inline-block;
          text-align: left;
          width:340px;
          margin-top:150px;
        }
        .active-box{
          display: inline-block;
          text-align: left;
          width:340px;
          margin-top:150px;
        }
        .input-tips{
          font-size:12px;
          padding-top:3px;
          color:#666;
        }
        .tips-error{
          color:coral;
        }
        .verify-code-box{
          position: absolute;
          top:21px;
          right:1px;
        }
        .kaptcha-box {
            position: relative;
            overflow: hidden;
        }
        .verify-code-box img{
          width:100px;
          height:48px;
        }
        .code-input{
          display: inline-block;
          width:60px;
          height:60px;
          text-align: center;
          font-weight: bold;
        }
        .link{
            color:#999;
        }
        .logo-image{
            width:250px;
        }
        .send-code-btn{
            position:absolute;
            right:10px;
            top:35px;
            font-size: 13px;
            color:#666;
            cursor: pointer;
        }
        .error-message{
            margin-top:10px;
            color: coral;
        }
</style>
<template>
<div>
   <div class="app" >
        <div class="main-box" >
                <div style="text-align:center;">
                        <img src="/image/logo.png" class="logo-image">
                    </div>
          <div style="position: relative;">
            <input class="forminput" @input="checkForm()" v-model="formItem.mobileNo" placeholder="手机号码"/>
            <div v-if="codeCutdown==0" @click="enableShowKaptcha" class="send-code-btn">发送验证码</div>
            <div v-if="codeCutdown>0" class="send-code-btn">{{codeCutdown}}</div>
            <div v-if="formItemError.mobileNo!=null" class="input-tips tips-error">{{formItemError.mobileNo}}</div>
          </div>
            <div v-if="showKaptcha">
                <div class="kaptcha-box">
                    <input class="forminput" @input="checkForm" v-model="kaptchaCode" placeholder="验证码"/>
                    <div @click="enableShowKaptcha" class="send-code-btn" style="top: 25px !important;">
                        <img :src="'/p/main/kaptcha?sign=' + kaptchaSign"/>
                    </div>

                </div>
                <div class="error-message" v-if="errorMessage">{{errorMessage}}</div>
                <div><button :disabled="!kaptchaCode || 4 !== kaptchaCode.length"
                             @click="sendVerifyCode" class="btn-block btn-primary">发送验证码</button></div>
            </div>
            <div v-else>
                <div>
                    <input class="forminput" @input="checkForm" v-model="formItem.code" placeholder="验证码"/>
                    <div v-if="formItemError.code!=null" class="input-tips tips-error">{{formItemError.code}}</div>
                </div>

                <div>
                    <input class="forminput" type="password" @input="checkForm" v-model="formItem.password" placeholder="密码"/>
                    <div v-if="formItemError.password!=null" class="input-tips tips-error">{{formItemError.password}}</div>
                </div>

                <div>
                    <input class="forminput" type="password" @input="checkForm" v-model="formItem.passwordRepeat" placeholder="确认密码"/>
                    <div v-if="formItemError.passwordRepeat!=null" class="input-tips tips-error">{{formItemError.passwordRepeat}}</div>
                </div>
                <div class="error-message" v-if="errorMessage">{{errorMessage}}</div>
                <div><button :disabled="!validateSuccess"
                             @click="resetPassword" class="btn-block btn-primary">重置密码</button></div>
            </div>
    </div>
    </div>
</div>

</template>
<script>

export default {

  data() {
    return {
            kaptchaSign: null,
            kaptchaCode: null,
            showKaptcha: false,
            serverAddr:serverAddr,
            ready:false,
            errorMessage:null,
            formItem:{
              mobileNo:null,
              code:null,
              password:null,
              passwordRepeat:null,
            },
            formItemError:{
              mobileNo:null,
              code:null,
              password:null,
              passwordRepeat:null,
            },
            validateSuccess:false,
            codeCutdown:0,
    }
  },
  mounted() {
  },
  methods: {
      // 刷新验证码
      refreshKaptchaCode() {
          this.kaptchaSign = getUuId();
      },
      checkForm(){
                this.validateSuccess=true;
                var mobileRegex=/^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\d{8}$/;
                if(this.formItem.mobileNo!=null){
                  if(this.formItem.mobileNo.match(mobileRegex)){
                    this.formItemError.mobileNo=null;
                  }else{
                    this.formItemError.mobileNo="请输入正确的手机号码";
                    this.validateSuccess=false;
                  }
                }else{
                  this.validateSuccess=false;
                }
                //
                if(this.formItem.password!=null){
                  var passwordRegex=/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$/
                  if(this.formItem.password.match(passwordRegex)){
                    this.formItemError.password=null;
                  }else{
                    this.formItemError.password="密码为8-16位的数字和字母的组合";
                    this.validateSuccess=false;
                  }
                }else{
                  this.validateSuccess=false;
                }
                //
                if(this.formItem.passwordRepeat!=null){
                  if(this.formItem.password==this.formItem.passwordRepeat){
                    this.formItemError.passwordRepeat=null;
                  }else{
                    this.formItemError.passwordRepeat="两次输入的密码不一致";
                    return false;
                  }
                }else{
                  this.validateSuccess=false;
                }
                if(this.formItem.code==null){
                  this.validateSuccess=false;
                }
                //
            },
            startCodeCutdown(){
                this.cutdownInterval=setInterval(()=>{
                    this.codeCutdown--;
                    if(this.codeCutdown<=0){
                        this.codeCutdown=0;
                        clearInterval(this.cutdownInterval);
                    }
                },1000)
            },
      // 打开发送验证码
      enableShowKaptcha () {
          this.errorMessage = '';
          this.kaptchaCode = null;
          if (!this.formItem.mobileNo || this.formItem.mobileNo.length !== 11 || this.formItem.sendCodeCountDown > 0) {
              return;
          }
          // 刷新验证码
          this.refreshKaptchaCode();
          this.showKaptcha = true;
      },
            sendVerifyCode(){
                this.errorMessage=null;
                if (!this.kaptchaCode || 4 !== this.kaptchaCode.length) {
                    return;
                }
                if(this.formItem.mobileNo==null){
                    return;
                }
                ajaxInvoke(this.serverAddr+'/p/api/invoke/',
                'BizAction.sendMobileVerifyCodeForResetPassword',
                    [this.formItem.mobileNo, this.kaptchaSign, this.kaptchaCode],()=>{
                    this.codeCutdown=60;
                    this.startCodeCutdown();
                    this.showKaptcha = false;
                },
                  (code,msg)=>{
                      this.refreshKaptchaCode();
                      this.errorMessage=msg;
                  }
                )
            },
            resetPassword(){
                this.errorMessage=null;

                ajaxInvoke(this.serverAddr+'/p/api/invoke/','BizAction.resetPassword',[
                    this.formItem.mobileNo,
                    this.formItem.password,
                    this.formItem.code],(t)=>{
                        this.errorMessage="重置成功";
                        setTimeout(()=>{
                            window.location.href="/login.html";
                        },2000)
                  },
                  (code,msg)=>{
                    this.errorMessage=msg;
                  }
                )
            },
  }
};
</script>
