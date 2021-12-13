<style scoped>
.tree-item-name{
    font-size:13px;
    padding:10px 25px;
    user-select: none;
    padding-right:5px;
    padding-left:0px;
    border-radius: 5px;
    color:#333;
}
.tree-item-label{
    padding-left:5px;
    width:calc(100% - 40px);
    max-width: calc(100% - 40px);
    display: inline-block;
    vertical-align: middle;
}
.tree-item-children{
    padding-left:20px;
}
.tree-item-name:hover{
  background-color:#F5F5F5;
}

.tree-opt{
    visibility: hidden;
    z-index: 999999;
    text-align: left;
}
.right-bar{
     float: right;
     text-align: right;
}
.tree-item-name:hover .tree-opt{
    visibility: visible;
}

.opt-list{
    width:150px;
    padding-top:8px;
    padding-bottom: 8px;
}
.opt-btn{
    font-size:14px;
    color:#666;
    padding:10px 20px;
    font-weight: bold;
}
.opt-btn:hover{
    background-color:#f3f3f3;
}
.opt-icon{
    font-size:18px;
    margin-right:10px;
}
.node-color{
    width:8px;
    height: 8px;
    border-radius: 50%;
    background-color: #808080;
    display: inline-block;
    margin-left:5px;
}
.node-draft{
    /*background-color: #0097F7;*/
}
.expand-icon{
    font-size:16px;
    vertical-align:text-top;
    margin-right:5px;
    width:15px;
    display: inline-block;
}
</style>
<template>
      <div class="tree-item">
            <div @click="toggleSelect" class="tree-item-name " >
                <template v-if="value.children!=null&&value.children.length>0">
                    <Icon class="expand-icon" @click.native.stop="value.expand=!value.expand" v-if="value.expand" type="ios-arrow-down" />
                    <Icon class="expand-icon" @click.native.stop="value.expand=!value.expand" v-if="value.expand==false" type="ios-arrow-forward" />
                    
                </template>
                <span v-if="value.children==null||value.children.length==0" 
                :class="{'node-draft':value.draftId>0}"
                class="node-color"></span>
                <span class="tree-item-label text-no-wrap"> {{value.name}}</span>

                <span class="right-bar">
                    <Icon v-if="selected" type="md-checkmark" />
                </span>
               
            </div>
            <div class="tree-item-children">
                <template   v-for="item in value.children">
                     <WikiTreeNode v-if="value.children&&value.expand"
                                :change="change"
                                :select="select" 
                                :key="'tctn_'+item.id" :value="item"></WikiTreeNode>
                </template>
               
            </div>
        </div> 
</template>
<script>
export default {
    name:"WikiTreeNode",
    props: ['value','change','select'],
    data (){
        return{
           
        }
    },
    computed:{
        selected(){
            return this.select.id==this.value.id
        }
    }, 
    methods:{    
        toggleSelect(){
            this.select.id=this.value.id;
            this.$emit('input')
            if(this.change){
                this.change();
            }
        }
    }
}
</script>