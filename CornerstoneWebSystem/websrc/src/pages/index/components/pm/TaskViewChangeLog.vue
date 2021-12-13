<style lang="less" scoped>
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
        padding-left: 40px;
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
        word-break: break-all;
    }

    .log-item-after {
        background-color: #d4efe6;
        color: #128c87;
        word-break: break-all;
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
        table-layout: fixed;
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
        padding: 2px 0px;
        text-align: center;
        color: #00d5cc;
        font-size: 12px;
        margin-left: 0px;
        transform: scale(0.7);
    }

    .log-item-owner {
        color: #0091ea;
    }

    .log-item-link {
        color: #0091ea;
        padding-left: 5px;
        padding-right: 5px;
        cursor: pointer;
    }

    .codebox {
        overflow: auto;
    }


    .version-task-table {
        border-left: 4px solid #ccc;
        padding-top: 5px;
        padding-left: 10px;

        &-row {
            height: 32px;
            display: flex;
            align-items: center;
            align-content: center;
            font-size: 12px;
            border-bottom: solid 1px #f3f3f3;
            cursor: pointer;

            &:hover {
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
            }
        }
    }
</style>
<i18n>
    {
    "en": {
    "创建了对象":"Create Object",
    "编辑属性":"Edit",
    "上传附件":"Upload Attachment",
    "删除附件":"Delete Attachment",
    "新增关联对象":"Add Relation",
    "取消关联对象":"Cancel Relation",
    "新增子对象":"Add sub-object",
    "删除子对象":"Delete sub-object",
    "修改了详细描述":"Edit Detail",
    "查看":"View",
    "收起":"Fold",
    "创建版本库版本":"Create Version",
    "编辑版本库版本":"Update Version",
    "删除版本库版本":"Delete Version",
    "版本库版本关联对象":"Version related objects",
    "版本库版本删除对象":"Version removes associated objects",
    "提交了代码":"Submit code",
    "关联WIKI":"Associate to wiki",
    "解除关联WIKI":"Dismiss associate to wiki",
    "未设置":"none"
    },
    "zh_CN": {
    "创建了对象":"创建了对象",
    "编辑属性":"编辑属性",
    "上传附件":"上传附件",
    "删除附件":"删除附件",
    "新增关联对象":"新增关联对象",
    "取消关联对象":"取消关联对象",
    "新增子对象":"新增子对象",
    "删除子对象":"删除子对象",
    "修改了详细描述":"修改了详细描述",
    "查看":"查看",
    "收起":"收起",
    "创建版本库版本":"创建版本",
    "编辑版本库版本":"编辑版本",
    "删除版本库版本":"删除版本",
    "版本库版本关联对象":"版本关联对象",
    "版本库版本删除对象":"版本移除关联对象",
    "提交了代码":"提交了代码",
    "关联WIKI":"关联WIKI",
    "解除关联WIKI":"解除关联WIKI",
    "未设置":"未设置"
    }
    }
