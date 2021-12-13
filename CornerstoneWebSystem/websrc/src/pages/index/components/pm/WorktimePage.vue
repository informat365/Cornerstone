<style scoped>
    .opt-bar{
        background-color: #F1F4F5;
        margin-top: 0;
    }
    .opt-right{
        text-align: right;
    }
    .content-box{
        display:flex;
        align-items: center;
        padding:10px;
        flex-direction: column;
    }
    .table-box{
        background-color: #fff;
        padding:30px;
        padding-top:10px;
        box-shadow: 0px 2px 10px 0px rgba(225,225,225,0.5);
        border: 1px solid rgba(216,216,216,1);
        width:1200px;
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
   .nodata{
       padding:60px;
       font-size:20px;
       color:#999;
       text-align: center;
   }
</style>

<i18n>
{
    "en": {
        "项目名称": "Project Name",
        "开始时间": "Start",
        "结束时间": "End",
        "查询": "Query",
        "导出": "Export",
        "条数据": " items",
        "总工时": " total work time",
        "小时": "Hour",
        "事项": "Item",
        "时间": "Time",
        "工时": "Work Time",
        "内容": "Content",
        "创建人": "Creater",
        "暂无数据": "No Data"
    },
    "zh_CN": {
        "项目名称": "项目名称",
        "开始时间": "开始时间",
        "结束时间": "结束时间",
        "查询": "查询",
        "导出": "导出",
        "条数据": "条数据",
        "总工时": "总工时",
        "小时": "小时",
        "事项": "事项",
        "时间": "时间",
        "工时": "工时",
        "内容": "内容",
        "创建人": "创建人",
        "暂无数据": "暂无数据"
    }
}
</i18n>

<template>
    <div class="page">
        <Row class="opt-bar opt-bar-light">
          <Col span="18" class="opt-left">
               <Form inline @submit.native.prevent>
                    <FormItem >
                      <Input @on-change="loadData(true)" type="text" v-model="formItem.projectName" :placeholder="$t('项目名称')"></Input>
                    </FormItem>
                     <FormItem >
                      <InputNumber style="width:150px" @on-change="loadData(true)" type="text" v-model="formItem.taskId" placeholder="#ID"></InputNumber>
                    </FormItem>
                    <FormItem >
                       <ExDatePicker @on-change="loadData(true)" type="date" style="width:130px" v-model="formItem.startTimeStart" :placeholder="$t('开始时间')"></ExDatePicker>
                    </FormItem>
                    <FormItem >
                       <ExDatePicker @on-change="loadData(true)" type="date" style="width:130px" :day-end="true" v-model="formItem.startTimeEnd" :placeholder="$t('结束时间')"></ExDatePicker>
                    </FormItem>
                    <FormItem>
                        <Button @click="loadData(true)" type="default">{{$t('查询')}}</Button>
                    </FormItem>
             </Form>

          </Col>
          <Col span="6" class="opt-right">
            <Form inline>
               <FormItem>
                    <Button v-if="false" type="default">{{$t('导出')}}</Button>
              </FormItem>
             </Form>
            </Col>
       </Row>

    <div class="content-box">

    <div class="table-info">
        <IconButton :size="15" :disabled="pageQuery.pageIndex==1" @click="changePage(-1)" icon="md-arrow-round-back"></IconButton>
        <span class="table-count">{{pageQuery.pageIndex}}/{{pageQuery.totalPage}} ， {{pageQuery.total}}{{$t('条数据')}} ， {{$t('总工时')}} {{pageQuery.totalHours}} {{$t('小时')}}</span>
        <IconButton :size="15" :disabled="pageQuery.pageIndex==pageQuery.totalPage"  @click="changePage(1)"  icon="md-arrow-round-forward"></IconButton>


    </div>
    <div v-if="list" class="table-box">
         <table class="table-content" style="table-layout:fixed">
               <thead>
                    <tr>
                        <th style="width:250px;">{{$t('事项')}}</th>
                        <th style="width:120px;">{{$t('时间')}}</th>
                        <th style="width:100px;">{{$t('工时')}}</th>
                        <th >{{$t('内容')}}</th>
                        <th style="width:150px">{{$t('创建人')}}</th>
                    </tr>
                  </thead>

                <tbody>
                    <tr  v-for="item in list" :key="'todo'+item.id" class="table-row">
                        <td style="width:250px;">
                           <div style="max-width:230px" class="text-no-wrap">
                                {{item.projectName}}
                            </div>
                            <div style="color:#999;max-width:230px"  class="text-no-wrap">
                                #{{item.taskSerialNo}} {{item.taskName}}
                            </div>
                        </td>

                        <td style="width:110px;">
                           {{item.startTime|fmtDate}}
                        </td>

                        <td style="width:100px;">
                           {{item.hour}}{{$t('小时')}}
                        </td>
                         <td class="text-no-wrap">
                           {{item.content}}
                        </td>

                        <td style="width:150px">
                           {{item.createAccountName}}
                        </td>
                    </tr>
                </tbody>
        </table>
        <div class="nodata" v-if="list.length==0">
            {{$t('暂无数据')}}
        </div>
    </div>
    </div>
    </div>
</template>

<script>

export default {
    mixins: [componentMixin],
    data(){
        return {
            title:"WorktimePage",
            formItem:{
                projectName:null,
                taskId:null,
                startTimeStart:null,
                startTimeEnd:null,
            },
            pageQuery:{
                total:0,
                totalHours:0,
                pageIndex:1,
                pageSize:50,
                totalPage:1,
            },
            list:[]
        }
    },
    methods:{
        pageLoad(){
            this.loadData();
        },
        pageUpdate(){

        },
        pageMessage(type,value){

        },
        loadData(resetPage){
            if(resetPage){
                this.pageQuery.pageIndex=1;
            }
            var query=copyObject(this.pageQuery,this.formItem);
           /* if (query.startTimeEnd) {
              const startTimeEnd = new Date(query.startTimeEnd);
              if(!Number.isNaN(startTimeEnd.getTime())){
                startTimeEnd.setDate(startTimeEnd.getDate() + 1);
                query.startTimeEnd = startTimeEnd.getTime();
              }
            }*/
            app.invoke('BizAction.getTaskWorkTimeLogList',[app.token,query],(info)=>{
                this.list=info.list;
                this.pageQuery.totalHours=info.totalHours;
                this.pageQuery.total=info.count;
                if(info.count==0){
                    this.pageQuery.totalPage=1;
                }else{
                    var t=Math.ceil(info.count/this.pageQuery.pageSize);
                    this.pageQuery.totalPage=t;
                }
            })
        },
        changePage(delta){
            var t=this.pageQuery.pageIndex+delta;
                if(t<=0||t>this.pageQuery.totalPage){
                return;
            }
            this.pageQuery.pageIndex=t;
            this.loadData();
        }
  }
}
</script>
