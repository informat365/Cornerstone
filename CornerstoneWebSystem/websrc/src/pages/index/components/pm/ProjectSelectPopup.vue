<style scoped>
    .group-label {
        color: #999;
        font-size: 12px;
        padding-left: 5px;
        margin-top: 5px;
    }

    .search-no-data {
        text-align: center;
        font-size: 16px;
        color: #999;
        font-weight: bold;
        padding: 30px 10px;
    }

    .filter-project-star {
        color: #fdc043 !important;
    }

    .filter-project-start-empty {
        font-size: 20px;
        color: #999;
        vertical-align: top;
        margin-left: 10px;
        cursor: pointer;
        margin-left: 2px;
        vertical-align: sub;
    }


</style>
<i18n>
    {
    "en": {
    "搜索项目":"Search Project",
    "首页":"Index",
    "暂无数据":"No Data",
    "未分组":"Un grouped",
    "工作台":"Dashboard"
    },
    "zh_CN": {
    "搜索项目":"搜索项目",
    "首页":"工作台",
    "暂无数据":"暂无数据",
    "未分组":"未分组",
    "工作台":"工作台"
    }
    }
</i18n>

<template>
    <Poptip transfer ref="selectProjectPoptip" class="poptip-full" trigger="click">
        <IconButton icon="ios-arrow-down" :size="16"></IconButton>

        <div slot="content" style="width:270px;">
            <div style="padding:20px;padding-bottom:10px">
                <Input style="width:220px" v-model="projectSearchContent" :placeholder="$t('搜索项目')" icon="ios-search" />
                <Icon
                    @click.native="isFilterStar=!isFilterStar"
                    class="filter-project-start-empty"
                    :class="{'filter-project-star':isFilterStar}"
                    type="md-star" />
            </div>
            <Row
                style="border-bottom:1px solid #eee" class="search-project-name" @click.native="showDashboard">
                <Col span="2">&nbsp;</Col>
                <Col span="22" style="padding-left:5px">{{$t('首页')}}</Col>
            </Row>

            <div
                class="scrollbox" v-if="list.length>0" style="padding-bottom:10px;max-height:300px;overflow:auto">

                <div v-for="group in groupProjectList" :key="'g_'+group.name">
                    <div class="group-label">{{group.name}}</div>
                    <template v-for="item in group.list">
                        <div :key="item.id" v-if="isNotProjectSet(item.templateUuid)">
                            <Row @click.native="showProjectInfo(item)" class="search-project-name">
                                <Col span="2" style="height:18px">
                                    <Icon
                                        v-if="item.star"
                                        class="project-star"
                                        style="margin-left:2px;vertical-align:sub;"
                                        type="md-star" />&nbsp;
                                </Col>
                                <Col span="22" style="padding-left:5px">
                                    <span class="popup-search-name text-no-wrap">{{item.name}}</span>
                                </Col>
                            </Row>
                        </div>
                    </template>
                </div>
                <div class="search-no-data" v-if="groupProjectList.length===0">
                    {{$t('暂无数据')}}
                </div>
            </div>
        </div>
    </Poptip>
</template>

<script>
    export default {
        mixins: [componentMixin],
        props: ['list'],
        data() {
            return {
                projectSearchContent: null,
                isFilterStar: false,
            };
        },
        mounted() {

        },
        computed: {
            groupProjectList() {
                const list = [];
                const groupMap = {};
                for (let i = 0; i < this.list.length; i++) {
                    const t = this.list[i];
                    if (t.group == null || t.group === '') {
                        t.group = this.$t('未分组');
                    }
                    if (!this.isFilterProject(t.name)
                        && !this.isFilterProject(t.group)
                        && !this.isFilterProject(t.pinyinShortName)) {
                        continue;
                    }
                    if (this.isFilterStar && t.star === false) {
                        continue;
                    }
                    if (groupMap[t.group] == null) {
                        groupMap[t.group] = {
                            name: t.group,
                            list: [],
                        };
                        const q = groupMap[t.group];
                        list.push(q);
                    }
                    groupMap[t.group].list.push(t);
                }
                return list;
            },
        },
        methods: {
            showDashboard() {
                this.$refs.selectProjectPoptip.ok();
                app.loadPage('/pm/index/dashboard');
                this.projectName = this.$t('工作台');
            },
            isFilterProject(name) {
                if (name == null) {
                    return false;
                }
                if (!this.projectSearchContent) {
                    return true;
                }
                const t = name.toLowerCase();
                const q = this.projectSearchContent.toLowerCase();
                return t.indexOf(q) !== -1;
            },
            showProjectInfo(item) {
                this.$refs.selectProjectPoptip.ok();
                app.loadPage('/pm/project/' + item.uuid + '/project');
                //TODO 切换项目时自动切换到对应的模块，目前确实，切换到的项目是否包含当前的模块，待处理
                /**
                 const params = this.$route.params;
                 const path = `/pm/project/${ item.uuid }/${ params.page || '/project' }`;
                 console.log('showProjectInfo',item);
                 app.loadPage(path)
                 */
            },
        },
    };
</script>
