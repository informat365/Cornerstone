<style scoped>
    .custom-span{
        padding-right:5px;
        color:#111;
        font-weight:bold;
    }
</style>
<template>
    <abbr :title="getJoinValue()">
       <span class="custom-span" v-for="item in value" :key="item">
           {{item}}
       </span>
    </abbr>
</template>
<script>
export default {
    name:"TaskCustomLabel",
    props: ['value'],
    data (){
        return{
            
        }
    },
    methods:{
        getJoinValue(){
            if(this.value==null){
                return "";
            }
            var s="";
            for(var i=0;i<this.value.length;i++){
                s+=this.value[i]+" ";
            }
        }
    }
}
</script>