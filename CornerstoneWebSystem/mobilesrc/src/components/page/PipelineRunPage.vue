<style scoped>
    .page{
       padding:0;
    }
    .topbar{
        padding:15px;
        background-color: #fff;
        border-bottom: 1px solid #eee;
    }
    .info-bar{
        font-size:13px;
        color:#666;
        display: flex;
        align-items: center;
        margin-top:5px;
    }
    .info-left{
        flex:1;
    }
    .info-right{
        width:90px;
    }
.run-box{
    font-size:10px;
    font-family: 'Courier New', Courier, monospace;
    white-space: pre;
    margin-top:5px;
    margin-bottom:5px;
    color:#fff;
  
    font-weight: 500;
}
.run-box-type-1{
    color:#fff;
     font-weight: bold;
}
.run-box-type-2{
    color:red;
     font-weight: bold;
}
.run-box-type-3{
    color:#98F568;
    font-weight: bold;
}

.run-log-box{
    height: calc(100vh - 115px);
    border:1px solid #ccc;
    overflow: auto;
    padding:10px;
    background-color: #000;
    -webkit-overflow-scrolling : touch;
}
.run-log-stage{
    color:#fff;
    display: inline-block;
    margin-right:5px;
}
.run-log-time{
    color:#fff;
    margin-left:5px;
}
</style>
<template>
  <div class="page">  
        <div class="topbar">
            <div>{{formItem.name}}</div>
            <div class="info-bar">
                 <div  class="info-left" v-if="formItem.runLog!=null"> 
                     <template v-if="formItem.runLog.id>0">
                        <span style="margin-right:10px">
                                    {{formItem.runLog.createTime|fmtDeltaTime}} 
                                    {{formItem.runLog.createAccountName}} </span>
                        <DataDictLabel style="margin-right:10px" type="ProjectPipelineRunLog.status" :value="formItem.runLog.status"></DataDictLabel>
                     </template>
                      <template v-if="formItem.runLog.id==0">
                          无执行记录
                      </template>
                   </div>
                  <div  class="info-right" >
                        <XButton mini v-if="formItem.runLog.status==1||formItem.runLog.status==2" @click.native="stopRun" type="warn">停止执行</XButton>
                        <XButton mini v-if="(formItem.runLog.status==0||formItem.runLog.status==3||formItem.runLog.status==4)" @click.native="startRun" type="primary">开始执行</XButton>
                </div>
            </div>
        </div>

        <div  class="run-log-box">
             <div class="run-log-item" v-for="log in logList" :key="'log_'+log.id">
                   <div class="run-box" :class="'run-box-type-'+log.type"><div class="run-log-stage">[{{log.step}}<span class="run-log-time">{{log.createTime|fmtDeltaTime}}]</span></div>{{log.message}}</div>
              </div>  
              <div ref="loadingBox">&nbsp;</div> 
        </div>   
  </div>
</template>

<script>
import { Flexbox,XButton, FlexboxItem,Group,GroupTitle,Cell,CellBox} from 'vux'
export default {
    components: {Flexbox,XButton,FlexboxItem,Group,GroupTitle,Cell,CellBox},
    mixins:[componentMixin],
    data () {
        return {
            lastLogId:0,
            formItem:{
                runLog:{}
            },
            logList:[],
            lastStatus:0,
            historyList:[],
        }
    },
    beforeDestroy(){
        clearInterval(this.refreshInterval);
    },
    methods:{
        pageLoad(){
            this.loadData();
            this.startRefresh();
        },
        loadData(){
                app.invoke('BizAction.getProjectPipelineInfoById',[app.token,this.args.id],(info)=>{
                    this.formItem=info;
                    if(info.runId>0){
                        this.loadLog();
                    }
                    if(this.lastStatus!=0&&this.lastStatus!=this.formItem.runLog.status){
                        app.postMessage('pipeline.runstatus');
                        if(this.formItem.runLog.status==3){
                            app.toast('执行成功');
                        }
                        if(this.formItem.runLog.status==4){
                            app.toast('执行失败');
                        }
                    }
                    this.lastStatus=this.formItem.runLog.status;
                    if(this.formItem.runLog.status==1){
                        //TODO
                    }
                })
        },
        loadLog(){
                app.invoke('BizAction.getProjectPipelineRunDetailLogList',[app.token,
                    this.formItem.runId,this.lastLogId],(list)=>{
                    if(list.length>0&&this.logList.length>0){
                        if(list[0].id==this.logList[this.logList.length-1].id){
                            this.logList.splice(this.logList.length-1,1);
                        }
                    }
                    for(var i=0;i<list.length;i++){
                        this.logList.push(list[i])
                    }
                    if(list.length>0){
                        this.lastLogId=list[list.length-1].id;
                    }
                    this.$nextTick(()=>{
                        if(this.$refs.loadingBox.$el){
                            this.$refs.loadingBox.$el.scrollIntoView();
                        }
                    })
                })
        },
        startRefresh(){
            this.refreshInterval=setInterval(()=>{
                if(this.formItem.runLog!=null){
                    this.loadData();
                }
            },5000)
        },
        startRun(){
            this.logList=[];
            app.invoke('BizAction.runProjectPipeline',[app.token,this.args.id],(info)=>{
                this.loadData();
                app.postMessage('pipeline.edit')
            })
        },
        stopRun(){
            app.invoke('BizAction.stopProjectPipeline',[app.token,this.args.id],(info)=>{
                this.loadData();
                app.postMessage('pipeline.edit')
            })
        },    
    }
}
</script>


