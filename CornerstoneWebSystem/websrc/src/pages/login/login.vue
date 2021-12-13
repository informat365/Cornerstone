<style>

    html, body {
        padding: 0;
        margin: 0;
        height: 100%;
    }
    .system-logo-custom{
        width: 110px;
        vertical-align: middle;
    }

    .login {
        position: relative;
        height: 100%;
        width: 100%;
        overflow: auto;
        overflow-scrolling: touch;
        scroll-behavior: smooth;
    }

    .app {
        position: relative;
        width: 400px;
        padding: 30px;
        border-radius: 6px;
        background-color: #fff;
        margin: 150px auto 0 auto;
    }

    .main-box {
    }

    .logo-image {
        width: 110px;
    }

    .login-title {
        font-size: 20px;
        font-weight: 500;
        margin-top: 10px;
        margin-bottom: 10px;
        color: #999;
    }

    .register-bar {
        color: #999;
        padding-top: 10px;
    }

    .reg-link {
        color: #17a7ed;
        margin-left: 5px;
    }

    .wxqrcode-box {
        text-align: center;
        padding: 20px;
    }

    .wxqrcode-box img {
        display: inline-block;
    }

    .company-name {
        margin-top: 10px;
        color: #666;
    }

    .error-message {
        color: red;
        padding: 7px 0;
    }

    .send-code-box {
        position: relative;
    }

    .send-code-btn {
        position: absolute;
        right: 8px;
        top: 37px;
        cursor: pointer;
        color: #17a7ed;
    }

    .send-code-btn-disabled {
        cursor: not-allowed;
        color: #999;
    }
</style>
<i18n>
    {
    "en": {
    "登录":"Login",
    "用户名/手机号码":"Username/Mobile",
    "密码":"Password",
    "忘记密码？":"Forget password?",
    "使用微信登录":"Wechat login",
    "还没有新账号吗？":"Don't have account yet?",
    "注册新账号":"Register new account",
    "使用微信扫描上方二维码":"Scan QR code with wechat",
    "使用验证码登录":"Login via verify code",
    "验证码":"Verify code",
    "使用AD域账号登录":"Login by active directory account",
    "AD域账号":"Active directory account",
    "AD域密码":"Active directory password",
    "返回":"back",
    "你的手机号码":"Mobile"
    },
    "zh_CN": {
    "用户名": "用户名",
    "密码": "密码",
    "忘记密码？": "忘记密码？",
    "登录": "登录",
    "使用微信登录": "使用微信登录",
    "还没有新账号吗？": "还没有新账号吗？",
    "注册新账号": "注册新账号",
    "使用微信扫描上方二维码": "使用微信扫描上方二维码",
    "使用验证码登录":"使用验证码登录",
    "验证码":"验证码",
    "使用AD域账号登录":"使用AD域账号登录",
    "AD域账号":"AD域账号",
    "AD域密码":"AD域密码",
    "返回": "返回",
    "你的手机号码":"你的手机号码"
    }
    }
