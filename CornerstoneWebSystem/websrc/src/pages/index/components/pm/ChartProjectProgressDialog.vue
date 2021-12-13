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
        "项目进度": "Project stat",
        "项目名称":"Project",
        "项目":"Project",
        "分组":"Group",
        "完成率":"Completion rate",
        "延期率":"Delay rate",
        "没有加入任何项目":"No Data",
        "导出":"Export"
    },
    "zh_CN": {
        "项目进度": "项目进度",
        "项目名称":"项目名称",
        "项目":"项目",
        "分组":"分组",
        "完成率":"完成率",
        "延期率":"延期率",
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
        :loading="false" :title="$t('项目进度')" class="fullscreen-modal"
         width="100%"
         :footer-hide="true">
    <div  v-if="projectList" class="page">
        <div class="chart-content">
             <div class="chart-opt-bar">
                 <Form inline>
                     <FormItem>
                         <Select v-model="formItem.group" :placeholder="$t('分组')"
                                 clearable
                                 filterable
                                 @on-change="loadProjectList"
                                 @on-clear="loadProjectList"
                                 style="width:200px">
                             <Option v-for="item in groupList" :value="item" :key="'group'+item">{{ item }}</Option>
                         </Select>
                     </FormItem>
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
                        <th style="width:200px;">{{$t('完成率')}}</th>
                        <th style="width:200px">{{$t('延期率')}}</th>
                    </tr>
                  </thead>
                    <tbody>
                        <tr  v-for="item in projectList" :key="'prj_'+item.projectName" class="table-row">
                            <td @click="goProject(item)">
                             {{item.projectName}}
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
            formItem: {
                group: null
            },
            projectList:null,
            groupList:[],
        }
    },

    methods:{
        pageLoad(){
            this.loadProjectList();
            this.loadProjectGroupList();
        },
        loadProjectList(){
            app.invoke("BizAction.getProjectProgressReportList",[app.token,this.formItem],list => {
                this.projectList=list;
            });
        },
        loadProjectGroupList(){
            app.invoke("BizAction.getMyRunningProjectGroupList",[app.token],list => {
                this.groupList=list;
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
            window.open('/p/main/export_project_progress_report?arg='+encodeURIComponent(encoded))
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
