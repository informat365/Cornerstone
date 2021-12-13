<style lang="less" scoped>
    .nav-tab-bar {
        color: #444;
        font-size: 14px;
        font-weight: 500;
        padding: 6px 0;
        height: 48px;
        overflow: hidden;

        /deep/ .ivu-tabs-bar {
            border-bottom: none;
            margin-bottom: 0;

            .ivu-tabs-nav {

                .ivu-tabs-ink-bar {
                    background-color: #2391ff;
                }

                .ivu-tabs-tab {
                    font-size: 14px;
                    padding: 0 2px;
                    height: 36px;
                    line-height: 36px;
                    transition: all 0.1s;

                    &-active {
                        color: #2391ff;
                        font-weight: bold;
                    }

                    &:hover {
                        color: #5391f0;
                    }

                    &:active {
                        color: #2391ff;
                    }
                }
            }
        }
    }
</style>
<template>
    <Tabs class="nav-tab-bar" v-if="tabsShow" v-model="currentModule" @on-click="onTabClick">
        <template v-for="item in moduleList">
            <TabPane
                :key="item.id" :label="item.name" :name="item.url"></TabPane>
        </template>
    </Tabs>
</template>
<script>
    export default {
        name: 'ModuleTabbar',
        props: ['value', 'current'],
        data() {
            return {
                currentModule: this.current,
                moduleList: [],
                tabsShow:true
            };
        },
        watch: {
            value() {
                this.setupModule();
            },
            current(val) {
                this.currentModule = val;
            },
        },
        methods: {
            setupModule() {
                if (!Array.isArray(this.value) || this.value.length === 0) {
                    this.moduleList = [];
                    return;
                }
                this.tabsShow = false;
                const moduleList = [];
                this.value.forEach(item => {
                    if (!this.isModuleShow(item)) {
                        return;
                    }
                    moduleList.push(item);
                });
                this.moduleList = moduleList;
                this.$nextTick(()=>{
                    this.tabsShow = true;
                });
            },
            isModuleShow(item) {
                if (item.objectType > 0) {
                    return app.prjPerm('task_list_' + item.objectType);
                } else {
                    if (item.url === 'agile') {
                        return app.prjPerm(['agility_iteration', 'agility_release', 'agility_subsystem']);
                    }
                    if (item.url === 'file') {
                        return app.prjPerm('file_list');
                    }
                    if (item.url === 'wiki') {
                        return app.prjPerm('wiki_list');
                    }
                    if (item.url === 'devops') {
                        return app.prjPerm('devops_list');
                    }
                    if (item.url === 'delivery') {
                        return app.prjPerm('delivery_list');
                    }
                    if (item.url === 'stage') {
                        return app.prjPerm('stage_list');
                    }
                    if (item.url === 'landmark') {
                        return app.prjPerm('landmark_list');
                    }
                    return false;
                }
            },
            onTabClick(url) {
                this.$emit('change', url);
            },
        },
    };
</script>
