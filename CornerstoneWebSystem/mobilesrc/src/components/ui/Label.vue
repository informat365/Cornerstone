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
    <abbr :title="value">{{value}}</abbr>
</div>
</template>
<script>
export default {
    name:"Label",
    props: ['value','width'],
    data (){
        return{    
            displayWidth:this.width==null?"auto":this.width
        }
    },
    mounted(){
        this.$nextTick(()=>{
            if(this.displayWidth=='auto'){
                this.displayWidth=this.$el.parentElement.clientWidth+"px";
            }
        })
    }
}
</script>