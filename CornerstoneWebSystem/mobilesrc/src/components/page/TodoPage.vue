<style scoped>
    .page{
       padding:0;
       background-color: #fff;
    }
  
    .table-count{
       background-color: #E8E8E8;
       color:#666;
       padding:3px 5px;
       border-radius: 3px;
       font-size:12px;
    }
    
</style>
<template>
  <div class="page">
      <TaskPopup @close="closeTaskPopup" v-if="currentTaskUUID" :uuid="currentTaskUUID"></TaskPopup>
      <div v-if="taskList">
             <div class="table-info">
                <div class="top-bar">
                    <div class="top-bar-left">
                      </div>
                    <div class="top-bar-center">
                         <span class="table-count">{{taskList.length}}条数据</span>
                     </div>
                     <div class="top-bar-right">
                     </div>
                </div>
               
            </div>

            <div style="padding:15px"></div>
            <div ref="mainBox" class="main-box">
                <div @click="showTask(item)" class="todo-box" v-for="item in taskList" :key="'t'+item.id">
                    <div class="todo-item">
                        <TaskObjectType :name="item.objectTypeName"></TaskObjectType>
                        <div class="serial-no">#{{item.serialNo}}</div>
                        <TaskStatus style="margin-left:10px" :label="item.statusName" :color="item.statusColor"></TaskStatus>
                        <div class="owners">
                            <template v-if="item.ownerAccountList">
                                    <template v-if="item.ownerAccountList.length==0">
                                            待认领
                                            </template>
                                            <template v-if="item.ownerAccountList.length==1">
                                            <AvatarImage  size="small" :name="item.ownerAccountList[0].name" :value="item.ownerAccountList[0].imageId" type="label"></AvatarImage>
                                        </template>
                                        
                                        <template v-if="item.ownerAccountList.length>1">
                                            <AvatarImage  v-for="acc in item.ownerAccountList" :key="item.id+'_acc'+acc.id" size="small" :name="acc.name" :value="acc.imageId" type="tips"></AvatarImage>
                                        </template>
                                </template>
                            <template v-if="item.ownerAccountList==null">
                                待认领
                            </template>
                        </div>
                        
                    </div>
                    <div class="todo-item" style="margin-top:5px">
                        <div class="task-name">{{item.name}}</div>
                    </div>
                </div>
            </div>
      </div>
  </div>
</template>

<script>
export default {
    mixins:[componentMixin],
    data () {
        return {
            taskList:null,
            currentTaskUUID:null,
        }
    },
    methods:{
        pageLoad(){
            this.loadData();
        },
        loadData(){
            app.invoke("BizAction.getTodoTaskInfoList",[app.token],list => {
                this.taskList=list;
            });
        },
        closeTaskPopup(){
            this.currentTaskUUID=null
        },
        showTask(item){
            this.currentTaskUUID=item.uuid;
        }
    }
}
</script>


