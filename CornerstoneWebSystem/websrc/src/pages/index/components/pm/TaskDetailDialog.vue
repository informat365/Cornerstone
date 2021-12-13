<style scoped>
.page{
  min-height: calc(100vh - 51px);
  background-color: #F1F4F5;
  width: 100%;
  padding:20px;
  text-align: center;
}
.detail-content{ 
    width:740px;
    display: inline-block;
    background-color: #fff;
    border:1px solid #eee;
    border-radius: 5px;
    text-align: left;
    padding:25px;
    min-height: calc(100vh - 100px)
}
.detail-content-wide{
    width:100%;
}
.task-name{
    font-size:18px;
    font-weight: bold;
    color:#2b2b2b;
    margin-bottom:10px;
}
</style>
<i18n>
{
    "en": {
        "详细描述": "Detail",
        "宽视图":"Wide",
        "未设置详细描述":"none",
        "取消":"Cancel",
        "保存":"Save"
    },
    "zh_CN": {
        "详细描述": "详细描述",
        "宽视图":"宽视图",
        "未设置详细描述":"未设置详细描述",
        "取消":"取消",
        "保存":"保存"
    }
}
</i18n>
<template>
 <Modal
        ref="dialog"  v-model="showDialog" 
        :closable="true" 
        :mask-closable="false"
        :loading="false" :title="task.name" class="fullscreen-modal"
         width="100%" 
         :footer-hide="true">
        <p slot="header">
            <span>{{$t('详细描述')}}</span>
            <span style="margin-left:10px;color:#999;font-size:12px">{{$t('宽视图')}}：</span>
            <i-Switch v-model="wideMode" size="small"/>
        </p>
    <div class="page">
          
        <div class="detail-content" :class="{'detail-content-wide':wideMode}">
            <div class="task-name">
                {{task.name}}
            </div>
            
            <div class="description-box" v-show="descEdit==false" @dblclick="editDesc">
                <RichtextLabel :value="task.content" :placeholder="$t('未设置详细描述')"></RichtextLabel>
            </div>

             <div class="description-box" v-show="descEdit==true">
                <RichtextEditor ref="descEditor" v-model="task.content"></RichtextEditor>
                <div style="margin-top:10px;text-align:right">
                    <Button type="text" style="margin-right:10px" @click="descEdit=false">{{$t('取消')}}</Button>
                    <Button type="default"  @click="descEdit=false;updateTask()" >{{$t('保存')}}</Button>
                </div>
            </div>

        </div>
    </div>
</Modal>
</template>

<script>

export default {
    mixins: [componentMixin],
    data(){
        return {
            task:{},
            wideMode:false,
            descEdit:false
        }
    },
    methods:{
        pageLoad(){
            this.task=this.args.task;
        },
        editDesc(){
            if(this.args.propDisable){
                return;
            }
            this.descEdit=true;
        },
        updateTask(){
            var str=this.$refs.descEditor.getValue();
            this.task.content=str;
            this.args.updateTaskContent(this.task)
        }
    }
}
</script>