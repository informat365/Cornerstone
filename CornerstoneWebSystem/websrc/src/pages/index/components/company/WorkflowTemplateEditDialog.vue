<style scoped>

</style>


<i18n>
    {
    "en": {
    "流程模板":"Workflow Template",
    "从复制":"Copy from【{0}】",
    "名称":"Name",
    "流程名称":"Workflow name",
    "保存":"Save",
    "创建":"Create",
    "操作成功":"Success"
    },
    "zh_CN": {
    "流程模板":"流程模板",
    "从复制":"从【{0}】复制",
    "名称":"名称",
    "流程名称":"流程名称",
    "保存":"保存",
    "创建":"创建",
    "操作成功":"操作成功"
    }
    }
</i18n>

 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('流程模板')" width="500"  @on-ok="confirm">
        <div style="min-height:300px;padding:15px">
            <Alert v-if="formItem.copyName" type="info">{{$t('从复制',[formItem.copyName])}}</Alert>
            <Form  ref="form" :rules="formRule" :model="formItem" label-position="top" >
                <FormItem :label="$t('名称')" prop="name">
                    <Input :placeholder="$t('流程名称')" v-model.trim="formItem.name" ></Input>
                </FormItem>
            </Form>
        </div>
        <div slot="footer">
            <Row>
                <Col span="24" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{formItem.id>0?$t('保存'):$t('创建')}}</Button></Col>
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
                    name:null,
                },
                formRule:{
                    name:[vd.req,vd.name],
                }
            }
        },
        methods: {
            pageLoad(){
                this.formItem.copyId=this.args.copyId;
                this.formItem.copyName=this.args.copyName;
            },
            confirm(){
                this.$refs.form.validate((r)=>{
                    if(r){
                        this.confirmForm();
                    }
                });
            },
            confirmForm(){
                app.invoke("WorkflowAction.addWorkflowDefine",[app.token,this.formItem],(id)=>{
                    app.toast(this.$t('操作成功'))
                    app.postMessage('workflow.template.edit')
                    window.open('/wfeditor.html#/'+id+"/info")
                    this.showDialog=false;
			    })
            }
        }
    }
</script>
