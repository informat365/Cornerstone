<style scoped>
    .mindmap {
        width: 100%;
        height: calc(100vh - 95px)
    }

    .option-box {
        width: 150px;
        height: 40px;
        padding: 5px;
        position: absolute;
        background-color: rgba(255, 255, 255, 0.9);
        box-shadow: rgba(0, 0, 0, 0.15) 0px 1px 3px 0px !important;
        color: #000;
        font-weight: bold;
        border-radius: 6px;
        z-index: 999;
    }

    .view-control {
        position: absolute;
        top: 10px;
        left: 0px;
        z-index: 999;
        width: 100%;
        font-size: 12px;
        padding-right: 20px;
    }

    .opt-divider {
        width: 1px;
        display: inline-block;
        background: #A7A7A7;
        height: 10px;
        line-height: 10px;
        margin-left: 10px;
        margin-right: 5px;
        vertical-align: middle;
    }

    .opt-reversed {
        display: flex;
        flex-direction: row-reverse;
        align-items: center;
    }
</style>
<i18n>
    {
    "en": {
    "所有":"All",
    "创建":"Add",
    "缩小":"zoom out",
    "放大":"zoom in",
    "总计条数据":"Total {0} items",
    "导出":"Export"
    },
    "zh_CN": {
    "所有":"所有",
    "创建":"创建",
    "缩小":"缩小",
    "放大":"放大",
    "总计条数据":"总计 {0} 条数据",
    "导出":"导出"
    }
    }
</i18n>
<template>
    <div style="position: relative;">
        <div v-show="optBoxStyle.show" class="option-box" :style="optBoxStyle">
            <div style="padding:5px">
                <IconButton @click="addNode" icon="md-add" :title="$t('创建')+objectTypeName"></IconButton>
            </div>
        </div>
        <div class="view-control">
            <Row>
                <Col span="12">
                <span style="color:#999;font-weight:bold">
                            {{$t('总计条数据',[allCount])}}
                </span>
                    <span class="opt-divider"></span>
                    <IconButton @click="exportImage" :title="$t('导出')"></IconButton>
                </Col>
                <Col span="12" class="opt-reversed">
                    <IconButton icon="ios-add" @click="zoomIn" :title="$t('放大')"></IconButton>
                    <IconButton icon="ios-remove" @click="zoomOut" :title="$t('缩小')"></IconButton>
                </Col>
            </Row>
        </div>
        <div class="mindmap scrollbox" ref="mindBox"></div>
    </div>
</template>

