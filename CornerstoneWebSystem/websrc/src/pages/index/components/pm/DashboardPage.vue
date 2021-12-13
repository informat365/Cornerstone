<style scoped>
    .page {
        display: flex;
    }

    .page-menu {
        width: 260px;
        height: calc(100vh - 50px);
        overflow: auto;
        background-color: #fff;
        border-right: 1px solid rgba(216, 216, 216, 1);
        padding: 25px;
        user-select: none;
    }

    @media (max-width: 1000px) {
        .page-menu {
            display: none;
        }

        .silde-control-bar {
            display: none;
        }
    }

    .page-right-content {
        flex: 1;
        height: calc(100vh - 50px);
        overflow: auto;

    }

    .menu-item {
        font-size: 15px;
        font-weight: 500;
        padding: 8px;
        color: #666;
        cursor: pointer;
        display: flex;
        align-items: center;
        align-content: center;
        position: relative;
        transition: all 0.3s;
    }

    .menu-item:hover {
        color: #009cf1;
    }

    .menu-item-active {
        color: #009cf1;
        font-weight: bold;
    }

    .menu-item-group {
        font-size: 12px;
        color: #999;
        padding: 8px;
        margin-top: 20px;
    }

    .filter-tag {
        font-size: 13px;
        padding: 3px 10px;
        background-color: #eee;
        color: #777;
        display: inline-block;
        margin-right: 10px;
        margin-top: 10px;
        border-radius: 13px;
        cursor: pointer;
        user-select: none;
    }

    .filter-count {
        font-size: 12px;
        margin-left: 5px;
        font-weight: bold;
    }

    .filter-tag-select {
        background-color: #009cf1;
        color: #fff;
    }

    .silde-control-bar {
        width: 6px;
        position: absolute;
        top: 100px;
        left: 260px;
    }

    .silde-control-bar-close {
        left: 0px;
    }

    .hide-side-button {
        position: absolute;
        right: 0px;
        top: 32px;
        width: 6px;
        height: 40px;
        background-color: rgba(216, 216, 216, 1);
        cursor: pointer;
        color: #fff;
        border-radius: 2px;
    }

    .hide-side-button:hover {
        background-color: #5391f0;
    }

    .hide-side-button-icon {
        position: absolute;
        left: -3px;
        top: 13px;
    }

    .badge {
        display: inline-block;
        width: 8px;
        height: 8px;
        position: absolute;
        top: 10px;
        left: 65px;
        background-color: #f43b1f;
        border-radius: 50%;
    }

    .user-role {
        font-size: 12px;
        background-color: #eee;
        border-radius: 3px;
        padding: 2px 4px;
        margin-left: 5px;
        display: inline-block;
    }

    @keyframes loading-spin {
        from {
            transform: rotate(0deg);
        }
        50% {
            transform: rotate(180deg);
        }
        to {
            transform: rotate(360deg);
        }
    }

    .loading-spin {
        animation: loading-spin 1s linear infinite;
    }
</style>
<i18n>
    {
    "zh_CN":{
    "我的待办":"我的待办",
    "项目集":"项目集",
    "我的创建":"我的创建",
    "系统管理":"系统管理",
    "WIKI":"通商百科",
    "我的项目":"我的项目",
    "我的变更":"我的变更",
    "项目动态":"项目动态",
    "报表":"报表",
    "汇报":"汇报",
    "仪表盘":"仪表盘",
    "讨论":"讨论",
    "流程":"流程",
    "问卷调查":"问卷调查"
    },
    "en": {
    "我的待办":"Todo",
    "项目集":"Project Set",
    "系统管理":"System Version",
    "WIKI":"Wiki",
    "我的创建":"Created",
    "我的项目":"Project",
    "我的变更":"Alteration",
    "项目动态":"Activity",
    "报表":"Chart",
    "汇报":"Report",
    "仪表盘":"Dashboard",
    "讨论":"Discuss",
    "流程":"Workflow",
    "问卷调查":"Surveys"
    }
    }
