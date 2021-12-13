<style scoped lang="less">
    .opt-bar {
        background-color: #f1f4f5;
        margin-top: 0;
    }

    .opt-right {
        text-align: right;
    }

    .create-popup {
        position: fixed;
        width: 200px;
        background-color: #fff;
        box-shadow: 0 1px 6px rgba(0, 0, 0, .2);
        z-index: 999;
    }

    .create-popup-opt {
        position: absolute;
        top: 0;
        left: 0;
        width: 200px;
        padding-left: 20px;
        color: #999;
    }

    .nodata {
        font-size: 20px;
        color: #999;
        margin-top: 30px;
    }

    .wechat-label {
        display: inline-block;
        padding: .25em .6em;
        font-size: 12px;
        font-weight: 600;
        line-height: 1;
        text-align: center;
        white-space: nowrap;
        vertical-align: middle;
        border-radius: 1rem;
        color: #fff;
        background-color: #83db5e;
        margin-left: 5px;
    }

    .checkbox-col {
        width: 60px;
    }

    .operation-box {
        text-align: center;

        div {
            padding: 8px 0 !important;
            cursor: pointer;

            &:hover {
                background-color: #eee;
            }
        }
    }

    .vertical-center-modal {
        display: flex;
        align-items: center;
        justify-content: center;

        .ivu-modal {
            top: 0;
            min-height: 600px;
        }
    }

    .model-container {
        min-height: 500px;
    }
</style>

<i18n>
    {
    "en": {
    "用户名":"Username",
    "姓名":"Name",
    "导入":"Import",
    "角色":"Role",
    "状态":"Status",
    "查询":"Query",
    "创建成员":"Create User",
    "邀请成员":"Invite User",
    "部门":"Department",
    "加入日期":"Create time",
    "微信":"Wechat",
    "设置":"Settings",
    "批量":"Batch",
    "修改角色":"Update account roles",
    "调整部门":"Adjust department",
    "加入项目":"Join project",
    "移出项目":"Remove project",
    "冻结账号":"Freeze Account",
    "删除账号":"Delete Account",
    "强制改密":"Force update passwd alert",
    "批量修改角色":"batch to update account role",
    "批量调整部门":"batch to adjust department",
    "批量加入项目":"batch to join project",
    "批量移出项目":"batch to remove from project",
    "批量冻结账号":"batch to freeze account",
    "批量删除账号":"batch to delete account",
    "批量账号改密":"batch to force alert account password update",
    "确认批量修改角色":"Do you confirm to batch  update account role？",
    "确认批量调整部门":"Do you confirm batch to adjust department？",
    "确认批量加入项目":"Do you confirm batch to join project？",
    "确认批量移出项目":"Do you confirm batch to remove from project？",
    "确认批量冻结账号":"Do you confirm batch to freeze account?",
    "确认批量删除账号":"Do you confirm batch to delete account?",
    "确认批量账号改密":"Do you confirm batch to force alert account password update?",
    "请先选择要操作的用户账号":"Please select the accounts to operate firstly.",
    "所属部门":"Department",
    "清除所有":"Clear all",
    "清除":"Clear",
    "找不到匹配的数据":"No matching data found",
    "搜索部门":"Search department",
    "选择项目角色":"Please select the project roles"
    },
    "zh_CN": {
    "姓名":"姓名",
    "导入":"导入",
    "角色":"角色",
    "状态":"状态",
    "查询":"查询",
    "创建成员":"创建成员",
    "邀请成员":"邀请成员",
    "部门":"部门",
    "用户名":"用户名",
    "加入日期":"加入日期",
    "微信":"微信",
    "设置":"设置",
    "批量":"批量",
    "修改角色":"修改角色",
    "调整部门":"调整部门",
    "加入项目":"加入项目",
    "移出项目":"移出项目",
    "冻结账号":"冻结账号",
    "删除账号":"删除账号",
    "强制改密":"强制改密",
    "批量修改角色":"批量修改角色",
    "批量调整部门":"批量调整部门",
    "批量加入项目":"批量加入到项目",
    "批量移出项目":"批量移出项目",
    "批量冻结账号":"确定批量冻结这些账号?",
    "批量删除账号":"确定批量删除这些账号?",
    "批量账号改密":"确定批量强制这些账号修改密码?",
    "确认批量修改角色":"确认批量修改角色吗？",
    "确认批量调整部门":"确认批量调整部门吗？",
    "确认批量加入项目":"确认批量加入到项目吗？",
    "确认批量移出项目":"确认批量移出项目吗？",
    "确认批量冻结账号":"确定批量冻结这些账号?",
    "确认批量删除账号":"确定批量删除这些账号?",
    "确认批量账号改密":"确定批量强制这些账号修改密码?",
    "请先选择要操作的用户账号":"请先选择要操作的用户账号",
    "所属部门":"所属部门",
    "清除所有":"清除所有",
    "清除":"清除",
    "找不到匹配的数据":"找不到匹配的数据",
    "搜索部门":"搜索部门",
    "选择项目角色":"请选择项目角色"
    }
    }
