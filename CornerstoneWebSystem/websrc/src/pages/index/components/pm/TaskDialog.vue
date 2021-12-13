<style scoped>
    .dialog-title {
        font-size: 15px;
        font-weight: bold;
        color: #999;
    }

    .taskview-box {
        height: calc(100vh - 52px);
    }
</style>
<template>
    <Modal
        ref="dialog"
        v-model="showDialog"
        width="750"
        :closable="false"
        class="task-modal"
        :footer-hide="true"
        :mask-closable="false">
        <div class="taskview-box">
            <TaskView
                @close="onClose"
                v-if="taskId!=null"
                :task-id="taskId"
                :task-list="taskList"
                :show-top-bar="showTopBar"
                :show-type="'dialog'"></TaskView>
        </div>
    </Modal>
</template>


<script>
    export default {
        mixins: [componentMixin],
        data() {
            return {
                taskId: null,
                taskList: null,
                editingTaskId:null
            };
        },
        methods: {
            pageLoad() {
                this.taskId = this.args.taskId;
                this.taskList = this.args.taskList;
                this.showTopBar = this.args.showTopBar;
                if (this.taskId == null) {
                    this.showDialog = false;
                }
            },
            onClose(closeType={}) {
                if(this.editingTaskId==this.taskId){
                    app.toast('无法关闭，请先保存详情信息');
                    return;
                }
                this.showDialog = false;
                app.postMessage('task.hide',{
                    id:this.taskId
                });
                if (this.args.callback) {
                    this.args.callback(closeType);
                }
            },
            pageMessage(type,content){
                if(type==='task.dialog.close'&&content.taskId==this.taskId){
                    this.onClose();
                }
                if(type==='task.editing'){
                    this.editingTaskId = content.id;
                }
            }
        },
    };
</script>
