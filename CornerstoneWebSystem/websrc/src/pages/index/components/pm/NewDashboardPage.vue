<style scoped lang="less">
    .page {
        width: 100%;
        height: calc(100vh - 60px);
        margin: 0;
        padding: 0;
        display: flex;
        overflow-y: scroll;


        &::-webkit-scrollbar {
            width: 2px;
            height: 6px;
            background-color: transparent;
            -ms-overflow-style: -ms-autohiding-scrollbar;
        }

        &::-webkit-scrollbar-track {
            border-radius: 0;
            background-color: transparent;
        }

        &::-webkit-scrollbar-thumb {
            border-radius: 0;
            background-color: #ddd;
        }

        .dashboard-left {
            padding-left: 8px;
            margin-top: 6px;
            width: 100%;
            height: 100%;
            overflow-y: scroll;

            &::-webkit-scrollbar {
                width: 2px;
                height: 6px;
                background-color: transparent;
                -ms-overflow-style: -ms-autohiding-scrollbar;
            }

            &::-webkit-scrollbar-track {
                border-radius: 0;
                background-color: transparent;
            }

            &::-webkit-scrollbar-thumb {
                border-radius: 0;
                background-color: #ddd;
            }


            /deep/ .ivu-collapse {
                border: none !important;
                margin-bottom: 10px;
            }


            &-menu {
                width: 100%;
                min-height: 100px;
                padding: 8px 5px;
                display: flex;
                justify-content: flex-start;
                margin-right: 10px;
                align-items: center;
                align-content: flex-start;
                flex-wrap: wrap;


                &-item {
                    cursor: pointer;
                    margin: 15px 15px 0 0;
                    border: 1px solid #eee;
                    border-radius: 3px;
                    background-color: #ffffff;
                    padding: 5px 8px;
                    display: inline-flex;
                    justify-content: space-around;
                    align-items: center;

                    &.chosen {
                        color: #2d8cf0;
                        font-weight: bold;
                        /*box-shadow: 2px 2px 2px #cc7a00;*/
                    }

                    span {
                        margin-left: 5px;
                    }
                }
            }

            &-focus {
                width: 100%;
                background-color: #ffffff;
                margin-top: 10px;
                overflow-y: scroll;

                &::-webkit-scrollbar {
                    width: 2px;
                    height: 6px;
                    background-color: transparent;
                    -ms-overflow-style: -ms-autohiding-scrollbar;
                }

                &::-webkit-scrollbar-track {
                    border-radius: 0;
                    background-color: transparent;
                }

                &::-webkit-scrollbar-thumb {
                    border-radius: 0;
                    background-color: #ddd;
                }
            }
        }

        .dashboard-right {
            margin-top: 6px;
            padding-left: 5px;
            background-color: #ffffff;
            width: 100%;
            height: 100%;
            overflow-y: auto;
            display: flex;
            flex-direction: column;

            &::-webkit-scrollbar {
                width: 2px;
                height: 6px;
                background-color: transparent;
                -ms-overflow-style: -ms-autohiding-scrollbar;
            }

            &::-webkit-scrollbar-track {
                border-radius: 0;
                background-color: transparent;
            }

            &::-webkit-scrollbar-thumb {
                border-radius: 0;
                background-color: #ddd;
            }


            &-header {

                /deep/ .menu-item-group {
                    margin-top: 3px !important;
                }

            }

            &-content {
                width: 100%;
                flex: 1;

            }

            /deep/ .ivu-collapse {
                border: none !important;
                margin-bottom: 10px;
            }

        }
    }

    .text-no-wrap {
        .hover-btn {
            display: none;
        }

        &:hover {
            .hover-btn {
                display: inline-block;
            }
        }
    }
    .collapse-tit{
        color: #5cadff !important;
    }

