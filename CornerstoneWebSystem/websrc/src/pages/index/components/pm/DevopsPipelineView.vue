<style scoped>
   .table-box{
        background-color: #fff;
        padding:30px;
        padding-top:10px;
        box-shadow: 0px 2px 10px 0px rgba(225,225,225,0.5);
        border: 1px solid rgba(216,216,216,1);
   }
   
    .table-info{
       color:#999;
       text-align: center;
   }
   .table-count{
       background-color: #E8E8E8;
       color:#666;
       padding:3px 5px;
       border-radius: 3px;
   }
   
   .table-col-name{
       display: inline-block;
       vertical-align: middle;
   }
   .table-remark{
       color:#999;
       margin-top:5px;
   }
    .nodata{
       padding:60px;
       font-size:20px;
       color:#999;
       text-align: center;
   }
   .table-info-row{
       display: flex;
       align-items: center;
   }
  
   .card-box{
       display: block;
   }

.pipeline-card{
    width:260px;
    text-align: left;
    display: inline-block;
    margin-right:10px;
    margin-top:10px;
    cursor: pointer;
    background: #fff !important;
    border:1px solid #eee;
}
.pipeline-card-name{
    font-size:14px;
    color:#333333;
    padding: 15px;
    padding-bottom:5px;
}
.pipeline-card-desc{
    color:#666666;
    font-size:12px;
    margin-top:5px;
    padding:0px 15px;
    padding-bottom:10px;
}
.pipeline-card-stage{
    background-color:#F9F9F9;
    padding:5px 10px;
}
.pipeline-stage-name{
    font-size:12px;
    font-weight: bold;
    color:#555;
}
.right-opt{
    display: flex;
    align-items: center;
    flex-direction: row-reverse;
}
</style>
<i18n>
{
    "en": {
        "卡片视图": "Card view",
        "列表视图":"Table view",
        "创建Pipeline":"Create",
        "个Pipeline":"{0} items",
        "无执行记录":"no log",
        "设置属性":"Edit",
        "未设置阶段":"no stage",
        "名称":"Name",
        "最后执行":"Last run",
        "流水线":"Pipeline",
        "地址":"Address",
        "创建人":"Created by",
        "备注":"Remark",
        "设置":"Edit",
        "暂无数据":"No Data",
        "未分组":"unclassified",
        "您没有权限执行此pipeline":"You do not have permission to execute this pipeline"
    },
    "zh_CN": {
        "卡片视图": "卡片视图",
        "列表视图":"列表视图",
        "创建Pipeline":"创建Pipeline",
        "个Pipeline":"{0}个Pipeline",
        "无执行记录":"无执行记录",
        "设置属性":"设置属性",
        "未设置阶段":"未设置阶段",
        "名称":"名称",
        "最后执行":"最后执行",
        "流水线":"流水线",
        "地址":"地址",
        "创建人":"创建人",
        "备注":"备注",
        "设置":"设置",
        "暂无数据":"暂无数据",
        "未分组":"未分组",
        "您没有权限执行此pipeline":"您没有权限执行此pipeline"
    }
}
</i18n>
<template>
<div v-if="list" style="padding:20px">
     <Row>
        <Col span="6">&nbsp;</Col>
        <Col span="12">
             <div  class="table-info">
                <span class="table-count">{{$t('个Pipeline',[list.length])}}</span>
            </div>
        </Col>
        <Col span="6" class="right-opt">
             <IconButton v-if="viewType=='table'" @click="changeViewType('card')" :title="$t('卡片视图')"></IconButton>
             <IconButton v-if="viewType=='card'" @click="changeViewType('table')" :title="$t('列表视图')"></IconButton>
            <IconButton v-if="prjPerm('devops_edit_machine')"  icon="md-add" @click="showCreatePipelineDialog()" :title="$t('创建Pipeline')"></IconButton>
        </Col>
    </Row>
    
    <div v-if="viewType=='card'" class="card-box">
            <template v-for="group in groupList">
            <Divider :key="'g'+group.name" orientation="left">
                {{group.name}} <span class="grp-cnt">{{group.list.length}}</span>
            </Divider>
           
            <Card class="no-padding-card  pipeline-card" v-for="item in group.list" :key="item.id" 
                    @click.native="showPipelineDialog(item)">
                      <div class="pipeline-card-name text-no-wrap"><Icon v-if="item.enableRole" type="ios-lock-outline" />{{item.name}}</div>
                      <div class="pipeline-card-desc text-no-wrap">
                        <template v-if="item.runLog.id==0">{{$t('无执行记录')}}</template>
                        <template v-if="item.runLog.id>0">
                           <DataDictLabel style="margin-right:10px" type="ProjectPipelineRunLog.status" :value="item.runLog.status"></DataDictLabel>
                          <span style="margin-left:5px">{{item.runLog.createTime|fmtDeltaTime}}</span>
                          {{item.lastRunAccountName}}
                        </template>
                      </div>
                      <div class="pipeline-card-stage text-no-wrap">
                          <template v-for="(stage,idx) in item.pipelineDefine.stages">
                            <span v-if="item.pipelineDefine" class="pipeline-stage-name" 
                             :key="item.id+'_'+stage.name">
                              {{stage.name}} <Icon v-if="idx<item.pipelineDefine.stages.length-1" type="md-arrow-round-forward" />
                            </span>
                             </template>
                             
                            <span class="pipeline-stage-name" v-if="item.pipelineDefine==null||item.pipelineDefine.stages==null||item.pipelineDefine.stages.length==0">
                                {{$t('未设置阶段')}}
                            </span>
                      </div>
                      <div class="card-opt">
                          <IconButton  v-if="prjPerm('devops_edit_pipeline')" 
                            @click.stop="showCreatePipelineDialog(item)"
                             :tips="$t('设置属性')" icon="ios-settings-outline"></IconButton>
                      </div>
                  </Card>

        </template>
        <div class="nodata" v-if="list.length==0">
            {{$t('暂无数据')}}
        </div>
    </div>

    <div v-if="viewType=='table'" class="table-box">
        <table class="table-content "  style="table-layout:fixed">
                <thead>
                    <tr>
                        <th>{{$t('名称')}}</th>
                        <th style="width:300px;">{{$t('最后执行')}}</th>
                        <th style="width:300px;">{{$t('流水线')}}</th>
                        <th style="width:100px">{{$t('创建人')}}</th>
                        <th style="width:80px">{{$t('设置')}}</th>
                    </tr>     
                  </thead>
                    <tbody>
                        <template v-for="group in groupList">
                        <tr :key="'grp_'+group.name" class="grp-tr">
                            <td colspan="5">{{group.name}} <span class="grp-cnt">{{group.list.length}}</span></td>
                        </tr>
                        <tr @click="showPipelineDialog(item)" v-for="item in group.list" :key="'prj_'+item.id" class="table-row clickable">
                            <td class="text-no-wrap">
                                {{item.name}}<Icon v-if="item.enableRole" type="ios-lock-outline" />
                            </td>
                            <td>
                                <template v-if="item.runLog.id==0">{{$t('无执行记录')}}</template>
                                <template v-if="item.runLog.id>0">
                                <DataDictLabel style="margin-right:10px" type="ProjectPipelineRunLog.status" :value="item.runLog.status"></DataDictLabel>
                                <span style="margin-left:5px">{{item.runLog.createTime|fmtDeltaTime}}</span>
                                {{item.lastRunAccountName}}
                                </template>

                            </td>
                            <td>
                               <div class="text-no-wrap">
                                   <template v-for="(stage,idx) in item.pipelineDefine.stages">
                                        <span v-if="item.pipelineDefine" class="pipeline-stage-name" 
                                        :key="item.id+'_'+stage.name">
                                        {{stage.name}} <Icon v-if="idx<item.pipelineDefine.stages.length-1" type="md-arrow-round-forward" />
                                        </span>
                                    </template>

                                    <span class="pipeline-stage-name" v-if="item.pipelineDefine==null||item.pipelineDefine.stages==null||item.pipelineDefine.stages.length==0">
                                        {{$t('未设置阶段')}}
                                    </span>
                                </div>
                            </td>
                            <td>{{item.createAccountName}}</td>
                             <td>
                               <IconButton  v-if="prjPerm('devops_edit_pipeline')" 
                                    @click.stop="showCreatePipelineDialog(item)"
                                    :tips="$t('设置属性')" icon="ios-settings-outline"></IconButton>
                            </td>

                        </tr>
                        </template>
                    </tbody>
        </table>
         <div v-if="list.length==0" class="table-nodata">
            <div>{{$t('暂无数据')}}</div>
        </div>

    </div>
