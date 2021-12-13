<style lang="less" scoped>

    .placeholder-label {
        font-size: 13px;
        vertical-align: middle;
        min-width: 50px;
    }
    .old-value{
        text-decoration: line-through;
    }
    .new-value{
        font-weight: 600;
        color: red;
    }

    .new-value:before{
        content: " -> ";
        color: #0a001f;
        margin-right: 10px;
    }

    .other-field {
        border-top: 1px solid #eee;
        padding-top: 20px;
    }

    .other-field-item {
        display: inline-block;
        width: 308px;
        height: 80px;
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
            margin: 5px 0;

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

            /deep/ .ivu-checkbox-disabled {
                display: none !important;
            }
        }

        .task-edit-form-checkbox /deep/ span.ivu-checkbox {
            display: block;

            & ~ span {
                display: none;
            }
        }
    }


    .edit-task-name {
        color: #444;
        font-weight: bold;
    }

    .edit-task-name-finish textarea {
        color: gray !important;
        text-decoration: line-through;
    }

    .edit-task-name textarea {
        width: 100%;
        border: none;
        outline: 0;
        font-size: 16px !important;
        padding: 0 !important;
        padding-top: 3px !important;
        resize: none !important;
        color: #333;
        font-weight: bold;
        border-radius: 5px !important;
    }

    .edit-task-name textarea:hover {
        background-color: #ddecf9;
        cursor: pointer;
    }

    .edit-task-name textarea:focus {
        background-color: #ddecf9;
    }

    .edit-task-name textarea:disabled {
        color: #333;
        opacity: 1;
        background-color: transparent !important;
    }

    .edit-task-name textarea:disabled:hover {
        background-color: none;
        cursor: default;
    }