</i18n>

<template>
    <div class="page">
        <Row class="opt-bar opt-bar-light">
            <Col span="18" class="opt-left">
                <Form inline @submit.native.prevent>
                    <FormItem>
                        <Input
                            style="width:150px"
                            @on-change="loadData(true)"
                            type="text"
                            v-model="formItem.accountUserName"
                            :placeholder="$t('用户名')"></Input>
                    </FormItem>
                    <FormItem>
                        <Input
                            style="width:150px"
                            @on-change="loadData(true)"
                            type="text"
                            v-model="formItem.accountName"
                            :placeholder="$t('姓名')"></Input>
                    </FormItem>
                    <FormItem>
                        <Input
                            style="width:150px"
                            @on-change="loadData(true)"
                            type="text"
                            v-model="formItem.roleName"
                            :placeholder="$t('角色')"></Input>
                    </FormItem>
                    <FormItem>
                        <DataDictSelect
                                style="width:150px"
                            @change="loadData(true)"
                            clearable
                            :placeholder="$t('状态')"
                            type="Account.status"
                            v-model="formItem.accountStatus"></DataDictSelect>
                    </FormItem>

                    <FormItem>
                        <Select
                            @on-change="loadData(true)"
                            clearable filterable
                            :placeholder="$t('部门')"
                            v-model="formItem.departmentId"
                            style="width:150px">
                            <template v-for="item in departmentList">
                                <Option :value="item.id" :key="item.id">{{ item.name }}</Option>
                            </template>
                        </Select>
                    </FormItem>

                    <FormItem>
                        <Button style="margin: 0 5px;" @click="loadData(true)" type="default">{{$t('查询')}}</Button>
                        <Poptip  placement="right-start"
                            v-if="perm('company_admin_manage')"
                            trigger="hover"
                            transfer>
                            <Button type="default">{{$t('批量')}}</Button>
                            <div slot="content">
                                <div class="operation-box">
                                    <div @click="batchOperate(1)">{{$t('修改角色')}}</div>
                                    <div @click="batchOperate(2)">{{$t('调整部门')}}</div>
                                    <div @click="batchOperate(3)">{{$t('加入项目')}}</div>
                                    <div @click="batchOperate(4)">{{$t('移出项目')}}</div>
                                    <div v-if="company.version==2" @click="batchOperate(5)">{{$t('冻结账号')}}</div>
                                    <div @click="batchOperate(6)">{{$t('删除账号')}}</div>
                                    <div v-if="company.version==2" @click="batchOperate(7)">{{$t('强制改密')}}</div>
                                </div>
                            </div>
                        </Poptip>
                    </FormItem>
                </Form>

            </Col>
            <Col span="6" class="opt-right">
                <Form inline>
                    <FormItem>
                        <Button
                            style="margin-right:5px" v-if="company.version==2" @click="showImportDialog" type="default">
                            {{$t('导入')}}
                        </Button>
                        <Button v-if="company.version==2" @click="showAddDialog" type="default" icon="md-person-add">
                            {{$t('创建成员')}}
                        </Button>
                        <Button v-if="company.version==1" @click="showInviteDialog" type="default" icon="md-person-add">
                            {{$t('邀请成员')}}
                        </Button>
                    </FormItem>
                </Form>
            </Col>
        </Row>


        <div style="padding:20px">
            <BizTable :fixed="true" @change="loadData" :page="pageQuery" :value="tableData">
                <template slot="thead">
                    <tr>
                        <th class="checkbox-col">
                            <Checkbox v-model="allCheck"></Checkbox>
                        </th>
                        <th>{{$t('姓名')}}</th>
                        <th style="width:150px">{{$t('角色')}}</th>
                        <th style="width:150px">{{$t('部门')}}</th>
                        <th style="width:150px">{{$t('用户名')}}</th>
                        <th style="width:100px">{{$t('状态')}}</th>
                        <th style="width:150px">{{$t('加入日期')}}</th>
                        <th style="width:120px;"></th>
                    </tr>
                </template>
                <template slot="tbody">
                    <tr v-for="(item,idx) in tableData" :key="item.id" class="table-row">
                        <td class="checkbox-col">
                            <Checkbox @on-change="checkOne($event,item,idx)" v-model="item.checked"></Checkbox>
                        </td>
                        <td>
                            <AvatarImage
                                size="small"
                                :value="item.accountImageId"
                                :name="item.accountName"
                                type="label"></AvatarImage>
                            <div v-if="item.accountWxOpenId" class="wechat-label">{{$t('微信')}}</div>
                        </td>
                        <td>
                            <Tag v-for="dp in item.roleList" :key="item.id+'_'+dp.id">{{dp.name}}</Tag>
                        </td>
                        <td>
                            <Tag v-for="dp in item.departmentList" :key="item.id+'_'+dp.id">{{dp.name}}</Tag>
                        </td>
                        <td>{{item.accountUserName}}</td>
                        <td>
                            <DataDictLabel type="Account.status" :value="item.accountStatus"/>
                        </td>
                        <td>{{item.createTime|fmtDateTime}}</td>
                        <td style="text-align:right">
                            <Button @click="showEditAccountDialog(item)" type="default">{{$t('设置')}}</Button>
                        </td>
                    </tr>

                </template>
            </BizTable>
        </div>
        <Modal
            v-model="showUpdateRoleModal"
            :closable="true"
            :mask-closable="true"
            :loading="false" :title="$t('批量修改角色')"
            width="960"
            class-name="vertical-center-modal" @on-ok="confirmUpdateRoles">
            <CheckboxGroup v-model="updateRoleIds">
                <Checkbox v-for="role in roleList" :key="'role'+role.id" :label="role.id">{{role.name}}</Checkbox>
            </CheckboxGroup>
        </Modal>
        <Modal
            v-model="showAdjustDepartmentModal"
            :closable="true"
            :mask-closable="true"
            :loading="false" :title="$t('批量调整部门')"
            width="960"
            height="600"
            transfer
            class-name="vertical-center-modal" @on-ok="confirmAdjustDepartments">
            <div class="model-container">
                <treeselect v-model="departmentIds"
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
                            :options="treeDepartmentList"></treeselect>
            </div>
        </Modal>
        <Modal
            v-model="showJoinProjectModal"
            :closable="true"
            :mask-closable="true"
            :loading="false" :title="$t('批量加入项目')"
            width="700"
            class-name="vertical-center-modal" @on-ok="confirmJoinProject">
            <Select label="选择项目" multiple filterable  v-model="joinProjectIds">
                <Option v-for="item in projectList" :key="'prj_'+item.id" :value="item.id" :lable="item.id">{{item.name}}</Option>
            </Select>
            <label style="margin: 5px 0;">{{$t('选择项目角色')}}</label>
            <CheckboxGroup  v-model="projectRoleIds">
                <Checkbox v-for="role in projectRoleList" :key="'role'+role.id" :label="role.id">{{role.name}}</Checkbox>
            </CheckboxGroup>
        </Modal>
        <Modal
            v-model="showRemoveProjectModal"
            :closable="true"
            :mask-closable="true"
            :loading="false" :title="$t('批量移出项目')"
            width="700"
            class-name="vertical-center-modal" @on-ok="confirmRemoveProject">
            <Select label="选择项目" multiple filterable  v-model="removeProjectIds">
                <Option v-for="item in projectList" :key="'prj_'+item.id" :value="item.id" :lable="item.id">{{item.name}}</Option>
            </Select>
        </Modal>
    </div>
