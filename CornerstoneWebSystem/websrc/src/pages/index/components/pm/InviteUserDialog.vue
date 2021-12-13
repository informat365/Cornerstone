<style scoped>

</style>
<i18n>
{
    "en": {
        "邀请成员": "Invite",
        "成员邮箱":"Email",
        "输入被邀请人的邮箱，如果有多个邮箱请用;隔开":"if there are more than one mailbox, please use; separate",
        "发送链接":"Send link",
        "邀请记录":"History",
        "被邀请人邮箱":"Email",
        "状态":"Status",
        "邀请时间":"Time",
        "已加入":"Joined",
        "等待加入":"Waiting",
        "删除":"Delete",
        "发送成功":"Success"
    },
    "zh_CN": {
        "邀请成员": "邀请成员",
        "成员邮箱":"成员邮箱",
        "输入被邀请人的邮箱，如果有多个邮箱请用;隔开":"输入被邀请人的邮箱，如果有多个邮箱请用;隔开",
        "发送链接":"发送链接",
        "邀请记录":"邀请记录",
        "被邀请人邮箱":"被邀请人邮箱",
        "状态":"状态",
        "邀请时间":"邀请时间",
        "已加入":"已加入",
        "等待加入":"等待加入",
        "删除":"删除",
        "发送成功":"发送成功"
    }
}
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('邀请成员')" width="700" :footer-hide="true">


     <Tabs value="email" :animated="false">
        <TabPane label="邮件邀请" name="email">
            <Form @submit.native.prevent   label-position="top" style="height:600px;padding:15px">
                <FormItem :label="$t('成员邮箱')">
                    <Row>
                        <Col span="20"><Input v-model.trim="email" @keyup.enter="sendInviteEmail" :placeholder="$t('输入被邀请人的邮箱，如果有多个邮箱请用;隔开')"></Input></Col>
                        <Col span="4" class="text-center"><Button :disabled="email==null||email==''" @click="sendInviteEmail()" type="default">{{$t('发送链接')}}</Button></Col>
                    </Row>
                </FormItem>
                <FormItem :label="$t('邀请记录')">
                <BizTable :fixed="true" :value="dataList">
                    <template slot="thead">
                            <tr>
                                <th >{{$t('被邀请人邮箱')}}</th>
                                <th style="width:100px">{{$t('状态')}}</th>
                                <th style="width:120px">{{$t('邀请时间')}}</th>
                                <th style="width:80px"></th>
                            </tr>
                    </template>
                    <template slot="tbody">
                        <tr v-for="item in dataList" :key="item.id" class="table-row">
                            <td>{{item.invitedEmail}}</td>
                            <td>{{item.isAgree?$t('已加入'):$t('等待加入')}}</td>
                            <td>{{item.createTime|fmtDeltaTime}}</td>
                            <td><Button @click="deleteInviteItem(item)" type="default">{{$t('删除')}}</Button></td>
                        </tr>

                    </template>
                </BizTable>
                </FormItem>
            </Form>
        </TabPane>
        <TabPane label="短信邀请" name="mobile">

            <Form @submit.native.prevent   label-position="top" style="height:600px;padding:15px">
                <div style="padding:15px 0px;color:#999;font-size:13px">如果被邀请人已经注册了CORNERSTONE，可以通过给被邀请人发送手机验证码的方式邀请加入企业</div>
                <FormItem label="手机号码">
                    <Input style="width:200px;margin-right:5px" v-model.trim="mobile" :placeholder="'输入被邀请人的手机号码'"></Input>
                    <Button :disabled="mobile==null||mobile.length!=11" @click="sendInviteSms()" type="default">发送验证码</Button>
                </FormItem>
                <FormItem label="验证码">
                     <Input style="width:200px" v-model.trim="verifyCode" :placeholder="'输入4位验证码'"></Input>
                </FormItem>
                <FormItem >
                     <Button :disabled="verifyCode==null||verifyCode.length!=4" @click="verifyInviteCode()" type="default">确定</Button>
                </FormItem>
            </Form>

        </TabPane>


         <TabPane label="企业邀请码" name="code">
            <div style="padding:15px;color:#999;font-size:13px">用户可通过在注册时填写邀请码直接加入企业
                <Button style="margin-left:7px" @click="resetCode()" size="small" type="default">重置邀请码</Button>
            </div>
            <BizTable :fixed="true" :value="codeList">
                    <template slot="thead">
                            <tr>
                                <th style="width:120px">邀请码</th>
                                <th style="width:100px">状态</th>
                                <th style="width:150px">使用人</th>
                                <th >加入时间</th>
                            </tr>
                    </template>
                    <template slot="tbody">
                        <tr v-for="item in codeList" :key="item.id" class="table-row">
                            <td>{{item.code}}</td>
                            <td><DataDictLabel type="CompanyMemberInviteCode.status" :value="item.status"></DataDictLabel></td>
                            <td>{{item.accountName}}</td>
                            <td>{{item.useTime|fmtDateTime}}</td>
                        </tr>
                    </template>
                </BizTable>

        </TabPane>


    </Tabs>




    </Modal>
</template>


<script>
    export default {
        mixins: [componentMixin],
        data () {
            return {
                email:null,
                dataList:[],
                codeList:[],
                verifyCode:null,
                mobile:null
            }
        },
        methods: {
            pageLoad(){
                this.loadInviteList();
                this.loadInviteCodeList();
            },
            sendInviteEmail(){
                app.invoke('BizAction.inviteProjectMember',[app.token,this.email,app.projectId],(list)=>{
                    app.toast(this.$t('发送成功'));
                    this.loadInviteList();
                })
            },
            loadInviteList(){
                app.invoke('BizAction.getCompanyMemberInviteInfoList',[app.token],(list)=>{
                    this.dataList=list;
                })
            },
            loadInviteCodeList(){
                app.invoke('BizAction.getCompanyMemberInviteCodeList',[app.token],(list)=>{
                    this.codeList=list;
                })
            },
            deleteInviteItem(item){
                app.invoke('BizAction.deleteCompanyMemberInvite',[app.token,item.id],()=>{
                   this.loadInviteList();
                })
            },
            verifyInviteCode(){
                app.invoke('BizAction.inviteMemberFromMobileNo',[app.token,this.mobile,this.verifyCode,app.projectId],()=>{
                   app.toast('邀请成功')
                   app.postMessage('member.edit')
                })
            },
            sendInviteSms(){
                app.invoke('BizAction.sendInviteMobileCode',[app.token,this.mobile],()=>{
                   app.toast('短信发送成功，请被邀请者查看短信')
                })
            },
            resetCode(){
                app.confirm('确定要重置所有的企业邀请码吗？重置后原有的邀请码将不能使用',()=>{
                    app.invoke('BizAction.resetCompanyMemberInviteCodeList',[app.token,this.mobile],()=>{
                        app.toast('重置成功');
                        this.loadInviteCodeList();
                    })
                })
            },
            confirm(){
                this.showDialog=false;
            }
        }
    }
</script>
