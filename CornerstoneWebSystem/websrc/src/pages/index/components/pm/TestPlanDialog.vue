<style scoped>
    .main-bg {
        background-color: #fff;
        box-shadow: 0px 2px 10px 0px rgba(225, 225, 225, 0.5);
        border: 1px solid rgba(216, 216, 216, 1);
        border-radius: 3px;
        padding: 25px;
        height: calc(100vh - 80px);
        overflow: auto;
    }

    .splite-container {
        padding: 10px;
    }

    .info-label {
        font-size: 13px;
        font-weight: bold;
    }

    .test-plan-name {
        font-size: 15px;
        font-weight: bold;
        color: #333;
    }

    .test-case-name {
        font-size: 15px;
        font-weight: bold;
        color: #333;
    }

    .dialog-main-box {
        background-color: #F1F4F5;
        height: 100%;
        display: flex;
    }

    .run-result {
        display: inline-block;
        padding: .5em 1em;
        font-size: 14px;
        font-weight: 600;
        line-height: 1;
        text-align: center;
        white-space: nowrap;
        vertical-align: middle;
        border-radius: 1rem;
        background-color: transparent;
        border: 1px solid #aaa;
        color: #8897AA;
        margin-right: 10px;
        transition: all 0.3s;
        display: inline-block;
    }

    .run-result-1:hover {
        background: #2CD3C9;
        border-color: #2CD3C9;
        color: #fff;
    }

    .run-result-2:hover {
        background: #F84F84;
        border-color: #F84F84;
        color: #fff;
    }

    .run-result-3:hover {
        background: #DDDDDD;
        border-color: #DDDDDD;
        color: #333;
    }

    .run-result-1-select {
        background: #2CD3C9;
        border-color: #2CD3C9;
        color: #fff;
    }

    .run-result-2-select {
        background: #F84F84;
        border-color: #F84F84;
        color: #fff;
    }

    .run-result-3-select {
        background: #DDDDDD;
        border-color: #DDDDDD;
        color: #333;
    }

    .run-progress-bar {
        background-color: #aaa;
    }

    .progress-item {
        display: inline-block;
        height: 3px;
        float: left;
    }

    .progress-item-1 {
        background-color: gray;
    }

    .progress-item-2 {
        background-color: #2CD3C9;
    }

    .progress-item-3 {
        background-color: #F84F84;
    }

    .progress-item-4 {
        background-color: #DDDDDD;
    }

    .type-1 {
        border: 1px solid gray !important;
        color: gray !important;
    }

    .type-2 {
        border: 1px solid #2CD3C9 !important;
        color: #2CD3C9 !important;
    }

    .type-3 {
        border: 1px solid #F84F84 !important;
        color: #F84F84 !important;
    }

    .type-4 {
        border: 1px solid #DDDDDD !important;
        color: #333 !important;
    }

    .table-query-form {
        margin-top: 10px;
        border-bottom: 1px solid #eee;
        border-top: 1px solid #eee;
        background-color: #F7F7F7;
        padding: 25px;
        padding-top: 10px;
        padding-bottom: 10px;
    }

    .pass-count-label {
        font-size: 13px;
        font-weight: 600;
        border: 1px solid #aaa;
        color: #8897AA;
        margin-right: 10px;
        transition: all 0.3s;
        padding-left: 3px;
        padding-right: 3px;
        display: inline-block;
    }

    .no-testcase-select {
        text-align: center;
        font-size: 20px;
        font-weight: bold;
        color: #999;
        padding-top: 40px;
        padding-bottom: 100px;
    }

    .table-row-select {
        color: #17A7ED;
        font-weight: bold;
    }

    .sorter {
        color: #0094fb;
    }
