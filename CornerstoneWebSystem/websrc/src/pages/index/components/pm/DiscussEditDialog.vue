<style scoped>
</style>
<i18n>
{
    "en": {
        "讨论管理": "Discuss",
        "名称":"Name",
        "讨论名称":"Name",
        "讨论内容":"Content",
        "关联项目":"Project",
        "选择项目":"Choose project",
        "项目成员均可以参与讨论":"Project members can participate in the discussion",
        "参与人":"Participants",
        "新增参与人":"Add",
        "删除":"Delete",
    
        "确认要删除讨论":"Are you sure you want to delete the discussion [{0}]?",
        "删除成功":"Success",
        "请选择参与人":"Choose participants",
        "保存成功":"Success",
        "创建成功":"Success",
        "保存":"Save",
        "创建":"Create"
    },
    "zh_CN": {
        "讨论管理": "讨论管理",
        "讨论名称":"讨论名称",
        "名称":"名称",
        "讨论内容":"讨论内容",
        "关联项目":"关联项目",
        "选择项目":"选择项目",
        "项目成员均可以参与讨论":"项目成员均可以参与讨论",
        "参与人":"参与人",
        "新增参与人":"新增参与人",
        "删除":"删除",
    
        "确认要删除讨论":"确认要删除讨论【{0}】吗？",
        "删除成功":"删除成功",
        "请选择参与人":"请选择参与人",
        "保存成功":"保存成功",
        "创建成功":"创建成功",
        "保存":"保存",
        "创建":"创建"
    }
}
</i18n>  
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('讨论管理')" width="700" >
 
    <Form  ref="form" :rules="formRule" :model="formItem"  label-position="top" style="height:550px;padding:15px;">
         <FormItem :label="$t('名称')" prop="name">
            <Input v-model.trim="formItem.name" :placeholder="$t('讨论名称')"></Input>
        </FormItem>
        <FormItem :label="$t('讨论内容')" prop="content">
            <Input  v-model.trim="formItem.content" type="textarea" :rows="3" ></Input>
        </FormItem>
        <FormItem :label="$t('关联项目')">
            <Select transfer clearable :placeholder="$t('选择项目')" v-model="formItem.projectId" >
                <Option v-for="item in projectList" :key="item.id" :value="item.id">{{item.name}}</Option>
            </Select>    
            <div>{{$t('项目成员均可以参与讨论')}}</div>
        </FormItem>
         <FormItem :label="$t('参与人')">
            <Tag v-for="item in formItem.memberInfos" :key="'s'+item.id" closable @on-close="removeUser(item)">{{item.name}}</Tag>
            <Button type="default" size="small" icon="md-add" @click="selectCompanyUser()">{{$t('新增参与人')}}</Button>
        </FormItem>

        <FormItem label="" v-if="formItem.id>0">
            <Button @click="deleteItem" type="error" size="large" >{{$t('删除')}}</Button>
        </FormItem> 
    </Form>

    
    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> 
            <Button @click="confirm" type="default" size="large" >
                {{formItem.id>0?$t('保存'):$t('创建')}}
            </Button>
            </Col>
        </Row>
           
    </div>

    </Modal>
</template>


<script>
    export default {
        mixins: [componentMixin],
        data () {
            return {
                continueCreate:false,
                formItem:{
                    id:0,
                    projectId:0,
                    name:null,
                    description:null,
                    status:1,
                    memberInfos:[],
                },
                formRule:{
                    name:[vd.req,vd.name],
                    content:[vd.req,vd.desc],
                },
                projectList:[]
            }
        },
        methods: {
            pageLoad(){
                console.log(this.args)
                this.loadProjectList();
                if(this.args.id){
                    this.loadData();
                }
            },
            loadData(){
                app.invoke('BizAction.getDiscussById',[app.token,this.args.id],(info)=>{
                    this.formItem=info;
                });
            },
            loadProjectList(){
                app.invoke( "BizAction.getMyProjectList",[app.token],list => {
                    this.projectList=list;
                })
            },
            selectCompanyUser(){
                app.showDialog(CompanyUserSelectDialog,{
                    callback:(t)=>{
                        for(var i=0;i<t.length;i++){
                            this.addToList(this.formItem.memberInfos,t[i])
                        }
                    }
                })
            },
            removeUser(item){
                this.removeFromList(this.formItem.memberInfos,item)
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
            deleteItem(){
                app.confirm(this.$t('确认要删除讨论',[this.formItem.name]),()=>{
                    app.invoke('BizAction.deleteDiscuss',[app.token,this.formItem.id],(info)=>{
                        app.toast(this.$t('删除成功'))
                        app.postMessage('discuss.edit')
                        this.showDialog=false;
                    })
                })
            },
            confirm(){
                this.$refs.form.validate((r)=>{if(r)this.confirmForm()});
            },
            confirmForm(){
                //
                this.formItem.members=[];
                this.formItem.memberInfos.forEach((item)=>{
                    this.formItem.members.push(item.id)
                })
                if(this.formItem.members.length==0){
                    app.toast(this.$t('请选择参与人'));
                    return;
                }
                var action=this.formItem.id>0?'BizAction.updateDiscuss':'BizAction.addDiscuss';
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.toast(this.formItem.name+(this.formItem.id>0? this.$t('保存成功'):this.$t('创建成功')))
                    app.postMessage('discuss.edit')
                    this.showDialog=false;
			    })
            }
        }
    }
</script>