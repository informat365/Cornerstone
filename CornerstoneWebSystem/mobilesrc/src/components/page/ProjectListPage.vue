<style scoped>
    .page{
        padding:0;
        background-color: #fff;
    }
   
    .table-count{
       background-color: #E8E8E8;
       color:#666;
       padding:3px 5px;
       border-radius: 3px;
       font-size:12px;
    }
    .project-box{
        padding:12px 20px;
        border-top:1px solid #eee;
        font-size:13px;
    }
    .project-item{
        display: flex;
        flex-direction: row;
    }
    .project-name{
        font-size:14px;
        font-weight: bold;
    }
    .iteration-name{
        color:#666;
    }
    .vux-x-icon {
        fill: #FFBC5A;
    }
</style>
<template>
  <div class="page">
      <div v-if="projectList">
           
               <div class="table-info">
                <div class="top-bar">
                    <div class="top-bar-left">
                      </div>
                    <div class="top-bar-center">
                         <span class="table-count">{{projectList.length}}条数据</span>
                     </div>
                     <div class="top-bar-right">
                     </div>
                </div>
               
            </div>

            <div style="padding:15px"></div>
            <div class="main-box">

            <div @click="showProject(item)" class="project-box" v-for="item in projectList" :key="'t'+item.id">
                <flexbox>
                    <flexbox-item :span="10">
                        <div class="project-name">
                            {{item.name}}<x-icon v-if="item.star" type="ios-star" size="12"></x-icon>
                        </div>
                    </flexbox-item>
                    <flexbox-item :span="2"><div>{{item.createTime|dateDiff}}天</div></flexbox-item>
                </flexbox>
                <flexbox v-if="item.iteration.id>0" style="margin-top:8px">
                    <flexbox-item :span="10"><div class="iteration-name" > {{item.iteration.name}}</div></flexbox-item>
                    <flexbox-item :span="2"><div> <DataDictLabel  type="ProjectIteration.status" :value="item.iteration.status"></DataDictLabel></div></flexbox-item>
                </flexbox>
            </div>
            </div>
      </div>
  </div>
</template>

<script>
import { Flexbox, FlexboxItem } from 'vux'
export default {
    components: {Flexbox,FlexboxItem},
    mixins:[componentMixin],
    data() {
        return {
            projectList:null,
        }
    },
    methods:{
        pageLoad(){
            this.loadData();
        },
        loadData(){
            app.invoke("BizAction.getMyProjectList",[app.token],list => {
                this.projectList=list;
            });
        },
        showProject(item){
            app.loadPage('project?id='+item.uuid)
        }
    }
}
</script>


