<template>
    <Modal
        v-model="showModal" :mask-closable="false" class="modal-edit" width="640">
        <div slot="header">
            {{ formItem.id > 0 ? '编辑' :'新增' }}系统
        </div>
        <div class="form">
            <Form
                ref="form"
                :model="formItem"
                :rules="formRule"
                inline
                label-position="top"
            >
                <FormItem
                    label="系统名称" prop="name">
                    <Input :disabled="readonly"
                           style="width: 250px; margin-top:8px"
                           v-model="formItem.name" :maxlength="32" show-word-limit placeholder="请输入系统名称"/>
                </FormItem>
                <FormItem
                    label="系统经理" prop="ownerAccountId">
                    <CompanyUserSelect
                        v-model="formItem.ownerAccountIdList"
                        :default-list="formItem.ownerAccountList"
                        clearable
                        multiple
                        :readonly="readonly"
                        placeholder="请选择系统经理"
                        style="width: 250px;margin-top:8px"
                    />
                </FormItem>
                <FormItem
                    label="开发科室" prop="ownerDepartmentId">
                    <CompanyDepartmentSelect
                        :readonly="readonly"
                        v-model="formItem.ownerDepartmentIdList"
                        :default-list="formItem.ownerDepartmentList"
                        clearable
                        multiple
                        placeholder="请选择开发科室"
                        style="width: 250px;margin-top:8px"
                    />
                </FormItem>
                <FormItem
                    label="业务经理" prop="businessLeader">
                    <Input :disabled="readonly"
                           style="width: 250px; margin-top:8px"
                           v-model="formItem.businessLeader" placeholder="业务经理"/>
                </FormItem>
                <FormItem
                    label="系统主管部门" prop="department">
                    <Input :disabled="readonly"
                           style="width: 250px; margin-top:8px"
                           v-model="formItem.department" placeholder="系统主管部门"/>
                </FormItem>

                <FormItem
                    label="系统最新版本号" prop="latest">
                    <Input :disabled="readonly"
                           style="width: 250px; margin-top:8px"
                           v-model="formItem.latest" placeholder="系统最新版本号"/>
                </FormItem>

                <FormItem
                    label="系统发布时间" prop="releaseDate">
                    <ExDatePicker
                        :disabled="readonly"
                        clearable type="date"
                        style="width: 250px; margin-top:8px"
                        v-model="formItem.releaseDate"
                        placeholder="系统发布时间"></ExDatePicker>
                </FormItem>

                <FormItem
                    label="系统状态" prop="status">
                    <DataDictSelect
                        :disabled="readonly"
                        style="width: 250px; margin-top:8px"
                        clearable
                        placeholder="系统状态"
                        type="CompanyVersionRepository.status"
                        v-model="formItem.status"
                    ></DataDictSelect>
                </FormItem>
                <FormItem
                    label="是否纳入332N架构体系" title="是否纳入332N架构体系" prop="isArch332n">
                    <div style="width: 250px; margin-top:8px">
                        <i-Switch :disabled="readonly" v-model="formItem.isArch332n" size="small"></i-Switch>
                    </div>

                </FormItem>

                <FormItem
                    label="332N板块分类" prop="arch">
                    <DataDictSelect
                        :disabled="readonly"
                        style="width: 250px; margin-top:8px"
                        clearable
                        placeholder="332N板块分类"
                        type="CompanyVersionRepository.arch"
                        v-model="formItem.arch"
                    ></DataDictSelect>
                </FormItem>

                <br>
                <FormItem
                    label="详细描述(200字以内)" title="详细描述(200字以内)" prop="description">
                    <Input
                        :disabled="readonly"
                        v-model="formItem.description"
                        placeholder="请输入系统简介"
                        type="textarea"
                        :rows="3"
                        :maxlength="200"
                        style="width:515px;margin-top:10px;"/>
                </FormItem>


                <FormItem v-if="formItem.id>0 && perm('version_repository_delete')&&!readonly">
                    <Alert type="error" style="width:515px;margin-top:10px;">
                        系统删除后，将同时删除该系统所有版本数据并且不可恢复，请审慎
                    </Alert>
                    <Button @click="onClickDelete" type="error">删除此系统</Button>
                </FormItem>
            </Form>
        </div>
        <div slot="footer">
            <Button
                type="text" :loading="loading" @click.native="showModal=false">关闭
            </Button>
            <Button v-if="!readonly"
                type="primary" :loading="loading" @click.native="onConfirm">{{formItem.id>0?'保存':'创建'}}
            </Button>
        </div>
    </Modal>
</template>