</style>
<i18n>
    {
    "zh_CN":{
    "名称":"名称",
    "进度":"进度",
    "迭代":"迭代",
    "负责人":"负责人",
    "项目":"项目",
    "责任人":"责任人",
    "创建人":"创建人",
    "时间":"时间",
    "状态":"状态",
    "更新时间":"更新时间",
    "我的待办":"我的待办",
    "项目集管理":"项目集管理",
    "我的创建":"我的创建",
    "我的项目":"我的项目",
    "我的变更":"我的变更",
    "项目动态":"项目动态",
    "报表":"报表",
    "汇报":"汇报",
    "仪表盘":"仪表盘",
    "讨论":"讨论",
    "流程":"流程",
    "问卷调查":"问卷调查",
    "迭代燃尽图": "迭代燃尽图",
    "代码提交统计": "代码提交统计",
    "状态分布统计": "状态分布统计",
    "每日新增曲线": "每日新增曲线",
    "每日完成曲线": "每日完成曲线",
    "累计数量曲线": "累计数量曲线",
    "成员工时列表": "成员工时列表",
    "成员缺陷统计": "成员缺陷统计",
    "项目进度": "项目进度",
    "迭代进度": "迭代进度",
    "迭代日历": "迭代日历",
    "成员任务":"成员任务",
    "项目工时列表":"项目工时列表",
    "项目报表":"项目报表",
    "项目数量统计":"项目数量统计",
    "全局人力视图":"全局人力视图",
    "创建和剩余时间":"创建和剩余时间",
    "工时统计":"工时统计"
    },
    "en": {
    "名称":"Name",
    "进度":"Process",
    "迭代":"Iteration",
    "负责人":"Owner",
    "项目":"Project",
    "责任人":"Owner",
    "创建人":"Creator",
    "时间":"Time",
    "更新时间":"Update Time",
    "状态":"Status",
    "我的待办":"Todo",
    "项目集管理":"Project Set",
    "我的创建":"Created",
    "我的项目":"Project",
    "我的变更":"Alteration",
    "项目动态":"Activity",
    "报表":"Chart",
    "汇报":"Report",
    "仪表盘":"Dashboard",
    "讨论":"Discuss",
    "流程":"Workflow",
    "问卷调查":"Surveys",
    "迭代燃尽图": "Burndown chart",
    "代码提交统计": "Code stat",
    "状态分布统计": "Status stat",
    "每日新增曲线": "Daily added",
    "每日完成曲线": "Daily finished",
    "累计数量曲线": "Total count",
    "成员工时列表": "Member Worktime",
    "成员缺陷统计": "Member bugs",
    "项目进度": "Project stat",
    "迭代进度": "Iteration stat",
    "迭代日历": "Iteration calander",
    "成员任务":"Member tasks",
    "项目工时列表":"Project Worktime",
    "项目报表":"Project Report",
    "项目数量统计":"Project count stat",
    "全局人力视图":"Total todo tasks",
    "创建和剩余时间":"Created and rest time",
    "工时统计":"Worktime stat"
    }
    }
</i18n>
<template>
    <div class="page">
        <Split v-model="sp" mode="horizontal">
            <div slot="left" class="dashboard-left ">
                <Collapse simple :value="'1'">
                    <Panel name="1">
                        <span class="collapse-tit">常用菜单({{activeMenuListCount}})</span>
                        <div slot="content" class="dashboard-left-menu">
                            <draggable v-model="menuList"
                                       group="menu"
                                       draggable=".dashboard-left-menu-item"
                                       @end="onmMoveMenuList">
                                <div class="dashboard-left-menu-item" :class="{'chosen':menu.view===view}"
                                     @click="clickMenu(menu)" v-if="hasPermission(menu)"
                                     v-for="menu in menuList" :key="'menu_'+menu.idx">
                                    <Icon :type="menu.icon" size="14"/>
                                    <span>{{menu.name}}</span>
                                </div>
                            </draggable>
                        </div>
                    </Panel>
                </Collapse>

                <Collapse simple>
                    <Panel name="2">
                        <span class="collapse-tit">不常用菜单({{inactiveMenuListCount}})</span>

                        <div slot="content" class="dashboard-left-menu inactive">
                            <draggable v-model="inactiveMenuList"
                                       group="menu"
                                       draggable=".dashboard-left-menu-item"
                                       @end="onmMoveMenuList1">
                                <div class="dashboard-left-menu-item" :class="{'chosen':menu.view===view}"
                                     @click="clickMenu(menu)" v-if="hasPermission(menu)"
                                     v-for="menu in inactiveMenuList" :key="'menu_'+menu.idx">
                                    <Icon :type="menu.icon" size="14"/>
                                    <span>{{menu.name}}</span>
                                </div>
                            </draggable>
                        </div>
                    </Panel>
                </Collapse>

                <div class="dashboard-left-focus ">
                    <FlagTitle label="我关注的项目"></FlagTitle>
                    <table class="table-content" style="table-layout:fixed">
                        <thead>
                        <tr>
                            <th style="width:150px">{{$t('名称')}}</th>
                            <th style="width:100px">{{$t('负责人')}}</th>
                            <th style="width:150px;">{{$t('创建和剩余时间')}}</th>
                            <th style="width:200px;">{{$t('进度')}}</th>
                            <th style="width:250px">{{$t('迭代')}}</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr v-for="(item,idx) in accountStar.projects" :key="'prj_'+item.id" class="table-row">
                            <td class="text-no-wrap" style="width: 150px;">
