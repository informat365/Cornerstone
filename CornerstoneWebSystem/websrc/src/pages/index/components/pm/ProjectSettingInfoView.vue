<style scoped>
    .check {
        color: #0097f7;
    }

    .tag-color {
        display: inline-block;
        width: 30px;
        height: 30px;
        border-radius: 50%;
        margin-right: 10px;
        color: #fff;
        overflow: hidden;
        text-align: center;
    }
</style>
<i18n>
    {
    "en": {
    "名称": "Name",
    "工作流程状态":"Workflow status",
    "项目名称":"Project name",
    "项目简介":"Project description",
    "公告":"Announcement",
    "分组":"Group",
    "项目分组":"Project group",
    "颜色标识":"Color",
    "封面":"Cover Image",
    "建议尺寸":"200*100",
    "项目类型":"Type",
    "项目模板":"Template",
    "保存":"Save",
    "开始日期":"Start date",
    "截止日期":"Due date",
    "负责人":"Owners",
    "分管领导":"Leaders",
    "添加":"Add"
    },
    "zh_CN": {
    "名称": "名称",
    "工作流程状态":"工作流程状态",
    "项目名称":"项目名称",
    "项目简介":"项目简介",
    "公告":"公告",
    "分组":"分组",
    "项目分组":"项目分组",
    "颜色标识":"颜色标识",
    "封面":"封面",
    "建议尺寸":"建议尺寸200*100",
    "项目类型":"项目类型",
    "项目模板":"项目模板",
    "保存":"保存",
    "开始日期":"开始日期",
    "截止日期":"截止日期",
    "负责人":"负责人",
    "分管领导":"分管领导",
    "添加":"添加"
    }
    }
</i18n>
<template>
    <div>
        <Form @submit.native.prevent ref="form" :rules="formRule" :model="formItem" label-position="top">
            <FormItem :label="$t('名称')" prop="name">
                <Input :disabled="disabled" v-model.trim="formItem.name" :placeholder="$t('项目名称')"></Input>
            </FormItem>

            <FormItem :label="$t('项目简介')" prop="description">
                <Input :disabled="disabled" v-model.trim="formItem.description" type="textarea" :rows="3" :placeholder="$t('项目简介')"></Input>
            </FormItem>

            <FormItem :label="$t('公告')" prop="announcement">
                <Input :disabled="disabled" type="textarea" :rows="3" v-model.trim="formItem.announcement" :placeholder="$t('公告')"></Input>
            </FormItem>
            <FormItem :label="$t('负责人')" prop="ownerAccountList">
                <Tag v-for="item in formItem.ownerAccountList" :key="'s'+item.id" :closable="!disabled"
                     @on-close="removeUser(item)">{{item.name}}
                </Tag>
                <Button v-if="!disabled" shape="circle" type="default" size="small" icon="ios-add" @click="selectCompanyUser">
                    {{$t('添加')}}
                </Button>
            </FormItem>
            <FormItem :label="$t('分管领导')" prop="leaderAccountList">
                <Tag v-for="item in formItem.leaderAccountList" :key="'s'+item.id" :closable="!disabled"
                     @on-close="removeLeader(item)">{{item.name}}
                </Tag>
                <Button v-if="!disabled" shape="circle" type="default" size="small" icon="ios-add" @click="selectCompanyLeader">
                    {{$t('添加')}}
                </Button>
            </FormItem>
            <FormItem :label="$t('工作流程状态')" prop="workflowStatus">
                <ProjectWorkflowSelect style="height: 24px;"
                        :disabled="disabled||prjPerm('project_edit_workflow_status') !== true"
                        v-model="formItem.workflowStatus" :projectId="project.id" :placeholder="$t('工作流程状态')"/>
            </FormItem>
            <FormItem v-if="formItem.isTemplate==false" :label="$t('开始日期')" prop="startDate">
                <ExDatePicker
                        :disabled="disabled"
                        style="width:200px"
                        v-model="formItem.startDate"
                        clearable
                        :options="startDateOptions"
                        :placeholder="$t('开始日期')"/>
            </FormItem>

            <FormItem v-if="formItem.isTemplate==false" :label="$t('截止日期')" prop="endDate">
                <ExDatePicker
                        :disabled="disabled"
                        style="width:200px"
                        v-model.trim="formItem.endDate"
                        clearable
                        :options="endDateOptions"
                        :placeholder="$t('截止日期')"/>
            </FormItem>

            <FormItem label="项目预计总工时" v-if="formItem.isTemplate==false&&isSpiEnable">
                {{formItem.expectWorkTime}}
            </FormItem>

            <FormItem v-if="formItem.isTemplate==false" :label="$t('分组')" prop="group">
                <AutoComplete
                        :disabled="disabled"
                        transfer
                        clearable
                        v-model="formItem.group"
                        :data="groupArray"
                        :placeholder="$t('项目分组')"
                        style="width:200px"></AutoComplete>
            </FormItem>

            <FormItem v-if="formItem.isTemplate==false" :label="$t('颜色标识')" prop="color">
                <div style="height:50px;">
                    <span
                            :disabled="disabled"
                            @click="setColor(item)"
                            v-for="item in colorArray"
                            :key="item"
                            class="tag-color"
                            :style="{backgroundColor:item}">
                        <Icon size="20" v-show="item==color" type="md-checkmark"/>
                    </span>
                </div>
            </FormItem>

            <FormItem v-if="formItem.isTemplate" :label="$t('封面')" prop="imageId">
                <UploadImage
                        :disabled="disabled"
                        v-model="formItem.imageId"
                        :placeholder="'/image/common/placeholder.png'"
                        :image-class="'project-cover-image'"/>
                <div style="font-size:12px;color:#999">{{$t('建议尺寸')}}</div>
            </FormItem>

            <FormItem v-if="formItem.isTemplate==false&&formItem.templateId>0" :label="$t('项目类型')">
                {{formItem.templateName}}
            </FormItem>

            <FormItem v-if="formItem.isTemplate" :label="$t('项目类型')">
                {{$t('项目模板')}}
            </FormItem>


            <FormItem label="">
                <Button v-if="!disabled" @click="confirm" type="default">{{$t('保存')}}</Button>
            </FormItem>

        </Form>

    </div>
