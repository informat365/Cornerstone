<style lang="less" scoped>
    .company-department-edit-dialog {
        /deep/ .ivu-form {
            .ivu-form-item-error {
                .department-select {
                    .vue-treeselect__control {
                        border: 1px solid #ed4014;
                    }
                }
            }
        }
    }
</style>

<i18n>
    {
    "en": {
    "管理部门":"Department",
    "所属部门":"Department",
    "上级部门":"Parent Department",
    "名称":"Name",
    "部门名称":"Department Name",
    "备注":"Remark",
    "备注信息":"Remark",
    "负责人":"Principal",
    "添加":"Add",
    "删除":"Delete",
    "继续创建下一个":"Continue to create the next ",
    "保存":"Save",
    "删除成功":"Delete success",
    "操作成功":"Success",
    "确认要删除吗？":"Are you sure you want to delete “{0}” ?",
    "创建":"Create"
    },
    "zh_CN": {
    "管理部门":"管理部门",
    "所属部门":"所属部门",
    "上级部门":"上级部门",
    "名称":"名称",
    "部门名称":"部门名称",
    "备注":"备注",
    "备注信息":"备注信息",
    "负责人":"负责人",
    "添加":"添加",
    "删除":"删除",
    "继续创建下一个":"继续创建下一个",
    "保存":"保存",
    "删除成功":"删除成功",
    "操作成功":"操作成功",
    "确认要删除吗？":"确认要删除 “{0}” 吗？",
    "创建":"创建"
    }
    }
</i18n>

<template>
    <Modal
        class="company-department-edit-dialog"
        ref="dialog"
        v-model="showDialog"
        :closable="true"
        :mask-closable="false"
        :loading="false"
        :title="$t('管理部门')"
        width="600"
        @on-ok="confirm">
        <Form ref="form" :rules="formRule" :model="formItem" label-position="top" style="height:500px;padding:15px">
            <FormItem :label="$t('所属部门')" prop="parentId" v-if="formItem.parentId !== 0">
                <DepartmentTreeSelect v-model="formItem.parentId" />
            </FormItem>
            <FormItem :label="$t('名称')" prop="name">
                <Input :placeholder="$t('部门名称')" v-model.trim="formItem.name" />
            </FormItem>
            <FormItem :label="$t('备注')" prop="remark">
                <Input :placeholder="$t('备注信息')" type="textarea" v-model.trim="formItem.remark"></Input>
            </FormItem>
            <FormItem :label="$t('负责人')">
                <ColorTag
                    style="margin-bottom:5px"
                    v-for="item in formItem.ownerAccountList"
                    :key="'o'+item.id"
                    :closable="true"
                    @on-close="removeFromList(formItem.ownerAccountList,item)">{{item.name}}
                </ColorTag>
                <Button icon="ios-add" type="dashed" size="small" @click="selectObject()">{{$t('添加')}}</Button>
            </FormItem>
            <FormItem v-if="formItem.id>0" label="">
                <Button @click="deleteDepartment" type="error">{{$t('删除')}}</Button>
            </FormItem>
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
        components: {},
        mixins: [componentMixin],
        data() {
            return {
                continueCreate: false,
                formItem: {
                    id: 0,
                    parentId: null,
                    name: null,
                    remark: null,
                    ownerAccountList: [],
                },
                formRule: {
                    parentId: [vd.req],
                    name: [vd.req, vd.name],
                    remark: [vd.desc],
                },
            };
        },
        methods: {
            pageLoad() {
                if (this.args.parentNode) {
                    this.formItem.parentId = this.args.parentNode.id;
                }
                if (this.args.id) {
                    this.loadData();
                }
            },
            loadData() {
                app.invoke('BizAction.getDepartmentInfo', [app.token, this.args.id], (info) => {
                    this.formItem = info;
                });
            },
            deleteDepartment() {
                app.confirm(this.$t('确认要删除吗？', [this.formItem.name]), () => {
                    app.invoke('BizAction.deleteDepartment', [app.token, this.formItem.id], (info) => {
                        app.toast('删除成功');
                        app.postMessage('department.edit');
                        this.showDialog = false;
                    });
                });
            },
            selectObject() {
                app.showDialog(MemberSelectDialog, {
                    callback: (list) => {
                        list.forEach(item => {
                            var user = {
                                id: item.accountId,
                                name: item.title,
                            };
                            this.addToList(this.formItem.ownerAccountList, user);
                        });
                    },
                });
            },
            addToList(list, item) {
                for (var i = 0; i < list.length; i++) {
                    var t = list[i];
                    if (t.id == item.id) {
                        return;
                    }
                }
                list.push(item);
            },
            removeFromList(list, item) {
                for (var i = 0; i < list.length; i++) {
                    var t = list[i];
                    if (t.id == item.id) {
                        list.splice(i, 1);
                        return;
                    }
                }
            },
            confirm() {
                this.$refs.form.validate((r) => {
                    if (r) {
                        this.confirmForm();
                    }
                });
            },
            confirmForm() {
                var idList = [];
                this.formItem.ownerAccountList.forEach(item => {
                    idList.push(item.id);
                });
                this.formItem.ownerAccountIdList = idList;
                var action = this.formItem.id > 0 ? 'BizAction.updateDepartment' : 'BizAction.createDepartment';
                app.invoke(action, [app.token, this.formItem], (info) => {
                    app.toast('操作成功');
                    app.postMessage('department.edit');
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
