<style scoped>
    .page{
        min-height: calc(100vh - 52px);
        background-color: #F1F4F5;
        width: 100%;
        display: flex;
        position: relative;
    }
    .sheet-box{
        width: 100%;
        flex:1;
    }
    .option-bar{
        align-items: center;
        display: flex;
        background-color: #fff;
        width:100%;
        height:22px;
        max-height: 22px;
    }
    .opt-right{
        text-align: right;
    }
    .table-info-bar{
        font-size:12px;
        font-weight:bold;
        color:#999999;
        text-align:right;
    }
    .doc-name input{
        width:100%;
        border:none;
        outline: 0;
        font-weight: bold;
        font-size: 18px;
        color:#333;
    }
    .option-box{
        width:150px;
        height:125px;
        padding:5px;
        position: absolute;
        background-color: rgba(255,255,255,0.9);
        box-shadow: rgba(0, 0, 0, 0.15) 0px 1px 3px 0px  !important;
        color:#000;
        font-weight: bold;
        border-radius: 6px;
        top:48px;
        right:0;
        z-index:999;
    }
    .tag-color{
        display: inline-block;
        width:20px;
        height: 20px;
        border-radius: 50%;
        margin-right:8px;
        color:#fff;
        overflow: hidden;
    }
    @import '../../../../assets/spreadsheet.css';
</style>

<i18n>
    {
    "en": {
         "创建页面": "Create",
    "输入文档标题": "Title",
    "已保存": "saved",
    "未保存": "unsaved",
    "保存": "Save",
    "保存并发布": "Save & Publish",
    "关闭": "Close",
    "撤销": "Undo",
    "重做": "Redo",
    "文本颜色": "Font Color",
    "背景颜色": "Bg Color",
    "粗体": "Bold",
    "斜体": "Italics",
    "锁定单元格": "Lock",
    "解锁单元格": "Unlock",
    "文件": "File",
    "格式": "Format",
    "编辑": "Edit",
    "清空": "Clear",
    "清空值": "Clear value",
    "清空样式": "Clear style",
    "清空全部": "Clear all",
    "插入": "Insert",
    "列": "Column",
    "行": "Row",
    "增加列": "Add Column",
    "删除列": "Delete Column",
    "增加行": "Add Row",
    "删除行": "Delete Row",
    "配置": "Setting",
    "下划线": "Underline",
    "对齐": "Align",
    "左": "Left",
    "右": "Right",
    "居中": "Center",
    "复制": "Copy",
    "请输入文档标题": "Input title",
    "保存成功": "Success",
    "发布成功": "Success",
    "帮助": "Help"
    },
    "zh_CN": {
    "创建页面": "创建页面",
    "输入文档标题": "输入文档标题",
    "已保存": "已保存",
    "未保存": "未保存",
    "保存": "保存",
    "保存并发布": "保存并发布",
    "关闭": "关闭",
    "撤销": "撤销",
    "重做": "重做",
    "文本颜色": "文本颜色",
    "背景颜色": "背景颜色",
    "粗体": "粗体",
    "斜体": "斜体",
    "锁定单元格": "锁定单元格",
    "解锁单元格": "解锁单元格",
    "文件": "文件",
    "格式": "格式",
    "编辑": "编辑",
    "清空": "清空",
    "清空值": "清空值",
    "清空样式": "清空样式",
    "清空全部": "清空全部",
    "插入": "插入",
    "列": "列",
    "行": "行",
    "增加列": "增加列",
    "删除列": "删除列",
    "增加行": "增加行",
    "删除行": "删除行",
    "配置": "配置",
    "下划线": "下划线",
    "对齐": "对齐",
    "左": "左",
    "右": "右",
    "居中": "居中",
    "复制": "复制",
    "请输入文档标题": "请输入文档标题",
    "保存成功": "保存成功",
    "发布成功": "发布成功",
    "帮助": "帮助"
    }
    }
</i18n>


<template>
 <Modal
        ref="dialog"  v-model="showDialog"
        :closable="false"
        :mask-closable="false"
        :loading="false" :title="$t('创建页面')" class="fullscreen-modal"
         width="100%"
         :footer-hide="true">
    <Row class="option-bar" slot="header">
          <Col span="12" class="opt-left" style="text-align:center">
                <div class="doc-name">
                  <input  maxlength="50" v-model.trim="formItem.name" type="text" :placeholder="$t('输入文档标题')"></input>
                </div>
          </Col>
          <Col span="12" class="opt-right">
            <Form inline>
                <FormItem>
                     <span v-if="lastSaveTime>0" style="color:#999">
                       {{$t('已保存')}} {{lastSaveTime|fmtDateTime}}
                    </span>
                    <span v-if="lastSaveTime==0" style="color:#999">
                       {{$t('未保存')}}
                    </span>
                </FormItem>
              <FormItem>
                <Button @click="saveDoc()" type="default" icon="ios-cloud-upload">{{$t('保存')}}</Button>
              </FormItem>
            <FormItem  v-if="false">
                    <Button @click="releasePage" type="success" icon="ios-paper-plane-outline">{{$t('保存并发布')}}</Button>
              </FormItem>
              <FormItem>
                  <Button @click="closePage" type="default">{{$t('关闭')}}</Button>
              </FormItem>
             </Form>
            </Col>
       </Row>



    <div  class="page">
        <div ref="container" class="sheet-box"></div>
    </div>
