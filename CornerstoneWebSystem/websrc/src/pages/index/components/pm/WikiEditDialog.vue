<style scoped>

</style>
<i18n>
    {
    "en": {
        "知识库": "Wiki",
        "知识库名称": "Wiki Name",
        "名称": "Name",
        "访问权限": "Permission",
        "所有项目成员都可以查看和编辑": "All project members can view and edit",
        "只允许拥有以上角色的成员查看和编辑": "Only members with the above roles are allowed to view and edit",
        "删除": "Delete",
        "继续创建下一个": "Continue to create next",
        "保存": "Save",
        "删除成功": "Success",
        "创建成功": "Success",
        "保存成功": "Success",
        "确认要删除知识库吗？": "Are you sure you want to delete the wiki {0} ?",
        "创建": "Add"
    },
    "zh_CN": {
        "知识库": "知识库",
        "知识库名称": "知识库名称",
        "名称": "名称",
        "访问权限": "访问权限",
        "所有项目成员都可以查看和编辑": "所有项目成员都可以查看和编辑",
        "只允许拥有以上角色的成员查看和编辑": "只允许拥有以上角色的成员查看和编辑",
        "删除": "删除",
        "继续创建下一个": "继续创建下一个",
        "保存": "保存",
        "删除成功": "删除成功",
        "创建成功": "创建成功",
        "保存成功": "保存成功",
        "确认要删除知识库吗？": "确认要删除知识库 “{0}” 吗？",
        "创建": "创建"
    }
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('知识库')" width="500"  @on-ok="confirm">

    <Form  ref="form" :rules="formRule" :model="formItem" label-position="top"
            style="height:400px;padding:15px;">
        <FormItem :label="$t('名称')" prop="name">
            <Input v-model.trim="formItem.name" :placeholder="$t('知识库名称')"></Input>
        </FormItem>

        <FormItem :label="$t('访问权限')">
            <i-Switch v-model="formItem.enableRole"></i-Switch>
            <span v-if="formItem.enableRole==false" style="margin-left:10px">{{$t('所有项目成员都可以查看和编辑')}}</span>
        </FormItem>


        <FormItem v-if="formItem.enableRole" >
            <CheckboxGroup v-model="formItem.roles">
                    <Checkbox v-for="item in roleList" :key="item.id" :label="item.id">{{item.name}}</Checkbox>
            </CheckboxGroup>
            <div style="color:#666;font-size:12px">{{$t('只允许拥有以上角色的成员查看和编辑')}}</div>
        </FormItem>

         <FormItem label="" v-if="formItem.id>0">
            <Button @click="deleteItem" type="error" size="large" >{{$t('删除')}}</Button>
        </FormItem>
    </Form>


    <div slot="footer">
        <Row>
            <Col span="12" style="text-align:left;padding-top:5px"> <Checkbox v-if="formItem.id==0" v-model="continueCreate" size="large">{{$t('继续创建下一个')}}</Checkbox>&nbsp;</Col>
            <Col span="12" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{formItem.id>0?$t('保存'):$t('创建')}}</Button></Col>
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
                    name:null,
                    projectId:null,
                    roles:[],
                    enableRole:false,
                },
                formRule:{
                    name:[vd.req,vd.name],
                },
                roleList:[]
            }
        },
        methods: {
            pageLoad(){
                this.formItem.projectId=app.projectId;
                this.loadRole();
                if(this.args.id){
                    app.invoke('BizAction.getWikiInfoById',[app.token,this.args.id],(info)=>{
                        this.formItem=info;
                    })
                }
            },
            loadRole(){
                app.invoke('BizAction.getRoleInfoList',[app.token,1],(list)=>{
                    this.roleList=list;
                })
            },
            deleteItem(){
                app.confirm(this.$t('确认要删除知识库吗？',[this.formItem.name]),()=>{
                    app.invoke('BizAction.deleteWiki',[app.token,this.formItem.id],(info)=>{
                        app.toast(this.$t('删除成功'))
                        app.postMessage('wiki.edit')
                        this.showDialog=false;
                    })
                })
            },
            confirm(){
                this.$refs.form.validate((r)=>{if(r)this.confirmForm()});
            },
            confirmForm(){
                this.formItem.condition=this.query;
                var action=this.formItem.id==0?"BizAction.addWiki":"BizAction.updateWiki";
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.toast(this.formItem.name+this.formItem.id==0?this.$t('创建成功'):this.$t('保存成功'))
                    app.postMessage('wiki.edit')
                    if(!this.continueCreate){
                        this.showDialog=false;
                    }else{
                        this.$refs.form.resetFields();
                    }
			    })
            }
        }
    }
</script>