<!--                                <IconButton-->
<!--                                    class="hover-btn"-->
<!--                                    @click="cancelStar(1,item,idx)"-->
<!--                                    icon="ios-star"-->
<!--                                    color="#FAA424"-->
<!--                                    tips="取消关注"/>-->
                                <div>
                                <span
                                    @click="showProject(item)" class="table-col-name"
                                    :title="item.name">{{item.name}}</span>
                                    <Icon v-if="item.star" class="project-star" type="md-star"/>
                                </div>
                                <div class="text-no-wrap project-group">{{item.description}}</div>
                            </td>
                            <td style="width:100px">
                                <span v-if="item.ownerAccountList&&!Array.isEmpty(item.ownerAccountList)"
                                      v-for="own in item.ownerAccountList" :key="'owner_'+own.id">{{own.name}} </span>
                            </td>
                            <td style="font-size:13px;color:#999;width:150px">
                                <div style="color:#333;margin-right:5px">
                                    <span>{{item.createTime|dateDiff}}天</span>
                                    <span v-if="item.endDate"> /
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
                <div class="dashboard-left-focus">
                    <FlagTitle label="我关注的任务"></FlagTitle>
                    <div class="table-box scrollbox">
                        <table class="table-content" style="table-layout:fixed">
                            <thead>
                            <tr>
                                <th style="width: 250px">{{$t('名称')}}</th>
                                <th style="width:150px;">{{$t('项目')}}</th>
                                <th style="width:120px;">{{$t('责任人')}}</th>
                                <th style="width:150px">{{$t('时间')}}</th>
                                <th style="width:90px;text-align:right">{{$t('状态')}}</th>
                            </tr>
                            </thead>

                            <tbody>
                            <tr v-for="(item,idx) in accountStar.tasks"
                                :key="'todo'+item.id"
                                class="table-row clickable">
                                <td class="text-no-wrap">
