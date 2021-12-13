<style scoped>
.task-tag{
  display: inline-block;
  font-size:12px;
  color: #fff;
  background-color: #dd5826;
  text-align: center;
  white-space: nowrap;
  border-radius: 12px;
  padding:0px 10px;
  height:20px;
  line-height: 20px;
  vertical-align: middle;
  margin-right:5px;
}

</style>
<template>
   <span class="task-tag" :style="{backgroundColor:color}">{{label}}</span>
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