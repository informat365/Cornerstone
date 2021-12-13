<style scoped>
    .upload-image{
        width:60px;
        height:60px;
        background-color: lightgray;
        border-radius: 50%;
    }
</style>
<template>
     <div style="display:flex;align-items:center">
                        <img :src="uuid|imageURL(placeholder)" class="upload-image" :class="imageClass"/>
                            <Upload 
                            ref="upload"
                            :show-upload-list="false"
                            :on-success="handleUploadSuccess"
                            :max-size="200*1024"
                            :on-format-error="handleUploadFormatError"
                            :format="['jpg','jpeg','png']"
                            :on-exceeded-size="handleUploadMaxSize"
                            :action="uploadServerAddr"
                            style="display: inline-block;">
                            <div style="padding-top:5px">
                    <Button style="margin-left:10px" size="small">上传</Button>
                    </div>
                    </Upload>
                </div>
</template>
<script>
export default {
    name:"UploadImage",
    props: ['value','imageClass','placeholder'],
    data() {
        return {
            uploadServerAddr: app.serverAddr+'/p/file/upload_file?token='+app.token,
            placeholderImage:null,
            uuid:this.value,
        }
    },
    watch:{
        uuid: function (val) {
            this.$emit('input', val);  
            this.$emit('change');
        },
        value: function (val) {
            this.uuid = val;
        }
    },
    filters:{
        imageURL(val,placeholder){
             if(val==null){
                if(placeholder==null){
                    return "/image/common/account-placeholder.png";
                }else{
                    return placeholder;
                }
            }
            return app.serverAddr+"/p/file/get_file/"+val+"?token="+app.token;
        }
    },
    methods:{
        handleUploadMaxSize:function(){
            app.toast("最大上传200k的文件");
        },
        handleUploadFormatError:function(){
            app.toast('请选择图片上传')
        },
        handleUploadSuccess:function(obj){
            this.uuid=obj.attachment.uuid;
        },
    }
}
</script>