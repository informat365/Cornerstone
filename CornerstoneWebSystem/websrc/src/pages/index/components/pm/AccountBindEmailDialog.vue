<style scoped>
   
</style>
<i18n>
{
    "en": {
        "绑定邮箱": "Bind Email",
        "绑定邮箱后": "After the mail is bound, system push will be sent to your bound mailbox.",
        "点击发送邮件按钮": "Click the Send Email button, the system will send a binding email to your email, click on the link to complete the binding.",
        "请查看邮箱，完成绑定": "Please check the mailbox to complete the binding",
        "邮箱地址":"Email",
        "发送邮件":"Send"
    },
    "zh_CN": {
        "绑定邮箱": "绑定邮箱",
        "邮箱地址":"邮箱地址",
        "绑定邮箱后": "绑定邮箱后，系统推送会发送到您绑定的邮箱中。",
        "点击发送邮件按钮": "点击发送邮件按钮，系统会发送一封绑定邮件到您的邮箱，点击其中的链接就可以完成绑定。",
        "请查看邮箱，完成绑定": "请查看邮箱，完成绑定",
        "发送邮件":"发送邮件"
    }
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('绑定邮箱')" width="500" >
    <Alert>
        {{$t('绑定邮箱后')}}
    </Alert>
    <Form ref="form" :rules="formRule" :model="formItem" label-position="top" 
        style="padding:30px;margin-bottom:10px">
         <FormItem :label="$t('邮箱地址')" prop="email">
            <Input v-model.trim="formItem.email" ></Input>
        </FormItem>
         <FormItem label="">
             {{$t('点击发送邮件按钮')}}
         </FormItem>
    </Form>
    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> 
            <Button @click="confirm" type="default" size="large" >
                {{$t('发送邮件')}}
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
                formItem:{
                   email:null,
                },
                formRule:{
                    email:[vd.req,vd.email],
                },
            }
        },
        methods: {
            pageLoad(){
               
            },
            confirm(){
                this.$refs.form.validate((r)=>{
                    if(r){
                        this.confirmForm();
                    }
                });
            },
            confirmForm(){
                app.invoke('BizAction.bindEmail',[app.token,this.formItem.email],(info)=>{
                    app.toast(this.$t('请查看邮箱，完成绑定'));
                    this.showDialog=false;
			    })
            }
        }
    }
</script>