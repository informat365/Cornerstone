<style scoped>
</style>
<i18n>
{
  
}
</i18n>  
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" title="创建文件夹" width="500"  @on-ok="confirm">

    <Form  @submit.native.prevent ref="form" :rules="formRule" :model="formItem"  
        label-position="top" style="padding:25px">
        <FormItem label="名称" prop="name">
            <Input v-model.trim="formItem.name"></Input>
        </FormItem>
    </Form>
    
    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"><Button @click="confirm" type="default" size="large" >创建</Button></Col>
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
              
            },
            confirm(){
                this.$refs.form.validate((r)=>{
                    if(r)this.confirmForm()
                });
            },
            confirmForm(){
                app.invoke("BizAction.sftpMkdir",[app.token,this.args.info.token,this.formItem.name],(info)=>{
                    app.postMessage('sftp.dir')
                    this.showDialog=false;
			    })
            }
        }
    }
</script>