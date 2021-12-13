<style scoped>
   .wrap-box{
       display: flex;
       height:calc(100vh - 220px);
       padding:15px;
       margin-bottom:10px;
       flex-direction: row;
   }
   .left{
       width:250px;
       padding-right:10px;
   }
   .right{
       flex:1;
   }
</style>
<i18n>
{
    "en": {
        "Pipeline": "Pipeline",
        "名称":"Name",
        "分组":"Group",
        "定时执行":"Schedule",
        "pipeline名称":"pipeline name",

        "cron表达式":"cron",
        "访问权限":"permission",
        "所有项目成员都可以执行":"All project members can execute",
        "只有以上角色的成员才能执行Pipeline":"Only members of the above roles can execute Pipeline",
        "删除":"Delete",
        "复制到其它项目":"Copy to other project",
        "脚本":"Script",
        "查看文档":"Help",
        "继续创建下一个":"Continue to create next",
        "保存":"Save",
        "创建":"Create",
        "确认要删除pipeline":"Are you sure you want to delete pipeline {0}?",
        "保存成功":"Success",
        "创建成功":"Success",
        "复制pipeline":"Copy pipeline"
    },
    "zh_CN": {
        "Pipeline": "Pipeline",
        "名称":"名称",
        "分组":"分组",
        "定时执行":"定时执行",
        "pipeline名称":"pipeline名称",

        "cron表达式":"cron表达式",
        "访问权限":"访问权限",
        "所有项目成员都可以执行":"所有项目成员都可以执行",
        "只有以上角色的成员才能执行Pipeline":"只有以上角色的成员才能执行Pipeline",
        "删除":"删除",
        "复制到其它项目":"复制到其它项目",
        "脚本":"脚本",
        "查看文档":"查看文档",
        "继续创建下一个":"继续创建下一个",
        "保存":"保存",
        "创建":"创建",
        "确认要删除pipeline":"确认要删除pipeline【{0}】吗？",
        "保存成功":"保存成功",
        "创建成功":"创建成功",
        "复制pipeline":"复制pipeline"
    }
}
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" title="Pipeline" width="1200"  @on-ok="confirm">
    <div class="wrap-box">
        <Form  class="left"  @submit.native.prevent ref="form" :rules="formRule" :model="formItem" label-position="top" >
       
                 <FormItem :label="$t('名称')" prop="name">
                    <Input v-model.trim="formItem.name" :placeholder="$t('pipeline名称')"></Input>
                </FormItem>
                 <FormItem :label="$t('分组')" prop="group">
                    <Input v-model.trim="formItem.group" :placeholder="$t('分组')"></Input>
                </FormItem>
                 <FormItem :label="$t('定时执行')"  prop="cron" style="position: relative;">
                    <Input v-model.trim="formItem.cron" :placeholder="$t('cron表达式')"></Input>
                </FormItem>

        <FormItem :label="$t('访问权限')">  
            <i-Switch v-model="formItem.enableRole"></i-Switch>
            <span v-if="formItem.enableRole==false" style="margin-left:10px">{{$t('所有项目成员都可以执行')}}</span>
        </FormItem>

        <FormItem v-if="formItem.enableRole">
            <CheckboxGroup v-model="formItem.roles" >
                    <Checkbox v-for="item in roleList" :key="item.id" :label="item.id">{{item.name}}</Checkbox>
            </CheckboxGroup>
            <div style="color:#666;font-size:12px">{{$t('只有以上角色的成员才能执行Pipeline')}}</div>
        </FormItem> 

        
        <FormItem label="" v-if="formItem.id>0">
            <Button @click="deleteItem" type="error" size="large" >{{$t('删除')}}</Button>
            <Button style="margin-left:10px" @click="copyItem" type="default" size="large" >{{$t('复制到其它项目')}}</Button>
        </FormItem> 
        </Form>

        <Form class="right">
         <FormItem :label="$t('脚本')"  prop="script" >
            <div class="form-item-title-append">
                <IconButton @click="showHelp()"  icon="ios-help-circle-outline" :title="$t('查看文档')"></IconButton>
            </div>
            <div>
                <MonacoEditor :style="editorStyle" ref="editor" mode="javascript"  v-model="formItem.script"></MonacoEditor>
            </div>
        </FormItem>
        </Form>
    </div>
    
    
    <div slot="footer">
        <Row>
            <Col span="12" style="text-align:left;padding-top:5px"> <Checkbox v-if="formItem.id==0" v-model="continueCreate" size="large">{{$t('继续创建下一个')}}</Checkbox></Col>
            <Col span="12" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{formItem.id>0?$t('保存'):$t('创建')}}</Button></Col>
         </Row>
           
    </div>

    </Modal>
