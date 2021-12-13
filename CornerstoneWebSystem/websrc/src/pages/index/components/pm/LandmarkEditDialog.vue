<style scoped lang="less">
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

    .landmark-container {
        display: flex;
        flex-direction: column;
        align-items: flex-start;
    }

    .form-container {
        display: block;
        position: relative;
        width: 100%;
        padding: 15px;
        margin-bottom: 10px
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
</style>
<i18n>
    {
    "en": {
    "里程碑管理": "Edit Landmark",
    "名称":"Name",
    "详细描述":"Remark",
    "创建信息":"Create information",
    "状态":"Status",
    "删除":"Delete",
    "里程碑":"Landmark",
    "里程碑名称":"Landmark name",
    "选择里程碑周期":"Choose landmark period",
    "里程碑周期":"Landmark period",
    "删除里程碑":"Delete",
    "工期":"Work day",
    "保存":"Save",
    "关闭":"Close",
    "创建":"Create",
    "关联阶段":"Associate stage",
    "前置里程碑":"Pre Landmark",
    "确认要删除里程碑":"Are you sure you want to delete landmark【{0}】?"
    },
    "zh_CN": {
    "里程碑管理": "编辑里程碑",
    "名称":"名称",
    "详细描述":"详细描述",
    "创建信息":"创建信息",
    "状态":"状态",
    "删除":"删除",
    "里程碑":"里程碑",
    "里程碑名称":"里程碑名称",
    "选择里程碑周期":"选择里程碑周期",
    "里程碑周期":"里程碑周期",
    "删除里程碑":"删除里程碑",
    "工期":"工期",
    "保存":"保存",
    "关闭":"关闭",
    "创建":"创建",
    "关联阶段":"关联阶段",
    "前置里程碑":"前置里程碑",
    "确认要删除里程碑":"确认要删除里程碑【{0}】吗？"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('里程碑管理')" width="1000">

        <Row>
            <Col span="15">
                <div class="landmark-container">
                    <div class="form-container">
                        <Form ref="form" :rules="formRule" :model="formItem" label-position="top"
                        >
                            <FormItem :label="$t('名称')" prop="name">
                                <Input :disabled="projectDisabled" v-model.trim="formItem.name"
                                       :placeholder="$t('里程碑名称')"></Input>
                            </FormItem>
                            <FormItem :label="$t('关联阶段')" prop="stageId">
                                <Tag :disabled="projectDisabled" v-if="formItem.stageId" :closable="!initStage&&!projectDisabled"
                                     @on-close="handleClose">
                                    {{formItem.stageName}}({{formItem.stageStartDate|fmtDate}}~{{formItem.stageEndDate|fmtDate}})
                                </Tag>
                                <i-button v-if="formItem.stageId==0&&!projectDisabled" @click="chooseStage"
                                          type="default" size="small"
                                          icon="ios-search"></i-button>
                            </FormItem>
                            <FormItem :label="$t('前置里程碑')" prop="preId">
                                <Tag v-if="formItem.preId" :closable="!projectDisabled" @on-close="handleCloseLandmark">
                                    {{formItem.preName}}
                                </Tag>
                                <i-button v-if="formItem.preId==0&&!projectDisabled" @click="chooseLandmark"
                                          type="default" size="small"
                                          icon="ios-search"></i-button>
                            </FormItem>
                            <!--                            <FormItem :label="$t('里程碑')" prop="beforeId">-->
                            <!--                                <Tag v-if="formItem.preId" closable @on-close="handleClose"> {{formItem.preLandmarkName}}-->
                            <!--                                </Tag>-->
                            <!--                                <i-button v-if="formItem.preId==0" @click="chooseLandmark" type="default" size="small"-->
                            <!--                                          icon="ios-search"></i-button>-->
                            <!--                            </FormItem>-->
                            <FormItem :label="$t('状态')">
                                <DataDictRadio :disabled="projectDisabled" v-model="formItem.status"
                                               type="Stage.status"/>
                            </FormItem>
                            <FormItem :label="$t('里程碑周期')">
                                <DatePicker :disabled="projectDisabled" transfer type="daterange" style="width:300px"
                                            v-model="formItem.dateRange"
                                            :placeholder="$t('选择里程碑周期')" @on-change="rangeChange($event)"></DatePicker>
                            </FormItem>
                            <FormItem :label="$t('详细描述')" prop="description">
                                <Input :disabled="projectDisabled" v-model.trim="formItem.remark" type="textarea"
                                       :row="2"
                                       placeholder="里程碑描述信息"></Input>
                            </FormItem>
                            <FormItem v-if="formItem.createAccountId" :label="$t('创建信息')" prop="description">
                                {{formItem.createAccountName}}于{{formItem.createTime|fmtDateTime}}创建
                            </FormItem>


                            <FormItem v-if="!projectDisabled&&formItem.id>0">
                                <Button type="default" @click="openAssociateDialog">关联任务</Button>
                            </FormItem>
                        </Form>
                    </div>

                    <FlagTitle label="关联任务" size="large"/>
                    <div class="landmark-associate">
                        <template v-for="(item,idx) in formItem.associateList">
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

                                <div class="landmark-associate-row-delete" v-if="prjPerm('landmark_delete')">
                                    <Icon size="16" type="ios-trash-outline"
                                          @click.stop="deleteLandmarkAssociate(item,idx)"/>
                                </div>
                            </div>
                        </template>
                    </div>
                </div>
            </Col>
            <Col span="9">
                <FlagTitle label="操作日志" size="large"/>
                <div class="landmark-log">
                    <div class="landmark-log-item" v-for="item in logList"
                         :key="'log_'+item.id">
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
        <div slot="footer">
            <Row>
                <Col span="12" style="text-align:left;padding-top:5px">
                </Col>
                <Col span="12" style="text-align:right">
                    <Button @click="showDialog=false" type="default" size="large">
                        {{$t('关闭')}}
                    </Button>
                    <Button v-if="formItem.id>0&&prjPerm('landmark_delete')&&!projectDisabled" @click="deleteItem"
                            type="error"
                            size="large">
                        {{$t('删除')}}
                    </Button>
                    <Button v-if="!projectDisabled" @click="confirm" type="default" size="large">
                        {{formItem.id>0?$t('保存'):$t('创建')}}
                    </Button>
                </Col>
            </Row>

        </div>

    </Modal>
</template>


<script>
    import SelectObject from "../../../../components/ui/SelectObject";

    export default {
        components: {SelectObject},
        mixins: [componentMixin],
        data() {
            return {
                continueCreate: false,
                formItem: {
                    id: 0,
                    stageId: 0,
                    stageStartDate: null,
                    stageEndDate: null,
                    preId: 0,
                    preStartDate: null,
                    preEndDate: null,
                    projectId: 0,
                    name: null,
                    remark: null,
                    status: 1,
                    startDate: null,
                    endDate: null,
                    dateRange: [],
                    associateList: [],
                    changeRemark: null
                },
                formRule: {
                    name: [vd.req, vd.name],
                    description: [vd.desc],
                    stageId: [vd.req]
                },
                logList: [],
                initStage:false
            }
        },
        methods: {
            rangeChange(val) {
                console.log(val)
                var _this = this;
                if (val.length === 2) {
                    if (!val[0] || !val[1]) {
                        app.toast("请设置里程碑周期时间");
                        return;
                    }
                    if (_this.formItem.id > 0) {
                        _this.$Modal.confirm({
                            render: (h) => {
                                return h('Input', {
                                    props: {
                                        value: _this.formItem.changeRemark,
                                        autofocus: true,
                                        placeholder: '请输入调整原因'
                                    },
                                    on: {
                                        input: (val) => {
                                            _this.formItem.changeRemark = val;
                                        }
                                    }
                                })
                            },
                            onOk: () => {
                                console.log("click ok")
                                if (!!_this.formItem.changeRemark) {
                                    _this.updateLandmarkWorkday();
                                } else {
                                    app.toast("请输入调整原因");
                                    // _this.$nextTick(()=>{
                                    _this.formItem.dateRange = [new Date(_this.formItem.startDate), new Date(_this.formItem.endDate)];
                                    console.log(_this.formItem)
                                    // })
                                }
                            }, onCancel: () => {
                                console.log("click cancel")
                                // _this.$nextTick(() => {
                                _this.formItem.dateRange = [new Date(_this.formItem.startDate), new Date(_this.formItem.endDate)];
                                // })

                            }
                        })
                    }
                }
            },
            pageLoad() {
                if(this.args.stage){
                    this.formItem.stageId = this.args.stage.id;
                    this.initStage = true;
                    let {name,startDate,endDate}  = this.args.stage;
                    this.formItem.stageName = name;
                    this.formItem.stageStartDate = startDate;
                    this.formItem.stageEndDate = endDate;
                }
                if (this.args.id) {
                    this.formItem.id = this.args.id;

                    this.loadData();
                }
            },
            loadData() {
                app.invoke('BizAction.getLandmarkById', [app.token, this.formItem.id], (info) => {
                    this.formItem = info;
                    this.formItem.dateRange = [new Date(info.startDate), new Date(info.endDate)];
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
                app.invoke('BizAction.getLandmarkLogList', [app.token, {landmarkId: this.formItem.id}], (info) => {
                    this.logList = info;
                });
            },
            updateLandmarkWorkday() {
                let dynamicUpate = false;
                let endDate = this.formItem.dateRange[1];
                let ask = false;
                if (Date.gt(endDate, this.formItem.stageEndDate) && !ask) {
                    app.confirm('里程碑结束时间晚于阶段结束时间，是否同步延期阶段?', () => {
                        ask = true;
                        dynamicUpate = true;
                        this.updateLandmarkWorkdayNative(dynamicUpate);
                    }, () => {
                        ask = true;
                        this.updateLandmarkWorkdayNative(dynamicUpate);
                    }, '是', '否');
                } else {
                    if (Date.gt(endDate, this.formItem.endDate) && !ask) {
                        if (!dynamicUpate) {
                            app.confirm('里程碑设置延期，是否同步阶段延期?', () => {
                                dynamicUpate = true;
                                ask = true;
                                this.updateLandmarkWorkdayNative(dynamicUpate);
                            }, () => {
                                ask = true;
                                this.updateLandmarkWorkdayNative(dynamicUpate);
                            }, '是', '否');
                        }
                    }
                }
            },
            updateLandmarkWorkdayNative(dynamicUpate) {
                let startDate = this.formItem.dateRange[0];
                let endDate = this.formItem.dateRange[1];
                this.formItem = Object.assign(this.formItem, {startDate: startDate, endDate: endDate})
                app.invoke('BizAction.updateLandmarkWorkday', [app.token, this.formItem, dynamicUpate], (info) => {
                    app.toast('修改成功')
                    app.postMessage('landmark.edit')
                    this.formItem.changeRemark = null;
                    this.showDialog = false;
                })
            },
            deleteItem() {
                app.confirm(this.$t('确认要删除里程碑', [this.formItem.name]), () => {
                    app.invoke('BizAction.deleteLandmark', [app.token, this.formItem.id], (info) => {
                        app.toast('删除成功')
                        app.postMessage('landmark.delete')
                        this.showDialog = false;
                    })
                })
            },
            confirm() {
                if (Array.isEmpty(this.formItem.dateRange) || (!this.formItem.dateRange[0] || !this.formItem.dateRange[1])) {
                    app.toast("请设置里程碑周期时间");
                    return;
                }
                //动态更新需要更新阶段的时间
                let dynamicUpate = false;
                let isCreate = this.formItem.id > 0;
                this.formItem.startDate = this.formItem.dateRange[0];
                this.formItem.endDate = this.formItem.dateRange[1];
                if (Date.gt(this.formItem.stageStartDate, this.formItem.startDate)) {
                    app.toast("创建里程碑开始时间不能早于阶段开始时间");
                    return;
                }
                if (Date.gt(this.formItem.endDate, this.formItem.stageEndDate)) {
                    if (isCreate) {
                        app.toast("创建里程碑结束时间不能晚于阶段结束时间");
                        return;
                    }
                }

                this.$refs.form.validate((r) => {
                    if (r) {
                        try {
                            this.confirmForm()
                        } catch (e) {
                            console.error(e)
                        }
                    }
                });
            },
            confirmForm() {
                //
                if (this.formItem.projectId == 0) {
                    this.formItem.projectId = this.args.projectId;
                }
                var action = this.formItem.id > 0 ? 'BizAction.updateLandmark' : 'BizAction.addLandmark';
                app.invoke(action, [app.token, this.formItem], (info) => {
                    if(this.formItem.stageId>0){
                        app.postMessage("stage.edit")
                    }
                    app.postMessage('landmark.edit')
                    if (!this.continueCreate) {
                        this.showDialog = false;
                    } else {
                        this.$refs.form.resetFields();
                    }
                })
            },
            chooseStage() {
                let _this = this;
                app.showDialog(StageSelectDialog, {
                    projectId: app.projectId,
                    id: this.formItem.stageId,
                    callback: (res) => {
                        if (!!res) {
                            _this.formItem.stageName = res[0].name;
                            _this.formItem.stageId = res[0].id;
                            _this.formItem.stageStartDate = res[0].startDate;
                            _this.formItem.stageEndDate = res[0].endDate;
                        }
                    },
                });
            },
            handleClose() {
                this.formItem.stageId = 0;
                this.formItem.stageName = null;
            },
            chooseLandmark() {
                let _this = this;
                app.showDialog(LandmarkSelectDialog, {
                    singleSelect:true,
                    projectId: app.projectId,
                    id: this.formItem.id,
                    callback: (res) => {
                        if (!!res) {
                            _this.formItem.preName = res[0].name;
                            _this.formItem.preId = res[0].id;
                        }
                    },
                });
            },
            handleCloseLandmark() {
                this.formItem.preId = 0;
                this.formItem.preName = null;
            },
            openAssociateDialog(type) {
                app.showDialog(TaskSelectDialog, {
                    singleProject: true,
                    projectId: app.projectId,
                    startDate: this.formItem.startDate,
                    endDate: this.formItem.endDate,
                    callback: (list) => {
                        this.addLandmarkAssociate(type, list);
                    },
                });
            },
            addLandmarkAssociate(type, list) {
                console.log(type, list)
                let associateList = list.map(item => {
                    return {
                        taskId: item.id,
                        landmarkId: this.formItem.id,
                    }
                })
                app.invoke('BizAction.batchAddLandmarkAssociate', [app.token, associateList], (info) => {
                    this.loadData();
                    app.postMessage('landmark.edit')
                    app.toast("关联成功");
                    this.formItem.associateList = this.formItem.associateList.concat(associateList)
                }, (err) => {
                    app.toast("关联失败");
                })
            },
            deleteLandmarkAssociate(associate, idx) {
                app.confirm('确定要解除此里程碑的任务关联吗？', () => {
                    app.invoke('BizAction.deleteLandmarkAssociate', [app.token, associate.id], (info) => {
                        app.postMessage('landmark.edit')
                        app.toast("解除成功");
                        // this.formItem.associateList.splice(idx, 1);
                        this.loadData();
                    }, (err) => {
                        app.toast("解除失败");
                    })
                });
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
        }
    }
</script>
