<style scoped>
.content-box{
    padding:40px;
    width:100%;
    height: 100vh;
    overflow: auto;
    background-color: #F5F5F5;
    display: flex;
    align-items: center;
    flex-direction: column;
}
.discuss-box{
    border-radius: 5px;
    background-color: #fff;
    border:1px solid #eee;
    width:800px;
    margin-bottom:50px;
    position: relative;
    padding-bottom:30px;
}
.discuss-title{
    font-weight: bold;
    color:#444;
    width:800px;
    font-size:16px;
    padding:7px;
    text-align: center;
}
.discuss-desc{
    color:#999;
    font-size:12px;
    padding:5px;
    width:800px;
    text-align: center;
}
.discuss-title-desc{
    font-weight: normal;
    font-size:12px;
}
.discuss-item{
    padding:15px 25px;
    position: relative;
    padding-bottom: 0px;
}
.discuss-user-item{
    padding:15px 25px;
    position: relative;
}

.discuss-item-user{
    color:#777;
    font-size:13px;
    margin-left:5px;
}
.discuss-item-time{
    color:#999;
    margin-left:5px;
}
.commit-box{
    background-color: #F3F3F3;
    border-radius: 3px;
    font-size:14px;
    padding:5px 15px;
    cursor: pointer;
    color:#999;
}
.footer-outter{
    position:fixed;
    z-index:50;
    bottom:0;
    left:0;
    width:100%;
    display: flex;
    align-items: center;
    flex-direction: column;
}
.footer-box{
    height: 50px;
    padding:10px;
    padding-left:30px;
    padding-right:30px;
    box-shadow: rgba(0, 0, 0, 0.15) 0px 0px 8px 0px  !important;
    transition: height 0.3s;
    background-color: #fff;
    width:800px;
}
.footer-box-open{
    height: 350px;
    border-top: 1px solid #ccc;
}
.attach-item{
        background-color: #EEEEEE;
        color:#555;
        font-size:12px;
        padding:2px 5px;
        text-align: center;
        display: inline-block;
        border-radius: 3px;
        margin-right: 5px;
        font-weight: bold;
        cursor: pointer;
}
.discuss-attach{
    margin-top:10px;
}

   .table-count{
       background-color: #E8E8E8;
       color:#666;
       padding:3px 5px;
       border-radius: 3px;
   }
.selected-btn{
    color:#0097F7 !important;
}


