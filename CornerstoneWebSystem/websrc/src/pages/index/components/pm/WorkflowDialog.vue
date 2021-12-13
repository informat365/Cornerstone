<style scoped>
.dialog-wrap{
    height:calc(100vh - 48px);
    display: flex;
    flex-direction: column;
    overflow: hidden;
    border-radius: 3px;
}
.dialog-form-content{
    flex:1;
    overflow: auto;
}
.form-number{
    font-size:16px;
    font-weight: bold;
    color:#666;
    position:absolute;
    top:15px;
    left:10px;
    display: flex;
    align-items: center;
}
.top-opt-btn{
    padding-top:2px;
    padding-bottom:2px;
}
.form-item-box{
    padding-bottom:40px;
    border-top:1px solid #eee;
}
.opt{
    display: flex;
    align-items:center;
    position:absolute;
    top:-26px;
    left:0px;
    width:750px;
}
.opt-gap{
    flex:1;
}
.mr{
    margin-right: 5px;
}
.ml{
    margin-left: 5px;
}
.section-header{
    display: flex;
    align-items: center;
    padding:15px 30px;
    padding-bottom:0px;
    border-bottom:1px solid #eee;
}
.section-tab{
    font-weight: bold;
    color:#666;
    font-size:14px;
    margin-right:15px;
    cursor: pointer;
    position: relative;
    padding-bottom:7px;
}
.section-tab-active{
    color:#009AF4;
}
.section-tab-active::after {
    height: 4px;
    width: 50%;
    transform:translateX(50%);
    display: block;
    background: rgba(35, 145, 255, 1);
    content: "";
    position: absolute;
    bottom: 0px;
    left: 0;
    z-index:10;
}
.form-dialog-header{
    position: relative;
    height:48px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #F7F7F7;
    border-bottom: 1px solid #ccc;
    padding: 0 30px;
}
.form-dialog-header-color{
    width:10px;
    height:10px;
    border-radius: 50%;
    display: inline-block;
    margin-right:5px;
}
.graph-container{
    display: flex;
    align-items: center;
    padding:20px;
    flex-direction: column;
}
.info-bar{
    padding:20px 30px;
    background-color: #fbfbfb;
    border-top:1px solid #eee;
    border-bottom:1px solid #eee;
}
.info-bar-title{
    font-size:13px;
    color:#666;
    width:80px;
}
.info-bar-row{
    display: flex;
    align-items: flex-start;
    margin-bottom:10px;
}
.info-bar-row:last-child{
    margin-bottom: 0;
}
.info-bar-value{
    flex:1;
    font-size:13px;
    color:#666;
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    font-weight: bold;
}

.changelog-container{
    padding:30px;
}

.comment-box{
    background-color: #fff;
    border-top:1px solid #eee;
    box-shadow: 0 0 8px 0 rgba(0,0,0,.15)!important;
}
.comment-box-editor{
    padding: 20px;
}
.comment-box-tips{
    padding:7px 15px;
}
.comment-box-tips-inner{
    background-color: #F3F3F3;
    border-radius: 4px;
    color:#666;
    font-size:14px;
    padding:5px 10px;
    cursor: pointer;
}
.cancel-btn{
    margin-right:10px;
    font-size:13px;
    color:#666;
    cursor: pointer;
}
.changelog-container .time{
    color:#666;
}
.changelog-container .content{
    font-size:13px;
    margin-top:10px;
}
.owner-row{
    display: flex;
    margin-bottom:5px;
}
.owner-row-name{
    width:100px;
}
.owner-row-detail{
    flex:1;
}
.owner-row-status{
    width:80px;
    border-radius: 3px;
    padding:1px 8px;
    font-weight: normal;
    font-size:12px;
}
.owner-row-status-1{
    color:#f29c2b;
    border:2px solid #f29c2b;
}
.owner-row-status-2{
    color:#8bc24c;
    border:2px solid #8bc24c;
}
.owner-row-status-3{
    color:#8bc24c;
    border:2px solid #8bc24c;
}

.owner-row-detail-time{
    font-size:12px;
    color:#666;
    font-weight: normal;
    margin-left:5px;
}

