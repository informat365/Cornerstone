<style scoped>
    .row{
        margin-bottom: 10px;
    }
</style>
<i18n>
{ 
    "en": {
        "选择项目":"Project",
        "类型":"Type",
        "创建":"Create"
    },
    "zh_CN": {
        "选择项目":"选择项目",
        "类型":"类型",
        "创建":"创建"
    }
   
}
</i18n>

<template>
  <Poptip transfer ref="selectPoptip" class="poptip-full" trigger="click">
    <IconButton icon="md-add" tips="快速创建"  :size="20"></IconButton>
    <div slot="content" style="width:270px;padding:20px">
      
        <div class="row">
             <Select transfer  v-model="formItem.projectId" :placeholder="$t('选择项目')" style="width:100%">
                <Option v-for="item in projectList" :value="item.id" :key="'prj'+item.id">{{ item.name }}</Option>
            </Select>
        </div>
        <div  class="row">
             <Select  transfer clearable v-model="formItem.objectType" :placeholder="$t('类型')" style="width:100%">
                <template v-for="item in moduleList">
                    <Option   :value="item.objectType" :key="'md'+item.id">{{ item.name }}</Option>
                </template> 
            </Select>
        </div>
        
        <div class="row" style="text-align:center;margin-top:20px">
            <Button class="create-btn" :disabled="formItem.objectType==null" @click="showCreateDialog" type="default" icon="md-add">
                {{$t('创建')}}
            </Button>
        </div>
        
    </div>
  </Poptip>
</template>

<script>
export default {
    mixins: [componentMixin],
    props:['list'],
    data() {
        return {
            projectList:[],
            moduleList:[],
            formItem:{
                projectId:null,
                objectType:null
            },
        };
    },
    mounted() {
        this.loadProjectList();
    },
    watch:{
        "formItem.projectId":function(val){
            this.loadProjectModule();
        },
        list(val){
            this.loadProjectList();
        }
    },
    methods: {
        loadProjectList(){
            this.projectList=[];
            this.list.forEach(element => {
                if(!element.isFinish){
                    this.projectList.push(element);
                }
            });
        },
        showCreateDialog(){
            this.$refs.selectPoptip.ok();
            app.showDialog(TaskCreateDialog,{
                projectId:this.formItem.projectId,
                type:this.formItem.objectType
            })
        },
        loadProjectModule(){
            this.formItem.objectType=null;
            app.invoke('BizAction.getProjectModuleInfoList',[app.token,this.formItem.projectId],(list)=>{
                this.moduleList=[];
                list.forEach(item=>{
                    if(item.objectType>0){
                        this.moduleList.push(item);
                    }
                })
                if(this.moduleList.length>0){
                    this.formItem.objectType=this.moduleList[0].objectType;
                }
            })
        },
    }
};
</script>