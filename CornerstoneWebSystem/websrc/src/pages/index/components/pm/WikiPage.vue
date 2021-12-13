<style scoped>
    .page {
        min-height: 400px;
    }

    .page-content-top {
        width: 1200px;
        display: inline-block;
        padding: 0px;
        padding-top: 5px;
        padding-bottom: 5px;
        text-align: left;
    }

    .opt-right {
        text-align: right;
    }

    .slide-box {
        width: 250px;
        max-width: 200px;
        background-color: #fff;
        height: calc(100vh - 106px);
        max-height: calc(100vh - 106px);
        border-left: 1px solid #d9d9d9;
        overflow: auto;
        text-align: left;
        padding: 20px;
        position: fixed;
        top: 103px;
        right: 0;
        z-index: 3;
    }

    .slide-box-close {
        position: absolute;
        top: 0px;
        right: -10px;
    }

    .page-wrap {
        display: flex;
    }

    .tree-view {
        display: block;
        width: 300px;
        max-width: 300px;
        background-color: #fff;
        height: calc(100vh - 102px);
        max-height: calc(100vh - 102px);
        border-right: 1px solid #ccc;
        overflow: auto;
        text-align: left;
        padding: 20px;
    }

    .content-view {
        display: block;
        flex: 1;
        height: calc(100vh - 102px);
        max-height: calc(100vh - 102px);
        height: 100%;
        text-align: center;
        overflow: auto;
        padding: 20px;
    }

    .content-view-tips {
        margin-top: 150px;
        font-size: 20px;
        color: #666;
        display: inline-block;
        height: calc(100vh - 350px);
        text-align: center;
        padding: 40px;
    }

    .content-page {
        display: inline-block;
        width: 750px;
        background-color: #fff;
        border: 1px solid #ddd;
        border-radius: 3px;
        min-height: calc(100vh - 200px);
        text-align: left;
        position: relative;
        padding: 40px;
    }

    .content-page-full {
        width: 100%;
    }

    .content-opt {
        position: absolute;
        top: 10px;
        right: 10px;
        text-align: right;
    }

    .wiki-title {
        font-size: 26px;
        font-weight: bold;
        color: #333;

    }

    .wiki-desc {
        margin-top: 15px;
        font-size: 13px;
        color: #999;
    }

    .tree-view-title {
        font-size: 12px;
        color: #9d9d9d;
        margin-top: 15px
    }

    .tree-view-draft-box {
        padding-top: 10px;
        padding-bottom: 10px;
    }

    .tree-view-draft-item {
        font-size: 13px;
        color: #333;
        padding: 8px 8px;
        border-radius: 3px;
    }

    .tree-view-item-selected {
        background-color: #f3f3f3;
    }

    .draft-label {
        background-color: #0097f7;
        color: #fff;
        display: inline-block;
        padding: 3px 8px;
        font-size: 12px;
        border-radius: 3px;
        vertical-align: middle;
        margin-right: 10px;
    }

    .assoc-label {
        background-color: #f84f84;
        color: #fff;
        display: inline-block;
        padding: 3px 8px;
        font-size: 12px;
        border-radius: 3px;
        vertical-align: middle;
        margin-right: 10px;
    }

    .log-label {
        background-color: green;
        color: #fff;
        display: inline-block;
        padding: 3px 8px;
        font-size: 12px;
        border-radius: 3px;
        vertical-align: middle;
        margin-right: 10px;
    }

    .no-page-data {
        margin-top: 20px;
        font-size: 16px;
        color: #666;
    }

    .change-time {
        color: #333;
        font-size: 13px;
    }

    .change-user {
        color: #999;
        font-size: 13px;
    }

    .change-item {
        border-radius: 5px;
    }

    .change-item:hover {
        background-color: #f3f3f3;
    }

    .change-item-selected {
        background-color: #f3f3f3;
    }

    .scroll-title {
        font-size: 18px;
        font-weight: bold;
        color: #333;
        position: absolute;
        top: 67px;
        left: 400px;
        width: calc(100vw - 500px);
        text-align: center;
        padding-left: 40px;
        padding-right: 40px;
    }

    .share-popup-box {
        padding: 20px;
        min-height: 350px;
        width: 300px;
    }

    .share-title {
        color: #666;
        padding: 7px 0;
    }

    .share-address {
        word-break: break-all;
        white-space: pre-line;
    }

    @import '../../../../assets/spreadsheet.css';
