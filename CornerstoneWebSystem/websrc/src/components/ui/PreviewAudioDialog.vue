<style scoped>

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
        ref="dialog" v-model="showDialog" :closable="true"
         :mask-closable="false"
         :loading="false"
         :title="$t('预览')" width="400"
         :footer-hide="true">

        <div v-if="filePath" style="text-align:center">
            <audio controls autoplay :src="filePath"></audio>
        </div>

    </Modal>
</template>
<script>
    export default {
        name:"PreviewAudioDialog",
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
