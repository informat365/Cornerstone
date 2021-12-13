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
       font-size:17px;
       font-weight: bold;
       padding:10px 0;
       margin-top:15px;
   } 
   .opt-right{
       text-align: right;
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
<div>
    <div style="padding:20px;padding-top:7px">
        <div  class="table-info">
            <IconButton :size="15" :disabled="pageQuery.pageIndex==1" @click="changePage(-1)" icon="md-arrow-round-back"></IconButton>
            <span class="table-count">{{pageQuery.pageIndex}}/{{pageQuery.totalPage}}</span>
            <IconButton :size="15" :disabled="pageQuery.pageIndex==pageQuery.totalPage"  @click="changePage(1)"  icon="md-arrow-round-forward"></IconButton>
        </div>  
        <div style="text-align:center">
             <HeatMap :free="true" :event-list="allList"></HeatMap>
        </div>
        <div class="table-box">
            <div v-for="group in list" :key="'log_'+group.name">
                <div  class="log-project-name">{{group.name}}</div>
                <div class="log-item" v-for="item in group.list" :key="'log'+item.id" >
                    <div class="log-item-right">
                        <div style="width:200px">
                            <span class="log-item-time">{{item.createTime|fmtDeltaTime}}</span>
                            <AvatarImage size="small" :name="item.createAccountName" :value="item.createAccountImageId" type="label"/>
                        </div>
                        <ProjectChangeLogItem  style="flex:1" :item="item"></ProjectChangeLogItem>
                    </div>
                </div>   
            </div>   
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
            list:[],
            allList:[],
            formItem:{
                projectName:null,
                createAccountName:null,
                createTimeStart:null,
                createTimeEnd:null
            },
            pageQuery:{
                total:0,
                pageIndex:1,
                pageSize:200,
                totalPage:1,
            },
            projectList:[]
        }
    },  
    mounted(){
        this.loadProjectList();
        this.loadData();
    },
    methods:{
        loadProjectList(){
            app.invoke("BizAction.getMyProjectList",[app.token],list => {
                this.projectList=list;
            });
        },
        filterList(formItem){
            this.formItem=copyObject(this.formItem,formItem)
            this.loadData(true);
        },
        changePage(delta){ 
            var t=this.pageQuery.pageIndex+delta;
                if(t<=0||t>this.pageQuery.totalPage){
                return;
            }
            this.pageQuery.pageIndex=t;
            this.loadData();
        },
        loadData(resetPage){
            if(resetPage){
                this.pageQuery.pageIndex=1;
            }
            var query=copyObject(this.pageQuery,this.formItem)
            app.invoke('BizAction.getAllChangeLogList',[app.token,query],(info)=>{
                info.list.map(item=>{
                    if(item.items!=''&&item.items!=null){
                        item.items=JSON.parse(item.items);
                    }else{
                        item.items={}
                    }
                    if(item.items.author){
                        item.createAccountName=item.items.author;
                    }
                })
                this.allList=info.list;
                var groupList={};
                var allList=[];
                for(var i=0;i<info.list.length;i++){
                    var t=info.list[i];
                    if(groupList[t.projectName]==null){
                        groupList[t.projectName]={
                            name:t.projectName,
                            list:[]
                        };
                        allList.push(groupList[t.projectName]);
                    }
                    groupList[t.projectName].list.push(t);
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