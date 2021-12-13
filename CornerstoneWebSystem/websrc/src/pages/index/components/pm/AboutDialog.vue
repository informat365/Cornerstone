<style scoped>
    .about-box {
        height: 200px;
        text-align: center;
        padding: 60px 20px;
    }

    .subtitle {
        font-size: 18px;
        color: #333;
        font-weight: bold;
        text-align: left;
    }

    .desc {
        text-align: left;
        font-size: 13px;
        margin-top: 20px;
        color: #666;
    }

    .copyright {
        margin-top: 40px;
        font-size: 12px;
        color: #999;
    }

    .about-wrap {
        display: flex;
        align-items: center;
    }

    .logo-box {
        width: 180px;
        text-align: center;
    }

    .about-box {
        flex: 1;
    }
</style>
<i18n>
    {
    "en": {
    "关于": "About",
    "一站式项目管理系统":"Project management and work platform",
    "访问CORNERSTONE官网":"Visit CORNERSTONE website",
    "了解更多关于CORNERSTONE的信息":"Learn more about CORNERSTONE.",
    "版权所有":"all rights reserved",
    "版权所有2":"CORNERSTONE™ is a registered trademark of Shenzhen Keystone Collaboration Technology Co., Ltd."
    },
    "zh_CN": {
    "关于": "关于",
    "一站式项目管理系统":"CORNERSTONE",
    "访问CORNERSTONE官网":"访问CORNERSTONE官网",
    "了解更多关于CORNERSTONE的信息":"了解更多关于CORNERSTONE的信息。",
    "提供技术支持":"提供技术支持",
    "版权所有":"版权所有",
    "版权所有2":"CORNERSTONE™为深圳市基石协作科技有限公司注册的商标。"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog"
        v-model="showDialog"
        :closable="true"
        :mask-closable="false"
        :footer-hide="true"
        :loading="false"
        :title="$t('关于')"
        width="600">
        <div class="about-wrap">
            <div class="logo-box">
                <img class="head-logo" src="/image/logo.png" />
            </div>
            <div class="about-box">
                <div class="subtitle">{{$t('一站式项目管理系统')}}</div>
                <div class="desc">
                    <div>Bundle  : {{releaseVersion}}</div>
                    <div>Version : v{{appVersion}}</div>
                </div>
                <div class="desc">
                    {{$t('访问CORNERSTONE官网')}} <a target="_blank" href="https://www.cornerstone365.cn">www.cornerstone365.cn</a>
                    {{$t('了解更多关于CORNERSTONE的信息')}}
                </div>
                <div class="desc">2018-{{currentYear}} CORNERSTONE·ITIT ©️ {{$t('版权所有')}}</div>
                <div class="desc">{{$t('版权所有2')}}</div>
            </div>
        </div>

    </Modal>
</template>


<script>
    export default {
        mixins: [componentMixin],
        data() {
            return {
                appVersion: '',
                releaseVersion: '',
                currentYear: '',
            };
        },
        methods: {
            pageLoad() {
                this.currentYear = new Date().getFullYear();
                // this.appVersion = app.version;
                this.releaseVersion = process.env.VUE_APP_RELEASE_VERSION;
                this.loadVersion();
                this.loadBuildVersion();
            },
            loadBuildVersion() {
                ajaxInvoke( '/p/main/', 'buildVersion', [], (t) => {
                        console.log(t)
                        if (t) {
                            this.releaseVersion = t.biz + "." + process.env.VUE_APP_RELEASE_VERSION;
                        } else {
                            this.releaseVersion = process.env.VUE_APP_RELEASE_VERSION;
                        }
                    },
                    (code, msg) => {
                        console.error(code,msg)
                    },
                );
            },
            loadVersion() {
                app.invoke('BizAction.getSystemConfig', [app.token], result => {
                    this.appVersion = result.appVersion;
                });
            },
        },
    };
</script>
