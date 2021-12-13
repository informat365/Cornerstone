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
    "成员":"Member",
    "项目":"Projects",
    "选择项目":"Choose Projects",
    "项目角色":"Project Role",
    "成员设置":"Member settings",
    "批量加入项目":"Join projects",
    "保存":"Save",
    "请选择项目":"Please select projects",
    "请选择项目角色":"Please select project role",
    "操作成功":"Success",
    "保存成功":"Save success"
    },
    "zh_CN": {
    "成员":"成员",
    "项目":"项目",
    "选择项目":"选择项目",
    "项目角色":"项目角色",
    "成员设置":"成员设置",
    "批量加入项目":"批量加入项目",
    "保存":"保存",
    "请选择项目":"请选择项目",
    "请选择项目角色":"请选择项目角色",
    "操作成功":"操作成功",
    "保存成功":"保存成功"
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
        :title="$t('批量加入项目')"
        width="700">
        <Form label-position="top" style="padding:15px">
            <FormItem :label="$t('成员')">
                <AvatarImage :name="info.name" :value="info.imageId" type="label" />
            </FormItem>
            <FormItem :label="$t('项目')">
                <template v-for="item in projectList">
                    <Tag :fade="false" :key="item.id" closable @on-close="onRemoveProject(item.id)">
                        {{ item.name }}
                    </Tag>
                </template>
                <Button size="small" icon="md-add" @click="onClickChooseProject">
                    {{ $t('选择项目') }}
                </Button>
            </FormItem>
            <FormItem :label="$t('项目角色')">
                <CheckboxGroup v-model="roleIds" style="margin-top:10px">
                    <Checkbox v-for="item in roleList" :key="item.id" :label="item.id">{{item.name}}</Checkbox>
                </CheckboxGroup>
            </FormItem>
        </Form>
        <div slot="footer">
            <Button
                type="default" @click="showDialog = false">
                取消
            </Button>
            <Button
                @click="confirm" type="primary">{{$t('保存')}}
            </Button>
        </div>
    </Modal>
</template>
<script>
    export default {
        mixins: [componentMixin],
        data() {
            return {
                info: {},
                roleIds: [],
                roleList: [],
                projectIds: [],
                projectList: [],
                company: {},
            };
        },
        methods: {
            pageLoad() {
                this.company = app.company;
                this.loadData(this.args.id);
            },
            loadData(id) {
                app.invoke('BizAction.getAccountCompanyInfo', [app.token, id], (info) => {
                    info.departmentIdList = info.departmentList.map(item => {
                        return parseInt(item.id);
                    });
                    this.info = info;
                    this.loadRole();
                });
            },
            loadRole() {
                app.invoke('BizAction.getRoleInfoList', [app.token, 1], (list) => {
                    this.roleList = list;
                });
            },
            onClickChooseProject() {
                app.showDialog(CompanyProjectDialog, {
                    callback: (list) => {
                        if (!Array.isArray(list) || list.length === 0) {
                            this.projectList = [];
                            this.projectIds = [];
                            return;
                        }
                        this.projectList = [...list];
                        this.projectIds = this.projectList.map(item => item.id);
                    },
                });
            },
            onRemoveProject(projectId) {
                const index = this.projectIds.indexOf(projectId);
                if (index === -1) {
                    return;
                }
                this.projectList.splice(index, 1);
                this.projectIds = this.projectList.map(item => item.id);
            },
            confirm() {
                if (!Array.isArray(this.projectIds) || this.projectIds.length === 0) {
                    app.toast(this.$t('请选择项目'));
                    return;
                }
                if (!Array.isArray(this.roleIds) || this.roleIds.length === 0) {
                    app.toast(this.$t('请选择项目角色'));
                    return;
                }
                this.confirmForm();
            },
            confirmForm() {
                app.invoke('BizAction.memberJoinProjects', [app.token, this.args.id, this.projectIds, this.roleIds], () => {
                    app.toast(this.$t('操作成功'));
                    this.showDialog = false;
                    if (this.args.callback) {
                        this.args.callback();
                    }
                });
            },
        },
    };
</script>
