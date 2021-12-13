<style scoped>
    .other-field {
        border-top: 1px solid #eee;
        padding-top: 20px;
    }

    .other-field-item {
        display: inline-block;
        width: 49%;
        padding-right: 30px;
    }

    .full-field-item {
        display: inline-block;
        width: 100%;
        padding-right: 0px;
    }

    .dialog-header {
        display: flex;
        align-items: center;
    }

    .dialog-header-title {
        font-size: 14px;
        font-weight: 700;
        color: #444;
    }

    .dialog-opt {
        flex: 1;
        text-align: right;
        padding-right: 30px;
    }

    .info-table {
        background-color: #F7F7F7;
    }
</style>
<i18n>
    {
    "en": {
    "未设置":"None",
    "待认领":"None",
    "创建": "Create",
    "上传附件":"Upload",
    "关联对象":"Relation",
    "名称":"Name",
    "责任人":"Owner",
    "状态":"Status",
    "优先级":"Priority",
    "分类":"Category",
    "项目":"Project",
    "迭代":"Iteration",
    "起始时间":"Start",
    "截止时间":"End",
    "关联子系统":"System",
    "关联Release":"Release",
    "预计工时":"Expect time",
    "附件":"Attachment",
    "移除":"Remove",
    "详细描述":"Detail",
    "关联对象":"Relations",
    "继续创建下一个":"Continue to create next",
    "创建":"Create",
    "正在上传图片中，请稍后":"Uploading",
    "表单中有数据不符合要求，请检查后重新填写":"The data in the form does not meet the requirements. Please check and fill in again.",
    "创建成功":"【{0}】created",
    "预计截止时间":"Expect deadline"
    },
    "zh_CN": {
    "待认领":"待认领",
    "未设置":"未设置",
    "创建": "创建",
    "上传附件":"上传附件",
    "关联对象":"关联对象",
    "名称":"名称",
    "责任人":"责任人",
    "状态":"状态",
    "优先级":"优先级",
    "分类":"分类",
    "项目":"项目",
    "迭代":"迭代",
    "起始时间":"起始时间",
    "截止时间":"截止时间",
    "关联子系统":"关联子系统",
    "关联Release":"关联Release",
    "预计工时":"预计工时",
    "附件":"附件",
    "移除":"移除",
    "详细描述":"详细描述",
    "关联对象":"关联对象",
    "继续创建下一个":"继续创建下一个",
    "创建":"创建",
    "正在上传图片中，请稍后":"正在上传图片中，请稍后",
    "表单中有数据不符合要求，请检查后重新填写":"表单中有数据不符合要求，请检查后重新填写",
    "创建成功":"【{0}】创建成功",
    "预计截止时间":"预计截止时间"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" width="700">
        <div slot="header" class="dialog-header">
            <div class="dialog-header-title">{{$t('创建')}}{{typeName}}</div>
            <div class="dialog-opt">
                <Poptip placement="bottom-end">
                    <IconButton icon="md-attach" :tips="$t('上传附件')"></IconButton>
                    <FileUploadView style="padding:10px;width:450px" multiple @change="uploadSuccess"
                                    slot="content"></FileUploadView>
                </Poptip>
                <IconButton v-show="!isProjectSetPro&&formItem.id>0" @click="showSelectTaskDialog" icon="ios-link"
                            :tips="$t('关联对象')"></IconButton>
            </div>
        </div>

        <Form ref="form" :rules="formRule" :model="formItem" label-position="top"
              style="height:calc(100vh - 200px);padding:15px">
            <FormItem prop="name">
                <Input class="bold-input" :placeholder="typeName+$t('名称')" v-model.trim="formItem.name"></Input>
            </FormItem>
            <Row>
                <Col span="6" v-if="hasField('ownerAccountName')">
                    <FormItem :label="getLabelName('ownerAccountName','责任人')" prop="ownerAccountIdList">
                        <MemberSelect ref="memberSelect" :placeholder="$t('待认领')" :multiple="true"
                                      :member-list="editInfo.memberList" v-model="formItem.ownerAccountIdList"
                                      placement="bottom-start"></MemberSelect>
                    </FormItem>
                </Col>
                <Col span="6" v-if="hasField('statusName')">
                    <FormItem :label="getLabelName('statusName','状态')" prop="status">
                        <TaskStatusSelect :status-list="editInfo.statusList" v-model="formItem.status"
                                          placement="bottom-start"></TaskStatusSelect>
                    </FormItem>
                </Col>
                <Col span="6" v-if="hasField('priorityName')">
                    <FormItem :label="getLabelName('priorityName','优先级')" prop="priority">
                        <TaskPrioritySelect :priority-list="editInfo.priorityList" v-model="formItem.priority"
                                            placement="bottom-start"></TaskPrioritySelect>
                    </FormItem>
                </Col>

                <Col span="6">
                    <FormItem :label="getLabelName('categoryIdList','分类')" style="overflow:hidden" prop="categoryIdList"
                              :rules="formRule.categoryIdList">
                        <CategorySelect :category-list="editInfo.categoryNodeList" v-model="formItem.categoryIdList"
                                        placement="bottom"></CategorySelect>
                    </FormItem>
                </Col>

            </Row>
            <Row class="other-field">
                <Col span="12">
                    <FormItem :label="$t('项目')">
                        <template v-if="editInfo.project">{{editInfo.project.name}}</template>
                    </FormItem>
                </Col>

                <Col span="12" v-if="hasField('iterationName')">
                    <FormItem :label="getLabelName('iterationName','迭代')" prop="iterationId">
                        <TaskObjectSelect :object-list="editInfo.iterationList" v-model="formItem.iterationId"
                                          placement="bottom-start"></TaskObjectSelect>
                    </FormItem>
                </Col>

            </Row>

            <div class="other-field">
                <template v-for="item in editInfo.fieldList">
                    <div class="other-field-item"
                         :class="{'full-field-item':item.type==1&&item.isSystemField==false}"
                         :key="item.id"
                         v-if="isSystemOtherField(item)">

                        <template v-if="item.isSystemField">
                            <FormItem v-if="item.field=='startDate'" :label="item.name"
                                      prop="startDate">
                                <DateSelect
                                    ref="start"
                                    :options="startDateOptions"
                                    :end="startDateOptions.end"
                                    type="date"
                                    v-model="formItem.startDate"
                                    :placeholder="$t('未设置')" placement="bottom-start"></DateSelect>
                            </FormItem>

                            <FormItem v-if="item.field=='endDate'"  :label="item.name" prop="endDate">
                                <DateSelect
                                    ref="end"
                                    :options="endDateOptions"
                                    :start="endDateOptions.start"
                                    type="date"
                                    v-model="formItem.endDate"
                                    placeholder="未设置" placement="bottom-start"></DateSelect>
                            </FormItem>

                            <FormItem v-if="item.field=='subSystemName'"  :label="item.name"  prop="subSystemId">
                                <TaskObjectSelect :object-list="editInfo.subSystemList" v-model="formItem.subSystemId"
                                                  placement="bottom-start"></TaskObjectSelect>
                            </FormItem>

                            <FormItem v-if="item.field=='releaseName'" :label="item.name" prop="releaseId">
                                <TaskObjectSelect :object-list="editInfo.releaseList" v-model="formItem.releaseId"
                                                  placement="bottom-start"></TaskObjectSelect>
                            </FormItem>
                            <FormItem v-if="item.field=='repositoryName'" :label="item.name" prop="repositoryId">
                                <TaskObjectSelect :object-list="editInfo.repositoryList" v-model="formItem.repositoryId"
                                                  placement="bottom-start"></TaskObjectSelect>
                            </FormItem>

                            <FormItem v-if="item.field=='expectEndDate'" :label="item.name"  prop="expectEndDate">
                                <DateSelect :options="endDateOptions" type="date" v-model="formItem.expectEndDate"
                                            placeholder="未设置" placement="bottom-start"></DateSelect>
                            </FormItem>

                            <FormItem v-if="item.field=='expectWorkTime'" :label="item.name"  prop="expectWorkTime">
                                <Input :maxlength="16" type="number" v-model.number="formItem.expectWorkTime"
                                       :placeholder="$t('未设置')"></Input>
                            </FormItem>
                            <FormItem v-if="item.field=='workLoad'" :label="item.name"  prop="workLoad">
                                <Input :maxlength="16" type="number" v-model.number="formItem.workLoad"
                                       :placeholder="$t('未设置')"></Input>
                            </FormItem>

                            <FormItem
                                v-if="item.field=='stageName'"  :label="item.name">
                                <TaskViewStageSelect
                                    :list="editInfo.stageList"
                                    v-model="formItem.stageId"
                                    :editable="true"
                                    :searchable="false"
                                    placement="bottom"/>
                            </FormItem>

                        </template>
                        <template v-else>
                            <FormItem :label="item.name" style="padding-right:20px"
                                      :prop="'customFields.field_'+item.id">
                                <Input type="textarea" autoresize :maxlength="500" v-if="item.type==1"
                                       v-model.trim="formItem.customFields['field_'+item.id]"
                                       :placeholder="$t('未设置')"></Input>
                                <Input :maxlength="16" type="number" style="width:100%" v-if="item.type==8"
                                       v-model.number="formItem.customFields['field_'+item.id]"
                                       :placeholder="$t('未设置')"></Input>
                                <DateSelect type="date" v-if="item.type==7" :showTimeField="item.showTimeField"
                                            v-model="formItem.customFields['field_'+item.id]"
                                            :placeholder="$t('未设置')"></DateSelect>
                                <TaskCustomSelect :multiple="true" v-if="item.type==3"
                                                  v-model="formItem.customFields['field_'+item.id]"
                                                  :object-list="item.valueRange"
                                                  placement="bottom-start"></TaskCustomSelect>
                                <TaskCustomSelect :multiple="false" v-if="item.type==4"
                                                  v-model="formItem.customFields['field_'+item.id]"
                                                  :object-list="item.valueRange"
                                                  placement="bottom-start"></TaskCustomSelect>


                                <MemberSelect :placeholder="$t('未设置')" v-if="item.type==6"
                                              :member-list="editInfo.memberList"
                                              :multiple="true"
                                              v-model="formItem.customFields['field_'+item.id]"
                                              placement="bottom-start"></MemberSelect>

                            </FormItem>

                        </template>

                    </div>
                </template>
            </div>
            <FormItem v-if="attachmentList.length>0" :label="$t('附件')">
                <table class="info-table table-content table-content-small">
                    <tbody>
                    <tr v-for="item in attachmentList" :key="'att_'+item.uuid" class="table-row table-row-small">
                        <td> {{item.name}}</td>
                        <td style="text-align:right;width:100px;color:#999">
                            <IconButton @click="deleteAttachment(item)" icon="ios-trash" :tips="$t('移除')"></IconButton>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </FormItem>


            <FormItem v-if="relTaskList.length>0" :label="$t('关联对象')">
                <table class="info-table table-content table-content-small">
                    <tbody>

                    <tr v-for="item in relTaskList" :key="'ass_'+item.id" class="table-row table-row-small">
                        <td class="clickable">
                            <TaskNameLabel @click.native="showTaskInfo(item)"
                                           :id="item.serialNo" :name="item.name" :rel="0"
                                           :object-type="item.objectTypeName"/>
                        </td>
                        <td style="text-align:right;width:100px;color:#999">
                            <IconButton @click="deleteRelObj(item)" icon="ios-trash" :tips="$t('移除')"></IconButton>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </FormItem>

            <FormItem :label="$t('详细描述')">
                <RichtextEditor ref="editor" v-model="formItem.content"></RichtextEditor>
            </FormItem>

        </Form>


        <div slot="footer">
            <Row>
                <Col span="12" style="text-align:left;padding-top:5px">
                    <Checkbox v-model="continueCreate" size="large">{{$t('继续创建下一个')}}</Checkbox>
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
                editInfo: {},
                formItem: {
                    name: "",
                    ownerAccountIdList: [],
                    status: null,
                    priority: null,
                    categoryIdList: [],
                    customFields: {},
                    startDate: new Date().getTime(),
                    endDate: null,
                    objectType: null,
                    objectTypeSystemName: null,
                },
                fieldMap: {
                    ownerAccountName: 'ownerAccountIdList',
                    statusName: 'status',
                    priorityName: 'priority',
                    iterationName: 'iterationId',
                    subSystemName: 'subSystemId',
                    releaseName: 'releaseId',
                    repositoryName: 'repositoryId',
                },
                relTaskList: [],
                attachmentList: [],
                formRule: {
                    name: [vd.req, vd.name2_100],
                },
                startDateOptions: {
                    disabledDate: (date) => {
                        if (this.formItem.endDate == null) {
                            return false;
                        }
                        if (this.args.associateType == 2) {
                            if (this.args.startDate) {
                                return date && date.valueOf() < this.args.startDate;
                            }
                        }
                        return date && date.valueOf() > this.formItem.endDate;
                    },
                    end: null
                },
                endDateOptions: {
                    disabledDate: (date) => {
                        if (this.formItem.startDate == null) {
                            return false;
                        }
                        if (this.args.associateType == 1) {
                            if (this.args.endDate) {
                                return date && date.valueOf() > this.args.endDate;
                            }
                        }
                        var t = new Date(this.formItem.startDate);
                        t.setHours(0, 0, 0, 0);
                        return date && date.valueOf() < t.getTime();
                    },
                    start: null,
                    // shortcuts: [{text:"截止时间",value: (e)=>new Date("2021-01-02")}]
                },
            }
        },
        watch: {
            'formItem.startDate': function (val, oldVal) {
                console.log("watch startDate", new Date(val), new Date(oldVal))
                if (val != null) {
                    this.$set(this.endDateOptions, "start", new Date(val))
                } else {
                    this.$set(this.endDateOptions, "start", null)
                }
            },
            'formItem.endDate': function (val) {
                if (val != null) {
                    this.$set(this.startDateOptions, "end", new Date(val))
                } else {
                    this.$set(this.startDateOptions, "end", null)
                }
            },
        },
        computed: {
            typeName: function () {
                return app.dataDictValue('Task.objectType', this.args.type);
            },
            isProjectSetPro() {
                return this.formItem.objectTypeSystemName === process.env.VUE_APP_PROJECT_SET_OBJECT_TYPE_SYSTEM_NAME_PRO;
            },
        },
        methods: {
            pageLoad() {
                this.formItem.projectId = this.args.projectId;
                this.formItem.iterationId = this.args.iterationId;
                this.formItem.objectType = this.args.type;
                this.formItem.objectTypeSystemName = this.args.objectTypeSystemName;
                this.formItem.parentId = this.args.parentId;
                this.formItem.status = this.args.status;
                if (this.args.categoryId) {
                    this.formItem.categoryIdList.push(this.args.categoryId)
                }
                if (this.args.startDate) {
                    this.formItem.startDate = this.args.startDate;
                }
                if (this.args.endDate) {
                    this.formItem.endDate = this.args.endDate;
                    if (this.formItem.startDate > this.formItem.endDate) {
                        this.formItem.startDate = this.formItem.endDate;
                    }
                }
                if (this.args.ownerAccountIdList) {
                    this.args.ownerAccountIdList.forEach(e => {
                        this.formItem.ownerAccountIdList.push(e);
                    })
                }
                this.loadEditInfo();
            },
            pageMessage(type) {
                if (type == 'category.edit') {
                    this.loadEditInfo();
                }
            },
            isSystemOtherField(item) {
                var excludesList = [
                    "workTime",
                    "startDays",
                    "endDays",
                    "iterationName",
                    "categoryIdList",
                    "name",
                    "ownerAccountName",
                    "createAccountName",
                    "priorityName",
                    "statusName",
                    "finishTime",
                    "createTime",
                    "updateTime",
                    "progress"
                ]
                for (var i = 0; i < excludesList.length; i++) {
                    var t = excludesList[i];
                    if (t == item.field) {
                        return false;
                    }
                }
                return true;
            },
            hasField(name) {
                if (this.editInfo.fieldList) {
                    for (var i = 0; i < this.editInfo.fieldList.length; i++) {
                        var f = this.editInfo.fieldList[i];
                        if (f.field == name) {
                            return true;
                        }
                    }
                }
                return false;
            },
            getLabelName(key, defVal) {
                if (this.editInfo && this.editInfo.fieldList && this.editInfo.fieldList.length > 0) {
                    let fieldDefine = this.editInfo.fieldList.find(k => k.field === key);
                    if (fieldDefine) {
                        return fieldDefine.name;
                    } else {
                        return defVal;
                    }
                }
                return defVal;
            },
            loadEditInfo() {
                app.invoke("BizAction.getEditTaskInfo", [app.token, this.args.projectId, this.args.type], info => {
                    this.editInfo = info;
                    //
                    this.editInfo.priorityList.map(item => {
                        if (item.isDefault) {
                            this.formItem.priority = item.id;
                        }
                    })
                    this.editInfo.statusList.map(item => {
                        if (item.type == 1 && this.args.status == null) {
                            this.formItem.status = item.id;
                        }
                    });
                    const formRule = {
                        name: [vd.req, vd.name2_100],
                    };
                    this.editInfo.fieldList.forEach(item => {
                        if (item.field === 'name') {
                            return;
                        }
                        if (item.isRequired) {
                            if (item.isSystemField) {
                                const field = this.fieldMap[item.field] || item.field;
                                formRule[field] = [vd.req];
                                return;
                            }
                            formRule['customFields.field_' + item.id] = [vd.req];
                        }
                    });
                    this.formRule = formRule;
                    // this.$nextTick(() => {
                    //     this.$refs.form.resetFields();
                    // });
                    //
                    if (this.formItem.iterationId == null && info.iterationList.length > 0) {
                        this.formItem.iterationId = info.iterationList[0].id;
                    }
                    //
                    if (this.editInfo.objectTypeTemplate) {
                        this.formItem.name = this.editInfo.objectTypeTemplate.name;
                        this.formItem.content = this.editInfo.objectTypeTemplate.content;
                        if (this.formItem.content != null) {
                            this.$refs.editor.setValue(this.formItem.content);
                        }
                    }
                });
            },
            uploadSuccess(uuid) {
                this.attachmentList.push(uuid);
            },
            containsAttachment(item) {
                for (var i = 0; i < this.attachmentList.length; i++) {
                    if (this.attachmentList[i].uuid == item.uuid) {
                        return true;
                    }
                }
                return false;
            },
            deleteAttachment(item) {
                for (var i = 0; i < this.attachmentList.length; i++) {
                    var t = this.attachmentList[i];
                    if (t.uuid == item.uuid) {
                        this.attachmentList.splice(i, 1);
                    }
                }
            },
            showTaskInfo(item) {
                app.showDialog(TaskDialog, {
                    taskId: item.uuid
                })
            },
            showSelectTaskDialog() {
                app.showDialog(TaskSelectDialog, {
                    projectId: this.args.projectId,
                    callback: this.addRelTask
                })
            },
            addRelTask(list) {
                if (list.length == 0) {
                    return;
                }
                var addList = [];
                for (var i = 0; i < list.length; i++) {
                    var t = list[i];
                    if (!this.containsRelTask(t)) {
                        addList.push(t);
                    }
                }
                this.relTaskList = this.relTaskList.concat(addList);
            },
            deleteRelObj(item) {
                for (var i = 0; i < this.relTaskList.length; i++) {
                    var t = this.relTaskList[i];
                    if (t.id == item.id) {
                        this.relTaskList.splice(i, 1);
                    }
                }
            },
            containsRelTask(item) {
                for (var i = 0; i < this.relTaskList.length; i++) {
                    if (this.relTaskList[i].id == item.id) {
                        return true;
                    }
                }
                return false;
            },
            confirm() {
                if (this.$refs.memberSelect) {
                    this.$refs.memberSelect.confirm();
                }

                this.$refs.form.validate((r) => {
                    if (r) {
                        this.confirmForm()
                    } else {
                        app.toast(this.$t('表单中有数据不符合要求，请检查后重新填写'));
                    }
                });
            },
            confirmForm() {
                if (this.$refs.editor.isUploading()) {
                    app.toast(this.$t('正在上传图片中，请稍后'));
                    return;
                }
                this.formItem.content = this.$refs.editor.getValue();
                var taskList = [];
                this.relTaskList.forEach((item) => {
                    taskList.push(item.id);
                })
                var attList = [];
                this.attachmentList.forEach((item) => {
                    attList.push(item.uuid);
                })
                this.formItem.attachmentUuidList = attList;
                this.formItem.associatedIdList = taskList;
                //前后关联时需要校验时间
                if (this.args.associateType && this.args.associateType > 0) {
                    var startDate = this.args.startDate;
                    var endDate = this.args.endDate;
                    if (this.args.associateType && this.args.associateType == 1) {
                        if (endDate && this.formItem.endDate) {
                            if (Date.parse(new Date(endDate)) < Date.parse(new Date(this.formItem.endDate))) {
                                app.toast('创建前置被关联对象的截止时间不能晚于关联对象的开始时间');
                                return;
                            }
                        }
                    }
                    if (this.args.associateType && this.args.associateType == 2) {
                        if (startDate && this.formItem.startDate) {
                            if (Date.parse(new Date(startDate)) > Date.parse(new Date(this.formItem.startDate))) {
                                app.toast('创建后置被关联对象的开始时间不能早于关联对象的截止时间');
                                return;
                            }
                        }
                    }
                }
                app.invoke('BizAction.createTask', [app.token, this.formItem], (id) => {
                    app.toast(this.$t('创建成功', [this.formItem.name]))
                    app.postMessage('task.edit', id)
                    if (this.args.callback) {
                        this.args.callback(id);
                    }
                    if (!this.continueCreate) {
                        this.showDialog = false;
                    } else {
                        var oldCategoryIds = this.formItem.categoryIdList;

                        this.formItem.content = "";
                        this.$refs.editor.setValue('<p></p>');
                        this.$refs.form.resetFields();
                        this.formItem.categoryIdList = oldCategoryIds;
                        //
                        if (this.editInfo.objectTypeTemplate) {
                            this.formItem.name = this.editInfo.objectTypeTemplate.name;
                            this.formItem.content = this.editInfo.objectTypeTemplate.content;
                            if (this.formItem.content != null) {
                                this.$refs.editor.setValue(this.formItem.content);
                            }
                        }
                        //
                        this.attachmentList = [];
                        this.relTaskList = [];
                    }
                })
            }
        }
    }
</script>
