<style scoped>
    .section-desc {
        color: #999;
        font-size: 12px;
    }

    .forminput {
        /*width: 200px;*/
        padding: 5px 10px;
    }

    .error-message {
        font-size: 13px;
        color: red;
        display: inline-block;
        width: 100%;
    }

    .btn-block {
        margin-left: 10px;
        margin-bottom: 8px;
    }
</style>
<i18n>
    {
    "en": {
    "手机": "Mobile",
    "飞书": "Feishu",
    "更换": "Change",
    "确认": "Confirm",
    "邮箱": "Email",
    "未绑定": "none",
    "绑定": "Bind",
    "解除绑定":"Unbind",
    "绑定邮箱后":"Use this mailbox to receive system notifications after binding a mailbox",
    "微信":"Wechat",
    "企业微信":"Work Wechat",
    "AD域账号":"Active Directory Account",
    "AD域密码":"Active Directory Password",
    "绑定AD域账号后":"After binding Active Directory Account,you can use the account to login system",
    "绑定微信后":"After binding WeChat, you can use WeChat login system to receive system notification",
    "绑定钉钉后":"After binging Dingtalk,you can use dingtalk login system without password and receive system notification    ",
    "绑定企业微信后":"After binding the Work WeChat, you can open the system without login in the Work WeChat",
    "登录凭证":"Credentials",
    "刷新":"Refresh",
    "保存成功":"data saved",
    "修改密码":"Change password",
    "登录凭证已复制到剪切板":"credentials have been copied to the clipboard",
    "操作成功":"Operation completed",
    "确认要解除绑定微信吗":"Are you sure you want to unbind WeChat? After unbinding, you will not be able to receive WeChat push    and use WeChat login function",
    "确认要解除绑定企业微信吗":"Are you sure you want to unbind Work WeChat? After unbinding, Work WeChat will can't open the system    without login",
    "确认要解除绑定邮箱吗":"Are you sure you want to unbind your mailbox? Unable to receive email notifications after unbinding",
    "确认要刷新登录凭证吗":"Are you sure you want to refresh your credentials?"
    },
    "zh_CN": {
    "手机": "手机",
    "飞书": "飞书",
    "更换": "更换",
    "确认": "确认",
    "邮箱": "邮箱",
    "未绑定": "未绑定",
    "绑定": "绑定",
    "解除绑定":"解除绑定",
    "绑定邮箱后":"绑定邮箱后可使用此邮箱接收系统通知",
    "微信":"微信",
    "企业微信":"企业微信",
    "AD域账号":"AD域账号",
    "AD域密码":"AD域密码",
    "绑定微信后":"绑定微信后可使用微信登录系统，接收系统通知",
    "绑定AD域账号后":"绑定后可使用AD域账号登录系统",
    "绑定钉钉后":"绑定钉钉后可使用钉钉登录系统，接收系统通知",
    "绑定企业微信后":"绑定企业微信后，可以在企业微信内可免登陆打开系统",
    "登录凭证":"登录凭证",
    "刷新":"刷新",
    "保存成功":"保存成功",
    "修改密码":"修改密码",
    "登录凭证已复制到剪切板":"登录凭证已复制到剪切板",
    "操作成功":"操作成功",
    "确认要解除绑定微信吗":"确认要解除绑定微信吗？解除绑定后将不能收到微信推送和使用微信登录功能",
    "确认要解除绑定企业微信吗":"确认要解除绑定微信吗？解除绑定后将不能使用企业微信免登陆打开系统的功能",
    "确认要解除绑定邮箱吗":"确认要解除绑定邮箱吗？解除绑定后将不能接收邮件通知",
    "确认要刷新登录凭证吗":"确认要刷新登录凭证吗？"
    }
    }
