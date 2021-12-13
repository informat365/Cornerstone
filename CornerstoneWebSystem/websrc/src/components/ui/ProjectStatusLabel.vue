<style scoped>

    .project-status-1{
        background-color: #00BE70 !important;
    }
    .project-status-2{
        background-color: #FF9725 !important;
    }
    .project-status-3{
        background-color: #EC0023 !important;
    }
    .project-status-info-1{
        color:#00BE70;
    }
    .project-status-info-2{
        color:#FF9725;
    }
    .project-status-info-3{
        color:#EC0023;
    }
      .project-status-dot{
        width:10px;
        height: 10px;
        border-radius: 50%;
        background-color: #0097F7;
        display: inline-block;
        margin-right:5px;
    }

</style>
<i18n>
{
    "en": {
        "进度正常":"normal",
        "存在风险":"risk",
        "进度失控":"out of control"
    },
    "zh_CN": {
        "进度正常":"进度正常",
        "存在风险":"存在风险",
        "进度失控":"进度失控"
    }
}
</i18n>
<template>
<span>
        <span class="project-status-dot" :class="'project-status-'+value"></span>
        <span class="project-status" :class="'project-status-info-'+value">
                            <template v-if="value==1">{{$t('进度正常')}}</template>
                            <template v-if="value==2">{{$t('存在风险')}}</template>
                            <template v-if="value==3">{{$t('进度失控')}}</template>
        </span>
    </span>
</template>
<script>
export default {
    name:"ProjectStatusLabel",
    props: ['value'],
    data (){
        return{      
        }
    }
}
</script>