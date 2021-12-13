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
    "查询":"Query",
    "类型":"Type",
    "删除人":"Deleter",
    "删除日期":"Delete time",
    "彻底删除":"Delete completely",
    "清空回收站":"Empty",
    "恢复":"Recover",
    "确定要恢复吗？":"Are you sure you want to recover “{0}”?",
    "确定要彻底删除吗？彻底删除后数据将不能恢复，请谨慎操作":"Are you sure you want to delete “{0}” completely? After completely deleting the data, it will not be recovered. Please operate with caution.",
    "确定要清空回收站吗？清空回收站后数据将不能恢复，请谨慎操作":"Are you sure you want to empty the recycle bin ? After empty, it will not be recovered. Please operate with caution."
    },
    "zh_CN": {
    "名称":"名称",
    "查询":"查询",
    "类型":"类型",
    "删除人":"删除人",
    "删除日期":"删除日期",
    "彻底删除":"彻底删除",
    "清空回收站":"清空回收站",
    "恢复":"恢复",
    "确定要恢复吗？":"确定要恢复“{0}”吗？",
    "确定要彻底删除吗？彻底删除后数据将不能恢复，请谨慎操作":"确定要彻底删除“{0}”吗？彻底删除后数据将不能恢复，请谨慎操作",
    "确定要清空回收站吗？清空回收站后数据将不能恢复，请谨慎操作":"确定要清空回收站吗？清空回收站后数据将不能恢复，请谨慎操作"
    }
    }
</i18n>


<template>
    <div class="page">
        <Row class="opt-bar opt-bar-light">
          <Col span="12" class="opt-left">
            <Form inline @submit.native.prevent>
                    <FormItem>
                      <Input @on-change="loadData(true)" v-model="queryItem.name"  type="text"  :placeholder="$t('名称')"></Input>
                    </FormItem>
                    <FormItem>
                        <Button @click="loadData(true)" type="default">{{$t('查询')}}</Button>
                </FormItem>
            </Form>
          </Col>
            <Col span="12" style="text-align:right">
            <Form inline>
                <FormItem v-if="tableData.length>0">
                    <Button @click="showEmptyDialog" type="default" class="mr15">{{$t('清空回收站')}}</Button>
                </FormItem>
            </Form>
            </Col>
       </Row>
        <div style="padding:20px">
         <BizTable :fixed="true" :page="pageQuery" :value="tableData" @change="loadData">
            <template slot="thead">
                    <tr>
                    <th>{{$t('名称')}}</th>
                    <th style="width:120px">{{$t('类型')}}</th>
                    <th style="width:150px">{{$t('删除人')}}</th>
                    <th style="width:140px;">{{$t('删除日期')}}</th>
                    <th style="width:250px;"></th>
                    </tr>
            </template>
            <template slot="tbody">
                <tr v-for="item in tableData" :key="item.id" class="table-row">
                    <td>{{item.name}}</td>
                     <td>
                        <template v-if="item.objectType==0">{{item.type|dataDict('CompanyRecycle.type')}}</template>
                        <template v-if="item.objectType>0">{{item.objectType|dataDict('Task.objectType')}}</template>

                    </td>
                    <td>{{item.createAccountName}}</td>
                    <td>{{item.createTime|fmtDateTime}}</td>
                    <td style="text-align:right">
                        <Button @click="confirmDelete(item)"  type="error" style="margin-right:10px">{{$t('彻底删除')}}</Button>
                        <Button @click="confirmRestore(item)"   type="default">{{$t('恢复')}}</Button>
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
            tableData:[],
            queryItem:{
                name:null,
            }
        }
    },
    methods:{
        pageLoad(){
            this.loadData();
        },
        loadData(resetPage){
            if(resetPage){
                this.pageQuery.pageIndex=1;
            }
            var query=copyObject(this.pageQuery,this.queryItem)
            app.invoke('BizAction.getCompanyRecycleInfoList',[app.token,query],(info)=>{
                this.tableData=info.list;
                this.pageQuery.total=info.count;
            })
        },
        confirmDelete(item){
            app.confirm(this.$t('确定要彻底删除吗？彻底删除后数据将不能恢复，请谨慎操作',[item.name]),()=>{
                app.invoke('BizAction.deleteCompanyRecycle',[app.token,item.id],(info)=>{
                    this.loadData();
                })
            })
        },
        showEmptyDialog(){
            app.confirm(this.$t('确定要清空回收站吗？清空回收站后数据将不能恢复，请谨慎操作',[]),()=>{
                app.invoke('BizAction.emptyCompanyRecycle',[app.token],(info)=>{
                    this.loadData();
                })
            })
        },
        confirmRestore(item){
            app.confirm(this.$t('确定要恢复吗？',[item.name]),()=>{
                app.invoke('BizAction.restoreCompanyRecycle',[app.token,item.id],(info)=>{
                    this.loadData();
                })
            })
        }
    }
}
</script>