</style>

<i18n>
    {
    "en": {
    "编辑知识库": "Edit",
    "创建知识库": "Create Wiki",
    "创建页面": "Create Page",
    "富文本": "Richtext",
    "思维导图": "Mindmap",
    "表格": "Table",
    "高级思维导图": "Pro Mindmap",
    "修订记录": "Version",
    "没有修订记录": "No Data",
    "草稿": "Draft",
    "页面": "Page",
    "没有任何页面": "No Data",
    "调整顺序": "Orders",
    "点击左侧页面查看详情": "Click items for details",
    "发布": "Publish",
    "分享": "Share",
    "链接地址": "Link",
    "二维码": "QRCode",
    "操作": "Opt",
    "查看修订历史": "Versions",
    "编辑页面": "Edit",
    "复制页面": "Copy",
    "删除页面": "Delete",
    "查看PDF版本": "View PDF",
    "生成": "Generate",
    "更新对象": "Update Object",
    "修订记录": "Versions",
    "已关联对象": "Rel",
    "最后一次修改了此页面": " edited",
    "分享地址已复制到剪切板中": "share address Copied to Clipboard",
    "确定要删除页面吗？": "Are you sure you want to delete the page {0}?",
    "操作成功": "Success",
    "确定要将此修订版本设置为当前版本吗？": "Are you sure you want to set this revised version to the current version?",
    "更新成功": "Success",
    "创建了此页面": " created "
    },
    "zh_CN": {
    "编辑知识库": "编辑知识库",
    "创建知识库": "创建知识库",
    "创建页面": "创建页面",
    "富文本": "富文本",
    "思维导图": "思维导图",
    "表格": "表格",
    "高级思维导图": "高级思维导图",
    "修订记录": "修订记录",
    "没有修订记录": "没有修订记录",
    "草稿": "草稿",
    "页面": "页面",
    "没有任何页面": "没有任何页面",
    "调整顺序": "调整顺序",
    "点击左侧页面查看详情": "点击左侧页面查看详情",
    "发布": "发布",
    "分享": "分享",
    "链接地址": "链接地址",
    "二维码": "二维码",
    "操作": "操作",
    "查看修订历史": "查看修订历史",
    "编辑页面": "编辑页面",
    "复制页面": "复制页面",
    "删除页面": "删除页面",
    "查看PDF版本": "查看PDF版本",
    "生成": "生成",
    "更新对象": "更新对象",
    "修订记录": "修订记录",
    "已关联对象": "已关联对象",
    "最后一次修改了此页面": "最后一次修改了此页面",
    "分享地址已复制到剪切板中": "分享地址已复制到剪切板中",
    "确定要删除页面吗？": "确定要删除页面“{0}”吗？",
    "操作成功": "操作成功",
    "确定要将此修订版本设置为当前版本吗？": "确定要将此修订版本设置为当前版本吗？",
    "更新成功": "更新成功",
    "创建了此页面": "创建了此页面"
    }
    }
</i18n>

