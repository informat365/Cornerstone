<style scoped>
    .page{
       padding:0;
       background-color: #fff;
    }
    
    .iconbtn{
        fill:#888888;
    }
    .table-count{
       background-color: #E8E8E8;
       color:#666;
       padding:3px 5px;
       border-radius: 3px;
       font-size:12px;
       margin-left:5px;
       margin-right:5px;
    }
   
    .addbtn{
        fill:#0097F7;
    }
</style>
<template>
  <div class="page">
    <div v-transfer-dom>
      <popup v-model="searchPopupShow" height="100%">
        <popup-header  right-text="确定" title="过滤条件" @on-click-right="confirmFilter"></popup-header>
        <div >
          <group>
               <group-title slot="title">责任人</group-title>
                <checklist  :options="queryInfo.memberList" v-model="queryItem.ownerAccountIdList" ></checklist>
        </group>
         <group  v-if="hasField('statusName')">
              <group-title slot="title">状态</group-title>
                <checklist   :options="queryInfo.statusList" v-model="queryItem.statusInList" ></checklist>
         </group>
          <group v-if="hasField('priorityName')">
               <group-title slot="title">优先级</group-title>
                <checklist  :options="queryInfo.priorityList" v-model="queryItem.priorityInList" ></checklist>
          </group>
        </div>
      </popup>
    </div>

     <TaskPopup @close="currentTaskUUID=null" v-if="currentTaskUUID" :uuid="currentTaskUUID"></TaskPopup>

      <div v-if="taskList">
            <div class="table-info">
                <div class="top-bar">
                    <div class="top-bar-left">
                          <x-icon @click.native="showFilter" class="addbtn" type="ios-search" size="25"></x-icon>
                     </div>
                    <div class="top-bar-center">
                         <x-icon @click.native="changePage(-1)" class="iconbtn" type="ios-arrow-back" size="20"></x-icon>
                        <span class="table-count">{{queryItem.pageIndex}}/{{pageQuery.totalPage}}，{{taskList.length}}条数据</span>
                         <x-icon @click.native="changePage(1)" class="iconbtn" type="ios-arrow-forward" size="20"></x-icon>
                    </div>
                     <div class="top-bar-right">
                         <x-icon @click.native="addTask" class="addbtn" type="ios-plus" size="25"></x-icon>
                     </div>
                </div>
               
            </div>

            <div style="padding:15px"></div>
            <div class="main-box">
            <div @click="showTask(item)" class="todo-box" v-for="item in taskList" :key="'t'+item.id">
                <div class="todo-item">
                    <TaskObjectType :name="item.objectTypeName"></TaskObjectType>
                    <div class="serial-no">#{{item.serialNo}}</div>
                    <TaskStatus v-if="hasField('statusName')" style="margin-left:10px" :label="item.statusName" :color="item.statusColor"></TaskStatus>
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

import { Flexbox,TransferDom, PopupHeader,Group,GroupTitle,Cell,Popup,Checklist} from 'vux'
export default {
    components: {Flexbox,PopupHeader,Group,GroupTitle,Cell,Popup,Checklist},
    directives: {TransferDom},
    mixins:[componentMixin],
    data () {
        return {
            taskList:null,
            currentTaskUUID:null,
            queryInfo:{
                statusList:[],
                priorityList:[],
                memberList:[],
            },
            queryItem:{
                pageIndex:1,
                pageSize:200,
                projectId:null,
                objectType:null,
                iterationId:null,
                statusInList:[],
                ownerAccountIdList:[],
                priorityInList:[],
            },
            pageQuery:{
                total:0,
                totalPage:1
            },
            searchPopupShow:false,
        }
    },
    methods:{
        pageLoad(){
            this.loadQueryInfo();
        },
        loadData(){
            app.invoke("BizAction.getTaskInfoList",[app.token,this.queryItem],info => {
                this.taskList=info.list;
                this.pageQuery.total=info.count;
                if(info.count==0){
                    this.pageQuery.totalPage=1;
                }else{
                    this.pageQuery.totalPage=parseInt(info.count/this.queryItem.pageSize)+1;
                }
            });
        },
        loadQueryInfo(){
            this.queryItem.objectType=this.args.type;
            this.queryItem.iterationId=this.args.iterationId;
            app.invoke("BizAction.getTaskQueryInfo",[app.token,this.args.projectId,this.args.type],info => {
                this.queryInfo=info;
                this.queryItem.projectId=info.project.id;
                this.queryInfo.priorityList.map(item=>{
                    item.key=item.id;
                    item.value=item.name;
                })
                this.queryInfo.memberList.map(item=>{
                    item.key=item.accountId;
                    item.value=item.accountName;
                })
                this.queryInfo.statusList.map(item=>{
                    item.key=item.id;
                    item.value=item.name;
                })
                this.loadData();
            });
        },
        hasField(name){
            if(this.queryInfo.fieldList){
                for(var i=0;i<this.queryInfo.fieldList.length;i++){
                    var f=this.queryInfo.fieldList[i];
                    if(f.field==name){
                        return true;
                    }
                }
            }
            return false;
        },
        changePage(delta){
            var t=this.queryItem.pageIndex+delta;
                if(t<=0||t>this.pageQuery.totalPage){
                return;
            }
            this.queryItem.pageIndex=t;
            this.loadData();
        },
        showTask(item){
            this.currentTaskUUID=item.uuid;
            //app.loadPage('/m/task?id='+item.uuid)
        },
        addTask(){
            app.loadPage('/m/task_add?projectId='+this.queryItem.projectId+"&type="+this.queryItem.objectType)
        },
        showFilter(){
            this.searchPopupShow=true;
        },
        confirmFilter(){
            this.searchPopupShow=false;
            this.loadData();
        }
    }
}
</script>