</i18n>
<template>
    <div class="page">
        <div class="silde-control-bar" :class="{'silde-control-bar-close': showSideMenu===false}">
            <div @click="showSideMenu=!showSideMenu" class="hide-side-button">
                <Icon v-if="showSideMenu" class="hide-side-button-icon" type="md-arrow-dropleft"/>
                <Icon v-else class="hide-side-button-icon" type="md-arrow-dropright"/>
            </div>
        </div>

        <div class="page-menu scrollbox" v-if="showSideMenu">
            <Row style="color:#666;margin-bottom:20px;padding-left:5px">
                <Col span="6" style="padding-top:3px;cursor:pointer" @click.native="showAccountSetting()">
                    <AvatarImage
                        size="large" :name="account.name" :value="account.imageId" type="none"/>
                </Col>
                <Col span="18">
                    <div class="text-no-wrap" style="padding-top:5px">
                        <span style="font-size:14px;">{{account.name}}</span>
                        <span v-for="role in roles" :key="'role'+role.id" class="user-role">{{role.name}}</span>
                    </div>
                    <div class="text-no-wrap" style="font-weight:bold;font-size:15px">{{company.name}}</div>
                </Col>
            </Row>


            <div class="menu-item" @click="showMenu('todo')" :class="{'menu-item-active':selectMenu==='todo'}">
                <Icon type="md-done-all" style="margin-right:10px"/>
                {{$t('我的待办')}}
            </div>

            <div
                class="menu-item"
                v-if="showProjectSetMenu"
                @click="showMenu('projectSet')"
                :class="{'menu-item-active':selectMenu==='projectSet'}">
                <Icon type="md-cube"/>
                <span style="padding: 0 10px;">{{$t('项目集')}}</span>
                <Spin size="small" v-show="projectSetLoading">
                    <Icon style="color: #999" type="ios-loading" size="16" class="loading-spin"></Icon>
                </Spin>
            </div>
            <div class="menu-item" @click="showMenu('my')" :class="{'menu-item-active':selectMenu==='my'}">
                <Icon type="md-document" style="margin-right:10px"/>
                {{$t('我的创建')}}
            </div>

            <div class="menu-item" @click="showMenu('project')" :class="{'menu-item-active':selectMenu==='project'}">
                <Icon type="md-list-box" style="margin-right:10px"/>
                {{$t('我的项目')}}
            </div>

            <div class="menu-item" @click="showMenu('alteration')"
                 :class="{'menu-item-active':selectMenu==='alteration'}">
                <Icon type="md-git-compare" style="margin-right:10px"/>
                {{$t('我的变更')}}
            </div>

            <div
                v-if="perm(['company_project_change_log'])"
                class="menu-item"
                @click="showMenu('log')"
                :class="{'menu-item-active':selectMenu==='log'}">
                <Icon type="md-list" style="margin-right:10px"/>
                {{$t('项目动态')}}
            </div>
            <!--通商银行版本新增wikI、系统管理,sass版本应屏蔽-->
            <div class="menu-item" v-if="perm(['company_wiki_list'])" @click="showMenu('wiki')" :class="{'menu-item-active':selectMenu==='wiki'}">
                <Icon type="logo-wordpress" style="margin-right:10px"/>
                {{isRepositoryVersionExtension?'通商百科':'企业WIKI'}}
            </div>
            <div class="menu-item"   v-if="perm(['version_repository_view'])&&isRepositoryVersionExtension" @click="showMenu('sysVersion')" :class="{'menu-item-active':selectMenu==='sysVersion'}">
                <Icon type="ios-keypad" style="margin-right:10px"/>
                {{isRepositoryVersionExtension?'系统管理':'版本库管理'}}
            </div>
            <div
                v-if="perm(['chart_view'])"
                class="menu-item"
                @click="showMenu('chart')"
                :class="{'menu-item-active':selectMenu==='chart'}">
                <Icon type="md-trending-up" style="margin-right:10px"/>
                {{$t('报表')}}
            </div>
            <div
                v-if="perm(['report_view'])"
                class="menu-item"
                @click="showMenu('report')"
                :class="{'menu-item-active':selectMenu==='report'}">
                <Icon type="md-clipboard" style="margin-right:10px"/>
                {{$t('汇报')}}
            </div>

            <div
                v-if="perm(['dashboard_view'])"
                class="menu-item"
                @click="showMenu('card')"
                :class="{'menu-item-active':selectMenu==='card'}">
                <Icon type="md-podium" style="margin-right:10px"/>
                {{$t('仪表盘')}}
            </div>

            <div
                v-if="perm(['discuss_view'])"
                class="menu-item"
                @click="showMenu('discuss')"
                :class="{'menu-item-active':selectMenu==='discuss'}">
                <Icon type="md-chatbubbles" style="margin-right:10px"/>
                {{$t('讨论')}}
            </div>

            <div
                v-if="perm(['workflow_view'])"
                class="menu-item"
                @click="showMenu('workflow')"
                :class="{'menu-item-active':selectMenu==='workflow'}">
                <Icon type="md-mail" style="margin-right:10px"/>
                {{$t('流程')}}
            </div>

            <div
                v-if="perm(['surveys_view'])"
                class="menu-item"
                @click="showMenu('surveys')"
                :class="{'menu-item-active':selectMenu==='surveys'}">
                <Icon type="md-checkbox-outline" style="margin-right:10px"/>
                {{$t('问卷调查')}}
            </div>


            <div style="margin-top:20px" v-if="selectMenu==='todo'">
                <DashboardTaskFilterView
                    ref="todoTaskFilterView" @list-changed="setupTodoTaskList" :type="selectMenu"/>
            </div>

            <div style="margin-top:20px" v-else-if="selectMenu==='my'">
                <DashboardTaskFilterView
                    ref="meTaskFilterView" @list-changed="setupMeTaskList" :type="selectMenu"/>
            </div>

            <div style="margin-top:20px" v-else-if="selectMenu==='alteration'">
                <DashboardAlterationFilterView
                    ref="alterationFilterView" @list-changed="setupAlterationList"/>
            </div>

            <div style="margin-top:20px" v-else-if="selectMenu==='project'">
                <DashboardProjectFilterView
                    ref="projectFilterView" @list-changed="setupProjectList"/>
            </div>
            <!--            <div style="margin-top:20px" v-else-if="selectMenu==='report'">-->
            <!--                <DashboardReportFilterView-->
            <!--                    ref="reportFilterView" @report-changed="setupReportList" />-->
            <!--            </div>-->
            <div style="margin-top:20px" v-else-if="selectMenu==='workflow'">
                <DashboardWorkflowFilterView
                    ref="workflowFilterView" @change="filterWorkflowList"/>
            </div>

            <div style="margin-top:20px" v-else-if="selectMenu==='log'">
                <DashboardLogFilterView ref="logFilterView" @change="filterLogList"/>
            </div>

            <div style="margin-top:20px" v-else-if="selectMenu==='surveys'">
                <DashboardSurveysFilterView
                    ref="surveysFilterView" @change="filterSurveysList"/>
            </div>

        </div>
        <div class="page-right-content scrollbox">
            <DashboardTaskView v-if="selectMenu==='todo'" :list="todoTaskList"/>
            <DashboardTaskView v-else-if="selectMenu==='my'" :list="meTaskList"/>
            <template v-else-if="selectMenu==='project'">
                <DashboardProjectView
                    v-if="projectList!=null" :list="projectList" :show-create="showProjectCreate"/>
            </template>
            <DashboardAlterationView v-else-if="selectMenu==='alteration'" :list="alterationList"/>
            <DashboardChartView v-else-if="selectMenu==='chart'"/>
            <DashboardLogView ref="logView" v-else-if="selectMenu==='log'"/>
            <DashboardReportView
                @page-changed="reportPageChanged" v-else-if="selectMenu==='report'"/>
            <DashboardCardView ref="cardView" v-else-if="selectMenu==='card'"/>
            <DashboardReportTemplateView v-else-if="selectMenu==='report_template'"/>
            <DashboardDiscussView ref="discussView" v-else-if="selectMenu==='discuss'"/>
            <DashboardWorkflowView ref="workflowView" v-else-if="selectMenu==='workflow'"/>
            <DashboardProfileView ref="profileView" v-else-if="selectMenu==='profile'"/>
            <DashboardSurveysView ref="surveysView" v-else-if="selectMenu==='surveys'"/>
            <DashboardRepositoryView  ref="repositoryView" v-if="selectMenu==='sysVersion'"/>
            <DashboardWikiView  ref="wikiView" v-if="selectMenu==='wiki'"/>
        </div>
    </div>
