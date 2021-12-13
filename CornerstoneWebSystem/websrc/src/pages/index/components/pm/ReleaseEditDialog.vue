<style scoped>
</style>
<i18n>
    {
    "en": {
    "Release管理": "Release",
    "名称":"Name",
    "分类":"category",
    "发布时间":"Time",
    "Release名称":"Release name",
    "Release分类":"Release category",
    "详细描述":"Remark",
    "插入对象":"Insert Object",
    "继续创建下一个":"Continue to create next",
    "保存":"Save",
    "删除":"Delete",
    "创建":"Create",
    "确认要删除Release":"Are you sure you want to delete Release {0}?",
    "正在上传图片中，请稍后":"Uploading",
    "保存成功":"Success",
    "创建成功":"Success"
    },
    "zh_CN": {
    "Release管理": "Release管理",
    "Release分类": "Release分类",
    "名称":"名称",
    "分类":"分类",
    "发布时间":"发布时间",
    "Release名称":"Release名称",
    "详细描述":"详细描述",
    "插入对象":"插入对象",
    "继续创建下一个":"继续创建下一个",
    "保存":"保存",
    "删除":"删除",
    "创建":"创建",
    "确认要删除Release":"确认要删除Release【{0}】吗？",
    "正在上传图片中，请稍后":"正在上传图片中，请稍后",
    "保存成功":"保存成功",
    "创建成功":"创建成功"
    }
    }
</i18n>
<template>
    <Modal
            ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
            :loading="false" :title="$t('Release管理')" width="700" @on-ok="confirm">

        <Form @submit.native.prevent ref="form" :rules="formRule" :model="formItem"
              label-position="top" style="height:550px;padding:15px">


            <FormItem :label="$t('名称')" prop="name">
                <Input :disabled="projectDisabled" v-model.trim="formItem.name" :placeholder="$t('Release名称')" :maxlength="50"></Input>
            </FormItem>

            <FormItem :label="$t('分类')">
                <AutoComplete
                    :disabled="projectDisabled"
                    transfer
                    clearable
                    v-model="formItem.category"
                    :data="args.categories"
                    :placeholder="$t('Release分类')"
                    style="width:185px"></AutoComplete>
            </FormItem>

            <FormItem :label="$t('发布时间')" prop="releaseDate">
                <ExDatePicker :disabled="projectDisabled" v-model="formItem.releaseDate" :placeholder="$t('发布时间')"></ExDatePicker>
            </FormItem>
            <FormItem label="" v-if="formItem.id>0">
                <Button :disabled="projectDisabled" @click="deleteItem" type="error" size="large">{{$t('删除')}}</Button>
            </FormItem>

            <FormItem :label="$t('详细描述')" prop="description">
                <RichtextEditor ref="editor" v-model="formItem.description"></RichtextEditor>
                <div>
                    <IconButton :disabled="projectDisabled" @click="insertObject" icon="ios-add" :title="$t('插入对象')"></IconButton>
                </div>
            </FormItem>


        </Form>


        <div slot="footer">
            <Row v-if="!projectDisabled">
                <Col span="12" style="text-align:left;padding-top:5px">
                    <Checkbox v-if="formItem.id==0" v-model="continueCreate" size="large">{{$t('继续创建下一个')}}</Checkbox>
                </Col>
                <Col span="12" style="text-align:right">
                    <Button @click="confirm" type="default" size="large">{{formItem.id>0?$t('保存'):$t('创建')}}</Button>
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
                    projectId: 0,
                    name: null,
                    releaseDate: null,
                    description: null,
                    category:""
                },
                formRule: {
                    name: [vd.req, vd.name],
                    releaseDate: [vd.req]
                },
            }
        },
        methods: {
            pageLoad() {
                if (this.args.item) {
                    this.formItem = copyObject(this.args.item);
                }
                if (this.formItem.projectId == 0) {
                    this.formItem.projectId = this.args.projectId;
                }
            },
            deleteItem() {
                app.confirm(this.$t('确认要删除Release', [this.formItem.name]), () => {
                    app.invoke('BizAction.deleteProjectRelease', [app.token, this.formItem.id], (info) => {
                        app.postMessage('release.edit')
                        this.showDialog = false;
                    })
                })
            },
            insertObject() {
                app.showDialog(TaskSelectDialog, {
                    projectId: this.formItem.projectId,
                    callback: this.confirmInsert
                });
            },
            confirmInsert(list) {
                var html = "<ul>";
                var host = app.getHost();
                for (var i = 0; i < list.length; i++) {
                    var t = list[i];
                    var link = "/#/pm/project/" + app.projectUUID + "/task" + t.objectType + "?id=" + t.uuid;
                    html += "<li>" + "<span>" + t.objectTypeName + "</span>" + "<a href='" + link + "'>#" + t.serialNo + "</a>" + "<span style='margin-left:5px'>" + encodeHTML(t.name) + "</span></li>"
                }
                html += "</ul>"
                var vv = this.$refs.editor.getValue();
                if (vv == null) {
                    vv = "";
                }
                this.$refs.editor.setValue(vv + html)
            },
            confirm() {
                this.$refs.form.validate((r) => {
                    if (r) {
                        this.confirmForm()
                    }
                });
            },
            confirmForm() {
                if (this.$refs.editor.isUploading()) {
                    app.toast(this.$t('正在上传图片中，请稍后'));
                    return;
                }
                this.formItem.description = this.$refs.editor.getValue();
                //
                var action = this.formItem.id > 0 ? 'BizAction.updateProjectRelease' : 'BizAction.createProjectRelease';
                app.invoke(action, [app.token, this.formItem], (info) => {
                    app.toast(this.formItem.name + " " + (this.formItem.id > 0 ? this.$t('保存成功') : this.$t('创建成功')))
                    app.postMessage('release.edit')
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
