<style scoped>
    .page {
        min-height: calc(100vh - 50px);
        background-color: #F1F4F5;
    }

    .option-bar {
        align-items: center;
        display: flex;
        background-color: #fff;
        width: 100%;
        height: 22px;
        max-height: 22px;
    }

    .opt-right {
        text-align: right;
    }

    .table-info-bar {
        font-size: 12px;
        font-weight: bold;
        color: #999999;
        text-align: right;
    }

    .doc-name input {
        width: 100%;
        border: none;
        outline: 0;
        font-weight: bold;
        font-size: 18px;
        color: #333;
        margin-bottom: 20px;
    }


</style>

<i18n>
    {
    "en": {
    "创建页面": "Create",
    "已保存": "saved",
    "未保存": "unsaved",
    "保存": "Save",
    "保存为修订版": "Save as Version",
    "关闭": "Close",
    "输入文档标题": "Title",
    "复制": "Copy",
    "请输入文档标题": "Please input title",
    "保存成功": "Success",
    "发布成功": "Success"
    },
    "zh_CN": {
    "创建页面": "创建页面",
    "已保存": "已保存",
    "未保存": "未保存",
    "保存": "保存",
    "保存为修订版": "保存为修订版",
    "关闭": "关闭",
    "输入文档标题": "输入文档标题",
    "复制": "复制",
    "请输入文档标题": "请输入文档标题",
    "保存成功": "保存成功",
    "发布成功": "发布成功"
    }
    }
</i18n>

<template>
    <Modal
        ref="dialog" v-model="showDialog"
        :closable="false"
        :mask-closable="false"
        :loading="false" :title="$t('创建页面')" class="fullscreen-modal"
        width="100%"
        :footer-hide="true">
        <Row class="option-bar" slot="header">
            <Col span="8" class="opt-left ">
             <span v-if="lastSaveTime>0" style="color:#999">
                {{$t('已保存')}} {{lastSaveTime|fmtDateTime}}
             </span>
                <span v-if="lastSaveTime==0" style="color:#999">
                {{$t('未保存')}}
             </span>
            </Col>
            <Col span="8" class="opt-left" style="text-align:center">

            </Col>
            <Col span="8" class="opt-right">
                <Form inline>
                    <FormItem>
                        <Button @click="saveDoc()" type="default" icon="ios-cloud-upload">{{$t('保存')}}</Button>
                    </FormItem>
                    <FormItem>
                        <Button @click="releasePage" type="success" icon="ios-paper-plane-outline">{{$t('保存为修订版')}}
                        </Button>
                    </FormItem>
                    <FormItem>
                        <Button @click="closePage" type="default">{{$t('关闭')}}</Button>
                    </FormItem>
                </Form>
            </Col>
        </Row>


        <div class="page">
            <div class="page-wrap">
                <div class="page-content" style="min-height:750px;width:750px;">
                    <div class="doc-name"><input v-model.trim="formItem.name" type="text"
                                                 :placeholder="$t('输入文档标题')"></input></div>
                    <MarkdownEditor ref="editor"></MarkdownEditor>
                </div>

            </div>
        </div>
    </Modal>
</template>

<script>

    export default {
        mixins: [componentMixin],
        data() {
            return {
                showSendMailBox: false,
                formItem: {
                    id: 0,
                    name: null,
                    content: null,
                },
                lastSaveTime: 0,
                outlineList: [],
                checkInterval: 0,
                autoSaveInterval: 0,
                isCompanyWiki: false
            }
        },
        watch: {
            showDialog(val) {
                if (val == false) {
                    this.removeListener();
                }
            }
        },
        methods: {
            pageLoad() {
                this.isCompanyWiki = !(this.args.projectId||this.args.parentId||this.args.wikiId);
                this.formItem.type = 2;
                this.formItem.wikiId = this.args.wikiId;
                this.formItem.parentId = this.args.parentId;
                if (this.args.id) {
                    app.invoke(`BizAction.get${this.isCompanyWiki ? 'Company' : ''}WikiPageById`, [app.token, this.args.id], (info) => {
                        this.formItem = info;
                        if (info.content) {
                            this.$refs.editor.setValue(info.content);
                        }
                        //
                        if (this.args.copy) {
                            this.formItem.id = 0;
                            this.formItem.originalId = null;
                            this.formItem.name = this.formItem.name + this.$t('复制');
                        }
                    });
                }
                this.createEditor();
                this.addKeyListener();

            },
            removeListener() {
                document.removeEventListener("keydown", this.keyHandler)
            },
            addKeyListener() {
                document.addEventListener("keydown", this.keyHandler, false);
            },
            keyHandler(e) {
                if (e.keyCode == 83 && (navigator.platform.match("Mac") ? e.metaKey : e.ctrlKey)) {
                    e.preventDefault();
                    this.saveDoc();
                }
            },
            createEditor() {

            },
            saveDoc(callback) {
                if (this.formItem.name == null || this.formItem.name == "") {
                    app.toast(this.$t('请输入文档标题'));
                    return;
                }
                var action = this.formItem.id == 0 ? `BizAction.add${this.isCompanyWiki?'Company':''}WikiPage`:`BizAction.update${this.isCompanyWiki?'Company':''}WikiPage`;
                this.formItem.content = this.$refs.editor.getValue();
                app.invoke(action, [app.token, this.formItem], (id) => {
                    if (id) {
                        this.formItem.id = id;
                    }
                    this.lastSaveTime = new Date().getTime();
                    app.toast(this.$t('保存成功'));
                    app.postMessage(this.isCompanyWiki?'company.wiki.edit':'wikipage.edit', this.formItem.id);
                    // app.postMessage('wikipage.edit', this.formItem.id)
                    if (callback) {
                        callback();
                    }
                })
            },

            releasePage() {
                this.saveDoc(() => {
                    app.invoke(`BizAction.release${this.isCompanyWiki?'Company':''}WikiPage`, [app.token, this.formItem.id], (id) => {
                        app.toast(this.$t('发布成功'));
                        app.postMessage(this.isCompanyWiki?'company.wiki.edit':'wikipage.edit', id);
                        // app.postMessage('wikipage.edit', id);
                        this.closePage();
                    })
                })
            },
            closePage() {
                this.removeListener();
                this.showDialog = false;
            }
        }
    }
</script>
