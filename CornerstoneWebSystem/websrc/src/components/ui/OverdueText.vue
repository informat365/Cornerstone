<style scoped>
.overdue-label{
    font-size:12px;
    border-radius: 3px;
    padding:0px 3px;
    display: inline-block;
    line-height: 1.2;
}
.lefttime-overdue{
  background-color:#FF3611;
  color:#fff;
}
.lefttime-normal{
  background-color:#009F87;
  color:#fff;
}
.lefttime-today{
  background-color:#FDC043;
  color:#fff;
}
</style>
<i18n>
{
    "en": {
        "超期":"overdue {0} day",
        "剩余":"rema {0} day",
       "提前":"before {0}day",
        "今天到期":"today"
    },
    "zh_CN": {
        "超期":"超期{0}天",
        "剩余":"剩余{0}天",
        "提前":"提前{0}天",
        "今天到期":"今天到期"
    }
}
</i18n>
<template>
  <span >
       <template v-if="onlyShowOver==true">
            <span v-if="leftDays<0&&!isFinish" class="overdue-label lefttime-overdue">{{$t('超期',[leftDays*-1])}}</span>
            <span v-if="diffDays<0&&isFinish" class="overdue-label lefttime-overdue">{{$t('超期',[diffDays*-1])}}</span>
        </template>
        <template v-if="onlyShowOver!=true">
             <span v-if="diffDays<0&&isFinish" class="overdue-label lefttime-overdue">{{$t('超期',[diffDays*-1])}}</span>
             <span v-if="leftDays<0&&!isFinish" class="overdue-label lefttime-overdue">{{$t('超期',[leftDays*-1])}}</span>
<!--             <span v-if="diffDays<0||leftDays<0" class="overdue-label lefttime-overdue">{{$t('超期',[diffDays*-1||leftDays*-1])}}</span>-->
            <span v-if="leftDays>0&&!isFinish" class="overdue-label lefttime-normal">{{$t('剩余',[leftDays])}}</span>
            <span v-if="diffDays>0&&isFinish" class="overdue-label lefttime-normal">{{$t('提前',[diffDays])}}</span>
            <span v-if="leftDays==0&&!isFinish" class="overdue-label lefttime-today">{{$t('今天到期')}}</span>
        </template>
</span>
</template>
<script>
export default {
    name:"OverdueLabel",
    props: ['value','onlyShowOver','isFinish','finishDate'],
    data (){
        return{
            tips:"",
            leftDays:0,
            diffDays:0
        }
    },
    mounted(){
        this.tips=window.formatDate(this.value);
        //完成时间-当前时间
        this.leftDays=window.getLeftDays(this.value);
        //d2-d1
        if(this.isFinish){
            // console.log(this.finishDate,this.value)
            //截止时间-完成时间
            this.diffDays = window.dateDiff(new Date(this.finishDate),new Date(this.value));
        }
    },
    watch:{
        value(val){
            this.tips=window.formatDate(this.value);
            this.leftDays=window.getLeftDays(this.value);
            if(this.isFinish){
                this.diffDays = window.dateDiff(new Date(this.finishDate),new Date(this.value));
            }
        }
    },
}
</script>
