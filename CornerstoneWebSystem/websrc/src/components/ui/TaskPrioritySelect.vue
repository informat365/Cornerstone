<style scoped>
    .check{
        color:#0097F7;
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
            <Icon custom="iconfont icon-youxianji" style="color:#999" :style="{color:selectedPriority.color}" size="24" />
            <TaskPriority v-if="selectedPriority.color" :label="selectedPriority.name" :color="selectedPriority.color" ></TaskPriority> 
            <span class="placeholder-label" v-if="selectedPriority.color==null">{{$t('未设置')}}</span>
        </span>

        <Poptip transfer v-if="disabled==null||disabled==false"  class="poptip-full" v-model="visible" :placement="placement" style="text-align:left">
        <span class="tag-label popup-select"> 
            <Icon custom="iconfont icon-youxianji" style="color:#999" :style="{color:selectedPriority.color}"  size="24" />
            <TaskPriority v-if="selectedPriority.color" :label="selectedPriority.name" :color="selectedPriority.color" ></TaskPriority> 
            <span class="placeholder-label" v-if="selectedPriority.color==null">{{$t('未设置')}}</span>
            <Icon style="margin-left:10px" class="popup-select-arrow" type="ios-arrow-down"></Icon> 
        </span>
        <div slot="content" class="popup-box scrollbox">
             <Row @click.native="selectedValue=item.id;visible=false;" class="popup-item-name"  v-for="item in priorityList" :key="item.id">
                   <Col span="18"><TaskPriority :label="item.name" :color="item.color"></TaskPriority> </Col>
                   <Col span="6" style="text-align:right"> 
                        <Icon v-if="item.id==selectedValue" type="md-checkmark" class="check"/> &nbsp;
                    </Col>
            </Row>
        </div>
        </Poptip>
    </span>
</template>
<script>
export default {
    name:"TaskPrioritySelect",
    props: ['placement','value','priorityList','disabled'],
    data (){
        return{
            firstValue:null,
            visible:false,
            selectedValue:this.value
        }
    },
    watch:{
        selectedValue:function(val){
            this.$emit('input',val)
        },
        value:function(val){
            this.selectedValue=val;
        },
        visible:function(val){
            if(val==false){
              this.$emit('change',this.selectedValue)
            }
        }
    },
    computed:{
        selectedPriority:function(){
            if(this.priorityList==null){
                return {};
            }
            for(var i=0;i<this.priorityList.length;i++){
                if(this.priorityList[i].id==this.selectedValue){
                    return this.priorityList[i];
                }
            }
            return {};
        }
    },
    methods:{
       
    }
}
</script>