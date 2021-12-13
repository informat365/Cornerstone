<style scoped>

</style>
<template>
   <span>
       <template v-if="type=='date'">{{value|fmtDate}}</template>
       <template v-if="type=='datetime'">{{value|fmtDateTime}}</template>
   </span>
</template>
<script>
export default {
    name:"TimeLabel",
    props: ['value','type'],
    data (){
        return{
            
        }
    }
}
</script>