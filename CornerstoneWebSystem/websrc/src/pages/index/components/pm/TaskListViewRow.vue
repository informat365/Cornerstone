<style scoped>
    .checkbox-col {
        width: 60px;
        text-align: center;
    }

    .checkbox-col .ivu-checkbox-wrapper {
        margin-right: 0;
    }

    .with-row-expand .checkbox-col {
        width: 80px;
    }

    .checkbox-col .row-number {
        color: #888;
        font-size: 12px;
    }

    .task-row-select {
        background-color: #ebf7ff !important;
    }

    .task-row th {
        padding: 10px !important;
    }

    .task-row td {
        position: relative;
        padding: 10px;
    }

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

    .table-row-group td {
        background-color: #f8f8f9;
        padding-top: 3px;
        padding-bottom: 3px;
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
        class="task-row"
        :class="{'task-row-select':currentTaskUuid===row.uuid,'with-row-expand': withExpandTask}"
        @mouseover="hover = true"
        @mouseout="hover=false">
        <td class="checkbox-col">
            <Checkbox v-show="showCheckBox" @on-change="onCheckboxChange" v-model="row.isSelect"></Checkbox>
            <span class="row-number" v-show="!showCheckBox">
                {{ row.rowNum }}
            </span>
        </td>
        <template v-for="field in viewSetting.tableFieldList">
            <td
                @click.stop="onClickTaskName(row)"
                class="clickable text-no-wrap"
                :key="field.id"
                v-if="field.isShow && !isTableFieldHide(field)">
                <div
                    @click.stop="onRelTaskToggle(row)"
                    v-if="field.field==='name'&&row.associatedList&&row.associatedList.length>0"
                    class="rel-task-switch numberfont">
                    <span>{{row.associatedFinishedCount}}/{{row.associatedList.length}}</span>
                    <Icon type="ios-link"/>
                </div>
                <TaskNameLabel
                    ref="task_row_head"
                    v-if="field.field==='name'"
                    :is-done="row.isFinish"
                    :loading="row.loading"
                    :expand="row.rowExpand"
                    :light="currentTaskUuid===row.uuid"
                    :id="row.serialNo"
                    :name="row.name"
                    :with-expand="withExpandTask"
                    :can-expand="canExpandTask"
                    :style="taskNameStyle"
                    @on-task-expand="onTaskRowExpand"/>
                <span v-else-if="field.field==='iterationName'">{{row.iterationName}}</span>
                <TaskStatus
                    v-else-if="field.field==='statusName'" :label="row.statusName" :color="row.statusColor"/>
                <TaskPriority
                    v-else-if="field.field==='priorityName'" :label="row.priorityName" :color="row.priorityColor"/>
                <AvatarImage
                    v-else-if="field.field==='createAccountName'"
                    size="small"
                    :name="row.createAccountName"
                    :value="row.createAccountImageId"
                    type="label"/>
                <TaskOwnerView
                    v-else-if="field.field==='ownerAccountName'" :tips="false" :value="row"/>
                <span v-else-if="field.field==='startDate'">
                          {{row.startDate|fmtDate}}
                    <DiffDayText v-if="row.startDate&&row.endDate" :start="row.startDate" :end="row.endDate"></DiffDayText>
                    </span>
                <span v-else-if="field.field==='endDate'">
                          {{row.endDate|fmtDate}}
                          <OverdueText :is-finish="row.isFinish" v-if="row.endDate"  :finish-date="row.finishTime"  style="margin-left:5px" :value="row.endDate"/>
                    </span>
                <span v-else-if="field.field==='createTime'">
                          {{row.createTime|fmtDateTime}}
                    </span>
                <span v-else-if="field.field==='finishTime'">
                          {{row.finishTime|fmtDateTime}}
                    </span>
                <span v-else-if="field.field==='updateTime'">
                          {{row.updateTime|fmtDateTime}}
                    </span>
                <span v-else-if="field.field==='expectEndDate'">
                      {{row.expectEndDate|fmtDate}}
                    <OverdueText :is-finish="row.isFinish"  v-if="row.expectEndDate" :finish-date="row.finishTime" style="margin-left:5px"
                                 :value="row.expectEndDate"/>
                </span>
                <span v-else-if="field.field==='startDays'">
                          {{row.startDays}}{{$t('天')}}
                    </span>
                <span v-else-if="field.field==='endDays'">
                          {{row.endDays}}{{$t('天')}}
                    </span>
                <span v-else-if="field.field==='expectWorkTime'">
                          {{row.expectWorkTime}}h
                    </span>
                <span v-else-if="field.field==='workTime'">
                      {{row.workTime}}h
                    </span>
                <span v-else-if="field.field==='workLoad'">
                      {{row.workLoad}}
                    </span>
                <span v-else-if="field.field==='progress'">
                    <Progress :percent="row.progress"/>
                </span>
                <Label :value="row.releaseName" v-if="field.field==='releaseName'"/>
                <Label :value="row.subSystemName" v-if="field.field==='subSystemName'"/>
                <Label :value="row.stageName" v-if="field.field==='stageName'"/>
                <CategoryLabel
                    :placeholder="$t('未分类')"
                    v-if="field.field==='categoryIdList'"
                    :category-list="queryInfo.categoryList"
                    :value="row.categoryIdList"/>
                <span v-if="field.isSystemField===false">
                          <template v-if="row.customFields">
                              <Label
                                  :value="row.customFields['field_'+field.id]"
                                  v-if="field.type===1||field.type===4||field.type===8"></Label>
                               <span v-if="field.type===6&&row.customFields['fieldobject_'+field.id]!=null">
                                    <template v-if="row.customFields['fieldobject_'+field.id].length===0">
                                      {{$t('未设置')}}
                                    </template>
                                    <template v-if="row.customFields['fieldobject_'+field.id].length===1">
                                      <AvatarImage
                                          size="small"
                                          :name="row.customFields['fieldobject_'+field.id][0].name"
                                          :value="row.customFields['fieldobject_'+field.id][0].imageId"
                                          type="label"/>
                                  </template>

                                  <template v-if="row.customFields['fieldobject_'+field.id].length>1">
                                      <AvatarImage
                                          v-for="acc in row.customFields['fieldobject_'+field.id]"
                                          :key="row.id+'_cst'+acc.id"
                                          size="small"
                                          :name="acc.name"
                                          :value="acc.imageId"
                                          type="none"></AvatarImage>
                                  </template>

                              </span>
                                <span v-if="field.type===7">
                                  <template v-if="field.showTimeField===true">{{row.customFields['field_'+field.id]|fmtDateTime}}</template>
                                  <template v-else>{{row.customFields['field_'+field.id]|fmtDate}}</template>
                                </span>
                              <TaskCustomLabel v-if="field.type===3" :value="row.customFields['field_'+field.id]"/>
                          </template>
                    </span>
            </td>
        </template>
        <td style="min-width:60px;"></td>
    </tr>
</template>

<script>
    import DiffDayText from "../../../../components/ui/DiffDayText";
    export default {
        components: {DiffDayText},
        mixins: [componentMixin],
        props: {
            row: {
                type: Object,
            },
            parentPath: {
                type: String,
            },
            group: {
                type: Object,
            },
            queryInfo: {
                type: Object,
            },
            tableCellWidth: {
                type: Object,
            },
            viewSetting: {
                type: Object,
                default() {
                    return {};
                },
            },
            currentTaskUuid: {
                type: String,
            },
            //项目集任务
            projectSetTask: {
                type: Boolean,
                default: false
            }
        },
        computed: {
            canExpandTask() {
                return this.row.subTaskCount > 0;
            },
            withExpandTask() {
                return this.viewSetting && this.viewSetting.displayView === 2;
            },
            taskNameStyle() {
                if (this.row.rowLevel > 0) {
                    return {
                        paddingLeft: `${this.row.rowLevel * 15}px`,
                    };
                }
                return {};
            },
            showCheckBox() {
                return this.row.isSelect || this.hover;
            },
            columnCount() {
                const fields = this.viewSetting.tableFieldList.filter(field => {
                    return field.isShow && !this.isTableFieldHide(field);
                });
                return fields.length;
            },
        },
        data() {
            return {
                hover: false,
            };
        },
        methods: {
            onCheckboxChange(val) {
                this.$emit('on-checkbox-change', val);
            },
            onTaskRowExpand(val) {
                this.$emit('on-task-row-expand', val);
            },
            onClickTaskName(item) {
                let projectUuid = item.customFields && item.customFields.associateProjectUuid;
                let params = {
                    uuid: item.uuid,
                    projectUuid: projectUuid
                };
                this.$emit('on-task-show', params);
            },
            isTableFieldHide(field) {
                if (!this.viewSetting.hideTableFieldList) {
                    return false;
                }
                return this.viewSetting.hideTableFieldList.indexOf(field.id) > -1;
            },
            onRelTaskToggle() {
                this.$emit('on-rel-task-toggle');
            },
            triggerChildExpand() {
                this.$nextTick(() => {
                    this.row.rowExpand = !this.row.rowExpand;
                    this.onTaskRowExpand(this.row.rowExpand)
                })
            },
        },
    };
</script>