<script>
    export default {
        mixins: [componentMixin],
        props: ["queryInfo", "queryForm"],
        data() {
            return {
                title: "TaskMindmapView",
                objectTypeName: "",
                allCount: 0,
                optBoxStyle: {
                    top: 0,
                    left: 0,
                    show: false
                },
                showTaskId: null,
                editingTaskId:0,
                node:null
            };
        },
        mounted() {
            this.objectTypeName = app.dataDictValue('Task.objectType', this.queryForm.objectType)
            //
            var mindBox = this.$refs.mindBox
            mindBox.innerHTML = "";
            var options = {
                container: mindBox,
                theme: 'orange',
                editable: false
            };
            var jm = new jsMind(options);
            jm.add_event_listener(this.onSelectNode);
            this.mindmap = jm;
        },
        methods: {
            pageMessage(type, content) {
                if (type == "category.edit") {
                    this.loadData();
                }
                if (type == 'task.hide') {
                    console.log("task.hide--->", content)
                    if (!!this.showTaskId && content.id == this.showTaskId) {
                        this.showTaskId = null;
                    }
                }
                if(type=='task.editing'){
                    this.editingTaskId  = content.id;
                    if(!!this.editingTaskId){
                        this.mindmap.select_clear();
                    }else{
                        if(this.node){
                            this.mindmap.select_node(this.node);
                        }
                    }
                }
            },
            exportImage() {
                this.mindmap.screenshot.shootDownload()
            },
            loadData() {
                this.loadCategoryNodeList();
            },
            showTaskInfo(item) {
                app.showDialog(TaskDialog, {
                    taskId: item.uuid
                });
            },
            showCreateDialog(categoryId) {
                app.showDialog(TaskCreateDialog, {
                    projectId: this.queryInfo.project.id,
                    type: this.queryForm.objectType,
                    categoryId: categoryId
                });
            },
            loadTaskList() {
                this.queryForm.pageIndex = 1;
                this.queryForm.pageSize = 1000;
                app.invoke("BizAction.getTaskInfoList", [app.token, this.queryForm], info => {
                        this.tableData = info.list;
                        this.allCount = info.list.length;
                        this.createEditor();
                    }
                );
            },
            loadCategoryNodeList() {
                app.invoke("BizAction.getCategoryNodeList", [app.token, this.queryInfo.project.id, this.queryForm.objectType], list => {
                    this.loadTaskList();
                    this.categoryList = list;
                });
            },
            hasCategory(task, categoryId) {
                if (task.categoryIdList == null) {
                    return false;
                }
                for (var i = 0; i < task.categoryIdList.length; i++) {
                    if (task.categoryIdList[i] == categoryId) {
                        return true;
                    }
                }
                return false;
            },
            zoomIn() {
                this.mindmap.view.zoomIn()
            },
            zoomOut() {
                this.mindmap.view.zoomOut()
            },
            createEditor() {
                var mind = {
                    "meta": {
                        "name": this.queryInfo.project.name + "-" + this.objectTypeName,
                        "author": "CORNERSTONE",
                        "version": "1"
                    },
                    "format": "node_tree",
                    "data": {
                        "id": "root", "topic": this.$t('所有') + this.objectTypeName, "children": []
                    }
                };
                for (var i = 0; i < this.categoryList.length; i++) {
                    var t = this.categoryList[i];
                    t.direction = (i % 2 == 0 ? "left" : "right");
                    travalTree(t, (node) => {
                        node.topic = node.name;
                        node.categoryId = node.id;
                        node["background-color"] = node.color;
                        if (node.children == null) {
                            node.children = [];
                        }
                    })
                    //
                    mind.data.children.push(t);
                }
                //

                for (var i = 0; i < this.categoryList.length; i++) {
                    var t = this.categoryList[i];
                    travalTree(t, (node) => {
                        for (var i = 0; i < this.tableData.length; i++) {
                            var task = this.tableData[i];
                            if (this.hasCategory(task, node.id)) {
                                node.children.push(this.createTaskNode(node, task))
                            }
                        }
                    })
                }
                //
                //未分类任务
                for (var i = 0; i < this.tableData.length; i++) {
                    var task = this.tableData[i];
                    if (task.categoryIdList == null || task.categoryIdList.length == 0) {
                        mind.data.children.push(this.createTaskNode(mind.data, task));
                    }
                }
                //
                this.mindmap.show(mind);
                this.mindmap.resize();
            },
            createTaskNode(node, task) {
                var node = {
                    id: node.id + "-" + task.uuid,
                    uuid: task.uuid,
                    topic: "#" + task.serialNo + (task.statusName?"【" + task.statusName + "】" :"  ")+ task.name,
                    "background-color": "#eeeeee",
                    "foreground-color": "#333333",
                    "border-color": task.statusColor,
                    "cross-line": task.isFinish
                }
                return node;
            },
            addNode() {
                this.optBoxStyle.show = false;
                this.showCreateDialog(this.selectedCategoryId);
            },
            onSelectNode(t, v) {
                if (v.evt == 'select_clear' || v.evt == 'remove_node' || v.evt == 'move_node') {
                    this.optBoxStyle.show = false;
                }
                if (v.evt == "select_node") {
                    if(this.node&&this.node.data&&v.node&&v.node.data&&this.node.data.uuid===v.node.data.uuid){
                        //防止获取焦点重复唤起任务详情
                        return false;
                    }
                    if((v.node&&v.node.data&&this.editingTaskId==v.node.data.uuid)||(this.editingTaskId!==0&&this.editingTaskId)){
                        app.toast("任务详情未保存，请先保存再操作");
                        return false;
                    }
                    // 已开启的task dialog没有关闭
                    if (!!this.showTaskId) {
                        app.postMessage('task.dialog.close', {
                            taskId: this.showTaskId
                        });
                    }
                    this.optBoxStyle.show = false;
                    if (v.node.data.uuid) {
                        app.showDialog(TaskDialog, {
                            taskId: v.node.data.uuid,
                            showTopBar: true
                        });
                        this.showTaskId = v.node.data.uuid;
                        this.node = v.node;
                    }
                    //
                    if (v.node.data.categoryId) {
                        this.selectedCategoryId = v.node.data.categoryId;
                        var canvas = this.$el.getElementsByClassName('jsmind-inner')[0]
                        var view = v.node._data.view;
                        this.optBoxStyle.top = (view.abs_y - canvas.scrollTop) + "px";
                        this.optBoxStyle.left = (view.abs_x + view.width + 15 - canvas.scrollLeft) + "px";
                        this.optBoxStyle.show = true;
                    }
                }
            },
        }
    };
</script>
