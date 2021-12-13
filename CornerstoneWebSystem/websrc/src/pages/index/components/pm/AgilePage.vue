<style scoped lang="less">
    .page {
        display: flex;
    }

    .page-menu {
        width: 260px;
        height: calc(100vh - 50px);
        overflow: auto;
        background-color: #fff;
        border-right: 1px solid rgba(216, 216, 216, 1);
        padding: 25px;
        user-select: none;
    }

    @media (max-width: 1000px) {
        .page-menu {
            display: none;
        }

        .silde-control-bar {
            display: none;
        }
    }

    .page-right-content {
        flex: 1;
        height: calc(100vh - 50px);
        overflow: auto;
        padding: 0px;
    }

    .menu-item {
        font-size: 15px;
        font-weight: 500;
        padding: 8px;
        color: #666;
        cursor: pointer;
        display: flex;
        align-items: center;
        position: relative;
        transition: all 0.3s;
    }

    .menu-item:hover {
        color: #009cf1;
    }

    .menu-item-active {
        color: #009cf1;
        font-weight: bold;
    }

    .menu-item-group {
        font-size: 12px;
        color: #999;
        padding: 8px;
        margin-top: 20px;
    }


    .silde-control-bar {
        width: 6px;
        position: absolute;
        top: 100px;
        left: 260px;
    }

    .silde-control-bar-close {
        left: 0px;
    }

    .hide-side-button {
        position: absolute;
        right: 0px;
        top: 32px;
        width: 6px;
        height: 40px;
        background-color: rgba(216, 216, 216, 1);
        cursor: pointer;
        color: #fff;
        border-radius: 2px;
    }

    .hide-side-button:hover {
        background-color: #5391f0;
    }

    .hide-side-button-icon {
        position: absolute;
        left: -3px;
        top: 13px;
    }

    .aglie-filter {
        margin-top: 25px;
        padding-left: 10px;

        &-item {
            padding: 10px 0;

            &-title {
                font-weight: 600;
                color: #9b9b9b;
            }

            &-content {
                display: flex;
                flex-wrap: wrap;
                justify-content: flex-start;

                /deep/ input{
                    outline: none !important;
                    /*border: none !important;*/
                    border-radius: 16px !important;
                }

                span {
                    display: inline-block;
                    /*height: 26px;*/
                    /*line-height: 26px;*/
                    border-radius: 12px;
                    padding: 2px 6px;
                    font-size: 13px;
                    background-color: #ddd;
                    margin-right: 8px;
                    cursor: pointer;

                    &.choosed {
                        background-color: #01a0e4;
                        color: #fff;
                    }
                }
            }
        }
    }


</style>
<i18n>
    {
    "en": {
    "迭代": "Iteration",
    "子系统":"System",
    "Release":"Release"
    },
    "zh_CN": {
    "迭代": "迭代",
    "子系统":"子系统",
    "Release":"Release"
    }
    }
</i18n>
<template>
    <div class="page">
        <div class="silde-control-bar" :class="{'silde-control-bar-close':showSideMenu==false}">
            <div @click="showSideMenu=!showSideMenu" class="hide-side-button">
                <Icon v-if="showSideMenu" class="hide-side-button-icon" type="md-arrow-dropleft"/>
                <Icon v-if="showSideMenu==false" class="hide-side-button-icon" type="md-arrow-dropright"/>
            </div>
        </div>

        <div class="page-menu scrollbox" v-if="showSideMenu">
            <div
                v-if="prjPerm('agility_iteration')"
                class="menu-item"
                @click="showMenu('iteration')"
                :class="{'menu-item-active':selectMenu=='iteration'}">
                <Icon type="md-albums" style="margin-right:10px"/>
                {{$t('迭代')}}
            </div>
            <div
                v-if="prjPerm('agility_subsystem')"
                class="menu-item"
                @click="showMenu('system')"
                :class="{'menu-item-active':selectMenu=='system'}">
                <Icon type="md-apps" style="margin-right:10px"/>
                {{$t('子系统')}}
            </div>
            <div
                v-if="prjPerm('agility_release')"
                class="menu-item"
                @click="showMenu('release')"
                :class="{'menu-item-active':selectMenu=='release'}">
                <Icon type="md-paper-plane" style="margin-right:10px"/>
                {{$t('Release')}}
            </div>

            <div class="aglie-filter">
                <div class="aglie-filter-item">
                    <div class="aglie-filter-item-title">名称</div>
                    <div class="aglie-filter-item-content">
                        <Input suffix="ios-search" placeholder="按名称搜索" clearable v-model="filter.name"
                               style="width: 200px;"/>
                    </div>
                </div>
                <div class="aglie-filter-item" v-if="selectMenu!='release'&&(prjPerm('agility_iteration')||prjPerm('agility_subsystem'))">
                    <div class="aglie-filter-item-title">状态</div>
                    <div class="aglie-filter-item-content">
                       <span @click="choose(status.name,'status')" :class="{choosed:hasChoose(status.name,'status')}"
                             v-for="(status,idx) in filter.statusList" :key="'status_'+idx">{{status.name}} {{status.count}}</span>
                    </div>
                </div>
                <div class="aglie-filter-item" v-if="selectMenu=='release'&&prjPerm('agility_release')">
                    <div class="aglie-filter-item-title">分类</div>
                    <div class="aglie-filter-item-content">
                         <span @click="choose(cate.name,'category')" :class="{choosed:hasChoose(cate.name,'category')}"
                               v-for="(cate,idx) in filter.categoryList" :key="'cate_'+idx">{{cate.name}} {{cate.count}}</span>
                    </div>
                </div>
            </div>
        </div>
        <div class="page-right-content scrollbox">
            <AgileIterationView ref="iterationView" v-if="selectMenu=='iteration'" :filter="iterationFilter" @on-filter="setFilterData($event,'statusList')"></AgileIterationView>
            <AgileSystemView ref="systemView" v-if="selectMenu=='system'" :filter="systemFilter" @on-filter="setFilterData($event,'statusList')"></AgileSystemView>
            <AgileReleaseView ref="releaseView" v-if="selectMenu=='release'" :filter="releaseFilter" @on-filter="setFilterData($event,'categoryList')"></AgileReleaseView>
        </div>
    </div>
