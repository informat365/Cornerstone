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
    "en": {
    "创建人": "Creator",
    "审批人": "Auditor",
    "成员类型": "Member",
    "项目筛选":"Project",
    "类型筛选":"Type",
    "状态筛选":"Status"
    },
    "zh_CN": {
    "创建人": "创建人",
    "审批人": "审批人",
    "成员类型": "成员类型",
    "项目筛选":"项目筛选",
    "类型筛选":"类型筛选",
    "状态筛选":"状态筛选"
    }
    }
</i18n>
<template>
    <div>
        <div class="menu-item-group">{{$t('成员类型')}}</div>
        <div :class="{'filter-tag-select':dateType==1}" @click="setAccountFilter(1)"
             class="filter-tag">{{$t('创建人')}}
        </div>
        <div :class="{'filter-tag-select':dateType==2}" @click="setAccountFilter(2)"
             class="filter-tag">{{$t('审批人')}}
        </div>

        <div class="menu-item-group" v-if="filterProjectList.length>0">{{$t('项目筛选')}}</div>
        <div :class="{'filter-tag-select':hasFilter('projectId',item.name)}"
             @click="toggleFilter('projectId',item.name)" class="filter-tag" v-for="item in filterProjectList"
             :key="'fp_'+item.name">{{item.name}}<span class="filter-count">{{item.count}}</span>
        </div>

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

        data() {
            return {
                filterProjectList: [],
                filterStatusList: [],
                filterTypeList: [],
                alterationList: [],
                filteredAlterationList:[],
                filters: {
                    objectType: [],
                    status: [],
                    projectId: [],
                },
                dateType: 0,
            }
        },
        mounted() {
            this.loadAlterationData()
        },
        methods: {
            pageMessage(type,content){
                if(type=='task.freeze'){
                    this.reloadData();
                }
            },
            setAccountFilter(value) {
                if (this.dateType == value) {
                    this.dateType = null;
                } else {
                    this.dateType = value;
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
                this.loadAlterationData();
            },
            loadAlterationData() {
                app.invoke("TaskAlterationAction.getTaskAlterationList", [app.token,{ownerAccountOrCreateAccountIdId:app.account.id,isFinishSort:1,pageSize:10000}], list => {
                    this.alterationList = list;
                    var allProject = {};
                    var allStatus = {};
                    var allType = {};
                    for (var i = 0; i < list.length; i++) {
                        var t = list[i];
                        if(t.flowStatus==0){
                            t.flowStatusName='已取消';
                            t.flowStatusColor='#ddd';
                        }
                        if (allStatus[t.flowStatusName] == null) {
                            allStatus[t.flowStatusName] = {
                                name: t.flowStatusName,
                                count: 1,
                            }
                        } else {
                            allStatus[t.flowStatusName].count++;
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
                    this.$emit('list-changed', this.alterationList)
                });
            },
            listChanged() {
                let list = this.filteredTaskList()||[];
                this.$emit('list-changed', list)
            },
            filteredTaskList(){
                console.log("xxxxxxxxxxxxxx",this.filters)
                var accountId = app.account.id;
                var list = [];
                if(!this.alterationList){
                    return [];
                }
                for (var i = 0; i < this.alterationList.length; i++) {
                    var t = this.alterationList[i];
                    if (this.dateType == 1) {
                        if (t.createAccountId!=accountId) {
                            continue;
                        }
                    }
                    if (this.dateType == 2) {
                        if (!Array.contains(t.ownerIdList,accountId,(a)=>a)) {
                            continue;
                        }
                    }
                    if (this.filters.projectId.length > 0) {
                        if (!this.filters.projectId.contains(t.projectName)) {
                            continue;
                        }
                    }
                    if (this.filters.status.length > 0) {
                        if (!this.filters.status.contains(t.flowStatusName)) {
                            continue;
                        }
                    }
                    if (this.filters.objectType.length > 0) {
                        if (!this.filters.objectType.contains(t.objectTypeName)) {
                            continue;
                        }
                    }
                    list.push(t);
                }
                return list;
            },
        }
    }
</script>
