<style scoped>
    .desc-info {
        font-size: 12px;
        color: #999;
    }

    .value-row {
        padding-top: 7px;
        padding-bottom: 7px;
        cursor: move;
    }

    .value-row-not-move {
        padding-top: 7px;
        padding-bottom: 7px;
    }

    .value-row:hover {
        background-color: #ebf7ff;
    }

    .field-checkbox {
        min-width: 100px;
    }
</style>
<i18n>
    {
    "en": {
    "对象类型": "Object type",
    "勾选需要显示的字段":"the fields you want to display",
    "系统字段":"System Fields",
    "自定义字段":"Custom Fields",
    "保存":"Save",
    "拖动调整显示顺序":"Drag to reorder",
    "暂无数据":"No Data",
    "必选":"Required",
    "保存成功":"Success"

    },
    "zh_CN": {
    "对象类型": "对象类型",
    "勾选需要显示的字段":"勾选需要显示的字段",
    "系统字段":"系统字段",
    "自定义字段":"自定义字段",
    "保存":"保存",
    "拖动调整显示顺序":"拖动调整显示顺序",
    "暂无数据":"暂无数据",
    "必选":"必显",
    "保存成功":"保存成功"
    }
    }
</i18n>
<template>
    <div>
        <Row>
            <Col span="16">
                <Form label-position="top">
                    <FormItem :label="$t('对象类型')">
                        <div>
                            <RadioGroup v-model="objectType">
                                <Radio
                                    v-for="item in moduleList"
                                    v-if="item.objectType>0"
                                    :key="item.objectType"
                                    :label="item.objectType">{{item.name}}
                                </Radio>
                            </RadioGroup>
                        </div>
                        <div class="desc-info">{{$t('勾选需要显示的字段')}}</div>
                    </FormItem>


                    <FormItem :label="$t('系统字段')">
                        <Checkbox
                            :disabled="item.isRequiredShow||disabled"
                            v-model="item.isShow"
                            class="field-checkbox"
                            v-if="item.isSystemField"
                            v-for="item in fieldList"
                            :key="item.id"
                            :label="item.id">{{item.name}}
                        </Checkbox>

                    </FormItem>

                    <FormItem :label="$t('自定义字段')">
                        <Checkbox
                                :disabled="disabled"
                            class="field-checkbox"
                            v-model="item.isShow"
                            v-if="item.isSystemField==false"
                            v-for="item in fieldList"
                            :key="item.id"
                            :label="item.id">{{item.name}}
                        </Checkbox>
                        <div class="table-nodata" v-if="customFieldCount==0">
                            {{$t('暂无数据')}}
                        </div>
                    </FormItem>
                    <FormItem>
                        <Button v-if="!disabled" @click="confirm" type="default">{{$t('保存')}}</Button>
                    </FormItem>
                </Form>

            </Col>
            <Col span="8" v-if="!disabled">
                <div class="desc-info">{{$t('拖动调整显示顺序')}}</div>
                <div style="margin-top:10px">
<!--                    <div-->
<!--                        class="value-row-not-move"-->
<!--                        v-for="item in fieldList"-->
<!--                        :key="item.id"-->
<!--                        v-if="item.isShow&&item.isRequiredShow">-->
<!--                        <span style="color:#999;padding-right:10px">{{$t('必选')}}</span>{{item.name}}-->
<!--                    </div>-->
                    <draggable v-model="fieldList" :options="{draggable:'.value-row'}">
                        <div
                            class="value-row"
                            v-for="item in fieldList"
                            :key="item.id"
                            v-if="item.isShow">
                            <Icon type="md-more" />
                            <span style="color:#999;padding-right:10px" v-if="item.isRequiredShow">{{$t('必选')}}</span>
                            {{item.name}}
                        </div>
                    </draggable>
                </div>
            </Col>
        </Row>


    </div>
</template>

<script>
    import draggable from 'vuedraggable';

    export default {
        mixins: [componentMixin],
        props: ['project'],
        components: {
            draggable,
        },
        mounted() {
            this.loadModuleList();
            this.disabled = this.project.isFinish;
        },
        data() {
            return {
                objectType: null,
                fieldList: [],
                moduleList: [],
                customFieldCount: 0,
                disabled:false
            };
        },
        watch: {
            'objectType': function (val) {
                this.loadData();
            },
        },
        methods: {
            loadData() {
                app.invoke('BizAction.getProjectFieldDefineInfoList', [app.token, this.project.id, this.objectType], (list) => {
                    this.fieldList = list;
                    let t = 0;
                    for (let i = 0; i < list.length; i++) {
                        const f = list[i];
                        if (f.isSystemField === false) {
                            t++;
                        }
                    }
                    this.customFieldCount = t;
                });
            },
            loadModuleList() {
                app.invoke('BizAction.getProjectModuleInfoList', [app.token, this.project.id], (list) => {
                    this.moduleList = list;
                    for (var i = 0; i < this.moduleList.length; i++) {
                        var t = this.moduleList[i];
                        if (t.objectType > 0) {
                            this.objectType = t.objectType;
                            break;
                        }
                    }
                });
            },
            confirm() {
                this.confirmForm();
            },
            confirmForm() {
                app.invoke('BizAction.saveProjectFieldDefineList', [app.token, this.fieldList], (info) => {
                    app.toast(this.$t('保存成功'));
                });
            },
        },
    };
</script>
