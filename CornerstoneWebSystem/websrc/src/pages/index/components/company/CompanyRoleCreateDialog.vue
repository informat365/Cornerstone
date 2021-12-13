<style scoped>
</style>


<i18n>
    {
    "en": {
    "编辑角色":"Edit role",
    "名称":"Name",
    "角色名称":"Role name",
    "备注":"Remark",
    "备注信息":"Remark",
    "删除":"Delete",
    "继续创建下一个":"Continue to create the next",
    "创建":"Create",
    "操作成功":"Success",
    "确定要删除角色吗？删除后关联了此角色的用户将不再有本角色赋予的权限!":"Are you sure you want to delete the role {0}? Users who have been associated with this role after deletion will no longer have the permissions granted by this role!"
    },
    "zh_CN": {
    "编辑角色":"编辑角色",
    "名称":"名称",
    "角色名称":"角色名称",
    "备注":"备注",
    "备注信息":"备注信息",
    "删除":"删除",
    "继续创建下一个":"继续创建下一个",
    "创建":"创建",
    "操作成功":"操作成功",
    "确定要删除角色吗？删除后关联了此角色的用户将不再有本角色赋予的权限!":"确定要删除角色{0}吗？删除后关联了此角色的用户将不再有本角色赋予的权限!"
    }
    }
</i18n>

 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('编辑角色')" width="500"  @on-ok="confirm">

    <Form  ref="form" :rules="formRule" :model="formItem" label-position="top" style="height:300px;padding:15px">

        <FormItem :label="$t('名称')" prop="name">
                    <Input :placeholder="$t('角色名称')" v-model.trim="formItem.name" ></Input>
        </FormItem>

        <FormItem :label="$t('备注')" prop="remark">
                    <Input :placeholder="$t('备注信息')" type="textarea" v-model.trim="formItem.remark" ></Input>
        </FormItem>


        <FormItem v-if="formItem.id>0"  label="">
           <Button @click="deleteField" type="error">{{$t('删除')}}</Button>
        </FormItem>
    </Form>


    <div slot="footer">
        <Row>
            <Col span="12" style="text-align:left;padding-top:5px"> <Checkbox v-model="continueCreate" size="large">{{$t('继续创建下一个')}}</Checkbox></Col>
            <Col span="12" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{formItem.id==0?$t('创建'):$t('保存')}}</Button></Col>
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
                    remark:null,
                },
                formRule:{
                    name:[vd.req,vd.name2_10],
                    remark:[vd.desc],
                },
            }
        },
        methods: {
            pageLoad(){
                if(this.args.item){
                    this.formItem=this.args.item;
                }else{
                    this.formItem.type=this.args.type
                }
            },
            confirm(){
                this.$refs.form.validate((r)=>{if(r)this.confirmForm()});
            },
            deleteField(){
                app.confirm(this.$t('确定要删除角色吗？删除后关联了此角色的用户将不再有本角色赋予的权限!',[this.formItem.name]),()=>{
                     app.invoke('BizAction.deleteRole',[app.token,this.formItem.id],(info)=>{
                        app.toast(this.$t('操作成功'))
                        app.postMessage('role.edit')
                        this.showDialog=false;
                    })
                })
            },
            confirmForm(){
                var action=this.formItem.id==0?'BizAction.addRole':"BizAction.updateRole";
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.toast(this.$t('操作成功'))
                    app.postMessage('role.edit')
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