</i18n>
<template>
    <div class="login" :style="loginBackStyle">
        <div class="app">
            <div
                v-show="loginType===1" class="main-box">
                <div>
                    <img v-if="systemConfig.logoImageId" :src="imageURL()" class="system-logo-custom"/>
                    <img
                        v-else
                        src="/image/logo.png" class="logo-image">
                    <br>
                </div>
                <div class="login-title">通过用户名密码登录</div>
                <div>
                    <input
                        v-focus="true" class="forminput" v-model="username" :placeholder="$t('用户名')">
                </div>
                <div>
                    <input
                        @keyup.enter="login"
                        class="forminput"
                        type="password"
                        v-model="password"
                        :placeholder="$t('密码')">
                </div>

                <div
                    v-if="systemConfig.isPrivateDeploy==false"
                    @click="resetPassword"
                    style="cursor: pointer;text-align: right;color:#999;margin-top:10px">{{$t('忘记密码？')}}
                </div>

                <div
                    class="error-message" v-if="errorMessage">{{errorMessage}}
                </div>
                <div>
                    <button
                        :disabled="username==''||password==''" @click="login" class="btn-block btn-primary">{{ $t('登录') }}
                    </button>
                </div>
                <div style="border-top:1px solid #eee;margin-top:20px;">
                    <div v-if="systemConfig.isWxAppIdSet==true">
                        <button
                            class="btn-block btn-outline" @click="wxLogin">{{$t('使用微信登录')}}
                        </button>
                    </div>

                    <div v-if="systemConfig.isSmsSet==true">
                        <button
                            class="btn-block btn-outline" @click="codeLogin">{{$t('使用验证码登录')}}
                        </button>
                    </div>
                    <div v-if="systemConfig.isAdSet==true">
                        <button
                            class="btn-block btn-outline" @click="adLogin">{{$t('使用AD域账号登录')}}
                        </button>
                    </div>

                    <div
                        v-if="systemConfig.isPrivateDeploy==false" class="register-bar">
                        {{$t('还没有新账号吗？')}}
                        <a
                            class="reg-link" href="register.html">{{$t('注册新账号')}}</a>
                    </div>
                </div>
            </div>

            <div
                v-show="loginType===2" class="main-box">
                <div>
                    <img
                        src="/image/logo.png" class="logo-image">
                    <div class="login-title">通过微信扫码登录</div>
                </div>
                <div
                    class="wxqrcode-box" id="wxqrcode"></div>
                <div
                    class="register-bar" style="text-align:center">{{$t('使用微信扫描上方二维码')}}
                </div>
                <div>
                    <button
                        class="btn-block btn-outline" @click="cancelWeixinLogin">{{$t('返回')}}
                    </button>
                </div>
            </div>
            <div
                v-if="loginType===3" class="main-box">
                <div>
                    <img
                        src="/image/logo.png" class="logo-image">
                    <div class="login-title">通过短信验证码登录</div>
                </div>
                <div>
                    <input
                        v-focus="true" class="forminput" v-model="mobileNo" :placeholder="$t('你的手机号码')">
                </div>
                <div class="send-code-box" v-if="showKaptcha">
                    <input
                        maxlength="4" class="forminput" v-model="kaptchaCode" :placeholder="$t('验证码')">

                    <div
                        @click="refreshKaptchaCode()"
                        :class="{'send-code-btn-disabled':mobileNo.length!=11 || sendCoding}"
                        class="send-code-btn">
                        <img :src="'/p/main/kaptcha?sign=' + kaptchaSign"/>
                    </div>
                </div>
                <div class="send-code-box" v-else>
                    <input
                        maxlength="4" class="forminput" v-model="verifyCode" :placeholder="$t('验证码')">
                    <div
                        @click="enableShowKaptcha()"
                        :class="{'send-code-btn-disabled':mobileNo.length!=11 || sendCoding}"
                        class="send-code-btn">
                        <template v-if="sendCodeCountDown<=0">获取验证码</template>
                        <template v-if="sendCodeCountDown>0">已发送{{sendCodeCountDown}}</template>
                    </div>
                </div>
                <div
                    class="error-message" v-if="errorMessage">{{errorMessage}}
                </div>
                <div v-if="showKaptcha">
                    <button
                        :disabled="!kaptchaCode || kaptchaCode.length!=4"
                        @click="sendCode()"
                        class="btn-block btn-primary">{{ $t('发送验证码') }}
                    </button>
                </div>
                <div v-else>
                    <button
                        :disabled="mobileNo==''||verifyCode.length!=4"
                        @click="loginByCode"
                        class="btn-block btn-primary">{{ $t('登录') }}
                    </button>
                </div>

                <div>
                    <button
                        class="btn-block btn-outline" @click="loginType=1">{{$t('返回')}}
                    </button>
                </div>

            </div>
            <div
                v-if="loginType===4" class="main-box">
                <div>
                    <img
                        src="/image/logo.png" class="logo-image">
                    <div class="login-title">通过AD域账号登录</div>
                </div>
                <div>
                    <input
                        v-focus="true" class="forminput" v-model="adAccount" :placeholder="$t('AD域账号')">
                </div>
                <div>
                    <input
                        @keyup.enter="loginByAdAccount"
                        class="forminput"
                        type="password"
                        v-model="adPassword"
                        :placeholder="$t('AD域密码')">
                </div>
                <div
                    class="error-message" v-if="errorMessage">{{errorMessage}}
                </div>
                <div>
                    <button
                        :disabled="!adAccount||!adPassword"
                        @click="loginByAdAccount"
                        class="btn-block btn-primary">{{ $t('登录') }}
                    </button>
                </div>

                <div>
                    <button
                        class="btn-block btn-outline" @click="loginType=1">{{$t('通过用户名密码登录')}}
                    </button>
                </div>

            </div>

            <div
                class="company-name" v-if="systemConfig.isPrivateDeploy">
                {{systemConfig.companyName}}·私有部署版本
            </div>
        </div>
    </div>
