<style scoped>
   .card{
        background-color: #fff;
        border-radius: 3px;
        background-color: #fff;
        box-shadow: 0 4px 6px 0 rgba(31, 31, 31, 0.05), 0 0 2px 0 rgba(31, 31, 31, 0.2);
        transition: box-shadow 0.2s ease;
        display: flex;
        flex-direction: column;
   }
   .opt-row{
       text-align: right;
       display: flex;
       flex-direction: row-reverse;
       align-items: center;
       height: 32px;
   }
   .icon-button-primary{
       color:#0094FB !important;
   }
</style>
<i18n>
{
    "en": {
        "请选择仪表盘": "Please choose dashboard",
        "新建仪表盘": "Create dashboard",
        "编辑": "Edit",
        "编辑布局": "Edit layout",
        "保存": "Save",
        "添加卡片":"Add",
        "保存成功":"Success",
        "刷新数据":"Refresh"
    },
    "zh_CN": {
        "请选择仪表盘": "请选择仪表盘",
        "新建仪表盘": "新建仪表盘",
        "编辑": "编辑",
        "编辑布局": "编辑布局",
        "保存": "保存",
        "添加卡片":"添加卡片",
        "保存成功":"保存成功",
        "刷新数据":"刷新数据"
    }
}
</i18n>
<template>
<div style="padding:20px;">
    <Row type="flex" justify="center" style="padding:0 10px">
        <Col span="12">

            <Select :placeholder="$t('请选择仪表盘')"  filterable v-model="dashboardId" style="width:200px">
                <Option v-for="item in dashbardList" :value="item.id" :key="item.id">{{ item.name }}</Option>
            </Select>
            <IconButton v-if="perm('dashboard_create')" @click="addDashboard" icon="md-add" tips="新建仪表盘"></IconButton>
            <IconButton v-if="dashboardId>0&&editable" @click="editDashboard" icon="ios-settings" :tips="$t('编辑')"></IconButton>
        </Col>
        <Col span="12">
             <div class="opt-row" v-if="dashboardId>0&&editable">
                <IconButton @click="refreshData" icon="md-refresh" :title="$t('刷新数据')"></IconButton>

                <IconButton @click="isEditLayout=true" v-if="isEditLayout==false" icon="md-move" :title="$t('编辑布局')"></IconButton>
                <IconButton @click="saveLayout" class="icon-button-primary" v-if="isEditLayout" :title="$t('保存')"></IconButton>
                <IconButton @click="addCard" v-if="isEditLayout==false"  icon="md-add" :title="$t('添加卡片')"></IconButton>
            </div>
        </Col>
    </Row>

    <grid-layout
            :layout.sync="layoutList"
            :col-num="6"
            :row-height="50"
            :is-draggable="isEditLayout"
            :is-resizable="isEditLayout"
            :is-mirrored="false"
            :vertical-compact="true"
            :margin="[10, 10]"
            :use-css-transforms="true">

    <grid-item class="card" v-for="item in layoutList" :key="item.id"
                   :x="item.x"
                   :y="item.y"
                   :w="item.w"
                   :h="item.h"
                   :i="item.i">
            <CardDateView :card="allCardMap[item.id]" v-if="allCardMap[item.id].type==4"></CardDateView>
            <template v-if="allCardMap[item.id].type==1">
                <CardPieView v-if="allCardMap[item.id].chartId==1" :card="allCardMap[item.id]" ></CardPieView>
                <CardBarView v-if="allCardMap[item.id].chartId==2" :card="allCardMap[item.id]" ></CardBarView>
                <CardBarView v-if="allCardMap[item.id].chartId==3" :card="allCardMap[item.id]" ></CardBarView>
                <CardLineView v-if="allCardMap[item.id].chartId==4" :card="allCardMap[item.id]" ></CardLineView>
                <CardLineView v-if="allCardMap[item.id].chartId==5" :card="allCardMap[item.id]" ></CardLineView>
                <CardLineView v-if="allCardMap[item.id].chartId==6" :card="allCardMap[item.id]" ></CardLineView>
                <CardPercentView v-if="allCardMap[item.id].chartId==7" :card="allCardMap[item.id]" ></CardPercentView>
                <CardPercentView v-if="allCardMap[item.id].chartId==8" :card="allCardMap[item.id]" ></CardPercentView>
            </template>
            <CardProjectView :card="allCardMap[item.id]" v-if="allCardMap[item.id].type==3"></CardProjectView>

            <CardNumberView :card="allCardMap[item.id]" v-if="allCardMap[item.id].type==2"></CardNumberView>
            <CardHeatmapView :card="allCardMap[item.id]" v-if="allCardMap[item.id].type==6"></CardHeatmapView>
            <CardIterationView :card="allCardMap[item.id]" v-if="allCardMap[item.id].type==5"></CardIterationView>


        </grid-item>
    </grid-layout>
