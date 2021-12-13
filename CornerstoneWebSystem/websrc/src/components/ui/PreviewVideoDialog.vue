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
        :draggable="true"
        :title="$t('预览')" width="1000"
        :footer-hide="true">

        <div v-if="filePath" style="text-align:center;height:500px">
            <video width="100%" height="100%" controls autoplay :src="filePath"></video>
        </div>

    </Modal>
</template>
<script>
    export default {
        name:"PreviewVideoDialog",
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
