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
        <TaskStatus v-if="selectedStatus" :label="selectedStatus.name" :color="selectedStatus.color" ></TaskStatus>
        <span class="placeholder-label" v-if="selectedStatus==null">{{$t('未设置')}}</span>
    </span>

    <Poptip transfer v-if="disabled==null||disabled==false" class="poptip-full" v-model="visible" :placement="placement" style="text-align:left">
        <span class="popup-select">
            <TaskStatus v-if="selectedStatus" :label="selectedStatus.name" :color="selectedStatus.color" ></TaskStatus>
            <span class="placeholder-label" v-if="selectedStatus==null">{{$t('未设置')}}</span>
            <Icon style="margin-left:10px" class="popup-select-arrow" type="ios-arrow-down"></Icon>
        </span>
        <div slot="content" class="popup-box scrollbox">
             <Row @click.native="selectedValue=item.id;visible=false;" class="popup-item-name"  v-for="item in statusList" :key="item.id">
                   <Col span="18"><TaskStatus :label="item.name" :color="item.color"></TaskStatus> </Col>
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
    name:"TaskAlterationStatusSelect",
    props: ['placement','value','statusList','disabled'],
      data (){
        return{
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
            console.error(val)
        },
        visible:function(val){
            if(val==false){
                this.$emit('change',this.selectedValue)
            }
        },
    },
    computed:{
        selectedStatus:function(){
            if(this.statusList==null){
                return null;
            }
            for(var i=0;i<this.statusList.length;i++){
                if(this.statusList[i].id==this.selectedValue){
                    return this.statusList[i];
                }
            }
            return null;
        },
    },
    methods:{

    }
}
</script>
