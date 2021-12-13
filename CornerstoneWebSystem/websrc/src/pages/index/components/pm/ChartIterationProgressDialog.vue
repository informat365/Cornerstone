<style scoped>
.page{
  min-height: calc(100vh - 51px);
  background-color: #F1F4F5;
  width: 100%;
  padding:20px;
  text-align: center;
}
.chart-content{
    width:1200px;
    display: inline-block;
    background-color: #fff;
    border:1px solid #eee;
    border-radius: 5px;
    text-align: left;
}
.chart-opt-bar{
    padding:20px;
    border-bottom: 1px solid #eee;
}
.chart-chart{
    padding:20px;
}
.rate{
    font-size:18px;
    font-weight: bold;
    margin-right:10px;
    color:#0097F7;
}
.delay{
    font-size:18px;
    font-weight: bold;
    margin-right:10px;
    color:#F84F84;
}
</style>
<i18n>
{
    "en": {
        "迭代进度": "Iteration progress",
        "项目名称":"Project",
        "迭代":"Iteration",
        "迭代状态":"Status",
        "迭代周期":"Period",
        "完成率":"Complete Rate",
        "延期率":"Delay Rate",
        "查询":"Query",
        "没有加入任何项目":"No Data",
        "导出":"Export"
    },
    "zh_CN": {
        "迭代进度": "迭代进度",
        "项目名称":"项目名称",
        "迭代":"迭代",
        "迭代状态":"迭代状态",
        "迭代周期":"迭代周期",
        "完成率":"完成率",
        "延期率":"延期率",
        "查询":"查询",
        "没有加入任何项目":"没有加入任何项目",
        "导出":"导出"
    }
}
</i18n>
<template>
 <Modal
        ref="dialog"  v-model="showDialog"
        :closable="true"
        :mask-closable="false"
        :loading="false" :title="$t('迭代进度')" class="fullscreen-modal"
         width="100%"
         :footer-hide="true">
    <div v-if="projectList" class="page">
        <div class="chart-content">
              <div class="chart-opt-bar">
                 <Form inline>

                     <FormItem>
                        <Button type="default" @click="exportData()">{{$t('导出')}}</Button>
                    </FormItem>

                 </Form>
            </div>

            <div class="chart-chart">
            <table  class="table-content table-color">
                <thead>
                    <tr>
                        <th>{{$t('项目名称')}}</th>
                        <th style="width:250px;">{{$t('迭代')}}</th>
                        <th style="width:100px;">{{$t('迭代状态')}}</th>
                        <th style="width:180px;">{{$t('迭代周期')}}</th>
                        <th style="width:150px;">{{$t('完成率')}}</th>
                        <th style="width:150px">{{$t('延期率')}}</th>
                    </tr>
                  </thead>
                    <tbody>
                        <tr  v-for="item in projectList" :key="'prj_'+item.projectName+item.iterationName" class="table-row">
                            <td  @click="goProject(item)">
                             {{item.projectName}}
                            </td>
                             <td >
                                {{item.iterationName}}
                            </td>
                            <td>
                                {{item.iterationStatus|dataDict('ProjectIteration.status')}}
                            </td>
                              <td>
                                {{item.startDate|fmtDate}}
                                    ~
                                {{item.endDate|fmtDate}}
                            </td>
                            <td>
                              <span class="rate"> {{percent(item.finishNum,item.totalNum)}}</span> {{item.finishNum}}/{{item.totalNum}}
                            </td>
                            <td>
                              <span  class="delay">  {{percent(item.delayNum,item.totalNum)}}</span> {{item.delayNum}}/{{item.totalNum}}
                            </td>



                        </tr>

                    </tbody>
        </table>
         <div class="table-nodata" v-if="projectList!=null&&projectList.length==0">
            {{$t('没有加入任何项目')}}
        </div>
            </div>
        </div>
    </div>
</Modal>
</template>

<script>

export default {
    mixins: [componentMixin],
    data(){
        return {
            projectList:null,
        }
    },

    methods:{
        pageLoad(){
            this.loadProjectList();
        },
        loadProjectList(){
            app.invoke("BizAction.getIterationProgressReportList",[app.token],list => {
                this.projectList=list;
            });
        },
         percent(v1,v2){
          if(v1<0){
              return '--';
          }
          if(v2==0){
              return "--";
          }
          return (v1*100/v2).toFixed(1)+"%";
        },
        exportData(){
            var query={
                token:app.token,
                query:this.formItem
            }
            var queryString=JSON.stringify(query);
            var encoded=Base64.encode(queryString);
            window.open('/p/main/export_iteration_progress_report?arg='+encodeURIComponent(encoded))
        },
        goProject(item){
            if(!!item.projectUuid){
                this.gotoProject(item.projectUuid,()=>{
                    this.showDialog = false;
                });
            }
        }
    }
}
</script>
