<style>
    .layer{
        margin: 0 auto;
        background: #FFF;
        box-shadow: 0 2px 10px 0 rgba(0,0,0,.2);
        transition: all .15s ease;
        position: relative;
    }
    .layer-info{
        position:absolute;
        top:-30px;
        left:0;
        height:30px;
        overflow: auto;
        background-color: red;
        color:#fff;
        font-size:12px;
    }
</style>
<template>
<div 
      class="layer"
      :style='{
            backgroundImage: "url(" + cropImageData + ")",
            backgroundRepeat: "no-repeat",
            backgroundSize: "contain",
            backgroundPosition: "center",
            backgroundSize: "" + layerData.frame.width + "px " + layerData.frame.height + "px", width: "" + layerData.frame.width + "px", height: "" + layerData.frame.height + "px"}'>
    <div class="layer-info">
        {{layerData.name}}  {{layerData.frame}}
    </div>
</div>
</template>
<script>
//
export default {
    props:['value','image'],
    data() {
        return {
            layerData:this.value,
            cropImageData:null
        }
    },
    watch:{
        value(val){
            this.layerData=val;
            this.loadImage();
        }
    },
    mounted() {
        this.loadImage();
    },
    methods: {
        loadImage(){
            var img = new Image()
            img.onload = ()=>{
                var cropImageData=this.getImagePortion(img,
                    this.layerData.frame.width,
                    this.layerData.frame.height,
                    this.layerData.frame.x,
                    this.layerData.frame.y-60)
                this.cropImageData=cropImageData;
            }
            img.src = this.image;
        },
        getImagePortion(imgObj, newWidth, newHeight, startX, startY){
            var tnCanvas = document.createElement('canvas');
            var tnCanvasContext = tnCanvas.getContext('2d');
            tnCanvas.width = newWidth; tnCanvas.height = newHeight;
            var bufferCanvas = document.createElement('canvas');
            var bufferContext = bufferCanvas.getContext('2d');
            bufferCanvas.width = imgObj.width;
            bufferCanvas.height = imgObj.height;
            bufferContext.drawImage(imgObj, 0, 0);
            tnCanvasContext.drawImage(bufferCanvas, startX,startY,newWidth , 
                                        newHeight,0,0,newWidth,newHeight);
            return tnCanvas.toDataURL();
        }
    }
};
</script>