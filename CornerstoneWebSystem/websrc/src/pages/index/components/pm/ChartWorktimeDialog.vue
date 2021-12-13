<style scoped lang="less">
    .page {
        min-height: calc(100vh - 51px);
        background-color: #F1F4F5;
        width: 100%;
        padding: 20px;
        text-align: center;
    }

    .chart-content {
        width: 1200px;
        display: inline-block;
        background-color: #fff;
        border: 1px solid #eee;
        border-radius: 5px;
        text-align: left;
    }

    .chart-opt-bar {
        padding: 20px;
        border-bottom: 1px solid #eee;
    }

    .chart-chart {
        padding: 20px 0;
    }

    .content-box {
        display: flex;
        align-items: center;
        padding: 10px;
        flex-direction: column;
    }

    .table-box {
        background-color: #fff;
        padding: 30px;
        padding-top: 10px;
    }

    .table-info {
        color: #999;
        text-align: center;
    }

    .table-count {
        background-color: #E8E8E8;
        color: #666;
        padding: 3px 5px;
        border-radius: 3px;
    }

    .nodata {
        padding: 60px;
        font-size: 20px;
        color: #999;
        text-align: center;
    }

    .chart-opt-delay-list {
        padding: 20px;

        &-title {
            padding-bottom: 10px;
            font-size: 16px;
            font-weight: bold;
            border-bottom: solid 3px #eee;
        }

        &-row {
            height: 40px;
            display: flex;
            align-items: center;
            align-content: center;

            &.account {
                position: relative;
                padding: 0 10px;

                &:before {
                    position: absolute;
                    top: 50%;
                    left: 0;
                    content: "";
                    background-color: #2391ff;
                    height: 12px;
                    width: 3px;
                    margin-top: -6px;
                    display: block;
                }

                .account-cost-time {
                    border-radius: 2px;
                    background-color: #cccccc;
                    font-size: 8px;
                    padding: 1px;
                    margin-left: 8px;
                }
            }

            &.task {
                padding: 0 20px;
                cursor: pointer;
            }

            &.task:hover {
                background-color: #ebf7ff;
            }


            &-name {
                flex: 1;
                overflow: hidden;
                text-overflow: ellipsis;
                display: -webkit-box;
                -webkit-line-clamp: 1;
                -webkit-box-orient: vertical;
                padding: 0 5px;

                .task-project-name {
                    color: #0a6fc0;
                    font-weight: bold;
                }

                .task-work-time {
                    margin: 0 6px;
                }

                .task-name{
                    font-weight: bold;
                }

                .task-content{
                    margin: 0 5px;
                    font-size: 11px;
                    overflow: hidden;
                    text-overflow:ellipsis;
                    white-space: nowrap;
                }
            }

            &-date {
                color: #ed4014;
            }
        }
    }

    .project-item {
        width: 100%;
        position: relative;

        .object-container {
            width: 100%;
            display: flex;
            justify-content: flex-start;

            .object-item {
                width: 120px;
                height: 100px;
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                border-radius: 5px;
                border: 1px solid #cccccc;
                margin: 10px;

                &.checked {
                    box-shadow: 1px 1px 2px 2px #C1C1C1;
                }

                &:hover {
                    box-shadow: 1px 1px 2px 2px #C1C1C1;
                }


                label {
                    font-weight: 600;
                    font-size: 15px;
                }

                .object-summary {
                    label{
                        font-size: 12px;
                        margin: 0 3px;
                    }
                    span {
                        font-size: 12px;
                    }
                }
            }
        }
    }

    .overtime {
        color: red;
    }

    .label-color {
        &-1 {
            color: #dd5826;
        }

        &-2 {
            color: #0AB169;
        }

        &-3 {
            color: #0b7ad1;
        }

        &-4 {
            color: #00a8c6;
        }

        &-5 {
            color: #0C1021;
        }

        &-6 {
            color: #0000ff;
        }

        &-7 {
            color: #6a1778;
        }

        &-8 {
            color: #9C27B0;
        }

        &-9 {
            color: #71a436;
        }
    }

    .v-center-dialog{
        display: flex;
        align-items: center;
        justify-content: center;

        .ivu-modal{
            top: 0;
        }
    }
