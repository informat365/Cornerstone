<style scoped>
    .page {
        min-height: 400px;
        overflow: hidden;
    }

    .task-opt-bar {
        display: flex;
        align-items: center;
        border-bottom: 1px solid #ccc;
        position: relative;
    }

    .opt-left {
        flex: 1;
        padding: 5px;
    }

    .opt-left-iteration {
        width: 180px;
        padding-left: 20px;
    }

    .opt-right {
        text-align: right;
        width: 400px;
        padding: 5px;
        padding-right: 20px;
    }

    .table-info-bar {
        font-size: 12px;
        font-weight: bold;
        color: #999;
        text-align: right;
    }

    .more-query-btn {
        font-size: 12px;
        color: #ccced3;
    }

    .more-query-btn-value {
        color: #0097f7;
    }

    .task-box-wrap {
        display: flex;
    }

    .task-view-box {
        flex: 1;
        max-height: calc(100vh - 95px);
        overflow: auto;
        padding-left: 10px;
        padding-right: 0px;
    }

    .side-box {
        background-color: #fff;
        display: block;
        width: 280px;
        overflow: auto;
        height: calc(100vh - 95px);
        transition: 0.35s;
        z-index: 2;
        position: relative;
    }

    .side-box-hide {
        transform: translate(-280px, 0);
    }

    .silde-control-bar {
        width: 6px;
        position: relative;
    }


    .slide-split-bar {
        position: absolute;
        top: 0;
        left: 0;
        height: 100%;
        width: 2px;
        background-color: #ccc;
        cursor: col-resize;
    }

    .slide-split-bar:hover,
    .slide-split-bar:active {
        background-color: #009cf1;
    }


    .hide-side-button {
        position: absolute;
        right: 0px;
        top: 32px;
        width: 6px;
        height: 40px;
        background-color: #ccc;
        cursor: pointer;
        color: #fff;
        border-radius: 2px;
    }

    .hide-side-button:hover {
        background-color: #5391f0;
    }

    .hide-side-button-icon {
        position: absolute;
        left: -3px;
        top: 13px;
    }

    .category-item-all {
        font-size: 13px;
        font-weight: bold;
        color: #808080;
        padding: 10px 20px;
        display: flex;
        align-items: center;
        justify-content: space-between;
    }

    .category-item-search {
        padding: 12px 20px;
    }

    .category-item-all .category-label {
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 1;
        -webkit-box-orient: vertical;
    }

    .category-item-all .category-actions {
        display: flex;
        align-items: center;
        justify-content: flex-end;
    }

    .category-item {
        font-size: 13px;
        font-weight: bold;
        color: #808080;
        padding: 10px 20px;
    }

    .category-item:hover {
        background-color: #f5f5f5;
    }

    .category-opt {
        padding: 10px 20px;
    }

    .category-checked {
        color: #0097f7;
    }

    .category-item-selected {
        font-size: 14px;
        padding: 10px 20px;
        color: #222;
        font-weight: bold;
        margin-top: 10px;
    }

    .more-query-input {
        width: 240px;
    }

    .filtername {
        position: absolute;
        top: 24px;
        right: -40px;
        width: 400px;
        text-align: right;
        z-index: 2;
    }

    .fintername-inner {
        color: rgba(35, 145, 255, 1);
        font-size: 12px;
        font-weight: bold;
        border: 1px solid rgba(35, 145, 255, 1);
        display: inline;
    }

    .iteration-prefix-text {
        font-size: 13px;
        color: #009af4;
        font-weight: bold;
        display: block;
    }

    .create-btn {
        max-width: 145px;
        text-overflow: ellipsis;
        overflow: hidden;
    }

    .public-label {
        position: absolute;
        top: 0px;
        right: 0px;
        text-align: right;
        font-size: 12px;
        color: #666;
        transform: scale(0.8)
    }

    .task-more-search-content {
        padding-bottom: 100px;
        padding: 20px;
        max-height: calc(100vh - 150px);
        overflow: auto;
        white-space: normal;
    }
