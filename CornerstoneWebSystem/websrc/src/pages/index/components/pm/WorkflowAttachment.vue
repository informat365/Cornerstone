<style scoped>
.not-set-label{
    font-size: 14px;
    color: rgb(153, 153, 153);
}
.workflow-attachment-select{
    display: flex;
    align-items: center;
    flex-wrap: wrap;
}
.ivu-form-item-error .workflow-attachment-select{
    border:1px solid #ed4014;
}
</style>

<i18n>
    {
    "en": {
         "添加": "Add",
        "未上传": "none",
        "只能上传一个文件": "Only one file can be uploaded"
    },
    "zh_CN": {
        "添加": "添加",
        "未上传": "未上传",
        "只能上传一个文件": "只能上传一个文件"
    }
    }
</i18n>

<template>
<div class="workflow-attachment-select clickable">
    <ColorTag @click="previewAttachment(item.id)" style="margin-bottom:5px"
        v-for="item in list"
        :key="'o'+item.id"
        :closable="!disabled" @on-close="removeFromList(list,item)">
        <Icon type="md-document" />{{item.name}}
    </ColorTag>
        <Poptip v-if="!disabled" transfer placement="bottom">
            <Button  icon="md-attach" type="dashed" size="small">{{$t('添加')}}</Button>
            <FileUploadView style="padding:10px;width:450px" :multiple="countType=='multiple'" @change="uploadSuccess" slot="content"></FileUploadView>
        </Poptip>
        <span class="not-set-label" v-if="disabled==true&&list.length==0">{{$t('未上传')}}</span>
</div>
</template>
<script>
export default {
    props:['value','countType','disabled','clickable'],
    data() {
        return {
            list:[]
        };
    },
    watch:{
        value(val){
            this.setupValue();
        }
    },
    mounted(){
        this.setupValue();
    },
    methods: {
        previewAttachment(id){
            var canPreivew=app.previewFile(id,true);
            if(canPreivew==false){
                app.downloadFile(id);
            }
        },
        setupValue(){
            this.list=[];
            if(this.value!=null){
                this.value.forEach(item=>{
                    this.list.push(item);
                })
            }
        },
        uploadSuccess(uuid){
            if (this.countType=='single'&&this.list.length>=1){
                app.toast($t('只能上传一个文件'));
                return;
            }
            var item={
                id:uuid.uuid,
                name:uuid.name
            }
            this.list.push(item);
            this.$emit('input',this.list)
        },
        removeFromList(list,item){
             for(var i=0;i<list.length;i++){
                var t=list[i];
                if(t.id==item.id){
                   list.splice(i,1);
                   this.$emit('input',this.list)
                   return;
                }
            }
        },
    }
};
</script>
