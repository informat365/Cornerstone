<style scoped>
.nav-tab-bar {
    color: #444;
    font-size: 14px;
    font-weight: 500;
    display: inline-block;
    padding-bottom: 4px;
    height: 48px;
    overflow: hidden;
}
.nav-tab-item {
    display: inline-block;
    min-width: 80px;
    max-width: 80px;
    overflow: hidden;
    cursor: pointer;
    height: 48px;
    position: relative;
    cursor: pointer;
    user-select: none;
}
.nav-tab-item-active {
    color: #5391f0;
}

.nav-tab-item-more-active {
    color: #5391f0;
}

.nav-tab-item-active::after {
    height: 4px;
    width: 50%;
    transform:translateX(50%);
    display: block;
    background: rgba(35, 145, 255, 1);
    content: "";
    position: absolute;
    bottom: 0px;
    left: 0;
    z-index:10;
}
.layout-header{
    display: flex;
    align-items: center;
    padding:5px 10px;
}
.header-left{
    padding-top:15px;
}
.header-right{
    flex:1;
    display: flex;
    flex-direction: row-reverse;
    align-items: center;
}
.router-content {
    width: calc(100vw - 200px);
    background-color: #fff;
    margin-left: 200px;
    height: calc(100vh - 48px);
    overflow: auto;
}
.flowname{
    max-width: 200px;
    font-size:14px;
    font-weight: bold;
    color:#666;
    display: inline-block;
    margin-right:10px;
}
</style>
<i18n>
{
	"en": {
        "保存": "保存",
		"基本信息": "基本信息",
		"表单设计": "表单设计",
		"工作流": "工作流",
		"填写权限": "填写权限",
		"数据权限": "数据权限",
		"流程设计": "流程设计",
		"当前页面没有保存是否保存": "当前页面没有保存，是否保存？"
    },
	"zh_CN": {
		"保存": "保存",
		"基本信息": "基本信息",
		"表单设计": "表单设计",
		"工作流": "工作流",
		"填写权限": "填写权限",
		"数据权限": "数据权限",
		"流程设计": "流程设计",
		"当前页面没有保存是否保存": "当前页面没有保存，是否保存？"
	}
}
</i18n>
<template>
    <div class="layout" >
        <div class="layout-header">
            <div class="header-left">
                <div class="nav-tab-bar" style="text-align:center">
                    <div
                        v-for="item in menuList"
                        :key="item.url"
                        @click="showModule(item.url)"
                        class="nav-tab-item"
                        :class="{'nav-tab-item-active':currentModule==item.url}"
                    >{{$t(item.name)}}</div>
                </div>
            </div>
            <div v-if="surveysDefine" class="header-right">
                  <Button @click="saveSurveysDefine" type="default">{{$t('保存')}}</Button>
                <div class="flowname text-no-wrap">
                    <span v-if="isSaved==false">*</span>
                    {{surveysDefine.name}}</div>
            </div>
        </div>
        <Content class="layout-content">
            <router-view v-if="surveysDefine"></router-view>
        </Content>
    </div>
</template>
<script>
export default {
    data() {
        return {
            currentModule: "info",
            surveysDefineId:null,
            surveysDefine:null,
            menuList: [
                { name: "基本信息", url: "info" },
                { name: "表单设计", url: "form" },
                { name: "填写权限", url: "op_permission" }
            ],
            isSaved:true
        };
    },
   
    mounted() {
        this.addKeyListener();
        this.currentModule=this.$route.params.page
        this.surveysDefineId=this.$route.params.uuid;
        this.loadSurveysDefine();
        app.onMessage('AppEvent',(event)=>{
            if(event.type=='surveys.template.edit'||event.type=='surveys.template.status.edit'){
                this.loadSurveysDefine();
            }
            if(event.type=='surveys.isSaved'){
                this.isSaved=event.content
            }
        });
    },
    methods: {
        addKeyListener(){
             document.addEventListener("keydown",this.keyHandler, false);
        },
        keyHandler(e){
           if (e.keyCode == 83 && (navigator.platform.match("Mac") ? e.metaKey : e.ctrlKey)) {
                e.preventDefault();
                this.saveSurveysDefine();;
            }
        },
        loadSurveysDefine(){
            app.invoke('SurveysAction.getSurveysDefineByUuid',[app.token,this.surveysDefineId],(info)=>{
                this.surveysDefine=info;
                app.surveysDefine=info;
                window.document.title=this.$t('表单设计')+"-"+info.name;
            });
        },
        saveSurveysDefine(){
            app.currentPage.save();
        },
        //
        showModule(name) {
            if(!app.currentPage.isPageSaved()){
                app.confirm(this.$t('当前页面没有保存是否保存'),()=>{
                    app.currentPage.save(()=>{
                        this.confirmShowModule(name)
                    });
                },()=>{
                    this.confirmShowModule(name)
                })
            }else{
                this.confirmShowModule(name)
            }
        },
        confirmShowModule(name){
            this.currentModule = name;
            app.loadPage(name);
        }
    }
};
</script>