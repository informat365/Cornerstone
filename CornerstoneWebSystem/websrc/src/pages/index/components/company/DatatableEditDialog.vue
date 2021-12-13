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
    "创建数据表格":"Create data table",
    "名称":"Name",
    "分组":"Group",
    "备注":"Remark",
    "访问权限":"Access permission",
    "所有项目成员都可以执行":"All project members can execute",
    "只有以上角色的成员才能查看数据表格":"Only members of the above roles can view the data table",
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
    "确认要删除数据表格吗？":"Are you sure you want to delete “{0}”? ",
    "继续创建下一个":"Continue to create the next"
    },
    "zh_CN": {
    "创建数据表格":"创建数据表格",
    "名称":"名称",
    "分组":"分组",
    "备注":"备注",
    "访问权限":"访问权限",
    "所有项目成员都可以执行":"所有项目成员都可以执行",
    "只有以上角色的成员才能查看数据表格":"只有以上角色的成员才能查看数据表格",
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
    "确认要删除数据表格吗？":"确认要删除数据表格 “{0}” 吗？",
    "继续创建下一个":"继续创建下一个"
    }
    }
</i18n>

 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('创建数据表格')" width="1200"  @on-ok="confirm">
    <div class="wrap-box">
        <Form  class="left"  @submit.native.prevent ref="form" :rules="formRule" :model="formItem" label-position="top" >

            <FormItem :label="$t('名称')" prop="name">
                <Input v-model.trim="formItem.name" :placeholder="$t('名称')"></Input>
            </FormItem>
            <FormItem :label="$t('分组')" prop="group">
                <Input v-model.trim="formItem.group" :placeholder="$t('分组')"></Input>
            </FormItem>
            <FormItem :label="$t('备注')" prop="remark">
                <Input v-model.trim="formItem.remark" :placeholder="$t('备注')"></Input>
            </FormItem>
             <FormItem :label="$t('访问权限')">
                <i-Switch v-model="formItem.enableRole"></i-Switch>
                <span v-if="formItem.enableRole==false" style="margin-left:10px">{{$t('所有项目成员都可以执行')}}</span>
            </FormItem>

            <FormItem v-if="formItem.enableRole">
                <CheckboxGroup v-model="formItem.roles" >
                        <Checkbox v-for="item in roleList" :key="item.id" :label="item.id">{{item.name}}</Checkbox>
                </CheckboxGroup>
                <div style="color:#666;font-size:12px">{{$t('只有以上角色的成员才能查看数据表格')}}</div>
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
`var dataTable={

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
                    group:null,
                    remark:null,
                    roles:[],
                    enableRole:false,
                    script:demoCode
                },
                editorFullScreen:false,
                editorStyle:"height:calc(100vh - 250px)",
                formRule:{
                    name:[vd.req,vd.name],
                    group:[vd.name],
                    remark:[vd.desc],
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
                    app.invoke('DataTableAction.getDataTableById',[app.token,this.args.id],(info)=>{
                        this.formItem=info;
                    })
                }
                this.loadRole();
            },

            loadRole(){
                app.invoke('BizAction.getRoleInfoList',[app.token,2],(list)=>{
                    this.roleList=list;
                })
            },
            setEnable(enable){
                var status=enable?1:2;
                app.invoke('DataTableAction.updateDataTableStatus',[app.token,this.formItem.id,status],(info)=>{
                    app.toast(this.$t('操作成功'))
                    app.postMessage('datatable.edit')
                    this.showDialog=false;
                })
            },
            deleteItem(){
                app.confirm(this.$t('确认要删除数据表格吗？',[this.formItem.name]),()=>{
                    app.invoke('DataTableAction.deleteDataTable',[app.token,this.formItem.id],(info)=>{
                        app.toast(this.$t('删除成功'))
                        app.postMessage('datatable.edit')
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
                this.formItem.script=this.$refs.editor.getValue();
                var action=this.formItem.id>0?'DataTableAction.updateDataTable':'DataTableAction.addDataTable';
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.toast(this.formItem.name+(this.formItem.id>0?this.$t('保存成功'):this.$t('创建成功')))
                    app.postMessage('datatable.edit')
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
