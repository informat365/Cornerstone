<style scoped>
    .line {
        border-top: 1px solid #eee;
        margin-top: 15px;
        margin-bottom: 15px;
    }

    .rule-desc {
        margin-bottom: 20px;
        color: #999;
    }

    .form-supplier /deep/ .ivu-form-item {
        margin-bottom: 8px !important;
    }
</style>

<i18n>
    {
    "en": {
    "外包人员": "Supplier Member",
    "选择成员": "Choose member",
    "姓名": "Name",
    "手机号": "Mobile",
    "供应商": "Supplier",
    "科室": "Department",
    "小组": "Team",
    "项目经理": "Leader manager",
    "工号":"code",
    "工位号":"work code",
    "工作地点":"work base station",
    "邮箱":"email",
    "身份证":"ID card",
    "门禁卡":"Door card delivery",
    "承诺书":"Promise announce sign",
    "状态":"Status",
    "级别":"Level",
    "入场时间":"Entry time",
    "离场时间":"Leave time",
    "籍贯":"Native place",
    "学历":"Educatioin",
    "毕业院校":"Graduation",
    "继续创建下一个":"Continue to create the next ",
    "保存":"Save",
    "创建":"Create",
    "选择日期":"Choose date",
    "删除成功":"Delete success",
    "操作成功":"Success",
    "请选择供应商用户":"Please choose notify user or role",
    "确认要删除吗？":"Are you sure you want to delete “{0}”?"
    },
    "zh_CN": {
    "外包人员": "外包人员",
    "选择成员": "选择成员",
    "姓名": "姓名",
    "手机号": "手机号",
    "供应商": "供应商",
    "科室": "科室",
    "小组": "小组",
    "项目经理": "项目经理",
    "工号":"工号",
    "工位号":"工位号",
    "工作地点":"工作地点",
    "邮箱":"邮箱",
    "身份证":"身份证",
    "门禁卡":"门禁卡",
    "承诺书":"承诺书",
    "状态":"状态",
    "级别":"级别",
    "入场时间":"入场时间",
    "离场时间":"离场时间",
    "籍贯":"籍贯",
    "学历":"学历",
    "毕业院校":"毕业院校",
    "继续创建下一个":"继续创建下一个",
    "保存":"保存",
    "创建":"创建",
    "选择日期":"选择日期",
    "删除成功":"删除成功",
    "操作成功":"操作成功",
    "请选择供应商用户":"请选择供应商用户",
    "确认要删除吗？":"确认要删除 “{0}” 吗？"
    }
    }
</i18n>

<template>
    <Modal
            ref="dialog"
            v-model="showDialog"
            :closable="true"
            :mask-closable="false"
            :loading="false"
            :title="$t('外包人员')"
            width="700"
            @on-ok="confirm">

        <Form inline :label-width="125" class="form-supplier"
              ref="form" :rules="formRule" label-position="top" :model="formItem" style="min-height:600px;padding:15px">
            <div v-if="loaded">
                <FlagTitle label="基本信息"></FlagTitle>
                <FormItem :label="$t('企业成员')" prop="accountId">
                    <Input  style="width: 160px;" v-if="formItem.name" :placeholder="$t('成员')"  clearable @on-clear="clearAccount" v-model="formItem.name"></Input>
                    <CompanyUserSelect v-else
                            v-model="formItem.accountId"
                            clearable
                            placeholder="选择成员"
                            style="width: 160px; "
                    />
                </FormItem>
                <FormItem :label="$t('关联钉钉成员')" prop="supplierId">
                    <Select transfer filterable  clearable v-model="formItem.dingtalkMemberId"
                            :remote-method="searchDingtalkMember"
                            :loading="loading" style="width: 160px;">
                        <Option v-for="item in dingtalkMemberList" :value="item.id" :key="item.id">{{ item.name }}{{item.mobileNo}}</Option>
                    </Select>
                </FormItem>
                <FormItem :label="$t('工作状态')" prop="status">
                    <DataDictSelect  style="width: 160px;" clearable :placeholder="$t('工作状态')" type="SupplierMember.workStatus" v-model="formItem.workStatus"/>
                </FormItem>
                <FormItem :label="$t('岗位')" prop="status">
                    <DataDictSelect  style="width: 160px;" clearable :placeholder="$t('岗位')" type="SupplierMember.position" v-model="formItem.position"/>
                </FormItem>
                <FormItem :label="$t('手机号')" prop="mobile">
                    <Input  style="width: 160px;" :placeholder="$t('手机号')" v-model.trim="formItem.mobile"></Input>
                </FormItem>
                <FormItem :label="$t('供应商')" prop="supplierId">
                    <Select transfer filterable  clearable v-model="formItem.supplierId"  style="width: 160px;">
                        <Option v-for="item in supplierList" :value="item.id" :key="item.id">{{ item.name }}</Option>
                    </Select>
