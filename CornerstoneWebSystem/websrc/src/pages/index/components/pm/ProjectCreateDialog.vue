<style scoped>
    .project-type-card {
        border: 1px solid #dcdee2;
        width: 210px;
        height: 100px;
        margin-top: 15px;
        margin-right: 15px;
        display: inline-block;
        position: relative;
        overflow: hidden;
        padding: 10px;
        background-color: #fff;
    }

    .project-type-box {
        text-align: left;
        display: flex;
        flex-wrap: wrap;
    }

    .project-card-select .check {
        visibility: visible;
    }

    .check {
        visibility: hidden;
        position: absolute;
        top: 30px;
        left: 80px;
        font-size: 50px;
        color: #0097f7;
    }

    .project-type-name {
        color: #333;
        font-weight: bold;
        padding-top: 10px;
        font-size: 16px;
        text-align: center;
    }

    .project-type-desc {
        color: #666;
        font-size: 12px;
        text-align: center;
        line-height: 1.2;
        padding-bottom: 10px;
    }

</style>
<i18n>
    {
    "en": {
    "创建项目": "Create Project",
    "模板":"Template",
    "名称":"Name",
    "开始日期":"Start date",
    "截止日期":"Due date",
    "项目名称":"Project name",
    "项目描述":"Remark",
    "项目类型":"Project description",
    "继续创建下一个":"Continue to create next",
    "创建":"Create",
    "请选择项目类型":"Please choose project type"
    },
    "zh_CN": {
    "创建项目": "创建项目",
    "模板":"模板",
    "名称":"名称",
    "开始日期":"开始日期",
    "截止日期":"截止日期",
    "项目名称":"项目名称",
    "项目描述":"项目描述",
    "项目类型":"项目类型",
    "继续创建下一个":"继续创建下一个",
    "创建":"创建",
    "请选择项目类型":"请选择项目类型"
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
        :title="(args.isTemplate?'【'+$t('模板')+'】':'')+$t('创建项目')"
        width="750">

        <Form
            @submit.native.prevent
            ref="form"
            :rules="formRule"
            :model="formItem"
            label-position="top"
            style="height:600px;padding:15px">
            <FormItem :label="$t('名称')" prop="name">
                <Input v-model.trim="formItem.name" :placeholder="$t('项目名称')" />
            </FormItem>
            <FormItem :label="$t('项目描述')" prop="description">
                <Input v-model.trim="formItem.description" type="textarea" :rows="3"></Input>
            </FormItem>
            <FormItem v-if="args.isTemplate||projectId==null" :label="$t('项目类型')">
                <div class="project-type-box">
                    <div
                        @click="selectedTemplate=item.id"
                        v-for="item in templateList"
                        :key="'item_'+item.id"
                        class="project-type-card"
                        :class="{'project-card-select':item.id==selectedTemplate}">
                        <div class="project-type-name">{{item.name}}</div>
                        <div class="project-type-desc">{{item.description}}</div>

                        <Icon type="md-checkmark" class="check" />
                    </div>
                </div>

            </FormItem>
        </Form>


        <div slot="footer">
            <Row>
                <Col span="12" style="text-align:left;padding-top:5px">
                    <Checkbox size="large" v-model="continueCreate">{{$t('继续创建下一个')}}</Checkbox>
                </Col>
                <Col span="12" style="text-align:right">
                    <Button @click="confirm" type="default" size="large">{{$t('创建')}}</Button>
                </Col>
            </Row>

        </div>

    </Modal>
</template>


<script>
    export default {
        mixins: [componentMixin],
        data() {
            return {
                continueCreate: false,
                formItem: {
                    name: null,
                    startDate: null,
                    endDate: null,
                    description: null,

                },
                formRule: {
                    name: [vd.req, vd.name],
                    description: [vd.desc],
                },
                selectedTemplate: null,
                projectId: null,
                templateList: [],
                startDateOptions: {
                    disabledDate: (date) => {
                        if (!date || !this.formItem.endDate) {
                            return false;
                        }
                        return date.getTime() >= this.formItem.endDate;
                    },
                },
                endDateOptions: {
                    disabledDate: (date) => {
                        if (!date || !this.formItem.startDate) {
                            return false;
                        }
                        return date.getTime() <= this.formItem.startDate;
                    },
                },
            };
        },
        methods: {
            pageLoad() {
                this.projectId = this.args.projectId;
                this.selectedTemplate = this.args.projectId;
                if (this.args.isTemplate) {
                    this.formItem.isTemplate = this.args.isTemplate;
                } else {
                    this.loadTemplate();
                }
            },

            loadTemplate() {
                app.invoke('BizAction.getTemplateProjectInfoList', [app.token], (list) => {
                    if (!Array.isArray(list)) {
                        return;
                    }
                    this.templateList = list.filter(item =>item.isDelete==false&& item.uuid !== process.env.VUE_APP_PROJECT_SET_TEMPLATE_UUID);
                });
            },
            confirm() {
                this.$refs.form.validate((r) => {
                    if (r) {
                        this.confirmForm();
                    }
                });
            },
            confirmForm() {
                if (this.selectedTemplate == null) {
                    app.toast(this.$t('请选择项目类型'));
                    return;
                }
                app.invoke('BizAction.createProject', [
                    app.token,
                    this.formItem,
                    this.selectedTemplate], (uuid) => {
                    if (!this.continueCreate) {
                        this.showDialog = false;
                        app.postMessage('project.edit');
                        if (this.args.isTemplate == null) {
                            app.loadPage('/pm/project/' + uuid + '/project');
                        }
                    } else {
                        this.$refs.form.resetFields();
                    }
                });
            },
        },
    };
</script>
