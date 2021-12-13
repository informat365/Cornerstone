<style scoped>
    .desc-info{
        font-size:12px;
        color:#999;
    }
    .value-row{
        padding:7px 0;
        border-bottom: 1px solid #eee;
    }
    .value-row:hover{
        background-color: #ebf7ff;
        cursor:move;
    }
</style>
<i18n>
{
    "en": {
        "对象类型": "Object type",
        "自定义优先级取值范围":"Value range",
        "优先级列表":"List",
        "默认":"Default",
        "设置":"Setting",
        "保存":"Save",
        "添加优先级":"Add",
        "保存成功":"Success"
    },
    "zh_CN": {
        "对象类型": "对象类型",
        "自定义优先级取值范围":"自定义优先级取值范围",
        "优先级列表":"优先级列表",
        "默认":"默认",
        "设置":"设置",
        "保存":"保存",
        "添加优先级":"添加优先级",
        "保存成功":"保存成功"
    }
}
</i18n>
<template>
    <div>
      <Form label-position="top">
        <FormItem :label="$t('对象类型')">
            <div>
                <RadioGroup   v-model="objectType">
                    <Radio v-for="item in moduleList" v-if="item.objectType>0" :key="item.objectType" :label="item.objectType">{{item.name}}</Radio>
                </RadioGroup>
            </div>
            <div class="desc-info">{{$t('自定义优先级取值范围')}}</div>
        </FormItem>
       
        <FormItem :label="$t('优先级列表')">
                <draggable v-model="dataList" :options="{draggable:'.value-row'}">
                <Row v-for="item in dataList" :key="item.id" class="value-row">
                    <Col span="16">
                        <div class="text-no-wrap" >
                            <Icon type="md-more" /><span :style="{color:item.color}">{{item.name}}</span>
                        </div>
                    </Col>
                    <Col span="3">
                    &nbsp;
                        <span v-if="item.isDefault">{{$t('默认')}}</span>
                    </Col>
                    <Col span="3" style="width:80px;text-align:right">
                        <IconButton @click="showPriorityEditDialog(item)" :tips="$t('设置')" icon="ios-settings-outline"></IconButton>
                    </Col>     
                </Row>
                </draggable>
               
        </FormItem>

        <div v-if="!disabled">
            <Button style="margin-right:10px" type="default" @click="updateOrder()">{{$t('保存')}}</Button>
            <IconButton @click="showPriorityEditDialog()" icon="ios-add"  :title="$t('添加优先级')"></IconButton>
        </div>

    </Form>
 
    </div>
</template>

<script>
import draggable from 'vuedraggable'
export default {
    mixins: [componentMixin],
    props:['project'],
    components: {
        draggable,
    },
    data(){
        return {
            objectType:null,
            dataList:[],
            moduleList:[],
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
            app.invoke('BizAction.getProjectPriorityDefineInfoList',[app.token,this.project.id,this.objectType],(list)=>{
                this.dataList=list;
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
        updateOrder(){
            var idList=[];
            this.dataList.forEach(item=>{
                idList.push(item.id);
            })
            app.invoke('BizAction.updateProjectPrioritySortWeight',[app.token,this.project.id,this.objectType,idList],()=>{
                app.toast(this.$t('保存成功'));
            })
        },
        showPriorityEditDialog(item){
            app.showDialog(ProjectSettingPriorityEditDialog,{
                item:item,
                project:this.project,
                objectType:this.objectType,
                callback:this.loadData
            })

        }
    }
}
</script>