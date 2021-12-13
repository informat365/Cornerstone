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
        border: 1px solid #0097F7;
    }

    .it-box {
        background-color: #F7F7F7;
        border-top: 1px solid #E4E4E4;
        border-bottom: 1px solid #E4E4E4;
        padding: 30px;
    }

    .grp-cnt {
        font-weight: normal;
        color: #666;
        margin-left: 5px;
        font-size: 12px;
    }
</style>
<i18n>
    {
    "en": {
    "个里程碑": "{0} Iteration(s)",
    "卡片视图":"Card view",
    "列表视图":"Table view",
    "创建里程碑":"Create Landmark",
    "剩余": "Remaining {0} day(s)",
    "今天到期": "Today",
    "超期": "Overdue {0} day(s)",
    "总工期": "Total {0} day(s)",
    "名称":"Name",
    "状态":"Status",
    "开始时间":"Start",
    "结束时间":"End",
    "备注":"Remark",
    "暂无数据":"No Data"
    },
    "zh_CN": {
    "个里程碑": "{0}个里程碑",
    "卡片视图":"卡片视图",
    "列表视图":"列表视图",
    "创建里程碑":"创建里程碑",
    "剩余": "剩余{0}天",
    "今天到期": "今天到期",
    "超期": "超期{0}天",
    "总工期": "总工期{0}天",
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
                    <span class="table-count">{{$t('个里程碑',[list.length])}}</span>
                </div>
            </Col>
            <Col span="6" class="right-opt">
                <IconButton v-if="viewType=='table'" @click="changeViewType('card')" :title="$t('卡片视图')"></IconButton>
                <IconButton v-if="viewType=='card'" @click="changeViewType('table')" :title="$t('列表视图')"></IconButton>
                <IconButton
                    v-if="prjPerm('landmark_edit')&&!projectDisabled"
                    icon="md-add"
                    @click="showEditDialog()"
                    :title="$t('创建里程碑')"
                ></IconButton>
            </Col>
        </Row>

        <div v-if="viewType=='card'" class="card-box">
            <template v-for="group in groupList">
                <div :key="'div_'+group.status">
                    <Divider orientation="left">
                        <DataDictLabel type="Stage.status" :value="group.status"></DataDictLabel>
                        <span class="grp-cnt">{{group.list.length}}</span>
                    </Divider>
                    <Card

                        class="iteration-card"
                        v-for="item in group.list"
                        :key="item.id">
                        <Row>
                            <Col span="12" style="color:#333;font-size:14px;" @click.native="showDetail(item)">
                                <Label :value="item.name"></Label>
                            </Col>
                            <Col span="12" class="text-right">
                                <DataDictLabel type="Stage.status" :value="item.status"></DataDictLabel>
                                <IconButton style="padding-right: 0px;" v-if="prjPerm('landmark_edit')"
                                            @click="showEditDialog(item)"
                                            icon="ios-settings-outline"></IconButton>
                            </Col>
                        </Row>
                        <Row>
                            <Col span="24" >
                                {{item.startDate|fmtDate}} ~ {{item.endDate|fmtDate}}
                            </Col>
                        </Row>
                        <Row>
                            <Col span="24">
                                <template v-if="item.status!=3">
                                <span
                                    v-if="getLeftDays(item.endDate)>0"
                                    style="font-weight:bold">{{$t('剩余',[getLeftDays(item.endDate)])}}</span>
                                    <span v-if="getLeftDays(item.endDate)==0" style="font-weight:bold">{{$t('今天到期')}}</span>
                                    <span
                                        v-if="getLeftDays(item.endDate)<0"
                                        style="font-weight:bold;color:#FF355A"
                                    >{{$t('超期',[getLeftDays(item.endDate)*-1])}}</span>
                                    <span
                                        style="font-weight:bold;margin-left: 5px;">{{$t('总工期',[getWorkDays(item)])}}</span>

                                </template>
                            </Col>
                        </Row>
                    </Card>
                </div>
            </template>

            <div class="nodata" v-if="list.length==0">暂无数据</div>
        </div>

        <div v-if="viewType=='table'" class="table-box">
            <Table
                class="workflow-table" :columns="columns" :data="dataList">
                <template slot="name" slot-scope="{ row }">
                    <div style="cursor: pointer" @click="showDetail(row)">{{row.name}}</div>
                </template>
                <template slot="status" slot-scope="{ row }">
                    <DataDictLabel type="Stage.status" :value="row.status"></DataDictLabel>
                </template>
                <template slot="start" slot-scope="{ row }">
                    {{row.startDate|fmtDate}}
                </template>
                <template slot="end" slot-scope="{ row }">
                    {{row.endDate|fmtDate}}
                </template>
                <template slot="remark" slot-scope="{ row }">
                    <div v-if="row.status!=3">
                        <span v-if="getLeftDays(row.endDate)>0" style="font-weight:bold">{{$t('剩余',[getLeftDays(row.endDate)])}}</span>
                        <span v-if="getLeftDays(row.endDate)==0" style="font-weight:bold">{{$t('今天到期')}}</span>
                        <span v-if="getLeftDays(row.endDate)<0" style="font-weight:bold;color:#FF355A">{{$t('超期',[getLeftDays(row.endDate)*-1])}}</span>
                        <span style="font-weight:bold;margin-left: 5px;">{{$t('总工期',[getWorkDays(row)])}}</span>
                    </div>
                </template>
                <template slot="opt" slot-scope="{ row }">
                    <IconButton v-if="prjPerm('landmark_edit')" @click="showEditDialog(row)"
                                icon="ios-settings-outline"></IconButton>
                </template>
            </Table>
            <div v-if="list.length==0" class="table-nodata">
                <div>{{$t('暂无数据')}}</div>
            </div>
        </div>
    </div>
</template>

<script>

    export default {
        name: 'LandmarkListView',
        mixins: [componentMixin],
        data() {
            return {
                viewType: "table",
                list: [],
                groupList: [],
                associateList: [],
                dataList: [],
                columns: [
                    {
                        title: this.$t('名称'),
                        slot: 'name',
                    },
                    {
                        title: this.$t('状态'),
                        slot: 'status',
                    },
                    {
                        title: this.$t('开始时间'),
                        slot: 'start',
                    },
                    {
                        title: this.$t('结束时间'),
                        slot: 'end',
                    },
                    {
                        title: this.$t('备注'),
                        slot: 'remark',
                    },
                    {
                        title: '设置',
                        slot: 'opt',
                    }
                ],

            };
        },
        mounted() {
            var t = app.loadObject("Landmark.viewType");
            if (t != null) {
                this.viewType = t;
            }
            this.loadData();
        },
        methods: {
            pageMessage(type, content) {
                if (type === 'landmark.edit') {
                    this.loadData();
                }

                if (type === 'landmark.delete') {
                    this.loadData();
                }

            },
            loadData() {
                var query = {
                    pageIndex: 1,
                    pageSize: 1000,
                    projectId: app.projectId
                };
                app.invoke(
                    "BizAction.getLandmarkList",
                    [app.token, query],
                    info => {
                        this.list = info;
                        this.list.forEach(item => item.children = []);
                        if (this.list && this.list.length > 0) {
                            this.computeGroup();
                        }
                        this.dataList = this.list;
                    }
                );

            },
            computeGroup() {
                var groupMap = {};
                this.groupList = [];
                for (var i = 0; i < this.list.length; i++) {
                    var t = this.list[i];
                    if (groupMap[t.status] == null) {
                        groupMap[t.status] = {
                            status: t.status,
                            list: []
                        }
                        this.groupList.push(groupMap[t.status])
                    }
                    groupMap[t.status].list.push(t);
                }
                //

            },
            getLeftDays(value) {
                return window.getLeftDays(value);
            },
            getWorkDays(landmark) {
                if (landmark.startDate && landmark.endDate) {
                    return window.dateDiff(new Date(landmark.startDate), new Date(landmark.endDate)) + 1;
                }
            },
            getUsedDays(landmark) {
                var now = new Date();
                if (landmark.startDate.getTime() < now.getTime()) {
                    return 0;
                } else {
                    if (landmark.endDate.getTime() >= now.getTime()) {
                        return window.dateDiff(landmark.startDate, new Date());
                    }
                }
                return window.dateDiff(landmark.startDate, new Date());
            },
            changeViewType(t) {
                this.viewType = t;
                app.saveObject("Landmark.viewType", t);
            },
            showEditDialog(item) {
                app.showDialog(LandmarkEditDialog, {
                    projectId: app.projectId,
                    id: !!item ? item.id : null
                });
            },
            showDetail(item) {
                app.showDialog(LandmarkInfoDialog, {
                    id: item.id
                });
            }
        }
    };
</script>