<template>
    <div class="page" @keyup="shiftSelect=false" @keydown.shift="shiftSelect=true">
        <Row class="opt-bar">
            <Col span="12" class="opt-left">
                <Form inline>
                    <FormItem>
                        <Select @on-change="selectedWikiChanged" v-model="selectedWiki" style="width:280px">
                            <Option v-for="item in wikiList" :key="item.id" :value="item.id">{{item.name}}</Option>
                        </Select>
                        <IconButton
                            v-if="prjPerm('wiki_edit')&&selectedWiki>0&&!projectDisabled" @click="showEditWikiDialog(selectedWiki)"
                            :tips="$t('编辑知识库')" size="16" icon="md-create"></IconButton>
                        <IconButton v-if="prjPerm('wiki_edit')&&!projectDisabled" @click="showEditWikiDialog()" icon="md-add"
                                    :tips="$t('创建知识库')">
                        </IconButton>

                    </FormItem>
                </Form>
            </Col>
            <Col span="12" class="opt-right">
                <Form inline>
                    <FormItem v-if="prjPerm('wiki_edit_page')&&!projectDisabled" style="text-align:left;">
                        <Dropdown>
                            <Button type="default" icon="md-add">{{$t('创建页面')}}
                                <Icon style="margin-left:8px" type="ios-arrow-down">
                                </Icon>
                            </Button>
                            <DropdownMenu slot="list">
                                <DropdownItem @click.native="showPageEditor({type:1,parentId:selectedWikiPage})">
                                    {{$t('富文本')}}
                                </DropdownItem>
                                <DropdownItem @click.native="showPageEditor({type:2,parentId:selectedWikiPage})">
                                    markdown
                                </DropdownItem>
                                <DropdownItem @click.native="showPageEditor({type:3,parentId:selectedWikiPage})">
                                    {{$t('思维导图')}}
                                </DropdownItem>
                                <DropdownItem @click.native="showPageEditor({type:4,parentId:selectedWikiPage})">
                                    {{$t('表格')}}
                                </DropdownItem>
                                <DropdownItem @click.native="showPageEditor({type:5,parentId:selectedWikiPage})">
                                    {{$t('高级思维导图')}}
                                </DropdownItem>
                            </DropdownMenu>
                        </Dropdown>
                    </FormItem>
                </Form>
            </Col>
        </Row>
        <transition name="fade">
            <span class="scroll-title text-no-wrap" v-if="scrollViewTitle!=null"> {{scrollViewTitle}}</span>
        </transition>
        <transition name="slide">
            <div class="slide-box" v-if="showChangeLog">
                <div style="position:relative">
                    <span style="font-size:14px;color:#333">{{$t('修订记录')}}</span>
                    <span class="slide-box-close">
                        <IconButton icon="md-close" @click="showChangeLog=false"></IconButton>
                    </span>
                </div>
                <div style="font-size:16px;color:#999;margin-top:30px" v-if="changeLogList.length==0">
                    {{$t('没有修订记录')}}
                </div>
                <Timeline style="margin-top:10px">
                    <TimelineItem
                        class="change-item" :class="{'change-item-selected':changeLogId==log.id}"
                        v-for="log in changeLogList" :key="'log'+log.id">
                        <div @click="loadChangeLogInfo(log)">
                            <p class="change-time">{{log.createTime|fmtDeltaTime}}</p>
                            <p class="change-user">{{log.createAccountName}}</p>
                        </div>
                    </TimelineItem>
                </Timeline>
            </div>
        </transition>

        <div class="page-wrap" style="padding-top:0px">
            <div class="tree-view scrollbox">
                <div v-if="draftList.length>0" class="tree-view-title">{{$t('草稿')}}</div>
                <div v-if="draftList.length>0" class="tree-view-draft-box">
                    <div
                        @click="wikiPageTreeSelect.id=null;selectPage(item.id)" v-for="item in draftList"
                        :key="'draft_'+item.id" :class="{'tree-view-item-selected':item.id==selectedWikiPage}"
                        class="tree-view-draft-item">{{item.name}}
                    </div>
                </div>

                <div v-if="!projectDisabled" class="tree-view-title">{{$t('页面')}}
                    <IconButton @click="showTreeEditDialog" style="float:right" icon="md-reorder" :tips="$t('调整顺序')">
                    </IconButton>
                </div>
                <div v-if="wikiPageTree.length>0">
                    <WikiTreeNode
                        :change="wikiTreeNodeChanged" v-for="node in wikiPageTree"
                        :select="wikiPageTreeSelect" :key="'node_'+node.id" :value="node"></WikiTreeNode>
                </div>
                <div v-if="wikiPageTreeLoaded&&wikiPageTree.length==0" class="no-page-data">
                    {{$t('没有任何页面')}}
                </div>
            </div>
            <div
                ref="contentView"
                @scroll="handleContentScroll" class="content-view scrollbox">
                <div v-if="wikiPage==null" class="content-view-tips">{{$t('点击左侧页面查看详情')}}</div>
                <div ref="pg"
                    class="content-page"
                    :class="{'content-page-full':wikiPage.type==3||wikiPage.type==4||wikiPage.type==5}"
                    v-if="wikiPage!=null">
                    <div class="content-opt">

                        <IconButton
                            @click="releaseWikiPage()" v-if="prjPerm('wiki_edit_page')&&wikiPage.status==1&&!projectDisabled"
                            icon="ios-paper-plane-outline" :title="$t('发布')"></IconButton>
                        <IconButton
                            @click="revertWikiPage()" v-if="prjPerm('wiki_edit_page')&&wikiPage.status==null&&!projectDisabled"
                            icon="ios-redo-outline" :title="$t('从此修订版恢复')"></IconButton>


                        <Poptip
                            v-if="wikiPage.type==1" placement="bottom-end" ref="poptip" transfer
                            class="poptip-full">
                            <Button
                                style="margin-left:5px" size="small" type="text"
                                icon="ios-share-alt-outline">{{$t('分享')}}
                            </Button>
                            <div slot="content">
                                <div class="share-popup-box">
                                    <div class="share-title">{{$t('链接地址')}}</div>
                                    <div
                                        class="share-address" v-clipboard:copy="shareURL"
                                        v-clipboard:success="copySuccess">{{shareURL}}
                                    </div>
                                    <div class="share-title">{{$t('二维码')}}</div>
                                    <div style="position: relative;">
                                        <qrcode :value="shareURL" :options="{ size: 200 }"></qrcode>
                                    </div>
                                </div>
                            </div>
                        </Poptip>

                        <Dropdown v-if="wikiPage.status!=null" style="text-align:left" placement="bottom-end">
                            <Button style="margin-left:5px" size="small" type="text" icon="ios-more">{{$t('操作')}}
                            </Button>
                            <DropdownMenu slot="list" style="width:150px">
                                <DropdownItem
                                    @click.native="showChangeLogView()"
                                    v-if="wikiPage.type==1||wikiPage.type==2">{{$t('查看修订历史')}}
                                </DropdownItem>
                                <DropdownItem v-if="prjPerm('wiki_edit_page')&&!projectDisabled" @click.native="editWikiPage(false)">
                                    {{$t('编辑页面')}}
                                </DropdownItem>
                                <DropdownItem v-if="prjPerm('wiki_edit_page')&&!projectDisabled" @click.native="editWikiPage(true)">
                                    {{$t('复制页面')}}
                                </DropdownItem>
                                <DropdownItem v-if="prjPerm('wiki_edit_page')&&!projectDisabled" @click.native="deleteWikiPage()">
                                    {{$t('删除页面')}}
                                </DropdownItem>
                                <DropdownItem
                                    v-if="wikiPage.type==1||wikiPage.type==2" divided
                                    @click.native="viewWikiPdf()">{{$t('查看PDF版本')}}
                                </DropdownItem>

                                <template v-if="(wikiPage.type==3||wikiPage.type==5)&&wikiPage.status==2">
                                    <DropdownItem
                                        :divided="idx==0" v-for="(module,idx) in moduleList"
                                        :key="'md_'+module.id" v-if="module.objectType>0&&prjPerm('task_create_'+module.objectType)&&!projectDisabled"
                                        @click.native="genObject(module.objectType)">{{$t('生成')}}{{module.name}}
                                    </DropdownItem>
                                    <DropdownItem
                                        @click.native="updateGenObject()" v-if="wikiPage.isAssociatedTask"
                                        divided>{{$t('更新对象')}}
                                    </DropdownItem>
                                    <DropdownItem v-if="wikiPage.type==3"
                                        @click.native="exportMindPicture2()"
                                        divided>{{$t('导出图片')}}
                                    </DropdownItem>
                                </template>
                            </DropdownMenu>
                        </Dropdown>

                    </div>
                    <h1 class="wiki-title">{{wikiPage.name}}
                        <span v-if="wikiPage.status==null" class="log-label">{{$t('修订记录')}}</span>
                        <span v-if="wikiPage.isAssociatedTask" class="assoc-label">{{$t('已关联对象')}}</span>
                    </h1>
                    <div class="wiki-desc">
                        <template v-if="wikiPage.createTime!=wikiPage.updateTime">
                            {{wikiPage.updateAccountName}} {{wikiPage.updateTime|fmtDeltaTime}} {{$t('最后一次修改了此页面')}} ，
                        </template>
                        {{wikiPage.createAccountName}} {{wikiPage.createTime|fmtDeltaTime}} {{$t('创建了此页面')}}
                    </div>
                    <RichtextLabel
                        class="wiki-page-richtext"
                        v-if="wikiPage.type===1"
                        style="margin-top:20px"
                        :value="wikiPage.content">
                    </RichtextLabel>
                    <MarkdownLabel
                        class="wiki-page-richtext"
                        v-if="wikiPage.type===2"
                        style="margin-top:20px"
                        :value="wikiPage.content">
                    </MarkdownLabel>
                    <MindmapLabel ref="mind3" v-if="wikiPage.type==3" style="width:100%;margin-top:20px" :value="wikiPage.content"
                                  @on-select-node="selectNode">
                    </MindmapLabel>
                    <SpreadSheetLabel
                        v-if="wikiPage.type==4" style="width:100%;margin-top:20px"
                        :value="wikiPage.content"></SpreadSheetLabel>
                    <MindProLabel ref="mind5" v-if="wikiPage.type==5" style="width:100%;margin-top:20px" :value="wikiPage.content"
                                  @on-select-node="selectNode">
                    </MindProLabel>
                </div>
            </div>
            <HtmlCatalog
                v-if="wikiPage && (wikiPage.type === 1 || wikiPage.type === 2)"
                ref="htmlCatalog"
                class="note-catalog"
                :offset-top="125"
                :offset-left="300"
                :scroll-box="$refs.contentView"/>
        </div>
    </div>
