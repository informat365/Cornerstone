<style scoped>
.popup{
    position: fixed;
    z-index:3;
    width:700px;
    top:49px;
    right:0;
    height: calc(100vh - 48px);
    max-height: calc(100vh - 48px);
    overflow: hidden;
    background-color: #fff;
    box-shadow:0px 2px 10px 0px rgba(225,225,225,0.5);
    border-left:1px solid rgba(216,216,216,1);
}

.taskview-box{
    height: calc(100vh - 48px);
    max-height: calc(100vh - 48px);
}
</style>
<template>
    <div class="popup" >
        <div class="taskview-box">
            <TaskView @close="closePopup" :task-id="taskId"></TaskView>
        </div>
    </div>
</template>
<script>
    export default {
        props:['taskId'],
        data () {
            return {
                
            }
        },
        mounted(){       
          
        },
        methods:{
            closePopup(){
                this.$emit('popup-close');
            }
        }
    }
</script>