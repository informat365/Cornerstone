<style scoped>
    .line {
        border-top: 1px solid #eee;
        margin-top: 15px;
        margin-bottom: 15px;
    }

    .rule-desc {
        margin-bottom: 20px;
        color: #999;
    }
</style>

<i18n>
    {
    "en": {
    "此通知已禁用，将不会发送新的通知":"This notification is disabled and no new notification will be sent",
    "系统通知":"System notification",
    "通知名称":"Name",
    "通知标题":"Title",
    "通知周期":"Period",
    "通知时间":"Reminder time",
    "请选择":"Choose",
    "通知日期":"Reminder Date",
    "日通知将会按照选择的时间发送通知":"Day notifications will be sent at the selected time.",
    "日通知将会每天按照选择的时间发送通知":"Daily notifications will be sent daily at the time selected.",
    "周通知将会按照选择发送通知":"Weekly notifications will be sent as selected.",
    "月通知将会按照选择发送通知":"Monthly notifications will be sent as selected.",
    "通知内容":"Notify content",
    "通知成员": "Notify users",
    "选择成员": "Choose",
    "部门": "Departments",
    "选择部门": "Choose department",
    "企业角色": "Company role",
    "选择角色": "Choose",
    "项目角色": "Project role",
    "编辑":"Edit",
    "删除":"Delete",
    "删除此通知":"Delete notification",
    "禁用此通知":"Disable notification",
    "启用此通知":"Enable notification",
    "继续创建下一个":"Continue to create the next ",
    "保存":"Save",
    "创建":"Create",
    "周一":"Monday",
    "周二":"Tuesday",
    "周三":"Wednesday",
    "周四":"Thursday",
    "周五":"Friday",
    "周六":"Saturday",
    "周日":"Sunday",
    "号":"Day {0}",
    "选择日期":"Choose date",
    "删除成功":"Delete success",
    "操作成功":"Success",
    "请选择通知用户":"Please choose notify user or role",
    "确认要删除吗？":"Are you sure you want to delete “{0}”?"
    },
    "zh_CN": {
    "此通知已禁用，将不会发送新的通知":"此通知已禁用，将不会发送新的通知",
    "系统通知":"系统通知",
    "通知标题":"通知标题",
    "通知名称":"通知名称",
    "通知周期":"通知周期",
    "通知时间":"通知时间",
    "请选择":"请选择",
    "通知日期":"通知日期",
    "日通知将会按照选择的时间发送通知":"日通知将会按照选择的时间发送通知",
    "日通知将会每天按照选择的时间发送通知":"日通知将会每天按照选择的时间发送通知",
    "周通知将会按照选择发送通知":"周通知将会在按照选择发送通知",
    "月通知将会按照选择发送通知":"月通知将会在按照选择发送通知",
    "接收通知的用户":"接收通知的用户",
    "通知内容":"通知内容",
    "通知成员": "通知成员",
    "选择成员": "选择成员",
    "部门": "部门",
    "选择部门": "选择部门",
    "企业角色": "企业角色",
    "选择角色": "选择角色",
    "项目角色": "项目角色",
    "编辑":"编辑",
    "删除":"删除",
    "删除此通知":"删除此通知",
    "禁用此通知":"禁用此通知",
    "启用此通知":"启用此通知",
    "继续创建下一个":"继续创建下一个",
    "保存":"保存",
    "创建":"创建",
    "周一":"周一",
    "周二":"周二",
    "周三":"周三",
    "周四":"周四",
    "周五":"周五",
    "周六":"周六",
    "周日":"周日",
    "号":"{0}号",
    "选择日期":"选择日期",
    "删除成功":"删除成功",
    "操作成功":"操作成功",
    "请选择通知用户":"请选择通知用户",
    "确认要删除吗？":"确认要删除 “{0}” 吗？"
    }
    }
</i18n>

