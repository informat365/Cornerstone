<style scoped>
.nav-tab-bar{
    color:#444;
    font-size:13px;
    font-weight: 500;
    display: inline-block;
    padding-bottom:4px;
    height: 48px;
    overflow: hidden;
}
.nav-tab-item{
    display: inline-block;
    max-width:140px;
    overflow: hidden;
    cursor: pointer;
    height: 48px;
    position: relative;
    cursor: pointer;
    user-select: none;
    margin-right:10px;
    transition: all 0.3s;
}
.nav-tab-item:hover{
    color:#009EEE;
}
</style>
<i18n>
{
    "zh_CN":{
        "最近访问：":"最近访问："
    },
    "en": {
        "最近访问：":"Recent:"
    }
}
</i18n>
<template>
   <div class="nav-tab-bar" style="padding-left:40px;text-align:center">
        <span v-if="projectList.length>0" class="nav-tab-item" style="color:#999">{{$t('最近访问：')}}</span>

        <template v-for="(p,idx) in projectList" v-if="isNotProjectSet(p.templateUuid)">
            <span @click="showProject(p)" class="nav-tab-item text-no-wrap"
                    v-if="idx<showCount" :key="'sp_'+p.id">
                    <Icon v-if="p.star" style="margin-left:2px;vertical-align:sub;" class="project-star"  type="md-star"/> {{p.name}}
            </span>
        </template>
    </div>
</template>
<script>
export default {
    name:"ProjectTabbar",
    props: ['value'],
    data (){
        return{
            projectList:this.value,
            showCount:0,
        }
    },
    mounted(){
        this.setupValue();
        this.computeDisplay();
        window.onresize = () => {
           this.computeDisplay();
        }
    },
    beforeDestroy(){
        window.onresize=null;
    },
    watch:{
        value(val){
            this.setupValue();
        },
    },
    methods:{
        isNotProjectSet(templateUuid){
            return templateUuid !== process.env.VUE_APP_PROJECT_SET_TEMPLATE_UUID;
        },
        setupValue(){
            var t=[];
            for(var i=0;i<this.value.length;i++){
                 t.push(this.value[i]);
            }
            this.projectList=t.sort((a,b)=>{
                return b.lastAccessTime-a.lastAccessTime;
            })
        },
        computeDisplay(){
            var tabbarWidth=document.body.clientWidth-640-70;
            this.showCount = tabbarWidth/140;
            if(this.showCount>3){
                this.showCount=3;
            }
        },
        showProject(item){
            app.loadPage('/pm/project/'+item.uuid+'/project')
        },
    }
}
</script>
