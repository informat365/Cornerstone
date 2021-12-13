<style scoped>
.desc-info{
        font-size:12px;
        color:#999;
}
.field-required{
    color:tomato;
}
</style>
<i18n>
{
    "en": {
        "对象类型": "Object type",
        "设置对象的可见性":"Setting Object Visibility",
        "可见性设置":"Visibility Setting",
        "所有项目成员均可以查看":"All member",
        "只有创建人和责任人可以查看":"Only creater and owner",
        "角色可见性":"Role",
        "以上成员角色可以查看所有数据":"All data can be viewed by the above member roles",
        "保存":"Save",
        "保存成功":"Success"
    },
    "zh_CN": {
        "对象类型": "对象类型",
        "设置对象的可见性":"设置对象的可见性",
        "可见性设置":"可见性设置",
        "所有项目成员均可以查看":"所有项目成员均可以查看",
        "只有创建人和责任人可以查看":"只有创建人和责任人可以查看",
        "角色可见性":"角色可见性",
        "以上成员角色可以查看所有数据":"以上成员角色可以查看所有数据",
        "保存":"保存",
        "保存成功":"保存成功"
    }
}
</i18n> 
<template>
    <div>
      <Form label-position="top">
        <FormItem :label="$t('对象类型')">
            <div> 
                <RadioGroup v-model="objectType">
                    <Radio v-for="item in moduleList" v-if="item.objectType>0" 
                        :key="item.objectType"
                        :label="item.objectType">{{item.name}}</Radio>
                </RadioGroup>
            </div>
            <div class="desc-info">{{$t('设置对象的可见性')}}</div>
        </FormItem>
       
        <FormItem :label="$t('可见性设置')">
            <i-Switch :disabled="disabled" v-model="module.isPublic"/>
            <div class="desc-info" v-if="module.isPublic">{{$t('所有项目成员均可以查看')}}</div>
            <div class="desc-info" v-if="module.isPublic==false">{{$t('只有创建人和责任人可以查看')}}</div>
        </FormItem>

        <FormItem  :label="$t('角色可见性')" v-if="module.isPublic==false">
            <CheckboxGroup :disabled="disabled" v-model="module.publicRoles" >
                    <Checkbox v-for="item in roleList" :key="item.id" :label="item.id">{{item.name}}</Checkbox>
            </CheckboxGroup>
            <div style="color:#666;font-size:12px">{{$t('以上成员角色可以查看所有数据')}}</div>
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
        moduleList:[],
        roleList:[],
        module:{},
        disabled:false
    }
  },  
  mounted(){
      this.loadModuleList();
      this.loadRole();
      this.disabled = this.project&&this.project.isFinish;
  },
  watch:{
      "objectType":function(val){
          this.loadData();
      }
  },
  methods:{
    loadRole(){
        app.invoke('BizAction.getRoleInfoList',[app.token,1],(list)=>{
            this.roleList=list;
        })
    },
    loadData(){
        for(var i=0;i<this.moduleList.length;i++){
             var t=this.moduleList[i];
             if(t.objectType==this.objectType){
                 this.module=t;
             }
        }
    },
    loadModuleList(){
        app.invoke('BizAction.getProjectModuleInfoList',[app.token,this.project.id],(list)=>{
                this.moduleList=list;
                for(var i=0;i<this.moduleList.length;i++){
                   var t=this.moduleList[i];
                   if(t.objectType>0){
                       this.objectType=t.objectType;
                       this.module=t;
                       break;
                   }
                }
        })
    },
    confirm(){
        app.invoke('BizAction.updateProjectModulePublicInfo',[app.token,this.module],(list)=>{
            app.toast(this.$t('保存成功'))
        })
    },
  }
}
</script>