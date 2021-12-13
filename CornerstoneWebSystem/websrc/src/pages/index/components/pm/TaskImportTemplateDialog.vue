<style scoped>
   
</style>
<i18n>
{
    "en": {
        "导入方案": "Schema",
        "方案名称":"Name",
        "确定":"OK",
        "保存成功":"Success"
    },
    "zh_CN": {
        "导入方案": "导入方案",
        "方案名称":"方案名称",
        "确定":"确定",
        "保存成功":"保存成功"
    }
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('导入方案')" width="500">

        <Form   ref="form" :rules="formRule" :model="formItem" label-position="top" style="padding:15px;margin-bottom:200px">
            <FormItem  :label="$t('方案名称')" prop="name">
                <Input v-model.trim="formItem.name"></Input>
            </FormItem>
        </Form>
    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> 
                <Button @click="confirm" type="default" size="large" >{{$t('确定')}}</Button>
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
                formItem:{
                   name:null,
                },
                formRule:{
                    name:[vd.req,vd.name],
                },
            }
        },
        methods: {
            pageLoad(){
               this.formItem.projectId=this.args.projectId;
               this.formItem.objectType=this.args.objectType;
               this.formItem.fields=this.args.fields;
            },
            confirm(){
                this.$refs.form.validate((r)=>{
                    if(r)this.confirmForm()
                });
            },
            
            confirmForm(){
                app.invoke("BizAction.createTaskImportTemplate",[app.token,this.formItem],data => {
                    app.toast(this.$t('保存成功'));
                    app.postMessage('import.template.edit')
                    this.showDialog=false;
                });
            }
        }
    }
</script>