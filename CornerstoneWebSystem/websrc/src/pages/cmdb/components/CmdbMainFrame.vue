<style scoped>
.side-menu {
    width: 200px;
    height: calc(100vh - 48px);
    overflow: auto;
    background-color: #404352;
    color: #fff;
    padding-top: 20px;
    position: absolute;
    top: 48px;
    left: 0;
}
.side-menu-wrap {
    position: relative;
}
.side-menu-item {
    padding: 10px 20px;
    font-size: 14px;
    cursor: pointer;
    position: relative;
    color: #ccc;
}
.side-menu-item:hover {
    background-color: #2e3341;
    color: #fff;
}
.side-menu-item-active {
    background-color: #2e3341;
}
.side-menu-item-active::before {
    content: "";
    position: absolute;
    top: 0px;
    left: 0;
    height: 40px;
    width: 3px;
    background-color: #1989fa;
}
.side-menu-item-group {
    font-size: 12px;
    color: #999;
    padding-left: 20px;
    margin-top: 20px;
}
.router-content {
    width: calc(100vw - 200px);
    background-color: #fff;
    margin-left: 200px;
    height: calc(100vh - 48px);
    overflow: auto;
}
.project-name-label {
    min-width: 100px;
    max-width: 140px;
    display: inline-block;
}
.logo-name {
    font-size: 20px;
    font-weight: bold;
    color: #333;
    display: inline-block;
    margin-right: 20px;
}
.company-name {
    display: inline-block;
    max-width: 180px;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    font-size: 14px;
    font-weight: 500;
    color: #333;
    margin-left: 20px;
}
.nav-left-box {
    display: flex;
    align-items: center;
}
</style>
<i18n>
{
	"en": {
		"退出": "退出",
		"资源": "资源",
		"主机": "主机",
		"系统": "系统",
		"实例": "实例",
		"交付物": "交付物",
		"图表": "图表",
		"系统关系图": "系统关系图",
		"设置": "设置23",
		"配置": "配置23"
	},
	"zh_CN": {
		"退出": "退出",
		"资源": "资源",
		"主机": "主机",
		"系统": "系统",
		"实例": "实例",
		"交付物": "交付物",
		"图表": "图表",
		"系统关系图": "系统关系图",
		"设置": "设置",
		"配置API": "配置API"
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
                            <img class="head-logo" src="/image/logo.png" />
                        </div>
                        <div class="company-name">
                            <span>{{companyInfo.name}} CMDB</span>
                        </div>
                    </div>
                </Col>
                <Col span="18" class="nav-right">
                    <div style="display:inline-block;">
                        <Dropdown style="text-align:left;margin-left:10px" trigger="click">
                            <AvatarImage
                                :name="account.name"
                                v-model="account.imageId"
                                type="label"
                            />
                            <DropdownMenu slot="list" style="width:250px">
                                <DropdownItem @click.native="logout">{{$t('退出')}}</DropdownItem>
                            </DropdownMenu>
                        </Dropdown>
                    </div>
                </Col>
            </Row>
        </Header>

        <Content class="layout-content">
            <div class="side-menu">
                <div class="side-menu-wrap">
                    <template v-for="item in menuList">
                        <div
                            :key="item.module"
                            v-if="item.module==null"
                            class="side-menu-item-group"
                        >{{$t(item.name)}}</div>
                        <div
                            :key="item.module"
                            v-if="item.module!=null"
                            @click="showModule(item.module)"
                            class="side-menu-item"
                            :class="{'side-menu-item-active':currentModule==item.module}"
                        >{{$t(item.name)}}</div>
                    </template>
                </div>
            </div>
            <div class="router-content">
                <router-view></router-view>
            </div>
        </Content>
    </div>
</template>
<script>
export default {
    data() {
        return {
            account: {},
            companyInfo: {},
            currentModule: "cmdb_machine",
            menuList: [
                { name: "资源" },
                { name: "主机", module: "cmdb_machine" },
                { name: "系统", module: "cmdb_application" },
                { name: "实例", module: "cmdb_instance" },
                { name: "交付物", module: "cmdb_artifacts" },

                { name: "图表" },
                { name: "系统关系图", module: "cmdb_application_graph" },
                { name: "设置" },
                { name: "配置API", module: "cmdb_api" },
                { name: "Robot", module: "cmdb_robot" }
            ]
        };
    },
    mounted() {
        document.title = "CORNERSTONE CMDB";
        app.$on("PageChangeEvent", page => {
            this.currentModule = page;
        });
        this.account = app.account;
        this.loadData();
    },
    methods: {
        loadData() {
            var companyUUID = this.$route.params.company;
            app.companyUUID = companyUUID;
            if (this.companyInfo.uuid != companyUUID) {
                app.invoke(
                    "BizAction.getCompanyInfoByUuid",
                    [app.token, companyUUID],
                    info => {
                        if (info == null) {
                            window.location.href = "/login.html";
                        } else {
                            this.companyInfo = info;
                            app.company = info;
                        }
                    }
                );
            }
            var page = this.$route.params.page;
            this.currentModule = page;
            if (page) {
                app.loadPage(page);
            }
        },
        showIndexPage() {
            app.loadPage("/pm/index/dashboard");
        },
        showModule(item) {
            app.loadPage(item);
        },
        logout() {
            app.deleteCookie("token");
            window.location.href = "/login.html";
        }
    }
};
</script>
