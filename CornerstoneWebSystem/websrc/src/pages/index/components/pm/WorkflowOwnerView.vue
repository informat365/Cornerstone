<style scoped>
    .owner-view{
        font-size:13px;
    }
    .owner-box{
        position: relative;
        display: inline-block;
    }
    .owner-check-wrap{
        width:7px;
        height:7px;
        background-color: #6abe83;
        border-radius: 50%;
        position:absolute;
        top:0;
        right:-2px;
        z-index:10;
    }
</style>
<template>
<span class="owner-view">
    <template v-if="list" >
        <template v-if="list.length==0">
            -
        </template>
        <div class="owner-box" v-if="list.length==1">
            <AvatarImage  size="small" :name="list[0].ownerAccountName" 
                :value="list[0].ownerAccountImageId" type="label"></AvatarImage>
        </div>
                            
        <template v-if="list.length>1">
            <div v-for="(acc,idx) in list" :key="'_acc'+idx"  class="owner-box" >
                <AvatarImage  
                    size="small" :name="acc.ownerAccountName" 
                    :value="acc.ownerAccountImageId" :type="type"></AvatarImage>
                    <div v-if="acc.status!=1" class="owner-check-wrap"></div>
            </div>
         </template>
     </template>
     <template  v-if="list==null">
        -
     </template>
  </span>
</template>
<script>
export default {
    name: "WorkflowOwnerView",
    props: ["value","type"],
    data() {
        return {
            list: this.value,
        };
    },
    watch:{
        value(val){
            this.list=val
        }
    },
    methods: {

    }
};
</script>