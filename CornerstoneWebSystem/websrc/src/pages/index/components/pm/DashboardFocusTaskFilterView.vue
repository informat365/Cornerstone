<style scoped>
    .menu-item-group {
        font-size: 12px;
        color: #999;
        padding: 8px 0px;
        margin-top: 20px;
    }

    .filter-tag {
        font-size: 13px;
        padding: 3px 10px;
        background-color: #EEEEEE;
        color: #777;
        display: inline-block;
        margin-right: 10px;
        margin-top: 10px;
        border-radius: 13px;
        cursor: pointer;
        user-select: none;
        max-width: 200px;
        text-overflow: ellipsis;
        overflow: hidden;
        white-space: nowrap;
    }

    .filter-count {
        font-size: 12px;
        margin-left: 5px;
        font-weight: bold;
    }

    .filter-tag-select {
        background-color: #009CF1;
        color: #fff;
    }
</style>
<i18n>
    {
    "zh_CN":{
    "日期筛选":"日期筛选",
    "已超期":"已超期",
    "本周到期":"本周到期",
    "今日到期":"今日到期",
    "计划中":"计划中",
    "项目筛选":"项目筛选",
    "类型筛选":"类型筛选",
    "责任人类型":"责任人类型",
    "主要责任人":"主要责任人",
    "次要责任人":"次要责任人",
    "状态筛选":"状态筛选"
    },
    "en": {
    "日期筛选":"Date Filter",
    "已超期":"Overdue",
    "本周到期":"This Week",
    "今日到期":"Today",
    "计划中":"Planing",
    "项目筛选":"Project Filter",
    "类型筛选":"Type Filter",
    "责任人类型":"Owner Type",
    "主要责任人":"Primary",
    "次要责任人":"Secondary",
    "状态筛选":"Status Filter"
    }
    }
</i18n>
<template>
    <div>
        <div class="menu-item-group">{{$t('责任人类型')}}</div>
        <div :class="{'filter-tag-select':hasFilter('resType',1)}" @click="toggleFilter('resType',1)" class="filter-tag">
            {{$t('主要责任人')}}
        </div>
        <div :class="{'filter-tag-select':hasFilter('resType',2)}" @click="toggleFilter('resType',2)" class="filter-tag">
            {{$t('次要责任人')}}
        </div>

        <div class="menu-item-group">{{$t('日期筛选')}}</div>
        <div :class="{'filter-tag-select':filters.dateType==1}" @click="setDateFilter(1)" class="filter-tag">
            {{$t('已超期')}}
        </div>
        <div :class="{'filter-tag-select':filters.dateType==2}" @click="setDateFilter(2)" class="filter-tag">
            {{$t('本周到期')}}
        </div>
        <div :class="{'filter-tag-select':filters.dateType==3}" @click="setDateFilter(3)" class="filter-tag">
            {{$t('今日到期')}}
        </div>
        <div :class="{'filter-tag-select':filters.dateType==4}" @click="setDateFilter(4)" class="filter-tag">
            {{$t('计划中')}}
        </div>

        <div class="menu-item-group" v-if="filterProjectList.length>0">{{$t('项目筛选')}}</div>
        <div :class="{'filter-tag-select':hasFilter('projectId',item.name)}"
             @click="toggleFilter('projectId',item.name)" class="filter-tag" v-for="item in filterProjectList"
             :key="'fp_'+item.name">{{item.name}}<span class="filter-count">{{item.count}}</span></div>

        <div class="menu-item-group" v-if="filterTypeList.length>0">{{$t('类型筛选')}}</div>
        <div :class="{'filter-tag-select':hasFilter('objectType',item.name)}"
             @click="toggleFilter('objectType',item.name)" class="filter-tag" v-for="item in filterTypeList"
             :key="'tp_'+item.name">{{item.name}}<span class="filter-count">{{item.count}}</span></div>

        <div class="menu-item-group" v-if="filterStatusList.length>0">{{$t('状态筛选')}}</div>
        <div :class="{'filter-tag-select':hasFilter('status',item.name)}"
             @click="toggleFilter('status',item.name)" class="filter-tag"
             v-for="item in filterStatusList" :key="'st_'+item.name">{{item.name}}<span class="filter-count">{{item.count}}</span>
        </div>
    </div>
</template>

