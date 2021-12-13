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


</style>
<i18n>
    {
    "en": {
    "里程碑列表": "Landmark List",
    "里程碑概览":"Landmark Summary",
    "Release":"Release"
    },
    "zh_CN": {
    "里程碑列表": "里程碑列表",
    "里程碑概览":"里程碑概览",
    "Release":"Release"
    }
    }
</i18n>
<template>
    <div class="page">
        <div class="page-right-content scrollbox">
            <LandmarkListView ref="landmarkView"></LandmarkListView>
        </div>
    </div>
</template>

<script>

    export default {
        name:"LandmarkPage",
        mixins: [componentMixin],
        data() {
            return {
                title: '里程碑',
                account: {},
                selectMenu: 'view',
                showSideMenu: true,
            };
        },
        methods: {
            pageMessage(type, value) {
                if (type == 'landmark.edit') {
                    if (this.$refs.landmarkView) {
                        this.$refs.landmarkView.loadData();
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
