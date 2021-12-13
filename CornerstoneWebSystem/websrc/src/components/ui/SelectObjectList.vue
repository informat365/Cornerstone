<style scoped>
   
</style>
<template>
    <div>
        <Tag v-for="(item,idx) in objectList" :key="item.id" :name="item.id" :closable="!readonly" @on-close="deleteObject(idx)">{{item.name}}</Tag>
            <i-button v-if="!readonly" @click="chooseObject" type="default" size="small" icon="ios-search"></i-button>
        </div>
  
</template>
<script>
export default {
    name:"SelectObjectList",
    props:{
        value:{
            type:Array,
            default:()=>{
                return [];
            }
        },
        domain:{
            type:String,
            required: true
        },
        readonly:{
            type:Boolean,
            default:false
        },
        query:{
            type:Object
        }
    },
    data:function(){
        return {
            objectList:this.value==null?[]:this.value
        }
    },
    watch:{
        objectList:function(val){
            this.$emit('input', val);
        },
        value:function(val){
            this.objectList=(this.value==null?[]:this.value);
        }
    },
    methods:{
        chooseObject:function(e){
           app.showDialog(SelectObjectDialog,{
               list:this.objectList,
               query:this.query,
               callback:this.chooseCallback,
               multiple:true,
               domain:this.domain
           });
        },
        deleteObject:function(idx){
            this.objectList.splice(idx,1);
        },
        containsObject:function(id){
            for(var i=0;i<this.objectList.length;i++){
                if(this.objectList[i].id==id){
                    return true;
                }
            }
            return false;
        },
        chooseCallback:function(list){
            for(var i=0;i<list.length;i++){
                if(!this.containsObject(list[i].id)){
                    this.objectList.push(list[i]);
                }
            }
        }
    }
}
</script>