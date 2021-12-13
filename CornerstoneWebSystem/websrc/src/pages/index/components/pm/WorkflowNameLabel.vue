<style scoped>
    .gray{
        color:#999;
        padding-right:5px;
        text-decoration: line-through !important;
    }
    .link{
        padding-right:5px;
        text-decoration: none;
    }
    .finish-type{
        background-color: #FF604B;
        color:#fff;
        border-radius: 3px;
        font-size:12px;
        margin-left:3px;
        padding:1px 3px;
    }
</style>

<i18n>
    {
    "en": {
         "已终止": "Terminal",
        "已撤回": "Revoke"
    },
    "zh_CN": {
        "已终止": "已终止",
        "已撤回": "已撤回"
    }
    }
</i18n>

<template>
    <span>
        <span v-if="id" class="numberfont" style="color:#999;padding-right:5px;font-weight:bold">#{{id}}</span>
        <abbr :title="name" class="link" :class="{'gray':isDone}">{{name}}</abbr>
        <span v-if="isDone&&finishType>1" class="finish-type" :class="'finish-type-'+finishType">
            <template v-if="finishType==2">
                <template v-if="finishText">{{finishText}}</template>
                <template v-else>{{$t('已终止')}}</template>
            </template>
            <template v-if="finishType==3">{{$t('已撤回')}}</template>
        </span>
    </span>
</template>
<script>
export default {
    name:"WorkflowNameLabel",
    props: ['id','name','isDone','finishType','finishText'],
    data (){
        return{

        }
    }
}
</script>
