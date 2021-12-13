<style scoped>

    .table-info-bar {
        font-size: 12px;
        font-weight: bold;
        color: #999;
        margin-bottom: 5px;
        margin-top: 5px;
        padding-left: 3px;
        padding-right: 3px;
    }

    .table-page-wrap {
        width: 100%;
        overflow: hidden;
        padding-right: 10px;
    }

    .content-main-box {
        display: block;
    }

    .content-box {
        display: block;
        width: 100%;
        transition: 0.35s;
    }

    .content-box-wrap {
        width: 100%;
        background-color: #fff;
        border: 1px solid #e5e5e5;
        position: relative;
    }

    .content-box-content {
        height: calc(100vh - 195px);
        max-height: calc(100vh - 195px);
        overflow: auto;
        overflow-y: scroll;
        width: 100%;
    }

    .content-box-header {
        height: 42px;
        overflow-x: hidden;
        overflow-y: scroll;
        width: 100%;
        position: relative;
    }

    .content-box-footer {
        height: 20px;
        overflow-x: hidden;
        overflow-y: hidden;
        width: 100%;
        position: relative;
        font-size: 12px;
        border-top: 1px solid #e5e5e5;
        color: #909399;
        padding: 0 10px;
    }

    .checkbox-col {
        width: 60px;
        text-align: center;
    }

    .with-expand .checkbox-col {
        width: 80px;
    }

    .checkbox-col .ivu-checkbox-wrapper {
        margin-right: 0;
    }

    .table-option-box {
        position: absolute;
        right: 0;
        top: 0;
        width: 40px;
        height: 40px;
        z-index: 2;
        padding-top: 10px;
        padding-right: 10px;
        vertical-align: middle;
        background-color: #fff;
    }

    .task-row-select {
        background-color: #ebf7ff !important;
    }

    .filtername {
        color: #009af4;
    }

    .opt-col-center {
        display: flex;
        align-items: center;
    }

    .table-footer-span {
        margin-right: 10px;
    }

    .task-table {
        table-layout: fixed !important;
    }

    .task-table th {
        padding: 10px !important;
    }

    .task-table td {
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

    .table-row-group-box {
        display: flex;
        align-items: center;
    }

    .totalspan {
        display: inline-block !important;
        min-width: 90px !important;
    }

    .pagespan {
        display: inline-block;
        width: 70px;
        text-align: center;
    }
</style>
<i18n>
    {
    "en": {
    "总计条数据":"total {0} items",
    "每页条":"{0} per page",
    "第页":"{0}/{1}",
    "批量操作":"Batch",
    "分组":"Group",
    "编辑":"Edit",
    "删除":"Delete",
    "复制":"Copy",
    "关联到其它对象":"Link to Others",
    "未设置":"none",
    "本页显示":"{0} items",
    "已完成":"finished",
    "未完成":"unfinished",
    "预计工时总计":"expect total time",
    "实际工时总计":"actual total time",
    "请选择要删除的数据":"Please select the items to be deleted",
    "确定要删除1":"Are you sure want to delete【{0}】and {1} {2} ？",
    "确定要删除2":"Are you sure want to delete【{0}】？",
    "请选择要编辑的数据":"Please select the items to be edited",
    "关联成功":"Success",
    "导出":"Import",
    "导入":"Export",
    "未分类":"none",
    "天":"Day",
    "树型视图":"Tree View",
    "平铺视图":"Tile View",
    "添加到父对象":"Add to parent"
    },
    "zh_CN": {
    "总计条数据":"总计 {0} 条数据",
    "每页条":"每页 {0} 条",
    "第页":"第 {0}/{1} 页",
    "批量操作":"批量操作",
    "分组":"分组",
    "编辑":"编辑",
    "删除":"删除",
    "复制":"复制",
    "未设置":"未设置",
    "关联到其它对象":"关联到其它对象",
    "本页显示":"本页显示{0}条数据",
    "已完成":"已完成",
    "未完成":"未完成",
    "预计工时总计":"预计工时总计",
    "实际工时总计":"实际工时总计",
    "请选择要删除的数据":"请选择要删除的数据",
    "确定要删除1":"确定要删除【{0}】等 {1}个{2}吗？",
    "确定要删除2":"确定要删除【{0}】吗？",
    "请选择要编辑的数据":"请选择要编辑的数据",
    "关联成功":"关联成功",
    "导出":"导出",
    "导入":"导入",
    "未分类":"未分类",
    "天":"天",
    "树型视图":"树型视图",
    "平铺视图":"平铺视图",
    "添加到父对象":"添加到父对象"
    }
    }
</i18n>
<template>
    <div v-hotkey="keymap" class="table-page-wrap" @keyup="shiftSelect=false" @keydown.shift="shiftSelect=true">

        <div v-if="queryInfo.fieldList" class="content-main-box">

            <div class="content-box">
                <Row class="table-info-bar" type="flex" align="middle">
                    <Col span="16" class="opt-col-center">
                        <span class="vcenter totalspan">{{$t('总计条数据',[pageQuery.total])}}</span>

                        <Poptip transfer placement="bottom" v-model="pageSizePopuptipVisible">
                            <span style="margin-left:5px;cursor:pointer" class="vcenter pagespan">{{$t('每页条',[pageQuery.pageSize])}}</span>
                            <div slot="content">
                                <PopupTipItem
                                    @click="selectPageSize(item)"
                                    :name="item+''"
                                    :selected="pageQuery.pageSize==item"
                                    v-for="item in [50,100,200]"
                                    :key="'ps'+item"/>
                            </div>
                        </Poptip>

                        <IconButton
                            :disabled="pageQuery.pageIndex==1"
                            :size="15"
                            @click="changePage(-1)"
                            icon="md-arrow-round-back"></IconButton>
                        <span class="vcenter">{{$t('第页',[pageQuery.pageIndex,pageQuery.totalPage])}}</span>
                        <IconButton
                            :disabled="pageQuery.pageIndex==pageQuery.totalPage"
                            :size="15"
                            @click="changePage(1)"
                            icon="md-arrow-round-forward"></IconButton>

                        <Dropdown
                            transfer-class-name="task-display-view"
                            trigger="click"
                            transfer
                            style="text-align:left;line-height:1"
                            @on-click="clickDisplayMenu">
                            <IconButton
                                size="16"
                                :icon="viewSetting.displayView === 2 ? 'ios-git-network' : 'logo-buffer'"
                                :title="$t(viewSetting.displayView === 2 ? '树型视图' : '平铺视图')"/>
                            <DropdownMenu slot="list">
                                <DropdownItem
                                    :selected="viewSetting.displayView === 1" style="width:150px" name="tile">
                                    <Icon type="logo-buffer"/>
                                    {{$t('平铺视图')}}
                                </DropdownItem>
                                <DropdownItem
                                    :selected="viewSetting.displayView === 2" style="width:150px" name="tree">
                                    <Icon type="ios-git-network"/>
                                    {{$t('树型视图')}}
                                </DropdownItem>
                            </DropdownMenu>
                        </Dropdown>
                        <Dropdown trigger="click" style="text-align:left;line-height:1" @on-click="clickOptionMenu" v-if="!projectDisabled">
                            <IconButton
                                size="16"
                                icon="ios-copy-outline"
                                :title="$t('批量操作')+getSelectedTaskCountText()"></IconButton>
                            <DropdownMenu slot="list">
                                <DropdownItem
                                    v-if="prjPerm('task_batch_edit_'+queryForm.objectType)"
                                    :disabled="getSelectedTaskCount()==0"
                                    style="width:150px"
                                    name="edit">{{$t('编辑')}}
                                </DropdownItem>
                                <DropdownItem
                                    v-if="prjPerm('task_batch_delete_'+queryForm.objectType)"
                                    :disabled="getSelectedTaskCount()==0"
                                    style="width:150px"
                                    name="delete">{{$t('删除')}}
                                </DropdownItem>
                                <DropdownItem
                                    v-if="prjPerm('task_batch_copy_'+queryForm.objectType)"
                                    :disabled="getSelectedTaskCount()==0"
                                    style="width:150px"
                                    name="copy">{{$t('复制')}}
                                </DropdownItem>
                                <DropdownItem
                                    :disabled="getSelectedTaskCount()==0" divided style="width:150px" name="rel">
                                    {{$t('关联到其它对象')}}
                                </DropdownItem>
                                <DropdownItem
                                    v-if="prjPerm('task_batch_edit_'+queryForm.objectType)"
                                    :disabled="getSelectedTaskCount()==0" style="width:150px" name="relParent">
                                    {{$t('添加到父对象')}}
                                </DropdownItem>
                            </DropdownMenu>
                        </Dropdown>
                    </Col>
                    <Col span="8" class="opt-col-center" style="flex-direction: row-reverse;">
                        <IconButton
                            v-if="prjPerm('task_export_'+queryForm.objectType)"
                            @click="showExportDialog"
                            icon="ios-redo"
                            :title="$t('导出')"></IconButton>
                        <IconButton
                            v-if="prjPerm('task_import_'+queryForm.objectType)&&!projectDisabled"
                            @click="showImportDialog"
                            icon="ios-undo"
                            :title="$t('导入')"></IconButton>

                        <Poptip transfer placement="bottom-end" v-model="groupPopuptipVisible">
                            <IconButton icon="md-apps" :title="$t('分组')"></IconButton>
                            <div slot="content">
                                <template v-for="item in tableFieldList">
                                    <PopupTipItem
                                        @click="clickGroupByItem(item)"
                                        v-if="isFilterField(item)"
                                        :name="item.name"
                                        :selected="viewSetting.groupByItem&&item.id==viewSetting.groupByItem.id"
                                        :key="'group'+item.id"/>
                                </template>
                            </div>
                        </Poptip>

                    </Col>
                </Row>
                <div class="content-box-wrap">
                    <div class="table-option-box">
                        <TaskTableHeaderSelect
                            @change="saveViewSetting()"
                            @on-reset-fields-order="onResetFieldsOrder"
                            @on-field-list-sort="onFieldListSort"
                            v-model="viewSetting.hideTableFieldList"
                            placement="bottom-end"
                            :field-list="tableFieldList">
                            <IconButton slot="popup-name" icon="ios-settings-outline"/>
                        </TaskTableHeaderSelect>
                    </div>
                    <div class="content-box-header scrollbox" ref="tableHeaderBox">
                        <table
                            v-if="tableFieldList"
                            class="task-table table-content table-line"
                            :class="{ 'with-expand':withExpandTask }">
                            <thead>
                            <tr>
                                <th class="checkbox-col">
                                    <Checkbox v-model="isSelectAll"></Checkbox>
                                </th>
                                <template v-for="item in tableFieldList">
                                    <TableHeader
                                        @change="sortChanged(item.field)"
                                        :sort="viewSetting.tableSort"
                                        :name="item.field"
                                        :key="'thead_'+item.id"
                                        v-if="item.isShow&&!isTableFieldHide(item)"
                                        :widthSetting="tableCellWidth"
                                        :field="item.field"
                                        :label="item.name"></TableHeader>
                                </template>
                                <th style="min-width:60px;"></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                    <div
                        ref="listViewBox"
                        class="content-box-content scrollbox scrollbox-bigbottom "
                        :class="{ 'with-expand':withExpandTask }"
                        @scroll="contentBoxScroll">
                        <table class="task-table table-content table-line">
                            <colgroup>
                                <col class="checkbox-col"/>
                                <template v-for="field in tableFieldList">
                                    <col
                                        :style="{width:tableCellWidth[field.field]+'px'}"
                                        :key="field.id"
                                        v-if="field.isShow&&!isTableFieldHide(field)"/>
                                </template>
                            </colgroup>
                            <tbody>
                            <template v-for="group in groupList">
                                <tr :key="'group_'+group.value" v-if="group.field!=null" class=" table-row-group">
                                    <td class="checkbox-col">
                                        <Checkbox
                                            @on-change="groupCheck" v-model="groupCheckList[group.value+'']"></Checkbox>
                                    </td>
                                    <td @click="group.show=!group.show" :colspan="columnCount">
                                        <div class="table-row-group-box">
                                            <span>
                                                <template v-if="group.value==null">{{$t('未设置')}}</template>
                                                <template v-if="group.value!=null">{{group.value}}</template>
                                            </span>
                                            <span class="table-row-group-count numberfont">{{group.count}}</span>
                                            <Icon
                                                v-if="group.show"
                                                class="table-row-group-icon"
                                                type="ios-arrow-down"></Icon>
                                            <Icon
                                                v-if="group.show==false"
                                                class="table-row-group-icon"
                                                type="ios-arrow-forward"></Icon>
                                        </div>
                                    </td>
                                </tr>
                                <template v-for="(row,idx) in tableData">
                                    <TaskListViewRow
                                        ref="task_row_list"
                                        :row="row"
                                        :group="group"
                                        v-show="row.rowShown"
                                        :tableCellWidth="tableCellWidth"
                                        :queryInfo="queryInfo"
                                        :viewSetting="viewSetting"
                                        :current-task-uuid="currentTaskUuid"
                                        @on-task-row-expand="(expand)=>onTaskRowExpand(row,idx,expand)"
                                        @on-checkbox-change="taskSelectChange(idx,row)"
                                        @on-task-show="showTaskInfo(row)"
                                        @on-rel-task-toggle="toggleRelTask(row)"
                                        :project-set-task="projectSetTask"
                                    />
                                    <TaskListViewRelTaskRow
                                        :row="row"
                                        :group="group"
                                        :queryInfo="queryInfo"
                                        :viewSetting="viewSetting"
                                        @on-rel-task-show="showTaskInfoDialog"/>
                                </template>
                            </template>
                            </tbody>
                        </table>
                    </div>
                    <div class="content-box-footer">
                        <span class="table-footer-span">
                            {{$t('本页显示',[tableFooterSummaryRow['name']])}}
                        </span>
                        <span v-if="hasField('statusName')" class="table-footer-span">
                            {{$t('已完成')}}：{{tableFooterSummaryRow['finished']}}
                        </span>
                        <span v-if="hasField('statusName')" class="table-footer-span">
                            {{$t('未完成')}}：{{tableFooterSummaryRow['unFinished']}}
                        </span>
                        <span class="table-footer-span" v-if="hasField('expectWorkTime')">
                            {{$t('预计工时总计')}}：{{tableFooterSummaryRow['expectWorkTime']}}h
                        </span>
                        <span class="table-footer-span" v-if="hasField('workTime')">
                            {{$t('实际工时总计')}}：{{tableFooterSummaryRow['workTime']}}h
                        </span>
                    </div>
                </div>
            </div>

        </div>

    </div>
</template>

<script>
    export default {
        mixins: [componentMixin],
        props: ['queryInfo', 'queryForm', 'taskId', "projectSetTask"],
        data() {
            return {
                title: 'TaskListView',
                pageQuery: {
                    total: 0,
                    pageIndex: 1,
                    pageSize: 50,
                    totalPage: 1,
                },
                viewSetting: {
                    tableFieldList: [],
                    hideTableFieldList: [],
                    groupByItem: null,
                    tableSort: {
                        name: null,
                        sort: null,
                    },
                    pageSize: 50,
                    displayView: 1,
                },
                currentTaskUuid: null,
                groupCheckList: {},
                shiftSelect: false,
                isSelectAll: false,
                tableData: [],
                groupList: [{}],
                groupField: null,
                tableCellWidth: {},
                tableFooterSummaryRow: {},
                showRelTaskListMap: {},
                pageSizePopuptipVisible: false,
                groupPopuptipVisible: false,
                tableFieldList: [],
                relMode: 'otherObject',
                expandedTaskTreeIdList: []  //前后顺序展开节点
            };
        },
        computed: {
            keymap() {
                return {
                    'up': this.openPriorTask,
                    'down': this.openNextTask,
                };
            },
            withExpandTask() {
                return this.viewSetting && this.viewSetting.displayView === 2;
            },
            columnCount() {
                const fields = this.queryInfo.fieldList.filter(field => {
                    return field.isShow && !this.isTableFieldHide(field);
                });
                return fields.length;
            },
        },
        watch: {
            isSelectAll: function (val) {
                this.tableData.map(item => {
                    item.isSelect = val;
                });
            },
            queryInfo: {
                immediate: true,
                handler(val) {
                    if (val) {
                        this.computeTableFields();
                        this.computeTableStyle();
                    }
                }
            },
            taskId: {
                immediate: true,
                handler(val) {
                    this.currentTaskUuid = val;
                },
            },
        },
        created() {
            this.loadViewSetting();
            // 当在其他视图切换后，需要把视图的排序内容加载过来
            this.checkViewSetting();
        },
        mounted() {
            this.computeTableFields();
            this.computeTableStyle();
            this.pageQuery.pageIndex = 1;
        },
        methods: {
            loadData(reload) {
                if (reload) {
                    this.pageQuery.pageIndex = 1;
                }
                this.loadViewSetting();
                this.loadTableData();
            },
            checkViewSetting() {
                let hasChange = false;
                Object.keys(this.queryForm).filter(key => key.endsWith('Sort')).forEach(key => {
                    if (key === 'customFieldsSort') {
                        return;
                    }
                    const sort = this.queryForm[key];
                    if (sort > 0) {
                        hasChange = true;
                        this.viewSetting.tableSort.name = key.replace('Sort', '');
                        this.viewSetting.tableSort.sort = sort;
                    }
                    // 重置掉queryForm上已有排序,重新计算排序字段,查询时会重新设置queryForm的排序
                    this.queryForm[key] = null;
                });
                // 如果从queryForm未找到需要增加的排序字段,则重置当前viewSetting排序设置
                if (!hasChange) {
                    this.viewSetting.tableSort.name = null;
                    this.viewSetting.tableSort.sort = null;
                }
                this.saveViewSetting();
            },
            loadTableData() {
                this.queryForm.pageIndex = this.pageQuery.pageIndex;
                this.queryForm.pageSize = this.pageQuery.pageSize;
                //
                const query = Object.assign({}, this.queryForm);
                query.customFieldsSort = {};
                query.parentId = this.pageQuery.parentId;
                if (this.viewSetting.tableSort.name != null) {
                    if (this.viewSetting.tableSort.name.indexOf('field_') !== -1) {
                        query.customFieldsSort[this.viewSetting.tableSort.name] = this.viewSetting.tableSort.sort;
                    } else {
                        query[this.viewSetting.tableSort.name + 'Sort'] = this.viewSetting.tableSort.sort;
                        this.$set(this.queryForm, this.viewSetting.tableSort.name + 'Sort', this.viewSetting.tableSort.sort);
                    }
                }
                // 加载已选择ids
                const selectIds = this.getSelectedTaskIds();
                app.invoke('BizAction.getTaskInfoListAndAssociateTasks', [app.token, query], info => {
                    let isSelectAll = info.list.length > 0;
                    info.list.forEach((item, index) => {
                        item.isSelect = selectIds.indexOf(item.id) > -1;
                        if (item.isSelect !== true) {
                            isSelectAll = false;
                        }
                        item.rowNum = `${index + 1}`;
                        item.rowLevel = 0;
                        item.rowExpand = false;
                        item.rowShown = true;
                        item.showRelTaskList = this.showRelTaskListMap[item.id] === true;
                        if (item.customFields) {
                            Object.keys(item.customFields).forEach(key => {
                                item[key] = item.customFields[key];
                            });
                        }
                        if (item.associatedList) {
                            var finishedCount = 0;
                            item.associatedList.forEach(t => {
                                if (t.isFinish) {
                                    finishedCount++;
                                }
                            });
                            item.associatedFinishedCount = finishedCount;
                        }
                    });
                    this.isSelectAll = isSelectAll;
                    this.$nextTick(() => {
                        this.tableData = info.list;
                        this.computeSummaryInfo();
                        this.pageQuery.total = info.count;
                        if (info.count === 0) {
                            this.pageQuery.totalPage = 1;
                        } else {
                            this.pageQuery.totalPage = Math.ceil(info.count / this.pageQuery.pageSize);
                        }
                        if (this.viewSetting.groupByItem) {
                            if (this.viewSetting.groupByItem.isSystemField) {
                                this.groupBy(this.viewSetting.groupByItem.field);
                            } else {
                                this.groupBy('field_' + this.viewSetting.groupByItem.id);
                            }
                        }

                        //如果是树形图，需要在子任务编辑后仍然保持树的打开状态
                        if (this.viewSetting.displayView === 2 && Array.isArray(this.expandedTaskTreeIdList)) {
                            if (this.expandedTaskTreeIdList.length === 0) {
                                return;
                            }
                            this.expandNode(this.expandedTaskTreeIdList[0]);
                        }
                    });
                });
            },
            computeSummaryInfo() {
                var summary = {
                    name: 0,
                    expectWorkTime: 0,
                    workTime: 0,
                    finished: 0,
                    unFinished: 0,
                };
                this.tableData.forEach(item => {
                    summary.name++;
                    summary.expectWorkTime += item.expectWorkTime;
                    summary.workTime += item.workTime;
                    if (item.isFinish) {
                        summary.finished++;
                    } else {
                        summary.unFinished++;
                    }
                });
                this.tableFooterSummaryRow = summary;

            },
            openPriorTask() {
                this.openDeltaTask(-1);
            },
            openNextTask() {
                this.openDeltaTask(1);
            },
            openDeltaTask(delta) {
                if (this.currentTaskUuid == null) {
                    return;
                }
                if (app.taskEditing == true) {
                    return;
                }
                var showIndex = 0;
                for (var i = 0; i < this.tableData.length; i++) {
                    var t = this.tableData[i];
                    if (t.uuid == this.currentTaskUuid) {
                        showIndex = i;
                    }
                }
                showIndex += delta;
                if (showIndex < 0 || showIndex >= this.tableData.length) {
                    return;
                }
                this.showTaskInfo(this.tableData[showIndex]);
            },
            isFilterField(item) {
                if (item.isShow == false) {
                    return false;
                }
                if (item.isSystemField) {
                    if (item.field == 'createAccountName'
                        || item.field == 'iterationName'
                        || item.field == 'statusName'
                        || item.field == 'priorityName'
                        || item.field == 'releaseName'
                        || item.field == 'subSystemName') {
                        return true;
                    }
                } else {
                    if (item.type == 4) {
                        return true;
                    }
                }
                return false;
            },

            hasField(name) {
                if (this.queryInfo.fieldList) {
                    for (var i = 0; i < this.queryInfo.fieldList.length; i++) {
                        var f = this.queryInfo.fieldList[i];
                        if (f.field == name) {
                            return true;
                        }
                    }
                }
                return false;
            },
            isTableFieldHide(field) {
                if (this.viewSetting.hideTableFieldList == null) {
                    return false;
                }
                for (var i = 0; i < this.viewSetting.hideTableFieldList.length; i++) {
                    if (this.viewSetting.hideTableFieldList[i] == field.id) {
                        return true;
                    }
                }
                return false;
            },
            onResetFieldsOrder() {
                if (this.queryInfo.fieldList == null) {
                    this.tableFieldList = [];
                    return;
                }
                const tableFieldList = [];
                this.queryInfo.fieldList.forEach(item => {
                    tableFieldList.push({
                        ...item,
                    });
                });
                this.viewSetting.tableFieldList = tableFieldList;
                this.tableFieldList = tableFieldList;
                this.saveViewSetting();
            },
            onFieldListSort(val) {
                this.tableFieldList = val;
                this.viewSetting.tableFieldList = this.tableFieldList;
                this.saveViewSetting();
            },
            saveViewSetting() {
                var objectType = this.queryForm.objectType;
                var projectId = this.queryForm.projectId;
                this.viewSetting.pageSize = this.pageQuery.pageSize;
                app.saveObject('TaskPage.viewSetting' + objectType + '.' + projectId, this.viewSetting);
                this.computeTableStyle();
            },
            loadViewSetting() {
                var objectType = this.queryForm.objectType;
                var projectId = this.queryForm.projectId;
                var vc = app.loadObject('TaskPage.viewSetting' + objectType + '.' + projectId);
                if (vc != null) {
                    if (vc.tableSort == null) {
                        vc.tableSort = {
                            name: null,
                            sort: null,
                        };
                    }
                    this.viewSetting = vc;
                    if (vc.pageSize) {
                        this.pageQuery.pageSize = vc.pageSize;
                    }
                    if (vc.displayView === 2) {
                        // 不查询子任务使用展开形式展示子任务
                        this.pageQuery.parentId = 0;
                    } else {
                        this.pageQuery.parentId = undefined;
                    }
                }
            },
            changePage(delta) {
                var t = this.pageQuery.pageIndex + delta;
                if (t <= 0 || t > this.pageQuery.totalPage) {
                    return;
                }
                this.pageQuery.pageIndex = t;
                this.$refs.listViewBox.scrollTop = 0;
                this.loadTableData();
            },
            computeTableFields() {
                if (this.queryInfo.fieldList == null) {
                    this.tableFieldList = [];
                    return;
                }
                let tableFieldList = [];
                if (Array.isArray(this.viewSetting.tableFieldList) && this.viewSetting.tableFieldList.length > 0) {
                    // 检查返回的字段列表和本地缓存列表是否一致，不一致则重新计算字段了表
                    const queryInfoFieldIndex = this.queryInfo.fieldList.findIndex(field => {
                        return this.viewSetting.tableFieldList.findIndex(vfield => vfield.field === field.field) === -1;
                    });
                    if (queryInfoFieldIndex === -1) {
                        tableFieldList = [...this.viewSetting.tableFieldList];
                    }

                    // 更新字段名
                    tableFieldList.forEach(item => {
                        const fieldObj = this.queryInfo.fieldList.find(
                            field => field.id === item.id
                        );
                        if (fieldObj) {
                            item.name = fieldObj.name;
                        }
                    });
                }
                if (tableFieldList.length === 0) {
                    this.queryInfo.fieldList.forEach(item => {
                        tableFieldList.push({
                            ...item,
                        });
                    });
                } else {
                    tableFieldList = tableFieldList.filter(item => {
                        return this.queryInfo.fieldList.findIndex(field => field.id === item.id) > -1;
                    });
                }
                this.viewSetting.tableFieldList = tableFieldList;
                this.tableFieldList = tableFieldList;

                this.loadViewSetting();
                this.checkViewSetting();
                // this.saveViewSetting();
            },
            computeTableStyle() {
                if (!Array.isArray(this.tableFieldList) || this.tableFieldList.length === 0) {
                    return;
                }
                let cellWidth = {};
                this.tableFieldList.forEach(item => {
                    if (item.isShow && !this.isTableFieldHide(item)) {
                        cellWidth[item.field] = this.getWidth(item);
                    }
                });
                this.tableCellWidth = cellWidth;
            },
            getWidth(item) {
                if (item.field == 'name') {
                    return 350;
                }
                if (item.field == 'statusName') {
                    return 120;
                }
                if (item.field == 'priorityName') {
                    return 100;
                }
                if (item.field == 'ownerAccountName') {
                    return 150;
                }
                if (item.field == 'createAccountName') {
                    return 100;
                }
                if (item.field == 'startDate') {
                    return 150;
                }
                if (item.field == 'endDate') {
                    return 150;
                }
                if (item.field == 'expectEndDate') {
                    return 150;
                }
                if (item.field == 'startDays' || item.field == 'endDays') {
                    return 100;
                }
                if (item.field == 'workTime' || item.field == 'expectWorkTime') {
                    return 100;
                }
                if (item.field == 'categoryIdList') {
                    return 200;
                }
                return 140;
            },
            contentBoxScroll(e) {
                if (e.target.scrollLeft != this.$refs.tableHeaderBox.scrollLeft) {
                    this.$refs.tableHeaderBox.scrollLeft = e.target.scrollLeft;
                }
            },
            sortChanged(fieldName) {
                this.$nextTick(() => {
                    //设置状态到queryForm上
                    this.$set(this.queryForm, fieldName + 'Sort', this.viewSetting.tableSort.sort);
                    this.saveViewSetting();
                    this.pageQuery.pageIndex = 1;
                    this.loadTableData();
                });
            },
            clickGroupByItem(item) {
                this.groupPopuptipVisible = false;
                if (this.viewSetting.groupByItem != null && this.viewSetting.groupByItem.id == item.id) {
                    this.viewSetting.groupByItem = null;
                    this.groupBy(null);
                } else {
                    this.viewSetting.groupByItem = item;
                    if (item.isSystemField) {
                        this.groupBy(item.field);
                    } else {
                        this.groupBy('field_' + item.id);
                    }
                }
                this.saveViewSetting();

            },
            groupBy(field) {
                this.groupField = field;
                if (field == null) {
                    this.groupList = [{}];
                    return;
                }
                var groupData = {};
                this.tableData.map(item => {
                    var fieldValue = item[field];
                    if (field.indexOf('field_') != -1) {
                        fieldValue = item.customFields[field];
                    }
                    //console.log(field,fieldValue)
                    var group = groupData[fieldValue];
                    if (group == null) {
                        group = {field: field, value: fieldValue, count: 0, show: true};
                        groupData[fieldValue] = group;
                    }
                    group.count++;
                });
                //
                var groupList = [];
                for (var k in groupData) {
                    groupList.push(groupData[k]);
                }
                this.groupList = groupList;
            },

            showCopyTaskDialog() {
                var itemList = [];
                this.tableData.map(item => {
                    if (item.isSelect) {
                        itemList.push(item);
                    }
                });
                if (itemList.length == 0) {
                    app.toast(this.$t('请选择要复制的数据'));
                    return;
                }

                app.showDialog(TaskCopyDialog, {
                    itemList: itemList,
                    objectType: this.queryForm.objectType,
                    projectId: this.queryForm.projectId,
                    iterationId: this.queryForm.iterationId,
                });
            },
            batchDelete() {
                var itemList = [];
                var itemName = null;
                this.tableData.map(item => {
                    if (item.isSelect) {
                        if (itemName == null) {
                            itemName = item.name;
                        }
                        itemList.push(item);
                    }
                });
                if (itemList.length == 0) {
                    app.toast(this.$t('请选择要删除的数据'));
                    return;
                }
                var objectName = app.dataDictValue('Task.objectType', this.queryForm.objectType);
                var message = this.$t('确定要删除1', [itemName, itemList.length, objectName]);
                if (itemList.length == 1) {
                    message = this.$t('确定要删除2', [itemName]);
                }
                app.confirm(message, () => {
                    this.confirmDelete(itemList);
                });
            },
            confirmDelete(itemList) {
                var objectName = app.dataDictValue('Task.objectType', this.queryForm.objectType);
                app.showDialog(MultiOperateDialog, {
                    title: this.$t('删除') + objectName,
                    runCallback: this.deleteAction,
                    finishCallback: this.finishDeleteRun,
                    itemList: itemList,
                });
            },
            finishDeleteRun() {
                app.postMessage('task.edit');
            },
            deleteAction(item, success, error) {
                app.invoke('BizAction.batchDeleteTaskList', [app.token, [item.id]], (info) => {
                    success();
                }, error);
            },
            showEditTaskDialog() {
                var itemList = [];
                this.tableData.map(item => {
                    if (item.isSelect) {
                        itemList.push(item);
                    }
                });
                if (itemList.length == 0) {
                    app.toast(this.$t('请选择要编辑的数据'));
                    return;
                }
                app.showDialog(TaskEditDialog, {
                    itemList: itemList,
                    objectType: this.queryForm.objectType,
                    projectId: this.queryForm.projectId,
                });
            },

            showTaskInfo(item) {
                this.currentTaskUuid = item.uuid;
                app.loadPage('task' + this.queryForm.objectType + '?id=' + item.uuid);
                // app.loadPage(`task${this.queryForm.objectType}?id=${item.uuid}&isProjectSet=${this.projectSetTask}&projectUuid=${item.projectUuid}`);
            },
            showTaskInfoDialog(item) {
                app.showDialog(TaskDialog, {
                    taskId: item.uuid,
                });
            },
            groupCheck() {
                for (var k in this.groupCheckList) {
                    var groupChecked = this.groupCheckList[k];
                    var groupValue = k;
                    if (groupValue == 'undefined') {
                        groupValue = null;
                    }
                    for (var i = 0; i < this.tableData.length; i++) {
                        var t = this.tableData[i];
                        if (t[this.groupField] == groupValue) {
                            t.isSelect = groupChecked;
                        }
                    }
                }
            },
            getSelectedTaskIds() {
                var ids = [];
                this.tableData.map(item => {
                    if (item.isSelect) {
                        ids.push(item.id);
                    }
                });
                return ids;
            },
            getSelectedTaskCount() {
                var cnt = 0;
                this.tableData.map(item => {
                    if (item.isSelect) {
                        cnt++;
                    }
                });
                return cnt;
            },
            getSelectedTaskCountText() {
                var t = this.getSelectedTaskCount();
                if (t > 0) {
                    return t;
                }
                return '';
            },
            onTaskRowExpand(row, idx, expand) {
                row.level = row.level || 0;
                row.rowExpand = expand === true;
                if (!row.rowExpand) {
                    if (Array.isArray(row.children)) {
                        this.toggleTaskRowChildFold(row, false);
                    }
                    return;
                }
                if (Array.isArray(row.children) && row.children.length > 0) {
                    row.children.forEach((item, index) => {
                        const child = this.tableData.find(drow => drow.id === item.id);
                        if (child) {
                            child.rowShown = true;
                            return;
                        }
                        item.isSelect = this.isSelectAll;
                        this.tableData.splice(idx + index + 1, 0, item);
                    });
                    return;
                }
                this.$set(row, 'loading', true);
                app.invoke('BizAction.getSubTaskInfoList', [app.token, row.uuid], list => {
                    this.compTaskRowChild(row, list, row.level, row.isSelect);
                    this.$set(row, 'children', row.children);
                    this.$set(row, 'loading', false, false);
                    if (!Array.isArray(row.children)) {
                        return;
                    }
                    row.children.forEach((item, index) => {
                        item["level"] = (row.level) + 1;
                        this.tableData.splice(idx + index + 1, 0, item);
                    });
                    expand ? this.setExpandSetting(row, row.uuid) : null;
                    this.expandNode(this.getNextExpandTaskId(row.id));
                }, () => {
                    this.$set(row, 'loading', false);
                });
            },
            toggleTaskRowChildFold(row, show, expand) {
                if (row == null || !Array.isArray(row.children)) {
                    return;
                }
                row.children.forEach(child => {
                    child.rowShown = show;
                    child.rowExpand = expand;
                    this.toggleTaskRowChildFold(child, show, expand);
                });
            },
            compTaskRowChild(parentRow, list, level, rowSelected) {
                if (parentRow == null || !Array.isArray(list)) {
                    return null;
                }
                const children = list.filter(item => item.parentId === parentRow.id);
                children.forEach((child, index) => {
                    child.rowNum = parentRow.rowNum + '.' + (index + 1);
                    child.rowExpand = false;
                    child.isSelect = rowSelected;
                    this.compTaskRowChild(child, list, level + 1);
                });
                parentRow.rowLevel = level;
                parentRow.loading = false;
                parentRow.rowShown = true;
                parentRow.children = children;
            },
            taskSelectChange(idx, item) {
                if (!this.shiftSelect) {
                    return;
                }
                if (item.isSelect == false) {
                    return;
                }
                if (this.groupField != null) {
                    return;
                }
                var startIdx = true;
                for (var i = 0; i < this.tableData.length; i++) {
                    var t = this.tableData[i];
                    if (t.isSelect) {
                        startIdx = i;
                        break;
                    }
                }
                for (var i = 0; i < this.tableData.length; i++) {
                    if (i >= startIdx && i <= idx) {
                        this.tableData[i].isSelect = true;
                    }
                }
            },
            showImportDialog() {
                app.showDialog(TaskImportDialog, {
                    objectType: this.queryForm.objectType,
                    projectId: this.queryForm.projectId,
                    fieldList: this.queryInfo.fieldList,
                });
            },
            showExportDialog() {
                app.showDialog(TaskExportDialog, {
                    fieldList: this.queryInfo.fieldList,
                    queryItem: this.queryForm,
                });
            },
            showRelDialog() {
                app.showDialog(TaskSelectDialog, {
                    projectId: this.queryForm.projectId,
                    iterationId: this.queryForm.iterationId,
                    objectType: this.queryForm.objectType,
                    singleSelect: this.relMode === 'parentObject',
                    callback: this.addRelToObject,
                });
            },
            addRelToObject(list) {
                var fromIds = [];
                this.tableData.map(item => {
                    if (item.isSelect) {
                        fromIds.push(item.id);
                    }
                });
                var toIds = list.map(item => {
                    return item.id;
                });
                app.invoke(this.relMode === 'parentObject' ? 'BizAction.batchAddToParentTask' : 'BizAction.batchAddTaskAssociatedList', [app.token, fromIds, toIds], (info) => {
                    app.toast(this.$t('关联成功'));
                    // window.location.reload();
                    this.tableData = [];
                    this.loadData(false);
                });
            },
            clickDisplayMenu(name) {
                this.viewSetting.displayView = name === 'tree' ? 2 : 1;
                if (this.viewSetting.displayView === 2) {
                    // 不查询子任务使用展开形式展示子任务
                    this.pageQuery.parentId = 0;
                } else {
                    this.pageQuery.parentId = undefined;
                    this.expandedTaskTreeIdList = [];
                }
                this.saveViewSetting();
                this.pageQuery.pageIndex = 1;
                this.loadTableData();
            },
            clickOptionMenu(name) {
                if (this.getSelectedTaskCount() == 0) {
                    return;
                }
                if (name == 'edit') {
                    this.showEditTaskDialog();
                }
                if (name == 'copy') {
                    this.showCopyTaskDialog();
                }
                if (name == 'delete') {
                    this.batchDelete();
                }
                if (name == 'rel') {
                    this.relMode = "otherObject";
                    this.showRelDialog();
                }
                //批量添加到父任务
                if (name == 'relParent') {
                    this.relMode = "parentObject";
                    this.showRelDialog();
                }
            },
            toggleRelTask(item) {
                item.showRelTaskList = !item.showRelTaskList;
                this.showRelTaskListMap[item.id] = item.showRelTaskList;
            },
            selectPageSize(size) {
                this.pageSizePopuptipVisible = false;
                this.pageQuery.pageSize = size;
                this.pageQuery.pageIndex = 1;
                this.saveViewSetting();
                this.loadTableData();
            },
            getNextExpandTaskId(currentExpandTaskId){
                var nextExpandTaskId = 0;
                if (!Array.isEmpty(this.expandedTaskTreeIdList)) {
                    for (let i = 0; i < this.expandedTaskTreeIdList.length; i++) {
                        if (currentExpandTaskId == this.expandedTaskTreeIdList[i]) {
                            if (i + 1 < this.expandedTaskTreeIdList.length) {
                                nextExpandTaskId = this.expandedTaskTreeIdList[i + 1];
                            }
                            break;
                        }
                    }
                }
                return nextExpandTaskId;
            },
            expandNode(readyToExpandTaskId) {
                // console.log("-----------> tree keep expand ",readyToExpandTaskId,this.expandedTaskTreeIdList)
                if (readyToExpandTaskId === 0) {
                    return;
                }
                for (let idx = 0; idx < this.tableData.length; idx++) {
                    if (this.tableData[idx].id == readyToExpandTaskId) {
                        if(this.tableData[idx].rowExpand){
                            break;
                        }
                        setTimeout(() => {
                            this.$refs.task_row_list[idx].triggerChildExpand();
                        }, 100)
                        break;
                    }
                }
            },
            setExpandSetting(task, uuid) {
                // console.log("---->set expand id list",task.id,this.expandedTaskTreeIdList)
                //层级的关联
                if (!Array.isEmpty(this.expandedTaskTreeIdList)) {
                    var arr = [];
                    var unshift = false;
                    for (let i = this.expandedTaskTreeIdList.length - 1; i >= 0; i--) {
                        if (task.parentId == this.expandedTaskTreeIdList[i]||unshift) {
                            unshift =true;
                            arr.unshift(this.expandedTaskTreeIdList[i]);
                        }
                        if(task.id==this.expandedTaskTreeIdList[i]){
                            return;
                        }
                    }
                    arr.push(task.id);
                    this.expandedTaskTreeIdList = arr;
                } else {
                    this.expandedTaskTreeIdList.push(task.id);
                }
            }
        },
    };
</script>
