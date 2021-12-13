<style scoped>
    .side-menu {
        height: 660px;
        max-height: calc(100vh - 160px);
        overflow: auto;
        background-color: #404352;
        color: #fff;
        padding-top: 20px;
    }

    .side-menu-item {
        padding: 10px 20px;
        font-size: 14px;
        cursor: pointer;
        position: relative;
        color: #999;
    }

    .side-menu-item:hover {
        background-color: #2e3341;
        color: #fff;
    }

    .side-menu-item-active {
        background-color: #2e3341;
    }

    .side-menu-item-active::before {
        content: '';
        position: absolute;
        top: 0px;
        left: 0;
        height: 40px;
        width: 3px;
        background-color: #1989fa;
    }

    .main-content {
        height: 660px;
        max-height: calc(100vh - 160px);
        overflow: auto;
        padding: 25px;
    }

</style>
<i18n>
    {
    "en": {
    "项目设置": "Project Settings",
    "基本信息": "Basic",
    "字段定义": "Field",
    "字段显示与顺序": "Filed Order",
    "项目工作流": "Project Workflow",
    "工作流": "Object Workflow",
    "变更工作流": "Alteration Workflow",
    "优先级": "Priority",
    "内容模板": "Template",
    "可见性": "Visibility",
    "文件树": "File Tree",
    "数据权限": "Permission",
    "功能开关": "Switch",
    "其它设置": "Misc"
    },
    "zh_CN": {
    "项目设置": "项目设置",
    "基本信息": "基本信息",
    "字段定义": "字段定义",
    "字段显示与顺序": "字段显示与顺序",
    "项目工作流": "项目工作流",
    "工作流": "对象工作流",
    "变更工作流": "变更工作流",
    "优先级": "优先级",
    "内容模板": "内容模板",
    "可见性": "可见性",
    "文件树": "文件树",
    "数据权限": "数据权限",
    "功能开关": "功能开关",
    "其它设置": "其它设置"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog"
        class="nopadding-modal"
        v-model="showDialog"
        :closable="true"
        :mask-closable="false"
        :loading="false"
        :title="$t('项目设置')"
        width="900"
        :footer-hide="true">
        <Row>
            <Col span="6" class="side-menu">
                <template
                    v-for="item in menuList">
                    <div
                        v-if="menuCanShow(item.id)"
                        @click="clickMenuItem(item)"
                        :key="item.name"
                        class="side-menu-item"
                        :class="{'side-menu-item-active':currentMenu==item.id}">{{item.name}}
                    </div>
                </template>
            </Col>
            <Col span="18" class="main-content scrollbox">
                <template v-if="project!=null">
                    <template v-if="currentMenu==='basic'">
                        <ProjectSettingInfoView :project="project" />
                    </template>
                    <template v-else-if="currentMenu==='field'">
                        <ProjectSettingFieldView :project="project" />
                    </template>
                    <template v-else-if="currentMenu==='order'">
                        <ProjectSettingFieldShowView :project="project" />
                    </template>
                    <template v-else-if="currentMenu==='projectWorkflow'">
                        <ProjectSettingProjectWorkflowView :project="project" />
                    </template>
                    <template v-else-if="currentMenu==='workflow'">
                        <ProjectSettingWorkflowView :project="project" />
                    </template>
                    <template v-else-if="currentMenu==='alterationWorkflow'">
                        <ProjectSettingAlterationWorkflowView :project="project" />
                    </template>
                    <template v-else-if="currentMenu==='priority'">
                        <ProjectSettingPriorityView :project="project" />
                    </template>
                    <template v-else-if="currentMenu==='template'">
                        <ProjectSettingTemplateView :project="project" />
                    </template>
                    <template v-else-if="currentMenu==='templateFileTree'">
                        <ProjectSettingTemplateFileTreeView :project="project" />
                    </template>
                    <template v-else-if="currentMenu==='viewpermission'">
                        <ProjectSettingViewView :project="project" />
                    </template>
                    <template v-else-if="currentMenu==='datapermission'">
                        <ProjectSettingPermissionView :project="project" />
                    </template>
                    <template v-else-if="currentMenu==='switch'">
                        <ProjectSettingModuleView :project="project" />
                    </template>
                    <template v-else-if="currentMenu==='other'">
                        <ProjectSettingMiscView :switch-home="switchHome" :project="project" @on-close="showDialog=false"/>
                    </template>
                </template>
            </Col>
        </Row>

    </Modal>
</template>


<script>
    export default {
        name: 'ProjectSettingDialog',
        mixins: [componentMixin],
        data() {
            return {
                project: null,
                currentMenu: 'basic',
                menuList: [
                    { id: 'basic', name: this.$t('基本信息'), template: false },
                    { id: 'field', name: this.$t('字段定义'), template: false },
                    { id: 'order', name: this.$t('字段显示与顺序'), template: false },
                    { id: 'projectWorkflow', name: this.$t('项目工作流'), template: false },
                    { id: 'workflow', name: this.$t('工作流'), template: false },
                    { id: 'alterationWorkflow', name: this.$t('变更工作流'), template: false },
                    { id: 'priority', name: this.$t('优先级'), template: false },
                    { id: 'template', name: this.$t('内容模板'), template: false },
                    { id: 'templateFileTree', name: this.$t('文件树'), template: true },
                    { id: 'viewpermission', name: this.$t('可见性'), template: false },
                    { id: 'datapermission', name: this.$t('数据权限'), template: false },
                    { id: 'switch', name: this.$t('功能开关'), template: false },
                    { id: 'other', name: this.$t('其它设置'), template: false },
                ],
                projectSetExcludeMenus: ['switch', 'other','alterationWorkflow'],
                moduleList:[],
                switchHome:true
            };
        },
        computed:{

        },
        methods: {
            pageLoad() {
                this.switchHome = this.args.switchHome;
                this.loadData();
            },
            menuCanShow(menuId) {
                if (!this.project) {
                    return false;
                }
                if(menuId=='templateFileTree'){
                    var contains =false;
                    for (let i = 0; i < this.moduleList.length; i++) {
                        if(this.moduleList[i].url=='file'){
                            contains =true;
                        }
                    }
                    if(!contains){
                        return false;
                    }
                }

                return this.isNotProjectSet(this.project.templateUuid) || this.projectSetExcludeMenus.indexOf(menuId) === -1;
            },
            loadData() {
                app.invoke('BizAction.getProjectInfoByUuid', [app.token, this.args.uuid], (info) => {
                    this.project = info;
                    this.loadModuleList(info.id);
                    if (!info.isTemplate) {
                        for (var i = 0; i < this.menuList.length; i++) {
                            var t = this.menuList[i];
                            //项目模板
                            if (t.template) {
                                this.menuList.splice(i, 1);
                            }
                        }
                    }
                });
            },
            loadModuleList(projectId){
                app.invoke('BizAction.getProjectModuleInfoList', [app.token, projectId], (info) => {
                    this.moduleList = info;
                });
            },
            reloadProject() {
                app.invoke('BizAction.getProjectInfoByUuid', [app.token, this.args.uuid], (info) => {
                    this.project = info;
                });
            },
            pageMessage(type) {
                if (type == 'project.delete') {
                    this.showDialog = false;
                }
                if (type == 'project.edit') {
                    this.reloadProject();
                }
            },
            clickMenuItem(item) {
                this.currentMenu = item.id;
            },
        },
    };
</script>
