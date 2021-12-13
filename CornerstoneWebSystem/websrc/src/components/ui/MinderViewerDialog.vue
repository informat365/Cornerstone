<style scope>
.over-hide .ivu-modal-body {
    overflow: hidden;
}
</style>
<template>
    <Modal
        Modal ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false" :loading="false" title="高级思维导图"
        class="fullscreen-modal over-hide" width="100%" :footer-hide="true" @on-cancel="removeListener">

        <MinderViewer :value="content" style="height:calc(100vh - 51px)" @onSelectionChange="onSelectionChange"></MinderViewer>

    </Modal>
</template>


<script>
export default {
    mixins: [componentMixin],
    data () {
        return {
            content: null,
            title:''
        }
    },
    methods: {
        pageLoad(){
            this.content=this.args.content;
            this.addKeyListener();
        },
        preventDefault(e){
            e.preventDefault();
        },
        removeListener() {
            window.removeEventListener("mousewheel", this.preventDefault, this.passiveSupported() ? { passive: false } : false);
        },
        passiveSupported(){
            var passiveSupported = false;
            try {
                var options = Object.defineProperty({}, "passive", {
                    get: function() {
                        passiveSupported = true;
                    }
                });
                window.addEventListener("test", null, options);
            } catch(err) {}
            return passiveSupported;
        },
        addKeyListener() {
            window.addEventListener("mousewheel", this.preventDefault, this.passiveSupported() ? { passive: false } : false);
        },
        onSelectionChange(v){
            if(v.minder._selectedNodes.length <= 0) return;
            const id = v.minder._selectedNodes[0].data.id || "";
            if(id.indexOf('task_')!=-1){
                app.showDialog(TaskDialog,{
                    taskId:id.substring(5),
                    showTopBar:true
                })
            }
        },
    }
}
</script>