<!--                                    <IconButton class="hover-btn"-->
<!--                                                @click.native="cancelStar(2,item,idx)"-->
<!--                                                icon="ios-star"-->
<!--                                                color="#FAA424"-->
<!--                                                tips="取消关注"/>-->
                                    <TaskNameLabel
                                        @click.native="showTaskInfo(item)"
                                        :is-done="item.isFinish"
                                        :id="item.serialNo"
                                        :name="item.name"
                                        :object-type="item.objectTypeName"/>

                                </td>
                                <td>
                                    <div style="max-width:180px" class="text-no-wrap">
                                        {{item.projectName}}
                                    </div>
                                </td>
                                <td class="text-no-wrap">
                                    <TaskOwnerView :tips="false" :value="item"/>
                                </td>
                                <td>
                                    <OverdueLabel v-if="!item.isFinish&&item.endDate"
                                                  :value="item.endDate"></OverdueLabel>
                                    <span v-if="item.isFinish||item.endDate==null">{{item.startDate|fmtDate}}<Icon
                                        type="md-arrow-round-forward"/>{{item.endDate|fmtDate}}</span>
                                </td>
                                <td style="text-align:right">
                                    <div v-if="item.objectType=='1001'||item.objectType=='1002'"></div>
                                    <TaskStatus v-else :label="item.statusName" :color="item.statusColor"></TaskStatus>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>

            </div>
            <div slot="right" class="dashboard-right ">
                <div class="dashboard-right-header" style="padding:10px;" v-if="view!=='wiki'&&view!='sysVersion'">
                    <Collapse simple value="3">
                        <Panel name="3">
                            <span class="collapse-tit">条件过滤</span>
                            <template  slot="content">
                                <div v-if="view==='todo'">
                                    <DashboardTaskFilterView
                                        ref="todoTaskFilterView" @list-changed="setupTodoTaskList" :type="view"/>
                                </div>
                                <div v-if="view==='focusTasks'">
                                    <DashboardFocusTaskFilterView
                                        ref="focusTaskFilterView" :datas="accountStar.tasks" @list-changed="setupFocusTaskList" :type="view"/>
                                </div>

                                <div v-else-if="view==='my'">
                                    <DashboardTaskFilterView
                                        ref="meTaskFilterView" @list-changed="setupMeTaskList" :type="view"/>
                                </div>

                                <div v-else-if="view==='alteration'">
                                    <DashboardAlterationFilterView
                                        ref="alterationFilterView" @list-changed="setupAlterationList"/>
                                </div>

                                <div v-else-if="view==='project'">
                                    <DashboardProjectFilterView
                                        ref="projectFilterView" @list-changed="setupProjectList"/>
                                </div>

                                <div v-else-if="view==='focusProjects'">
                                    <DashboardFocusProjectFilterView
                                        ref="focusProjectFilterView"  :datas="accountStar.projects" @list-changed="setupFocusProjectList"/>
                                </div>
                                <div v-else-if="view==='workflow'">
                                    <DashboardWorkflowFilterView
                                        ref="workflowFilterView" @change="filterWorkflowList"/>
                                </div>

                                <div v-else-if="view==='log'">
                                    <DashboardLogFilterView ref="logFilterView" @change="filterLogList"/>
                                </div>

                                <div v-else-if="view==='surveys'">
                                    <DashboardSurveysFilterView
                                        ref="surveysFilterView" @change="filterSurveysList"/>
                                </div>
                            </template>
                        </Panel>
                    </Collapse>

                </div>

                <div class="dashboard-right-content ">
                    <DashboardTaskView v-if="view==='todo'" :list="todoTaskList"/>
                    <DashboardTaskView v-if="view==='focusTasks'" :list="focusTasksList"/>
                    <DashboardTaskView v-else-if="view==='my'" :list="meTaskList"/>
                    <template v-else-if="view==='project'">
                        <DashboardProjectView
                            v-if="projectList!=null" :list="projectList" :show-create="showProjectCreate"/>
                    </template>
                    <DashboardAlterationView v-else-if="view==='alteration'" :list="alterationList"/>
                    <DashboardChartView v-else-if="view==='chart'"/>
                    <DashboardLogView ref="logView" v-else-if="view==='log'"/>
                    <DashboardReportView
                        @page-changed="reportPageChanged" v-else-if="view==='report'"/>
                    <DashboardCardView ref="cardView" v-else-if="view==='card'"/>
                    <DashboardReportTemplateView v-else-if="view==='report_template'"/>
                    <DashboardDiscussView ref="discussView" v-else-if="view==='discuss'"/>
                    <DashboardWorkflowView ref="workflowView" v-else-if="view==='workflow'"/>
                    <DashboardProfileView ref="profileView" v-else-if="view==='profile'"/>
                    <DashboardSurveysView ref="surveysView" v-else-if="view==='surveys'"/>
                    <DashboardRepositoryView  ref="repositoryView" v-if="view==='sysVersion'"/>
                    <DashboardWikiView  ref="wikiView" v-if="view==='wiki'"/>
                </div>
            </div>
        </Split>
    </div>
</template>

