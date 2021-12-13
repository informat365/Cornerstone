<style >
    .board{
        width:9600px;
        height:9600px;
        background-color: #F0F2F5;
        position: relative;
    }
    .screen{
        margin: 0 auto;
        position: absolute;
        left: 50%;
        top: 50%;
        background: #FFF;
        box-shadow: 0 2px 10px 0 rgba(0,0,0,.2);
        transition: all .15s ease;
    }
    .board-inner{
        position: relative;
    }
    .layer-box{
        position:absolute;
    }
</style>
<template>
<div ref="board" class="board">
   <div 
      v-if="sketchData.imageData" 
      class="screen"
      :style='{
        transform: "scale("+viewScale+")",
        backgroundImage: "url(" + sketchData.imageData + ")",
        backgroundRepeat: "no-repeat",
        backgroundSize: "contain",
        backgroundPosition: "center",
        backgroundSize: "" + sketchData.imageWidth + "px " + sketchData.imageHeight + "px", width: "" + sketchData.imageWidth + "px", height: "" + sketchData.imageHeight + "px"}'>
   </div>
</div>
</template>
<script>
import Layer from './Layer'
//
export default {
    props:["value",'scale'],
    components:{
        Layer
    },
    data() {
        return {
            sketchData:this.value,
            viewScale:this.scale/100
        }
    },
    watch:{
        value(val){
            this.sketchData=val;
            this.setupScrollPosition();
        },
        scale(val){
            this.viewScale=val/100;
        }
    },
    mounted() {
        this.setupDragEvent();
    },
    methods: {
       setupScrollPosition(){
            var mainbox=this.$refs.board.parentElement;
            mainbox.scrollLeft=(4800-this.sketchData.imageWidth);
            mainbox.scrollTop=(4800-this.sketchData.imageHeight)
       },
       setupDragEvent(){
            var mainbox=this.$refs.board.parentElement;
            var div=this.$refs.board;
            div.onmousedown = (ev)=>{
                ev.preventDefault(); 
                var oevent = ev || event; 
                var distanceX = oevent.clientX; 
                var distanceY = oevent.clientY; 
                var scrollLeft=mainbox.scrollLeft;
                var scrollTop=mainbox.scrollTop;
                
                document.onmousemove = (ev)=>{ 
                    var oevent = ev || event;
                    var x1 = oevent.clientX - distanceX;
                    var y1 = oevent.clientY - distanceY;
                    mainbox.scrollLeft=scrollLeft-x1;
                    mainbox.scrollTop=scrollTop-y1;
        　　　　};
            　　document.onmouseup = ()=>{ 
                    document.onmousemove = null;
            　　};
            
	    }
       }
    }
};
</script>