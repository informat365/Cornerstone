<style scoped>
    .menu-item-group {
        font-size: 12px;
        color: #999;
        padding: 8px 0;
        margin-top: 20px;
    }

    .filter-tag {
        font-size: 13px;
        padding: 3px 10px;
        background-color: #eee;
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
        background-color: #009cf1;
        color: #fff;
    }
</style>
<i18n>
    {
    "en": {
    "分类筛选": "Category filter",
    "优先级筛选": "Priority filter",
    "状态筛选": "Status filter"
    },
    "zh_CN": {
    "分类筛选": "分类筛选",
    "优先级筛选": "优先级筛选",
    "状态筛选": "状态筛选"
    }
    }
</i18n>
<template>
    <div class="dashboard-project-set-filter-view">
        <template v-if=" categoryList && categoryList.length > 0">
            <div class="menu-item-group">{{$t('分类筛选')}}</div>
            <div
                v-for="item in categoryList"
                :key="'c_'+item.id"
                :class="{'filter-tag-select': categorySelected(item)}"
                @click="toggleCategory(item)"
                class="filter-tag">{{item.name}}
            </div>
        </template>
        <!--        <template v-if=" priorityList && priorityList.length > 0">-->
        <!--            <div class="menu-item-group">{{$t('优先级筛选')}}</div>-->
        <!--            <div-->
        <!--                v-for="item in priorityList"-->
        <!--                :key="'p_'+item.id"-->
        <!--                :class="{'filter-tag-select':query.priority===item.id}"-->
        <!--                @click="togglePriority(item)"-->
        <!--                class="filter-tag">{{item.name}}-->
        <!--            </div>-->
        <!--        </template>-->
        <template v-if=" statusList && statusList.length > 0">
            <div class="menu-item-group">{{$t('状态筛选')}}</div>
            <div
                v-for="item in statusList"
                :key="'s_'+item.id"
                :class="{'filter-tag-select':query.status===item.id}"
                @click="toggleStatus(item)"
                class="filter-tag">{{item.name}}
            </div>
        </template>
    </div>
</template>

<script>
    export default {
        mixins: [componentMixin],
        data() {
            return {
                query: {
                    categoryIdList: null,
                    priority: null,
                    status: null,
                },
                categoryList: [],
                priorityList: [],
                statusList: [],
                projectList: [],
            };
        },
        mounted() {
            this.loadData(true);
        },
        methods: {
            categorySelected(category) {
                if (!Array.isArray(this.query.categoryIdList)) {
                    return false;
                }
                return this.query.categoryIdList.indexOf(category.id) > -1;
            },
            filterList() {
                return this.projectList;
            },
            toggleCategory(category) {
                if (!Array.isArray(this.query.categoryIdList)) {
                    this.query.categoryIdList = [category.id];
                    this.loadData();
                    return;
                }
                const index = this.query.categoryIdList.indexOf(category.id);
                if (index > -1) {
                    this.query.categoryIdList.splice(index, 1);
                    this.loadData();
                    return;
                }
                this.query.categoryIdList.push(category.id);
                this.loadData();
            },
            togglePriority(priority) {
                if (this.query.priority === priority.id) {
                    this.query.priority = null;
                } else {
                    this.query.priority = priority.id;
                }
                this.loadData();
            },
            toggleStatus(status) {
                if (this.query.status === status.id) {
                    this.query.status = null;
                } else {
                    this.query.status = status.id;
                }
                this.loadData();
            },
            loadTaskQueryInfo(projectUuid, objectType) {
                if (!projectUuid || !objectType) {
                    return;
                }
                app.invoke('BizAction.getTaskQueryInfo', [app.token, projectUuid, objectType], result => {
                    if (!result) {
                        return;
                    }
                    this.categoryList = result.categoryList;
                    this.priorityList = result.priorityList;
                    this.statusList = result.statusList;
                });
            },
            loadData(loadTaskQueryInfo) {
                clearInterval(this.timeoutId);
                this.timeoutId = setTimeout(() => {
                    this.loadProjectData(loadTaskQueryInfo);
                }, 250);
            },
            loadProjectData(loadTaskQueryInfo) {
                const query = JSON.parse(JSON.stringify(this.query));
                if (Array.isArray(query.categoryIdList) && query.categoryIdList.length === 0) {
                    query.categoryIdList = null;
                }
                app.invoke('BizAction.getProjectSetInfo', [app.token, query], result => {
                    if (!result || !result.list) {
                        this.projectList = [];
                    } else {
                        this.projectList = result.list;
                    }
                    if (loadTaskQueryInfo) {
                        this.loadTaskQueryInfo(result.projectUuid, result.objectType);
                    }
                    this.$emit('list-changed', this.filterList());
                });
            },
        },
    };
</script>
