<style scoped>

</style>
<i18n>
    {
    "en": {
    "绑定企业微信": "Bind WeChat",
    "请使用企业微信扫描": " Please use Work WeChat to scan the QR code below. After binding the Work WeChat, you can open the system without login in the Work WeChat.",
    "绑定成功": "Binding completed",
    "确定": "OK"
    },
    "zh_CN": {
    "绑定企业微信": "绑定企业微信",
    "请使用企业微信扫描": " 请使用企业微信扫描下方的二维码，绑定企业微信后，可以在企业微信内可免登陆打开系统。",
    "绑定成功": "绑定成功",
    "确定": "确定"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog"
        v-model="showDialog"
        :closable="true"
        :mask-closable="false"
        :loading="false"
        :title="$t('绑定企业微信')"
        width="500">
        <Alert>
            {{$t('请使用企业微信扫描')}}
        </Alert>
        <div v-if="isBind===false" style="padding: 20px 0;"></div>
        <div v-else style="height:300px;text-align:center;font-size:20px;padding-top:30px">
            {{$t('绑定成功')}}
        </div>
        <div slot="footer">
            <Button @click="confirm" type="default" size="large">{{$t('确定')}}</Button>
        </div>
    </Modal>
</template>


<script>
    export default {
        mixins: [componentMixin],
        data() {
            return {
                qrcodeURL: null,
                isBind: false,
            };
        },
        beforeDestroy() {
            clearInterval(this.intervalId);
            app.postMessage('account.edit');
            if (this.$refs.wxqrcode) {
                this.$refs.wxqrcode.innerHTML = '';
            }
        },
        methods: {
            pageLoad() {
                this.isWeixinBind(this.genQRcode);
                this.intervalId = setInterval(() => {
                    this.isWeixinBind();
                }, 3000);
            },
            popupCenter ({ url, title, w, h }){
                const dualScreenLeft = window.screenLeft !== undefined ? window.screenLeft : window.screenX;
                const dualScreenTop = window.screenTop !== undefined ? window.screenTop : window.screenY;

                const width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
                const height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;

                const systemZoom = width / window.screen.availWidth;
                const left = (width - w) / 2 / systemZoom + dualScreenLeft;
                const top = (height - h) / 2 / systemZoom + dualScreenTop;
                const newWindow = window.open(url, title,
                    `toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no
                            ,width=${ w / systemZoom },height=${ h / systemZoom },top=${ top },left=${ left }`,
                );
                if (window.focus) newWindow.focus();
            },
            genQRcode() {
                this.qrcodeURL = `${ app.serverAddr }/p/qywx/get_bind_url?token=${ app.token }`;
                this.popupCenter({
                    url: this.qrcodeURL,
                    title: '绑定企业微信',
                    w: 500,
                    h: 600,
                });
            },
            isWeixinBind(callback) {
                app.invoke('QywxAction.isBind', [app.token], result => {
                    this.isBind = result === true;
                    if (callback) {
                        callback();
                    }
                });
            },
            confirm() {
                this.showDialog = false;
            },
        },
    };
</script>
