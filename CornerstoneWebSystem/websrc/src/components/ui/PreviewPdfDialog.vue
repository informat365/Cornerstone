<style scoped>
.pdf-frame{
    width:100%;
    flex:1;
}
</style>
<i18n>
{
    "en": {
        "预览":"Preview"
    },
    "zh_CN": {
        "预览":"预览"
    }
}
</i18n>
<template>
    <Modal
        ref="dialog" class="full-modal" v-model="showDialog" :closable="true"
         :mask-closable="false"
          :draggable="true"
         :loading="false"
         :title="$t('预览')"
         width="1000"
         :footer-hide="true">
        <iframe  width="100%" v-if="filePath!=null" :src="filePath" class="pdf-frame" frameborder="0"></iframe>
    </Modal>
</template>
<script>
    export default {
        name:"PreviewPdfDialog",
        mixins: [componentMixin],
        data () {
            return {
               filePath:null,
            }
        },
        methods:{
            pageLoad(){
                this.filePath=app.serverAddr+'/p/file/get_file/'+this.args.uuid+"?token="+app.token+"&inline=true"
            },
        }
    }
</script>
