<style scoped>
    .upload-box{
        min-width:300px;
        width:100%;
        display: inline-block;
        text-align: left;
        padding:10px;
    }
</style>
<i18n>
{
    "en": {
        "点击或者将文件拖动到这里上传":"Click or drag the file to upload here",
        "文件格式错误,可选格式为":"File format is incorrect. The optional format is ",
        "最大可上传100M的文件":"Up to 100M files can be uploaded",
        "上传错误":"Upload error"
    },
    "zh_CN": {
        "点击或者将文件拖动到这里上传":"点击或者将文件拖动到这里上传",
        "文件格式错误,可选格式为":"文件格式错误,可选格式为:",
        "最大可上传100M的文件":"最大可上传100M的文件",
        "上传错误":"上传错误"
    }
}
</i18n> 
<template>
<div>
    <div class="upload-box">
        <Upload
                            :format="format"
                            :multiple="false"
                            :show-upload-list="true"
                            :on-success="handleUploadSuccess"
                            :max-size="maxSize"
                            :on-error="onError"
                            :on-format-error="handleUploadFormatError"
                            :on-exceeded-size="handleUploadMaxSize"
                            :action="uploadServerAddr"
                            type="drag"
        >
        <div style="padding: 20px 0">
            <Icon type="ios-cloud-upload" size="52" style="color: #3399ff"></Icon>
            <p>{{$t('点击或者将文件拖动到这里上传')}}</p>
        </div>
    </Upload>
    </div>
</div>
</template>
<script>
    export default {
        name:"ScpFileUploadView",
        props:['format','object'],
        data(){
            return{
                 uploadServerAddr: app.serverAddr+'/p/file/scp_upload_file',
                 maxSize:1024*1024*100
            }
        },
        methods: {
            setAddr(addr){
                this.uploadServerAddr=addr;
            },
            handleUploadMaxSize:function(){
                app.toast(this.$t("最大可上传100M的文件"));
            },
            handleUploadSuccess(obj){
                this.$emit('change')
            },
            handleUploadFormatError:function(){
                app.toast(this.$t('文件格式错误,可选格式为')+this.format)
            },
            onError:function(){
                app.toast(this.$t('上传错误'))
            },
        }
    }
</script>