</template>

<script>

    export default {
        mixins: [componentMixin],
        computed: {
            permMeuns() {
                const menus = [];
                if (this.prjPerm('agility_iteration')) {
                    menus.push('iteration');
                }
                if (this.prjPerm('agility_subsystem')) {
                    menus.push('system');
                }
                if (this.prjPerm('agility_release')) {
                    menus.push('release');
                }
                return menus;
            },
            iterationFilter(){
                return {
                    name:this.filter.name,
                    status:this.filter.status
                }
            } ,
            systemFilter(){
                return {
                    name:this.filter.name,
                    status:this.filter.status
                }
            } ,
            releaseFilter(){
                return {
                    name:this.filter.name,
                    category:this.filter.category
                }
            } ,
        },
        data() {
            return {
                title: 'AgilePage',
                account: {},
                selectMenu: null,
                showSideMenu: true,
                filter: {
                    name: null,
                    category: [],
                    status: [],
                    categoryList: [],
                    statusList: []
                }
            };
        },
        methods: {
            choose(val, type) {
                if(type==='status'){
                    let idx = this.filter.status.findIndex(k=>k===val);
                    if(idx!==-1){
                        this.filter.status.splice(idx,1);
                    }else{
                        this.filter.status.push(val);
                    }
                }else if(type==='category'){
                    let idx = this.filter.category.findIndex(k=>k===val);
                    if(idx!==-1){
                        this.filter.category.splice(idx,1);
                    }else{
                        this.filter.category.push(val);
                    }
                }
                console.log(this.filter)
            },
            hasChoose(val,type) {
                if(type==='status'){
                    return this.filter.status.some(k=>k===val);
                }else if(type==='category'){
                    return this.filter.category.some(k=>k===val);
                }
            },
            pageLoad() {
                this.account = app.account;
                if (this.permMeuns.length === 0) {
                    return;
                }
                let selectMenu = this.args.view;
                if (!!selectMenu && selectMenu === this.selectMenu) {
                    return;
                }
                if (!selectMenu || this.permMeuns.indexOf(selectMenu) === -1) {
                    selectMenu = this.permMeuns[0];
                }
                this.selectMenu = selectMenu;
                app.loadPage('?view=' + selectMenu);
            },
            pageUpdate() {
                if (this.permMeuns.length === 0) {
                    return;
                }
                let selectMenu = this.args.view;
                if (!!selectMenu && selectMenu === this.selectMenu) {
                    return;
                }
                if (!selectMenu || this.permMeuns.indexOf(selectMenu) === -1) {
                    selectMenu = this.permMeuns[0];
                }
                this.selectMenu = selectMenu;
                app.loadPage('?view=' + selectMenu);
            },
            pageMessage(type, value) {
                if (type == 'iteration.edit') {
                    if (this.$refs.iterationView) {
                        this.$refs.iterationView.loadData();
                    }
                }
                if (type == 'release.edit') {
                    if (this.$refs.releaseView) {
                        this.$refs.releaseView.loadData();
                    }
                }
                if (type == 'system.edit') {
                    if (this.$refs.systemView) {
                        this.$refs.systemView.loadData();
                    }
                }
            },
            showMenu(item) {
                this.filter.name = null;
                this.filter.status = [];
                this.filter.category = [];

                app.loadPage('?view=' + item);
            },
            setFilterData(data,type){
                this.filter[type] = data;
            },
        },
    };
</script>
