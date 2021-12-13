<style scoped>
.page{
  min-height: 400px;
}

.opt-right{
  text-align: right;
}

.cal-content{
    display: flex;
    align-items: center;
    flex-direction: column;
    padding:10px;
}
.caltable{
    width:1000px;
    background-color: #fff;
    border-radius: 5px;
}
.day-span{
    color:#2b2b2b;
    font-weight: bold;
    display: inline-block;
    width:80px;
    font-size: 14px;
}
.percent-span{
    color:#009AF4;
    font-size:13px;
    display: inline-block;
    width:70px;
}
</style>
<i18n>
{
    "en": {
        "进度趋势": "Trending",
        "状态":"Status",
        "进度":"Progress",
        "时间":"Time",
        "备注":"Remark",
        "处理人":"Creater",
        "暂无数据":"No Data"
    },
    "zh_CN": {
        "进度趋势": "进度趋势",
        "状态":"状态",
        "进度":"进度",
        "时间":"时间",
        "备注":"备注",
        "处理人":"处理人",
        "暂无数据":"暂无数据"
    }
}
</i18n> 
<template>
    <div class="page">
         <Row class="opt-bar">
          <Col span="4" class="opt-left">
            <Form inline>
                <FormItem>
                    <span class="summary-label">{{$t('进度趋势')}}</span>
                </FormItem>
            </Form>
          </Col>
          <Col span="16" style="text-align:center">
                <Sparkline :value="progressList" :width="300"/>
          </Col>
          <Col span='4'>
            &nbsp;
          </Col>
       </Row>
       <div class="cal-content">
            <div  class="table-info">
                <IconButton :size="15" :disabled="pageQuery.pageIndex==1" @click="changePage(-1)" icon="md-arrow-round-back"></IconButton>
                <span class="table-count">{{pageQuery.pageIndex}}/{{pageQuery.totalPage}}</span>
                <IconButton :size="15" :disabled="pageQuery.pageIndex==pageQuery.totalPage"  @click="changePage(1)"  icon="md-arrow-round-forward"></IconButton>
            </div> 

           <div class="caltable">
            <table class="table-content ">
               <thead>
                    <tr>
                        <th style="width:100px;">{{$t('状态')}}</th>
                        <th style="width:100px;">{{$t('进度')}}</th>
                        <th style="width:150px">{{$t('时间')}}</th>
                        <th >{{$t('备注')}}</th>
                        <th style="width:120px;">{{$t('处理人')}}</th>
                    </tr>     
                  </thead>

                <tbody>
                    <tr v-for="item in list" :key="'todo'+item.id" class="table-row ">
                        <td>
                             <ProjectStatusLabel v-model="item.runStatus"></ProjectStatusLabel>
                        </td>   
                        <td>{{item.progress}}%</td>    
                        <td>{{item.createTime|fmtDate}}</td>  
                        <td>{{item.remark}}</td>  
                        <td>{{item.createAccountName}}</td> 
                    </tr>
                </tbody>
            </table>
            <div class="table-nodata" v-if="list.length==0">
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
            title:"ProjectReportPage",
            pageQuery:{
                total:0,
                pageIndex:1,
                pageSize:100,
                totalPage:1, 
            },
            progressList:[],
            list:[],
        }
    },
    methods:{
        pageLoad(){
            this.loadData();
            //
            
        },
        changePage(delta){ 
            var t=this.pageQuery.pageIndex+delta;
                if(t<=0||t>this.pageQuery.totalPage){
                return;
            }
            this.pageQuery.pageIndex=t;
            this.loadData();
        },
        loadData(){
            this.pageQuery.projectId=app.projectId;
            
            app.invoke('BizAction.getProjectRunLogList',[app.token,this.pageQuery],(info)=>{
                this.list=info.list;
                this.setupProgressList(info.list);
                if(info.count==0){
                    this.pageQuery.totalPage=1;
                }else{
                    var t=Math.ceil(info.count/this.pageQuery.pageSize);
                    this.pageQuery.totalPage=t;
                }
            });
        },
        setupProgressList(list){
            this.progressList=[];
            for(var i=list.length-1;i>=0;i--){
                this.progressList.push(list[i].progress)
            }
            
        }
    }
}
</script>