<style scoped>

    .kanban-wrap {
        width: 100%;
        overflow-y: auto;
        position: relative;
    }

    .kanban-container {
        padding: 30px 15px 0 15px;
    }

    .kanban-col {
        display: inline-block;
        background-color: #e4e4e4;
        border-radius: 5px;
        padding: 10px;
        width: 270px;
        height: calc(100vh - 138px);
        margin-right: 15px;
        padding-top: 0;
        padding-right: 0;
        position: relative;
    }

    .kanban-title {
        font-size: 15px;
        color: #333;
        font-weight: bold;
        padding: 10px;

    }

    .kanban-title-label {
        max-width: 180px;
        display: inline-block;
    }

    .task-item {
        background: rgba(255, 255, 255, 1);
        box-shadow: 0px 2px 6px 0px rgba(212, 212, 212, 0.45);
        border-radius: 4px;
        border: 1px solid #eee;
        padding: 15px;
        margin-top: 15px;
        width: 250px;
        cursor: pointer;
    }

    .task-item-dragging {
        border: 1px dashed #eee;
    }

    .task-item:first-child {
        margin-top: 10px;
    }

    .task-time {
        color: #666;
        margin-top: 10px;
    }

    .task-progress {
        color: #666;
        margin-top: 10px;
    }

    .task-name {
        font-size: 14px;
        color: #333;
        vertical-align: middle;
    }

    .task-name-finish {
        color: gray;
        text-decoration: line-through;
    }

    .task-item-priority {
        border-left: 4px solid #ff9c3b;
        border-top: none;
        border-right: none;
        border-bottom: none;
    }

    .task-item-owner {
        width: 100%;
        display: inline-flex;
        justify-content: space-between;
        margin-bottom: 5px;
        color: #333;
    }

    .task-name {
        text-overflow: ellipsis;
        overflow: hidden;
        white-space: nowrap;
        margin-left: 5px;
    }

    .task-item-piece {
        padding: 2px 3px;
        background-color: #bbbbbb;
        border-radius: 3px;
        margin-right: 5px;
    }

    .kanban-col-wrap {
        height: 100%;
        display: flex;
        flex-direction: column;
    }

    .kanban-col-itembox {
        flex: 1;
        overflow: hidden;
    }

    .kanban-col-itembox:hover {
        overflow: auto;
    }

    .kanban-opt {
        float: right;
    }

    .category-line {
        margin-top: 7px;
    }

    .task-serialno {
        color: #999;
        font-weight: bold;
    }

    .stat-info-box {
        position: absolute;
        top: 0;
        left: 15px;
        height: 30px;
        display: flex;
        align-items: center;
        color: #999;
        z-index: 10;
    }


    .opt-divider {
        width: 1px;
        display: inline-block;
        background: #A7A7A7;
        height: 10px;
        line-height: 10px;
        margin-left: 10px;
        margin-right: 5px;
        vertical-align: middle;
    }
</style>
<i18n>
    {
    "en": {
    "总计条数据":"total {0} items",
    "每页条":"{0} per page",
    "第页":"{0}/{1}",
    "创建": "Create",
    "编辑本列":"Edit",
    "删除本列":"Delete",
    "复制本列":"Copy",
    "未分类":"none",
    "请选择要复制的数据":"Please select the items to be copied",
    "请选择要编辑的数据":"Select the items you want to edit",
    "确定要删除1":"Are you sure want to delete【{0}】and {1} {2} ？",
    "确定要删除2":"Are you sure want to delete【{0}】？",
    "删除":"Delete",
    "责任人升序":"Owner asc",
    "责任人降序":"Owner desc",
    "状态升序":"Status asc",
    "状态降序":"Status desc",
    "优先级升序":"Priority asc",
    "优先级降序":"Priority desc",
    "开始时间升序":"Start asc",
    "开始时间降序":"Start desc",
    "结束时间升序":"End asc",
    "结束时间降序":"End desc",
    "重置排序":"Reset sort",
    "全局应用":"Set as global sort"
    },
    "zh_CN": {
    "总计条数据":"总计 {0} 条数据",
    "每页条":"每页 {0} 条",
    "第页":"第 {0}/{1} 页",
    "创建": "创建",
    "编辑本列":"编辑本列",
    "删除本列":"删除本列",
    "复制本列":"复制本列",
    "未分类":"未分类",
    "请选择要复制的数据":"请选择要复制的数据",
    "请选择要编辑的数据":"请选择要编辑的数据",
    "确定要删除1":"确定要删除【{0}】等 {1}个{2}吗？",
    "确定要删除2":"确定要删除【{0}】吗？",
    "删除":"删除",
    "责任人升序":"责任人升序",
    "责任人降序":"责任人降序",
    "状态升序":"状态升序",
    "状态降序":"状态降序",
    "优先级升序":"优先级升序",
    "优先级降序":"优先级降序",
    "开始时间升序":"开始时间升序",
    "开始时间降序":"开始时间降序",
    "结束时间升序":"结束时间升序",
    "结束时间降序":"结束时间降序",
    "重置排序":"重置排序",
    "全局应用":"设为全局排序"
    }
    }