.online-box{
    position: fixed;
    top:60px;
    left:30px;
    width:50px;
    text-align: center;
}
.online-box-title{
    font-weight: bold;
    color:#999
}
.discuss-content{
    padding:10px;
    font-size:13px;
    background-color: #eee;
    color:#333;
    border-radius: 6px;
    margin:20px 55px;
}
</style>
<i18n>
{
    "en": {
        "参与": "Members",
        "发起":" Created",
        "人参与":"{0} member(s)",
        "条讨论":"{0} items",
        "时间线":"Timeline",
        "人员":"Member",
        "设置":"Edit",
        "上传附件":"Upload",
        "取消":"Cancel",
        "提交":"Submit",
        "发表讨论，ctrl+enter提交":"ctrl+enter submit"
    },
    "zh_CN": {
        "参与": "参与",
        "发起":"发起",
        "人参与":"{0}人参与",
        "条讨论":"{0}条讨论",
        "时间线":"时间线",
        "人员":"人员",
        "设置":"设置",
        "上传附件":"上传附件",
        "取消":"取消",
        "提交":"提交",
        "发表讨论，ctrl+enter提交":"发表讨论，ctrl+enter提交"

    }
}
</i18n>
<template>
    <Modal
         ref="dialog" class="nopadding-modal fullscreen-title-nofooter-modal " fullscreen v-model="showDialog" :closable="true"
         :mask-closable="false" 
         :loading="false" 
         :footer-hide="true"
         width="800">
        <div class="content-box scrollbox" v-if="discuss.id>0">

            <div class="online-box">
                <div class="online-box-title">{{$t('参与')}}</div>
                <div>
                    <AvatarImage v-for="item in discuss.memberInfos" 
                        :key="'di'+item.id" :name="item.name" :value="item.imageId" type="tips"></AvatarImage>
                </div>
            </div>

            <div class="discuss-title">
                {{discuss.name}} 
            </div>
            <div class="discuss-desc">
                {{discuss.createAccountName}} {{discuss.createTime|fmtDeltaTime}}{{$t('发起')}}
                <template v-if="discuss.projectName">· {{discuss.projectName}}</template>
            </div>
            <div class="discuss-box">
                <Row>
                    <Col span="6">&nbsp;</Col>
                    <Col span="12" style="text-align:center">
                        <span class="table-count">{{$t('人参与',[discuss.memberInfos.length])}}，{{$t('条讨论',[messageList.length])}}</span>
                    </Col>
                    <Col span="6" style="text-align:right">
                        <IconButton :class="{'selected-btn':viewType=='time'}" @click="viewType='time'" :title="$t('时间线')"></IconButton>
                        <IconButton :class="{'selected-btn':viewType=='user'}" @click="viewType='user'" :title="$t('人员')"></IconButton>
                        <IconButton v-if="isOwner" @click="showEditDialog" icon="ios-settings-outline" :title="$t('设置')" ></IconButton>
                    </Col>
                </Row>

                <div class="discuss-content">
                    {{discuss.content}}
                </div>

                <div v-if="viewType=='time'">
                    <div v-for="item in timeMessageList" :key="'m'+item.id" class="discuss-item">
                        <div>
                            <AvatarImage :name="item.createAccountName" :value="item.createAccountImageId" type="tips"></AvatarImage>
                            <span class="discuss-item-user">{{item.createAccountName}}</span>
                            <span class="discuss-item-time">{{item.createTime|fmtDeltaTime}}</span>
                        </div>
                        <DiscussItemContent :value=item></DiscussItemContent>
                    </div>
                </div>


                <div v-if="viewType=='user'">
                    <div v-for="item in userMessageList" :key="'acc'+item.accountId" class="discuss-user-item">
                        <div>
                            <AvatarImage :name="item.accountName" :value="item.accountImageId" type="tips"></AvatarImage>
                            <span class="discuss-item-user">{{item.accountName}}</span>
                        </div>
                        
                        <div class="discuss-item" style="padding-left:0" :key="t.id" v-for="t in item.list">
                            <div class="discuss-item-time" style="padding-left:30px">{{t.createTime|fmtDeltaTime}}</div>
                            <DiscussItemContent :value="t"></DiscussItemContent>
                        </div>
                
                    </div>
                </div>


            </div>
            
            <div class="footer-outter">
             <div class="footer-box" :class="{'footer-box-open':commitBoxOpen}">
                  <Row>
                    <Col :style="{paddingTop:commitBoxOpen?'20px':'0px'}" span="2"> 
                        <AvatarImage :name="account.name" :value="account.imageId" type="tips"/> 
                    </Col>
                    <Col span="22">
                        <div  class="fixed-height-editor" v-show="commitBoxOpen" 
                            style=";padding-bottom:10px;padding-top:20px">
                            <RichtextEditor :mention="mentionList"  v-model="replyContent" ref="commentEditor" ></RichtextEditor>
                            <Row>
                                <Col span="12">
                                     <div style="margin-top:10px;width:100%">                  

                                    <Poptip  placement="top">
                                        <IconButton size="12" icon="md-attach"  :title="$t('上传附件')" ></IconButton>
                                        <FileUploadView style="padding:10px;width:450px" multiple @change="uploadSuccess" slot="content"></FileUploadView>
                                    </Poptip>
                                        <span v-for="file in replyAttachmentList" :key="file.uuid" 
                                        class="attach-item"><Icon type="md-attach" />{{file.name}}</span>
                                     </div>
                                </Col>
                                <Col span="12">
                                    <div style="margin-top:10px;width:100%;text-align:right">
                                        <Button type="text"  @click="commitBoxOpen=false">{{$t('取消')}}</Button>
                                        <Button type="default"  @click="replyComment">{{$t('提交')}}</Button>
                                    </div>        
                                </Col>    
                            </Row>
                            
                        </div>
                        <div v-show="!commitBoxOpen" @click="commitBoxOpen=true" class="commit-box">
                            {{$t('发表讨论，ctrl+enter提交')}}
                        </div>
                    </Col>
                </Row>
            </div>
            </div>

             <BackTop></BackTop>
        </div>
   
    </Modal>
