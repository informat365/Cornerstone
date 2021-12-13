<style scoped>
    .poptip-content{
        max-height:400px;
        width:300px;
        overflow:auto;
    }
    .popup-item-name:hover .edit-btn{
        visibility: visible;
    }
    .edit-btn{
         visibility: hidden;
         margin-right:10px;
    }
    .filter-name{
        width:150px;
        display: inline-block;
        line-height: 18px;
        vertical-align: middle;
        color:#2f2e2e;
    }
     .list-nodata{
        text-align: center;
        color:#999;
        font-size:14px;
        font-weight: bold;
        padding-top:15px;
        padding-bottom:15px;
     }
     .popup-item-name-divide{
         display: flex;
         align-items: center;
     }
     
</style> 
<i18n>
{
    "en": {
        "暂无数据": "No Data",
        "创建过滤器": "Create filter"
    },
    "zh_CN": {
        "暂无数据": "暂无数据",
        "创建过滤器": "创建过滤器"
    }
}
</i18n>
<template>
   <Poptip ref="poptip" class="poptip-full" v-model="visible" :placement="placement" >
        <slot name="popup-name"></slot>
        <div slot="content" style="text-align:left;">
            <div v-if="filterList==null||filterList.length==0" class="list-nodata">{{$t('暂无数据')}}</div>
           <div class="scrollbox poptip-content">
                <Row class="popup-item-name" v-for="item in filterList" :key="item.id">
                    <Col  @click.native="selectFilter(item)" span="18">
                        <span class="filter-name text-no-wrap">{{item.name}}</span>
                    </Col>
                    <Col span="6" style="text-align:right"> 
                        <IconButton v-if="item.id>0" class="edit-btn" @click.stop="showCreateDialog(item)" icon="ios-settings-outline"/>
                        <Icon v-if="selectedFilter==item.id" type="md-checkmark" /> 
                    </Col>
                </Row>
            </div>
            <div @click="showCreateDialog" class="popup-item-name popup-item-name-divide">
                  {{$t('创建过滤器')}}<Icon type="md-add"/>
            </div>
        </div>
    </Poptip>

</template>
<script>
export default {
    name:"FilterSelect",
    props: ['placement','objectType','filterList','value'],
    data (){
        return{
            visible:false,
            selectedFilter:this.value
        }
    },
    watch:{
        value(val){
            this.selectedFilter=val;
        },
    },
    methods:{
        selectFilter(item){
            this.$refs.poptip.ok();
            if(this.selectedFilter==item.id){
                this.selectedFilter=null;
            }else{
                this.selectedFilter=item.id;
            }
            this.$emit('input',this.selectedFilter);
            this.$emit('change')
        },
        showCreateDialog(item){
            this.visible=false;
             app.showDialog(FilterEditDialog,{
                 objectType:this.objectType,
                 id:item.id,
             })
        }
    }
}
</script>