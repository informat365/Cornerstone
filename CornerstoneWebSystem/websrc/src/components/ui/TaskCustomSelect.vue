<style scoped>
    .tag-label{
        cursor: pointer;
    }
    .check{
        color:#0097F7;
    }
     .list-nodata{
        text-align: center;
        padding-top:10px;
        padding-bottom: 10px;
        color:#999;
     }
     .item-label{
        max-width: 200px;
    }
</style>
<i18n>
{
    "en": {
        "未设置":"none",
        "暂无数据":"No Data"
    },
    "zh_CN": {
        "未设置":"未设置",
        "暂无数据":"暂无数据"
    }
}
</i18n>
<template>

<span>
    <span v-if="disabled==true">
        <TaskCustomLabel :value="selectedValue"/>
        <span class="placeholder-label" v-if="selectedValue.length==0">{{$t('未设置')}}</span>
    </span>
    <Poptip transfer v-if="disabled==false||disabled==null"  class="poptip-full" v-model="visible" :placement="placement" style="text-align:left">
        <span class="tag-label popup-select">
            <TaskCustomLabel :value="selectedValue"/>
            <span class="placeholder-label" v-if="selectedValue.length==0">{{$t('未设置')}}</span>
            <Icon style="margin-left:10px" class="popup-select-arrow" type="ios-arrow-down"></Icon>
        </span>
        <div slot="content" class="popup-box scrollbox">
            <div class="list-nodata" v-if="objectValueList.length==0">{{$t('暂无数据')}}</div>
             <Row @click.native="selectItem(item)" class="popup-item-name"  v-for="(item,idx) in objectValueList" :key="'cust'+idx">
                   <Col span="22" :title="item" class="text-no-wrap">{{item}} </Col>
                   <Col span="2" style="text-align:right">
                        <Icon v-if="isSelect(item)" type="md-checkmark" class="check"/> &nbsp;
                    </Col>
            </Row>
        </div>
    </Poptip>
</span>

</template>
<script>
export default {
    name:"TaskCustomSelect",
    props: ['placement','value','objectList','multiple','nodata','disabled'],
      data (){
        return{
            visible:false,
            selectedValue:[],
            objectValueList:[],
        }
    },
    mounted(){
        this.setupObjectValue();
        this.setupValue();
    },
    watch:{
        objectList(val){
            this.setupObjectValue();
        },
        value:function(val){
           this.setupValue();
        },
        visible:function(val){
            if(val==false){
                 if(this.multiple==true){
                    this.$emit('change',val)
                }else{
                    this.$emit('change',this.selectedValue[0])
                }
            }
        }
    },
    methods:{
        setupObjectValue(){
            this.objectValueList=[];
            if(this.nodata){
                this.objectValueList.push("无")
            }
            if(this.objectList){
                for(var i=0;i<this.objectList.length;i++){
                    this.objectValueList.push(this.objectList[i]);
                }
            }
            //旧的值无法删除
            if(this.value&&Array.isArray(this.value)){
                this.value.forEach(item=>{
                    if(!Array.contains(this.objectList,item,k=>k)){
                        this.objectValueList.push(item)
                    }
                })
            }
        },
        fireChanged(){
            if(this.multiple==true){
                this.$emit('input',this.selectedValue)
            }else{
                this.$emit('input',this.selectedValue[0])
            }
        },
        setupValue(){
            this.selectedValue=[];
            if(this.multiple==true){
                if(this.value!=null&&this.value.length){
                    for(var i=0;i<this.value.length;i++){
                        this.selectedValue.push(this.value[i]);
                    }
                }
            }else{
                if(this.value!=null){
                    this.selectedValue.push(this.value)
                }
            }
        },
        isSelect(item){
            if(this.selectedValue==null){
                return false;
            }
            for(var i=0;i<this.selectedValue.length;i++){
                if(this.selectedValue[i]==item){
                    return true;
                }
            }
            return false;
        },
        selectItem(item){
            if(this.selectedValue==null){
                this.selectedValue=[];
            }
            if(this.multiple==true){
                 for(var i=0;i<this.selectedValue.length;i++){
                    if(this.selectedValue[i]==item){
                        this.selectedValue.splice(i,1);
                        this.fireChanged();
                        return;
                    }
                }
                this.selectedValue.push(item);
                this.fireChanged();
            }else{
                if(this.selectedValue!=null&&this.selectedValue.length==1&&this.selectedValue[0]==item){
                    this.selectedValue=[];
                    this.visible=false;
                }else{
                    this.selectedValue=[];
                    this.selectedValue.push(item);
                    this.visible=false;
                }
                this.fireChanged();
            }
        }
    }
}
</script>