</i18n>
<template>
    <div style="padding:30px">
        <Form v-if="account.id>0" label-position="top">
            <FormItem :label="$t('手机')" v-if="!systemConfig.isPrivateDeploy||account.mobileNo">
                <span>{{account.mobileNo}}</span>
                <Button @click="changeMobileNo()" v-if="!systemConfig.isPrivateDeploy" style="margin-left:10px" size="small" type="default">{{$t('更换')}}
                </Button>
            </FormItem>

            <FormItem :label="$t('邮箱')">
                <div><span>{{account.email}}</span>
                    <span v-if="account.email==null">{{$t('未绑定')}}</span>
                    <Button v-if="account.email==null" @click="bindEmail()" size="small" style="margin-left:10px"
                            type="default">{{$t('绑定')}}
                    </Button>
                    <Button v-if="account.email!=null" @click="unbindEmail()" size="small" style="margin-left:10px"
                            type="error">{{$t('解除绑定')}}
                    </Button>
                </div>
                <div class="section-desc">
                    {{$t('绑定邮箱后')}}
                </div>
            </FormItem>

            <FormItem v-if="systemConfig.isWxAppIdSet" :label="$t('微信')">
                <div>
                    <span>{{account.wxOpenId}}</span>
                    <span v-if="account.wxOpenId==null">{{$t('未绑定')}}</span>
                    <Button v-if="account.wxOpenId==null" @click="bindWechat()" size="small" style="margin-left:10px"
                            type="default">{{$t('绑定')}}
                    </Button>
                    <Button v-if="account.wxOpenId!=null" @click="unbindWechat()" size="small" style="margin-left:10px"
                            type="error">{{$t('解除绑定')}}
                    </Button>
                </div>
                <div class="section-desc">
                    {{$t('绑定微信后')}}
                </div>
            </FormItem>

            <FormItem v-if="systemConfig.isAdSet" :label="$t('AD域账号')">
                <div>
                    <span>{{account.adName}}</span>
                    <span v-if="!account.adName">{{$t('未绑定')}}</span>
                    <Poptip :value="visible" placement="right" width="300"
                            trigger="click">
                        <Button @click="clickBindBtn" v-if="!account.adName" size="small" style="margin-left:10px"
                                type="default">{{$t('绑定')}}
                        </Button>
                        <div class="api" slot="content">
                            <div>
                                <Input
                                    v-focus="true" class="forminput" v-model="adAccount" :placeholder="$t('AD域账号')"/>
                            </div>
                            <div>
                                <Input
                                    class="forminput"
                                    type="password"
                                    v-model="adPassword"
                                    :placeholder="$t('AD域密码')"/>
                            </div>
                            <div
                                class="error-message" v-if="errorMessage">{{errorMessage}}
                            </div>
                            <div>
                                <Button
                                    type="success" size="small"
                                    :disabled="!adAccount||!adPassword"
                                    @click="bindAdAccount"
                                    class="btn-block">{{ $t('确认') }}
                                </Button>
                            </div>
                        </div>
                    </Poptip>

                    <Button v-if="!!account.adName" @click="unbindAdAccount()" size="small" style="margin-left:10px"
                            type="error">{{$t('解除绑定')}}
                    </Button>
                </div>
                <div class="section-desc">
                    {{$t('绑定AD域账号后')}}
                </div>
            </FormItem>

            <FormItem v-if="systemConfig.isQywxAppIdSet&&systemConfig.isPrivateDeploy" :label="$t('企业微信')">
                <div>
                    <span>{{account.qywxUserId}}</span>
                    <span v-if="!account.qywxUserId">{{$t('未绑定')}}</span>
                    <Button v-if="!account.qywxUserId" @click="bindWorkWechat()" size="small" style="margin-left:10px"
                            type="default">{{$t('绑定')}}
                    </Button>
                    <Button v-if="!!account.qywxUserId" @click="unbindWorkWechat()" size="small"
                            style="margin-left:10px" type="error">{{$t('解除绑定')}}
                    </Button>
                </div>
                <div class="section-desc">
                    {{$t('绑定企业微信后')}}
                </div>
            </FormItem>

            <FormItem v-if="systemConfig.isDingtalkAppIdSet&&systemConfig.isPrivateDeploy" :label="$t('钉钉')">
                <div>
                    <span>{{account.dingtalkUserId}}</span>
                    <span v-if="!account.dingtalkUserId">{{$t('未绑定')}}</span>
                    <Button v-if="!account.dingtalkUserId" @click="bindDingtalk()" size="small" style="margin-left:10px"
                            type="default">{{$t('绑定')}}
                    </Button>
                    <Button v-if="!!account.dingtalkUserId" @click="unbindDingtalk()" size="small"
                            style="margin-left:10px" type="error">{{$t('解除绑定')}}
                    </Button>
                </div>
                <div class="section-desc">
                    {{$t('绑定钉钉后')}}
                </div>
            </FormItem>


            <FormItem v-if="systemConfig.isLarkAppIdSet" :label="$t('飞书')">
                <div>
                    <span>{{account.larkOpenId}}</span>
                    <span v-if="account.larkOpenId==null">{{$t('未绑定')}}</span>
                    <Button v-if="account.larkOpenId==null" @click="bindLark()" size="small" style="margin-left:10px"
                            type="default">{{$t('绑定')}}
                    </Button>
                    <Button v-if="account.larkOpenId!=null" @click="unbindLark()" size="small" style="margin-left:10px"
                            type="error">{{$t('解除绑定')}}
                    </Button>
                </div>
            </FormItem>


            <FormItem :label="$t('登录凭证')">
                <span style="cursor:pointer" v-clipboard:copy="account.accessToken" v-clipboard:success="copySuccess">{{account.accessToken}}</span>
                <Button @click="refreshAccessToken()" size="small" style="margin-left:10px" type="default">
                    {{$t('刷新')}}
                </Button>
            </FormItem>

            <FormItem label="登录密码">
                <Button size="small" @click="changePassword" type="default">{{$t('修改密码')}}</Button>
            </FormItem>

        </Form>

    </div>
