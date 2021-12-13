<style scoped>
    .ivu-collapse{
     border-top: none !important;
        padding: 8px 0;
    }
    .ivu-collapse /deep/ .ivu-collapse-header{
        border: none !important;
    }
    .table-box {
        background-color: #fff;
        padding: 30px;
        padding-top: 10px;
        box-shadow: 0px 2px 10px 0px rgba(225, 225, 225, 0.5);
        border: 1px solid rgba(216, 216, 216, 1);
    }

    .project-owner{
        font-weight: 400;
        overflow: hidden;
    }

    .table-info {
        color: #999;
        text-align: center;
    }

    .table-count {
        background-color: #e8e8e8;
        color: #666;
        padding: 3px 5px;
        border-radius: 3px;
    }

    .left-day {
        color: #17a7ed;
        margin-left: 5px;
    }

    .left-overdue {
        color: #f84f84;
    }

    .table-col-name {
        display: inline-block;
        vertical-align: middle;
    }

    .table-remark {
        color: #999;
        margin-top: 5px;
    }

    .nodata {
        padding: 60px;
        font-size: 20px;
        color: #999;
        text-align: center;
    }

    .table-info-row {
        display: flex;
        align-items: center;
    }

    .project-group {
        color: #999;
        padding-top: 5px;
    }

    .project-day-range {
        width: 150px;
        display: inline-block;
    }

    .card-box {
        display: block;
    }

    .project-card {
        width: 300px;
        height: 160px;
        display: inline-block;
        background-color: #fff;
        border-radius: 5px;
        margin-right: 10px;
        margin-top: 10px;
        transition: all 0.5s;
        cursor: pointer;
        position: relative;
    }

    .project-card-inner {
        position: absolute;
        top: 0;
        left: 0;
        width: 300px;
        height: 160px;
        padding: 15px;
        overflow: hidden;
    }

    .project-card-sparkline {
        position: absolute;
        top: 5px;
        right: 5px;
        width: 300px;
        height: 150px;
        text-align: right;
    }

    .project-card-name {
        font-size: 16px;
        color: #333;
        font-weight: bold;
        cursor: pointer;
        width: 235px;
        display: inline-block;
    }

    .project-card-desc {
        color: #999;
        margin-top: 3px;
        height: 42px;
        overflow: hidden;
        margin-bottom: 3px;
    }

    .project-card:hover {
        border: 1px solid #0097f7;
    }

    .add-project-label {
        cursor: pointer;
        font-size: 20px;
        text-align: center;
    }

    .add-project-card-inner {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        color: #999;
        height: 130px;
        width: 100%;
    }

    .card-wrap-box {
        display: flex;
        flex-wrap: wrap;
    }

    .right-opt {
        display: flex;
        align-items: center;
        flex-direction: row-reverse;
    }

</style>
<i18n>
    {
    "en": {
    "卡片视图": "Card view",
    "列表视图": "Table view",
    "个项目": "{0} project(s)",
    "创建项目": "Create",
    "项目集管理":"Project Set",
    "暂无描述": "No description",
    "剩余": "Remaining {0} day(s)",
    "今天到期": "Today",
    "超期": "Overdue {0} day(s)",
    "负责人": "Owners",
    "名称": "Name",
    "活动量": "Activity",
    "运行天数": "Created",
    "进度": "Schedule",
    "迭代": "Iteration",

    "没有加入任何项目":"No Data",
    "暂无数据":"No Data",
    "未分组":"unclassified",
    "创建和剩余时间":"Running/Left"
    },
    "zh_CN": {
    "卡片视图": "卡片视图",
    "列表视图": "列表视图",
    "个项目": "{0}个项目",
    "创建项目": "创建项目",
    "项目集管理":"项目集管理",
    "暂无描述": "暂无描述",
    "剩余": "剩余{0}天",
    "今天到期": "今天到期",
    "超期": "超期{0}天",

    "名称": "名称",
    "活动量": "活动量",
    "运行天数": "运行天数",
    "进度": "进度",
    "迭代": "迭代",
    "负责人": "负责人",

    "没有加入任何项目":"没有加入任何项目",
    "暂无数据":"暂无数据",
    "未分组":"未分组",
    "创建和剩余时间":"创建和剩余时间"
    }
    }
