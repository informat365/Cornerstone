<style lang="less" scoped>
    .table-box {
        background-color: #fff;
        padding: 15px 30px;
        box-shadow: 0px 2px 10px 0px rgba(225, 225, 225, 0.5);
        border: 1px solid rgba(216, 216, 216, 1);
    }

    .table-info {
        color: #999;
        text-align: center;
    }

    .table-count {
        background-color: #E8E8E8;
        color: #666;
        padding: 3px 5px;
        border-radius: 3px;
    }

    .nodata {
        padding: 60px;
        font-size: 20px;
        color: #999;
        text-align: center;
    }

    .wiki-item {
        width: 100%;
        display: flex;
        flex-direction: column;
        padding: 10px 20px;
        border-bottom: 1px solid #ddd;
        cursor: pointer;

        &-header {

            &-name {
                font-weight: 500;
                margin-right: 20px;
            }
        }

        &-footer {
            margin-top: 8px;
            font-size: 13px;
            color: #b5b5b5;
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
    <div>
        <div style="padding:20px;">
            <Row>
                <Col span="8">&nbsp;
                </Col>
                <Col span="8">
                    <div class="table-info">
                        <IconButton :size="15" :disabled="pageQuery.pageIndex==1" @click="loadData(-1)"
                                    icon="md-arrow-round-back"></IconButton>
                        <span class="table-count">{{pageQuery.totalCount}}条数据 ，{{pageQuery.pageIndex}}/{{pageQuery.totalPage}}</span>
                        <IconButton :size="15" :disabled="pageQuery.pageIndex==pageQuery.totalPage"
                                    @click="loadData(1)"
                                    icon="md-arrow-round-forward"></IconButton>
                    </div>
                    &nbsp;
                </Col>
                <Col span="8" style="text-align:right">
                    <div class="actions">
                        <Dropdown style="text-align:left" v-if="perm(['company_wiki_create'])" >
                            <Button type="default" icon="md-add">{{$t('创建页面')}}
                                <Icon style="margin-left:8px" type="ios-arrow-down">
                                </Icon>
                            </Button>
                            <DropdownMenu slot="list">
                                <DropdownItem @click.native="showPageEditor({type:1})">
                                    {{$t('富文本')}}
                                </DropdownItem>
                                <DropdownItem @click.native="showPageEditor({type:2})">
                                    markdown
                                </DropdownItem>
                                <DropdownItem @click.native="showPageEditor({type:3})">
                                    {{$t('思维导图')}}
                                </DropdownItem>
                                <DropdownItem @click.native="showPageEditor({type:4})">
                                    {{$t('表格')}}
                                </DropdownItem>
                                <DropdownItem @click.native="showPageEditor({type:5})">
                                    {{$t('高级思维导图')}}
                                </DropdownItem>
                            </DropdownMenu>
                        </Dropdown>
                    </div>
                </Col>
            </Row>

            <div style="padding: 5px;background: #fff;">
                <div class="chart-opt-bar">
                    <Input
                        placeholder="名称"
                        style="width: 150px;"
                        type="text"
                        @on-change="loadData(0,true)"
                        v-model.trim="formItem.name"
                    />
                    <DataDictSelect
                        style="width: 150px; margin-left: 10px"
                        @change="loadData(0,true)"
                        clearable
                        placeholder="类型"
                        type="WikiPage.type"
                        v-model="formItem.type"
                    ></DataDictSelect>
                    <CompanyUserSelect
                        v-model="formItem.createAccountIdInList"
                        clearable
                        multiple
                        placeholder="创建人"
                        style="width: 150px; margin-left: 10px"
                        @on-change="loadData(0,true)"
                    />
                    <ExDatePicker clearable @on-change="loadData(0,true)" type="date"
                                  style="width: 150px; margin-left: 10px"
                                  :editable="false"
                                  v-model="formItem.createTimeStart"
                                  placeholder="创建开始时间"></ExDatePicker>

                    <ExDatePicker clearable @on-change="loadData(0,true)" type="date" :day-end="true"
                                  style="width: 150px; margin-left: 10px"
                                  :editable="false"
                                  v-model="formItem.createTimeEnd"
                                  placeholder="创建结束时间"></ExDatePicker>
                </div>

                <template v-for="wikiPage in wikiPageList">
                    <div class="wiki-item" @click="showDashboardWikiPageDialog(wikiPage)">
                        <div class="wiki-item-header">
                            <span class="wiki-item-header-name">{{wikiPage.name}}</span>
                            <DataDictLabel type="WikiPage.type" v-model="wikiPage.type"/>
                        </div>
                        <div class="wiki-item-footer">
                            <span>{{wikiPage.createAccountName}}于{{wikiPage.createTime|fmtDateTime}}创建</span>
                            <span style="margin-left: 15px;">最后修订于{{wikiPage.createTime|fmtDateTime}}</span>
                        </div>
                    </div>
                </template>
            </div>
        </div>
    </div>
</template>
<script>


    import DataDictLabel from "../../../../components/ui/DataDictLabel";

    export default {
        components: {DataDictLabel},
        mixins: [componentMixin],
        data() {
            return {
                companyUuid: null,
                wikiPageList: [],
                pageSizePopuptipVisible: false,
                pageQuery: {
                    pageIndex: 1,
                    pageSize: 20,
                    totalCount: 0,
                    totalPage: 0,
                },
                formItem: {},
            };
        },
        computed: {
            noRepositoryData() {
                return Array.isArray(this.wikiPageList) && this.wikiPageList.length === 0 && this.pageQuery.pageIndex === 1;
            },
        },
        mounted() {
            this.companyUuid = app.company.uuid;
            this.loadData(0, true);
            app.onMessage('AppEvent', (event) => {
                if (event.type === 'company.wiki.edit') {
                    this.loadData(0, true);
                }
            });
        },
        methods: {
            showPageEditor(args) {
                args.wikiId = this.selectedWiki;
                if (args.type == 1) {
                    app.showDialog(WikiRichtextEditDialog, args);
                }
                if (args.type == 2) {
                    app.showDialog(WikiMarkdownEditDialog, args);
                }
                if (args.type == 3) {
                    app.showDialog(WikiMindmapEditDialog, args);
                }
                if (args.type == 4) {
                    app.showDialog(WikiSpreadSheetEditDialog, args);
                }
                if (args.type == 5) {
                    app.showDialog(WikiProMindmapEditDialog, args);
                }
            },
            loadData(pageAddition = 0, reload = false) {
                if (this.pageQuery.pageIndex + pageAddition < 1) {
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
                let query = Object.assign({}, this.formItem, this.pageQuery);
                app.invoke('BizAction.getCompanyWikiPageList', [app.token, query], (res) => {
                    if (!res || !Array.isArray(res.list)) {
                        if (this.pageQuery.pageIndex === 1 || reload) {
                            this.wikiPageList = [];
                            this.pageQuery.totalCount = 0;
                            this.pageQuery.totalPage = 0;
                        }
                        return;
                    }
                    this.wikiPageList = res.list;
                    if (!Number.check(this.repositoryId, 1)) {
                        this.repositoryId = this.wikiPageList[0].id;
                    }
                    this.pageQuery.totalCount = Number.parseIntWithDefault(res.count, 0);
                    this.pageQuery.totalPage = Math.ceil(this.pageQuery.totalCount / this.pageQuery.pageSize);
                }, (code, message) => {
                    app.toast(`加载数据失败，${message || ''}`);
                });
            },
            showDashboardWikiPageDialog(wikiPage) {
                app.showDialog(DashboardWikiPageDialog, {
                    wiki: wikiPage.id,
                    id: wikiPage.id,
                    page: wikiPage,
                })
            },

        }
    };
</script>