</template>

<script>
    export default {
        mixins: [componentMixin],
        props: ['project'],
        data() {
            return {
                formItem: {
                    name: null,
                    description: null,
                    group: null,
                    announcement: null,
                    startDate: null,
                    endDate: null,
                    workflowStatus: null,
                    ownerAccountIdList: [],
                    ownerAccountList: [],
                    leaderAccountIdList:[],
                    leaderAccountList:[]
                },
                startDateOptions: {
                    disabledDate: (date) => {
                        if (!date || !this.formItem.endDate) {
                            return false;
                        }
                        return date.getTime() >= this.formItem.endDate;
                    },
                },
                endDateOptions: {
                    disabledDate: (date) => {
                        if (!date || !this.formItem.startDate) {
                            return false;
                        }
                        return date.getTime() <= this.formItem.startDate;
                    },
                },
                color: null,
                formRule: {
                    name: [vd.req, vd.name],
                    ownerAccountList: [vd.req],
                    group: [vd.name],
                    description: [vd.desc],
                    announcement: [vd.desc],
                },
                colorArray: [
                    '#2e94b9',
                    '#f0b775',
                    '#d25565',
                    '#f54ea2',
                    '#42218e',
                    '#5be7c4',
                    '#525564',
                ],
                groups: [],
                disabled:false,
                isSpiEnable:app.isSpiEnable
            };
        },
        computed: {
            groupArray: function () {
                let list=[];
                if(!this.formItem.group){
                    //太长导致输入框被遮挡的问题
                    list = this.groups.slice(0,12);
                }else{
                    list = this.groups.filter(k=>k.indexOf(this.formItem.group)!==-1);
                }
                return list;
            },
        },
        mounted() {
            this.formItem = this.project;
            this.disabled = this.project.isFinish;
            if (!this.formItem.ownerAccountList) {
                this.formItem.ownerAccountList = [];
            }
            this.color = this.project.color;
            this.loadProjectGroups();
        },
        methods: {
            setColor(color) {
                if (this.color === color) {
                    this.color = null;
                } else {
                    this.color = color;
                }
            },
            loadProjectGroups() {
                app.invoke('BizAction.getProjectGroups', [app.token], (info) => {
                    if (!!info && Array.isArray(info)) {
                        this.groups = info;
                    }
                });
            },
            confirm() {
                if (!this.project.isTemplate) {
                    if (!this.formItem.ownerAccountList || Array.isEmpty(this.formItem.ownerAccountList)) {
                        app.toast("请指派项目负责人");
                        return;
                    }
                }
                this.$refs.form.validate((r) => {
                    if (r) {
                        this.confirmForm();
                    }
                });
            },
            confirmForm() {
                this.formItem.color = this.color;
                this.formItem.ownerAccountIdList = [];
                if (!!this.formItem.ownerAccountList && Array.isArray(this.formItem.ownerAccountList)) {
                    this.formItem.ownerAccountList.forEach(item => this.formItem.ownerAccountIdList.push(item.id));
                }
                this.formItem.leaderAccountIdList = [];
                if (!Array.isEmpty(this.formItem.leaderAccountList)) {
                    this.formItem.leaderAccountIdList = this.formItem.leaderAccountList.map(k=>k.id);
                }
                app.invoke('BizAction.updateProject', [app.token, this.formItem], (info) => {
                    app.postMessage('project.edit');
                    app.toast(this.formItem.name + '保存成功');
                });
            },
            selectCompanyUser() {
                let that = this;
                app.showDialog(CompanyUserSelectDialog, {
                    callback: (t) => {
                        for (var i = 0; i < t.length; i++) {
                            that.addToList(that.formItem.ownerAccountList, t[i]);
                            that.$forceUpdate();
                        }
                    }
                })
            },
            selectCompanyLeader() {
                let that = this;
                app.showDialog(CompanyUserSelectDialog, {
                    callback: (t) => {
                        if(!this.formItem.leaderAccountList){
                            this.formItem.leaderAccountList = [];
                        }
                        for (var i = 0; i < t.length; i++) {
                            that.addToList(that.formItem.leaderAccountList, t[i]);
                            that.$forceUpdate();
                        }
                    }
                })
            },
            addToList(list, t) {
                for (var i = 0; i < list.length; i++) {
                    var q = list[i];
                    if (q.id == t.accountId) {
                        return;
                    }
                }
                list.push({
                    id: t.accountId,
                    name: t.title
                })
            },
            removeFromList(list, item) {
                for (var i = 0; i < list.length; i++) {
                    var t = list[i];
                    if (t.id == item.id) {
                        list.splice(i, 1);
                        return;
                    }
                }
            },
            removeUser(item) {
                this.removeFromList(this.formItem.ownerAccountList, item)
                this.$forceUpdate();
            },
            removeLeader(item) {
                if(!this.formItem.leaderAccountList){
                    this.formItem.leaderAccountList=[];
                }
                this.removeFromList(this.formItem.leaderAccountList, item)
                this.$forceUpdate();
            },
            handleSearch() {
                //do nothing
            }

        },
    };
</script>
