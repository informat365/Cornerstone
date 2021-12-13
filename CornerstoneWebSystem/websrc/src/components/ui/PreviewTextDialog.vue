<style scoped>
.content-box{
    white-space: pre;
    padding:15px;
    color:#222;
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
         :loading="false"
          :draggable="true"
         :title="$t('预览')" width="1000"
         :footer-hide="true">

        <div class="content-box">{{content}}</div>
    </Modal>
</template>
<script>
    export default {
        name:"PreviewTextDialog",
        mixins: [componentMixin],
        data () {
            return {
               filePath:null,
               content:null,
            }
        },
        methods:{
            pageLoad(){
                var path=app.serverAddr+'/p/file/get_file/'+this.args.uuid+"?token="+app.token;
                ajax().get(path).always( (response, xhr)=>{
                    this.content=response;
                })
            },
        }
    }
</script>
