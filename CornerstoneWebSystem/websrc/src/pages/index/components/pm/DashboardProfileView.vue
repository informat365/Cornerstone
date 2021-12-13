<style scoped>
    .profile-wrap{
        display: flex;
        flex-direction: row;
    }
    .profile-left{
        width:320px;
    }
    .profile-right{
        flex:1;
    }
  
    .profile-img{
        text-align: center;
        padding-top:50px;
    }
    .profile-avatar-img{
        box-shadow: 0 4px 6px 0 rgba(31, 31, 31, 0.05), 0 0 2px 0 rgba(31, 31, 31, 0.2);
    }
    .profile-card{
        background-color: #fff;
        border-radius: 3px;
        background-color: #fff;
        box-shadow: 0 4px 6px 0 rgba(31, 31, 31, 0.05), 0 0 2px 0 rgba(31, 31, 31, 0.2);
        transition: box-shadow 0.2s ease;
        display: flex;
        flex-direction: column;
        margin: 15px;
    }
    .user-role{
        font-size:12px;
        margin-right:5px;
        display: inline-block;
    }
    .project-item{
        font-size:12px;
        background-color: #EEEEEE;
        border-radius: 7px;
        padding:2px 4px;
        margin-right:5px;
        display: inline-block;
        margin-top:7px;
        cursor: pointer;
    }
    .profile-num-box{
        text-align: center;
        cursor: pointer;
    }
    .profile-num{
        font-size:35px;
        font-weight: bold;
    }
    .profile-num-desc{
        color:#666;
        font-size:13px;
    }
     .log-item{
        position: relative;
        margin-top:20px;
    }
    .log-item-right{
        display: flex;
        width:100%;
        align-items: center;
    }
    .log-item-name{
        font-size:12px;
        color:#333;
    }
    .log-item-time{
        font-size:12px;
        color:#999;
        padding-right:10px;
    }
    .section-title{
        font-size:15px;
        color:#555;
        margin-bottom:10px;
    }
</style>
<i18n>
{
    "en": {
        "暂无数据": "No Data"
    },
    "zh_CN": {
        "暂无数据": "暂无数据"
    }
}
</i18n> 
<template> 
<div >
    <div v-if="info" class="profile-wrap">
    <div class="profile-left">
        <div class="profile-card">
          
            <div class="profile-img">
                <AvatarImage 
                    class="profile-avatar-img"
                    size="verylarge"
                    :name="info.account.name" 
                    :value="info.account.imageId" 
                    type="none"></AvatarImage>
                
                <div style="text-align:center;">
                    <div style="padding-top:10px;font-size:18px;font-weight:bold">{{info.account.name}}</div>
                    <div style="padding-top:5px;font-size:13px;">{{info.company.name}}</div>
                    <div style="text-align:right"><IconButton @click="showAccountSetting()" icon="ios-settings-outline"/>
                    </div>
                </div>
            </div>
          
            <div style="border-top:1px solid #eee;padding:25px;margin-top:15px">
                <div>
                    <span style="font-size:12px;color:#666;margin-right:10px">角色</span>
                    <span v-for="role in info.roles" :key="'role'+role.id" class="user-role">{{role.name}}</span>
                </div>
                <div style="margin-top:7px">
                    <span style="font-size:12px;color:#666;margin-right:10px">部门</span>
                    <span v-for="role in info.departmentList" :key="'role'+role.id" class="user-role">{{role.name}}</span>
                </div>
            </div>
            
        </div>


        <div class="profile-card" style="padding:25px">
            <div class="section-title">参与的项目({{info.projectList.length}})</div>
            <div>
                <span @click="showProject(item)" v-for="item in info.projectList" :key="item.id" class="project-item">
                    {{item.name}}
                </span>
            </div>
        </div>
    </div>
    <div class="profile-right">
        <div class="profile-card" style="padding:25px">
            <Row>
                <Col @click.native="showMenu('todo')" span="6" class="profile-num-box">
                    <div class="profile-num numberfont">{{info.todoTaskNum}}</div>
                    <div class="profile-num-desc">待办</div>
                </Col>
                <Col  @click.native="showMenu('todo')" span="6" class="profile-num-box">
                    <div class="profile-num numberfont">{{info.delayTaskNum}}</div>
                    <div class="profile-num-desc">延期</div>
                </Col>
                <Col @click.native="showMenu('my')" span="6" class="profile-num-box">
                    <div class="profile-num numberfont">{{info.createTaskNum}}</div>
                    <div class="profile-num-desc">创建</div>
                </Col>
                <Col @click.native="showMenu('project')" span="6" class="profile-num-box">
                    <div class="profile-num numberfont">{{info.projectList.length}}</div>
                    <div class="profile-num-desc">参与项目</div>
                </Col>
            </Row>
        </div>

        <div v-if="eventList.length>0" class="profile-card" style="padding:25px">
            <div class="section-title">活动图</div>
            <HeatMap  :free="false" :event-list="eventList"></HeatMap>
        </div>

        <div v-if="eventList.length>0" class="profile-card" style="padding:25px">
            <div class="section-title">动态</div>
            <template v-for="(item,idx) in eventList">
            <div class="log-item" v-if="idx<100" :key="'log'+item.id" >
                    <div class="log-item-right">
                        <div style="width:80px">
                            <span class="log-item-time">{{item.createTime|fmtDeltaTime}}</span>
                        </div>
                        <ProjectChangeLogItem  style="flex:1" :item="item"></ProjectChangeLogItem>
                    </div>
            </div>
            </template>
        </div>
    </div>

</div>
</div>
</template>

<script>
export default {
    mixins: [componentMixin],
    data(){
        return { 
            info:null,
            eventList:[]
        }
    },  
    mounted(){
        this.loadData();
    },
    methods:{ 
        showAccountSetting(){
            app.showDialog(AccountSettingDialog)
        },
        showMenu(item){
            app.loadPage('?view='+item)
        },
        loadData(){
            app.invoke("BizAction.getAccountProfileInfo",[app.token],info => {
                this.info=info;
                this.setupEventList();
            });
        },
        setupEventList(){
            var logList=this.info.changeLogList.list;
            logList.map(item=>{
                    if(item.items!=''&&item.items!=null){
                        item.items=JSON.parse(item.items);
                    }else{
                        item.items={}
                    }
                    if(item.items.author){
                        item.createAccountName=item.items.author;
                    }
            })
            this.eventList=logList;          
        },
        showProject(item) {
            app.loadPage("/pm/project/" + item.uuid + "/project");
        },
    }
}
</script>