</style>
<i18n>
    {
    "en": {
    "编辑": "Edit",
    "不更新": "No update",
    "正在编辑":"Editing {0} {1}",
    "请设置需要申请变更的属性":"Set properties that need to carry out alteration.",
    "责任人":"Owner",
    "待认领":"none",
    "状态":"Status",
    "优先级":"Priority",
    "分类":"Category",
    "项目":"Project",
    "迭代":"Iteration",
    "未设置":"none",
    "申请变更":"Apply alteration",
    "提交":"Submit"
    },
    "zh_CN": {
    "编辑": "编辑",
    "不更新": "不更新",
    "正在编辑":"正在编辑{0}个{1}",
    "请设置需要申请变更的属性":"请勾选要申请变更的字段，有勾选框的字段表示可以申请变更。申请提交成功后当前任务将会被冻结无法进行编辑操作",
    "责任人":"责任人",
    "待认领":"待认领",
    "状态":"状态",
    "优先级":"优先级",
    "分类":"分类",
    "项目":"项目",
    "迭代":"迭代",
    "未设置":"未设置",
    "申请变更":"申请变更",
    "提交":"提交"
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
            :title="$t('申请变更')+typeName"
            width="700">
        <Alert>
            {{$t('请设置需要申请变更的属性')}}
        </Alert>
        <Form
                ref="form" class="task-edit-form" :model="formItem" :rules="formRule"
                label-position="top" style="height:600px;padding:15px">
            <CheckboxGroup v-model="checkFields">
                <Row>
                    <Col span="10">
                        <FormItem :label="$t('项目')">
                            {{task.projectName}}
                        </FormItem>
                    </Col>
                    <Col span="10">
                        <FormItem  :label="getLabelName('name','名称')">
                            {{task.name}}
                        </FormItem>
                    </Col>
                    <Col span="4">
                        <FormItem :label="getLabelName('statusName','状态')" >
                            <TaskStatusSelect
                                    :disabled="true"
                                    :status-list="editInfo.statusList"
                                    v-model="task.status"
                                    placement="bottom-start"/>
                        </FormItem>
                    </Col>
                </Row>

                <Row class="other-field">
                    <Col
                            span="12" v-if="hasField('iterationName')" class="task-edit-form-field">
                        <Checkbox :disabled="disableField('iterationName')"
                                  class="task-edit-form-checkbox" label="iterationId"/>
                        <FormItem :label="getLabelName('iterationName','迭代')">
                            <div class="placeholder-label">
                                {{task.iterationName||'未设置'}}
                            </div>
                            <TaskObjectSelect
                                    v-if="checkFields.indexOf('iterationId') > -1"
                                    :nodata="true"
                                    :object-list="editInfo.iterationList"
                                    v-model="formItem.iterationId"
                                    placement="bottom-start"/>
                        </FormItem>
                    </Col>
                    <Col
                            span="12" v-if="hasField('ownerAccountName')" class="task-edit-form-field">
                        <Checkbox :disabled="disableField('ownerAccountName')"
                                  class="task-edit-form-checkbox" label="ownerAccountIdList"/>
                        <FormItem :label="getLabelName('ownerAccountName','责任人')">
                            <div class="placeholder-label">
                                <AvatarImage
                                        v-for="account in task.ownerAccountList"
                                        :key="'member_'+account.id"
                                        size="small"
                                        :title="account.name"
                                        :value="account.imageId"
                                        :name="account.name"/>
                            </div>
                            <MemberSelect
                                    v-if="checkFields.indexOf('ownerAccountIdList') > -1"
                                    ref="memberSelect"
                                    :placeholder="$t('待认领')"
                                    :multiple="true"
                                    :member-list="editInfo.memberList"
                                    v-model="formItem.ownerAccountIdList"
                                    placement="bottom-start"/>

                        </FormItem>
                    </Col>
                </Row>

                <Row class="other-field edit-task-name">

                    <Col
                            span="12" v-if="hasField('priorityName')" class="task-edit-form-field">
                        <Checkbox :disabled="disableField('priorityName')"
                                  class="task-edit-form-checkbox" label="priority"/>
                        <FormItem :label="getLabelName('priorityName','优先级')" >
                            <TaskPrioritySelect
                                    v-if="hasField('priorityName')"
                                    :disabled="true"
                                    :priority-list="editInfo.priorityList"
                                    v-model="task.priority"
                                    placement="bottom"></TaskPrioritySelect>
                            <br>
                            <TaskPrioritySelect
                                    v-if="checkFields.indexOf('priority') > -1"
                                    :priority-list="editInfo.priorityList"
                                    v-model="formItem.priority"
                                    placement="bottom-start"/>
                        </FormItem>
                    </Col>
                    <Col
                            span="12" class="task-edit-form-field">
                        <Checkbox :disabled="disableField('categoryIdList')"
                                  class="task-edit-form-checkbox" label="categoryIdList"/>
                        <FormItem :label="getLabelName('categoryIdList','分类')" >
                            <CategorySelect
                                    :disabled="true"
                                    :category-list="editInfo.categoryNodeList"
                                    v-model="task.categoryIdList"
                                    placement="bottom"/>
                            <br>
                            <CategorySelect
                                    v-if="checkFields.indexOf('categoryIdList') > -1"
                                    :category-list="editInfo.categoryNodeList"
                                    v-model="formItem.categoryIdList"
                                    placement="bottom"/>
                        </FormItem>
                    </Col>

                </Row>

                <div class="other-field">
                    <template v-for="item in editInfo.fieldList">
                        <div
                                class="task-edit-form-field other-field-item edit-task-name"
                                :class="{'full-field-item':item.type==1&&item.isSystemField==false}"
                                :key="item.id"
                                v-if="isSystemOtherField(item)">
                            <template v-if="item.isSystemField">
                                <Checkbox :disabled="disableField(item.field)"
                                          class="task-edit-form-checkbox" :label="item.field"/>
                                <FormItem
                                        v-if="item.field=='startDate'" :label="item.name">
                                    <div class="placeholder-label">{{task.startDate|fmtDate}}</div>
                                    <DateSelect
                                            v-if="checkFields.indexOf('startDate') > -1"
                                            :options="startDateOptions"
                                            type="date"
                                            v-model="formItem.startDate"
                                            :placeholder="$t('未设置')"
                                            placement="bottom-start"/>
                                </FormItem>
                                <FormItem
                                        v-if="item.field=='endDate'" :label="item.name">
                                    <div class="placeholder-label"> {{task.endDate|fmtDate}}</div>
                                    <DateSelect
                                            v-if="checkFields.indexOf('endDate') > -1"
                                            :options="endDateOptions"
                                            type="date"
                                            v-model="formItem.endDate"
                                            :placeholder="$t('未设置')"
                                            placement="bottom-start"/>
                                </FormItem>

                                <FormItem
                                        v-if="item.field=='subSystemName'" :label="item.name">
                                    <div class="placeholder-label">{{task.subSystemName||'未设置'}}</div>
                                    <TaskObjectSelect
                                            v-if="checkFields.indexOf('subSystemName') > -1"
                                            :nodata="true"
                                            :object-list="editInfo.subSystemList"
                                            v-model="formItem.subSystemId"
                                            placement="bottom-start"/>
                                </FormItem>

                                <FormItem
                                    v-if="item.field=='releaseName'" :label="item.name">
                                    <div class="placeholder-label">{{task.releaseName||'未设置'}}</div>
                                    <TaskObjectSelect
                                        v-if="checkFields.indexOf('releaseName') > -1"
                                        :nodata="true"
                                        :object-list="editInfo.releaseList"
                                        v-model="formItem.releaseId"
                                        placement="bottom-start"/>
                                </FormItem>

                                <FormItem
                                    v-if="item.field=='repositoryName'" :label="item.name">
                                    <div class="placeholder-label">{{task.repositoryName||'未设置'}}</div>
                                    <TaskObjectSelect
                                        v-if="checkFields.indexOf('repositoryName') > -1"
                                        :nodata="true"
                                        :object-list="editInfo.repositoryList"
                                        v-model="formItem.repositoryId"
                                        placement="bottom-start"/>
                                </FormItem>

                                <FormItem
                                        v-if="item.field=='workLoad'" :label="item.name">
                                    <div class="placeholder-label">{{task.workLoad||0}}</div>
                                    <Input
                                            class="edit-task-name"
                                            v-if="checkFields.indexOf('workLoad') > -1"
                                            type="number"
                                            min="0"
                                            max="100"
                                            :placeholder="$t('未设置')"
                                            v-model.number="formItem.workLoad"/>
                                </FormItem>
                                <FormItem
                                        v-if="item.field=='expectWorkTime'" :label="item.name">
                                    <div class="placeholder-label">{{task.expectWorkTime}}</div>
                                    <Input
                                            v-if="checkFields.indexOf('expectWorkTime') > -1"
                                            type="number"
                                            :placeholder="$t('未设置')"
                                            v-model.number="formItem.expectWorkTime"/>
                                </FormItem>

                                <FormItem
                                        v-if="item.field=='progress'" :label="item.name">
                                    <div class="placeholder-label">{{task.progress||0}}%</div>
                                    <Input
                                            class="edit-task-name"
                                            v-if="checkFields.indexOf('progress') > -1"
                                            type="number"
                                            min="0"
                                            max="100"
                                            :placeholder="$t('未设置')"
                                            v-model.number="formItem.progress"/>
                                </FormItem>

                                <FormItem
                                        v-if="item.field=='finishTime'" :label="item.name">
                                    <div class="placeholder-label">{{task.finishTime|fmtDateTime}}</div>
                                    <DateSelect
                                            v-if="checkFields.indexOf('finishTime') > -1"
                                            :options="endDateOptions"
                                            :showTimeField="true"
                                            v-model="formItem.finishTime"
                                            :placeholder="$t('未设置')"
                                            placement="bottom-start"/>
                                </FormItem>

                                <FormItem
                                        v-if="item.field=='expectEndDate'" :label="item.name">
                                    <div class="placeholder-label">{{task.expectEndDate|fmtDate}}</div>
                                    <DateSelect
                                            v-if="checkFields.indexOf('expectEndDate') > -1"
                                            v-model="formItem.expectEndDate"
                                            :placeholder="$t('未设置')"
                                            placement="bottom-start"/>
                                </FormItem>

                                <FormItem
                                        v-if="item.field=='stageName'" :label="item.name">
                                    <div class="placeholder-label">{{task.stageName||'未设置'}}</div>
                                    <TaskViewStageSelect
                                            v-if="checkFields.indexOf('stageName') > -1"
                                            :list="editInfo.stageList"
                                            v-model="formItem.stageId"
                                            :editable="true"
                                            placement="bottom"/>
                                </FormItem>


                            </template>
                            <template v-if="item.isSystemField==false">
                                <Checkbox :disabled="disableField(item.field)"
                                          class="task-edit-form-checkbox" :label="'customFields.field_'+item.id"/>
                                <FormItem
                                        :label="item.name" style="padding-right:20px" :prop="'customFields.field_'+item.id">
                                    <div class="placeholder-label">
                                        <div v-if="item.type==1||item.type==3||item.type==4||item.type==8">{{task.customFields['field_'+item.id]||'未设置'}}</div>
                                        <div v-if="item.type==7">{{task.customFields['field_'+item.id]|fmtDate}}</div>
                                        <div v-if="item.type==6">
                                            <AvatarImage
                                                    v-for="(account,idx) in task.customFields['field_'+item.id]"
                                                    :key="'member_'+idx"
                                                    size="small"
                                                    :title="account.name"
                                                    :value="account.imageId"
                                                    :name="account.name"/>
                                        </div>
                                    </div>
                                    <div
                                         v-if="checkFields.indexOf('customFields.field_'+item.id) > -1">
                                        <Input
                                                class="edit-task-name"
                                                type="textarea"
                                                autoresize
                                                :maxlength="500"
                                                v-if="item.type==1"
                                                v-model.trim="formItem.customFields['field_'+item.id]"
                                                :placeholder="$t('未设置')"/>
                                        <Input
                                                class="edit-task-name"
                                                type="number"
                                                style="width:100%"
                                                v-if="item.type==8"
                                                v-model.number="formItem.customFields['field_'+item.id]"
                                                :placeholder="$t('未设置')"/>
                                        <DateSelect
                                                type="date"
                                                :showTimeField="item.showTimeField"
                                                v-if="item.type==7"
                                                v-model="formItem.customFields['field_'+item.id]"
                                                :placeholder="$t('未设置')"/>
                                        <TaskCustomSelect
                                                :nodata="true"
                                                :multiple="true"
                                                v-if="item.type==3"
                                                v-model="formItem.customFields['field_'+item.id]"
                                                :object-list="item.valueRange"
                                                placement="bottom-start"/>
                                        <TaskCustomSelect
                                                :nodata="true"
                                                :multiple="false"
                                                v-if="item.type==4"
                                                v-model="formItem.customFields['field_'+item.id]"
                                                :object-list="item.valueRange"
                                                placement="bottom-start"/>
                                        <MemberSelect
                                                :placeholder="$t('未设置')"
                                                v-if="item.type==6"
                                                :member-list="editInfo.memberList"
                                                :multiple="true"
                                                v-model="formItem.customFields['field_'+item.id]"
                                                placement="bottom-start"/>
                                    </div>
                                </FormItem>
                            </template>

                        </div>
                    </template>
                </div>
            </CheckboxGroup>
            <FormItem label="变更原因" prop="reason" style="padding: 15px 0;">
                <Input v-model="formItem.reason" type="textarea" :rows="2" placeholder="请填写变更原因"/>
            </FormItem>
        </Form>
        <div slot="footer">
            <Row>
                <Col span="24" style="text-align:right">
                    <Button @click="confirm" type="default" size="large" :disabled="noUpdate">
                        {{$t('提交')}}
                    </Button>
                </Col>
            </Row>
        </div>
    </Modal>
</template>


<script>
    export default {
        name: 'TaskAlterationCreateDialog',
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
                    reason: null
                },
                keyToField: {
                    "iterationName": "iterationId",
                    "ownerAccountName": "ownerAccountIdList",
                    "statusName": "status",
                    "priorityName": "priority",
                    "subSystemName": "subSystemId",
                    "releaseName": "releaseId",
                    "repositoryName": "repositoryId",
                    "stageName": "stageId",
                },
                updateFields: [],
                checkFields: [],
                alterationFields: [],
                editableFields: [],
                formRule: {
                    reason: [vd.req]
                },
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
                task: {},
                valuemap: {
                    "iterationName": "iterationId",
                    "ownerAccountName": "ownerAccountIdList",
                    "statusName": "status",
                    "priorityName": "priority",
                    "subSystemName": "subSystemId",
                    "releaseName": "releaseId",
                    "stageName": "stageId",
                }

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
        filters: {
            fmtOldValue(field) {

            }
        },
        methods: {
            pageLoad() {
                this.formItem.projectId = this.args.projectId;
                this.formItem.objectType = this.args.objectType;
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
                app.invoke("BizAction.getTaskEditableFieldList", [app.token, this.args.taskId, this.args.objectType], info => {
                    this.editableFields = info;
                });
                app.invoke('BizAction.getTaskInfoByUuid', [app.token, this.args.uuid], info => {
                    this.editInfo = info.editTaskInfo;
                    this.task = info.task;
                });
            },
            // field type name oldVlaue newValue
            confirm() {
                if (!this.formItem.reason) {
                    app.toast("请填写变更原因");
                    return;
                }
                const updateFields = [];
                console.log(this.checkFields, this.formItem);
                let fm = new Map();
                this.editableFields.forEach(f => {
                    fm.set(this.compKeyToField(f.field), f)
                })
                console.log(fm)
                this.checkFields.forEach(checkField => {
                    let field = this.compKeyToField(checkField);
                    if (field.indexOf('customFields') === 0) {
                        field = field.split('.')[1];
                    }

                    let oldValue = this.task[field] || this.task.customFields[field];
                    let newValue = this.formItem[field] || this.formItem.customFields[field];
                    if(field.substring(field.length-2)==='Id'&&(!newValue||newValue<=0)){
                        newValue=0;
                    }
                    let type = fm.get(field).type;
                    if(type==3){
                        if(!!this.editInfo.fieldList){
                            let fieldValue = this.editInfo.fieldList.filter(item=>item.field==field)[0].valueRange;
                            if(!!fieldValue){
                                newValue = fieldValue.filter(v=>newValue.contains(v));
                            }else{
                                newValue=[];
                            }
                        }else{
                            newValue=null;
                        }
                    }else if(type==4){
                        if(!!this.editInfo.fieldList){
                            let fieldValue = this.editInfo.fieldList.filter(item=>item.field==field)[0].valueRange;
                            if(!!fieldValue){
                                newValue = fieldValue.filter(v=>newValue==v)[0];
                            }else{
                                newValue=null;
                            }
                        }else{
                            newValue=null;
                        }
                    }

                    console.log(checkField, field, oldValue, newValue, fm.get(field))
                    updateFields.push({
                        field: field,
                        type: fm.get(field).type,
                        name: fm.get(field).name,
                        oldValue: oldValue,
                        newValue: newValue,
                        oldShowValue: this.getShowValue(field, oldValue, fm.get(field).type,),
                        newShowValue: this.getShowValue(field, newValue, fm.get(field).type,)
                    });
                });
                this.updateFields = updateFields;
                console.log(this.updateFields)
                app.confirm('确认提交该变更申请吗?提交成功后任务将被冻结无法编辑', () => {
                    let altertion = {
                        taskId: this.task.id,
                        fields: updateFields,
                        reason: this.formItem.reason
                    };
                    app.invoke("TaskAlterationAction.addTaskAlteration", [app.token, altertion], info => {
                        app.toast("提交成功");
                        app.postMessage("task.freeze");
                        this.showDialog = false;
                    });
                }, () => {
                }, '是', '否');
            },
            getShowValue(field, value, type) {
                if (!value) {
                    return null;
                }
                if (field === 'priority') {
                    if (!!this.editInfo.priorityList) {
                        let val= this.editInfo.priorityList.find(item => item.id == value);
                        if(!!val){
                            return val.name;
                        }else{
                            return null;
                        }
                    }
                }
                if (field === 'iterationId') {
                    if (!!this.editInfo.iterationList) {
                        let val= this.editInfo.iterationList.find(item => item.id == value);
                        if(!!val){
                            return val.name;
                        }else{
                            return null;
                        }
                    }
                }
                if (field === 'releaseId') {
                    if (!!this.editInfo.releaseList) {
                        let val= this.editInfo.releaseList.find(item => item.id == value);
                        if(!!val){
                            return val.name;
                        }else{
                            return null;
                        }
                    }
                }
                if (field === 'stageId') {
                    if (!!this.editInfo.stageList) {
                        let val= this.editInfo.stageList.find(item => item.id == value);
                        if(!!val){
                            return val.name;
                        }else{
                            return null;
                        }
                    }
                }
                if (field === 'releaseId') {
                    if (!!this.editInfo.releaseList) {
                        let val= this.editInfo.releaseList.find(item => item.id == value);
                        if(!!val){
                            return val.name;
                        }else{
                            return null;
                        }
                    }
                }
                if (field === 'repositoryId') {
                    if (!!this.editInfo.repositoryList) {
                        let val= this.editInfo.repositoryList.find(item => item.id == value);
                        if(!!val){
                            return val.name;
                        }else{
                            return null;
                        }
                    }
                }
                if (field === 'subSystemId') {
                    if (!!this.editInfo.subSystemList) {
                        let val= this.editInfo.subSystemList.find(item => item.id == value);
                        if(!!val){
                            return val.name;
                        }else{
                            return null;
                        }
                    }
                }
                if (field === 'ownerAccountIdList') {
                    if (!!this.editInfo.memberList) {
                        return this.editInfo.memberList.filter(acc => Array.contains(value, acc.accountId, a => a)).map(item => item.accountName).join("、");
                    }
                }
                if (field === 'categoryIdList') {
                    if (!!this.editInfo.categoryNodeList) {
                        let categoryNames = [];
                        this.recurivseCategory(this.task.categoryIdList, this.editInfo.categoryNodeList, categoryNames)
                        return categoryNames.join("、");
                    }
                }
                if (field.indexOf("field_") === 0) {
                    if (type == 6) {
                        if (!!this.editInfo.memberList) {
                            return this.editInfo.memberList.filter(acc => Array.contains(value, acc.accountId, a => a)).map(item => item.accountName).join("、");
                        }
                    } else if (type == 7) {
                        if (!!value) {
                            return formatDatetime(value);
                        }
                    }else if(type==3){
                        if(!!this.editInfo.fieldList){
                            let fieldValue = this.editInfo.fieldList.filter(item=>item.field==field)[0].valueRange;
                            if(!!fieldValue){
                                return fieldValue.filter(v=>value.contains(v));
                            }
                        }
                        return [];
                    }else if(type==4){
                        if(!!this.editInfo.fieldList){
                            let fieldValue = this.editInfo.fieldList.filter(item=>item.field==field)[0].valueRange;
                            if(!!fieldValue){
                                return fieldValue.filter(v=>value==v)[0];
                            }
                        }
                        return null;
                    }

                }
                return value;
            },
            compKeyToField(key) {
                return this.keyToField[key] || key;
            },
            disableField(field) {
                let nop = true;
                for (let i = 0; i < this.editableFields.length; i++) {
                    if (this.editableFields[i].field === field) {
                        nop = this.editableFields[i].editable;
                        break;
                    }
                }
                return nop;
            },
            recurivseCategory(idList, categoyList, names) {
                if (!!categoyList) {
                    categoyList.forEach(item => {
                        if (Array.contains(idList, item.id, (a) => a)) {
                            names.push(item.name);
                        }
                        if (!!item.children && !Array.isEmpty(item.children)) {
                            this.recurivseCategory(idList, item.children, names);
                        }
                    })
                }
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
        },
    };
</script>
