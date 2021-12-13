<style scoped>
.head-logo-custom{
    height: 42px;
    vertical-align: middle;
}
.side-menu{
    width:200px;
    height:calc(100vh - 48px);
    overflow: auto;
    background-color: #404352;
    color:#fff;
    padding-top:20px;
    position: absolute;
    top:48px;
    left:0;
}
.side-menu-wrap{
    position: relative;
}
.side-menu-item{
    padding:10px 20px;
    font-size:14px;
    cursor: pointer;
    position: relative;
    color:#ccc;
}
.side-menu-item:hover{
    background-color: #2E3341;
    color:#fff;
}
.side-menu-item-active{
    background-color: #2E3341;
}
.side-menu-item-active::before{
    content: '';
    position: absolute;
    top:0px;
    left:0;
    height:40px;
    width:3px;
    background-color: #1989FA;
}
.side-menu-item-group{
    font-size:12px;
    color:#999;
    padding-left:20px;
    margin-top:20px;
}
.router-content{
        width:calc(100vw - 200px);
        background-color: #fff;
        margin-left:200px;
        height: calc(100vh - 50px);
        overflow: auto;
}
.project-name-label{
    min-width: 100px;
    max-width:140px;
    display: inline-block;
}
.company-name{
    display: inline-block;
    max-width: 180px;
    text-overflow:ellipsis;
	overflow: hidden;
	white-space: nowrap;
    font-size:14px;
    font-weight: 500;
    color:#333;
    margin-right:20px;
    padding-left:15px;
}
.nav-left-box{
    display: flex;
    align-items: center;
}
</style>

<i18n>
    {
    "en": {
    "基本信息":"Basic info",
    "企业信息":"Company information",
    "项目列表":"Projects",
    "回收站":"Recycle bin",
    "汇报模板":"Report template",
    "系统通知":"System notification",
    "操作日志":"Opt log",
    "流程模板":"Workflow Template",
    "流程":"Workflow",
    "流程数据":"Workflow Data",
    "角色权限":"Role Permissions",
    "组织架构":"Organization",
    "成员列表":"Members",
    "项目角色":"Project Role",
    "企业角色":"Enterprise Role",
    "代码关联":"Code association",
    "代码分支":"Code branch",
    "系统设置":"Settings",
    "对象类型":"Object type",
    "项目模板":"Project template",
    "参数设置":"Parameters",
    "数据表格":"Data table",
    "系统Hook":"System hook",
    "没有权限":"Permission denied",
    "其它":"Other"
    },
    "zh_CN": {
    "基本信息":"基本信息",
    "企业信息":"企业信息",
    "项目列表":"项目列表",
    "回收站":"回收站",
    "汇报模板":"汇报模板",
    "系统通知":"系统通知",
    "操作日志":"操作日志",
    "流程模板":"流程模板",
    "流程":"流程",
    "流程数据":"流程数据",
    "角色权限":"角色权限",
    "组织架构":"组织架构",
    "成员列表":"成员列表",
    "项目角色":"项目角色",
    "企业角色":"企业角色",
    "代码关联":"代码关联",
    "代码分支":"代码分支",
    "系统设置":"系统设置",
    "对象类型":"对象类型",
    "项目模板":"项目模板",
    "参数设置":"参数设置",
    "数据表格":"数据表格",
    "系统Hook":"系统Hook",
    "没有权限":"没有权限",
    "其它":"其它"
    }
    }
</i18n>

<template>
    <div class="layout">
            <Header class="layout-header">
                <Row>
                    <Col span="6" class="nav-left">
                        <div class="nav-left-box">
                        <div>
                            <img v-if="companyInfo.imageId" @click="showIndexPage" :src="imageURL(companyInfo.imageId)" class="head-logo-custom"/>
                            <img v-else @click="showIndexPage" class="head-logo" src="/image/logo.png">
                        </div>
                        <div class="company-name">
                            <span >{{companyInfo.name}}</span>
                        </div>

                        </div>
                    </Col>
                    <Col span="18" class="nav-right">
                        <div style="display:inline-block;">
                            <Dropdown style="text-align:left;margin-left:10px" trigger="click">
                                <AvatarImage :name="account.name" v-model="account.imageId" type="label"/>
                                <DropdownMenu slot="list" style="width:250px" >
                                    <DropdownItem @click.native="logout">退出</DropdownItem>
                                </DropdownMenu>
                            </Dropdown>

                        </div>

                    </Col>
                </Row>
            </Header>

            <Content class="layout-content">
                <div class="side-menu scrollbox">
                    <div class="side-menu-wrap">
                        <template v-for="item in menuList" >
                            <div  :key="'group'+item.name" v-if="item.show" class="side-menu-item-group">{{item.name}}</div>
                            <div  v-for="menu in item.children"  v-if="menu.show" :key="menu.module"  @click="showModule(menu.module)"
                                class="side-menu-item" :class="{'side-menu-item-active':currentModule==menu.module}">{{menu.name}}</div>
                        </template>
                    </div>
                </div>
                <div class="router-content"><router-view></router-view></div>
            </Content>
    </div>


