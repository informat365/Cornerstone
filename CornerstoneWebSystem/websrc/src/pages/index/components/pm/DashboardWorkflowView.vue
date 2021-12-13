<style scoped>
   .table-box{
        background-color: #fff;
        padding:15px 30px;
        box-shadow: 0px 2px 10px 0px rgba(225,225,225,0.5);
        border: 1px solid rgba(216,216,216,1);
   }
   .table-info{
       color:#999;
       text-align: center;
   }
   .table-count{
       background-color: #E8E8E8;
       color:#666;
       padding:3px 5px;
       border-radius: 3px;
   }
   .nodata{
       padding:60px;
       font-size:20px;
       color:#999;
       text-align: center;
   }
   .section-bar{
       display: flex;
       align-items: center;
   }
   .section-item{
       font-weight: bold;
       color:#666;
       font-size:14px;
       margin-right:15px;
       cursor: pointer;
       position: relative;
       padding-bottom:5px;
   }
   .section-item-active{
       color:#009CF1;
   }
   .section-item-active::after {
    height: 4px;
    width: 50%;
    transform:translateX(50%);
    display: block;
    background: #009CF1;
    content: "";
    position: absolute;
    bottom: 0px;
    left: 0;
    z-index:10;
}
.serial-no{
    color:#666;
}

</style>
<i18n>
{
    "zh_CN":{
        "待办流程":"待办流程",
        "我发起的":"我发起的",
        "我参与的":"我参与的",
        "流程数据":"流程数据",
        "条数据":"{0}条数据",
        "发起流程":"发起流程",
        "类型":"类型",
        "标题":"标题",
        "当前步骤":"当前步骤",
        "当前负责人":"当前负责人",
        "提交人":"提交人",
        "提交时间":"提交时间",
        "暂无数据":"暂无数据"
    },
    "en": {
        "待办流程":"Todo",
        "我发起的":"Creation",
        "我参与的":"Involved",
        "流程数据":"Data",
        "条数据":"{0} items",
        "发起流程":"Create",
        "类型":"Type",
        "标题":"Title",
        "当前步骤":"Step",
        "当前负责人":"Owner",
        "提交人":"Created by",
        "提交时间":"Time",
        "暂无数据":"No Data"
    }
}
</i18n>
<template>
<div style="padding:20px;">
     <Row>
        <Col span="8">
            <div class="section-bar">
                <div @click="selectFilter('todo')" class="section-item" :class="{'section-item-active':selectTab=='todo'}">{{$t('待办流程')}}</div>
                <div @click="selectFilter('create')" class="section-item" :class="{'section-item-active':selectTab=='create'}">{{$t('我发起的')}}</div>
                <div @click="selectFilter('join')" class="section-item" :class="{'section-item-active':selectTab=='join'}">{{$t('我参与的')}}</div>
                <div @click="showDataView()" class="section-item" :class="{'section-item-active':selectTab=='data'}">{{$t('流程数据')}}</div>
            </div>
        </Col>
        <Col span="8">
            <div v-if="selectTab!='data'" class="table-info">
                <IconButton :size="15" :disabled="pageQuery.pageIndex==1" @click="changePage(-1)" icon="md-arrow-round-back"></IconButton>
                <span class="table-count">{{$t('条数据',[pageQuery.total])}} ，{{pageQuery.pageIndex}}/{{pageQuery.totalPage}}</span>
                <IconButton :size="15" :disabled="pageQuery.pageIndex==pageQuery.totalPage"  @click="changePage(1)"  icon="md-arrow-round-forward"></IconButton>
            </div> 
            &nbsp;
        </Col>
        <Col span="8" style="text-align:right">
            <IconButton  @click="showCreateDialog" :title="$t('发起流程')" icon="md-add"></IconButton>
        </Col>
    </Row>
    
    <div  v-if="selectTab!='data'" class="table-box">
        <table class="table-content" style="table-layout:fixed">
               <thead>
                    <tr>
                        <th style="width:140px;">{{$t('类型')}}</th>
                        <th style="min-width:100px;">{{$t('标题')}}</th>
                        <th style="width:120px;">{{$t('当前步骤')}}</th>
                        <th style="width:120px;">{{$t('当前负责人')}}</th>
                        <th style="width:100px;">{{$t('提交人')}}</th>
                        <th style="width:120px">{{$t('提交时间')}}</th>
                    </tr>     
                </thead>
                <tbody>
                    <tr @click="showWorkflowInfo(item)" v-for="item in list" :key="'todo'+item.id" class="table-row clickable">
                        <td class="text-no-wrap">
                            <WorkflowDefineNameLabel :name="item.workflowDefineName" :color="item.workflowDefineColor"></WorkflowDefineNameLabel>
                        </td>
                        <td  class="text-no-wrap">
                            <WorkflowNameLabel :id="item.serialNo" 
                                :name="item.title" 
                                :finishType="item.finishType"
                                :finishText="item.finishText"
                                :isDone="item.isFinished"></WorkflowNameLabel>
                        </td>
                      
                        <td class="text-no-wrap">
                            {{item.currNodeName}}
                        </td> 

                        <td class="text-no-wrap">
                            <WorkflowOwnerView type="tips" :value="item.currAccountList"/>
                        </td> 
                        <td>
                            <AvatarImage size="small" :name="item.createAccountName" 
                                :value="item.createAccountImageId" type="label"></AvatarImage>
                        </td>
                        <td >
                            {{item.createTime|fmtDateTime}}
                        </td>   
                    </tr>
                </tbody>
        </table>
        <div class="nodata" v-if="list.length==0">
            {{$t('暂无数据')}}
        </div>
    </div>

    <div v-if="selectTab=='data'">
        <WorkflowDataView/>
    </div>

