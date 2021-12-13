<style scoped>
   .table-box{
        background-color: #fff;
        padding:15px 30px;
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
   .section-bar{
       display: flex;
       align-items: center;
   }
   .section-item{
       font-weight: bold;
       color:#666;
       font-size:14px;
       margin-right:15px;
       cursor: pointer;
       position: relative;
       padding-bottom:5px;
   }
   .section-item-active{
       color:#009CF1;
   }
   .section-item-active::after {
    height: 4px;
    width: 50%;
    transform:translateX(50%);
    display: block;
    background: #009CF1;
    content: "";
    position: absolute;
    bottom: 0px;
    left: 0;
    z-index:10;
}
.serial-no{
    color:#666;
}

</style>
<i18n>
{
    "zh_CN":{
        "待填写":"待填写",
        "已填写":"已填写",
        "我创建的":"我创建的",
        "流程数据":"流程数据",
        "条数据":"{0}条数据",
        "发起流程":"发起流程",
        "状态":"状态",
        "标题":"标题",
        "创建人":"创建人",
        "创建时间":"创建时间",
        "创建人":"创建人",
        "创建时间":"创建时间",
        "提交人数":"提交人数",
        "最后提交时间":"最后提交时间",
        "暂无数据":"暂无数据",
        "设置":"设置",
        "查看数据":"查看数据",
        "创建问卷调查":"创建问卷调查",
        "删除":"删除",
        "复制":"复制"
    },
    "en": {
         "条数据":"{0}条数据"
    }   
}
</i18n>
<template>
<div style="padding:20px;">
     <Row>
        <Col span="8">
            <div class="section-bar">
                <div @click="selectFilter('todo')" class="section-item" :class="{'section-item-active':selectTab=='todo'}">{{$t('待填写')}}</div>
                <div @click="selectFilter('done')" class="section-item" :class="{'section-item-active':selectTab=='done'}">{{$t('已填写')}}</div>
                <div @click="selectFilter('create')" class="section-item" :class="{'section-item-active':selectTab=='create'}">{{$t('我创建的')}}</div>
            </div>
        </Col>
        <Col span="8">
            <div class="table-info">
                <IconButton :size="15" :disabled="pageQuery.pageIndex==1" @click="changePage(-1)" icon="md-arrow-round-back"></IconButton>
                <span class="table-count">{{$t('条数据',[pageQuery.total])}} ，{{pageQuery.pageIndex}}/{{pageQuery.totalPage}}</span>
                <IconButton :size="15" :disabled="pageQuery.pageIndex==pageQuery.totalPage"  @click="changePage(1)"  icon="md-arrow-round-forward"></IconButton>
            </div> 
            &nbsp;
        </Col>
        <Col span="8" style="text-align:right">
            <IconButton v-if="perm('surveys_create')" @click="showCreateDialog" :title="$t('创建问卷调查')" icon="md-add"></IconButton>
        </Col>
    </Row>
    
    <div   class="table-box">
        <table v-if="selectTab!='create'" class="table-content" style="table-layout:fixed">
               <thead>
                    <tr>
                        <th style="min-width:100px;">{{$t('标题')}}</th>
                        <th style="width:120px;">{{$t('状态')}}</th>
                        <th style="width:150px;">{{$t('创建人')}}</th>
                        <th style="width:120px">{{$t('创建时间')}}</th>
                    </tr>     
                </thead>
                <tbody>
                    <tr @click="viewItem(item)" v-for="item in list" :key="'todo'+item.id" class="table-row clickable">
                       <td  class="text-no-wrap">
                           {{item.name}}
                        </td>
                      
                        <td class="text-no-wrap">
                            <DataDictLabel  type="SurveysInstance.status" :value="item.instanceStatus"/>
                        </td> 

                        <td class="text-no-wrap">
                            <AvatarImage   size="small" :name="item.createAccountName" :value="item.createAccountImageId" type="label"></AvatarImage>
                       
                        </td> 
                        <td >
                            {{item.createTime|fmtDateTime}}
                        </td>   
                        
                    </tr>
                </tbody>
        </table>

        <table v-if="selectTab=='create'" class="table-content" style="table-layout:fixed">
               <thead>
                    <tr>
                        <th style="min-width:100px;">{{$t('标题')}}</th>
                        <th style="width:90px;">{{$t('状态')}}</th>
                        <th style="width:90px;">{{$t('提交人数')}}</th>
                        <th style="width:120px;">{{$t('最后提交时间')}}</th>
                        <th style="width:150px;">{{$t('创建人')}}</th>
                        <th style="width:120px">{{$t('创建时间')}}</th>
                        <th style="width:250px;"></th>
                    </tr>     
                </thead>
                <tbody>
                    <tr @click="viewItem(item)" v-for="item in list" :key="'todo'+item.id" class="table-row ">
                       <td  class="text-no-wrap">
                           {{item.name}}
                        </td>
                      
                        <td class="text-no-wrap">
                            <DataDictLabel  type="Common.status" :value="item.status"/>
                        </td> 
                        <td>
                            {{item.submitCount}}
                        </td>
                        <td>
                            {{item.lastSubmitTime|fmtDateTime}}
                        </td>
                        <td class="text-no-wrap">
                            <AvatarImage   size="small" :name="item.createAccountName" :value="item.createAccountImageId" type="label"></AvatarImage>
                        </td> 
                        <td >
                            {{item.createTime|fmtDateTime}}
                        </td>   
                         <td style="text-align:right">
                              <Button size="small" style="margin-right:5px" @click="settingItem(item)" type="default">{{$t('设置')}}</Button>
                              <Button size="small" style="margin-right:5px" @click="copyItem(item)" type="default">{{$t('复制')}}</Button>
                              <Button size="small" style="margin-right:5px" @click="viewData(item)" type="default">{{$t('查看数据')}}</Button>
                              <Button size="small"  @click="deleteItem(item)" type="error">{{$t('删除')}}</Button>
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
            pageQuery:{
                total:0,
                pageIndex:1,
                pageSize:30,
                totalPage:1,
            },
            selectTab:'todo'
        }
    },  
    mounted(){
        this.loadData();
    },
    methods:{
        showCreateDialog(){
            app.showDialog(SurveysDefineEditDialog)
        },
        selectFilter(type){
            this.selectTab=type;
            this.pageIndex=1;
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
        reloadData(){
            this.pageQuery.pageIndex=1;
            this.loadData();
        },
        filterList(info){
            this.pageQuery.pageIndex=1;
            this.pageQuery.name=info.name;
            this.loadData();
        },
        loadData(){
            var query=copyObject(this.pageQuery,{})
            if(this.selectTab=='todo'){
                query.type=1;
            }
            if(this.selectTab=='done'){
                query.type=2;
            }
            if(this.selectTab=='create'){
                query.type=3;
            }
            var action="SurveysAction.getSurveysDefineList";
            app.invoke(action,[app.token,query],(info)=>{
                this.list=info.list;
                this.pageQuery.total=info.count;
                if(info.count==0){
                    this.pageQuery.totalPage=1;
                }else{
                    var t=Math.ceil(info.count/this.pageQuery.pageSize);
                    this.pageQuery.totalPage=t;
                }
            });
        },
        settingItem(item){
            window.open('/surveyseditor.html#/'+item.uuid+'/info')
        },
        viewData(item){
            app.showDialog(SurveysDataDialog,{
                id:item.id
            })
        },
        viewItem(item){
            if(this.selectTab=='create'){
                return;
            }
            app.showDialog(SurveysDialog,{
                uuid:item.uuid
            })
        },
        copyItem(item){
            app.confirm('确定要复制此问卷调查吗？',()=>{
                app.invoke("SurveysAction.copySurveysDefine",[app.token,item.id],info => {
                    app.postMessage('surveys.edit');
                });
            })
        },
        deleteItem(item){
            app.confirm('确定要删除此问卷调查吗？',()=>{
                app.invoke("SurveysAction.deleteSurveysDefine",[app.token,item.id],info => {
                    app.postMessage('surveys.edit');
                });
            })
        }
    }
}
</script> 