</style>
<i18n>
    {
    "en": {
    "工时统计": "Worktime stat",
    "项目":"Project",
    "用户":"Member",
    "类型":"Object Type",
    "开始时间":"Start",
    "结束时间":"End",
    "查询":"Query",
    "暂无数据":"No Data",
    "时间":"Time",
    "工时":"Worktime",
    "内容":"Content",
    "创建人":"Created by",
    "小时":"H",
    "条数据 ， 总工时":"{0} items ， total {1} hours",
    "导出":"Export",
    "工时详情":"Worktime list",
    "已用工时":"Used worktime",
    "总工时":"Total expect worktime"
    },
    "zh_CN": {
    "工时统计": "工时统计",
    "项目":"项目",
    "用户":"用户",
    "类型":"对象类型",
    "开始时间":"开始时间",
    "结束时间":"结束时间",
    "查询":"查询",
    "暂无数据":"暂无数据",
    "项目":"项目",
    "时间":"时间",
    "工时":"工时",
    "内容":"内容",
    "创建人":"创建人",
    "小时":"小时",
    "条数据 ， 总工时":"{0} 条数据 ， 总工时 {1} 小时",
    "导出":"导出",
    "工时详情":"工时详情",
    "已用工时":"已用工时",
    "总工时":"总工时"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog"
        :closable="true"
        :mask-closable="false"
        :loading="false" :title="$t('工时统计')" class="fullscreen-modal"
        width="100%"
        :footer-hide="true">
        <div class="page">
            <div class="chart-content">
                <div class="chart-opt-bar">
                    <Form inline>
                        <FormItem>
                            <Select v-model="formItem.projectIdInList" :placeholder="$t('项目')"
                                    filterable  multiple  style="width:400px">
                                <Option v-for="item in projectList" :value="item.id" :key="'prj'+item.id">{{ item.name}}
                                </Option>
                            </Select>
<!--                            <ListSelect placeholder="项目"-->
<!--                                        :multiple="true"-->
<!--                                        :data-list="projectList"-->
<!--                                        v-model="formItem.projectIdInList"-->
<!--                                        placement="bottom-start"></ListSelect>-->
                        </FormItem>
                        <!--<FormItem>
                            <Select clearable v-model="formItem.objectType" :placeholder="$t('类型')" style="width:150px">
                                <template v-for="item in moduleList">
                                    <Option v-if="item.objectType>0" :value="item.objectType" :key="'md'+item.id">{{
                                        item.name }}
                                    </Option>
                                </template>
                            </Select>
                        </FormItem>-->
                        <FormItem>
                            <CompanyUserSelect
                                v-model="formItem.createAccountIdInList"
                                clearable
                                multiple
                                style="width: 150px;margin-right: 20px;"
                                />
                        </FormItem>


                        <FormItem>
                            <ExDatePicker :clearable="true"  type="date" style="width:130px" v-model="formItem.startTimeStart"
                                          :placeholder="$t('开始时间')"></ExDatePicker>
                        </FormItem>

                        <FormItem>
                            <ExDatePicker :day-end="true" :clearable="true" type="date" style="width:130px" v-model="formItem.startTimeEnd"
                                          :placeholder="$t('结束时间')"></ExDatePicker>
                        </FormItem>

                        <FormItem>
                            <Button :disabled="Array.isEmpty(formItem.projectIdInList)&&Array.isEmpty(formItem.createAccountIdInList)" type="default" @click="loadChartData()">
                                {{$t('查询')}}
                            </Button>
                        </FormItem>
                        <FormItem>
                            <Button :disabled="Array.isEmpty(formItem.projectIdInList)&&Array.isEmpty(formItem.createAccountIdInList)" type="default" @click="exportData()">
                                {{$t('导出')}}
                            </Button>
                        </FormItem>

                    </Form>
                </div>
                <div class="project-item" v-for="(project,idx) in list" :key="idx">
                    <Divider size="small">{{project.projectName}}  &nbsp;&nbsp;
                        <span style="font-weight: 500;">{{project.totalCostTime}}/{{project.totalExpectTime}}</span>
                    </Divider>
                    <div class="object-container">
                        <div class="object-item" :class="{checked:(project.projectId+':'+object.objectType)==checkedLog}"
                             v-if="Array.isArray(project.objects)&&!Array.isEmpty(project.objects)"
                             v-for="object in project.objects" :key="'obj_'+object.objectType"
                             @click="showObjectLogs(project.id,object)">
                            <label :class="'label-color-'+object.objectType%9">{{object.objectTypeName}}</label>
                            <div class="object-summary">
                                <div >
                                    <label>{{$t('已用工时')}}</label>
                                    <span :class="{overtime:object.costTime>object.expectTime}">{{object.costTime}}</span>
                                </div>

                                <div >
                                    <label>{{$t('总工时')}}</label>
                                    <span >{{object.expectTime}}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <Modal
                ref="listDialog" v-model="showListDialog"
                :closable="true"
                :mask-closable="true"
                :loading="false" :title="$t('工时详情')"
                width="960"
                class-name="v-center-dialog"
                :footer-hide="true">
                <div class="chart-chart">
                    <div class="chart-opt-delay-list">
                       <!-- <div class="chart-opt-delay-list-title">
                            {{$t('工时详情')}}
                        </div>-->
                        <div class="chart-opt-delay-list-wrap scrollbox">
                            <template v-for="(account) in worktimeLogList">
                                <div
                                    :key="'da-'+account.accountId" class="chart-opt-delay-list-row account">
                                    <AvatarImage
                                        size="small"
                                        :name="account.accountName"
                                        v-model="account.accountImageId"
                                        type="label"/>
                                    <span class="account-cost-time">已用工时 <span
                                        :class="{overtime:account.costTime>account.expectTime}">{{account.costTime}}</span>/{{account.expectTime}}小时</span>
                                </div>
                                <div v-for="log in account.logs" :key="log.id"
                                     @click.stop="showTaskInfo(log)"
                                     class="chart-opt-delay-list-row task">
                                    <Tag>{{ checkedObjectTypeName }}</Tag>
                                    <div class="chart-opt-delay-list-row-name">
                                        <span class="task-project-name">
                                            {{ log.projectName }}
                                        </span>
                                        <span class="task-no numberfont">#{{ log.taskSerialNo }}</span>
                                        <span class="task-work-time">+{{log.hour}}/{{log.expectWorkTime}}</span>
                                        <span class="task-name">
                                          {{ log.taskName }}
                                        </span>
                                        <span class="task-content">
                                          {{ log.content }}
                                        </span>
                                    </div>
                                    <div class="chart-opt-delay-list-row-date">
                                        <Icon type="md-calendar" size="16"
                                              style="margin-right: 3px;margin-bottom: 3px;"/>
                                        <span>{{ log.startTime | fmtDateTime }}</span>
                                    </div>
                                </div>
                            </template>
                            <template v-if="!!worktimeLogList && worktimeLogList.length === 0">
                                <div class="nodata">{{ $t('暂无数据') }}</div>
                            </template>
                        </div>
                    </div>
                </div>
            </Modal>
        </div>
    </Modal>
</template>

<script>

    import MemberSelect from "../../../../components/ui/MemberSelect";

    export default {
        components: {Label, MemberSelect},
        mixins: [componentMixin],
        data() {
            return {
                projectList: [],
                iterationList: [],
                moduleList: [],
                formItem: {
                    projectId: null,
                    startTimeStart: null,
                    startTimeEnd: null,
                    createAccountName: null,
                    createAccountIdInList:[]
                },
                pageQuery: {
                    totalHours: 0,
                    total: 0,
                    pageIndex: 1,
                    pageSize: -1,
                    totalPage: 1,
                },
                list: [],
                logMap: {},
                worktimeLogList: [],
                checkedLog: "",
                checkedObjectTypeName: "",
                memberList: [],
                showListDialog:false
            }
        },
        watch: {
            "formItem.projectId": function (val) {
                this.loadProjectModule();
                // this.loadMemberList();
            },
        },
        methods: {
            pageLoad() {
                this.loadProjectList();
            },
            loadMemberList() {
                app.invoke('BizAction.getProjectMemberInfoList', [app.token, this.formItem.projectId], (list) => {
                    this.formItem.createAccountIdInList =[];
                    this.memberList = list;
                })
            },
            loadProjectList() {
                app.invoke("BizAction.getMyProjectList", [app.token], list => {
                    this.projectList = list;
                });
            },
            loadProjectModule() {
                this.formItem.objectType = null;
                app.invoke('BizAction.getProjectModuleInfoList', [app.token, this.formItem.projectId], (list) => {
                    this.moduleList = list;
                })
            },

            loadChartData() {
                this.loadData(true)
            },
            loadData(resetPage) {
                if (resetPage) {
                    this.pageQuery.pageIndex = 1;
                }
                var query = copyObject(this.pageQuery, this.formItem);
                app.invoke('BizAction.getProjectWorkTimeLogList', [app.token, query], (info) => {
                    this.worktimeLogList = [];
                    this.checkedLog = null;
                    this.list = info.projects;
                    // this.pageQuery.total = info.count;
                    this.pageQuery.totalHours = info.totalHours;
                })
            },
            changePage(delta) {
                var t = this.pageQuery.pageIndex + delta;
                if (t <= 0 || t > this.pageQuery.totalPage) {
                    return;
                }
                this.pageQuery.pageIndex = t;
                this.loadData();
            },
            exportData() {
                var query = {
                    token: app.token,
                    query: this.formItem
                }
               /* if (query.query.startTimeEnd) {
                    const startTimeEnd = new Date(query.query.startTimeEnd);
                    if (!Number.isNaN(startTimeEnd.getTime())) {
                        startTimeEnd.setDate(startTimeEnd.getDate() + 1);
                        query.query.startTimeEnd = startTimeEnd.getTime();
                    }
                }*/
                var queryString = JSON.stringify(query);
                var encoded = Base64.encode(queryString);
                window.open('/p/main/export_project_worktime_list?arg=' + encodeURIComponent(encoded))
            },
            showObjectLogs(projectId, item) {
                this.checkedLog = projectId + ":" + item.objectType;
                this.checkedObjectTypeName = item.objectTypeName;
                this.worktimeLogList = [];
                //根据人来分组[{id,name,imageId,logs:[]}]
                let map = new Map();
                let accountExpectTimeMap = new Map();
                if (!!item.logs && Array.isArray(item.logs)) {
                    item.logs.forEach(item => {
                        let accountmap = map.get(item.createAccountId);
                        if (!accountmap) {
                            map.set(item.createAccountId, {
                                accountId: item.createAccountId,
                                accountName: item.createAccountName,
                                accountImageId: item.createAccountImageId,
                                costTime: item.hour,
                                expectTime: item.expectWorkTime,
                                logs: [item]
                            });
                            //同一个任务多次记录工时
                            accountExpectTimeMap.set(item.createAccountId + ":" + item.taskId, item.createAccountId);
                        } else {
                            let timemap = accountExpectTimeMap.get(item.createAccountId + ":" + item.taskId);
                            if (!timemap) {
                                accountmap.expectTime += item.expectWorkTime;
                                accountExpectTimeMap.set(item.createAccountId + ":" + item.taskId, item.createAccountId);
                            }
                            accountmap.costTime += item.hour;
                            accountmap.logs.push(item);
                        }
                    })

                    //
                    map.forEach((value, key) => {
                        this.worktimeLogList.push(value);
                    })
                }
                this.showListDialog = true;
                console.log(this.worktimeLogList)
            },
            showTaskInfo(item) {
                app.showDialog(TaskDialog, {
                    taskId: item.taskUuid,
                    showTopBar: true,
                    callback: (res) => {
                        if (!res || !res.type) {
                            return;
                        }
                        if (res.type === 'goto') {

                        }
                    },
                });
            }
        }
    }
</script>
