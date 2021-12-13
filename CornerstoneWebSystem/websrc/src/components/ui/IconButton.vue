<style scoped>
.icon-button{
    cursor: pointer;
    color:#A7A7A7;
    display: inline-flex;
    padding-left:8px;
    padding-right:8px;
    vertical-align: middle;
    position: relative;
    line-height: 1;
    user-select: none;
    border-radius: 4px;
    font-weight: bold;
    align-items: center;
}
.disabled{
    cursor: not-allowed;
    color:#ccc;
}
.disabled:hover{
    cursor: not-allowed;
    color:#ccc !important;
    background-color: transparent !important;
}
.icon-title{
    line-height: 1;
}
.icon-button:hover{
    color:#5391F0;
    background-color: #ECEDF1;
}
.icon-button .ivu-icon{
    line-height: unset !important;
    vertical-align: middle !important;
}
.icon-button-badge{
    width:6px;
    height: 6px;
    border-radius: 50%;
    background-color:tomato;
    position: absolute;
    top:-2px;
    right:5px;
}

</style>
<template>

   <div class="icon-button" :class="{'disabled':disabled}" @click="clickButton($event)">
       <template v-if="icon!=null">
            <Tooltip :transfer="transfer" v-if="tips!=null&&tips!=''" :content="tips">
                <Icon :type="icon" :size="size" :color="color"/>
            </Tooltip>
        <Icon v-if="tips==null||tips==''" :type="icon" :size="size" :color="color"/>
       </template>
       <span class="icon-title" v-if="title!=null">
           {{title}}
       </span>
       <div v-if="badge" class="icon-button-badge"></div>
   </div>
</template>
<script>
export default {
    name:"IconButton",
    props: {
        icon:String,
        title:String,
        tips:String,
        color:String,
        disabled:{type:Boolean,default:false},
        size:{type:[Number,String],default:20},
        badge:{type:Boolean,default:false},
        transfer:{type:Boolean,default:true},
    },
    data (){
        return{
        }
    },
    methods:{
        clickButton(e){
            if(this.disabled==true){
                e.stopPropagation();
                e.preventDefault();
                return;
            }
            this.$emit('click',e)
        }
    }
}
</script>