</style>
<i18n>
    {
    "en": {
    "待认领":"none",
    "名称":"Name",
    "所有责任人":"All Owners",
    "所有状态":"All Status",
    "创建人":"Creater",
    "优先级":"Priority",
    "所有Release":"All Releases",
    "所有子系统":"All Systems",
    "所有优先级":"All Priority",
    "未设置":"none",
    "重置":"Reset",
    "查询":"Query",
    "创建":"Add ",
    "同步":"Sync",
    "过滤器":"Filter",
    "已过滤":"filterd",
    "视图":"View",
    "表格":"Table",
    "分栏":"Column",
    "看板":"Kanban",
    "甘特图":"Gantt",
    "日历":"Calendar",
    "统计":"Statistics",
    "汇总":"Week Summary",
    "创建人和责任人可见":"Only Create and Owner",
    "已选择":"Selected",
    "所有":"All",
    "未分类":"none",
    "分类":"Category",
    "全局分类":"Global Category",
    "创建全局分类":"Create Global Category",
    "搜索全局分类":"Search Global Category",
    "调整全局分类顺序":"Reorder Global Category",
    "创建分类":"Create Category",
    "搜索分类":"Search Category",
    "调整顺序":"Reorder",
    "未规划":"Backlog",
    "同步成功":"Sync success",
    "同步失败":"Sync Failure",
    "分类导图":"Mindmap",
    "责任人部门": "Owner's Department",
    "请选择责任人部门": "Select Owner's Department Please"
    },
    "zh_CN": {
    "待认领":"待认领",
    "名称":"名称",
    "所有责任人":"所有责任人",
    "所有状态":"所有状态",
    "所有优先级":"所有优先级",
    "创建人":"创建人",
    "优先级":"优先级",
    "所有Release":"所有Release",
    "所有子系统":"所有子系统",
    "未设置":"未设置",
    "重置":"重置",
    "查询":"查询",
    "创建":"创建",
    "同步":"同步",
    "过滤器":"过滤器",
    "已过滤":"已过滤",
    "视图":"视图",
    "表格":"表格",
    "分栏":"分栏",
    "看板":"看板",
    "甘特图":"甘特图",
    "日历":"资源日历",
    "统计":"统计",
    "汇总":"汇总",
    "创建人和责任人可见":"创建人和责任人可见",
    "已选择":"已选择",
    "所有":"所有",
    "分类":"分类",
    "全局分类":"全局分类",
    "创建全局分类":"创建全局分类",
    "搜索全局分类":"搜索全局分类",
    "调整全局分类顺序":"调整全局分类顺序",
    "未分类":"未分类",
    "创建分类":"创建分类",
    "搜索分类":"搜索分类",
    "调整顺序":"调整顺序",
    "未规划":"未规划",
    "同步成功":"同步成功",
    "同步失败":"同步失败",
    "分类导图":"分类导图",
    "责任人部门": "责任人部门",
    "请选择责任人部门": "请选择责任人部门"
    }
    }
