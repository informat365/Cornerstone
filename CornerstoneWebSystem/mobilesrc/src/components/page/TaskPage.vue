<style scoped>
    .page {
        padding: 0;
        background-color: #fff;
        min-height: 100vh;
    }

    .name-input {
        width: 100%;
        border: none;
        outline: 0;
        font-weight: bold;
        font-size: 16px;
        padding: 0;
        padding-top: 3px;
        padding-bottom: 3px;
        resize: none;
        color: #333;
        font-weight: bold;
    }

    .name-input:disabled {
        color: #444;
    }

    .section {
        padding: 15px;
    }

    .section-border {
        border-top: 1px solid #efefef;
        padding: 15px;
    }

    .section-color {
        background-color: #F8F8F9;
    }

    .info-box {
        background-color: #F8F8F9;
        padding: 15px;
    }

    .info-row {
        font-size: 13px;
        display: flex;
        align-items: center;
        margin-top: 15px;
    }

    .info-row:first-child {
        margin-top: 0;
    }

    .info-key {
        width: 100px;
        color: #94929D;
    }

    .info-value {
        flex: 1;
        display: flex;
        align-items: center;
        text-align: right;
        justify-content: flex-end;
    }

    .section-box {
        padding: 15px;
    }

    .section-title {
        font-size: 13px;
        font-weight: 700;
        margin-top: 5px;
        margin-bottom: 5px;
        border-left: 4px solid #222;
        padding-left: 10px;
    }

    .section-content {
        padding: 10px;
        font-size: 14px;
    }

    .richtext-body {
        border: none;
    }

    .richtext-body .simditor-body {
        border: none;
        padding: 15px 0px;
        min-height: 0;
    }

    .reply-box {
        display: flex;
    }

    .reply-image-box {
        width: 30px;
    }

    .reply-content-box {
        flex: 1;
    }

    .reply-content-author {
        font-size: 12px;
        color: #999;
    }

    .reply-btn {
        float: right;
        color: #0097F7;
        vertical-align: middle;
    }

    .vux-x-icon {
        fill: #666;
    }

    .custom-tag {
        margin-left: 4px;
    }

    .task-row-item {
        display: flex;
        align-items: center;
        margin-top: 4px;
        font-size: 13px !important;
    }

    .task-file-item {
        font-size: 13px;
        margin-top: 7px;
        text-decoration: underline;
    }

    .log-item {
        position: relative;
        margin-top: 20px;
    }

    .log-item-left {
        position: absolute;
        display: inline-block;
        width: 20px;
    }

    .log-item-right {
        display: inline-block;
        width: 100%;
        padding-left: 35px;
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
        font-size: 12px;
        line-height: 1.5;
    }

    .log-item-before {
        background-color: #FFD3D3;
        color: #FF3B58;
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

    .log-item-icon {
        font-size: 14px;
        margin-right: 5px;
        vertical-align: text-top;
    }

    .log-item:hover .log-item-opt {
        visibility: visible;
    }

    .change-log-table {
        width: 100%;
        margin-top: 5px;
        font-size: 12px;
    }

    .change-log-table-row {
        display: flex;
    }

    .change-log-table td {
        border: none;
    }

    .scm-reversion-link {
        color: #2C5D95;
        cursor: pointer;
    }

    .change-log-table {
        border-left: 4px solid #ccc;
    }

    .log-item-creater {
        display: inline-block;
        padding: 2px 6px;
        text-align: center;
        background-color: #00D5CC;
        color: #fff;
        border-radius: 12px;
        font-size: 12px;
        margin-left: 0px;
        transform: scale(0.7);
    }

    .log-item-owner {
        background-color: #0091EA;
    }

    .log-item-link {
        color: #0091EA;
        padding-left: 5px;
        padding-right: 5px;
        cursor: pointer;
    }

    .codebox {
        overflow: auto;
        font-size: 12px;
    }

    .v-right /deep/ input{
        text-align: right;
    }
</style>
<template>
    <div class="page" v-show="task.id>0">


        <FilePopup @close="fileUUID=null" v-if="fileUUID" :uuid="fileUUID"></FilePopup>
        <x-header @on-click-back="clickBackBtn" class="task-header"
                  :left-options="{backText: '',showBack: showBack,preventGoBack:true}">
            <span style="font-weight:bold">{{task.objectTypeName}} #{{task.serialNo}}</span>
            <span @click="showMenus = true" slot="right">
                 <x-icon type="ios-arrow-down" size="20"></x-icon>
            </span>
        </x-header>
        <div v-transfer-dom>
            <actionsheet @on-click-menu="actionMenuClick" :menus="menus" v-model="showMenus" show-cancel></actionsheet>
        </div>

        <TaskPopupSelect v-model="showPopup" @change="popupConfirm"
                         :multiple="popupMultiple"
                         :list="popupSelectList"
                         :selected-list="popupSelectValue"></TaskPopupSelect>
        <div v-transfer-dom>
            <popup :popup-style="popupStyle" v-model="showReplyBox">
                <popup-header
                        left-text="取消"
                        right-text="确定"
                        title=""
                        :show-bottom-border="false"
                        @on-click-left="showReplyBox = false"
                        @on-click-right="confirmReply"></popup-header>
                <group gutter="0">
                    <x-textarea show-counter :rows="5" autosize placeholder="评论内容"
                                v-model.trim="replyContent"></x-textarea>
                </group>
            </popup>
        </div>

        <div v-transfer-dom>
            <popup :popup-style="popupStyle" v-model="showTextEditBox">
                <popup-header
                        left-text="取消"
                        right-text="确定"
                        title=""
                        :show-bottom-border="false"
                        @on-click-left="showTextEditBox = false"
                        @on-click-right="confirmTextEdit"></popup-header>
                <group gutter="0">
                    <x-textarea show-counter :rows="5" autosize placeholder="请输入"
                                v-model.trim="textEditContent"></x-textarea>
                </group>
            </popup>
        </div>

        <div v-transfer-dom>
            <popup :popup-style="popupStyle" v-model="showNumberEditBox">
                <popup-header
                        left-text="取消"
                        right-text="确定"
                        title=""
                        :show-bottom-border="false"
                        @on-click-left="showNumberEditBox = false"
                        @on-click-right="confirmNumberEdit"></popup-header>
                <group gutter="0">
                    <x-input class="v-right" placeholder="请输入"
                                v-model.trim="numberEditContent"></x-input>
                </group>
            </popup>
        </div>


        <div v-transfer-dom>
            <popup :popup-style="popupStyle" v-model="showEditBox">
                <popup-header
                        left-text="取消"
                        right-text="确定"
                        title=""
                        :show-bottom-border="false"
                        @on-click-left="showEditBox = false"
                        @on-click-right="confirmEdit"></popup-header>
                <group gutter="0">
                    <RichtextEditor ref="editor" v-model="task.content"></RichtextEditor>
                </group>
            </popup>
        </div>

        <!--      工时弹窗-->
        <div v-transfer-dom>
            <popup :popup-style="popupStyle" v-model="showEditWorktime">
                <popup-header
                        left-text="取消"
                        right-text="确定"
                        title="填写工时"
                        :show-bottom-border="false"
                        @on-click-left="showEditWorktime = false"
                        @on-click-right="confirmWorktime"></popup-header>
                <group gutter="0">
                    <div class="info-box">
                        <div class="info-row">
                            <div class="info-key">工作事项：</div>
                            <div class="info-value">
                            <textarea rows="1" :disabled="propDisable"
                                      @keyup="resizeNameInput" class="name-input"
                                      autosize
                                      v-model.trim="worktime.content"></textarea>
                            </div>
                        </div>
                        <div class="info-row">
                            <div class="info-key">开始时间：</div>
                            <div style="background-color: white;height:27px;" class="info-value"
                                 @click="editWorktimeDate">
                                <span>{{worktime.startTime|fmtDate}}</span>
                            </div>
                        </div>
                        <div class="info-row">
                            <div class="info-key">工时：</div>
                            <div class="info-value">
                                <input style="text-align:right;" class="name-input" width="50px;" type="number"
                                       v-model="worktime.hour"/>
                            </div>
                        </div>
                    </div>
                </group>
            </popup>
        </div>


        <div class="section" style="padding-bottom:5px">
          <textarea rows="1" :disabled="propDisable" @blur="updateTask('name')"
                    ref="nameInput" @keyup="resizeNameInput"
                    class="name-input" autosize v-model.trim="task.name"></textarea>
        </div>
        <div class="section-border">
            <flexbox>
                <flexbox-item :span="8" @click.native="editOwner">
                    <template v-if="task.ownerAccountList">
                        <template v-if="task.ownerAccountList.length==0">
                            <span style="font-size:13px">待认领</span>
                        </template>
                        <template v-if="task.ownerAccountList.length==1">
                            <AvatarImage size="small" :name="task.ownerAccountList[0].name"
                                         :value="task.ownerAccountList[0].imageId" type="label"></AvatarImage>
                        </template>

                        <template v-if="task.ownerAccountList.length>1">
                            <AvatarImage v-for="acc in task.ownerAccountList" :key="task.id+'_acc'+acc.id" size="small"
                                         :name="acc.name" :value="acc.imageId" type="tips"></AvatarImage>
                        </template>
                    </template>
                    <template v-if="task.ownerAccountList==null">
                        <span style="font-size:13px">待认领</span>
                    </template>
                </flexbox-item>
                <flexbox-item :span="4" style="text-align:right">
                    <TaskStatus @click.native="editStatus" :label="task.statusName"
                                :color="task.statusColor"></TaskStatus>

                </flexbox-item>
            </flexbox>
        </div>

        <div class="info-box">
            <div class="info-row">
                <div class="info-key">项目：</div>
                <div class="info-value">{{task.projectName}}</div>
            </div>
            <div class="info-row">
                <div class="info-key">创建人：</div>
                <div class="info-value">
                    <Avatar-Image
                            type="label"
                            size="small"
                            :name="task.createAccountName"
                            :value="task.createAccountImageId"></Avatar-Image>
                </div>
            </div>
            <div class="info-row">
                <div class="info-key">创建时间：</div>
                <div class="info-value">{{task.createTime|fmtDeltaTime}}</div>
            </div>
            <div class="info-row" v-for="field in editTaskInfo.fieldList" :key="field.id"
                 v-if="showInDetail(field)">
                <div class="info-key">{{field.name}}：</div>
                <div class="info-value">
                    <template v-if="field.isSystemField">
                                    <span v-if="field.field=='iterationName'">
                                            {{task.iterationName}}
                                    </span>
                        <TaskPriority @click.native="editPriority" v-if="field.field=='priorityName'"
                                      :label="task.priorityName" :color="task.priorityColor">

                        </TaskPriority>
                        <span @click="editDate(field)" v-if="field.field=='startDate'">
                                        {{task.startDate|fmtDate}}
                                    </span>
                        <span @click="editDate(field)" v-if="field.field=='endDate'">
                                        {{task.endDate|fmtDate}}
                                    </span>
                        <span v-if="field.field=='createTime'">
                                        {{task.createTime|fmtDate}}
                                    </span>
                        <span v-if="field.field=='updateTime'">
                                        {{task.updateTime|fmtDate}}
                                    </span>
                        <span @click="editDate(field)" v-if="field.field=='expectEndDate'">
                                        {{task.expectEndDate|fmtDate}}
                                    </span>
<!--                        <span v-if="field.field=='expectEndDate'">-->
<!--                                        {{task.expectEndDate|fmtDate}}-->
<!--                                    </span>-->

                        <span v-if="field.field=='startDays'">
                                        {{task.startDays}}天
                                    </span>
                        <span v-if="field.field=='endDays'">
                                        {{task.endDays}}天
                                      </span>

                        <span v-if="field.field=='expectWorkTime'" @click="editNumber(field)">
                                                 {{task.expectWorkTime}}h
                                            </span>
<!--                        <span v-if="field.field=='expectWorkTime'">-->
<!--                                        {{task.expectWorkTime}}h-->
<!--                                      </span>-->

                        <span v-if="field.field=='workTime'">
                                            <template v-if="propDisable">{{task.workTime}}h </template>
                                            <template v-else><span
                                                    @click="showEditWorktime=true">{{task.workTime}}h</span> </template>
                                        </span>

                        <!-- <span  v-if="field.field=='workTime'" @click="showEditWorktime=true">
                           {{task.workTime}}h
                         </span>
                         <span  v-if="field.field=='workTime'" @click="showEditWorktime=true">
                           {{task.workTime}}h
                         </span>-->
                        <span @click="editStage" v-if="field.field=='stageName'">
                                        <template v-if="task.stageName">{{task.stageName}}</template>
                                         <template v-if="task.stageName==null">未设置</template>
                                      </span>
                        <span @click="editRelease" v-if="field.field=='releaseName'">
                                        <template v-if="task.releaseName">{{task.releaseName}}</template>
                                         <template v-if="task.releaseName==null">未设置</template>
                                      </span>
                        <span @click="editSubSystem" v-if="field.field=='subSystemName'">
                                        <template v-if="task.subSystemName">{{task.subSystemName}}</template>
                                        <template v-if="task.subSystemName==null">未设置</template>
                                      </span>
                        <span v-if="field.field=='categoryIdList'">
                                        <TaskCategory class="task-category" :label="getCategoryNode(cate).name"
                                                      :color="getCategoryNode(cate).color"
                                                      v-if="getCategoryNode(cate)"
                                                      v-for="cate in task.categoryIdList" :key="'cat_'+cate">
                                        </TaskCategory>     
                                    </span>
                    </template>

                    <span v-if="field.isSystemField==false">
                                        <template v-if="task&&task.customFields">
                                             <span @click="editText(field)" v-if="field.type==1">
                                                 <template
                                                         v-if="task.customFields['field_'+field.id]==null">未设置</template>
                                                 {{task.customFields['field_'+field.id]}}
                                            </span>
                                             <span @click="editNumber(field)" v-if="field.type==8">
                                                 {{task.customFields['field_'+field.id]}}
                                                  <template
                                                          v-if="task.customFields['field_'+field.id]==null">未设置</template>
                                            </span>

                                             <span @click="editCustomMember(field)"
                                                   v-if="field.type==6">
                                                   <template v-if="!task.customFields['field_'+field.id]||task.customFields['field_'+field.id].length===0">
                                                    <span>未设置</span>
                                                 </template>
                                                <template v-if="task.customFields['field_'+field.id]&&task.customFields['field_'+field.id].length===1">
                                                    <AvatarImage  size="small" :name="getMember(task.customFields['field_'+field.id][0]).accountName" :value="getMember(task.customFields['field_'+field.id][0]).accountImageId" type="label"/>
                                                </template>
                                                 <template v-if="task.customFields['field_'+field.id]&&task.customFields['field_'+field.id].length>1">
                                                     <AvatarImage  v-for="acc in task.customFields['field_'+field.id]" :key="'_acc'+acc" size="small" :name="getMember(acc).accountName" :value="getMember(acc).accountImageId" type="tips"/>
                                                </template>
                                            </span> 
              
                                            <span v-if="field.type==7" @click="editDate(field)">{{task.customFields['field_'+field.id]|fmtDate}}</span>
                                            <span v-if="field.type==3" @click="editCustomSelect(field)">
                                                <template
                                                        v-if="task.customFields['field_'+field.id]==null">未设置</template>
                                                 <span class="custom-tag"
                                                       v-for="t in task.customFields['field_'+field.id]">{{t}}</span>
                                            </span>
                                             <span v-if="field.type==4" @click="editCustomSelect(field)">
                                                 <template
                                                         v-if="task.customFields['field_'+field.id]==null">未设置</template>
                                                 {{task.customFields['field_'+field.id]}}</span>
                                        </template>
                                      </span>


                </div>
            </div>
        </div>


        <div class="section-box" v-if="task.attachmentList&&task.attachmentList.length>0">
            <div class="section-title">附件</div>
            <div class="section-content" style="padding:0;padding-top:5px;">
                <div @click="showAttachment(item)" class="task-file-item" v-for="item in task.attachmentList"
                     :key="'att_'+item.id">
                    <div>{{item.name}}</div>
                </div>
            </div>
        </div>


        <div class="section-box">
            <div class="section-title">详细内容
                <span v-if="propDisable==false" @click="showEditBox=true" class="reply-btn">编辑</span>
            </div>
            <div class="section-content" style="padding:0">
                <div v-if="task.content&&task.content!=''" class="simditor richtext-body">
                    <div class="simditor-body richtext-box-body" v-html="task.content"></div>
                </div>
                <div style="margin-top:15px;color:#666" v-if="task.content==null||task.content==''">暂无内容</div>
            </div>
        </div>


        <div class="section-box"
             v-if="task.subTaskList&&task.associatedList&&(task.subTaskList.length+task.associatedList.length)>0">
            <div class="section-title">关联内容

            </div>
            <div class="section-content" style="padding:0;padding-top:5px;">
                <div class="task-row-item" v-for="item in task.subTaskList" :key="'sub_'+item.id">
                    <TaskObjectType :name="'子'+item.objectTypeName"></TaskObjectType>
                    <div class="serial-no">#{{item.serialNo}}</div>
                    <div style="margin-left:5px;">{{item.name}}</div>
                </div>

                <div class="task-row-item" v-for="item in task.associatedList" :key="'ass_'+item.id">
                    <TaskObjectType :name="item.objectTypeName"></TaskObjectType>
                    <div class="serial-no">#{{item.serialNo}}</div>
                    <div style="margin-left:5px;">{{item.name}}</div>
                </div>

            </div>
        </div>


        <div class="section-box">
            <div class="section-title">变更记录</div>
            <div class="section-content" style="padding:0;margin-top:15px">


                <div class="log-item" v-for="item in changeLogList" :key="'log_'+item.id">
                    <div class="log-item-left">
                        <AvatarImage v-if="item.createAccountId>0" :name="item.createAccountName"
                                     :value="item.createAccountImageId" type="tips"/>

                    </div>
                    <div class="log-item-right">
                        <div>
                            <span class="log-item-name" v-if="item.type==20">
                                {{item.items.author}}
                            </span>
                            <span class="log-item-name">{{item.createAccountName}}</span>
                            <span class="log-item-time">{{item.createTime|fmtDeltaTime}}</span>
                        </div>
                        <div class="log-item-content" style="color:#999;font-size:12px">
                            <div v-if="item.type==1">创建了{{task.objectTypeName}}</div>
                            <div v-if="item.type==2">编辑属性</div>
                            <div v-if="item.type==3">上传附件
                                <span class="log-item-link">{{item.items.name}}</span>
                            </div>
                            <div v-if="item.type==4">删除附件
                                <span>{{item.items.name}}</span>
                            </div>
                            <div v-if="item.type==5">新增关联对象
                                <span class="log-item-link">{{item.items.name}}</span>
                            </div>
                            <div v-if="item.type==6">取消关联对象
                                <span class="log-item-link">{{item.items.name}}</span>
                            </div>
                            <div v-if="item.type==7">新增子任务
                                <span class="log-item-link">{{item.items.name}}</span>
                            </div>
                            <div v-if="item.type==8">删除子任务
                                <span>{{item.items.name}}</span>
                            </div>
                            <div v-if="item.type==9">修改了详细描述
                            </div>
                            <div v-if="item.type==20">
                                提交了代码 {{item.items.version}}
                            </div>
                        </div>
                        <div v-if="item.type==20" class="codebox">
                            <pre>{{item.items.comment}}</pre>
                            <pre>{{item.items.changed}}</pre>
                        </div>
                        <div v-if="item.type==2" class="change-log-table">
                            <div class="change-log-table-row" v-for="(t,idx) in item.items" :key="item.id+'_log_'+idx">
                                <div class="text-no-wrap" style="width:60px;padding-left:5px">{{t.name}}</div>
                                <div style="flex:1">
                                    <span class="log-item-before">
                                       <template v-if="t.beforeContent"> {{t.beforeContent}}</template>
                                       <template v-if="t.beforeContent==null||t.beforeContent==''"> 未设置</template>
                                    </span>
                                    <span style="margin-left:3px">--</span>
                                    <span class="log-item-after">
                                        <template v-if="t.afterContent"> {{t.afterContent}}</template>
                                        <template v-if="t.afterContent==null||t.afterContent==''"> 未设置</template>
                                    </span>

                                    <span class="log-item-time">{{t.createTime|fmtDeltaTime}}</span>
                                </div>

                            </div>
                        </div>


                    </div>

                </div>


            </div>
        </div>

        <div class="section-box">
            <div class="section-title">评论
                <span @click="showReplyBox=true" class="reply-btn">发表评论</span>
            </div>
            <div class="section-content" style="padding:0;margin-top:15px">
                <div style="color:#666" v-if="comments.length==0">暂无评论</div>
                <div class="reply-box" :key="item.id" v-for="item in comments">
                    <div class="reply-image-box">
                        <AvatarImage
                                size="small"
                                type="tips"
                                :name="item.createAccountName"
                                :value="item.createAccountImageId"></AvatarImage>
                    </div>
                    <div class="reply-content-box">
                        <div>
                                    <span class="reply-content-author">
                                            {{item.createAccountName}} 
                                            {{item.createTime|fmtDeltaTime}}
                                    </span>

                        </div>
                        <div class="simditor reply-content-info richtext-body">
                            <div v-html="item.comment" class="simditor-body richtext-box-body"></div>
                        </div>
                    </div>
                </div>


            </div>
        </div>
    </div>
</template>

<script>
    import Vue from 'vue'
    import {
        Flexbox,
        TransferDom,
        DatetimePlugin,
        PopupHeader,
        XTextarea,
        FlexboxItem,
        XHeader,
        Actionsheet,
        Group,
        Popup,
        ToastPlugin,
        XInput
    } from 'vux'

    Vue.use(DatetimePlugin)
    Vue.use(ToastPlugin, {position: 'top'})
    export default {
        components: {Flexbox, XTextarea, PopupHeader, FlexboxItem, XHeader, Actionsheet, Group, Popup, XInput},
        directives: {TransferDom},
        mixins: [componentMixin],
        data() {
            return {
                popupStyle: {
                    zIndex: 600
                },
                showMenus: false,
                showBack: false,
                menus: {},
                task: {},
                comments: [],
                editTaskInfo: {},
                popupSelectList: [],
                popupSelectValue: [],
                popupMultiple: false,
                showPopup: false,
                showReplyBox: false,
                showEditBox: false,
                showTextEditBox: false,
                showNumberEditBox: false,
                currentEditProp: null,
                replyContent: null,
                textEditContent: null,
                numberEditContent: null,
                permissionMap: {},
                fileUUID: null,
                changeLogQuery: {
                    pageIndex: 1,
                    pageSize: 50,
                },
                changeLogList: [],
                changeLogCount: 0,
                showEditWorktime: false,
                worktime: {
                    content: '',
                    startTime: null,
                    hour: 0
                }
            }
        },
        computed: {
            propDisable() {
                if (this.task.isFinish || this.task.isFreeze || this.task.isDelete) {
                    return true;
                }
                if (!this.tperm('task_edit')) {
                    return true;
                }
                return false;
            },
            statusDisable() {
                if (!this.tperm('task_change_status')) {
                    return true;
                }
                return false;
            },
            memberDisable() {
                if (this.task.isFinish || this.task.isFreeze || this.task.isDelete) {
                    return true;
                }
                if (!this.tperm('task_change_owner')) {
                    return true;
                }
                return false;
            }
        },
        methods: {
            pageLoad() {
                if (this.args.showBack) {
                    this.showBack = this.args.showBack;
                }
                this.loadData();
            },
            clickBackBtn() {
                this.$emit('close')
            },
            loadData() {
                app.invoke("BizAction.getTaskInfoByUuid", [app.token, this.args.id], (info) => {
                    this.permissionMap = {};
                    for (var i = 0; i < info.permissionList.length; i++) {
                        this.permissionMap[info.permissionList[i]] = true;
                    }
                    this.editTaskInfo = info.editTaskInfo;
                    if (info.task.customFields == null) {
                        info.task.customFields = {};
                    }
                    this.task = info.task;
                    this.copyTaskToOldTask();
                    this.loadComment();
                    this.loadChangeLog();
                    this.$nextTick(() => {
                        var el = this.$refs.nameInput;
                        el.style.height = 'auto';
                        el.style.height = el.scrollHeight + 'px';
                    })
                    //
                    if (this.tperm('task_delete')) {
                        this.menus = {
                            deleteObject: "删除",
                            taskList: "前往" + this.task.objectTypeName + "列表",
                            home: "回到主页"
                        }
                    } else {
                        this.menus = {
                            taskList: "前往" + this.task.objectTypeName + "列表",
                            home: "回到主页"
                        }
                    }
                })
                //
            },
            tperm(list) {
                if (this.task.isFreeze || this.task.isFinish || this.task.isDelete) {
                    return false;
                }
                //1-超级boss（读写） 2-超级boss（只读）
                if (!!app.account.superBoss) {
                    if (app.account.superBoss == 1 || app.account.superBoss == 3) {
                        return true;
                    } else if (app.account.superBoss == 2 || app.account.superBoss == 4) {
                        if (Array.isArray(list)) {
                            var readPermissionCount = list.filter(item => item.indexOf("_list") > -1 || item.indexOf("_view") > 0 || item.indexOf("_log") > 0).length;
                            if (readPermissionCount > 0) {
                                return true;
                            }
                        } else {
                            if (list.indexOf("_list") > 0 || list.indexOf("_view") > 0 || list.indexOf("_log") > 0) {
                                return true;
                            }
                        }
                    }
                }
                if (Array.isArray(list)) {
                    const index = list.findIndex(t => {
                        return this.permissionMap[t + '_' + this.task.objectType] === true;
                    });
                    return index > -1;
                }
                return this.permissionMap[list + '_' + this.task.objectType] === true;
                /*if(typeof list === 'object' && !isNaN(list.length)){
                    for(var i=0;i<list.length;i++){
                        var t=list[i];
                        if(this.permissionMap[t+"_"+this.task.objectType]!=null){
                            return true;
                        }
                    }
                    return false;
                }else{
                    return this.permissionMap[list+"_"+this.task.objectType]!=null
                }*/
            },
            copyTaskToOldTask() {
                this.oldTask = Object.assign({}, this.task);
                this.oldTask.customFields = Object.assign({}, this.task.customFields);
            },
            loadComment() {
                var commentQuery = {
                    pageIndex: 1,
                    pageSize: 50,
                    taskId: this.task.id
                }
                app.invoke("BizAction.getTaskCommentInfoList", [app.token, commentQuery], (list) => {
                    this.comments = list;
                });
            },
            loadChangeLog() {
                this.changeLogQuery.taskId = this.task.id;
                app.invoke("BizAction.getChangeLogList", [app.token, this.changeLogQuery], list => {
                    list.map(item => {
                        if (item.items != '' && item.items != null) {
                            item.items = JSON.parse(item.items);
                        } else {
                            item.items = {}
                        }
                    })
                    if (this.changeLogQuery.pageIndex != 1) {
                        list.map(item => {
                            this.changeLogList.push(item)
                        })
                    } else {
                        this.changeLogList = list
                    }
                    this.changeLogCount = list.length;
                })
            },
            resizeNameInput(e) {
                var el = e.target;
                el.style.height = 'auto';
                el.style.height = el.scrollHeight + 'px';
            },
            getCategoryNode(id) {
                for (var i = 0; i < this.editTaskInfo.categoryNodeList.length; i++) {
                    var t = this.editTaskInfo.categoryNodeList[i];
                    if (t.id == id) {
                        return t;
                    }
                }
                return null;
            },
            showInDetail(field) {
                if (field.isShow == false) {
                    return false;
                }
                if (field.field == 'name'
                    || field.field == 'statusName'
                    || field.field == 'createAccountName'
                    || field.field == 'ownerAccountName') {
                    return false;
                }
                return true;
            },
            editText(field) {
                if (this.propDisable) {
                    return;
                }
                var value = null;
                if (field.isSystemField) {
                    this.currentEditProp = field.field;
                    value = this.task[this.currentEditProp];
                } else {
                    this.currentEditProp = 'field_' + field.id;
                    value = this.task.customFields[this.currentEditProp];
                }
                this.currentCustomField = field;
                this.textEditContent = value;
                this.showTextEditBox = true;
            },
            editNumber(field) {
                if (this.propDisable) {
                    return;
                }
                var value = null;
                if (field.isSystemField) {
                    this.currentEditProp = field.field;
                    value = this.task[this.currentEditProp];
                } else {
                    this.currentEditProp = 'field_' + field.id;
                    value = this.task.customFields[this.currentEditProp];
                }
                this.currentCustomField = field;
                this.numberEditContent = value;
                this.showNumberEditBox = true;
            },
            editDate(field) {
                if (this.propDisable) {
                    return;
                }
                var value = null;
                if (field.isSystemField) {
                    this.currentEditProp = field.field;
                    value = this.task[this.currentEditProp];
                } else {
                    this.currentEditProp = 'field_' + field.id;
                    value = this.task.customFields[this.currentEditProp];
                }
                this.currentCustomField = field
                var that = this;
                this.$vux.datetime.show({
                    cancelText: "取消",
                    confirmText: "确定",
                    onConfirm(value) {
                        if (that.currentCustomField.isSystemField) {
                            that.task[that.currentEditProp] = value;
                        } else {
                            that.task.customFields[that.currentEditProp] = value;
                        }
                        that.updateTask(that.currentEditProp);
                    },
                })
            },
            editWorktimeDate() {
                var that = this;
                this.$vux.datetime.show({
                    cancelText: "取消",
                    confirmText: "确定",
                    onConfirm(value) {
                        that.worktime.startTime = value;
                    },
                })
            },
            editStatus() {
                if (this.statusDisable) {
                    return;
                }
                this.currentEditProp = "status"
                this.showPopup = true;
                this.popupSelectList = [];
                this.popupMultiple = false;
                for (var i = 0; i < this.editTaskInfo.statusList.length; i++) {
                    var t = this.editTaskInfo.statusList[i]
                    this.popupSelectList.push({
                        key: t.id,
                        value: t.name
                    })
                }
                this.popupSelectValue = [this.task.status];
            },
            editPriority() {
                if (this.propDisable) {
                    return;
                }
                this.currentEditProp = "priority"
                this.showPopup = true;
                this.popupSelectList = [];
                this.popupMultiple = false;
                for (var i = 0; i < this.editTaskInfo.priorityList.length; i++) {
                    var t = this.editTaskInfo.priorityList[i]
                    this.popupSelectList.push({
                        key: t.id,
                        value: t.name
                    })
                }
                this.popupSelectValue = [this.task.priority];
            },
            editSubSystem() {
                if (this.propDisable) {
                    return;
                }
                this.currentEditProp = "subSystemId"
                this.showPopup = true;
                this.popupSelectList = [];
                this.popupMultiple = false;
                for (var i = 0; i < this.editTaskInfo.subSystemList.length; i++) {
                    var t = this.editTaskInfo.subSystemList[i]
                    this.popupSelectList.push({
                        key: t.id,
                        value: t.name
                    })
                }
                if (this.task.subSystemId) {
                    this.popupSelectValue = [this.task.subSystemId];
                }
            },
            editRelease() {
                if (this.propDisable) {
                    return;
                }
                this.currentEditProp = "releaseId"
                this.showPopup = true;
                this.popupSelectList = [];
                this.popupMultiple = false;
                for (var i = 0; i < this.editTaskInfo.releaseList.length; i++) {
                    var t = this.editTaskInfo.releaseList[i]
                    this.popupSelectList.push({
                        key: t.id,
                        value: t.name
                    })
                }
                if (this.task.releaseId) {
                    this.popupSelectValue = [this.task.releaseId];
                }
            },
            editStage() {
                if (this.propDisable) {
                    return;
                }
                this.currentEditProp = "stageId"
                this.showPopup = true;
                this.popupSelectList = [];
                this.popupMultiple = false;
                for (var i = 0; i < this.editTaskInfo.stageList.length; i++) {
                    var t = this.editTaskInfo.stageList[i]
                    this.popupSelectList.push({
                        key: t.id,
                        value: t.name
                    })
                }
                if (this.task.stageId) {
                    this.popupSelectValue = [this.task.stageId];
                }
            },
            editOwner() {
                if (this.memberDisable) {
                    return;
                }
                this.editMember('ownerAccountIdList');
                for (var i = 0; i < this.task.ownerAccountIdList.length; i++) {
                    this.popupSelectValue.push(this.task.ownerAccountIdList[i])
                }
            },
            editCustomMember(field) {
                this.currentCustomField = field;
                this.editMember('field_' + field.id);
                var list = this.task.customFields['field_' + field.id];
                if (list) {
                    for (var i = 0; i < list.length; i++) {
                        this.popupSelectValue.push(list[i])
                    }
                }
            },
            editCustomSelect(field) {
                this.currentEditProp = 'field_' + field.id;
                this.currentCustomField = field

                this.showPopup = true;
                this.popupSelectList = [];
                this.popupMultiple = field.type == 3;
                var list = this.task.customFields['field_' + field.id];
                this.popupSelectValue = [];
                if (list) {
                    if (field.type == 3) {
                        for (var i = 0; i < list.length; i++) {
                            this.popupSelectValue.push(list[i])
                        }
                    }
                    if (field.type == 4) {
                        this.popupSelectValue.push(list)
                    }
                }
                for (var i = 0; i < field.valueRange.length; i++) {
                    var t = field.valueRange[i]
                    this.popupSelectList.push({
                        key: t,
                        value: t
                    })
                }
            },
            editMember(prop) {
                this.currentEditProp = prop
                this.showPopup = true;
                this.popupSelectList = [];
                this.popupMultiple = true;
                for (var i = 0; i < this.editTaskInfo.memberList.length; i++) {
                    var t = this.editTaskInfo.memberList[i]
                    this.popupSelectList.push({
                        key: t.accountId,
                        value: t.accountName
                    })
                }
                this.popupSelectValue = [];
            },
            updateTask(field) {
                var obj = {};
                if (field == 'content') {
                    obj.content = this.$refs.editor.getValue();
                    this.task.content = obj.content;
                }
                if (field.indexOf('field_') != -1) {
                    if (this.oldTask.customFields[field] == this.task.customFields[field]) {
                        return;
                    }
                } else {
                    if (this.oldTask[field] == this.task[field]) {
                        return;
                    }
                }
                obj.id = this.task.id;
                if (field.indexOf('field_') == -1) {
                    obj[field] = this.task[field];
                } else {
                    obj.customFields = {};
                    obj.customFields[field] = this.task.customFields[field];
                }
                //
                let _this = this;
                if (field == 'startDate' || field == 'endDate') {
                    app.invoke('BizAction.getTaskAssociateStat', [app.token, this.task.id], info => {
                        if (info.beforeCount > 0 || info.afterCount > 0 || info.subCount > 0) {
                            this.$vux.confirm.show({
                                content: '该对象关联有前后置对象或子对象，是否需要同步其开始截止时间?',
                                confirmText: '需要',
                                cancelText: '不需要',
                                onCancel() {
                                    _this.updateTaskNative(obj, [field], true);
                                },
                                onConfirm() {
                                    _this.updateTaskNative(obj, [field], false);
                                }
                            })
                        } else {
                            this.updateTaskNative(obj, [field], true);
                        }
                    }, (error) => {
                        this.loadData();
                    });
                } else {
                    this.updateTaskNative(obj, [field], true);
                }
            },
            updateTaskNative(obj, fields, isManualUpdate) {
                app.invoke("BizAction.updateTask", [app.token, obj, fields, isManualUpdate],
                    info => {
                        app.postMessage('task.edit')
                        this.loadData();
                    },
                    () => {
                        this.loadData()
                    }
                );
            },
            popupConfirm(v) {
                if (this.currentEditProp == 'status' && v.length == 1) {
                    var vv = v[0];
                    this.task[this.currentEditProp] = vv;
                    this.updateTask(this.currentEditProp)
                }
                if (this.currentEditProp == 'priority' && v.length == 1) {
                    var vv = v[0];
                    this.task[this.currentEditProp] = vv;
                    this.updateTask(this.currentEditProp)
                }
                if (this.currentEditProp == 'ownerAccountIdList') {

                    this.task[this.currentEditProp] = v;
                    this.updateTask(this.currentEditProp)
                }
                if (this.currentEditProp == 'subSystemId' && v.length == 1) {
                    var vv = v[0];
                    this.task[this.currentEditProp] = vv;
                    this.updateTask(this.currentEditProp)
                }
                if (this.currentEditProp == 'releaseId' && v.length == 1) {
                    var vv = v[0];
                    this.task[this.currentEditProp] = vv;
                    this.updateTask(this.currentEditProp)
                }
                if (this.currentEditProp == 'stageId' && v.length == 1) {
                    var vv = v[0];
                    this.task[this.currentEditProp] = vv;
                    this.updateTask(this.currentEditProp)
                }
                if (this.currentEditProp.indexOf('field_') != -1) {
                    if (this.currentCustomField.type == 6) {
                        this.task.customFields[this.currentEditProp] = v;
                        this.updateTask(this.currentEditProp)
                    }
                    if (this.currentCustomField.type == 3) {
                        this.task.customFields[this.currentEditProp] = v;
                        this.updateTask(this.currentEditProp)
                    }
                    if (this.currentCustomField.type == 4 && v.length == 1) {
                        this.task.customFields[this.currentEditProp] = v[0];
                        this.updateTask(this.currentEditProp)
                    }
                }
            },
            confirmTextEdit() {
                this.showTextEditBox = false;
                var value = this.textEditContent;
                if (this.currentCustomField.isSystemField) {
                    this.task[this.currentEditProp] = value;
                } else {
                    this.task.customFields[this.currentEditProp] = value;
                }
                this.updateTask(this.currentEditProp);
            },
            confirmNumberEdit() {
                this.showNumberEditBox = false;
                var value = this.numberEditContent;
                if (this.currentCustomField.isSystemField) {
                    this.task[this.currentEditProp] = value;
                } else {
                    this.task.customFields[this.currentEditProp] = value;
                }
                this.updateTask(this.currentEditProp);
            },
            confirmReply() {
                this.showReplyBox = false;
                if (this.replyContent == null || this.replyContent == '') {
                    return;
                }
                var comment = {
                    taskId: this.task.id,
                    comment: this.replyContent
                }
                app.invoke("BizAction.addTaskComment", [app.token, comment], info => {
                    this.loadComment();
                    this.replyContent = null;
                })
            },
            confirmEdit() {
                this.showEditBox = false;
                this.updateTask('content');
            },
            confirmWorktime() {
                this.showEditWorktime = false;
                if (!this.worktime.content) {
                    this.$vux.toast.text('请填写工作内容')
                    return;
                }
                if (!this.worktime.startTime) {
                    this.$vux.toast.text('请填写工时开始时间')
                    return;
                }
                if (!this.worktime.hour || this.worktime.hour <= 0) {
                    this.$vux.toast.text('请填写工时时长')
                    return;
                }
                this.worktime.taskId = this.task.id;
                let _this = this;
                this.$vux.confirm.show({
                    content: '确定提交该工时吗?',
                    onCancel() {
                        //do nothing
                    },
                    onConfirm() {
                        app.invoke("BizAction.addTaskWorkTimeLog", [app.token, _this.worktime], (info) => {
                            _this.$vux.toast.text('提交成功');
                            _this.worktime.content = "";
                            _this.worktime.startTime = null;
                            _this.worktime.hour = 0;
                            _this.loadData();
                        })
                    }
                })
            },
            actionMenuClick(key, item) {
                if (key == 'home') {
                    this.$emit('close')
                    app.loadPage('/t/todo')
                }
                if (key == 'taskList') {
                    this.$emit('close')
                    app.loadPage('/t/task_list?projectId=' + this.editTaskInfo.project.uuid + '&type=' + this.task.objectType)
                }
                if (key == 'deleteObject') {
                    this.deleteItem();
                }
            },
            deleteItem() {
                app.confirm('确定要删除此' + this.task.objectTypeName + "吗?", () => {
                    this.confirmDelete();

                })
            },
            confirmDelete() {
                app.invoke("BizAction.deleteTask", [app.token, this.task.id], info => {
                    app.popPage();
                    this.$emit('close')
                })
            },
            showAttachment(item) {
                this.fileUUID = item.uuid;
            },
            getMember(id){
                for(var i=0;i<this.editTaskInfo.memberList.length;i++){
                    var t=this.editTaskInfo.memberList[i];
                    if(t.accountId==id){
                        return t;
                    }
                }
                return {};
            },
        }
    }
</script>


