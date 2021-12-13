<template>
    <Modal
        v-model="showModal" :mask-closable="false" class="modal-edit" width="770">
        <div slot="header">
            版本列表
        </div>
        <div
            style="margin-bottom: 20px"
            v-if="perm('version_add')"
            class="version-list-card version-list-add row-first"
            @click="showCreateDialog">
            <Icon type="ios-add" size="30"/>
            <div class="text">添加版本</div>
        </div>
        <div class="version-list scrollbox" style="overflow:auto;max-height: 400px;" v-if="Array.isArray(versionList) && versionList.length > 0">
            <div style="height: 45px; width: 100%"></div>
            <template v-for="(item,index) in versionList">
                <VersionCard
                    @click.native="showEditDialog(item)"
                    class="version-list-card"
                    :class="{ 'row-first': index % 2 === 1 }"
                    :version="item"
                    :key="item.id"/>
            </template>
        </div>
        <div v-else class="nodata">暂无数据</div>
        <div slot="footer">
            <Button
                type="text" @click.native="showModal=false">关闭
            </Button>

        </div>
    </Modal>
</template>

<script>
    import VersionCard from '../VersionRepository/components/VersionCard';

    export default {
        name: 'DashboardVersionListDialog',
        components: {VersionCard},
        mixins: [componentMixin],
        data() {
            return {
                loading: false,
                showModal: false,
                versionList: [],
                query: {
                    repositoryId: 0,
                    pageIndex: 1,
                    pageSize: 512
                },
            };
        },
        methods: {
            pageMessage(type){
                if(type==='version.item.edit'){
                    this.loadData();
                }
            },
            pageLoad() {
                this.showModal = true;
                this.query.repositoryId = this.args.repositoryId;
                this.loadData();
            },
            loadData() {
                this.loading = true;
                app.invoke('BizAction.getCompanyVersionList', [app.token, this.query], (res) => {
                    if (Object.isEmpty(res)) {
                        return;
                    }
                    this.versionList = (res && res.list) || [];
                }, (code, message) => {
                    app.toast(message || '加载版本信息失败');
                    this.loading = false;
                });
            },
            showEditDialog(version) {
                // app.showDialog(DashboardVersionCreateDialog, {repositoryId: this.args.repositoryId});
                app.showDialog(DashboardVersionEditDialog, {id: version.id});
            },
            showCreateDialog() {
                app.showDialog(DashboardVersionCreateDialog, {repositoryId: this.args.repositoryId});
            }
        },
    };
</script>

<style lang="less" scoped>
    .modal-edit {
        /deep/ .ivu-modal-body {
            min-height: 300px;
            padding: 16px 40px;
        }
    }

    .version-list {
        display: flex;
        align-items: center;
        align-content: center;
        flex-wrap: wrap;
        padding-bottom: 20px;

        &-card {
            margin-left: 10px;

            &.row-first {
                margin-left: 10px;
            }
        }

        &-add {
            position: relative;
            background: #fff;
            box-shadow: 0 2px 6px 0 hsla(0, 0%, 83.1%, .45);
            border-radius: 4px;
            border: 1px solid #eee;
            padding: 15px;
            margin-top: 15px;
            margin-left: 10px;
            width: 320px;
            height: 114px;
            cursor: pointer;
            display: flex;
            align-items: center;
            align-content: center;
            flex-direction: column;
            justify-content: center;
            color: #888;

            &:hover {
                color: #17a7ed;
            }
        }

        .repository-more-icon {
            &:hover {
                background-color: #000;
            }
        }
    }

    .nodata {
        padding: 60px;
        font-size: 20px;
        color: #999;
        text-align: center;
    }
</style>
