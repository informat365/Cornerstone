<style lang="less" scoped>
    .desc-info {
        font-size: 12px;
        color: #999;
    }

    .check {
        color: #0bc266;
    }

    .workflow-table {
        /deep/ .ivu-table-overflowX {
            overflow-scrolling: touch;
            scroll-behavior: smooth;

            &::-webkit-scrollbar {
                width: 4px;
                height: 8px;
                background-color: transparent;
                -ms-overflow-style: -ms-autohiding-scrollbar;
            }

            &::-webkit-scrollbar-track {
                border-radius: 0;
            }

            &::-webkit-scrollbar-thumb {
                border-radius: 0;
                background-color: #ddd;
            }
        }

        /deep/ .ivu-table-cell {
            padding: 0 5px;
        }

        .transfer-to-cell {
            position: relative;
            cursor: pointer;

            &-placeholder {
                position: relative;
                height: 32px;
            }
        }
    }
</style>
<i18n>
    {
    "en": {
    "项目": "Project",
    "对象类型": "Object type",
    "自定义状态流转图":"Customize the status flow chart. Click on the cells in the figure below to modify the flow settings.",
    "流转设置":"Setting",
    "可流转到":"transfer",
    "流转图":"Chart",
    "保存":"Save",
    "保存成功":"Success",
    "添加状态":"Add"
    },
    "zh_CN": {
    "项目": "项目",
    "对象类型": "对象类型",
    "自定义状态流转图":"自定义状态流转图,点击下图中的单元格可以修改流转设置",
    "流转设置":"流转设置",
    "可流转到":"可流转到",
    "流转图":"流转图",
    "保存":"保存",
    "保存成功":"保存成功",
    "添加状态":"添加状态"
    }
    }
</i18n>
<template>
    <div>
        <Form label-position="top">
            <FormItem :label="$t('流转设置')">
                <Table
                    class="workflow-table" border :columns="columns" :data="dataList">
                    <template slot="name" slot-scope="{ row }" v-if="columns">
                        <IconButton
                            @click="showStatusEditDialog(row)" icon="ios-settings-outline"></IconButton>
                        <TaskStatus :label="row.name" :color="row.color"></TaskStatus>
                    </template>
                    <template
                        slot-scope="{ row }" :slot="item.slot" v-for="(item) in columns.slice(1)">
                        <div
                            class="transfer-to-cell"
                            :key="item.slot+'_'+item.id"
                            @click="setTransfer(row.id,item.dataId)">
                            <template v-if="row.id === item.dataId">
                                <span>--</span>
                            </template>
                            <template v-else-if="row[item.slot]">
                                <Icon type="md-checkmark-circle" class="check" size="18" />
                            </template>
                            <template v-else>
                                <div class="transfer-to-cell-placeholder"></div>
                            </template>
                        </div>
                    </template>
                </Table>
                <div>
                    <IconButton  @click="showStatusEditDialog()" icon="ios-add" :title="$t('添加状态')"></IconButton>
                </div>
            </FormItem>

            <FormItem label="" >
                <Button @click="confirm" type="default">{{$t('保存')}}</Button>
            </FormItem>
            <FormItem :label="$t('流转图')">
                <div class="scrollbox" style="overflow:auto;padding: 10px">
                    <div ref="graph"></div>
                </div>
            </FormItem>
        </Form>
    </div>
</template>

