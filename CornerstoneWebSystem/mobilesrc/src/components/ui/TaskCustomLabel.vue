<style scoped>
    .custom-span{
        padding-right:5px;
        color:#111;
        font-weight:bold;
    }
</style>
<template>
    <span>
       <span  class="custom-span" v-for="item in value" :key="item">
           {{item}}
       </span>
    </span>
</template>
<script>
export default {
    name:"TaskCustomLabel",
    props: ['value'],
    data (){
        return{
            
        }
    }
}
</script>