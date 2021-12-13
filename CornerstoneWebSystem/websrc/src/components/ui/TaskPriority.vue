<style scoped>
.task-priority{
    color: #8897AA;
    font-size:13px;
    font-weight: 600;
    vertical-align: middle;
}

</style>
<template>
   <span class="task-priority" :style="{color:color}">{{label}}</span>
</template>
<script>
export default {
    name:"TaskPriority",
    props: ['label','color'],
    data (){
        return{

        }
    }
}
</script>