<style scoped>
   .table-box{
        background-color: #fff;
        padding:30px;
        padding-top:10px;
        border-radius: 3px;
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
   .left-day{
       font-weight: bold;
       color:#17A7ED;
       margin-left:5px;
   }
   .left-overdue{
       color:#F84F84;
   }
   
   .table-col-name{
       display: inline-block;
       vertical-align: middle;
   }
    .nodata{
       padding:60px;
       font-size:20px;
       color:#999;
       text-align: center;
   }
   .group-tr{
       background-color: #F8F8F9;
       
       font-weight: bold;
   }
   .group-tr td{
       background-color: #F8F8F9;
       font-size:16px;
       padding:0;
   }
   .group-icon{
       cursor: pointer;
   }
   .group-icon-selected{
       color:#0094FB;
   }
</style>
<i18n>
{
    "en": {
        "个汇报模板":"{0} items",
        "新增汇报模板": "Create",
        "名称": "Name",
        "周期": "Period",
        "创建人":"Creater",
        "创建日期":"Create Time",
        "状态":"Status",
        "关联项目":"Project",
        "暂无数据":"No Data",
        "设置":"Edit",
        "复制":"Copy"
    },
    "zh_CN": {
        "个汇报模板":"{0}个汇报",
        "新增汇报模板": "新增汇报模板",
        "名称": "名称",
        "周期": "周期",
        "创建人":"创建人",
        "创建日期":"创建日期",
        "状态":"状态",
        "关联项目":"关联项目",
        "暂无数据":"暂无数据",
        "设置":"设置",
        "复制":"复制"
    }
}
</i18n>
<template>
<div v-if="list"  style="padding:20px;">
    
    <Row>
        <Col span="3">&nbsp;</Col>
        <Col span="18">
             <div  class="table-info">
                <IconButton :size="15" :disabled="query.pageIndex==1" @click="changePage(-1)" icon="md-arrow-round-back"></IconButton>
                <span class="table-count">{{$t('个汇报模板',[query.count])}}，{{query.pageIndex}}/{{query.totalPage}}</span>
                <IconButton :size="15" :disabled="query.pageIndex==query.totalPage"  @click="changePage(1)"  icon="md-arrow-round-forward"></IconButton>
            </div>
        </Col>
        <Col span="3" style="text-align:right">
            <IconButton v-if="perm('report_template_admin')" @click="showEditTemplateDialog"  :title="$t('新增汇报模板')"></IconButton>
        </Col>
    </Row>
     

    <div class="table-box">
        <table class="table-content ">
                <thead>
                    <tr>
                    <th>{{$t('名称')}}</th>
                    <th style="width:80px">{{$t('周期')}}</th> 
                    <th style="width:80px">{{$t('状态')}}</th> 
                    <th>{{$t('关联项目')}}</th> 
                    <th style="width:100px">{{$t('创建人')}}</th> 
                    <th style="width:130px;">{{$t('创建日期')}}</th>
                    <th style="width:180px;"></th>
                    </tr> 
                  </thead>

                    <tbody>
                      
                    <tr v-for="item in list" :key="item.id" class="table-row">
                    <td >{{item.name}}</td>
                    <td ><DataDictLabel type="ReportTemplate.period" :value="item.period"/></td>
                    <td ><DataDictLabel type="Common.status" :value="item.status"/></td>
                    <td >{{item.projectName}}</td>
                    <td>{{item.createAccountName}}</td> 
                    <td>{{item.createTime|fmtDateTime}}</td> 
                    <td style="text-align:right">
                        <Button style="margin-right:5px" @click="copyItem(item)" type="default">{{$t('复制')}}</Button>
                        <Button  @click="settingItem(item)" type="default">{{$t('设置')}}</Button>
                    </td> 
                </tr>
                      
                    </tbody>
        </table>
        

         <div class="nodata" v-if="list.length==0">
            {{$t('暂无数据')}}
        </div>

    </div>
</div>
</template>

<script>
export default {
    mixins: [componentMixin],
    data(){
        return {
            list:[],
            query:{
                pageIndex:1,
                totalPage:1,
                pageSize:20,
                count:0
            }
        }
    },  
    mounted(){
        this.loadData();
    },
    methods:{
        pageMessage(type){
            if(type=='report.template.edit'){
                this.loadData(true);
            }
        },
        changePage(delta){ 
            var t=this.query.pageIndex+delta;
            if(t<=0||t>this.query.totalPage){
                return;
            }
        },
        loadData(resetPage){
            if(resetPage){
                this.query.pageIndex=1;
            }
            app.invoke('BizAction.getReportTemplateList',[app.token,this.query],(info)=>{
                this.list=info.list;
                this.query.count=info.count;
            })   
        },
        showEditTemplateDialog(){
            app.showDialog(CompanyReportTemplateEditDialog)
        },
        settingItem(item){
            app.showDialog(CompanyReportTemplateEditDialog,{
              id:item.id,
            })
        },
        copyItem(item){
             app.showDialog(CompanyReportTemplateEditDialog,{
              copyId:item.id,
            })
        },
        showTemplate(){

        }
    }
}
</script>