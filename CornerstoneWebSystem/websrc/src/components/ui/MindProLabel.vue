<style scoped>
    .mind-map-container {
        display: flex;
        height: 60vh;
        flex-direction: column;
    }

    .mind-map-body {
        position: relative;
        width: 100%;
        flex: 1;
        border: none;
    }
</style>
<i18n>
{
    "en": {
        "缩小":"zoom out",
        "放大":"zoom in",
        "全屏":"fullscreen"
    },
    "zh_CN": {
        "缩小":"缩小",
        "放大":"放大",
        "全屏":"全屏"
    }
}
</i18n>
<template>
    <div class="mind-map-container">
        <div style="text-align:right">
            <IconButton icon="ios-expand" @click="fullscreen" :title="$t('全屏')"></IconButton>
            <!-- <IconButton icon="ios-remove" @click="zoomOut" :title="$t('缩小')"></IconButton>
            <IconButton icon="ios-add" @click="zoomIn" :title="$t('放大')"></IconButton> -->
        </div>
        <MinderViewer ref="viewer" :value="value" class="mind-map-body" :style="{height:bodyHeight}" @onSelectionChange="onSelectionChange"></MinderViewer>
    </div>
</template>
<script>
    export default {
        name: "MindProLabel",
        props: ['value'],
        data: function () {
            return {
                bodyHeight: '400px',
                isload: false
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
                }
            },
            zoomIn() {
                this.$refs.viewer.mindmap.execCommand("ZoomIn");
            },
            zoomOut() {
                this.$refs.viewer.mindmap.execCommand("ZoomOut");
            },
            fullscreen(){
                app.showDialog(MinderViewerDialog,{content:this.value});
            },
            onSelectionChange(v){
                if(v.minder._selectedNodes.length <= 0){
                    this.$emit("on-select-node",null);
                    return;
                }
                const id = v.minder._selectedNodes[0].data.id || "";
                if(id.indexOf('task_')!=-1){
                    app.showDialog(TaskDialog,{
                        taskId:id.substring(5),
                        showTopBar:true
                    })
                }
                this.$emit("on-select-node",id);
            },
            capture(name){
                console.log("pro capture---->");
                this.$refs.viewer.capture(name);
            }
        }
    }
</script>