.node-name{
    color:#555;
    background-color: #eee;
    border-radius: 3px;
    padding:1px 7px;
    font-size:12px;
}
.owner-log-detail-remark{
    border-left:4px solid #eee;
    padding-left:5px;
    margin-top:10px;
    margin-bottom:10px;
    font-weight: normal;
}
.change-log-detail-box{
    border-left:4px solid #eee;
    padding-left:5px;
    margin-top:10px;
    margin-bottom:10px;
}
.owner-log-detail-box{
    border-left:4px solid #ddd;
    padding-left:10px;
    margin-top:10px;
    margin-bottom:10px;
}
.change-log-comment-box{
    position: relative;
}
.change-log-comment-box:hover .delete-comment-btn{
    visibility:visible;
}
.change-log-content-desc{
    color:#666;
}
.delete-comment-btn{
    right:0;
    top:0;
    position:absolute;
    visibility: hidden;
    color:#bbb;
    cursor: pointer;
    z-index:10;
}
.change-log-remark{
    border-left:4px solid #eee;
    padding-left:5px;
    margin-top:10px;
    margin-bottom:10px;
    font-weight: normal;
}
.change-log-before{
    background-color: #ffd3d3;
    color: #ff3b58;
    word-break: break-all;
}
.change-log-after{
    background-color: #d4efe6;
    color: #128c87;
    word-break: break-all;
}
.change-log-detail-opt{
    display:flex;
    padding-left:6px;
    margin-bottom:10px;
}
.change-log-detail-opt-name{
    width:100px;
}
.change-log-detail-opt-value{
    flex:1;
}
</style>


<i18n>
    {
    "en": {
        "暂存": "Save",
    "提交": "Submit",
    "终止": "Terminal",
    "打印": "Print",
    "撤回": "Revoke",
    "删除": "Delete",
    "回退到": "Backto",
    "转交": "Transfer",
    "提交人：": "Submitter：",
    "提交时间：": "Start Time：",
    "完成时间：": "Finish Time：",
    "当前步骤：": "Step：",
    "流转方式：": "Type：",
    "所有负责人提交后流转到下一步骤": "All",
    "任意一个负责人提交后流转到下一步骤": "Any",
    "抄送：": "CC：",
    "负责人：": "Owner：",
    "未处理": "Working",
    "已提交": "Processed",
    "备注：": "Remark：",
    "变更记录": "Change Log",
    "停留时间": "Stay Time",
    "流程图": "Chart",
    "创建了流程": "Create",
    "终止了流程": "Terminal",
    "撤回了流程": "Revoke",
    "发表了评论": "Commit",
    "发起流程流转": "Next",
    "转交责任人": "Transfer",
    "回退了流程": "Return",
    "流程结束": "End",
    "提交了流程": "Start process",
    "结果": "Result",
    "原责任人": "Old Owner",
    "新责任人": "New Owner",
    "节点": "Node",
    "状态": "Status",
    "停留时间": "Stay Time",
    "详细记录": "Detail",
    "已处理": "Processed",
    "发表评论": "Comment",
    "取消": "Cancel",
    "确定": "OK",
    "确定删除此评论吗？": "Are you sure to delete this comment?",
    "请输入评论内容": "Please enter comments",
    "评论成功": "Success",
    "操作成功": "Success",
    "确定要终止此流程吗？": "Are you sure you want to terminate this process?",
    "确定要撤回此流程吗？": "确定要撤回此流程吗？",
    "确定要删除此流程吗？删除后数据不能恢复，请谨慎操作！": "Are you sure you want to delete this process? Data can not be recovered after deletion, please operate with caution!",
    "确定要提交流程到下一节点吗？": "Are you sure you want to submit the process to the next node?",
    "提交成功": "Success",
    "保存成功": "Success",
    "他人": "Others",
    "确定要回退此流程到吗？": "Are you sure you want to return this process to {0}?",
    "提交了流程": "Submitted process"
    },
    "zh_CN": {
    "暂存": "暂存",
    "提交": "提交",
    "终止": "终止",
    "打印": "打印",
    "撤回": "撤回",
    "删除": "删除",
    "回退到": "回退到",
    "转交": "转交",
    "提交人：": "提交人：",
    "提交时间：": "提交时间：",
    "完成时间：": "完成时间：",
    "当前步骤：": "当前步骤：",
    "流转方式：": "流转方式：",
    "所有负责人提交后流转到下一步骤": "所有负责人提交后流转到下一步骤",
    "任意一个负责人提交后流转到下一步骤": "任意一个负责人提交后流转到下一步骤",
    "抄送：": "抄送：",
    "负责人：": "负责人：",
    "未处理": "未处理",
    "已提交": "已提交",
    "备注：": "备注：",
    "变更记录": "变更记录",
    "停留时间": "停留时间",
    "流程图": "流程图",
    "创建了流程": "创建了流程",
    "终止了流程": "终止了流程",
    "撤回了流程": "撤回了流程",
    "发表了评论": "发表了评论",
    "发起流程流转": "发起流程流转",
    "转交责任人": "转交责任人",
    "回退了流程": "回退了流程",
    "流程结束": "流程结束",
    "提交了流程": "提交了流程",
    "结果": "结果",
    "原责任人": "原责任人",
    "新责任人": "新责任人",
    "节点": "节点",
    "状态": "状态",
    "停留时间": "停留时间",
    "详细记录": "详细记录",
    "已处理": "已处理",
    "发表评论": "发表评论",
    "取消": "取消",
    "确定": "确定",
    "确定删除此评论吗？": "确定删除此评论吗？",
    "请输入评论内容": "请输入评论内容",
    "评论成功": "评论成功",
    "操作成功": "操作成功",
    "确定要终止此流程吗？": "确定要终止此流程吗？",
    "确定要撤回此流程吗？": "确定要撤回此流程吗？",
    "确定要删除此流程吗？删除后数据不能恢复，请谨慎操作！": "确定要删除此流程吗？删除后数据不能恢复，请谨慎操作！",
    "确定要提交流程到下一节点吗？": "确定要提交流程到下一节点吗？",
    "提交成功": "提交成功",
    "保存成功": "保存成功",
    "他人": "他人",
    "确定要回退此流程到吗？": "确定要回退此流程到【{0}】吗？",
    "提交了流程": "提交了流程"
    }
    }
