<style>

</style>

<template>
    <div>
        <div class="markdown-body" v-html="markedValue"></div>
        <span style="font-size:14px" v-if="markedValue==null">{{placeholder}}</span>
    </div>
</template>
<script>
export default {
    name:"MarkdownLabel",
    props: ['value','placeholder'],
    data: function () {
        return {
            markedValue:null,
        }
    },
    watch:{
        value(val){
            this.parse();
        }
    },
    mounted:function(){
        this.parse();
        this.addEvent();
    },
    methods:{
        parse(){
            if (window.marked&&this.value) {
                this.markedValue=marked(this.value);
            }else{
                this.markedValue=null;
            }
        },
        addEvent(){
            var imgs = this.$el.getElementsByTagName("img");
            var imgSrcs=[];
            for(var i=0;i<imgs.length;i++){
                imgSrcs.push(imgs[i].src)
            }
            $(this.$el).on('click','img',function(e){
                var images =[];
                app.previewImages(e.currentTarget.src,imgSrcs)
            })
            //
            $(this.$el).on('click','a',function(e){
                var url=$(this).attr('href');
                if(url.indexOf('http')!=-1){
                    window.open(url);
                }
                return false;
            })
        }
    }
}
</script>