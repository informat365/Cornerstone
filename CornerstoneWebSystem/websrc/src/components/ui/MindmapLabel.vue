<style>
    .mind-map-container {
        background-color: #fff;
        margin-top: 0 !important;
    }

    .mind-map-body {
        position: relative;
        min-height: 400px;
        height: 400px;
        /*width: 100%;*/
        overflow: auto;
        width: auto;
        background-color: #fff;
    }
</style>
<i18n>
    {
    "en": {
    "缩小":"zoom out",
    "放大":"zoom in"
    },
    "zh_CN": {
    "缩小":"缩小",
    "放大":"放大"
    }
    }
</i18n>
<template>
    <div class="mind-map-container" >
        <div style="text-align:right">
            <IconButton icon="ios-remove" @click="zoomOut" :title="$t('缩小')"></IconButton>
            <IconButton icon="ios-add" @click="zoomIn" :title="$t('放大')"></IconButton>
        </div>
        <div class="mind-map-body" ref="mind" :style="{height:bodyHeight}"></div>
    </div>
</template>
<script>
    import domtoimage from 'dom-to-image'

    export default {
        name: "MindmapLabel",
        props: ['value', 'placeholder'],
        data: function () {
            return {
                bodyHeight: '400px'
            }
        },
        watch: {
            value(val) {
                this.parse();
            }
        },
        mounted: function () {
            this.parse();
        },
        methods: {
            parse() {
                if (this.value) {
                    this.bodyHeight = '400px';
                    this.$nextTick(() => {
                        this.createEditor(JSON.parse(this.value))
                    })
                }
            },
            zoomIn() {
                this.mindmap.view.zoomIn()
            },
            zoomOut() {
                this.mindmap.view.zoomOut()
            },
            createEditor(data) {
                if (data == null) {
                    return;
                }
                if (this.mindmap) {
                    this.mindmap.show(data);
                    this.resizeEditor();
                    return;
                }
                var mindBox = this.$el.getElementsByClassName('mind-map-body')[0]
                var options = {
                    container: mindBox,
                    theme: 'orange',
                    editable: false
                };
                var jm = new jsMind(options);
                jm.show(data);
                this.mindmap = jm;
                jm.add_event_listener(this.onSelectNode);
                this.resizeEditor();
            },
            onSelectNode(t, v) {
                if (v.evt == "select_node") {
                    if (v.node.id.indexOf('task_') != -1) {
                        app.showDialog(TaskDialog, {
                            taskId: v.node.id.substring(5),
                            showTopBar: true
                        })
                    }
                    this.$emit("on-select-node", v.node.id);
                }
                if (v.evt == 'select_clear') {
                    this.$emit("on-select-node", null);
                }
            },
            resizeEditor() {
                this.mindmap.resize();
                var height = this.$el.getElementsByTagName('canvas')[0].height;
                this.bodyHeight = (height + 100) + "px";
            },
            capture(name) {
                var target=this.$el.getElementsByTagName('jmnodes')[0];
                var height = target.offsetHeight,width=target.offsetWidth;
                domtoimage.toPng(target, {
                    scale:2
                }).then(base64 => {
                    var canvas = document.createElement("canvas")
                    var ctx = canvas.getContext("2d");
                    canvas.width = width;
                    canvas.height=height;


                    var lineCanvas= this.$el.getElementsByTagName('canvas')[0];
                    var img0 = new Image();
                    img0.src = lineCanvas.toDataURL("image/png");
                    img0.onload=()=>{
                        ctx.fillStyle='#ffffff';
                        ctx.fillRect(0,0,width,height);
                        ctx.drawImage(img0,0,0,width,height);

                        var img1 = new Image();
                        img1.src = base64;
                        img1.onload=()=>{
                            ctx.drawImage(img1,0,0,width,height);
                            const a = document.createElement('a');
                            a.href = canvas.toDataURL("image/png");
                            a.setAttribute('download', name+'.png');
                            a.click();
                        };


                    }
                })
            },
        }
    }
</script>