</i18n>

 <template>
    <Modal
        ref="dialog"
        v-model="showDialog"
        :closable="true"
        :mask-closable="false"
        :loading="false"
        width="750"
        class="workflow-dialog"
        :footer-hide="true" >
        <div  class="dialog-wrap " :class="{'blur':loading}">

        <div v-if="permission" class="opt">
                <Button v-if="permission.enableSave" @click="save(false)" class="mr top-opt-btn" icon="ios-cloud-upload-outline" type="default">{{$t('暂存')}}</Button>
                <Button v-if="permission.enableSubmit" @click="save(true)" class="mr top-opt-btn" icon="md-arrow-round-forward" type="default">
                    <template v-if="permission.submitButtonText!=null&&permission.submitButtonText!=''">{{permission.submitButtonText}}</template>
                    <template v-else>{{$t('提交')}}</template>
                </Button>
                <Button v-if="permission.enableTerminal" @click="terminal" class="mr top-opt-btn" icon="md-radio-button-on" type="error">
                    <template v-if="permission.terminalButtonText!=null&&permission.terminalButtonText!=''">{{permission.terminalButtonText}}</template>
                    <template v-else>{{$t('终止')}}</template>
                </Button>

                <Button v-if="permission.enablePrint"  @click="print()" class="mr top-opt-btn" icon="ios-print-outline" type="default">{{$t('打印')}}</Button>
                <div class="opt-gap"></div>

                <Button v-if="permission.enableCancel" @click="cancel" class="ml top-opt-btn" icon="ios-undo" type="error">{{$t('撤回')}}</Button>
                <Button v-if="permission.enableDelete" @click="deleteWorkflow" class="ml top-opt-btn" icon="ios-undo" type="error">{{$t('删除')}}</Button>
                <Poptip v-if="permission.enableBackword&&permission.backwordNodeList&&permission.backwordNodeList.length>0" ref="poptip" transfer class="poptip-full" v-model="backPoptipVisible">
                    <Button class="ml top-opt-btn" icon="md-return-left" type="default">{{$t('回退到')}}</Button>
                    <div slot="content" >
                        <div class="popup-box scrollbox">
                          <div @click="backwardTo(item)" class="popup-item-name"
                                    v-for="item in permission.backwordNodeList" :key="item.id">
                               {{item.name}}
                            </div>
                        </div>
                    </div>
                </Poptip>

                <Button class="ml top-opt-btn" v-if="permission.enableTransferTo" @click="transfer" icon="ios-redo" type="default">{{$t('转交')}}</Button>
        </div>

        <div class="form-dialog-header" >
            <div style="font-size:16px;" class="text-no-wrap">
                <div class="form-dialog-header-color" :style="headerStyle"></div>
                <WorkflowNameLabel :id="instance.serialNo"
                                :name="instance.title"
                                :finishType="instance.finishType"
                                :finishText="instance.finishText"
                                :isDone="instance.isFinished"></WorkflowNameLabel>
               <template v-if="instance.currNodeName"> - {{instance.currNodeName}}</template>
            </div>
        </div>

            <div v-if="form.id" class="dialog-form-content scrollbox">
                <WorkflowForm :id="'form-'+form.id" ref="form" v-if="form.id" :form="form" :value="formFieldValue" :formFieldList="permission.formFieldList"></WorkflowForm>
                <div class="info-bar">
                    <div class="info-bar-row">
                        <div class="info-bar-title">{{$t('提交人：')}}</div><div class="info-bar-value">
                            <AvatarImage size="small" :name="instance.createAccountName"
                                :value="instance.createAccountImageId" type="label"></AvatarImage>
                        </div>
                    </div>
                    <div class="info-bar-row">
                        <div class="info-bar-title">{{$t('提交时间：')}}</div><div class="info-bar-value">{{instance.createTime|fmtDateTime}}</div>
                    </div>
                    <div class="info-bar-row" v-if="instance.finishTime">
                        <div class="info-bar-title">{{$t('完成时间：')}}</div><div class="info-bar-value">{{instance.finishTime|fmtDateTime}}</div>
                    </div>
                    <template v-if="instance.isFinished==false">
                    <div class="info-bar-row">
                        <div class="info-bar-title">{{$t('当前步骤：')}}</div><div class="info-bar-value">{{instance.currNodeName}}</div>
                    </div>
                    <div v-if="permission.forwardRule&&ownerList.length>1" class="info-bar-row">
                        <div class="info-bar-title">{{$t('流转方式：')}}</div><div class="info-bar-value">
                           <template v-if="permission.forwardRule=='all'">{{$t('所有负责人提交后流转到下一步骤')}}</template>
                           <template v-if="permission.forwardRule=='any'">{{$t('任意一个负责人提交后流转到下一步骤')}}</template>
                        </div>
                    </div>
                    <div  class="info-bar-row" v-if="ccList.length>0">
                        <div class="info-bar-title">{{$t('抄送：')}}</div>
                        <div class="info-bar-value">
                             <AvatarImage  v-for="acc in ccList" :key="acc.ownerAccountId"
                                size="small" :name="acc.ownerAccountName"
                                :value="acc.ownerAccountImageId" type="tips"></AvatarImage>
                        </div>
                    </div>
                    <div  class="info-bar-row" v-if="ownerList&&ownerList.length>0">
                        <div class="info-bar-title">{{$t('负责人：')}}</div>
                        <div class="info-bar-value">
                            <div style="width:100%">
                                <template v-for="item in ownerList">
                                <div class="owner-row" :key="'a'+item.id">
                                    <div class="owner-row-name">
                                        <AvatarImage  size="small" :name="item.ownerAccountName"
                                            :value="item.ownerAccountImageId" type="label"></AvatarImage>
                                    </div>
                                    <div class="owner-row-detail">
                                        <div>
                                            <span v-if="item.status==1" class="owner-row-status owner-row-status-1">{{$t('未处理')}}</span>
                                            <span v-if="item.status==2" class="owner-row-status owner-row-status-2">
                                               <template v-if="item.submitButtonText">{{item.submitButtonText}}</template>
                                               <template v-else>{{$t('已提交')}}</template>
                                            </span>
                                            <span v-if="item.status==3" class="owner-row-status owner-row-status-3">
                                                {{$t('他人')}}<template v-if="item.submitButtonText">{{item.submitButtonText}}</template>
                                                <template v-else>{{$t('已提交')}}</template>
                                            </span>

                                            <span class="owner-row-detail-time">{{item.enterTime|fmtDateTime}}</span>
                                            <Icon type="md-arrow-round-forward" />
                                            <span class="owner-row-detail-time">{{item.leaveTime|fmtDateTime}}</span>
                                        </div>
                                    </div>
                                </div>
                                <div v-if="item.remark" class="owner-log-detail-remark" :key="'r'+item.id">
                                    {{$t('备注：')}}{{item.remark}}
                                </div>
                                <div class="owner-log-detail-box" :key="'b'+item.id" >
                                    <WorkflowFormValue v-if="item.formData"
                                    :formData="item.formData"
                                    :formFieldList="permission.formFieldList"></WorkflowFormValue>
                                 </div>

                                </template>
                            </div>
                        </div>
                    </div>
                    </template>

                </div>
                <div class="section-header">
                    <div class="section-tab" :class="{'section-tab-active':selectedTab=='changelog'}"  @click="selectedTab='changelog'">{{$t('变更记录')}}</div>
                    <div class="section-tab" :class="{'section-tab-active':selectedTab=='time'}"  @click="selectedTab='time'">{{$t('停留时间')}}</div>
                    <div class="section-tab" :class="{'section-tab-active':selectedTab=='graph'}"  @click="selectedTab='graph'">{{$t('流程图')}}</div>
                </div>

                <div class="changelog-container" v-if="selectedTab=='changelog'">
                     <Timeline>
                        <TimelineItem v-for="item in changeList" :key="item.id">

                            <Icon v-if="item.type==1" type="md-paper-plane" slot="dot"></Icon>
                            <Icon v-if="item.type==2" type="md-radio-button-on" slot="dot"></Icon>
                            <Icon v-if="item.type==3" type="ios-undo" slot="dot"></Icon>
                            <Icon v-if="item.type==4" type="ios-text" slot="dot"></Icon>
                            <Icon v-if="item.type==5" type="md-arrow-round-forward" slot="dot"></Icon>
                            <Icon v-if="item.type==6" type="ios-redo" slot="dot"></Icon>
                            <Icon v-if="item.type==7" type="md-arrow-round-backward" slot="dot"></Icon>
                            <Icon v-if="item.type==8" type="md-paper-plane" slot="dot"></Icon>
                            <Icon v-if="item.type==9" type="md-arrow-round-up" slot="dot"></Icon>

                            <p class="time">
                                <AvatarImage size="small" :name="item.ownerAccountName"
                                    :value="item.ownerAccountImageId" type="tips"></AvatarImage>
                                {{item.createTime|fmtDateTime}}
                            </p>
                            <p class="content">
                                <div class="change-log-content-desc">
                                    <span style="margin-right:3px">{{item.ownerAccountName}}</span>
                                    <span style="margin-right:3px">
                                        <template v-if="item.type==1">{{$t('创建了流程')}}</template>
                                        <template v-if="item.type==2">{{$t('终止了流程')}}</template>
                                        <template v-if="item.type==3">{{$t('撤回了流程')}}</template>
                                        <template v-if="item.type==4">{{$t('发表了评论')}}</template>
                                        <template v-if="item.type==5">{{$t('发起流程流转')}}</template>
                                        <template v-if="item.type==6">{{$t('转交责任人')}}</template>
                                        <template v-if="item.type==7">{{$t('回退了流程')}}</template>
                                        <template v-if="item.type==8">{{$t('流程结束')}}</template>
                                        <template v-if="item.type==9">{{$t('提交了流程')}}</template>

                                    </span>

                                    <template v-if="item.type==5">
                                        <span v-if="item.beforeNodeName" class="node-name">{{item.beforeNodeName}}</span>
                                        <Icon type="md-arrow-round-forward" />
                                        <span class="node-name">{{item.nodeName}}</span>
                                    </template>
                                    <template v-if="item.type!=5">
                                        <span class="node-name">{{item.nodeName}}</span>
                                    </template>

                                </div>
                                <div class="change-log-detail-box">

                                    <div v-if="item.type==4" class="change-log-comment-box">
                                        <RichtextLabel  :value="item.detail"></RichtextLabel>
                                        <Icon v-if="isCommentOwner(item)" class="delete-comment-btn"
                                            type="md-trash" size="16" @click.native="deleteComment(item)"></Icon>
                                    </div>
                                    <div v-if="item.type==9">
                                        <div v-if="item.submitButtonText" class="change-log-detail-opt">
                                            <div class="change-log-detail-opt-name">{{$t('结果')}}</div>
                                            <div class="change-log-detail-opt-value">
                                                <span class="change-log-after">{{item.submitButtonText}}</span></div>
                                        </div>
                                         <div v-if="item.remark" class="change-log-detail-opt">
                                            <div class="change-log-detail-opt-name">{{$t('备注')}}</div>
                                            <div class="change-log-detail-opt-value">
                                                 <span class="change-log-after">{{item.remark}}</span></div>
                                        </div>
                                        <WorkflowFormValue v-if="item.detail"
                                            :formData="item.detail"
                                            :formFieldList="permission.formFieldList"></WorkflowFormValue>
                                    </div>
                                    <div v-if="item.type==3">
                                        <div v-if="item.remark" class="change-log-detail-opt">
                                            <div class="change-log-detail-opt-name">{{$t('备注')}}</div>
                                            <div class="change-log-detail-opt-value">
                                                 <span class="change-log-after">{{item.remark}}</span></div>
                                        </div>
                                    </div>
                                    <div v-if="item.type==2">
                                        <div v-if="item.terminalButtonText" class="change-log-detail-opt">
                                            <div class="change-log-detail-opt-name">{{$t('结果')}}</div>
                                            <div class="change-log-detail-opt-value">
                                                 <span class="change-log-before">{{item.terminalButtonText}}</span></div>
                                        </div>
                                         <div v-if="item.remark" class="change-log-detail-opt">
                                            <div class="change-log-detail-opt-name">{{$t('备注')}}</div>
                                            <div class="change-log-detail-opt-value">
                                                 <span class="change-log-before">{{item.remark}}</span></div>
                                        </div>
                                    </div>
                                    <div v-if="(item.type==5||item.type==6)&&item.detail">
                                        <div v-if="item.detail.beforeOwnerList" class="change-log-detail-opt">
                                            <div class="change-log-detail-opt-name">{{$t('原责任人')}}</div>
                                            <div class="change-log-detail-opt-value">
                                                <WorkflowOwnerView style="font-size:12px !important" type="tips" :value="item.detail.beforeOwnerList"/>
                                            </div>
                                        </div>

                                         <div v-if="item.detail.afterOwnerList" class="change-log-detail-opt">
                                            <div class="change-log-detail-opt-name">{{$t('新责任人')}}</div>
                                            <div class="change-log-detail-opt-value">
                                                <WorkflowOwnerView style="font-size:12px !important" type="tips" :value="item.detail.afterOwnerList"/>
                                            </div>
                                        </div>

                                    </div>
                                </div>
                            </p>
                        </TimelineItem>
                    </Timeline>
                </div>

                <div v-if="selectedTab=='graph'" class="graph-container" >
                    <WorkflowGraph :graph="permission" :allNodeLogList="allNodeLogList"/>
                </div>
                <div v-if="selectedTab=='time'" class="changelog-container">
                    <table class="table-content table-grid table-content-small"
                        style="border-top:1px solid #eee;table-layout:fixed">
                        <thead>
                            <tr class="table-row">
                                <th style="width:140px">{{$t('节点')}}</th>
                                <th style="width:80px">{{$t('状态')}}</th>
                                <th style="width:140px">{{$t('停留时间')}}</th>
                                <th>{{$t('详细记录')}}</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="item in allNodeLogList" :key="'st'+item.nodeId" class="table-row">
                                <td class="text-no-wrap">{{item.nodeName}}</td>
                                <td>
                                    <span v-if="item.finish==false" class="owner-row-status owner-row-status-1">{{$t('未处理')}}</span>
                                    <span v-if="item.finish" class="owner-row-status owner-row-status-2">{{$t('已处理')}}</span>
                                </td>
                                <td>{{item.leftTime|fmtSeconds}}</td>
                                <td>
                                    <div v-for="timelog in item.list" :key="'logid'+timelog.id">
                                        {{timelog.enterTime|fmtDateTime}} <Icon type="md-arrow-round-forward" /> {{timelog.leaveTime|fmtDateTime}}
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <div v-if="form.id&&instance.isFinished==false" class="comment-box">
                <div class="comment-box-editor" v-show="showCommentBox">
                    <RichtextEditor  ref="commentEditor"   v-model="commentContent"></RichtextEditor>
                    <div style="text-align:right;margin-top:5px">
                       <span class="cancel-btn" @click="showCommentBox=false" type="text">{{$t('取消')}}</span>
                       <Button @click="confirmComment()" type="default">{{$t('确定')}}</Button>
                    </div>
                </div>
                <div class="comment-box-tips" v-show="showCommentBox==false">
                    <div @click="showCommentBox=true" class="comment-box-tips-inner">{{$t('发表评论')}}</div>
                </div>
            </div>
        </div>
    </Modal>
