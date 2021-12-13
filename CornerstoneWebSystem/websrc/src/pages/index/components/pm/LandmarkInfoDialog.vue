<style scoped lang="less">

    .landmark-info-container{
        display: block;
        position: relative;
        padding: 8px;
        width: 100%;

        &-item{
            display: inline-block;
            width: 49%;
            margin: 15px 0;

            .content{
                margin-left: 8px;
            }
        }
    }

    .landmark-associate {
        width: 100%;
        display: block;
        position: relative;
        margin-top: 20px;
        border: 1px solid #eee;
        border-radius: 3px;
        overflow: auto;

        & > div {
            border-bottom: 1px solid #efefef;
        }

        &-row {
            width: 100%;
            height: 40px;
            display: flex;
            align-items: center;

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
                    margin-right: 5px;
                    font-weight: bold;
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

    .landmark-log {
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
    "描述信息":"Details",
    "里程碑周期":"Landmark Period",
    "前置里程碑":"Pre landmark",
    "工期":"Work day(s)",
    "名称":"Landmark Name",
    "状态":"Status",
    "关联阶段":"The associate stage",
    "实际":"Actual"
    },
    "zh_CN": {
    "阶段信息": "阶段信息",
    "创建":"创建",
    "燃尽图":"燃尽图",
    "天视图":"天视图",
    "描述信息":"描述信息",
    "里程碑周期":"里程碑周期",
    "工期":"工期(天)",
    "名称":"阶段名称",
    "前置里程碑":"前置里程碑",
    "状态":"状态",
    "关联阶段":"关联阶段",
    "实际":"实际"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('里程碑信息')" width="1000" :footer-hide="true">
        <div>
            <Row>
                <Col span="15">
                    <div class="landmark-info-container">
                        <div class="landmark-info-container-item">
                            <span class="title">{{$t('名称')}}：</span>
                            <span class="content">{{formItem.name}}</span>
                        </div>
                        <div class="landmark-info-container-item">
                            <span class="title">{{$t('状态')}}：</span>
                            <span class="content"> <DataDictLabel v-model="formItem.status" type="Stage.status"/></span>
                        </div>
                        <br>
                        <div class="landmark-info-container-item">
                            <span class="title">{{$t('关联阶段')}}：</span>
                            <span class="content">{{formItem.stageName}}</span>
                        </div>
                        <br>
                        <div class="landmark-info-container-item">
                            <span class="title">{{$t('前置里程碑')}}：</span>
                            <span class="content">{{formItem.preName}}</span>
                        </div>
                        <br>
                        <div class="landmark-info-container-item">
                            <span class="title">{{$t('里程碑周期')}}：</span>
                            <span class="content">{{formItem.startDate|fmtDate}} ~ {{formItem.endDate|fmtDate}}</span>
                        </div>
                        <br>
                        <div class="landmark-info-container-item">
                            <span class="title">{{$t('描述信息')}}：</span>
                            <span class="content">{{formItem.remark}}</span>
                        </div>
                        <br>
                        <div class="landmark-info-container-item">
                            <span class="title">创建信息：</span>
                            <span class="content">{{formItem.createAccountName}}于{{formItem.createTime|fmtDateTime}}创建</span>
                        </div>
                    </div>


                    <FlagTitle label="关联任务" size="large"/>
                    <div class="landmark-associate">
                        <template v-for="(item,idx) in formItem.associateList"  >
                            <div
                                @click.stop="showTaskInfo(item)"
                                class="landmark-associate-row task">
                                <div class="landmark-associate-row-owner">
                                    <TaskOwnerView :value="item"/>
                                </div>

                                <Tag class="tag">{{ item.objectTypeName }}</Tag>
                                <div class="landmark-associate-row-name">
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
                    <div class="landmark-log">
                        <div class="landmark-log-item" v-for="item in logList"  :key="'log_'+item.id">
                            <div class="landmark-log-item-left">
                                <AvatarImage :value="item.createAccountImageId"
                                             :name="item.createAccountName"></AvatarImage>
                            </div>
                            <div class="landmark-log-item-right">
                                <div class="landmark-log-item-right-content">
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
                landmarkViewType: 'Month',
                logList: []
            }
        },
        watch: {
            "landmarkViewType": function (val) {
                this.gantt_chart.change_view_mode(val);
            },
        },
        methods: {
            pageLoad() {
                this.loadData();
            },
            loadData() {
                app.invoke('BizAction.getLandmarkById', [app.token, this.args.id], (info) => {
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
                this.getLogList();
            },
            getLogList() {
                app.invoke('BizAction.getLandmarkLogList', [app.token, {landmarkId: this.args.id}], (info) => {
                    this.logList = info;
                });
            },
        }
    }
</script>
