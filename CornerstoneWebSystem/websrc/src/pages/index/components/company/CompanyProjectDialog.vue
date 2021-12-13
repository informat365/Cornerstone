<template>
    <Modal
        ref="dialog"
        v-model="showDialog"
        :closable="true"
        :mask-closable="false"
        :loading="false"
        :title="$t('选择项目')"
        width="1000">
        <div class="content">
            <Row class="opt-bar opt-bar-light">
                <Col span="12" class="opt-left">
                    <Form inline @submit.native.prevent>
                        <FormItem>
                            <Input
                                @on-change="loadData(true)"
                                type="text"
                                v-model="formItem.name"
                                :placeholder="$t('名称')"></Input>
                        </FormItem>
                        <FormItem>
                            <DataDictSelect
                                @change="loadData(true)"
                                clearable
                                :placeholder="$t('状态')"
                                type="Project.status"
                                v-model="formItem.status"></DataDictSelect>
                        </FormItem>
                        <FormItem>
                            <Button @click="loadData(true)" type="default">{{$t('查询')}}</Button>
                        </FormItem>
                    </Form>
                </Col>
                <Col span="12" style="text-align:right">
                    {{ $t('已选择项目',[selectRows.length]) }}
                </Col>
            </Row>
            <div style="padding:0 20px">
                <BizTable :fixed="true" @change="loadData" :page="pageQuery" :value="tableData">
                    <template slot="thead">
                        <tr>
                            <th style="width: 120px;">
                                <Checkbox
                                    :value="checkAll" @click.prevent.native="onHandleCheckAll" />
                            </th>
                            <th>{{$t('名称')}}</th>
                            <th style="width:120px">{{$t('状态')}}</th>
                            <th style="width:150px">{{$t('创建人')}}</th>
                            <th style="width:140px;">{{$t('创建日期')}}</th>
                        </tr>
                    </template>
                    <template slot="tbody">
                        <tr v-for="item in tableData" :key="item.id" class="table-row">
                            <td>
                                <Checkbox
                                    :value="selectIds.indexOf(item.id) > -1"
                                    @on-change="(val)=>{onRowSelect(item,val)}" />
                            </td>
                            <td>{{item.name}}</td>
                            <td>
                                <DataDictLabel type="Project.status" :value="item.status" />
                            </td>
                            <td>{{item.createAccountName}}</td>
                            <td>{{item.createTime|fmtDateTime}}</td>
                        </tr>

                    </template>
                </BizTable>
            </div>
        </div>
        <div slot="footer">
            <Button
                type="default" @click="showDialog = false">
                {{ $t('取消') }}
            </Button>
            <Button
                type="primary" :disabled="!hasSelection" @click.native="onConfirm">
                {{ $t('确认') }}
            </Button>
        </div>
    </Modal>
</template>
<i18n>
    {
    "en": {
    "选择项目":"Choose Project",
    "名称":"Name",
    "状态":"Status",
    "查询":"Query",
    "创建项目":"Create",
    "创建人":"Creator",
    "创建日期":"Create time",
    "取消":"Cancel",
    "确认":"Confirm",
    "已选择项目":"({0}) projects selected"
    },
    "zh_CN": {
    "选择项目":"选择项目",
    "名称":"名称",
    "状态":"状态",
    "查询":"查询",
    "创建项目":"创建项目",
    "创建人":"创建人",
    "创建日期":"创建日期",
    "取消":"取消",
    "确认":"确认",
    "已选择项目":"已选择 ({0}) 个项目"
    }
    }
</i18n>
<script>
    export default {
        mixins: [componentMixin],
        props: {
            multiple: {
                type: Boolean,
                default: false,
            },
            selectedRows: {
                type: Array,
                default: () => [],
            },
            selectedIds: {
                type: Array,
                default: () => [],
            },
            listAll: {
                type: Boolean,
                default: false,
            },
        },
        data() {
            return {
                showDialog: false,
                pageQuery: {
                    pageIndex: 1,
                    pageSize: 15,
                },
                formItem: {
                    name: null,
                    status: null,
                },
                tableData: [],
                queryForm: {
                    deviceNum: null,
                    position: null,
                    deviceType: null,
                    tenantIds: null,
                },
                currentPage: 1,
                selectRows: [],
                selectIds: [],
            };
        },
        computed: {
            hasSelection() {
                return !Array.isEmpty(this.selectIds);
            },
            checkAll() {
                if ((!Array.isArray(this.tableData) || this.tableData.length === 0) || (!Array.isArray(this.selectIds) || this.selectIds.length === 0)) {
                    return false;
                }
                const unSelect = this.tableData.filter(_ => this.selectIds.indexOf(_.id) === -1);
                return Array.isArray(unSelect) && unSelect.length === 0;
            },
        },
        beforeDestroy() {
            this.reset();
        },
        methods: {
            pageLoad() {
                this.reset();
                this.loadData(true);
            },
            reset() {
                this.selectRows = [];
                this.selectIds = [];
            },
            loadData(resetPage) {
                if (resetPage) {
                    this.pageQuery.pageIndex = 1;
                }
                const query = copyObject(this.pageQuery, this.formItem);
                app.invoke('BizAction.getAllProjectList', [app.token, query], (info) => {
                    this.tableData = info.list;
                    this.pageQuery.total = info.count;
                });
            },
            onHandleCheckAll() {
                if (this.checkAll) {
                    this.selectRows = [];
                    this.selectIds = [];
                    return;
                }
                this.tableData.forEach(_ => {
                    const index = this.selectIds.indexOf(_.id);
                    if (index > -1) {
                        return;
                    }
                    this.selectRows.push({
                        id: _.id,
                        name: _.name,
                    });
                });
                this.selectIds = this.selectRows.map(item => item.id);
            },
            onRowSelect(row) {
                const index = this.selectIds.indexOf(row.id);
                if (index > -1) {
                    this.selectRows.splice(index, 1);
                    this.selectIds.splice(index, 1);
                    return;
                }
                this.selectRows.push({
                    id: row.id,
                    name: row.name,
                });
                this.selectIds = this.selectRows.map(item => item.id);
            },
            onConfirm() {
                if (this.args.callback) {
                    this.args.callback(this.selectRows);
                }
                this.showDialog = false;
            },
        },
    };
</script>

<style lang="less" scoped></style>
