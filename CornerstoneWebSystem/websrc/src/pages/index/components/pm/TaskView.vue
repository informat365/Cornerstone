<style scoped>
    .info-box {
        background-color: #fbfbfb;
        border-bottom: 1px solid rgb(238, 238, 238);
        padding: 25px;
    }

    .header {
        padding: 4px 20px;
        border-bottom: 1px solid #ccc;
        padding-right: 5px;
        background-color: #f7f7f7;
    }

    .header-left {
        display: flex;
        align-items: center;
        height: 34px;
    }

    .section-box {
        padding: 25px;
    }

    .info-section-row {
        margin-bottom: 10px;
    }

    .info-form-item {
        width: 300px;
    }

    .info-form-item-custom {
        width: 640px;
    }

    .description-box {
        margin-top: 20px;
        font-size: 14px;
        cursor: pointer;
    }

    .task-box {
        display: flex;
        flex-direction: column;
        flex: 1;
        overflow: auto;
    }

    .main-content-box {
        flex: 1;
        overflow-y: auto;
    }

    .footer-box {
        height: 50px;
        padding: 10px;
        padding-left: 30px;
        padding-right: 30px;
        box-shadow: rgba(0, 0, 0, 0.15) 0px 0px 8px 0px !important;
        transition: height 0.3s;
    }

    .footer-box-open {
        height: 350px;
        border-top: 1px solid #ccc;
    }

    .commit-box {
        background-color: #f3f3f3;
        border-radius: 3px;
        font-size: 14px;
        padding: 5px 15px;
        cursor: pointer;
        color: #999;
    }

    .dialog-title {
        font-size: 15px;
        font-weight: bold;
        color: #999;
        cursor: pointer;
        padding-top: 3px;
    }

    .main-box {
        display: flex;
        flex-direction: column;
        height: 100%;
    }

    .task-nav-box {
        position: absolute;
        top: -20px;
        width: 100%;
        height: 20px;
    }

    .goto-task-list-btn {
        background-color: #eee;
        padding: 3px 10px;
        border-radius: 3px;
        cursor: pointer;
        max-width: 300px;
        user-select: none;
    }

    .switch-task-btn {
        background-color: #eee;
        padding: 3px 10px;
        border-radius: 3px;
        cursor: pointer;
        margin-right: 5px;
        user-select: none;
    }

    .switch-task-btn-disabled {
        color: #999;
        cursor: not-allowed;
    }

    .worktime-log-item {
        display: inline-block;
        text-decoration: none;
        vertical-align: middle;
    }

    .project-box {
        padding: 25px;
        font-size: 13px;
        padding-top: 10px;
        padding-bottom: 10px;
        color: #999;
        border-bottom: 1px solid rgb(238, 238, 238);
    }

    .has-reminder {
        color: #0094fb !important;
    }

    .list-count {
        font-size: 12px;
    }

    .change-log-select-popup {
        padding: 30px;
        display: flex;
        align-items: center;
        justify-items: center;
    }

    .progress-text {
        font-weight: bold;
        margin-left: 5px;
    }

    .log-user-name {
        background-color: #eee;
        color: #555;
        font-size: 12px;
        padding: 1px 5px;
        text-align: center;
        display: inline-block;
        border-radius: 3px;
        margin-right: 5px;
        font-weight: bold;
        margin-bottom: 3px;
    }

    .split-line {
        width: 1px;
        height: 12px;
        background-color: #a2a2a2;
        margin-left: 7px;
        margin-right: 7px;
    }

    .table-box {
        border: 1px solid #eee;
    }
</style>
<i18n>
    {
    "en": {
    "前往项目":"Go to project",
    "显示全部字段":"All Fields",
    "只显示系统字段":"Only System Fields",
    "上一个":"Previous",
    "下一个":"Next",
    "前往列表":"Goto {0} {1} list",
    "关联版本":"Version Control Relation",
    "设置提醒":"Setting Reminder",
    "上传附件":"Upload",
    "下载PDF":"Download as pdf",
    "添加子对象":"Add sub-object",
    "关联对象":"Add Relation",
    "前置对象":"Pre-object",
    "后置对象":"Post-object",
    "删除":"Delete",
    "复制":"Copy",
    "待认领":"none",
    "未设置":"none",
    "增加工时记录":"Add work log",
    "条记录":"{0}",
    "查看记录":"Logs",
    "工时记录":"Work Log",
    "记录":"Add",
    "编辑":"Edit",
    "暂无工时记录":"No Data",
    "详细描述":"Detail",
    "全屏":"Fullscreen",
    "编辑描述":"Edit",
    "未设置详细描述":"none",
    "取消":"Cancel",
    "保存":"Save",
    "附件":"Attachment",
    "上传附件":"Upload",
    "上传":"Upload",
    "下载附件":"Download",
    "删除附件":"Delete",
    "测试用例":"Testcase",
    "关联用例":"Link Testcase",
    "新建用例":"Add Testcase",
    "执行用例":"Run Testcase",
    "执行记录":"Execution Log",
    "解除关联":"Unlink",
    "暂无用例":"No Data",
    "关联内容":"Relation",
    "关联对象":"Relation",
    "添加子对象":"Add sub-object",
    "解除子对象":"Delete sub-object",
    "解除父对象":"Delete parent-object",
    "解除关联":"Remove",
    "评论":"Comment",
    "变更记录":"Change Log",
    "用户":"Member",
    "记录类型":"Type",
    "所有用户":"All Member",
    "所有记录类型":"All Type",
    "状态记录":"Status",
    "暂无评论":"No Data",
    "暂无数据":"No Data",
    "加载更多":"Load More",
    "状态":"Status",
    "停留时间":"Stay time",
    "详细记录":"Detail",
    "输入@提醒项目成员":"@ mention member",
    "提交":"Submit",
    "取消":"Cancel",
    "评论内容，输入@提醒项目成员，ctrl+enter提交":"@ mention member,ctrl+enter submit",
    "创建对象":"Create object",
    "创建":"Create",
    "编辑属性":"Edit",
    "上传附件":"Upload Attachment",
    "本地上传":"Local upload",
    "文件库关联":"Associate file library",
    "wiki关联":"Associate wiki library",
    "删除附件":"Delete Attachment",
    "新增关联对象":"Add Relation",
    "取消关联对象":"Cancel Relation",
    "新增子对象":"Add sub-object",
    "删除子对象":"Delete sub-object",
    "修改详细描述":"Edit Detail",
    "提交代码":"Submit code",
    "流转图":"Status flow graph",
    "确定要删除":"Are you sure want to delete {0}」？",
    "确定要解除":"Are you sure to dismiss「{0}」？",
    "确定要解除关联":"Are you sure you want to de-associate {0}?",
    "确定要删除附件":"Are you sure you want delete {0}」吗？",
    "操作成功":"Success",
    "已解除关联":"{0} de-associated",
    "已删除":"{0} deleted",
    "已解除":"{0} dismissed",
    "正在上传文件，请稍后":"Uploading",
    "确定要删除评论":"Are you sure you want to delete {0} comments?",
    "ID已复制到剪切板":"ID has been copied to the clipboard",
    "变更申请":"Apply for change"
    },
    "zh_CN": {
    "前往项目":"前往项目",
    "显示全部字段":"显示全部字段",
    "只显示系统字段":"只显示系统字段",
    "上一个":"上一个",
    "下一个":"下一个",
    "前往列表":"前往{0}{1}列表",
    "关联版本":"关联版本",
    "设置提醒":"设置提醒",
    "上传附件":"上传附件",
    "下载PDF":"下载PDF",
    "添加子对象":"添加子对象",
    "关联对象":"关联对象",
    "前置对象":"前置对象",
    "后置对象":"后置对象",
    "删除":"删除",
    "复制":"复制",
    "待认领":"待认领",
    "未设置":"未设置",
    "增加工时记录":"增加工时记录",
    "条记录":"{0}条",
    "查看记录":"查看",
    "工时记录":"工时记录",
    "记录":"记录",
    "编辑":"编辑",
    "暂无工时记录":"暂无工时记录",
    "详细描述":"详细描述",
    "创建":"创建",
    "全屏":"全屏",
    "编辑描述":"编辑描述",
    "未设置详细描述":"未设置详细描述",
    "取消":"取消",
    "保存":"保存",
    "附件":"附件",
    "上传附件":"上传附件",
    "上传":"上传",
    "下载附件":"下载附件",
    "删除附件":"删除附件",
    "测试用例":"测试用例",
    "关联用例":"关联用例",
    "新建用例":"新建用例",
    "执行用例":"执行用例",
    "执行记录":"执行记录",
    "解除关联":"解除关联",
    "暂无用例":"暂无用例",
    "关联内容":"关联内容",
    "关联对象":"关联对象",
    "添加子对象":"添加子对象",
    "解除子对象":"解除子对象",
    "解除父对象":"解除父对象",
    "解除关联":"解除关联",
    "评论":"评论",
    "变更记录":"变更记录",
    "用户":"用户",
    "记录类型":"记录类型",
    "所有用户":"所有用户",
    "所有记录类型":"所有记录类型",
    "状态记录":"状态记录",
    "暂无评论":"暂无评论",
    "暂无数据":"暂无数据",
    "加载更多":"加载更多",
    "状态":"状态/停留时间",
    "停留时间":"停留时间",
    "详细记录":"详细记录",
    "输入@提醒项目成员":"输入@提醒项目成员",
    "提交":"提交",
    "取消":"取消",
    "评论内容，输入@提醒项目成员，ctrl+enter提交":"评论内容，输入@提醒项目成员，ctrl+enter提交",
    "创建对象":"创建对象",
    "编辑属性":"编辑属性",
    "上传附件":"上传附件",
    "本地上传":"本地上传",
    "流转图":"流转图",
    "文件库关联":"文件库关联",
    "wiki关联":"wiki关联",
    "删除附件":"删除附件",
    "新增关联对象":"新增关联对象",
    "取消关联对象":"取消关联对象",
    "新增子对象":"新增子对象",
    "删除子对象":"删除子对象",
    "修改详细描述":"修改详细描述",
    "提交代码":"提交代码",
    "确定要删除":"确定要删除「{0}」吗？",
    "确定要解除":"确定要解除「{0}」吗？",
    "确定要解除关联":"确定要解除关联「{0}」吗？",
    "确定要删除附件":"确定要删除附件「{0}」吗？",
    "操作成功":"操作成功",
    "已解除关联":"「{0}」已解除关联",
    "已删除":"「{0}」已删除",
    "已解除":"{0} 已解除",
    "正在上传文件，请稍后":"正在上传文件，请稍后",
    "确定要删除评论":"确定要删除「{0}」的评论吗？",
    "ID已复制到剪切板":"ID已复制到剪切板",
    "变更申请":"变更申请"
    }
    }
