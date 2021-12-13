<style scoped>
.page{
  min-height: calc(100vh - 51px);
  background-color: #F1F4F5;
  width: 100%;
  padding:20px;
  text-align: center;
}
.chart-content{
    display: inline-block;
    background-color: #fff;
    border:1px solid #eee;
    border-radius: 5px;
    text-align: left;
    width:100%;
}
.chart-opt-bar{
    padding:20px;
    border-bottom: 1px solid #eee;
}
.chart-chart{
    padding:20px;
    overflow: auto;
}
.nodata{
       padding:60px;
       font-size:20px;
       color:#999;
       text-align: center;
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
</style>
<i18n>
{
    "en": {
        "问卷调查数据": "问卷调查数据",
        "条数据":"{0} Items",
        "暂无数据":"No Data",
        "查询":"Query",
        "导出":"Export",
        "提交人":"Submitter",
        "提交时间":"Submit time",
        "编号":"#",
        "删除":"Delete"
    },
    "zh_CN": {
        "问卷调查数据": "问卷调查数据",
        "条数据":"{0}条数据",
        "暂无数据":"暂无数据",
        "查询":"查询",
        "导出":"导出",
        "提交人":"提交人",
        "提交时间":"提交时间",
        "编号":"编号",
        "删除":"删除"
    }
}
</i18n>
<template>
 <Modal
        ref="dialog"  v-model="showDialog" 
        :closable="true" 
        :mask-closable="false"
        :loading="false" :title="$t('问卷调查数据')" class="fullscreen-modal"
         width="100%" 
         :footer-hide="true">
    <div class="page">
        <div class="chart-content">
             <div class="chart-opt-bar">
                 <Row>
                    <Col span="12">
                        <Form inline>
                            <FormItem>
                                <Button  type="default" @click="loadData()">{{$t('查询')}}</Button>
                            </FormItem>

                            <FormItem>
                                <Button  type="default" @click="exportData()">{{$t('导出')}}</Button>
                            </FormItem>
                        </Form>
                    </Col>
                    <Col span="12" style="display:flex;justify-content: flex-end;">
                        <Form inline>
                            <FormItem>
                                <Button  type="error" @click="resetData()">{{$t('重置数据')}}</Button>
                            </FormItem>
                        </Form>
                    </Col>
                </Row>
                 
            </div>

            <div class="chart-chart">
                 <div class="table-info">
                    <IconButton :size="15" :disabled="pageQuery.pageIndex==1" @click="changePage(-1)" icon="md-arrow-round-back"></IconButton>
                    <span class="table-count">{{pageQuery.pageIndex}}/{{pageQuery.totalPage}} ， {{$t('条数据',[pageQuery.total])}}</span>
                    <IconButton :size="15" :disabled="pageQuery.pageIndex==pageQuery.totalPage"  @click="changePage(1)"  icon="md-arrow-round-forward"></IconButton>
       
                </div>   
          
            <div v-if=" list.length>0"  class="table-box">
                <table  class="line-table">
                    <thead>
                            <tr >
                                <th style="width:60px;">{{$t('编号')}}</th>
                                <th style="width:100px;">{{$t('提交人')}}</th>
                                <th style="width:150px">{{$t('提交时间')}}</th>
                                <th class="text-no-wrap" v-for="field in fieldList" :style="{width:getColumnWidth(field)}" :key='field.id'>{{field.name}}</th>
                                <th style="width:80px;">{{$t('编号')}}</th>
                            </tr>
                        </thead>
                    <tbody>
                        <tr v-for="(item,idx) in list" :key="item.id">
                                <td>{{idx+1}}</td>
                                <td>{{item.createAccountName}}</td>
                                <td>{{item.createTime|fmtDateTime}}</td>
                                <td v-for="field in fieldList" class="text-no-wrap" :key='field.id'>
                                    <SurveysDataLabel :field="field" :formData="item.formData" />
                                </td>
                                <td> <Button  type="error" size="small" @click="deleteDataItem(item)">{{$t('删除')}}</Button></td>
                        </tr>
                    </tbody>
                </table>
            </div>

         <div class="table-nodata" v-if="list.length==0">
            {{$t('暂无数据')}}
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
            fieldList:[],
            formItem:{
            },
            pageQuery:{
                total:0,
                pageIndex:1,
                pageSize:50,
                totalPage:1,
            },
            list:[],
            colWidth:{
                "text-single":150,
                "text-area":250,
                "text-number":100,
                "attachment":200,
                "date":150,
                "select":150,
                "radio":150,
                "checkbox":150
            },
        }
    },
    
    methods:{
        pageLoad(){
            this.loadDefine()
        },
        loadDefine(){
            app.invoke('SurveysAction.getSurveysFormDefineById',[app.token,this.args.id],(info)=>{
                if(info.fieldList){
                    var flist=JSON.parse(info.fieldList)
                    flist.forEach(item=>{
                        if(item.type.indexOf('static')==-1){
                            this.fieldList.push(item);
                        }
                    })
                }
                this.loadData(true)
            });
        },
        loadData(resetPage){
            if(resetPage){
                this.pageQuery.pageIndex=1;
            }
            this.formItem.surveysDefineId=this.args.id;
            var query=copyObject(this.pageQuery,this.formItem)
            //
            app.invoke('SurveysAction.getMyCreateSurveysInstanceList',[app.token,query],(info)=>{
                info.list.forEach(item=>{
                    if(item.formData){
                        item.formData=JSON.parse(item.formData);
                    }else{
                        item.formData={};
                    }
                })
                this.list=info.list;
                this.pageQuery.total=info.count;
                if(info.count==0){
                    this.pageQuery.totalPage=1;
                }else{
                    var t=Math.ceil(info.count/this.pageQuery.pageSize);
                    this.pageQuery.totalPage=t;
                }
            })   
        },
        getColumnWidth(field){
            var t=this.colWidth[field.type];
            if(t){
                return t+"px";
            }
            return 150+"px"
        },
        changePage(delta){ 
            var t=this.pageQuery.pageIndex+delta;
                if(t<=0||t>this.pageQuery.totalPage){
                return;
            }
            this.pageQuery.pageIndex=t;
            this.loadData();
        },
        resetData(){
            app.confirm('确定要重置此问卷调查吗？重置后所有提交的数据将会被删除，请先导出数据做好备份！',()=>{
                app.invoke("SurveysAction.resetSurveysInstances",[app.token,this.args.id],info => {
                    app.postMessage('surveys.edit');
                    this.loadData(true)
                });
            })
        },
        deleteDataItem(item){
            app.confirm('确定要删除此条数据吗？',()=>{
                app.invoke("SurveysAction.deleteSurveysInstance",[app.token,item.id],info => {
                    app.postMessage('surveys.edit');
                    this.loadData(true)
                });
            })
        },
        exportData(){
            var query={
                token:app.token,
                query:this.formItem
            }
            var queryString=JSON.stringify(query);
            var encoded=Base64.encode(queryString);
            window.open('/p/main/export_surveys_instance?arg='+encodeURIComponent(encoded))
        }
    }
}
</script>