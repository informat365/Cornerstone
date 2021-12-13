<style lang="less" scoped>
    .other-field {
        border-top: 1px solid #eee;
        padding-top: 20px;
    }

    .other-field-item {
        display: inline-block;
        width: 314px;
        padding-right: 30px;
    }

    .full-field-item {
        display: inline-block;
        width: 100%;
        padding-right: 0;
    }

    .task-edit-form {

    }

    .task-edit-form {
        .task-edit-form-field {
            position: relative;
            padding-left: 20px;

            .task-edit-form-checkbox {
                position: absolute;
                top: 0;
                left: 0;
                width: 24px;
                vertical-align: top;
            }

            /deep/ .ivu-form-item {
                position: relative;
                width: 100%;
            }
        }

        .task-edit-form-checkbox /deep/ span.ivu-checkbox {
            display: block;

            & ~ span {
                display: none;
            }
        }
    }

</style>
<i18n>
    {
    "en": {
    "编辑": "Edit",
    "不更新": "No update",
    "正在编辑":"Editing {0} {1}",
    "请设置需要批量编辑的属性":"Set properties that require batch editing. Unset properties will not be updated.",
    "责任人":"Owner",
    "待认领":"none",
    "状态":"Status",
    "优先级":"Priority",
    "分类":"Category",
    "项目":"Project",
    "迭代":"Iteration",
    "未设置":"none",
    "保存":"Save"
    },
    "zh_CN": {
    "编辑": "编辑",
    "不更新": "不更新",
    "正在编辑":"正在编辑{0}个{1}",
    "请设置需要批量编辑的属性":"请勾选要设置的属性 未勾选的不会被更新。",
    "责任人":"责任人",
    "待认领":"待认领",
    "状态":"状态",
    "优先级":"优先级",
    "分类":"分类",
    "项目":"项目",
    "迭代":"迭代",
    "未设置":"未设置",
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
        :title="$t('编辑')+typeName"
        width="700">
        <Alert>
            {{$t('正在编辑',[itemList.length,typeName])}},{{$t('请设置需要批量编辑的属性')}}
        </Alert>
        <Form
            ref="form" class="task-edit-form" :model="formItem" label-position="top" style="height:600px;padding:15px">
            <CheckboxGroup v-model="checkFields">
                <Row>
                    <Col
                        span="6" v-if="hasField('ownerAccountName')" class="task-edit-form-field">
                        <Checkbox
                            class="task-edit-form-checkbox" label="ownerAccountIdList" />
                        <FormItem :label="getLabelName('ownerAccountName','责任人')" >
                            <MemberSelect
                                v-show="checkFields.indexOf('ownerAccountIdList') > -1"
                                ref="memberSelect"
                                :placeholder="$t('待认领')"
                                :multiple="true"
                                :member-list="editInfo.memberList"
                                v-model="formItem.ownerAccountIdList"
                                placement="bottom-start" />
                            <span v-show="checkFields.indexOf('ownerAccountIdList') === -1" class="placeholder-label">
                                {{$t('不更新')}}
                            </span>
                        </FormItem>
                    </Col>
                    <Col
                        span="6" v-if="hasField('statusName')" class="task-edit-form-field">
                        <Checkbox
                            class="task-edit-form-checkbox" label="status" />
                        <FormItem :label="getLabelName('statusName','状态')" >
                            <TaskStatusSelect
                                v-show="checkFields.indexOf('status') > -1"
                                :status-list="editInfo.statusList"
                                v-model="formItem.status"
                                placement="bottom-start" />
                            <span v-show="checkFields.indexOf('status') === -1" class="placeholder-label">
                                {{$t('不更新')}}
                            </span>
                        </FormItem>
                    </Col>
                    <Col
                        span="6" v-if="hasField('priorityName')" class="task-edit-form-field">
                        <Checkbox
                            class="task-edit-form-checkbox" label="priority" />
                        <FormItem :label="getLabelName('priorityName','优先级')" >
                            <TaskPrioritySelect
                                v-show="checkFields.indexOf('priority') > -1"
                                :priority-list="editInfo.priorityList"
                                v-model="formItem.priority"
                                placement="bottom-start" />
                            <span v-show="checkFields.indexOf('priority') === -1" class="placeholder-label">
                                {{$t('不更新')}}
                            </span>
                        </FormItem>
                    </Col>
                    <Col
                        span="6" class="task-edit-form-field">
                        <Checkbox
                            class="task-edit-form-checkbox" label="categoryIdList" />
                        <FormItem :label="getLabelName('categoryIdList','分类')" >
                            <CategorySelect
                                v-show="checkFields.indexOf('categoryIdList') > -1"
                                :category-list="editInfo.categoryNodeList"
                                v-model="formItem.categoryIdList"
                                placement="bottom" />
                            <span v-show="checkFields.indexOf('categoryIdList') === -1" class="placeholder-label">
                                {{$t('不更新')}}
                            </span>
                        </FormItem>
                    </Col>

                </Row>
                <Row class="other-field">
                    <Col span="12">
                        <FormItem :label="$t('项目')">
                            <template v-if="editInfo.project">
                                {{editInfo.project.name}}
                            </template>
                        </FormItem>
                    </Col>
                    <Col
                        span="12" v-if="hasField('iterationName')" class="task-edit-form-field">
                        <Checkbox
                            class="task-edit-form-checkbox" label="iterationId" />
                        <FormItem :label="getLabelName('iterationName','迭代')" >
                            <TaskObjectSelect
                                v-show="checkFields.indexOf('iterationId') > -1"
                                :nodata="true"
                                :object-list="editInfo.iterationList"
                                v-model="formItem.iterationId"
                                placement="bottom-start" />
                            <span v-show="checkFields.indexOf('iterationId') === -1" class="placeholder-label">
                                {{$t('不更新')}}
                            </span>
                        </FormItem>
                    </Col>
                </Row>
                <div class="other-field">
                    <template v-for="item in editInfo.fieldList">
                        <div
                            class="task-edit-form-field other-field-item"
                            :class="{'full-field-item':item.type==1&&item.isSystemField==false}"
                            :key="item.id"
                            v-if="isSystemOtherField(item)">
                            <template v-if="item.isSystemField">
                                <Checkbox
                                    class="task-edit-form-checkbox" :label="item.field" />
                                <FormItem
                                    v-if="item.field=='startDate'" :label="item.name">
                                    <DateSelect
                                        v-show="checkFields.indexOf('startDate') > -1"
                                        :options="startDateOptions"
                                        type="date"
                                        v-model="formItem.startDate"
                                        :placeholder="$t('未设置')"
                                        placement="bottom-start" />
                                    <span v-show="checkFields.indexOf('startDate') === -1" class="placeholder-label">
                                        {{$t('不更新')}}
                                    </span>
                                </FormItem>
                                <FormItem
                                    v-if="item.field=='endDate'" :label="item.name">
                                    <DateSelect
                                        v-show="checkFields.indexOf('endDate') > -1"
                                        :options="endDateOptions"
                                        type="date"
                                        v-model="formItem.endDate"
                                        :placeholder="$t('未设置')"
                                        placement="bottom-start" />
                                    <span v-show="checkFields.indexOf('endDate') === -1" class="placeholder-label">
                                        {{$t('不更新')}}
                                    </span>
                                </FormItem>

                                <FormItem
                                    v-if="item.field=='subSystemName'" :label="item.name">
                                    <TaskObjectSelect
                                        v-show="checkFields.indexOf('subSystemName') > -1"
                                        :nodata="true"
                                        :object-list="editInfo.subSystemList"
                                        v-model="formItem.subSystemId"
                                        placement="bottom-start" />
                                    <span
                                        v-show="checkFields.indexOf('subSystemName') === -1" class="placeholder-label">
                                        {{$t('不更新')}}
                                    </span>
                                </FormItem>

                                <FormItem
                                    v-if="item.field=='releaseName'" :label="item.name">
                                    <TaskObjectSelect
                                        v-show="checkFields.indexOf('releaseName') > -1"
                                        :nodata="true"
                                        :object-list="editInfo.releaseList"
                                        v-model="formItem.releaseId"
                                        placement="bottom-start" />
                                    <span v-show="checkFields.indexOf('releaseName') === -1" class="placeholder-label">
                                        {{$t('不更新')}}
                                    </span>
                                </FormItem>

                                <FormItem
                                    v-if="item.field=='repositoryName'" :label="item.name">
                                    <TaskObjectSelect
                                        v-show="checkFields.indexOf('repositoryName') > -1"
                                        :nodata="true"
                                        :object-list="editInfo.repositoryList"
                                        v-model="formItem.repositoryId"
                                        placement="bottom-start" />
                                    <span v-show="checkFields.indexOf('repositoryName') === -1" class="placeholder-label">
                                        {{$t('不更新')}}
                                    </span>
                                </FormItem>

                                <FormItem
                                        v-if="item.field=='workLoad'" :label="item.name">
                                    <input
                                            v-show="checkFields.indexOf('workLoad') > -1"
                                            type="number"
                                            :placeholder="$t('未设置')"
                                            v-model.number="formItem.workLoad" />
                                    <span
                                            v-show="checkFields.indexOf('workLoad') === -1" class="placeholder-label">
                                        {{$t('不更新')}}
                                    </span>
                                </FormItem>
                                <FormItem
                                    v-if="item.field=='expectWorkTime'" :label="item.name">
                                    <input
                                        v-show="checkFields.indexOf('expectWorkTime') > -1"
                                        type="number"
                                        :placeholder="$t('未设置')"
                                        v-model.number="formItem.expectWorkTime" />
                                    <span
                                        v-show="checkFields.indexOf('expectWorkTime') === -1" class="placeholder-label">
                                        {{$t('不更新')}}
                                    </span>
                                </FormItem>

                                <FormItem
                                    v-if="item.field=='progress'" :label="item.name">
                                    <input
                                        v-show="checkFields.indexOf('progress') > -1"
                                        type="number"
                                        min="0"
                                        max="100"
                                        :placeholder="$t('未设置')"
                                        v-model.number="formItem.progress" />
                                    <span v-show="checkFields.indexOf('progress') === -1" class="placeholder-label">
                                        {{$t('不更新')}}
                                    </span>
                                </FormItem>

                                <FormItem
                                    v-if="item.field=='finishTime'" :label="item.name">
                                    <DateSelect
                                        v-show="checkFields.indexOf('finishTime') > -1"
                                        :options="endDateOptions"
                                        :showTimeField = "true"
                                        v-model="formItem.finishTime"
                                        :placeholder="$t('未设置')"
                                        placement="bottom-start" />
                                    <span v-show="checkFields.indexOf('finishTime') === -1" class="placeholder-label">
                                        {{$t('不更新')}}
                                    </span>
                                </FormItem>

                                <FormItem
                                    v-if="item.field=='expectEndDate'" :label="item.name">
                                    <DateSelect
                                        v-show="checkFields.indexOf('expectEndDate') > -1"
                                        v-model="formItem.expectEndDate"
                                        :placeholder="$t('未设置')"
                                        placement="bottom-start" />
                                    <span v-show="checkFields.indexOf('expectEndDate') === -1" class="placeholder-label">
                                        {{$t('不更新')}}
                                    </span>
                                </FormItem>

                                <FormItem
                                    v-if="item.field=='stageName'" :label="item.name">
                                        <TaskViewStageSelect
                                            v-show="checkFields.indexOf('stageName') > -1"
                                            :list="editInfo.stageList"
                                            v-model="formItem.stageId"
                                            :editable="true"
                                            placement="bottom" />
                                        <span v-show="checkFields.indexOf('stageName') === -1" class="placeholder-label">
                                            {{$t('不更新')}}
                                        </span>
                                </FormItem>


                            </template>
                            <template v-if="item.isSystemField==false">
                                <Checkbox
                                    class="task-edit-form-checkbox" :label="'customFields.field_'+item.id" />
                                <FormItem
                                    :label="item.name" style="padding-right:20px" :prop="'customFields.field_'+item.id">
                                    <div
                                        v-show="checkFields.indexOf('customFields.field_'+item.id) > -1">
                                        <Input
                                            type="textarea"
                                            autoresize
                                            :maxlength="500"
                                            v-if="item.type==1"
                                            v-model.trim="formItem.customFields['field_'+item.id]"
                                            :placeholder="$t('未设置')" />
                                        <Input
                                            type="number"
                                            style="width:100%"
                                            v-if="item.type==8"
                                            v-model.number="formItem.customFields['field_'+item.id]"
                                            :placeholder="$t('未设置')" />
                                        <DateSelect
                                            type="date"
                                            :showTimeField="item.showTimeField"
                                            v-if="item.type==7"
                                            v-model="formItem.customFields['field_'+item.id]"
                                            :placeholder="$t('未设置')" />
                                        <TaskCustomSelect
                                            :nodata="true"
                                            :multiple="true"
                                            v-if="item.type==3"
                                            v-model="formItem.customFields['field_'+item.id]"
                                            :object-list="item.valueRange"
                                            placement="bottom-start" />
                                        <TaskCustomSelect
                                            :nodata="true"
                                            :multiple="false"
                                            v-if="item.type==4"
                                            v-model="formItem.customFields['field_'+item.id]"
                                            :object-list="item.valueRange"
                                            placement="bottom-start" />
                                        <MemberSelect
                                            :placeholder="$t('未设置')"
                                            v-if="item.type==6"
                                            :member-list="editInfo.memberList"
                                            :multiple="true"
                                            v-model="formItem.customFields['field_'+item.id]"
                                            placement="bottom-start" />
                                    </div>
                                    <span
                                        v-show="checkFields.indexOf('customFields.field_'+item.id) === -1"
                                        class="placeholder-label">
                                        {{$t('不更新')}}
                                    </span>
                                </FormItem>
                            </template>

                        </div>
                    </template>
                </div>
            </CheckboxGroup>
        </Form>
        <div slot="footer">
            <Row>
                <Col span="24" style="text-align:right">
                    <Button @click="confirm" type="default" size="large" :disabled="noUpdate">
                        {{$t('保存')}}
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
                editInfo: {},
                formItem: {
                    ownerAccountIdList: null,
                    customFields: {},
                    startDate: null,
                    endDate: null,
                },
                keyToField: {
                    subSystemName: 'subSystemId',
                    releaseName: 'releaseId',
                    stageName: 'stageId',
                    repositoryName: 'repositoryId',
                },
                updateFields: [],
                checkFields: [],
                itemList: [],
                formRule: {},
                startDateOptions: {
                    disabledDate: (date) => {
                        if (this.formItem.endDate == null) {
                            return false;
                        }
                        return date && date.valueOf() > this.formItem.endDate;
                    },
                },
                endDateOptions: {
                    disabledDate: (date) => {
                        if (this.formItem.startDate == null) {
                            return false;
                        }
                        var t = new Date(this.formItem.startDate);
                        t.setHours(0, 0, 0, 0);
                        return date && date.valueOf() < t.getTime();
                    },
                },
            };
        },
        computed: {
            typeName: function () {
                return app.dataDictValue('Task.objectType', this.args.objectType);
            },
            noUpdate() {
                return !Array.isArray(this.checkFields) || this.checkFields.length === 0;
            },
        },
        methods: {
            pageLoad() {
                this.formItem.projectId = this.args.projectId;
                this.formItem.objectType = this.args.objectType;
                this.itemList = this.args.itemList;
                this.updateFields = [];
                this.loadEditInfo();
            },
            isSystemOtherField(item) {
                var excludesList = [
                    'workTime',
                    'startDays',
                    'endDays',
                    'iterationName',
                    'categoryIdList',
                    'name',
                    'ownerAccountName',
                    'createAccountName',
                    'priorityName',
                    'statusName',
                    'createTime',
                    'updateTime',
                ];
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
                        if (f.field === name) {
                            return true;
                        }
                    }
                }
                return false;
            },
            loadEditInfo() {
                app.invoke('BizAction.getEditTaskInfo', [app.token, this.args.projectId, this.args.objectType], info => {
                    this.editInfo = info;
                });
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
            confirm() {
                var memberSelect = this.$refs.memberSelect;
                if (memberSelect) {
                    memberSelect.hideView();
                }
                this.confirmForm();
            },
            confirmForm() {
                const updateFields = [];
                this.checkFields.forEach(checkField => {
                    checkField = this.compKeyToField(checkField);
                    if (checkField.indexOf('customFields') === 0) {
                        const field = checkField.split('.')[1];
                        if (!field) {
                            return;
                        }
                        updateFields.push(field);
                        return;
                    }
                    updateFields.push(checkField);
                });
                this.updateFields = updateFields;
                app.showDialog(MultiOperateDialog, {
                    title: this.$t('编辑') + this.typeName,
                    runCallback: this.editAction,
                    finishCallback: this.finishRun,
                    itemList: this.itemList,
                });
            },
            compKeyToField(key) {
                return this.keyToField[key] || key;
            },
            finishRun() {
                app.postMessage('task.edit');
                this.showDialog = false;
            },
            editAction(item, success, error) {
                app.invoke('BizAction.batchUpdateTask', [app.token, this.formItem, [item.id], this.updateFields], (info) => {
                    success();
                }, error);
            },
        },
    };
</script>
