<style scoped>

</style>
<template>
  <abbr  :title="tips">
    <span >{{getWeekDay()}}</span>
</abbr>
</template>
<script>
export default {
    name:"WeekDayLabel",
    props: ['value'],
    data (){
        return{
            tips:"",
        }
    },
    mounted(){
        this.tips=window.formatDate(this.value);
    },
    watch:{
        value(val){
            this.tips=window.formatDate(this.value);  
        }
    },
    methods:{
        getWeekDay(){
            return window.formatWeekDay(this.value);
        }
    }
}
</script>