</template>
<script>

    export default {
        data () {
            return {
                currentModule:"company_info",
                account:{},
                companyInfo:{},
                menuList:[
                    {name:this.$t('基本信息'),
                        children:[
                            {name:this.$t('企业信息'),module:"company_info",permission:"company_admin"},
                            {name:this.$t('项目列表'),module:"company_project",permission:"company_admin_view_project"},
                            {name:this.$t('回收站'),module:"company_recycle_bin",permission:"company_admin_manage_recycle"},
                            {name:this.$t('汇报模板'),module:"company_report_template",permission:"company_report_template"},
                            {name:this.$t('系统通知'),module:"company_system_notification",permission:"company_admin_system_notifaction"},
                            {name:this.$t('操作日志'),module:"company_optlog",permission:"company_admin_manage"},
                    ]},

                    {name:this.$t('流程'),
                        children:[
                            {name:this.$t('流程模板'),module:"company_workflow_template",permission:"workflow_template_admin"},
                            {name:this.$t('流程数据'),module:"company_workflow_admin",permission:"workflow_data"},
                    ]},
                    {name:this.$t('角色权限'),children:[
                        {name:this.$t('组织架构'),module:"company_department",permission:"company_admin_manage_department"},
                        {name:this.$t('成员列表'),module:"company_user",permission:"company_admin_manage_member"},
                        {name:this.$t('项目角色'),module:"company_role_project",permission:"company_admin_manage_project_role"},
                        {name:this.$t('企业角色'),module:"company_role_global",permission:"company_admin_manage_company_role"},
                    ]},
                    {name:this.$t('供应商'),children:[
                        {name:this.$t('供应商'),module:"company_supplier",permission:"supplier_list"},
                        {name:this.$t('外包人员'),module:"company_supplier_member",permission:"supplier_list"},
                        {name:this.$t('考勤列表'),module:"company_dingtalk_attendance",permission:"attendance_list"},
                    ]},
                    {name:this.$t('其它'),children:[
                        {name:this.$t('代码关联'),module:"company_scm",permission:"company_admin"},
                        {name:this.$t('代码分支'),module:"company_scm_branch",permission:"company_admin"},
                    ]},

                ],
                privateDeployMenuList:[
                    {name:this.$t('系统设置'),children:[
                        {name:this.$t('对象类型'),module:"company_admin_object_type",permission:"company_admin_system_settings"},
                        {name:this.$t('项目模板'),module:"company_admin_project",permission:"company_admin_system_settings"},
                        {name:this.$t('参数设置'),module:"company_admin_setting",permission:"company_admin_system_settings"},
                        {name:this.$t('数据表格'),module:"company_datatable",permission:"company_admin_system_settings"},
                        {name:this.$t('系统Hook'),module:"company_hook",permission:"company_admin_system_settings"},
                        {name:'WebAPI',module:"company_webapi",permission:"company_admin_system_settings"},
                        /*{name:"版本升级",module:"company_upgrade",permission:"company_admin_system_settings"},*/
                    ]}
                ]
            }
        },
        watch:{
            '$route' (to, from) {
                this.loadData();
            },
        },
        mounted(){
            this.account=app.account;
            this.loadData();
        },
        methods:{
            setupMenuPermission(){
                if(this.companyInfo.version==2){
                    for(var i=0;i<this.privateDeployMenuList.length;i++){
                        this.menuList.push(this.privateDeployMenuList[i])
                    }
                }
                console.log(this.menuList)
                //
                this.menuList.forEach(item=>{
                    var itemPermissionCount=0;
                    item.children.forEach(menu=>{
                        menu.show=app.perm(menu.permission);
                        if(menu.show){
                            itemPermissionCount++;
                        }
                    })
                    //
                    item.show=itemPermissionCount>0;
                })
            },
            loadData(){
                if(app.perm('company_admin_manage')==false){
                    app.toast(this.$t('没有权限'));
                    window.location.href="/login.html";
                }
                //
                var companyUUID=this.$route.params.company;
                app.companyUUID=companyUUID;
                if(this.companyInfo.uuid!=companyUUID){
                     app.invoke('BizAction.getCompanyInfoByUuid',[app.token,companyUUID],(info)=>{
                        this.companyInfo=info;
                        document.title="CORNERSTONE "+info.name;
                        app.company=info;
                        this.setupMenuPermission();
                    })
                }
                var page= this.$route.params.page;
                this.currentModule=page;
                if(page){
                    app.loadPage(page)
                }
            },
            showIndexPage(){
                 window.location.href="/"
            },
            showModule(name){
                app.loadPage(name);
            },
            logout(){
                app.deleteCookie('token');
                window.location.href="/login.html"
            },
            imageURL: function (imgId) {
                console.log('app.serverAddr----->', app.serverAddr);
                return app.serverAddr + "/p/file/get_file/" + imgId + "?token=" + app.token;
            },
        },
    }
</script>
