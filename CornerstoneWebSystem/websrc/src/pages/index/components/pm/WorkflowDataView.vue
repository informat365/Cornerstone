<style scoped>
   .table-opt-bar{
       padding:15px 0;
       border-top: 1px solid #ccc;
       display: flex;
       align-content: center;
   }
   .nodata{
       padding:60px;
       font-size:20px;
       color:#999;
       text-align: center;
   }
    .table-box{
        background-color: #fff;
        padding:15px 10px;
        box-shadow: 0px 2px 10px 0px rgba(225,225,225,0.5);
        border: 1px solid rgba(216,216,216,1);
        overflow: auto;
   }
   .field-col{
       width:200px;
   }


    .table-pager{
        padding:5px 0;
        display: flex;
        flex-direction: row-reverse;
    }
    .opt-gap{
        flex:1;
    }
</style>

<i18n>
{
    "en": {
        "选择流程类型": "Choose Workflow type",
        "提交人": "Submitter",
        "编号": "NO.",
        "已结束": "Finished",
        "未结束": "Unfinished",
        "开始时间": "Start",
        "结束时间": "End",
        "查询": "Query",
        "导出": "Export",
        "当前步骤": "Step",
        "是否结束": "Status",
        "当前负责人": "Owner",
        "提交时间": "Create Time",
        "暂无数据": "No Data"
    },
    "zh_CN": {
        "选择流程类型": "选择流程类型",
        "提交人": "提交人",
        "编号": "编号",
        "已结束": "已结束",
        "未结束": "未结束",
        "开始时间": "开始时间",
        "结束时间": "结束时间",
        "查询": "查询",
        "导出": "导出",
        "当前步骤": "当前步骤",
        "是否结束": "是否结束",
        "当前负责人": "当前负责人",
        "提交时间": "提交时间",
        "暂无数据": "暂无数据"
    }
}
</i18n>

<template>
<div>
    <div class="table-opt-bar">
        <Select @on-change="loadData()" v-model="workflowDefineId" style="width:150px" :placeholder="$t('选择流程类型')">
            <Option v-for="item in workflowDefineList" :value="item.id" :key="'wf'+item.id">{{ item.name }}</Option>
        </Select>
        <template v-if="workflowDefine">
            <Input style="width:100px;margin-left:5px" v-model="formItem.createAccountName" :placeholder="$t('提交人')"></Input>
            <Input style="width:100px;margin-left:5px" v-model="formItem.serialNo" :placeholder="$t('编号')"></Input>
            <Input style="width:100px;margin-left:5px" v-model="formItem.currNodeName" :placeholder="$t('当前步骤')"></Input>
            <Select style="width:100px;margin-left:5px" clearable v-model="formItem.isFinished"  :placeholder="$t('是否结束')">
                <Option :value="1">{{$t('已结束')}}</Option>
                <Option :value="2">{{$t('未结束')}}</Option>
            </Select>
            <ExDatePicker style="width:110px;margin-left:5px" v-model="formItem.createTimeStart" :placeholder="$t('开始时间')"></ExDatePicker>
            <ExDatePicker style="width:110px;margin-left:5px"  :day-end="true" v-model="formItem.createTimeEnd" :placeholder="$t('结束时间')"></ExDatePicker>
            <Button style="margin-left:5px" @click="loadListData()" type="default">{{$t('查询')}}</Button>
        </template>
        <div class="opt-gap"></div>
        <Button @click="exportData()" v-if="pageInfo.total>0" type="default">{{$t('导出')}}</Button>
    </div>
    <div v-if="workflowDefine && list.length>0"  class="table-box">
        <table  class="line-table">
               <thead>
                    <tr >
                        <th style="width:80px;">{{$t('编号')}}</th>
                        <th style="width:100px;">{{$t('提交人')}}</th>
                        <th style="width:150px;">{{$t('当前步骤')}}</th>
                        <th style="width:80px;">{{$t('是否结束')}}</th>
                        <th style="width:120px;">{{$t('当前负责人')}}</th>
                        <th style="width:150px">{{$t('提交时间')}}</th>
                        <th class="text-no-wrap" v-for="field in workflowFieldList" :style="{width:getColumnWidth(field)}" :key='field.id'>{{field.name}}</th>
                    </tr>
                </thead>
            <tbody>
                <tr class="clickable" @click="showWorkflow(item)" v-for="item in list" :key="item.id">
                        <td>{{item.serialNo}}</td>
                        <td>{{item.createAccountName}}</td>
                        <td>{{item.currNodeName}}</td>
                        <td>
                            <template v-if="item.isFinished">{{$t('已结束')}}</template>
                            <template v-if="item.isFinished==false">{{$t('未结束')}}</template>
                        </td>
                        <td> <WorkflowOwnerView type="none" :value="item.currAccountList"/></td>
                        <td>{{item.createTime|fmtDateTime}}</td>
                        <td v-for="field in workflowFieldList" class="text-no-wrap" :key='field.id'>
                            <WorkflowDataLabel :field="field" :formData="item.formData" />
                        </td>
                </tr>
            </tbody>
        </table>
    </div>
    <div class="nodata" v-if="list.length==0">
        {{$t('暂无数据')}}
    </div>
    <div v-if="workflowDefine" class="table-pager">
             <Page  :total="pageInfo.total"
                :current="pageQuery.pageIndex"
                :page-size="pageQuery.pageSize"
                :page-size-opts="[10,15, 20, 50, 200]"
                placement="top"
                show-sizer
                show-total
                @on-page-size-change="changePageSize"
                @on-change="changePage"></Page>
    </div>