</template>


<script>
export default {
    mixins: [componentMixin],
    data() {
        return {
            form:{},
            define:null,
            chart:null,
            instance:{},
            formFieldValue:{},
            graphItemProp:{},
            permission:{},
            selectedTab:'changelog',
            commentContent:null,
            showCommentBox:false,
            backwordNodeList:[],
            nodeChangeList:[],
            ccList:[],
            backPoptipVisible:false,
            headerStyle:{
                backgroundColor:null
            },
            allNodeLogList:[],
            changeList:[],
            loading:false
        };
    },
    methods: {
        pageLoad() {
            this.loadData();
        },
        loadData() {
            var admin=this.args.admin;
            if(admin==null){
                admin=false;
            }
            this.loading=true;
            app.invoke('WorkflowAction.getWorkflowInstanceInfo',[app.token,this.args.id,admin],(info)=>{
                this.form=info.form;
                this.define=info.define;
                this.permission=info.permission;
                this.instance=info.instance;
                this.nodeChangeList=info.nodeChangeList;
                this.ownerList=info.ownerList;
                this.ccList=info.ccList;
                this.computeNodeLogList();
                //
                this.headerStyle={
                    backgroundColor:info.define.color
                }
                //
                this.setupValue();
                this.loadChangeLogList();
                this.loading=false;
            })
        },
        setupValue(){
            var formFieldValue={};
            if(this.instance.formData){
                formFieldValue=JSON.parse(this.instance.formData);
            }
            //设置permission.formFieldList的字段类型 columnList
            var formFieldList=JSON.parse(this.form.fieldList);
            var allFormField={};
            formFieldList.forEach(f=>{
                allFormField[f.id]=f;
            })
            this.permission.formFieldList.forEach(f=>{
                var t=allFormField[f.id];
                if(t){
                    f.type=t.type;
                    f.columnList=t.columnList;
                    f.optionList=t.optionList;
                    f.systemValueType=t.systemValueType;
                    f.minValue=t.minValue;
                    f.maxValue=t.maxValue;
                    f.maxLength=t.maxLength;
                    f.minLength=t.minLength;
                }
            })
            //设置系统字段和其它字段的默认值
            this.permission.formFieldList.forEach(f=>{
                if(f.type=='date'){
                    var dateValue=formFieldValue[f.id];
                    if(dateValue!=null){
                        var time=new Date(dateValue);
                        formFieldValue[f.id]=time;
                    }
                }
                if(f.type=='system-value'){
                    if( formFieldValue[f.id]==null){
                        if(f.systemValueType=='submitAccount'){
                            formFieldValue[f.id]=this.instance.createAccountName;
                        }
                        if(f.systemValueType=='systemTime'){
                            formFieldValue[f.id]=window.formatDate(new Date());
                        }
                        if(f.systemValueType=='serialNO'){
                            formFieldValue[f.id]=this.instance.serialNo;
                        }
                    }
                }
                if(f.type=='text-number'){
                    var oldValue=formFieldValue[f.id]
                    if(oldValue==null){
                        if(f.minValue){
                            formFieldValue[f.id]=f.minValue;
                        }
                        if(formFieldValue[f.id]==null){
                            formFieldValue[f.id]=1;
                        }
                    }

                }
                if(f.type=='select'||f.type=='radio'){//从optionList选择默认选中的值
                    if(formFieldValue[f.id]==null){
                        var selectedOption=null;
                        f.optionList.forEach(option=>{
                            if(option.checked){
                                selectedOption=option.value;
                            }
                        })
                        if(selectedOption){
                            formFieldValue[f.id]=selectedOption;
                        }
                    }
                }
                if(f.type=='checkbox'){
                    if(formFieldValue[f.id]==null){
                        var selectedOptionList=[];
                            f.optionList.forEach(option=>{
                                if(option.checked){
                                    selectedOptionList.push(option.value);
                                }
                            })
                        if(selectedOptionList.length>0){
                            formFieldValue[f.id]=selectedOptionList;
                        }
                    }
                }
            })
            //
            this.formFieldValue=formFieldValue;
        },
        loadChangeLogList(){
            var query={
                pageIndex:1,
                pageSize:200,
                workflowInstanceId:this.instance.id
            }
            app.invoke('WorkflowAction.getWorkflowInstanceChangeLogList',[app.token,query],(info)=>{
                info.list.forEach(item=>{
                   if(item.type==5||item.type==6){
                        try{
                           item.detail=JSON.parse(item.detail)
                        }catch(e){console.log(e)}
                   }
                })
                this.changeList=info.list;
            });
        },
        print(){
            var iHeight=600;
            var iWidth=850;
            var iLeft = (window.screen.availWidth - 10 - iWidth) / 2;
            var w = window.open('', 'Print', 'height='+iHeight+',width='+iWidth+",top=0,left="+iLeft);
            var pd=w.document;
            pd.write('<html><head><title>'+this.$refs.form.encode(this.form.title)+'-#'+this.instance.serialNo+'</title>');
            pd.write('<meta charset="utf-8">');
            pd.write('<link rel="stylesheet" type="text/css" media="print" href="/css/workflow-print.css">');
            pd.write('<link rel="stylesheet" type="text/css" href="/css/workflow-print.css">');
            pd.write('</head><body >');
            pd.write('<div class="form-no">#'+this.instance.serialNo+'</div>');
            pd.write(this.$refs.form.getPrintContent());
            pd.write('</body></html>');
            pd.close();
            w.focus()
            setTimeout(()=>{
                w.print();
                w.close();
            },1000)
        },
        computeNodeLogList(){
            var list=this.nodeChangeList;
            var statusMap={}
            var allStatusLogList=[];
            list.forEach(element => {
                if(statusMap[element.nodeId]==null){
                    var temp={
                        list:[],
                        nodeName:element.nodeName,
                        nodeId:element.nodeId,
                        finish:true
                    }
                    statusMap[element.nodeId]=temp;
                        allStatusLogList.push(temp)
                }
                statusMap[element.nodeId].list.push(element);
            });
            allStatusLogList.forEach(item=>{
                item.leftTime=0;
                item.list.forEach(t=>{
                    var a=new Date().getTime();
                    if(t.leaveTime){
                        a=t.leaveTime;
                    }else{
                        item.finish=false;
                    }
                    item.leftTime+=(a-t.enterTime);
                })
            })
            this.allNodeLogList=allStatusLogList;
        },
        getNodeName(id){
            for(var i=0;i<this.graph.nodes.length;i++){
                var n=this.graph.nodes[i];
                if(n.id==id){
                    return n.name;
                }
            }
            return id;
        },

        isCommentOwner(item){
            return app.account.id==item.ownerAccountId;
        },
        deleteComment(item){
            app.confirm(this.$t('确定删除此评论吗？'),()=>{
                app.invoke('WorkflowAction.deleteWorkflowInstanceComment',[app.token,item.id],(info)=>{
                    this.loadChangeLogList();
                })
            })
        },
        confirmComment(){
            var content=this.$refs.commentEditor.getValue();
            if(content==null||content==""){
                app.toast(this.$t('请输入评论内容'));
                return;
            }
            app.invoke('WorkflowAction.commentWorkflowInstance',[app.token,this.instance.id,content],(info)=>{
                app.toast(this.$t('评论成功'));
                this.commentContent="";
                this.showCommentBox=false;
                this.loadChangeLogList();
            })
        },
        transfer() {
            app.showDialog(MemberSelectDialog,{
                callback:(list)=>{
                    var userList=[];
                    list.forEach(item=>{
                        userList.push(item.accountId)
                    })
                    app.invoke('WorkflowAction.transferWorkflowInstance',[app.token,this.instance.uuid,userList],(info)=>{
                        app.toast(this.$t('操作成功'));
                        app.postMessage('workflow.edit');
                        this.showDialog=false;
                    })
                }
            })
        },
        terminal(){
            this.workflowConfirm(this.$t('确定要终止此流程吗？'),(remark)=>{
                app.invoke('WorkflowAction.terminalWorkflowInstance',[app.token,this.instance.uuid,remark],(info)=>{
                    app.toast(this.$t('操作成功'));
                    app.postMessage('workflow.edit')
                    this.showDialog=false;
                })
            })
        },
        cancel(){
            this.workflowConfirm(this.$t('确定要撤回此流程吗？'),(remark)=>{
                app.invoke('WorkflowAction.cancelWorkflowInstance',[app.token,this.instance.uuid,remark],(info)=>{
                    app.toast(this.$t('操作成功'));
                    app.postMessage('workflow.edit')
                    this.showDialog=false;
                })
            })
        },
        deleteWorkflow(){
            app.confirm(this.$t('确定要删除此流程吗？删除后数据不能恢复，请谨慎操作！'),()=>{
                app.invoke('WorkflowAction.deleteWorkflowInstance',[app.token,this.instance.uuid],(info)=>{
                    app.toast(this.$t('操作成功'));
                    app.postMessage('workflow.edit')
                    this.showDialog=false;
                })
            })
        },
        workflowConfirm(msg,callback){
            app.showDialog(WorkflowConfirmDialog,{
                confirm:callback,
                message:msg
            })
        },
        save(submit){
            if(submit){
                 this.$refs.form.validateForm(()=>{
                    this.workflowConfirm(this.$t('确定要提交流程到下一节点吗？'),(remark)=>{
                        this.confirmSave(submit,remark);
                    })
                 })
            }else{
                this.confirmSave(submit);
            }
        },
        confirmSave(submit,remark){
            var submitValue={};
            this.permission.formFieldList.forEach(item=>{
                if(item.editable){
                    submitValue[item.id]=this.formFieldValue[item.id];
                }
            })
            var formData=JSON.stringify(submitValue);
            var info={
                workflowInstanceId:this.instance.id,
                submit:submit,
                formData:formData,
                remark:remark,
            }
            app.invoke('WorkflowAction.saveWorkflow',[app.token,info],(info)=>{
               if(submit){
                   app.toast(this.$t('提交成功'));
                   this.showDialog=false;
                   app.postMessage('workflow.edit')
               }else{
                   app.toast(this.$t('保存成功'));
               }
            })
        },
        backwardTo(item){
            this.backPoptipVisible=false;
            app.confirm(this.$t('确定要回退此流程到吗？',[item.name]),()=>{
                app.invoke('WorkflowAction.backToWorkflowInstance',[app.token,
                this.instance.uuid,item.id],(info)=>{
                    app.toast(this.$t('操作成功'));
                    app.postMessage('workflow.edit')
                    this.showDialog=false;
                })
            })
        }
    }
};
</script>
