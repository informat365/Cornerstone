<style scoped>
</style>
<i18n>
{
	"en": {
        "交付物API配置": "交付物API配置",
		"上传URL": "上传URL",
		"下载URL": "下载URL"
    },
	"zh_CN": {
		"交付物API配置": "交付物API配置",
		"上传URL": "上传URL",
		"下载URL": "下载URL"
	}
}
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('交付物API配置')" width="700"  :footer-hide="true">

    <Form ref="form" label-position="top" style="height:300px;padding:15px">
       
         <FormItem :label="$t('上传URL')">
           <span>{{uploadUrl}}</span>
        </FormItem>

         <FormItem :label="$t('下载URL')">
            <span>{{downloadUrl}}</span>
        </FormItem>

    </Form>
    </Modal>
</template>

<script>
    export default {
        mixins: [componentMixin],
        data () {
            return {
                uploadUrl:null,
                downloadUrl:null
            }
        },
        methods: {
            pageLoad(){
                this.uploadUrl=app.getHost()+"/webapi/artifacts/upload?uuid=UUID";
                this.downloadUrl=app.getHost()+"/webapi/artifacts/download?uuid=UUID";
            }
        }
    }
</script>