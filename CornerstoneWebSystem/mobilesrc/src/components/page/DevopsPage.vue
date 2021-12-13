<style scoped>
    .page{
       padding:0;
    }
    .pipeline-card-stage{
        padding:5px 15px;
        padding-bottom:0;
        display: flex;
        align-items: center;
        width:100%;
        margin-bottom:10px;
    }
    .pipeline-stage-name{
        font-size:12px;
        font-weight: bold;
        color:#999;
    }

</style>
<template>
  <div class="page">  
      <div class="table-nodata" v-if="pipelineList.length==0">
          暂无数据
      </div>
     <group v-for="item in pipelineList" :key="item.id" :title="item.name">
            <cell-box @click.native="runPipeline(item)" is-link :border-intent="false" class="sub-item" style="padding:10px 0">
                <div style="width:100%">
                <div style="font-size:13px;padding:5px 10px;margin-left:5px">
                    <template v-if="item.runLog.id==0">无执行记录</template>
                    <template v-if="item.runLog.id>0"> 
                            <span>{{item.runLog.createTime|fmtDeltaTime}}</span>
                            {{item.runLog.createAccountName}}
                            <DataDictLabel style="margin-right:10px" type="ProjectPipelineRunLog.status" :value="item.runLog.status"></DataDictLabel>
                    </template>
                </div>

                <div class="pipeline-card-stage">
                            <template v-if="item.pipelineDefine" v-for="(stage,idx) in item.pipelineDefine.stages">
                                <span  class="pipeline-stage-name" :key="item.id+'_'+stage.name">{{stage.name}}  </span>
                                <x-icon v-if="idx<item.pipelineDefine.stages.length-1" type="ios-arrow-thin-right" size="20"></x-icon>
                            </template>
                            <span class="pipeline-stage-name" v-if="item.pipelineDefine==null||item.pipelineDefine.stages==null||item.pipelineDefine.stages.length==0">
                                未设置阶段
                            </span>
                      </div>
                </div>

            </cell-box>
    </group>
       
  </div>
</template>

<script>
import { Flexbox, FlexboxItem,Group,GroupTitle,Cell,CellBox} from 'vux'
export default {
    components: {Flexbox,FlexboxItem,Group,GroupTitle,Cell,CellBox},
    mixins:[componentMixin],
    data () {
        return {
            pipelineList:[]
        }
    },
    methods:{
        pageLoad(){
            this.loadData();
        },
        loadData(){
            this.loadPipelineList()
        },
        loadPipelineList(){
            var query={
                pageIndex:1,
                pageSize:1000,
                projectId:this.args.projectId
            }
            app.invoke("BizAction.getProjectPipelineInfoList",[app.token,query],info => {
                this.pipelineList=info.list;
            });
        },
        runPipeline(item){
            app.loadPage('/m/pipeline_run?id='+item.id)
        }
    }
}
</script>


