<style scoped>
   
</style>
<i18n>
{
    "en": {
        "绑定微信": "Bind WeChat",
        "请使用微信扫描": " Please use WeChat to scan the QR code below. After binding to WeChat, you can use WeChat to log in to the system and receive system notification.",
        "绑定成功": "Binding completed",
        "确定": "OK"
    },
    "zh_CN": {
        "绑定微信": "绑定微信",
        "请使用微信扫描": " 请使用微信扫描下方的二维码，绑定微信后，可以使用微信登录系统、接收系统通知。",
        "绑定成功": "绑定成功",
        "确定": "确定"
    }
}
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('绑定微信')" width="500" >
    <Alert>
        {{$t('请使用微信扫描')}}
    </Alert>
    <div v-if="isBind==false" style="height:300px;text-align:center">
        <img v-if="qrcodeURL!=null" :src="qrcodeURL" style="width:240px;height:240px">
    </div>
     <div v-if="isBind==true" style="height:300px;text-align:center;font-size:20px;padding-top:30px">
       
        {{$t('绑定成功')}}
    </div>
    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{$t('确定')}}</Button></Col>
         </Row>
    </div>

    </Modal>
</template>


<script>
export default {
mixins: [componentMixin],
    data () {
        return {
            qrcodeURL:null,
            isBind:false,
        }
    },
    beforeDestroy(){
        clearInterval(this.intervalId)
        app.postMessage('account.edit');
    },
    methods: {
        pageLoad(){
            this.isWeixinBind(this.genQRcode);
            this.intervalId=setInterval(()=>{
                this.isWeixinBind();
            },3000)
        },
        genQRcode(){
            app.invoke("BizAction.generateQRCode",[app.token],info => {
                this.qrcodeURL="https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+info;
            });
        },
        isWeixinBind(callback){
            app.invoke("BizAction.isWeixinBind",[app.token],result => {
                this.isBind=result;
                if(callback){
                    callback();
                }
            });
        },
        confirm(){
            this.showDialog=false;
        }
    }
}
</script>