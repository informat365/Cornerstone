<style>
.search-project-name{
    font-size:14px;
    color:#666;
    padding:10px 20px;
    padding-left:5px;
    font-weight: 400;
    cursor: pointer;
}
.search-project-name:hover{
    background-color:#f3f3f3;
}
.iteration-item{
    font-size:14px;
    color:#666;
    padding:10px 20px;
    font-weight: 400;
    cursor: pointer;
}
.iteration-item:hover{
    background-color:#f3f3f3;
}
.popup-search-name{
    max-width:220px;
    display: inline-block;
    line-height: 1;
    vertical-align: middle;
    color:#2f2e2e;
}
.iteration-item-status{
    color:#999;
    font-size:12px;
    text-align: right;
}
.head-logo{
    cursor: pointer;
}

.dropdown-icon{
    color:#A7A7A7;
    cursor: pointer;
    font-size:16px;
}
.top-nav-bar{
    display: flex;
}
.nav-left-box{
    display: flex;
    align-items: center;
    width:350px;
}
.nav-toolbar{
    flex:1;
    text-align: left;
    overflow: hidden;
    height: 48px;
    display: flex;
}
.nav-opt-bar{
    width:300px;
}
.project-name-box{
   display: inline-block;
   height: 48px;
   user-select: none;
}
.project-name{
    max-width: 180px;
    text-overflow:ellipsis;
	overflow: hidden;
	white-space: nowrap;
    font-size:14px;
    font-weight: bold;
    color:#444;
    cursor: pointer;
    height: 40px;
    margin-top:-10px;
}
.project-name-active{
    color:#0097F7;
}
.iteration-name{
    height: 20px;
    color: #666;
    font-size: 12px;
    margin-top: -6px;
    line-height: 20px;
    cursor: pointer;
    max-width: 180px;
}

.iteration-name-inner{
    max-width: 120px;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    display: inline-block;
}

.logo-name{
    font-size:20px;
    font-weight: bold;
    color:#333;
    display: inline-block;
    margin-right:20px;
}

.popup-icon{
    width:30px;
}
.top-header{
    transition: all 0.5s;
}
.evulate-box{
    position:fixed;
    top:0;
    left:0;
    background-color: #de4307;
    color:#fff;
    font-size: 12px;
    z-index:9999;
}

