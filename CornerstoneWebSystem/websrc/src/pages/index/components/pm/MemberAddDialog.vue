<style scoped>
    .search-box {
        position: absolute;
        top: 32px;
        left: 0;
        width: 422px;
        max-height: 400px;
        overflow: auto;
        background-color: #fff;
        box-shadow: 0 1px 6px rgba(0, 0, 0, .2);
    }

    .search-item {
        padding: 10px;
        font-size: 13px;
        border-bottom: 1px solid #eee;
        cursor: pointer;
    }

    .search-item:hover {
        background-color: #ebf7ff;
    }

    .search-no-data {
        text-align: center;
        padding: 20px;
    }
</style>
<i18n>
    {
    "en": {
    "添加成员": "Add Member",
    "成员角色":"Role",
    "成员标签":"Tag",
    "请选择成员标签":"Select",
    "添加成员标签":"Add Member Tag",
    "未找到数据,您可以添加数据":"No Data",
    "清除所有":"Clear all",
    "清除":"Clear",
    "找不到匹配的用户":"No Data",
    "搜索成员":"Search",
    "添加":"Add",
    "请选择成员角色":"Choose role",
    "请选择用户":"Choose member",
    "添加成功":"Success"
    },
    "zh_CN": {
    "添加成员": "添加成员",
    "成员角色":"成员角色",
    "成员标签":"成员标签",
    "请选择成员标签":"请选择成员标签",
    "添加成员标签":"添加成员标签",
    "未找到数据,您可以添加数据":"未找到数据,您可以添加数据",
    "清除所有":"清除所有",
    "清除":"清除",
    "找不到匹配的用户":"找不到匹配的用户",
    "搜索成员":"搜索成员",
    "添加":"添加",
    "请选择成员角色":"请选择成员角色",
    "请选择用户":"请选择用户",
    "添加成功":"添加成功"
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
        :title="$t('添加成员')"
        width="700"
        @on-ok="confirm">
        <div style="height:500px;padding:10px">
            <div>
                <div style="color:#999;font-size:13px">{{$t('成员角色')}}</div>
                <CheckboxGroup v-model="selectedRoles" style="margin-top:10px">
                    <Checkbox v-for="item in roleList" :key="item.id" :label="item.id">{{item.name}}</Checkbox>
                </CheckboxGroup>

            </div>
            <div style="padding-top: 10px">
                <div style="color:#999;font-size:13px">{{$t('成员标签')}}</div>
                <div style="padding-top: 10px">
                    <Select
                        v-model="memberTags"
                        style="min-width:150px;max-width: 240px"
                        transfer
                        clearable
                        multiple
                        :placeholder="$t('请选择成员标签')"
                        not-found-text="未找到数据,您可以添加数据">
                        <Option v-for="item in projectMemberTags" :value="item" :key="item">{{ item }}</Option>
                    </Select>
                    <Poptip
                        v-model="memberPopperShow" transfer @on-popper-show="onMemberPopperShow">
                        <Button style="margin-left: 20px" size="small" icon="md-add">{{ $t('添加成员标签') }}</Button>
                        <div slot="content" style="padding: 15px 20px;">
                            <div>
                                {{ $t('添加成员标签') }}
                            </div>
                            <div>
                                <Input v-model="memberPopperTag" :maxlength="20" style="width: 180px" />
                                <Button
                                    style="margin-left: 10px"
                                    size="small"
                                    icon="md-checkmark"
                                    @click.native="onMemberAdd">{{ $t('添加') }}
                                </Button>
                            </div>
                        </div>
                    </Poptip>
                </div>
            </div>
            <div style="margin-top:10px;">
                <treeselect
                    v-model="selectedUsers"
                    :clear-all-text="$t('清除所有')"
                    :clear-value-text="$t('清除')"
                    :no-results-text="$t('找不到匹配的用户')"
                    :search-prompt-text="$t('搜索成员')"
                    :placeholder="$t('搜索成员')"
                    no-options-text=""
                    value-format="object"
                    :match-keys='["pinyinName","label","userName"]'
                    :show-count="true"
                    value-consists-of="LEAF_PRIORITY"
                    :always-open="true"
                    :max-height="400"
                    :searchable="true"
                    :default-expand-level="1"
                    :multiple="true"
                    :options="treeData">

                    <label
                        slot="option-label"
                        slot-scope="{ node, shouldShowCount, count, labelClassName, countClassName }"
                        :class="labelClassName">
                        <Icon v-if="node.raw.type==1" type="ios-people" />
                        <Icon v-if="node.raw.type==2" type="md-person" />
                        {{ node.label }}
                        <span v-if="shouldShowCount" :class="countClassName">({{ count }})</span>
                    </label>
                </treeselect>
            </div>

        </div>
        <div slot="footer">
            <Row>
                <Col span="24" style="text-align:right">
                    <Button @click="confirm" type="default" size="large">{{$t('添加')}}</Button>
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
        components: { Treeselect },
        data() {
            return {
                treeData: [],
                roleList: [],
                memberTags: [],
                projectMemberTags: [],
                selectedUsers: [],
                selectedRoles: [],
                memberPopperShow: false,
                memberPopperTag: null,
            };
        },

        methods: {
            pageLoad() {
                this.loadData();
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
            loadData() {
                app.invoke('BizAction.getDepartmentTree', [app.token, true], (info) => {
                    travalTree(info[0], (item) => {
                        item.label = item.title;
                        if (item.type == 1 && item.children.length == 0) {
                            item.isDisabled = true;
                        }
                        if (item.children.length == 0) {
                            delete item.children;
                        }

                    });
                    this.treeData = info;
                });
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
            confirm: function () {
                if (this.selectedRoles.length === 0) {
                    app.toast(this.$t('请选择成员角色'));
                    return;
                }
                if (this.selectedUsers.length === 0) {
                    app.toast(this.$t('请选择用户'));
                    return;
                }
                var userId = this.selectedUsers.map(item => {
                    return item.accountId;
                });
                app.invoke('BizAction.addProjectMembers', [app.token, this.args.projectId, userId, this.selectedRoles, this.memberTags], (info) => {
                    app.toast(this.$t('添加成功'));
                    app.postMessage('member.edit');
                    this.showDialog = false;
                });
            },
        },
    };
</script>
