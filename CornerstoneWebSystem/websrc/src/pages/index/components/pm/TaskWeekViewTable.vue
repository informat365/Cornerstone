<style scoped>

</style>
<i18n>
    {
        "en": {
            "名称":"Name",
            "分类":"Category",
            "截止时间":"Deadline",
            "责任人":"Owner",
            "状态":"Status",
            "待认领":"none",
            "未分类":"none",
            "暂无数据":"No Data"
        },
        "zh_CN": {
            "名称":"名称",
            "分类":"分类",
            "截止时间":"截止时间",
            "责任人":"责任人",
            "状态":"状态",
            "待认领":"待认领",
            "未分类":"未分类",
            "暂无数据":"暂无数据"
        }
    }
</i18n>
<template>
    <div>
        <table class="table-report">
            <tr>
                <th>{{$t('名称')}}</th>
                <th style="width:200px">{{$t('分类')}}</th>
                <th style="width:120px">{{$t('截止时间')}}</th>
                <th style="width:150px">{{$t('责任人')}}</th>
                <th style="width:100px">{{$t('状态')}}</th>
            </tr>
            <tr class="clickable" @click="showTaskInfo(item.uuid)" v-for="item in dataList" :key="item.id">

                <td>

                    <TaskNameLabel
                        :is-done="item.isFinish" :id="item.serialNo" :name="item.name" />
                </td>
                <td>
                    <CategoryLabel
                        :placeholder="$t('未分类')" :category-list="queryInfo.categoryList" :value="item.categoryIdList" />
                </td>
                <td>
                    <template v-if="type==='normal'">
                        <WeekDayLabel :value="endDate(item)"></WeekDayLabel>
                        <OverdueLabel
                            style="margin-left:5px"
                            v-if="!item.isFinish"
                            :onlyShowOver="true"
                            :value="endDate(item)"></OverdueLabel>
                    </template>
                    <template v-else-if="type==='date'">
                        {{ endDate(item) | fmtDate }}
                        <OverdueLabel
                            style="margin-left:5px"
                            v-if="!item.isFinish"
                            :onlyShowOver="true"
                            :value="endDate(item)"></OverdueLabel>
                    </template>
                    <template v-else-if="type==='due'">
                        <OverdueLabel :value="endDate(item)"></OverdueLabel>
                    </template>
                </td>
                <td class="text-no-wrap">
                    <template v-if="item.ownerAccountList">
                        <template v-if="item.ownerAccountList.length==0">
                            {{$t('待认领')}}
                        </template>
                        <template v-if="item.ownerAccountList.length==1">
                            <AvatarImage
                                size="small"
                                :name="item.ownerAccountList[0].name"
                                :value="item.ownerAccountList[0].imageId"
                                type="label"></AvatarImage>
                        </template>

                        <template v-if="item.ownerAccountList.length>1">
                            <AvatarImage
                                v-for="acc in item.ownerAccountList"
                                :key="item.id+'_acc'+acc.id"
                                size="small"
                                :name="acc.name"
                                :value="acc.imageId"
                                type="none"></AvatarImage>
                        </template>
                    </template>
                    <template v-if="item.ownerAccountList==null">
                        {{$t('待认领')}}
                    </template>
                </td>
                <td>
                    <TaskStatus :label="item.statusName" :color="item.statusColor"></TaskStatus>
                </td>
            </tr>
        </table>
        <div v-if="taskList.length==0" class="table-nodata">{{$t('暂无数据')}}</div>
    </div>
</template>

<script>
    export default {
        mixins: [componentMixin],
        props: {
            queryInfo: {
                type: Object,
                default() {
                    return {};
                },
            },
            taskList: {
                type: Array,
                default() {
                    return [];
                },
            },
            type: {
                type: String,
                default: '',
            },
        },
        data() {
            return {};
        },
        computed: {
            dataList() {
                return this.taskList.sort((a, b) => {
                    return a.endDate - b.endDate;
                });
            },
        },
        methods: {
            endDate(item) {
                if(item.expectEndDate){
                    return item.expectEndDate;
                }
                return item.endDate;
            },
            showTaskInfo(uuid) {
                app.showDialog(TaskDialog, {
                    taskId: uuid,
                });
            },
        },
    };
</script>
