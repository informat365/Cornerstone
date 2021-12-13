<style scoped>
  .content-container{
    width:100%;
  }
</style>
<template>
   <div class="content-container">
      <div v-for="item in pageList" :key="item">
        <div :id="'Page_'+item"></div>
      </div>
    </div>
</template>
<script>
//
export default {
  name:"NavigationPageContainer",
  data(){
    return {
      pageList:[],
      currentComponent:null,
    }
  },
  watch: {
    '$route' (to, from) {
        this.showPage(to.fullPath,to.params.page,to.query);
    },
  },
  beforeDestroy(){
      if(this.currentComponent){
        this.currentComponent.$destroy();
      }
  },
  mounted(){
        this.showPage(this.$route.fullPath,this.$route.params.page,this.$route.query);
  },
  methods:{
        showPage:function(url,id,query){
            var camelCased = id.replace(/_([a-z])/g, function (g) { return g[1].toUpperCase(); });
            camelCased=camelCased.charAt(0).toUpperCase() + camelCased.slice(1);
            //
            if(camelCased.indexOf('Task')!=-1){
                 let objectType= id.substr(4);
                 if(Number.isNumeric(objectType)){
                     app.objectType = objectType;
                 }
                camelCased="TaskPage";
            }else{
                camelCased=camelCased+"Page";
            }
            //
            console.log("show page ",url,query,id,camelCased,this.pageList);
            var copyQuery=Object.assign({}, query);
            copyQuery.$page=id;
            app.$emit("PageChangeEvent",id);
            if(id){
                this.showPageView(url,camelCased,id,copyQuery);
            }
        },
        showPageView:function(url,component,id,query){
            if(this.pageList.length==0||this.pageList[0]!=id){
                this.pageList=[];
                this.pageList.push(id);
                if(this.currentComponent!=null){
                    this.currentComponent.$destroy();
                }
                this.$nextTick(()=>{
                    this.currentComponent=app.loadComponent(component,query,'#Page_'+id)
                })
                return;
            }
            if(this.pageList.length>0&&this.pageList[0]==id){
                if(this.currentComponent.pageUpdate){
                    console.log('page update ',id)
                    this.currentComponent.args=query;
                    this.currentComponent.pageUpdate();
                }
            }
        },
  }
}
</script>

