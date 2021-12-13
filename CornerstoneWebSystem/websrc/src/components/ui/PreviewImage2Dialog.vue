<style scoped>

</style>
<template>
<viewer style="z-index:999999" :options="options" :images="previewImages"
                  @inited="inited"
                  class="viewer" ref="viewer">
                   <template slot-scope="scope">
        <img v-for="src in scope.images" :src="src" :key="src">
        {{scope.options}}
      </template>
</viewer>
</template>
<script>
    import 'viewerjs/dist/viewer.css'
    import Viewer from 'v-viewer'
    import Vue from 'vue'
    Vue.use(Viewer)
    export default {
        name:"PreviewImage2Dialog",
        mixins: [componentMixin],
        data () {
            return {
                previewImages:[],
                startIndex:0,
                options: {
                    inline:false,
                    hidden:  ()=>{
                        this.$viewer.destroy();
                        this.$el.parentElement.removeChild(this.$el)
                    },
                },
            }
        },
        methods:{
            pageLoad(){
                if(this.args.list==null||this.args.list.length==0){
                    this.args.list=[];
                    this.args.list.push(this.args.item);
                }
                for(var i=0;i<this.args.list.length;i++){
                    var t=this.args.list[i];
                    if(t==this.args.item){
                        this.startIndex=i;
                        break;
                    }
                }
                this.previewImages=this.args.list;
            },
            inited (viewer) {
                this.$viewer = viewer
                this.$viewer.view(this.startIndex)
                //this.$viewer.destroy();
            },
        }
    }
</script>