<style scoped>
.page{
     padding:0;
    background-color: #fff;
    min-height: 100vh;
}
.section{
    padding:15px;
}
.section-border{
    border-top:1px solid #efefef;
    padding:15px;
}
.section-color{
    background-color: #F8F8F9;
}
.info-box{
    background-color: #F8F8F9;
    padding:15px;    
}
.info-row{
    font-size:13px;
    display:flex;
    margin-top:10px;
    align-items:flex-start;
}
.info-row:first-child{
    margin-top:0;
}
.info-key{
    width:80px;
    color:#94929D;
    text-overflow:ellipsis;
    overflow: hidden;
    white-space: nowrap;
    word-break: break-all;
}
.info-value{
    flex:1;
}
.title{
    text-align: center;
    padding:7px 0;
    border-bottom:1px solid #eee;
}
.owner-row{
    display: flex;
    margin-bottom:5px;
    align-items: flex-start;
}
.owner-row-name{
    width:50px;
}
.owner-row-detail{
    flex:1;
}
.owner-row-status{
    width:80px;
    border-radius: 3px;
    padding:1px 8px;
    font-weight: normal;
    font-size:12px;
}
.owner-row-status-1{
    color:#f29c2b;
    border:2px solid #f29c2b;
}
.owner-row-status-2{
    color:#8bc24c;
    border:2px solid #8bc24c;
}
.owner-row-status-3{
    color:#8bc24c;
    border:2px solid #8bc24c;
}

.owner-row-detail-time{
    font-size:12px;
    color:#666;
    font-weight: normal;
    margin-left:5px;
}
</style>
<template>
<div class="page" v-show="instance.id>0">  
    <div class="title">
        <span class="numberfont">#{{instance.serialNo}}</span>
        <span>{{instance.title}}</span>
    </div>

    <WorkflowForm ref="form" v-if="form.id" 
            :form="form" 
            :value="formFieldValue" 
            :formFieldList="permission.formFieldList"></WorkflowForm>

    <div class="info-box">
        <div class="info-row">
            <div class="info-key">类型</div>
            <div class="info-value">{{instance.workflowDefineName}}</div>
        </div>
        <div class="info-row">
            <div class="info-key">提交人</div>
            <div class="info-value">{{instance.createAccountName}}</div>
        </div>
        <div class="info-row">
            <div class="info-key">提交时间</div>
            <div class="info-value">{{instance.createTime|fmtDateTime}}</div>
        </div>
        <div v-if="instance.finishTime" class="info-row">
            <div class="info-key">完成时间</div>
            <div class="info-value">{{instance.finishTime|fmtDateTime}}</div>
        </div>
        <div class="info-row">
            <div class="info-key">当前步骤</div>
            <div class="info-value">{{instance.currNodeName}}</div>
        </div>
        <div class="info-row" v-if="ccList.length>0">
            <div class="info-key">抄送</div>
            <div class="info-value">
                 <span style="margin-right:3px" v-for="acc in ccList" :key="acc.ownerAccountId">
                     {{acc.ownerAccountName}}
                 </span>
            </div>
        </div>
         <div class="info-row" v-if="ownerList&&ownerList.length>0">
            <div class="info-key">负责人</div>
            <div class="info-value">
                    <template v-for="item in ownerList">
                                <div class="owner-row" :key="'a'+item.id">
                                    <div class="owner-row-name text-no-wrap">
                                       {{item.ownerAccountName}}
                                    </div>
                                    <div class="owner-row-detail">
                                        <div>
                                            <span v-if="item.status==1" class="owner-row-status owner-row-status-1">未处理</span>
                                            <span v-if="item.status==2" class="owner-row-status owner-row-status-2">
                                               <template v-if="item.submitButtonText">{{item.submitButtonText}}</template>
                                               <template v-else>已提交</template>
                                            </span>
                                            <span v-if="item.status==3" class="owner-row-status owner-row-status-3">
                                                他人<template v-if="item.submitButtonText">{{item.submitButtonText}}</template>
                                                <template v-else>已提交</template>
                                            </span>
                                        </div>
                                    </div>
                                </div>
                                <div v-if="item.remark" class="owner-log-detail-remark" :key="'r'+item.id">
                                    备注：{{item.remark}}
                                </div>
                                <div class="owner-log-detail-box" :key="'b'+item.id" >
                                    <WorkflowFormValue v-if="item.formData" 
                                    :formData="item.formData" 
                                    :formFieldList="permission.formFieldList"></WorkflowFormValue>
                                 </div>            
                    </template>               
            </div>
        </div>
    </div>

  </div>
</template>

<script>
import Vue from 'vue'
import { Flexbox,TransferDom, DatetimePlugin,PopupHeader,XTextarea,FlexboxItem,XHeader,Actionsheet,Group,Popup} from 'vux'
Vue.use(DatetimePlugin)
export default {
    components: {Flexbox,XTextarea,PopupHeader,FlexboxItem,XHeader,Actionsheet,Group,Popup},
    directives: {TransferDom},
    mixins:[componentMixin],
    data () {
        return {
            workflow:{},
            form:{},
            define:null,
            chart:null,
            instance:{},
            formFieldValue:{},
            graphItemProp:{},
            permission:{},
            commentContent:null,
            showCommentBox:false,
            backwordNodeList:[],
            nodeChangeList:[],
            ccList:[],
            allNodeLogList:[],
            changeList:[],
            ownerList:[],
        }
    },
    methods:{
        pageLoad(){
            this.loadData();
        },
        clickBackBtn(){
            this.$emit('close')
        },
        loadData(){
            app.invoke("WorkflowAction.getWorkflowInstanceInfo",[app.token,this.args.id,false],(info)=>{
                this.form=info.form;
                this.define=info.define;
                this.permission=info.permission;
                this.instance=info.instance;
                this.nodeChangeList=info.nodeChangeList;
                this.ownerList=info.ownerList;
                this.ccList=info.ccList;
                //
                this.setupValue();
                this.loadChangeLogList();
            })
            //
        },
        setupValue(){
            if(this.instance.formData){
                this.formFieldValue=JSON.parse(this.instance.formData);
            }
            //设置permission.formFieldList的字段类型 columnList
            var formFieldList=JSON.parse(this.form.fieldList);
            var allFormField={};
            formFieldList.forEach(f=>{
                allFormField[f.id]=f;
            })
            this.permission.formFieldList.forEach(f=>{
                var t=allFormField[f.id];
                if(t){
                    f.type=t.type;
                    f.columnList=t.columnList;
                    f.systemValueType=t.systemValueType;
                }
            })
            //设置系统字段
            this.permission.formFieldList.forEach(f=>{
                if(f.type=='system-value'){
                    if(f.systemValueType=='submitAccount'){
                        this.formFieldValue[f.id]=this.instance.createAccountName;
                    }
                    if(f.systemValueType=='systemTime'){
                        this.formFieldValue[f.id]=window.formatDate(new Date());
                    }
                    if(f.systemValueType=='serialNO'){
                        this.formFieldValue[f.id]=this.instance.serialNo;
                    }
                }
            })
        },
        loadChangeLogList(){
            var query={
                pageIndex:1,
                pageSize:200,
                workflowInstanceId:this.instance.id
            }
            app.invoke('WorkflowAction.getWorkflowInstanceChangeLogList',[app.token,query],(info)=>{
                info.list.forEach(item=>{
                   if(item.type==5||item.type==6){
                        try{
                           item.detail=JSON.parse(item.detail)
                        }catch(e){console.log(e)}
                   }
                })
                this.changeList=info.list;
            });
        }
    }
}
</script>


