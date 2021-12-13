<style scoped>
    .import-table th {
        min-width: 100px;
        max-width: 200px;
    }

    .import-table td {
        min-width: 100px;
        max-width: 200px;
    }

    .template-name {
        max-width: 150px;
        display: inline-block;
    }

</style>
<i18n>
    {
    "en": {
    "导入": "Import",
    "表头":"Header",
    "方案":"Scheme",
    "删除方案":"Delete",
    "保存方案":"Save",
    "请上传包含有":"Please upload an excel file containing imported data. The supported file type is. xlsx.",
    "详细描述":"Detail",
    "请选择数据列对应的字段类型":"Select the field type corresponding to the data column",
    "导入成功":"Success",
    "保存成功":"Success",
    "确定要删除方案":"Are you sure you want to delete the scheme {0}?",
    "不导入":"Cancel import",
    "加载更多":"Loading more"
    },
    "zh_CN": {
    "导入": "导入",
    "表头":"表头",
    "方案":"方案",
    "删除方案":"删除方案",
    "保存方案":"保存方案",
    "请上传包含有":"请上传包含有导入数据的excel文件，支持的文件类型为.xlsx",
    "详细描述":"详细描述",
    "请选择数据列对应的字段类型":"请选择数据列对应的字段类型",
    "导入成功":"导入成功",
    "保存成功":"保存成功",
    "确定要删除方案":"确定要删除方案【{0}】吗？",
    "不导入":"不导入",
    "加载更多":"加载更多"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :fullscreen="fullscreen" :closable="true" :mask-closable="false"
        :loading="false" width="1000">
        <p @dblclick="fullscreen=!fullscreen" slot="header">
            {{$t('导入')}}{{typeName}}
        </p>

        <div style="height:400px">
            <table v-if="tableData!=null" class="table-content import-table">
                <thead>
                <tr>
                    <th style="width:120px">{{$t('表头')}}</th>
                    <th v-for="(header,headerIdx) in tableData.headers" :key="headerIdx">{{header}}</th>
                </tr>
                </thead>

                <tbody>
                <tr class="table-row table-row-small">
                    <td>
                        <Poptip placement="bottom-start" class="poptip-full" trigger="click">
                            <a class="text-no-wrap" style="max-width:80px;display:inline-block;padding-top:3px"
                               href="javascript:void(0)">
                                {{currentTemplate==null?$t('方案'):currentTemplate.name}}
                                <Icon type="ios-arrow-down"></Icon>
                            </a>

                            <div slot="content" style="width:270px;padding-top:10px">
                                <div v-for="t in importTemplateList" :key="t.id">
                                    <div @click="selectTemplate(t)" class="search-project-name">
                                        <span class="template-name text-no-wrap">{{t.name}}</span>
                                        <IconButton @click.stop="deleteTemplate(t)" style="float:right" icon="ios-trash"
                                                    size="20" :tips="$t('删除方案')"/>
                                    </div>
                                </div>

                                <div style="border-top:1px solid #eee">
                                    <div @click="saveTemplate" class="search-project-name">{{$t('保存方案')}}</div>
                                </div>
                            </div>
                        </Poptip>
                    </td>
                    <td class="import-select-field" v-for="(header,idx) in tableData.headers" :key="'ih'+idx">
                        <Select class="noborder-select" v-model="importFields[idx]" clearable transfer
                                style="width:100px" :placeholder="$t('不导入')">
                            <template v-for="field in fieldList">
                                <Option v-if="field.isShow" :key="field.id" :value="field.id">{{field.name}}</Option>
                            </template>
                        </Select>
                    </td>
                </tr>
                <!--                <TaskImportTable :table-list="tableData.contents"/>-->

                <tr class="table-row table-row-small" v-for="(row,idx) in showList" :key="'row'+idx">
                    <td>#{{idx+1}}</td>
                    <td class="text-no-wrap" v-for="(item,cellIdx) in row" :key="'c'+idx+'_'+cellIdx">{{item}}</td>
                </tr>

                <tr v-if="showMore">
                    <td style="padding-left: 450px;" :colspan="tableData.headers.length"><span @click="loadingMore">{{$t('加载更多')}}</span>
                    </td>
                </tr>
                </tbody>
            </table>

            <div v-if="tableData==null" style="text-align:center">
                <div style="font-size:16px;color:#999;font-weight:bold;margin-bottom:20px;margin-top:20px">
                    {{$t('请上传包含有')}}
                </div>
                <FileUploadView :format="['xlsx']" @change="uploadSuccess"></FileUploadView>
            </div>
        </div>


        <div slot="footer">
            <Row>
                <Col span="24" style="text-align:right">
                    <Button :disabled="tableData==null" @click="confirm" type="default" size="large">{{$t('导入')}}
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
                fullscreen: false,
                formItem: {},
                fieldList: [],
                importFields: [],
                tableData: null,
                currentTemplate: null,
                importTemplateList: [],
                fileUUID: null,
                showList: [],
                pageIdx: 1,
                pageSize:20
            }

        },
        computed: {
            typeName: function () {
                return app.dataDictValue('Task.objectType', this.args.objectType);
            },
            showMore: function () {
                return this.tableData.contents.length > this.pageIdx*this.pageSize;
            }
        },
        methods: {
            pageLoad() {
                this.formItem.projectId = this.args.projectId;
                this.formItem.objectType = this.args.objectType;
                this.fieldList = [];
                this.args.fieldList.forEach(element => {
                    this.fieldList.push(element);
                });
                this.fieldList.push({
                    id: -1,
                    name: this.$t("详细描述"),
                    isShow: true
                })
                this.loadTemplateList();
            },
            pageMessage(type) {
                if (type == 'import.template.edit') {
                    this.loadTemplateList();
                }
            },
            uploadSuccess(uuid) {
                this.fileUUID = uuid.uuid;
                app.invoke("BizAction.getTableDataFromExcel", [app.token, uuid.uuid], data => {
                    this.tableData = data;
                    this.setShowList();
                    this.autoSetImportFields(data);
                });
            },
            autoSetImportFields(data) {
                for (var i = 0; i < data.headers.length; i++) {
                    var header = data.headers[i];
                    this.importFields.push(this.getHeaderFieldId(header));
                }
            },
            getHeaderFieldId(name) {
                for (var i = 0; i < this.fieldList.length; i++) {
                    var ff = this.fieldList[i];
                    if (ff.name == name) {
                        return ff.id;
                    }
                }
                return 0;
            },
            loadTemplateList() {
                var query = {
                    pageIndex: 1,
                    pageSize: 100,
                    projectId: this.formItem.projectId,
                    objectType: this.formItem.objectType,
                }
                app.invoke("BizAction.getTaskImportTemplateList", [app.token, query], data => {
                    this.importTemplateList = data.list;
                });
            },
            confirm() {
                var count = 0;
                for (var i = 0; i < this.importFields.length; i++) {
                    count += this.importFields[i];
                }
                if (count == 0) {
                    app.toast(this.$t('请选择数据列对应的字段类型'));
                    return;
                }
                //
                app.invoke("BizAction.importTaskFromExcel", [app.token,
                    this.formItem.projectId,
                    this.formItem.objectType,
                    this.importFields,
                    this.fileUUID
                ], data => {
                    app.toast(this.$t('导入成功'));
                    this.showDialog = false;
                    app.postMessage('task.import', this.objectType);
                });
            },
            selectTemplate(item) {
                this.currentTemplate = item;
                for (var i = 0; i < this.importFields.length; i++) {
                    if (i < item.fields.length) {
                        this.importFields[i] = item.fields[i];
                    }
                }
                this.$set(this.importFields, this.importFields)
            },
            saveTemplate() {
                if (this.currentTemplate != null) {
                    this.currentTemplate.fields = this.importFields;
                    app.invoke("BizAction.updateTaskImportTemplate", [app.token, this.currentTemplate], data => {
                        app.toast(this.$t('保存成功'));
                    });
                } else {
                    app.showDialog(TaskImportTemplateDialog, {
                        projectId: this.args.projectId,
                        objectType: this.args.objectType,
                        fields: this.importFields
                    })
                }
            },
            deleteTemplate(item) {
                app.confirm(this.$t('确定要删除方案', [item.name]), () => {
                    app.invoke("BizAction.deleteTaskImportTemplate", [app.token, item.id], data => {
                        this.loadTemplateList();
                    });
                })
            },
            setShowList() {
                this.showList = this.showList.concat(this.tableData.contents.slice((this.pageIdx - 1) * this.pageSize, this.pageIdx * this.pageSize));
            },
            loadingMore() {
                this.pageIdx++;
                this.setShowList();
            }

        }
    }
</script>
