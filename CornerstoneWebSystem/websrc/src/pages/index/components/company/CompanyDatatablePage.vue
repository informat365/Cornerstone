<style scoped>
.opt-bar{
  background-color: #F1F4F5;
  margin-top: 0;
}
</style>
<i18n>
{
    "en":{
    "名称":"Name",
    "查询":"Query",
    "状态":"Status",
    "创建":"Create",
    "分组":"Group",
    "备注":"Remark",
    "创建人":"Creater",
    "创建日期":"Create time",
    "设置":"Setting"
    },
    "zh_CN": {
    "名称":"名称",
    "查询":"查询",
    "状态":"状态",
    "创建":"创建",
    "分组":"分组",
    "备注":"备注",
    "创建人":"创建人",
    "创建日期":"创建日期",
    "设置":"设置"
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
                    <Button @click="showEditDialog" type="default" icon="md-add" >{{$t('创建')}}</Button>
              </FormItem>
             </Form>
            </Col>
       </Row>
        <div style="padding:20px">
         <BizTable :fixed="true" @change="loadData" :page="pageQuery" :value="tableData" >
            <template slot="thead">
                    <tr>
                    <th style="width:250px">{{$t('名称')}}</th>
                    <th  style="width:100px">{{$t('分组')}}</th>
                    <th >{{$t('备注')}}</th>
                    <th style="width:100px">{{$t('状态')}}</th>
                    <th style="width:100px">{{$t('创建人')}}</th>
                    <th style="width:150px;">{{$t('创建日期')}}</th>
                    <th style="width:100px;"></th>
                    </tr>
            </template>
            <template slot="tbody">
                <tr v-for="item in tableData" :key="item.id" class="table-row">
                    <td >{{item.name}}</td>
                    <td class="text-no-wrap">{{item.group}}</td>
                    <td class="text-no-wrap">{{item.remark}}</td>
                    <td ><DataDictLabel type="Common.status" :value="item.status"/></td>
                    <td>{{item.createAccountName}}</td>
                    <td>{{item.createTime|fmtDateTime}}</td>
                    <td style="text-align:right">
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
            if(type=='datatable.edit'){
                this.loadData();
            }
        },
        loadData(resetPage){
            if(resetPage){
                this.pageQuery.pageIndex=1;
            }
            var query=copyObject(this.pageQuery,this.formItem)
            app.invoke('DataTableAction.getDataTableList',[app.token,query],(info)=>{
                this.tableData=info.list;
                this.pageQuery.total=info.count;
            })
        },
        showEditDialog(){
            app.showDialog(DatatableEditDialog)
        },
        settingItem(item){
            app.showDialog(DatatableEditDialog,{
              id:item.id,
            })
        }
    }
}
</script>
