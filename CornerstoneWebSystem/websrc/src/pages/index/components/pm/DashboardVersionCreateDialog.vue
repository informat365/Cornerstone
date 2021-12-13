<template>
    <Modal
        v-model="showModal" :mask-closable="false" class="modal-edit" width="700">
        <div slot="header">
            创建系统版本
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
                            label="版本号" prop="versionNo">
                            <Input
                                v-model="formItem.versionNo" :maxlength="20" show-word-limit placeholder="请输入版本号" />
                        </FormItem>
                    </Col>
                    <Col :span="12">
                        <FormItem
                            label="版本负责人" prop="ownerAccountIdList">
                            <CompanyUserSelect
                                v-model="formItem.ownerAccountIdList"
                                clearable
                                multiple
                                placeholder="版本负责人"
                                style="width: 300px;"
                            />
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
                type="primary" :loading="loading" @click.native="onConfirm">创建
            </Button>
        </div>
    </Modal>
</template>

<script>
    export default {
        name: 'DashboardVersionCreateDialog',
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
                    ownerAccountId: [
                        vd.req,
                    ],
                    versionNo: [
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
            pageLoad(){
                this.formItem.repositoryId = this.args.repositoryId;
                this.showModal = true;
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
                app.invoke( 'BizAction.addCompanyVersion', [app.token,{ ...this.formItem }], () => {
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
