<style scoped>
.overdue-label{
    font-size:12px;
    border-radius: 3px;
    padding:0px 3px;
    display: inline-block;
    line-height: 1.2;
}
.lefttime-normal{
  background-color:#01a0e4;
  color:#fff;
}
</style>
<i18n>
{
    "en": {
        "时长":" {0} day"
    },
    "zh_CN": {
        "时长":"时长{0}天"
    }
}
</i18n>
<template>
  <span >
       <template>
            <span v-if="leftDays>0" class="overdue-label lefttime-normal">{{$t('时长',[leftDays])}}</span>
        </template>
</span>
</template>
<script>
export default {
    name:"DiffDayText",
    props: ['start','end'],
    data (){
        return{
            tips:"",
        }
    },
    mounted(){
        this.tips=window.formatDate(this.value);
    },
    computed:{
      leftDays:function () {
          return window.dateDiff(new Date(this.end), new Date(this.start))*-1+1;
      }
    },
}
</script>
