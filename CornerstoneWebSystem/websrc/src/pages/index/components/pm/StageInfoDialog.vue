<style scoped lang="less">

    .stage-info-container{
        display: block;
        position: relative;
        padding: 8px;
        width: 100%;

        &-item{
            display: inline-block;
            width: 49%;
            margin-top: 15px;

            .content{
                margin-left: 8px;
            }
        }
    }

    .stage-associate {
        width: 100%;
        display: block;
        position: relative;
        margin-top: 20px;
        border: 1px solid #eee;
        border-radius: 3px;
        overflow-x: auto;

        & > div {
            border-bottom: 1px solid #efefef;
        }


        &-row {
            height: 40px;
            display: flex;
            align-items: center;
            align-content: center;


            &.task {
                padding: 0 20px;
                cursor: pointer;
            }

            &.task:hover {
                background-color: #ebf7ff;
            }

            &-owner{
                min-width: 80px;
                margin-right: 5px;
            }


            &-name {
                flex: 1;
                overflow: hidden;
                text-overflow: ellipsis;
                display: -webkit-box;
                -webkit-line-clamp: 1;
                -webkit-box-orient: vertical;
                padding: 0 5px;

                .task-name {
                    /*font-weight: bold;*/
                    margin-right: 5px;
                }


            }

            &-date {
                color: #ed4014;
                display: inline-flex;
                align-items: center;
            }

            &-delete {
                margin-left: 8px;
                font-size: 18px;
            }
        }
    }

    .stage-log {
        padding: 5px;
        display: block;

        &-item {
            display: flex;
            align-items: center;
            justify-content: flex-start;
            margin-bottom: 10px;

            &-left {
                padding: 5px;
            }

            &-right {
                display: flex;
                flex-direction: column;
                align-items: flex-start;
                color: #2b2b2b;

                &-content {
                    display: flex;
                    justify-content: flex-start;
                    align-items: center;

                    .account {
                        font-size: 13px;
                    }

                    .time {
                        margin-left: 10px;
                    }
                }

                .content {
                    margin-left: 3px;
                    color: #01a0e4;
                    font-size: 11px;
                }
            }
        }
    }


