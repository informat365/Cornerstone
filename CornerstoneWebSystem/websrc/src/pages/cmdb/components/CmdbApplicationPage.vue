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
		"分组": "分组",
		"查询": "查询",
		"创建": "创建",
		"类型": "类型",
		"依赖项": "依赖项",
		"创建日期": "创建日期",
		"设置": "设置"
    },
	"zh_CN": {
		"名称": "名称",
		"分组": "分组",
		"查询": "查询",
		"创建": "创建",
		"类型": "类型",
		"依赖项": "依赖项",
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
                      <Input @on-change="loadData(true)" v-model="formItem.group"  type="text" :placeholder="$t('分组')"></Input>
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
                    <th style="width:150px">{{$t('分组')}}</th> 
                    <th style="width:150px">{{$t('类型')}}</th> 
                    <th style="width:300px">{{$t('依赖项')}}</th> 
                    <th style="width:140px;">{{$t('创建日期')}}</th>
                    <th style="width:120px;"></th>
                    </tr>
            </template>
            <template slot="tbody">
                <tr v-for="item in tableData" :key="item.id" class="table-row">
                   
                    <td>{{item.name}}</td>
                    <td>{{item.group}}</td>
                    <td>{{item.type}}</td>
                    <td>
                       <Tag v-for="(item,itemIdx) in item.depends" :key="'item'+itemIdx">{{item}}</Tag>
                    </td>
                    <td>{{item.createTime|fmtDateTime}}</td> 
                    <td style="text-align:right">
                        <Button size="small"  @click="settingItem(item)" type="default">{{$t('设置')}}</Button>
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
            this.loadPageStatus("CmdbApplicationPage")
            this.loadData();
        },
        pageMessage(type){
            if(type=='cmdbapplication.edit'){
                this.loadData(true);
            }
        },
        loadData(resetPage){
            if(resetPage){
                this.pageQuery.pageIndex=1;
            }
            var query=copyObject(this.pageQuery,this.formItem)
            //
             this.savePageStatus("CmdbApplicationPage")
            //
            app.invoke('BizAction.getCmdbApplicationList',[app.token,query],(info)=>{
                this.tableData=info.list;
                this.pageQuery.total=info.count;
            })   
        },
        settingItem(item){
            app.showDialog(CmdbApplicationEditDialog,{
                id:item.id
            })
        },
        showCreateDialog(){
            app.showDialog(CmdbApplicationEditDialog)
        }
    }
}
</script>