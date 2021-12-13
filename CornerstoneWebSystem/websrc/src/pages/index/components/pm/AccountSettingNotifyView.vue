<style scoped>
    .notify-item {
        font-size: 13px;
        color: #666;
    }

    .notify-row {
        margin-bottom: 20px;
    }

    .notify-item-row {
        color: #333 !important;
        font-size: 13px;
    }

    .unbind {
        font-size: 12px;
        color: #333;
        padding-top: 5px;
    }
</style>
<i18n>
    {
    "en": {
    "变更项": "Item",
    "邮件": "Email",
    "微信": "WeChat",
    "钉钉": "Dingtalk",
    "飞书": "Lark",
    "保存": "Save",
    "保存成功":"Data saved"
    },
    "zh_CN": {
    "变更项": "变更项",
    "邮件": "邮件",
    "微信": "微信",
    "钉钉": "钉钉",
    "飞书": "飞书",
    "保存": "保存",
    "保存成功":"保存成功"
    }
    }
</i18n>
<template>
    <div style="padding:30px">
        <table v-if="systemConfig" class="table-content table-grid table-content-small"
               style="border-top:1px solid #eee">
            <thead>
            <tr>
                <th style="width:180px">{{$t('变更项')}}</th>
                <th style="width:100px">
                    {{$t('邮件')}}
                    <div class="unbind" v-if="account.email==null">未绑定</div>
                    <i-Switch @on-change="bindAllChange('email')" v-if="account.email!=null" size="small"
                              v-model="bindall.email"></i-Switch>
                </th>
                <th style="width:100px" v-if="systemConfig.isWxAppIdSet">
                    {{$t('微信')}}
                    <div class="unbind" v-if="account.wxOpenId==null">未绑定</div>
                    <i-Switch @on-change="bindAllChange('wechat')" v-if="account.wxOpenId!=null" size="small"
                              v-model="bindall.wechat"></i-Switch>
                </th>
                <th style="width:100px" v-if="systemConfig.isLarkAppIdSet">
                    {{$t('飞书')}}
                    <div class="unbind" v-if="account.larkOpenId==null">未绑定</div>
                    <i-Switch @on-change="bindAllChange('lark')" v-if="account.larkOpenId!=null" size="small"
                              v-model="bindall.lark"></i-Switch>
                </th>
                <th style="width:100px" v-if="systemConfig.isDingtalkAppIdSet">
                    {{$t('钉钉')}}
                    <div class="unbind" v-if="account.dingtalkUserId==null">未绑定</div>
                    <i-Switch @on-change="bindAllChange('dingtalk')" v-if="account.dingtalkUserId!=null" size="small"
                              v-model="bindall.dingtalk"></i-Switch>
                </th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="item in notifyList" :key="item.id">
                <td>{{item.type|dataDict('AccountNotificationSetting.type')}}</td>
                <td>
                    <i-Switch size="small" :disabled="account.email==null" v-model="item.isEmailEnable"
                              @on-change="checkAll"></i-Switch>
                </td>
                <td v-if="systemConfig.isWxAppIdSet">
                    <i-Switch size="small" :disabled="account.wxOpenId==null" v-model="item.isWeixinEnable"
                              @on-change="checkAll"></i-Switch>
                </td>
                <td v-if="systemConfig.isLarkAppIdSet">
                    <i-Switch size="small" :disabled="account.larkOpenId==null" v-model="item.isLarkEnable"
                              @on-change="checkAll"></i-Switch>
                </td>
                <td v-if="systemConfig.isDingtalkAppIdSet">
                    <i-Switch size="small" :disabled="account.dingtalkUserId==null" v-model="item.isDingtalkEnable"
                              @on-change="checkAll"></i-Switch>
                </td>
            </tr>
            </tbody>
        </table>

        <div style="margin-top:10px;">
            <Button @click="saveConfig" type="default">{{$t('保存')}}</Button>
        </div>
    </div>
</template>

<script>
    export default {
        mixins: [componentMixin],
        data() {
            return {
                notifyList: [],
                account: {},
                systemConfig: null,
                bindall: {
                    email: false,
                    wechat: false,
                    lark: false,
                    dingtalk:false
                }
            }
        },
        mounted() {
            this.loadData();
        },
        methods: {
            checkAll() {
                this.bindall.email = !(this.notifyList.filter(item => !item.isEmailEnable).length > 0);
                this.bindall.wechat = !(this.notifyList.filter(item => !item.isWeixinEnable).length > 0);
                this.bindall.lark = !(this.notifyList.filter(item => !item.isLarkEnable).length > 0);
                this.bindall.dingtalk = !(this.notifyList.filter(item => !item.isDingtalkEnable).length > 0);
            },
            loadData() {
                app.invoke("BizAction.getAccountNotificationSettingList", [app.token], list => {
                    this.notifyList = list;
                    this.checkAll();
                });
                app.invoke("BizAction.getAccountInfo", [app.token], info => {
                    this.account = info;
                });
                app.invoke("BizAction.getSystemConfig", [], info => {
                    this.systemConfig = info;
                });
            },
            bindAllChange(which) {
                if (which == 'wechat') {
                    this.notifyList.forEach(item => {
                        item.isWeixinEnable = this.bindall.wechat
                    })
                }
                if (which == 'lark') {
                    this.notifyList.forEach(item => {
                        item.isLarkEnable = this.bindall.lark
                    })
                }
                if (which == 'email') {
                    this.notifyList.forEach(item => {
                        item.isEmailEnable = this.bindall.email
                    })
                }
                if (which == 'dingtalk') {
                    this.notifyList.forEach(item => {
                        item.isDingtalkEnable = this.bindall.dingtalk
                    })
                }
            },
            saveConfig() {
                app.invoke("BizAction.saveAccountNotificationSetting", [app.token, this.notifyList], list => {
                    app.toast(this.$t('保存成功'));
                });
            }
        }
    }
</script>
