<style scoped>
    iframe {
        width: 100%;
    }
</style>
<template>
    <iframe @load="init" src="/minder-editor/index.html" frameborder="0"></iframe>
</template>
<script>
    let minder = null;
    let editor = null;
    export default {
        name: "MinderEditor",
        props: ['value'],
        data: function () {
            return {
                content: null
            };
        },
        mounted() {},
        watch: {
            value: function (val) {
                if (JSON.stringify(val) != JSON.stringify(this.content)) {
                    this.content = val;
                    this.importJson(val);
                }
            },
            content: function (val) {
                this.$emit('input', val);
            }
        },
        methods: {
            exportJson() {
                return minder && minder.exportJson();
            },
            importJson(data) {
                minder && minder.importJson(data);
            },
            init() {
                let _this = this;
                let editorFrame = event.currentTarget || event.target;
                setTimeout(()=>{
                    editor = editorFrame.contentWindow.editor;
                    minder = editorFrame.contentWindow.minder;
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
                    if (_this.content != null) {
                        mind = _this.content;
                    }
                    _this.importJson(mind);
                    minder.on('contentchange', function (e) {
                        _this.content = minder.exportJson();
                    });
                    minder.on('interactchange', function (e) {
                        _this.content = minder.exportJson();
                    });
                    _this.$emit('ready');
                },0);
            }
        }
    }
</script>