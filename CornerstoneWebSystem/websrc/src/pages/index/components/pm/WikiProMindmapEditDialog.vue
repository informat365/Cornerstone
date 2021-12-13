<style scoped>
    .page {
        min-height: calc(100vh - 50px);
        background-color: #F1F4F5;
        width: 100%;
        display: flex;
        position: relative;
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
        text-align: center;
    }
</style>

<i18n>
    {
    "en": {
    "创建页面": "Create",
    "已保存": "saved",
    "未保存": "unsaved",
    "输入文档标题": "Title",
    "保存": "Save",
    "保存并发布": "Save & Publish",
    "关闭": "Close",
    "复制": "Copy",
    "请输入文档标题": "Input title",
    "请输入文档内容": "Input content",
    "保存成功": "Success",
    "发布成功": "Success"
    },
    "zh_CN": {
    "创建页面": "创建页面",
    "已保存": "已保存",
    "未保存": "未保存",
    "输入文档标题": "输入文档标题",
    "保存": "保存",
    "保存并发布": "保存并发布",
    "关闭": "关闭",
    "复制": "复制",
    "请输入文档标题": "请输入文档标题",
    "请输入文档内容": "请输入文档内容",
    "保存成功": "保存成功",
    "发布成功": "发布成功"
    }
    }
</i18n>
<template>
    <Modal ref="dialog" v-model="showDialog" :closable="false" :mask-closable="false" :loading="false"
           :title="$t('创建页面')"
           class="fullscreen-modal" width="100%" :footer-hide="true">
        <Row class="option-bar" slot="header">
            <Col span="6" class="opt-left">
            <span v-if="lastSaveTime>0" style="color:#999">
                {{$t('已保存')}} {{lastSaveTime|fmtDateTime}}
            </span>
                <span v-if="lastSaveTime==0" style="color:#999">
                {{$t('未保存')}}
            </span>
            </Col>
            <Col span="12" class="opt-left" style="text-align:center">
                <div class="doc-name">
                    <input maxlength="50" v-model.trim="formItem.name" type="text" :placeholder="$t('输入文档标题')">
                </div>
            </Col>
            <Col span="6" class="opt-right">
                <Form inline>
                    <FormItem>
                        <Button @click="saveDoc()" type="default" icon="ios-cloud-upload">{{$t('保存')}}</Button>
                    </FormItem>
                    <FormItem>
                        <Button @click="closePage" type="default">{{$t('关闭')}}</Button>
                    </FormItem>
                </Form>
            </Col>
        </Row>

        <MinderEditor class="page" v-model="content" @ready="editorInit"></MinderEditor>
    </Modal>
</template>

<script>
    export default {
        mixins: [componentMixin],
        data() {
            return {
                formItem: {
                    id: 0,
                    name: null,
                    content: null,
                    parentId: null,
                    projectId: null,
                },
                content: null,
                optBoxStyle: null,
                currentNodeColor: null,
                selectRootNode: false,
                colorArray: [
                    "#2E94B9",
                    "#F0B775",
                    "#D25565",
                    "#F54EA2",
                    "#42218E",
                    "#5BE7C4",
                    "#525564",
                    "#FDC043",
                ],
                lastSaveTime: 0,
                autoSaveInterval: 0,
                isCompanyWiki: false
            }
        },
        watch: {
            showDialog(val) {

            }
        },
        methods: {
            pageLoad() {
                this.isCompanyWiki = !(this.args.projectId||this.args.parentId||this.args.wikiId);
                this.formItem.type = 5;
                this.formItem.wikiId = this.args.wikiId;
                this.formItem.parentId = this.args.parentId;
            },
            editorInit() {
                if (this.args.id) {
                    app.invoke(`BizAction.get${this.isCompanyWiki ? 'Company' : ''}WikiPageById`, [app.token, this.args.id], (info) => {
                        this.formItem = info;
                        var data = null;
                        if (info.content) {
                            data = JSON.parse(info.content);
                        }
                        this.$nextTick(() => {
                            this.createEditor(data)
                        })
                        if (this.args.copy) {
                            this.formItem.id = 0;
                            this.formItem.originalId = null;
                            this.formItem.name = this.formItem.name + this.$t('复制');
                        }
                    });
                } else {
                    this.$nextTick(this.createEditor)
                }
                this.addKeyListener();
            },
            preventDefault(e) {
                e.preventDefault();
            },
            removeListener() {
                document.removeEventListener("keydown", this.keyHandler);
                window.removeEventListener("mousewheel", this.preventDefault, this.passiveSupported() ? {passive: false} : false);
            },
            passiveSupported() {
                var passiveSupported = false;
                try {
                    var options = Object.defineProperty({}, "passive", {
                        get: function () {
                            passiveSupported = true;
                        }
                    });
                    window.addEventListener("test", null, options);
                } catch (err) {
                }
                return passiveSupported;
            },
            addKeyListener() {
                document.addEventListener("keydown", this.keyHandler, false);
                window.addEventListener("mousewheel", this.preventDefault, this.passiveSupported() ? {passive: false} : false);
            },
            keyHandler(e) {
                if (e.keyCode == 83 && (navigator.platform.match("Mac") ? e.metaKey : e.ctrlKey)) {
                    e.preventDefault();
                    this.saveDoc();
                }
            },
            createEditor(data) {
                // var mind = {
                //     "root": {
                //         "data": {
                //             "created": new Date(),
                //             "text": "想法？脑洞？"
                //         },
                //         "children": []
                //     }
                // };
                if (data != null) {
                    this.content = data;
                }

            },
            saveDoc(callback) {
                if (this.formItem.name == null || this.formItem.name == "") {
                    app.toast(this.$t('请输入文档标题'));
                    return;
                }
                if (this.content == null) {
                    app.toast(this.$t('请输入文档内容'));
                    return;
                }
                var action = this.formItem.id == 0 ? `BizAction.add${this.isCompanyWiki ? 'Company' : ''}WikiPage` : `BizAction.update${this.isCompanyWiki ? 'Company' : ''}WikiPage`;
                //var t = this.minder.exportJson();
                this.formItem.content = JSON.stringify(this.content);
                app.invoke(action, [app.token, this.formItem], (id) => {
                    if (id) {
                        this.formItem.id = id;
                    }
                    this.lastSaveTime = new Date().getTime();
                    app.toast(this.$t('保存成功'));
                    app.postMessage(this.isCompanyWiki ? 'company.wiki.edit' : 'wikipage.edit', this.formItem.id);
                    if (callback) {
                        callback();
                    }
                })
            },
            releasePage() {
                this.saveDoc(() => {
                    app.invoke(`BizAction.release${this.isCompanyWiki ? 'Company' : ''}WikiPage`, [app.token, this.formItem.id], (id) => {
                        app.toast(this.$t('发布成功'));
                        app.postMessage(this.isCompanyWiki ? 'company.wiki.edit' : 'wikipage.edit', id);
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
