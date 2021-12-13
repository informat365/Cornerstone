<style scoped>
.log-item {
  position: relative;
  margin-top: 20px;
}
.log-item-left {
  position: absolute;
  display: inline-block;
  width: 20px;
}
.log-item-right {
  display: inline-block;
  width: 100%;
  padding-left: 50px;
}
.log-item-name {
  font-size: 12px;
  color: #333;
}
.log-item-time {
  font-size: 12px;
  color: #999;
  padding-left: 10px;
}
.log-item-content {
  padding-top: 10px;
  font-size: 13px;
  line-height: 1.5;
}
.log-item-before {
  background-color: #ffd3d3;
  color: #ff3b58;
  text-decoration-line: line-through;
}
.log-item-after {
  background-color: #d4efe6;
  color: #128c87;
}
.log-item-opt {
  float: right;
  visibility: hidden;
}
.log-item-icon {
  font-size: 14px;
  margin-right: 5px;
  vertical-align: text-top;
}

.log-item:hover .log-item-opt {
  visibility: visible;
}

.log-item-creater {
  display: inline-block;
  padding: 2px 0px;
  text-align: center;
  color: #00d5cc;
  font-size: 12px;
  margin-left: 0px;
  transform: scale(0.7);
}
.log-item-owner {
  color: #0091ea;
}
.log-item-link {
  color: #0091ea;
  padding-left: 5px;
  padding-right: 5px;
  cursor: pointer;
}

</style>
<i18n>
{
    "en": {
        "创建人":"Creater",
        "责任人":"Owner",
        "删除评论":"Delete",
        "引用评论":"Quote"
    },
    "zh_CN": {
        "创建人":"创建人",
        "责任人":"责任人",
        "删除评论":"删除评论",
        "引用评论":"引用评论"
    }   
}
</i18n>
<template>
  <div>
    <div class="log-item">
      <div class="log-item-left">
        <AvatarImage :name="item.createAccountName" :value="item.createAccountImageId" type="tips"/>
      </div>
      <div class="log-item-right">
        <div>
          <span class="log-item-name">{{item.createAccountName}}</span>
          <span class="log-item-time">{{item.createTime|fmtDeltaTime}}</span>

          <span class="log-item-creater" v-if="item.createAccountId==task.createAccountId">{{$t('创建人')}}</span>
          <span class="log-item-creater log-item-owner" v-if="isOwner(item.createAccountId)">{{$t('责任人')}}</span>

          <span class="log-item-opt">
            <IconButton @click="quoteCommentReply(item)" icon="md-quote" :tips="$t('引用评论')"/>
            <IconButton
              v-if="item.createAccountId==account.id"
              @click="deleteComment(item)"
              icon="ios-trash"
              :tips="$t('删除评论')"
            />
          </span>
        </div>

        <div class="log-item-content">
          <RichtextLabel :value="item.comment"></RichtextLabel>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
    name: "TaskViewComment",
    props: ["value",'task','account'],
    data() {
        return {
            item: this.value
        };
    },
    watch:{
        value(val){
            this.item=val;
        }
    },
    methods: {
        isOwner(accountId){
            if(this.task.ownerAccountIdList==null||this.task.ownerAccountIdList.length==0){
                return false;
            }
            for(var i=0;i<this.task.ownerAccountIdList.length;i++){
                if(this.task.ownerAccountIdList[i]==accountId){
                    return true;
                }
            }
            return false;
        },
        quoteCommentReply(item){
            this.$emit('quoteCommentReply')
        },
        deleteComment(item){
            this.$emit('deleteComment')
        }
  }
};
</script>