</i18n>
<template>
    <div
        class="task-view main-box"
        :class="{'blur':loading}"
        @keyup.ctrl.13="replyComment">
        <div v-if="showTopBar&&editInfo.project" class="task-nav-box">
            <Row>
                <Col span="12">
                    <template v-if="taskList!=null&&taskList.length>1">
                        <span
                            @click="showNextTask(-1)"
                            class="switch-task-btn"
                            :class="{'switch-task-btn-disabled':currentTaskIndex==0}">{{$t('上一个')}}</span>
                        <span class="switch-task-btn">{{currentTaskIndex+1}}/{{taskList.length}}</span>
                        <span
                            @click="showNextTask(1)"
                            class="switch-task-btn"
                            :class="{'switch-task-btn-disabled':currentTaskIndex==taskList.length-1}">{{$t('下一个')}}</span>
                    </template> &nbsp;
                </Col>
                <Col span="12" style="text-align:right">
                    <span
                        @click="gotoProjectTaskList"
                        class="text-no-wrap goto-task-list-btn">{{$t('前往列表',[editInfo.project.name,task.objectTypeName])}}</span>
                </Col>
            </Row>
        </div>

        <Row class="header">
            <Col span="12" class="header-left">
                <div
                    class="dialog-title numberfont"
                    v-clipboard:copy="'#'+task.serialNo"
                    v-clipboard:success="copySuccess"> {{task.objectTypeName}}
                    #{{task.serialNo}}
                </div>
                <OverdueLabel
                    style="margin-left:10px"
                    v-if="!task.isFinish&&task.endDate"
                    :value="task.endDate"></OverdueLabel>
            </Col>
            <Col span="12">
                <div style="display:flex;align-items:center;height:34px;text-align:right;justify-content: flex-end">
                    <IconButton
                        @click="toggleTaskStar"
                        icon="ios-star"
                        :color="task.star?'#FAA424':'#A7A7A7'"
                        :tips="task.star?'取消关注':'设为关注'"/>
                    <IconButton
                        v-if="showGoToProject"
                        @click="onClickGoToProject"
                        icon="ios-paper-plane"
                        :tips="$t('前往项目')"/>
                    <IconButton
                            v-if="showAlteration"
                            @click="showAlterationDialog"
                            icon="md-git-compare"
                            :tips="$t('变更申请')"/>
                    <IconButton
                        :class="{'has-reminder':haveRemind}"
                        v-if="hasField('startDate')&&hasField('endDate')"
                        @click="showTaskAlarmDialog"
                        icon="md-alarm"
                        :tips="$t('设置提醒')"></IconButton>
                    <Poptip
                        trigger="click" v-model="headerAttachmentVisiable"
                        v-if="tperm(['task_add_attachment','task_edit'])&&!projectFinishDisabled"
                        placement="bottom-end">
                        <IconButton
                            icon="md-attach" :tips="$t('上传附件')"></IconButton>
                        <div slot="content">
                            <TaskSelectAttachmentView
                                :project-id="task.projectId"
                                @on-cancel="cancelTaskAttachment"
                                @on-confirm="addTaskAttachment"
                                @on-select="selectAttachmentFile"></TaskSelectAttachmentView>
                        </div>
                    </Poptip>
                    <IconButton
                        @click="onClickDownloadAsPdf"
                        icon="md-download"
                        :tips="$t('下载PDF')"/>
                    <IconButton
                        v-if="tperm('task_create')&&task.objectType!=5&&!projectFinishDisabled"
                        @click="showAddSubTaskDialog"
                        icon="md-add"
                        :tips="$t('添加子对象')"/>
                    <Poptip
                        v-if="tperm('task_edit') && showTaskLinkBtn"
                        ref="poptip"
                        transfer
                        class="poptip-full"
                        v-model="relPopupTopVisible">
                        <IconButton
                            icon="ios-link" :tips="$t('关联对象')"></IconButton>
                        <div slot="content">
                            <div class="popup-box scrollbox">
                                <div
                                    @click="showSelectTaskDialog(0)"
                                    class="popup-item-name">{{$t('关联对象')}}
                                </div>
                                <div
                                    @click="showSelectTaskDialog(1)"
                                    class="popup-item-name">{{$t('前置对象')}}
                                </div>
                                <div
                                    @click="showSelectTaskDialog(2)"
                                    class="popup-item-name">{{$t('后置对象')}}
                                </div>
                            </div>
                        </div>
                    </Poptip>

                    <IconButton
                        v-if="tperm('task_delete')&&!projectFinishDisabled"
                        @click="deleteTask(task)"
                        icon="ios-trash"
                        :tips="$t('删除')"></IconButton>
                    <IconButton
                        v-if="tperm('task_create')"
                        @click="showCopyTaskDialog()"
                        icon="ios-copy"
                        :tips="$t('复制')"></IconButton>

                    <template v-if="showType!='split'">
                        <div class="split-line"></div>
                        <IconButton
                            @click="$emit('close')"
                            icon="md-close"></IconButton>
                    </template>
                    <span
                        v-if="showType=='split'"
                        style="padding:0 5px;display:inline-block"></span>
                </div>
            </Col>
        </Row>
        <div class="task-box">
            <div ref="viewBox" class="main-content-box scrollbox">
                <Row class="project-box">
                    <Alert v-if="task.isFreeze" type="warning">{{task.objectTypeName}}进入变更流程被冻结，暂无法编辑</Alert>
                    <Col span="12">
                        <Icon type="ios-bookmark" size="18"></Icon>
                        {{task.projectName}}
                    </Col>
                    <Col span="12" style="text-align:right">
                        <AvatarImage
                            :name="task.createAccountName"
                            :value="task.createAccountImageId"
                            type="label"
                            size="small"/>
                        <span style="padding-left:10px;display: inline-block;vertical-align: middle;">{{task.createTime|fmtDeltaTime}} {{$t('创建')}}</span>
                    </Col>
                </Row>
                <div class="section-box" style="border-bottom:1px solid #eee">

                    <div
                        class="edit-task-name"
                        :class="{'edit-task-name-finish':task.isFinish}">
                        <Input
                            :disabled="propDisable"
                            @on-blur="updateTask('name')"
                            type="textarea"
                            :autosize="true"
                            v-model.trim="task.name"></Input>
                    </div>
                    <Row style="margin-top:10px">
                        <Col span="12" style="min-height:24px">
                            <MemberSelect
                                placeholder="待认领"
                                v-if="hasField('ownerAccountName')"
                                :disabled="memberDisable||isProjectSetTask"
                                @change="updateTask('ownerAccountIdList')"
                                :multiple="true"
                                :member-list="editInfo.memberList"
                                :value-list="task.ownerAccountList"
                                v-model="task.ownerAccountIdList"
                                placement="bottom-start"></MemberSelect>
                        </Col>
                        <Col span="12" style="text-align:right">
                            <TaskPrioritySelect
                                v-if="hasField('priorityName')"
                                :disabled="propDisable"
                                style="margin-right:30px"
                                @change="updateTask('priority')"
                                :priority-list="editInfo.priorityList"
                                v-model="task.priority"
                                placement="bottom"></TaskPrioritySelect>
                            <TaskStatusSelect
                                v-if="hasField('statusName')&&!isProjectSetTask"
                                :disabled="statusDisable"
                                @change="updateTask('status')"
                                :status-list="editInfo.statusList"
                                v-model="task.status"
                                placement="bottom-end"></TaskStatusSelect>
                        </Col>
                    </Row>
                    <div
                        style="margin-top:15px"
                        v-if="hasField('categoryIdList')">
                        <Icon
                            style="color:#999;margin-right:9px"
                            :size="18"
                            type="md-pricetags"/>
                        <CategorySelect
                            :disabled="categoryDisable"
                            @change="updateTask('categoryIdList')"
                            :category-list="editInfo.categoryNodeList"
                            v-model="task.categoryIdList"
                            placement="bottom-start"></CategorySelect>
                    </div>
                </div>

                <Row class="project-box" v-if="hasField('iterationName')||hasField('stageName')">
                    <Col span="12">
                        <template v-if="hasField('iterationName')">
                            <TaskViewIterationSelect
                                @change="updateTask('iterationId')"
                                :editable="!propDisable"
                                v-model="task.iterationId"
                                :list="editInfo.iterationList"></TaskViewIterationSelect>
                        </template>
                    </Col>
                    <Col :span="hasField('iterationName')?12:24" style="text-align:right">
                        <template v-if="hasField('stageName')" >
                            <TaskViewStageSelect
                                @change="updateTask('stageId')"
                                :editable="!propDisable"
                                :searchable="true"
                                v-model="task.stageId"
                                :list="editInfo.stageList"></TaskViewStageSelect>
                        </template>
                    </Col>
                </Row>

                <div v-if="showInfoBox" class="info-box">
                    <Form label-position="left" :label-width="100" inline>
                        <template v-for="item in editInfo.fieldList">
                            <FormItem
                                :key="item.id"
                                v-if="isSystemOtherField(item)"
                                :label="item.name"
                                :title="item.name"
                                class="info-form-item"
                                :class="{'info-form-item-custom':item.type==1&&item.isSystemField==false}">
                                <template v-if="item.isSystemField">
                                    <MemberSelect
                                        :placeholder="$t('未设置')"
                                        :disabled="propDisable"
                                        @change="updateTask('leaderAccountIdList')"
                                        v-if="item.field=='leaderAccountName'"
                                        :multiple="true"
                                        :member-list="editInfo.memberList"
                                        :value-list="task.leaderAccountList"
                                        v-model="task.leaderAccountIdList"
                                        placement="bottom-start"></MemberSelect>
                                    <TaskObjectSelect
                                        :disabled="propDisable"
                                        v-if="item.field=='subSystemName'"
                                        @change="updateTask('subSystemId')"
                                        :object-list="editInfo.subSystemList"
                                        v-model="task.subSystemId"
                                        placement="bottom-start"></TaskObjectSelect>
                                    <TaskObjectSelect
                                        :disabled="propDisable"
                                        v-if="item.field=='releaseName'"
                                        @change="updateTask('releaseId')"
                                        :object-list="editInfo.releaseList"
                                        v-model="task.releaseId"
                                        placement="bottom-start"></TaskObjectSelect>
                                    <TaskObjectSelect
                                        :disabled="propDisable"
                                        v-if="item.field=='repositoryName'"
                                        @change="updateTask('repositoryId')"
                                        :object-list="editInfo.repositoryList"
                                        v-model="task.repositoryId"
                                        placement="bottom-start"></TaskObjectSelect>
                                    <div
                                        class="vrow"
                                        v-if="item.field=='expectWorkTime'">
                                        <input
                                            type="number"
                                            :disabled="propDisable"
                                            @blur="updateTask('expectWorkTime')"
                                            class="edit-task-prop-input"
                                            style="width:100px"
                                            v-model.number="task['expectWorkTime']"
                                            :placeholder="$t('未设置')"/>
                                    </div>

                                    <div
                                        class="vrow"
                                        v-if="item.field=='progress'">
                                        <Slider
                                            :disabled="propDisable"
                                            style="width:150px"
                                            v-model="task['progress']"
                                            :placeholder="$t('未设置')"
                                            @on-change="updateTask('progress')"></Slider>
                                        <span class="progress-text">{{task['progress']}}%</span>
                                    </div>

                                    <div
                                        class="vrow"
                                        v-if="item.field=='workTime'">
                                        <span style="color:#333;font-weight:bold"> {{task.workTime}}h</span>
                                        <IconButton
                                            v-if="!propDisable"
                                            @click="showAddWorkTimeDialog"
                                            icon="md-add"
                                            :tips="$t('增加工时记录')"
                                            :size="16"></IconButton>
                                        <IconButton
                                            @click="showWorkTimeBox=!showWorkTimeBox"
                                            :title="$t('查看记录')"></IconButton>
                                        <span v-if="task.workTimeLogList&&task.workTimeLogList.length>0">（ {{$t('条记录',[task.workTimeLogList.length])}}）</span>
                                    </div>

                                    <div v-if="item.field=='startDate'">
                                        <DateSelect
                                            :options="startDateOptions"
                                            :end="startDateOptions.end"
                                            :disabled="taskEditStartEndDateDisable"
                                            @change="updateTask('startDate')"
                                            ref="start"
                                            type="date"
                                            v-model="task.startDate"
                                            :placeholder="$t('未设置')"></DateSelect>
                                    </div>
                                    <div v-if="item.field=='finishTime'">
                                        <DateSelect
                                            :options="endDateOptions"
                                            :start="endDateOptions.start"
                                            :disabled="finishTimeDisable"
                                            @change="updateTask('finishTime')"
                                            :showTimeField="true"
                                            v-model="task.finishTime"
                                            :placeholder="$t('未设置')"></DateSelect>
                                    </div>

                                    <div v-if="item.field=='endDate'">
                                        <DateSelect
                                            :options="endDateOptions"
                                            :disabled="taskEditStartEndDateDisable"
                                            @change="updateTask('endDate')"
                                            type="date"
                                            ref="end"
                                            :start="endDateOptions.start"
                                            v-model="task.endDate"
                                            :placeholder="$t('未设置')"></DateSelect>
                                    </div>
                                    <div v-if="item.field=='expectEndDate'">
                                        <DateSelect
                                            :disabled="propDisable"
                                            :options="endDateOptions"
                                            @change="updateTask('expectEndDate')"
                                            type="date"
                                            v-model="task.expectEndDate"
                                            :placeholder="$t('未设置')"></DateSelect>
                                    </div>
                                    <div v-if="item.field=='workLoad'">
                                        <input
                                                type="number"
                                                :disabled="propDisable"
                                                @blur="updateTask('workLoad')"
                                                class="edit-task-prop-input"
                                                style="width:100px"
                                                v-model.number="task['workLoad']"
                                                :placeholder="$t('未设置')"/>
                                    </div>
                                    <div v-if="item.field=='createTime'">
                                        <span
                                            style="color:#333;font-weight:bold"> {{task.createTime|fmtDateTime}}</span>
                                    </div>
                                    <div v-if="item.field=='updateTime'">
                                        <span
                                            style="color:#333;font-weight:bold"> {{task.updateTime|fmtDateTime}}</span>
                                    </div>
                                </template>
                                <template v-if="item.isSystemField==false">
                                    <Input
                                        :maxlength="500"
                                        type="textarea"
                                        :autosize="true"
                                        :disabled="item.isPsField||customFieldPropDisable"
                                        @on-blur="updateTask('field_'+item.id)"
                                        class="edit-task-prop"
                                        v-if="item.type==1"
                                        v-model="task.customFields['field_'+item.id]"
                                        :placeholder="$t('未设置')"></Input>
                                    <input
                                        type="number"
                                        :disabled="item.isPsField||customFieldPropDisable"
                                        @blur="updateTask('field_'+item.id)"
                                        class="edit-task-prop-input"
                                        style="width:200px"
                                        v-if="item.type==8"
                                        v-model.number="task.customFields['field_'+item.id]"
                                        :placeholder="$t('未设置')"/>
                                    <DateSelect
                                        :disabled="item.isPsField||customFieldPropDisable"
                                        @change="updateTask('field_'+item.id)"
                                        type="date"
                                        :showTimeField="item.showTimeField"
                                        v-if="item.type==7"
                                        v-model="task.customFields['field_'+item.id]"
                                        :placeholder="$t('未设置')">

                                    </DateSelect>
                                    <TaskCustomSelect
                                        :disabled="item.isPsField||customFieldPropDisable"
                                        @change="updateTask('field_'+item.id)"
                                        :multiple="true"
                                        v-if="item.type==3"
                                        v-model="task.customFields['field_'+item.id]"
                                        :object-list="item.valueRange"
                                        placement="bottom-start"></TaskCustomSelect>
                                    <TaskCustomSelect
                                        :disabled="item.isPsField||customFieldPropDisable"
                                        @change="updateTask('field_'+item.id)"
                                        :multiple="false"
                                        v-if="item.type==4"
                                        v-model="task.customFields['field_'+item.id]"
                                        :object-list="item.valueRange"
                                        placement="bottom-start"></TaskCustomSelect>

                                    <MemberSelect
                                        :placeholder="$t('未设置')"
                                        :disabled="item.isPsField||customFieldPropDisable"
                                        @change="updateTask('field_'+item.id)"
                                        v-if="item.type==6"
                                        :multiple="true"
                                        :member-list="editInfo.memberList"
                                        :value-list="task.customFields['fieldobject_'+item.id]"
                                        v-model="task.customFields['field_'+item.id]"
                                        placement="bottom-start"></MemberSelect>

                                </template>
                            </FormItem>
                        </template>
                    </Form>
                </div>

                <div class="section-box">
                    <template v-if="showWorkTimeBox">
                        <div class="info-section-row">
                            <div class="info-section-header">{{$t('工时记录')}}
                            </div>
                            <span class="info-section-header-sub"> {{task.workTimeLogList.length}} </span>
                        </div>
                        <table
                            class="table-content table-content-small"
                            style="margin-top:10px">
                            <tbody>
                            <tr
                                v-for="item in task.workTimeLogList"
                                :key="'worklog'+item.id"
                                class="table-row table-row-small">
                                <td style="width:130px;font-weight:bold;font-size:14px">
                                    {{item.startTime|fmtDate}} {{item.hour}}h
                                </td>
                                <td>
                                    <div style="max-width:310px;word-wrap: break-word;">
                                        {{item.content}}
                                    </div>
                                </td>
                                <td style="width:200px;text-align:right">
                                    {{item.createAccountName}}
                                    {{item.createTime|fmtDeltaTime}}
                                    {{$t('记录')}}
                                </td>
                                <td style="width:60px;text-align:right;">
                            <span
                                style="color:#009af4;cursor:pointer"
                                v-if="item.createAccountId==account.id"
                                @click="editWorkTimeItem(item)">{{$t('编辑')}}</span>
                                </td>
                            </tr>

                            </tbody>
                        </table>
                        <div
                            v-if="task.workTimeLogList==0" class="table-nodata">
                            {{$t('暂无工时记录')}}
                        </div>
                    </template>


                    <div class="info-section-row">
                        <div class="info-section-header">
                            {{$t('详细描述')}}
                        </div>
                        <div class="info-section-opt">
                            <IconButton
                                :size="16"
                                @click="showDetailDialog"
                                icon="md-expand"
                                :title="$t('全屏')"></IconButton>
                            <IconButton
                                :size="16"
                                v-if="!propDisable"
                                @click="editDesc"
                                icon="md-checkbox"
                                :title="$t('编辑描述')"></IconButton>
                        </div>

                    </div>

                    <div
                        class="description-box"
                        v-show="descEdit==false"
                        @dblclick="editDesc">
                        <RichtextLabel
                            :value="task.content"
                            :placeholder="$t('未设置详细描述')"></RichtextLabel>
                    </div>

                    <div class="description-box" v-show="descEdit==true&&!projectFinishDisabled">
                        <RichtextEditor
                            ref="descEditor"
                            v-model="task.content" @on-change="changeDesc"></RichtextEditor>
                        <div style="margin-top:10px;text-align:right">
                            <Button
                                type="text"
                                style="margin-right:10px"
                                @click="descEdit=false">{{$t('取消')}}
                            </Button>
                            <Button
                                type="default"
                                @click="descEdit=false;updateTask('content')">
                                {{$t('保存')}}
                            </Button>
                        </div>
                    </div>

                    <template v-if="task.attachmentList.length>0||(task.wikiPageList&&task.wikiPageList.length>0)">
                        <div class="info-section-row">
                            <div class="info-section-header">{{$t('附件')}}</div>
                            <span class="info-section-header-sub">{{task.attachmentList.length+(task.wikiPageList?task.wikiPageList.length:0)}} </span>

                            <div class="info-section-opt">
                                <Poptip
                                    trigger="click" v-model="contentAttachmentVisiable"
                                    v-if="tperm('task_add_attachment')&&tperm('task_edit')&&!projectFinishDisabled"
                                    placement="right">
                                    <IconButton
                                        icon="md-attach"
                                        :title="$t('上传附件')"></IconButton>
                                    <!--                                    <FileUploadView-->
                                    <!--                                        style="padding:10px;width:450px"-->
                                    <!--                                        multiple-->
                                    <!--                                        @change="uploadSuccess"-->
                                    <!--                                        slot="content"></FileUploadView>-->
                                    <div slot="content">
                                        <TaskSelectAttachmentView
                                            :project-id="task.projectId"
                                            @on-cancel="cancelTaskAttachment"
                                            @on-confirm="addTaskAttachment"
                                            @on-select="selectAttachmentFile"></TaskSelectAttachmentView>
                                    </div>
                                </Poptip>
                            </div>


                        </div>
                        <table
                            class="table-content table-content-small"
                            style="margin-top:10px">
                            <tbody>
                            <tr
                                v-for="item in task.attachmentList"
                                :key="'att_'+item.id"
                                class="table-row table-row-small">
                                <td
                                    @click="previewAttachment(item)"
                                    style="width:200px">
                                    <div
                                        style="max-width:300px"
                                        class="text-no-wrap table-col-name">
                                        {{item.name}}
                                    </div>
                                </td>
                                <td style="text-align:right">
                                    <span
                                        class="table-row-hover-hide"
                                        style="font-size:12px;color:#999;">{{item.size|fmtBytes}} {{item.createAccountName}} {{item.createTime|fmtDeltaTime}} 上传</span>
                                    <span class="table-row-opt">
                                    <IconButton
                                        @click="downloadAttachment(item)"
                                        :tips="$t('下载附件')"
                                        icon="ios-cloud-download"></IconButton>
                                    <IconButton
                                        v-if="tperm(['task_edit'])&&!projectFinishDisabled"
                                        @click="deleteAttachment(item,1)"
                                        :tips="$t('删除附件')"
                                        icon="ios-trash"></IconButton>
                                </span>
                                </td>
                            </tr>

                            </tbody>
                        </table>
                        <table
                            class="table-content table-content-small"
                            v-if="task.wikiPageList&&task.wikiPageList.length>0"
                        >
                            <tbody>
                            <tr
                                v-for="item in task.wikiPageList"
                                :key="'att_'+item.id"
                                class="table-row table-row-small">
                                <td
                                    @click="previewWikiPage(item)"
                                    style="width:200px">
                                    <div
                                        style="max-width:300px"
                                        class="text-no-wrap table-col-name">
                                        {{item.name}}.wiki
                                    </div>
                                </td>
                                <td style="text-align:right">
                                    <span
                                        class="table-row-hover-hide"
                                        style="font-size:12px;color:#999;">{{item.createAccountName}} {{item.createTime|fmtDeltaTime}} 创建</span>
                                    <span class="table-row-opt">
                                    <IconButton
                                        v-if="tperm(['task_edit'])&&!projectFinishDisabled"
                                        @click="deleteAttachment(item,2)"
                                        :tips="$t('删除附件')"
                                        icon="ios-trash"></IconButton>
                                </span>
                                </td>
                            </tr>

                            </tbody>
                        </table>
                    </template>


                    <template v-if="task.objectType==4">
                        <div class="info-section-row">
                            <div class="info-section-header">{{$t('测试用例')}}
                            </div>
                            <span class="info-section-header-sub">{{task.testCaseList.length}}</span>
                            <div class="info-section-opt">
                                <IconButton v-if="!projectFinishDisabled"
                                    @click="showSelectTestCaseDialog"
                                    icon="ios-link"
                                    :title="$t('关联用例')"></IconButton>
                                <IconButton
                                    v-if="tperm('task_create')&&!projectFinishDisabled"
                                    @click="showAddTestCaseDialog"
                                    icon="ios-add"
                                    :title="$t('新建用例')"></IconButton>
                                <IconButton
                                    @click="showRunTestCaseDialog"
                                    icon="ios-build"
                                    :title="$t('执行用例')"></IconButton>
                            </div>
                        </div>
                        <table
                            class="table-content table-content-small table-box"
                            style="margin-top:10px">
                            <tbody>
                            <tr
                                v-for="item in task.testCaseList"
                                :key="'tsk_'+item.id"
                                class="table-row table-row-small">
                                <td class="clickable">
                                    <TaskNameLabel
                                        @click.native="showTaskInfo(item)"
                                        :id="item.serialNo"
                                        :name="item.testCaseName"
                                        :type="5"/>
                                </td>
                                <td style="text-align:right;width:200px;color:#999">
                                    <span class="table-row-hover-hide">
                                        <DataDictLabel
                                            type="TestPlanTestCase.status"
                                            :value="item.status"
                                            style="margin-right:10px"></DataDictLabel>
                                        <AvatarImage
                                            size="small"
                                            :name="item.createAccountName"
                                            :value="item.createAccountImageId"
                                            type="tips"></AvatarImage>
                                    </span>
                                    <span class="table-row-opt" v-if="!projectFinishDisabled">
                                        <IconButton
                                            @click="deleteTestCase(item)"
                                            icon="ios-trash"
                                            :tips="$t('解除关联')"></IconButton>
                                    </span>
                                </td>
                            </tr>

                            </tbody>
                        </table>
                        <div
                            class="table-nodata"
                            v-if="task.testCaseList.length==0">{{$t('暂无用例')}}
                        </div>
                    </template>

                    <template v-if="task.associatedList.length+task.subTaskList.length+task.parentTaskList.length>0">
                        <div class="info-section-row">
                            <div class="info-section-header">{{$t('关联内容')}}
                            </div>
                            <span class="info-section-header-sub">{{task.associatedList.length+task.subTaskList.length+task.parentTaskList.length}}</span>
                            <div class="info-section-opt">

                                <Poptip
                                    v-if="tperm('task_edit') && showTaskLinkBtn"
                                    ref="poptip"
                                    class="poptip-full"
                                    v-model="relPopupBottomVisible">
                                    <IconButton
                                        icon="ios-link"
                                        :title="$t('关联对象')"></IconButton>
                                    <div slot="content">
                                        <div
                                            class="popup-box scrollbox"
                                            style="text-align:left">
                                            <div
                                                @click="showSelectTaskDialog(0)"
                                                class="popup-item-name">
                                                {{$t('关联对象')}}
                                            </div>
                                            <div
                                                @click="showSelectTaskDialog(1)"
                                                class="popup-item-name">
                                                {{$t('前置对象')}}
                                            </div>
                                            <div
                                                @click="showSelectTaskDialog(2)"
                                                class="popup-item-name">
                                                {{$t('后置对象')}}
                                            </div>
                                        </div>
                                    </div>
                                </Poptip>
                                <IconButton
                                    v-if="tperm('task_create')&&hasField('statusName')&&!projectFinishDisabled"
                                    @click="showAddSubTaskDialog"
                                    icon="ios-add"
                                    :title="$t('添加子对象')"></IconButton>
                            </div>
                        </div>

                        <table
                            class="table-content table-color table-content-small table-box"
                            style="margin-top:10px;table-layout:fixed">
                            <tbody>

                            <tr
                                v-for="item in task.parentTaskList"
                                :key="'tsk_'+item.id"
                                class="table-row table-row-small">
                                <td class="clickable text-no-wrap">
                                    <div>
                                        <TaskNameLabel
                                            @click.native="showTaskInfo(item)"
                                            :id="item.serialNo"
                                            :name="item.name"
                                            :is-done="item.isFinish"
                                            :parent="true"
                                            :object-type="item.objectTypeName"/>
                                    </div>
                                    <div style="margin-top:3px">
                                        <TaskOwnerView :value="item"/>
                                    </div>
                                </td>
                                <td
                                    style="text-align:right;width:250px;color:#999"
                                    class="text-no-wrap">
                                     <span class="table-row-hover-hide">
                                    <div>
                                        <TaskStatus
                                            :label="item.statusName"
                                            :color="item.statusColor"></TaskStatus>
                                    </div>
                                    <div style="margin-top:3px">
                                        <template v-if="item.startDate">
                                            {{item.startDate|fmtDate}}
                                        </template>
                                        <Icon
                                            v-if="item.startDate!=null||item.endDate!=null"
                                            type="md-arrow-round-forward"/>
                                        <template v-if="item.endDate">
                                            {{item.endDate|fmtDate}}
                                            <OverdueLabel
                                                v-if="!item.isFinish&&item.endDate"
                                                style="margin-left:5px"
                                                :value="item.endDate"></OverdueLabel>
                                        </template>
                                    </div>
                                     </span>
                                    <span class="table-row-opt" v-if="!projectFinishDisabled">
                                        <IconButton
                                            @click="deleteParentTask(item)"
                                            icon="ios-trash"
                                            :tips="$t('解除父对象')"></IconButton>
                                    </span>
                                </td>
                            </tr>

                            <tr
                                v-for="item in task.subTaskList"
                                :key="'tsk_'+item.id"
                                class="table-row table-row-small">
                                <td class="clickable text-no-wrap">
                                    <div>
                                        <TaskNameLabel
                                            @click.native="showTaskInfo(item)"
                                            :id="item.serialNo"
                                            :name="item.name"
                                            :is-done="item.isFinish"
                                            :sub="true"
                                            :object-type="item.objectTypeName"/>
                                    </div>
                                    <div style="margin-top:3px">
                                        <TaskOwnerView :value="item"/>
                                    </div>
                                </td>
                                <td
                                    style="text-align:right;width:250px;color:#999"
                                    class="text-no-wrap">
                                    <span class="table-row-hover-hide">
                                        <div><TaskStatus
                                            :label="item.statusName"
                                            :color="item.statusColor"></TaskStatus></div>
                                         <div style="margin-top:3px">
                                                    <template v-if="item.startDate">
                                                        {{item.startDate|fmtDate}}
                                                    </template>
                                                    <Icon
                                                        v-if="item.startDate!=null||item.endDate!=null"
                                                        type="md-arrow-round-forward"/>
                                                     <template v-if="item.endDate">
                                                        {{item.endDate|fmtDate}}
                                                        <OverdueLabel
                                                            v-if="!item.isFinish&&item.endDate"
                                                            style="margin-left:5px"
                                                            :value="item.endDate"></OverdueLabel>
                                            </template>
                                        </div>
                                    </span>
                                    <span class="table-row-opt" v-if="!projectFinishDisabled">
                                        <IconButton
                                            @click="deleteSubTask(item)"
                                            icon="ios-trash"
                                            :tips="$t('解除子对象')"></IconButton>
                                    </span>
                                </td>
                            </tr>

                            <tr
                                v-for="item in task.associatedList"
                                :key="'ass_'+item.id"
                                class="table-row table-row-small">
                                <td class="clickable">
                                    <div class=" text-no-wrap">
                                        <TaskNameLabel
                                            @click.native="showTaskInfo(item)"
                                            :id="item.serialNo"
                                            :name="item.name"
                                            :is-done="item.isFinish"
                                            :rel="item.associatedType"
                                            :object-type="item.objectTypeName"/>
                                    </div>
                                    <div style="margin-top:3px">
                                        <TaskOwnerView :value="item"/>
                                    </div>
                                </td>
                                <td
                                    style="text-align:right;width:250px;color:#999"
                                    class="text-no-wrap">
                                    <span class="table-row-hover-hide">
                                        <div>
                                             <TaskStatus
                                                 v-if="item.statusName!=null"
                                                 :label="item.statusName"
                                                 :color="item.statusColor"></TaskStatus>
                                        </div>
                                        <div style="margin-top:3px">
                                            <template v-if="item.startDate">
                                                {{item.startDate|fmtDate}}
                                            </template>
                                            <Icon
                                                v-if="item.startDate!=null||item.endDate!=null"
                                                type="md-arrow-round-forward"/>
                                         <template v-if="item.endDate">
                                            {{item.endDate|fmtDate}}
                                            <OverdueLabel
                                                v-if="!item.isFinish&&item.endDate"
                                                style="margin-left:5px"
                                                :value="item.endDate"/>
                                            </template>
                                        </div>
                                    </span>
                                    <span class="table-row-opt" v-if="!projectFinishDisabled">
                                        <IconButton
                                            @click="deleteRelObj(item)"
                                            icon="ios-trash"
                                            :tips="$t('解除关联')"></IconButton>
                                    </span>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </template>

                    <div
                        ref="commentHeader"
                        class="info-section-header clickable">
                        <span
                            @click="showLogView='comment'"
                            :class="{'info-section-header-deactive':showLogView!=='comment'}">
                            {{$t('评论')}}
                            <span
                                class="list-count" v-if="commentList.length>0">{{commentList.length}}</span>
                        </span>
                        <span style="padding-left:5px;padding-right:5px">/</span>
                        <span
                            @click="showLogView='changeLog'"
                            :class="{'info-section-header-deactive':showLogView!=='changeLog'}">
                                {{$t('变更记录')}}
                            <span
                                class="list-count"
                                v-if="changeLogList.length>0">{{changeLogList.length}}</span>
                             <Poptip width="300">
                                <Icon type="ios-search"/>
                                <div
                                    class="change-log-select-popup"
                                    slot="content">
                                      <Form ref="form" label-position="top">
                                        <FormItem :label="$t('用户')">
                                            <Input
                                                v-model.trim="filterChangeLogUserName"
                                                :placeholder="$t('所有用户')"/>
                                        </FormItem>
                                        <FormItem :label="$t('记录类型')">
                                             <Select
                                                 :placeholder="$t('所有记录类型')"
                                                 clearable
                                                 transfer
                                                 v-model="filterChangeLogType"
                                                 style="width:200px">
                                                <Option
                                                    v-for="item in changeLogTypeList"
                                                    :value="item.value"
                                                    :key="item.value">{{ item.name }}</Option>
                                            </Select>
                                        </FormItem>
                                    </Form>
                                </div>
                            </Poptip>
                        </span>
                        <template v-if="allStatusLogList.length>0">
                            <span style="padding-left:5px;padding-right:5px">/</span>
                            <span
                                @click="showLogView='statusLog'"
                                :class="{'info-section-header-deactive':showLogView!=='statusLog'}">{{$t('状态记录')}}</span>
                        </template>
                        <template v-if="task.statusChangeLogList&&task.statusChangeLogList.length>0">
                            <span style="padding-left:5px;padding-right:5px">/</span>
                            <span
                                @click="showStatusFlow"
                                :class="{'info-section-header-deactive':showLogView!=='statusFlow'}">{{$t('流转图')}}</span>
                        </template>
                        <template v-if="task.isFreeze||task.alterationId>0">
                            <span style="padding-left:5px;padding-right:5px">/</span>
                            <span
                                @click="showLogView='alterationLog'"
                                :class="{'info-section-header-deactive':showLogView!=='alterationLog'}">
                                {{$t('变更申请')}}
                                <span  class="list-count">{{alterationList.length}}</span>
                            </span>
                        </template>
                        <template v-if="testCaseRunLogCount > 0">
                            <span style="padding-left:5px;padding-right:5px">/</span>
                            <span
                                @click="showLogView='testCaseRunLog'"
                                :class="{'info-section-header-deactive':showLogView!=='testCaseRunLog'}">
                                {{$t('执行记录')}}
                            <span
                                class="list-count"
                                v-if="testCaseRunLogCount > 0">{{testCaseRunLogCount}}</span>
                        </span>
                        </template>
                    </div>
                    <div style="margin-top:20px" v-if="showLogView==='comment'">
                        <TaskViewComment
                            v-for="item in commentList"
                            :value="item"
                            :account="account"
                            :key="'comment_'+item.id"
                            :task="task"
                            @quoteCommentReply="quoteCommentReply(item)"
                            @deleteComment="deleteComment(item)"/>
                        <div class="table-nodata" v-if="commentList.length===0">
                            {{$t('暂无评论')}}
                        </div>
                        <div
                            v-if="commentCount>=commentQuery.pageSize"
                            style="text-align:center;padding:10px">
                            <Button
                                @click="loadCommentMore"
                                size="small"
                                type="text"
                                icon="ios-refresh-circle-outline">
                                {{$t('加载更多')}}
                            </Button>
                        </div>
                    </div>

                    <div
                        style="margin-top:20px"
                        v-if="showLogView==='changeLog'">
                        <template v-for="item in changeLogList">
                            <TaskViewChangeLog
                                v-if="filterChangeLog(item)"
                                :value="item"
                                :key="'log_'+item.id"/>
                        </template>
                    </div>
                    <div
                        style="margin-top:20px"
                        v-if="showLogView==='statusFlow'">
                        <div class="scrollbox" style="overflow:auto;padding: 10px">
                            <div ref="statusgraph" class="statusgraph"></div>
                        </div>
                    </div>
                    <div
                        style="margin-top:20px"
                        v-if="showLogView==='alterationLog'">
                        <table
                            class="table-content table-content-small table-box"
                            style="margin-top:10px">
                            <tbody>
                            <tr
                                v-for="item in alterationList"
                                :key="'alter_'+item.id"
                                class="table-row table-row-small">
                                <td class="clickable"  @click="showAlterationInfo(item)">
                                    {{item.createAccountName}}提交的变更申请
                                </td>
                                <td style="text-align:right;min-width:200px;color:#999">
                                    <span class="table-row">
                                        <span v-if="item.status!=3" style="font-weight: 500;"
                                              :style="{color:item.flowStatusColor,border:'1px solid '+item.flowStatusColor,borderRadius:'15px',padding:'3px',fontSize:'12px'}">
                                            {{item.flowStatusName}}</span>
                                        <span v-if="item.status==3" style="font-weight: 500;color:darkgrey" >已取消</span>
                                        <AvatarImage
                                            style="margin: 0 10px;"
                                            size="small"
                                            :name="item.createAccountName"
                                            :value="item.createAccountImageId"
                                            type="tips"></AvatarImage>
                                        <span>{{item.createTime|fmtDateTime}}</span>
                                    </span>
                                </td>
                            </tr>

                            </tbody>
                        </table>
                    </div>
                    <div
                        style="margin-top:20px"
                        v-if="showLogView==='testCaseRunLog'">
                        <div
                            class="table-nodata"
                            v-if="testCaseRunLogList.length===0">{{$t('暂无数据')}}
                        </div>
                        <table
                            class="table-content table-grid table-content-small"
                            style="border-top:1px solid #eee;">
                            <thead>
                            <tr>
                                <th style="width:140px">执行人</th>
                                <th style="width:140px">执行时间</th>
                                <th style="width:140px">执行状态</th>
                                <th>所属计划</th>
                            </tr>
                            </thead>
                            <tbody>
                            <template v-for="item in testCaseRunLogList">
                                <tr :key="item.id">
                                    <td style="width:140px">
                                        {{ item.createAccountName }}
                                    </td>
                                    <td style="width:140px">
                                        {{ item.createTime | fmtDateTime }}
                                    </td>
                                    <td style="width:140px">
                                        <DataDictLabel
                                            :value="item.status"
                                            type="TestPlanTestCase.status"/>
                                    </td>
                                    <td>
                                        <div
                                            class="text-no-wrap table-col-name"
                                            @click="onClickTestCaseRunLog(item)">
                                            {{ item.testPlanName }}
                                        </div>
                                    </td>
                                </tr>
                            </template>

                            </tbody>
                        </table>
                        <div
                            v-if="testCaseRunLogCount > testCaseRunLogList.length"
                            style="text-align:center;padding:10px">
                            <Button
                                @click="loadTestPlanTestCaseRunLogListMore"
                                size="small"
                                type="text"
                                icon="ios-refresh-circle-outline">
                                {{$t('加载更多')}}
                            </Button>
                        </div>
                    </div>
                    <div
                        style="margin-top:20px"
                        v-if="showLogView==='statusLog'">
                        <table
                            class="table-content table-grid table-content-small"
                            style="border-top:1px solid #eee;">
                            <thead>
                            <tr class="table-row">
                                <th rowspan="2" style="width:140px">
                                    {{$t('状态')}}
                                </th>
                                <th colspan="3" style="text-align:center">
                                    {{$t('详细记录')}}
                                </th>
                            </tr>
                            <tr>
                                <th style="width:140px">时间段</th>
                                <th style="width:140px">状态变更</th>
                                <th>责任人</th>
                            </tr>
                            </thead>
                            <tbody>
                            <template v-for="item in allStatusLogList">
                                <tr
                                    :key="'st'+item.statusName"
                                    class="table-row">
                                    <td :rowspan="item.list.length+1">
                                        <TaskStatus
                                            :label="item.statusName"
                                            :color="item.statusColor"/>
                                        <div style="margin-top:5px">
                                            {{item.leftTime|fmtSeconds}}
                                        </div>
                                    </td>
                                    <td style="padding:0"></td>
                                    <td style="padding:0"></td>
                                    <td style="padding:0"></td>
                                </tr>
                                <tr
                                    :key="'ss'+item.statusName+timelog.id"
                                    v-for="timelog in item.list">
                                    <td :key="'logid'+timelog.id">
                                        <div>{{timelog.enterTime|fmtDateTime}}
                                            <Icon type="md-arrow-round-forward"/>
                                        </div>
                                        <div>{{timelog.leaveTime|fmtDateTime}}
                                        </div>
                                    </td>
                                    <td>
                                        <div><span class="log-user-name">{{timelog.oldStatusName}}</span>
                                            <Icon type="md-arrow-round-forward"/>
                                        </div>
                                        <div><span class="log-user-name">{{timelog.statusName}}</span>
                                        </div>
                                    </td>
                                    <td>
                                        <div>
                                     <span
                                         class="log-user-name"
                                         v-for="o in timelog.beforeOwnerList"
                                         :key="'b'+o.id">
                                        {{o.name}}
                                     </span>
                                            <span
                                                style="font-size:12px"
                                                v-if="timelog.beforeOwnerList==null||timelog.beforeOwnerList.length==0">未设置</span>
                                            <Icon type="md-arrow-round-forward"/>
                                        </div>
                                        <div>
                                     <span
                                         class="log-user-name"
                                         v-for="o in timelog.afterOwnerList"
                                         :key="'t'+o.id">
                                        {{o.name}}
                                     </span>
                                            <span
                                                style="font-size:12px"
                                                v-if="timelog.afterOwnerList==null||timelog.afterOwnerList.length==0">未设置</span>
                                        </div>
                                    </td>
                                </tr>
                            </template>

                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <div class="footer-box" :class="{'footer-box-open':commitBoxOpen}">
                <Row v-if="!projectFinishDisabled">
                    <Col
                        span="2"
                        :style="{paddingTop:commitBoxOpen?'20px':'0px'}">
                        <AvatarImage
                            :name="account.name"
                            :value="account.imageId"
                            type="tips"/>
                    </Col>
                    <Col span="22">
                        <div
                            class="fixed-height-editor"
                            v-show="commitBoxOpen"
                            style=";padding-bottom:10px;padding-top:20px">
                            <RichtextEditor
                                v-if="mentionList.length>0"
                                v-model="replyContent"
                                :mention="mentionList"
                                ref="commentEditor"/>
                            <div style="margin-top:10px;text-align:right">
                                <span style="color:#999;padding-right:10px">{{$t('输入@提醒项目成员')}}</span>
                                <Button
                                    style="margin-right:10px"
                                    type="text"
                                    @click="commitBoxOpen=false">
                                    {{$t('取消')}}
                                </Button>
                                <Button type="default" @click="replyComment">
                                    {{$t('提交')}}
                                </Button>
                            </div>
                        </div>

                        <div
                            v-show="!commitBoxOpen"
                            @click="commitBoxOpen=true"
                            class="commit-box">
                            {{$t('评论内容，输入@提醒项目成员，ctrl+enter提交')}}
                        </div>
                    </Col>
                </Row>
            </div>

        </div>
    </div>
