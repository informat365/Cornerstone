<style scoped>
    .iteration-label{
        font-size:13px;
        display: inline-flex;
        width:150px;
        position: relative;
        cursor: pointer;
        align-items: center;
    }
    .iteration-label-left{
        flex:1;
    }
    .iteration-label-icon{
        width:40px;
        color:#A7A7A7;
    }
    .it-time{
        font-size:12px;
        color:#666;
    }
    .it-name{
        font-size:14px;
        font-weight: bold;
        color:#0094FB;
        max-width: 135px;
        width: 135px;
    }
    .check{
        color:#0097F7;
    }
    .list-nodata{
        padding:10px;
        color:#999;
        text-align: center;
    }
    .list-it-name{
        display: inline-block;
        max-width: 200px;
    }
    .line-b{
        border-bottom:1px solid #eee;
    }
    .name-row{
         display: flex;
         align-items: center;
    }
    .it-status-1{
        color:#0095A4;
    }
    .it-status-2{
        color:#66BE86;
    }
    .it-status-3{
        color:#666666;
    }
</style>
<i18n>
{
    "en": {
        "所有迭代": "All",
        "没有匹配的数据":"No Data",
        "搜索迭代":"Search",
        "迭代":"Iteration"
    },
    "zh_CN": {
        "所有迭代": "所有迭代",
        "没有匹配的数据":"没有匹配的数据",
        "搜索迭代":"搜索迭代",
        "迭代":"迭代"
    }
}
</i18n> 
<template>
    <Poptip ref="poptip" transfer class="poptip-full" v-model="visible"  >
        <div class="iteration-label"> 
            <div class="iteration-label-left">
                <div class="it-name text-no-wrap">
                    <template v-if="selectedItem==null">{{$t('所有迭代')}}</template>
                    <template v-if="selectedItem">{{$t('迭代')}}:{{selectedItem.name}}</template>
                </div>
                <div class="it-time">
                <template v-if="selectedItem&&selectedItem.startDate">
                    {{selectedItem.startDate|fmtDate}} ~ {{selectedItem.endDate|fmtDate}}
                    </template>
                </div>
            </div>
            <Icon class="iteration-label-icon" size="16" type="ios-arrow-down" />
        </div>
        <div slot="content" >
            <div style="padding:10px;padding-bottom:0">
                <Input v-model="searchText" :placeholder="$t('搜索迭代')" icon="ios-search"></Input>
            </div>
            <div class="list-nodata" v-if="filteredList.length==0">{{$t('没有匹配的数据')}}</div>
            <div class="popup-box scrollbox">
                 <Row @click.native="selectItem(null)" class="popup-item-name line-b" >
                    {{$t('所有迭代')}}
                </Row>
                <Row @click.native="selectItem(item)" class="popup-item-name" 
                        v-for="item in filteredList" :key="item.id">
                    <Col span="21" class="name-row">
                        <div class="list-it-name text-no-wrap" :class="'it-status-'+item.status">{{item.name}}</div>
                    </Col>
                    <Col span="3" style="text-align:right">
                        <Icon v-if="selectedValue==item.id" class="check" type="md-checkmark"/> &nbsp;
                    </Col>
                </Row>
           </div>
        </div>
    </Poptip>
</template>
<script>
export default {
    name:"IterationSelect",
    props: ['list','value'],
    data (){
        return{
            visible:false,
            searchText:null,
            selectedValue:null,
        }
    },
    watch:{
        value:function(val){
            this.setupValue();
        }
    },
    mounted(){
        this.setupValue();
    },
    computed:{
        filteredList(){
            var t=[];
            if(this.searchText==null||this.searchText==""){
                return this.list;
            }
            for(var i=0;i<this.list.length;i++){
                var q=this.list[i];
                if(q.name.indexOf(this.searchText)!=-1){
                    t.push(q);
                }
            }
            return t;
        },
        selectedItem(){
            for(var i=0;i<this.list.length;i++){
                var t=this.list[i];
                if(t.id==this.selectedValue){
                    return t;
                }
            }
            return null;
        }
    },
    methods:{
        confirm(){
           this.emitEvents();
        },
        emitEvents(){
            this.$emit('input',this.selectedValue)
            this.$emit('change',this.selectedValue)
        },
        setupValue(){
            this.selectedValue=this.value;
           //console.log(this.list)
        },
        selectItem(item){
            if(item==null){
                this.selectedValue=null;
            }else{
                this.selectedValue=item.id;
            }
            this.visible=false;
            this.emitEvents();
        }
    }
}
</script>