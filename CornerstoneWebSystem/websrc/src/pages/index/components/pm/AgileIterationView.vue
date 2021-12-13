<style scoped>
.table-box {
    background-color: #fff;
    padding: 30px;
    padding-top: 10px;
    box-shadow: 0px 2px 10px 0px rgba(225, 225, 225, 0.5);
    border: 1px solid rgba(216, 216, 216, 1);
}

.table-info {
    color: #999;
    text-align: center;
}
.table-count {
    background-color: #e8e8e8;
    color: #666;
    padding: 3px 5px;
    border-radius: 3px;
}

.table-col-name {
    display: inline-block;
    vertical-align: middle;
}
.table-remark {
    color: #999;
    margin-top: 5px;
}
.nodata {
    padding: 60px;
    font-size: 20px;
    color: #999;
    text-align: center;
}
.table-info-row {
    display: flex;
    align-items: center;
}

.card-box {
    display: block;
}

.right-opt {
    display: flex;
    align-items: center;
    flex-direction: row-reverse;
}


.iteration-card{
    width:260px;
    text-align: left;
    display: inline-block;
    margin-right:10px;
    margin-top:10px;
    cursor: pointer;
    background: #fff !important;
    border:1px solid #eee;
}
.iteration-card-select{
  border:1px solid #0097F7;
}
.it-box{
  background-color:#F7F7F7;
  border-top:1px solid #E4E4E4;
  border-bottom:1px solid #E4E4E4;
  padding:30px;
}
.grp-cnt{
    font-weight: normal;
    color:#666;
    margin-left:5px;
    font-size:12px;
}
</style>
<i18n>
{
    "en": {
        "个迭代": "{0} Iteration(s)",
        "卡片视图":"Card view",
        "列表视图":"Table view",
        "创建迭代":"Create Iteration",
        "剩余": "Remaining {0} day(s)",
        "今天到期": "Today",
        "超期": "Overdue {0} day(s)",
        "名称":"Name",
        "状态":"Status",
        "开始时间":"Start",
        "结束时间":"End",
        "备注":"Remark",
        "暂无数据":"No Data"
    },
    "zh_CN": {
        "个迭代": "{0}个迭代",
        "卡片视图":"卡片视图",
        "列表视图":"列表视图",
        "创建迭代":"创建迭代",
        "剩余": "剩余{0}天",
        "今天到期": "今天到期",
        "超期": "超期{0}天",
        "名称":"名称",
        "状态":"状态",
        "开始时间":"开始时间",
        "结束时间":"结束时间",
        "备注":"备注",
        "暂无数据":"暂无数据"
    }
}
</i18n>
<template>
    <div v-if="list" style="padding:20px">
        <Row>
            <Col span="6">&nbsp;</Col>
            <Col span="12">
                <div class="table-info">
                    <span class="table-count">{{$t('个迭代',[list.length])}}</span>
                </div>
            </Col>
            <Col span="6" class="right-opt">
                <IconButton v-if="viewType=='table'" @click="changeViewType('card')" :title="$t('卡片视图')"></IconButton>
                <IconButton v-if="viewType=='card'" @click="changeViewType('table')" :title="$t('列表视图')"></IconButton>
                <IconButton
                    v-if="prjPerm('project_iteration_config')&&!projectDisabled"
                    icon="md-add"
                    @click="showEditIterationDialog()"
                    :title="$t('创建迭代')"
                ></IconButton>
            </Col>
        </Row>
        <!--<Row style="margin: 5px 0;">
            <Col span="4">
                <Input suffix="ios-search" placeholder="搜索迭代"  clearable v-model="search" style="width: 200px;" />
            </Col>
        </Row>-->

        <div v-if="viewType=='card'" class="card-box">
            <template v-for="group in groupFilterList">
                <div :key="'div_'+group.status">
                    <Divider  orientation="left">
                        <DataDictLabel type="ProjectIteration.status" :value="group.status"></DataDictLabel>
                        <span class="grp-cnt">{{group.list.length}}</span>
                    </Divider>
            <Card
                @click.native="showIteration(item)"
                class="iteration-card"
                v-for="item in group.list"
                :key="item.id">
                <Row>
                    <Col span="18" style="color:#333;font-size:14px;">
                        <Label :value="item.name"></Label>
                    </Col>
                    <Col span="6" class="text-right">
                        <DataDictLabel type="ProjectIteration.status" :value="item.status"></DataDictLabel>
                    </Col>
                </Row>
                <div style="color:#666666;font-size:12px;margin-top:5px">
                    {{item.startDate|fmtDate}} ~ {{item.endDate|fmtDate}}
                    <template
                        v-if="item.status!=3"
                    >
                        <span
                            v-if="getLeftDays(item.endDate)>0"
                            style="font-weight:bold"
                        >{{$t('剩余',[getLeftDays(item.endDate)])}}</span>
                        <span v-if="getLeftDays(item.endDate)==0" style="font-weight:bold">{{$t('今天到期')}}</span>
                        <span
                            v-if="getLeftDays(item.endDate)<0"
                            style="font-weight:bold;color:#FF355A"
                        >{{$t('超期',[getLeftDays(item.endDate)*-1])}}</span>
                    </template>
                </div>
            </Card>
            </div>
            </template>

            <div class="nodata" v-if="list.length==0">暂无数据</div>
        </div>

        <div v-if="viewType=='table'" class="table-box">
            <table class="table-content">
                <thead>
                    <tr>
                        <th>{{$t('名称')}}</th>
                        <th style="width:100px;">{{$t('状态')}}</th>
                        <th style="width:120px;">{{$t('开始时间')}}</th>
                        <th style="width:120px;">{{$t('结束时间')}}</th>
                        <th style="width:150px">{{$t('备注')}}</th>
                    </tr>
                </thead>
                <tbody>
                    <tr
                        @click="showIteration(item)"
                        v-for="item in filterList"
                        :key="'prj_'+item.id"
                        class="table-row clickable"
                    >
                        <td>
                            {{item.name}}
                        </td>
                        <td>
                            <DataDictLabel type="ProjectIteration.status" :value="item.status"></DataDictLabel>
                        </td>
                        <td>
                           {{item.startDate|fmtDate}}
                        </td>
                        <td>
                           {{item.endDate|fmtDate}}
                        </td>
                        <td>
                        <template v-if="item.status!=3">
                        <span
                            v-if="getLeftDays(item.endDate)>0"
                            style="font-weight:bold">{{$t('剩余',[getLeftDays(item.endDate)])}}</span>
                            <span v-if="getLeftDays(item.endDate)==0" style="font-weight:bold">{{$t('今天到期')}}</span>
                            <span
                                v-if="getLeftDays(item.endDate)<0"
                                style="font-weight:bold;color:#FF355A">{{$t('超期',[getLeftDays(item.endDate)*-1])}}</span>
                            </template>
                        </td>
                    </tr>
                </tbody>
            </table>
            <div v-if="list.length==0" class="table-nodata">
                <div>{{$t('暂无数据')}}</div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    mixins: [componentMixin],
    props:["filter"],
    data() {
        return {
            viewType: "card",
            list: [],
            filterList:[],
            groupList:[],
            groupFilterList:[]
        };
    },
    mounted() {
        var t = app.loadObject("Agile.iteration.viewType");
        if (t != null) {
            this.viewType = t;
        }
        this.loadData();
    },
    watch:{
        filter(val){
            let list =[];
            for (let i = 0; i < this.list.length; i++) {
                let item = this.list[i];
                if(!!val.name){
                    if(item.name.indexOf(val.name)===-1){
                        continue;
                    }
                }
                if(!!val.status&&val.status.length>0){
                    if(!val.status.contains(app.dataDictValue('ProjectIteration.status',item.status))){
                        continue;
                    }
                }

                list.push(this.list[i]);
            }
            this.filterList = list;


            //分组过滤
            var groupMap={};
            let groupList=[];
            for(var i=0;i<this.list.length;i++){
                var t=this.list[i];
                if(!!val.name){
                    if(t.name.indexOf(val.name)===-1){
                        continue;
                    }
                }
                if(!!val.status&&val.status.length>0){
                    if(!val.status.contains(app.dataDictValue('ProjectIteration.status',t.status))){
                        continue;
                    }
                }
                if(groupMap[t.status]==null){
                    groupMap[t.status]={
                        status:t.status,
                        list:[]
                    }
                    groupList.push(groupMap[t.status])
                }
                groupMap[t.status].list.push(t);
            }

            this.groupFilterList = groupList;

            console.log(val)
        }
    },
    methods: {

        loadData() {
            var query = {
                pageIndex: 1,
                pageSize: 1000,
                projectId: app.projectId
            };
            app.invoke(
                "BizAction.getProjectIterationList",
                [app.token, query],
                info => {
                    this.list = info.list;
                    this.filterList = info.list;
                    this.computeGroup();
                }
            );
        },
        computeGroup(){
            var groupMap={};
            this.groupList=[];
            for(var i=0;i<this.list.length;i++){
                var t=this.list[i];
                if(groupMap[t.status]==null){
                    groupMap[t.status]={
                        status:t.status,
                        list:[]
                    }
                    this.groupList.push(groupMap[t.status])
                }
                groupMap[t.status].list.push(t);
            }

            this.groupFilterList = this.groupList;

            let statusList = [];
            for (let k in groupMap){
               var name= app.dataDictValue('ProjectIteration.status',k)
                statusList.push({name:name,count:groupMap[k].list.length})
            }
            this.$emit("on-filter",statusList);
            //

        },
        getLeftDays(value){
            return window.getLeftDays(value);
        },
        changeViewType(t) {
            this.viewType = t;
            app.saveObject("Agile.iteration.viewType", t);
        },
        showEditIterationDialog(item) {
            app.showDialog(IterationEditDialog, {
                projectId: app.projectId,
                item: item
            });
        },
        showIteration(item) {
            app.showDialog(IterationInfoDialog, {
                id: item.id
            });
        }
    }
};
</script>