</style>
<i18n>
    {
    "en": {
    "执行测试计划":"Run Test plan",
    "总用例数":"Total",
    "执行率":"Finish Rate",
    "未执行":"Not Start",
    "通过":"Pass",
    "不通过":"Fail",
    "阻塞":"Block",
    "名称":"Name",
    "状态":"Status",
    "批量操作":"Batch",
    "执行次数":"Run Times",
    "执行人":"Executer",
    "暂无数据":"No Data",
    "请点击测试用例开始执行":"Please click on the test case to start execution",
    "执行结果":"Result",
    "实际结果与备注":"Result and Remark",
    "跳过":"Skip",
    "执行":"Run",
    "用例详情":"Detail",
    "未设置详情":"none",
    "未设置":"none",
    "关联缺陷":"Add Bug Relation",
    "创建缺陷":"Add Bug",
    "关联对象":"Add Relation",
    "前置对象":"Pre-object",
    "后置对象":"Post-object",
    "执行记录":"History",
    "请选择要批量执行的用例":"Select use cases to be executed in batches",
    "执行成功，自动切换到下一条用例":"Successful execution, automatically switch to the next use case",
    "已全部执行完毕":"All Done",
    "批量执行成功":"Success"
    },
    "zh_CN": {
    "执行测试计划":"执行测试计划",
    "总用例数":"总用例数",
    "执行率":"执行率",
    "未执行":"未执行",
    "通过":"通过",
    "不通过":"不通过",
    "阻塞":"阻塞",
    "名称":"名称",
    "状态":"状态",
    "批量操作":"批量操作",
    "执行次数":"执行次数",
    "执行人":"执行人",
    "暂无数据":"暂无数据",
    "请点击测试用例开始执行":"请点击测试用例开始执行",
    "执行结果":"执行结果",
    "实际结果与备注":"实际结果与备注",
    "跳过":"跳过",
    "执行":"执行",
    "用例详情":"用例详情",
    "未设置详情":"未设置详情",
    "未设置":"未设置",
    "关联缺陷":"关联缺陷",
    "关联对象":"关联对象",
    "前置对象":"前置对象",
    "后置对象":"后置对象",
    "创建缺陷":"创建缺陷",
    "执行记录":"执行记录",
    "请选择要批量执行的用例":"请选择要批量执行的用例",
    "执行成功，自动切换到下一条用例":"执行成功，自动切换到下一条用例",
    "已全部执行完毕":"已全部执行完毕",
    "批量执行成功":"批量执行成功"
    }
    }
</i18n>
<template>
    <Modal class="nopadding-modal fullscreen-title-modal " ref="dialog" fullscreen v-model="showDialog"
           :title="$t('执行测试计划')"
           :closable="true"
           :footer-hide="true"
           :mask-closable="false">

        <Row class="dialog-main-box">
            <Split v-model="panelSplite">
                <div slot="left" class="splite-container">
                    <div class="main-bg scrollbox" style="padding:0">
                        <div style="padding:25px">
                            <Row>
                                <Col span="18" class="test-plan-name">
                                    <TaskNameLabel :id="task.serialNo" :name="task.name" :type="4"/>
                                </Col>
                                <Col span="6" style="text-align:right">
                                    <span style="color:#999">{{task.createAccountName}} {{task.createTime|fmtDeltaTime}}创建</span>
                                </Col>
                            </Row>
                            <Row style="margin-top:10px">
                                <Col span="24">
                        <span class="info-label">
                            <span style="margin-right:10px">{{$t('总用例数')}}：{{task.testCaseList.length}}</span>
                            <span style="margin-right:10px">{{$t('执行率')}}：{{runPercent}}%</span>
                            <span class="pass-count-label type-1">{{$t('未执行')}}：{{getStatusCount(1).count}}</span>
                            <span class="pass-count-label type-2">{{$t('通过')}}：{{getStatusCount(2).count}}</span>
                            <span class="pass-count-label type-3">{{$t('不通过')}}：{{getStatusCount(3).count}}</span>
                            <span class="pass-count-label type-4">{{$t('阻塞')}}：{{getStatusCount(4).count}}</span>

                        </span>
                                </Col>
                            </Row>
                            <div class="run-progress-bar" style="margin-top:7px">
                                <div class="progress-item progress-item-1"
                                     :style="{width:getStatusCount(1).percent+'%'}"></div>
                                <div class="progress-item progress-item-2"
                                     :style="{width:getStatusCount(2).percent+'%'}"></div>
                                <div class="progress-item progress-item-3"
                                     :style="{width:getStatusCount(3).percent+'%'}"></div>
                                <div class="progress-item progress-item-4"
                                     :style="{width:getStatusCount(4).percent+'%'}"></div>
                            </div>
                        </div>

                        <Form inline class="table-query-form">
                            <FormItem>
                                <Input style="width:70px" placeholder="ID" v-model.trim="queryItem.serialNo"></Input>
                            </FormItem>
                            <FormItem>
                                <Input :placeholder="$t('名称')" v-model.trim="queryItem.name"></Input>
                            </FormItem>
                            <FormItem style="min-width: 70px;border: 1px solid #ddd;">
                                <!--                <Input :placeholder="$t('分类')" v-model.trim="queryItem.categoryName"></Input>-->