<script>
    export default {
        mixins: [componentMixin],
        props: ['type',"datas"],
        data() {
            return {
                taskList: [],
                filterProjectList: [],
                filterStatusList: [],
                filterTypeList: [],
                filters: {
                    objectType: [],
                    status: [],
                    projectId: [],
                    dateType: null,
                    resType:[]
                },
            }
        },
        mounted() {
            this.loadTaskData(this.type)
        },
        methods: {
            filteredTaskList() {
                var list = [];
                for (var i = 0; i < this.taskList.length; i++) {
                    var t = this.taskList[i];
                    var now = new Date().getTime();
                    if (this.filters.dateType == 1) {
                        if (t.endDate == null || getLeftDays(t.endDate) >= 0) {
                            continue;
                        }
                    }
                    if (this.filters.dateType == 2) {
                        if (t.endDate == null || !dateSameWeek(new Date(t.endDate), new Date())) {
                            continue;
                        }
                    }
                    if (this.filters.dateType == 3) {
                        if (t.endDate == null || !dateSameDay(new Date(t.endDate), new Date())) {
                            continue;
                        }
                    }
                    if (this.filters.dateType == 4) {
                        if (t.endDate != null && t.startDate != null) {
                            continue;
                        }
                    }
                    if (this.filters.projectId.length > 0) {
                        if (!this.filters.projectId.contains(t.projectName)) {
                            continue;
                        }
                    }
                    if (this.filters.status.length > 0) {
                        if (!this.filters.status.contains(t.statusName)) {
                            continue;
                        }
                    }
                    if (this.filters.objectType.length > 0) {
                        if (!this.filters.objectType.contains(t.objectTypeName)) {
                            continue;
                        }
                    }
                    if (this.filters.resType.length > 0) {
                        if(!t.ownerAccountIdList||Array.isEmpty(t.ownerAccountIdList)){
                            continue;
                        }
                        var contain = false;
                        for (let j = 0; j < this.filters.resType.length; j++) {
                            let r = this.filters.resType[j];
                            if(r==1&&app.account.id==t.ownerAccountIdList[0]){
                                contain = true;
                                break;
                            }
                            if(r==2&&t.ownerAccountIdList.length>1&&app.account.id!=t.ownerAccountIdList[0]){
                                contain = true;
                                break;
                            }

                        }
                        if(!contain){
                            continue;
                        }
                    }
                    list.push(t);
                }
                return list;
            },
            setDateFilter(value) {
                if (this.filters.dateType == value) {
                    this.filters.dateType = null;
                } else {
                    this.filters.dateType = value;
                }
                this.listChanged();
            },
            toggleFilter(type, value) {
                var list = this.filters[type];
                for (var i = 0; i < list.length; i++) {
                    if (list[i] == value) {
                        list.splice(i, 1);
                        this.listChanged();
                        return;
                    }
                }
                list.push(value);
                this.listChanged();
            },
            hasFilter(type, value) {
                var list = this.filters[type];
                return list.contains(value)
            },
            reloadData() {
                this.loadTaskData(this.type);
            },
            loadTaskData(type) {
                this.filterProjectList = [];
                this.filterStatusList = [];
                this.filterTypeList = [];
                this.taskList = this.datas;
                var allProject = {};
                var allStatus = {};
                var allType = {};
                for (var i = 0; i < this.datas.length; i++) {
                    var t = this.datas[i];
                    if (allStatus[t.statusName] == null) {
                        allStatus[t.statusName] = {
                            name: t.statusName,
                            count: 1,
                        }
                    } else {
                        allStatus[t.statusName].count++;
                    }
                    if (allType[t.objectTypeName] == null) {
                        allType[t.objectTypeName] = {
                            name: t.objectTypeName,
                            count: 1,
                        }
                    } else {
                        allType[t.objectTypeName].count++;
                    }
                    if (allProject[t.projectName] == null) {
                        allProject[t.projectName] = {
                            name: t.projectName,
                            count: 1,
                        }
                    } else {
                        allProject[t.projectName].count++;
                    }
                }
                this.filterProjectList = [];
                var containsFilter = false
                for (var k in allProject) {
                    this.filterProjectList.push(allProject[k])
                }
                //
                this.filterStatusList = [];
                for (var k in allStatus) {
                    this.filterStatusList.push(allStatus[k])
                }
                //
                this.filterTypeList = [];
                for (var k in allType) {
                    this.filterTypeList.push(allType[k])
                }
                //
                this.checkFilters('projectId', this.filterProjectList);
                this.checkFilters('status', this.filterStatusList);
                this.checkFilters('objectType', this.filterTypeList);
                //
                this.listChanged();
            },
            listChanged() {
                this.$emit('list-changed', this.filteredTaskList())
            },
            checkFilters(filterType, targetList) {
                var newList = [];
                for (var i = 0; i < this.filters[filterType].length; i++) {
                    var name = this.filters[filterType][i];
                    if (targetList.containsKey('name', name)) {
                        newList.push(name);
                    }
                }
                this.filters[filterType] = newList;
            },
        }
    }
</script>
