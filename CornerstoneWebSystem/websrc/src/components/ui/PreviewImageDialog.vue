<style scoped>
 
</style>
<template>
    <LightBox v-if="previewImages!=null&&previewImages.length>0" :startAt="startIndex" @onOpened="onOpen" :images="previewImages"></LightBox>
</template>
<script>
    import LightBox from 'vue-image-lightbox';
    export default {
        name:"PreviewImageDialog",
        components: {
            LightBox,
        },
        mixins: [componentMixin],
        data () {
            return {
                previewImages:[],
                startIndex:0,
            }
        },
        methods:{
            pageLoad(){
                if(this.args.list==null||this.args.list.length==0){
                    this.args.list=[];
                    this.args.list.push(this.args.item);
                }
                var idx=0;
                var images=this.args.list.map(item=>{
                    if(item==this.args.item){
                        this.startIndex=idx++;
                    }
                    return{
                        thumb:item,
                        src:item,
                    }
                })
                this.previewImages=images;
            },
            onOpen(v){
                if(v==false){
                    this.$el.parentElement.removeChild(this.$el)
                }
            }
        }
    }
</script>