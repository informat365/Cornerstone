<style scoped>
  .note-view {
    position: fixed;
    top: 0;
    width: 100%;
    height: 100vh;
    background-color: #fff;
    display: flex;
    flex-grow: 1;
    z-index: 1000;
  }

  .sidebar {
    width: 300px;
    border-right: 1px solid #ccc;
    height: 100vh;
  }

  .sidebar-opt {
    height: 48px;
    border-bottom: 1px solid #ccc;
    display: flex;
    align-items: center;
    padding: 0 20px;
    padding-right: 10px;
  }

  .sidebar-list {
    height: calc(100vh - 48px);
    overflow: auto;
  }

  .nodata {
    padding: 30px;
    font-size: 20px;
    color: #999;
    text-align: center;
  }

  .table-count {
    background-color: #e8e8e8;
    color: #666;
    padding: 3px 5px;
    border-radius: 3px;
  }

  .sidebar-opt-category {
    flex: 1;
    font-size: 14px;
    font-weight: bold;
    cursor: pointer;
  }

  .sidebar-opt-category-name-wrap {
    display: flex;
    align-items: center;
  }

  .sidebar-opt-category-name {
    display: inline-block;
    max-width: 180px;
  }

  .sidebar-count {
    padding: 5px 10px;
    text-align: center;
  }

  .sidebar-opt-btn {
    width: 60px;
    text-align: right;
  }

  .sidebar-note {
    padding: 10px 20px;
    border-bottom: 1px solid #eee;
    cursor: pointer;
    transition: all 0.3s;
  }

  .sidebar-note:first-child {
    border-top: 1px solid #eee;
  }

  .sidebar-note:hover {
    background-color: #ebf7ff;
  }

  .sidebar-note-select .sidebar-note-title {
    color: #009cf1;
  }

  .sidebar-note-title {
    font-size: 14px;
    color: #333;
    word-break: break-all;
  }

  .sidebar-note-time {
    font-size: 12px;
    color: #999;
  }

  .note-editor {
    position: relative;
    flex: 1;
    background-color: #f1f4f5;
  }

  .note-editor-opt {
    height: 48px;
    border-bottom: 1px solid #ccc;
    display: flex;
    align-items: center;
    padding: 0 20px;
    background-color: #fff;
  }

  .note-name {
    border-bottom: 1px solid #eee;
    padding-bottom: 10px;
  }

  .note-name input {
    width: 100%;
    border: none;
    outline: 0;
    font-weight: bold;
    font-size: 18px;
    margin-top: 10px;
    color: #333;
    padding: 0 15px;
  }

  .note-content-box {
    display: flex;
    align-items: center;
    flex-direction: column;
    height: calc(100vh - 48px);
    overflow: auto;
    padding-top: 20px;
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

  .note-content-area {
    width: 750px;
    border-radius: 3px;
    border: 1px solid #ddd;
    text-align: left;
    position: relative;
    padding: 40px;
    background-color: #fff;
  }

  .note-content {
    padding: 20px 0;
  }

  .note-editor-toolbar {
    flex: 1;
    border: none;
  }

  .note-editor-btn {
    width: 150px;
    text-align: right;
  }

  .note-close-btn {
    cursor: pointer;
  }

  .silde-control-bar {
    width: 6px;
    position: absolute;
    top: 100px;
    left: 300px;
    z-index: 10;
  }

  .silde-control-bar-close {
    left: 0px;
  }

  .hide-side-button {
    position: absolute;
    right: 0px;
    top: 32px;
    width: 6px;
    height: 40px;
    background-color: rgba(216, 216, 216, 1);
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

  .sidebar-search {
    margin: 7px 20px;
    border-radius: 5px;
    border: 1px solid #eee;
    overflow: hidden;
  }

  .sidebar-search input {
    width: 100%;
    outline: none;
    padding: 5px 10px;
    border: none;
  }

  .note-info-bar {
    height: 30px;
    display: flex;
    align-items: center;
    margin-bottom: 10px;
  }

  .note-info-bar-category {
    flex: 1;
    padding-left: 15px;
  }

  .note-info-bar-opt {
    width: 200px;
    text-align: right;
  }

  .note-popup-box {
    padding: 20px;
    min-height: 200px;
    width: 280px;
    display: flex;
    flex-wrap: wrap;
    flex-direction: row;
    align-items: flex-start;
    justify-content: flex-start;
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

  .note-save-bar-info {
    position: absolute;
    top: 50px;
    right: 50px;
    text-align: right;
    color: #999;
    font-size: 12px;
    margin-right: 10px;
  }

  .note-close {
    position: absolute;
    top: 12px;
    right: 10px;
    z-index: 200;
  }
</style>
<i18n>
  {
  "en": {
  "全部笔记": "All Notes",
  "没有设置任何标签":"No Tag",
  "新建笔记":"Add",
  "搜索笔记":"Search",
  "暂无数据":"No Data",
  "未保存":"un saved",
  "已保存":"saved",
  "删除":"Delete",
  "保存":"Save",
  "标签":"Tag",
  "链接地址":"Share link",
  "分享":"Share",
  "二维码":"QRcode",
  "输入笔记标题":"untitled",
  "请输入标题":"Input note title",
  "确定要删除":"Are you sure you want to delete【{0}】?",
  "点击左侧页面查看详情": "Click items for details",
  "分享链接地址已复制到剪切板中":"Shared Link Address Copied to Clipboard"
  },
  "zh_CN": {
  "全部笔记": "全部笔记",
  "没有设置任何标签":"没有设置任何标签",
  "新建笔记":"新建笔记",
  "搜索笔记":"搜索笔记",
  "暂无数据":"暂无数据",
  "未保存":"未保存",
  "已保存":"已保存",
  "删除":"删除",
  "保存":"保存",
  "标签":"标签",
  "链接地址":"链接地址",
  "分享":"分享",
  "二维码":"二维码",
  "输入笔记标题":"输入笔记标题",
  "请输入标题":"请输入标题",
  "确定要删除":"确定要删除【{0}】吗？",
  "点击左侧页面查看详情": "点击左侧页面查看详情",
  "分享链接地址已复制到剪切板中":"分享链接地址已复制到剪切板中"
  }
  }
</i18n>
<template>
  <div class="note-view">
    <div class="note-close">
      <Icon @click.native="closePopup" class="note-close-btn" size="18" type="md-close" />
    </div>
    <div class="sidebar" v-show="showSideMenu">
      <div class="sidebar-opt">
        <div class="sidebar-opt-category">
          <Poptip placement="bottom-start" ref="poptip" transfer class="poptip-full" v-model="tagSelectPopupTopVisible">
            <span class="sidebar-opt-category-name-wrap">
                <template v-if="selectTagName==''">{{$t('全部笔记')}} </template>
                <span v-else class="sidebar-opt-category-name text-no-wrap">{{selectTagName}}</span>
                <Icon type="ios-arrow-down" />
            </span>
            <div slot="content">
              <div class="note-popup-box scrollbox">
                <NoteTag
                  @click.native="selectTag(item)"
                  v-for="item in tagList"
                  :key="item.id"
                  :selected="item.isSelected"
                  :value="item"></NoteTag>
                <div v-if="tagList.length==0" class="nodata">
                  {{$t('没有设置任何标签')}}
                </div>
              </div>
            </div>
          </Poptip>

        </div>
        <div class="sidebar-opt-btn">
          <IconButton
            style="color:#0ba2ef"
            icon="md-add"
            :size="18"
            @click="clickAddNote" :tips="$t('新建笔记')"></IconButton>
        </div>
      </div>

      <div class="sidebar-list scrollbox">
        <div class="sidebar-search">
          <input @change="loadData(true)" v-model="pageQuery.keyword" type="text" :placeholder="$t('搜索笔记')" />
        </div>
        <div class="sidebar-count">
          <IconButton
            :size="15"
            :disabled="pageQuery.pageIndex==1"
            @click="changePage(-1)"
            icon="md-arrow-round-back"></IconButton>
          <span class="table-count">{{pageQuery.totalCount}}条笔记，{{pageQuery.pageIndex}}/{{pageQuery.totalPage}}</span>
          <IconButton
            :size="15"
            :disabled="pageQuery.pageIndex==pageQuery.totalPage"
            @click="changePage(1)"
            icon="md-arrow-round-forward"></IconButton>
        </div>
        <div
          @click="selectNote(item)" v-for="item in noteList" :key="item.id"
          class="sidebar-note" :class="{'sidebar-note-select':item.uuid==note.uuid}">
          <div class="sidebar-note-title">{{item.title}}</div>
          <div class="sidebar-note-time">{{item.updateTime|fmtDateTime}}</div>
        </div>
        <div v-if="noteList.length==0" class="nodata">{{$t('暂无数据')}}</div>
      </div>
    </div>

    <div class="silde-control-bar" :class="{'silde-control-bar-close':showSideMenu==false}">
      <div @click="showSideMenu=!showSideMenu" class="hide-side-button">
        <Icon v-if="showSideMenu" class="hide-side-button-icon" type="md-arrow-dropleft" />
        <Icon v-if="showSideMenu==false" class="hide-side-button-icon" type="md-arrow-dropright" />
      </div>
    </div>

    <div class="note-editor">
      <div v-show="note.id>0" class="note-save-bar-info">
        <template v-if="saveTime==0">{{$t('未保存')}}</template>
        <template v-if="saveTime>0">{{$t('已保存')}} {{saveTime|fmtDeltaTime}}</template>
      </div>

      <div class="note-editor-opt">
        <div class="simditor simditor-mac simditor-toolbar-noborder note-editor-toolbar" ref="editorToolbar"></div>
        <div v-show="note.id>0" class="note-editor-btn">
          <Button @click="deleteNote" style="margin-right:10px" size="small" type="error" icon="ios-trash">
            {{$t('删除')}}
          </Button>
          <Button @click="saveNote" style="margin-right:20px" size="small" type="default">{{$t('保存')}}</Button>
        </div>
      </div>
      <div ref="noteContent" class="note-content-box scrollbox">
        <div v-if="!note.id" class="content-view-tips">{{$t('点击左侧页面查看详情')}}</div>
        <div v-show="note.id>0" class="simditor-toolbar-noborder note-content-area">
          <div class="note-info-bar">
            <div class="note-info-bar-category">
              <NoteTag
                v-for="item in note.tagInfoList"
                :key="item.id"
                :value="item"></NoteTag>
            </div>
            <div class="note-info-bar-opt">
              <Button @click="showSelectTagDialog" size="small" type="default" icon="ios-pricetags-outline">
                {{$t('标签')}}
              </Button>

              <Poptip placement="bottom-end" ref="poptip" transfer class="poptip-full">
                <Button style="margin-left:5px" size="small" type="default" icon="ios-share-alt-outline">{{$t('分享')}}
                </Button>
                <div slot="content">
                  <div class="share-popup-box">
                    <div class="share-title">{{$t('链接地址')}}</div>
                    <div class="share-address" v-clipboard:copy="shareURL" v-clipboard:success="copySuccess">
                      {{shareURL}}
                    </div>
                    <div class="share-title">{{$t('二维码')}}</div>
                    <div style="position: relative;">
                      <qrcode :value="shareURL" :options="{ size: 200 }"></qrcode>
                    </div>
                  </div>
                </div>
              </Poptip>
            </div>

          </div>
          <div v-show="note.id>0" class="note-name">
            <input :maxlength="100" v-model.trim="note.title" type="text" :placeholder="$t('输入笔记标题')"></input>
          </div>
          <div v-show="note.id>0" class="note-content">
            <textarea ref="noteEditorTextarea"></textarea>
          </div>
        </div>
        <HtmlCatalog
          ref="htmlCatalog"
          class="note-catalog"
          :offset-top="72"
          :offset-left="offsetLeft"
          :scroll-box="$refs.noteContent" />
      </div>
    </div>
  </div>
</template>

<script>
  //
  import qrcode from '@xkeshi/vue-qrcode';

  export default {
    mixins: [componentMixin],
    components: {
      qrcode,
    },
    data() {
      return {
        tagSelectPopupTopVisible: false,
        showSideMenu: true,
        pageQuery: {
          pageIndex: 1,
          pageSize: 50,
          totalPage: 1,
          totalCount: 0,
          keyword: null,
          tagIdList: null,
        },
        saveTime: null,
        shareURL: null,
        noteList: [],
        tagList: [],
        note: {},
      };
    },
    computed: {
      selectTagName() {
        var names = [];
        this.tagList.forEach(item => {
          if (item.isSelected) {
            names.push(item.name);
          }
        });
        return names.join(',');
      },
      offsetLeft() {
        return this.showSideMenu ? 300 : 0;
      },
    },
    mounted() {
      this.loadData();
      this.loadTagList();
      this.addKeyListener();
    },
    beforeDestroy() {
      this.removeListener();
    },
    methods: {
      removeListener() {
        document.removeEventListener('keydown', this.keyHandler);
      },
      addKeyListener() {
        document.addEventListener('keydown', this.keyHandler, false);
      },
      keyHandler(e) {
        if (e.keyCode == 83 && (navigator.platform.match('Mac') ? e.metaKey : e.ctrlKey)) {
          e.preventDefault();
          this.saveNote();
        }
      },
      loadData(resetPage) {
        if (resetPage) {
          this.pageQuery.pageIndex = 1;
        }
        this.pageQuery.tagIdList = [];
        this.tagList.forEach(item => {
          if (item.isSelected) {
            this.pageQuery.tagIdList.push(item.id);
          }
        });
        app.invoke('NoteAction.getNoteList', [app.token, this.pageQuery], (info) => {
          this.noteList = info.list;
          this.pageQuery.totalCount = info.count;
          if (info.count == 0) {
            this.pageQuery.totalPage = 1;
          } else {
            var t = Math.ceil(info.count / this.pageQuery.pageSize);
            this.pageQuery.totalPage = t;
          }
        });
      },
      resetQuery() {
        this.pageQuery.keyword = null;
        this.tagList.forEach(item => {
          item.isSelected = false;
        });
      },
      loadTagList() {
        this.tagList = [];
        app.invoke('NoteAction.getMyNoteTagList', [app.token], (list) => {
          list.forEach(item => {
            item.isSelected = false;
          });
          this.tagList = list;
        });
      },
      changePage(delta) {
        var t = this.pageQuery.pageIndex + delta;
        if (t <= 0 || t > this.pageQuery.totalPage) {
          return;
        }
        this.pageQuery.pageIndex = t;
        this.loadData();
      },
      clickAddNote() {
        this.autoSave(() => {
          var bean = {
            title: '未命名',
          };
          app.invoke('NoteAction.addNote', [app.token, bean], (id) => {
            this.resetQuery();
            this.loadData(true);
            this.loadNote(id);
          });
        });
      },
      loadNote(id) {
        app.invoke('NoteAction.getNoteByUuid', [app.token, id], (info) => {
          this.note = info;

          this.$nextTick(() => {
            this.createEditor();
            if (info.content) {
              this.editor.setValue(info.content);
            } else {
              this.editor.setValue('');
            }
          });
          this.saveTime = null;
          var host = app.getHost();
          this.shareURL = host + '/noteshare.html?id=' + info.uuid;
        });
      },
      updateTitle() {
        this.noteList.forEach(item => {
          if (item.uuid == this.note.uuid) {
            item.title = this.note.title;
          }
        });
      },
      saveNote() {
        if (this.note.id == null) {
          return;
        }
        if (this.note.title == null || this.note.title == '') {
          app.toast(this.$t('请输入标题'));
          return;
        }
        this.updateTitle();
        this.note.content = this.editor.getValue();
        app.invoke('NoteAction.updateNote', [app.token, this.note], (info) => {
          this.saveTime = new Date().getTime();
        });
      },
      autoSave(callback) {
        if (this.saveTime != 0 || this.note.id == null) {
          callback();
          return;
        }
        if (this.note.title == null || this.note.title == '') {
          this.note.title = '未命名';
        }
        this.updateTitle();
        this.note.content = this.editor.getValue();
        app.invoke('NoteAction.updateNote', [app.token, this.note], (info) => {
          callback();
        });
      },
      deleteNote() {
        app.confirm(this.$t('确定要删除', [this.note.title]), () => {
          app.invoke('NoteAction.deleteNote', [app.token, this.note.id], (info) => {
            this.loadData(true);
            this.note = {};
          });
        });
      },
      selectNote(item) {
        this.autoSave(() => {
          this.loadNote(item.uuid);
        });
      },
      selectTag(item) {
        item.isSelected = !item.isSelected;
        this.loadData(true);
      },
      copySuccess() {
        app.toast(this.$t('分享链接地址已复制到剪切板中'));
      },
      createEditor() {
        if (this.editor != null) {
          return;
        }
        var tabBarFloat = true;
        var toolbar = ['title', 'bold', 'italic', 'underline', 'strikethrough', 'fontScale', 'color', 'mark', 'checklist',
          '|', 'ol', 'ul', 'blockquote', 'code', 'table',
          '|', 'link', 'image', 'hr',
          '|', 'indent', 'outdent', 'alignment'];
        var editor = new Simditor({
          textarea: this.$refs.noteEditorTextarea,
          toolbar: toolbar,
          upload: true,
          toolbarFloat: tabBarFloat,
          pasteImage: true,
          upload: {
            url: app.serverAddr + '/p/file/upload?token=' + app.token,
            fileKey: 'file',
            connectionCount: 3,
          },
        });
        this.editor = editor;
        editor.cleanNodeFormat = true;
        editor.on('valuechanged', () => {
          this.note.content = this.editor.getValue();
          this.saveTime = 0;
          this.$nextTick(() => {
            this.$refs.htmlCatalog.analyzeElHtml($('.note-content .simditor-body')[0]);
          });
        });
        $('.note-content .simditor-toolbar').appendTo(this.$refs.editorToolbar);
      },
      closePopup() {
        this.$emit('popup-close');
      },
      showSelectTagDialog() {
        this.saveNote();
        var selectedList = this.note.tagInfoList;
        app.showDialog(NoteTagDialog, {
          selectedList: selectedList,
          callback: (list) => {
            this.updateTagList(list);
          },
        });
      },
      updateTagList(list) {
        this.loadTagList();
        app.invoke('NoteAction.updateNoteTags', [app.token, this.note.id, list], (info) => {
          this.loadNote(this.note.uuid);
        });
      },
    },
  };
</script>
