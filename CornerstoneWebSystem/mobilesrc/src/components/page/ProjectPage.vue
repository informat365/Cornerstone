<style scoped>
    .page{
       padding:0;
    }
    .project-name{
        font-size:18px;
        font-weight: bold;
    }
    .project-desc{
        font-size:16px;
        color:#666;
        margin-top:10px;
    }
    .iteration-item{
        display: flex;
        padding:8px 15px;
    }
    .iteration-name{
        flex:1;
    }
    .iteration-status{
        width:100px;
    }
</style>
<template>
  <div class="page">  
      <group >
           <group-title slot="title">项目信息</group-title>
           <div style="padding:15px">
                <div class="project-name">{{project.name}}</div>
                <div class="project-desc">{{project.description}}</div>
           </div>
    </group>

     <group >
           <group-title slot="title">迭代</group-title>
           <cell v-for="it in iterationList" :key="it.id" :title="it.name" :inline-desc="iterationDesc(it)">
                 <DataDictLabel slot="icon" type="ProjectIteration.status" style="margin-right:10px" :value="it.status"></DataDictLabel>
           </cell>
    </group>

     <group>
            <group-title slot="title">模块</group-title>
            <cell @click.native="showModule(module)" v-for="module in moduleList" :key="module.id" 
                v-if="isModuleShow(module)" :title="module.name" is-link></cell>
    </group>
       
  </div>
</template>

<script>
import { Flexbox, FlexboxItem,Group,GroupTitle,Cell} from 'vux'
export default {
    components: {Flexbox,FlexboxItem,Group,GroupTitle,Cell},
    mixins:[componentMixin],
    data () {
        return {
            project:{},
            moduleList:[],
            iterationList:[],
        }
    },
    methods:{
        pageLoad(){
            this.loadData();
        },
        loadData(){
            this.loadProjectInfo(this.args.id)
        },
        loadProjectInfo(uuid){
            app.invoke('BizAction.getProjectInfoByUuid',[app.token,uuid],(info)=>{
                this.project=info;
                this.loadProjectShowInfo(info.id)
            });
        },
        loadProjectShowInfo(projectId){
            app.invoke('BizAction.getProjectShowInfo',[app.token,projectId],(info)=>{
                this.moduleList=info.moduleList;
                this.loadIterationList(info.iterationList);
                app.projectPermissionMap={};
                for(var i=0;i<info.permissionList.length;i++){
                    var t=info.permissionList[i];
                    app.projectPermissionMap[t]=true;
                }
            });
        },
        loadIterationList(list){
            this.iterationList=list;
        },
        iterationDesc(item){
            return formatDate(item.startDate)+"~"+formatDate(item.endDate)
        },
        showModule(item){
            if(item.objectType>0){
                app.loadPage('/t/task_list?projectId='+this.project.uuid+"&type="+item.objectType)
            }else{
                app.loadPage('/t/'+item.url+'?projectId='+this.project.id)
            }
        },
        isModuleShow(item){
            if(item.show==false){
                return false;
            }
            if(item.objectType>0){
                return app.prjPerm('task_list_'+item.objectType);
            }else{
                if(item.url=='devops'){
                    return app.prjPerm('devops_list');
                }
                return false;
            }
            return false;
        },
    }
}
</script>