</template>

<script>
    import Treeselect from '@riophae/vue-treeselect';
    import '@riophae/vue-treeselect/dist/vue-treeselect.css';

    export default {
        mixins: [componentMixin],
        components: {Treeselect},
        data() {
            return {
                company: {},
                formItem: {
                    accountName: null,
                    accountUserName: null,
                    roleName: null,
                    accountStatus: null,
                    departmentId: null,
                },
                pageQuery: {
                    pageIndex: 1,
                    pageSize: 20,
                },
                projectRoleList:[],
                departmentList: [],
                tableData: [],
                allCheck: false,
                showUpdateRoleModal: false,
                showAdjustDepartmentModal: false,
                showJoinProjectModal: false,
                showRemoveProjectModal: false,
                updateRoleIds: [],
                projectRoleIds:[],
                joinProjectIds: [],
                departmentIds: [],
                removeProjectIds: [],
                roleList: [],
                projectList: [],
                treeDepartmentList: []
            };
        },
        watch: {
            allCheck(val) {
                this.checkAll(val);
            }
        },
        methods: {
            pageLoad() {
                this.company = app.company;
                this.loadData();
                this.loadDepartmentList();
            },
            pageMessage(type) {
                if (type == 'companymember.edit') {
                    this.loadData();
                }
            },
            loadDepartmentList() {
                app.invoke('BizAction.getDepartmentInfoList', [app.token], (list) => {
                    if (Array.isArray(list)) {
                        this.departmentList = list.filter(item => item.type === 1);
                    }
                });
            },
            loadData(resetPage) {
                if (resetPage) {
                    this.pageQuery.pageIndex = 1;
                }
                var query = copyObject(this.pageQuery, this.formItem);
                app.invoke('BizAction.getCompanyMemberList', [app.token, query], (info) => {
                    if (this.allCheck && info.list) {
                        info.list.forEach(item => {
                            item["checked"] = true;
                        })
                    } else {
                        this.allCheck = false;
                    }
                    this.tableData = info.list;
                    // this.tableData.forEach(item => item['checked'] = false);
                    this.pageQuery.total = info.count;
                });
            },
            showEditAccountDialog(item) {
                app.showDialog(CompanyAccountEditDialog, {
                    id: item.accountId,
                });
            },
            showInviteDialog() {
                app.showDialog(InviteUserDialog);
            },
            showAddDialog() {
                app.showDialog(CompanyAccountCreateDialog);
            },
            showImportDialog() {
                app.showDialog(CompanyAccountImportDialog);
            },
            checkAll(val) {
                for (let i = 0; i < this.tableData.length; i++) {
                    this.$set(this.tableData[i], "checked", val);
                }
                this.getAccountIds();
            },
            checkOne(checked, item, idx) {
                this.getAccountIds();
            },
            getAccountIds() {
                let ids = this.tableData.filter(item => item.checked).map(item => item.accountId);
                console.log(ids)
                return ids;
            },
            getRoleList() {
                app.invoke('BizAction.getRoleInfoList', [app.token, 2], (list) => {
                    this.roleList = list;
                })
            },
            getProjectRoleList() {
                app.invoke('BizAction.getRoleInfoList', [app.token, 1], (list) => {
                    this.projectRoleList = list;
                })
            },
            getProjectList() {
                app.invoke('BizAction.getAllProjectList', [app.token, {pageSize:-1}], (list) => {
                    this.projectList = list.list;
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
                    this.treeDepartmentList = info;

                })
            },
            batchOperate(operation) {
                var memberIds = this.getAccountIds();
                if (!memberIds || Array.isEmpty(memberIds)) {
                    app.toast(this.$t('请先选择要操作的用户账号'));
                    return;
                }
                //修改角色
                if (operation == 1) {
                    if (Array.isEmpty(this.roleList)) {
                        this.getRoleList();
                    }
                    this.showUpdateRoleModal = true;
                }
                //调整部门
                if (operation == 2) {
                    if (Array.isEmpty(this.treeDepartmentList)) {
                        this.getDepartmentList();
                    }
                    this.showAdjustDepartmentModal = true;
                }
                //加入项目
                if (operation == 3) {
                    if (Array.isEmpty(this.projectRoleList)) {
                        this.getProjectRoleList();
                        this.getProjectList();
                    }
                    this.showJoinProjectModal = true;
                }
                //移除项目
                if (operation == 4) {
                    if (Array.isEmpty(this.projectList)) {
                        this.getProjectList();
                    }
                    this.showRemoveProjectModal = true;
                }
                //冻结账号
                if (operation == 5) {
                    app.confirm(this.$t('确认批量冻结账号'), () => {
                        app.invoke('BizAction.batchFreezeAccount', [app.token, memberIds], (info) => {
                            app.toast(this.$t('操作成功'));
                            app.postMessage('companymember.edit')
                        });
                    });
                }
                //删除账号
                if (operation == 6) {
                    app.confirm(this.$t('确认批量删除账号'), () => {
                        app.invoke('BizAction.batchDeleteAccount', [app.token, memberIds], (info) => {
                            app.toast(this.$t('操作成功'));
                            app.postMessage('companymember.edit')
                        });
                    });
                }
                //强制改密
                if (operation == 7) {
                    app.confirm(this.$t('确认批量账号改密'), () => {
                        app.invoke('BizAction.batchForceUpdatePasswd', [app.token, memberIds], (info) => {
                            // app.postMessage('companymember.edit')
                            app.toast(this.$t('操作成功'));
                        });
                    });
                }
            },
            confirmUpdateRoles() {
                var memberIds = this.getAccountIds();
                var roleIds = this.updateRoleIds;
                if (!!memberIds && Array.isArray(roleIds) && roleIds.length > 0) {
                    app.confirm(this.$t('确认批量修改角色'), () => {
                        app.invoke('BizAction.batchUpdateRole', [app.token, memberIds, roleIds], (info) => {
                            app.toast(this.$t('操作成功'));
                            app.postMessage('companymember.edit')
                        });
                        this.updateRoleIds = [];
                        this.showUpdateRoleModal = false;
                    });
                }
            },
            confirmAdjustDepartments() {
                var memberIds = this.getAccountIds();
                var departmentIds = this.departmentIds;
                if (!!memberIds && Array.isArray(departmentIds) && departmentIds.length > 0) {
                    app.confirm(this.$t('确认批量调整部门'), () => {
                        app.invoke('BizAction.batchAdjustDepartment', [app.token, memberIds, departmentIds], (info) => {
                            app.toast(this.$t('操作成功'));
                            app.postMessage('companymember.edit')
                        });
                        this.departmentIds = [];
                        this.showAdjustDepartmentModal = false;
                    });
                }
            },
            confirmJoinProject() {
                var memberIds = this.getAccountIds();
                var projectIds = this.joinProjectIds;
                var projectRoleIds = this.projectRoleIds;
                if (!!memberIds && Array.isArray(projectIds) && projectIds.length > 0&&Array.isArray(projectRoleIds)&&projectRoleIds.length>0) {
                    app.confirm(this.$t('确认批量加入项目'), () => {
                        app.invoke('BizAction.batchJoinProject', [app.token, memberIds, projectIds,projectRoleIds], (info) => {
                            app.toast(this.$t('操作成功'));
                            app.postMessage('companymember.edit')
                        });
                        this.joinProjectIds = [];
                        this.showJoinProjectModal = false;
                    });
                }
            },
            confirmRemoveProject() {
                var memberIds = this.getAccountIds();
                var projectIds = this.removeProjectIds;
                if (!!memberIds && Array.isArray(projectIds) && projectIds.length > 0) {
                    app.confirm(this.$t('确认批量移出项目'), () => {
                        app.invoke('BizAction.batchRemoveProject', [app.token, memberIds, projectIds], (info) => {
                            app.toast(this.$t('操作成功'));
                            app.postMessage('companymember.edit')
                        });
                        this.removeProjectIds = [];
                        this.showRemoveProjectModal = false;
                    });
                }
            }
        },
    };
</script>
