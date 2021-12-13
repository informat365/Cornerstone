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
		"主机": "主机",
		"查询": "查询",
		"批量操作": "批量操作",
		"编辑": "编辑",
		"创建": "创建",
		"应用": "应用",
		"端口": "端口",
		"创建日期": "创建日期",
		"设置": "设置",
		"更新成功": "更新成功"
    },
	"zh_CN": {
		"名称": "名称",
		"分组": "分组",
		"主机": "主机",
		"查询": "查询",
		"批量操作": "批量操作",
		"编辑": "编辑",
		"创建": "创建",
		"应用": "应用",
		"端口": "端口",
		"创建日期": "创建日期",
		"设置": "设置",
		"更新成功": "更新成功"
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
                      <Input @on-change="loadData(true)" v-model="formItem.machineName"  type="text" :placeholder="$t('主机')"></Input>
                    </FormItem>
                    <FormItem>
                        <Button @click="loadData(true)" type="default">{{$t('查询')}}</Button>
                    </FormItem>
             </Form>

          </Col>
        <Col span="6" class="opt-right">
            <Form inline>
                <FormItem>
                    <Dropdown placement="bottom-end">
                        <Button type="default" >{{$t('批量操作')}}
                                <Icon style="margin-left:5px" type="ios-arrow-down"></Icon>
                        </Button>
                        <DropdownMenu slot="list" style="text-align:left">
                            <DropdownItem @click.native="batchEdit()" :disabled="selectCount()==0">{{$t('编辑')}}</DropdownItem>
                        </DropdownMenu>
                    </Dropdown>
                </FormItem>
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
                    <th style="width:60px"><Checkbox  v-model="isSelectAll"></Checkbox></th>
                    <th>{{$t('名称')}}</th>
                    <th style="width:150px">{{$t('分组')}}</th> 
                    <th style="width:150px">{{$t('主机')}}</th> 
                    <th style="width:150px">{{$t('应用')}}</th> 
                    <th style="width:80px">{{$t('端口')}}</th>
                    <th style="width:140px;">{{$t('创建日期')}}</th>
                    <th style="width:120px;"></th>
                    </tr>
            </template>
            <template slot="tbody">
                <tr v-for="item in tableData" :key="item.id" class="table-row">
                    <td><Checkbox  v-model="item.isSelect"></Checkbox></td>
                    <td>{{item.name}}</td>
                    <td>{{item.group}}</td>
                    <td>{{item.machineName}}</td>
                    <td>{{item.applicationName}}</td>
                    <td>{{item.port}}</td>
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
                group:null,
                machineName:null,
            },
            isSelectAll:false,
            tableData:[],
        }
    },
    watch:{
        isSelectAll(val){
            for(var i=0;i<this.tableData.length;i++){
                this.tableData[i].isSelect=val;
            }
        }
    },
   
    methods:{
        pageLoad(){
            this.loadPageStatus("CmdbInstancePage")
            this.loadData();
        },
        pageMessage(type){
            if(type=='cmdbinstance.edit'){
                this.loadData(true);
            }
        },
        selectCount(){
            var t=0;
            for(var i=0;i<this.tableData.length;i++){
                if(this.tableData[i].isSelect){
                    t++;
                }
            }
            return t;
        },
        batchEdit(){
            if(this.selectCount()==0){
                return;
            }
            app.showDialog(CmdbInstanceEditDialog,{
                editCallback:(item)=>{
                    this.batchUpdate(item);
                }
            })
        },
        batchUpdate(item){
            var list=[];
            for(var i=0;i<this.tableData.length;i++){
                if(this.tableData[i].isSelect){
                    list.push(this.tableData[i].id)
                }
            }
            //
            app.invoke('BizAction.batchUpdateCmdbInstance',[app.token,list,item],()=>{
                app.toast('更新成功');
            }) 
        },
        loadData(resetPage){
            this.isSelectAll=false;
            if(resetPage){
                this.pageQuery.pageIndex=1;
            }
            var query=copyObject(this.pageQuery,this.formItem)
            this.savePageStatus("CmdbInstancePage")
            app.invoke('BizAction.getCmdbInstanceList',[app.token,query],(info)=>{
                this.tableData=info.list;
                this.pageQuery.total=info.count;
            })   
        },
        settingItem(item){
            app.showDialog(CmdbInstanceEditDialog,{
                id:item.id
            })
        },
        showCreateDialog(){
            app.showDialog(CmdbInstanceEditDialog)
        }
    }
}
</script>