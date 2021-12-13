<style scoped>
.lark-iframe{
    width:100%;
    height:500px;
    border: none;
}
</style>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" 
        :footer-hide="true"
        title="绑定飞书" width="700" >

        <iframe class="lark-iframe" :src="larkURL">

        </iframe>
    </Modal>
</template>


<script>
export default {
mixins: [componentMixin],
    data () {
        return {
            larkURL:null
        }
    },
    beforeDestroy(){
        app.postMessage('account.edit');
    },
    methods: {
        pageLoad(){
            app.invoke("LarkAction.getSsoUrl",[app.token],info => {
                this.larkURL=info;
            });
        }
    }
}
</script>