<script>
    export default {
        mixins: [componentMixin],
        props: ['project'],
        data() {
            return {
                objectType: 0,
                statusList: [],
                dataList: [],
                moduleList: [],
                columns: [],
                disabled:false
            };
        },
        mounted() {
            this.disabled = this.project&&this.project.isFinish;
        },
        created() {
            this.loadData();
        },
        methods: {
            loadData() {
                app.invoke('BizAction.getProjectStatusDefineInfoList', [app.token, this.project.id, this.objectType], (list) => {
                    if (!Array.isArray(list) || list.length === 0) {
                        this.dataList = [];
                        this.columns = [];
                        this.loadGraph();
                        return;
                    }
                    const columns = [];
                    let width = 120;
                    list.forEach(item => {
                        if (item.name.length > 8) {
                            width = 140;
                        }
                        columns.push({
                            title: item.name,
                            align: 'center',
                            slot: 'status_' + item.id,
                            width: width,
                            dataId: item.id,
                        });
                    });
                    if (columns.length > 0) {
                        columns.unshift({
                            title: this.$t('可流转到'),
                            slot: 'name',
                            width: width + 50,
                            fixed: 'left',
                        });
                    }
                    const dataList = [];
                    list.forEach(xitem => {
                        const dataItem = {
                            ...xitem,
                        };
                        list.forEach(yitem => {
                            dataItem[`status_${ yitem.id }`] = Array.isArray(xitem.transferTo) && xitem.transferTo.indexOf(yitem.id) > -1;
                        });
                        dataList.push(dataItem);
                    });
                    this.dataList = dataList;
                    this.columns = columns;
                    this.loadGraph();
                });
            },
            loadGraph() {
                let s = `digraph workflow{
                        fontsize = 9;
                        node [shape="record" fontsize="9"];
                        edge [style="dashed"];
                `;
                this.dataList.map(item => {
                    if (item.type === 1) {
                        s += item.name + '[style="filled", color="' + item.color + '", fillcolor="#ffffff"]\n';
                    }
                    if (item.type === 2) {
                        s += item.name + '[style="filled", color="' + item.color + '", fillcolor="#ffffff"]\n';
                    }
                    if (item.type === 3) {
                        s += item.name + '[style="filled", color="' + item.color + '", fillcolor="#ffffff"]\n';
                    }
                });
                this.dataList.map(item => {
                    this.dataList.forEach(toItem => {
                        if (this.transferTo(item, toItem)) {
                            s += '\n' + item.name + ' -> ' + toItem.name + ';\n';
                        }
                    });
                });
                s += '\n}';
                var svg = Viz(s);
                this.$refs.graph.innerHTML = svg;
            },
            transferTo(item, toItem) {
                if (item.transferTo == null) {
                    return false;
                }
                return item.transferTo.indexOf(toItem.id) > -1;
            },
            setTransfer(itemId, rdItemId) {
                // if(this.disabled){
                //     return;
                // }
                if (itemId === rdItemId) {
                    return;
                }
                const item = this.dataList.find(val => val.id === itemId);
                if (item.transferTo == null) {
                    item.transferTo = [];
                }
                const index = item.transferTo.indexOf(rdItemId);
                if (index > -1) {
                    item.transferTo.splice(index, 1);
                } else {
                    item.transferTo.push(rdItemId);
                }
                item[`status_${ rdItemId }`] = item.transferTo.indexOf(rdItemId) > -1;
                this.loadGraph();
            },
            confirm() {
                const dataList = [];
                this.dataList.forEach(item => {
                    dataList.push({
                        checkFieldList: item.checkFieldList,
                        color: item.color,
                        companyId: item.companyId,
                        createAccountId: item.createAccountId,
                        createTime: item.createTime,
                        id: item.id,
                        name: item.name,
                        objectType: item.objectType,
                        projectId: item.projectId,
                        setOwnerList: item.setOwnerList,
                        transferTo: item.transferTo,
                        type: item.type,
                        updateAccountId: item.updateAccountId,
                        updateTime: item.updateTime,
                    });
                });
                app.invoke('BizAction.saveProjectStatusDefineList', [app.token, dataList], (list) => {
                    app.toast(this.$t('保存成功'));
                    app.postMessage('project.workflow.edit');
                });
            },
            showStatusEditDialog(item) {
                app.showDialog(ProjectSettingStatusEditDialog, {
                    item: item,
                    project: this.project,
                    objectType: this.objectType,
                    callback: this.loadData,
                });

            },
        },
    };
</script>