</i18n>
<template>
    <div v-if="queryInfo.project" class="page">
        <div class="task-opt-bar">
            <div v-if="hasField('iterationName')" class="opt-left-iteration">
                <IterationSelect
                    :list="iterationList" @change="debounceQueryData" v-model="queryItem.iterationId"></IterationSelect>
            </div>
            <div class="opt-left">
                <Form inline>
                    <FormItem>
                        <Input
                            @on-change="debounceQueryData"
                            v-model.trim="queryItem.serialNo"
                            type="text"
                            placeholder="#ID"
                            style="width:70px"></Input>
                    </FormItem>
                    <FormItem>
                        <Input
                            @on-change="debounceQueryData"
                            v-model.trim="queryItem.name"
                            type="text"
                            :placeholder="$t('名称')"></Input>
                    </FormItem>
                    <FormItem class="wide-screen" v-if="hasField('ownerAccountName')&&!isProjectSet">
                        <ProjectMemberSelect
                            v-model="queryItem.ownerAccountIdList"
                            style="width:180px"
                            clearable
                            multiple
                            :placeholder="$t('所有责任人')"
                            :list="queryInfo.memberList"
                            @on-change="debounceQueryData"/>
                    </FormItem>
                    <FormItem class="wide-screen" v-if="hasField('ownerAccountName')&&isProjectSet">
                        <CompanyUserSelect
                            v-model="queryItem.ownerAccountIdList"
                            style="width:180px"
                            clearable
                            multiple
                            :placeholder="$t('所有责任人')"
                            @on-change="debounceQueryData"/>
                    </FormItem>
                    <!-- <FormItem class="wide-screen" v-if="showDepartmentFilter">
                        <DepartmentSelect
                            clearable
                            style="width:180px"
                            v-model="queryItem.departmentIdInList"
                            :placeholder="$t('请选择责任人部门')"
                            :list="departmentList"
                            multiple
                            @on-change="debounceQueryData"
                        ></DepartmentSelect>
                    </FormItem> -->
                    <FormItem class="wide-screen" v-if="hasField('statusName')">
                        <AutoWidthSelect
                            :value-list="queryInfo.statusList"
                            @on-change="debounceQueryData"
                            multiple
                            clearable
                            v-model="queryItem.statusInList"
                            :placeholder="$t('所有状态')"></AutoWidthSelect>
                    </FormItem>
                    <FormItem v-if="hasMoreOption">
                        <Poptip transfer placement="bottom" width="600" class="poptip-gray nowrap-poptip">
                          <span class="more-query-btn" :class="{'more-query-btn-value':moreOptionHasValue()}">
                            <Icon type="ios-more" size="18"/>
                          </span>
                            <div slot="content" class="task-more-search-content scrollbox">
                                <Form label-position="top">
                                    <Row>
                                        <template v-for="field in queryInfo.fieldList">
                                            <Col
                                                span="12"
                                                v-if="field.field=='createAccountName'"
                                                style="padding:10px"
                                                :key="'field_'+field.field">
                                                <FormItem :label="$t('创建人')" :key="'field1_'+field.field">
                                                    <Select
                                                        class="more-query-input"
                                                        transfer
                                                        @on-change="debounceQueryData"
                                                        clearable
                                                        v-model="queryItem.createAccountId"
                                                        :placeholder="$t('创建人')">
                                                        <Option
                                                            v-for="item in queryInfo.memberList"
                                                            :value="item.accountId"
                                                            :key="item.accountId">{{item.accountName}}
                                                        </Option>
                                                    </Select>
                                                </FormItem>
                                            </Col>
                                            <Col
                                                span="12"
                                                v-if="field.field=='priorityName'"
                                                style="padding:10px"
                                                :key="'field_'+field.field">
                                                <FormItem :label="$t('优先级')" :key="'field1_'+field.field">
                                                    <Select
                                                        transfer
                                                        @on-change="debounceQueryData"
                                                        multiple
                                                        clearable
                                                        v-model="queryItem.priorityInList"
                                                        :placeholder="$t('所有优先级')"
                                                        class="more-query-input">
                                                        <Option
                                                            v-for="item in queryInfo.priorityList"
                                                            :key="item.id"
                                                            :value="item.id">{{item.name}}
                                                        </Option>
                                                    </Select>
                                                </FormItem>
                                            </Col>

                                            <Col
                                                span="12"
                                                v-if="field.field=='releaseName'"
                                                style="padding:10px"
                                                :key="'field_'+field.field">
                                                <FormItem label="Release" :key="'field1_'+field.field">
                                                    <Select
                                                        transfer
                                                        @on-change="debounceQueryData"
                                                        multiple
                                                        clearable
                                                        v-model="queryItem.releaseInList"
                                                        :placeholder="$t('所有Release')"
                                                        class="more-query-input">
                                                        <Option
                                                            v-for="item in queryInfo.releaseList"
                                                            :key="item.id"
                                                            :value="item.id">{{item.name}}
                                                        </Option>
                                                    </Select>
                                                </FormItem>
                                            </Col>

                                            <Col
                                                span="12"
                                                v-if="field.field=='subSystemName'"
                                                style="padding:10px"
                                                :key="'field_'+field.field">
                                                <FormItem
                                                    :key="'field1_'+field.field"
                                                    v-if="field.field=='subSystemName'"
                                                    label="子系统">
                                                    <Select
                                                        transfer
                                                        @on-change="debounceQueryData"
                                                        multiple
                                                        clearable
                                                        v-model="queryItem.subSystemInList"
                                                        :placeholder="$t('所有子系统')"
                                                        class="more-query-input">
                                                        <Option
                                                            v-for="item in queryInfo.subSystemList"
                                                            :key="item.id"
                                                            :value="item.id">{{item.name}}
                                                        </Option>
                                                    </Select>
                                                </FormItem>
                                            </Col>

                                            <Col
                                                span="12"
                                                :key="'field_'+field.id"
                                                v-if="field.isSystemField==false&&(field.type==1||field.type==3||field.type==4||field.type==6)&&!isProjectSet"
                                                style="padding:10px">
                                                <FormItem :key="'field1_'+field.id" :label="field.name">
                                                    <Input
                                                        v-if="field.type==1"
                                                        @on-change="debounceQueryData"
                                                        v-model.trim="queryItem.customFields['field_'+field.id]"
                                                        type="text"
                                                        class="more-query-input"></Input>

                                                    <Select
                                                        transfer
                                                        v-if="field.type==6"
                                                        class="more-query-input"
                                                        multiple
                                                        @on-change="debounceQueryData"
                                                        clearable
                                                        v-model="queryItem.customFields['field_'+field.id]">
                                                        <Option :value="0">{{$t('未设置')}}</Option>
                                                        <Option
                                                            v-for="item in queryInfo.memberList"
                                                            :value="item.accountId"
                                                            :key="item.accountId">{{item.accountName}}
                                                        </Option>
                                                    </Select>

                                                    <Select
                                                        transfer
                                                        v-if="(field.type==3||field.type==4)"
                                                        @on-change="debounceQueryData"
                                                        multiple
                                                        clearable
                                                        v-model="queryItem.customFields['field_'+field.id]"
                                                        class="more-query-input">
                                                        <Option
                                                            v-for="(item,itemIndex) in field.valueRange"
                                                            :key="'vr'+field.id+'_'+itemIndex"
                                                            :value="item">{{item}}
                                                        </Option>
                                                    </Select>
                                                </FormItem>
                                            </Col>
                                        </template>
                                    </Row>
                                </Form>
                            </div>
                        </Poptip>

                    </FormItem>
                    <FormItem>
                        <IconButton style="margin-right:5px" @click="resetQuery" :title="$t('重置')"></IconButton>
                        <IconButton @click="debounceQueryData" :title="$t('查询')"></IconButton>
                    </FormItem>
                </Form>

            </div>
            <div class="opt-right">
                <Form inline>
                    <FormItem>
                        <Button
                            class="create-btn"
                            v-if="prjPerm('task_edit_'+type) && showSyncBtn&&!projectDisabled"
                            @click="syncTasksFromProject"
                            :loading="syncTasking"
                            type="default"
                            icon="md-sync">
                            {{$t('同步')}}{{queryItem.objectType|dataDict('Task.objectType')}}
                        </Button>
                    </FormItem>
                    <FormItem>
                        <Button
                            class="create-btn"
                            v-if="prjPerm('task_create_'+type)&&!isProjectSet&&!projectDisabled"
                            @click="showCreateDialog"
                            type="default"
                            icon="md-add">
                            {{$t('创建')}}{{queryItem.objectType|dataDict('Task.objectType')}}
                        </Button>
                    </FormItem>
                    <FormItem>
                        <FilterSelect
                            @change="queryData()"
                            :object-type="type"
                            :filter-list="queryInfo.filterList"
                            v-model="queryItem.filterId"
                            placement="bottom-end">
                            <span slot="popup-name" class="dropdown-btn">{{$t('过滤器')}}</span>
                        </FilterSelect>
                        <div v-if="filterName!=null" class="filtername">
                            <div class="fintername-inner">{{$t('已过滤')}}：{{filterName}}</div>
                        </div>
                    </FormItem>

                    <FormItem>

                        <Poptip transfer placement="bottom-end" v-model="viewPopuptipVisible">
                            <span class="dropdown-btn">{{$t('视图')}}</span>
                            <div slot="content">
                                <PopupTipItem
                                    @click="viewChangeClick('table')"
                                    :name="$t('表格')"
                                    :selected="viewItem.viewShowType=='table'"/>
                                <PopupTipItem
                                    @click="viewChangeClick('split')"
                                    :name="$t('分栏')"
                                    :selected="viewItem.viewShowType=='split'"/>
                                <PopupTipItem
                                    @click="viewChangeClick('kanban')"
                                    :name="$t('看板')"
                                    :selected="viewItem.viewShowType=='kanban'"
                                    v-if="hasField('statusName')"/>
                                <PopupTipItem
                                    @click="viewChangeClick('gantt')"
                                    :name="$t('甘特图')"
                                    :selected="viewItem.viewShowType=='gantt'"
                                    v-if="hasField('startDate')&&hasField('endDate')"/>
                                <PopupTipItem
                                    @click="viewChangeClick('cal')"
                                    :name="$t('日历')"
                                    :selected="viewItem.viewShowType=='cal'"
                                    v-if="hasField('startDate')&&hasField('endDate')"/>
                                <PopupTipItem
                                    @click="viewChangeClick('stat')"
                                    :name="$t('统计')"
                                    :selected="viewItem.viewShowType=='stat'"
                                    v-if="hasField('statusName')"/>
                                <PopupTipItem
                                    @click="viewChangeClick('week')"
                                    :name="$t('汇总')"
                                    :selected="viewItem.viewShowType=='week'"
                                    v-if="hasField('startDate')&&hasField('endDate')"/>
                                <PopupTipItem
                                    @click="viewChangeClick('mindmap')"
                                    :name="$t('分类导图')"
                                    :selected="viewItem.viewShowType=='mindmap'"/>
                            </div>
                        </Poptip>


                    </FormItem>
                </Form>
            </div>
            <div class="public-label" v-if="queryInfo.isPublic==false">
                {{$t('创建人和责任人可见')}}
            </div>
        </div>
        <div class="task-box-wrap">
            <div
                class="side-box scrollbox"
                :class="{'side-box-hide':viewItem.showSideBar===false}"
                :style="slideBoxWidthStyle">
                <div class="category-item-selected text-no-wrap">
                    {{$t('已选择')}}：
                    <template v-if="globalCategory==1">
                        {{$t('所有')}}{{queryItem.objectType|dataDict('Task.objectType')}}
                    </template>
                    <template v-if="globalCategory==2">
                        {{$t('未分类')}}{{queryItem.objectType|dataDict('Task.objectType')}}
                    </template>
                    <template v-if="selectedCategoryList&&selectedCategoryList.length>0">
                        <span style="padding-right:5px" v-for="item in selectedCategoryList" :key="'sc_'+item.id">{{item.name}}</span>
                    </template>
                </div>
                <div
                    @click="setGlobalCategory(1)" class="category-item" :class="{'category-checked':globalCategory==1}">
                    {{$t('所有')}}{{queryItem.objectType|dataDict('Task.objectType')}}
                </div>
                <div
                    @click="setGlobalCategory(2)" class="category-item" :class="{'category-checked':globalCategory==2}">
                    {{ $t('未分类') }}{{queryItem.objectType|dataDict('Task.objectType')}}
                </div>
                <div class="category-item-all">
                    <div class="category-label">
                        {{ $t('全局分类') }}
                    </div>
                    <div class="category-actions">
                        <IconButton
                            v-if="prjPerm('project_global_category')&&!projectDisabled"
                            size="18"
                            @click.stop="showEditGlobalCategoryDialog()"
                            icon="md-add"
                            :tips="$t('创建全局分类')"/>
                        <IconButton
                            size="18" @click.stop="onGlobalCategoryFilterShow" icon="md-search" :tips="$t('搜索全局分类')"/>
                        <IconButton
                            v-if="prjPerm('project_global_category')&&!projectDisabled"
                            size="18"
                            @click.stop="showEditGlobalCategoryTreeDialog()"
                            icon="md-reorder"
                            :tips="$t('调整全局分类顺序')"/>
                    </div>
                </div>
                <div class="category-item-search" v-show="globalCategoryFilterShow">
                    <Input
                        v-model="globalCategoryKeyword" :placeholder="$t('搜索全局分类')" suffix="ios-search"/>
                </div>
                <TaskCategoryTreeNode
                    :ref="'category-'+category.id"
                    :change="categorySelectChanged"
                    permissionId="project_global_category"
                    v-for="category in globalCategories"
                    :value="category"
                    :key="category.id"/>
                <div class="category-item-all">
                    <div class="category-label">{{queryItem.objectType|dataDict('Task.objectType')}}{{ $t('分类') }}</div>
                    <div class="category-actions">
                        <IconButton
                            v-if="prjPerm('task_edit_category_'+type)&&!projectDisabled"
                            size="18"
                            @click.stop="showEditCategoryDialog()"
                            icon="md-add"
                            :tips="$t('创建分类')"/>
                        <IconButton
                            size="18" @click.stop="onCategoryFilterShow" icon="md-search" :tips="$t('搜索分类')"/>
                        <IconButton
                            v-if="prjPerm('task_edit_category_'+type)&&!projectDisabled"
                            size="18"
                            @click.stop="showEditCategoryTreeDialog()"
                            icon="md-reorder"
                            :tips="$t('调整顺序')"/>
                    </div>
                </div>
                <div class="category-item-search" v-show="categoryFilterShow">
                    <Input
                        v-model="categoryKeyword" :placeholder="$t('搜索全局分类')" suffix="ios-search"/>
                </div>
                <TaskCategoryTreeNode
                    :ref="'category-'+category.id"
                    :permissionId="'task_edit_category_'+type"
                    :change="categorySelectChanged"
                    v-for="category in categories"
                    :value="category"
                    :key="category.id"/>
            </div>
            <div class="silde-control-bar">
                <div class="slide-split-bar" @mousedown.stop="drag"></div>
                <div @click="toggleSideBar" class="hide-side-button">
                    <Icon v-if="viewItem.showSideBar" class="hide-side-button-icon" type="md-arrow-dropleft"/>
                    <Icon v-if="viewItem.showSideBar==false" class="hide-side-button-icon" type="md-arrow-dropright"/>
                </div>
            </div>

            <div class="task-view-box scrollbox">
                <TaskListView
                    ref="table"
                    :project-set-task="isProjectSet"
                    :task-id="currentTaskId"
                    :query-info="queryInfo"
                    :query-form="queryItem"
                    v-if="viewItem.viewShowType=='table'"></TaskListView>
                <TaskKanbanView
                    ref="kanban"
                    :task-id="currentTaskId"
                    :query-info="queryInfo"
                    :query-form="queryItem"
                    v-if="viewItem.viewShowType=='kanban'"></TaskKanbanView>
                <TaskStatView
                    ref="stat"
                    :query-info="queryInfo"
                    :query-form="queryItem"
                    v-if="viewItem.viewShowType=='stat'"></TaskStatView>
                <TaskNewGanttView
                    ref="gantt"
                    :is-project-set="isProjectSet"
                    :task-id="currentTaskId"
                    :query-info="queryInfo"
                    :query-form="queryItem"
                    v-if="viewItem.viewShowType=='gantt'"></TaskNewGanttView>
                <TaskSplitView
                    ref="split"
                    :task-id="currentTaskId"
                    :query-info="queryInfo"
                    :query-form="queryItem"
                    v-if="viewItem.viewShowType=='split'"></TaskSplitView>
                <TaskSchedulerView
                    ref="cal"
                    :task-id="currentTaskId"
                    :query-info="queryInfo"
                    :query-form="queryItem"
                    v-if="viewItem.viewShowType=='cal'"></TaskSchedulerView>
                <TaskWeekView
                    ref="week"
                    :task-id="currentTaskId"
                    :query-info="queryInfo"
                    :query-form="queryItem"
                    v-if="viewItem.viewShowType=='week'"></TaskWeekView>
                <TaskMindmapView
                    ref="mindmap"
                    :task-id="currentTaskId"
                    :query-info="queryInfo"
                    :query-form="queryItem"
                    v-if="viewItem.viewShowType=='mindmap'"></TaskMindmapView>

            </div>
        </div>
        <transition name="slide">
            <TaskPopup
                @popup-close="closeTaskPopup"
                :task-id="currentTaskId"
                v-if="currentTaskId!=null&&viewItem.viewShowType=='table'"></TaskPopup>
        </transition>


    </div>
