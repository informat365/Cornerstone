<style scoped lang="less">
    .opt-bar {
        background-color: #f1f4f5;
        margin-top: 0;
    }

    .opt-right {
        text-align: right;
    }

    .create-popup {
        position: fixed;
        width: 200px;
        background-color: #fff;
        box-shadow: 0 1px 6px rgba(0, 0, 0, .2);
        z-index: 999;
    }

    .create-popup-opt {
        position: absolute;
        top: 0;
        left: 0;
        width: 200px;
        padding-left: 20px;
        color: #999;
    }

    .nodata {
        font-size: 20px;
        color: #999;
        margin-top: 30px;
    }

    .wechat-label {
        display: inline-block;
        padding: .25em .6em;
        font-size: 12px;
        font-weight: 600;
        line-height: 1;
        text-align: center;
        white-space: nowrap;
        vertical-align: middle;
        border-radius: 1rem;
        color: #fff;
        background-color: #83db5e;
        margin-left: 5px;
    }

    .checkbox-col {
        width: 60px;
    }

    .operation-box {
        text-align: center;

        div {
            padding: 8px 0 !important;
            cursor: pointer;

            &:hover {
                background-color: #eee;
            }
        }
    }

    .vertical-center-modal {
        display: flex;
        align-items: center;
        justify-content: center;

        .ivu-modal {
            top: 0;
            min-height: 600px;
        }
    }

    .model-container {
        min-height: 500px;
    }
</style>

<i18n>
    {
    "en": {
    "用户名":"Username",
    "姓名":"Name",
    "导入":"Import",
    "角色":"Role",
    "状态":"Status",
    "查询":"Query",
    "创建成员":"Create User",
    "邀请成员":"Invite User",
    "部门":"Department",
    "加入日期":"Create time",
    "微信":"Wechat",
    "设置":"Settings",
    "批量":"Batch",
    "修改角色":"Update account roles",
    "调整部门":"Adjust department",
    "加入项目":"Join project",
    "移出项目":"Remove project",
    "冻结账号":"Freeze Account",
    "删除账号":"Delete Account",
    "强制改密":"Force update passwd alert",
    "请先选择要操作的用户账号":"Please select the accounts to operate firstly.",
    "所属部门":"Department",
    "清除所有":"Clear all",
    "清除":"Clear",
    "找不到匹配的数据":"No matching data found",
    "搜索部门":"Search department",
    "选择项目角色":"Please select the project roles"
    },
    "zh_CN": {
    "姓名":"姓名",
    "导入":"导入",
    "角色":"角色",
    "状态":"状态",
    "查询":"查询",
    "创建成员":"创建成员",
    "邀请成员":"邀请成员",
    "部门":"部门",
    "用户名":"用户名",
    "加入日期":"加入日期",
    "微信":"微信",
    "设置":"设置",
    "批量":"批量",
    "修改角色":"修改角色",
    "调整部门":"调整部门",
    "加入项目":"加入项目",
    "移出项目":"移出项目",
    "冻结账号":"冻结账号",
    "删除账号":"删除账号",
    "强制改密":"强制改密",
    "请先选择要操作的用户账号":"请先选择要操作的用户账号",
    "所属部门":"所属部门",
    "清除所有":"清除所有",
    "清除":"清除",
    "找不到匹配的数据":"找不到匹配的数据",
    "搜索部门":"搜索部门",
    "选择项目角色":"请选择项目角色"
    }
    }
</i18n>

<template>
    <div class="page">
        <Row class="opt-bar opt-bar-light">
            <Col span="18" class="opt-left">
                <Form inline @submit.native.prevent>
                    <FormItem>
                        <Input
                                style="width:150px"
                                @on-change="loadData(true)"
                                type="text"
                                v-model="formItem.dingtalkName"
                                :placeholder="$t('名称')"></Input>
                    </FormItem>
                    <FormItem>
                        <ExDatePicker @on-change="loadData(true)"
                                      type="date" style="width:150px"
                                      v-model="formItem.workDateStart"
                                      :placeholder="$t('工作日开始时间')"></ExDatePicker>
                    </FormItem>
                    <FormItem>
                        <ExDatePicker @on-change="loadData(true)"
                                      type="date" style="width:150px"
                                      v-model="formItem.workDateEnd"
                                      :day-end="true"
                                      :placeholder="$t('工作日结束时间')"></ExDatePicker>
                    </FormItem>

                    <FormItem>
                        <Button style="margin: 0 5px;" @click="loadData(true)" type="default">{{$t('查询')}}</Button>
                    </FormItem>
                </Form>

            </Col>
            <Col span="6" class="opt-right">
                <Form inline>
                    <FormItem>
<!--                        <Button v-if="company.version==2"  @click="syncAdAccount"  type="default" >-->
<!--                            {{$t('同步AD域账号')}}-->
<!--                        </Button>-->
<!--                        <Button v-if="company.version==2"  @click="syncDingtalkMember"  type="default" >-->
<!--                            {{$t('同步考勤人员')}}-->
<!--                        </Button>-->
                        <Button style="margin-left: 10px;" v-if="company.version==2&&!visible"  @click="openAsynAttendance"  type="default" >
                            {{$t('同步考勤')}}
                        </Button>
                        <ExDatePicker v-if="visible"
                                type="month" style="width:130px;margin-left: 10px;"
                                v-model="syncDate"
                                :day-end="true"
                                :placeholder="$t('选择月份')"></ExDatePicker>
                        <Button :disabled="!syncDate"  v-if="company.version==2&&visible"  @click="syncDingtalkAttendance" type="primary" >
                            {{$t('确认同步')}}
                        </Button>
