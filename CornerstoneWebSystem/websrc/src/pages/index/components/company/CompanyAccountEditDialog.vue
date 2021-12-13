<style scoped>
    .disabled-status {
        color: red;
        margin-left: 10px;
        font-size: 15px;
    }
</style>
<i18n>
    {
    "en":{
    "成员设置":"Member settings",
    "此用户下次登录时将会强制修改密码":"This user will be forced to change the password when they log in.",
    "重置":"Reset",
    "已禁用":"Disabled",
    "已启用":"Enabled",
    "姓名":"Name",
    "用户名":"Username",
    "手机号码":"Phone",
    "邮箱":"Email",
    "参与项目":"Projects",
    "全局角色":"Global role",
    "所属部门":"Department",
    "清除所有":"Clear all",
    "清除":"Clear",
    "找不到匹配的数据":"No matching data found",
    "搜索部门":"Search department",
    "从企业中移除":"Remove from company",
    "从所有项目中移除":"Remove from all projects",
    "批量加入项目":"Join projects",
    "禁用账户":"Disable",
    "启用账户":"Enable",
    "重置密码":"Reset password",
    "强制修改密码":"Force password change",
    "激活AD域账户":"Active the directory account",
    "保存":"Save",
    "确认要移除成员":"Are you sure you want to remove “{0}” ?",
    "移除成功":"Remove success",
    "确认要将成员移出其参与的所有项目吗？":"Are you sure you want to remove “{0}” from projects？",
    "确认要禁用账户吗？禁用后账户将不能登录":"Are you sure you want to disable “{0}” ? After disabling, this account can not be logged in.",
    "确认要重置账户的密码吗？":"Are you sure you want to reset {0}'s password?",
    "新密码为：":"New password：",
    "点击确定后，账户下次登录将强制修改密码":"After clicking OK, “{0}” will be forced to change the password the next time log in",
    "操作成功":"Success",
    "点击确定后，账户的错误登录次数将会被重置":"After clicking OK, {0}'s incorrect login times will be reset.",
    "请选择用户角色":"Please select user role",
    "请选择用户所属部门":"Please select department",
    "保存成功":"Save success",
    "超级BOSS":"Super Boss",
    "boss提示":"This privilage has a higher permission weight,config carefully please.For example,the permission holders can view or edit any project information even though they are not the project member or manager!"
    },
    "zh_CN": {
    "成员设置":"成员设置",
    "此用户下次登录时将会强制修改密码":"此用户下次登录时将会强制修改密码",
    "此用户今天登录时密码输入错误次，超过10次后将不能登录":"此用户今天登录时密码输入错误 {0} 次，超过10次后将不能登录",
    "重置":"重置",
    "已禁用":"已禁用",
    "已启用":"已启用",
    "姓名":"姓名",
    "用户名":"用户名",
    "手机号码":"手机号码",
    "邮箱":"邮箱",
    "参与项目":"参与项目",
    "全局角色":"全局角色",
    "所属部门":"所属部门",
    "清除所有":"清除所有",
    "清除":"清除",
    "找不到匹配的数据":"找不到匹配的数据",
    "搜索部门":"搜索部门",
    "从企业中移除":"从企业中移除",
    "从所有项目中移除":"从所有项目中移除",
    "批量加入项目":"批量加入项目",
    "禁用账户":"禁用账户",
    "启用账户":"启用账户",
    "重置密码":"重置密码",
    "强制修改密码":"强制修改密码",
    "保存":"保存",
    "激活AD域账户":"激活AD域账户",
    "确认要移除成员":"确认要移除成员 “{0}” 吗？",
    "移除成功":"移除成功",
    "禁用账户":"禁用账户",
    "确认要将成员移出其参与的所有项目吗？":"确认要将成员 “{0}” 移出其参与的所有项目吗？",
    "确认要禁用账户吗？禁用后账户将不能登录":"确认要禁用账户 “{0}” 吗？禁用后账户将不能登录",
    "确认要重置账户的密码吗？":"确认要重置账户 “{0}” 的密码吗？",
    "新密码为：":"新密码为：",
    "点击确定后，账户下次登录将强制修改密码":"点击确定后，账户“{0}”下次登录将强制修改密码",
    "操作成功":"操作成功",
    "点击确定后，账户的错误登录次数将会被重置":"点击确定后，账户“{0}” 的错误登录次数将会被重置",
    "请选择用户角色":"请选择用户角色",
    "请选择用户所属部门":"请选择用户所属部门",
    "保存成功":"保存成功",
    "超级BOSS":"超级BOSS",
    "boss提示":"提示：超级boss拥有较高权限(例如在不是项目成员的情况下，查看编辑项目信息等)，请慎重配置"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('成员设置')" width="700">
        <Alert v-if="info.needUpdatePassword">
            {{$t('此用户下次登录时将会强制修改密码')}}
        </Alert>
        <Alert v-if="info.dailyLoginFailCount>0">
            {{$t('此用户今天登录时密码输入错误次，超过10次后将不能登录',[info.dailyLoginFailCount])}}
            <Button @click="resetLoginCount" type="error">
                {{ $t('重置') }}
            </Button>
        </Alert>

        <div style="padding:15px;">
            <AvatarImage size="verylarge" :value="info.imageId" :name="info.name" type="tips"></AvatarImage>
            <span class="disabled-status" v-if="info.status==2">
                    {{ $t('已禁用') }}
                </span>
        </div>
        <Form :rules="formRule" :model="info" ref="form" label-position="top" style="min-height:500px;padding:15px">
            <Row>
                <Col span="11">
                    <FormItem :label="$t('姓名')" prop="name">
                        <Input :disabled="info.status==2" v-model.trim="info.name"></Input>
                    </FormItem>
                </Col>
                <Col span="11" offset="2">
                    <FormItem :label="$t('用户名')" prop="userName">
                        <Input :disabled="info.status==2" v-model.trim="info.userName"></Input>
                    </FormItem>
                </Col>
            </Row>
            <Row>
                <Col span="11">
                    <FormItem :label="$t('手机号码')" prop="mobileNo">
                        <Input  v-model.trim="info.mobileNo"></Input>
                    </FormItem>
                </Col>
                <Col span="11" offset="2">
                    <FormItem :label="$t('邮箱')" prop="email">
                        <Input :disabled="info.status==2" v-model.trim="info.email"></Input>
                    </FormItem>
                </Col>
            </Row>

            <FormItem v-if="info.projectList&&info.projectList.length>0" :label="$t('参与项目')">
                <Tag v-for="item in info.projectList" :key="'prj_'+item.id">{{item.name}}</Tag>


            </FormItem>
            <FormItem :label="$t('全局角色')">
                <CheckboxGroup v-model="info.roleList">
                    <Checkbox v-for="role in roleList" :key="'role'+role.id" :label="role.id">{{role.name}}</Checkbox>
                </CheckboxGroup>
            </FormItem>
            <FormItem :label="$t('所属部门')">
                <treeselect v-model="info.departmentIdList"
                            :clear-all-text="$t('清除所有')"
                            :clear-value-text="$t('清除')"
                            :flat="true"
                            :no-results-text="$t('找不到匹配的数据')"
                            :search-prompt-text="$t('搜索部门')"
                            :placeholder="$t('搜索部门')"
                            no-options-text=""
                            value-format="id"
                            :match-keys='["pinyin","label"]'
                            :show-count="true" value-consists-of="LEAF_PRIORITY"
                            :always-open="false"
                            :max-height="400"
                            :searchable="true" :default-expand-level="1" :multiple="true"
                            :options="departmentList">

                    <label slot="option-label"
                           slot-scope="{ node, shouldShowCount, count, labelClassName, countClassName }"
                           :class="labelClassName">
                        <Icon v-if="node.raw.type==1" type="ios-people"/>
                        <Icon v-if="node.raw.type==2" type="md-person"/>
                        {{ node.label }}
                        <span v-if="shouldShowCount" :class="countClassName">({{ count }})</span>
                    </label>
                </treeselect>
            </FormItem>
            <FormItem v-if="perm('company_admin_manage')" :label="$t('超级BOSS')">
                <span style="color: #dd5826;font-size: 12px;">{{$t('boss提示')}}</span>
              <RadioGroup v-model="info.superBoss">
                  <Radio :value="0" :label="0" >非超级BOSS</Radio>
                  <Radio :value="1" :label="1" >超级BOSS（查看修改）</Radio>
                  <Radio :value="2" :label="2"  >超级BOSS（仅查看）</Radio>
                  <Radio :value="3" :label="3" >部门BOSS（查看修改）</Radio>
                  <Radio :value="4" :label="4"  >部门BOSS（仅查看）</Radio>
              </RadioGroup>
          </FormItem>
            <FormItem>
                <Button style="margin-right:10px;margin-bottom: 10px" @click="removeMember" type="error">
                    {{$t('从企业中移除')}}
                </Button>
                <Button style="margin-right:10px;margin-bottom: 10px" @click="removeProject" type="error">
                    {{$t('从所有项目中移除')}}
                </Button>
                <Button style="margin-right:10px;margin-bottom: 10px" @click="bathJoinProject" type="error">
                    {{$t('批量加入项目')}}
                </Button>
                <Button style="margin-right:10px;margin-bottom: 10px" type="default"
                        v-if="company.version==2&&info.status==1" @click="disableAccount">{{$t('禁用账户')}}
                </Button>
                <Button style="margin-right:10px;margin-bottom: 10px" type="default"
                        v-if="company.version==2&&info.status==2" @click="enableAccount(0)">{{$t('启用账户')}}
                </Button>
                <Button  style="margin-right:10px;margin-bottom: 10px" type="default"
                        v-if="company.version==2&&info.status==1&&!info.isActivated&&info.adName" @click="enableAccount(1)">{{$t('激活AD域账户')}}
                </Button>
<!--                <Button  style="margin-right:10px;margin-bottom: 10px" type="default"-->
<!--                        v-if="company.version==2&&info.status==1&&info.isActivated&&info.adName" @click="enableAccount">{{$t('禁用AD域账户')}}-->
<!--                </Button>-->
                <Button style="margin-right:10px;margin-bottom: 10px" type="default" v-if="company.version==2"
                        @click="resetPassword">{{$t('重置密码')}}
                </Button>
                <Button style="margin-right:10px;margin-bottom: 10px" type="default"
                        v-if="company.version==2&&info.needUpdatePassword==false" @click="forceChangePassword">
                    {{$t('强制修改密码')}}
                </Button>
            </FormItem>
        </Form>

        <div slot="footer">
            <Row>
                <Col span="24" style="text-align:right">
                    <Button @click="confirm" type="default" size="large">{{$t('保存')}}</Button>
                </Col>
            </Row>

        </div>

    </Modal>
</template>


<script>
    import Treeselect from '@riophae/vue-treeselect';
    import '@riophae/vue-treeselect/dist/vue-treeselect.css';

    export default {
        mixins: [componentMixin],
        components: {Treeselect},
        data() {
            return {
                info: {
                    superBoss:0
                },
                roleList: [],
                departmentList: [],
                company: {},
                formRule: {
                    name: [vd.req, vd.name],
                    userName: [vd.req, vd.name],
                    email: [vd.email],
                    mobileNo: [vd.mobile],
                },
            }
        },
        methods: {
            pageLoad() {
                this.company = app.company;
                this.loadData(this.args.id);
                this.getRoleList();
                this.getDepartmentList();
            },
            loadData(id) {
                app.invoke('BizAction.getAccountCompanyInfo', [app.token, id], (info) => {
                    if(!!info.departmentList){
                        var departIds = info.departmentList.map(item => {
                            return parseInt(item.id)
                        })
                        info.departmentIdList = departIds;
                    }
                    this.info = info;
                })
            },
            getRoleList() {
                app.invoke('BizAction.getRoleInfoList', [app.token, 2], (list) => {
                    this.roleList = list;
                })
            },
            getDepartmentList() {
                app.invoke('BizAction.getDepartmentTree', [app.token, false], (info) => {
                    travalTree(info[0], (item) => {
                        item.label = item.title;
                        if (item.children.length == 0) {
                            delete item.children;
                        }
                    })
                    this.departmentList = info;

                })
            },
            removeMember() {
                app.confirm(this.$t("确认要移除成员", [this.info.name]), () => {
                    app.invoke('BizAction.deleteCompanyMember', [app.token, this.info.id], (info) => {
                        app.toast(this.$t('移除成功'))
                        this.showDialog = false;
                        app.postMessage('companymember.edit')
                    })
                })
            },
            removeProject() {
                app.confirm(this.$t("确认要将成员移出其参与的所有项目吗？", [this.info.name]), () => {
                    app.invoke('BizAction.deleteCompanyMemberProject', [app.token, this.info.id], (info) => {
                        app.toast(this.$t('移除成功'))
                        this.showDialog = false;
                        app.postMessage('companymember.edit')
                    })
                })
            },
            bathJoinProject() {
                app.showDialog(CompanyAccountJoinProjectDialog, {
                    id: this.info.id,
                    callback: () => {
                        this.loadData(this.args.id);
                    },
                });
            },
            disableAccount() {
                app.confirm(this.$t("确认要禁用账户吗？禁用后账户将不能登录", [this.info.name]), () => {
                    app.invoke('BizAction.disableAccount', [app.token, this.info.id], (info) => {
                        app.toast(this.info.name + this.$t('已禁用'))
                        this.showDialog = false;
                        app.postMessage('companymember.edit')
                    })
                })
            },
            enableAccount(type) {
                app.invoke('BizAction.enableAccount', [app.token, this.info.id,type], (info) => {
                    if(type==1){
                        app.toast(this.info.name + this.$t('已激活'))
                    }else{
                        app.toast(this.info.name + this.$t('已启用'))
                    }
                    this.showDialog = false;
                    app.postMessage('companymember.edit')
                })
            },
            resetPassword() {
                app.confirm(this.$t("确认要重置账户的密码吗？", [this.info.name]), () => {
                    app.invoke('BizAction.resetAccountPassword', [app.token, this.info.id], (info) => {
                        this.showDialog = false;
                        app.confirm(this.$t('新密码为：') + info);
                    })
                })

            },
            forceChangePassword() {
                app.confirm(this.$t("点击确定后，账户下次登录将强制修改密码", [this.info.name]), () => {
                    app.invoke('BizAction.forceResetPassword', [app.token, this.info.id], (info) => {
                        this.showDialog = false;
                        app.toast(this.$t('操作成功'))
                    })
                })
            },
            resetLoginCount() {
                app.confirm(this.$t("点击确定后，账户的错误登录次数将会被重置", [this.info.name]), () => {
                    app.invoke('BizAction.resetAccountDailyLoginFailCount', [app.token, this.info.id], (info) => {
                        this.showDialog = false;
                        app.toast(this.$t('操作成功'))
                    })
                })
            },
            confirm() {
                this.$refs.form.validate((r) => {
                    if (r) this.confirmForm()
                });
            },
            confirmForm() {
                if (this.info.roleList == null || this.info.roleList.length == 0) {
                    app.toast(this.$t('请选择用户角色'));
                    return;
                }
                if (this.info.departmentIdList == null || this.info.departmentIdList.length == 0) {
                    app.toast(this.$t('请选择用户所属部门'));
                    return;
                }
                app.invoke('BizAction.updateCompanyAccount', [app.token,
                    this.info.id,
                    this.info], () => {
                    this.showDialog = false;
                    app.postMessage('companymember.edit');
                    app.toast(this.$t('保存成功'));
                })
            },
        }
    }
</script>
