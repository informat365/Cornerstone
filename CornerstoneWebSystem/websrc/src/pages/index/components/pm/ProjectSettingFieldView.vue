<style scoped>
    .desc-info {
        font-size: 12px;
        color: #999;
    }

    .field-required {
        color: tomato;
    }
</style>
<i18n>
    {
    "en": {
    "对象类型": "Object Type",
    "可设定系统字段内容必填/选填，扩展系统功能":"Set system field content required / optional, to extend system functions",
    "可自定义多种类型的字段，扩展系统功能":"Multiple types of fields can be customized to extend system functions",
    "必填":"Required",
    "选填":"Optional",
    "设置字段":"Setting",
    "暂无数据":"No Data",
    "添加字段":"Add",
    "系统字段":"System Fields",
    "自定义字段":"Custom Fields",
    "保存成功":"Success"
    },
    "zh_CN": {
    "对象类型": "对象类型",
    "可设定系统字段内容必填/选填，扩展系统功能":"可设定系统字段内容必填，扩展系统功能",
    "可自定义多种类型的字段，扩展系统功能":"可自定义多种类型的字段，扩展系统功能",
    "必填":"必填",
    "选填":"选填",
    "设置字段":"设置字段",
    "暂无数据":"暂无数据",
    "添加字段":"添加字段",
    "系统字段":"系统字段",
    "自定义字段":"自定义字段",
    "保存成功":"保存成功"
    }
    }
</i18n>
<template>
    <div>
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
            </FormItem>
            <FormItem :label="$t('自定义字段')">
                <div class="desc-info">{{$t('可自定义多种类型的字段，扩展系统功能')}}</div>
                <table class="table-content table-content-small" style="margin-top:10px;border-top:1px solid #eee">
                    <tbody>
                    <template v-for="item in fieldList">
                        <tr v-if="item.isSystemField===false" :key="item.id" class="table-row table-row-small">
                            <td style="width:150px" @click="showFieldEditDialog(item)">
                                <div class=" table-col-name" :title="item.name">{{item.name}}</div>
                            </td>
                            <td style="width:100px">
                                {{item.type|dataDict('ProjectFieldDefine.type')}}
                            </td>
                            <td>
                                <div style="width:200px;font-size:12px;color:#666" class="text-no-wrap">
                                    <span v-if="item.type==3||item.type==4">{{item.valueRange}}</span>
                                </div>
                            </td>
                            <td style="width:100px;text-align:right">
                                <span class="table-row-hover-hide" :class="{'field-required':item.isRequired}">
                                    {{item.isRequired?$t("必填"):$t("选填")}}
                                </span>
                                <span class="table-row-opt">
                                    <IconButton
                                        @click="showFieldEditDialog(item)"
                                        :tips="$t('设置字段')"
                                        icon="ios-settings-outline"></IconButton>
                                </span>
                            </td>
                        </tr>
                    </template>
                    </tbody>
                </table>
                <div class="table-nodata" v-if="customFieldCount==0">
                    {{$t('暂无数据')}}
                </div>
                <div>
                    <IconButton v-if="!disabled" @click="showFieldEditDialog()" icon="ios-add" :title="$t('添加字段')"></IconButton>
                </div>
            </FormItem>
            <FormItem :label="$t('系统字段')">
                <div class="desc-info">{{$t('可设定系统字段内容必填/选填，扩展系统功能')}}</div>
                <table class="table-content table-content-small" style="margin-top:10px;border-top:1px solid #eee">
                    <tbody>
                    <template v-for="item in fieldList">
                        <tr v-if="item.isSystemField===true" :key="item.id" class="table-row table-row-small">
                            <td style="width:150px" @click="showFieldEditDialog(item)">
                                <div class="text-no-wrap table-col-name">{{item.name}}</div>
                            </td>
                            <td style="width:100px">
                                {{ item.type | dataDict('ProjectFieldDefine.type') }}
                            </td>
                            <td>
                                <div style="width:200px;font-size:12px;color:#666" class="text-no-wrap">
                                    <span v-if="item.type===3||item.type===4">{{item.valueRange}}</span>
                                </div>
                            </td>
                            <td style="width:200px;text-align:right">
                                <i-switch
                                    :disabled="disabled"
                                    v-model="item.isRequired"
                                    :loading="item.loading"
                                    size="large"
                                    @on-change="(value) => {onFieldRequireChange(value,item)}">
                                    <span slot="open">{{ $t("必填") }}</span>
                                    <span slot="close">{{ $t("选填") }}</span>
                                </i-switch>
                            </td>
                        </tr>
                    </template>
                    </tbody>
                </table>
                <div class="table-nodata" v-if="customFieldCount==0">
                    {{$t('暂无数据')}}
                </div>
            </FormItem>
        </Form>

    </div>
</template>

<script>
    export default {
        mixins: [componentMixin],
        props: ['project'],
        data() {
            return {
                objectType: null,
                fieldList: [],
                customFieldCount: 0,
                moduleList: [],
                disabled:false
            };
        },
        mounted() {
            this.loadModuleList();
            this.disabled = this.project.isFinish;
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
                    this.customFieldCount = 0;
                    for (var i = 0; i < list.length; i++) {
                        if (list[i].isSystemField == false) {
                            this.customFieldCount++;
                        }
                    }
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
            showFieldEditDialog(item) {
                app.showDialog(ProjectSettingFieldEditDialog, {
                    item: item,
                    project: this.project,
                    objectType: this.objectType,
                    callback: this.loadData,
                });
            },
            onFieldRequireChange(val, item) {
                this.$set(item, 'loading', true);
                app.invoke('BizAction.setProjectFieldDefineIsRequired', [app.token, item.id, val], () => {
                    this.$set(item, 'loading', false);
                    app.toast(this.$t('保存成功'));
                }, () => {
                    item.isRequired = !val;
                    this.$set(item, 'loading', false);
                });
            },
        },
    };
</script>
