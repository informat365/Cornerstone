<style scoped>
    .split-container{
        display: flex;
        flex-direction: row;
    }
    .split-table{
        flex:1;
        background-color: #fff;
        border-left:1px solid #D8D8D8;
        border-right:1px solid #D8D8D8;
        height: calc( 100vh - 95px);
        overflow-y: auto;
        overflow-x: hidden;
        min-width: 250px;
    }
    .split-table-inner{
        height: calc( 100vh - 140px);
        overflow-y: auto;
    }
    .split-view{
        width:750px;
        height: calc( 100vh - 95px);
        background-color: #fff;
        overflow: hidden;
    }
    .task-row{
        padding:10px 15px;
        border-top:1px solid #E9EEF5;
        cursor: pointer;
        border-left:4px solid #fff;
        transition: all 0.3s;
    }
    .task-row:first-child{
        border-top:none;
    }
    .task-row:hover {
        background-color: #ebf7ff
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
   .no-task{
       text-align: center;
       font-size:20px;
       color:#999;
       padding:40px;
       margin-top:100px;
   }
   .split-tab-bar{
       background-color: #fff;
       border-bottom: 1px solid #eee;
       padding:10px;
       height:43px;
   }
   .task-status-line{
       display: flex;
       align-items: center;
       flex-direction: row-reverse;
   }
   .task-row-select{
       background-color: #ebf7ff;
   }
   .view-center{
        text-align:center;
        display: flex;
        align-items: center;
        justify-content: center;
   }
   .category-line{
       margin-top:7px;
   }
</style>
<i18n>
{
    "en": {
        "条数据":"{0}/{1}，{2} items",
        "未分类":"none",
        "暂无数据":"No Data",
        "责任人升序":"Owner asc",
        "责任人降序":"Owner desc",
        "状态升序":"Status asc",
        "状态降序":"Status desc",
        "优先级升序":"Priority asc",
        "优先级降序":"Priority desc",
        "开始时间升序":"Start asc",
        "开始时间降序":"Start desc",
        "结束时间升序":"End asc",
        "结束时间降序":"End desc"
    },
    "zh_CN": {
        "条数据":"{0}/{1}，{2}条数据",
        "未分类":"未分类",
        "暂无数据":"暂无数据",
        "责任人升序":"责任人升序",
        "责任人降序":"责任人降序",
        "状态升序":"状态升序",
        "状态降序":"状态降序",
        "优先级升序":"优先级升序",
        "优先级降序":"优先级降序",
        "开始时间升序":"开始时间升序",
        "开始时间降序":"开始时间降序",
        "结束时间升序":"结束时间升序",
        "结束时间降序":"结束时间降序"
    }
}
</i18n>
<template>
    <div v-hotkey="keymap" class="split-container">
        <div class="split-table">
             <Row class="split-tab-bar">
                <Col span="3">&nbsp;</Col>
                <Col span="18" class="view-center">
                    <IconButton :size="15" :disabled="pageQuery.pageIndex==1" @click="changePage(-1)" icon="md-arrow-round-back"></IconButton>
                    <span class="table-count">{{$t("条数据",[pageQuery.pageIndex,pageQuery.totalPage,pageQuery.total])}}</span>
                    <IconButton :size="15" :disabled="pageQuery.pageIndex==pageQuery.totalPage"  @click="changePage(1)"  icon="md-arrow-round-forward"></IconButton>
                 </Col>
                <Col span="3" style="text-align:right;">

                     <Poptip transfer placement="bottom-end" v-model="sortPopuptipVisible">
                        <IconButton :size="15" icon="md-list"></IconButton>
                        <div slot="content">
                            <template  v-for="item in sortConfigList">
                                <PopupTipItem @click="setSortType(item.name,item.sort)" v-if="hasField(item.name)"
                                :name="item.title"
                                :selected="viewSetting.tableSort.sort==item.sort&&viewSetting.tableSort.name==item.name"
                                :key="item.id+'sort'" />
                            </template>
                        </div>
                    </Poptip>

                </Col>
            </Row>
            <div class="scrollbox split-table-inner">
                <div @click="showTaskInfo(item)" class="task-row"
                    :class="{'task-row-select':currentTaskUUID==item.uuid}"
                    :style="{borderLeftColor:item.priorityColor}" v-for="item in tableData" :key="item.id">
                    <Row>
                        <Col span="12" >
                            <TaskOwnerView  :value="item"/>
                        </Col>

                        <Col span="12" class="task-status-line">
                            <TaskStatus  v-if="hasField('statusName')" :label="item.statusName" :color="item.statusColor"></TaskStatus>
                            <OverdueLabel  v-if="hasField('endDate') && !item.isFinish&&item.endDate" style="margin-right:10px" :value="item.endDate"></OverdueLabel>
                        </Col>
                    </Row>
                    <div style="margin-top:7px;font-size:13px;font-weight:bold">
                        <TaskNameLabel
                                            :is-done="item.isFinish"
                                            :light="currentTaskUUID==item.uuid"
                                            :id="item.serialNo"
                                            :name="item.name" />
                    </div>

                    <div v-if="item.categoryIdList&&item.categoryIdList.length>0" class="category-line">
                        <CategoryLabel  :placeholder="$t('未分类')" :category-list="queryInfo.categoryList" :value="item.categoryIdList"></CategoryLabel>
                    </div>
                </div>
            </div>
        </div>
        <div class="split-view">
             <TaskView
                v-if="currentTaskUUID!=null"
                :task-id="currentTaskUUID"
                :show-top-bar="true"
                :show-type="'split'"
                 ></TaskView>
            <div class="no-task" v-if="currentTaskUUID==null" >
                {{$t('暂无数据')}}
            </div>
        </div>
    </div>
</template>

<script>
export default {
    mixins: [componentMixin],
    props:['queryInfo','queryForm','taskId'],
    data(){
        return {
            title:"TaskSplitView",
            tableData:[],
            taskUUID:null,
            currentTaskUUID:null,
            viewSetting:{
                tableSort:{
                    name:null,
                    sort:null,
                },
            },
            pageQuery:{
                total:0,
                pageIndex:1,
                pageSize:50,
                totalPage:1,
            },
            sortConfigList:[
                { id: 0, name: 'ownerAccountName', title: this.$t('责任人升序'), sort: 1 },
                { id: -1, name: 'ownerAccountName', title: this.$t('责任人降序'), sort: 2 },
                { id: 1, name: 'statusName', title: this.$t('状态升序'), sort: 1 },
                { id: 2, name: 'statusName', title: this.$t('状态降序'), sort: 2 },
                { id: 3, name: 'priorityName', title: this.$t('优先级升序'), sort: 1 },
                { id: 4, name: 'priorityName', title: this.$t('优先级降序'), sort: 2 },
                { id: 5, name: 'startDate', title: this.$t('开始时间升序'), sort: 1 },
                { id: 6, name: 'startDate', title: this.$t('开始时间降序'), sort: 2 },
                { id: 7, name: 'endDate', title: this.$t('结束时间升序'), sort: 1 },
                { id: 8, name: 'endDate', title: this.$t('结束时间降序'), sort: 2 },

            ],
            sortPopuptipVisible:false,
            projectFinishDisabled:false
        }
    },
    watch:{
        taskId:function(val){
            this.currentTaskUUID=val;
        },
    },
    computed:{
        keymap () {
            return {
                'up': this.openPriorTask,
                'down': this.openNextTask,
            }
        }
    },
    created() {
        this.loadViewSetting();
        // 当在其他视图切换后，需要把视图的排序内容加载过来
        this.checkViewSetting();
    },
    mounted(){
        this.currentTaskUUID=this.taskId;
        this.pageQuery.pageIndex=1;
    },
    methods:{
        checkViewSetting() {
            let hasChange = false;
            Object.keys(this.queryForm).filter(key => key.endsWith('Sort')).forEach(key => {
                if (key === 'customFieldsSort') {
                    return;
                }
                const sort = this.queryForm[key];
                if (sort > 0) {
                    hasChange = true;
                    this.viewSetting.tableSort.name = key.replace('Sort', '');
                    this.viewSetting.tableSort.sort = sort;
                }
                // 重置掉queryForm上已有排序,重新计算排序字段,查询时会重新设置queryForm的排序
                this.queryForm[key] = null;
            });
            // 如果从queryForm未找到需要增加的排序字段,则重置当前viewSetting排序设置
            if (!hasChange) {
                this.viewSetting.tableSort.name = null;
                this.viewSetting.tableSort.sort = null;
            }
            this.saveViewSetting();
        },
        pageMessage(type){
            if(type=='task.edit'){
                this.loadData();
            }
        },
        changePage(delta){
            var t=this.pageQuery.pageIndex+delta;
                if(t<=0||t>this.pageQuery.totalPage){
                return;
            }
            this.pageQuery.pageIndex=t;
            this.loadTableData();
        },
        setSortType(type,sort){
            this.sortPopuptipVisible=false;
            if(this.viewSetting.tableSort.name==type&&this.viewSetting.tableSort.sort==sort){
                this.viewSetting.tableSort.name=null;
                this.viewSetting.tableSort.sort=null;
            }else{
                this.viewSetting.tableSort.name=type;
                this.viewSetting.tableSort.sort=sort;
            }
            //设置状态到queryForm上
            this.$set(this.queryForm, type + 'Sort', this.viewSetting.tableSort.sort);
            this.saveViewSetting();
            this.loadData();
        },
        saveViewSetting(){
            var objectType=this.queryForm.objectType;
            var projectId=this.queryForm.projectId;
            app.saveObject('TaskPage.SplitView.viewSetting'+objectType+"."+projectId,this.viewSetting);
        },
        loadViewSetting(){
            var objectType=this.queryForm.objectType;
            var projectId=this.queryForm.projectId;
            var vc=app.loadObject('TaskPage.SplitView.viewSetting'+objectType+"."+projectId);
            if(vc!=null){
                if(vc.tableSort==null){
                    vc.tableSort={
                        name:null,
                        sort:null,
                    }
                }
                this.viewSetting=vc;
            }
        },
        hasField(name){
            if(this.queryInfo.fieldList){
                for(var i=0;i<this.queryInfo.fieldList.length;i++){
                    var f=this.queryInfo.fieldList[i];
                        if(f.field==name){
                            return true;
                        }
                    }
                }
            return false;
        },
        loadData(){
            this.loadTableData();
        },
        loadTableData(){
            this.loadViewSetting();
            this.queryForm.pageIndex=this.pageQuery.pageIndex;
            this.queryForm.pageSize=this.pageQuery.pageSize;
            var query=Object.assign({}, this.queryForm);
            if(this.viewSetting.tableSort.name!=null){
                this.$set(this.queryForm, this.viewSetting.tableSort.name + 'Sort', this.viewSetting.tableSort.sort);
                query[this.viewSetting.tableSort.name+"Sort"]=this.viewSetting.tableSort.sort;
            }
            this.projectFinishDisabled = this.isProjectDisabled(query.projectId);
            app.invoke( "BizAction.getTaskInfoList",[app.token,query],info => {
                this.tableData=info.list;
                //
                this.pageQuery.total=info.count;
                if(info.count==0){
                    this.pageQuery.totalPage=1;
                }else{
                    this.pageQuery.totalPage=Math.ceil(info.count/this.pageQuery.pageSize);
                }
            });

        },
        openPriorTask(){
            this.openDeltaTask(-1);
        },
        openNextTask(){
            this.openDeltaTask(1);
        },
        openDeltaTask(delta){
            if(this.currentTaskUUID==null){
                return;
            }
            if(app.taskEditing==true){
                return;
            }
            var showIndex=0;
            for(var i=0;i<this.tableData.length;i++){
                var t=this.tableData[i];
                if(t.uuid==this.currentTaskUUID){
                    showIndex=i;
                }
            }
            showIndex+=delta;
            if(showIndex<0||showIndex>=this.tableData.length){
                return;
            }
            this.showTaskInfo(this.tableData[showIndex]);
        },
        showTaskInfo(item){
            this.currentTaskUUID=item.uuid;
            app.loadPage('task'+this.queryForm.objectType+'?id='+item.uuid)
        },
  }
}
</script>
