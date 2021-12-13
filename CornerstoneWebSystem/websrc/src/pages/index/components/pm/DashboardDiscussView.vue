<style scoped>
   .table-box{
        background-color: #fff;
        padding:30px;
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
   .nodata{
       padding:60px;
       font-size:20px;
       color:#999;
       text-align: center;
   }
   .discuss-item{
       display: flex;
       align-items: center;
       padding:12px 0;
       border-bottom:1px solid #eee;
   }
   .discuss-item:last-child{
       border-bottom: none;
   }
   .discuss-main{
       flex:1;
       padding-left:20px;
   }
   .discuss-title{
       font-size:14px;
       color:#535353;
       font-weight: 400;
   }
    .discuss-title:hover{
       text-decoration: underline;
       cursor: pointer;
   }
   .discuss-desc{
       color:#979797;
       font-size:12px;
       margin-top:5px;
   }
   .discuss-count{
       width:100px;
       color: #999;
   }
    .project-label{
        background-color: #EEEEEE;
        color:#555;
        font-size:12px;
        padding:2px 5px;
        text-align: center;
        display: inline-block;
        border-radius: 3px;
        margin-right: 5px;
        font-weight: bold;
        margin-left:5px;
    }
</style>
<i18n>
{
    "en": {
        "创建讨论": "Create Discuss",
        "发表于": "created",
        "最后回复": " last replied",
        "暂无数据": "No Data"
    },
    "zh_CN": {
        "创建讨论": "创建讨论",
        "发表于": "发表于",
        "最后回复": "最后回复",
        "暂无数据": "暂无数据"
    }
}
</i18n> 
<template>
<div style="padding:20px;">
    

     <Row>
        <Col span="3">&nbsp;</Col>
        <Col span="18">
            <div  class="table-info">
                <IconButton :size="15" :disabled="pageQuery.pageIndex==1" @click="changePage(-1)" icon="md-arrow-round-back"></IconButton>
                <span class="table-count">{{pageQuery.pageIndex}}/{{pageQuery.totalPage}}</span>
                <IconButton :size="15" :disabled="pageQuery.pageIndex==pageQuery.totalPage"  @click="changePage(1)"  icon="md-arrow-round-forward"></IconButton>
            </div> 
        </Col>
        <Col span="3" style="text-align:right">
            &nbsp;
            <IconButton v-if="perm('discuss_create')" @click="showCreateDialog" :title="$t('创建讨论')" icon="md-add"></IconButton>
        </Col>
    </Row>
    

    <div class="table-box">
       <div v-for="item in list" :key="item.id" class="discuss-item">
           <AvatarImage size="large" :name="item.createAccountName" :value="item.createAccountImageId" type="tips"></AvatarImage>
            <div class="discuss-main">
                <div @click="showInfo(item)" class="discuss-title text-no-wrap">{{item.name}}
                   <span v-if="item.projectName" class="project-label">{{item.projectName}}</span>
                </div>
                <div class="discuss-desc">
                {{item.createAccountName}} <span style="color:#999">{{$t('发表于')}} {{item.createTime|fmtDeltaTime}}</span>
                
                <template v-if="item.lastMessageAccountId>0">
                        <Icon type="md-arrow-round-back" style="margin-left:20px"/> 
                        {{item.lastMessageAccountName}} {{item.lastMessageTime|fmtDeltaTime}}<span style="color:#999">{{$t('最后回复')}} </span>
                </template>
                </div>
            </div>
            <div class="discuss-count">
                <Icon type="ios-chatbubbles" /> {{item.replyCount}}
            </div>
       </div>

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
            pageQuery:{
                total:0,
                pageIndex:1,
                pageSize:100,
                totalPage:1,
            },
        }
    },  
    mounted(){
        this.loadData();
    },
    methods:{
        showCreateDialog(){
            app.showDialog(DiscussEditDialog)
        },
        changePage(delta){ 
            var t=this.pageQuery.pageIndex+delta;
                if(t<=0||t>this.pageQuery.totalPage){
                return;
            }
            this.pageQuery.pageIndex=t;
            this.loadData();
        },
        reloadData(){
            this.pageQuery.pageIndex=1;
            this.loadData();
        },
        loadData(){
            app.invoke('BizAction.getDiscussList',[app.token,this.pageQuery],(info)=>{
                this.list=info.list;
                if(info.count==0){
                    this.pageQuery.totalPage=1;
                }else{
                    var t=Math.ceil(info.count/this.pageQuery.pageSize);
                    this.pageQuery.totalPage=t;
                }
            });
        },
        showInfo(item){
            app.showDialog(DiscussInfoDialog,{
                id:item.id
            })
        }
    }
}
</script>