<!--                    <ListSelect style="width: 160px; " :multiple="false" v-model="formItem.supplierId" :data-list="supplierList" placeholder="供应商"></ListSelect>-->
                </FormItem>
                <!--                <FormItem :label="$t('科室')" prop="departmentId">-->
                <!--                    <Input :placeholder="$t('科室')" v-model.trim="formItem.departmentId"></Input>-->
                <!--                </FormItem>-->
                <!--                <FormItem :label="$t('小组')" prop="teamId">-->
                <!--                    <Input :placeholder="$t('小组')" v-model.trim="formItem.teamId"></Input>-->
                <!--                </FormItem>-->

                <FormItem :label="$t('项目经理')" prop="leaderAccountId">
                    <Input  style="width: 160px;" v-if="formItem.leaderAccountName"  clearable :placeholder="$t('项目经理')" @on-clear="clearLeaderAccount" v-model="formItem.leaderAccountName"></Input>
                    <CompanyUserSelect v-else
                            v-model="formItem.leaderAccountId"
                            clearable
                            placeholder="项目经理"
                            style="width: 160px; "

                    />
                    <br>
                </FormItem>
                <FormItem :label="$t('系统')" prop="supplierId">
                    <Select :placeholder="$t('系统')"  transfer filterable multiple  clearable v-model="formItem.productIds"
                            style="width: 160px;">
                        <Option v-for="item in repositoryList" :value="item.id" :key="item.id">{{ item.name }}</Option>
                    </Select>
                </FormItem>
                <br>
                <FormItem :label="$t('所属科室')">
                    <Tag v-for="item in formItem.departmentList" :key="'prj_'+item.id">{{item.name}}</Tag>
                </FormItem>
                <br>
                <FormItem :label="$t('参与项目')" prop="teamId">
                    <Tag v-for="item in formItem.projectList" :key="'prj_'+item.id">{{item.name}}</Tag>
                </FormItem>



                <FlagTitle label="其他信息"></FlagTitle>
                <FormItem :label="$t('工作地点')" prop="baseStation">
                    <Input style="width: 450px;" :placeholder="$t('工作地点')" v-model.trim="formItem.baseStation"></Input>
                </FormItem>
                <FormItem :label="$t('工号')" prop="code">
                    <Input style="width: 160px;" :placeholder="$t('工号')" v-model.trim="formItem.code"></Input>
                </FormItem>
                <FormItem :label="$t('工位号')" prop="baseStationCode">
                    <Input style="width: 160px;" :placeholder="$t('工位号')" v-model.trim="formItem.baseStationCode"></Input>
                </FormItem>
                <FormItem :label="$t('邮箱')" prop="email">
                    <Input  style="width: 160px;" :placeholder="$t('邮箱')" v-model.trim="formItem.email"></Input>
                </FormItem>
                <FormItem :label="$t('身份证')" prop="idCard">
                    <Input  style="width: 160px;" :placeholder="$t('身份证')" v-model.trim="formItem.idCard"></Input>
                </FormItem>
                <FormItem :label="$t('门禁卡')" prop="entryCard">
                    <RadioGroup style="width:160px" v-model="formItem.entryCardVal">
                        <Radio :label="1">
                            <span>已发放</span>
                        </Radio>
                        <Radio :label="2">
                            <span>未发放</span>
                        </Radio>
                    </RadioGroup>
                </FormItem>
                <FormItem :label="$t('承诺书')" prop="promiseDesc">
                    <RadioGroup style="width:160px" v-model="formItem.promiseDescVal">
                        <Radio :label="1">
                            <span>已签</span>
                        </Radio>
                        <Radio :label="2">
                            <span>未签</span>
                        </Radio>
                    </RadioGroup>
                </FormItem>

                <FormItem :label="$t('级别')" prop="level">
                    <DataDictSelect style="width: 160px;" clearable :placeholder="$t('级别')" type="SupplierMember.level" v-model="formItem.level"/>
                </FormItem>
                <FormItem :label="$t('入场时间')" prop="entryTime">
                    <ExDatePicker clearable
                                  type="date" style="width:160px"
                                  v-model="formItem.entryTime"
                                  :placeholder="$t('入场时间')"></ExDatePicker>
                </FormItem>
                <FormItem :label="$t('离场时间')" prop="leaveTime">
                    <ExDatePicker clearable
                                  type="date" style="width:160px"
                                  v-model="formItem.leaveTime"
                                  :placeholder="$t('离场时间')"></ExDatePicker>
                </FormItem>
                <FormItem :label="$t('籍贯')" prop="nativePlace">
                    <Input style="width: 160px;" :placeholder="$t('籍贯')" v-model.trim="formItem.nativePlace"></Input>
                </FormItem>
                <FormItem :label="$t('学历')" prop="education">
                    <Input style="width: 160px;" :placeholder="$t('学历')" v-model.trim="formItem.education"></Input>
                </FormItem>
                <FormItem :label="$t('毕业院校')" prop="graduation">
                    <Input style="width: 160px;" :placeholder="$t('毕业院校')" v-model.trim="formItem.graduation"></Input>
                </FormItem>


                <div class="line"></div>
                <FormItem v-if="formItem.id>0" label="">
                    <Button v-if="perm('supplier_delete')" @click="deleteItem" type="error">{{$t('删除此供应商成员')}}</Button>
                    <Button
                            v-if="formItem.status===1&&perm('supplier_edit')" style="margin-left:10px" @click="setEnable(false)" type="default">
                        {{$t('禁用此供应商成员')}}
                    </Button>
                    <Button v-if="formItem.status===2&&perm('supplier_edit')" style="margin-left:10px" @click="setEnable(true)" type="default">
                        {{$t('启用此供应商成员')}}
                    </Button>
                </FormItem>
            </div>
        </Form>

        <div slot="footer">
            <Row>
                <Col span="12" style="text-align:left;padding-top:5px">
                    <Checkbox v-if="formItem.id===0" v-model="continueCreate" size="large">{{$t('继续创建下一个')}}</Checkbox>&nbsp;
                </Col>
                <Col span="12" style="text-align:right">
                    <Button @click="confirm" type="default" size="large">{{formItem.id>0?$t('保存'):$t('创建')}}</Button>
                </Col>
            </Row>

        </div>

    </Modal>
