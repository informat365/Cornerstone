<style scoped>
    .popup {
        position: fixed;
        z-index: 100;
        width: 500px;
        top: 0;
        right: 0;
        height: 100vh;
        overflow: hidden;
        background-color: #fff;
        box-shadow: 9px 0px 11px 15px rgba(225, 225, 225, 0.5);
        border-left: 1px solid rgba(216, 216, 216, 1);
    }

    .notice-list {
        height: calc(100vh - 48px);
        overflow: auto;
    }

    .user-name {
        color: #2962aa;
        font-size: 13px;
    }

    .user-time {
        font-size: 12px;
        color: #999;
        margin-top: 5px;
    }

    .item-info {
        display: inline;
        color: #333;
    }

    .link {
        color: #2962aa;
        cursor: pointer;
    }

    .close-btn {
        cursor: pointer;
        vertical-align: middle;
    }

    .nodata {
        font-size: 20px;
        color: #999;
        text-align: center;
        padding-top: 100px;
    }

    .log-item {
        position: relative;
        margin-top: 25px;
    }

    .log-item-right {
        display: inline-block;
        width: 100%;
    }

    .log-item-name {
        font-size: 12px;
        color: #333;
    }

    .log-item-time {
        font-size: 12px;
        color: #999;
        padding-left: 10px;
    }

    .log-item-content {
        padding-top: 10px;
        font-size: 13px;
        line-height: 1.5;
    }

    .log-item-before {
        background-color: #ffd3d3;
        color: #ff3b58;
        text-decoration-line: line-through;
    }

    .log-item-after {
        background-color: #d4efe6;
        color: #128c87;
    }

    .log-item-opt {
        float: right;
        visibility: hidden;
    }

    .log-item:hover .log-item-opt {
        visibility: visible;
    }

    .change-log-table {
        width: 100%;
        margin-top: 5px;
    }

    .change-log-table td {
        border: none;
    }

    .scm-reversion-link {
        color: #2c5d95;
        cursor: pointer;
    }

    .change-log-table {
        border-left: 4px solid #ccc;
    }

    .log-item-creater {
        display: inline-block;
        padding: 2px 6px;
        text-align: center;
        background-color: #00d5cc;
        color: #fff;
        border-radius: 12px;
        font-size: 12px;
        margin-left: 10px;
    }

    .log-item-owner {
        background-color: #0091ea;
    }

    .log-item-link {
        color: #333;
        padding-left: 5px;
        padding-right: 5px;
    }

    .log-item-link2 {
        color: #0091ea;
        padding-left: 5px;
        padding-right: 5px;
        cursor: pointer;
    }

    .comment-box {
        border-left: 4px solid #ccc;
        padding: 10px;
        margin-top: 5px;
        margin-bottom: 5px;
    }

    .user-name {
        font-weight: bold;
        color: #333;
        margin-right: 5px;
    }
</style>
<i18n>
    {
    "en": {
    "通知":"Notification",
    "修改了":"Edit {0} ",
    "将责任人设置为":"Change owner to ",
    "待认领":"none",
    "发表了新的评论":"New comments",
    "在评论中@了你":"Comment mentioned you",
    "删除了":"Delete",
    "提交了代码":"Submit code ",
    "关联了子对象":"Add relation object ",
    "上传了附件":"Upload file ",
    "删除了关联子对象":"Delete releation object ",
    "删除了关联对象":"Delete releation object",
    "删除了附件":"Delete file",
    "提醒":"Reminder",
    "讨论提醒":"Discuss reminder",
    "已超期提醒":"Overdue reminder",
    "系统通知":"System Notification",
    "流程变更通知":"Workflow change reminder",
    "待填写汇报通知":"Report to be completed reminder",
    "待审核汇报通知":"Report to be review reminder",
    "流程步骤从":"Workflow step changed ",
    "变更到":"to",
    "未设置":"none",
    "对象关联WIKI":"Associate WIKI",
    "对象变更待审批":"Alteration ",
    "对象变更审批结果通知":"Alteration Result",
    "没有通知":"No Data"
    },
    "zh_CN": {
    "通知":"通知",
    "修改了":"修改了{0}属性",
    "将责任人设置为":"将责任人设置为",
    "待认领":"待认领",
    "发表了新的评论":"发表了新的评论",
    "在评论中@了你":"在评论中@了你",
    "删除了":"删除了",
    "提交了代码":"提交了代码",
    "关联了子对象":"关联了子对象",
    "上传了附件":"上传了附件",
    "删除了关联子对象":"删除了关联子对象",
    "删除了关联对象":"删除了关联对象",
    "删除了附件":"删除了附件",
    "提醒":"提醒",
    "讨论提醒":"讨论提醒",
    "已超期提醒":"已超期提醒",
    "系统通知":"系统通知",
    "流程变更通知":"流程变更通知",
    "待填写汇报通知":"待填写汇报通知",
    "待审核汇报通知":"待审核汇报通知",
    "流程步骤从":"流程步骤从",
    "变更到":"变更到",
    "未设置":"未设置",
    "对象关联WIKI":"对象关联WIKI",
    "对象变更待审批":"对象变更待审批",
    "对象变更审批结果通知":"对象变更审批结果通知",
    "没有通知":"没有通知"
    }
    }