<!--                                <Select v-model="queryItem.categoryName"  clearable style="width:100px">-->
<!--                                    <Option v-for="item in categoryList" :value="item.name" :key="item.id">{{ item.name-->
<!--                                        }}-->
<!--                                    </Option>-->
<!--                                </Select>-->
                                <CategorySelect
                                        :light="true"
                                        :category-list="categoryNodeList"
                                        v-model="queryItem.categoryIdList"
                                        placement="bottom-start"></CategorySelect>
                            </FormItem>
                            <FormItem>
                                <DataDictSelect clearable :placeholder="$t('状态')" type="TestPlanTestCase.status"
                                                v-model="queryItem.status"></DataDictSelect>
                            </FormItem>

                            <FormItem v-if="!projectFinishDisabled">
                                <Dropdown>
                                    <Button type="default">
                                        {{$t('批量操作')}}
                                        <Icon type="ios-arrow-down"></Icon>
                                    </Button>
                                    <DropdownMenu slot="list">
                                        <DropdownItem @click.native="batchRunTestCase(2)">通过</DropdownItem>
                                        <DropdownItem @click.native="batchRunTestCase(3)">不通过</DropdownItem>
                                        <DropdownItem @click.native="batchRunTestCase(4)">阻塞</DropdownItem>
                                    </DropdownMenu>
                                </Dropdown>
                            </FormItem>
                        </Form>

                        <table class="table-content" style="margin-top:10px;margin-bottom:30px">
                            <thead>
                            <tr>
                                <th style="width:40px">
                                    <Checkbox v-model="isSeletAll"></Checkbox>
                                </th>
                                <th style="width:80px;cursor: pointer" @click="sortByKey('serialNo')">ID
                                    <template v-if="sort.serialNo">
                                        <Icon type="md-arrow-round-up" v-if="sort.serialNo === 1" class="sorter" />
                                        <Icon type="md-arrow-round-down" v-if="sort.serialNo === 2" class="sorter" />
                                    </template>
                                </th>
                                <th style="cursor: pointer" @click="sortByKey('testCaseName')">{{$t('名称')}}
                                    <template v-if="sort.testCaseName">
                                        <Icon type="md-arrow-round-up" v-if="sort.testCaseName === 1" class="sorter" />
                                        <Icon type="md-arrow-round-down" v-if="sort.testCaseName === 2" class="sorter" />
                                    </template>
                                </th>
                                <th style="width:120px">{{$t('状态')}}</th>
                                <th style="width:120px">{{$t('分类')}}</th>
                                <th style="width:100px">{{$t('执行次数')}}</th>
                                <th style="width:120px">{{$t('执行人')}}</th>
                            </tr>
                            </thead>
                            <tr v-for="item in filteredList" :key="'tca'+item.id"
                                class="table-row clickable"
                                :class="{'table-row-select':currentTestPlanTeseCaseId==item.id}"
                                @click="showTestCaseInfo(item)">
                                <td>
                                    <Checkbox v-model="item.isSelect" @click.stop></Checkbox>
                                </td>
                                <td>
                                    #{{item.serialNo}}
                                </td>
                                <td>{{item.testCaseName}}</td>
                                <td>
                                    <span :class="'pass-count-label type-'+item.status">{{item.status|dataDict('TestPlanTestCase.status')}}</span>
                                </td>
                                <td>
                                    <span class="category" :style="{'color':item.color}" v-for="cate in item.categorys">{{cate.name}} </span>
                                </td>
                                <td>{{item.runCount}}</td>
                                <td>{{item.lastRunAccountName}}</td>
                            </tr>
                        </table>
                        <div v-if="filteredList.length==0" class="table-nodata">
                            {{$t('暂无数据')}}
                        </div>
                    </div>
                </div>

                <div slot="right" class="splite-container" style="padding-left:15px">
                    <div class="main-bg" v-if="testCase.id==null">
                        <div class="no-testcase-select">{{$t('请点击测试用例开始执行')}}</div>
                    </div>
                    <div class="main-bg scrollbox" v-if="testCase.id!=null">
                        <div>
                            <div class="test-case-name">
                                <TaskNameLabel :id="testCase.serialNo" :name="testCase.name"
                                               :type="testCase.objectType"/>
                            </div>

                            <div style="margin-top:10px" v-if="!projectFinishDisabled">
                                <div class="info-section-row">
                                    <div class="info-section-header">{{$t('执行结果')}}</div>
                                    <Poptip ref="poptip" transfer placement="bottom"
                                            v-model="relPopupTopVisible">
                                        <IconButton icon="ios-bug-outline" :title="$t('关联缺陷')"></IconButton>
                                        <div slot="content">
                                            <div class="popup-box scrollbox" style="text-align:left">
                                                <div @click="showAddBugDialog" class="popup-item-name" >
                                                    {{$t('创建缺陷')}}
                                                </div>
                                                <div class="popup-item-name" @click="showSelectTaskDialog(0)" >
                                                    {{$t('关联对象')}}
                                                </div>
                                                <!--<div class="popup-item-name" @click="showSelectTaskDialog(1)">-->
                                                <!--    {{$t('前置对象')}}-->
                                                <!--</div>-->
                                                <!--<div class="popup-item-name" @click="showSelectTaskDialog(2)">-->
                                                <!--    {{$t('后置对象')}}-->
                                                <!--</div>-->
                                            </div>
                                        </div>
                                    </Poptip>
                                </div>

                                <div style="margin-top:25px" v-if="!projectFinishDisabled">
                                    <span @click="runResult=2" :class="{'run-result-1-select':runResult==2}"
                                          class="run-result run-result-1">{{$t('通过')}}</span>
                                    <span @click="runResult=3" :class="{'run-result-2-select':runResult==3}"
                                          class="run-result run-result-2">{{$t('不通过')}}</span>
                                    <span @click="runResult=4" :class="{'run-result-3-select':runResult==4}"
                                          class="run-result run-result-3">{{$t('阻塞')}}</span>
                                </div>
                                <div style="margin-top:25px" v-if="!projectFinishDisabled">
                                    <Input :rows="4" v-model.trim="runRemark" type="textarea"
                                           :placeholder="$t('实际结果与备注')"/>
                                </div>

                                <div style="text-align:right;margin-top:15px" >
                                    <Button @click="getNextRunItem" style="margin-right:10px" type="default">
                                        {{$t('跳过')}}
                                    </Button>
                                    <Button :disabled="runResult==0" @click="runTeseCase" type="default">{{$t('执行')}}
                                    </Button>
                                </div>

                            </div>

                            <div style="margin-top:20px">
                                <div class="info-section-row">
                                    <div class="info-section-header">{{$t('用例详情')}}</div>
                                </div>
                            </div>
                            <div>
                                <table class="table-content table-content-small" style="margin-top:10px;">
                                    <tbody>
                                    <template v-for="field in editTaskInfo.fieldList">
                                        <tr v-if="field.isSystemField==false" :key="'field_'+field.id"
                                            class="table-row table-row-small">
                                            <td style="width:100px;color:#666" class="text-no-wrap">
                                                {{field.name}}
                                            </td>
                                            <td style="font-size:14px">
                                <span v-if="field.type==6&&testCase.customFields['fieldobject_'+field.id]!=null">
                                    <template v-if="testCase.customFields['fieldobject_'+field.id].length==0">
                                      {{$t('未设置')}}
                                    </template>
                                    <template v-if="testCase.customFields['fieldobject_'+field.id].length==1">
                                      <AvatarImage size="small"
                                                   :name="testCase.customFields['fieldobject_'+field.id][0].name"
                                                   :value="testCase.customFields['fieldobject_'+field.id][0].imageId"
                                                   type="label"></AvatarImage>
                                  </template>

                                  <template v-if="testCase.customFields['fieldobject_'+field.id].length>1">
                                      <AvatarImage v-for="acc in testCase.customFields['fieldobject_'+field.id]"
                                                   :key="testCase.id+'_cst'+acc.id"
                                                   size="small"
                                                   :name="acc.name"
                                                   :value="acc.imageId" type="none"></AvatarImage>
                                  </template>
                              </span>


                                                <span v-if="field.type==7">{{testCase.customFields['field_'+field.id]|fmtDate}}</span>
                                                <span v-if="field.type==3||field.type==1||field.type==4||field.type==8">
                                  {{testCase.customFields['field_'+field.id]}}
                              </span>
                                            </td>
                                        </tr>
                                    </template>
                                    </tbody>
                                </table>
                            </div>
                            <div style="margin-top:15px">
                                <RichtextLabel :value="testCase.content" :placeholder="$t('未设置详情')"></RichtextLabel>
                            </div>

                            <div style="margin-top:20px">
                                <div class="info-section-row">
                                    <div class="info-section-header">{{$t('关联缺陷')}}</div>
                                </div>
                            </div>

                            <table class="table-content table-content-small" style="margin-top:10px">
                                <tbody>
                                <tr v-for="item in bugList" :key="'bug_'+item.id" class="table-row table-row-small">
                                    <td class="clickable">
                                        <TaskNameLabel @click.native="showTaskInfo(item)" :id="item.serialNo"
                                                       :name="item.name" :type="item.objectType"/>
                                    </td>
                                    <td style="text-align:right;width:200px;color:#999">
                            <span>
                                <TaskStatus style="margin-right:10px" :label="item.statusName"
                                            :color="item.statusColor"></TaskStatus>
                                <TaskOwnerView :value="item"/>
                             </span>
                                    </td>
                                </tr>
                                </tbody>
                            </table>

                            <div v-if="bugList.length==0" class="table-nodata">
                                {{$t('暂无数据')}}
                            </div>


                            <div style="margin-top:20px">
                                <div class="info-section-row">
                                    <div class="info-section-header">{{$t('执行记录')}}</div>
                                </div>
                            </div>

                            <table class="table-content table-content-small" style="margin-top:10px">
                                <tbody>
                                <tr v-for="item in runLogList" :key="'bug_'+item.id" class="table-row table-row-small">
                                    <td style="width:30px">
                                        <AvatarImage size="small" :name="item.createAccountName"
                                                     :value="item.createAccountImageId" type="tips"></AvatarImage>
                                    </td>
                                    <td style="width:110px">
                                        <span>{{item.createTime|fmtDeltaTime}}</span>
                                    </td>
                                    <td style="width:100px">
                                        <span :class="'pass-count-label type-'+item.status">{{item.status|dataDict('TestPlanTestCase.status')}}</span>

                                    </td>
                                    <td>
                                        {{item.remark}}
                                    </td>
                                </tr>
                                </tbody>
                            </table>

                            <div v-if="runLogList.length==0" class="table-nodata">
                                {{$t('暂无数据')}}
                            </div>
                        </div>
                    </div>
                </div>
            </Split>
        </Row>


    </Modal>
