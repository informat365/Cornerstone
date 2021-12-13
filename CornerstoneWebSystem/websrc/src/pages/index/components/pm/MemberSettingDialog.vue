<style scoped></style>
<i18n>
    {
    "en": {
    "设置成员": "Member Setting",
    "成员":"Member",
    "角色":"Role",
    "成员标签":"Tag",
    "请选择成员标签":"Select",
    "添加成员标签":"Add Member Tag",
    "添加":"Add",
    "未找到数据,您可以添加数据":"No Data",
    "操作":"Edit",
    "移除成员":"Remove",
    "保存":"Save",
    "请选择角色":"Choose role"
    },
    "zh_CN": {
    "设置成员": "设置成员",
    "成员":"成员",
    "角色":"角色",
    "成员标签":"成员标签",
    "请选择成员标签":"请选择成员标签",
    "添加成员标签":"添加成员标签",
    "添加":"添加",
    "未找到数据,您可以添加数据":"未找到数据,您可以添加数据",
    "操作":"操作",
    "移除成员":"移除成员",
    "保存":"保存",
    "请选择角色":"请选择角色"
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
            :title="$t('设置成员')"
            width="700">

        <Form label-position="top" style="padding:15px">
            <FormItem :label="$t('成员')">
                <AvatarImage :name="member.accountName" :value="member.accountImageId" type="label"/>
            </FormItem>

            <FormItem :label="$t('角色')">
                <CheckboxGroup :disabled="disabled" v-model="roleIdList" style="margin-top:10px">
                    <Checkbox v-for="item in roleList" :key="item.id" :label="item.id">{{item.name}}</Checkbox>
                </CheckboxGroup>
            </FormItem>
            <FormItem :label="$t('成员标签')">
                <Select
                        :disabled="disabled"
                        v-model="memberTags"
                        style="min-width:150px;max-width: 240px"
                        transfer
                        multiple
                        :placeholder="$t('请选择成员标签')"
                        not-found-text="未找到数据,您可以添加数据">
                    <Option v-for="item in projectMemberTags" :value="item" :key="item">{{ item }}</Option>
                </Select>
                <Poptip
                        v-model="memberPopperShow" transfer @on-popper-show="onMemberPopperShow">
                    <Button :disabled="disabled" style="margin-left: 20px" size="small" icon="md-add">{{ $t('添加成员标签') }}</Button>
                    <div slot="content" style="padding: 15px 20px;">
                        <div>
                            {{ $t('添加成员标签') }}
                        </div>
                        <div>
                            <Input v-model="memberPopperTag" :maxlength="20" style="width: 180px"/>
                            <Button v-if="!disabled"
                                    style="margin-left: 10px" size="small" icon="md-checkmark" @click.native="onMemberAdd">
                                {{ $t('添加') }}
                            </Button>
                        </div>
                    </div>
                </Poptip>
            </FormItem>

            <FormItem :label="$t('操作')" v-if="!disabled">
                <Button @click="deleteMember" type="error" size="large">{{$t('移除成员')}}</Button>
            </FormItem>
        </Form>

        <div slot="footer">
            <Row v-if="!disabled">
                <Col span="24" style="text-align:right">
                    <Button @click="updateMember" type="default" size="large">{{$t('保存')}}</Button>
                </Col>
            </Row>
        </div>

    </Modal>
</template>


<script>
    export default {
        name: 'MemberSettingDialog',
        mixins: [componentMixin],
        data() {
            return {
                member: {},
                memberTags: [],
                projectMemberTags: [],
                roleIdList: [],
                roleList: [],
                memberPopperShow: false,
                memberPopperTag: null,
                batch: false,
                disabled: false
            };
        },
        methods: {
            pageLoad() {
                this.member = this.args.member;
                this.roleIdList = this.member.roleList.map(item => {
                    return item.id;
                });
                if (Array.isArray(this.member.tag)) {
                    this.memberTags = [
                        ...this.member.tag,
                    ];
                }
                this.loadRole();
                this.disabled = this.args.project && this.args.project.isFinish;
            },

            onMemberPopperShow() {
                this.memberPopperTag = null;
            },
            onMemberAdd() {
                if (!this.memberPopperTag) {
                    return;
                }
                if (!Array.isArray(this.projectMemberTags)) {
                    this.projectMemberTags = [this.memberPopperTag];
                    return;
                }
                if (this.projectMemberTags.indexOf(this.memberPopperTag) > -1) {
                    return;
                }
                this.projectMemberTags.push(this.memberPopperTag);
                this.memberPopperShow = false;
            },
            loadRole() {
                app.invoke('BizAction.getRoleInfoList', [app.token, 1], (list) => {
                    //允许项目经理添加成员为项目管理员
                    if(!app.isAddMemberLimit&&this.args.isCurrentProjectManager){
                        if(list){
                            this.roleList = list.filter(k=>k.id!==3);
                        }
                    }else{
                        this.roleList = list;
                    }
                });
                app.invoke('BizAction.getProjectMemberTags', [app.token, this.args.projectId], (list) => {
                    this.projectMemberTags = list;
                });
            },
            deleteMember() {
                app.invoke('BizAction.deleteProjectMember', [app.token, this.member.id], (list) => {
                    app.postMessage('member.edit');
                    this.showDialog = false;
                });
            },
            updateMember() {
                if (this.roleIdList.length == 0) {
                    app.toast(this.$t('请选择角色'));
                    return;
                }
                app.invoke('BizAction.updateProjectMember', [app.token,
                    this.member.id,
                    this.roleIdList,
                    this.memberTags,
                    true
                ], (list) => {
                    app.postMessage('member.edit');
                    this.showDialog = false;
                });
            },
        },
    };
</script>