</template>

<script>

    import ProjectMemberSelect from './ProjectMemberSelect';
    import CompanyUserSelect from './CompanyUserSelect';
    import DepartmentSelect from './DepartmentSelect';

    export default {
        components: {ProjectMemberSelect, DepartmentSelect, CompanyUserSelect},
        mixins: [componentMixin],
        data() {
            return {
                title: 'TaskPage',
                queryInfo: {},
                type: 1,
                globalCategoryKeyword: null,
                categoryKeyword: null,
                globalCategoryFilterShow: false,
                categoryFilterShow: false,
                currentTaskId: null,
                projectId: null,
                iterationList: [],
                departmentList: [],
                queryItem: {
                    filterId: null,
                    projectId: null,
                    iterationId: null,
                    serialNo: null,
                    name: null,
                    ownerAccountId: null,
                    ownerAccountIdList: null,
                    statusInList: [],
                    priorityInList: [],
                    objectType: null,
                    customFields: {},
                    isSetCategory: null,
                    categoryIdList: [],
                    // departmentIdInList: [],
                    customFieldsSort: {},
                    nameSort: undefined,
                    ownerAccountNameSort: undefined,
                    priorityNameSort: undefined,
                    startDateSort: undefined,
                    endDateSort: undefined,
                    statusNameSort: undefined,
                },
                selectedCategoryList: [],
                globalCategoryList: [],
                categoryList: [],
                globalCategory: 1,
                categoryListLoaded: false,
                hasMoreOption: false,
                syncTasking: false,
                viewItem: {
                    viewShowType: 'table',
                    showSideBar: true,
                },
                viewPopuptipVisible: false,
                resizing: false,
                slideWidth: 280,
                isProjectSet: false
            };
        },
        computed: {
            slideBoxWidthStyle() {
                if (!this.viewItem.showSideBar) {
                    return {
                        width: 0,
                    };
                }
                return {
                    width: `${this.slideWidth}px`,
                };
            },
            globalCategories() {
                if (!this.globalCategoryKeyword) {
                    return this.globalCategoryList;
                }
                const t = JSON.parse(JSON.stringify(this.globalCategoryList));
                return this.deepFilterCategory(t, this.globalCategoryKeyword);
            },
            categories() {
                if (!this.categoryKeyword) {
                    return this.categoryList;
                }
                const t = JSON.parse(JSON.stringify(this.categoryList));
                return this.deepFilterCategory(t, this.categoryKeyword);
            },
            filterName() {
                if (this.queryItem.filterId == null) {
                    return null;
                }
                if (this.queryInfo.filterList == null) {
                    return null;
                }
                for (var i = 0; i < this.queryInfo.filterList.length; i++) {
                    var f = this.queryInfo.filterList[i];
                    if (f.id == this.queryItem.filterId) {
                        return f.name;
                    }
                }
                return null;
            },
            keymap() {
                return {
                    'ctrl+n': this.showCreateDialog,
                };
            },
            showSyncBtn() {
                if (!Array.isArray(app.moduleList)) {
                    return false;
                }
                const objectTypeModule = app.moduleList.find(item => item.objectType === this.type);
                if (!objectTypeModule) {
                    return false;
                }
                return objectTypeModule.objectTypeSystemName === process.env.VUE_APP_PROJECT_SET_OBJECT_TYPE_SYSTEM_NAME_PRO;
            },
            // showDepartmentFilter() {
            //     return process.env.VUE_APP_TASK_OWNER_DEPARTMENT_FILTER === 'show';
            // },
        },
        watch: {
            queryItem: {
                deep: true,
                handler() {
                    this.$nextTick(() => {
                        this.saveStatus();
                    });
                },
            },
        },
        created() {
            this.debounceQueryData = app.debounce(this.queryData, 250);
        },
        beforeDestroy() {
            this.debounceQueryData = null;
        },
        methods: {
            drag(e) {
                const startX = e.clientX;
                const startWidth = 280;
                this.resizing = true;
                document.body.className += ' mouse-resizing';
                document.onmousemove = app.debounce((ev) => {
                    const moveX = ev.clientX;
                    const offset = moveX - startX;
                    let width = startWidth + offset;
                    if (width < 250) {
                        width = 250;
                    }
                    if (width > 500) {
                        width = 500;
                    }
                    this.slideWidth = width;
                }, 20);
                document.onmouseup = () => {
                    document.onmousemove = null;
                    document.onmouseup = null;
                    this.resizing = false;
                    document.body.className = document.body.className.replace(' mouse-resizing', '');
                };
            },
            pageLoad() {
                this.projectId = app.projectId;
                this.type = parseInt(this.args.$page.substring(4));
                //是否是项目集
                this.isProjectSet = this.type == '1001';
                //
                if (!app.prjPerm('task_list_' + this.type)) {
                    app.toast('权限不足');
                    app.loadPage('/pm/index/dashboard');
                    return;
                }
                this.showTask(this.args.id);
                this.loadQueryInfo();
                // if(this.showDepartmentFilter) {
                //     this.getDepartmentList();
                // }
                if (this.isProjectSet) {
                    if(app.isAutoRefreshProjectSet){
                        this.syncTasksFromProject();
                    }
                }
            },
            onGlobalCategoryFilterShow() {
                this.globalCategoryKeyword = null;
                this.globalCategoryFilterShow = !this.globalCategoryFilterShow;
            },
            onCategoryFilterShow() {
                this.categoryKeyword = null;
                this.categoryFilterShow = !this.categoryFilterShow;
            },
            deepFilterCategory(list, keyword) {
                if (!Array.isArray(list) || list.length === 0) {
                    return [];
                }
                return list.filter((item) => {
                    if (item.children) {
                        item.children = this.deepFilterCategory(item.children, keyword);
                    }
                    return item.name.indexOf(keyword) > -1 || (Array.isArray(item.children) && item.children.length > 0);
                });
            },
            pageUpdate() {
                this.showTask(this.args.id);
            },
            pageMessage(type, data) {
                if (type == 'task.import') {
                    this.queryItem.iterationId = null;
                    this.debounceQueryData();
                }
                if (type == 'task.edit') {
                    this.debounceQueryData();
                }
                if (type == 'filter.edit') {
                    this.loadQueryInfo();
                }
                if (type == 'category.edit') {
                    this.loadCategory();
                }
                if (type == 'task.delete') {
                    this.closeTaskPopup();
                }
                if (type == 'websocket.taskUpdate') {
                    //console.log('xxxxx',data)
                    var objectType = this.queryItem.objectType;
                    var projectId = this.queryItem.projectId;
                    if (data.projectId == projectId && data.objectType == objectType) {
                        this.debounceQueryData();
                    }
                }
                if (type == 'websocket.taskCategoryUpdate') {
                    var objectType = this.queryItem.objectType;
                    var projectId = this.queryItem.projectId;
                    if (data.projectId == projectId && data.objectType == objectType) {
                        this.loadCategory();
                    }
                }
            },
            resetQuery() {
                this.queryItem.filterId = null;
                this.queryItem.serialNo = null;
                this.queryItem.name = null;
                this.queryItem.ownerAccountId = null;
                this.queryItem.ownerAccountIdList = null;
                this.queryItem.statusInList = null;
                this.queryItem.priorityInList = null;
                this.queryItem.createAccountId = null;

                this.queryItem.priorityInList = [];
                this.queryItem.releaseInList = [];
                this.queryItem.subSystemInList = [];

                for (var k in this.queryItem.customFields) {
                    this.queryItem.customFields[k] = null;
                }
                this.setGlobalCategory(1);
                this.debounceQueryData(true);
            },
            setGlobalCategory(type) {
                this.globalCategory = type;
                this.selectedCategoryList = [];
                this.resetCategory();
                this.debounceQueryData(true);
            },
            resetCategory() {
                this.globalCategoryList.forEach(item => {
                    travalTree(item, (node) => {
                        node.selected = false;
                    });
                    this.$refs['category-' + item.id][0].updateSelect();
                });
                this.categoryList.forEach(item => {
                    travalTree(item, (node) => {
                        node.selected = false;
                    });
                    this.$refs['category-' + item.id][0].updateSelect();
                });
            },
            categorySelectChanged() {
                const selectedCategory = [];
                this.globalCategories.forEach(item => {
                    travalTree(item, (node) => {
                        if (node.selected) {
                            selectedCategory.push(node);
                        }
                    });
                });
                this.categories.forEach(item => {
                    travalTree(item, (node) => {
                        if (node.selected) {
                            selectedCategory.push(node);
                        }
                    });
                });
                this.selectedCategoryList = selectedCategory;
                if (this.selectedCategoryList.length > 0) {
                    this.globalCategory = 0;
                } else {
                    this.globalCategory = 1;
                }
                this.debounceQueryData(true);
            },
            showEditGlobalCategoryDialog(item) {
                app.showDialog(TaskCategoryEditDialog, {
                    projectId: this.queryItem.projectId,
                    objectType: 0,
                    parentId: 0,
                    item: item,
                });
            },
            showEditGlobalCategoryTreeDialog() {
                app.showDialog(TaskCategoryTreeEditDialog, {
                    projectId: this.queryItem.projectId,
                    objectType: 0,
                });
            },
            showEditCategoryDialog(item) {
                app.showDialog(TaskCategoryEditDialog, {
                    objectType: this.queryItem.objectType,
                    projectId: this.queryItem.projectId,
                    parentId: 0,
                    item: item,
                });
            },
            showEditCategoryTreeDialog() {
                var objectType = this.queryItem.objectType;
                var projectId = this.queryItem.projectId;
                app.showDialog(TaskCategoryTreeEditDialog, {
                    objectType: objectType,
                    projectId: projectId,
                });
            },
            toggleSideBar() {
                this.viewItem.showSideBar = !this.viewItem.showSideBar;
                this.saveStatus();
            },
            computeMoreOption() {
                //hasMoreOption
                var count = 0;
                for (var i = 0; i < this.queryInfo.fieldList.length; i++) {
                    var t = this.queryInfo.fieldList[i];
                    if (this.hasField('createAccountName')
                        || this.hasField('priporityName')
                        || this.hasField('releaseName')
                        || this.hasField('subSystemName')) {
                        count++;
                    }
                    if (t.isSystemField == false) {
                        count++;
                    }
                }
                this.hasMoreOption = count > 0;
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
            loadQueryInfo() {
                this.loadStatus();
                this.queryItem.objectType = this.type;
                //
                //
                app.invoke('BizAction.getTaskQueryInfo', [app.token, app.projectUUID, this.type], info => {
                    this.queryInfo = info;
                    this.queryItem.projectId = info.project.id;
                    this.loadIterationList(info.iterationList);
                    this.checkQueryItem(info);
                    this.computeMoreOption();
                    this.loadCategory();
                });
            },
            loadIterationList(list) {
                this.iterationList = list;
                this.iterationList.push({
                    name: this.$t('未规划'),
                    id: 0,
                });
                //
                var containsIterationId = false;
                //
                for (var i = 0; i < list.length; i++) {
                    if (list[i].id == this.queryItem.iterationId) {
                        containsIterationId = true;
                    }
                }
                //
                if (containsIterationId == false) {
                    this.queryItem.iterationId = null;
                }
                //
                if (!this.hasField('iterationName')) {
                    //未配置迭代属性
                    this.queryItem.iterationId = null;
                }
            },
            loadCategory() {
                let cateList = this.queryItem.categoryIdList;
                if (cateList == null) {
                    cateList = [];
                }
                app.invoke('BizAction.getCategoryNodeList', [app.token, this.queryItem.projectId, this.queryItem.objectType], list => {
                    this.selectedCategoryList = [];
                    this.categoryListLoaded = true;
                    const categoryList = [];
                    const globalCategoryList = [];
                    list.forEach(item => {
                        travalTree(item, (node) => {
                            if (cateList.indexOf(node.id) > -1) {
                                node.selected = true;
                                this.selectedCategoryList.push(node);
                            } else {
                                node.selected = false;
                            }
                        });
                        if (item.objectType > 0) {
                            categoryList.push(item);
                        } else {
                            globalCategoryList.push(item);
                        }
                    });
                    if (this.selectedCategoryList.length > 0) {
                        this.globalCategory = 0;
                    } else {
                        this.globalCategory = 1;
                    }
                    // 如果分类刷新则尝试更新queryInfo中的分类数据
                    const flatCategoryList = [];
                    if (Array.isArray(list)) {
                        this.flattenCategoryList(list, flatCategoryList);
                        this.queryInfo.categoryList = [
                            ...flatCategoryList,
                        ];
                    }
                    this.categoryList = categoryList;
                    this.globalCategoryList = globalCategoryList;
                    this.debounceQueryData();
                });
            },
            // 去除CategoryList的层级
            flattenCategoryList(input, output) {
                if (!Array.isArray(input) || input.length === 0) {
                    return;
                }
                input.forEach(item => {
                    output.push({...item});
                    this.flattenCategoryList(item.children, output);
                });
            },
            checkQueryItem() {
                this.queryItem.ownerAccountId = null;
            },
            queryData(reload) {
                if (this.categoryListLoaded === false) {
                    return;
                }
                this.queryItem.categoryIdList = [];
                this.queryItem.isSetCategory = null;
                for (var i = 0; i < this.selectedCategoryList.length; i++) {
                    this.queryItem.categoryIdList.push(this.selectedCategoryList[i].id);
                }
                if (this.globalCategory === 2) {
                    this.queryItem.isSetCategory = false;
                }
                this.saveStatus();
                this.$nextTick(() => {
                    if (this.viewItem.viewShowType == 'table' && this.$refs.table) {
                        this.$refs.table.loadData(reload);
                    }
                    if (this.viewItem.viewShowType == 'split' && this.$refs.split) {
                        this.$refs.split.loadData();
                    }
                    if (this.viewItem.viewShowType == 'kanban' && this.$refs.kanban) {
                        this.$refs.kanban.loadData();
                    }
                    if (this.viewItem.viewShowType == 'stat' && this.$refs.stat) {
                        this.$refs.stat.loadData();
                    }
                    if (this.viewItem.viewShowType == 'gantt' && this.$refs.gantt) {
                        this.$refs.gantt.loadData(reload);
                    }
                    if (this.viewItem.viewShowType == 'cal' && this.$refs.cal) {
                        this.$refs.cal.loadData();
                    }
                    if (this.viewItem.viewShowType == 'week' && this.$refs.week) {
                        this.$refs.week.loadData();
                    }
                    if (this.viewItem.viewShowType == 'mindmap' && this.$refs.mindmap) {
                        this.$refs.mindmap.loadData();
                    }

                });
            },
            moreOptionHasValue() {
                if (this.queryItem == null || this.queryInfo == null) {
                    return false;
                }
                if (this.queryItem.createAccountId != null) {
                    return true;
                }
                if (this.queryItem.priorityInList && this.queryItem.priorityInList.length > 0) {
                    return true;
                }
                if (this.queryItem.releaseInList && this.queryItem.releaseInList.length > 0) {
                    return true;
                }
                if (this.queryItem.subSystemInList && this.queryItem.subSystemInList.length > 0) {
                    return true;
                }
                for (var i = 0; i < this.queryInfo.fieldList.length; i++) {
                    var field = this.queryInfo.fieldList[i];
                    if (!field.isSystemField) {
                        var t = this.queryItem.customFields['field_' + field.id];
                        if (t != null && t != '') {
                            return true;
                        }
                    }
                }
                return false;
            },
            saveStatus() {
                app.saveObject('TaskPage.queryItem' + this.type + '.' + this.projectId, this.queryItem);
                app.saveObject('TaskPage.viewItem' + this.type + '.' + this.projectId, this.viewItem);
            },
            loadStatus() {
                var t = app.loadObject('TaskPage.viewItem' + this.type + '.' + this.projectId);
                if (t != null) {
                    this.viewItem = copyObject(this.viewItem, t);
                }
                var query = app.loadObject('TaskPage.queryItem' + this.type + '.' + this.projectId);
                if (query != null) {
                    this.queryItem = copyObject(this.queryItem, query);
                }
            },
            getSelectWidth(list) {
                if (list == null || list.length == 0) {
                    return '150px';
                }
                var computedWidth = (list.length * 100);
                if (computedWidth < 150) {
                    return 150 + 'px';
                }
                if (computedWidth > 350) {
                    return '350px';
                }
                return computedWidth + 'px';
            },
            showTask(id) {
                this.currentTaskId = id;
            },
            closeTaskPopup() {
                app.loadPage(this.args.$page);
            },
            showCreateDialog() {
                app.showDialog(TaskCreateDialog, {
                    projectId: this.queryInfo.project.id,
                    type: this.type,
                    iterationId: this.queryItem.iterationId == null ? 0 : this.queryItem.iterationId,
                });
            },
            syncTasksFromProject() {
                this.syncTasking = true;
                app.showLoading('正在同步');
                app.invoke('BizAction.syncTasksFromProject', [app.token], () => {
                    app.toast(this.$t('同步成功'));
                    this.debounceQueryData();
                    this.syncTasking = false;
                    app.hideLoading();
                }, () => {
                    app.toast(this.$t('同步失败'));
                    this.syncTasking = false;
                    app.hideLoading();
                });
            },
            viewChangeClick(item) {
                this.viewPopuptipVisible = false;
                this.viewItem.viewShowType = item;
                this.$nextTick(this.debounceQueryData);
            },
            getDepartmentList() {
                app.invoke('BizAction.getDepartmentTree', [app.token, false], (info) => {
                    travalTree(info[0], (item) => {
                        item.label = item.title;
                        if (item.children.length == 0) {
                            delete item.children;
                        }
                    })
                    this.departmentList = info;
                })
            },
        },
    };
</script>
