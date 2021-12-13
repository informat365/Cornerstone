<style scoped>
.nav-tab-bar{
    color:#444;
    font-size:14px;
    font-weight: 500;
    display: inline-block;
    padding-bottom:4px;
    height: 48px;
    overflow: hidden;
}
.nav-tab-item{
    display: inline-block;
    min-width: 80px;
    max-width:80px;
    overflow: hidden;
    cursor: pointer;
    height: 48px;
    position: relative;
    cursor: pointer;
    user-select: none;
}
.nav-tab-item-active{
    color:#5391F0;
}

.nav-tab-item-more-active{
    color:#5391F0;
}

.nav-tab-item-active::after{
    height: 4px;
    width:100%;
    display: block;
    background:  rgba(35,145,255,1);
    content: '';
    position: absolute;
    bottom:0;
    left:0;
}
.router-content{
        width:calc(100vw - 200px);
        background-color: #fff;
        margin-left:200px;
        height: calc(100vh - 48px);
        overflow: auto;
}
.project-name-label{
    min-width: 100px;
    max-width:140px;
    display: inline-block;
}
.logo-name{
    font-size:20px;
    font-weight: bold;
    color:#333;
    display: inline-block;
    margin-right:20px;
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
}
.nav-left-box{
    display: flex;
    align-items: center;
}
</style>
<i18n>
{
	"en": {
        "代码助手": "代码助手",
		"退出": "退出",
		"代码设计器": "代码设计器",
		"数据库": "数据库",
		"代码模板": "代码模板",
		"代理": "代理",
		"无权限查看": "无权限查看"
    },
	"zh_CN": {
		"代码助手": "代码助手",
		"退出": "退出",
		"代码设计器": "代码设计器",
		"数据库": "数据库",
		"代码模板": "代码模板",
		"代理": "代理",
		"无权限查看": "无权限查看"
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
                            <img  class="head-logo" src="/image/logo.png">
                        </div>
                        <div class="company-name">
                            <span style="padding-left:10px;padding-right:10px">{{companyInfo.name}}</span>
                            <span>{{$t('代码助手')}}</span>
                        </div>

                        </div>
                    </Col>
                    <Col span="12" style="text-align:center">
                    <div class="nav-tab-bar" style="text-align:center">
                            <div
                                v-for="item in menuList" :key="item.url"
                                v-if="isModuleShow(item)"
                                @click="showModule(item.url)"
                                class="nav-tab-item"
                                :class="{'nav-tab-item-active':currentModule==item.url}">{{$t(item.name)}}</div>
                        </div>
                    </Col>
                    <Col span="6" class="nav-right">
                       <div style="display:inline-block;">
                            <Dropdown style="text-align:left;margin-left:10px" trigger="click">
                                <AvatarImage :name="account.name" v-model="account.imageId" type="label"/>
                                <DropdownMenu slot="list" style="width:250px" >
                                    <DropdownItem @click.native="logout">{{$t('退出')}}</DropdownItem>
                                </DropdownMenu>
                            </Dropdown>

                        </div>

                    </Col>
                </Row>
            </Header>

            <Content class="layout-content">
                 <router-view></router-view>
            </Content>
    </div>


</template>
<script>

    export default {
        data () {
            return {
                currentModule:"codegen_designer",
                companyInfo:{},
                account:{},
                menuList:[
                    {name:"代码设计器",url:"codegen_designer"},
                    {name:"数据库",url:"codegen_database"},
                    {name:"代码模板",url:"codegen_template"},
                    {name:"MySQL代理",url:"codegen_mysql_proxy"},
                ]
            }
        },
        mounted(){
            document.title="CORNERSTONE " + this.$t('代码助手')
            this.account=app.account;
            this.loadData();
        },
        methods:{
            loadData(){
                var companyUUID=this.$route.params.company;
                app.invoke('BizAction.getCompanyInfoByUuid',[app.token,companyUUID],(info)=>{
                    this.companyInfo=info;
                    app.company=info;
                    if(info==null||info.version!=2){
                        toast(this.$t("无权限查看"))
                        window.location.href="/login.html"
                    }
                })
                var page= this.$route.params.page;
                this.currentModule=page;
                if(this.currentModule==null){
                    this.currentModule="codegen_designer"
                }
                if(this.currentModule&&this.currentModule.indexOf('codegen_')!=-1){
                    app.loadPage(this.currentModule)
                }
            },
            isModuleShow(item){
                if(item.url=='codegen_designer'&&app.perm('codegen_code_desginer')){
                    return true;
                }
                if(item.url=='codegen_database'&&app.perm('codegen_database_config')){
                    return true;
                }
                if(item.url=='codegen_mysql_proxy'&&app.perm('codegen_edit_mysql_proxy')){
                    return true;
                }
                if(item.url=='codegen_template'&&app.perm('codegen_template_config')){
                    return true;
                }
                return false;
            },
            showModule(name){
                this.currentModule=name;
                app.loadPage(name);
            },
            showIndexPage(){
                app.loadPage('/pm/index/dashboard')
            },
            logout(){
                app.deleteCookie('token');
                window.location.href="/login.html"
            }
        },
    }
</script>
