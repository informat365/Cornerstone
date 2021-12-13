<style scoped>
.confirm-content{
    word-break: break-all;
    font-size:15px;
    font-weight:bold;
    color:#444;
    padding:20px;
    line-height:1.8;
}
</style>
<i18n>
{
    "en": {
        "确认":"Confirm?",
        "需要":"Require",
        "不需要":"Not require",
        "确定":"OK",
        "取消":"Cancel"
    },
    "zh_CN": {
        "确认":"确认",
        "需要":"需要",
        "不需要":"不需要",
        "确定":"确定",
        "取消":"取消"
    }
}
</i18n>
<template>
    <Modal
        v-model="showDialog"
        :closable="false"
        :mask-closable="false"
        :title="$t('确认')"
        width="500"
        :loading="false">
        <p class="confirm-content">{{args.message}}</p>
        <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right">
                <Button @click="cancel" type="text" size="large" style="margin-right:5px" >{{$t(args.cancelText||'取消')}}</Button>
                <Button @click="confirm" type="default" size="large" >{{$t(args.confirmText||'确定')}}</Button>
            </Col>
        </Row>
    </div>
    </Modal>
</template>
<script>
    export default {
        name:"ConfirmDialog",
        mixins: [componentMixin],
        methods: {
            confirm () {
                this.showDialog=false;
                if(this.args.callback){
                    this.args.callback();
                }
            },
            cancel(){
                this.showDialog=false;
                if(this.args.cancel){
                    this.args.cancel();
                }
            }
        }
    }
</script>