</style>
<i18n>
    {
    "en": {
    "阶段信息": "Iteration",
    "创建":"Create",
    "燃尽图":"Burndown chart",
    "天视图":"Day",
    "详细描述":"Details",
    "阶段周期":"Stage Period",
    "工期":"Work day(s)",
    "名称":"Stage Name",
    "状态":"Status",
    "前置阶段":"The pre stage",
    "实际":"Actual"
    },
    "zh_CN": {
    "阶段信息": "阶段信息",
    "创建":"创建",
    "燃尽图":"燃尽图",
    "天视图":"天视图",
    "详细描述":"详细描述",
    "阶段周期":"阶段周期",
    "工期":"工期(天)",
    "名称":"阶段名称",
    "状态":"状态",
    "前置阶段":"前置阶段",
    "实际":"实际"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('阶段信息')" width="1000" :footer-hide="true">
        <div>
            <Row>
                <Col span="15">
                    <div class="stage-info-container">
                        <div class="stage-info-container-item">
                            <span class="title">{{$t('名称')}}：</span>
                            <span class="content">{{formItem.name}}</span>
                        </div>
                        <div class="stage-info-container-item">
                            <span class="title">{{$t('状态')}}：</span>
                            <span class="content"> <DataDictLabel v-model="formItem.status" type="Stage.status"/></span>
                        </div>
                        <br>
                        <div class="stage-info-container-item">
                            <span class="title">{{$t('前置阶段')}}：</span>
                            <span class="content">{{formItem.preStageName}}</span>
                        </div>
                        <br>
                        <div class="stage-info-container-item">
                            <span class="title">{{$t('工期')}}：</span>
                            <span class="content">{{formItem.workDay}}</span>
                        </div>
                        <div class="stage-info-container-item">
                            <span class="title">{{$t('阶段周期')}}：</span>
                            <span class="content">{{formItem.startDate|fmtDate}} ~ {{formItem.endDate|fmtDate}}</span>
                        </div>
                        <br>
                        <div class="stage-info-container-item">
                            <span class="title">{{$t('详细描述')}}：</span>
                            <span class="content">{{formItem.remark}}</span>
                        </div>
                        <br>
                        <div class="stage-info-container-item">
                            <span class="title">创建：</span>
                            <span class="content">{{formItem.createAccountName}}于{{formItem.createTime|fmtDateTime}}创建</span>
                        </div>
                    </div>
                    <FlagTitle label="工期调整记录" size="large"/>
                    <div class="stage-log">
                        <div class="stage-log-item" v-for="item in formItem.logList" v-if="item.type===1" :key="'log_'+item.id">
                            <div class="stage-log-item-left">
                                <AvatarImage :value="item.createAccountImageId"
                                             :name="item.createAccountName"></AvatarImage>
                            </div>
                            <div class="stage-log-item-right">
                                <div class="stage-log-item-right-content">
                                    <span class="account">{{item.createAccountName}}</span>
                                    <span class="time">{{item.createTime|fmtDateTime}}</span>
                                </div>
                                <span class="content">{{item.content}}</span>
                            </div>
                        </div>
                    </div>

                    <FlagTitle label="关联里程碑" size="large"/>
                    <div class="stage-associate">
                        <template v-for="(item,idx) in formItem.associateList" v-if="item.type===2&&item.landmarkId>0">
                            <div
                                @click.stop="showLandmarkInfo(item)"
                                class="stage-associate-row task">
                                <Icon type="ios-star"  color="red" title="里程碑事件"/>
                                <div class="stage-associate-row-name">
                                    <span class="stage-associate-row-name1">
                                        {{ item.landmarkName }}
                                    </span>
                                </div>
                                <div class="stage-associate-row-date">
                                    <Icon
                                        type="md-calendar" size="16" style="margin-right: 3px;"/>
                                    <span>{{ item.landmarkStartDate | fmtDate }}~{{item.landmarkEndDate|fmtDate}}</span>
                                </div>
                            </div>
                        </template>
                    </div>

                    <FlagTitle label="关联事件" size="large"/>
                    <div class="stage-associate">
                        <template  v-if="item.type===1" v-for="item in formItem.associateList" >
                            <div
                                @click.stop="showTaskInfo(item)"
                                class="stage-associate-row task">
                                <div class="stage-associate-row-owner">
                                    <TaskOwnerView :value="item"/>
                                </div>
                                <Tag class="tag">{{ item.objectTypeName }}</Tag>
                                <div class="stage-associate-row-name">
                                    <span class="task-name">
                                         <TaskStatus
                                             v-if="item.taskStatusName"
                                             :label="item.taskStatusName"
                                             :color="item.taskStatusColor"/>
                                    </span>
                                    <span class="task-no numberfont">#{{ item.taskSerialNo }}</span>
                                    <span class="task-name">
                                      {{ item.taskName }}
                                    </span>
                                </div>
                                <div
                                    class="stage-associate-row-time"
                                    v-if="!!item.taskCreateTime || !!item.taskEndTime">
                                    {{item.taskCreateTime|fmtDate}}
                                    <Icon type="md-arrow-round-forward"/>
                                    {{item.taskEndTime|fmtDate}}
                                    <template v-if="item.taskEndTime">
                                        <OverdueText
                                            :is-finish="item.isFinish"
                                            v-if="item.taskEndTime"
                                            style="margin-left:5px"
                                            :value="item.taskEndTime"/>
                                    </template>
                                </div>
                            </div>
                        </template>
                    </div>
                </Col>
                <Col span="9">
                    <FlagTitle label="操作日志" size="large"/>
                    <div class="stage-log">
                        <div class="stage-log-item" v-for="item in formItem.logList" v-if="item.type===2" :key="'log_'+item.id">
                            <div class="stage-log-item-left">
                                <AvatarImage :value="item.createAccountImageId"
                                             :name="item.createAccountName"></AvatarImage>
                            </div>
                            <div class="stage-log-item-right">
                                <div class="stage-log-item-right-content">
                                    <span class="account">{{item.createAccountName}}</span>
                                    <span class="time">{{item.createTime|fmtDateTime}}</span>
                                </div>
                                <span class="content">{{item.content}}</span>
                            </div>
                        </div>
                    </div>
                </Col>
            </Row>


        </div>

    </Modal>
</template>

<script>
    import DataDictLabel from "../../../../components/ui/DataDictLabel";
    import AvatarImage from "../../../../components/ui/AvatarImage";


    export default {
        components: {AvatarImage, DataDictLabel},
        mixins: [componentMixin],
        props: ['iterationId'],
        data() {
            return {
                formItem: {},
                rateList: [],
                stageViewType: 'Month',
            }
        },
        watch: {
            "stageViewType": function (val) {
                this.gantt_chart.change_view_mode(val);
            },
        },
        methods: {
            pageLoad() {
                this.loadData();
            },
            loadData() {
                app.invoke('BizAction.getStageById', [app.token, this.args.id], (info) => {
                    this.formItem = info;
                    if (Array.isArray(this.formItem.associateList) && !Array.isEmpty(this.formItem.associateList)) {
                        this.formItem.associateList.forEach(item => {
                            for (let i = 0; i < app.moduleList.length; i++) {
                                if (item.objectType == app.moduleList[i].objectType) {
                                    item.objectTypeName = app.moduleList[i].name;
                                    break;
                                }
                            }
                        })
                    }
                });
            },
            showEditDialog(item) {
                app.showDialog(StageEditDialog, {
                    projectId:app.projectId,
                    id: item.id
                })
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
                            this.showDialog = false;
                        }
                    },
                });
            },
            showLandmarkInfo(item){
                app.showDialog(LandmarkInfoDialog,{
                    id:item.landmarkId
                });
            },
        }
    }
</script>
