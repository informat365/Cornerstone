<style scoped>
    .page {
        min-height: 400px;
    }

    .project-page-content {
        width: 1200px;
        display: inline-block;
        text-align: left;
        margin-bottom: 50px;
    }

    .project-page-box {
        box-shadow: 0px 2px 10px 0px rgba(225, 225, 225, 0.5);
        background-color: #fff;
        padding: 30px;
        border-radius: 2px;
    }

    .project-page-box-no-padding {
        box-shadow: 0px 2px 10px 0px rgba(225, 225, 225, 0.5);
        background-color: #fff;
        border-radius: 2px;
    }

    .project-name-label {
        color: #333;
        font-size: 16px;
        font-weight: bold;
        text-align: left;
        margin-right: 5px;
    }

    .create-user-label {
        font-size: 12px;
        color: #999;
        padding-right: 10px;
    }

    .project-description {
        margin-top: 40px;
        font-size: 13px;
        color: #666;
        text-align: left;
        line-height: 1.8;
    }

    .member-card {
        width: 100px;
        text-align: center;
        display: inline-block;
        margin-right: 10px;
        margin-top: 10px;
        position: relative;
    }

    .active-item {
        margin-top: 15px;
    }

    .active-info {
        font-size: 13px;
        margin-top: 10px;
    }

    .active-link {
        color: teal;
        cursor: pointer;
    }

    .active-time {
        color: #999;
        font-size: 12px;
    }

    .project-section-box {
        padding: 30px;
        padding-bottom: 10px;
        padding-top: 25px;
        border-bottom: 10px solid #f1f4f5;
    }

    .project-section-box:last-child {
        border-bottom: none;
    }

    .project-section-box2 {
        padding: 0px;
        padding-bottom: 10px;
        padding-top: 10px;
        border-bottom: none;
    }

    .stat-item {
        display: inline-block;
        padding-left: 10px;
        padding-right: 10px;
        text-align: center;
        width: 120px;
    }

    .stat-value {
        font-size: 25px;
        font-weight: bold;
    }

    .stat-key {
        font-size: 12px;
        color: #999;
    }

    .count-type-label {
        font-size: 15px;
        color: #666;
        font-weight: bold;
        margin-bottom: 5px;
    }

    .count-label {
        color: #999;
        font-size: 13px;
    }

    .count-value {
        font-size: 20px;
        font-weight: bold;
        color: #f84f84;
    }

    .count-value-big {
        font-size: 50px;
        margin-top: 20px;
    }

    .count-box {
        padding: 20px;
        font-weight: bold;
        text-align: center;
    }

    .project-star-hover {
        transition: all 0.3s;
    }

    .project-star-hover:hover {
        color: rgb(250, 164, 36);
    }

    .readonly-alert {
        background-color: #fffbe6;
        text-align: center;
        padding: 15px;
        border-radius: 8px;
        margin-bottom: 10px;
    }

    .announcement-alert {
        background-color: #fff5c3;
        color: #333;
        text-align: center;
        padding: 15px;
        border-radius: 8px;
        margin-bottom: 10px;
        font-size: 14px;
        font-weight: bold;
    }

    .project-status {
        font-size: 15px;
        font-weight: bold;
        color: #333;
        padding-left: 5px;
        display: inline-block;
    }

    .project-status-desc {
        font-size: 13px;
        color: #999;
    }

    .project-group-label {
        background-color: #eee;
        color: #555;
        font-size: 12px;
        padding: 2px 5px;
        text-align: center;
        display: inline-block;
        border-radius: 3px;
        margin-right: 5px;
        font-weight: bold;
        margin-left: 5px;
    }

    .activity-nodata {
        text-align: center;
        font-size: 16px;
        font-weight: bold;
        color: #999;
        padding: 60px 20px;
    }

    .project-sparkline {
        position: absolute;
        top: -20px;
        right: 10px;
        width: 250px;
    }

    .project-name-box {
        position: absolute;
        z-index: 100;
    }
