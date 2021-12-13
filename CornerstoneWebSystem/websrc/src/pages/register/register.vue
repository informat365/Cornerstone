<style>
.app {
    text-align: center;
}

.main-box {
    display: inline-block;
    text-align: left;
    width: 340px;
    margin-top: 150px;
}

.active-box {
    display: inline-block;
    text-align: left;
    width: 340px;
    margin-top: 150px;
}

.input-tips {
    font-size: 12px;
    padding-top: 3px;
    color: #666;
}

.tips-error {
    color: #ff7f50;
}

.verify-code-box {
    position: relative;
    overflow: hidden;
}

.verify-code-box .forminput {
    padding-right: 125px;
}

.verify-code-box .verify-code-send {
    position: absolute;
    top: 21px;
    right: 1px;
    height: 48px;
    width: 120px;
}

.verify-code-box .verify-code-send .verify-code-send-btn {
    margin-top: 0 !important;
    height: 48px;
    width: 120px;
    border: none;
}

.code-input {
    display: inline-block;
    width: 200px;
    height: 60px;
    text-align: center;
    font-weight: bold;
    font-size: 25px;
    letter-spacing: 10px;
}

.link {
    color: #999;
}

.logo-image {
    width: 110px;
}

.info-title {
    font-size: 20px;
    font-weight: 500;
    margin-top: 10px;
    margin-bottom: 10px;
    color: #999;
}

.error-message {
    margin-top: 10px;
    color: #ff7f50;
}
</style>
<template>
    <div>
        <div class="app">
            <div class="main-box">
                <div>
                    <img src="/image/logo.png" class="logo-image">
                </div>
                <div class="info-title">开启高效协作之旅</div>
                <div>
                    <input
                        class="forminput"
                        name="userName"
                        v-validate="'required|alpha_num|min:4|max:20'"
                        v-model="formItem.userName"
                        :placeholder="$t('用户名')" />
                    <div class="input-tips tips-error">{{errors.first('userName')}}</div>
                </div>
                <div>
                    <input
                        class="forminput"
                        maxlength="30"
                        name="name"
                        v-validate="'required'"
                        v-model="formItem.name"
                        :placeholder="$t('姓名')" />
                    <div class="input-tips tips-error">{{errors.first('name')}}</div>
                </div>
                <div>
                    <input
                        class="forminput"
                        name="mobileNo"
                        v-validate="'required|digits:11'"
                        v-model="formItem.mobileNo"
                        :placeholder="$t('手机号码')" />
                    <div class="input-tips tips-error">{{errors.first('mobileNo')}}</div>
                </div>
                <div class="verify-code-box" v-if="showKaptcha">
                    <input
                        class="forminput"
                        name="code"
                        v-validate="'required'"
                        v-model="kaptchaCode"
                        :placeholder="$t('验证码')" />
                    <div class="verify-code-send" @click="refreshKaptchaCode()">
                        <img :src="'/p/main/kaptcha?sign=' + kaptchaSign"/>
                    </div>
                    <div class="input-tips tips-error">{{errors.first('code')}}</div>
                </div>
                <div v-else>
                    <div class="verify-code-box">
                        <input
                            class="forminput"
                            name="code"
                            v-validate="'required'"
                            v-model="formItem.code"
                            :placeholder="$t('短信验证码')" />
                        <div class="verify-code-send">
                            <button
                                class="verify-code-send-btn btn-primary"
                                :disabled="sendVerifyState.sending || sendVerifyState.looping"
                                @click="enableShowKaptcha()">
                                <template v-if="sendVerifyState.sending">
                                    {{ $t('发送中') }}
                                </template>
                                <template v-else-if="sendVerifyState.looping">
                                    {{ sendVerifyState.second }}{{ $t('秒') }}
                                </template>
                                <template v-else>
                                    {{ $t('发送验证码') }}
                                </template>
                            </button>
                        </div>
                        <div class="input-tips tips-error">{{errors.first('code')}}</div>
                    </div>
                    <div>
                        <input
                            ref="password"
                            class="forminput"
                            name="password"
                            v-validate="'required|alpha_num|min:8|max:16'"
                            type="password"
                            v-model="formItem.password"
                            :placeholder="$t('密码')" />
                        <div class="input-tips tips-error">{{errors.first('password')}}</div>
                    </div>
                    <div>
                        <input
                            class="forminput"
                            name="passwordRepeat"
                            v-validate="'required|confirmed:password'"
                            type="password"
                            v-model="formItem.passwordRepeat"
                            :placeholder="$t('确认密码')" />
                        <div class="input-tips tips-error">{{errors.first('passwordRepeat')}}</div>
                    </div>
                    <div>
                        <input
                            class="forminput"
                            name="inviteNumberCode"
                            v-model="formItem.inviteNumberCode"
                            :placeholder="$t('企业邀请码')" />
                        <div style="color:#999">通过邀请码可以直接加入已经存在的企业</div>
                        <div class="input-tips tips-error">{{errors.first('inviteNumberCode')}}</div>
                    </div>
                </div>



                <div class="error-message" v-if="errorMessage">{{errorMessage}}</div>
                <!--                showKaptcha-->
                <div>
                    <button
                        v-if="showKaptcha"
                        :disabled="!kaptchaCode || 4 !== kaptchaCode.length"
                        @click="sendSmsCode()" class="btn-block btn-primary">{{$t('发送验证码')}}
                    </button>
                    <button
                        v-else
                        :disabled="agreeElua==false"
                        @click="register" class="btn-block btn-primary">{{$t('注册')}}
                    </button>
                </div>
                <div style="color:#999;margin-top:10px">
                    <input v-model="agreeElua" class="eulacheck" type="checkbox"></input>
                    {{$t('我已阅读并同意')}}<a class="link" href="/eula.html" target="_blank">《{{$t('用户许可协议')}}》</a>与<a
                    class="link"
                    href="/privacyla.html"
                    target="_blank">《{{$t('隐私条款')}}》</a></div>
            </div>
        </div>
    </div>

