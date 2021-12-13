<style scoped>
</style>
<i18n>
    {
    "en": {
    "创建成员":"Create member",
    "姓名":"Name",
    "用户名":"Username",
    "手机号码":"Phone",
    "邮箱":"Email",
    "密码":"Password",
    "全局角色":"Global role",
    "所属部门":"Department",
    "清除所有":"Clear all",
    "清除":"Clear",
    "找不到匹配的数据":"No matching data found",
    "搜索部门":"Search department",
    "继续创建下一个":"Continue to create the next",
    "创建":"Create",
    "请选择用户角色":"Please select user roles",
    "请选择用户所属部门":"Please select the department",
    "创建成功":"Create success"
    },
    "zh_CN": {
    "创建成员":"创建成员",
    "姓名":"姓名",
    "用户名":"用户名",
    "手机号码":"手机号码",
    "邮箱":"邮箱",
    "密码":"密码",
    "全局角色":"全局角色",
    "所属部门":"所属部门",
    "清除所有":"清除所有",
    "清除":"清除",
    "找不到匹配的数据":"找不到匹配的数据",
    "搜索部门":"搜索部门",
    "继续创建下一个":"继续创建下一个",
    "创建":"创建",
    "请选择用户角色":"请选择用户角色",
    "请选择用户所属部门":"请选择用户所属部门",
    "创建成功":"创建成功"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('创建成员')" width="700">
        <Form :rules="formRule" :model="formItem" ref="form" label-position="top" style="height:500px;padding:15px">
            <Row>
                <Col span="11">
                    <FormItem :label="$t('姓名')" prop="name">
                        <Input v-model.trim="formItem.name"></Input>
                    </FormItem>
                </Col>
                <Col span="11" offset="2">
                    <FormItem :label="$t('用户名')" prop="userName">
                        <Input v-model.trim="formItem.userName"></Input>
                    </FormItem>
                </Col>
            </Row>
            <Row>
                <Col span="11">
                    <FormItem :label="$t('手机号码')" prop="mobileNo">
                        <Input v-model.trim="formItem.mobileNo"></Input>
                    </FormItem>
                </Col>
                <Col span="11" offset="2">
                    <FormItem :label="$t('邮箱')" prop="email">
                        <Input v-model.trim="formItem.email"></Input>
                    </FormItem>
                </Col>
            </Row>
            <FormItem :label="$t('密码')">
                {{formItem.password}}
            </FormItem>
            <FormItem :label="$t('全局角色')">
                <CheckboxGroup v-model="formItem.roleList">
                    <Checkbox v-for="role in roleList" :key="'role'+role.id" :label="role.id">{{role.name}}</Checkbox>
                </CheckboxGroup>
            </FormItem>
            <FormItem :label="$t('所属部门')">
                <treeselect v-model="formItem.departmentList"
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
        </Form>
        <div slot="footer">
            <Row>
                <Col span="12" style="text-align:left;padding-top:5px">
                    <Checkbox v-model="continueCreate" size="large">{{$t('继续创建下一个')}}</Checkbox>&nbsp;
                </Col>
                <Col span="12" style="text-align:right">
                    <Button @click="confirm" type="default" size="large">{{$t('创建')}}</Button>
                </Col>
            </Row>

        </div>

    </Modal>
</template>

<script>
    import Treeselect from '@riophae/vue-treeselect'
    import '@riophae/vue-treeselect/dist/vue-treeselect.css'

    export default {
        mixins: [componentMixin],
        components: {Treeselect},
        data() {
            return {
                roleList: [],
                departmentList: [],
                continueCreate: false,
                formItem: {
                    id: 0,
                    name: null,
                    userName: null,
                    email: null,
                    mobileNo: null,
                    password: null,
                },
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
                this.setupData();
                this.getRoleList();
                this.getDepartmentList();
            },
            setupData() {
                this.formItem.password = this.createPassword(8, 12);
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
            createPassword: function (min, max) {
                var num = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"];
                var english = ["a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"];
                var ENGLISH = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"];
                var special = ["-", "_", "#"];
                var config = num.concat(english).concat(ENGLISH).concat(special);
                var arr = [];
                arr.push(getOne(num));
                arr.push(getOne(english));
                arr.push(getOne(ENGLISH));
                arr.push(getOne(special));

                var len = min + Math.floor(Math.random() * (max - min + 1));

                for (var i = 4; i < len; i++) {
                    arr.push(config[Math.floor(Math.random() * config.length)]);
                }

                var newArr = [];
                for (var j = 0; j < len; j++) {
                    newArr.push(arr.splice(Math.random() * arr.length, 1)[0]);
                }

                function getOne(arr) {
                    return arr[Math.floor(Math.random() * arr.length)];
                }

                return newArr.join("");
            },
            confirm() {
                this.$refs.form.validate((r) => {
                    if (r) this.confirmForm()
                });
            },
            confirmForm() {
                if (this.formItem.roleList == null || this.formItem.roleList.length == 0) {
                    app.toast(this.$t('请选择用户角色'));
                    return;
                }
                if (this.formItem.departmentList == null || this.formItem.departmentList.length == 0) {
                    app.toast(this.$t('请选择用户所属部门'));
                    return;
                }
                app.invoke('BizAction.createCompanyAccount', [app.token, this.formItem], (list) => {
                    app.postMessage('companymember.edit');
                    app.toast(this.formItem.name + this.$t('创建成功'));
                    if (!this.continueCreate) {
                        this.showDialog = false;
                    } else {
                        this.$refs.form.resetFields();
                        this.setupData();
                    }
                })
            },
        }
    }
</script>
