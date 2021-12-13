<style scoped>
    .seach-box {
        margin-bottom: 10px;
        position: absolute;
        width: 90%;
        height: 60px;
        background-color: #fff;
        left: 5%;
        top: 51px;
        z-index: 999;
        padding-top: 10px;
    }

    .table-content {
        margin-top: 20px;
    }

</style>
<i18n>
    {
    "en": {
    "团队成员": "Member",
    "搜索成员":"Search member",
    "添加":"Add",
    "批量操作":"Batch",
    "设置成员属性":"Edit",
    "加入":" joined"
    },
    "zh_CN": {
    "团队成员": "团队成员",
    "搜索成员":"搜索成员",
    "批量操作":"批量操作",
    "添加":"添加",
    "设置成员属性":"设置成员属性",
    "加入":"加入"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('团队成员')+ '('+project.memberList.length+')' " width="650" :footer-hide="true">

        <div style="padding:15px;height:500px;">

            <Row style="margin-bottom:10px" class="seach-box">
                <template v-if="prjPerm('project_member_config')==false&&perm('company_admin_manage')==false">
                    <Col span="24"><Input v-model="searchContent" :placeholder="$t('搜索成员')" icon="ios-search"></Input>
                    </Col>
                </template>
                <template v-if="prjPerm('project_member_config')||perm('company_admin_manage')">
                    <Col span="18"><Input v-model="searchContent" :placeholder="$t('搜索成员')" icon="ios-search"></Input>
                    </Col>
                    <Col span="6" style="text-align:right;">
                        <Button :disabled="disabled" style="margin-right: 8px;" @click="showAddDialog" type="default">
                            {{$t('添加')}}
                        </Button>
                        <Poptip
                            v-model="visible"
                            :disabled="disabled"
                            v-if="prjPerm('project_member_config')||perm('company_admin_manage')"
                            transfer
                            class="poptip-full">
                            <Button type="default" :disabled="disabled">{{$t('批量操作')}}</Button>
                            <div slot="content">
                                <div class="popup-box scrollbox">
                                    <div @click="batchRemoveMembers()" class="popup-item-name">移除成员</div>
                                    <div @click="batchSetupRoles()" class="popup-item-name">设置角色</div>
                                </div>
                            </div>
                        </Poptip>
                    </Col>
                </template>
            </Row>

            <table class="table-content">
                <tbody>
                <tr v-if="(prjPerm('project_member_config')||perm('company_admin_manage'))&&!disabled">
                    <td>
                        <Checkbox v-model="checkAll" @on-change="onCheckAll"></Checkbox>
                    </td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
                <tr v-for="(item,idx) in filterList" :key="item.id" class="table-row">
                    <td style="width: 30px;"
                        v-if="(prjPerm('project_member_config')||perm('company_admin_manage'))&&!disabled">
                        <Checkbox :value="item.checked" @on-change="checkOne(item,idx)"></Checkbox>
                    </td>
                    <td style="width:60px">
                        <AvatarImage :name="item.accountName" :value="item.accountImageId" type="tips"/>
                    </td>
                    <td>
                        <div>{{item.accountName}}</div>
                        <div style="color:#999;font-size:12px;margin-top:7px">
                            <span style="padding-right:3px;" :key="role.id"
                                  v-for="role in item.roleList">{{role.name}}</span>
                        </div>
                    </td>
                    <td style="width:140px;color:#999;">{{item.createTime|fmtDate}} {{$t('加入')}}</td>
                    <td style="width:60px;text-align:right">
                        <IconButton v-if="prjPerm('project_member_config')||perm('company_admin_manage')"
                                    @click="showMemberSettingDialog(item)" :tips="$t('设置成员属性')"
                                    icon="ios-settings-outline"></IconButton>
                    </td>

                </tr>
                </tbody>
            </table>

            <Modal
                ref="dialog1" v-model="roleModal" :closable="true" :mask-closable="false"
                width="650" title="选择角色" @on-ok="confirmSetup">
                <CheckboxGroup v-model="checkRoleIds">
                    <Checkbox v-for="role in roleList" :label="role.id" :key="'role_'+role.id">{{role.name}}</Checkbox>
                </CheckboxGroup>
            </Modal>
        </div>

    </Modal>
</template>


<script>
    export default {
        name: "ProjectMemberListDialog",
        mixins: [componentMixin],
        data() {
            return {
                searchContent: null,
                project: {
                    memberList: []
                },
                checkAccountIds: [],
                checkAccounts: [],
                roleList: [],
                checkRoleIds: [],
                roleModal: false,
                checkAll: false,
                visible: false
            }
        },
        computed: {
            filterList() {
                var result = [];
                for (var i = 0; i < this.project.memberList.length; i++) {
                    var item = this.project.memberList[i]
                    var accept = false;
                    if (this.searchContent == null || this.searchContent == "") {
                        accept = true;
                    } else {
                        accept = item.accountName.indexOf(this.searchContent) != -1;
                    }
                    if (accept) {
                        result.push(item);
                    }
                }
                return result;
            },
            disabled() {
                return this.project && this.project.isFinish;
            }
        },
        methods: {
            pageLoad() {
                this.loadData();
            },
            pageMessage(type) {
                if (type == 'member.edit') {
                    this.loadData();
                }
            },
            loadData() {
                app.invoke('BizAction.getProjectInfoByUuid', [app.token, this.args.projectUUID], (info) => {
                    if (!Array.isEmpty(info.memberList)) {
                        info.memberList.forEach(item => item["checked"] = false);
                    }
                    this.project = info;
                });

                app.invoke('BizAction.getRoleInfoList', [app.token, 1], (list) => {
                    //限制项目管理员添加成员为项目管理员
                    if (!app.isAddMemberLimit && this.args.isCurrentProjectManager) {
                        if (list) {
                            this.roleList = list.filter(k => k.id !== 3);
                        }
                    } else {
                        this.roleList = list;
                    }
                });
            },

            showAddDialog() {
                app.showDialog(MemberAddDialog, {
                    projectId: this.project.id,
                    isCurrentProjectManager: this.args.isCurrentProjectManager
                })
            },
            showMemberSettingDialog(item) {
                app.showDialog(MemberSettingDialog, {
                    member: item,
                    projectId: this.project.id,
                    project: this.project,
                    isCurrentProjectManager: this.args.isCurrentProjectManager
                })
            },
            batchRemoveMembers() {
                this.visible = false;
                if (Array.isEmpty(this.checkAccountIds)) {
                    app.toast("请先选择要移除的成员");
                    return false;
                }
                app.showDialog(MultiOperateDialog, {
                    title: '移除成员',
                    runCallback: this.removeAction,
                    finishCallback: this.finishRun,
                    itemList: this.checkAccounts,
                });
            },
            finishRun() {
                // app.postMessage('project.edit');
                this.roleModal = false;
                this.checkAll = false;
                this.loadData();
            },
            removeAction(member, success, error) {
                app.invoke('BizAction.deleteProjectMember', [app.token, member.id], (info) => {
                    success();
                }, error);
            },
            batchSetupRoles() {
                this.visible = false;
                this.roleModal = true;
            },
            confirmSetup() {
                console.log(this.checkRoleIds)
                if (Array.isEmpty(this.checkAccountIds)) {
                    app.toast("请先选择要设置角色的成员");
                    return false;
                }
                if (Array.isEmpty(this.checkRoleIds)) {
                    app.toast("请先选择要设置的角色");
                    return false;
                }
                app.showDialog(MultiOperateDialog, {
                    title: '设置角色',
                    runCallback: this.setupAction,
                    finishCallback: this.finishRun,
                    itemList: this.checkAccounts,
                });
            },
            setupAction(member, success, error) {
                app.invoke('BizAction.updateProjectMember', [app.token, member.id, this.checkRoleIds, [], false], (info) => {
                    success();
                }, error);
            },
            checkOne(item, idx) {
                var checked = !item.checked;
                this.$set(this.filterList[idx], 'checked', checked);
                if (checked) {
                    this.checkAccountIds.push(item.accountId);
                    this.checkAccounts.push(item);
                } else {
                    var index = this.checkAccountIds.findIndex(k => k == item.accountId);
                    if (index > -1) {
                        this.checkAccountIds.splice(index, 1);
                        this.checkAccounts.splice(index, 1);
                    }
                }
            },
            onCheckAll() {
                console.log(this.checkAll)
                this.checkAccountIds = [];
                this.checkAccounts = [];
                for (let i = 0; i < this.filterList.length; i++) {
                    this.$set(this.filterList[i], 'checked', this.checkAll);
                    if (this.checkAll) {
                        this.checkAccountIds.push(this.filterList[i].accountId);
                        this.checkAccounts.push(this.filterList[i]);
                    }
                }
            }
        }
    }
</script>
