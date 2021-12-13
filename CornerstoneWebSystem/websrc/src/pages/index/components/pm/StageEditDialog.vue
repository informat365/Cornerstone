<style scoped lang="less">
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

    .stage-container {
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

            &-owner {
                margin-right: 5px;
                min-width: 80px;
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

        /* .stage-associate-row {
             padding: 10px;

             &-title {
                 padding-bottom: 10px;
                 font-size: 16px;
                 font-weight: bold;
                 border-bottom: solid 3px #eee;
             }


         }*/
    }
</style>
<i18n>
    {
    "en": {
    "阶段管理": "Stage Details",
    "名称":"Name",
    "详细描述":"Remark",
    "状态":"Status",
    "删除":"Delete",
    "阶段":"Stage",
    "阶段名称":"Stage name",
    "选择阶段周期":"Choose stage period",
    "删除阶段":"Delete",
    "工期":"Work day(days)",
    "保存":"Save",
    "关闭":"Close",
    "创建":"Create",
    "前置阶段":"Pre Stage",
    "确认要删除阶段":"Are you sure you want to delete stage【{0}】?"
    },
    "zh_CN": {
    "阶段管理": "阶段详情",
    "名称":"名称",
    "详细描述":"详细描述",
    "状态":"状态",
    "删除":"删除",
    "阶段":"阶段",
    "阶段名称":"阶段名称",
    "选择阶段周期":"选择阶段周期",
    "删除阶段":"删除阶段",
    "工期":"工期(天)",
    "保存":"保存",
    "关闭":"关闭",
    "创建":"创建",
    "前置阶段":"前置阶段",
    "确认要删除阶段":"确认要删除阶段【{0}】吗？"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('阶段管理')" width="1000">

        <Row>
            <Col span="15">
                <div class="stage-container">
                    <div class="form-container">
                        <Form ref="form" :rules="formRule" :model="formItem" label-position="top"
                        >
                            <FormItem :label="$t('名称')" prop="name">
                                <Input :disabled="projectDisabled" v-model.trim="formItem.name"
                                       :placeholder="$t('阶段名称')"></Input>
                            </FormItem>
                            <FormItem :label="$t('前置阶段')" prop="beforeId">
                                <Tag v-if="formItem.preId" :closable="!projectDisabled" @on-close="handleClose">
                                    {{formItem.preStageName}}
                                </Tag>
                                <i-button v-if="formItem.preId==0&&!projectDisabled" @click="chooseStage" type="default"
                                          size="small"
                                          icon="ios-search"></i-button>
                            </FormItem>
                            <FormItem :label="$t('状态')">
                                <DataDictRadio :disabled="projectDisabled" v-model="formItem.status"
                                               type="Stage.status"/>
                            </FormItem>
                            <FormItem :label="$t('阶段周期')">
                                <DatePicker :disabled="projectDisabled" type="daterange" style="width:300px"
                                            v-model="formItem.dateRange"
                                            :placeholder="$t('选择阶段周期')" @on-change="rangeChange($event)"></DatePicker>
                            </FormItem>
                            <FormItem :label="$t('工期')" prop="name">
                                <span>{{formItem.workDay}}</span>
                            </FormItem>
                            <FormItem :label="$t('详细描述')" prop="description">
                                <Input :disabled="projectDisabled" v-model.trim="formItem.remark"
                                       placeholder="阶段描述信息"></Input>
                            </FormItem>
                            <FormItem v-if="formItem.createAccountId" :label="$t('创建信息')" prop="description">
                                {{formItem.createAccountName}}于{{formItem.createTime|fmtDateTime}}创建
                            </FormItem>

                            <FormItem v-if="!projectDisabled&&formItem.id>0">
                                <Button type="default" @click="openAssociateDialog(1)">关联事件</Button>
                                <Button  style="margin-left: 10px;" type="default"
                                        @click="chooseLandmarkDialog()">
                                    关联里程碑
                                </Button>
                                <Button  style="margin-left: 10px;" type="default"
                                        @click="createLandmarkDialog()">
                                    创建里程碑
                                </Button>

                            </FormItem>
                        </Form>
                    </div>

                    <FlagTitle v-if="formItem.id>0"  label="工期调整记录" size="large"/>
                    <div class="stage-log">
                        <div class="stage-log-item" v-for="item in formItem.logList" v-if="item.type===1"
                             :key="'log_'+item.id">
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

                    <FlagTitle v-if="formItem.id>0"  label="关联里程碑" size="large"/>
                    <div class="stage-associate">
                        <template v-for="(item,idx) in formItem.associateList" v-if="item.type===2&&item.landmarkId>0">
                            <div
                                @click.stop="showLandmarkInfo(item)"
                                class="stage-associate-row task">
                                <Icon type="ios-star" color="red" title="里程碑事件"/>
                                <div class="stage-associate-row-name">
                            <span class="task-project-name">
                                {{ item.landmarkName }}
                            </span>
                                </div>
                                <div class="stage-associate-row-date">
                                    <Icon
                                        type="md-calendar" size="16" style="margin-right: 3px;"/>
                                    <span>{{ item.landmarkStartDate | fmtDate }}~{{item.landmarkEndDate|fmtDate}}</span>
                                </div>

                                <div class="stage-associate-row-delete" v-if="prjPerm('stage_delete')">
                                    <Icon type="ios-trash-outline" @click.stop="deleteLandmarkAssociate(item,idx)"/>
                                </div>
                            </div>
                        </template>
                    </div>

                    <FlagTitle v-if="formItem.id>0" label="关联事件" size="large"/>
                    <div class="stage-associate">
                        <template v-for="(item,idx) in formItem.associateList" v-if="item.type===1">
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
                                <div class="chart-opt-delay-list-row-delete" v-if="prjPerm('stage_delete')">
                                    <Icon size="16" type="ios-trash-outline"
                                          @click.stop="deleteStageAssociate(item,idx)"/>
                                </div>
                            </div>
                        </template>
                    </div>
                </div>
            </Col>
            <Col span="9">
                <FlagTitle label="操作日志" size="large"/>
                <div class="stage-log">
                    <div class="stage-log-item" v-for="item in formItem.logList" v-if="item.type===2"
                         :key="'log_'+item.id">
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
        <div slot="footer">
            <Row>
                <Col span="12" style="text-align:left;padding-top:5px">
                </Col>
                <Col span="12" style="text-align:right">
                    <Button @click="showDialog=false" type="default" size="large">
                        {{$t('关闭')}}
                    </Button>
                    <Button v-if="formItem.id>0&&prjPerm('stage_delete')&&!projectDisabled" @click="deleteItem"
                            type="error" size="large">
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
    import LandmarkInfoDialog from "./LandmarkInfoDialog";

    export default {
        components: {SelectObject},
        mixins: [componentMixin],
        data() {
            return {
                continueCreate: false,
                formItem: {
                    id: 0,
                    preId: 0,
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
                },
                hasLandmark: false,
            }
        },
        methods: {
            rangeChange(val) {
                console.log(val)
                var _this = this;
                if (val.length === 2) {
                    if (!val[0] || !val[1]) {
                        app.toast("请设置阶段周期时间");
                        return;
                    }
                    let workDay = window.dateDiff(new Date(val[0]), new Date(val[1])) + 1;
                    if (_this.formItem.workDay != workDay) {
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
                                        _this.formItem.startDate = _this.formItem.dateRange[0];
                                        _this.formItem.endDate = _this.formItem.dateRange[1];
                                        _this.formItem.workDay = workDay;
                                        _this.updateStageWorkday();
                                    } else {
                                        app.toast("请输入调整原因");
                                        // _this.$nextTick(()=>{
                                        _this.formItem.dateRange = [new Date(_this.formItem.startDate), new Date(_this.formItem.endDate)];
                                        console.log(_this.formItem)
                                        // })
                                    }
                                }, onCancel: () => {
                                    console.log("click cancel")
                                    _this.$nextTick(() => {
                                        _this.formItem.dateRange = [new Date(_this.formItem.startDate), new Date(_this.formItem.endDate)];
                                    })

                                }
                            })
                        }
                    }
                } else {
                    this.formItem.workDay = 0;
                }
            },
            pageLoad() {
                if (this.args.id) {
                    this.formItem.id = this.args.id;
                    this.loadData();
                }
            },
            /*pageMessage(type){
                if(type==='stage.edit'){
                    this.loadData();
                }
            },*/
            loadData() {
                app.invoke('BizAction.getStageById', [app.token, this.formItem.id], (info) => {
                    this.formItem = info;
                    this.formItem.dateRange = [new Date(info.startDate), new Date(info.endDate)];
                    if (Array.isArray(this.formItem.associateList) && !Array.isEmpty(this.formItem.associateList)) {
                        this.formItem.associateList.forEach(item => {
                            if (item.type === 2 && item.landmarkId > 0) {
                                this.hasLandmark = true;
                            }
                            for (let i = 0; i < app.moduleList.length; i++) {
                                if (item.objectType == app.moduleList[i].objectType) {
                                    item.objectTypeName = app.moduleList[i].name;
                                    break;
                                }
                            }
                        })
                    }
                })
            },
            updateStageWorkday() {
                app.invoke('BizAction.updateStageWorkday', [app.token, this.formItem, true], (info) => {
                    app.toast('修改成功')
                    app.postMessage('stage.edit')
                    this.formItem.changeRemark = null;
                    this.showDialog = false;
                })
            },
            deleteItem() {
                app.confirm(this.$t('确认要删除阶段', [this.formItem.name]), () => {
                    app.invoke('BizAction.deleteStage', [app.token, this.formItem.id], (info) => {
                        app.toast('删除成功')
                        app.postMessage('stage.delete')
                        this.showDialog = false;
                    })
                })
            },
            confirm() {
                if (Array.isEmpty(this.formItem.dateRange) || (!this.formItem.dateRange[0] || !this.formItem.dateRange[1])) {
                    app.toast("请设置阶段周期时间");
                    return;
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
                this.formItem.startDate = this.formItem.dateRange[0];
                this.formItem.endDate = this.formItem.dateRange[1];
                var action = this.formItem.id > 0 ? 'BizAction.updateStage' : 'BizAction.addStage';
                app.invoke(action, [app.token, this.formItem], (info) => {
                    app.postMessage('stage.edit')
                    if (!this.continueCreate) {
                        this.showDialog = false;
                    } else {
                        this.$refs.form.resetFields();
                    }
                })
            },
            openAssociateDialog(type) {
                //已关联的不再显示
                let excludeIds = [];
                if(!Array.isEmpty(this.formItem.associateList)){
                    excludeIds =this.formItem.associateList.filter(k=>k.type===1).map(k=>k.associateId);
                }
                app.showDialog(TaskSelectDialog, {
                    projectId: app.projectId,
                    idNotInList:excludeIds,
                    singleProject:true,
                    startDate: this.formItem.startDate,
                    endDate: this.formItem.endDate,
                    callback: (list) => {
                        this.addStageAssociate(type, list);
                    },
                });
            },
            chooseLandmarkDialog() {
                var excludeIds =[];
                if(!Array.isEmpty(this.formItem.associateList)){
                    excludeIds =this.formItem.associateList.filter(k=>k.type===2).map(k=>k.associateId);
                }
                app.showDialog(LandmarkSelectDialog, {
                    idNotInList:excludeIds,
                    projectId: app.projectId,
                    callback: (list) => {
                        this.addStageAssociate(2, list);
                    },
                });
            },
            addStageAssociate(type, list) {
                console.log(type, list)
                let associateList = list.map(item => {
                    return {
                        stageId: this.formItem.id,
                        associateId: item.id,
                        landmarkId: item.id,
                        type: type
                    }
                })
                app.invoke('BizAction.batchAddStageAssociate', [app.token, associateList], (info) => {
                    this.loadData();
                    app.postMessage('stage.edit')
                    app.toast("关联成功");
                    this.formItem.associateList = this.formItem.associateList.concat(associateList)
                }, (err) => {
                    app.toast("关联失败");
                })
            },
            deleteStageAssociate(associate, idx) {
                app.confirm('确定要解除此阶段事件关联吗？', () => {
                    app.invoke('BizAction.deleteStageAssociate', [app.token, associate.id], (info) => {
                        app.postMessage('stage.edit')
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
            chooseStage() {
                let _this = this;
                app.showDialog(StageSelectDialog, {
                    projectId: app.projectId,
                    id: this.formItem.id,
                    callback: (res) => {
                        if (!!res) {
                            console.log(res)
                            _this.formItem.preStageName = res[0].name;
                            _this.formItem.preId = res[0].id;
                            _this.$forceUpdate();
                            console.log(_this.formItem)
                        }
                    },
                });
            },

            handleClose() {
                this.formItem.preId = 0;
                this.formItem.preStageName = null;
            },
            createLandmarkDialog() {
                app.showDialog(LandmarkEditDialog, {
                    projectId: this.formItem.projectId,
                    stage: this.formItem
                })
            },
            showLandmarkInfo(item) {
                app.showDialog(LandmarkInfoDialog, {
                    id: item.landmarkId
                });
            },
            deleteLandmarkAssociate(item) {
                app.confirm('确定要解除此里程碑关联吗？', () => {
                    app.invoke('BizAction.deleteStageAssociate', [app.token, item.id], (info) => {
                        app.postMessage('stage.edit')
                        app.toast("解除成功");
                        this.loadData();
                    }, (err) => {
                        app.toast("解除失败");
                    })
                });
            }

        }
    }
</script>