</template>

<script>
    import qrcode from '@xkeshi/vue-qrcode';

    export default {
        mixins: [componentMixin],
        components: {
            qrcode,
        },
        data() {
            return {
                title: 'WikiPage',
                queryItem: {
                    name: null,
                    pageIndex: 1,
                    pageSize: 20,
                    projectId: null,
                    parentId: 0,
                },
                shareURL: null,
                selectedWiki: null,
                selectedWikiPage: null,
                wikiList: [],
                draftList: [],
                wikiPageTree: [],
                wikiPageTreeSelect: {
                    id: null,
                },
                wikiPage: null,
                changeLogList: [],
                scrollViewTitle: null,
                changeLogId: null,
                showChangeLog: false,
                moduleList: [],
                wikiPageTreeLoaded: false,
                lastSelectedWiki: 0,
                nodeId: "",
            };
        },
        watch: {
            wikiPage: {
                immediate: true,
                handler(val) {
                    if (!val) {
                        // 重置目录树
                        if (!this.$refs.htmlCatalog) {
                            return;
                        }
                        this.$refs.htmlCatalog.analyzeElHtml();
                        return;
                    }
                    if (val.type !== 1 && val.type !== 2) {
                        return;
                    }
                    setTimeout(() => {
                        // 计算目录树
                        this.$refs.htmlCatalog.analyzeElHtml($('.content-page .wiki-page-richtext')[0]);
                    }, 0);
                },
            },
        },
        methods: {
            pageLoad() {
                if (this.args.wiki) {
                    this.wikiPageTreeSelect.id = parseInt(this.args.wiki);
                }
                this.loadWiki();
                this.loadData();
                this.loadProjectModule();
            },
            pageUpdate() {
                if (this.args.wiki) {
                    this.wikiPageTreeSelect.id = parseInt(this.args.wiki);
                }
                this.loadData();
            },
            selectedWikiChanged() {
                if (this.selectedWiki) {
                    app.loadPage('?id=' + this.selectedWiki);
                }
                this.wikiPageTreeSelect.id = null;
            },
            loadProjectModule() {
                app.invoke('BizAction.getProjectModuleInfoList', [app.token, app.projectId], (list) => {
                    this.moduleList = list;
                });
            },
            loadData() {
                if (this.args.id != this.lastSelectedWiki) {
                    this.loadWikiList(this.args.id);
                }
                if (this.args.wiki) {
                    this.selectedWikiPage = parseInt(this.args.wiki);
                    app.invoke('BizAction.getWikiPageById', [app.token, this.args.wiki], (info) => {
                        this.wikiPage = info;
                        var host = app.getHost();
                        this.shareURL = host + '/wikishare.html?id=' + info.uuid;
                    });
                } else {
                    this.wikiPage = null;
                    this.selectedWikiPage = null;
                    this.showChangeLog = false;
                }
            },
            pageMessage(type, value) {
                if (type == 'wiki.edit') {
                    this.loadWiki();
                }
                if (type == 'wikipage.edit') {
                    this.loadWikiList(this.selectedWiki);
                    this.loadWikiPage(value);
                }
            },
            handleContentScroll(e) {
                if (this.wikiPage == null) {
                    return;
                }
                var offset = e.target.scrollTop;
                if (offset > 100) {
                    this.scrollViewTitle = this.wikiPage.name;
                } else {
                    this.scrollViewTitle = null;
                }
            },
            copySuccess() {
                app.toast(this.$t('分享地址已复制到剪切板中'));
            },
            loadWiki() {
                var query = {
                    pageIndex: 1,
                    pageSize: 1000,
                    projectId: app.projectId,
                };
                app.invoke('BizAction.getWikiInfoList', [app.token, query], (list) => {
                    this.wikiList = list;
                    this.setupLastWiki();
                });
            },
            setupLastWiki() {
                if (this.wikiList.length > 0) {
                    this.selectedWiki = this.wikiList[0].id;
                }
                if (this.args.id) {
                    this.selectedWiki = parseInt(this.args.id);
                }
                if (this.selectedWiki != null) {
                    this.loadWikiList(this.selectedWiki);
                }
            },
            loadWikiList(id) {
                if (id == null) {
                    return;
                }
                this.selectedWiki = parseInt(id);
                this.lastSelectedWiki = this.selectedWiki;
                app.invoke('BizAction.getWikiPageList', [app.token, id], (info) => {
                    this.draftList = info.draftList;
                    this.wikiPageTree = info.wikiPageTree;
                    this.wikiPageTreeLoaded = true;
                });
            },
            showTreeEditDialog() {
                app.showDialog(WikiTreeEditDialog, {
                    id: this.selectedWiki,
                });
            },
            loadChangeLog() {
                app.invoke('BizAction.getWikiPageChangeLogInfoList', [app.token, this.wikiPage.id], (list) => {
                    this.changeLogList = list;
                });
            },
            wikiTreeNodeChanged() {
                if (this.wikiPageTreeSelect.id) {
                    this.selectPage(this.wikiPageTreeSelect.id);
                }
            },
            selectPage(id) {
                this.selectedWikiPage = id;
                this.showChangeLog = false;
                this.loadWikiPage(id);
            },
            loadWikiPage(id) {
                if (id == null) {
                    app.loadPage('?id=' + this.selectedWiki);
                } else {
                    if (this.wikiPage != null && this.wikiPage.id == id) {
                        this.loadData();
                    } else {
                        app.loadPage('?id=' + this.selectedWiki + '&wiki=' + id);
                    }
                }
            },
            showChangeLogView() {
                this.showChangeLog = true;
                this.loadChangeLog();
            },
            loadChangeLogInfo(log) {
                this.changeLogId = log.id;
                this.selectedWikiPage = null;
                app.invoke('BizAction.getWikiPageChangeLogInfoById', [app.token, log.id], (info) => {
                    var oldType = this.wikiPage.type;
                    this.wikiPage = info;
                    this.wikiPage.type = oldType;
                });
            },
            editWikiPage(copy) {
                if (this.wikiPage.status == 2) { //已发布
                    app.invoke('BizAction.getLatestDraftWikiPage', [app.token, this.wikiPage.id], (id) => {
                        this.showPageEditor({
                            type: this.wikiPage.type,
                            id: id,
                            copy: copy,
                        });
                    });
                }
                if (this.wikiPage.status == 1) { //草稿
                    this.showPageEditor({
                        type: this.wikiPage.type,
                        id: this.wikiPage.id,
                        copy: copy,
                    });
                }

            },
            deleteWikiPage() {
                app.confirm(this.$t('确定要删除页面吗？', [this.wikiPage.name]), () => {
                    app.invoke('BizAction.deleteWikiPage', [app.token, this.wikiPage.id], (info) => {
                        app.toast(this.$t('操作成功'));
                        this.selectedWikiPage = null;
                        this.wikiPage = null;
                        app.postMessage('wikipage.edit', null);
                    });
                });
            },
            releaseWikiPage() {
                var oldId = this.wikiPage.id;
                app.invoke('BizAction.releaseWikiPage', [app.token, oldId], (id) => {
                    app.toast(this.$t('发布成功'));
                    app.postMessage('wikipage.edit', id);
                });
            },
            revertWikiPage() {
                app.confirm(this.$t('确定要将此修订版本设置为当前版本吗？'), () => {
                    app.invoke('BizAction.revertWikiPage', [app.token, this.wikiPage.id], (id) => {
                        app.toast(this.$t('操作成功'));
                        this.selectPage(id);
                        app.postMessage('wikipage.edit', id);
                    });
                });
            },
            showEditWikiDialog(id) {
                app.showDialog(WikiEditDialog, {
                    projectId: app.projectId,
                    id: id == null ? 0 : id,
                });
            },
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
            genObject(objectType, wikiPageType) {
                app.showDialog(WikiMindmapTaskCreateDialog, {
                    projectId: app.projectId,
                    iterationId: app.iterationId,
                    type: objectType,
                    wikiPageId: this.wikiPage.id,
                    wikiPageType: this.wikiPage.type,
                    nodeId: this.nodeId,
                    callback: () => {
                        app.invoke('BizAction.getWikiPageById', [app.token, this.wikiPage.id], (info) => {
                            this.wikiPage = info;
                        });
                    },
                });
            },
            updateGenObject() {
                app.invoke(this.wikiPage.type == 5 ? 'BizAction.updateSeniorWikiPageByTasks' : 'BizAction.updateWikiPageByTasks', [app.token, this.wikiPage.id], (info) => {
                    app.toast(this.$t('更新成功'));
                    this.wikiPage = info;
                });
            },
            viewWikiPdf() {
                app.showDialog(WikiPdfDialog, {
                    title: this.wikiPage.name,
                    uuid: this.wikiPage.uuid,
                });
            },
            selectNode(nodeId) {
                this.nodeId = nodeId || "";
            },
            exportMindPicture2(){
                app.toast('图片即将生成，请稍后');
                this.$refs['mind'+this.wikiPage.type].capture(this.wikiPage.name);
            }

        },
    };
</script>
