<style scoped>
    iframe {
        width: 100%;
    }
</style>
<template>
    <iframe @load="createEditor(value,true)"
            @resize="resizeEditor"
            src="/minder-editor/viewer-complate.html" frameborder="0"
            id="myframe" name="mindFrame"></iframe>
</template>
<script>
    export default {
        name: "MinderViewer",
        props: ['value'],
        data: function () {
            return {
                isload: false
            }
        },
        mounted() {

        },
        destroyed() {

        },
        watch: {
            value(val) {
                this.$nextTick(() => {
                    this.changeContent(this.value)
                })
            }
        },
        methods: {
            changeContent(data){
                if (data == null) {
                    return;
                }
                if (this.minder) {
                    data = JSON.parse(data);
                    this.resizeEditor();
                    this.minder.importJson(data);
                }
            },
            createEditor(data, isload) {
                if (isload !== true && this.isload !== true) return;
                if (data == null) {
                    return;
                }
                data = JSON.parse(data);
                let _this = this;
                let editorFrame = event.currentTarget || event.target;
                this.isload = true;
                setTimeout(()=>{
                    _this.editor = editorFrame.contentWindow.editor;
                    _this.minder = editorFrame.contentWindow.minder;
                    _this.angularApp = editorFrame.contentWindow.getApp();
                    _this.resizeEditor();
                    let editorSettings = editorFrame.contentWindow.ThirdSettings;
                    editorSettings.set('imageUpload', app.serverAddr + '/p/file/upload_file?token=' + app.token);
                    editorSettings.set('fileKey', 'file');
                    var mind = {
                        "root": {
                            "data": {
                                "created": new Date(),
                                "text": "想法？脑洞？"
                            },
                            "children": []
                        }
                    };
                    if (data != null) {
                        mind = data;
                    }
                    //_this.minder.disable();
                    _this.minder.importJson(mind);
                    _this.minder.on('selectionchange', (e) => {
                        // 延迟100毫秒防止触发拖拽事件
                        setTimeout(()=> { _this.$emit('onSelectionChange', e) }, 100);
                    });
                    _this.$emit('ready');
                },0);
                // data = JSON.parse(data);
                // if (this.mindmap) {
                //     this.mindmap.importJson(data);
                //     this.resizeEditor();
                //     return;
                // }
                // var mindBoxWindow = this.$el.contentWindow;

                // var km = new mindBoxWindow.ProMinderViewer(data);
                // km.execCommand('Background', '#ffffff');
                // km.execCommand('hand');
                // this.mindmap = km;
                // this.resizeEditor();
                // this.$emit('ready');
            },
            resizeEditor() {
                this.minder.enable();
                this.minder.execCommand("Zoom",100);
                this.minder.execCommand('camera', this.minder.getRoot(), 600);
                this.angularApp._$scope.$apply();
                this.$emit('resize');
                this.minder.disable();
            },
            getViewer(){
                return this.minder;
            },
            capture(name){
                document.getElementById('myframe').contentWindow.capture(name);
            }
        }
    }
</script>