</template>
<script>
    export default {
        mixins: [componentMixin],
        data () {
            return {
                discuss:{},
                account:{},
                replyContent:null,
                commitBoxOpen:false,
                viewType:'time',
                messageList:[],
                replyAttachmentList:[],
                isEditing:false,
                mentionList:[]
            }
        },
        computed:{
            timeMessageList(){
               return this.computeMessageList();
            },
            userMessageList(){
                var list=this.computeMessageList();
                var accountMap={};
                var accountList=[];
                list.forEach(item=>{
                    if(accountMap[item.createAccountId+'']==null){
                        accountMap[item.createAccountId+'']={
                            accountId:item.createAccountId,
                            accountName:item.createAccountName,
                            accountImageId:item.createAccountImageId,
                            list:[]
                        }
                        accountList.push(accountMap[item.createAccountId+''])
                    }
                    //
                    accountMap[item.createAccountId+''].list.push(item);
                })
                return accountList;
            },
            isOwner(){
                return this.discuss.createAccountId==app.account.id
            }
        },
        beforeDestroy(){
            if(this.checkInterval){
                clearInterval(this.checkInterval);
            }
        },
        methods:{
            pageLoad(){
                this.account=app.account;
                this.loadData();
                this.checkInterval=setInterval(this.checkUpdate,30000);
            },
            computeMessageList(){
                var list=[];
                var allListMap={};
                this.messageList.forEach(item=>{
                    if(item.replyMessageId==0){
                        list.push(item);
                        item.replyMessageList=[];
                        allListMap[item.id+""]=item;
                    }
                })
                //
                this.messageList.forEach(item=>{
                    if(item.replyMessageId>0){
                        var t=allListMap[item.replyMessageId];
                        if(t){
                            t.replyMessageList.push(item);
                        }
                    }
                })
                list.forEach(item=>{
                    item.replyMessageList=item.replyMessageList.reverse();
                })
                return list;
            },
            checkUpdate(){
                if(this.isEditing){
                    return;
                }
                app.invoke('BizAction.getDiscussUpdateTimeById',[app.token,this.args.id],(time)=>{
                    if(time!=this.discuss.updateTime){
                        this.loadData();
                    }
                });
            },  
            pageMessage(type,t){
                if(type=='discuss.message.update'){
                    this.loadMessageList();
                }
                if(type=='discuss.message.edit'){
                    this.isEditing=t;
                }
                if(type=='discuss.edit'){
                    this.loadData();
                }
            },
            loadData(){
                app.invoke('BizAction.getDiscussById',[app.token,this.args.id],(info)=>{
                    this.discuss=info;
                    for(var i=0;i<info.memberInfos.length;i++){
                        var mem=info.memberInfos[i];
                        this.mentionList.push({
                            id:mem.accountId,
                            name:mem.name
                        })
                    }
                    this.loadMessageList();
                });
            },
            loadMessageList(){
                var query={
                    discussId:this.discuss.id,
                    pageIndex:1,
                    pageSize:1000
                }
                app.invoke('BizAction.getDiscussMessageList',[app.token,query],(info)=>{
                    this.messageList=info.list;
                });
            },
            replyComment(){
                var bean={
                    message:this.$refs.commentEditor.getValue(),
                    discussId:this.discuss.id,
                    attachments:this.replyAttachmentList
                }
                app.invoke('BizAction.addDiscussMessage',[app.token,bean],(info)=>{
                    this.replyAttachmentList=[];
                    this.commitBoxOpen=false;
                    this.$refs.commentEditor.setValue('')
                    this.loadData();
                });
            },
            uploadSuccess(file){
                this.replyAttachmentList.push(file)
            },
            showEditDialog(){
                app.showDialog(DiscussEditDialog,{
                    id:this.discuss.id
                })
            }
        }
    }
</script>