</i18n>
<template>
    <div v-if="list" style="padding:20px;">
        <Row>
            <Col span="8">&nbsp;</Col>
            <Col span="8">
                <div class="table-info">
                    <span class="table-count">{{$t('个项目',[list.length])}}</span>
                </div>
            </Col>
            <Col span="8" class="right-opt">
                <IconButton v-if="viewType=='table'" @click="changeViewType('card')" :title="$t('卡片视图')"></IconButton>
                <IconButton v-if="viewType=='card'" @click="changeViewType('table')" :title="$t('列表视图')"></IconButton>
                <IconButton
                    v-if="perm('project_set_view')"
                    @click="gotoProjectSet"
                    size="16"
                    icon="md-cube"
                    :disabled="projectSetLoading"
                    :title="$t('项目集管理')"/>
                <IconButton
                    v-if="perm('company_create_project')"
                    @click="showAddProjectDialog"
                    icon="md-add"
                    :title="$t('创建项目')"/>
            </Col>
        </Row>

        <div v-show="viewType=='card'" class="card-box">
            <template v-for="group in groupProjectList">
                <Divider :key="'div_'+group.name" orientation="left">
                    {{group.name}} <span class="grp-cnt">{{group.list.length}}</span>
                </Divider>
                <div class="card-wrap-box" :key="'div_box_'+group.name">
                    <div
                        @click="showProject(item)"
                        v-for="item in group.list"
                        :key="'prj_'+item.id"
                        class="project-card"
                        :style="'border-left:4px solid '+item.color">

                        <div class="project-card-sparkline">
                            <Sparkline
                                v-if="item.activityCount" :value="item.activityCount" :width="100" :height="30"/>
                        </div>
                        <div class="project-card-inner">
                            <div>
                                <span class="project-card-name text-no-wrap">{{item.name}}
                                    <Icon v-if="item.star" class="project-star" type="md-star"/>
                                </span>

                            </div>
                            <div class="project-card-desc">
                                <template v-if="item.description!=null">{{item.description}}</template>
                                <template v-if="item.description==null">{{$t('暂无描述')}}</template>
                            </div>
                            <div class="project-owner">
                                <span v-if="item.ownerAccountList&&!Array.isEmpty(item.ownerAccountList)" v-for="own in item.ownerAccountList" :key="'owner_'+own.id">{{own.name}}  </span>
                            </div>
                            <div v-if="item.runStatus>0">
                                <div>
                                    <Progress :percent="item.progress"/>
                                </div>
                                <div class="table-remark">
                                    <ProjectStatusLabel v-model="item.runStatus"></ProjectStatusLabel>
                                </div>
                            </div>
                            <div v-if="item.runStatus==0">
                                <div v-if="item.iteration.id>0">
                                    <div class="table-info-row">
                                        <DataDictLabel
                                            type="ProjectIteration.status"
                                            :value="item.iteration.status"></DataDictLabel>
                                        <span style="margin-left:5px">{{item.iteration.name}}</span>
                                    </div>
                                    <div class="table-remark">
                                        <span class="project-day-range">
                                            {{item.iteration.startDate|fmtDate}}
                                            ~
                                            {{item.iteration.endDate|fmtDate}}
                                        </span>
                                        <template v-if="item.iteration.status!=3">
                                            <span
                                                class="left-day" v-if="getLeftDays(item.iteration.endDate)>0">{{$t('剩余',[getLeftDays(item.iteration.endDate)])}}</span>
                                            <span
                                                class="left-day" v-if="getLeftDays(item.iteration.endDate)==0">{{$t('今天到期')}}</span>
                                            <span
                                                class="left-day left-overdue"
                                                v-if="getLeftDays(item.iteration.endDate)<0">{{$t('超期',[getLeftDays(item.iteration.endDate)*-1])}}</span>
                                        </template>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
            </template>

            <div
                v-if="perm('company_create_project')" class="project-card" style="text-align:center">
                <div @click="showAddProjectDialog" class="add-project-card-inner">
                    <Icon type="md-add" size="25"/>
                    <div class="add-project-label">{{$t('创建项目')}}</div>
                </div>
            </div>
        </div>

        <div v-show="viewType=='table'" class="table-box">
            <table class="table-content" style="table-layout:fixed">
                <thead>
                <tr>
                    <th style="width:150px">{{$t('名称')}}</th>
                    <th style="width:100px">{{$t('负责人')}}</th>
                    <th style="width:150px">{{$t('活动量')}}</th>
                    <th style="width:150px;">{{$t('创建和剩余时间')}}</th>
                    <th style="width:200px;">{{$t('进度')}}</th>
                    <th style="width:250px">{{$t('迭代')}}</th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
            <template v-for="group in groupProjectList">
                <Collapse simple :key="'grp_'+group.name" :value="defaultOpenTablePanel">
                    <Panel :name="group.name">
                        <span><label style="font-size: 14px;font-weight: bold;">{{group.name}}</label>   ({{group.list.length}})</span>
                        <div slot="content">
                            <table class="table-content" style="table-layout:fixed">
                                <tbody>
                                <tr v-for="item in group.list" :key="'prj_'+item.id" class="table-row">
                                    <td class="text-no-wrap" style="width: 150px;">
                                        <div>
                                            <span
                                                @click="showProject(item)" class="table-col-name" :title="item.name">{{item.name}}</span>
                                            <Icon v-if="item.star" class="project-star" type="md-star"/>
                                        </div>
                                        <div class="text-no-wrap project-group">{{item.description}}</div>
                                    </td>
                                    <td style="width:100px">
                                        <span v-if="item.ownerAccountList&&!Array.isEmpty(item.ownerAccountList)" v-for="own in item.ownerAccountList" :key="'owner_'+own.id">{{own.name}} </span>
                                    </td>
                                    <td style="width:150px">
                                        <Sparkline
                                            v-if="item.activityCount" :value="item.activityCount" :width="100"
                                            :height="30"/>
                                    </td>
                                    <td style="font-size:13px;color:#999;width:150px">
                                        <div style="color:#333;margin-right:5px">
                                            <span>{{item.createTime|dateDiff}}天</span>
                                            <span v-if="item.endDate"> /
