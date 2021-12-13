<style scoped>
    .task-wrap{
        z-index:500;
        position: fixed;
        top:0;
        left:0;
        width:100vw;
        height: 100vh;
        display: flex;
        background-color: #fff;
    }
     .task-popup{
        flex:1;
        overflow: auto;
        -webkit-overflow-scrolling:touch
    }
</style>
<template>
<div class="task-wrap">
    <div class="task-popup">
        <TaskPage @close="closePopup" ref="taskPage"></TaskPage>
    </div>
</div>

</template>

<script>
import { TransferDom,Popup} from 'vux'
export default {
    components: {Popup},
    directives: {TransferDom},
    props:['uuid'],
    data () {
        return {
           showPopup:true
        }
    },
    watch:{
        uuid(val){
           this.loadData();
        },
    },
    mounted(){
        this.loadData();
    },
    methods:{
        loadData(){
            this.showPopup=true;
            this.$refs.taskPage.args.id=this.uuid;
            this.$refs.taskPage.args.showBack=true;
            this.$refs.taskPage.pageLoad();
        },
        closePopup(){
            this.$emit('close')
        }
    }
}
</script>


