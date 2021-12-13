<style scoped>
   .menu-item-group{
        font-size:12px;
        color:#999;
        padding:8px 0px;
        margin-top:20px;
    }
    .filter-tag{
        font-size:13px;
        padding:3px 10px;
        background-color: #EEEEEE;
        color:#777;
        display: inline-block;
        margin-right: 10px;
        margin-top:10px;
        border-radius: 13px;
        cursor: pointer;
        user-select: none;
        max-width: 200px;
        text-overflow:ellipsis;
        overflow: hidden;
        white-space: nowrap;
    }
    .filter-count{
        font-size:12px;
        margin-left: 5px;
        font-weight: bold;
    }
    .filter-tag-select{
        background-color: #009CF1;
        color:#fff;
    }
    .search-input{
        border:none;
        border-bottom: 1px solid #eee;
        outline: none;
        width:100%;
    }
</style>
<i18n>
{
    "en": {
        "状态选择": "Status filter",
        "全部待汇报": "submit",
        "全部待审核": "review",
        "全部已审核":"reviewed",
        "待我汇报":"submit me",
        "待我审核":"review me",
        "周期选择":"Period filter",
        "日报":"day",
        "周报":"week",
        "月报":"month",
        "标题":"Title",
        "搜索标题":"search title",
        "关联项目":"Project",
        "搜索关联项目":"search project",
        "汇报人":"Submitter",
        "搜索汇报人":"search submitter"
    },
    "zh_CN": {
        "状态选择": "状态选择",
        "全部待汇报": "全部待汇报",
        "全部待审核": "全部待审核",
        "全部已审核":"全部已审核",
        "待我汇报":"待我汇报",
        "待我审核":"待我审核",
        "周期选择":"周期选择",
        "日报":"日报",
        "周报":"周报",
        "月报":"月报",
        "标题":"标题",
        "搜索标题":"搜索标题",
        "关联项目":"关联项目",
        "搜索关联项目":"搜索关联项目",
        "汇报人":"汇报人",
        "搜索汇报人":"搜索汇报人"
    }
}
</i18n>
<template>
<div>
      <div class="menu-item-group">{{$t('状态选择')}}</div>
        <div :class="{'filter-tag-select':filters.status==1}"  @click="setTypeFilter(1)" class="filter-tag">{{$t('全部待汇报')}}</div>
        <div :class="{'filter-tag-select':filters.status==2}"  @click="setTypeFilter(2)" class="filter-tag">{{$t('全部待审核')}}</div>
        <div :class="{'filter-tag-select':filters.status==3}"  @click="setTypeFilter(3)" class="filter-tag">{{$t('全部已审核')}}</div>

        <div :class="{'filter-tag-select':filters.myStatus==1}"  @click="setMyTypeFilter(1)" class="filter-tag">{{$t('待我汇报')}}</div>
        <div :class="{'filter-tag-select':filters.myStatus==2}"  @click="setMyTypeFilter(2)" class="filter-tag">{{$t('待我审核')}}</div>
        
    <div class="menu-item-group">{{$t('周期选择')}}</div>
        <div :class="{'filter-tag-select':filters.period==1}"  @click="setPeriodFilter(1)" class="filter-tag">{{$t('日报')}}</div>
        <div :class="{'filter-tag-select':filters.period==2}"  @click="setPeriodFilter(2)" class="filter-tag">{{$t('周报')}}</div>
        <div :class="{'filter-tag-select':filters.period==3}"  @click="setPeriodFilter(3)" class="filter-tag">{{$t('月报')}}</div>

    <div class="menu-item-group">{{$t('标题')}}</div>
    <div >
        <input class="search-input" :placeholder="$t('搜索标题')" type="text" @change="nameUpdated" v-model.trim="filters.name"/>
    </div>

    <div class="menu-item-group">{{$t('关联项目')}}</div>
    <div >
        <input class="search-input" :placeholder="$t('搜索关联项目')" type="text" @change="projectNameUpdated" v-model.trim="filters.projectName"/>
    </div>

     <div class="menu-item-group">{{$t('汇报人')}}</div>
    <div >
        <input class="search-input" :placeholder="$t('搜索汇报人')" type="text" @change="createAccountUpdated" v-model.trim="filters.submitterName"/>
    </div>

</div>
</template>

<script>
export default {
    mixins: [componentMixin],
    data(){
        return {
            filters:{
                pageIndex:1,
                pageSize:50,
                myStatus:null,
                status:null,
                period:null,
                name:null,
                submitterName:null,
                projectName:null
            },
            dataList:[],
        }
    },  
    mounted(){
        this.reloadData()
    },
    methods:{
        reloadData(pageIndex){
            if(pageIndex){
                this.filters.pageIndex=pageIndex;
            }
            this.loadData();
        },
        setTypeFilter(type){
            this.filters.myStatus=null;
            if(this.filters.status==type){
                this.filters.status=null;
            }else{
                 this.filters.status=type;
            }
            this.filters.pageIndex=1;
            this.loadData();
        },
        setMyTypeFilter(type){
            this.filters.status=null;
            if(this.filters.myStatus==type){
                this.filters.myStatus=null;
            }else{
                this.filters.myStatus=type;
            }
            this.filters.pageIndex=1;
            this.loadData();
        },
        setPeriodFilter(type){
            if(this.filters.period==type){
                this.filters.period=null;
            }else{
                 this.filters.period=type;
            }
            this.filters.pageIndex=1;
            this.loadData();
        },
        nameUpdated(){
            this.filters.pageIndex=1;
            this.loadData();
        },
        projectNameUpdated(){
            this.filters.pageIndex=1;
            this.loadData();
        },
        createAccountUpdated(){
            this.filters.pageIndex=1;
            this.loadData();
        },
        loadData(){
            //
            var options = Object.assign({}, this.filters);
            if(this.filters.myStatus==1){
                options.status=this.filters.myStatus;
                options.submitterName=app.account.name;
            }
            if(this.filters.myStatus==2){
                options.status=this.filters.myStatus;
                options.auditorId=app.account.id;
            }
            //
            app.invoke("BizAction.getReportList",[app.token,options],info => {
                info.pageIndex=this.filters.pageIndex;
                info.pageSize=this.filters.pageSize;
                if(info.count==0){
                    info.totalPage=1;
                }else{
                    var t=Math.ceil(info.count/this.filters.pageSize);
                    info.totalPage=t;
                }
                this.$emit('report-changed',info)
            });
        }
    }
}
</script>