</i18n>
<template>
    <div class="popup">
        <Row style="padding:0px 15px;height:48px">
            <Col span="20" style="font-size:15px;color:#333;line-height:48px">{{$t('通知')}}</Col>
            <Col span="4" style="text-align:right;line-height:48px">
                <Icon class="close-btn" @click.native="clickCloseBtn" type="ios-close" :size="30"></Icon>
            </Col>
        </Row>
        <div v-if="tableData" class="scrollbox notice-list">
            <table class="table-content table-content-small" style="border-top:1px solid #ddd">
                <tbody>
                <tr v-for="item in tableData" :key="item.id" class="table-row table-row-small">
                    <td valign="top" style="width:30px;">
                        <AvatarImage
                            v-if="item.optAccountName"
                            :value="item.optAccountImageId"
                            :name="item.optAccountName"
                            type="tips" />
                    </td>
                    <td>
                        <div class="item-title">
                            <div class="item-info">
                                <span class="user-name">{{item.optAccountName}}</span>
                                <span v-if="item.type==2">{{item.name}}</span>
<!--                                <span v-if="item.type==2">{{$t('修改了',[item.detail.task.objectTypeName])}}</span>-->
                                <span v-if="item.type==3">{{$t('将责任人设置为')}}
                                    <template v-if="item.detail.ownerList">{{item.detail.ownerList}}</template>
                                    <template v-if="item.detail.ownerList==''">{{$t('待认领')}}</template>
                                </span>
                                <span v-if="item.type==4">{{$t('发表了新的评论')}}</span>
                                <span v-if="item.type==5">{{$t('在评论中@了你')}}</span>
                                <span v-if="item.type==6">{{$t('删除了')}}{{item.detail.task.objectTypeName}}</span>
                                <span v-if="item.type==7">{{$t('提交了代码')}}</span>
                                <span v-if="item.type==8">{{$t('关联了子对象')}}</span>
                                <span v-if="item.type==9">{{item.name}}</span>
                                <span v-if="item.type==90">{{item.name}}</span>
                                <span v-if="item.type==10">{{$t('上传了附件')}}</span>
                                <span v-if="item.type==11">{{$t('删除了关联子对象')}}</span>
                                <span v-if="item.type==12">{{item.name}}</span>
                                <span v-if="item.type==120">{{item.name}}</span>
                                <span v-if="item.type==13">{{$t('删除了附件')}}</span>
                                <span v-if="item.type==14">{{$t('提醒')}} {{item.detail.remind&&item.detail.remind.remark}}</span>
                                <span v-if="item.type==15">{{$t('对象关联WIKI')}}</span>
                                <span v-if="item.type==16">{{$t('对象变更待审批')}}</span>
                                <span v-if="item.type==17">{{$t('对象变更审批结果通知')}}</span>
                                <span v-if="item.type==51">{{$t('讨论提醒')}}</span>
                                <span v-if="item.type==52">
                                    【{{item.detail.pipelineName}}】{{item.detail.content}}
                                </span>
                                <span v-if="item.type==101">{{$t('已超期提醒')}}
                                        {{item.detail.name}}</span>
                                <span v-if="item.type==102">{{$t('系统通知')}}
                                        {{item.detail.content}}
                                </span>
                                <span v-if="item.type==103">{{$t('即将超期提醒')}}
                                        {{item.detail.name2_15}}
                                </span>
                                <span v-if="item.type==201">{{$t('流程变更通知')}}
                                    <WorkflowNameLabel
                                        class="clickable"
                                        @click.native="showWorkflow(item.detail.uuid)"
                                        :id="item.detail.serialNo"
                                        :name="item.detail.title"
                                        :isDone="false" />
                                </span>
                                <div v-if="item.type==201">
                                    {{$t('流程步骤从')}}
                                    <span class="log-item-after">{{item.detail.beforeNodeName}}</span>
                                    {{$t('变更到')}}
                                    <span class="log-item-after">{{item.detail.currNodeName}}</span>
                                </div>
                                <span v-if="item.type==202">{{$t('在流程@了你')}}
                                    <WorkflowNameLabel
                                        class="clickable"
                                        @click.native="showWorkflow(item.detail.uuid)"
                                        :id="item.detail.serialNo"
                                        :name="item.detail.title"
                                        :isDone="false" />
                                </span>
                                <div v-if="item.type==202">
                                    <div class="comment-box" >
                                        <RichtextLabel :value="item.detail.content"></RichtextLabel>
                                    </div>
                                </div>
                                <span v-if="item.type==301">
                                    {{$t('待填写汇报通知')}} <strong>{{item.detail.name}}</strong>
                                </span>
                                <span v-if="item.type==302">
                                    {{$t('待审核汇报通知')}} <strong>{{item.detail.name}}</strong>
                                </span>

                                <TaskNameLabel
                                    v-if="item.type!=6&&item.detail.task"
                                    style="cursor:pointer;margin-left:8px"
                                    @click.native="showTaskInfo(item.detail.task.uuid)"
                                    :id="item.detail.task.serialNo"
                                    :name="item.detail.task.name"
                                    :object-type="item.detail.task.objectTypeName" />
                                <span v-if="item.type==6">#{{item.detail.task.serialNo}} {{item.detail.task.name}}</span>
                                <span v-if="item.type==51" v-html="item.detail.message"></span>

                                <table v-if="item.type==2" class="change-log-table">
                                    <tr v-for="(t,idx) in item.detail.changeLogItemList" :key="item.id+'_log_'+idx">
                                        <td
                                            class="text-no-wrap"
                                            style="width:70px;max-width:70px;padding-left:5px;padding-top:0;padding-bottom:0">
                                            {{t.name}}
                                        </td>
                                        <td style="padding:5px;padding-top:0;padding-bottom:0">
                                            <span class="log-item-before">
                                                <template v-if="t.beforeContent"> {{t.beforeContent}}</template>
                                                <template v-if="t.beforeContent==null||t.beforeContent==''"> {{$t('未设置')}}</template>
                                            </span>
                                            <Icon
                                                type="md-arrow-round-forward"
                                                style="padding-left:5px;padding-right:5px" />
                                            <span class="log-item-after">
                                                <template v-if="t.afterContent"> {{t.afterContent}}</template>
                                                <template v-if="t.afterContent==null||t.afterContent==''"> {{$t('未设置')}}</template>
                                            </span>
                                        </td>

                                    </tr>
                                </table>
                                <div class="comment-box" v-if="item.type==4||item.type==5">
                                    <RichtextLabel :value="item.detail.commentInfo.comment"></RichtextLabel>
                                </div>

                            </div>
                        </div>
                        <div class="user-time">
                            {{item.createTime|fmtDeltaTime}} {{item.projectName}}
                        </div>
                    </td>
                </tr>

                </tbody>
            </table>
            <div class="nodata" v-if="tableData.length==0">
                {{$t('没有通知')}}
            </div>
        </div>

    </div>
</template>
<script>
    export default {
        data() {
            return {
                tableData: null,
            };
        },
        mounted() {
            this.loadData();
        },
        methods: {
            loadData() {
                var query = {
                    pageIndex: 1,
                    pageSize: 50,
                };
                app.invoke('BizAction.getAccountNotificationList', [app.token, query], (list) => {
                    for (var i = 0; i < list.length; i++) {
                        var t = list[i];
                        try {
                            t.detail = JSON.parse(t.content);
                        } catch (e) {
                        }
                    }
                    this.tableData = list;
                });
            },
            showTaskInfo(uuid) {
                app.showDialog(TaskDialog, {
                    taskId: uuid,
                    showTopBar: true,
                });
            },
            showWorkflow(uuid) {
                app.showDialog(WorkflowDialog, {
                    id: uuid,
                });
            },
            clickCloseBtn() {
                this.$emit('popup-close');
            },
        },
    };
</script>
