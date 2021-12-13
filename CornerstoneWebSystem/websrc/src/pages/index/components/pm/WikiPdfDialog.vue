<style scoped>
.pdf-frame{
    width:100%;
    flex:1;
}
</style>
<template>
    <Modal
        ref="dialog" class="full-modal" v-model="showDialog" :closable="true"
         :mask-closable="false" 
         :loading="false" 
         :title="title" 
         width="1000"  
         :footer-hide="true">
        <iframe  width="100%" v-if="filePath!=null" :src="filePath" class="pdf-frame" frameborder="0"></iframe>
    </Modal>
</template>
<script>
    export default {
        name:"WikiPdfDialog",
        mixins: [componentMixin],
        data () {
            return {
               filePath:null,
               title:"",
            }
        },
        methods:{
            pageLoad(){
                this.title=this.args.title;
                this.filePath=app.serverAddr+'/p/file/download_wiki/'+this.args.uuid+"?token="+app.token;
            },
        }
    }
</script>