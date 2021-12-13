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
    "创建系统Hook":"Create system hook",
    "名称":"Name",
    "备注":"Remark",
    "类型":"Type",
    "cron表达式":"cron",
    "应用于所有项目":"Applied to all projects",
    "应用于项目":"Applied to the project",
    "删除":"Delete",
    "禁用":"Disable",
    "启用":"Enable",
    "脚本":"Script",
    "保存":"Save",
    "创建":"Create",
    "操作成功":"Success",
    "删除成功":"Delete success",
    "保存成功":"Save success",
    "创建成功":"Create success",
    "请选择系统Hook应用的项目":" Please select the project ",
    "请填写cron表达式":"Please input cron",
    "确认要删除系统Hook吗？":"Are you sure you want to delete the system Hook “{0}” ?",
    "继续创建下一个":"Continue to create the next"
    },
    "zh_CN": {
    "创建系统Hook":"创建系统Hook",
    "名称":"名称",
    "备注":"备注",
    "类型":"类型",
    "cron表达式":"cron表达式",
    "应用于所有项目":"应用于所有项目",
    "应用于项目":"应用于项目",
    "删除":"删除",
    "禁用":"禁用",
    "启用":"启用",
    "脚本":"脚本",
    "保存":"保存",
    "创建":"创建",
    "操作成功":"操作成功",
    "删除成功":"删除成功",
    "保存成功":"保存成功",
    "创建成功":"创建成功",
    "请选择系统Hook应用的项目":"请选择系统Hook应用的项目",
    "请填写cron表达式":"请填写cron表达式",
    "确认要删除系统Hook吗？":"确认要删除系统Hook “{0}” 吗？",
    "继续创建下一个":"继续创建下一个"
    }
    }
</i18n>

 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('创建系统Hook')" width="1200"  @on-ok="confirm">
    <div class="wrap-box">
        <Form  class="left"  @submit.native.prevent ref="form" :rules="formRule" :model="formItem" label-position="top" >

            <FormItem :label="$t('名称')" prop="name">
                <Input v-model.trim="formItem.name" :placeholder="$t('名称')"></Input>
            </FormItem>
            <FormItem :label="$t('备注')" prop="remark">
                <Input v-model.trim="formItem.remark" :placeholder="$t('备注')"></Input>
            </FormItem>
             <FormItem :label="$t('类型')" prop="type">
                <DataDictRadio v-model="formItem.type"
                         type="SystemHook.type"></DataDictRadio>
            </FormItem>
             <FormItem v-if="formItem.type==2" label="cron" prop="cron">
                <Input v-model.trim="formItem.cron" :placeholder="$t('cron表达式')"></Input>
            </FormItem>
            <FormItem v-if="formItem.type==1" :label="$t('应用于所有项目')">
               <i-Switch v-model="formItem.isAllProject" @change="changeAllProject"/>
            </FormItem>
            <FormItem v-if="formItem.type==1&&formItem.isAllProject==false" :label="$t('应用于项目')">
                <Select transfer multiple v-model="formItem.projectIds" >
                    <Option v-for="item in projectList" :value="item.id" :key="item.id">{{ item.name }}</Option>
                </Select>
            </FormItem>
            <FormItem label="" v-if="formItem.id>0">
                <Button @click="deleteItem" type="error" >{{$t('删除')}}</Button>
                <Button v-if="formItem.status==1" style="margin-left:10px" @click="setEnable(false)" type="default">{{$t('禁用')}}</Button>
                <Button v-if="formItem.status==2" style="margin-left:10px" @click="setEnable(true)" type="default">{{$t('启用')}}</Button>
            </FormItem>
        </Form>

        <Form class="right">
         <FormItem :label="$t('脚本')"  prop="script">
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
`var systemHook={

}
`
export default {
        mixins: [componentMixin],
        data () {
            return {
                continueCreate:false,
                formItem:{
                    id:0,
                    name:null,
                    remark:null,
                    type:1,
                    cron:null,
                    projectIds:[],
                    isAllProject:false,
                    script:demoCode
                },
                editorFullScreen:false,
                editorStyle:"height:calc(100vh - 250px)",
                formRule:{
                    name:[vd.req,vd.name],
                    group:[vd.name],
                    remark:[vd.desc],
                    cron:[vd.req,vd.desc],
                },
                projectList:[]
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
                    app.invoke('SystemHookAction.getSystemHookById',[app.token,this.args.id],(info)=>{
                        this.formItem=info;
                    })
                }
                this.loadProjects();
            },
            changeAllProject(){
                if(this.formItem.isAllProject){
                    this.formItem.projectIds=[];
                }
            },
            loadProjects(){
                var query={
                    pageIndex:1,
                    pageSize:9999
                }
                app.invoke('BizAction.getAllProjectList',[app.token,query],(info)=>{
                    this.projectList=info.list;
                })
            },
            setEnable(enable){
                var status=enable?1:2;
                app.invoke('SystemHookAction.updateSystemHookStatus',[app.token,this.formItem.id,status],(info)=>{
                    app.toast(this.$t('操作成功'))
                    app.postMessage('systemhook.edit')
                    this.showDialog=false;
                })
            },
            deleteItem(){
                app.confirm(this.$t('确认要删除系统Hook吗？',[this.formItem.name]),()=>{
                    app.invoke('SystemHookAction.deleteSystemHook',[app.token,this.formItem.id],(info)=>{
                        app.toast(this.$t('删除成功'))
                        app.postMessage('systemhook.edit')
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

            },
            confirm(){
                this.$refs.form.validate((r)=>{if(r)this.confirmForm()});
            },
            confirmForm(){
                if(this.formItem.type==1&&this.formItem.isAllProject==false){
                    if(this.formItem.projectIds.length==0){
                        app.toast(this.$t('请选择系统Hook应用的项目'));
                        return;
                    }
                }
                if(this.formItem.type==2){
                    if(this.formItem.cron==null||this.formItem.cron==''){
                        app.toast(this.$t('请填写cron表达式'));
                        return;
                    }
                }
                this.formItem.script=this.$refs.editor.getValue();
                var action=this.formItem.id>0?'SystemHookAction.updateSystemHook':'SystemHookAction.addSystemHook';
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.toast(this.formItem.name+(this.formItem.id>0?this.$t('保存成功'):this.$t('创建成功')))
                    app.postMessage('systemhook.edit')
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