<!--                        <Button   v-if="company.version==2&&visible"  @click="visible=!visible" type="default" >-->
<!--                            {{$t('取消同步')}}-->
<!--                        </Button>-->
                       <!-- <Poptip trigger="click"  v-model="visible"  placement="left-end">

                            <div slot="content">

                            </div>
                        </Poptip>-->

                    </FormItem>
                    <FormItem>

                    </FormItem>
                </Form>
            </Col>
        </Row>


        <div style="padding:20px">
            <BizTable :fixed="true" @change="loadData" :page="pageQuery" :value="tableData">
                <template slot="thead">
                    <tr>
                        <th style="width:100px">{{$t('姓名')}}</th>
                        <th style="width:150px">{{$t('考勤方式')}}</th>
                        <th style="width:150px">{{$t('上午考勤')}}</th>
                        <th style="width:150px">{{$t('上午打卡范围')}}</th>
                        <th style="width:150px">{{$t('下午考勤')}}</th>
                        <th style="width:150px">{{$t('下午打卡范围')}}</th>
                        <th style="width:100px">{{$t('工作日')}}</th>
                        <th style="width:100px">{{$t('工时')}}</th>
                        <th style="width:120px;">同步时间</th>
                    </tr>
                </template>
                <template slot="tbody">
                    <tr v-for="(item,idx) in tableData" :key="item.id" class="table-row">
                        <td>
                            {{item.dingtalkName}}
                        </td>
                        <td>
                            <DataDictLabel type="Attendance.source" :value="item.sourceType"/>
                        </td>
                        <td>
                            <DataDictLabel type="Attendance.result" :value="item.amTimeResult"/>
                            <div>{{item.amBaseTime|fmtDateTime}}</div>
                            <div>{{item.amUserTime|fmtDateTime}}</div>
                        </td>
                        <td>

                            <div v-if="item.amLocation&&item.amLocation<3">
                                <DataDictLabel type="Attendance.location" :value="item.amLocation"/>
                            </div>
                            <div v-else>--</div>
                        </td>
                        <td>
                            <DataDictLabel type="Attendance.result" :value="item.pmTimeResult"/>
                            <div>{{item.pmBaseTime|fmtDateTime}}</div>
                            <div>{{item.pmUserTime|fmtDateTime}}</div>
                        </td>
                        <td>
                            <DataDictLabel type="Attendance.location" :value="item.pmLocation"/>
                        </td>
                        <td>
                           {{item.workDate|fmtDate}}
                        </td>
                        <td>
                           {{calcTimeDelta(item)}}
                        </td>
                        <td>{{item.createTime|fmtDateTime}}</td>
<!--                        <td style="text-align:right">-->
<!--                            <Button @click="showEditDialog(item)" type="default">{{$t('设置')}}</Button>-->
<!--                        </td>-->
                    </tr>

                </template>
            </BizTable>
        </div>

    </div>
</template>

<script>
    import Treeselect from '@riophae/vue-treeselect';
    import '@riophae/vue-treeselect/dist/vue-treeselect.css';

    export default {
        mixins: [componentMixin],
        components: {Treeselect},
        data() {
            return {
                company: {},
                formItem: {
                    dingtalkId: null,
                    name: null,
                    workDateStart: null,
                    workDateEnd: null,
                },
                pageQuery: {
                    pageIndex: 1,
                    pageSize: 20,
                },
                tableData: [],
                syncDate:null,
                visible:false
            };
        },
        methods: {
            pageLoad() {
                this.company = app.company;
                this.loadData();
            },
            loadData(resetPage) {
                if (resetPage) {
                    this.pageQuery.pageIndex = 1;
                }
                var query = copyObject(this.pageQuery, this.formItem);
                app.invoke('BizAction.getDingtalkAttendanceList', [app.token, query], (info) => {
                    this.tableData = info.list;
                    this.pageQuery.total = info.count;
                });
            },
            calcTimeDelta(item){
                if(item.amUserTime&&item.pmUserTime){
                    var leave1 = item.amUserTime;
                    var leave2  = item.pmUserTime;
                    var delta= new Date(leave2).getTime()-new Date(leave1).getTime();

                    var hours=Math.floor(delta/(3600*1000))//计算出小时数
                    //计算相差分钟数
                    var leave2=delta%(3600*1000)    //计算小时数后剩余的毫秒数
                    var minutes=Math.floor(leave2/(60*1000))//计算相差分钟数

                    return `${hours}时${minutes}分`
                }
                return "";
            },
            syncDingtalkAttendance(){
                this.visible = false;
                app.showLoading("同步中...")
                app.invoke('BizAction.syncDingtalkAttendance', [app.token,this.syncDate], (info) => {
                    app.hideLoading();
                },error=>{
                    app.hideLoading();
                });
            },
            syncDingtalkMember(){
                app.showLoading("同步成员中...")
                app.invoke('BizAction.syncDingtalkMember', [app.token], (info) => {
                    app.hideLoading();
                },error=>{
                    app.hideLoading();
                });
            },
            openAsynAttendance(){
                this.visible = true;
            },
            syncAdAccount(){
                app.showLoading()
                app.invoke('BizAction.syncAdAccount', [app.token], (info) => {
                    app.hideLoading();
                },error=>{
                    app.hideLoading();
                });
            }
        },
    };
</script>