</template>
<script>
    import DataDictLabel from '../../../../components/ui/DataDictLabel';
    import TaskSelectAttachmentView from "./TaskSelectAttachmentView";

    export default {
        components: {TaskSelectAttachmentView, DataDictLabel},
        props: ['taskId', 'taskList', 'showType', 'showTopBar'],
        mixins: [componentMixin],
        data() {
            return {
                haveRemind: false,
                taskUUID: this.taskId,
                permissionMap: {},
                title: 'TaskView',
                loading: false,
                account: {},
                oldTask: {},
                currentTaskIndex: 0,
                showLogView: 'comment',
                descEdit: false,
                showWorkTimeBox: false,
                commitBoxOpen: false,
                task: {
                    attachmentList: [],
                    associatedList: [],
                    subTaskList: [],
                    parentTaskList: [],
                    workTimeLogList: [],
                    categoryIdList: [],
                    testCaseList: [],
                    startDate: null,
                    endDate: null
                },
                showInfoBox: false,
                mentionList: [],
                replyContent: null,
                editInfo: {},
                commentQuery: {
                    pageIndex: 1,
                    pageSize: 50,
                },
                testCaseRunLogQuery: {
                    pageIndex: 1,
                    pageSize: 50,
                },
                commentList: [],
                commentCount: 0,
                testCaseRunLogList: [],
                testCaseRunLogCount: 0,
                changeLogQuery: {
                    pageIndex: 1,
                    pageSize: 500,
                },
                statusSvg: "",
                changeLogList: [],
                allStatusLogList: [],
                changeLogCount: 0,
                alterationList:[],
                filterChangeLogType: null,
                filterChangeLogUserName: null,
                changeLogTypeList: [
                    {name: this.$t('创建对象'), value: 1},
                    {name: this.$t('编辑属性'), value: 2},
                    {name: this.$t('上传附件'), value: 3},
                    {name: this.$t('删除附件'), value: 4},
                    {name: this.$t('新增关联对象'), value: 5},
                    {name: this.$t('取消关联对象'), value: 6},
                    {name: this.$t('新增子对象'), value: 7},
                    {name: this.$t('删除子对象'), value: 8},
                    {name: this.$t('修改详细描述'), value: 9},
                    {name: this.$t('提交代码'), value: 20},
                ],
                relPopupTopVisible: false,
                relPopupBottomVisible: false,
                isProjectSetTask: false,
                taskAttachment: null,
                headerAttachmentVisiable: false,
                contentAttachmentVisiable: false,
                startDateOptions: {
                    end: null,
                    disabledDate: (date) => {
                        if (this.task.endDate == null) {
                            return false;
                        }
                        return date && date.valueOf() > this.task.endDate;
                    },
                },
                endDateOptions: {
                    start: null,
                    disabledDate: (date) => {
                        if (this.task.startDate == null) {
                            return false;
                        }
                        return date && date.valueOf() < this.task.startDate;
                    },
                },
                projectFinishDisabled:false,
                isProjectSetTaskOwner:false
            };
        },
        computed: {
            showAlteration() {
                //项目集也禁用变更
                if (!this.task||this.isProjectSetTask) {
                    return false;
                }

                return !this.projectFinishDisabled&&!this.task.isFreeze &&!this.task.isDelete&&!this.task.isFinish
                    && this.task.ownerAccountIdList && this.task.ownerAccountIdList.indexOf(app.account.id) >= 0;
            },
            showGoToProject() {
                if (!this.task) {
                    return false;
                }
                return !this.projectFinishDisabled&&!!this.task.associateProjectUuid;
            },
            propDisable() {
                if (this.task.isFinish||this.projectFinishDisabled) {
                    return true;
                }
                if (!this.tperm('task_edit')) {
                    return true;
                }
                if (this.isProjectSetTask) {
                    return true;
                }
                return false;
            },
            customFieldPropDisable() {
                if (this.task.isFinish||this.projectFinishDisabled) {
                    return true;
                }
                if(this.projectSetTask&&this.isProjectSetTaskOwner){
                    return true;
                }
                if (!this.tperm('task_edit')) {
                    return true;
                }
                return false;
            },
            statusDisable() {
                if (!this.tperm('task_change_status')||this.projectFinishDisabled) {
                    return true;
                }
                return false;
            },
            categoryDisable() {
                if (this.task.isFinish||this.projectFinishDisabled) {
                    return true;
                }
                if (!this.tperm('task_edit_category')) {
                    return true;
                }
                return false;
            },
            memberDisable() {
                if (this.task.isFinish||this.projectFinishDisabled) {
                    return true;
                }
                if (!this.tperm('task_change_owner')) {
                    return true;
                }
                return false;
            },
            showTaskLinkBtn() {
                if (!this.task||this.projectFinishDisabled) {
                    return false;
                }
                return this.task.objectTypeSystemName !== process.env.VUE_APP_PROJECT_SET_OBJECT_TYPE_SYSTEM_NAME_PRO;
            },
            taskEditStartEndDateDisable() {
                return this.propDisable || !this.tperm(`task_edit_startend_date`)||this.projectFinishDisabled;
            },
            finishTimeDisable() {
                if (!this.tperm('task_edit')||this.projectFinishDisabled) {
                    return true;
                }
                if (this.isProjectSetTask) {
                    return true;
                }
                return !this.tperm(`task_edit_finish_date`);
            },
        },
        watch: {
            taskId: function (val) {
                this.taskUUID = val;
            },
            taskUUID: function (val) {
                this.$refs.viewBox.scrollTop = 0;
                this.loadData();
            },
            descEdit: function (val) {
                app.taskEditing = val;
                if (val == true) {
                    this.$nextTick(() => {
                        this.$refs.descEditor.focus();
                        app.postMessage("task.editing", {id: this.task.uuid})
                    });
                } else {
                    app.postMessage("task.editing", {id: 0})
                }
            },
            commitBoxOpen: function (val) {
                app.taskEditing = val;
                if (val == true) {
                    this.$nextTick(() => {
                        this.$refs.commentEditor.focus();
                    });
                }
            },
             'task.startDate': function (val) {
                 // console.log("task.startDate--->",val)
                 if (val != null) {
                     this.$set(this.endDateOptions, "start", new Date(val))
                 }else{
                     this.$set(this.endDateOptions, "start", null)
                 }
             },
             'task.endDate': function (val) {
                // console.log("task.endDate--->",val)
                 if (val != null) {
                     this.$set(this.startDateOptions, "end", new Date(val))
                 }else{
                     this.$set(this.startDateOptions, "end",null)
                 }
             },
        },
        beforeDestroy() {
            app.taskEditing = false;
            if (this.checkInterval) {
                clearInterval(this.checkInterval);
                this.removeKeyboardListener();
            }
        },
        mounted() {
            this.account = app.account;
            this.loading = true;
            this.loadData();
            this.checkInterval = setInterval(this.syncComment, 10 * 1000);
            //
            if (this.taskList != null) {
                for (var i = 0; i < this.taskList.length; i++) {
                    if (this.taskList[i].uuid == this.taskId) {
                        this.currentTaskIndex = i;
                        break;
                    }
                }
            }
            this.addKeyboardListener();
        },
        methods: {
            loadData() {
                app.invoke('BizAction.getTaskInfoByUuid', [app.token, this.taskUUID], info => {
                    this.permissionMap = {};
                    for (var i = 0; i < info.permissionList.length; i++) {
                        this.permissionMap[info.permissionList[i]] = true;
                    }
                    if (info.task.customFields == null) {
                        info.task.customFields = {};
                    }
                    this.haveRemind = info.haveRemind;
                    // 获取旧的任务内容
                    let editTaskContent = this.task.content;
                    if (this.task.id !== info.task.id) {
                        editTaskContent = null;
                    }
                    this.task = info.task;
                    this.task.star = info.haveStar;
                    this.projectFinishDisabled = this.isProjectDisabled(info.task.projectId);
                    //项目集任务
                    this.isProjectSetTask = info.task.objectTypeSystemName ===process.env.VUE_APP_PROJECT_SET_OBJECT_TYPE_SYSTEM_NAME_PRO;
                    this.isProjectSetTaskOwner =this.isProjectSetTask&& !Array.isEmpty(info.task.ownerAccountIdList)&&info.task.ownerAccountIdList.some(item=>item===app.account.id)
                    if (!!editTaskContent && editTaskContent !== info.task.content) {
                        this.task.content = editTaskContent;
                    }
                    this.copyTaskToOldTask();
                    this.oldTaskName = info.task.name;
                    this.editInfo = info.editTaskInfo;
                    this.computeStatusLogList();
                    //
                    this.mentionList = [];
                    for (var j = 0; j < info.editTaskInfo.memberList.length; j++) {
                        var mem = info.editTaskInfo.memberList[j];
                        this.mentionList.push({
                            id: mem.accountId,
                            name: mem.accountName + '(' + mem.accountUserName + ')',
                            pinyin: mem.accountPinyinName,
                        });
                    }
                    var fieldCount = 0;
                    for (var k = 0; k < info.editTaskInfo.fieldList.length; k++) {
                        var f = info.editTaskInfo.fieldList[k];
                        if (this.isSystemOtherField(f)) {
                            fieldCount++;
                        }
                        if (f.isSystemField === false) {
                            fieldCount++;
                        }
                    }
                    this.showInfoBox = fieldCount > 0;
                    //
                    this.loading = false;
                    this.loadComment();
                    this.loadChangeLog();
                    //如果是测试用例则加载测试用例的执行记录
                    this.testCaseRunLogList = [];
                    if (this.task.objectTypeSystemName === process.env.VUE_APP_TEST_CASE_OBJECT_TYPE_SYSTEM_NAME_TCA) {
                        this.loadTestPlanTestCaseRunLogList();
                    }
                    //
                    this.loadSyncComment();
                    this.loadAlterationList();
                }, () => {
                    this.$emit('close');
                });
            },
            computeStatusLogList() {
                var list = this.task.statusChangeLogList;
                var statusMap = {};
                var allStatusLogList = [];
                list.forEach(element => {
                    if (statusMap[element.statusName] == null) {
                        var temp = {
                            list: [],
                            statusName: element.statusName,
                            statusColor: element.statusColor,
                        };
                        statusMap[element.statusName] = temp;
                        allStatusLogList.push(temp);
                    }
                    statusMap[element.statusName].list.push(element);
                });
                allStatusLogList.forEach(item => {
                    item.leftTime = 0;
                    item.list.forEach(t => {
                        var a = new Date().getTime();
                        if (t.leaveTime) {
                            a = t.leaveTime;
                        }
                        item.leftTime += (a - t.enterTime);
                    });
                });
                this.allStatusLogList = allStatusLogList;
            },
            filterChangeLog(item) {
                var ret = true;
                if (this.filterChangeLogType != null) {
                    ret = this.filterChangeLogType === item.type;
                }
                if (ret === false) {
                    return ret;
                }
                if (this.filterChangeLogUserName != null) {
                    ret = item.createAccountName.indexOf(this.filterChangeLogUserName) > -1;
                }
                return ret;
            },
            showTaskAlarmDialog() {
                app.showDialog(TaskAlarmDialog, {
                    task: this.task,
                    editInfo: this.editInfo,
                    callback: () => {
                        this.loadData();
                    },
                });
            },
            showDetailDialog() {
                app.showDialog(TaskDetailDialog, {
                    task: this.task,
                    propDisable: this.propDisable,
                    updateTaskContent: this.updateTaskContent,
                });
            },
            updateTaskContent(task) {
                this.task.content = task.content;
                this.updateTask('content');
            },
            copySuccess() {
                app.toast(this.$t('ID已复制到剪切板'));
            },
            removeKeyboardListener() {
                document.removeEventListener('keyup', this.handleKeyboard);
            },
            addKeyboardListener() {
                document.addEventListener('keyup', this.handleKeyboard);
            },
            tperm(list) {
                if(this.task.isFreeze){
                    return false;
                }
                //1-超级boss（读写） 2-超级boss（只读）
                if (app.account.superBoss) {
                    if (app.account.superBoss == 1 || app.account.superBoss == 3) {
                        return true;
                    } else if (app.account.superBoss == 2 || app.account.superBoss == 4) {
                        if (Array.isArray(list)) {
                            var readPermissionCount = list.filter(item => item.indexOf("_list") > -1 || item.indexOf("_view") > 0 || item.indexOf("_log") > 0).length;
                            if (readPermissionCount > 0) {
                                return true;
                            }
                        } else {
                            if (list.indexOf("_list") > 0 || list.indexOf("_view") > 0 || list.indexOf("_log") > 0) {
                                return true;
                            }
                        }
                    }
                }
                if (Array.isArray(list)) {
                    const index = list.findIndex(t => {
                        return this.permissionMap[t + '_' + this.task.objectType] === true;
                    });
                    return index > -1;
                }
                return this.permissionMap[list + '_' + this.task.objectType] === true;
            },
            isSystemOtherField(item) {
                var excludesList = [
                    'startDays',
                    'endDays',
                    'iterationName',
                    'stageName',
                    'categoryIdList',
                    'name',
                    'ownerAccountName',
                    'createAccountName',
                    'priorityName',
                    'statusName',
                ];
                for (var i = 0; i < excludesList.length; i++) {
                    var t = excludesList[i];
                    if (t === item.field) {
                        return false;
                    }
                }
                return true;
            },
            loadSyncComment() {
                var t = app.loadObject('TaskView.comment' + this.task.id);
                if (t && t.content) {
                    this.replyContent = t.content;
                }
            },
            syncComment() {
                if (this.replyContent == null || this.replyContent === '') {
                    app.deleteObject('TaskView.comment' + this.task.id);
                } else {
                    app.saveObject('TaskView.comment' + this.task.id, {content: this.replyContent});
                }
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
            //
            copyTaskToOldTask() {
                this.oldTask = Object.assign({}, this.task);
                this.oldTask.customFields = Object.assign({}, this.task.customFields);
            },
            //
            editDesc() {
                if (this.propDisable) {
                    return;
                }
                this.descEdit = true;
            },
            loadChangeLog() {
                this.changeLogQuery.taskId = this.task.id;
                app.invoke('BizAction.getChangeLogList', [app.token, this.changeLogQuery], list => {
                    list.map(item => {
                        if (item.items !== '' && item.items != null) {
                            item.items = JSON.parse(item.items);
                        } else {
                            item.items = {};
                        }
                    });
                    if (this.changeLogQuery.pageIndex !== 1) {
                        list.map(item => {
                            this.changeLogList.push(item);
                        });
                    } else {
                        this.changeLogList = list;
                    }
                    this.changeLogCount = list.length;
                });
            },
            loadComment() {
                this.commentQuery.taskId = this.task.id;
                app.invoke('BizAction.getTaskCommentInfoList', [app.token, this.commentQuery], list => {
                    if (this.commentQuery.pageIndex !== 1) {
                        list.forEach(item => {
                            this.commentList.push(item);
                        });
                    } else {
                        this.commentList = list;
                    }
                    this.commentCount = list.length;
                    //
                    if (this.commentList.length === 0) {
                        this.showLogView = 'changeLog';
                    } else {
                        this.showLogView = 'comment';
                    }
                });
            },
            loadCommentMore() {
                this.commentQuery.pageIndex++;
                this.loadComment();
            },
            loadTestPlanTestCaseRunLogList() {
                this.testCaseRunLogQuery.testCaseId = this.task.id;
                app.invoke('BizAction.getTestPlanTestCaseRunLogList', [app.token, this.testCaseRunLogQuery], res => {
                    let list = res.list;
                    if (!Array.isArray(res.list)) {
                        list = [];
                    }
                    if (this.testCaseRunLogList.pageIndex !== 1) {
                        list.forEach(item => {
                            this.testCaseRunLogList.push(item);
                        });
                    } else {
                        this.testCaseRunLogList = list;
                    }
                    this.testCaseRunLogCount = res.count > 0 ? res.count : 0;
                });
            },
            loadTestPlanTestCaseRunLogListMore() {
                this.testCaseRunLogQuery.pageIndex++;
                this.loadComment();
            },
            onClickTestCaseRunLog(item) {
                app.loadPage('/pm/project/' + this.task.projectUuid + '/task4?id=' + item.testPlanUuid);
            },
            updateTask(field) {
                this.$nextTick(() => {
                    this.updateTask0(field);
                });
            },
            updateTask0(field) {
                var obj = {};
                if (field == 'status') {
                    if (this.oldTask.content !== this.task.content) {
                        // console.log("task content not change--->")
                        this.$set(this.task, "status", this.oldTask.status)
                        app.toast(this.$t('请先保存详情描述再变更状态'));
                        return false;
                    }
                }
                if (field == 'content') {
                    obj.content = this.$refs.descEditor.getValue();
                    this.task.content = obj.content;
                }
                if (field.indexOf('field_') != -1) {
                    var newValue = this.task.customFields[field];
                    if (!Array.isArray(newValue)) {
                        if (this.oldTask.customFields[field] == this.task.customFields[field]) {
                            return;
                        }
                    }
                } else {
                    if (this.oldTask[field] == this.task[field]) {
                        return;
                    }
                }
                obj.id = this.task.id;
                if (field.indexOf('field_') == -1) {
                    obj[field] = this.task[field];
                } else {
                    obj.customFields = {};
                    obj.customFields[field] = this.task.customFields[field];
                }
                if (this.$refs.descEditor.isUploading()) {
                    app.toast(this.$t('正在上传图片中，请稍后'));
                    return;
                }

                if (field == 'startDate' || field == 'endDate') {
                    if (this.task.subTaskCount > 0) {
                        app.confirm('该对象关联有子对象，是否需要同步其开始截止时间?', () => {
                            this.updateTaskNative(obj, [field], false);
                        }, () => {
                            this.updateTaskNative(obj, [field], true);
                        }, '需要', '不需要');
                        return;
                    } else {
                        app.invoke('BizAction.getTaskAssociateStat', [app.token, this.task.id], info => {
                            if (info.beforeCount > 0 || info.afterCount > 0) {
                                app.confirm('该对象关联有前后置对象，是否需要同步其开始截止时间?', () => {
                                    this.updateTaskNative(obj, [field], false);
                                }, () => {
                                    this.updateTaskNative(obj, [field], true);
                                }, '需要', '不需要');
                            } else {
                                this.updateTaskNative(obj, [field], true);
                            }
                        }, (error) => {
                            // this.loadData();
                        });
                    }
                } else {
                    this.updateTaskNative(obj, [field], true);
                }
            },
            pageMessage(type,content){
                if(type=='task.freeze'){
                    this.loadData();
                }
            },
            //关联任务的时间调整方式，手动-不影响后置任务、子任务 自动-影响后置任务、子任务
            updateTaskNative(obj, field, isManualUpdate) {
                app.invoke('BizAction.updateTask', [app.token, obj, field, isManualUpdate], info => {
                    if (field == 'status') {
                        app.postMessage('task.edit');
                        this.loadData();
                    }
                    if (field == 'content') {
                        app.postMessage('task.edit');
                        app.postMessage("task.editing", {id: 0})
                    }
                    if (field == 'startDate' || field == 'endDate') {
                        app.postMessage('task.edit',obj.id);
                        this.loadData();
                    } else {
                        app.postMessage('task.edit');
                        this.copyTaskToOldTask();
                        this.loadChangeLog();
                    }
                }, (error) => {
                    this.loadData();
                });
            },
            uploadSuccess(uuid) {
                app.invoke('BizAction.addTaskAttachment', [app.token, uuid.uuid, this.task.id], info => {
                    this.loadData();
                });
            },
            deleteAttachment(item, type) {
                app.confirm(this.$t('确定要删除附件', [item.name]), () => {
                    app.invoke('BizAction.deleteTaskAttachment', [app.token, this.task.id, item.id, type], info => {
                        this.loadData();
                    });
                });
            },
            downloadAttachment(item) {
                app.downloadFile(item.uuid);
            },
            previewAttachment(item) {
                app.previewFile(item.uuid);
            },
            showAddWorkTimeDialog() {
                app.showDialog(TaskWorkTimeAddDialog, {
                    taskId: this.task.id,
                    callback: () => {
                        this.loadData();
                        app.postMessage('task.edit');
                    },
                });
            },
            editWorkTimeItem(item) {
                app.showDialog(TaskWorkTimeAddDialog, {
                    taskId: this.task.id,
                    item: item,
                    callback: () => {
                        this.loadData();
                        app.postMessage('task.edit');
                    },
                });
            },
            onClickGoToProject() {
                this.gotoProject(this.task.associateProjectUuid, () => {
                    this.$emit("close");
                });
            },
            onClickDownloadAsPdf() {
                const url = [app.serverAddr];
                url.push('/p/file/download_task_pdf/');
                url.push(this.taskUUID);
                url.push('?token=');
                url.push(app.token);
                window.open(url.join(''));
            },
            showAlterationDialog() {
                app.showDialog(TaskAlterationCreateDialog, {
                    uuid:this.task.uuid,
                    taskId:this.task.id,
                    projectId:this.task.projectId,
                    objectType:this.task.objectType,
                    callback: this.loadData,
                });
            },
            showAddSubTaskDialog() {
                app.showDialog(TaskCreateDialog, {
                    projectId: this.task.projectId,
                    iterationId: this.task.iterationId,
                    type: this.task.objectType,
                    objectTypeSystemName: this.task.objectTypeSystemName,
                    parentId: this.task.id,
                    startDate: this.task.startDate,
                    endDate: this.task.endDate,
                    callback: this.loadData,
                });
            },
            showAddTestCaseDialog() {
                app.showDialog(TaskCreateDialog, {
                    projectId: this.task.projectId,
                    type: 5,
                    parentId: this.task.id,
                    callback: this.loadData,
                });
            },
            showSelectTaskDialog(type) {
                this.relPopupTopVisible = false;
                this.relPopupBottomVisible = false;
                if (type > 0) {
                    var startDate, endDate;
                    if (type == 1 && !this.task.startDate) {
                        // app.toast("请先设置当前对象的开始时间");
                        // return false;
                    }
                    if (type == 2 && !this.task.endDate) {
                        app.toast("请先设置当前对象的截止时间");
                        return false;
                    }
                    //前置关联
                    if (type == 1 && this.task.startDate) {
                        endDate = this.task.startDate - 24 * 3600 * 1000;
                    }
                    if (type == 2 && this.task.endDate) {
                        startDate = this.task.endDate + 24 * 3600 * 1000;
                    }
                    app.showDialog(TaskSelectDialog, {
                        singleProject:this.task.objectType==4||this.task.objectType==5,//测试计划和测试用例只能关联本项目
                        objectType: this.task.objectType,
                        projectId: this.task.projectId,
                        startDate: startDate,
                        endDate: endDate,
                        associateType: type,
                        callback: (list) => {
                            this.addRelTask(type, list);
                        },
                    });
                } else {
                    app.showDialog(TaskSelectDialog, {
                        singleProject:this.task.objectType==4||this.task.objectType==5,
                        projectId: this.task.projectId,
                        callback: (list) => {
                            this.addRelTask(type, list);
                        },
                    });
                }
            },
            showSelectTestCaseDialog() {
                app.showDialog(TaskSelectDialog, {
                    singleProject:true,
                    projectId: this.task.projectId,
                    objectType: 5,
                    callback: this.addRelTestCase,
                });
            },
            addRelTestCase(list) {
                if (list.length === 0) {
                    return;
                }
                var ids = list.map(item => {
                    return item.id;
                });
                app.invoke('BizAction.addTestPlanTestCaseList', [app.token, this.task.id, ids], info => {
                    app.postMessage('task.edit');
                    this.loadData();
                });
            },
            addRelTask(type, list) {
                if (list.length === 0) {
                    return;
                }
                var ids = list.map(item => {
                    return item.id;
                });
                app.invoke('BizAction.addTaskAssociatedList', [app.token, this.task.id, type, ids], info => {
                    app.postMessage('task.edit');
                    this.loadData();
                });
            },
            deleteTestCase(item) {
                app.confirm(this.$t('确定要解除关联', [item.testCaseName]), () => {
                    app.invoke('BizAction.deleteTestPlanTestCase', [app.token, item.id], info => {
                        app.postMessage('task.edit');
                        this.loadData();
                    });
                });
            },
            deleteRelObj(item) {
                app.confirm(this.$t('确定要解除关联', [item.name]), () => {
                    app.invoke('BizAction.deleteTaskAssociated', [app.token, item.id], info => {
                        app.postMessage('task.edit');
                        this.loadData();
                        app.toast(this.$t('已解除关联', [item.name]));
                    });
                });
            },
            //解除子任务
            deleteSubTask(item) {
                app.confirm(this.$t('确定要解除', [item.name]), () => {
                    app.invoke('BizAction.dismissPubSubTask', [app.token, item.id], info => {
                        app.toast(this.$t('已解除', [item.name]));
                        app.postMessage('task.edit');
                        this.loadData();

                    });
                });
            },
            //解除父任务
            deleteParentTask(item) {
                app.confirm(this.$t('确定要解除', [item.name]), () => {
                    app.invoke('BizAction.dismissPubSubTask', [app.token, this.task.id], info => {
                        app.toast(this.$t('已解除', [item.name]));
                        app.postMessage('task.edit');
                        this.loadData();

                    });
                });
            },

            deleteTask(item) {
                app.confirm(this.$t('确定要删除', [item.name]), () => {
                    app.invoke('BizAction.deleteTask', [app.token, item.id], info => {
                        app.toast(this.$t('已删除', [item.name]));
                        app.postMessage('task.edit');
                        app.postMessage('task.delete');
                        this.$emit('close');
                    });
                });
            },
            showTaskInfo(item) {
                app.showDialog(TaskDialog, {
                    taskId: item.uuid,
                });
            },
            replyComment() {
                this.commitBoxOpen = false;
                this.$refs.commentEditor.sync();
                if (this.$refs.commentEditor.isUploading()) {
                    app.toast(this.$t('正在上传文件，请稍后'));
                    return;
                }
                this.replyContent = this.$refs.commentEditor.getValue();
                if (this.replyContent != null && this.replyContent != '') {
                    var comment = {
                        taskId: this.task.id,
                        comment: this.replyContent,
                    };
                    app.invoke('BizAction.addTaskComment', [app.token, comment], info => {
                        this.$refs.commentHeader.scrollIntoView();
                        this.loadComment();
                        this.replyContent = null;
                        this.$refs.commentEditor.setValue('<p></p>');
                        this.syncComment();
                    });
                }
            },
            quoteCommentReply(item) {
                this.commitBoxOpen = true;
                this.replyContent = '<blockquote >' +
                    '<div>' + item.createAccountName + '</div>'
                    + item.comment + '</blockquote>';
                this.replyContent += '<div><a href="javascript:;" data-mention="true">@'
                    + item.createAccountName
                    + '(' + item.createAccountUserName + ')' + '</a>&nbsp;</div>';
            },
            deleteComment(item) {
                app.confirm(this.$t('确定要删除评论', [item.createAccountName]), () => {
                    app.invoke('BizAction.deleteTaskComment', [app.token, item.id], info => {
                        this.loadComment();
                    });
                });
            },

            showRunTestCaseDialog() {
                app.showDialog(TestPlanDialog, {
                    uuid: this.task.uuid,
                });
            },
            showCopyTaskDialog() {
                var itemList = [this.task];
                app.showDialog(TaskCopyDialog, {
                    itemList: itemList,
                    objectType: this.task.objectType,
                    projectId: this.task.projectId,
                    iterationId: this.task.iterationId,
                });
            },
            gotoProjectTaskList() {
                this.$emit('close', {
                    type: 'goto',
                });
                var args = {};
                if (this.task.iterationId > 0) {
                    args.iterationId = this.task.iterationId;
                }
                app.loadPage('/pm/project/' + this.editInfo.project.uuid + '/task' + this.task.objectType, args);
            },
            showNextTask(delta) {
                this.currentTaskIndex += delta;
                if (this.currentTaskIndex < 0) {
                    this.currentTaskIndex = 0;
                }
                if (this.currentTaskIndex > this.taskList.length - 1) {
                    this.currentTaskIndex = this.taskList.length - 1;
                }
                this.taskUUID = this.taskList[this.currentTaskIndex].uuid;
            },
            handleKeyboard(e) {
                if (e.keyCode === 27) {//ese
                    if (this.commitBoxOpen || this.descEdit) {
                        return;
                    }
                    this.$emit('close');
                }
            },
            selectAttachmentFile(f) {
                this.taskAttachment = null;
                // console.log("selectAttachmentFile-->", f)
                this.taskAttachment = f;
            },
            cancelTaskAttachment() {
                this.contentAttachmentVisiable = false;
                this.headerAttachmentVisiable = false;
                this.taskAttachment = null;
            },
            addTaskAttachment() {
                this.contentAttachmentVisiable = false;
                this.headerAttachmentVisiable = false;
                if (!this.taskAttachment) {
                    return;
                }
                if (this.taskAttachment.type === 'wiki') {
                    app.invoke('BizAction.addTaskWiki', [app.token, this.taskAttachment.uuid, this.task.id], info => {
                        this.taskAttachment = null;
                        this.loadData();
                    });
                } else {
                    app.invoke('BizAction.addTaskAttachment', [app.token, this.taskAttachment.uuid, this.task.id], info => {
                        this.taskAttachment = null;
                        this.loadData();
                    });
                }
            },
            previewWikiPage(wikiPage) {
                app.invoke('BizAction.getWikiPageById', [app.token, wikiPage.id], (info) => {
                    if (info && info.isDelete) {
                        app.toast(this.$t("页面已删除，无法查看"));
                        return false;
                    } else {
                        app.showDialog(WikiPageDialog, {wiki: wikiPage.id});
                    }
                });
            },
            changeDesc() {
                //  console.log("task content change--->")
            },
            loadTaskStatusFlow() {
                let statusList = this.task.statusChangeLogList;
                if (!statusList || Array.isEmpty(statusList)) {
                    return;
                }
                let statusMap = new Map();
                let s = `digraph workflow{
                        fontsize = 9;
                        node [shape="record" fontsize="9"];
                        edge [style="dashed"];
                `;
                s += '未设置[style="filled", color="#bbb", fillcolor="#ffffff"]\n';
                statusMap.set("未设置", "#bbb");
                statusList.forEach(item => {
                    if (item.oldStatusName) {
                        if (!statusMap.get(item.oldStatusName)) {
                            s += item.oldStatusName + '[style="filled", color="' + item.oldStatusColor + '", fillcolor="#ffffff"]\n';
                            statusMap.set(item.oldStatusName, item.oldStatusColor)
                        }
                    }
                    if (item.statusName) {
                        if (!statusMap.get(item.statusName)) {
                            s += item.statusName + '[style="filled", color="' + item.statusColor + '", fillcolor="#ffffff"]\n';
                            statusMap.set(item.statusName, item.statusColor)
                        }
                    }
                });
                statusList.reverse().forEach(toItem => {
                    if (!toItem.oldStatusName) {
                        s += '\n未设置'
                    } else {
                        s += '\n' + toItem.oldStatusName
                    }
                    s += ' -> ' + toItem.statusName + ';\n';
                });
                s += '\n}';
                var svg = Viz(s);
                var target = document.getElementsByClassName("statusgraph")[0];
                target.innerHTML = svg;
                // this.$refs["statusgraph"].innerHTML = svg;
            },
            showStatusFlow() {
                this.showLogView = 'statusFlow';
                this.$nextTick(() => {
                    this.loadTaskStatusFlow();
                })
            },
            loadAlterationList(){
                app.invoke("TaskAlterationAction.getTaskAlterationList", [app.token, {taskId:this.task.id}], list => {
                    this.alterationList = list;
                });
            },
            showAlterationInfo(item){
                app.showDialog(TaskAlterationInfoDialog,{
                    taskId:item.taskId,
                    objectType:item.objectType,
                    projectId:item.projectId,
                    id:item.id
                })
            },
            toggleTaskStar(){
                let action = "BizAction.addAccountStar";
                if(this.task.star){
                    action = "BizAction.deleteAccountStar";
                }
                app.invoke(action, [app.token, {type:2,associateId:this.task.id}], () => {
                    this.task.star = !this.task.star;
                    this.$forceUpdate();
                    app.postMessage("task.star");
                    app.toast("操作成功");
                });
            }
        },
    };
</script>
