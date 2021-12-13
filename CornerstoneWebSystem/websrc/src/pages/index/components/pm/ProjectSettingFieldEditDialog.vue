<style scoped>
    .value-row {
        padding-top: 7px;
        padding-bottom: 7px;
        cursor: move;
    }

    .value-row:hover {
        background-color: #ebf7ff;
    }

    .value-row:hover .move-icon {
        visibility: visible;
    }

    .move-icon {
        visibility: hidden;
        width: 30px;
        height: 20px;
    }

    .desc-info {
        font-size: 12px;
        color: #999;
        padding-left: 10px;
    }
</style>
<i18n>
    {
    "en": {
    "自定义字段": "Custom Field",
    "系统字段": "System Field",
    "名称":"Name",
    "备注":"Remark",
    "必填":"Required",
    "唯一":"Unique",
    "类型":"Type",
    "取值范围":"Range",
    "删除":"Delete",
    "增加值":"Add",
    "拖动可调整顺序":"Drag to reorder",
    "删除此字段":"Delete",
    "继续创建下一个":"Continue to create next",
    "创建":"Create",
    "保存":"Save",
    "确定要删除字段":"Are you sure you want to delete field {0}? After deletion, the input data will be deleted at the same time. Please operate with caution!",
    "请输入可选值":"Please input option value",
    "显示时间":"Show time field"
    },
    "zh_CN": {
    "自定义字段": "自定义字段",
    "系统字段": "系统字段",
    "名称":"名称",
    "备注":"备注",
    "必填":"必填",
    "唯一":"不可重复",
    "类型":"类型",
    "取值范围":"取值范围",
    "删除":"删除",
    "增加值":"增加值",
    "拖动可调整顺序":"拖动可调整顺序",
    "删除此字段":"删除此字段",
    "继续创建下一个":"继续创建下一个",
    "创建":"创建",
    "保存":"保存",
    "确定要删除字段":"确定要删除字段{0}吗？删除后录入的数据也会一并删除，请谨慎操作!",
    "请输入可选值":"请输入可选值",
    "显示时间":"显示时间"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="formItem.isSystemField?$t('系统字段'):$t('自定义字段')" width="700" @on-ok="confirm">
        <Form
            @submit.native.prevent
            ref="form"
            :rules="formRule"
            :model="formItem"
            label-position="top"
            style="height:500px;padding:15px">
            <Row>
                <Col span="11">
                    <FormItem :label="$t('名称')" prop="name">
                        <Input :disabled="disabled" v-model.trim="formItem.name" placeholder=""></Input>
                    </FormItem>
                </Col>
                <Col offset="2" span="11">
                    <FormItem :label="$t('备注')" prop="remark">
                        <Input :disabled="disabled" v-model.trim="formItem.remark" placeholder=""></Input>
                    </FormItem>
                </Col>
            </Row>
            <Row>
                <Col span="11">
                    <FormItem :label="$t('必填')">
                        <i-Switch :disabled="disabled" v-model="formItem.isRequired"></i-Switch>
                    </FormItem>
                </Col>
                <Col span="11" offset="2">
                    <FormItem :label="$t('唯一')">
                        <i-Switch :disabled="disabled" v-model="formItem.isUnique"></i-Switch>
                    </FormItem>
                </Col>
            </Row>

            <FormItem :label="$t('类型')">
                <DataDictRadio :disabled="formItem.id>0" v-model="formItem.type" type="ProjectFieldDefine.type"/>
            </FormItem>

            <FormItem v-if="formItem.type==7" :label="$t('显示时间')">
                <i-Switch :disabled="disabled" v-model="formItem.showTimeField"></i-Switch>
            </FormItem>

            <FormItem v-if="formItem.type==2||formItem.type==3||formItem.type==4" :label="$t('取值范围')">
                <div>
                    <draggable v-model="valueRange" :options="{draggable:'.value-row'}">
                        <Row class="value-row" v-for="(item,itemIdx) in valueRange" :key="'op'+itemIdx">
                            <Col span="1">
                                <Icon type="md-more" :size="20" style="color:#999"/>
                            </Col>
                            <Col span="12">

                                <Input v-model.trim="item.value"></Input>
                            </Col>
                            <Col span="11">
                                <IconButton icon="ios-trash" v-if="!disabled" @click="removeValue(item)"
                                            :tips="$t('删除')"></IconButton>
                            </Col>
                        </Row>
                    </draggable>
                </div>
                <div>
                    <IconButton v-if="!disabled" @click="addValueRange" icon="ios-add" :title="$t('增加值')"></IconButton>
                    <span class="desc-info vcenter">{{$t('拖动可调整顺序')}}</span>
                </div>
            </FormItem>

            <FormItem v-if="formItem.id>0&&!disabled" label="">
                <Button @click="deleteField" type="error">{{$t('删除此字段')}}</Button>
            </FormItem>

        </Form>

        <div slot="footer">
            <Row>
                <Col span="12" style="text-align:left;padding-top:5px">
                    <Checkbox v-if="formItem.id==0&&!disabled" v-model="continueCreate" size="large">{{$t('继续创建下一个')}}
                    </Checkbox> &nbsp;
                </Col>
                <Col span="12" style="text-align:right">
                    <Button v-if="!disabled" @click="confirm" type="default" size="large">
                        {{formItem.id==0?$t("创建"):$t("保存")}}
                    </Button>
                </Col>
            </Row>
        </div>

    </Modal>
</template>


<script>
    import draggable from 'vuedraggable'

    export default {
        components: {
            draggable,
        },
        mixins: [componentMixin],
        data() {
            return {
                continueCreate: false,
                formItem: {
                    id: 0,
                    name: null,
                    remark: null,
                    isRequired: false,
                    type: 1,
                    showTimeField: false
                },
                valueRange: [],
                formRule: {
                    name: [vd.req, vd.name2_30],
                    remark: [vd.desc],
                },

            }
        },
        watch: {
            "formItem.type": function (val) {
                if (val == 2 || val == 3 || val == 4) {
                    if (this.valueRange == null || this.valueRange.length == 0) {
                        this.valueRange = [
                            {value: ""},
                            {value: ""},
                            {value: ""},
                        ]
                    }
                }
            }
        },
        computed: {
            disabled: function () {
                return this.args.project && this.args.project.isFinish;
            }
        },
        methods: {
            pageLoad() {
                if (this.args.item) {
                    this.formItem = this.args.item;
                    if (this.formItem.valueRange) {
                        this.formItem.valueRange.map((item) => {
                            this.valueRange.push({value: item})
                        })
                    }
                } else {
                    this.formItem.objectType = this.args.objectType;
                    this.formItem.projectId = this.args.project.id;
                }
            },
            removeValue(item) {
                for (var i = 0; i < this.valueRange.length; i++) {
                    var t = this.valueRange[i];
                    if (t == item) {
                        this.valueRange.splice(i, 1);
                        return;
                    }
                }
            },
            addValueRange() {
                this.valueRange.push({value: ""});
            },
            deleteField() {
                app.confirm(this.$t('确定要删除字段', [this.formItem.name]), () => {
                    app.invoke('BizAction.deleteProjectFieldDefine', [app.token, this.formItem.id], (info) => {
                        this.args.callback();
                        this.showDialog = false;
                    })
                })
            },
            confirm() {
                this.$refs.form.validate((r) => {
                    if (r) {
                        this.confirmForm();
                    }
                });
            },
            confirmForm() {
                //
                var type = this.formItem.type;
                if (type == 2 || type == 3 || type == 4) {
                    var validValue = 0;
                    for (var i = 0; i < this.valueRange.length; i++) {
                        var t = (this.valueRange[i])
                        if (t != '') {
                            validValue++;
                        }
                    }
                    if (validValue == 0) {
                        app.toast(this.$t('请输入可选值'))
                        return;
                    }
                }
                //
                this.formItem.valueRange = [];
                this.valueRange.map((item) => {
                    var t = item.value;
                    if (t != "") {
                        this.formItem.valueRange.push(item.value)
                    }
                })
                var action = this.formItem.id == 0 ? 'BizAction.addProjectFieldDefine' : "BizAction.updateProjectFieldDefine";
                app.invoke(action, [app.token, this.formItem], (info) => {
                    this.args.callback();
                    if (!this.continueCreate) {
                        this.showDialog = false;
                    } else {
                        this.$refs.form.resetFields();
                    }
                })
            }
        }
    }
</script>
