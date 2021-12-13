<style scoped>
.confirm-message{
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
        "确认": "Confirm",
        "备注": "Remark",
        "取消":"Cancel",
        "确定":"OK"
    },
    "zh_CN": {
        "确认": "确认",
        "备注": "备注",
        "取消":"取消",
        "确定":"确定"
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
        <p class="confirm-message">{{args.message}}</p>
        <div style="padding:20px;padding-top:10px">
            <Input :placeholder="$t('备注')" type="textarea" :rows="3" v-model="remark" :maxlength="100"/>
        </div>
        <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> 
                <Button @click="cancel" type="text" size="large" style="margin-right:5px" >{{$t('取消')}}</Button>
                <Button @click="confirm" type="default" size="large" >{{$t('确定')}}</Button>
            </Col>
        </Row>
    </div>
    </Modal>
</template>
<script>
    export default {
        name:"WorkflowConfirmDialog",
        mixins: [componentMixin],
        data(){
            return{
                remark:null,
            }
        },
        methods: {
            confirm () {
                this.showDialog=false;
                if(this.args.confirm){
                    this.args.confirm(this.remark);
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
