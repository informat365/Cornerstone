<style scoped>

</style>
<i18n>
{
    "en": {
        "修改密码": "Change password",
        "原密码": "Original password",
        "新密码": "New password",
        "确认密码": "Repeat password",
        "确定": "OK",
        "两次密码输入不一致":"Please confirm your password",
        "修改成功":"Operation completed",
         "本次登录需修改密码才可继续操作":"Before you can continue,you should modify a new password"
    },
    "zh_CN": {
        "修改密码": "修改密码",
        "原密码": " 原密码",
        "新密码": "新密码",
        "确认密码": "确认密码",
        "确定": "确定",
        "两次密码输入不一致":"两次密码输入不一致",
        "修改成功":"修改成功",
        "本次登录需修改密码才可继续操作":"本次登录需修改密码才可继续操作"
    }
}
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('修改密码')" width="500" @on-cancel="onClose" @on-ok="confirm">

    <Form   ref="form" :rules="formRule" :model="formItem" label-position="top" style="padding:30px;margin-bottom:10px">
        <FormItem :label="$t('原密码')" prop="oldPassword">
            <Input type="password"  v-model.trim="formItem.oldPassword" ></Input>
        </FormItem>

         <FormItem :label="$t('新密码')" prop="newPassword">
            <Input type="password"  v-model.trim="formItem.newPassword" ></Input>
        </FormItem>

         <FormItem :label="$t('确认密码')" prop="newPasswordRepeat">
            <Input type="password" v-model.trim="formItem.newPasswordRepeat" ></Input>
        </FormItem>

    </Form>

    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{$t('确定')}}</Button></Col>
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
                   oldPassword:null,
                   newPassword:null,
                   newPasswordRepeat:null,
                },
                formRule:{
                    oldPassword:[vd.req],
                    newPassword:[vd.req,vd.password],
                    newPasswordRepeat:[vd.req,vd.password],
                },
            }
        },
        methods: {
            pageLoad(){

            },
            onClose(){
                if(app.account.needUpdatePassword){
                    app.toast(this.$t('本次登录需修改密码才可继续操作'));
                    return;
                }else{
                    this.showDialog = false;
                }
            },
            confirm(){
                this.$refs.form.validate((r)=>{
                    if(r){
                        this.confirmForm()
                    }
                });
            },
            confirmForm(){
                if(this.formItem.newPassword!=this.formItem.newPasswordRepeat){
                    app.toast(this.$t('两次密码输入不一致'));
                    return;
                }
                app.invoke('BizAction.updatePassword',[app.token,
                    this.formItem.oldPassword,
                    this.formItem.newPassword],(info)=>{
                    app.toast(this.$t('修改成功'));
                    this.showDialog=false;
			    })
            }
        }
    }
</script>
