<style scoped>
.overdue-label{
    font-size:13px;
}
.lefttime-overdue{
  color:#FF3611;
}
.lefttime-normal{
  color:#009F87;
}
.lefttime-today{
  color:#FDC043;
}
</style>
<template>
<span class="overdue-label">
    <span v-if="getLeftDays()<0" class="lefttime-overdue">超期 {{getLeftDays()*-1}} 天</span>
    <span v-if="getLeftDays()>0" class="lefttime-normal">剩余 {{getLeftDays()}} 天</span>
    <span v-if="getLeftDays()==0" class="lefttime-today">今天到期</span>
</span>
</template>
<script>
export default {
    name:"OverdueLabel",
    props: ['value'],
    data (){
        return{
        }
    },
    methods:{
        getLeftDays(){
            return window.getLeftDays(this.value);
        }
    }
}
</script>