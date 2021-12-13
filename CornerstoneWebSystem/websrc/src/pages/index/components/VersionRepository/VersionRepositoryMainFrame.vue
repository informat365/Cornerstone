<style lang="less" scoped>
    .version-repository {
        position: relative;
        height: 100%;
        width: 100%;
        display: flex;
        flex-direction: column;
        overflow: hidden;

        &-header {
            position: relative;
            width: 100%;
            border-bottom: 1px solid #ccc;
            padding: 5px 20px;
            overflow: hidden;

            &-wrap {
                position: relative;
                max-width: 1000px;
                margin: 0 auto;
                display: flex;
                align-items: center;
                align-content: center;
                justify-content: space-between;
                overflow: hidden;

                .actions {

                }

                .pager {
                    font-size: 12px;
                    font-weight: 700;
                    color: #999;
                }
            }
        }

        &-body {
            flex: 1;
            overflow: auto;
            overflow-scrolling: touch;
        }

        &-content {
            position: relative;
            max-width: 1000px;
            margin: 0 auto;
        }

    }

    .nodata {
        padding: 100px;
        font-size: 24px;
        color: #999;
        text-align: center;

        .actions {
            padding-top: 40px;
        }
    }

    .version-list {
        display: flex;
        align-items: center;
        align-content: center;
        flex-wrap: wrap;
        padding-bottom: 20px;

        &-card {
            margin-left: 10px;

            &.row-first {
                margin-left: 0;
            }
        }

        &-add {
            position: relative;
            background: #fff;
            box-shadow: 0 2px 6px 0 hsla(0, 0%, 83.1%, .45);
            border-radius: 4px;
            border: 1px solid #eee;
            padding: 15px;
            margin-top: 15px;
            margin-left: 10px;
            width: 320px;
            height: 114px;
            cursor: pointer;
            display: flex;
            align-items: center;
            align-content: center;
            flex-direction: column;
            justify-content: center;
            color: #888;

            &:hover {
                color: #17a7ed;
            }
        }

        .repository-more-icon {
            &:hover {
                background-color: #000;
            }
        }
    }
</style>
<i18n>
    {
    "en": {
    "总计条数据":"total {0} items",
    "每页条":"{0} per page",
    "第页":"{0}/{1}",
    "保存": "保存",
    "基本信息": "基本信息",
    "表单设计": "表单设计",
    "工作流": "工作流",
    "填写权限": "填写权限",
    "数据权限": "数据权限",
    "流程设计": "流程设计",
    "当前页面没有保存是否保存": "当前页面没有保存，是否保存？"
    },
    "zh_CN": {
    "总计条数据":"总计 {0} 条数据",
    "每页条":"每页 {0} 条",
    "第页":"第 {0}/{1} 页",
    "保存": "保存",
    "基本信息": "基本信息",
    "表单设计": "表单设计",
    "工作流": "工作流",
    "填写权限": "填写权限",
    "数据权限": "数据权限",
    "流程设计": "流程设计",
    "当前页面没有保存是否保存": "当前页面没有保存，是否保存？"
    }
    }
