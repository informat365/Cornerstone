<style scoped>
.log-item{
    position: relative;
    margin-top:20px;
}
.log-item-right{
    display: inline-block;
    width:100%;
}
.log-item-name{
    font-size:12px;
    color:#333;
}
.log-item-time{
    font-size:12px;
    color:#999;
    padding-left:10px;
}
</style>

<template>
    <div>
        <template v-for="(item,idx) in changeLogList">
        <div class="log-item" v-if="idx<15"  :key="'log_'+item.id">
            <div class="log-item-right">
                <div>
                    <AvatarImage size="small" :name="item.createAccountName" :value="item.createAccountImageId" type="label"/>
                    <span class="log-item-time">{{item.createTime|fmtDeltaTime}}</span>
                </div>
                <ProjectChangeLogItem :item="item" style="margin-top:10px"></ProjectChangeLogItem>
            </div>
        </div>
         </template>
    </div>
</template>

<script>
export default {
  mixins: [componentMixin],
  props:['changeLogList'],
  data(){
    return {
      
    }
  },  
  methods:{
       showTaskInfo(uuid){
            app.showDialog(TaskDialog,{
                taskId:uuid,
                showTopBar:true
            })
        },
  }
}
</script>