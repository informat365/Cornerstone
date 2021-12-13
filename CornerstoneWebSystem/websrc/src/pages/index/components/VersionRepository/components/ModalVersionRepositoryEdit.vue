<template>
    <Modal
        v-model="showModal" :mask-closable="false" class="modal-edit" width="500">
        <div slot="header">
            {{ formItem.id > 0 ? '编辑' :'新增' }}版本库
        </div>
        <div class="form">
            <Form
                ref="form" :model="formItem" :rules="formRule">
                <FormItem
                    label="版本库名称(20字以内)" prop="name">
                    <Input
                        v-model="formItem.name" :maxlength="20" show-word-limit placeholder="请输入版本库名称" />
                </FormItem>
                <FormItem
                    label="简介(200字以内)" prop="description">
                    <Input
                        v-model="formItem.description"
                        placeholder="请输入版本库简介"
                        type="textarea"
                        :rows="5"
                        :maxlength="200"
                        style="width: 100%" />
                </FormItem>
                <FormItem v-if="formItem.id>0 && perm('version_repository_delete')" >
                    <Alert type="error">
                        删除版本库后,会关联删除版本库版本数据，数据库不可恢复
                    </Alert>
                    <Button @click="onClickDelete" type="error">删除此版本库</Button>
                </FormItem>
            </Form>
        </div>
        <div slot="footer">
            <Button
                type="text" :loading="loading" @click.native="showModal=false">取消
            </Button>
            <Button
                type="primary" :loading="loading" @click.native="onConfirm">提交修改
            </Button>
        </div>
    </Modal>
</template>

<script>
    import VueMixin from '../vue.mixin';

    export default {
        name: 'ModalVersionRepositoryEdit',
        mixins: [VueMixin, componentMixin],
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
                        vd.req, vd.desc,
                    ],
                },
            };
        },
        methods: {
            show(repositoryId) {
                this.$refs.form.resetFields();
                this.formItem = {
                    id: 0,
                    name: null,
                    description: null,
                };
                this.showModal = true;
                if (Number.check(repositoryId, 1)) {
                    this.loadData(repositoryId);
                }
            },
            loadData(repositoryId) {
                this.loading = true;
                this.request('BizAction.getCompanyVersionRepositoryById', [repositoryId], (res) => {
                    if (Object.isEmpty(res)) {
                        return;
                    }
                    this.formItem = {
                        ...this.formItem,
                        ...res,
                    };
                    this.loading = false;
                }, (code, message) => {
                    app.toast(message || '加载版本库信息失败');
                    this.loading = false;
                });
            },
            onClickDelete() {
                app.confirm('确定要删除此版本库吗？删除后不可恢复.', () => {
                    this.loading = true;
                    this.request('BizAction.deleteCompanyVersionRepository', [this.formItem.id], () => {
                        app.toast('删除版本库成功');
                        app.postMessage('version.repository.edit');
                        this.showModal = false;
                        this.loading = false;
                    }, (code, message) => {
                        app.toast(message || '删除版本库失败');
                        this.loading = false;
                    });
                });
            },
            onConfirm() {
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
                this.request(action, [{ ...this.formItem }], () => {
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
                        app.toast(`更新失败，${ message || '' }`);
                    } else {
                        app.toast(`添加失败，${ message || '' }`);
                    }
                    this.loading = false;
                });
            },
        },
    };
</script>

<style lang="less" scoped>
    .modal-edit {
        /deep/ .ivu-modal-body {
            padding: 16px 40px;
        }
    }
</style>