</i18n>
<template>
    <div class="kanban-wrap scrollbox scrollbox-bigbottom">
        <div class="stat-info-box">
            <div style="">
                {{$t('总计条数据',[pageQuery.total])}}
            </div>
            <Poptip transfer placement="bottom" v-model="pageSizePopuptipVisible">
                <span
                    style="margin-left:5px;cursor:pointer"
                    class="vcenter pagespan">{{$t('每页条',[pageQuery.pageSize])}}</span>
                <div slot="content">
                    <PopupTipItem
                        @click="selectPageSize(item)"
                        :name="item+''"
                        :selected="pageQuery.pageSize==item"
                        v-for="item in [500,1000,1500]"
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
            <span class="opt-divider"></span>
            <Poptip transfer placement="bottom-end" v-model="sortPopuptipVisible">
                <IconButton :size="15" icon="md-list"></IconButton>
                <div slot="content">
                    <template v-for="item in sortConfigList">
                        <PopupTipItem
                            @click="setSortType(item.name,item.sort)"
                            v-if="hasField(item.name)"
                            :name="item.title"
                            :selected="viewSetting.tableSort.sort==item.sort&&viewSetting.tableSort.name==item.name"
                            :key="item.id+'sort'"/>
                    </template>
                </div>
            </Poptip>
            <span class="opt-divider"></span>
            <Icon style="cursor: pointer;" @click="clearSortCache" :title="$t('重置排序')" type="ios-trash"
                  size="16"></Icon>
            <span v-if="showSortBtn&&prjPerm('project_edit_config')" class="opt-divider"></span>
            <Icon v-if="showSortBtn&&prjPerm('project_edit_config')" style="cursor: pointer;" @click="saveGlobalSort"
                  :title="$t('全局应用')" type="ios-apps"
                  size="16"></Icon>
        </div>
        <div class="kanban-container" :style="'width:'+containerWidth">
            <template v-for="t in columnList">
                <div class="kanban-col " v-if="statusTableData[t.id]" :key="'status_'+t.id">
                    <div class="kanban-col-wrap">
                        <div class="kanban-title">
                            <span class="vcenter  text-no-wrap kanban-title-label">{{t.name}}（{{statusTableData[t.id].length}}）
                          </span>

                            <span class="kanban-opt" v-if="!projectFinishDisabled">
                            <IconButton
                                v-if="prjPerm('task_edit_'+queryForm.objectType)"
                                @click="showCreateDialog(t.id)"
                                icon="md-add"
                                :tips="$t('创建')"></IconButton>
                             <Dropdown
                                 v-if="prjPerm(['task_batch_edit_'+queryForm.objectType,'task_batch_delete_'+queryForm.objectType,'task_batch_copy_'+queryForm.objectType])"
                                 trigger="click"
                                 style="text-align:left">
                              <IconButton icon="ios-arrow-down"></IconButton>
                              <DropdownMenu slot="list">
                                    <DropdownItem
                                        v-if="prjPerm('task_batch_edit_'+queryForm.objectType)"
                                        :disabled="statusTableData[t.id].length==0"
                                        @click.native="batchEdit(t.id)"
                                        name="edit"
                                        style="width:150px">{{$t('编辑本列')}}</DropdownItem>
                                    <DropdownItem
                                        v-if="prjPerm('task_batch_delete_'+queryForm.objectType)"
                                        :disabled="statusTableData[t.id].length==0"
                                        @click.native="batchDelete(t.id)"
                                        name="delete"
                                        style="width:150px">{{$t('删除本列')}}</DropdownItem>
                                    <DropdownItem
                                        v-if="prjPerm('task_batch_copy_'+queryForm.objectType)"
                                        :disabled="statusTableData[t.id].length==0"
                                        @click.native="batchCopy(t.id)"
                                        name="copy"
                                        style="width:150px">{{$t('复制本列')}}</DropdownItem>
                              </DropdownMenu>
                          </Dropdown>

                          </span>

                        </div>
                        <div class="kanban-col-itembox scrollbox">
                            <draggable
                                style="min-height:90%"
                                v-model="statusTableData[t.id]"
                                @start="dragStart"
                                @end="dragEnd"
                                @change="dragChanged($event,t)"
                                :options="{draggable:'.task-item',group:'people',chosenClass:'task-item-dragging',dragClass:'task-item-dragging'}">
                                <div
                                    @click="showTaskInfo(item)"
                                    class="task-item task-item-priority"
                                    :style="{borderColor:item.priorityColor}"
                                    v-for="item in statusTableData[t.id]"
                                    :key="'task_'+item.id"
                                    :title="item.name">
                                    <div class="task-item-owner">
                                        <TaskOwnerView :value="item"/>
                                        <TaskPriority :color="item.priorityColor" :label="item.priorityName"/>
                                    </div>
                                    <div class="task-name" :class="{'task-name-finish':item.isFinish}">
                                        <span class="task-serialno numberfont">#{{item.serialNo}}</span>
                                        <span class="task-name">{{item.name}}</span>
                                    </div>
                                    <Row
                                        v-if="item.startDate||item.endDate">
                                        <Col span="24">
                                            <TaskPeriod :start="item.startDate" :end="item.endDate"/>
                                        </Col>
                                    </Row>

                                    <Row
                                        class="task-progress"
                                        v-if="item.isFinish==false&&hasField('progress')&&item.progress>0">
                                        <Col span="24">
                                            <Progress :percent="item.progress" :stroke-width="5"></Progress>
                                        </Col>
                                    </Row>
                                    <div v-if="item.categoryIdList&&item.categoryIdList.length>0" class="category-line">
                                        <CategoryLabel
                                            :placeholder="$t('未分类')"
                                            :category-list="queryInfo.categoryList"
                                            :value="item.categoryIdList"></CategoryLabel>
                                    </div>
                                    <Row>
                                        <Col span="24">
                                            <span class="task-item-piece" v-if="item.workLoad&&item.workLoad>0"
                                                  title="工作量">
                                                <Icon type="ios-analytics"/>
                                                <span>{{item.workLoad}}</span>
                                            </span>
                                            <span class="task-item-piece" v-if="item.subTaskCount&&item.subTaskCount>0"
                                                  title="子对象">
                                                <Icon type="ios-git-merge"/>
                                                <span>{{item.subTaskCount}}</span>
                                            </span>
                                            <span class="task-item-piece"
                                                  v-if="item.associateCount&&item.associateCount>0" title="关联任务">
                                                <Icon type="ios-link-outline"/>
                                                <span>{{item.associateCount}}</span>
                                            </span>
                                        </Col>
                                    </Row>
                                </div>
                            </draggable>

                        </div>
                    </div>
                </div>
            </template>
        </div>
    </div>
