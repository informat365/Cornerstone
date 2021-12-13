<style scoped>
    .page {
        min-height: calc(100vh - 50px);
        background-color: #F1F4F5;
        width: 100%;
        display: flex;
        position: relative;
    }

    .mind-box {
        width: 100%;
        flex: 1;
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
    }

    .option-box {
        width: 150px;
        padding: 5px;
        position: absolute;
        background-color: rgba(255, 255, 255, 0.9);
        box-shadow: rgba(0, 0, 0, 0.15) 0px 1px 3px 0px !important;
        color: #000;
        font-weight: bold;
        border-radius: 6px;
        top: 48px;
        right: 0;
        z-index: 999;
    }

    .tag-color {
        display: inline-block;
        width: 20px;
        height: 20px;
        border-radius: 50%;
        margin-right: 8px;
        color: #fff;
        overflow: hidden;
    }
</style>

<i18n>
    {
    "en": {
    "创建页面": "Create",
    "已保存": "saved",
    "未保存": "unsaved",
    "保存": "Save",
    "保存并发布": "Save",
    "关闭": "Close",
    "输入文档标题": "Title",
    "复制": "Copy",
    "请输入文档标题": "Please input title",
    "新增子节点": "Add node",
    "保存成功": "Success",
    "删除节点": "Delete",
    "复制": "Copy",
    "未命名": "Untitled",
    "想法？脑洞？": "Idea?",
    "未命名节点": "Untitled",
    "发布成功": "Success"
    },
    "zh_CN": {
    "创建页面": "创建页面",
    "已保存": "已保存",
    "未保存": "未保存",
    "保存": "保存",
    "保存并发布": "保存并发布",
    "关闭": "关闭",
    "输入文档标题": "输入文档标题",
    "复制": "复制",
    "请输入文档标题": "请输入文档标题",
    "新增子节点": "新增子节点",
    "保存成功": "保存成功",
    "删除节点": "删除节点",
    "复制": "复制",
    "未命名": "未命名",
    "想法？脑洞？": "想法？脑洞？",
    "未命名节点": "未命名节点",
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
            <Col span="6" class="opt-left ">
             <span v-if="lastSaveTime>0" style="color:#999">
                {{$t('已保存')}} {{lastSaveTime|fmtDateTime}}
             </span>
                <span v-if="lastSaveTime==0" style="color:#999">
                {{$t('未保存')}}
             </span>
            </Col>
            <Col span="12" class="opt-left" style="text-align:center">
                <div class="doc-name">
                    <input maxlength="50" v-model.trim="formItem.name" type="text" :placeholder="$t('输入文档标题')"></input>
                </div>
            </Col>
            <Col span="6" class="opt-right">
                <Form inline>
                    <FormItem>
                        <Button @click="saveDoc()" type="default" icon="ios-cloud-upload">{{$t('保存')}}</Button>
                    </FormItem>
                    <FormItem v-if="false">
                        <Button @click="releasePage" type="success" icon="ios-paper-plane-outline">{{$t('保存并发布')}}
                        </Button>
                    </FormItem>
                    <FormItem>
                        <Button @click="closePage" type="default">{{$t('关闭')}}</Button>
                    </FormItem>
                </Form>
            </Col>
        </Row>


        <div class="page">
            <div class="mind-box"></div>
            <div v-if="optBoxStyle" class="option-box" :style="optBoxStyle">
                <div style="padding:5px">
                    <IconButton @click="addNode" icon="md-add" :title="$t('新增子节点')"></IconButton>
                </div>
                <div v-if="selectRootNode==false" style="padding:5px">
                    <IconButton @click="deleteNode" icon="ios-trash-outline" :title="$t('删除节点')"></IconButton>
                </div>
                <div style="padding:5px;padding-left:15px">
                <span @click="setNodeColor(item)" v-for="item in colorArray" :key="item" class="tag-color"
                      :style="{backgroundColor:item}">
                    <Icon size="20" v-if="item==currentNodeColor" type="md-checkmark"/>
                </span>
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
                formItem: {
                    id: 0,
                    name: null,
                    content: null,
                    parentId: null,
                    projectId: null,
                },
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
                this.formItem.type = 3;
                this.formItem.wikiId = this.args.wikiId;
                this.formItem.parentId = this.args.parentId;
                if (this.args.id) {
                    app.invoke(`BizAction.get${this.isCompanyWiki?'Company':''}WikiPageById`, [app.token, this.args.id], (info) => {
                        this.formItem = info;
                        var data = null;
                        if (info.content) {
                            data = JSON.parse(info.content);
                        }
                        this.$nextTick(() => {
                            this.createEditor(data)
                        })
                        //
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

            createEditor(data) {
                var mind = {
                    "meta": {
                        "name": this.$t('未命名'),
                        "author": "",
                        "version": "1"
                    },
                    "format": "node_tree",
                    "data": {
                        "id": "root", "topic": this.$t('想法？脑洞？'), "children": []
                    }
                };
                if (data == null) {
                    data = mind;
                }
                var mindBox = this.$el.getElementsByClassName('mind-box')[0]
                var options = {
                    container: mindBox,
                    theme: 'orange',
                    editable: true
                };
                var jm = new jsMind(options);
                jm.show(data);
                jm.add_event_listener(this.onSelectNode);
                this.mindmap = jm;
                this.mindmap.resize();
            },
            onSelectNode(e, v) {
                if (v.evt == "select_node") {
                    if (v.node._data) {
                        var canvas = this.$el.getElementsByClassName('jsmind-inner')[0]
                        this.selectRootNode = v.node.id == 'root';
                        var view = v.node._data.view;
                        this.optBoxStyle = {};
                        this.optBoxStyle.top = (view.abs_y - canvas.scrollTop) + "px";
                        this.optBoxStyle.left = (view.abs_x + view.width + 15 - canvas.scrollLeft) + "px";
                    }
                }
                if (v.evt == 'select_clear' || v.evt == 'remove_node' || v.evt == 'move_node') {
                    this.optBoxStyle = null;
                }

            },
            get_selected_nodeid() {
                var selected_node = this.mindmap.get_selected_node();
                if (!!selected_node) {
                    return selected_node.id;
                } else {
                    return null;
                }
            },
            addNode() {
                var selected_node = this.mindmap.get_selected_node(); // as parent of new node
                if (!selected_node) {
                    return;
                }
                var nodeid = jsMind.util.uuid.newid();
                var topic = this.$t('未命名节点');
                var node = this.mindmap.add_node(selected_node, nodeid, topic);
                //
                var bgColor = selected_node.data["background-color"];
                if (bgColor) {
                    this.mindmap.set_node_color(nodeid, bgColor, null);
                }
            },
            deleteNode() {
                var selected_id = this.get_selected_nodeid();
                if (!selected_id) {
                    return;
                }
                this.mindmap.remove_node(selected_id);
            },
            setNodeColor(color) {
                var selected_id = this.get_selected_nodeid();
                if (!selected_id) {
                    return;
                }
                this.mindmap.set_node_color(selected_id, color, null);
            },
            saveDoc(callback) {
                if (this.formItem.name == null || this.formItem.name == "") {
                    app.toast(this.$t('请输入文档标题'));
                    return;
                }
                var action = this.formItem.id == 0 ? `BizAction.add${this.isCompanyWiki?'Company':''}WikiPage`:`BizAction.update${this.isCompanyWiki?'Company':''}WikiPage`;
                var t = this.mindmap.get_data('node_tree');
                this.formItem.content = JSON.stringify(t);
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
