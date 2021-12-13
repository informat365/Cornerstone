<style scoped>
.opt-bar{
  background-color: #F1F4F5;
  margin-top: 0;
}
</style>

<i18n>
    {
    "en": {
    "编号":"N.O.",
    "提交人":"Submitter",
    "查询":"Query",
    "类型":"Type",
    "标题":"Title",
    "当前步骤":"Current step",
    "当前负责人":"Current Owner",
    "提交时间":"Submit time"
    },
    "zh_CN": {
    "编号":"编号",
    "提交人":"提交人",
    "查询":"查询",
    "类型":"类型",
    "标题":"标题",
    "当前步骤":"当前步骤",
    "当前负责人":"当前负责人",
    "提交时间":"提交时间"
    }
    }
</i18n>

<template>
    <div class="page">

        <Row class="opt-bar opt-bar-light">
          <Col span="12" class="opt-left">
               <Form inline @submit.native.prevent>
                   <FormItem>
                      <Input @on-change="loadData(true)" type="text" v-model="formItem.serialNo" :placeholder="$t('编号')"></Input>
                    </FormItem>
                    <FormItem>
                      <Input @on-change="loadData(true)" type="text" v-model="formItem.createAccountName" :placeholder="$t('提交人')"></Input>
                    </FormItem>
                    <FormItem>
                        <Button @click="loadData(true)" type="default">{{$t('查询')}}</Button>
                    </FormItem>
             </Form>
          </Col>
          <Col span="12" style="text-align:right">

            </Col>
       </Row>
        <div style="padding:20px">
         <BizTable :fixed="true" @change="loadData" :page="pageQuery" :value="tableData" >
            <template slot="thead">
                    <tr>
                        <th style="width:140px;">{{$t('类型')}}</th>
                        <th style="min-width:100px;">{{$t('标题')}}</th>
                        <th style="width:120px;">{{$t('当前步骤')}}</th>
                        <th style="width:120px;">{{$t('当前负责人')}}</th>
                        <th style="width:100px;">{{$t('提交人')}}</th>
                        <th style="width:120px">{{$t('提交时间')}}</th>
                    </tr>
            </template>
            <template slot="tbody">
                <tr @click="showWorkflowInfo(item)" v-for="item in tableData" :key="item.id" class="clickable table-row">
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
                            <WorkflowOwnerView type="none" :value="item.currAccountList"/>
                        </td>
                        <td>
                            <AvatarImage size="small" :name="item.createAccountName"
                                :value="item.createAccountImageId" type="label"></AvatarImage>
                        </td>
                        <td >
                            {{item.createTime|fmtDateTime}}
                        </td>
                </tr>

            </template>
        </BizTable>
        </div>
    </div>
</template>

<script>

export default {
    mixins: [componentMixin],
    data(){
        return {
            pageQuery:{
                pageIndex:1,
                pageSize:20,
            },
            formItem:{
                name:null,
                status:null,
            },
            tableData:[],
        }
    },

    methods:{
        pageLoad(){
            this.loadData();
        },
        pageMessage(type){
            if(type=='workflow.edit'){
                this.loadData();
            }
        },
        loadData(resetPage){
            if(resetPage){
                this.pageQuery.pageIndex=1;
            }
            var query=copyObject(this.pageQuery,this.formItem)
            app.invoke('WorkflowAction.getWorkflowInstanceListByAdmin',[app.token,query],(info)=>{
                this.tableData=info.list;
                this.pageQuery.total=info.count;
            })
        },
        showWorkflowInfo(item){
            app.showDialog(WorkflowDialog,{
                id:item.uuid,
                admin:true
            })
        }
    }
}
</script>