</style>
<i18n>
{
    "en": {
        "创建项目":"New Project",
        "帮助中心":"Help",
        "邀请成员":"Invite Member",
        "搜索":"Search",
        "通知":"Notification",
        "日历":"Calendar",
        "CMDB":"CMDB",
        "笔记":"Note",
        "代码助手":"CodeGen",
        "工时统计":"Worktime Stat",
        "账号设置":"Account Setting",
        "企业设置":"Company Setting",
        "切换工作台":"Use new desktop",
        "版本库管理":"Version Repository Setting",
        "创建企业":"New Company",
        "演示模式":"Stage Mode",
        "退出演示模式":"Exit Stage Mode",
        "关于CORNERSTONE":"About CORNERSTONE",
        "关于":"About",
        "清除本地缓存":"Clear local cache",
        "清除缓存提醒":"Clear cache reminder",
        "清除提示内容":"<div><div style='font-size: 16px;font-weight: bold;padding-bottom: 10px;'>Are you sure you want to clear the local data cache?</div><div>This action will clear your Task personalization <b>(filter conditions/table view, field display and sorting of Gantt chart)</b></div></div>",
        "清除成功":"Success",
        "退出":"Exit",
        "首页":"Dashboard"
    },
    "zh_CN": {
        "创建项目":"创建项目",
        "帮助中心":"帮助中心",
        "邀请成员":"邀请成员",
        "搜索":"搜索",
        "通知":"通知",
        "日历":"日历",
        "笔记":"笔记",
        "CMDB":"CMDB",
        "代码助手":"代码助手",
        "工时统计":"工时统计",
        "账号设置":"账号设置",
        "企业设置":"企业设置",
        "切换工作台":"切换工作台",
        "创建企业":"创建企业",
        "版本库管理":"版本库管理",
        "演示模式":"演示模式",
        "退出演示模式":"退出演示模式",
        "关于":"关于",
        "关于CORNERSTONE":"关于CORNERSTONE",
        "清除本地缓存":"清除本地缓存",
        "清除缓存提醒":"清除缓存提醒",
        "清除提示内容":"<div><div style='font-size: 16px;font-weight: bold;padding-bottom: 10px;'>确定要清除本地的数据缓存吗?</div><div>此操作会清除您的任务个性化设置<b>(过滤条件/表格视图、甘特图的字段显示和排序)</b></div></div>",
        "清除成功":"清除成功",
        "退出":"退出",
        "首页":"工作台"
    }
}
</i18n>
<template>
    <div class="layout">
        <div v-if="company.version==2&&company.status!=1" class="evulate-box">
            评估试用版
        </div>
        <Header class="layout-header top-header">
                <div class="top-nav-bar">
                    <div  class="nav-left">
                        <div class="nav-left-box">
                            <div @click="clickLogoBox" style="margin-right:20px;cursor:pointer">
                                <SystemLogo v-model="company.imageId"></SystemLogo>
                            </div>

                            <IconButton style="color:#0BA2EF"
                            v-if="perm('company_create_project')"
                            v-show="projectUUID==null"
                            icon="md-add"
                            :size="16"
                            @click="showAddProjectDialog"  :tips="$t('创建项目')"></IconButton>


                            <div class="project-name-box" >
                                <div class="project-name" :class="{'project-name-active':currentModule=='project'}"
                                    style="margin-top:0"
                                    @click="showProjectPage"><span>{{projectName}}</span></div>
                            </div>

                            <div>
                                <ProjectSelectPopup :list="projectList"></ProjectSelectPopup>
                            </div>

                        </div>
                    </div>
                    <div class="nav-toolbar">
                        &nbsp;
                        <ModuleTabbar v-if="projectUUID!=null" :current="currentModule" :value="moduleList" @change="showModule"></ModuleTabbar>
                        <ProjectTabbar v-if="projectUUID==null" :value="projectList"/>
                    </div>
                    <div class="nav-opt-bar">
                        <div style="float:right;display:inline-block;">
                            <QuickCreatePopup :list="projectList"></QuickCreatePopup>

                            <IconButton :transfer="true" @click="clickHelpBtn" size="20" icon="md-help-circle" :tips="$t('帮助中心')"></IconButton>
                            <IconButton :transfer="true" @click="showNoteView"  icon="md-bookmarks" :tips="$t('笔记')"></IconButton>



                            <IconButton :transfer="true"   @click="showSearch=true" size="20" icon="ios-search" :tips="$t('搜索')"></IconButton>
                            <IconButton :transfer="true" @click="showNotice=true;unReadNotificationNum=0" :badge="unReadNotificationNum>0" icon="md-notifications" :tips="$t('通知')"></IconButton>
                            <IconButton :transfer="true"  style="margin-right:10px" @click="showCalendarView"  icon="ios-calendar" :tips="$t('日历')"></IconButton>
                            <IconButton style="color:#009CF1" :transfer="true" v-if="company&&company.version==1" size="24" @click="showInviteDialog" icon="ios-person-add" :tips="$t('邀请成员')"></IconButton>


                            <Dropdown transfer style="text-align:left;" transfer-class-name="user-drop-down" trigger="click" @on-click="clickAccountMenu">
                                <AvatarImage style="cursor:pointer"
                                    :name="account.name"
                                    type="none" v-model="account.imageId"/>
                                <DropdownMenu slot="list" style="width:250px" >
                                    <DropdownItem @click.native="switchCompany(item.uuid)" v-for="item in companyList" :key="item.id">
                                        {{item.name}}
                                        <Icon v-if="item.uuid==company.uuid" style="float:right" type="md-checkmark" />
                                    </DropdownItem>

                                    <DropdownItem v-if="company&&company.version==2&&perm('cmdb_view')" name="cmdb" divided>{{$t('CMDB')}}</DropdownItem>
                                    <DropdownItem v-if="perm('version_repository_view')&&!isRepositoryVersionExtension" name="version_repository">
                                        {{ $t('版本库管理') }}
                                    </DropdownItem>
                                    <DropdownItem v-if="company&&company.version==2&&perm('codegen_view')" name="codegen">{{$t('代码助手')}}</DropdownItem>

                                    <DropdownItem name="workTimeStat" divided> {{$t('工时统计')}}</DropdownItem>
                                    <DropdownItem name="accountSetting" divided> {{$t('账号设置')}}</DropdownItem>
                                    <DropdownItem v-if="perm('company_admin_manage')"  name="companySetting">{{$t('企业设置')}}</DropdownItem>
                                    <DropdownItem v-if="company&&company.version===1" name="createCompany">{{$t('创建企业')}}</DropdownItem>
                                    <DropdownItem name="newDesktop" >
                                        <template >{{$t('切换工作台')}}</template>
                                    </DropdownItem>
                                    <DropdownItem name="fullScreenMode" divided>
                                        <template v-if="inFullscreen==false">{{$t('演示模式')}}</template>
                                        <template v-if="inFullscreen">{{$t('退出演示模式')}}</template>
                                    </DropdownItem>
                                    <DropdownItem name="about">
                                        <div>{{$t('关于CORNERSTONE')}}</div>
                                        <div style="font-size:12px;color:#bbb" v-if="websocketEventConnected">实时更新已启用</div>
                                    </DropdownItem>
                                    <DropdownItem name="cleanCache" divided>
                                        {{ $t('清除本地缓存') }}
                                    </DropdownItem>
                                    <DropdownItem name="logout" divided>{{$t('退出')}}</DropdownItem>
                                </DropdownMenu>
                            </Dropdown>
                        </div>

                    </div>
                </div>
            </Header>

            <transition name="slide">
                <ProjectMainFrameNoticePopup @popup-close="showNotice=false" v-if="showNotice"></ProjectMainFrameNoticePopup>
            </transition>
            <transition name="slidedownslow">
             <ProjectMainFrameSearchPopup @popup-close="showSearch=false" v-if="showSearch"></ProjectMainFrameSearchPopup>
            </transition>
            <transition name="slidedown">
                <ProjectCalendarView @popup-close="showCalendar=false" v-if="showCalendar"></ProjectCalendarView>
            </transition>
            <transition name="slidedown">
                <NoteView @popup-close="showNote=false" v-show="showNote"></NoteView>
            </transition>

            <Content class="layout-content">
                <router-view v-if="projectShowInfoLoaded"></router-view>
            </Content>
    </div>