</template>

<script>
    import draggable from 'vuedraggable';
    import TaskPriority from "../../../../components/ui/TaskPriority";
    import TaskPeriod from "../../../../components/ui/TaskPeriod";

    export default {
        mixins: [componentMixin],
        props: ['queryInfo', 'queryForm'],
        components: {
            TaskPriority,
            draggable,
            TaskPeriod
        },
        data() {
            return {
                title: 'TaskKanbanView',
                columnList: [],
                statusTableData: {},
                sortConfigList: [
                    {id: 0, name: 'ownerAccountName', title: this.$t('责任人升序'), sort: 1},
                    {id: -1, name: 'ownerAccountName', title: this.$t('责任人降序'), sort: 2},
                    {id: 3, name: 'priorityName', title: this.$t('优先级升序'), sort: 1},
                    {id: 4, name: 'priorityName', title: this.$t('优先级降序'), sort: 2},
                    {id: 5, name: 'startDate', title: this.$t('开始时间升序'), sort: 1},
                    {id: 6, name: 'startDate', title: this.$t('开始时间降序'), sort: 2},
                    {id: 7, name: 'endDate', title: this.$t('结束时间升序'), sort: 1},
                    {id: 8, name: 'endDate', title: this.$t('结束时间降序'), sort: 2},

                ],
                pageQuery: {
                    total: 0,
                    pageIndex: 1,
                    pageSize: 500,
                    totalPage: 1,
                },
                pageSizePopuptipVisible: false,
                sortPopuptipVisible: false,
                viewSetting: {
                    tableSort: {
                        name: null,
                        sort: null,
                    },
                },
                parentWidth: 0,
                projectFinishDisabled: false,
                showSortBtn: app.globalKanbanSort
            };
        },
        computed: {
            tableDataCount() {
                if (!this.statusTableData) {
                    return 0;
                }
                let count = 0;
                Object.keys(this.statusTableData).forEach(key => {
                    if (!Array.isArray(this.statusTableData[key])) {
                        return;
                    }
                    count += this.statusTableData[key].length;
                });
                return count;
            },
            containerWidth() {
                if (this.columnList == null || this.$el == null || this.parentWidth === 0) {
                    return '100%';
                }
                var width = this.columnList.length * 320 + 20;
                if (width > this.parentWidth) {
                    return (width + 20) + 'px';
                }
                return '100%';
            },
        },
        created() {
            window.onresize = () => {
                if (this.$el && this.$el.parentElement) {
                    this.parentWidth = this.$el.parentElement.clientWidth;
                }
            };
            this.loadViewSetting();
            // 当在其他视图切换后，需要把视图的排序内容加载过来
            this.checkViewSetting();
        },
        beforeDestroy() {
            window.onresize = null;
        },
        mounted() {
            if (this.$el && this.$el.parentElement) {
                this.parentWidth = this.$el.parentElement.clientWidth;
            }
            this.loadGlobalSort();
        },
        methods: {
            clearSortCache() {
                app.confirm('确认重置自定义的排序吗?', () => {
                    for (let status in this.statusTableData) {
                        let key = [app.projectId, app.objectType, status, "kanbansort"].join("_");
                        app.removeObject(key);
                    }
                    app.toast("重置完成");
                    this.loadData();
                });
            },
            saveGlobalSort(showToast = true) {
                if (!app.globalKanbanSort) {
                    return;
                }
                let setting = {};
                for (let status in this.statusTableData) {
                    let key = [app.projectId, app.objectType, status, "kanbansort"].join("_");
                    setting[status] = app.loadObject(key);
                }

                app.invoke('BizAction.saveTaskSortSetting', [app.token, {
                    projectId: app.projectId,
                    objectType: app.objectType,
                    type: 1,
                    sortData: setting
                }], () => {
                    if (showToast) {
                        app.toast("设置成功");
                    }
                    console.log("save success")
                }, () => {
                });
            },
            loadGlobalSort() {
                if (app.globalKanbanSort) {
                    app.invoke('BizAction.getTaskSortSetting', [app.token, app.projectId, app.objectType], (res) => {
                        if (res && res.sortData) {
                            let sobj = JSON.parse(res.sortData);
                            for (let key in sobj) {
                                let key0 = [app.projectId, app.objectType, key, "kanbansort"].join("_");
                                app.saveObject(key0, sobj[key]);
                            }
                        }
                    }, () => {
                    });
                }
            },
            pageMessage(type) {
                if (type == 'task.edit') {
                    this.loadData();
                }
            },
            dragStart(e) {
            },
            dragEnd(e) {

            },
            dragChanged(e, column) {
                console.log("drag event--->", e.name, column.name)
                if (e.moved) {
                    this.setupKanbanSort(e.moved.oldIndex, e.moved.newIndex, e.moved.element.id, e.moved.element.objectType, e.moved.element.status);
                }
                if (!e.added) {
                    return;
                }

                this.updateTaskStatus(e.added.element, column);
            },

            /**
             * 变更列表状态检查
             * @deprecated
             */
            updateStatusList() {
                for (var k in this.statusTableData) {
                    var list = this.statusTableData[k];
                    for (var i = 0; i < list.length; i++) {
                        var task = list[i];
                        //console.log('task',k,task)
                        if (task && task.status != k) {
                            this.updateTaskStatus(task, k);
                        }
                    }
                }
            },
            updateTaskStatus(task, column) {
                if (this.projectFinishDisabled) {
                    app.toast("项目已完成，无法编辑状态");
                    this.loadData();
                    return;
                }
                const obj = {
                    id: task.id,
                    status: column.id,
                };
                app.invoke('BizAction.updateTask', [app.token, obj, ['status'], true], () => {
                    // 如果更新到的状态是终止状态则手动更新isFinish字段
                    task.status = column.id;
                    task.isFinish = column.type === 3;
                    this.loadData(column.id);
                }, () => {
                    this.loadData();
                });
            },
            getPercent(item) {
                if (item.expectWorkTime == 0) {
                    return 0;
                }
                if (item.workTime >= item.expectWorkTime) {
                    return 100;
                }
                return parseInt((item.workTime / item.expectWorkTime) * 100);
            },
            // hasField(name) {
            //     if (this.queryInfo.fieldList) {
            //         for (var i = 0; i < this.queryInfo.fieldList.length; i++) {
            //             var f = this.queryInfo.fieldList[i];
            //             if (f.field == name) {
            //                 return true;
            //             }
            //         }
            //     }
            //     return false;
            // },
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
            changePage(delta) {
                const t = this.pageQuery.pageIndex + delta;
                if (t <= 0 || t > this.pageQuery.totalPage) {
                    return;
                }
                this.pageQuery.pageIndex = t;
                this.loadData();
            },
            selectPageSize(size) {
                this.pageSizePopuptipVisible = false;
                this.pageQuery.pageSize = size;
                this.pageQuery.pageIndex = 1;
                this.saveViewSetting();
                this.loadData();
            },
            loadData(status) {
                this.loadViewSetting();
                this.columnList = this.queryInfo.statusList;
                this.queryForm.pageIndex = this.pageQuery.pageIndex;
                this.queryForm.pageSize = this.pageQuery.pageSize;
                const query = Object.assign({}, this.queryForm);
                query.customFieldsSort = {};
                if (this.viewSetting.tableSort.name != null) {
                    query[this.viewSetting.tableSort.name + 'Sort'] = this.viewSetting.tableSort.sort;
                    this.$set(this.queryForm, this.viewSetting.tableSort.name + 'Sort', this.viewSetting.tableSort.sort);
                }
                this.projectFinishDisabled = this.isProjectDisabled(query.projectId)
                let s = status;
                //
                app.invoke('BizAction.getTaskInfoListAndAssociateTasks', [app.token, {
                    ...query,
                    status,
                }], info => {
                    this.$nextTick(() => {
                        const statusList = {};
                        this.columnList.map(item => {
                            statusList[item.id] = [];
                        });
                        for (let i = 0; i < info.list.length; i++) {
                            const t = info.list[i];
                            if (statusList[t.status]) {
                                statusList[t.status].push(t);
                            }
                        }
                        if (s > 0) {
                            if (!this.statusTableData) {
                                this.statusTableData = statusList;
                                return;
                            }
                            this.statusTableData[s] = statusList[s];
                            return;
                        }
                        this.pageQuery.total = info.count;
                        if (info.count === 0) {
                            this.pageQuery.totalPage = 1;
                        } else {
                            this.pageQuery.totalPage = Math.ceil(info.count / this.pageQuery.pageSize);
                        }
                        //排序
                        if (!this.hasQueryCondition()) {
                            for (let key in statusList) {
                                var objectType = query.objectType;
                                var taskList = statusList[key];
                                var gsort = app.loadObject([app.projectId, objectType, key, "kanbansort"].join("_"));
                                if (gsort && gsort.length > 1) {
                                    const sortByObject = gsort.reduce(
                                        (obj, item, index) => ({
                                            ...obj,
                                            [item]: index + 1
                                        }), {}
                                    );

                                    taskList.sort((a, b) => {
                                        return (parseInt(sortByObject[a.id] || 0) - parseInt(sortByObject[b.id] || 0))
                                    })
                                    statusList[key] = taskList;
                                }
                            }
                        }
                        this.statusTableData = statusList;
                    });
                });
            },
            setupKanbanSort(oldIdx, newIdx, taskId, objectType, status) {
                if (this.hasQueryCondition()) {
                    console.log("has query params ,skip auto sort")
                    return;
                }
                var key = [app.projectId, objectType, status, "kanbansort"].join("_");
                var taskList = this.statusTableData[status];
                if (!!taskList && !Array.isEmpty(taskList)) {
                    var gsort = taskList.map(k => k.id);
                    // console.log("after set sort-----<",gsort)
                    app.saveObject(key, gsort);
                }

                // if(this.showSortBtn&&this.prjPerm('project_edit_config')){
                //     this.saveGlobalSort(false);
                // }
            },
            //有查询参数则不进行主动排序
            hasQueryCondition() {
                var obj = this.queryForm;
                var keys = ["filterId", "iterationId", "serialNo", "name", "ownerAccountId", "priorityInList", "categoryIdList"];
                var exist = false;
                if (!app.isEmptyOrNull(obj)) {
                    for (let i = 0; i < keys.length; i++) {
                        if (!app.isEmptyOrNull(obj[keys[i]])) {
                            exist = true;
                            break;
                        }
                    }
                    for (let key in obj) {
                        if (key.indexOf("Sort") > 0) {
                            if (!app.isEmptyOrNull(obj[key])) {
                                exist = true;
                                break;
                            }
                        }
                    }
                    if (!!obj.customFields) {
                        for (let key in obj.customFields) {
                            if (!app.isEmptyOrNull(obj.customFields[key])) {
                                exist = true;
                                break;
                            }
                        }
                    }

                }
                return exist;
            },
            showTaskInfo(item) {
                app.showDialog(TaskDialog, {
                    taskId: item.uuid,
                });
            },
            batchCopy(status) {
                var itemList = this.statusTableData[status];
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
            batchEdit(status) {
                var list = this.statusTableData[status];
                if (list.length == 0) {
                    app.toast(this.$t('请选择要编辑的数据'));
                    return;
                }
                app.showDialog(TaskEditDialog, {
                    itemList: list,
                    objectType: this.queryForm.objectType,
                    projectId: this.queryForm.projectId,
                });
            },
            batchDelete(status) {
                var itemName = '';
                var itemList = this.statusTableData[status];
                if (itemList.length == 0) {
                    return;
                } else {
                    itemName = itemList[0].name;
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
            showCreateDialog(status) {
                app.showDialog(TaskCreateDialog, {
                    projectId: this.queryInfo.project.id,
                    type: this.queryForm.objectType,
                    status: status,
                });
            },
            setSortType(type, sort) {
                this.sortPopuptipVisible = false;
                if (this.viewSetting.tableSort.name == type && this.viewSetting.tableSort.sort == sort) {
                    this.viewSetting.tableSort.name = null;
                    this.viewSetting.tableSort.sort = null;
                } else {
                    this.viewSetting.tableSort.name = type;
                    this.viewSetting.tableSort.sort = sort;
                }
                //设置状态到queryForm上
                this.$set(this.queryForm, type + 'Sort', this.viewSetting.tableSort.sort);
                this.saveViewSetting();
                this.loadData();
            },
            saveViewSetting() {
                var objectType = this.queryForm.objectType;
                var projectId = this.queryForm.projectId;
                this.viewSetting.pageSize = this.pageQuery.pageSize;
                app.saveObject('TaskPage.KanbanView.viewSetting' + objectType + '.' + projectId, this.viewSetting);
            },
            loadViewSetting() {
                var objectType = this.queryForm.objectType;
                var projectId = this.queryForm.projectId;
                var vc = app.loadObject('TaskPage.KanbanView.viewSetting' + objectType + '.' + projectId);
                if (vc != null) {
                    if (vc.pageSize) {
                        this.pageQuery.pageSize = vc.pageSize;
                    }
                    if (vc.tableSort == null) {
                        vc.tableSort = {
                            name: null,
                            sort: null,
                        };
                    }
                    this.viewSetting = vc;
                }
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
        },
    };
</script>
