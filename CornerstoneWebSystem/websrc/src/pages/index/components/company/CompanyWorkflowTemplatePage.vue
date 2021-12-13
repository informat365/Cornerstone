<style scoped>
.opt-bar{
  background-color: #F1F4F5;
  margin-top: 0;
}
</style>

<i18n>
    {
    "en": {
    "名称":"Name",
    "状态":"Status",
    "查询":"Query",
    "创建模板":"Create template",
    "备注":"Remark",
    "创建人":"Creater",
    "创建日期":"Create time",
    "复制":"Copy",
    "设置":"Setting",
    "确定要删除此流程模板吗？":"Are you sure you want to delete this template?",
    "操作成功":"Delete success",
    "删除":"Delete"
    },
    "zh_CN": {
    "名称":"名称",
    "状态":"状态",
    "查询":"查询",
    "创建模板":"创建模板",
    "备注":"备注",
    "创建人":"创建人",
    "创建日期":"创建日期",
    "复制":"复制",
    "设置":"设置",
    "确定要删除此流程模板吗？":"确定要删除此流程模板吗？",
    "操作成功":"操作成功",
    "删除":"删除"
    }
    }
</i18n>

<template>
    <div class="page">

        <Row class="opt-bar opt-bar-light">
          <Col span="12" class="opt-left">
               <Form inline @submit.native.prevent>
                    <FormItem >
                      <Input @on-change="loadData(true)" type="text" v-model="formItem.name" :placeholder="$t('名称')"></Input>
                    </FormItem>
                    <FormItem >
                        <DataDictSelect @change="loadData(true)" clearable :placeholder="$t('状态')" type="Common.status" v-model="formItem.status"></DataDictSelect>
                    </FormItem>
                    <FormItem>
                        <Button @click="loadData(true)" type="default">{{$t('查询')}}</Button>
                    </FormItem>
             </Form>
          </Col>
          <Col span="12" style="text-align:right">
            <Form inline>
               <FormItem>
                    <Button @click="showEditTemplateDialog" type="default" icon="md-add" class="mr15">{{$t('创建模板')}}</Button>
              </FormItem>
             </Form>
            </Col>
       </Row>
        <div style="padding:20px">
         <BizTable :fixed="true" @change="loadData" :page="pageQuery" :value="tableData" >
            <template slot="thead">
                    <tr>
                    <th style="width:200px">{{$t('名称')}}</th>
                    <th >{{$t('备注')}}</th>
                    <th style="width:100px">{{$t('状态')}}</th>
                    <th style="width:100px">{{$t('创建人')}}</th>
                    <th style="width:130px;">{{$t('创建日期')}}</th>
                    <th style="width:250px;"></th>
                    </tr>
            </template>
            <template slot="tbody">
                <tr v-for="item in tableData" :key="item.id" class="table-row">
                    <td class="text-no-wrap">{{item.name}}</td>
                    <td class="text-no-wrap">{{item.remark}}</td>
                    <td ><DataDictLabel type="Common.status" :value="item.status"/></td>
                    <td>{{item.createAccountName}}</td>
                    <td>{{item.createTime|fmtDateTime}}</td>
                    <td style="text-align:right">
                        <Button style="margin-right:5px" @click="copyItem(item)" type="default">{{$t('复制')}}</Button>
                        <Button style="margin-right:5px" @click="deleteItem(item)" type="error">{{$t('删除')}}</Button>
                        <Button  @click="settingItem(item)" type="default">{{$t('设置')}}</Button>
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
            if(type=='workflow.template.edit'){
                this.loadData();
            }
        },
        loadData(resetPage){
            if(resetPage){
                this.pageQuery.pageIndex=1;
            }
            var query=copyObject(this.pageQuery,this.formItem)
            app.invoke('WorkflowAction.getWorkflowDefineList',[app.token,query],(info)=>{
                this.tableData=info.list;
                this.pageQuery.total=info.count;
            })
        },
        showEditTemplateDialog(){
            app.showDialog(WorkflowTemplateEditDialog)
        },
        settingItem(item){
            window.open("/wfeditor.html#/"+item.id+"/info")
        },
        deleteItem(item){
            app.confirm(this.$t('确定要删除此流程模板吗？'),()=>{
                app.invoke('WorkflowAction.deleteWorkflowDefine',[app.token,item.id],(info)=>{
                    app.toast(this.$t('操作成功'));
                    this.loadData(true);
                })
            })
        },
        copyItem(item){
            app.showDialog(WorkflowTemplateEditDialog,{
                copyId:item.id,
                copyName:item.name
            })
        }
    }
}
</script>