</template>
<i18n>
{
    "en": {
        "手机号码":"Mobile",
        "用户名":"Username",
        "姓名":"Name",
        "确认密码":"Confirm password",
        "密码":"Password",
        "验证码":"Kaptcha",
        "短信验证码":"SMS Code",
        "发送验证码":"Send SMS Code",
        "发送中":"Sending",
        "秒":"Second",
        "注册":"Register",
        "用户许可协议":"EULA",
        "隐私条款":"Privacy policy",
        "请查看短信，输入4位验证码":"Please check the SMS and enter the 4-digit verification code.",
        "完成注册":"Finish",
        "我已阅读并同意":"I have read and agreed",
        "企业邀请码":"Invite Code"
    },
    "zh_CN": {
        "手机号码":"手机号码",
        "用户名":"用户名",
        "姓名":"姓名",
        "确认密码":"确认密码",
        "验证码":"验证码",
        "短信验证码":"短信验证码",
        "发送验证码":"发送验证码",
        "发送中":"发送中",
        "秒":"秒",
        "注册":"注册",
        "密码":"密码",
        "用户许可协议":"用户许可协议",
        "隐私条款":"隐私条款",
        "请查看短信，输入4位验证码":"请查看短信，输入4位验证码",
        "完成注册":"完成注册",
        "我已阅读并同意":"我已阅读并同意",
        "企业邀请码":"企业邀请码（选填）"
    }
}
</i18n>
<script>
function uuid(len, radix) {
    var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
    var uuid = [], i;
    radix = radix || chars.length;
    if (len) {
        // Compact form
        for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random() * radix];
    } else {
        // rfc4122, version 4 form
        var r;
        // rfc4122 requires these characters
        uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
        uuid[14] = '4';
        // Fill in random data. At i==19 set the high bits of clock sequence as
        // per rfc4122, sec. 4.1.5
        for (i = 0; i < 36; i++) {
            if (!uuid[i]) {
                r = 0 | Math.random() * 16;
                uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
            }
        }
    }
    return uuid.join('');
}

import VeeValidate from 'vee-validate';
//
import Vue from 'vue';

