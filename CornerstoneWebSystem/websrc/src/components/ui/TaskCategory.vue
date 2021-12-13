<style scoped>
.task-tag{
    display: inline-block;
    font-size:12px;
    color: #ffffff;
    background-color: #eee;
    text-align: center;
    white-space: nowrap;
    border-radius: 12px;
    padding: .25em .6em;
    font-size: 12px;
    font-weight: 600;
    border-radius: 1rem;
    line-height: 1;
    vertical-align: middle;
    white-space: nowrap;
    margin-right:5px;
    user-select: none;
    font-weight: bold;
}

</style>
<template>
   <span class="task-tag" :style="{backgroundColor:color+'55'}">
       <span :style="{color:color}" :title="label">{{label}}</span>
    </span>
</template>
<script>
export default {
    name:"TaskCategory",
    props: ['color','label'],
    data (){
        return{

        }
    }
}
</script>