<!--                                                <OverdueLabel-->
<!--                                                :value="item.endDate"></OverdueLabel>-->
                                                 <OverdueText :value="item.endDate" :is-finish="item.isFinish"
                                                              :finish-date="item.finishDate"/>
                                            </span>
                                        </div>
                                        <div>{{item.startDate|fmtDate}} ~ {{item.endDate|fmtDate}}</div>
                                    </td>
                                    <td style="width:200px">
                                        <div v-if="item.runStatus>0">
                                            <Progress :percent="item.progress"/>
                                        </div>
                                        <div v-if="item.runStatus>0" class="table-remark">
                                            <ProjectStatusLabel v-model="item.runStatus"></ProjectStatusLabel>
                                        </div>
                                    </td>
                                    <td style="width:250px">
                                        <div v-if="item.iteration.id>0">
                                            <div class="table-info-row">
                                                <DataDictLabel
                                                    type="ProjectIteration.status"
                                                    :value="item.iteration.status"></DataDictLabel>
                                                <span style="margin-left:5px">{{item.iteration.name}}</span>
                                            </div>
                                            <div class="table-remark">
                                    <span class="project-day-range">
                                        {{item.iteration.startDate|fmtDate}}
                                        ~
                                        {{item.iteration.endDate|fmtDate}}
                                    </span>
                                                <template v-if="item.iteration.status!=3">
                                                    <OverdueLabel :value="item.iteration.endDate"></OverdueLabel>

                                                </template>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </Panel>
                </Collapse>
            </template>

            <div class="table-nodata" v-if="showCreate">
                <div>{{$t('没有加入任何项目')}}</div>
                <div v-if="perm('company_create_project')" style="margin-top:10px">
                    <Button @click="showAddProjectDialog" type="default" icon="md-add">{{$t('创建项目')}}</Button>
                </div>
            </div>

            <div class="nodata" v-if="list.length==0&&showCreate==false">{{$t('暂无数据')}}</div>
        </div>
    </div>
</template>

<script>
    export default {
        mixins: [componentMixin],
        props: ['list', 'showCreate'],
        data() {
            return {
                viewType: 'table',
                projectSetLoading: false,
                defaultOpenTablePanel:null
            };
        },
        computed: {
            groupProjectList() {
                var list = [];
                var groupMap = {};
                for (var i = 0; i < this.list.length; i++) {
                    var t = this.list[i];
                    if (t.group == null || t.group == '') {
                        t.group = this.$t('未分组');
                    }
                    if (groupMap[t.group] == null) {
                        groupMap[t.group] = {
                            name: t.group,
                            list: [],
                            expand: true
                        };
                        list.push(groupMap[t.group]);
                    }
                    groupMap[t.group].list.push(t);
                }
                this.defaultOpenTablePanel = list[0]&&list[0].name;
                return list;
            },
        },
        mounted() {
            var t = app.loadObject('Dashboard.projectList.viewType');
            if (t != null) {
                this.viewType = t;
            }
        },
        methods: {
            changeViewType(t) {
                this.viewType = t;
                app.saveObject('Dashboard.projectList.viewType', t);
            },
            getLeftDays(date) {
                return getLeftDays(date);
            },
            showProject(item) {
                app.loadPage('/pm/project/' + item.uuid + '/project');
            },
            showTaskInfo(item) {
                app.showDialog(TaskDialog, {
                    taskId: item.uuid,
                });
            },
            showAddProjectDialog() {
                app.showDialog(ProjectCreateDialog);
            },
            gotoProjectSet() {
                this.projectSetLoading = true;
                app.invoke('BizAction.getProjectSetInfo', [app.token], result => {
                    this.projectSetLoading = false;
                    if (!result) {
                        return;
                    }
                    app.postMessage('project.edit');
                    app.loadPage('/pm/project/' + result.projectUuid + '/project');
                }, () => {
                    this.projectSetLoading = false;
                });
            },
        },
    };
</script>