</style>
<i18n>
    {
    "en": {
    "此项目已归档": "The project has been archived and the content is read-only. If you need to reopen it, please contact the administrator",
    "加入星标可以快速找到项目":"Star project",
    "取消星标":"Cancel star",
    "项目设置":"Setting",
    "项目集信息设置":"Project set information setting",
    "暂无描述":"No description",
    "设置项目状态":"Set status",
    "项目工作流程状态":"Project workflow status",
    "编辑":"Edit",
    "查看历史记录":"View log",
    "天":"day",
    "创建":"created",
    "成员":"Member",
    "添加团队成员":"Add member",
    "邀请成员":"Invite member",
    "查看全部":"View all",
    "展开更多":"Expand more",
    "收起更多":"Collapse more",
    "设置成员属性":"Setting",
    "活动":"Activity",
    "完成度统计":"Statistics",
    "延期":"Delayed",
    "总数":"Total",
    "完成":"Finished",
    "项目动态":"Activity",
    "查看所有":"View all",
    "近15天无动态":"No event in the past 15 days",
    "已为项目加星标":"}Star marker has been added for project {0}.",
    "项目工作流程状态更新成功":"Project {0} workflow status update was successful."
    },
    "zh_CN": {
    "此项目已归档": "此项目已归档，项目内容为只读状态。如需重新打开请联系管理员",
    "加入星标可以快速找到项目":"设为特别关注可以快速找到项目",
    "取消星标":"取消关注",
    "项目设置":"项目设置",
    "项目集信息设置":"项目集信息设置",
    "暂无描述":"暂无描述",
    "设置项目状态":"设置项目状态",
    "项目工作流程状态":"项目工作流程状态",
    "编辑":"编辑",
    "查看历史记录":"查看历史记录",
    "天":"天",
    "创建":"创建",
    "成员":"成员",
    "添加团队成员":"添加团队成员",
    "邀请成员":"邀请成员",
    "查看全部":"查看全部",
    "展开更多":"展开更多",
    "收起更多":"收起更多",
    "设置成员属性":"设置成员属性",
    "活动":"活动",
    "完成度统计":"完成度统计",
    "延期":"延期",
    "总数":"总数",
    "完成":"完成",
    "延期":"延期",
    "项目动态":"项目动态",
    "查看所有":"查看所有",
    "近15天无动态":"近15天无动态",
    "已为项目加星标":"已为项目{0}加入星标",
    "项目工作流程状态更新成功":"项目{0}流程状态更新成功"
    }
    }