</template>

<script>
    import DataDictSelect from "../../../../components/ui/DataDictSelect";
    import FlagTitle from "../VersionRepository/components/FlagTitle";
    import SelectObject from "../../../../components/ui/SelectObject";
    import ListSelect from "../../../../components/ui/ListSelect";
    import Treeselect from '@riophae/vue-treeselect';
    import '@riophae/vue-treeselect/dist/vue-treeselect.css';

    export default {
        components: {Treeselect, ListSelect, SelectObject, FlagTitle, DataDictSelect},
        mixins: [componentMixin],
        data() {
            return {
                loaded: false,
                continueCreate: false,
                formItem: {
                    id: 0,
                    name: null,
                    status: 1,
                    accountId: null,
                    code: null,
                    entryCard: false,
                    promiseDesc: false,
                    entryCardVal: 2,
                    promiseDescVal: 2,
                    workStatus:1

                },
                formRule: {
                    name: [vd.req, vd.name],
                    email: [vd.email],
                    mobile: [vd.mobile],
                    title: [vd.req, vd.name],
                    content: [vd.req, vd.name2_500],
                },
                supplierList: [],
                repositoryList: [],
                departmentList: [],
                dingtalkMemberList:[],
                account: null,
                initAccountId:0,
                loading:false
            };
        },
        watch: {
            'formItem.accountId': function (newVal, oldVal) {
                if (newVal&&newVal!=this.initAccountId) {
                    this.loadAccount();
                }
            },
        },
        methods: {
            pageLoad() {
                if (this.args.id) {
                    this.loadData(this.args.id);
                } else if (this.args.copyId) {
                    this.loadData(this.args.copyId, true);
                } else {
                    this.loaded = true;
                }
                this.loadSupplierList();
                this.loadVersionRepositoryList();
                this.getDepartmentList();
                this.loadDingtalkMemberList();
            },
            loadData(id, copy) {
                if (id > 0) {
                    app.invoke('BizAction.getSupplierMemberById', [app.token, id], (info) => {
                        this.formItem = info;
                        this.initAccountId = info.accountId;
                        this.formItem.entryCardVal = info.entryCard ? 1 : 2;
                        this.formItem.promiseDescVal = info.promiseDesc ? 1 : 2;
                        this.loaded = true;
                        if (copy) {
                            this.formItem.id = 0;
                        }
                    },err=>{
                        console.error(err)
                    });
                }
            },
            getDepartmentList() {
                app.invoke('BizAction.getDepartmentTree', [app.token, false], (info) => {
                    travalTree(info[0], (item) => {
                        item.label = item.title;
                        if (item.children.length == 0) {
                            delete item.children;
                        }
                    })
                    this.departmentList = info;

                })
            },
            searchDingtalkMember(query){
                if (query !== '') {
                    this.loading = true;
                    app.invoke('BizAction.getDingtalkMemberList', [app.token, {pageSize:-1,name:query}], (info) => {
                        this.dingtalkMemberList = info.list;
                        this.loading = false;
                    });
                } else {
                    this.dingtalkMemberList = [];
                }
            },
            loadDingtalkMemberList(){
                app.invoke('BizAction.getDingtalkMemberList', [app.token, {pageIndex:1}], (info) => {
                    this.dingtalkMemberList = info.list;
                });
            },
            loadVersionRepositoryList() {
                app.invoke('BizAction.getCompanyVersionRepositoryList', [app.token, {pageSize: 1000}], (info) => {
                    this.repositoryList = info.list;
                    this.$forceUpdate();
                });
            },
            loadSupplierList() {
                app.invoke('BizAction.getSupplierList', [app.token, {pageSize: -1}], (info) => {
                    this.supplierList = info.list;
                });
            },
            loadAccount() {
                app.invoke('BizAction.getAccountCompanyInfo', [app.token,this.formItem.accountId], (info) => {
                    if(info){
                        this.account = info;
                        this.formItem.status = this.account.status;
                        this.formItem.mobile = this.account.mobileNo;
                        this.formItem.email = this.account.email;
                        this.formItem.projectList = info.projectList;
                        this.formItem.departmentList = info.departmentList;
                        this.$forceUpdate();
                    }
                });
            },
            setEnable(bool){
                if(!this.checkPermission()){
                    app.toast(this.$t('权限不足'));
                }
                if(bool){
                    this.enableAccount();
                }else{
                    this.disableAccount();
                }
            },
            disableAccount() {
                app.confirm("确认要禁用供应商成员账户吗？禁用后账户将不能登录",() => {
                    app.invoke('BizAction.disableAccount', [app.token, this.formItem.accountId], (info) => {
                        app.toast(this.formItem.name + this.$t('已禁用'))
                        this.showDialog = false;
                        app.postMessage('supplierMember.edit')
                    })
                })
            },
            enableAccount() {
                app.confirm("确认要启用供应商成员账户吗？",() => {
                    app.invoke('BizAction.enableAccount', [app.token, this.formItem.accountId], (info) => {
                        app.toast(this.formItem.name + this.$t('已启用'))
                        this.showDialog = false;
                        app.postMessage('supplierMember.edit')
                    })
                });
            },
            deleteItem() {
                if(!this.checkPermission()){
                    app.toast(this.$t('权限不足'));
                }
                app.confirm(this.$t('确认要删除吗？', [this.formItem.name]), () => {
                    app.invoke('BizAction.deleteSupplierMember', [app.token, this.formItem.id], (info) => {
                        app.toast(this.$t('删除成功'));
                        app.postMessage('supplierMember.edit');
                        this.showDialog = false;
                    });
                });
            },
            clearAccount(){
                this.formItem.accountId=0;
                this.initAccountId = -1;
                this.formItem.name=null;
            },
            clearLeaderAccount(){
                this.formItem.leaderAccountId=0;
                this.formItem.leaderAccountName=null;
            },
            confirm() {
                this.$refs.form.validate((r) => {
                    if (r) {
                        this.confirmForm();
                    }
                });
            },
            isEmptyArray(list) {
                return !Array.isArray(list) || list.length === 0;
            },
            checkPermission(){
                return this.perm("supplier_edit")
            },
            confirmForm() {
                if(!this.checkPermission()){
                    app.toast(this.$t('权限不足'));
                }
                this.formItem.entryCard = this.formItem.entryCardVal == 1;
                this.formItem.promiseDesc = this.formItem.promiseDescVal == 1;
                var action = this.formItem.id > 0 ? 'BizAction.updateSupplierMember' : 'BizAction.addSupplierMember';
                app.invoke(action, [app.token, this.formItem], (info) => {
                    app.toast(this.$t('操作成功'));
                    app.postMessage('supplierMember.edit');
                    if (!this.continueCreate) {
                        this.showDialog = false;
                    } else {
                        this.$refs.form.resetFields();
                    }
                });
            },
        },
    };
</script>
