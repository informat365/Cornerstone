<style scoped>
  .type-color{
    display: inline-block;
    width:8px;
    height:8px;
    border-radius: 50%;
    color:#333;
    margin-right: 3px;
}
</style>
<template>
    <span>
        <span class="type-color" :style="{backgroundColor:color}"></span>
        <span>{{name}}</span>
    </span>
</template>
<script>
export default {
    name:"WorkflowDefineNameLabel",
    props: ['name','color'],
    data (){
        return{
            
        }
    }
}
</script>