<script>

    export default {
        name: 'DashboardVersionRepositoryEditDialog',
        mixins: [componentMixin],
        data() {
            return {
                loading: false,
                showModal: false,
                formItem: {
                    id: 0,
                    name: null,
                    description: null,
                },
                formRule: {
                    name: [
                        vd.req, vd.name2_20,
                    ],
                    description: [
                        vd.desc,
                    ],
                },
                ownerDepartment: [],
                ownerAccount: [],
                originReposiotryName: null,
                repeatReposiotryName: null,
                readonly: false
            };
        },
        methods: {
            pageLoad() {
                this.readonly = this.args.readonly;
                this.$refs.form.resetFields();
                this.showModal = true;
                if (this.args.id) {
                    this.formItem.id = this.args.id;
                }
                if (this.formItem.id > 0) {
                    this.loadData();
                }
            },
            loadData() {
                this.loading = true;
                app.invoke('BizAction.getCompanyVersionRepositoryById', [app.token, this.formItem.id], (res) => {
                    if (Object.isEmpty(res)) {
                        return;
                    }
                    this.originReposiotryName = res.name;
                    this.formItem = {
                        ...this.formItem,
                        ...res,
                    };
                   /* if(res.ownerDepartmentId){
                        this.ownerDepartment = [{id: res.ownerDepartmentId, name: res.ownerDepartmentName}]
                    }*/
                    // if(res.ownerAccountId){
                    //
                    // this.ownerAccount = [{id: res.ownerAccountId, name: res.ownerAccountName}]
                    // }

                    this.formItem.isArch332n = this.formItem.isArch332n || false;
                    this.loading = false;
                }, (code, message) => {
                    app.toast(message || '加载系统信息失败');
                    this.loading = false;
                });
            },
            onClickDelete() {
                this.twiceFactorValid(() => {
                    this.loading = true;
                    app.invoke('BizAction.deleteCompanyVersionRepository', [app.token, this.formItem.id], () => {
                        app.toast('删除系统成功');
                        app.postMessage('version.repository.edit');
                        this.showModal = false;
                        this.loading = false;
                    }, (code, message) => {
                        app.toast(message || '删除系统失败');
                        this.loading = false;
                    });
                })
            },
            onConfirm() {
                if(this.formItem.isArch332n&&(!this.formItem.arch||this.formItem.arch<=0)){
                    app.toast('系统332N板块分类不能为空');
                    return false;
                }
                this.$refs.form.validate((valid) => {
                    if (!valid) {
                        return;
                    }
                    this.confirmSave();
                });
            },
            confirmSave() {
                this.loading = true;
                const action = this.formItem.id > 0 ? 'BizAction.updateCompanyVersionRepository' : 'BizAction.addCompanyVersionRepository';
                app.invoke(action, [app.token, {...this.formItem}], () => {
                    if (this.formItem.id > 0) {
                        app.toast('更新成功');
                    } else {
                        app.toast('添加成功');
                    }
                    app.postMessage('version.repository.edit');
                    this.loading = false;
                    this.showModal = false;
                }, (code, message) => {
                    if (this.formItem.id > 0) {
                        app.toast(`更新失败，${message || ''}`);
                    } else {
                        app.toast(`添加失败，${message || ''}`);
                    }
                    this.loading = false;
                });
            },
            twiceFactorValid(callback) {
                let _this = this;
                _this.$Modal.confirm({
                    render: h => {
                        return h("Input", {
                            props: {
                                value: _this.repeatRepositoryName,
                                autoFocus: true,
                                placeholder: "请输入系统名称以进行确认删除"
                            },
                            on: {
                                input: val => {
                                    _this.repeatRepositoryName = val;
                                }
                            }
                        });
                    },
                    onOk: () => {
                        if (_this.repeatRepositoryName) {
                            if (_this.repeatRepositoryName !== this.originReposiotryName) {
                                app.toast("输入系统名称不匹配");
                                _this.repeatRepositoryName = null;
                            } else {
                                if (callback) {
                                    callback.apply();
                                }
                            }
                        }else{
                            app.toast("请输入系统名称");
                            return false;
                        }
                    },
                    onCancel: () => {
                        _this.repeatRepositoryName = null;
                    }
                })
            }
        },
    };
</script>

<style lang="less" scoped>
    .modal-edit {
        /deep/ .ivu-modal-body {
            padding: 16px 40px;
            .ivu-form-item {
                margin-bottom: 22px!important;
            }
            .ivu-form-label-top {
                .ivu-form-item-label {
                    padding-top: 8px;
                    padding-bottom: 5px;
                }
            }

        }
    }
</style>
