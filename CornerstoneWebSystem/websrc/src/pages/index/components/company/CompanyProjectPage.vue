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
    "创建项目":"Create",
    "创建人":"Creater",
    "创建日期":"Create time",
    "成员管理":"Members",
    "设置":"Settings"
    },
    "zh_CN": {
    "名称":"名称",
    "状态":"状态",
    "查询":"查询",
    "创建项目":"创建项目",
    "创建人":"创建人",
    "创建日期":"创建日期",
    "成员管理":"成员管理",
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
                     <DataDictSelect @change="loadData(true)" clearable :placeholder="$t('状态')" type="Project.status" v-model="formItem.status"></DataDictSelect>
                    </FormItem>
                    <FormItem>
                        <Button @click="loadData(true)" type="default">{{$t('查询')}}</Button>
                    </FormItem>
             </Form>
          </Col>
          <Col span="12" style="text-align:right">
            <Form inline>
               <FormItem>
                    <Button @click="showAddProjectDialog" type="default" icon="md-add" class="mr15">{{$t('创建项目')}}</Button>
              </FormItem>
             </Form>
            </Col>
       </Row>
        <div style="padding:20px">
         <BizTable :fixed="true" @change="loadData" :page="pageQuery" :value="tableData" >
            <template slot="thead">
                    <tr>
                    <th>{{$t('名称')}}</th>
                    <th style="width:120px">{{$t('状态')}}</th>
                    <th style="width:150px">{{$t('创建人')}}</th>
                    <th style="width:140px;">{{$t('创建日期')}}</th>
                    <th style="width:200px;"></th>
                    </tr>
            </template>
            <template slot="tbody">
                <tr v-for="item in tableData" :key="item.id" class="table-row">
                    <td >{{item.name}}</td>
                    <td ><DataDictLabel type="Project.status" :value="item.status"/></td>
                    <td>{{item.createAccountName}}</td>
                    <td>{{item.createTime|fmtDateTime}}</td>
                    <td style="text-align:right">
                        <Button style="margin-right:5px" @click="showMemberListDialog(item)" type="default">{{$t('成员管理')}}</Button>
                        <Button  @click="settingCompany(item)" type="default">{{$t('设置')}}</Button>
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
        loadData(resetPage){
            if(resetPage){
                this.pageQuery.pageIndex=1;
            }
            var query=copyObject(this.pageQuery,this.formItem)
            app.invoke('BizAction.getAllProjectList',[app.token,query],(info)=>{
                this.tableData=info.list;
                this.pageQuery.total=info.count;
            })
        },
        showAddProjectDialog(){
            app.showDialog(ProjectCreateDialog)
        },
        showMemberListDialog(item){
            app.showDialog(MemberListDialog,{
                projectUUID:item.uuid
            })
        },
        settingCompany(item){
            app.showDialog(ProjectSettingDialog,{
              uuid:item.uuid,
                switchHome:false
            })
        },
        pageMessage(type){
            if(type==='project.delete'){
                this.loadData();
            }
        }
    }
}
</script>
