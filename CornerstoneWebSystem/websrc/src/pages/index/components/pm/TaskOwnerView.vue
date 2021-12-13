<style scoped>
    .owner-view{
        font-size:13px;
    }
</style>
<i18n>
{
    "en": {
        "待认领":"none"
    },
    "zh_CN": {
        "待认领":"待认领"
    }
}
</i18n>
<template>
<span class="owner-view">
    <template v-if="item.ownerAccountList" >
        <template v-if="item.ownerAccountList.length==0">
            {{$t('待认领')}}
        </template>
        <template v-if="item.ownerAccountList.length==1">
            <AvatarImage  size="small" :name="item.ownerAccountList[0].name" :value="item.ownerAccountList[0].imageId" type="label"></AvatarImage>
        </template>
                            
        <template v-if="item.ownerAccountList.length>1">
            <AvatarImage  v-for="acc in item.ownerAccountList" 
                :key="item.id+'_acc'+acc.id" 
                size="small" :name="acc.name" 
                :value="acc.imageId" :type="tips?'tips':'none'"></AvatarImage>
         </template>
     </template>
     <template  v-if="item.ownerAccountList==null">
             {{$t('待认领')}}
     </template>
  </span>
</template>
<script>
export default {
    name: "TaskOwnerView",
    props: ["value","tips"],
    data() {
        return {
            item: this.value
        };
    },
    watch:{
        value(val){
            this.item=val;
        }
    },
    methods: {

    }
};
</script>