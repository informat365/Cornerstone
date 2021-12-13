<style scoped>
    .page {
        padding: 10px;
        height: 600px;
    }
</style>
<i18n>
    {
    "en": {
    "未分类":"None",
    "所有分类":"All {0} category",
    "分类":"categories",
    "选择项目":"Select Project",
    "选择对象":"Select Object",
    "类型":"Type",
    "名称":"Name",
    "查询":"Query",
    "责任人":"Owner",
    "创建人":"Creator",
    "创建":"Create",
    "取消":"Cancel",
    "确定":"OK",
    "开始时间":"Start date",
    "截止时间":"End date"
    },
    "zh_CN": {
    "未分类":"未分类",
    "所有分类":"所有 {0}",
    "分类":"分类",
    "选择项目":"选择项目",
    "选择对象":"选择对象",
    "类型":"类型",
    "名称":"名称",
    "查询":"查询",
    "责任人":"责任人",
    "创建人":"创建人",
    "创建":"创建",
    "取消":"取消",
    "确定":"确定",
    "开始时间":"开始时间",
    "截止时间":"截止时间"
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
        :title="$t('选择对象')"
        width="1000">
        <div class="page">
            <Form inline>
                <FormItem v-show="!isProjectSetProject">
                    <Select
                        transfer
                        :disabled="singleSelect||singleProject"
                        style="width:200px"
                        @on-change="projectChanged"
                        :placeholder="$t('选择项目')"
                        v-model="formItem.projectId">
                        <Option v-for="item in projectList" :key="item.id" :value="item.id">{{item.name}}</Option>
                    </Select>
                </FormItem>
                <FormItem v-show="!isProjectSetProject">
                    <Select
                        style="width:120px"
                        @on-change="onModuleChange"
                        transfer
                        clearable
                        :placeholder="$t('类型')"
                        v-model="formItem.objectType">
                        <template v-for="item in moduleList">
                            <Option v-if="item.objectType>0" :value="item.objectType" :key="'m'+item.id">
                                {{ item.name }}
                            </Option>
                        </template>
                    </Select>
                </FormItem>
                <FormItem v-if="formItem.projectId > 0 && formItem.objectType > 0">
                    <ProjectCategorySelect
                        v-model="formItem.categoryIdList"
                        multiple
                        style="width:200px"
                        :placeholder="$t('所有分类',[objectTypeName])"
                        :object-type-name="objectTypeName"
                        :project-id="formItem.projectId"
                        :object-type="formItem.objectType"
                        @on-change="debounceLoadData"
                        @on-category-loaded="onCategoryLoaded"/>
                </FormItem>
                <FormItem>
                    <Input
                        @on-change="debounceLoadData"
                        type="text"
                        v-model.trim="formItem.serialNo"
                        placeholder="ID"
                        style="width:80px"/>
                </FormItem>
                <FormItem>
                    <Input
                        @on-change="debounceLoadData"
                        type="text"
                        v-model.trim="formItem.name"
                        :placeholder="$t('名称')"></Input>
                </FormItem>
                <FormItem>
                    <Button style="margin: 0 5px;" @click="debounceLoadData" type="default">{{$t('查询')}}</Button>
                    <Poptip v-if="createEnable"
                            trigger="hover"
                            transfer
                            class="poptip-full">
                        <Button type="default">{{$t('创建')}}</Button>
                        <div slot="content">
                            <div class="popup-box scrollbox">
                                <div v-for="(moudle,idx) in moduleList" :key="idx"
                                     v-if="moudle.isStatusBased&&prjPerm('task_create_'+moudle.objectType)"
                                     @click="createNewAssociateTask(moudle.objectType)"
                                     class="popup-item-name">{{moudle.name}}
                                </div>
                            </div>
                        </div>
                    </Poptip>

                </FormItem>
            </Form>
            <BizTable
                ref="table"
                @change="loadData"
                :value="tableData"
                :page="formItem"
                style="border-top:1px solid #eee;margin-top:20px">
                <template slot="thead">
                    <tr>
                        <th style="width:40px">
                            <Checkbox :disabled="singleSelect" v-model="isSelectAll"></Checkbox>
                        </th>
                        <th style="width:220px"> {{$t('名称')}}</th>
                        <th v-if="categoryList.length > 0">
                            {{$t('分类')}}
                        </th>
                        <th style="width:100px">{{$t('开始时间')}}</th>
                        <th style="width:100px">{{$t('截止时间')}}</th>
                        <th style="width:120px">{{$t('责任人')}}</th>
                        <th style="width:80px">{{$t('创建人')}}</th>
                    </tr>
                </template>
                <template slot="tbody">

                    <tr v-for="item in tableData" :key="item.id" class="table-row">
                        <td>
                            <Checkbox @on-change="selectOne($event,item.id)" v-model="item.isSelect"></Checkbox>
                        </td>
                        <td class="text-no-wrap">
                            <TaskNameLabel
                                :id="item.serialNo"
                                :name="item.name"
                                :isDone="item.isFinish"
                                :object-type="item.objectTypeName"/>
                        </td>
                        <td v-if="categoryList.length > 0">
                            <CategoryLabel
                                :placeholder="$t('未分类')" :category-list="categoryList" :value="item.categoryIdList"/>
                        </td>
                        <td>
                            {{item.startDate|fmtDate}}
                        </td>
                        <td>
                            {{item.endDate|fmtDate}}
                        </td>
                        <td>
                            <TaskOwnerView :tips="false" :value="item"/>
                        </td>
                        <td>
                            <AvatarImage
                                size="small"
                                :name="item.createAccountName"
                                :value="item.createAccountImageId"
                                type="label"></AvatarImage>
                        </td>

                    </tr>
                </template>
            </BizTable>


        </div>

        <div slot="footer">
            <Row>
                <Col span="24" style="text-align:right">
                    <Button @click="showDialog=false" type="text" size="large">{{$t('取消')}}</Button>
                    <Button :disabled="selectCount==0" @click="confirm" type="default" size="large">
                        {{$t('确定')}}
                    </Button>
                </Col>
            </Row>

        </div>
    </Modal>
</template>

<script>

    export default {
        mixins: [componentMixin],
        data() {
            return {
                formItem: {
                    projectId: null,
                    iterationId: null,
                    serialNo: null,
                    name: null,
                    objectType: null,
                    categoryIdList: null,
                    status: null,
                    pageIndex: 1,
                    pageSize: 50,
                    idNotInList: []
                },
                categoryList: [],
                projectList: [],
                iterationList: [],
                tableData: [],
                isSelectAll: false,
                isProjectSetProject: true,
                moduleList: [],
                singleSelect: false,
                singleProject: false
            };
        },
        watch: {
            'isSelectAll': function (val) {
                for (let i = 0; i < this.tableData.length; i++) {
                    this.$set(this.tableData[i], 'isSelect', val || false);
                }
                // this.tableData.map(item => {
                //     item.isSelect = val;
                // });
            },
        },
        computed: {
            objectTypeName() {
                if (!this.formItem.objectType) {
                    return this.$t('分类');
                }
                const objectTypeObj = this.moduleList.find(item => item.objectType === this.formItem.objectType);
                if (!objectTypeObj || !objectTypeObj.name) {
                    return this.$t('分类');
                }
                return objectTypeObj.name;
            },
            selectCount() {
                var count = 0;
                this.tableData.map(item => {
                    if (item.isSelect) {
                        count++;
                    }
                });
                return count;
            },
            //创建权限隔离
            createEnable() {
                if (!!this.moduleList) {
                    return this.moduleList.filter(moudle => moudle.isStatusBased && this.prjPerm('task_create_' + moudle.objectType)).length > 0;
                }
                return false;
            }
        },
        methods: {
            pageLoad() {
                this.loadViewSetting();
                this.singleProject = this.args.singleProject;
                this.singleSelect = this.args.singleSelect;
                console.log(this.singleProject, this.singleSelect)
                this.formItem.projectId = this.args.projectId;
                this.formItem.iterationId = this.args.iterationId;
                this.formItem.objectType = this.args.objectType;
                this.formItem.idNotInList = this.args.idNotInList;
                this.formItem.repositoryVersionId = this.args.repositoryVersionId;
                this.categoryList = [];
                if (!this.formItem.projectId) {
                    this.formItem.projectId = this.args.projectId;
                }
                this.loadProjectList();
                this.loadProjectInfo(() => {
                    this.loadData();
                });
            },
            loadProjectList() {
                app.invoke('BizAction.getMyProjectList', [app.token], list => {
                    this.projectList = list;
                });
            },
            projectChanged() {
                if (!this.formItem.projectId) {
                    this.formItem.projectId = this.args.projectId;
                }
                this.loadProjectInfo(() => {
                    this.debounceLoadData();
                });
            },
            saveViewSetting() {
                app.saveObject('TaskSelectDialog.viewSetting.' + this.formItem.projectId, this.formItem);
            },
            loadViewSetting() {
                var vc = app.loadObject('TaskSelectDialog.viewSetting.' + this.formItem.projectId);
                if (vc != null) {
                    this.formItem = vc;
                }
            },
            loadProjectInfo(callback) {
                if (!this.formItem.projectId) {
                    this.isProjectSetProject = false;
                    if (callback) {
                        callback();
                    }
                    return;
                }
                app.invoke('BizAction.getProjectShowInfo', [app.token, this.formItem.projectId], (info) => {
                    if (!info || !Array.isArray(info.moduleList)) {
                        this.isProjectSetProject = false;
                        if (callback) {
                            callback();
                        }
                        return;
                    }
                    this.moduleList = info.moduleList;
                    //关联到父对象限制为同一对象类型
                    if (this.args.singleSelect) {
                        this.formItem.objectType = this.args.objectType;

                        if (callback) {
                            callback();
                        }
                        return;
                    }

                    const proObjectType = info.moduleList.find(item => item.objectTypeSystemName === process.env.VUE_APP_PROJECT_SET_OBJECT_TYPE_SYSTEM_NAME_PRO);
                    this.isProjectSetProject = !!proObjectType;
                    if (!this.isProjectSetProject) {
                        if (callback) {
                            callback();
                        }
                        return;
                    }

                    this.formItem.objectType = proObjectType.objectType;
                    if (callback) {
                        callback();
                    }
                }, () => {
                    if (callback) {
                        callback();
                    }
                });
            },
            onModuleChange() {
                this.formItem.categoryIdList = [];
                this.formItem.isSetCategory = null;
                this.categoryList = [];
                this.debounceLoadData();
            },
            debounceLoadData() {
                clearTimeout(this.loadDateTimeoutId);
                this.loadDateTimeoutId = setTimeout(() => {
                    this.loadData();
                }, 250);
            },
            onCategoryLoaded(list) {
                const output = [];
                this.handleCategoryList(list, output);
                this.categoryList = output;
            },
            handleCategoryList(list, output) {
                if (!Array.isArray(list)) {
                    return;
                }
                list.forEach(item => {
                    output.push({
                        ...item,
                    });
                    item.label = item.name;
                    if (!Array.isArray(item.children) || item.children.length === 0) {
                        delete item.children;
                        return;
                    }
                    this.handleCategoryList(item.children, output);
                });
            },
            loadData() {
                this.saveViewSetting();
                const formItem = JSON.parse(JSON.stringify(this.formItem));
                formItem.isSetCategory = Array.isArray(formItem.categoryIdList) && formItem.categoryIdList.length > 0 ? true : null;
                if (formItem.isSetCategory) {
                    if (formItem.categoryIdList.indexOf(0) > -1) {
                        formItem.categoryIdList = [];
                        formItem.isSetCategory = false;
                    } else {
                        formItem.categoryIdList = formItem.categoryIdList.filter(item => item > 0);
                        formItem.isSetCategory = formItem.categoryIdList.length > 0;
                    }
                }
                app.invoke('BizAction.getTaskInfoList', [app.token, formItem], info => {
                    if (this.isSelectAll && info.list) {
                        info.list.forEach(item => {
                            item["isSelect"] = true;
                        })
                    } else {
                        this.isSelectAll = false;
                    }
                    this.tableData = info.list;
                    this.formItem.total = info.count;

                });
            },
            getSelectedCount() {
                var count = 0;
                this.tableData.map(item => {
                    if (item.isSelect) {
                        count++;
                    }
                });
                return count;
            },
            confirm: function () {
                var list = [];
                this.tableData.map(item => {
                    if (item.isSelect) {
                        list.push(item);
                    }
                });
                if (this.args.callback) {
                    this.args.callback(list);
                }
                this.showDialog = false;
            },
            selectOne(selected, itemId) {
                //多选时走原来的逻辑，不作处理
                if (this.singleSelect) {
                    if (selected) {
                        for (let i = 0; i < this.tableData.length; i++) {
                            if (this.tableData[i].id != itemId) {
                                this.$set(this.tableData[i], 'isSelect', false);
                            } else {
                                this.$set(this.tableData[i], 'isSelect', true);
                            }
                        }
                    }
                }
            },
            createNewAssociateTask(type) {
                let _this = this;
                app.showDialog(TaskCreateDialog, {
                    projectId: _this.args.projectId,
                    startDate: this.args.startDate,
                    endDate: this.args.endDate,
                    associateType: this.args.associateType,
                    type: type,
                    callback: function (taskId) {
                        if (_this.args.callback) {
                            _this.args.callback([{id: taskId}]);
                        }
                        _this.showDialog = false;
                    }
                });
            }
        },
    };
</script>