</i18n>
<template>
    <div class="page" :class="{'blur':loading}">
        <div class="page-wrap">
            <div class="project-page-content">
                <div v-if="project.status==2" class="readonly-alert">
                    {{$t('此项目已归档')}}
                </div>

                <div
                    v-if="project.announcement!=null&&project.announcement!=''" class="announcement-alert">
                    {{project.announcement}}
                </div>

                <div class="project-page-box" :style="projectColorStyle">
                    <Row>
                        <Col span="13" style="position:relative">
                            <div class="project-name-box">

                                <span class="project-name-label vcenter">{{project.name}}</span>
                                <Tooltip
                                    v-if="project.star==false&&isCurrentProjectMember" :content="$t('加入星标可以快速找到项目')">
                                    <Icon
                                        @click="toggleProjectStar()"
                                        class="project-start-empty project-star-hover"
                                        type="md-star-outline"
                                        style="cursor:pointer;font-size:20px;margin-left:0;margin-right:5px" />
                                </Tooltip>
                                <Tooltip
                                    v-if="project.star&&isCurrentProjectMember" :content="$t('取消星标')">
                                    <Icon
                                        @click="toggleProjectStar()"
                                        class="project-star project-star-hover"
                                        type="md-star"
                                        style="cursor:pointer;font-size:20px;margin-left:0;margin-right:5px" />
                                </Tooltip>

                                <span
                                    v-if="project.group" class="project-group-label vcenter">{{project.group}}</span>
                                <IconButton
                                    v-if="prjPerm('project_edit_config')"
                                    @click="showProjectSettingDialog"
                                    icon="ios-settings-outline"
                                    :tips="$t('项目设置')"></IconButton>

                                <IconButton
                                    v-if="prjPerm('project_edit_config')&&isCurrentProjectOwner&&!disabled&&project.taskUuid"
                                    @click="showProjectSetTaskDialog"
                                    icon="ios-brush-outline"
                                    :tips="$t('项目集信息设置')"></IconButton>
                            </div>
                            <div class="project-description">
                                <div v-if="project.serialNo" style="margin-right: 5px;">#{{project.serialNo}}</div>
                                <template v-if="project.description!=null">
                                    {{project.description}}
                                </template>
                                <template v-if="project.description==null">
                                    {{$t('暂无描述')}}
                                </template>
                            </div>
                            <div class="project-sparkline">
                                <Sparkline
                                    v-if="project.activityCount"
                                    :value="project.activityCount"
                                    :width="250"
                                    :height="60" />
                            </div>
                        </Col>
                        <Col span="8">
                            <div
                                v-if="project.id > 0"
                                style="display:flex;align-items:center;align-content:center;height:32px;padding: 0 8px">
                                <div>
                                    {{ $t('项目工作流程状态') }}：
                                </div>
                                <ProjectWorkflowSelect
                                    ref="projectWorkflowSelect"
                                    :disabled="!prjPerm('project_edit_workflow_status') "
                                    :value="project.workflowStatus"
                                    :project-id="project.id"
                                    @change="onProjectWorkflowChange" />
                            </div>
                            <template v-if="project.runStatus > 0&&!isProjectSet">
                                <div style="display:flex;align-items:center;height:32px">
                                    <ProjectStatusLabel v-model="project.runStatus"></ProjectStatusLabel>
                                    <Progress
                                        status="normal"
                                        style="width:180px;margin-left:10px;"
                                        :percent="project.progress" />
                                    <IconButton
                                        v-if="prjPerm('project_edit_progress')&&!disabled"
                                        @click="editProjectProgress"
                                        size="16"
                                        icon="ios-checkbox-outline"
                                        :tips="$t('编辑')"></IconButton>
                                    <IconButton
                                        @click="viewProjectProgress"
                                        size="16"
                                        icon="ios-list-box-outline"
                                        :tips="$t('查看历史记录')"></IconButton>
                                </div>
                                <div class="stat-key text-no-wrap">
                                    {{project.runLogRemark}}
                                </div>
                            </template>
                            <template v-else>
                                <div style="display:flex;align-items:center;height:32px">
                                    <IconButton
                                        v-if="prjPerm('project_edit_progress')&&!isProjectSet"
                                        @click="editProjectProgress"
                                        size="16"
                                        icon="ios-checkbox-outline"
                                        :title="$t('设置项目状态')"></IconButton>
                                </div>
                            </template>
                        </Col>
                        <Col span="3">
                            <div class="stat-item">
                                <div class="stat-value numberfont">
                                    {{project.createTime|dateDiff}}
                                    <span style="font-size:12px">天</span>
                                </div>
                                <div class="stat-key">
                                    {{project.createTime|fmtDate}} {{$t('创建')}}
                                </div>
                            </div>
                        </Col>
                    </Row>
                </div>

                <Row style="margin-top:20px" :gutter="20">
                    <Col :span="showProjectActivity ? 18 : 24">
                        <div class="project-page-box-no-padding">
                            <div class="project-section-box">
                                <div class="info-section-row">
                                    <div class="info-section-header">
                                        {{$t('成员')}}
                                    </div>
                                    <span
                                        v-if="project.memberList.length>0" class="info-section-header-sub">{{project.memberList.length}}</span>
                                    <div class="info-section-opt">
                                        <IconButton
                                            v-if="prjPerm('project_member_config')&&!disabled"
                                            @click="showAddMemberDialog"
                                            icon="ios-add"
                                            :title="$t('添加团队成员')"></IconButton>
                                        <IconButton
                                            v-if="prjPerm('project_member_config')&&company.version==1&&!disabled"
                                            @click="showInviteDialog"
                                            icon="ios-person-add"
                                            :title="$t('邀请成员')"></IconButton>
                                        <IconButton
                                            @click="showMemberListDialog"
                                            icon="ios-list"
                                            :title="$t('查看全部')"></IconButton>
                                        <template v-if="Array.isArray(project.memberList) && project.memberList.length > 21">
                                            <IconButton
                                                @click="showAllMember = !showAllMember"
                                                :icon="showAllMember ? 'md-more' : 'ios-more'"
                                                :title="$t(showAllMember? '收起更多' : '展开更多')" />
                                        </template>
                                    </div>
                                </div>

                                <div style="padding-top:20px;padding-bottom:10px">
                                    <div
                                        class="card member-card" v-for="item in showProjectMemberList" :key="item.id">
                                        <div>
                                            <AvatarImage
                                                size="large"
                                                :name="item.accountName"
                                                :value="item.accountImageId"
                                                type="none" />
                                        </div>
                                        <div
                                            class="text-no-wrap" style="font-size:13px;margin-top:5px">
                                            {{item.accountName}}
                                        </div>
                                        <div
                                            class="text-no-wrap" style="font-size:12px;color:#999;margin-top:3px">
                                            <span
                                                style="padding-right:3px;" :key="role.id" v-for="role in item.roleList">{{role.name}}</span>
                                        </div>
                                        <div class="card-opt">
                                            <IconButton
                                                v-if="prjPerm('project_member_config')"
                                                @click="showMemberSettingDialog(item)"
                                                :tips="$t('设置成员属性')"
                                                icon="ios-settings-outline"></IconButton>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div
                                class="project-section-box" v-if="showProjectActivity">
                                <div class="info-section-row">
                                    <div class="info-section-header">
                                        {{$t('活动')}}
                                    </div>
                                </div>
                                <div style="padding-top:20px;padding-bottom:10px">
                                    <HeatMap :event-list="changeLogList"></HeatMap>
                                </div>
                            </div>

                            <div
                                v-if="rateList.length>0" class="project-section-box">
                                <div class="info-section-row">
                                    <div class="info-section-header">
                                        {{$t('完成度统计')}}
                                    </div>
                                </div>
                                <div style="margin-top:20px;margin-bottom:30px">
                                    <Row>
                                        <Col
                                            span="12" v-for="item in rateList" :key="'rate'+item.objectType">
                                            <div class="count-box">
                                                <div class="count-value-big numberfont">
                                                    {{item.finishNum|fmtPercentStr(item.totalNum)}}
                                                    <span
                                                        v-if="item.delayNum>0" class="count-value">{{$t('延期')}} {{item.delayNum|fmtPercentStr(item.totalNum)}}</span>
                                                </div>
                                                <div
                                                    class="count-type-label">
                                                    {{item.objectType|dataDict('Task.objectType')}}
                                                </div>
                                                <div class="count-label">
                                                    {{$t('总数')}}
                                                    {{item.totalNum}}
                                                    <template
                                                        v-if="item.finishNum>0">
                                                        / {{$t('完成')}}
                                                        {{item.finishNum}}
                                                    </template>
                                                    <template
                                                        v-if="item.delayNum>0">
                                                        / {{$t('延期')}}
                                                        {{item.delayNum}}
                                                    </template>
                                                </div>
                                            </div>
                                        </Col>
                                    </Row>
                                </div>
                            </div>
                        </div>
                    </Col>
                    <Col span="6" v-if="showProjectActivity">
                        <div
                            class="project-page-box" style=" min-height:300px;padding-top:25px">
                            <div class="info-section-row">
                                <div class="info-section-header">
                                    {{$t('项目动态')}}
                                </div>
                                <IconButton
                                    @click="showAllProjectLog()" :tips="$t('查看所有')" icon="ios-list"></IconButton>
                            </div>
                            <div>
                                <ProjectChangeLogView :change-log-list="changeLogList"></ProjectChangeLogView>
                                <div
                                    class="activity-nodata" v-if="changeLogLoaded&&changeLogList.length==0">
                                    {{$t('近15天无动态')}}
                                </div>
                            </div>
                        </div>
                    </Col>
                </Row>
            </div>
        </div>
    </div>
