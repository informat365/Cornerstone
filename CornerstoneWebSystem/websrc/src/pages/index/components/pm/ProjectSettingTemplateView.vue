<style scoped>
.desc-info{
    font-size:12px;
    color:#999;
}
</style>
<i18n>
{
    "en": {
        "对象类型": "Object type",
        "如果设置了模板":"If a template is set, it will be used as content by default when creating objects",
        "名称":"Name",
        "详细描述":"Content",
        "保存":"Save",
        "正在上传图片中，请稍后":"Uploading,please wait a moment.",
        "保存成功":"Success"
    },
    "zh_CN": {
        "对象类型": "对象类型",
        "如果设置了模板":"如果设置了模板，创建对象时会默认将模板作为内容",
        "名称":"名称",
        "详细描述":"详细描述",
        "保存":"保存",
        "正在上传图片中，请稍后":"正在上传图片中，请稍后",
        "保存成功":"保存成功"
    }
}
</i18n>
<template>
    <div>
      <Form label-position="top" ref="form" :model="formItem" :rules="formRule" >
        <FormItem :label="$t('对象类型')">
            <div>
                <RadioGroup v-model="objectType">
                    <Radio v-for="item in moduleList" v-if="item.objectType>0"
                            :key="item.objectType" :label="item.objectType">{{item.name}}</Radio>
                </RadioGroup>
            </div>
            <div class="desc-info">{{$t('如果设置了模板')}}</div>
        </FormItem>

        <FormItem :label="$t('名称')" prop="name">
             <Input :disabled="disabled" v-model.trim="formItem.name" ></Input>
        </FormItem>

         <FormItem :label="$t('详细描述')">
             <RichtextEditor  ref="editor" v-model="formItem.content"></RichtextEditor>
        </FormItem>

        <FormItem label="" v-if="!disabled">
           <Button @click="confirm" type="default">{{$t('保存')}}</Button>
        </FormItem>

    </Form>

    </div>
</template>

<script>
export default {
    mixins: [componentMixin],
    props:['project'],
    data(){
        return {
            objectType:null,
            dataList:[],
            moduleList:[],
            formItem:{
                id:0,
                name:null,
                content:null
            },
            formRule:{
                name:[vd.name,vd.req],
            },
            disabled:false
        }
    },
    mounted(){
        this.loadModuleList();
        this.disabled = this.project&&this.project.isFinish;
    },
    watch:{
        "objectType":function(val){
            this.loadData();
        }
    },
    methods:{
        loadData(){
            this.formItem.objectType=this.objectType;
            app.invoke('BizAction.getProjectObjectTypeTemplateByProjectIdObjectType',[app.token,this.project.id,this.objectType],(info)=>{
               if(info!=null){
                   this.formItem=info;
               }else{
                   this.formItem={
                       name:null,
                       content:null,
                       objectType:this.objectType
                   }
               }
            })
        },
        loadModuleList(){
            app.invoke('BizAction.getProjectModuleInfoList',[app.token,this.project.id],(list)=>{
                this.moduleList=list;
                for(var i=0;i<this.moduleList.length;i++){
                   var t=this.moduleList[i];
                   if(t.objectType>0){
                       this.objectType=t.objectType;
                       break;
                   }
                }
            })
        },
        confirm(){
            this.$refs.form.validate((r)=>{
                if(r){
                    this.saveTemplate()
                }
            });
        },
        saveTemplate(){
             if(this.$refs.editor.isUploading()){
                app.toast(this.$t('正在上传图片中，请稍后'));
                return;
            }
            this.formItem.content=this.$refs.editor.getValue();
            this.formItem.projectId=this.project.id;
            app.invoke('BizAction.saveProjectObjectTypeTemplate',[app.token,this.formItem],(info)=>{
                app.toast(this.$t('保存成功'))
			})
        }
    }
}
</script>
