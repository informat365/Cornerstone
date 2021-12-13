<style scoped>
    .table-box {
        background-color: #fff;
        padding: 30px;
        padding-top: 10px;
        box-shadow: 0px 2px 10px 0px rgba(225, 225, 225, 0.5);
        border: 1px solid rgba(216, 216, 216, 1);
    }

    .table-info {
        color: #999;
        text-align: center;
    }

    .table-count {
        background-color: #e8e8e8;
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

    .right-opt {
        display: flex;
        align-items: center;
        flex-direction: row-reverse;
    }
</style>
<i18n>
    {
    "zh_CN":{
    "条数据":"{0}条数据",
    "名称":"变更任务名称",
    "项目":"项目",
    "审批人":"审批人",
    "创建人":"创建人",
    "时间":"创建时间",
    "状态":"流程状态",
    "更新时间":"更新时间",
    "待认领":"待认领",
    "暂无数据":"暂无数据",
    "类型":"类型",
    "快速创建":"快速创建"
    },
    "en": {
    "条数据":"{0} Items",
    "名称":"Name",
    "项目":"Project",
    "审批人":"Auditor",
    "创建人":"Creator",
    "时间":"Craete Time",
    "更新时间":"Update Time",
    "状态":"Status",
    "待认领":"Unassigned",
    "暂无数据":"No Data",
    "类型":"Type",
    "快速创建":"Create"
    }
    }
</i18n>
<template>
    <div style="padding:20px;">
        <div class="table-info">
            <span class="table-count">{{$t('条数据',[list.length])}}</span>
        </div>
        <div v-if="list" class="table-box">
            <table class="table-content" style="table-layout:fixed">
                <thead>
                <tr>
                    <th>{{$t('名称')}}</th>
                    <th style="width:150px;">{{$t('项目')}}</th>
                    <th style="width:120px;">{{$t('审批人')}}</th>
                    <th style="width:100px;">{{$t('创建人')}}</th>
                    <th style="width:150px">{{$t('时间')}}</th>
                    <th style="width:90px;text-align:right">{{$t('状态')}}</th>
                    <th style="width:150px;text-align:center;">{{$t('更新时间')}}</th>
                </tr>
                </thead>

                <tbody>
                <tr @click="showAlterationInfo(item)" v-for="item in list" :key="'alter_'+item.id" class="table-row clickable">
                    <td class="text-no-wrap">

                        <TaskNameLabel
                            :id="item.serialNo"
                            :name="item.taskName"
                            :object-type="item.objectTypeName" />

                    </td>
                    <td>
                        <div style="max-width:180px" class="text-no-wrap">
                            {{item.projectName}}
                        </div>
                    </td>
                    <td class="text-no-wrap">
                        <TaskOwnerView :tips="false" :value="item" />
                    </td>

                    <td class="text-no-wrap">
                        <AvatarImage
                            size="small"
                            :name="item.createAccountName"
                            :value="item.createAccountImageId"
                            type="label"></AvatarImage>
                    </td>

                    <td>
                        <span>{{item.createTime|fmtDateTime}}</span>
                    </td>
                    <td style="text-align:right">
                        <span
                            :style="{color:item.flowStatusColor,border:'1px solid '+item.flowStatusColor,borderRadius:'15px',padding:'3px',fontSize:'12px'}">
                            {{item.flowStatusName}}</span>
                    </td>
                    <td style="text-align:center">
                        {{ item.updateTime | fmtDateTime }}
                    </td>
                </tr>
                </tbody>
            </table>
            <div class="nodata" v-if="list.length==0">
                {{$t('暂无数据')}}
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        mixins: [componentMixin],
        props: ['list'],
        data() {
            return {
                showTaskId:null
            };
        },

        mounted() {

        },
        methods: {
            showAlterationInfo(item) {
                app.showDialog(TaskAlterationInfoDialog, {
                    id: item.id,
                    taskId:item.taskId,
                    projectId:item.projectId,
                    objectType:item.objectType,
                });
            },
            pageMessage(type,content){
            }
        },
    };
</script>
