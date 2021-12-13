<style scoped>
</style>
<i18n>
{
    "en": {
        "仪表盘": "Dashboard",
        "名称": "Name",
        "类型": "Type",
        "选择仪表盘类型": "Choose dashboard type", 
        "项目":"Project",
        "选择项目":"Choose project",
        "分享给其它用户":"Share with others",
        "新增用户":"Add",
        "删除":"Delete",
        "保存":"Save",
        "空仪表盘":"Empty",
        "项目仪表盘":"Project dashboard",
        "操作成功":"Success",
        "确认要删除":"Are you sure you want to delete「{0}」？"
    },
    "zh_CN": {
        "仪表盘": "仪表盘",
        "名称": "名称",
        "类型": "类型",
        "选择仪表盘类型": "选择仪表盘类型", 
        "项目":"项目",
        "选择项目":"选择项目",
        "分享给其它用户":"分享给其它用户",
        "新增用户":"新增用户",
        "删除":"删除",
        "保存":"保存",
        "空仪表盘":"空仪表盘",
        "项目仪表盘":"项目仪表盘",
        "操作成功":"操作成功",
        "确认要删除":"确认要删除「{0}」吗？"
    }
}
</i18n> 
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('仪表盘')" width="500">

    <Form   @submit.native.prevent ref="form" :rules="formRule" :model="formItem" label-position="top" style="height:400px;padding:15px">
        <FormItem :label="$t('名称')" prop="name">
            <Input v-model.trim="formItem.name" ></Input> 
        </FormItem>

        <FormItem v-if="formItem.id==0&&loaded" :label="$t('类型')">
            <Select transfer style="width:100%" :placeholder="$t('选择仪表盘类型')" v-model="formItem.type" >
                <Option  v-for="item in typeList" :key="item.id" :value="item.id">{{item.name}}</Option>
            </Select>
        </FormItem>


        <FormItem v-if="formItem.type==2" :label="$t('项目')">
             <Select transfer filterable  style="width:100%" :placeholder="$t('选择项目')" v-model="formItem.projectId" >
                <Option  v-for="item in projectList" :key="item.id" :value="item.id">{{item.name}}</Option>
            </Select>  
        </FormItem>


        <FormItem :label="$t('分享给其它用户')">
            <Tag v-for="item in formItem.shareAccountList" :key="'s'+item.id" closable @on-close="removeUser(item)">{{item.name}}</Tag>
            <Button type="default" size="small" icon="md-add" @click="selectCompanyUser()">{{$t('新增用户')}}</Button>
        </FormItem>


         <FormItem label="" v-if="formItem.id>0">
            <Button @click="deleteItem" type="error" size="large" >{{$t('删除')}}</Button>
        </FormItem> 


    </Form>
    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> 
                <Button @click="confirm" type="default" size="large" >{{$t('保存')}}</Button></Col>
        </Row> 
           
    </div>

    </Modal>
</template>


<script>
    export default {
        mixins: [componentMixin],
        data () {
            return {
                formItem:{
                    id:0,
                    name:null,
                    type:1,
                    projectId:null,
                    shareAccountList:[]
                },
                formRule:{
                    name:[vd.req,vd.name],
                },
                typeList:[
                    {id:1,name:this.$t("空仪表盘")},
                    {id:2,name:this.$t("项目仪表盘")}
                ],
                loaded:false,
                projectList:[],
            }
        },
        methods: {
            pageLoad(){
                if(this.args.id){
                    this.loadData();
                }else{
                    this.loaded=true;
                }
                
                this.loadProjectList();
            },
            selectCompanyUser(){
                app.showDialog(CompanyUserSelectDialog,{
                    callback:(t)=>{
                        for(var i=0;i<t.length;i++){
                            this.addToList(this.formItem.shareAccountList,t[i])
                        }
                    }
                })
            },
            removeUser(item){
                this.removeFromList(this.formItem.shareAccountList,item)
            },
            removeFromList(list,item){
                for(var i=0;i<list.length;i++){
                    var t=list[i];
                    if(t==item){
                        list.splice(i,1);
                        return;
                    }
                }
            },
            addToList(list,t){
                for(var i=0;i<list.length;i++){
                    var q=list[i];
                    if(q.id==t.accountId){
                        return;
                    }
                }
                list.push({
                    id:t.accountId,
                    name:t.title
                })
            },
            loadProjectList(){
                app.invoke( "BizAction.getMyProjectList",[app.token],list => {
                    this.projectList=list;
                })
            },
            loadData(){
                app.invoke('BizAction.getDashboardById',[app.token,this.args.id],(info)=>{
                    if(info.shareAccountList==null){
                        info.shareAccountList=[];
                    }
                    this.formItem=info;
                    this.loaded=true;
                });
            },
            deleteItem(){
                app.confirm(this.$t('确认要删除',[this.formItem.name]),()=>{
                    app.invoke('BizAction.deleteDashboard',[app.token,this.formItem.id],(info)=>{
                        app.toast(this.$t('操作成功'))
                        this.showDialog=false;
                        app.deleteObject('Dashboard.card.dashboardId')
                        if(this.args.callback){
                            this.args.callback();
                        }
                    })
                })
            },
            confirm(){
                this.$refs.form.validate((r)=>{
                    if(r){
                        this.confirmForm()
                    }
                });
            },
            confirmForm(){
                this.formItem.shareAccountIdList=[];
                this.formItem.shareAccountList.forEach(item=>{
                    this.formItem.shareAccountIdList.push(item.id)
                })
                var action=this.formItem.id==0?'BizAction.addDashboard':"BizAction.updateDashboard";
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.toast(this.$t('操作成功'))
                    this.showDialog=false;
                    if(this.args.callback){
                        this.args.callback(info);
                    }
			    })
            }
        }
    }
</script>