</template>

<script>
    export default {
        mixins: [componentMixin],
        data() {
            return {
                account: {},
                systemConfig: {},
                workWechatBindWin: null,
                adAccount: null,
                adPassword: null,
                errorMessage: null,
                visible: false
            }
        },
        beforeDestroy() {
            clearInterval(this.intervalId);
            if (this.workWechatBindWin && !this.workWechatBindWin.closed) {
                this.workWechatBindWin.close();
            }
            this.workWechatBindWin = null;
        },
        mounted() {
            this.loadData();
        },
        methods: {
            startCheckWorkWeChatBind() {
                clearInterval(this.intervalId);
                if (!this.workWechatBindWin) {
                    return;
                }
                this.intervalId = setInterval(() => {
                    if (this.workWechatBindWin.closed) {
                        this.loadData();
                        clearInterval(this.intervalId);
                        this.workWechatBindWin = null;
                    }
                }, 1000);
            },
            startCheckDingtalkBind() {
                clearInterval(this.dingtalkintervalId);
                if (!this.workWechatBindWin) {
                    return;
                }
                this.dingtalkintervalId = setInterval(() => {
                    if (this.workWechatBindWin.closed) {
                        this.loadData();
                        clearInterval(this.dingtalkintervalId);
                        this.workWechatBindWin = null;
                    }
                }, 1000);
            },
            loadData() {
                app.invoke("BizAction.getAccountInfo", [app.token], info => {
                    this.account = info;
                });
                //
                app.invoke("BizAction.getSystemConfig", [], info => {
                    this.systemConfig = info;
                });
            },
            pageMessage(type) {
                if (type == 'account.edit') {
                    this.loadData();
                }
            },
            copySuccess() {
                app.toast(this.$t('登录凭证已复制到剪切板'))
            },
            saveAccount() {
                app.invoke("BizAction.updateAccountInfo", [app.token, this.account], info => {
                    app.toast(this.$t('保存成功'));
                    app.postMessage('account.edit');
                });
            },
            unbindWechat() {
                app.confirm(this.$t('确认要解除绑定微信吗'), () => {
                    app.invoke("BizAction.unbindWeixin", [app.token, this.account], info => {
                        app.toast(this.$t('操作成功'));
                        this.loadData();
                        app.postMessage('account.edit');
                    });
                })
            },
            bindEmail() {
                app.showDialog(AccountBindEmailDialog)
            },
            unbindEmail() {
                app.confirm(this.$t('确认要解除绑定邮箱吗'), () => {
                    app.invoke("BizAction.unbindEmail", [app.token, this.account], info => {
                        app.toast(this.$t('操作成功'));
                        this.loadData();
                        app.postMessage('account.edit');
                    });
                })
            },
            refreshAccessToken() {
                app.confirm(this.$t('确认要刷新登录凭证吗'), () => {
                    app.invoke("BizAction.refreshAccessToken", [app.token, this.account], info => {
                        app.toast(this.$t('操作成功'));
                        this.loadData();
                    });
                })
            },
            bindWechat() {
                app.showDialog(AccountBindWechatDialog)
            },
            bindLark() {
                app.showDialog(AccountBindLarkDialog)
            },
            unbindLark() {
                app.confirm('确认要解除绑定飞书吗', () => {
                    app.invoke("LarkAction.unbindLark", [app.token, this.account], info => {
                        app.toast(this.$t('操作成功'));
                        this.loadData();
                        app.postMessage('account.edit');
                    });
                });
            },
            popupCenter({url, title, w, h, type}) {
                const dualScreenLeft = window.screenLeft !== undefined ? window.screenLeft : window.screenX;
                const dualScreenTop = window.screenTop !== undefined ? window.screenTop : window.screenY;

                const width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
                const height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;

                const systemZoom = width / window.screen.availWidth;
                const left = (width - w) / 2 / systemZoom + dualScreenLeft;
                const top = (height - h) / 2 / systemZoom + dualScreenTop;
                const newWindow = window.open(url, title,
                    `toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no
                            ,width=${w / systemZoom},height=${h / systemZoom},top=${top},left=${left}`,
                );
                if (window.focus) {
                    newWindow.focus();
                }
                if (this.workWechatBindWin && this.workWechatBindWin.closed) {
                    this.workWechatBindWin.close();
                }
                this.workWechatBindWin = newWindow;
                if (type === 'wechat') {
                    this.startCheckWorkWeChatBind();
                }
                if (type === 'dingtalk') {
                    this.startCheckDingtalkBind();
                }
            },
            clickBindBtn() {
                if (!this.visible) {
                    this.visible = true;
                }
            },
            bindAdAccount() {
                app.showLoading("绑定中。。。");
                app.invoke("BizAction.bindAdAccount", [app.token, this.adAccount, this.adPassword], info => {
                    app.hideLoading();
                    this.visible = false;
                    app.toast(this.$t('操作成功'));
                    this.loadData();
                    app.postMessage('account.edit');
                }, err => {
                    console.error(err)
                    app.hideLoading();
                });
            },
            unbindAdAccount() {
                app.confirm('确认要解除绑定AD域账号吗', () => {
                    app.invoke("BizAction.unbindAdAccount", [app.token, this.account], info => {
                        app.toast(this.$t('操作成功'));
                        this.loadData();
                        app.postMessage('account.edit');
                    });
                });
            },
            bindWorkWechat() {
                this.qrcodeURL = `${app.serverAddr}/p/qywx/get_bind_url?token=${app.token}`;
                this.popupCenter({
                    url: this.qrcodeURL,
                    title: '绑定企业微信',
                    w: 500,
                    h: 600,
                    type: 'wechat'
                });
            },
            unbindWorkWechat() {
                app.confirm('确认要解除绑定企业微信吗', () => {
                    app.invoke("QywxAction.unbind", [app.token, this.account], info => {
                        app.toast(this.$t('操作成功'));
                        this.loadData();
                        app.postMessage('account.edit');
                    });
                });
            },
            bindDingtalk() {
                this.qrcodeURL = `${app.serverAddr}/p/dingtalk/get_bind_url?token=${app.token}`;
                this.popupCenter({
                    url: this.qrcodeURL,
                    title: '绑定钉钉',
                    w: 500,
                    h: 600,
                    type: 'dingtalk'
                });
            },
            unbindDingtalk() {
                app.confirm('确认要解除绑定钉钉吗', () => {
                    app.invoke("DingtalkAction.unbind", [app.token, this.account], info => {
                        app.toast(this.$t('操作成功'));
                        this.loadData();
                        app.postMessage('account.edit');
                    });
                });
            },
            changeMobileNo() {
                app.showDialog(AccountChangeMobileDialog)
            },
            changePassword() {
                app.showDialog(AccountChangePwdDialog)
            }
        }
    }
</script>
