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
		"语言": "语言",
		"创建人": "创建人",
		"操作": "操作",
		"编辑": "编辑",
		"删除": "删除",
		"确认要删除模板吗": "确认要删除模板“{0}”吗？",
		"删除成功": "删除成功"
    },
	"zh_CN": {
		"名称": "名称",
		"查询": "查询",
		"创建": "创建",
		"语言": "语言",
		"创建人": "创建人",
		"操作": "操作",
		"编辑": "编辑",
		"删除": "删除",
		"确认要删除模板吗": "确认要删除模板“{0}”吗？",
		"删除成功": "删除成功"
	}
}
</i18n>
<template>
    <div class="page">
         <Row class="opt-bar">
          <Col span="18" class="opt-left">
               <Form inline>
                    <FormItem >
                      <Input type="text" @on-change="loadData(true)" v-model="formItem.name" :placeholder="$t('名称')"></Input>
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
     
     
      <div style="padding:25px;background-color:#fff">

     
       <BizTable :fixed="true" :page="pageQuery"  @change="loadData" :value="tableData">
            <template slot="thead">
                    <tr>
                     <th>{{$t('名称')}}</th>
                      <th style="width:150px">{{$t('语言')}}</th>
                      <th style="width:200px">{{$t('创建人')}}</th>
                      <th style="width:160px;text-align:right">{{$t('操作')}}</th>    
                    </tr>
            </template>
            <template slot="tbody">
               
                <tr class="table-row" v-for=" item in tableData" :key="item.id">
                <td>
                    {{item.name}}
                </td>
                <td >
                    {{item.language|dataDict('DesignerTemplate.language')}}
                </td>
                <td>
                        <div>{{item.createAccountName}}</div>
                        <div>{{item.createTime|fmtDateTime}}</div>
                </td>
                <td style="text-align:right">
                        <Button style="margin-right:5px" @click="showEditDialog(item)" type="default" size="small">{{$t('编辑')}}</Button>
                        <Button @click="deleteItem(item)" type="error" size="small">{{$t('删除')}}</Button>
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
                instanceId:null,
            },
            tableData:[],
        }
    },
    methods:{
        pageLoad(){
            this.loadData();
        },
        pageUpdate(){
        
        },
        pageMessage(type){
            if(type=='template-edit'){
                this.loadData(true);
            }
        },
        loadData(resetPage){
            if(resetPage){
                this.pageQuery.pageIndex=1;
            }
            var query=copyObject(this.pageQuery,this.formItem)
            app.invoke('DesignerAction.getDesignerTemplates',[app.token,query],(info)=>{
                this.tableData=info.list;
                this.pageQuery.total=info.count;
            })   
        },
        showCreateDialog(){
            app.showDialog(CodegenTemplateEditDialog)
        },
        showEditDialog(item){
             app.showDialog(CodegenTemplateEditDialog,{
                 id:item.id
             })
        },
        deleteItem(item){
            app.confirm(this.$t('确认要删除模板吗',[item.name]),()=>{
                    app.invoke('DesignerAction.deleteDesignerTemplate',[app.token,item.id],(obj)=>{
                        app.toast(this.$t("删除成功"));
                        app.postMessage('template-edit');
			        }); 
                }
            );
        }
    }
}
</script>