<script>
    import draggable from "vuedraggable"
    import FlagTitle from "../VersionRepository/components/FlagTitle";

    export default {
        mixins: [componentMixin],
        components: {
            FlagTitle,
            draggable
        },
        data() {
            return {
                title: 'NewDashboardPage',
                sp: 0.3,
                list: [],
                account: {},
                roles: [],
                selectMenu: null,
                company: {},
                view: 'todo',
                showSideMenu: true,
                showProjectCreate: false,
                projectSetLoading: false,
                reportNum: 0,
                isRepositoryVersionExtension:app.isRepositoryVersionExtension,
                menus: [
                    {
                        idx: 1,
                        view: 'todo',
                        name: '我的待办',
                        icon: 'md-done-all'
                    },
                    {
                        idx: 2,
                        view: 'my',
                        name: '我的创建',
                        icon: 'md-document'
                    },
                    {
                        idx: 3,
                        view: 'projectSet',
                        name: '项目集',
                        projectSet: true,
                        icon: 'md-cube'
                    },
                    {
                        idx: 4,
                        view: 'project',
                        name: '我的项目',
                        icon: 'md-list-box'
                    },
                    {
                        idx: 5,
                        view: 'alteration',
                        name: '我的变更',
                        icon: 'md-git-compare'
                    },
                    {
                        idx: 6,
                        view: 'log',
                        name: '项目动态',
                        permission: 'company_project_change_log',
                        icon: 'md-list'
                    },
                    {
                        idx: 7,
                        view: 'card',
                        name: '仪表盘',
                        permission: "dashboard_view",
                        icon: 'md-podium'
                    },
                    {
                        idx: 8,
                        view: 'chart',
                        name: '我的报表',
                        icon: 'md-trending-up',
                        permission: 'chart_view',
                        children: [
                            {
                                label: '项目报表',
                                children: [
                                    {id: 0, name: this.$t('迭代燃尽图'),},
                                    {id: 1, name: this.$t('代码提交统计'),},
                                    {id: 5, name: this.$t('项目数量统计'),},
                                    {id: 7, name: this.$t('工时统计'),},
                                    {id: 8, name: this.$t('项目报表'),},
                                    {id: 9, name: this.$t('成员缺陷统计')},
                                ]
                            },
                            {
                                label: '全局报表',
                                children: [
                                    {id: 100, name: this.$t('项目进度'),},
                                    {id: 101, name: this.$t('迭代进度'),},
                                    {id: 102, name: this.$t('迭代日历'),},
                                    {id: 103, name: this.$t('成员任务'),},
                                    {id: 104, name: this.$t('全局人力视图'),},
                                ]
                            },
                            {
                                label: '数据表格',
                                children: []
                            },
                        ]
                    },
                    {
                        idx: 9,
                        view: 'report',
                        name: '我的汇报',
                        permission: "report_view",
                        icon: 'md-clipboard'
                    },
                    {
                        idx: 10,
                        view: 'discuss',
                        name: '我的讨论',
                        permission: "discuss_view",
                        icon: 'md-chatbubbles'
                    },
                    {
                        idx: 11,
                        view: 'workflow',
                        name: '我的流程',
                        permission: "workflow_view",
                        icon: 'md-mail'
                    },
                    {
                        idx: 12,
                        view: 'surveys',
                        name: '问卷调查',
                        permission: "surveys_view",
                        icon: 'md-checkbox-outline'
                    },
                   /* {
                        idx: 13,
                        view: 'foucsProject',
                        name: '关注项目',
                        icon: 'md-star-outline'
                    },
                    {
                        idx: 14,
                        view: 'focusTasks',
                        name: '关注任务',
                        icon: 'md-snow'
                    },*/
                    {
                        idx: 15,
                        view: 'wiki',
                        name: '通商百科',
                        permission: "company_wiki_list",
                        icon: 'logo-wordpress'
                    },
                    {
                        idx: 16,
                        view: 'sysVersion',
                        name: '系统管理',
                        permission: "version_repository_view",
                        icon: 'ios-keypad'
                    },
                ],
                menuList: [],
                inactiveMenuList: [
                    // {
                    //     idx: 1,
                    //     name: '我的待办111',
                    //     icon: 'md-done-all'
                    // },
                ],
                accountStar: {
                    tasks: [],
                    projects: []
                },
                todoTaskList: [],
                focusTasksList: [],
                focusProjectsList: [],
                meTaskList: [],
                alterationList: [],
                projectList: null,
                reportInfo: {},
            };
        },
        computed: {
            showProjectSetMenu() {
                return this.perm('project_set_view') && process.env.VUE_APP_DASHBOARD_PAGE_PROJECT_SET_MENU === 'show';
            },
            inactiveMenuListCount() {
                return this.inactiveMenuList.filter(k => k.idx !== 10000 && this.hasPermission(k)).length;
            },
            activeMenuListCount() {
                return this.menuList.filter(k => k.idx !== 10000 && this.hasPermission(k)).length;
            }
        },
        watch: {
            view(val) {
                if (val === 'my') {
                    this.$nextTick(() => {
                        this.$refs.meTaskFilterView.reloadData();
                    });
                }
                if (val === 'todo') {
                    this.$nextTick(() => {
                        this.$refs.todoTaskFilterView.reloadData();
                    });

                }
            }
        },
        mounted() {
            // this.fillDefaultMenu();
            if(!this.isRepositoryVersionExtension){
                let ms =[];
                 this.menus.map(k=>{
                    if(k.idx!=16){
                        if(k.idx===15){
                            k.name='企业WIKI'
                        }
                        ms.push( k);
                    }
                })
                this.menus = ms;
            }
            console.log(this.menus)
        },
        methods: {
            getLeftDays(date) {
                return getLeftDays(date);
            },
            hasPermission(menu) {
                if (menu.projectSet) {
                    if (!this.showProjectSetMenu) {
                        return false;
                    }
                }
                if (menu.permission) {
                    return this.perm(menu.permission);
                }
                return true;
            },
            clickMenu(menu) {
                let idx = menu.idx;
                if (idx === 10000) {
                    return;
                }
                this.view = menu.view;
                this.updateAccountHomeSetting();
                this.showMenu(menu.view)
            },
            onmMoveMenuList() {
                console.log(this.menuList)
                console.log(this.inactiveMenuList)
                this.fillDefaultMenu();
            },
            onmMoveMenuList1() {
                // console.log(this.menuList)
                // console.log(this.inactiveMenuList)
                this.fillDefaultMenu();
            },
            fillDefaultMenu() {
                var defaultMenu = {
                    idx: 10000, name: '无可用菜单', icon: "md-done-all"
                };
                if (Array.isEmpty(this.menuList)) {
                    this.menuList = [defaultMenu]
                } else {
                    let index = this.menuList.findIndex(item => item.idx === 10000);
                    if (index >= 0) {
                        this.menuList.splice(index, 1);
                    }
                }
                if (Array.isEmpty(this.inactiveMenuList)) {
                    this.inactiveMenuList = [defaultMenu]
                } else {
                    let index = this.inactiveMenuList.findIndex(item => item.idx === 10000);
                    if (index >= 0) {
                        this.inactiveMenuList.splice(index, 1);
                    }
                }

                this.updateAccountHomeSetting();
            },
            updateAccountHomeSetting() {
                let inactive = this.inactiveMenuList.map(k => k.idx);
                let active = this.menuList.map(k => k.idx);
                app.invoke("BizAction.saveAccountHomeSetting", [app.token, {
                    currentMenu: this.menus.filter(item => item.view === this.view)[0].idx,
                    visible: true,
                    menuList: active,
                    inactiveMenuList: inactive
                }], res => {
                    console.log("res--->", res)
                });
            },
            getAccountHomeSetting() {
                let _this = this;
                app.invoke("BizAction.getAccountHomeSetting", [app.token], res => {
                    console.log("res--->", res);
                    if (res&&res.menuList) {
                        if (_this.args.view) {
                            _this.view = _this.args.view;
                        } else {
                            if (res.currentMenu) {
                                _this.view = _this.menus.filter(k => k.idx === res.currentMenu)[0].view;
                            }
                        }
                        res.menuList.forEach(idx => {
                            _this.menus.forEach(mi => {
                                if (mi.idx === idx) {
                                    _this.menuList.push(mi);
                                }
                            })
                        });
                        _this.menus.forEach(item=>{
                            if (!Array.contains(_this.menuList, item.idx, k => k.idx)) {
                                _this.inactiveMenuList.push(item);
                            }
                        })
                    } else {
                        _this.menuList = _this.menus.filter(k => k.idx !== 10000 && this.hasPermission(k));
                    }
                });
            },
            getAccountStars() {
                app.invoke('BizAction.getAccountStars', [app.token], result => {
                    this.accountStar = result;
                }, () => {
                    this.projectSetLoading = false;
                });
            },
            pageLoad() {
                this.account = app.account;
                this.company = app.company;
                this.roles = app.roles;
                if (this.args.view) {
                    this.view = this.args.view;
                }
                this.getAccountHomeSetting();
                this.getAccountStars();

            },
            /*  pageUpdate() {
                  if (this.args.view) {
                      this.view = this.args.view;
                  } else {
                      this.view = 'card';
                  }
              },*/
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
                if (type == 'task.star') {
                    this.getAccountStars();
                }
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
            setupFocusTaskList(e) {
                this.focusTasksList = e;
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
            setupFocusProjectList(e) {
                this.focusProjectsList = e;
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
            showProject(item) {
                app.loadPage('/pm/project/' + item.uuid + '/project');
            },
            showTaskInfo(item) {
                if (!!this.showTaskId) {
                    return;
                }
                app.showDialog(TaskDialog, {
                    taskId: item.uuid,
                    showTopBar: true,
                });
            },
            cancelStar(type, item, idx) {
                app.confirm('确认取消关注吗?', () => {
                    if (type === 1) {
                        this.accountStar.projects.splice(idx, 1);
                        app.invoke('BizAction.cancelStarProject', [app.token, item.id], result => {
                            console.log("cancel project")
                        });
                    } else if (type === 2) {
                        this.accountStar.tasks.splice(idx, 1);
                        app.invoke('BizAction.deleteAccountStar', [app.token, {
                            type: type,
                            associateId: item.id
                        }], result => {
                            console.log("cancel project")
                        });
                    }
                });

            }
        },
    };
</script>