</template>

<script>

    export default {
        mixins: [componentMixin],
        data() {
            return {
                title: 'ProjectPage',
                loading: false,
                showAllMember: false,
                projectColor: null,
                project: {
                    memberList: [],
                    createTime: 0,
                },
                company: {},
                currentIteration: 0,
                changeLogQuery: {
                    pageIndex: 1,
                    pageSize: 10,
                },
                rateList: [],
                changeLogList: [],
                changeLogLoaded: false,
                changeLogCount: 0,
                isSupperBoss:false,
                isProjectSet:false,
                isCurrentProjectMember:false,
                isCurrentProjectOwner:false,
                isSpiEnable:app.isSpiEnable,
                isCurrentProjectManager:false
            };
        },
        computed: {
            showProjectMemberList() {
                if (!Array.isArray(this.project.memberList) || this.project.memberList.length === 0) {
                    return [];
                }
                if (this.showAllMember) {
                    return this.project.memberList;
                }
                return this.project.memberList.filter((_, index) => index <= 20);
            },
            showProjectActivity() {
                return this.perm(['company_project_change_log']);
            },
            projectColorStyle() {
                if (this.projectColor == null) {
                    return {};
                }
                return {
                    borderLeft: '4px solid ' + this.projectColor,
                };
            },
            disabled(){
                return this.project&&this.project.isFinish;
            },
        },
        methods: {
            pageLoad() {
                this.company = app.company;
                this.isSupperBoss = app.account.superBoss>0;
                this.loadData();
                app.postMessage('project.show', app.projectUUID);
            },
            pageUpdate() {
                this.loadData();
            },
            pageMessage(type, content) {
                if (
                    type == 'project.edit' ||
                    type == 'iteration.edit' ||
                    type == 'release.edit' ||
                    type == 'system.edit' ||
                    type == 'member.edit'
                ) {
                    this.loadData();
                    return;
                }
                if (type === 'project.workflow.edit') {
                    if (this.$refs.projectWorkflowSelect) {
                        this.$refs.projectWorkflowSelect.loadData();
                    }
                }
            },
            getLeftDays(value) {
                return window.getLeftDays(value);
            },

            loadData() {
                if (app.projectUUID == null) {
                    return;
                }

                this.loading = true;
                app.invoke(
                    'BizAction.getProjectInfoByUuid',
                    [app.token, app.projectUUID],
                    info => {
                        this.loading = false;
                        if (info.activityCount) {
                            info.activityCount = JSON.parse(info.activityCount);
                        }
                        this.project = info;
                        this.projectColor = info.color;
                        this.loadChangeLog();
                        this.loadFinishRateData();
                        this.isProjectSet = info.templateId=='10001';
                        app.isProjectSet = this.isProjectSet;
                        app.projectId = info.id;
                        //针对boss角色可以穿透项目的权限，可能不在项目中
                        this.isCurrentProjectMember= !Array.isEmpty(this.project.memberList)&&this.project.memberList.some(item=>item.accountId===app.account.id)
                        this.isCurrentProjectOwner = !Array.isEmpty(this.project.ownerAccountIdList)&&this.project.ownerAccountIdList.some(item=>item===app.account.id);
                        if(!Array.isEmpty(this.project.memberList)&&this.isCurrentProjectMember){
                           let roleList= this.project.memberList.find(item=>item.accountId===app.account.id).roleList;
                           this.isCurrentProjectManager = roleList&&roleList.some(k=>k.type==1&&k.name=='项目经理');
                        }
                    },
                );
            },
            loadFinishRateData() {
                app.invoke(
                    'BizAction.getProjectFinishDelayRate',
                    [app.token, this.project.id],
                    info => {
                        this.rateList = info;
                    },
                );
            },

            loadChangeLog() {
                this.changeLogQuery.projectId = this.project.id;

                app.invoke(
                    'BizAction.getChangeLogList',
                    [app.token, this.changeLogQuery],
                    list => {
                        this.changeLogLoaded = true;
                        list.map(item => {
                            if (item.items != '' && item.items != null) {
                                item.items = JSON.parse(item.items);
                            } else {
                                item.items = {};
                            }
                            if (item.items.author) {
                                item.createAccountName = item.items.author;
                            }
                        });
                        if (this.changeLogQuery.pageIndex != 1) {
                            list.map(item => {
                                this.changeLogList.push(item);
                            });
                        } else {
                            this.changeLogList = list;
                        }
                        this.changeLogCount = list.length;
                    },
                );
            },
            toggleProjectStar() {
                var action = 'BizAction.setStarProject';
                if (this.project.star) {
                    action = 'BizAction.cancelStarProject';
                }
                app.invoke(action, [app.token, this.project.id], () => {
                    app.postMessage('project.edit');
                    if (action == 'BizAction.setStarProject') {
                        app.toast(this.$t('已为项目加星标', [this.project.name]));
                    }
                });
            },
            onProjectWorkflowChange(status) {
                if (!status || this.project.workflowStatus === status) {
                    return;
                }
                //结束状态的工作流给与提示
                app.invoke('BizAction.getProjectStatusDefineInfoList',[app.token,this.project.id,0],(res)=>{
                    if(res){
                        let statusDefine = res.filter(item=>item.id===status)[0];
                        if(statusDefine){
                            if(statusDefine.type===3){
                                app.confirm('变更为该状态后项目将不可再编辑，请确认是否变更?', () => {
                                    this.updateProjectWorkFlowNative(status,true);
                                }, () => {
                                    window.location.reload();
                                }, '确定', '取消');
                            }else{
                                this.updateProjectWorkFlowNative(status,false);
                            }
                        }else{
                            app.toast("工作流状态不存在");
                            return;
                        }
                    }
                });

            },
            updateProjectWorkFlowNative(status,finish){
                const oldStatus = this.project.workflowStatus;
                this.project.workflowStatus = status;
                app.invoke('BizAction.updateProjectWorkflowStatus', [app.token, this.project.id, status], () => {
                    app.toast(this.$t('项目工作流程状态更新成功', [this.project.name]));
                    if(finish){
                        setTimeout(()=>{
                            window.location.reload();
                        },2000)
                    }else{
                        this.loadChangeLog();
                    }
                }, () => {
                    this.project.workflowStatus = oldStatus;
                });
            },
            editProjectProgress() {
                app.showDialog(ProjectStatusEditDialog, {
                    project: this.project,
                });
            },
            viewProjectProgress() {
                app.loadPage('project_status_log');
            },
            showAllProjectLog() {
                app.loadPage('project_log');
            },
            showInviteDialog() {
                app.showDialog(InviteUserDialog);
            },
            showMemberListDialog() {
                app.showDialog(MemberListDialog, {
                    projectUUID: this.project.uuid,
                    projectId: this.project.id,
                    isCurrentProjectManager:this.isCurrentProjectManager,
                });
            },
            showProjectSettingDialog() {
                app.showDialog(ProjectSettingDialog, {
                    uuid: this.project.uuid,
                });
            },
            showAddMemberDialog() {
                app.showDialog(MemberAddDialog, {
                    projectId: this.project.id,
                    isCurrentProjectManager:this.isCurrentProjectManager
                });
            },
            showMemberSettingDialog(item) {
                app.showDialog(MemberSettingDialog, {
                    member: item,
                    projectId: this.project.id,
                    isCurrentProjectManager:this.isCurrentProjectManager
                });
            },
            showEditIterationDialog(item) {
                app.showDialog(IterationEditDialog, {
                    projectId: this.project.id,
                    item: item,
                });
            },

            showIteration(item) {
                app.showDialog(IterationInfoDialog, {
                    id: item.id,
                });
            },
            showProjectSetTaskDialog(){
                app.showDialog(TaskDialog, {
                    taskId: this.project.taskUuid
                })
            }
        },
    };
</script>
