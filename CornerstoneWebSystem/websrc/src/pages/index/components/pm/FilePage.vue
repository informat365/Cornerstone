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

    .location-label {
        font-size: 12px;
        font-weight: bold;
        color: #999;
        padding-right: 10px;
        cursor: pointer;
        line-height: 20px;
        display: inline-block;
    }

    .location-label1 {
        font-size: 12px;
        font-weight: bold;
        color: #999;
        padding-right: 10px;
        line-height: 20px;
        display: inline-block;
        letter-spacing: 1px;
    }

    .location-total-label {
        font-size: 12px;
        font-weight: bold;
        color: #999;
        line-height: 20px;
        text-align: right;
    }

    .moveto-header {
        padding: 5px;
        border-bottom: 1px solid #eee;
        color: #333;
        font-weight: bold;
        font-size: 14px;
    }

    .moveto-name {
        max-width: 150px;
    }

    .card-content {
        text-align: left;
        background-color: #fff;
        height: calc(100vh - 135px);
        overflow: auto;
        padding: 30px;
        width: 100%;
        border-top: 1px solid rgba(216, 216, 216, 1);
    }

    .card-content-inner {
        display: flex;
        flex-wrap: wrap;
    }

    .file-card {
        display: inline-block;
        text-align: center;
        width: 100px;
        height: 100px;
        border-radius: 5px;
        padding: 10px;
        user-select: none;
        margin: 5px;
        overflow: hidden;
        position: relative;
    }

    .file-card-inner {
        height: 50px;
        overflow: hidden;
        position: relative;
    }

    .filecard-lock {
        position: absolute;
        top: 0;
        right: 0;
    }

    .active {
        background-color: #E0E0E0;
    }

    .rectbox {
        position: absolute;
        z-index: 1090;
        border: 2px dashed #cbd3e3;
    }

    .card-pager {
        color: #999;
        font-weight: bold;
        text-align: center;
        position: absolute;
        width: 100%;
        top: 155px;
    }

    .card-nodata {
        font-size: 16px;
        color: #999;
        text-align: center;
        padding: 40px;
    }

    .image-file {
        max-width: 50px;
        max-height: 50px;
    }

    .file-wrap {
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    .folder-icon {
        color: #59CEF7;
    }

    .file-table-icon {
        position: relative;
    }
</style>
<i18n>
    {
    "en": {
    "文件名": "File",
    "上传人": "Created by",
    "查询":"Query",
    "上传":"Upload",
    "创建文件夹":"Create Directory",
    "所有文件":"All Files",
    "个文件":"{0} file(s)",
    "下载":"Download",
    "删除":"Delete",
    "移动到":"Move to",
    "选择要移动到的目录":"Please choose directory",
    "移动个文件到":"Move {0} file(s) to ",
    "卡片视图":"Card view",
    "列表视图":"Table view",
    "确定":"OK",
    "更新":"Update",
    "暂无数据":"No Data",
    "确定要删除1":"Are you sure you want to delete [{0}]? Files will not be recoverable after deletion, please be cautious!",
    "确定要删除2":"Are you sure you want to delete {1} files such as [{0}]? Files will not be recoverable after deletion, please be cautious!",
    "名称":"Name",
    "大小":"Size",
    "创建人":"Created by",
    "创建时间":"Created time",
    "编辑":"Edit"
    },
    "zh_CN": {
    "文件名": "文件名",
    "上传人": "上传人",
    "查询":"查询",
    "上传":"上传",
    "创建文件夹":"创建文件夹",
    "所有文件":"所有文件",
    "个文件":"{0}个文件",
    "下载":"下载",
    "删除":"删除",
    "移动到":"移动到",
    "选择要移动到的目录":"选择要移动到的目录",
    "移动个文件到":"移动{0}个文件到",
    "卡片视图":"卡片视图",
    "列表视图":"列表视图",
    "确定":"确定",
    "更新":"更新",
    "暂无数据":"暂无数据",
    "确定要删除1":"确定要删除【{0}】吗？删除后文件将不可恢复，请谨慎操作!",
    "确定要删除2":"'确定要删除【{0}】等{1}个文件吗？删除后文件将不可恢复，请谨慎操作!",
    "名称":"名称",
    "大小":"大小",
    "创建人":"创建人",
    "创建时间":"创建时间",
    "编辑":"编辑"
    }
    }
</i18n>
<template>
    <div class="page" @keyup="shiftSelect=false" @keydown.shift="shiftSelect=true">
        <Row class="opt-bar">
            <Col span="12" class="opt-left">
                <Form inline>
                    <FormItem>
                        <Input @on-change="loadData" v-model="queryItem.name" style="width:200px" type="text" :placeholder="$t('文件名')"></Input>
                    </FormItem>
                    <FormItem>
                        <Input @on-change="loadData" v-model="queryItem.createAccountName" style="width:150px" type="text" :placeholder="$t('上传人')"></Input>
                    </FormItem>
                    <FormItem>
                        <Button @click="loadData" type="default">{{$t('查询')}}</Button>
                    </FormItem>
                </Form>
            </Col>
            <Col span="12" class="opt-right">
                <Form inline @submit.native.prevent>

                    <FormItem v-if="prjPerm('file_upload')&&!projectDisabled">
                        <Poptip placement="bottom-end" :disabled="projectDisabled">
                            <Button type="default" icon="ios-cloud-upload">{{$t('上传')}}</Button>
                            <FileUploadView style="padding:10px;width:500px" multiple @change="uploadSuccess" slot="content"></FileUploadView>
                        </Poptip>
                    </FormItem>
                    <FormItem v-if="prjPerm('file_create_dir')&&!projectDisabled">
                        <Button @click="showCreateDialog()" type="default" icon="ios-folder">{{$t('创建文件夹')}}</Button>
                    </FormItem>
                </Form>
            </Col>
        </Row>
        <div v-if="loaded" class="page-wrap file-wrap" style="padding-top:0px">
            <Row class="page-content-top">
                <Col span="18">
                    <span @click="switchFolder" class="location-label">{{$t('所有文件')}}</span>

                    <Poptip v-model="forlderSelectVisible" placement="bottom-start" width="400">
                        <span>
                            <Icon type="ios-arrow-down" style="color:#999;margin-right:10px;cursor:pointer"/>
                        </span>
                        <div slot="content" style="padding:10px">
                            <Tree @on-select-change="switchFolder" :data="treeData"></Tree>
                        </div>
                    </Poptip>
                    <span class="location-label1" v-if="parent==null">/</span>
                    <span class="location-label1" v-if="parent">{{parent.path}}</span>

                    <span style="padding-left:10px;color:#999;font-size:12px">{{$t('个文件',[totalCount])}}</span>
                </Col>
                <Col span="6" class="location-total-label">

                    <IconButton v-if="prjPerm('file_download')" @click="downloadFiles()" :disabled="selectedCount()==0" :title="$t('下载')"></IconButton>
                    <IconButton v-if="prjPerm('file_delete')&&!projectDisabled" @click="deleteFiles()" :disabled="selectedCount()==0" :title="$t('删除')"></IconButton>
                    <Poptip v-if="prjPerm('file_move')&&!projectDisabled" v-model="forlderMoveSelectVisible" placement="top-end" width="400">
                        <IconButton :disabled="selectedCount()==0||projectDisabled" :title="$t('移动到')"></IconButton>
                        <div slot="content" style="text-align:left;padding:10px">
                            <div class="moveto-header">
                                <span v-if="moveToDir==null">{{$t('选择要移动到的目录')}}</span>
                                <span v-if="moveToDir">
                                            {{$t('移动个文件到',[selectedCount()])}}<span class="moveto-name text-no-wrap"> {{moveToDir.name}}</span>
                                            <Button style="margin-left:10px" @click="confirmMove()" type="default" size="small">{{$t('确定')}}</Button>
                                        </span>

                            </div>
                            <Tree style="max-height:200px;overflow: auto;" @on-select-change="folderMoveChanged" :data="allTreeData"></Tree>
                        </div>
                    </Poptip>

                    <IconButton v-if="showType=='table'" @click="setShowType('card')" :title="$t('卡片视图')"></IconButton>
                    <IconButton v-if="showType=='card'" @click="setShowType('table')" :title="$t('列表视图')"></IconButton>
                </Col>
            </Row>

            <div class="page-content" v-if="showType=='table'">
                <div>
                    <table class="table-content" style="table-layout:fixed">
                        <thead>
                        <tr>
                            <th style="width:60px" >
                                <Checkbox v-model="isSelectAll"></Checkbox>
                            </th>
                            <th style="width:40px"></th>
                            <TableHeader @change="sortChanged" :sort="tableSort" name="name" :label="$t('名称')"></TableHeader>
                            <TableHeader style="width:120px" @change="sortChanged" :sort="tableSort" name="size" :label="$t('大小')"></TableHeader>
                            <TableHeader style="width:140px" @change="sortChanged" :sort="tableSort" name="createAccountName" :label="$t('创建人')"></TableHeader>
                            <TableHeader style="width:120px" @change="sortChanged" :sort="tableSort" name="createTime" :label="$t('创建时间')"></TableHeader>


                            <th style="width:140px;text-align:right"></th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr v-for="(item,idx) in tableData" :key="item.id" class="table-row">
                            <td>
                                <Checkbox @on-change="fileSelectChange(idx)" v-model="item.isSelect"></Checkbox>
                            </td>
                            <td>
                                <div class="file-table-icon">
                                    <Icon class="filecard-lock" v-if="item.enableRole" type="ios-lock-outline"/>
                                    <Icon class="folder-icon" :style="{'color':item.color}" :size="25" v-if="item.isDirectory" type="ios-folder"/>
                                    <Icon style="color:#59CEF7" :size="25" v-if="item.isDirectory==false" type="ios-document"/>
                                </div>
                            </td>
                            <td @click="previewFile(item)" class="table-col-name">{{item.name}}</td>
                            <td>
                                <template v-if="item.isDirectory==false">{{item.size|fmtBytes}}</template>
                                <template v-if="item.isDirectory">--</template>
                            </td>
                            <td class="text-no-wrap">
                                <AvatarImage size="small" :name="item.createAccountName" :value="item.createAccountImageId" type="label"></AvatarImage>
                            </td>
                            <td>{{item.createTime|fmtDeltaTime}}</td>
                            <td style="text-align:right">
                               <span class="table-row-opt">
                                    <IconButton :transfer="true" v-if="item.isDirectory==false||prjPerm('file_download')==false" @click="downloadFile(item)" icon="ios-cloud-download-outline" :tips="$t('下载')"></IconButton>
                                    <IconButton :transfer="true" v-if="item.isDirectory&&prjPerm('file_create_dir')&&!projectDisabled" @click="editDir(item)" icon="ios-settings-outline" :tips="$t('编辑')"></IconButton>
                                    
                                    <Poptip v-if="prjPerm('file_upload')&&!projectDisabled" placement="bottom-end">
                                        <IconButton :transfer="true" icon="ios-cloud-upload-outline" :tips="$t('更新')"></IconButton>
                                        <FileUploadView style="padding:10px;width:500px" :object="item" @change="uploadSingleSuccess" slot="content"> </FileUploadView>
                                    </Poptip>

                                    <IconButton :transfer="true" v-if="prjPerm('file_delete')&&!projectDisabled" @click="deleteFile(item)" icon="ios-trash-outline" :tips="$t('删除')"></IconButton>

                              </span>
                            </td>
                        </tr>

                        </tbody>
                    </table>
                    <div class="table-nodata" v-if="tableData.length==0">
                        {{$t('暂无数据')}}
                    </div>

                    <Row class="table-footer-bar">
                        <Col span="24" class="table-footer-bar-right">
                            <Page :page-size="queryItem.pageSize" @on-change="pagerChanged" :total="totalCount"></Page>
                        </Col>
                    </Row>
                </div>
            </div>
        </div>

        <context-menu id="context-menu" ref="ctxMenu">
            <li v-if="!projectDisabled" @click="editDirs()"
                class="context-menu-item"
                :class="{'context-menu-item-disabled':(selectedCount()==0||!canEditDirs()||!prjPerm('file_create_dir'))}">{{$t('编辑')}}
            </li>

            <li @click="downloadFiles()"
                class="context-menu-item" :class="{'context-menu-item-disabled':(selectedCount()==0||!prjPerm('file_download'))}">{{$t('下载')}}
            </li>
            <li  v-if="!projectDisabled" @click="deleteFiles()"
                class="context-menu-item" :class="{'context-menu-item-disabled':(selectedCount()==0||!prjPerm('file_delete'))}">{{$t('删除')}}
            </li>

        </context-menu>

        <div v-if="showType=='card'&&totalPage>1" class="card-pager">
            <IconButton :disabled="queryItem.pageIndex==1" @click="changePage(-1)" :size="16" icon="md-arrow-round-back"></IconButton>
            <span style="line-height:1;vertical-align:middle">{{queryItem.pageIndex}}/{{totalPage}}</span>
            <IconButton :disabled="queryItem.pageIndex==totalPage" @click="changePage(1)" :size="16" icon="md-arrow-round-forward"></IconButton>
        </div>
        <div v-if="showType=='card'"
             @contextmenu.prevent="$refs.ctxMenu.open"

             @mousemove="cardContentMouseMove"
             @mousedown="cardContentMouseDown"
             @mouseup="cardContentMouseUp" class="card-content scrollbox">
            <div ref="rectbox" v-show="rectBoxShow" class="rectbox" :style="rectBoxStyle"></div>
            <div class="card-content-inner">
                <div :ref="'filecard'+item.id" @mousedown.stop="clickCardFileItem(item,$event)" class="file-card"
                     :class="{'active':item.isSelect}" v-for="(item) in tableData" :key="item.id">

                    <div class="file-card-inner">
                        <div class="filecard-lock" v-if="item.enableRole">
                            <Icon type="ios-lock-outline"/>
                        </div>

                        <Icon class="folder-icon" :style="{'color':item.color}" :size="50" v-if="item.isDirectory" type="ios-folder"/>
                        <Icon style="color:#59CEF7" :size="50" v-if="item.isDirectory==false&&isImage(item)==false" type="ios-document"/>
                        <img class="image-file" v-if="item.isDirectory==false&&isImage(item)" :src="item.uuid|imageURL">
                    </div>
                    <div>{{item.name}}</div>
                </div>
            </div>

            <div class="card-nodata" v-if="tableData.length==0">
                {{$t('暂无数据')}}
            </div>

        </div>
    </div>
</template>

<script>
    import contextMenu from 'vue-context-menu'

    export default {
        mixins: [componentMixin],
        components: {contextMenu},
        data() {
            return {
                title: "FilePage",
                loaded: false,
                forlderSelectVisible: false,
                forlderMoveSelectVisible: false,
                treeData: [],
                tableData: [],
                parent: null,
                allTreeData: [],
                totalCount: 0,
                isSelectAll: false,
                moveToDir: null,
                shiftSelect: false,
                totalPage: 1,
                queryItem: {
                    name: null,
                    createAccountName: null,
                    pageIndex: 1,
                    pageSize: 50,
                    projectId: null,
                    parentId: 0,
                },
                tableSort: {
                    name: null,
                    sort: null,
                },
                rectBoxStyle: {
                    top: 0,
                    left: 0,
                    width: 0,
                    height: 0
                },
                rectBoxStartPoint: {},
                rectBoxShow: false,
                showType: 'card'
            }
        },
        watch: {
            isSelectAll: function (val) {
                this.tableData.map(item => {
                    item.isSelect = val;
                })
            }
        },
        filters: {
            imageURL(val) {
                return app.serverAddr + "/p/file/get_file_ex/" + val + "?token=" + app.token;
            }
        },
        methods: {
            pageLoad() {
                this.queryItem.projectId = app.projectId;
                if (this.args.p) {
                    this.queryItem.parentId = this.args.p;
                } else {
                    this.queryItem.parentId = 0;
                }
                this.loadStatus();
                this.loadData();
                this.loadDirNode();
            },
            pageUpdate() {
                if (this.args.p) {
                    this.queryItem.parentId = this.args.p;
                } else {
                    this.queryItem.parentId = 0;
                }
                this.loadData();
            },
            pageMessage(type) {
                if (type == 'file.edit') {
                    this.loadData();
                }
                if (type == 'dir.edit') {
                    this.loadData();
                    this.loadDirNode();
                }
            },
            pagerChanged(e) {
                this.queryItem.pageIndex = e;
                this.loadData(true);
            },
            isImage(item) {
                var t = item.name.toLowerCase();
                return t.indexOf('.png') != -1 ||
                    t.indexOf('.jpg') != -1 ||
                    t.indexOf('.bmp') != -1 ||
                    t.indexOf('.jpeg') != -1;
            },
            saveStatus() {
                app.saveObject('FilePage.viewType.' + app.projectId, this.showType);
            },
            loadStatus() {
                var t = app.loadObject('FilePage.viewType.' + app.projectId);
                if (t != null) {
                    this.showType = t;
                }
            },
            setShowType(type) {
                this.showType = type;
                this.saveStatus();
            },
            sortChanged() {
                this.loadData();
            },
            showCreateDialog() {
                app.showDialog(FileDirectoryEditDialog, {
                    projectId: app.projectId,
                    parentId: this.queryItem.parentId
                })
            },
            editDir(item) {
                app.showDialog(FileDirectoryEditDialog, {
                    projectId: app.projectId,
                    parentId: this.queryItem.parentId,
                    item: item,
                })
            },
            changePage(delta) {
                this.queryItem.pageIndex += delta;
                if (this.queryItem.pageIndex > this.totalPage) {
                    this.queryItem.pageIndex = this.totalPage;
                }
                if (this.queryItem.pageIndex < 1) {
                    this.queryItem.pageIndex = 1;
                }
                this.loadData(true);
            },
            loadData(keepPage) {
                if (keepPage != true) {
                    this.queryItem.pageIndex = 1;
                }
                if (this.tableSort.name != null) {
                    this.queryItem[this.tableSort.name + "Sort"] = this.tableSort.sort;
                }
                app.invoke("BizAction.getFiles", [app.token, this.queryItem], info => {
                    info.list.map(item => {
                        item.isSelect = false
                    })
                    this.tableData = info.list;
                    this.parent = info.parent;
                    this.totalCount = info.count;
                    this.totalPage = Math.ceil(info.count / this.queryItem.pageSize);
                    if (this.totalPage == 0) {
                        this.totalPage = 1;
                    }
                    this.loaded = true;
                })
            },
            loadDirNode() {
                app.invoke("BizAction.getDirectoryNode", [app.token, this.queryItem.projectId], info => {
                    this.treeData = info;
                    var cloneInfo = JSON.parse(JSON.stringify(info))
                    this.allTreeData = [{
                        id: 0,
                        title: this.$t("所有文件"),
                        expand: true,
                        children: cloneInfo
                    }]
                })
            },
            fileSelectChange(idx) {
                if (!this.shiftSelect) {
                    this.$set(this.tableData, idx, this.tableData[idx])
                    return;
                }
                var startIdx = true;
                for (var i = 0; i < this.tableData.length; i++) {
                    var t = this.tableData[i];
                    if (t.isSelect) {
                        startIdx = i;
                        break;
                    }
                }
                for (var i = 0; i < this.tableData.length; i++) {
                    if (i >= startIdx && i <= idx) {
                        this.tableData[i].isSelect = true;
                        this.$set(this.tableData, i, this.tableData[i])
                    }
                }

            },

            folderMoveChanged(item) {
                if (item == null || item.length == 0) {
                    return;
                }
                item[0].selected = false;
                var dir = {
                    id: item[0].id,
                    name: item[0].title,
                }
                this.moveToDir = dir;
            },
            switchFolder(dir) {
                if (dir == null || dir == undefined) {
                    dir = {
                        id: 0
                    };
                }
                if (dir.length != undefined && dir.length > 0) {
                    dir = dir[0];
                }
                if (dir.id == undefined) {
                    dir.id = 0;
                }
                app.loadPage('?p=' + dir.id)
                this.forlderSelectVisible = false;
            },
            selectedCount() {
                var count = 0;
                this.tableData.map(item => {
                    if (item.isSelect) {
                        count++;
                    }
                })
                return count;
            },
            uploadSuccess(uuid) {
                app.invoke("BizAction.addFile", [app.token, app.projectId, uuid.uuid, this.queryItem.parentId], info => {
                    app.postMessage('file.edit');
                })
            },
            uploadSingleSuccess(obj) {
                app.invoke("BizAction.updateFile", [app.token, obj.object.id, obj.uuid.uuid], info => {
                    app.postMessage('file.edit');
                })
            },
            previewFile(item) {
                if (item.isDirectory) {
                    this.switchFolder(item);
                    return;
                }
                if (app.prjPerm('file_preview')) {
                    app.previewFile(item.uuid);
                }
            },

            editFile(item) {
                if (item.isDirectory) {
                    app.showDialog(FileDirectoryEditDialog, {
                        item: item,
                    })
                    return;
                }
            },
            downloadFile(item) {
                window.open(app.serverAddr + '/p/file/download_file/' + item.uuid + "?token=" + app.token)
            },
            getSelectedFileIds() {
                var ids = [];
                this.tableData.map(item => {
                    if (item.isSelect) {
                        ids.push(item.id);
                    }
                });
                return ids;
            },
            getSelectedFileItems() {
                var ids = [];
                this.tableData.map(item => {
                    if (item.isSelect) {
                        ids.push(item);
                    }
                });
                return ids;
            },
            deleteFile(item) {
                app.confirm(this.$t('确定要删除1', [item.name]), () => {
                    app.invoke("BizAction.deleteFiles", [app.token, [item.id]], info => {
                        if (item.isDirectory) {
                            app.postMessage('dir.edit');
                        } else {
                            app.postMessage('file.edit');
                        }

                    })
                })
            },
            downloadFiles() {
                var count = this.selectedCount();
                if (count == 1) {
                    this.downloadFile(this.getSelectedFileItems()[0]);
                    return;
                }
                var uuids = this.getSelectedFileItems().map((item) => {
                    return item.uuid
                });
                window.open(app.serverAddr + '/p/file/download_file_list/' + uuids.join('_') + "?token=" + app.token)
            },
            deleteFiles() {
                var count = this.selectedCount();
                if (count == 1) {
                    this.deleteFile(this.getSelectedFileItems()[0]);
                    return;
                }
                var firstName = this.getSelectedFileItems()[0].name;
                app.confirm(this.$t('确定要删除2', [firstName, count]), () => {
                    app.invoke("BizAction.deleteFiles", [app.token, this.getSelectedFileIds()], info => {
                        app.postMessage('dir.edit');
                    })
                })
            },
            canEditDirs() {
                var count = this.selectedCount();
                if (count >= 1) {
                    var item = this.getSelectedFileItems()[0];
                    if (item.isDirectory) {
                        return true;
                    }
                }
                return false;
            },
            editDirs() {
                var count = this.selectedCount();
                if (count >= 1) {
                    var item = this.getSelectedFileItems()[0];
                    if (item.isDirectory) {
                        this.editDir(item);
                        return;
                    }
                }
            },
            confirmMove() {
                this.forlderMoveSelectVisible = false;
                app.invoke("BizAction.moveFiles", [app.token, app.projectId,
                    this.getSelectedFileIds(),
                    this.moveToDir.id], info => {
                    app.postMessage('dir.edit');
                    this.moveToDir = null;
                })
            },
            //
            clickCardFileItem(e, event) {
                if (event.button == 2) {
                    return;
                }
                if (e.lastClick == null) {
                    e.lastClick = new Date().getTime();
                } else {
                    if ((new Date().getTime() - e.lastClick) < 300) {
                        this.previewFile(e);
                    }
                }
                e.isSelect = !e.isSelect;
                e.lastClick = new Date().getTime();
            },
            //
            cardContentMouseDown(e) {
                if (e.button == 2) {
                    return;
                }
                this.rectBoxStartPoint = {x: e.clientX, y: e.clientY}
                this.rectBoxStyle.top = e.clientY + "px";
                this.rectBoxStyle.left = e.clientX + "px";
                this.rectBoxStyle.width = "1px";
                this.rectBoxStyle.height = "1px";
                this.rectBoxShow = true;
            },
            cardContentMouseUp(e) {
                if (e.button == 2) {
                    return;
                }
                this.rectBoxShow = false;
                this.rectBoxStyle.width = 0;
                this.rectBoxStyle.height = 0;
            },
            cardContentMouseMove(e) {
                if (this.rectBoxShow) {
                    var style = {}
                    var x1, y1, x2, y2;
                    x1 = Math.min(this.rectBoxStartPoint.x, e.clientX);
                    y1 = Math.min(this.rectBoxStartPoint.y, e.clientY);
                    x2 = Math.max(this.rectBoxStartPoint.x, e.clientX);
                    y2 = Math.max(this.rectBoxStartPoint.y, e.clientY);

                    this.rectBoxStyle.width = (x2 - x1) + "px";
                    this.rectBoxStyle.height = (y2 - y1) + "px";
                    this.rectBoxStyle.top = y1 + "px";
                    this.rectBoxStyle.left = x1 + "px";
                    for (var k in this.$refs) {
                        var file = this.$refs[k][0];
                        if (file != null && file != this.$refs.rectbox) {
                            var t = this.cross(this.$refs.rectbox, file);
                            this.setFileSelect(k, t)
                        }
                    }
                }
            },
            setFileSelect(key, isSelected) {
                for (var i = 0; i < this.tableData.length; i++) {
                    var t = this.tableData[i];
                    if ("filecard" + t.id == key) {
                        t.isSelect = isSelected;
                        return;
                    }
                }
            },
            cross(a, b) {
                var aTop = this.offset(a).top, aLeft = this.offset(a).left, bTop = this.offset(b).top, bLeft = this.offset(b).left;
                return !(((aTop + a.offsetHeight) < (bTop)) || (aTop > (bTop + b.offsetHeight)) || ((aLeft + a.offsetWidth) < bLeft) || (aLeft > (bLeft + b.offsetWidth)));
            },
            offset(el) {
                var r = el.getBoundingClientRect();
                return {top: r.top + document.body.scrollTop, left: r.left + document.body.scrollLeft}
            }

        }
    }
</script>