</Modal>
</template>

<script>
import { Spreadsheet,i18n} from "dhx-spreadsheet";

export default {
  mixins: [componentMixin],
  data(){
    return {
        formItem:{
            id:0,
            name:null,
            content:null,
            parentId:null,
            projectId:null,
        },
        lastSaveTime:0,
        autoSaveInterval:0,
        isCompanyWiki: false
    }
  },
  watch:{
      showDialog(val){

      }
  },
  mounted(){
      var cn = {
        undo: this.$t('撤销'),
        redo: this.$t('重做'),
        textColor: this.$t('文本颜色'),
        backgroundColor: this.$t('背景颜色'),
        bold: this.$t('粗体'),
        italic: this.$t('斜体'),

        lockCell: this.$t('锁定单元格'),
        unlockCell: this.$t('解锁单元格'),

        file: this.$t('文件'),
        format: this.$t('格式'),
        edit: this.$t('编辑'),
        clear: this.$t('清空'),
        clearValue: this.$t('清空值'),
        clearStyles: this.$t('清空样式'),
        clearAll: this.$t('清空全部'),

        insert: this.$t('插入'),
        columns: this.$t('列'),
        rows: this.$t('行'),
        addColumn: this.$t('增加列'),
        removeColumn: this.$t('删除列'),
        addRow: this.$t('增加行'),
        removeRow: this.$t('删除行'),

        configuration: this.$t('配置'),
        underline: this.$t('下划线'),

        align: this.$t('对齐'),
        left: this.$t('左'),
        right: this.$t('右'),
        center: this.$t('居中'),
        help: this.$t('帮助')
    };
    i18n.setLocale("spreadsheet", cn);
    this.spreadsheet = new Spreadsheet(this.$refs.container, {
			rowsCount: 100,
            colsCount: 30,
            toolbarBlocks: [
                "undo","colors","decoration","align",
                "lock", "clear", "rows", "columns"
            ]
    });
  },
  methods:{
        pageLoad(){
            this.isCompanyWiki = !(this.args.projectId||this.args.parentId||this.args.wikiId);
            this.formItem.type=4;
            this.formItem.wikiId=this.args.wikiId;
            this.formItem.parentId=this.args.parentId;
            if(this.args.id){
                app.invoke(`BizAction.get${this.isCompanyWiki?'Company':''}WikiPageById`,[app.token,this.args.id],(info)=>{
                    this.formItem=info;
                    var data=null;
                    if(info.content){
                        data=JSON.parse(info.content);
                        this.spreadsheet.parse(data);
                    }
                    if(this.args.copy){
                        this.formItem.id=0;
                        this.formItem.originalId=null;
                        this.formItem.name=this.formItem.name+this.$t('复制');
                    }
                });
            }
            this.addKeyListener();
            //
        },
        removeListener(){
              document.removeEventListener("keydown",this.keyHandler)
        },
        addKeyListener(){
             document.addEventListener("keydown",this.keyHandler, false);
        },
        keyHandler(e){
           if (e.keyCode == 83 && (navigator.platform.match("Mac") ? e.metaKey : e.ctrlKey)) {
                e.preventDefault();
                this.saveDoc();
            }
        },
        saveDoc(callback){
            if(this.formItem.name==null||this.formItem.name==""){
                app.toast(this.$t('请输入文档标题'));
                return;
            }
            var action = this.formItem.id == 0 ? `BizAction.add${this.isCompanyWiki?'Company':''}WikiPage`:`BizAction.update${this.isCompanyWiki?'Company':''}WikiPage`;
            this.formItem.content=JSON.stringify(this.spreadsheet.serialize())
            app.invoke(action,[app.token,this.formItem],(id)=>{
                if(id){
                    this.formItem.id=id;
                }
                this.lastSaveTime=new Date().getTime();
                app.toast(this.$t('保存成功'));
                app.postMessage(this.isCompanyWiki?'company.wiki.edit':'wikipage.edit', this.formItem.id);
                if(callback){
                    callback();
                }
            })
        },
        releasePage(){
           this.saveDoc(()=>{
                app.invoke(`BizAction.release${this.isCompanyWiki?'Company':''}WikiPage`,[app.token,this.formItem.id],(id)=>{
                    app.toast(this.$t('发布成功'));
                    app.postMessage(this.isCompanyWiki?'company.wiki.edit':'wikipage.edit', id);
                    this.closePage();
                })
           })
        },
        closePage(){
            this.removeListener();
            this.showDialog=false;
        }
  }
}
</script>
