<style scoped>
    .line {
        border-top: 1px solid #eee;
        margin-top: 15px;
        margin-bottom: 15px;
    }

    .rule-desc {
        margin-bottom: 20px;
        color: #999;
    }
</style>

<i18n>
    {
    "en": {
    "系统供应商":"Supplier",
    "组织机构代码": "Organization code",
    "是否中断或异常退出": "whther interrupt or exit",
    "部门": "Departments",
    "选择部门": "Choose department",
    "企业角色": "Company role",
    "选择角色": "Choose",
    "项目角色": "Project role",
    "编辑":"Edit",
    "删除":"Delete",
    "删除此供应商":"Delete notification",
    "禁用此供应商":"Disable notification",
    "启用此供应商":"Enable notification",
    "继续创建下一个":"Continue to create the next ",
    "保存":"Save",
    "创建":"Create",
    "选择日期":"Choose date",
    "删除成功":"Delete success",
    "操作成功":"Success",
    "请选择供应商用户":"Please choose notify user or role",
    "供应商名称":"Supplier Name",
    "曾用名1":"Old name",
    "曾用名2":"Old name",
    "状态":"Status",
    "查询":"Query",
    "实施方式":"service type",
    "跨境情况":"overland type",
    "关联外包类型":"out type",
    "中断或异常退出":"if interrupt or exit",
    "创建时间":"Create time",
    "确认要删除吗？":"Are you sure you want to delete supplier “{0}”?"
    },
    "zh_CN": {
    "系统供应商":"系统供应商",
    "组织机构代码": "组织机构代码",
    "是否中断或异常退出": "是否中断或异常退出",
    "部门": "部门",
    "选择部门": "选择部门",
    "企业角色": "企业角色",
    "选择角色": "选择角色",
    "项目角色": "项目角色",
    "编辑":"编辑",
    "删除":"删除",
    "删除此供应商":"删除此供应商",
    "禁用此供应商":"禁用此供应商",
    "启用此供应商":"启用此供应商",
    "继续创建下一个":"继续创建下一个",
    "保存":"保存",
    "创建":"创建",
    "选择日期":"选择日期",
    "删除成功":"删除成功",
    "操作成功":"操作成功",
    "请选择供应商用户":"请选择供应商用户",
    "供应商名称":"供应商名称",
    "曾用名1":"曾用名1",
    "曾用名2":"曾用名2",
    "状态":"状态",
    "查询":"查询",
    "实施方式":"实施方式",
    "跨境情况":"跨境情况",
    "关联外包类型":"关联外包类型",
    "中断或异常退出":"中断或异常退出",
    "创建时间":"创建时间",
    "确认要删除吗？":"确认要删除供应商 “{0}” 吗？"
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
        :title="$t('系统供应商')"
        width="700"
        @on-ok="confirm">

        <Form ref="form" :rules="formRule" :model="formItem" label-position="top" style="min-height:600px;padding:15px">
            <div v-if="loaded">
                <FormItem :label="$t('供应商名称')" prop="name">
                    <Input :placeholder="$t('供应商名称')" v-model.trim="formItem.name"></Input>
                </FormItem>
                <FormItem :label="$t('曾用名1')" prop="oldName1">
                    <Input :placeholder="$t('曾用名1')" v-model.trim="formItem.oldName1"></Input>
                </FormItem>
                <FormItem :label="$t('曾用名2')" prop="oldName2">
                    <Input :placeholder="$t('曾用名2')" v-model.trim="formItem.oldName2"></Input>
                </FormItem>
                <FormItem :label="$t('组织机构代码')" prop="code">
                    <Input :placeholder="$t('组织机构代码')" v-model.trim="formItem.code"></Input>
                </FormItem>
                <FormItem :label="$t('实施方式')" prop="serviceType">
                    <DataDictSelect style="width: 200px;" clearable :placeholder="$t('实施方式')" type="Supplier.service" v-model="formItem.serviceType" />
                </FormItem>
                <FormItem  :label="$t('跨境情况')" prop="overlandType">
                    <DataDictSelect  style="width: 200px;" clearable :placeholder="$t('跨境情况')" type="Supplier.overland" v-model="formItem.overlandType" />
                </FormItem>
                <FormItem :label="$t('关联外包类型')" prop="outType">
                    <DataDictSelect  style="width: 200px;" clearable :placeholder="$t('关联外包类型')" type="Supplier.out" v-model="formItem.outType" />
                </FormItem>

                <FormItem :label="$t('是否中断或异常退出')" prop="exited">
                    <RadioGroup  style="width: 200px;" v-model="formItem.exit">
                        <Radio :label="1">
                            <span>是</span>
                        </Radio>
                        <Radio :label="2">
                            <span>否</span>
                        </Radio>
                    </RadioGroup>
                </FormItem>


                <div class="line"></div>
                <FormItem v-if="formItem.id>0" label="">
                    <Button @click="deleteItem" type="error">{{$t('删除此供应商')}}</Button>
<!--                    <Button-->
<!--                        v-if="formItem.status===1" style="margin-left:10px" @click="setEnable(false)" type="default">-->
<!--                        {{$t('禁用此供应商')}}-->
<!--                    </Button>-->
<!--                    <Button v-if="formItem.status===2" style="margin-left:10px" @click="setEnable(true)" type="default">-->
<!--                        {{$t('启用此供应商')}}-->
<!--                    </Button>-->
                </FormItem>
            </div>
        </Form>

        <div slot="footer">
            <Row>
                <Col span="12" style="text-align:left;padding-top:5px">
                    <Checkbox v-if="formItem.id===0" v-model="continueCreate" size="large">{{$t('继续创建下一个')}}</Checkbox>&nbsp;
                </Col>
                <Col span="12" style="text-align:right">
                    <Button @click="confirm" type="default" size="large">{{formItem.id>0?$t('保存'):$t('创建')}}</Button>
                </Col>
            </Row>

        </div>

    </Modal>
</template>

<script>
    import DataDictSelect from "../../../../components/ui/DataDictSelect";
    export default {
        components: {DataDictSelect},
        mixins: [componentMixin],

        data() {
            return {
                loaded: false,
                continueCreate: false,
                formItem: {
                    id: 0,
                    name: null,
                    oldName1: null,
                    oldName2: null,
                    code: null,
                    serviceType: null,
                    overlandType: null,
                    outType: null,
                    exit:2,
                    exited:false
                },
                formRule: {
                    name: [vd.req, vd.name],
                    serviceType: [vd.req],
                    overlandType: [vd.req,],
                    outType: [vd.req],
                },
            };
        },
        methods: {
            pageLoad() {
                if (this.args.id) {
                    this.loadData(this.args.id);
                } else if (this.args.copyId) {
                    this.loadData(this.args.copyId, true);
                } else {
                    this.loaded = true;
                }
            },
            loadData(id, copy) {
                app.invoke('BizAction.getSupplierById', [app.token, id], (info) => {
                    this.formItem = info;
                    this.formItem.exit = info.exited?1:2;
                    this.loaded = true;
                    if (copy) {
                        this.formItem.id = 0;
                    }
                });
            },
            deleteItem() {
                app.confirm(this.$t('确认要删除吗？', [this.formItem.name]), () => {
                    app.invoke('BizAction.deleteSupplier', [app.token, this.formItem.id], (info) => {
                        app.toast(this.$t('删除成功'));
                        app.postMessage('supplier.edit');
                        this.showDialog = false;
                    });
                });
            },
            setEnable(bool){
                this.formItem.status =bool?1:2;
                app.invoke(action, [app.token, this.formItem], (info) => {
                    app.toast(this.$t('操作成功'));
                    app.postMessage('supplier.edit');
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
            isEmptyArray(list) {
                return !Array.isArray(list) || list.length === 0;
            },
            confirmForm() {
                this.formItem.exited = this.formItem.exit==1;
                if(!this.perm('supplier_edit')){
                    app.toast(this.$t('权限不足'));
                }
                var action = this.formItem.id > 0 ? 'BizAction.updateSupplier' : 'BizAction.addSupplier';
                app.invoke(action, [app.token, this.formItem], (info) => {
                    app.toast(this.$t('操作成功'));
                    app.postMessage('supplier.edit');
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