</div>
</template>

<script>
import VueGridLayout from 'vue-grid-layout';
export default {
    mixins: [componentMixin],
    components: {
        GridLayout: VueGridLayout.GridLayout,
        GridItem: VueGridLayout.GridItem
    },
    data(){
        return {
            dashboardId:0,
            dashbardList:[],
            dashbardCardList:[],
            layoutList:[],
            isEditLayout:false,
            allCardMap:[]
        }
    },
    watch:{
        dashboardId(val){
            this.loadCard(val);
        }
    },
    mounted(){
        this.loadDashboard();
    },
    computed:{
        editable(){
            if(this.dashboardId==0){
                return false;
            }
            var ds=null;
            for(var i=0;i<this.dashbardList.length;i++){
                if(this.dashbardList[i].id==this.dashboardId){
                    ds=this.dashbardList[i];
                    break;
                }
            }
            if(ds==null){
                return false;
            }
            return app.account.id==ds.createAccountId;
        }
    },
    methods:{
        loadDashboard(){
            this.isEditLayout=false;
            app.invoke('BizAction.getDashboardList',[app.token],(list)=>{
                var dashboardId=this.loadSavedDashboardId(list);
                if(dashboardId==null){
                    if(list.length>0){
                        dashboardId=list[0].id;
                    }
                }
                this.dashboardId=dashboardId;
                this.dashbardList=list;
            });
        },
        loadSavedDashboardId(list){
            var dashboardId=app.loadObject('Dashboard.card.dashboardId');
            if(dashboardId==null){
                return null;
            }
            for(var i=0;i<list.length;i++){
                var t=list[i];
                if(t.id==dashboardId){
                    return dashboardId;
                }
            }
            app.deleteObject('Dashboard.card.dashboardId')
            return null;
        },
        reloadCard(){
            this.loadCard(this.dashboardId);
        },
        refreshData(){
            app.invoke('BizAction.refreshDashboardAllCard',[app.token,this.dashboardId],()=>{
                this.loadDashboard();
            });
        },
        loadCard(val){
            if(val==null||val==undefined){
                this.dashbardCardList=[];
                return;
            }
            app.saveObject('Dashboard.card.dashboardId',val);
            var query={
                dashboardId:val
            }
            app.invoke('BizAction.getDashboardCardList',[app.token,query],(list)=>{
                this.dashbardCardList=list;
                this.checkUpdate();
                this.syncToLayout();
            });
        },
        checkUpdate(){
            var updateCardIds=[];
            for(var i=0;i<this.dashbardCardList.length;i++){
                var card=this.dashbardCardList[i];
                if(card.updatePassSecond>3600){
                    updateCardIds.push(card.id);
                }
            }
            if(updateCardIds.length>0){
                app.invoke('BizAction.batchRefreshDashboardCard',[app.token,updateCardIds],(list)=>{
                    app.postMessage('card.edit');
                });
            }
        },
        syncToLayout(){
            this.layoutList=[];
            for(var i=0;i<this.dashbardCardList.length;i++){
                    var item=this.dashbardCardList[i];
                    this.layoutList.push({
                        x:item.x,
                        y:item.y,
                        w:item.width,
                        h:item.height,
                        i:item.id+"",
                        id:item.id,
                    })
                    this.allCardMap[item.id]=item;
            }
        },
        saveLayout(){
            this.isEditLayout=false;
            var list=[];
            for(var i=0;i<this.layoutList.length;i++){
                var t=this.layoutList[i];
                t.width=t.w;
                t.height=t.h;
            }
            app.invoke('BizAction.saveDashboardCardList',[app.token,this.layoutList],(list)=>{
                app.toast(this.$t('保存成功'));
            });
        },
        addCard(){
            app.showDialog(CardEditDialog,{
                dashboardId:this.dashboardId
            })
        },
        editDashboard(){
            app.showDialog(CardGroupEditDialog,{
                id:this.dashboardId,
                callback:()=>{
                    this.loadDashboard();
                }
            })
        },
        addDashboard(){
            app.showDialog(CardGroupEditDialog,{
                callback:(dashboardId)=>{
                    app.saveObject('Dashboard.card.dashboardId',dashboardId);
                    this.loadDashboard();
                }
            })
        }
    }
}
</script>
