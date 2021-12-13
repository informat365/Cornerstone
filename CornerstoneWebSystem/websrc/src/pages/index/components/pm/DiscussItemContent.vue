<style scoped>
.discuss-content-item{
    padding:8px 40px;
    position: relative;
    padding-left:0;
}

.discuss-opt{
    position:absolute;
    right:10px;
    top:10px;
    visibility: hidden;
    z-index:10;
    background-color: #fff;
    border-radius: 3px;
}
.discuss-content-item:hover .discuss-opt{
    visibility:visible;
}
.discuss-reply-item-opt{
    visibility: hidden;
}
.discuss-content-item:hover .discuss-reply-item-opt{
    visibility:visible;
}

.discuss-content{
    padding-left:35px;
    font-size:14px;
    color:#111;
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
.discuss-reply{
     background-color: #F3F3F3;
     padding:7px;
     margin-top:7px;
     border-radius: 5px;
}
.discuss-reply input{
    outline: none;
    border-radius: 5px;
    width:100%;
    padding:3px;
}
.discuss-reply-box{
    border-left:5px solid #EEEEEE;
    padding-left:10px;
    margin-left:35px;
    font-size:14px;
    margin-top:8px;
}
.discuss-reply-item{
    display: flex;
    align-items: flex-start;
    margin-bottom: 10px;
    font-size:13px;
    line-height: 1.7;
}
.discuss-reply-item-info{
    color:#999;
    margin-left:5px;
    font-size:12px;
    display: inline-flex;
    align-items:center;
}
.reply-line{
    padding-left:10px;
    display:flex;
    align-items: center;
}
</style>
<i18n>
{
    "en": {
        "删除": "Delete",
        "回复": "Reply"
    },
    "zh_CN": {
        "删除": "删除",
        "回复": "回复"
    }
}
</i18n>
<template>
  <div class="discuss-content-item">
    <div class="discuss-content">
        <RichtextLabel :value="value.message"></RichtextLabel>
        <div v-if="value.attachments&&value.attachments.length>0" class="discuss-attach">
            <span @click="preview(att)" v-for="att in value.attachments" :key="'att'+att.uuid" class="attach-item">
                <Icon type="md-attach"/>{{att.name}}
            </span>
        </div>
    </div>
    <div class="discuss-reply-box">
      
      <div v-for="sub in value.replyMessageList" :key="'sb'+sub.id" class="discuss-reply-item">
        <AvatarImage size="small" :name="sub.createAccountName" :value="sub.createAccountImageId" type="tips"></AvatarImage>  
        <div class="reply-line">
            {{sub.message}}
            <span class="discuss-reply-item-info"> 
                    {{sub.createTime|fmtDeltaTime}}
                    <IconButton @click="deleteMessage(sub.id)" v-if="account.id==sub.createAccountId" class="discuss-reply-item-opt" icon="ios-trash" size="13" :tips="$t('删除')"/>
            </span>
        </div>
      </div>

      <div v-if="reply" class="discuss-reply">
        <input v-model.trim="replyContent" @keyup.enter="replyMessage" type="text">
      </div>
    </div>

    <div class="discuss-opt"> 
      <IconButton size="16" @click="reply=!reply" icon="ios-chatbubbles" :tips="$t('回复')"/>
      <IconButton @click="deleteMessage(value.id)" v-if="account.id==value.createAccountId" icon="ios-trash" :tips="$t('删除')"/>
    </div>
  </div>
</template>
<script>
export default {
    name: "DiscussItemContent",
    props: ["value"],
    data() {
        return {
            reply:false,
            replyContent:null,
            account:{}
        };
    },
    watch:{
        reply(val){
            app.postMessage('discuss.message.edit',val)
        }
    },
    mounted(){
        this.account=app.account
    },
    methods:{
        replyMessage(){
            var bean={
                    message:this.replyContent,
                    discussId:this.value.discussId,
                    replyMessageId:this.value.id
            }
            this.replyContent=null;
            app.invoke('BizAction.addDiscussMessage',[app.token,bean],(info)=>{
                this.reply=false;
                app.postMessage('discuss.message.update')
            });
            
        },
        deleteMessage(id){
            app.invoke('BizAction.deleteDiscussMessage',[app.token,id],(info)=>{
                app.postMessage('discuss.message.update')
            });
        },
        preview(item){
             app.previewFile(item.uuid);
        }
    }

};
</script>