</div>
</template>

<script>
export default {
    mixins: [componentMixin],
    data(){
        return {
            list:[],
            pageQuery:{
                total:0,
                pageIndex:1,
                pageSize:30,
                totalPage:1,
            },
            selectTab:'todo'
        }
    },  
    mounted(){
        this.loadData();
    },
    methods:{
        showCreateDialog(){
            app.showDialog(WorkflowCreateDialog)
        },
        selectFilter(type){
            this.selectTab=type;
            this.pageIndex=1;
            this.loadData();
        },
        showDataView(){
            this.selectTab='data';
        },
        changePage(delta){ 
            var t=this.pageQuery.pageIndex+delta;
                if(t<=0||t>this.pageQuery.totalPage){
                return;
            }
            this.pageQuery.pageIndex=t;
            this.loadData();
        },
        reloadData(){
            this.pageQuery.pageIndex=1;
            this.loadData();
        },
        showWorkflowInfo(item){
            app.showDialog(WorkflowDialog,{
                id:item.uuid
            })
        },
        filterList(info){
            this.pageQuery.pageIndex=1;
            this.pageQuery.createAccountName=info.createAccountName;
            this.pageQuery.title=info.title;
            this.pageQuery.workflowDefineName=info.workflowDefineName;
            this.pageQuery.serialNo=info.serialNo;
            this.loadData();
        },
        loadData(){
            var action="";
            if(this.selectTab=='todo'){
                action="WorkflowAction.getOwnerWorkflowInstanceList"
            }
            if(this.selectTab=='create'){
                action="WorkflowAction.getMyCreateWorkflowInstanceList"
            }
            if(this.selectTab=='join'){
                action="WorkflowAction.getMyJoinWorkflowInstanceList"
            }
            app.invoke(action,[app.token,this.pageQuery],(info)=>{
                this.list=info.list;
                this.pageQuery.total=info.count;
                if(info.count==0){
                    this.pageQuery.totalPage=1;
                }else{
                    var t=Math.ceil(info.count/this.pageQuery.pageSize);
                    this.pageQuery.totalPage=t;
                }
            });
        }
    }
}
</script> 