Vue.use(VeeValidate);
//
export default {
    data() {
        return {
            kaptchaSign: null,
            kaptchaCode: null,
            showKaptcha: false,
            serverAddr: serverAddr,
            ready: false,
            errorMessage: null,
            formItem: {
                mobileNo: null,
                userName: null,
                name: null,
                sign: null,
                password: null,
                passwordRepeat: null,
                code: null,
                inviteCode: null,
                inviteNumberCode: null,
                larkAuthorizeData: null,
            },
            codeForm: {
                code: null,
            },
            agreeElua: true,
            formItemError: {
                mobileNo: null,
                name: null,
                userName: null,
                password: null,
                passwordRepeat: null,
                code: null,
            },
            validateSuccess: false,
            sendVerifyState: {
                sending: false,
                looping: false,
                intervalId: 0,
                second: 60,
            },
        };
    },
    mounted() {
        const dict = {
            custom: {
                mobileNo: {
                    required: '请输入手机号码',
                    digits: '请输入正确的手机号码',
                },
                userName: {
                    required: '请输入用户名',
                    alpha_num: '用户名为4-20位数字和字母的组合',
                    min: '用户名为4-20位数字和字母的组合',
                    max: '用户名为4-20位数字和字母的组合',
                },
                name: {
                    required: '请输入姓名',
                },
                password: {
                    required: '请输入密码',
                    alpha_num: '密码为8-16位数字和字母的组合',
                    min: '密码为8-16位数字和字母的组合',
                    max: '密码为8-16位数字和字母的组合',
                },
                passwordRepeat: {
                    required: '请再次输入密码',
                    confirmed: '两次输入的密码不一致',
                },
                code: {
                    required: '请输入短信验证码',
                },
            },
        };
        this.$validator.localize('zh', dict);
    },
    beforeDestroy() {
        this.stopLoop();
    },
    created() {
        const url = new URL(window.location.href);
        this.serverAddr = [window.location.protocol, '//', window.location.host].join('');
        const pageArgs = {};
        url.searchParams.forEach((k, v) => {
            pageArgs[v] = k;
        });
        if (pageArgs.data) {
            this.formItem.larkAuthorizeData = pageArgs.data;
        }
    },
    methods: {
        // 刷新验证码
        refreshKaptchaCode() {
            this.kaptchaSign = getUuId();
        },
        register() {
            this.$validator.validate().then(result => {
                if (result) {
                    this.register2();
                }
            });
        },
        register0() {
            this.errorMessage = null;
            this.formItem.inviteCode = getCookie('inviteCode');
            ajaxInvoke(this.serverAddr + '/p/api/invoke/', 'BizAction.register', [this.formItem], (t) => {
                    this.uuid = t;
                    this.active();
                },
                (code, msg) => {
                    this.errorMessage = msg;
                },
            );
        },
        // 打开发送验证码
        enableShowKaptcha () {
            this.errorMessage = '';
            this.kaptchaCode = null;
            this.$validator.validate('mobileNo').then(result => {
                if (!result) {
                    return;
                }
                // 刷新验证码
                this.refreshKaptchaCode();
                this.showKaptcha = true;
            });
        },
        onClickSendVerify() {
            this.$validator.validate('mobileNo').then(result => {
                if (!result) {
                    return;
                }
                this.sendSmsCode();
            });
        },
        sendSmsCode() {
            // 验证码已填
            if (!this.kaptchaCode || 4 !== this.kaptchaCode.length) {
                return;
            }
            this.errorMessage = null;
            this.sending = true;
            this.sendVerifyState.sending = true;
            ajax().post(this.serverAddr + '/p/main/send_mobile_code', {
                mobileNo: this.formItem.mobileNo,
                sign: this.kaptchaSign,
                kaptchaCode: this.kaptchaCode
            }).then(res => {
                let data = {
                    code: -1,
                };
                try {
                    data = JSON.parse(res);
                } catch (e) {
                }
                if (data.code !== 0) {
                    this.errorMessage = data.msg || '发送验证码失败，请稍后重试';
                    this.refreshKaptchaCode();
                    return;
                }
                this.startLoop();
                this.showKaptcha = false;
            }).catch((response, xhr) => {
                if (xhr.status === 200) {
                    this.errorMessage = '网络连接失败，请稍后重试';
                }
            }).always(() => {
                this.sendVerifyState.sending = false;
            });
        },
        startLoop() {
            this.stopLoop();
            this.sendVerifyState.sending = false;
            this.sendVerifyState.looping = true;
            this.sendVerifyState.intervalId = setInterval(() => {
                if (this.sendVerifyState.second < 1) {
                    this.stopLoop();
                    return;
                }
                this.sendVerifyState.second -= 1;
            }, 1000);
        },
        stopLoop() {
            clearInterval(this.sendVerifyState.intervalId);
            this.sendVerifyState.looping = false;
            this.sendVerifyState.second = 60;
        },
        register2() {
            this.errorMessage = null;
            this.formItem.inviteCode = getCookie('inviteCode');
            ajaxInvoke(this.serverAddr + '/p/main/','registerNew', [this.formItem], (t) => {
                    setCookie('token', t);
                    deleteCookie('inviteCode');
                    this.errorMessage = '注册成功';
                    setTimeout(() => {
                        window.location.href = '/#/';
                    }, 1000);
                },
                (code, msg) => {
                    this.errorMessage = msg;
                },
            );
        },
        active() {
            this.errorMessage = null;
            ajaxInvoke(this.serverAddr + '/p/api/invoke/', 'BizAction.activateAccount', [1, this.uuid], () => {
                    this.viewType = 'active';
                },
                (code, msg) => {
                    this.errorMessage = msg;
                },
            );
        },
        confirmActive() {
            this.errorMessage = null;
            var code = this.codeForm.code;
            ajaxInvoke(this.serverAddr + '/p/api/invoke/', 'BizAction.confirmActivateAccount', [1, this.uuid, code], () => {
                    deleteCookie('inviteCode');
                    this.errorMessage = '注册成功';
                    setTimeout(() => {
                        debugger;
                        window.location.href = 'login.html';
                    }, 2000);
                },
                (code, msg) => {
                    this.errorMessage = msg;
                });
        },
    },
};
</script>