<template>
    <Modal
        ref="dialog"
        v-model="showDialog"
        :closable="true"
        :mask-closable="false"
        :loading="false"
        :title="$t('系统通知')"
        width="700"
        @on-ok="confirm">

        <Form ref="form" :rules="formRule" :model="formItem" label-position="top" style="min-height:600px;padding:15px">
            <div v-if="loaded">
                <Alert v-if="formItem.status===2">
                    {{$t('此通知已禁用，将不会发送新的通知')}}
                </Alert>
                <FormItem :label="$t('通知名称')" prop="name">
                    <Input :placeholder="$t('通知名称')" v-model.trim="formItem.name"></Input>
                </FormItem>
                <Row>
                    <Col span="11">
                        <FormItem :label="$t('通知周期')" prop="period">
                            <DataDictRadio
                                v-model="formItem.period"
                                type="SystemNotification.period"
                                @changed="onPeriodChange"></DataDictRadio>
                        </FormItem>
                    </Col>
                    <Col span="11" offset="2">
                        <FormItem :label="$t('通知时间')" prop="notifyTime">
                            <TimePicker
                                format="HH:mm"
                                v-model="formItem.notifyTime"
                                :placeholder="$t('请选择')"
                                style="width: 112px"></TimePicker>
                        </FormItem>
                    </Col>
                </Row>
                <FormItem v-if="formItem.period===1" :label="$t('通知日期')">
                    <DatePicker
                        v-model="formItem.dailyTime"
                        :editable="false"
                        confirm
                        :options="periodDailyOption"
                        type="date"
                        :placeholder="$t('选择日期')"
                        @on-clear="onDatePeriodClear"
                        @on-ok="onDatePeriodConfirm" />
                </FormItem>
                <FormItem v-else-if="formItem.period===2" :label="$t('通知日期')">
                    <CheckboxGroup v-model="formItem.periodSetting">
                        <Checkbox v-for="item in weeklyPeriodSettingArray" :key="'p'+item.value" :label="item.value">
                            <span>{{item.name}}</span>
                        </Checkbox>
                    </CheckboxGroup>
                </FormItem>
                <FormItem v-else-if="formItem.period===3" :label="$t('通知日期')">
                    <CheckboxGroup v-model="formItem.periodSetting">
                        <Row :gutter="24">
                            <template v-for="item in monthlyPeriodSettingArray">
                                <Col span="3">
                                    <Checkbox :key="'p'+item.value" :label="item.value">
                                        <span>{{item.name}}</span>
                                    </Checkbox>
                                </Col>
                            </template>
                        </Row>
                    </CheckboxGroup>
                </FormItem>
                <div v-if="formItem.period===1" class="rule-desc">
                    <template v-if="formItem.dailyTime">
                        {{$t('日通知将会按照选择的时间发送通知')}}
                    </template>
                    <template v-else>
                        {{$t('日通知将会每天按照选择的时间发送通知')}}
                    </template>
                </div>
                <div v-if="formItem.period===2" class="rule-desc">{{$t('周通知将会在每周五的通知时间发送')}}</div>
                <div v-if="formItem.period===3" class="rule-desc">{{$t('月通知将会在每月最后一天的通知时间发送')}}</div>
                <FormItem :label="$t('通知标题')" prop="title">
                    <Input :placeholder="$t('通知标题')" v-model.trim="formItem.title"></Input>
                </FormItem>
                <FormItem :label="$t('通知内容')" prop="content">
                    <Input
                        v-model.trim="formItem.content"
                        type="textarea"
                        :autosize="{ minRows: 3, maxRows: 5 }"
                        style="resize: none"
                        :placeholder="$t('通知内容')"
                        :maxlength="300" />
                </FormItem>
                <div class="line"></div>
                <FormItem :label="$t('通知成员')">
                    <div class="form-value-tag-select">
                        <ColorTag
                            class="choose-tag"
                            v-for="item in formItem.accountList"
                            :key="'o'+item.id"
                            :closable="true"
                            @on-close="removeFromList(formItem.accountList,item)">{{item.name}}
                        </ColorTag>
                        <Button icon="ios-add" type="dashed" size="small" @click="selectUser">{{$t('选择成员')}}</Button>
                    </div>
                </FormItem>
                <FormItem :label="$t('部门')">
                    <div class="form-value-tag-select">
                        <ColorTag
                            class="choose-tag"
                            v-for="item in formItem.departmentList"
                            :key="'o'+item.id"
                            :closable="true"
                            @on-close="removeFromList(formItem.departmentList,item)">{{item.name}}
                        </ColorTag>
                        <Button icon="ios-add" type="dashed" size="small" @click="selectDepartment">{{$t('选择部门')}}
                        </Button>
                    </div>
                </FormItem>
                <FormItem :label="$t('企业角色')">
                    <div class="form-value-tag-select">
                        <ColorTag
                            class="choose-tag"
                            v-for="item in formItem.companyRoleList"
                            :key="'cr'+item.id"
                            :closable="true"
                            @on-close="removeFromList(formItem.companyRoleList,item)">{{item.name}}
                        </ColorTag>
                        <Button
                            icon="ios-add" type="dashed" size="small" @click="selectCompanyRole">{{$t('选择角色')}}
                        </Button>
                    </div>
                </FormItem>
                <FormItem :label="$t('项目角色')">
                    <div class="form-value-tag-select">
                        <ColorTag
                            class="choose-tag"
                            v-for="item in formItem.projectRoleList"
                            :key="'pr'+item.id"
                            :closable="true"
                            @on-close="removeFromList(formItem.projectRoleList,item)">{{item.name}}
                        </ColorTag>
                        <Button
                            icon="ios-add" type="dashed" size="small" @click="selectProjectRole()">{{$t('选择角色')}}
                        </Button>
                    </div>
                </FormItem>
                <div class="line"></div>
                <FormItem v-if="formItem.id>0" label="">
                    <Button @click="deleteItem" type="error">{{$t('删除此通知')}}</Button>
                    <Button
                        v-if="formItem.status===1" style="margin-left:10px" @click="setEnable(false)" type="default">
                        {{$t('禁用此通知')}}
                    </Button>
                    <Button v-if="formItem.status===2" style="margin-left:10px" @click="setEnable(true)" type="default">
                        {{$t('启用此通知')}}
                    </Button>
                </FormItem>
            </div>
        </Form>

        <div slot="footer">
            <Row>
                <Col span="12" style="text-align:left;padding-top:5px">
                    <Checkbox v-if="formItem.id===0" v-model="continueCreate" size="large">{{$t('继续创建下一个')}}</Checkbox>&nbsp;
                </Col>
                <Col span="12" style="text-align:right">
                    <Button @click="confirm" type="default" size="large">{{formItem.id>0?$t('保存'):$t('创建')}}</Button>
                </Col>
            </Row>

        </div>

    </Modal>
