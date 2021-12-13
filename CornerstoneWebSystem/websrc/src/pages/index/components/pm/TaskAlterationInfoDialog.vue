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
        background-color: #efefef;
    }

    .other-field-item {
        display: inline-block;
        width: 314px;
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

</style>
<i18n>
    {
    "en": {
    "编辑": "Edit",
    "名称": "名称",
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
    "变更详情":"Apply alteration Detail",
    "关闭":"Close",
    "确认审批":"Confirm",
    "保存":"Save",
    "撤销":"Cancel"
    },
    "zh_CN": {
    "编辑": "编辑",
    "名称": "名称",
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
    "变更详情":"变更详情",
    "关闭":"关闭",
    "确认审批":"确认审批",
    "保存":"保存",
    "撤销":"撤销"
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
        :title="$t('变更详情')"
        width="700">
        <Form
            ref="form" class="task-edit-form" :model="formItem"
            label-position="top"
            style="height:600px;padding:15px">

            <Row>
                <Col span="8">
                    <FormItem :label="$t('项目')">
                        {{task.projectName}}
                    </FormItem>
                </Col>
                <Col span="8">
                    <FormItem :label="getLabelName('name','名称')">
                        {{task.name}}
                    </FormItem>
                </Col>
                <Col span="4">
                    <FormItem :label="getLabelName('statusName','状态')">
                        <span
                            :style="{color:task.statusColor}">{{task.statusName}}</span>
                    </FormItem>
                </Col>
                <Col span="4">
                    <TaskAlterationStatusSelect
                        @change="changeFlowStatus"
                        :disabled="statusDisable||projectFinishDisabled"
                        :status-list="flowStatusList"
                        v-model="alteration.flowStatus"
                        placement="bottom-end">
                    </TaskAlterationStatusSelect>
                </Col>
            </Row>

            <Row class="other-field">
                <Col
                    span="12" v-if="hasField('iterationName')" class="task-edit-form-field">
                    <FormItem :label="getLabelName('iterationName','迭代')">
                        <span class="placeholder-label" :class="{'old-value':hasUpdate('iterationId')}">
                            {{task.iterationName||'未设置'}}
                        </span>
                        <span
                            :class="{'new-value':hasUpdate('iterationId')}">{{alterationValue('iterationName')}}</span>
                    </FormItem>
                </Col>
                <Col
                    span="12" v-if="hasField('ownerAccountName')" class="task-edit-form-field">
                    <FormItem :label="getLabelName('ownerAccountName','责任人')">
                        <span class="placeholder-label" :class="{'old-value':hasUpdate('ownerAccountIdList')}">
                            <span v-for="account in task.ownerAccountList"
                                  :key="'member_'+account.id">{{account.name}} </span>
                        </span>
                        <span :class="{'new-value':hasUpdate('ownerAccountIdList')}">{{alterationValue('ownerAccountName')}}</span>
                    </FormItem>
                </Col>
            </Row>

            <Row class="other-field">
                <Col
                    span="12" v-if="hasField('priorityName')" class="task-edit-form-field">
                    <FormItem :label="getLabelName('priorityName','优先级')" >
                        <span :class="{'old-value':hasUpdate('priority')}" :style="{color:task.priorityColor}">{{task.priorityName}}</span>
                        <br>
                        <span :class="{'new-value':hasUpdate('priority')}">{{alterationValue('priorityName')}}</span>
                    </FormItem>
                </Col>
                <Col
                    span="12" class="task-edit-form-field">
                    <FormItem :label="getLabelName('categoryIdList','分类')">
                        <span :class="{'old-value':hasUpdate('categoryIdList')}"
                              v-html="alterationOldValue('categoryIdList')"></span>
                        <span
                            :class="{'new-value':hasUpdate('categoryIdList')}">{{alterationValue('categoryIdList')}}</span>
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
                            <FormItem
                                v-if="item.field=='startDate'" :label="item.name">
                                <span class="placeholder-label" :class="{'old-value':hasUpdate('startDate')}">{{task.startDate|fmtDate}}</span>
                                <span v-if="hasUpdate('startDate')" :class="{'new-value':hasUpdate('startDate')}">{{alterationValue('startDate')|fmtDate}}</span>
                            </FormItem>
                            <FormItem
                                v-if="item.field=='endDate'" :label="item.name">
                                <span class="placeholder-label" :class="{'old-value':hasUpdate('endDate')}"> {{task.endDate|fmtDate}}</span>
                                <span v-if="hasUpdate('endDate')"
                                    :class="{'new-value':hasUpdate('endDate')}">{{alterationValue('endDate')|fmtDate}}</span>
                            </FormItem>

                            <FormItem
                                v-if="item.field=='subSystemName'" :label="item.name">
                                <span class="placeholder-label" :class="{'old-value':hasUpdate('subSystemId')}">{{task.subSystemName||'未设置'}}</span>
                                <span :class="{'new-value':hasUpdate('subSystemId')}">
                                    {{alterationValue('subSystemName')}}
                                </span>
                            </FormItem>

                            <FormItem
                                v-if="item.field=='releaseName'" :label="item.name">
                                <span class="placeholder-label" :class="{'old-value':hasUpdate('releaseId')}">{{task.releaseName||'未设置'}}</span>
                                <span :class="{'new-value':hasUpdate('releaseId')}">{{alterationValue('releaseName')}}
                                </span>
                            </FormItem>

                            <FormItem
                                v-if="item.field=='repositoryName'" :label="item.name">
                                <span class="placeholder-label" :class="{'old-value':hasUpdate('repositoryId')}">{{task.repositoryName||'未设置'}}</span>
                                <span :class="{'new-value':hasUpdate('repositoryId')}">{{alterationValue('repositoryName')}}
                                </span>
                            </FormItem>

                            <FormItem
                                v-if="item.field=='expectWorkTime'" :label="item.name">
                                <span class="placeholder-label" :class="{'old-value':hasUpdate('expectWorkTime')}">{{task.expectWorkTime}}</span>
                                <span :class="{'new-value':hasUpdate('expectWorkTime')}">
                                    {{alterationValue('expectWorkTime')}}
                                </span>
                            </FormItem>
                            <FormItem
                                    v-if="item.field=='workLoad'" :label="item.name">
                                <span class="placeholder-label" :class="{'old-value':hasUpdate('workLoad')}">{{task.workLoad||0}}</span>
                                <span :class="{'new-value':hasUpdate('workLoad')}">{{alterationValue('workLoad')}}
                                </span>
                            </FormItem>
                            <FormItem
                                v-if="item.field=='progress'" :label="item.name">
                                <span class="placeholder-label" :class="{'old-value':hasUpdate('progress')}">{{task.progress||0}}</span>
                                <span :class="{'new-value':hasUpdate('progress')}">{{alterationValue('progress')}}
                                </span>
                            </FormItem>

                            <FormItem
                                v-if="item.field=='finishTime'" :label="item.name">
                                <span class="placeholder-label" :class="{'old-value':hasUpdate('finishTime')}">{{task.finishTime|fmtDateTime}}</span>
                                <span v-if="hasUpdate('finishTime')" :class="{'new-value':hasUpdate('finishTime')}">
                                    {{alterationValue('finishTime')|fmtDateTime}}
                                </span>
                            </FormItem>

                            <FormItem
                                v-if="item.field=='expectEndDate'" :label="item.name">
                                <span class="placeholder-label" :class="{'old-value':hasUpdate('expectEndDate')}">{{task.expectEndDate|fmtDate}}</span>
                                <span v-if="hasUpdate('expectEndDate')" :class="{'new-value':hasUpdate('expectEndDate')}">
                                    {{alterationValue('expectEndDate')|fmtDate}}
                                </span>
                            </FormItem>

                            <FormItem
                                v-if="item.field=='stageName'" :label="item.name">
                                <span class="placeholder-label" :class="{'old-value':hasUpdate('stageId')}">{{task.stageName||'未设置'}}</span>
                                <span
                                    :class="{'new-value':hasUpdate('stageId')}">{{alterationValue('stageName')}}</span>
                            </FormItem>


                        </template>
                        <template v-if="item.isSystemField==false">
                            <FormItem
                                :label="item.name" style="padding-right:20px"
                                :prop="'customFields.field_'+item.id">
                                <div class="placeholder-label"                                    >
                                    <span v-if="item.type==1||item.type==3||item.type==4||item.type==8"
                                          :class="{'old-value':hasUpdate('field_'+item.id)}">
                                        {{task.customFields['field_'+item.id]||'未设置'}}
                                    </span>
                                    <span v-if="item.type==7"
                                          :class="{'old-value':hasUpdate('field_'+item.id)}"
                                    >{{(task.customFields['field_'+item.id])|fmtD}}</span>
                                    <span v-if="item.type==6"
                                          :class="{'old-value':hasUpdate('field_'+item.id)}"
                                    >
                                        <AvatarImage
                                            v-for="account in task.customFields['field_'+item.id]"
                                            :key="'member_'+account.id"
                                            size="small"
                                            :title="account.name"
                                            :value="account.imageId"
                                            :name="account.name"/>
                                    </span>


                                    <span :class="{'new-value':hasUpdate('field_'+item.id)}">
                                        {{alterationValue('field_'+item.id)}}
                                    </span>
                                </div>
                            </FormItem>
                        </template>
                    </div>
                </template>
            </div>
            <div style="margin-left: 20px;">
                <FormItem label="变更原因" prop="reason" style="padding: 15px 0;">
                    <Input :disabled="!isCreator||alteration.isFinish||projectFinishDisabled" v-model="alteration.reason" type="textarea" :rows="2" placeholder="请填写变更原因"/>
                </FormItem>
                <FormItem v-if="!alteration.isFinish&&!projectFinishDisabled" label="备注" prop="remark" style="padding: 15px 0;">
                    <Input :disabled="!isOwner||alteration.isFinish" v-model="formItem.remark"  placeholder="备注"/>
                </FormItem>

                <FlagTitle label="审批日志" size="large"/>
                <table
                    class="table-content table-content-small table-box"
                    style="margin-top:10px">
                    <tbody>
                    <tr
                        v-for="item in alteration.logList"
                        :key="'log_'+item.id"
                        class="table-row table-row-small">
                        <td class="clickable">
                            <AvatarImage
                                style="margin: 0 5px;"
                                size="small"
                                :name="item.createAccountName"
                                :value="item.createAccountImageId"
                                type="tips"></AvatarImage>
                        </td>
                        <td >
                            <span>{{item.createTime|fmtDateTime}}</span>
                        </td>
                        <td >
                            <span v-if="item.status==3">已取消</span>
                            <TaskAlterationStatusSelect v-else
                                :disabled="true"
                                :status-list="statusList"
                                v-model="item.flowStatus"
                                placement="bottom-end">
                            </TaskAlterationStatusSelect>
                        </td>
                        <td style="text-align:right;width:200px;color:#999">
                            {{item.remark}}
                        </td>
                    </tr>

                    </tbody>
                </table>
            </div>

        </Form>
        <div slot="footer">
            <Row>
                <Col span="24" style="text-align:right">
                    <Button @click="showDialog=false" type="default" size="large">
                        {{$t('关闭')}}
                    </Button>
                    <Button v-if="isOwner&&!alteration.isFinish&&!projectFinishDisabled" :disabled="!changed" @click="confirm(1)" type="success" size="large">
                        {{$t('确认审批')}}
                    </Button>
                    <Button v-if="isCreator&&isInitStatus&&!alteration.isFinish&&!projectFinishDisabled" @click="confirm(2)" type="success" size="large">
                        {{$t('保存')}}
                    </Button>
                    <Button v-if="isCreator&&isInitStatus&&!alteration.isFinish&&!projectFinishDisabled" @click="confirm(3)" type="error" size="large">
                        {{$t('撤销')}}
                    </Button>
                </Col>
            </Row>
        </div>
    </Modal>
</template>


<script>
    export default {
        name: 'TaskAlterationInfoDialog',
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
                    reason: null,
                    remark:null
                },
                keyToField: {
                    "iterationName": "iterationId",
                    "ownerAccountName": "ownerAccountIdList",
                    "statusName": "status",
                    "priorityName": "priority",
                    "subSystemName": "subSystemId",
                    "releaseName": "releaseId",
                    "stageName": "stageId",
                },
                updateFields: [],
                checkFields: [],
                alterationFields: [],
                editableFields: [],
                task: {},
                valuemap: {
                    "iterationName": "iterationId",
                    "ownerAccountName": "ownerAccountIdList",
                    "statusName": "status",
                    "priorityName": "priority",
                    "subSystemName": "subSystemId",
                    "releaseName": "releaseId",
                },
                alteration: {},
                statusList:[],
                initFlowStatus:0,
                changed:false,
                projectFinishDisabled:false
            };
        },
        computed: {
            typeName: function () {
                return app.dataDictValue('Task.objectType', this.args.objectType);
            },
            flowStatusList(){
                if(!this.alteration.flowStatus||!this.statusList||Array.isEmpty(this.statusList)){
                    return [];
                }
                let transfer1 = this.statusList.find(item=>item.id==this.alteration.flowStatus);
                let transfer = transfer1.transferTo;
                if(!transfer){
                    return [];
                }else{
                    let sList= this.statusList.filter(item=>Array.contains(transfer,item.id,a=>a));
                    sList.unshift(transfer1);
                    return sList;
                }
            },
            statusDisable(){
                if(this.changed){
                    return true;
                }
                let isOwner=false,isCreator=false;
                if(this.alteration.ownerIdList){
                     isOwner= Array.contains(this.alteration.ownerIdList,app.account.id,a=>a);
                }
                isCreator= this.alteration.createAccountId===app.account.id;

                return isCreator||!isOwner;
            },
            isInitStatus(){
                if(!this.alteration.flowStatus||!this.statusList||Array.isEmpty(this.statusList)){
                    return false;
                }
                let transfer = this.statusList.find(item=>item.id==this.alteration.flowStatus);
                return transfer&&transfer.type===1;
            },
            isOwner(){
                if(this.alteration.ownerIdList){
                    let isOwner= Array.contains(this.alteration.ownerIdList,app.account.id,a=>a);
                    console.log("isOwner",isOwner)
                    return isOwner;
                }
                return false;
            },
            isCreator(){
                let isCreator= this.alteration.createAccountId===app.account.id;
                console.log("isCreateor",isCreator)
                return isCreator;
            },
        },
        filters:{
            fmtD(v){
                console.log("------",v)
                return formatDate(v)
            }
        },
        methods: {
            pageLoad() {
                this.formItem.projectId = this.args.projectId;
                this.formItem.objectType = this.args.objectType;
                this.updateFields = [];
                this.loadEditInfo();
                this.loadAlterationDefineList();
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
                app.invoke('TaskAlterationAction.getTaskAlterationById', [app.token, this.args.id], info => {
                    this.alteration = info;
                    this.initFlowStatus = info.flowStatus;
                    this.task = info.task;
                    this.projectFinishDisabled = this.isProjectDisabled(this.alteration.projectId);

                    app.invoke('BizAction.getEditTaskInfo', [app.token, info.projectId, info.objectType], res => {
                        this.editInfo = res;
                    });
                });
            },
            loadAlterationDefineList(){
                app.invoke('TaskAlterationAction.getTaskAlterationDefineList', [app.token, {projectId:this.args.projectId,objectType:this.args.objectType}], res => {
                    this.statusList = res;
                });

            },
            changeFlowStatus(){
                if(this.alteration.flowStatus!=this.initFlowStatus){
                    this.changed = true;
                }
            },
            // field type name oldVlaue newValue
            confirm(type) {
                if(type==1){
                    let bean={
                        id :this.alteration.id,
                        alterationId :this.alteration.id,
                        flowStatus:this.alteration.flowStatus,
                        remark:this.formItem.remark
                    }
                    app.confirm("确认执行此审批吗",()=>{
                        app.invoke('TaskAlterationAction.auditTaskAlteration',[app.token,bean],(info)=>{
                            app.toast(this.$t('操作成功'))
                            app.postMessage('task.freeze')
                            this.showDialog=false;
                        })
                    })
                }else if(type==2){
                    let bean={
                        id :this.alteration.id,
                        alterationId :this.alteration.id,
                        reason:this.alteration.reason,
                    }
                    app.invoke('TaskAlterationAction.updateTaskAlteration',[app.token,bean],(info)=>{
                        app.toast(this.$t('操作成功'))
                        this.showDialog=false;
                    })
                }else if(type==3){
                    let bean={
                        id :this.alteration.id,
                        alterationId :this.alteration.id,
                        reason:this.alteration.reason,
                    }

                    app.confirm("确认撤销此变更吗",()=>{
                        app.invoke('TaskAlterationAction.cancelTaskAlteration',[app.token,bean],(info)=>{
                            app.toast(this.$t('操作成功'))
                            app.postMessage('task.freeze')
                            this.showDialog=false;
                        })
                    })
                }
            },
            compKeyToField(key) {
                return this.keyToField[key] || key;
            },
            /**
             * 返回新的值
             * @param field
             * @returns {string|*}
             */
            alterationValue(field) {
                field = this.compKeyToField(field) || field;
                if (!this.editInfo) {
                    return '未设置';
                }
                if (!this.alteration || !this.alteration.fields) {
                    return '';
                }
                for (let i = 0; i < this.alteration.fields.length; i++) {
                    let updateField = this.alteration.fields[i];
                    if (field === updateField.field) {
                        let newValue = updateField.newValue;
                        if (!newValue) {
                            return '未设置';
                        } else {
                            return updateField.newShowValue;
                        }
                    }
                }
                return '';
            },
            hasUpdate(field) {
                if (!this.alteration || !this.alteration.fields) {
                    return false;
                }
                for (let i = 0; i < this.alteration.fields.length; i++) {
                    let updateField = this.alteration.fields[i];
                    if (field === updateField.field) {
                        return true;
                    }
                }
                return false;
            },
            /**
             * 返回旧的值
             * @param field
             * @returns {string|*|string}
             */
            alterationOldValue(field) {
                if (field === 'categoryIdList') {
                    let categoryList = this.editInfo.categoryNodeList;
                    if (!!categoryList && !!this.task.categoryIdList && !Array.isEmpty(this.task.categoryIdList)) {
                        let categoryNames = [];
                        this.recurivseCategory(this.task.categoryIdList, this.editInfo.categoryNodeList, categoryNames)
                        return categoryNames.join("、");
                    }

                } else if (field === 'ownerAccountName') {
                    let accountList = this.task.ownerAccountList;
                    if (!!accountList) {
                        return accountList.map(k => k.name).join("、");
                    }
                } else if (field.indexOf('field_') === 0) {
                    if (!!this.task.customFields) {
                        return this.task.customFields[field] || '未设置';
                    }
                } else {
                    return this.task[field] || '未设置';
                }
                return '未设置';
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
