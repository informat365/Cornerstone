<style scoped>
.page{
  min-height: 400px;
}
.opt-bar{
  background-color: #F1F4F5;
  margin-top: 0;
}
.opt-right{
  text-align: right;
}
.table-info-bar{
  font-size:12px;
  font-weight:bold;
  color:#999999;
  text-align:right;
}

</style>
<i18n>
{
	"en": {
        "名称": "名称",
		"查询": "查询",
		"创建": "创建",
		"备注": "备注",
		"创建日期": "创建日期",
		"设置": "设置"
    },
	"zh_CN": {
		"名称": "名称",
		"查询": "查询",
		"创建": "创建",
		"备注": "备注",
		"创建日期": "创建日期",
		"设置": "设置"
	}
}
</i18n>
<template>
    <div class="page">
         <Row class="opt-bar">
          <Col span="18" class="opt-left">
               <Form inline @submit.native.prevent>
                    <FormItem>
                      <Input @on-change="loadData(true)" v-model="formItem.name"  type="text" :placeholder="$t('名称')"></Input>
                    </FormItem>
                    <FormItem>
                        <Button @click="loadData(true)" type="default">{{$t('查询')}}</Button>
                    </FormItem>
             </Form>

          </Col>
          <Col span="6" class="opt-right">
            <Form inline>
              <FormItem>
                    <Button @click="showCreateDialog" type="default" icon="md-add">{{$t('创建')}}</Button>
              </FormItem>
             </Form>
            </Col>
       </Row>

        <div style="padding:20px">
         <BizTable :fixed="true" @change="loadData" :page="pageQuery" :value="tableData" >
            <template slot="thead">
                    <tr>
                    <th>{{$t('名称')}}</th>
                    <th style="width:250px">{{$t('备注')}}</th> 
                    <th style="width:140px;">{{$t('创建日期')}}</th>
                    <th style="width:120px;"></th>
                    </tr>
            </template>
            <template slot="tbody">
                <tr v-for="item in tableData" :key="item.id" class="table-row">
                    <td>{{item.name}}</td>
                    <td style="max-width:250px">{{item.remark}}</td>
                    <td>{{item.createTime|fmtDateTime}}</td> 
                    <td style="text-align:right">
                        <Button size="small" @click="settingItem(item)" type="default">{{$t('设置')}}</Button>
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
                id:null,
                group:null
            },
            tableData:[],
        }
    },
    methods:{
        pageLoad(){
            this.loadPageStatus("CmdbApiPage")
            this.loadData();
        },
        pageMessage(type){
            if(type=='cmdapi.edit'){
                this.loadData(true);
            }
        },
        loadData(resetPage){
            if(resetPage){
                this.pageQuery.pageIndex=1;
            }
             this.savePageStatus("CmdbApiPage")
            var query=copyObject(this.pageQuery,this.formItem)
            app.invoke('BizAction.getCmdbApiList',[app.token,query],(info)=>{
                this.tableData=info.list;
                this.pageQuery.total=info.count;
            })   
        },
        settingItem(item){
            app.showDialog(CmdbApiEditDialog,{
                id:item.id
            })
        },
        showCreateDialog(){
            app.showDialog(CmdbApiEditDialog)
        }
    }
}
</script>