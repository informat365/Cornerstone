<style scoped>
    .rel-task-switch {
        position: absolute;
        bottom: 0;
        right: 0;
        font-size: 12px;
    }

    .rel-task-item {
        display: flex;
        width: 800px;
        margin-bottom: 5px;
        align-items: center;
    }

    .rel-task-item:hover {
        font-weight: bold !important;
    }

    .rel-task-name {
        flex: 1;
        overflow: hidden;
        cursor: pointer;
    }

    .rel-task-status {
        width: 80px;
        overflow: hidden;
    }

    .rel-task-owner {
        width: 100px;
        overflow: hidden;
        display: flex;
        align-items: center;
    }

    .rel-task-time {
        width: 250px;
        overflow: hidden;
    }

    .rel-td {
        cursor: default;
        background-color: #f8f8f9;
    }

    .rel-td:hover {
        background-color: #f8f8f9;
    }
</style>
<i18n>
    {
    "en": {
    "未设置":"none",
    "未分类":"none",
    "天":"Day"
    },
    "zh_CN": {
    "未设置":"未设置",
    "未分类":"未分类",
    "天":"天"
    }
    }
</i18n>
<template>
    <tr
        v-if="(row[group.field] === group.value && group.show) || group.field==null"
        v-show="row.showRelTaskList"
        class="task-rel-row">
        <td></td>
        <td class="rel-td" :colspan="columnCount">
            <div
                v-for="relTask in row.associatedList"
                :key="'ass'+relTask.id"
                class="rel-task-item">
                <div class="rel-task-owner  text-no-wrap">
                    <TaskOwnerView :value="relTask" />
                </div>
                <div class="rel-task-status  text-no-wrap">
                    <TaskStatus
                        v-if="relTask.statusName"
                        :label="relTask.statusName"
                        :color="relTask.statusColor" />
                </div>
                <div class="rel-task-name text-no-wrap">
                    <TaskNameLabel
                        @click.native="onClickRelTaskName(relTask)"
                        :id="relTask.serialNo"
                        :is-done="relTask.isFinish"
                        :name="relTask.name"
                        :rel="relTask.associatedType"
                        :object-type="relTask.objectTypeName" />
                </div>
                <div
                    class="rel-task-time"
                    v-if="!!relTask.startDate || !!relTask.startDate">
                    {{relTask.startDate|fmtDate}}
                    <Icon type="md-arrow-round-forward" />
                    {{relTask.endDate|fmtDate}}
                    <template v-if="relTask.endDate">
                        <OverdueText :is-finish="relTask.isFinish" v-if="relTask.endDate"  :finish-date="relTask.finishTime"  style="margin-left:5px" :value="relTask.endDate"/>
<!--                        <OverdueText-->
<!--                            v-if="!relTask.isFinish&&relTask.endDate"-->
<!--                            style="margin-left:5px"-->
<!--                            :value="relTask.endDate" />-->
                    </template>
                </div>
            </div>
        </td>
    </tr>
</template>

<script>
    export default {
        mixins: [componentMixin],
        props: {
            row: {
                type: Object,
            },
            group: {
                type: Object,
            },
            queryInfo: {
                type: Object,
            },
            viewSetting: {
                type: Object,
                default() {
                    return {};
                },
            },
        },
        computed: {
            columnCount() {
                const fields = this.queryInfo.fieldList.filter(field => {
                    return field.isShow && !this.isTableFieldHide(field);
                });
                return fields.length;
            },
        },
        data() {
            return {
                title: 'TaskListViewRelTaskRow',
            };
        },
        methods: {
            onClickRelTaskName(item) {
                this.$emit('on-rel-task-show', item);
            },
            isTableFieldHide(field) {
                if (!this.viewSetting.hideTableFieldList) {
                    return false;
                }
                return this.viewSetting.hideTableFieldList.findIndex(item => item.id === field.id) > -1;
            },
        },
    };
</script>