</template>
<script>
    var checkLoginStatusInterval = null;
    var currentState = null;

    function checkLoginStatus() {
        if (checkLoginStatusInterval) {
            clearInterval(checkLoginStatusInterval);
        }
        checkLoginStatusInterval = setInterval(function () {
            ajaxInvoke(
                this.serverAddr + '/p/api/invoke/',
                'BizAction.getWxOauth',
                [currentState],
                function (obj) {
                    if (obj.token != null) {
                        setCookie('token', obj.token);
                        window.location.href = '/#/';
                        this.saveLoginType(2)
                    }
                },
            );
        }, 3000);
    }

    function stopCheckStatus() {
        clearInterval(checkLoginStatusInterval);
    }

    export default {
        directives: {
            focus: {
                inserted: function (el, { value }) {
                    if (value) {
                        el.focus();
                    }
                },
            },
        },
        data() {
            return {
                logoImageId: null,
                kaptchaSign: null,
                kaptchaCode: null,
                showKaptcha: false,
                loginType: 1,
                serverAddr: serverAddr,
                ready: false,
                username: '',
                password: '',
                errorMessage: null,
                systemConfig: {},
                mobileNo: '',
                verifyCode: '',
                sendCodeCountDown: 0,
                sendCoding: false,
                adAccount:null,
                adPassword:null,
                loginTypeKey:'login.success.key'
            };
        },
        computed: {
            loginBackStyle() {
                if (!this.systemConfig || !this.systemConfig.loginBackgroundUrl) {
                    return {};
                }
                return {
                    background: `url("${ this.systemConfig.loginBackgroundUrl }") top center no-repeat`,
                    backgroundSize: 'cover',
                };
            },
        },
        mounted() {
            // this.loadLoginType();
            this.getSystemConfig();
        },
        beforeDestroy() {
            if(this.countdownInterval){
                clearInterval(this.countdownInterval);
            }
            this.sendCodeCountDown = 0;
        },
        methods: {
            // 刷新验证码
            refreshKaptchaCode() {
                this.kaptchaSign = getUuId();
            },
            saveLoginType(type){
                window.localStorage[this.loginTypeKey] = type;
            },
            loadLoginType(){
                return  window.localStorage[this.loginTypeKey]||4;
            },
            login() {
                if (this.username == '' || this.password == '') {
                    return;
                }
                this.errorMessage = null;
                ajaxInvoke(
                    this.serverAddr + '/p/api/invoke/',
                    'BizAction.login',
                    [this.username, this.password],
                    result => {
                        if (result.errCode == 0) {
                            setCookie('token', result.token);
                            window.location.href = '/#/';
                            this.saveLoginType(1)
                        } else {
                            this.errorMessage = result.errMsg;
                        }
                    },
                    (code, msg) => {
                        this.errorMessage = msg;
                    },
                );
            },
            getSystemConfig() {
                ajaxInvoke(
                    this.serverAddr + '/p/api/invoke/', 'BizAction.getSystemConfig', [], obj => {
                        this.systemConfig = obj;
                        this.logoImageId = obj.logoImageId;
                        console.log('this.systemConfig----->', this.systemConfig);
                        if (obj.isAdSet) {
                            this.loginType=4;
                        }
                    },
                );
            },
            adLogin(){
                this.loginType = 4;
            },
            loginByAdAccount(){
                if (!this.adAccount||!this.adPassword) {
                    return;
                }
                this.errorMessage = null;
                ajaxInvoke(
                    this.serverAddr + '/p/api/invoke/',
                    'BizAction.loginbyAdAccount',
                    [this.adAccount, this.adPassword],
                    result => {
                        if (result.errCode == 0) {
                            setCookie('token', result.token);
                            window.location.href = '/#/';
                            this.saveLoginType(4)
                        } else {
                            this.errorMessage = result.errMsg;
                        }
                    },
                    (code, msg) => {
                        this.errorMessage = msg;
                    },
                );
            },
            loginByCode() {
                if (this.mobileNo == '' || this.verifyCode == '') {
                    return;
                }
                this.errorMessage = null;
                ajaxInvoke(
                    this.serverAddr + '/p/api/invoke/',
                    'BizAction.loginByMobileNo',
                    [this.mobileNo, this.verifyCode],
                    result => {
                        if (result.errCode == 0) {
                            setCookie('token', result.token);
                            window.location.href = '/#/';
                            this.saveLoginType(3)
                        } else {
                            this.errorMessage = result.errMsg;
                        }
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
                if (this.sendCoding || this.mobileNo.length !== 11 || this.sendCodeCountDown > 0) {
                    return;
                }
                // 刷新验证码
                this.refreshKaptchaCode();
                this.showKaptcha = true;
            },
            sendCode() {
                if (this.sendCoding || this.mobileNo.length !== 11 || this.sendCodeCountDown > 0) {
                    return;
                }
                // 验证码已填
                if (!this.kaptchaCode || 4 !== this.kaptchaCode.length) {
                    return;
                }

                this.sendCoding = true;
                this.errorMessage = '';
                ajaxInvoke(this.serverAddr + '/p/api/invoke/', 'BizAction.sendLoginMobileCode', [this.mobileNo, this.kaptchaSign, this.kaptchaCode], obj => {
                        this.sendCoding = false;
                        this.showKaptcha = false;
                        this.startCountDownTimer();

                    }, (code, msg) => {
                        this.errorMessage = msg;
                        this.sendCoding = false;
                        // 刷新验证码
                        this.refreshKaptchaCode();
                    },
                );
            },
            startCountDownTimer() {
                clearInterval(this.countdownInterval);
                this.sendCodeCountDown = 60;
                this.countdownInterval = setInterval(() => {
                    this.sendCodeCountDown--;
                    if (this.sendCodeCountDown <= 0) {
                        clearInterval(this.countdownInterval);
                    }
                }, 1000);
            },
            codeLogin() {
                this.loginType = 3;
            },
            wxLogin() {
                this.loginType = 2;
                ajaxInvoke(
                    this.serverAddr + '/p/api/invoke/', 'BizAction.createWxOauthState',
                    [],
                    obj => {
                        currentState = obj;
                        checkLoginStatus();
                        document.getElementById('wxqrcode').innerHTML = '';
                        var host =
                            window.location.protocol + '//' + window.location.host;
                        new QRCode(
                            document.getElementById('wxqrcode'),
                            host + '/p/wx/enter?state=' + obj,
                        );
                    },
                );
            },
            resetPassword() {
                window.location.href = '/resetpwd.html';
            },
            cancelWeixinLogin() {
                this.loginType = 1;
                stopCheckStatus();
            },
            imageURL: function () {
                return this.serverAddr + "/p/file/get_system_file?type=01";
            },
        },
    };
</script>