</i18n>
<template>
    <div class="version-repository">
        <div class="version-repository-header" v-show="!noRepositoryData">
            <div class="version-repository-header-wrap">
                <div class="actions">
                    <PagePoptipSelect
                        v-model="repositoryId"
                        :list="repositoryList"
                        :query="repositoryPageQuery"
                        ref="repositorySelect"
                        width="250"
                        vkey="id"
                        vlabel="name"
                        @on-page-change="onRepositoryChange">
                        <template
                            v-slot:item="{data}">
                            <div
                                class="repository-select-item">
                                <div class="repository-select-item-label">
                                    {{ data.name }}
                                </div>
                                <Icon
                                    v-if="perm('version_repository_add')"
                                    class="repository-select-item-more-icon"
                                    type="ios-more"
                                    size="16"
                                    @click.native.stop="onClickRepositoryEdit(data)" />
                            </div>
                        </template>
                    </PagePoptipSelect>
                    <Button
                        icon="md-add-circle"
                        v-if="perm('version_repository_add')"
                        style="margin-left: 20px;"
                        @click.native="onClickRepositoryAdd">
                        添加版本库
                    </Button>
                </div>
                <div class="pager">
                    <span class="vcenter totalspan">{{$t('总计条数据',[pageQuery.totalCount])}}</span>
                    <Poptip transfer placement="bottom" v-model="pageSizePopuptipVisible">
                        <span style="margin-left:5px;cursor:pointer" class="vcenter pagespan">{{$t('每页条',[pageQuery.pageSize])}}</span>
                        <div slot="content">
                            <PopupTipItem
                                @click="onSelectPageSize(item)"
                                :name="item+''"
                                :selected="pageQuery.pageSize===item"
                                v-for="item in [30,60]"
                                :key="'ps'+item" />
                        </div>
                    </Poptip>
                    <IconButton
                        :disabled="pageQuery.pageIndex===1"
                        :size="15"
                        @click="onChangePage(-1)"
                        icon="md-arrow-round-back" />
                    <span class="vcenter">{{$t('第页',[pageQuery.pageIndex,pageQuery.totalPage])}}</span>
                    <IconButton
                        :disabled="pageQuery.pageIndex===pageQuery.totalPage"
                        :size="15"
                        @click="onChangePage(1)"
                        icon="md-arrow-round-forward"></IconButton>
                </div>
            </div>
        </div>
        <div class="version-repository-body scrollbox">
            <div class="version-repository-content">
                <template v-if="noRepositoryData">
                    <div class="nodata">
                        <div class="text">暂无可用版本库</div>
                        <div class="actions" v-if="perm('version_repository_add')">
                            <Button
                                icon="md-add-circle" type="primary" @click.native="onClickRepositoryAdd">
                                添加版本库
                            </Button>
                        </div>
                    </div>
                </template>
                <template v-else>
                    <div class="nodata" v-show="Array.isArray(versionList) && versionList.length === 0">
                        <div class="text">暂无可用版本</div>
                        <div class="actions" v-if="perm('version_add')">
                            <Button
                                icon="md-add-circle" type="primary" @click.native="onClickVersionAdd">
                                添加版本
                            </Button>
                        </div>
                    </div>
                </template>
                <div class="version-list" v-if="Array.isArray(versionList) && versionList.length > 0">
                    <div
                        v-if="perm('version_add')"
                        class="version-list-card version-list-add row-first"
                        @click="onClickVersionAdd">
                        <Icon type="ios-add" size="30" />
                        <div class="text">添加版本</div>
                    </div>
                    <template v-for="(item,index) in versionList">
                        <VersionCard
                            @click.native="onClickVersion(item)"
                            class="version-list-card"
                            :class="{ 'row-first': index % 3 === 2 }"
                            :version="item"
                            :key="item.id" />
                    </template>
                </div>
            </div>
        </div>
        <ModalVersionRepositoryEdit ref="repositoryEdit" />
        <ModalVersionEdit ref="versionEdit" />
        <DrawerVersion ref="versionDetail" />
    </div>
