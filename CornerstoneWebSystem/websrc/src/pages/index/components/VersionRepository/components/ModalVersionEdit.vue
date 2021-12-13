<template>
    <Modal
        v-model="showModal" :mask-closable="false" class="modal-edit" width="700">
        <div slot="header">
            {{ formItem.id > 0 ? '编辑' :'新增' }}版本
        </div>
        <div class="form">
            <Form
                ref="form" :model="formItem" :rules="formRule">
                <Row :gutter="20">
                    <Col :span="24">
                        <FormItem
                            label="版本名称" prop="name">
                            <Input
                                v-model="formItem.name" :maxlength="20" show-word-limit placeholder="请输入版本名称" />
                        </FormItem>
                    </Col>
                    <Col :span="12">
                        <FormItem
                            label="版本开始时间" prop="startTime">
                            <DatePicker
                                type="datetime"
                                :options="startTimeOptions"
                                confirm
                                transfer
                                style="width: 100%"
                                v-model="formItem.startTime"
                                placeholder="请选择版本开始时间" />
                        </FormItem>
                    </Col>
                    <Col :span="12">
                        <FormItem
                            label="版本发布时间" prop="endTime">
                            <DatePicker
                                type="datetime"
                                :options="endTimeOptions"
                                confirm
                                transfer
                                style="width: 100%"
                                v-model="formItem.endTime"
                                placeholder="请选择版本发布时间" />
                        </FormItem>
                    </Col>
                    <Col :span="24">
                        <FormItem
                            label="备注" prop="remark"></FormItem>
                    </Col>
                    <Col :span="24">
                        <RichtextEditor
                            v-model="formItem.remark" placeholder="请输入版本备注" />
                    </Col>
                </Row>
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
        name: 'ModalVersionEdit',
        mixins: [VueMixin],
        data() {
            return {
                loading: false,
                showModal: false,
                formItem: {
                    id: 0,
                    repositoryId: 0,
                    status: 1,
                    name: null,
                    remark: null,
                    startTime: null,
                    endTime: null,
                },
                formRule: {
                    name: [
                        vd.req, vd.name2_20,
                    ],
                    remark: [
                        vd.req,
                    ],
                },
                startTimeOptions: {
                    disabledDate: (date) => {
                        if (Date.isDate(this.formItem.endTime)) {
                            return date.getTime() > this.formItem.endTime.getTime();
                        }
                        return false;
                    },
                },
                endTimeOptions: {
                    disabledDate: (date) => {
                        if (Date.isDate(this.formItem.startTime)) {
                            return date.getTime() < this.formItem.startTime.getTime();
                        }
                        return false;
                    },
                },
            };
        },
        methods: {
            show(repositoryId, versionId) {
                this.$refs.form.resetFields();
                this.formItem = {
                    id: 0,
                    repositoryId: 0,
                    status: 1,
                    name: null,
                    remark: null,
                };
                this.formItem.repositoryId = repositoryId;
                this.showModal = true;
            },
            loadData(versionId) {
                this.loading = true;
                this.request('BizAction.getCompanyVersionById', [versionId], (res) => {
                    if (Object.isEmpty(res)) {
                        return;
                    }
                    this.formItem = {
                        ...this.formItem,
                        ...res,
                    };
                    this.loading = false;
                }, (code, message) => {
                    app.toast(message || '加载版本信息失败');
                    this.loading = false;
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
                const action = this.formItem.id > 0 ? 'BizAction.updateCompanyVersion' : 'BizAction.addCompanyVersion';
                this.request(action, [{ ...this.formItem }], () => {
                    if (this.formItem.id > 0) {
                        app.toast('更新成功');
                    } else {
                        app.toast('添加成功');
                    }
                    app.postMessage('version.item.edit');
                    this.showModal = false;
                    this.loading = false;
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
