<style scoped>
.tag{
    display: inline-block;
    padding: 3px 7px;
    border-radius: 10px;
    background-color: #3C97F0;
    color: #fff;
    font-size: 12px;
    line-height: 1;
    margin-right: 5px;
    text-overflow:ellipsis;
	overflow: hidden;
    white-space: nowrap;
    word-break: break-all;
    position: relative;
    padding-right:15px;
    min-width:40px;
    height:18px;
    vertical-align: middle;
}
.close-btn{
    cursor: pointer;
    position:absolute;
    top:3px;
    right:3px;
}
</style>
<template>
    <div @click="handleClick" class="tag">
         <slot></slot>
         <Icon v-if="closable" class="close-btn" @click.native.stop="handleClose" type="md-close" />
    </div>
</template>
<script>
export default {
    props:['closable'],
    data(){
        return {
            
        }
    },
    methods:{
        handleClose(){
            this.$emit('on-close');
        },
        handleClick(){
            this.$emit('click');
        }
    }
}
</script>