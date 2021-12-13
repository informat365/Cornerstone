<style scoped>
.vnc-frame{
    width:100%;
    height: calc(100vh - 100px);
}
</style>
<template>
    <Modal
        ref="dialog" class="full-modal" v-model="showDialog" :closable="true"
         :mask-closable="false" 
         :loading="false" 
         :title="title" 
         width="100%"  
         :footer-hide="true">
        <iframe  width="100%" :src="filePath" class="vnc-frame" frameborder="0"></iframe>
    </Modal>
</template>
<script>
    export default {
        name:"VncDialog",
        mixins: [componentMixin],
        data () {
            return {
               filePath:null,
               title:""
            }
        },
        methods:{
            pageLoad(){
                this.title=this.args.info.machineName;
                this.filePath="/webvnc.html?"+
                            "host="+this.args.info.websocketHost
                            +"&port="+this.args.info.websocketPort
                            +"&token="+this.args.info.token;
                console.log(this.filePath)
            },
        }
    }
</script>