</template>

<script>

    export default {
        mixins: [componentMixin],
        data() {
            return {
                title: 'DashboardPage',
                account: {},
                roles: [],
                selectMenu: null,
                todoTaskList: [],
                meTaskList: [],
                alterationList: [],
                projectList: null,
                reportInfo: {},
                company: {},
                view: 'todo',
                showSideMenu: true,
                showProjectCreate: false,
                projectSetLoading: false,
                reportNum: 0,
                isRepositoryVersionExtension:app.isRepositoryVersionExtension
            };
        },
        computed: {
            showProjectSetMenu() {
                return this.perm('project_set_view') && process.env.VUE_APP_DASHBOARD_PAGE_PROJECT_SET_MENU === 'show';
            },
        },
        mounted() {
            if (app.showNewHome) {
                app.loadPage('/pm/index/newDashboard');
            }
        },
        methods: {
            pageLoad() {
                this.account = app.account;
                this.company = app.company;
                this.roles = app.roles;
                if (this.args.view) {
                    this.selectMenu = this.args.view;
                } else {
                    this.selectMenu = 'todo';
                }
            },
            pageUpdate() {
                if (this.args.view) {
                    this.selectMenu = this.args.view;
                } else {
                    this.selectMenu = 'todo';
                }
                if (this.selectMenu == 'my') {
                    this.$nextTick(() => {
                        this.$refs.meTaskFilterView.reloadData();
                    });
                }
                if (this.selectMenu == 'todo') {
                    this.$nextTick(() => {
                        this.$refs.todoTaskFilterView.reloadData();
                    });

                }
            },
            filterWorkflowList(info) {
                this.$refs.workflowView.filterList(info);
            },
            filterLogList(info) {
                this.$refs.logView.filterList(info);
            },
            filterSurveysList(info) {
                this.$refs.surveysView.filterList(info);
            },
            pageMessage(type, value) {
                if (type == 'task.edit') {
                    if (this.$refs.todoTaskFilterView) {
                        this.$refs.todoTaskFilterView.reloadData();
                    }
                    if (this.$refs.meTaskFilterView) {
                        this.$refs.meTaskFilterView.reloadData();
                    }
                }
                if (type == 'project.edit') {
                    if (this.$refs.projectFilterView) {
                        this.$refs.projectFilterView.reloadData();
                    }
                }
                // if (type == 'report.edit') {
                //     if (this.$refs.reportFilterView) {
                //         this.$refs.reportFilterView.reloadData();
                //     }
                // }
                if (type == 'dashboard.reportnum') {
                    this.reportNum = value;
                }
                if (type == 'card.edit') {
                    if (this.$refs.cardView) {
                        this.$refs.cardView.reloadCard();
                    }
                }
                if (type == 'discuss.edit') {
                    if (this.$refs.discussView) {
                        this.$refs.discussView.reloadData();
                    }
                }
                if (type == 'workflow.edit') {
                    if (this.$refs.workflowView) {
                        this.$refs.workflowView.reloadData();
                    }
                }
                if (type == 'surveys.edit') {
                    if (this.$refs.surveysView) {
                        this.$refs.surveysView.reloadData();
                    }
                }
                if (type == 'appaccount.update') {
                    this.account = app.account;
                    if (this.$refs.profileView) {
                        this.$refs.profileView.loadData();
                    }
                }
            },
            reportPageChanged(e) {
                this.$refs.reportFilterView.reloadData(e);
            },
            setupReportList(e) {
                this.reportInfo = e;
            },
            setupTodoTaskList(e) {
                this.todoTaskList = e;
            },
            setupMeTaskList(e) {
                this.meTaskList = e;
            },
            setupAlterationList(e) {
                this.alterationList = e;
            },
            setupProjectShowCreate(e) {
                this.showProjectCreate = e;
            },
            setupProjectList(e) {
                this.projectList = e;
            },
            showMenu(item) {
                if (item === 'projectSet') {
                    this.gotoProjectSet();
                    return;
                }
                if (item === 'report' && this.selectMenu !== 'report') {
                    this.reportNum = 0;
                }
                app.loadPage('?view=' + item);
            },
            gotoProjectSet() {
                if (this.projectSetLoading === true) {
                    return;
                }
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
            showAccountSetting() {
                this.showMenu('profile');
            },
        },
    };
</script>
