<style scoped>
    .desc-info {
        font-size: 12px;
        color: #666;
    }

    .value-row {
        padding-top: 7px;
        padding-bottom: 7px;
        cursor: move;
    }

    .value-row:hover {
        background-color: #ebf7ff;
    }

    .gray {
        color: #999;
    }
</style>


<i18n>
    {
    "en": {
    "对象类型":"Object type",
    "名称":"Name",
    "分组":"Group",
    "备注":"Remark",
    "系统字段设置":"System field settings",
    "拖动调整显示顺序":"Drag to change display order",
    "固定字段":"Fixed field",
    "保存成功":"Save success",
    "保存":"Save",
    "确定要删除此对象类型吗？":"Confirm to delete?",
    "删除":"Delete",
    "全选":"Choose all",
    "字段名称":"Field name",
    "是否设为固定":"Whether  set it as fixed field",
    "是否使用字段":"Whether  use it"
    },
    "zh_CN": {
    "对象类型":"对象类型",
    "名称":"名称",
    "分组":"分组",
    "备注":"备注",
    "系统字段设置":"系统字段设置",
    "拖动调整显示顺序":"拖动调整显示顺序",
    "固定字段":"固定字段",
    "保存成功":"保存成功",
    "保存":"保存",
    "确定要删除此对象类型吗？":"确定要删除此对象类型吗？",
    "删除":"删除",
    "全选":"全选",
    "字段名称":"字段名称",
    "是否设为固定":"是否设为固定字段",
    "是否使用字段":"是否使用字段"
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
        :title="$t('对象类型')"
        width="700">
        <Form
            :rules="formRule"
            :model="formItem"
            ref="form"
            label-position="top"
            style="height:500px;padding:15px">
            <Row :gutter="24">
                <Col span="6">
                    <FormItem :label="$t('名称')" prop="name">
                        <Input v-model.trim="formItem.name"></Input>
                    </FormItem>
                </Col>
                <Col span="6">
                    <FormItem :label="$t('分组')" prop="group">
                        <Input v-model.trim="formItem.group"></Input>
                    </FormItem>
                </Col>
                <Col span="12">
                    <FormItem :label="$t('备注')" prop="remark">
                        <Input v-model.trim="formItem.remark"></Input>
                    </FormItem>
                </Col>
            </Row>
            <FormItem :label="$t('系统字段设置')">
                <Row>
                    <Col span="14">
                        {{$t('字段名称')}}
                    </Col>
                    <Col span="6">
                        {{$t('是否设为固定')}}
                    </Col>
                    <Col span="4">
                        {{$t('是否使用字段')}}
                    </Col>
                </Row>
                <Row>
                    <Col span="14">
                        <div class="desc-info">{{$t('拖动调整显示顺序')}}</div>
                    </Col>
                    <Col span="6">
                        <Checkbox v-model="changeAll" @on-change="onChangeAll">{{$t('全选')}}</Checkbox>
                    </Col>
                    <Col span="4">
                        <i-Switch @on-change="onCheckAll"
                                  v-model="checkAll"></i-Switch>
                    </Col>
                </Row>
                <draggable
                    v-model="fieldList" :options="{draggable:'.value-row'}">
                    <Row
                        class="value-row"
                        v-for="(item,idx) in fieldList"
                        :key="item.field+item.id">
                        <Col
                            span="14"
                            :class="{'gray':item.isSelected==null||item.isSelected==false}">
                            {{item.name}}
                        </Col>
                        <Col span="6">
                            <Checkbox
                                :class="{'gray':item.isSelected==null||item.isSelected==false}"
                                v-model="item.isRequiredShow" @on-change="changeOne($event,idx)">{{$t('固定字段')}}
                            </Checkbox>
                        </Col>
                        <Col span="4">
                            <i-Switch v-model="item.isSelected" @on-change="checkOne($event,idx)"></i-Switch>
                        </Col>
                    </Row>
                </draggable>
            </FormItem>
        </Form>
        <div slot="footer">
            <Row>
                <Col span="24" style="text-align:right">
                    <Button
                        v-if="formItem.id>0"
                        style="margin-right:5px"
                        @click="deleteItem"
                        type="error"
                        size="large">{{$t('删除')}}
                    </Button>
                    <Button @click="confirm" type="default" size="large">
                        {{$t('保存')}}
                    </Button>
                </Col>
            </Row>

        </div>

    </Modal>
</template>


<script>

    import draggable from 'vuedraggable';

    export default {
        mixins: [componentMixin],
        components: {
            draggable,
        },
        data() {
            return {
                continueCreate: false,
                formItem: {
                    id: 0,
                    name: null,
                    group: null,
                    remark: null,
                },
                formRule: {
                    name: [vd.req, vd.name2_20],
                    group: [vd.req, vd.name2_10],
                },
                fieldList: [],
                checkAll: true,
                changeAll: true
            };
        },
        methods: {
            pageLoad() {
                this.getFieldList();
            },
            getFieldList() {
                app.invoke('BizAction.getObjectTypeSystemFieldDefineList', [app.token], (list) => {
                    list.forEach(item => {
                        item.isSelected = false;
                        if (!item.isRequiredShow) {
                            this.changeAll = false;
                        }
                    });
                    this.fieldList = list;
                    if (this.args.id) {
                        this.loadData();
                    }
                });
            },
            loadData() {
                app.invoke('BizAction.getObjectTypeById', [app.token, this.args.id], (info) => {
                    this.formItem = info;
                    for (var i = 0; i < this.fieldList.length; i++) {
                        var t = this.fieldList[i];
                        var tt = this.getObjectTypeField(t.field);
                        if (tt != null) {
                            t.sortWeight = tt.sortWeight;
                            t.isRequiredShow = tt.isRequiredShow;
                            t.isSelected = true;
                        } else {
                            t.isSelected = false;
                            this.checkAll = false;
                        }
                    }
                    this.fieldList = this.fieldList.sort((a, b) => {
                        return a.sortWeight - b.sortWeight;
                    });
                });
            },
            getObjectTypeField(field) {
                for (var i = 0; i < this.formItem.fieldDefineList.length; i++) {
                    var t = this.formItem.fieldDefineList[i];
                    if (t.field == field) {
                        return t;
                    }
                }
                return null;
            },
            deleteItem() {
                app.confirm(this.$t('确定要删除此对象类型吗？'), () => {
                    this.confirmDelete();
                });
            },
            confirmDelete() {
                app.invoke('BizAction.deleteObjectType', [app.token, this.formItem.id], () => {
                    app.postMessage('objectType.edit');
                    this.showDialog = false;
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
                var list = [];
                for (var i = 0; i < this.fieldList.length; i++) {
                    var t = this.fieldList[i];
                    if (t.isSelected) {
                        list.push(t);
                    }
                }
                app.invoke('BizAction.saveObjectType', [app.token,
                    this.formItem, list], () => {
                    app.postMessage('objectType.edit');
                    app.toast(this.formItem.name + this.$t('保存成功'));
                    this.showDialog = false;
                });
            },
            onCheckAll(checked) {
                this.fieldList.forEach(item => {
                    if (checked) {
                        item.isSelected = true;
                    } else {
                        item.isSelected = false;
                    }
                })
            },
            onChangeAll(change) {
                this.fieldList.forEach(item => {
                    if (change) {
                        item.isRequiredShow = true;
                    } else {
                        item.isRequiredShow = false;
                    }
                })
            },
            changeOne(value, idx) {
                this.$set(this.fieldList[idx], "isRequireShow", value);
                if (value) {
                    this.changeAll = this.fieldList.filter(item => !item.isRequiredShow).length === 0;
                } else {
                    this.changeAll = false;
                }
            },
            checkOne(value, idx) {
                this.$set(this.fieldList[idx], "isSelected", value);
                if (value) {
                    this.checkAll = this.fieldList.filter(item => !item.isSelected).length === 0;
                } else {
                    this.checkAll = false;
                }
            }
        },
    };
</script>
