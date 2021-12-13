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
    .log-item{
        position: relative;
        margin-top:20px;
    }
    .log-item-right{
        display: flex;
        width:100%;
        align-items: center;
    }
    .log-item-name{
        font-size:12px;
        color:#333;
    }
    .log-item-time{
        font-size:12px;
        color:#999;
        padding-right:10px;
    }
    .nodata{
       padding:60px;
       font-size:20px;
       color:#999;
       text-align: center;
   }
   .log-project-name{
       font-size:20px;
       font-weight: bold;
       padding:10px 0;
       margin-top:15px;
   }
</style>
<i18n>
{
    "en": {
        "暂无数据": "No Data"
    },
    "zh_CN": {
        "暂无数据": "暂无数据"
    }
}
</i18n>
<template>
 <div class="page">
    <div class="page-wrap">
      <div class="page-content" style="margin-bottom:20px;padding-bottom:30px">
    <div  class="table-info">
        <IconButton :size="15" :disabled="pageQuery.pageIndex==1" @click="changePage(-1)" icon="md-arrow-round-back"></IconButton>
        <span class="table-count">{{pageQuery.pageIndex}}/{{pageQuery.totalPage}}</span>
        <IconButton :size="15" :disabled="pageQuery.pageIndex==pageQuery.totalPage"  @click="changePage(1)"  icon="md-arrow-round-forward"></IconButton>
    </div>  
    <div class="table-box">
        <div v-for="group in list" :key="'log_'+group.name">
            <div class="log-project-name">{{group.name}}</div>
            <div class="log-item" v-for="item in group.list" :key="'log'+item.id" >
                <div class="log-item-right">
                    <div style="width:200px">
                        <span class="log-item-time">{{item.createTime|fmtDeltaTime}}</span>
                        <AvatarImage size="small" :name="item.createAccountName" :value="item.createAccountImageId" type="label"/>
                    </div>
                    <ProjectChangeLogItem style="flex:1" :item="item"></ProjectChangeLogItem>
                </div>
            </div>
                    
        </div>   

        
         <div class="nodata" v-if="list.length==0">
            {{$t('暂无数据')}}
        </div>

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
            list:[],
            pageQuery:{
                total:0,
                pageIndex:1,
                pageSize:100,
                totalPage:1, 
            },
        }
    },  
    methods:{
        pageLoad(){
            this.loadData();
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
            app.invoke('BizAction.getAllProjectChangeLogList',[app.token,this.pageQuery],(info)=>{
                info.list.map(item=>{
                    if(item.items!=''&&item.items!=null){
                        item.items=JSON.parse(item.items);
                    }else{
                        item.items={}
                    }
                })
                var groupList={};
                var allList=[];
                for(var i=0;i<info.list.length;i++){
                    var t=info.list[i];
                    t.dateString=formatDate(t.createTime);
                    if(groupList[t.dateString]==null){
                        groupList[t.dateString]={
                            name:t.dateString,
                            list:[]
                        };
                        allList.push(groupList[t.dateString]);
                    }
                    groupList[t.dateString].list.push(t);
                }
                
                this.list=allList;
                if(info.count==0){
                    this.pageQuery.totalPage=1;
                }else{
                    var t=Math.ceil(info.count/this.pageQuery.pageSize);
                    this.pageQuery.totalPage=t;
                }
            });
        }
    }
}
</script>