</template>
<script>
    import DrawerVersion from './components/DrawerVersion';
    import ModalVersionEdit from './components/ModalVersionEdit';
    import ModalVersionRepositoryEdit from './components/ModalVersionRepositoryEdit';
    import PagePoptipSelect from './components/PagePoptipSelect';
    import VersionCard from './components/VersionCard';
    import VueMixin from './vue.mixin';

    export default {
        components: { PagePoptipSelect, DrawerVersion, VersionCard, ModalVersionEdit, ModalVersionRepositoryEdit },
        mixins: [VueMixin, componentMixin],
        data() {
            return {
                companyUuid: null,
                repositoryList: null,
                versionList: null,
                repositoryId: null,
                pageSizePopuptipVisible: false,
                repositoryPageQuery: {
                    pageIndex: 1,
                    pageSize: 20,
                    totalCount: 0,
                    totalPage: 0,
                },
                pageQuery: {
                    pageIndex: 1,
                    pageSize: 30,
                    totalCount: 0,
                    totalPage: 0,
                },
            };
        },
        watch: {
            repositoryId() {
                this.loadVersionList(0, true);
            },
        },
        computed: {
            noRepositoryData() {
                return Array.isArray(this.repositoryList) && this.repositoryList.length === 0 && this.repositoryPageQuery.pageIndex === 1 && !this.loading;
            },
        },
        created() {
            //原来的版本在头像侧边栏显示
            document.title = '版本库管理 | CORNERSTONE';
        },
        mounted() {
            this.companyUuid = this.$route.params.uuid;
            app.getLoginInfo(() => {
                this.loadRepositoryList();
            });
            app.onMessage('AppEvent', (event) => {
                if (event.type === 'version.item.edit') {
                    this.loadVersionList();
                    return;
                }
                if (event.type === 'version.repository.edit') {
                    this.loadRepositoryList();
                }
            });
        },
        methods: {
            onRepositoryChange(pageAddition) {
                this.loadRepositoryList(pageAddition);
            },
            loadRepositoryList(pageAddition = 0, reload) {
                if (this.loading) {
                    return;
                }
                if (this.repositoryPageQuery.pageIndex + pageAddition < 1) {
                    return;
                }
                if (reload) {
                    this.repositoryPageQuery.pageIndex = 1;
                } else {
                    if (this.repositoryPageQuery.pageIndex + pageAddition < 1) {
                        return;
                    }
                    this.repositoryPageQuery.pageIndex += pageAddition;
                }
                this.loading = true;
                this.request('BizAction.getCompanyVersionRepositoryList', [this.repositoryPageQuery], (res) => {
                    this.loading = false;
                    if (!res || !Array.isArray(res.list)) {
                        if (this.repositoryPageQuery.pageIndex === 1 || reload) {
                            this.repositoryList = [];
                            this.repositoryPageQuery.totalCount = 0;
                            this.repositoryPageQuery.totalPage = 0;
                        }
                        return;
                    }
                    this.repositoryList = res.list;
                    if (!Number.check(this.repositoryId, 1)) {
                        this.repositoryId = this.repositoryList[0].id;
                    }
                    this.repositoryPageQuery.totalCount = Number.parseIntWithDefault(res.count, 0);
                    this.repositoryPageQuery.totalPage = Math.ceil(this.repositoryPageQuery.totalCount / this.repositoryPageQuery.pageSize);
                }, (code, message) => {
                    app.toast(`加载数据失败，${ message || '' }`);
                    this.loading = false;
                });
            },
            onClickRepositoryAdd() {
                this.$refs.repositoryEdit.show();
            },
            onClickRepositoryEdit(item) {
                this.$refs.repositorySelect.visible(false);
                this.$nextTick(() => {
                    this.$refs.repositoryEdit.show(item.id);
                });
            },
            onClickVersion(item) {
                this.$refs.versionDetail.show(item.id);
            },
            onClickVersionAdd() {
                if (!Number.check(this.repositoryId, 1)) {
                    app.toast('请选择版本库');
                    return;
                }
                this.$refs.versionEdit.show(this.repositoryId);
            },
            onSelectPageSize(item) {
                this.pageSizePopuptipVisible = false;
                this.pageQuery.pageIndex = 1;
                this.pageQuery.pageSize = Number.parseIntWithDefault(item, 50);
                this.loadVersionList();
            },
            onChangePage(pageAddition) {
                this.loadVersionList(pageAddition);
            },
            loadVersionList(pageAddition = 0, reload) {
                if (this.loading) {
                    return;
                }
                if (!Number.check(this.repositoryId, 1)) {
                    this.versionList = [];
                    return;
                }
                if (reload) {
                    this.pageQuery.pageIndex = 1;
                } else {
                    if (this.pageQuery.pageIndex + pageAddition < 1) {
                        return;
                    }
                    this.pageQuery.pageIndex += pageAddition;
                }
                this.loading = true;
                this.request('BizAction.getCompanyVersionList', [{
                    ...this.pageQuery,
                    repositoryId: this.repositoryId,
                }], (res) => {
                    this.loading = false;
                    if (!res || !Array.isArray(res.list)) {
                        if (this.pageQuery.pageIndex === 1 || reload) {
                            this.versionList = [];
                            this.pageQuery.totalCount = 0;
                            this.pageQuery.totalPage = 0;
                        }
                        return;
                    }
                    this.versionList = res.list;
                    this.pageQuery.totalCount = Number.parseIntWithDefault(res.count, 0);
                    this.pageQuery.totalPage = Math.ceil(this.pageQuery.totalCount / this.pageQuery.pageSize);
                }, (code, message) => {
                    app.toast(`加载版本数据失败，${ message || '' }`);
                    this.loading = false;
                });
            },
        },
    };
</script>
