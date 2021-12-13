<style scoped>
.page{
  display: flex;
}
.page-menu{
    width:260px;
    height: calc(100vh - 50px);
    overflow: auto;
    background-color: #fff;
    border-right:1px solid rgba(216,216,216,1);
    padding:25px;
    user-select: none;
}
@media (max-width: 1000px) {
    .page-menu {
        display: none;
    }
    .silde-control-bar{
        display: none;
    }
}
.page-right-content{
    flex:1;
    height: calc(100vh - 50px);
    overflow: auto;
    padding:0px;
}
.menu-item{
    font-size:15px;
    font-weight: 500;
    padding:8px;
    color:#666;
    cursor: pointer;
    display: flex;
    align-items: center;
    position: relative;
    transition: all 0.3s;
}
.menu-item:hover{
    color:#009CF1;
}
.menu-item-active{
    color: #009CF1;
    font-weight: bold;
}
.menu-item-group{
    font-size:12px;
    color:#999;
    padding:8px;
    margin-top:20px;
}


.silde-control-bar{
    width:6px;
    position: absolute;
    top:100px;
    left:260px;
}
.silde-control-bar-close{
    left:0px;
}
.hide-side-button{
  position: absolute;
  right:0px;
  top:32px;
  width:6px;
  height: 40px;
  background-color: rgba(216,216,216,1);
  cursor: pointer;
  color: #fff;
  border-radius: 2px;
}
.hide-side-button:hover{
  background-color: #5391F0;
}
.hide-side-button-icon{
    position: absolute;
    left: -3px;
    top: 13px;
}
</style>
<i18n>
{
    "en": {
        "Pipeline": "Pipeline",
        "主机":"Machine",
        "交付物":"Artifact"
    },
    "zh_CN": {
        "Pipeline": "Pipeline",
        "主机":"主机",
        "交付物":"交付物"
    }
}
</i18n>
<template>
    <div class="page">
       <div class="silde-control-bar" :class="{'silde-control-bar-close':showSideMenu==false}">
                <div @click="showSideMenu=!showSideMenu" class="hide-side-button">
                        <Icon v-if="showSideMenu" class="hide-side-button-icon" type="md-arrow-dropleft" />
                        <Icon v-if="showSideMenu==false" class="hide-side-button-icon" type="md-arrow-dropright" />
                </div>    
       </div> 

        <div class="page-menu scrollbox" v-if="showSideMenu">

           
             <div class="menu-item" @click="showMenu('pipeline')" :class="{'menu-item-active':selectMenu=='pipeline'}">
                <Icon type="md-construct" style="margin-right:10px" /> {{$t('Pipeline')}}
            </div>
             <div class="menu-item" @click="showMenu('node')" :class="{'menu-item-active':selectMenu=='node'}">
                <Icon type="md-desktop" style="margin-right:10px" /> {{$t('主机')}}
            </div>
            <div class="menu-item" @click="showMenu('artifact')" :class="{'menu-item-active':selectMenu=='artifact'}">
                <Icon type="md-apps" style="margin-right:10px"/> {{$t('交付物')}}
            </div>            
        </div>
        <div class="page-right-content scrollbox">
                <DevopsNodeView ref="nodeView" v-if="selectMenu=='node'"></DevopsNodeView>
                <DevopsPipelineView ref="pipelineView" v-if="selectMenu=='pipeline'"></DevopsPipelineView>
                <DevopsArtifactView ref="artifactView" v-if="selectMenu=='artifact'"></DevopsArtifactView>
        </div>
    </div>
</template>

<script>

export default {
    mixins: [componentMixin],
    data(){
        return {
            title:"DevOpsPage",
            account:{},
            selectMenu:null,
            showSideMenu:true
        }
    },
    methods:{
        pageLoad(){
           this.account=app.account;
           if(this.args.view){
                this.selectMenu=this.args.view;
           }else{
                this.selectMenu='pipeline';
           }
        },
        pageUpdate(){
            if(this.args.view){
                this.selectMenu=this.args.view;
            }else{
                this.selectMenu='pipeline';
            }
        },
        pageMessage(type,data){
            if(type=='machine.edit'){
                if(this.$refs.nodeView){
                    this.$refs.nodeView.loadData();
                }
            }
            if(type=='pipeline.edit'||type=='pipeline.runstatus'){
                if(this.$refs.pipelineView){
                    this.$refs.pipelineView.loadData();
                }
            }
            if(type=='websocket.pipelineUpdate'){
                var projectId=app.projectId;
                if(data.projectId==projectId){
                    if(this.$refs.pipelineView){
                        this.$refs.pipelineView.loadData();
                    }
                }
            }
        },
        
        showMenu(item){
            app.loadPage('?view='+item)
        }
  }
}
</script>