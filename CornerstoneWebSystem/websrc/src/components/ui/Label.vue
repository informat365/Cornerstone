<style scoped>
    .label{
        display: inline-block;
        text-overflow:ellipsis;
	    overflow: hidden;
        white-space: nowrap;
        vertical-align: middle;
        line-height: 1;
    }
    .label abbr{
        text-decoration: none;
    }
</style>
<template>
<div class="label" :style="{maxWidth:displayWidth}">
    <abbr :title="label">{{label}}</abbr>
</div>
</template>
<script>
export default {
    name:"Label",
    props: ['value','width'],
    data (){
        return{  
        	label:this.value,
            displayWidth:this.width==null?"auto":this.width
        }
    },
    watch:{
        value(val){
            this.label=val;
        }
    },
    mounted(){
        this.$nextTick(()=>{
            if(this.displayWidth=='auto'){
                var w=this.$el.parentElement.clientWidth;
                if(w>0){
                    this.displayWidth=w+"px";
                } 
            }
        })
    }
}
</script>