</i18n>
<template>
    <div>
        <div class="log-item">
            <div class="log-item-left">
                <AvatarImage
                    v-if="item.createAccountId>0"
                    :name="item.createAccountName"
                    :value="item.createAccountImageId"
                    type="tips"/>
                <AvatarImage
                    v-if="item.type==20&&item.createAccountId==0" :name="item.items.author" type="tips"/>
            </div>
            <div class="log-item-right">
                <div>
                    <span class="log-item-name" v-if="item.type==20">{{item.items.author}}</span>
                    <span class="log-item-name">{{item.createAccountName}}</span>
                    <span class="log-item-time">{{item.createTime|fmtDeltaTime}}</span>
                </div>
                <div class="log-item-content" style="color:#999;font-size:12px">
                    <div v-if="item.type==1">
                        <Icon class="log-item-icon" type="ios-document-outline"/>
                        {{$t('创建了对象')}}
                    </div>
                    <div v-if="item.type==2">
                        <Icon class="log-item-icon" type="ios-create-outline"/>
                        {{$t('编辑属性')}}
                    </div>
                    <div v-if="item.type==3">
                        <Icon class="log-item-icon" type="ios-attach"/>
                        {{$t('上传附件')}}
                        <span
                            class="log-item-link" @click="previewAttachment(item.items)">{{item.items.name}}</span>
                    </div>
                    <div v-if="item.type==4">
                        <Icon class="log-item-icon" type="ios-trash-outline"/>
                        {{$t('删除附件')}}
                        <span>{{item.items.name}}</span>
                    </div>
                    <div v-if="item.type==5">
                        <Icon class="log-item-icon" type="ios-link-outline"/>
                        {{$t('新增关联对象')}}
                        <span
                            class="log-item-link" @click="showTaskInfo(item.items)">{{item.items.name}}</span>
                    </div>
                    <div v-if="item.type==6">
                        <Icon class="log-item-icon" type="ios-trash-outline"/>
                        {{$t('取消关联对象')}}
                        <span
                            class="log-item-link" @click="showTaskInfo(item.items)">{{item.items.name}}</span>
                    </div>
                    <div v-if="item.type==7">
                        <Icon class="log-item-icon" type="ios-link-outline"/>
                        {{$t('新增子对象')}}
                        <span
                            class="log-item-link" @click="showTaskInfo(item.items)">{{item.items.name}}</span>
                    </div>
                    <div v-if="item.type==8">
                        <Icon class="log-item-icon" type="ios-trash-outline"/>
                        {{$t('删除子对象')}}
                        <span>{{item.items.name}}</span>
                    </div>
                    <div v-if="item.type==9">
                        <Icon class="log-item-icon" type="ios-create-outline"/>
                        {{$t('修改了详细描述')}}
                        <span class="log-item-link" @click="viewContentDiff(item.items)">{{$t('查看')}}</span>
                    </div>
                    <div v-if="item.type==20">
                        <Icon class="log-item-icon" type="ios-code"/>
                        {{$t('提交了代码')}}
                        <Icon type="md-git-commit"/>
                        {{item.items.version}}
                    </div>
                    <div v-if="item.type===1100">
                        <Icon class="log-item-icon" type="ios-document-outline"/>
                        {{$t('创建版本库版本')}}
                    </div>
                    <div v-if="item.type===1101">
                        <Icon class="log-item-icon" type="ios-create-outline"/>
                        {{$t('编辑版本库版本')}}
                    </div>
                    <div v-if="item.type===1102">
                        <Icon class="log-item-icon" type="ios-trash-outline"/>
                        {{$t('删除版本库版本')}}
                    </div>
                    <div v-if="item.type===1103">
                        <Icon class="log-item-icon" type="ios-link-outline"/>
                        {{$t('版本库版本关联对象')}}
                        <span class="log-item-link" @click="viewDetailShow(item)" v-if="item.shown !== true">{{$t('查看')}}</span>
                        <span class="log-item-link" @click="viewDetailShow(item)" v-else>{{$t('收起')}}</span>
                    </div>
                    <div v-if="item.type===1104">
                        <Icon class="log-item-icon" type="ios-trash-outline"/>
                        {{$t('版本库版本删除对象')}}
                        <span class="log-item-link" @click="viewDetailShow(item)" v-if="item.shown !== true">{{$t('查看')}}</span>
                        <span class="log-item-link" @click="viewDetailShow(item)" v-else>{{$t('收起')}}</span>
                    </div>
                </div>
                <div v-if="item.type==20">
                    <pre class="scrollbox codebox">{{item.items.comment}}</pre>
                    <pre class="scrollbox codebox">{{item.items.changed}}</pre>
                </div>
                <table v-if="item.type==2||item.type==1400" class="change-log-table">
                    <tr v-for="(t,idx) in item.items" :key="item.id+'_log_'+idx">
                        <td
                            class="text-no-wrap" style="width:80px;max-width:80px;padding-left:5px">{{t.name}}
                        </td>
                        <td style="width:300px">
                            <span class="log-item-before">
                                <template v-if="t.beforeContent">{{t.beforeContent}}</template>
                                <template v-if="t.beforeContent==null||t.beforeContent==''">{{$t('未设置')}}</template>
                            </span>
                            <Icon
                                type="md-arrow-round-forward" style="padding-left:5px;padding-right:5px"/>
                            <span class="log-item-after">
                                <template v-if="t.afterContent">{{t.afterContent}}</template>
                                <template v-if="t.afterContent==null||t.afterContent==''">{{$t('未设置')}}</template>
                            </span>

                            <span class="log-item-time">{{t.createTime|fmtDeltaTime}}</span>
                        </td>
                    </tr>
                </table>
                <template v-if="item.type===1103 || item.type===1104">
                    <div class="version-task-table" v-show="item.shown">
                        <div
                            v-for="(t,idx) in item.items"
                            :key="item.id+'_log_'+idx"
                            class="version-task-table-row"
                            @click="showTaskInfo(t)">
                            <Tag size="small">{{ t.objectTypeName }}</Tag>
                            <div class="version-task-table-row-name">
                            <span class="task-project-name">
                                {{ t.projectName }}
                            </span>
                                <span class="task-no numberfont">#{{ t.serialNo }}</span>
                                <span class="task-name">
                              {{ t.name }}
                            </span>
                            </div>
                        </div>
                    </div>
                </template>
                <div v-if="item.type==1200">
                    <Icon class="log-item-icon" type="ios-attach"/>
                    {{$t('创建交付版本')}}
                    <span class="log-item-link">{{item.items.name}}</span>
                </div>
                <div v-if="item.type==1201">
                    <Icon class="log-item-icon" type="ios-create-outline"/>
                    {{$t('编辑交付版本')}}
                    <span class="log-item-link">{{item.items.name}}</span>
                </div>
                <div v-if="item.type==1202">
                    <Icon class="log-item-icon" type="ios-trash"/>
                    {{$t('删除交付版本')}}
                    <span class="log-item-link">{{item.items.name}}</span>
                </div>
                <div v-if="item.type==1300">
                    <Icon class="log-item-icon" type="ios-trash"/>
                    {{$t('恢复汇报')}}
                    <span class="log-item-link">{{item.items.name}}</span>
                </div>
                <div v-if="item.type==1400">
                    <Icon class="log-item-icon" type="ios-trash"/>
                    {{$t('编辑工时记录')}}
                    <span class="log-item-link">{{item.items.name}}</span>
                </div>
                <div v-if="item.type==1500">
                    <Icon class="log-item-icon" type="ios-attach"/>
                    {{$t('关联WIKI')}}
                    <span
                        class="log-item-link" @click="previewWikiPage(item.items)">{{item.items.name}}</span>
                </div>
                <div v-if="item.type==1501">
                    <Icon class="log-item-icon" type="ios-attach"/>
                    {{$t('解除关联WIKI')}}
                    <span
                        class="log-item-link" @click="previewWikiPage(item.items)">{{item.items.name}}</span>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
    export default {
        name: 'TaskViewChangeLog',
        props: ['value'],
        data() {
            return {
                item: this.value,
            };
        },
        watch: {
            value(val) {
                this.item = val;
            },
        },
        methods: {
            previewAttachment(item) {
                app.previewFile(item.uuid);
            },
            viewContentDiff(item) {
                app.showDialog(TaskContentDiffDialog, {
                    id: item,
                });
            },
            viewDetailShow(item) {
                this.$set(item, 'shown', !item.shown);
            },
            showTaskInfo(item) {
                app.showDialog(TaskDialog, {
                    taskId: item.uuid,
                });
            },
            previewWikiPage(wikiPage){
                app.invoke('BizAction.getWikiPageById', [app.token, wikiPage.id], (info) => {
                    if(info&&info.isDelete){
                        app.toast(this.$t("页面已删除，无法查看"));
                        return false;
                    }else{
                        app.showDialog(WikiPageDialog,{wiki:wikiPage.id});
                    }
                });
                // app.showDialog(WikiPageDialog,{wiki:wikiPage.id});
            }
        },
    };
</script>