</div>
</template>

<script>
export default {
    mixins: [componentMixin],
    data(){
        return {
            viewType:'card',
            list:[]
        }
    },  
    computed:{
        groupList(){
            var list=[];
            var groupMap={};
            for(var i=0;i<this.list.length;i++){
                var t=this.list[i];
                if(t.group==null||t.group==''){
                    t.group=this.$t("未分组");
                }
                if(groupMap[t.group]==null){
                    groupMap[t.group]={
                        name:t.group,
                        list:[]
                    }
                    list.push(groupMap[t.group])
                }
                groupMap[t.group].list.push(t);
            }
            return list;
        }
    },
    mounted(){
        var t=app.loadObject('DevOps.pipeline.viewType');
        if(t!=null){
            this.viewType=t;
        }
        this.loadData();
    },
    methods:{
        loadData(){
            var query={
                projectId:app.projectId
            }
            app.invoke("BizAction.getProjectPipelineInfoList",[app.token,query],list => {
                this.list=list;
            });
        },
        changeViewType(t){
            this.viewType=t;
            app.saveObject('DevOps.pipeline.viewType',t)
        },
        showPipelineDialog(item){
            app.showDialog(PipelineRunDialog,{
                id:item.id
            })
        },
        showCreatePipelineDialog(item){
            app.showDialog(PipelineEditDialog,{
                projectId:app.projectId,
                id:item==null?null:item.id
            })
        }
    }
}
</script>