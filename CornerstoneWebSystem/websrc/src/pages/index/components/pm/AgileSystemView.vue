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

.iteration-card {
    width: 260px;
    text-align: left;
    display: inline-block;
    margin-right: 10px;
    margin-top: 10px;
    cursor: pointer;
    background: #fff !important;
    border: 1px solid #eee;
}
.iteration-card-select {
    border: 1px solid #0097f7;
}
.it-box {
    background-color: #f7f7f7;
    border-top: 1px solid #e4e4e4;
    border-bottom: 1px solid #e4e4e4;
    padding: 30px;
}
</style>
<i18n>
{
    "en": {
        "个子系统": "{0} system(s)",
        "卡片视图":"Card view",
        "列表视图":"Table view",
        "创建子系统":"Create system",
        "编辑":"Edit",
        "名称":"Name",
        "状态":"Status",
        "备注":"Remark",
        "设置":"Edit",
        "暂无数据":"No Data"
    },
    "zh_CN": {
        "个子系统": "{0}个子系统",
        "卡片视图":"卡片视图",
        "列表视图":"列表视图",
        "创建子系统":"创建子系统",
        "编辑":"编辑",
        "名称":"名称",
        "状态":"状态",
        "备注":"备注",
        "设置":"设置",
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
                    <span class="table-count">{{$t('个子系统',[list.length])}}</span>
                </div>
            </Col>
            <Col span="6" class="right-opt">
                <IconButton v-if="viewType=='table'" @click="changeViewType('card')" :title="$t('卡片视图')"></IconButton>
                <IconButton v-if="viewType=='card'" @click="changeViewType('table')" :title="$t('列表视图')"></IconButton>
                <IconButton
                    v-if="prjPerm('project_sub_system_config')&&!projectDisabled"
                    icon="md-add"
                    @click="showEditSystemDialog()"
                    :title="$t('创建子系统')"
                ></IconButton>
            </Col>
        </Row>

        <div v-if="viewType=='card'" class="card-box">
            <Card class="iteration-card" v-for="item in filterList" :key="item.id">
                <Row>
                    <Col span="16" style="color:#333;font-size:14px;">
                        <Label :value="item.name"></Label>
                    </Col>
                    <Col span="8" class="text-right">
                        <DataDictLabel type="ProjectSubSystem.status" :value="item.status"></DataDictLabel>
                    </Col>
                </Row>
                <div
                    class="text-no-wrap"
                    style="color:#666666;font-size:12px;margin-top:5px"
                >{{item.description}}&nbsp;</div>
                <div class="card-opt">
                    <IconButton
                        v-if="prjPerm('project_sub_system_config')"
                        @click="showEditSystemDialog(item)"
                        :tips="$t('编辑')"
                        icon="ios-settings-outline"
                    ></IconButton>
                </div>
            </Card>

            <div class="nodata" v-if="list.length==0">{{$t('暂无数据')}}</div>
        </div>

        <div v-if="viewType=='table'" class="table-box">
            <table class="table-content">
                <thead>
                    <tr>
                        <th>{{$t('名称')}}</th>
                        <th style="width:100px;">{{$t('状态')}}</th>
                        <th style="width:250px">{{$t('备注')}}</th>
                        <th style="width:100px">{{$t('设置')}}</th>
                    </tr>
                </thead>
                <tbody>
                    <tr
                        v-for="item in filterList"
                        :key="'prj_'+item.id"
                        class="table-row">
                        <td>{{item.name}}</td>
                        <td>
                            <DataDictLabel type="ProjectSubSystem.status" :value="item.status"></DataDictLabel>
                        </td>
                        <td>
                           {{item.description}}
                        </td>
                        <td>
                            <IconButton
                                v-if="prjPerm('project_sub_system_config')"
                                @click="showEditSystemDialog(item)"
                                :tips="$t('编辑')"
                                icon="ios-settings-outline"
                            ></IconButton>
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
    props:['filter'],
    data() {
        return {
            viewType: "card",
            list: [],
            filterList:[]
        };
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
                    if(!val.status.contains(app.dataDictValue('ProjectSubSystem.status',item.status))){
                        continue;
                    }
                }

                list.push(this.list[i]);
            }
            this.filterList = list;
        }
    },
    mounted() {
        var t = app.loadObject("Agile.system.viewType");
        if (t != null) {
            this.viewType = t;
        }
        this.loadData();
    },
    methods: {
        loadData() {
            var query = {
                pageIndex: 1,
                pageSize: 1000,
                projectId: app.projectId
            };
            app.invoke(
                "BizAction.getProjectSubSystemList",
                [app.token, query],
                info => {
                    this.list = info.list;
                    this.filterList = info.list;


                    let statusList=[];
                    this.list.forEach(item=>{
                        let statusName = app.dataDictValue('ProjectSubSystem.status',item.status)
                        let contains = false;
                        for (let i = 0; i < statusList.length; i++) {
                            if(statusList[i].name===statusName){
                                contains = true;
                                statusList[i].count++;
                            }
                        }
                        if(!contains&&!!statusName){
                            statusList.push({name:statusName,count:1})
                        }
                    });
                    this.$emit("on-filter",statusList);
                }
            );
        },
        changeViewType(t) {
            this.viewType = t;
            app.saveObject("Agile.system.viewType", t);
        },
        showEditSystemDialog(item){
            app.showDialog(SystemEditDialog,{
                projectId:app.projectId,
                item:item
            })
        },
    }
};
</script>
