<style scoped>
    .log-item {
        position: relative;
        margin-top: 20px;
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
        font-size: 13px;
        line-height: 1.5;
        word-break: break-all;
    }

    .log-item-before {
        background-color: #ffd3d3;
        color: #ff3b58;
        text-decoration-line: line-through;
    }

    .log-item-after {
        background-color: #8cffd9;
        color: #00d5cc;
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
</style>
<i18n>
    {
    "en": {
    "创建了项目": "Create project",
    "编辑属性":"Edit",
    "新增关联对象":"Add relation object",
    "删除附件":"Delete attchment",
    "上传附件":"Upload attachment",
    "新增子对象":"Add sub object",
    "删除关联对象":"Delete relation object",
    "删除子对象":"Delete sub object",
    "提交了代码":"Submit code",
    "新增项目成员":"Add member",
    "删除项目成员":"Remove member",
    "新增迭代":"Add iteration",
    "删除迭代":"Delete iteration",
    "新增":"Add ",
    "删除了":"Delete ",
    "新增Release":"Add Release",
    "删除Release":"Delete Release",
    "新增子系统":"Add system",
    "删除子系统":"Delete system",
    "从回收站恢复":"Recovery from Recycling Bin",
    "创建知识库":"Add Wiki",
    "删除知识库":"Delete Wiki",
    "创建知识库页面":"Add Wiki page",
    "删除知识库页面":"Delete Wiki page",
    "新增阶段":"Add Stage",
    "编辑阶段":"Edit Stage",
    "删除阶段":"Delete Stage",
    "新增里程碑":"Add Landmark",
    "编辑里程碑":"Edit Landmark",
    "删除里程碑":"Delete Landmark",
    "创建主机":"Add machine",
    "删除主机":"Delete machine",
    "归档了项目":"Archive project",
    "删除了项目":"Delete project",
    "重新打开了项目":"Reopen project",
    "登录主机":"Login machine",
    "执行pipeline":"execute pipeline",
    "填写了进度报告":"Edit progress",
    "上传了文件":"Upload file",
    "删除了文件":"Delete file",
    "状态":"Status",
    "流程状态":"Workflow status",
    "变更项目工作流程状态":"Update project status",
    "未设置":"none"
    },
    "zh_CN": {
    "创建了项目": "创建了项目",
    "编辑属性":"编辑属性",
    "新增关联对象":"新增关联对象",
    "删除附件":"删除附件",
    "上传附件":"上传附件",
    "新增子对象":"新增子对象",
    "删除关联对象":"删除关联对象",
    "删除子对象":"删除子对象",
    "提交了代码":"提交了代码",
    "新增项目成员":"新增项目成员",
    "删除项目成员":"删除项目成员",
    "新增迭代":"新增迭代",
    "删除迭代":"删除迭代",
    "新增":"新增",
    "删除了":"删除了",
    "新增Release":"新增Release",
    "删除Release":"删除Release",
    "新增子系统":"新增子系统",
    "删除子系统":"删除子系统",
    "从回收站恢复":"从回收站恢复",
    "创建知识库":"创建知识库",
    "删除知识库":"删除知识库",
    "从回收站恢复":"从回收站恢复",
    "创建知识库页面":"创建知识库页面",
    "删除知识库页面":"删除知识库页面",
    "新增阶段":"新增阶段",
    "编辑阶段":"编辑阶段",
    "删除阶段":"删除阶段",
    "新增里程碑":"新增里程碑",
    "编辑里程碑":"编辑里程碑",
    "删除里程碑":"删除里程碑",
    "创建主机":"创建主机",
    "删除主机":"删除主机",
    "归档了项目":"归档了项目",
    "删除了项目":"删除了项目",
    "重新打开了项目":"重新打开了项目",
    "登录主机":"登录主机",
    "执行pipeline":"执行pipeline",
    "填写了进度报告":"填写了进度报告",
    "上传了文件":"上传了文件",
    "删除了文件":"删除了文件",
    "状态":"状态",
    "流程状态":"流程状态",
    "变更项目工作流程状态":"变更项目工作流程状态",
    "未设置":"未设置"

    }
    }
</i18n>
<template>
    <div>
        <div class="log-item-content" style="color:#999;font-size:12px">
            <div v-if="item.type==1">{{$t('创建了项目')}}</div>
            <div v-if="item.type==2">
                {{$t('编辑属性')}}
                <span
                    @click="showTaskInfo(item.itemId)"
                    class="log-item-link2"
                    v-if="item.itemId"
                >{{item.remark}}</span>
                <template v-if="item.itemId==null">{{item.remark}}</template>
            </div>
            <div v-if="item.type==5">
                {{$t('新增关联对象')}}
                <span
                    @click="showTaskInfo(item.items.uuid)"
                    class="log-item-link2"
                ><span class="numberfont">#{{item.items.serialNo}}</span> {{item.items.name}}</span>
            </div>
            <div v-if="item.type==53">
                {{$t('新增关联后置对象')}}
                <span
                    @click="showTaskInfo(item.items.uuid)"
                    class="log-item-link2"
                ><span class="numberfont">#{{item.items.serialNo}}</span> {{item.items.name}}</span>
            </div>
            <div v-if="item.type==52">
                {{$t('新增关联前置对象')}}
                <span
                    @click="showTaskInfo(item.items.uuid)"
                    class="log-item-link2"
                ><span class="numberfont">#{{item.items.serialNo}}</span> {{item.items.name}}</span>
            </div>
            <div v-if="item.type==4">
                {{$t('删除附件')}}
                <span
                    @click="showTaskInfo(item.items.uuid)"
                    class="log-item-link2"
                ><span class="numberfont">#{{item.items.serialNo}}</span> {{item.items.name}}</span>
            </div>
            <div v-if="item.type==3">
                {{$t('上传附件')}}
                <span
                    @click="showTaskInfo(item.items.uuid)"
                    class="log-item-link2"
                ><span class="numberfont">#{{item.items.serialNo}}</span> {{item.items.name}}</span>
            </div>
            <div v-if="item.type==7">
                {{$t('新增子对象')}}
                <span
                    @click="showTaskInfo(item.items.uuid)"
                    class="log-item-link2"
                ><span class="numberfont">#{{item.items.serialNo}}</span> {{item.items.name}}</span>
            </div>
            <div v-if="item.type==6">
                {{$t('删除关联对象')}}
                <span
                    @click="showTaskInfo(item.items.uuid)"
                    class="log-item-link2"
                ><span class="numberfont">#{{item.items.serialNo}}</span> {{item.items.name}}</span>
            </div>
            <div v-if="item.type==8">
                {{$t('删除子对象')}}
                <span><span class="numberfont">#{{item.items.serialNo}}</span> {{item.items.name}}</span>
            </div>
            <div v-if="item.type==20">
                {{$t('提交了代码')}}
                <span>
                    <Icon type="md-git-commit"/>
                    {{item.items.version}} {{item.items.comment}}
                </span>
                <span
                    @click="showTaskInfo(item.items.uuid)"
                    class="log-item-link2"
                ><span class="numberfont">#{{item.items.serialNo}}</span> {{item.items.name}}</span>
            </div>

            <div v-if="item.type==10">
                {{$t('新增项目成员')}}
                <span
                    v-for="user in item.items"
                    :key="'user_'+user.id"
                    class="log-item-link"
                >{{user.accountName}}</span>
            </div>
            <div v-if="item.type==11">
                {{$t('删除项目成员')}}
                <span class="log-item-link">{{item.items.accountName}}</span>
            </div>

            <div v-if="item.type==14">
                {{$t('新增迭代')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>
            <div v-if="item.type==15">
                {{$t('删除迭代')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>

            <div v-if="item.type==12">
                {{$t('新增')}}{{item.items.objectType|dataDict('Task.objectType')}}
                <span
                    @click="showTaskInfo(item.items.uuid)"
                    class="log-item-link2"
                ><span class="numberfont">#{{item.items.serialNo}}</span> {{item.items.name}}</span>
            </div>
            <div v-if="item.type==13">
                {{$t('删除了')}}{{item.items.objectType|dataDict('Task.objectType')}}
                <span
                    class="log-item-link"
                >{{item.items.name}}</span>
            </div>

            <div v-if="item.type==16">
                {{$t('新增Release')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>
            <div v-if="item.type==17">
                {{$t('删除Release')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>

            <div v-if="item.type==18">
                {{$t('新增子系统')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>
            <div v-if="item.type==19">
                {{$t('删除子系统')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>

            <div v-if="item.type==201">
                {{$t('从回收站恢复')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>

            <div v-if="item.type==110">
                {{$t('从回收站恢复')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>

            <div v-if="item.type==101">
                {{$t('创建知识库')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>
            <div v-if="item.type==102">
                {{$t('删除知识库')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>
            <div v-if="item.type==103">
                {{$t('从回收站恢复')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>

            <div v-if="item.type==120">
                {{$t('创建知识库页面')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>
            <div v-if="item.type==121">
                {{$t('删除知识库页面')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>
            <div v-if="item.type==122">
                {{$t('从回收站恢复')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>

            <div v-if="item.type==131">
                {{$t('创建主机')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>
            <div v-if="item.type==132">
                {{$t('删除主机')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>
            <div v-if="item.type==133">
                {{$t('从回收站恢复')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>

            <div v-if="item.type==141">{{$t('归档了项目')}}</div>
            <div v-if="item.type==142">{{$t('删除了项目')}}</div>
            <div v-if="item.type==143">{{$t('重新打开了项目')}}</div>
            <div v-if="item.type==144">
                {{$t('变更项目工作流程状态')}}
                <table  class="change-log-table">
                    <tr >
                        <td style="width:60px;padding-left:5px">{{$t('流程状态')}}</td>
                        <td>
                            <span class="log-item-before">
                                <template >{{item.items.beforeContent||$t('未设置')}}</template>
                            </span>
                            <Icon type="md-arrow-round-forward" style="padding-left:5px;padding-right:5px"/>

                            <span class="log-item-after">
                        <template >{{item.items.afterContent||$t('未设置')}}</template>
                    </span>
                        </td>
                    </tr>
                </table>
                <!--                <span class="log-item-link">{{item.items.content}}</span>-->
            </div>

            <div v-if="item.type==150">
                {{$t('登录主机')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>
            <div v-if="item.type==160">
                {{$t('执行pipeline')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>

            <div v-if="item.type==170">
                {{$t('填写了进度报告')}}
                <span class="log-item-link">{{item.items.content}}</span>
            </div>

            <div v-if="item.type==171">
                {{$t('编辑迭代')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>

            <div v-if="item.type==180">
                {{$t('上传了文件')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>
            <div v-if="item.type==181">
                {{$t('删除了文件')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>
            <div v-if="item.type==1500">
                {{$t('关联WIKI')}}
                <span
                    @click="showTaskInfo(item.items.uuid)"
                    class="log-item-link2"
                ><span class="numberfont">#{{item.items.serialNo}}</span> {{item.items.name}}</span>
            </div>
            <div v-if="item.type==1501">
                {{$t('解除关联WIKI')}}
                <span
                    @click="showTaskInfo(item.items.uuid)"
                    class="log-item-link2"
                ><span class="numberfont">#{{item.items.serialNo}}</span> {{item.items.name}}</span>
            </div>
            <div v-if="item.type==1600">
                {{$t('新增阶段')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>
            <div v-if="item.type==1601">
                {{$t('编辑阶段')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>
            <div v-if="item.type==1602">
                {{$t('删除阶段')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>
            <div v-if="item.type==1700">
                {{$t('新增里程碑')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>
            <div v-if="item.type==1701">
                {{$t('编辑里程碑')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>
            <div v-if="item.type==1702">
                {{$t('删除里程碑')}}
                <span class="log-item-link">{{item.items.name}}</span>
            </div>
        </div>
        <table v-if="item.type==2" class="change-log-table">
            <tr v-for="(t,idx) in item.items" :key="item.id+'_log_'+idx">
                <td style="width:60px;padding-left:5px">{{t.name}}</td>
                <td>
                    <span class="log-item-before">
                        <template v-if="t.beforeContent">{{t.beforeContent}}</template>
                        <template v-if="t.beforeContent==null">{{$t('未设置')}}</template>
                    </span>
                    <Icon type="md-arrow-round-forward" style="padding-left:5px;padding-right:5px"/>

                    <span class="log-item-after">
                        <template v-if="t.afterContent">{{t.afterContent}}</template>
                        <template v-if="t.afterContent==null">{{$t('未设置')}}</template>
                    </span>
                </td>
            </tr>
        </table>
    </div>
</template>

<script>
    export default {
        mixins: [componentMixin],
        props: ["item"],
        data() {
            return {};
        },
        methods: {
            showTaskInfo(uuid) {
                if (uuid == null) {
                    return;
                }
                if (uuid.length < 10) {
                    //非任务
                    return;
                }
                app.showDialog(TaskDialog, {
                    taskId: uuid,
                    showTopBar: true
                });
            }
        }
    };
</script>