</div>
</template>

<script>
export default {
    data(){
        return {
            list:[],
            workflowDefineId:null,
            workflowDefineList:[],
            workflowDefine:null,
            workflowFieldList:[],
            colWidth:{
                "text-single":150,
                "text-area":150,
                "text-rich":300,
                "text-number":100,
                "attachment":150,
                "date":100,
                "select":100,
                "radio":100,
                "checkbox":100,
                "table":300,
                "user-select":150,
                "department-select":150,
                "role-project-select":150,
                "system-value":150,
            },
            pageInfo:{
                total:0
            },
            pageQuery:{
                pageIndex:1,
                pageSize:50
            },
            formItem:{
                createAccountName:null,
                serialNo:null,
                isFinished:null,
                createTimeStart:null,
                createTimeEnd:null,
                currNodeName:null
            }
        }
    },
    mounted(){
        this.loadWorkflowDefineList();
    },
    methods:{
        getColumnWidth(field){
            var t=this.colWidth[field.type];
            if(t){
                return t+"px";
            }
            if(field.type=='table'){
                var w=40;
                field.columnList.forEach(t=>{
                    w+=t.width;
                });
                return w+"px";
            }
            return 150+"px"
        },
        loadWorkflowDefineList(){
            app.invoke('WorkflowAction.getMyDataWorkflowDefineList',[app.token],(list)=>{
                this.workflowDefineList=list;
            });
        },
        loadData(){
            if(this.workflowDefineId!=null){
                this.workflowFieldList=[];
                app.invoke('WorkflowAction.getWorkflowFormDefineById',[app.token,this.workflowDefineId],(info)=>{
                    this.workflowDefine=info;
                    if(info.fieldList){
                        var flist=JSON.parse(info.fieldList)
                        flist.forEach(item=>{
                            if(item.type.indexOf('static')==-1){
                                this.workflowFieldList.push(item);
                            }
                        })
                    }
                    this.loadListData();
                });
            }
        },
        loadListData(){
            this.setupQuery();
            app.invoke('WorkflowAction.getDataWorkflowInstanceList',[app.token,this.pageQuery],(info)=>{
                info.list.forEach(item=>{
                    if(item.formData){
                        item.formData=JSON.parse(item.formData);
                    }else{
                        item.formData={};
                    }
                })
                this.list=info.list;
                this.pageInfo.total=info.count;
                if(info.count==0){
                    this.pageQuery.totalPage=1;
                }else{
                    var t=Math.ceil(info.count/this.pageQuery.pageSize);
                    this.pageQuery.totalPage=t;
                }
            });
        },
        changePageSize(){
            this.loadListData();
        },
        changePage(){
            this.loadListData();
        },
        showWorkflow(item){
            app.showDialog(WorkflowDataDialog,{
                id:item.uuid
            })
        },
        setupQuery(){
            this.pageQuery.workflowDefineId=this.workflowDefineId;
            this.pageQuery.isFinished=null;
            if(this.formItem.isFinished){
                this.pageQuery.isFinished= this.formItem.isFinished==1?true:false
            }
            this.pageQuery.createAccountName=this.formItem.createAccountName;
            this.pageQuery.serialNo=this.formItem.serialNo;
            this.pageQuery.createTimeStart=this.formItem.createTimeStart;
            this.pageQuery.createTimeEnd=this.formItem.createTimeEnd;
            this.pageQuery.currNodeName=this.formItem.currNodeName;
        },
        exportData(){
            this.setupQuery();
            var query={
                token:app.token,
                query:this.pageQuery
            }
            //
            var queryString=JSON.stringify(query);
            var encoded=Base64.encode(queryString);
            window.open('/p/main/export_workflow_instance?arg='+encodeURIComponent(encoded))
        }
    }
}
</script>