</template>


<script>
    export default {
        mixins: [componentMixin],
        data() {
            return {
                panelSplite: 0.55,
                task: {
                    testCaseList: [],
                },
                sort: {
                    testCaseName: null,
                    serialNo: null,
                },
                queryItem: {
                    serialNo: null,
                    name: null,
                    status: null,
                    categoryName: null
                },
                editTaskInfo: {},
                runResult: 0,
                runRemark: null,
                testCase: {
                    associatedList: [],
                },
                bugList: [],
                runLogList: [],
                currentTestPlanTeseCaseId: null,
                runPercent: "--",
                statusCount: {},
                relPopupTopVisible: false,
                isSeletAll: false,
                categoryList: [],
                categoryNodeList:[],
                projectFinishDisabled:false
            }
        },
        watch: {
            isSeletAll(val) {
                for (var i = 0; i < this.filteredList.length; i++) {
                    this.filteredList[i].isSelect = val;
                }
            }
        },
        computed: {
            filteredList: function () {
                var list = [];
                for (var i = 0; i < this.task.testCaseList.length; i++) {
                    var t = this.task.testCaseList[i];
                    if (this.queryItem.serialNo != null && this.queryItem.serialNo != '') {
                        if ((t.serialNo + "").indexOf(this.queryItem.serialNo) == -1) {
                            continue;
                        }
                    }
                    if (this.queryItem.name != null && this.queryItem.name != '') {
                        if (t.testCaseName.indexOf(this.queryItem.name) == -1) {
                            continue;
                        }
                    }
                    if (this.queryItem.status != null && this.queryItem.status != '') {
                        if (this.queryItem.status != t.status) {
                            continue;
                        }
                    }
                    var contain = false;
                    if (this.queryItem.categoryIdList) {
                        this.queryItem.categoryIdList.forEach(cid=>{
                            if (!!t.testCaseCategoryIdList&&!Array.isEmpty(t.testCaseCategoryIdList)) {
                                var contains = t.testCaseCategoryIdList.filter(s => s === cid).length > 0;
                                if (contains) {
                                    contain = true;
                                }
                            }
                        })
                        // var categorIdx = this.categoryList.findIndex((value) => this.queryItem.categoryName.indexOf(value.name) > -1);
                        // if (categorIdx > 0) {
                        //     var categoryId = this.categoryList[categorIdx].id;
                        //     if (!!t.testCaseCategoryIdList&&!Array.isEmpty(t.testCaseCategoryIdList)) {
                        //         var contains = t.testCaseCategoryIdList.filter(s => s === categoryId).length > 0;
                        //         if (!contains) {
                        //             continue;
                        //         }
                        //     }else{
                        //         continue;
                        //     }
                        // } else {
                        //     continue;
                        // }
                        if(!contain){
                            continue;
                        }
                    }
                    list.push(t);
                }
                Object.keys(this.sort).forEach((key) => {

                    if(this.sort[key] == 1) {
                        console.log(key);
                        list.sort((a,b) => a[key].toString().localeCompare(b[key].toString()));
                    } else if(this.sort[key] == 2) {
                        console.log(key);
                        list.sort((a,b) => b[key].toString().localeCompare(a[key].toString()));
                    }
                });
                return list;
            }
        },
        methods: {
            pageLoad() {

                this.loadData();
            },
            pageMessage(type) {
                if (type == 'task.edit') {
                    this.loadRelBugList();
                }
            },
            loadTestCaseCategoryList() {
                //OBJECTTYPE_测试计划 = 4; OBJECTTYPE_测试用例 = 5;
                app.invoke("BizAction.getCategoryNodeList", [app.token, this.task.projectId, 5], info => {
                    if (!!info && !Array.isEmpty(info)) {
                        this.categoryNodeList = info;
                        this.recursiveSetCategory(info);
                        this.setTestCaseCategoryName();
                    }
                })
            },
            setTestCaseCategoryName() {
                let _this = this;
                for (var i = 0; i < this.task.testCaseList.length; i++) {
                    var cates = [];
                    var ids =[];
                    if (!!this.task.testCaseList[i].testCaseCategoryIdList && !Array.isEmpty(this.task.testCaseList[i].testCaseCategoryIdList)) {
                        this.task.testCaseList[i].testCaseCategoryIdList.forEach(item => {
                            var matches = this.categoryList.filter(idx => idx.id == item);
                            if(!Array.isEmpty(matches)){
                                for (let j = 0; j < matches.length; j++) {
                                    if(!Array.contains(ids,item,a=>a)){
                                        cates.push(matches[j])
                                        ids.push(item);
                                    }
                                }
                            }
                        })
                    }
                    _this.$set(  this.task.testCaseList[i],"categorys",cates);
                    // this.task.testCaseList[i].categorys = cates;
                }
            },
            loadData() {
                this.isSeletAll = false;
                app.invoke("BizAction.getTaskInfoByUuid", [app.token, this.args.uuid], info => {
                    for (var i = 0; i < info.task.testCaseList.length; i++) {
                        info.task.testCaseList[i].isSelect = false;
                    }
                    this.task = info.task;
                    this.projectFinishDisabled = this.isProjectDisabled(this.task.projectId);
                    this.computeStatus();
                    this.loadTestCaseCategoryList();
                })
            },
            recursiveSetCategory(categoryNodeList) {
                if (!Array.isEmpty(categoryNodeList)) {
                    categoryNodeList.forEach(item => {
                        if(!Array.contains(this.categoryList,item.id,(val)=>{
                            return val.id
                        })){
                            this.categoryList.push(item);
                            if (!Array.isEmpty(item.children)) {
                                this.recursiveSetCategory(item.children);
                            }
                        }
                    })
                }
            },
            computeStatus() {
                this.runPercent = "--";
                this.statusCount = {};
                if (this.task.testCaseList.length == 0) {
                    return;
                }
                var statusCount = {};
                var runCount = 0;
                for (var i = 0; i < this.task.testCaseList.length; i++) {
                    var t = this.task.testCaseList[i];
                    if (t.status != 1) {
                        runCount++;
                    }
                    if (statusCount[t.status] == null) {
                        statusCount[t.status] = {count: 0, percent: 0};
                    }
                    statusCount[t.status].count = statusCount[t.status].count + 1;
                }
                //
                for (var k in statusCount) {
                    var t = statusCount[k];
                    t.percent = parseInt((t.count / this.task.testCaseList.length) * 100);
                }
                //
                this.runCount = runCount;
                this.runPercent = parseInt((runCount / this.task.testCaseList.length) * 100);
                this.statusCount = statusCount;

            },
            getStatusCount(status) {
                if (this.statusCount[status] == null) {
                    return {count: 0, percent: 0};
                }
                return this.statusCount[status];
            },
            showTestCaseInfo(item) {
                this.currentTestPlanTeseCaseId = item.id;
                this.runResult = 0;
                app.invoke("BizAction.getTaskInfoByUuid", [app.token, item.uuid], info => {
                    this.testCase = info.task;
                    this.editTaskInfo = info.editTaskInfo;
                    this.loadRelBugList();
                    this.loadRunLogList();
                })
            },
            runTeseCase() {
                if (this.runResult == 0) {
                    app.toast(this.$t('请选择执行结果'));
                    return;
                }
                app.invoke("BizAction.runTestPlanTestCase", [app.token,
                    this.currentTestPlanTeseCaseId,
                    this.runResult,
                    this.runRemark], info => {
                    this.runRemark = null;
                    this.getNextRunItem();
                    this.loadData();
                })
            },
            batchRunTestCase(runResult) {
                var selectedId = [];
                for (var i = 0; i < this.filteredList.length; i++) {
                    var t = this.filteredList[i];
                    if (t.isSelect) {
                        selectedId.push(t.id);
                    }
                }
                if (selectedId.length == 0) {
                    app.toast(this.$t('请选择要批量执行的用例'));
                    return;
                }
                app.invoke("BizAction.batchRunTestPlanTestCase", [app.token,
                    selectedId,
                    runResult], info => {
                    app.toast(this.$t('批量执行成功'));
                    this.loadData();
                })
            },
            getNextRunItem() {
                var nextId = 0;
                for (var i = 0; i < this.filteredList.length; i++) {
                    var t = this.filteredList[i];
                    if (t.id == this.currentTestPlanTeseCaseId) {
                        nextId = i + 1;
                        break;
                    }
                }
                if (nextId <= this.filteredList.length - 1) {
                    var next = this.filteredList[nextId];
                    this.currentTestPlanTeseCaseId = next.id;
                    this.showTestCaseInfo(next);
                    app.toast(this.$t('执行成功，自动切换到下一条用例'));
                } else {
                    app.toast(this.$t('已全部执行完毕'));
                    this.testCase.id = null;
                }
                // for (var i = 0; i < this.task.testCaseList.length; i++) {
                //     var t = this.task.testCaseList[i];
                //     if (t.id == this.currentTestPlanTeseCaseId) {
                //         nextId = i + 1;
                //         break;
                //     }
                // }
                // if (nextId <= this.task.testCaseList.length - 1) {
                //     var next = this.task.testCaseList[nextId];
                //     this.currentTestPlanTeseCaseId = next.id;
                //     this.showTestCaseInfo(next);
                //     app.toast(this.$t('执行成功，自动切换到下一条用例'));
                // } else {
                //     app.toast(this.$t('已全部执行完毕'));
                //     this.testCase.id = null;
                // }
            },
            deleteItem() {

            },
            showSelectTaskDialog(type) {
                if (!this.testCase.id) {
                    app.toast(this.$t('请点击测试用例开始执行'))
                    return;
                }
                this.relPopupTopVisible = false;
                app.showDialog(TaskSelectDialog, {
                    projectId: this.task.projectId,
                    callback: (list) => {
                        this.addRelTask(type, list);
                    },
                });
            },
            addRelTask(type, list) {
                if (list.length === 0) {
                    return;
                }
                var ids = list.map(item => {
                    return item.id;
                });
                app.invoke('BizAction.addTaskAssociatedList', [app.token, this.testCase.id, type, ids], info => {
                    this.loadRelBugList();
                });
            },
            showAddBugDialog() {
                this.relPopupTopVisible = false;
                app.showDialog(TaskCreateDialog, {
                    projectId: this.task.projectId,
                    type: 2,
                    callback: this.relBug
                })
            },
            loadRelBugList() {
                app.invoke("BizAction.getBugListFromTestCaseId", [app.token, this.testCase.id], info => {
                    this.bugList = info;
                })
            },
            loadRunLogList() {
                app.invoke("BizAction.getTestPlanTestCaseRunLogInfoList", [app.token, this.currentTestPlanTeseCaseId], info => {
                    this.runLogList = info;
                })
            },
            relBug(id) {
                app.invoke("BizAction.addBugFromTestPlanTestCase", [app.token,
                    this.currentTestPlanTeseCaseId, id], info => {
                    this.loadRelBugList();
                })
            },
            showTaskInfo(item) {
                app.showDialog(TaskDialog, {
                    taskId: item.uuid
                })
            },
            sortByKey(target) {
                Object.keys(this.sort).forEach((key) => {
                    if(key === target) {
                        this.sort[key] = this.sort[key] === 1 ? 2 : (this.sort[key] === null ? 1 : null);
                    } else {
                        this.sort[key] = null;
                    }
                });
            }
        }
    }
</script>
