<style scoped>
.remove-btn{
    position:absolute;
    top:-10px;
    right:10px;
    color:#FE526A;
    cursor: pointer;
}
.upload-image{
    width:80px;
    height:80px;
    margin-right:15px;
}
</style>
<template>
     <div style="display:flex;align-items:center">
         <div v-if="uuid&&uuid.length">
             <Tag v-if="showType!='image'" v-for="(item,idx) in uuid" :key="item.id" :name="item.id" 
             :closable="!readonly" 
             @on-close="deleteObject(idx)">{{item.name}}</Tag>  


            <div v-if="showType=='image'" style="display: flex;flex-wrap: wrap">
                <div v-for="(item,idx) in uuid" :key="item.id" :name="item.id" 
                    style="position:relative;display:inline-block">
                    <img :src="item.uuid|imgurl" class="upload-image"/>
                    <span class="remove-btn" @click="deleteObject(idx)"><Icon type="close-circled"></Icon></span>
                </div>
            </div>

        </div>      
                      
         <Upload 
                   ref="upload"
                            :show-upload-list="false"
                            :on-success="handleUploadSuccess"
                            :max-size="maxSize"
                            :on-format-error="handleUploadFormatError"
                            :format="fileFormat"
                            :on-error="onError"
                            :on-exceeded-size="handleUploadMaxSize"
                            :action="uploadServerAddr"
                            style="display: inline-block;">                        
                            <div style="padding-top:5px">
                    <Button type="ghost" icon="ios-cloud-upload-outline" size="small">上传</Button>
                    </div>
        </Upload>
        </div>
</template>
<script>
export default {
     name:"UploadFile",
    props: ['value','max-size','multiple','readonly','show-type'],
    data: function () {
        return {
            uuid:this.value,
            uploadServerAddr: app.serverAddr+'/p/file/upload_file?token='+app.token
        }
    },
    computed:{
        fileFormat:function(){
            if(this.showType=='image'){
                return ['jpg','jpeg','png'];
            }
            return null;
        }
    },
    watch:{
        uuid: function (val) {
            this.$emit('input', val);
        },
        value: function (val) {
            this.uuid = val;
        }
    },
    methods:{
        handleUploadMaxSize:function(){
            app.toast("文件太大了");
        },
        handleUploadFormatError:function(){
            app.toast('请选择图片上传')
        },
        handleUploadSuccess:function(obj){
            if(this.uuid.length!=null){
                this.uuid.push({
                    id:0,
                    uuid:obj.attachment.uuid,
                    name:obj.attachment.name
                });
            }else{
                this.uuid={ 
                    id:0,
                    uuid:obj.attachment.uuid,
                    name:obj.attachment.name
                }
            }
        },
        onError:function(){
             app.toast('上传错误')
        },
        deleteObject:function(idx){
            this.uuid.splice(idx,1);
        },
    }
}
</script>