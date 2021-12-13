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
        width:100%;
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
   .download-link{
       cursor: pointer; 
       color:#0094FB;
   }
</style>
<i18n>
{
    "en": {
        "名称": "Name",
        "版本号":"Version",
        "查询":"Query",
        "条数据":"{0} items",
        "大小":"Size",
        "创建人":"Created by",
        "创建时间":"Time",
        "地址":"Link",
        "暂无数据":"No Data",
        "下载链接已复制到剪切板":"Download link has been copied to the clipboard"
    },
    "zh_CN": {
        "名称": "名称",
        "版本号":"版本号",
        "查询":"查询",
        "条数据":"{0}条数据",
        "大小":"大小",
        "创建人":"创建人",
        "创建时间":"创建时间",
        "地址":"地址",
        "暂无数据":"暂无数据",
        "下载链接已复制到剪切板":"下载链接已复制到剪切板"
    }
}
</i18n>
<template>
    <div>
        <Row class="opt-bar opt-bar-light">
          <Col span="18" class="opt-left">
               <Form inline @submit.native.prevent>
                    <FormItem >
                      <Input @on-change="loadData(true)" type="text" v-model="formItem.name" :placeholder="$t('名称')"></Input>
                    </FormItem>
                     <FormItem >
                      <Input @on-change="loadData(true)" type="text" v-model="formItem.version" :placeholder="$t('版本号')"></Input>
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
        <span class="table-count">{{pageQuery.pageIndex}}/{{pageQuery.totalPage}} ， {{$t('条数据',[pageQuery.total])}}</span>
        <IconButton :size="15" :disabled="pageQuery.pageIndex==pageQuery.totalPage"  @click="changePage(1)"  icon="md-arrow-round-forward"></IconButton>

        
    </div>   
    <div v-if="list" class="table-box">
         <table class="table-content " style="table-layout:fixed">
               <thead>
                    <tr>
                        <th >{{$t('名称')}}</th>
                        <th style="width:100px;">{{$t('版本号')}}</th>
                        <th style="width:120px;">{{$t('大小')}}</th>
                        <th style="width:100px">{{$t('创建人')}}</th>
                        <th style="width:150px">{{$t('创建时间')}}</th>
                        <th style="width:100px">{{$t('地址')}}</th>
                    </tr>     
                  </thead>

                <tbody>
                    <tr  v-for="item in list" :key="'todo'+item.id" class="table-row">
                        <td class="text-no-wrap">
                            <div>{{item.name}}</div>
                            <div style="color:#999">MD5HASH:{{item.md5}}</div>
                        </td>
                        <td>{{item.version}}</td>
                        <td>{{item.size|fmtBytes}}</td>
                        <td> {{item.createAccountName}}</td>  
                        <td> {{item.createTime|fmtDateTime}}</td>  
                        <td>
                             <span  class="download-link" v-clipboard:copy="getItemURL(item)" v-clipboard:success="copySuccess" >复制链接</span>
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
            title:"DevopsArtifactPage",
            formItem:{
                name:null,
                version:null
            },
            pageQuery:{
                total:0,
                pageIndex:1,
                pageSize:50,
                totalPage:1,
            },
            list:[]
        }
    },
    mounted(){
        this.loadData();
    },
    methods:{
       
        loadData(resetPage){
            if(resetPage){ 
                this.pageQuery.pageIndex=1;
            }
            var query=copyObject(this.pageQuery,this.formItem)
            query.projectId=app.projectId;
            app.invoke('BizAction.getProjectArtifactList',[app.token,query],(info)=>{
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
        changePage(delta){ 
            var t=this.pageQuery.pageIndex+delta;
                if(t<=0||t>this.pageQuery.totalPage){
                return;
            }
            this.pageQuery.pageIndex=t;
            this.loadData();
        },
        copySuccess(){
            app.toast(this.$t('下载链接已复制到剪切板'));
        },
        getItemURL(item){
            var serverAddr=app.getHost();
            return serverAddr+"/p/webapi/download_artifact/"+item.uuid;
        }
  }
}
</script>