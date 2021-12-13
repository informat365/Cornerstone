<style scoped>
    .desc-info {
        font-size: 12px;
        color: #999;
    }

    .check {
        color: #0097f7;
    }

    .tag-color {
        display: inline-block;
        width: 30px;
        height: 30px;
        border-radius: 50%;
        margin-right: 10px;
        color: #fff;
        overflow: hidden;
    }
</style>
<i18n>
    {
    "en": {
    "分类设置": "Category Setting",
    "全局分类设置": "Global Category Setting",
    "名称":"Name",
    "备注":"Remark",
    "颜色":"Color",
    "继续创建下一个":"Continue to create next",
    "创建":"Create",
    "保存":"Save"
    },
    "zh_CN": {
    "分类设置": "分类设置",
    "全局分类设置": "全局分类设置",
    "名称":"名称",
    "备注":"备注",
    "颜色":"颜色",
    "继续创建下一个":"继续创建下一个",
    "创建":"创建",
    "保存":"保存"
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
        :title="formItem.objectType > 0 ? $t('分类设置') :$t('全局分类设置')"
        width="500"
        @on-ok="confirm">

        <Form ref="form" :rules="formRule" :model="formItem" label-position="top" style="height:330px;padding:25px">
            <FormItem :label="$t('名称')" prop="name">
                <Input autofocus @on-enter="confirm" v-model.trim="formItem.name" placeholder=""></Input>
            </FormItem>
            <FormItem prop="remark" :label="$t('备注')">
                <Input v-model.trim="formItem.remark" placeholder=""></Input>
            </FormItem>
            <FormItem :label="$t('颜色')">
                <div style="height:100px;text-align:center">
                    <span
                        @click="formItem.color=item"
                        v-for="item in colorArray"
                        :key="item"
                        class="tag-color"
                        :style="{backgroundColor:item}">
                        <Icon size="20" v-if="item===formItem.color" type="md-checkmark" />
                    </span>
                </div>
            </FormItem>

        </Form>

        <div slot="footer">
            <Row>
                <Col span="12" style="text-align:left;padding-top:5px">
                    <Checkbox v-model="continueCreate" size="large">{{$t('继续创建下一个')}}</Checkbox>
                </Col>
                <Col span="12" style="text-align:right">
                    <Button @click="confirm" type="default" size="large">{{formItem.id==0?$t('创建'):$t('保存')}}</Button>
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
                    id: 0,
                    name: null,
                    remark: null,
                    color: '#2e94b9',
                },
                formRule: {
                    name: [vd.req, vd.name2_30],
                    remark: [vd.desc],
                    color: [vd.req],
                },
                colorArray: [
                    '#2e94b9',
                    '#f0b775',
                    '#d25565',
                    '#f54ea2',
                    '#42218e',
                    '#5be7c4',
                    '#525564',
                ],
            };
        },
        methods: {
            pageLoad() {
                if (this.args.item) {
                    this.formItem = copyObject(this.args.item);
                } else {
                    this.formItem.objectType = this.args.objectType;
                    this.formItem.projectId = this.args.projectId;
                    this.formItem.parentId = this.args.parentId;
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
                var action = this.formItem.id == 0 ? 'BizAction.createCategory' : 'BizAction.updateCategory';
                app.invoke(action, [app.token, this.formItem], (info) => {
                    app.postMessage('category.edit');
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