</template>
<script>
    export default {
        mixins: [componentMixin],
        data () {
            return {
                projectSearchContent:null,
                account:{},
                companyList:[],
                projectList:[],
                company:{
                    uuid:null,
                    imageId:null,
                    version:0,
                },
                projectName:this.$t("首页"),
                projectUUID:null,
                projectColor:null,
                showNotice:false,
                showCalendar:false,
                showNote:false,
                showSearch:false,
                moduleList:[],
                currentModule:null,
                unReadNotificationNum:0,
                projectShowInfoLoaded:false,
                inFullscreen:false,
                websocketEventConnected:null,
                isSuperBoss:false,
                showNewHome:app.showNewHome,
                isRepositoryVersionExtension:app.isRepositoryVersionExtension
            }
        },
        watch:{
            '$route' (to, from) {
                if(to.params.project!=from.params.project){
                    this.loadProject();
                }
                this.currentModule=this.$route.params.page;
                app.postMessage('module.change',this.currentModule)
            },
        },
        mounted(){
            document.body.ondrop =  (event)=>{
                event.preventDefault();
                event.stopPropagation();
            }
            //
            app.onMessage('AppEvent',(event)=>{
                if(event.type=='project.edit'||
                    event.type=='account.edit'){
                    app.getLoginInfo(()=>{
                        this.account=app.account;
                        this.projectList=app.projectList;
                        this.loadProject();
                        app.postMessage('appaccount.update')
                    })
                }
                if(event.type=='project.delete'||event.type=='project.archive'){
                    app.loadPage('/pm/index/dashboard')
                }
                if(event.type=='project.show'){
                    for(var i=0;i<this.projectList.length;i++){
                        var t=this.projectList[i];
                        if(t.uuid==event.content){
                            t.lastAccessTime=new Date().getTime();
                        }
                    }
                }
            })
            this.account=app.account;
            this.isSuperBoss = app.account.superBoss>0;
            this.companyList=app.companyList;
            this.projectList=app.projectList;
            this.company.uuid=app.company.uuid;
            this.company.imageId=app.company.imageId;
            this.company.version=app.company.version;
            this.company.status=app.company.status;

            this.loadProject();
            //
            this.heartbeat();
            setInterval(this.heartbeat,30*1000*5);
            //
            if(app.account.needUpdatePassword){
                app.showDialog(AccountChangePwdDialog)
            }
            //
            //app.showDialog(WelcomeDialog)
            this.checkInviteCode();
            //
            this.setupEventWebSocket();
        },
        methods:{
            websocketEventFire(msg){
                if(msg.type==1||msg.type==2||msg.type==3||msg.type==4){
                    app.postMessage('websocket.taskUpdate',msg.data);
                    return;
                }
                if(msg.type==10||msg.type==11){
                    app.postMessage('websocket.pipelineUpdate',msg.data);
                    return;
                }
                if(msg.type==21||msg.type==22||msg.type==23){
                    app.postMessage('websocket.taskCategoryUpdate',msg.data);

                }
            },
            checkInviteCode(){
                var inviteCode=app.getCookie('inviteCode');
                if(inviteCode!=null&&inviteCode!=""){
                    app.invoke('BizAction.joinProjectMember',[app.token,inviteCode],(info)=>{
                        app.deleteCookie('inviteCode');
                        window.location.reload();
                    });
                }
            },
            loadProject(){
                app.terminalToken=this.$route.params.token;
                this.projectUUID=this.$route.params.project;
                this.currentModule=this.$route.params.page;
                var projectId=0;

                document.title="CORNERSTONE";
                this.projectName=this.$t("首页");
                this.projectColor=null;
                for(var i=0;i<this.projectList.length;i++){
                    if(this.projectList[i].uuid==this.projectUUID){
                        this.projectName=this.projectList[i].name;
                        document.title=this.projectName;
                        this.projectColor=this.projectList[i].color;
                        projectId=this.projectList[i].id;
                    }
                }
                //
                if(projectId>0){
                    this.projectShowInfoLoaded=false;
                    this.loadProjectShowInfo(projectId);
                }else{
                    this.projectShowInfoLoaded=true;
                }
                app.projectId=projectId;
                app.projectUUID=this.projectUUID;
            },
            loadProjectShowInfo(projectId){
                app.invoke('BizAction.getProjectShowInfo',[app.token,projectId],(info)=>{
                    this.moduleList=info.moduleList;
                    app.projectId=projectId;
                    app.moduleList=info.moduleList;
                    app.project = info;
                    app.projectPermissionMap={};
                    for(var i=0;i<info.permissionList.length;i++){
                        var t=info.permissionList[i];
                        app.projectPermissionMap[t]=true;
                    }
                    this.projectShowInfoLoaded=true;
                });
            },

            showProjectPage(){
                if(app.projectUUID==null||app.projectUUID==""){
                    return;
                }
                app.loadPage('project')
            },

            showCalendarView(){
                this.showCalendar=true;
            },
            showNoteView(){
                this.showNote=true;
            },
            showAddProjectDialog(){
                app.showDialog(ProjectCreateDialog)
            },

            showModule(item){
                app.currentModule=item;
                app.loadPage(item)
            },
            showInviteDialog(){
                app.showDialog(InviteUserDialog)
            },
            switchCompany(uuid){
                if(this.company.uuid!=uuid){
                    app.loadPage('/pm/index/switch_company?id='+uuid)
                }
            },
            clickLogoBox(){
                this.projectName=this.$t("首页");
                if(app.showNewHome){
                    app.loadPage('/pm/index/newDashboard')
                }else{
                    app.loadPage('/pm/index/dashboard')
                }
            },
            clickHelpBtn(){
                window.open('https://www.cornerstone365.cn/doc/doc.html')
            },
            clickAccountMenu(item) {
                if (item === 'cmdb') {
                    this.showPlugin('cmdb');
                } else if (item === 'codegen') {
                    this.showPlugin('codegen');
                } else if (item === 'workTimeStat') {
                    app.loadPage('/pm/index/worktime');
                } else if (item === 'accountSetting') {
                    app.showDialog(AccountSettingDialog);
                } else if (item === 'companySetting') {
                    window.open('/#/company/' + app.company.uuid + '/company_info');
                } else if (item === 'createCompany') {
                    app.loadPage('/main');
                } else if (item === 'version_repository') {
                    // this.showPlugin('version_repository');
                    app.loadPage('/pm/version/repository/');
                } else if (item === 'logout') {
                    app.invoke('BizAction.logout', [app.token], () => {
                        app.deleteCookie('token');
                        window.location.href = '/login.html';
                    });
                } else if (item === 'fullScreenMode') {
                    if (this.inFullscreen) {
                        exitFullscreen();
                        this.inFullscreen = false;
                    } else {
                        requestFullscreen();
                        this.inFullscreen = true;
                    }
                }else if(item==='newDesktop'){
                    this.showNewHome = !this.showNewHome;
                    if(this.showNewHome){
                        app.loadPage('/pm/index/newDashboard');
                    }else{
                        app.loadPage('/pm/index/dashboard');
                    }
                    app.showNewHome = this.showNewHome;
                    this.updateHomeSetting();
                } else if (item === 'about') {
                    app.showDialog(AboutDialog);
                } else if (item === 'cleanCache') {
                    this.cleanLocalCache();
                }
            },
            showPlugin(item){
                window.open(item+'.html#'+"/"+this.company.uuid+"/")
            },
            updateHomeSetting(){
                app.invoke('BizAction.updateAccountHomeVisible',[app.token,app.showNewHome],(info)=>{});
            },
            cleanLocalCache(){
                this.$Modal.confirm({
                    title: this.$t('清除缓存提醒'),
                    content: this.$t('清除提示内容'),
                    loading: true,
                    onOk: () => {
                        this.doCleanLocalCache().then(() => {
                            app.toast(this.$t('清除成功'));
                            setTimeout(() => {
                                this.$Modal.remove();
                                window.location.reload();
                            }, 1500);
                        }).catch(() => {
                            app.toast(this.$t('清除成功'));
                            setTimeout(() => {
                                this.$Modal.remove();
                            }, 1500);
                        });
                    },
                });
            },
            doCleanLocalCache(){
                return new Promise((resolve,reject) => {
                    if (!window.localStorage) {
                        reject();
                        return;
                    }
                    for (const key in window.localStorage) {
                        if (!window.localStorage.hasOwnProperty(key)) {
                            continue;
                        }
                        if (key &&( key.indexOf('TaskPage') > -1||key.indexOf("_kanbansort")>-1||key.indexOf('_ganttSort')>-1)) {
                            console.log('localStorage.removed', key);
                            window.localStorage.removeItem(key);
                        }
                    }
                    resolve();
                });
            },
            heartbeat(){
                //检查websocket状态

                //
                app.invoke('BizAction.heartbeat',[app.token],(info)=>{
                    this.unReadNotificationNum=info.unReadNotificationNum;
                    var reportNum=info.myReportSubmitNum+info.myReportAuditNum;
                    app.postMessage('dashboard.reportnum',reportNum)
                    if(info.notificationList){
                        for(var i=0;i<info.notificationList.length;i++){
                            var t=info.notificationList[i];
                            if(t.type==1){//remind
                                try{
                                    var obj=JSON.parse(t.content);
                                    obj.notificationId=t.id;
                                }catch(e){
                                    console.log(e)
                                }
                            }
                        }
                        this.showWebNotification(info.notificationList);

                    }
                },(error)=>{},true)
                //心跳接口 隐藏loading
                if(this.websocketEventConnected==false){
                    this.setupEventWebSocket();
                }
                if(this.websocketEventConnected){
                    app.websocket.send('{}');
                }
            },
            showWebNotification(list){
                let _this =this;
                if(window.Notification==null){
                    return;
                }
                if (Notification.permission == "granted") {
                    _this.showWebNotification0(list);
                } else if (Notification.permission != "denied") {
                    Notification.requestPermission(function (permission) {
                        _this.showWebNotification0(list);
                    });
                }
            },
            showWebNotification0(list){
                var lastNotificationId=window.localStorage['lastNotificationId'];
                if(lastNotificationId==null){
                    lastNotificationId=0;
                }
                var lastId=0;
                //lastNotificationId=0;
                list.forEach(item=>{
                    if(item.id>lastId){
                        lastId=item.id;
                    }
                    if(item.id>lastNotificationId){
                        var content=JSON.parse(item.content);
                        var body="";
                        if(content.task){
                            body="【"+content.task.objectTypeName+"】#"+content.task.serialNo+"-"+content.task.name;
                        }
                        var projectName="";
                        if(item.projectName){
                            projectName="【"+item.projectName+"】";
                        }
                        var notification = new Notification(
                            projectName+item.name, {
                                body: body,
                                icon: app.serverAddr+"/p/file/get_file/"+item.optAccountImageId
                            }
                        );
                    }
                })
                //
                window.localStorage['lastNotificationId']=lastId;
            },
            //
            setupEventWebSocket(){
                console.log('setupEventWebSocket:'+app.webEventPort)
                if(app.webEventPort==0){
                    return;
                }
                var httpsEnabled = window.location.protocol == "https:";
                var host=window.location.hostname;
                var httpPort=window.location.port;
                var url="";
                if(httpsEnabled){
                    url='wss://'+host+'/event/'+app.token;
                }else{
                    if(httpPort==80){
                        url='ws://'+host+'/event/'+app.token;
                    }else{
                        url='ws://'+host+":"+app.webEventPort+'/event/'+app.token;
                    }
                }
                var ws = new WebSocket(url);
                ws.onopen = (event)=>{
                    this.websocketEventConnected=true;
                };
                //
                ws.onmessage = (event)=>{
                    var msg=JSON.parse(event.data);
                    if(msg.optAccountId!=app.account.id){
                        this.websocketEventFire(msg);
                    }
                }
                //
                ws.onclose = (event)=> {
                    console.log('websocket event close')
                    this.websocketEventConnected=false;
                }
                //
                ws.onerror = (error)=>{
                    console.log(error)
                }
                //
                app.websocket=ws;
            }
            //
        }
    }
</script>