</template>


<script>
var demoCode=
`var pipeline={

}
`
export default {
        mixins: [componentMixin],
        data () {
            return {
                continueCreate:false,
                formItem:{
                    id:0,
                    projectId:0,
                    name:null,
                    group:null,
                    roles:[],
                    enableRole:false,
                    script:demoCode
                },
                editorFullScreen:false,
                editorStyle:"height:calc(100vh - 250px)",
                formRule:{
                    name:[vd.req,vd.name],
                    group:[vd.name],
                    cron:[vd.desc],
                },
                roleList:[]
            }
        },
        watch:{
            showDialog(val){
                if(val==false){
                    this.removeKeyListener();
                }
            }
        },
        methods: {
            pageLoad(){
                if(this.args.id){
                    this.addKeyListener();
                    app.invoke('BizAction.getProjectPipelineInfoById',[app.token,this.args.id],(info)=>{
                        this.formItem=info;
                    })
                }else{
                    this.formItem.projectId=this.args.projectId;
                }
                 this.loadRole();
            },
            showHelp(){
                window.open('https://www.cornerstone365.cn/doc/doc.html#/devops');
            },
            
            loadRole(){
                app.invoke('BizAction.getRoleInfoList',[app.token,1],(list)=>{
                    this.roleList=list;
                })
            },
            deleteItem(){
                app.confirm(this.$t('确认要删除pipeline',[this.formItem.name]),()=>{
                    app.invoke('BizAction.deleteProjectPipeline',[app.token,this.formItem.id],(info)=>{
                        app.postMessage('pipeline.edit')
                        this.showDialog=false;
                    })
                })
            },
            addKeyListener(){
                this.$el.addEventListener("keydown",this.keyHandler, false);
            },
            removeKeyListener(){
                this.$el.removeEventListener("keydown",this.keyHandler)
            },
            keyHandler(e){
                if (e.keyCode == 83 && (navigator.platform.match("Mac") ? e.metaKey : e.ctrlKey)) {
                    e.preventDefault();
                    this.confirm();
                }
                //return true; 
            },
            copyItem(){
                app.showDialog(CopySelectMyProjectDialog,{
                    callback:this.confirmCopy
                })
            },
            confirmCopy(projectList){
                 app.showDialog(MultiOperateDialog,{
                    title:this.$t("复制pipeline"),
                    runCallback:this.copyAction,
                    finishCallback:this.finishCopyRun,
                    itemList:projectList,
                })
            },
            finishCopyRun(){
                app.postMessage('pipeline.edit');
            },
            copyAction(item,success,error){
                this.formItem.projectId=item;
                this.formItem.id=-1;
                app.invoke('BizAction.createProjectPipeline',[app.token,this.formItem],(info)=>{
                    success();  
                },error)
            },
            confirm(){
                this.$refs.form.validate((r)=>{
                    if(r){this.confirmForm()}
                });
            },
            confirmForm(){
                this.formItem.script=this.$refs.editor.getValue();
                var action=this.formItem.id>0?'BizAction.updateProjectPipeline':'BizAction.createProjectPipeline';
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.toast('【'+this.formItem.name+'】'+(this.formItem.id>0? this.$t('保存成功'):this.$t('创建成功')))
                    app.postMessage('pipeline.edit')
                    if(this.formItem.id==0){
                        if(!this.continueCreatet){
                            this.showDialog=false;
                        }else{
                            this.$refs.form.resetFields();
                        }
                    }
                   
			    })
            }
        }
    }
</script>