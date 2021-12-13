<style scoped>
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

</style>
<i18n>
    {
    "en": {
    "阶段列表": "Stage List",
    "阶段概览":"Stage Summary",
    "Release":"Release"
    },
    "zh_CN": {
    "阶段列表": "阶段列表",
    "阶段概览":"阶段概览",
    "Release":"Release"
    }
    }
</i18n>
<template>
    <div class="page">
        <div class="silde-control-bar" :class="{'silde-control-bar-close':showSideMenu==false}">
            <div @click="showSideMenu=!showSideMenu" class="hide-side-button">
                <Icon v-if="showSideMenu" class="hide-side-button-icon" type="md-arrow-dropleft" />
                <Icon v-if="showSideMenu==false" class="hide-side-button-icon" type="md-arrow-dropright" />
            </div>
        </div>

        <div class="page-menu scrollbox" v-if="showSideMenu">
            <div
                v-if="prjPerm('stage_list')"
                class="menu-item "
                @click="showMenu('view')"
                :class="{'menu-item-active':selectMenu=='view'}">
                <Icon type="md-albums" style="margin-right:10px" />
                {{$t('阶段列表')}}
            </div>
            <div
                v-if="prjPerm('stage_list')"
                class="menu-item "
                @click="showMenu('summary')"
                :class="{'menu-item-active':selectMenu=='summary'}">
                <Icon type="md-apps" style="margin-right:10px" />
                {{$t('阶段概览')}}
            </div>
        </div>

        <div class="page-right-content scrollbox">
            <StageListView ref="stageView" v-show="selectMenu=='view'"></StageListView>
            <StageSummaryView ref="stageSummaryView"  v-show="selectMenu=='summary'"></StageSummaryView>
        </div>
    </div>
</template>

<script>

    export default {
        name:"StagePage",
        mixins: [componentMixin],
        data() {
            return {
                title: '阶段',
                account: {},
                selectMenu: 'view',
                showSideMenu: true,
            };
        },
        methods: {
            /*pageLoad() {
                console.log(this.args)
                this.account = app.account;
                let selectMenu = this.args.view;
                if (!!selectMenu && selectMenu === this.selectMenu) {
                    return;
                }
                if(!!selectMenu){
                    this.selectMenu = selectMenu;
                    app.loadPage('?view=' + selectMenu);
                }else{
                    this.selectMenu = 'view';
                    app.loadPage('?view=view' );
                }
            },
            pageUpdate() {
                let selectMenu = this.args.view;
                if (!!selectMenu && selectMenu === this.selectMenu) {
                    return;
                }
                if(!!selectMenu){
                    this.selectMenu = selectMenu;
                    app.loadPage('?view=' + selectMenu);
                }else{
                    this.selectMenu = 'view';
                    app.loadPage('?view=view' );
                }
            },*/
            pageMessage(type, value) {
                if (type == 'stage.edit') {
                    if (this.$refs.stageView) {
                        this.$refs.stageView.loadData();
                    }
                }
            },
            showMenu(item) {
                this.selectMenu = item;
                // app.loadPage('?view=' + item);
            },
        },
    };
</script>
