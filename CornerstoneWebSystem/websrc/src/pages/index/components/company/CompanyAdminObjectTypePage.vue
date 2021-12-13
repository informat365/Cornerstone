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
    "分组":"Group",
    "查询":"Query",
    "创建":"Create",
    "备注":"Remark",
    "创建日期":"Create Time",
    "设置":"Settings"
    },
    "zh_CN": {
    "名称":"名称",
    "分组":"分组",
    "查询":"查询",
    "创建":"创建",
    "备注":"备注",
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
                      <Input @on-change="loadData(true)" type="text" v-model="formItem.group" :placeholder="$t('分组')"></Input>
                    </FormItem>
                    <FormItem>
                        <Button @click="loadData(true)" type="default">{{$t('查询')}}</Button>
                    </FormItem>
             </Form>
          </Col>
          <Col span="12" style="text-align:right">
            <Form inline>
               <FormItem>
                    <Button @click="settingItem()" type="default" icon="md-add">{{$t('创建')}}</Button>
              </FormItem>
             </Form>
            </Col>
       </Row>
        <div style="padding:20px">
         <BizTable :fixed="true" @change="loadData" :page="pageQuery" :value="tableData" >
            <template slot="thead">
                    <tr>
                    <th style="width:150px">{{$t('名称')}}</th>
                    <th style="width:150px">{{$t('分组')}}</th>
                     <th >{{$t('备注')}}</th>
                    <th style="width:140px;">{{$t('创建日期')}}</th>
                    <th style="width:120px;"></th>
                    </tr>
            </template>
            <template slot="tbody">
                <tr v-for="item in tableData" :key="item.id" class="table-row">
                    <td >{{item.name}}</td>
                    <td>{{item.group}}</td>
                    <td>{{item.remark}}</td>
                    <td>{{item.createTime|fmtDateTime}}</td>
                    <td style="text-align:right">
                        <Button  @click="settingItem(item.id)" type="default">{{$t('设置')}}</Button>
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
            group:null,
        },
        tableData:[],
        }
    },

    methods:{
        pageLoad(){
            this.loadData();
        },
        pageMessage(type){
            if(type=='objectType.edit'){
                this.loadData();
            }
        },
        loadData(resetPage){
            if(resetPage){
                this.pageQuery.pageIndex=1;
            }
            var query=copyObject(this.pageQuery,this.formItem)
            app.invoke('BizAction.getObjectTypeList',[app.token,query],(info)=>{
                this.tableData=info.list;
                this.pageQuery.total=info.count;
            })
        },
        settingItem(id){
            app.showDialog(CompanyObjectTypeEditDialog,{
              id:id,
            })
        }
    }
}
</script>