</template>

<script>
    export default {
        mixins: [componentMixin],

        data() {
            return {
                loaded: false,
                continueCreate: false,
                formItem: {
                    id: 0,
                    name: null,
                    period: 1,
                    notifyTime: null,
                    dailyTime: null,
                    accountList: [],
                    companyRoleList: [],
                    projectRoleList: [],
                    departmentList: [],
                    title: null,
                    content: null,
                    periodSetting: [],
                },
                periodDailyOption: {
                    disabledDate(date) {
                        const now = new Date();
                        const periodDailyStartDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());
                        return date && date.getTime() < periodDailyStartDate.getTime();
                    },
                },
                formRule: {
                    name: [vd.req, vd.name],
                    notifyTime: [vd.req],
                    title: [vd.req, vd.name],
                    content: [vd.req, vd.name2_500],
                },
                projectList: [],
                weeklyPeriodSettingArray: [
                    { name: this.$t('周一'), value: 1 },
                    { name: this.$t('周二'), value: 2 },
                    { name: this.$t('周三'), value: 3 },
                    { name: this.$t('周四'), value: 4 },
                    { name: this.$t('周五'), value: 5 },
                    { name: this.$t('周六'), value: 6 },
                    { name: this.$t('周日'), value: 7 },
                ],
                monthlyPeriodSettingArray: [],
            };
        },
        created() {
            const monthlyPeriodSettingArray = [];
            for (let i = 1; i <= 28; i++) {
                monthlyPeriodSettingArray.push({
                    name: this.$t('号', [i]),
                    value: i,
                });
            }
            this.monthlyPeriodSettingArray = monthlyPeriodSettingArray;
        },
        methods: {
            pageLoad() {
                if (this.args.id) {
                    this.loadData(this.args.id);
                } else if (this.args.copyId) {
                    this.loadData(this.args.copyId, true);
                } else {
                    this.loaded = true;
                }
            },
            loadData(id, copy) {
                app.invoke('BizAction.getSystemNotificationById', [app.token, id], (info) => {
                    const formItem = info;
                    formItem.dailyTime = null;
                    if (formItem.period === 1 && formItem.periodSetting && formItem.periodSetting.length > 0) {
                        formItem.dailyTime = new Date(formItem.periodSetting[0]);
                        if (Number.isNaN(formItem.dailyTime.getTime())) {
                            formItem.dailyTime = null;
                        }
                    }
                    this.formItem = formItem;
                    this.loaded = true;
                    if (copy) {
                        this.formItem.id = 0;
                    }
                });
            },
            onPeriodChange() {
                setTimeout(() => {
                    this.formItem.periodSetting = [];
                }, 0);
            },
            onDatePeriodClear() {
                this.formItem.periodSetting = [];
            },
            onDatePeriodConfirm() {
                setTimeout(() => {
                    if (!this.formItem.dailyTime) {
                        this.formItem.periodSetting = [];
                        return;
                    }
                    this.formItem.periodSetting = [this.formItem.dailyTime.getTime()];
                }, 0);
            },
            addToList(list, item) {
                const index = list.findIndex(litem => litem.id === item.id);
                if (index > -1) {
                    return;
                }
                list.push(item);
            },
            removeFromList(list, item) {
                const index = list.findIndex(litem => litem.id === item.id);
                if (index === -1) {
                    return;
                }
                list.splice(index, 1);
            },
            selectUser() {
                app.showDialog(MemberSelectDialog, {
                    callback: (list) => {
                        if (!Array.isArray(list) || list.length === 0) {
                            return;
                        }
                        if (!Array.isArray(this.formItem.accountList)) {
                            this.formItem.accountList = [];
                        }
                        list.forEach(item => {
                            this.addToList(this.formItem.accountList, {
                                id: item.accountId,
                                name: item.title,
                            });
                        });
                    },
                });
            },
            selectDepartment() {
                app.showDialog(DepartmentSelectDialog, {
                    callback: (list) => {
                        if (!Array.isArray(list) || list.length === 0) {
                            return;
                        }
                        if (!Array.isArray(this.formItem.departmentList)) {
                            this.formItem.departmentList = [];
                        }
                        list.forEach(item => {
                            this.addToList(this.formItem.departmentList, {
                                id: item.id,
                                name: item.title,
                            });
                        });
                    },
                });
            },
            selectCompanyRole() {
                app.showDialog(RoleSelectDialog, {
                    roleType: 2,
                    callback: (list) => {
                        if (!Array.isArray(list) || list.length === 0) {
                            return;
                        }
                        if (!Array.isArray(this.formItem.companyRoleList)) {
                            this.formItem.companyRoleList = [];
                        }
                        list.forEach(item => {
                            this.addToList(this.formItem.companyRoleList, {
                                id: item.id,
                                name: item.name,
                            });
                        });
                    },
                });
            },
            selectProjectRole() {
                app.showDialog(RoleSelectDialog, {
                    roleType: 1,
                    callback: (list) => {
                        if (!Array.isArray(list) || list.length === 0) {
                            return;
                        }
                        if (!Array.isArray(this.formItem.projectRoleList)) {
                            this.formItem.projectRoleList = [];
                        }
                        list.forEach(item => {
                            this.addToList(this.formItem.projectRoleList, {
                                id: item.id,
                                name: item.name,
                            });
                        });
                    },
                });
            },
            deleteItem() {
                app.confirm(this.$t('确认要删除吗？', [this.formItem.name]), () => {
                    app.invoke('BizAction.deleteSystemNotification', [app.token, this.formItem.id], (info) => {
                        app.toast(this.$t('删除成功'));
                        app.postMessage('report.notification.edit');
                        this.showDialog = false;
                    });
                });
            },
            setEnable(enable) {
                var status = enable ? 1 : 2;
                app.invoke('BizAction.setSystemNotificationStatus', [app.token, this.formItem.id, status], (info) => {
                    app.toast(this.$t('操作成功'));
                    app.postMessage('report.notification.edit');
                    this.showDialog = false;
                });
            },
            confirm() {
                this.$refs.form.validate((r) => {
                    if (r) {
                        this.confirmForm();
                    }
                });
            },
            isEmptyArray(list) {
                return !Array.isArray(list) || list.length === 0;
            },
            confirmForm() {
                let hasChooseUser = false;
                if (!this.isEmptyArray(this.formItem.accountList)) {
                    hasChooseUser = true;
                }
                if (!this.isEmptyArray(this.formItem.companyRoleList)) {
                    hasChooseUser = true;
                }
                if (!this.isEmptyArray(this.formItem.projectRoleList)) {
                    hasChooseUser = true;
                }
                if (!this.isEmptyArray(this.formItem.departmentList)) {
                    hasChooseUser = true;
                }
                if (!hasChooseUser) {
                    app.toast(this.$t('请选择通知用户'));
                    return;
                }
                var action = this.formItem.id > 0 ? 'BizAction.updateSystemNotification' : 'BizAction.addSystemNotification';
                app.invoke(action, [app.token, this.formItem], (info) => {
                    app.toast(this.$t('操作成功'));
                    app.postMessage('report.notification.edit');
                    if (!this.continueCreate) {
                        this.showDialog = false;
                    } else {
                        this.$refs.form.resetFields();
                    }
                });
            },
        },
    };
</script>
