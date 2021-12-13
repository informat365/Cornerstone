<style scoped>

</style>
<i18n>
{
    "en": {
        "修改手机": "Change mobile",
        "手机号码": " mobile",
        "发送验证码": "Send verification code",
        "验证码": "verification code",
        "重新发送": "Resend",
        "确定": "OK",
        "验证码发送成功":"verification code was sent",
        "修改成功":"Operation completed"
    },
    "zh_CN": {
        "修改手机": "修改手机",
        "手机号码": " 手机号码",
        "发送验证码": "发送验证码",
        "验证码": "验证码",
        "重新发送": "重新发送",
        "确定": "确定",
        "验证码发送成功":"验证码发送成功",
        "修改成功":"修改成功"
    }
}
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('修改手机')" width="500" >

    <Form   ref="form" :rules="formRule" :model="formItem" label-position="top" style="padding:30px;margin-bottom:10px">
        <FormItem :label="$t('手机号码')" prop="mobileNo">
            <Row>
                <Col span="18"><Input v-model.trim="formItem.mobileNo" ></Input></Col>
                <Col span="6"><Button :disabled="sendCodeTimeout>0" @click="sendVerifyCode()" style="margin-left:10px"  type="default">
                    <template v-if="sendCodeTimeout==0">{{$t('发送验证码')}}</template>
                    <template v-if="sendCodeTimeout>0">{{$t('重新发送')}} ({{sendCodeTimeout}})</template>

                </Button></Col>
            </Row>
        </FormItem>

         <FormItem :label="$t('验证码')" prop="code">
            <Input style="width:100px" v-model.trim="formItem.code" ></Input>
        </FormItem>
    </Form>

    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> <Button @click="changeMobile" type="default" size="large" >{{$t('确定')}}</Button></Col>
         </Row>
    </div>

    </Modal>
</template>


<script>
export default {
        mixins: [componentMixin],
        data () {
            return {
                formItem:{
                   mobileNo:null,
                   code:null,
                },
                formRule:{
                    mobileNo:[vd.req,vd.mobile],
                },
                sendCodeTimeout:0,
            }
        },
        watch:{
            showDialog(val){
                if(val==false&&this.timeoutInterval){
                    clearInterval(this.timeoutInterval);
                }
            }
        },
        methods: {
            pageLoad(){

            },
            startCodeInterval(){
                this.timeoutInterval=setInterval(()=>{
                    this.sendCodeTimeout--;
                    if(this.sendCodeTimeout<=0){
                        this.sendCodeTimeout=0;
                        clearInterval(this.timeoutInterval);
                        this.timeoutInterval=null;
                    }
                },1000);
            },
            sendVerifyCode0(){
                app.invoke('BizAction.sendMobileVerifyCode',[app.token, this.formItem.mobileNo],(info)=>{
                    app.toast(this.$t('验证码发送成功'));
                    this.sendCodeTimeout=60;
                    this.startCodeInterval();
			    })
            },
            sendVerifyCode(){
                this.$refs.form.validate((r)=>{if(r)this.sendVerifyCode0()});
            },
            changeMobile(){
                this.$refs.form.validate((r)=>{if(r)this.confirmChangeMobile()});
            },
            confirmChangeMobile(){
                app.invoke('BizAction.bindNewMobile',[app.token,
                    this.formItem.mobileNo,
                    this.formItem.code],(info)=>{
                    app.toast(this.$t('修改成功'));
                    this.showDialog=false;
			    })
            }
        }
    }
</script>
