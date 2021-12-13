<style scoped>
</style>
<i18n>
    {
    "en": {
    "编辑交付版本": "Edit Delivery Version",
    "创建交付版本":"Create Delivery Version",
    "名称":"Name",
    "备注":"remark",
    "交付时间":"Delivery Date",
    "关联版本库":"Associate Company Version",
    "参与人":"Participants",
    "新增":"Add",
    "删除":"Delete",
    "版本库":"Company version repository name",
    "版本":"Company version name",
    "版本备注":"Company version remark",
    "保存成功":"Success",
    "创建成功":"Success",
    "保存":"Save",
    "创建":"Create"
    },
    "zh_CN": {
    "创建交付版本": "创建交付版本",
    "编辑交付版本":"编辑交付版本",
    "名称":"名称",
    "备注":"备注",
    "交付时间":"交付时间",
    "关联版本库":"关联版本库",
    "新增":"新增",
    "删除":"删除",
    "版本库":"版本库",
    "版本":"版本",
    "版本备注":"版本备注",
    "保存成功":"保存成功",
    "创建成功":"创建成功",
    "保存":"保存",
    "创建":"创建"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="args.id>0?$t('编辑交付版本'):$t('创建交付版本')" width="700">

        <Form ref="form" :rules="formRule" :model="formItem" label-position="top" style="height:550px;padding:15px;">
            <FormItem :label="$t('名称')" prop="name">
                <Input v-model.trim="formItem.name" :placeholder="$t('名称')"></Input>
            </FormItem>
            <FormItem :label="$t('备注')" prop="remark">
                <Input v-model.trim="formItem.remark" type="textarea" :rows="1"></Input>
            </FormItem>
            <FormItem :label="$t('交付时间')">
                <ExDatePicker clearable type="datetime" style="width:200px"
                              v-model="formItem.deliveryDate" :placeholder="$t('交付时间')"></ExDatePicker>
            </FormItem>

            <FormItem :label="$t('关联版本库')">
                <Button @click="addItems" type="primary">{{$t('新增')}}</Button>
                <table class="table-content table-color" v-if="!Array.isEmpty(formItem.deliveryItems)">
                    <thead>
                    <tr>
                        <th>{{$t('版本库')}}</th>
                        <th style="width:200px;">{{$t('版本')}}</th>
                        <th style="width:200px">{{$t('版本备注')}}</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="(item,idx) in formItem.deliveryItems" :key="'version_'+item.id" class="table-row">
                        <td>
                            {{item.repositoryName}}
                        </td>
                        <td>
                            {{item.name}}
                        </td>
                        <td>
                            <div v-html="item.remark"></div>
                        </td>
                        <td> <Button @click="deleteItem(idx)" type="error">{{$t('删除')}}</Button></td>
                    </tr>
                    </tbody>
                </table>

            </FormItem>
        </Form>


        <div slot="footer">
            <Row>
                <Col span="24" style="text-align:right">
                    <Button @click="confirm" type="default" size="large">
                        {{formItem.id>0?$t('保存'):$t('创建')}}
                    </Button>
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
                    projectId: 0,
                    name: null,
                    remark: null,
                    status: 1,
                    deliveryItems: [],
                },
                formRule: {
                    name: [vd.req, vd.name],
                    content: [vd.req, vd.desc],
                },
                projectList: []
            }
        },
        mounted() {
            this.formItem.projectId = app.projectId;
        },
        methods: {
            pageLoad() {
                console.log(this.args)
                if (this.args.id) {
                    this.loadData();
                }
            },
            loadData() {
                if (this.args.id > 0) {
                    app.invoke('BizAction.getDeliveryById', [app.token, this.args.id], (info) => {
                        this.formItem = info;
                    });
                }
            },
            deleteItem(idx) {
                this.formItem.deliveryItems.splice(idx,1);
            },
            confirm() {
                this.$refs.form.validate((r) => {
                    if (r) this.confirmForm()
                });
            },
            confirmForm() {
                var action = this.formItem.id > 0 ? 'BizAction.updateDelivery' : 'BizAction.addDelivery';
                app.invoke(action, [app.token, this.formItem], (info) => {
                    app.toast(this.formItem.name + (this.formItem.id > 0 ? this.$t('保存成功') : this.$t('创建成功')))
                    app.postMessage('delivery.edit')
                    this.showDialog = false;
                })
            },
            addItems(){
                let _this = this;
                app.showDialog(VersionRepositorySelectDialog,{
                    callback:function (list) {
                        if(!!list&&!Array.isEmpty(list)){
                            if(Array.isEmpty(_this.formItem.deliveryItems)){
                                _this.formItem.deliveryItems =list;
                            }else{
                                _this.formItem.deliveryItems = _this.formItem.deliveryItems.concat(list);
                            }
                        }
                        console.log(_this.formItem.deliveryItems)
                    }
                });
            }
        }
    }
</script>
