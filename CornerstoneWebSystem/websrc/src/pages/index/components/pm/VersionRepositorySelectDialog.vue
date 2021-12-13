<style scoped>
    .page {
        padding: 10px;
        height: 600px;
    }
</style>
<i18n>
    {
    "en": {
    "选择版本库":"Select Version Repository",
    "选择版本":"Select Version",
    "名称":"Name",
    "开始时间":"Start Time",
    "发布时间":"Publish Time",
    "查询":"Query",
    "责任人":"Owner",
    "创建人":"Creator",
    "取消":"Cancel",
    "确定":"OK"
    },
    "zh_CN": {
    "选择版本库":"选择版本库",
    "选择版本":"选择版本",
    "名称":"名称",
    "开始时间":"开始时间",
    "发布时间":"发布时间",
    "查询":"查询",
    "责任人":"责任人",
    "创建人":"创建人",
    "取消":"取消",
    "确定":"确定"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog"
        v-model="showDialog"
        :closable="true"
        :mask-closable="false"
        :loading="false"
        :title="$t('选择版本')"
        width="1000">
        <div class="page">
            <Form inline>
                <FormItem>
                    <Select
                        clearable
                        transfer
                        style="width:200px"
                        :placeholder="$t('选择版本库')"
                        @on-change="onVersionRepositoryChange"
                        v-model="formItem.repositoryId">
                        <Option v-for="item in repositoryList" :key="item.id" :value="item.id">{{item.name}}</Option>
                    </Select>
                </FormItem>
                <FormItem>
                    <Button type="default">{{$t('查询')}}</Button>
                </FormItem>
            </Form>
            <BizTable
                ref="table" :value="tableData" :page="formItem" style="border-top:1px solid #eee;margin-top:20px">
                <template slot="thead">
                    <tr>
                        <th style="width:60px">
                            <Checkbox :disabled="!multipart"  v-model="isSelectAll"></Checkbox>
                        </th>
                        <th>{{$t('名称')}}</th>
                        <th>{{$t('状态')}}</th>
                        <th>{{$t('备注')}}</th>
                        <th>{{$t('开始时间')}}</th>
                        <th>{{$t('发布时间')}}</th>
                    </tr>
                </template>
                <template slot="tbody">
                    <tr v-for="item in tableData" :key="item.id" class="table-row">
                        <td>
                            <Checkbox @on-change="selectOne($event,item.id)" v-model="item.isSelect"></Checkbox>
                        </td>
                        <td class="text-no-wrap">
                            {{ item.name }}
                        </td>
                        <td class="text-no-wrap">
                           <DataDictLabel v-model="item.status" type="CompanyVersion.status"/>
                        </td>
                        <td class="text-no-wrap">
                            <div v-html="item.remark"></div>
                        </td>
                        <td>
                            {{ item.startTime | fmtDateTime }}
                        </td>
                        <td>
                            {{ item.endTime | fmtDateTime }}
                        </td>
                    </tr>
                </template>
            </BizTable>
        </div>
        <div slot="footer">
            <Row>
                <Col span="24" style="text-align:right">
                    <Button @click="showDialog=false" type="text" size="large">{{$t('取消')}}</Button>
                    <Button :disabled="!hasSelected" @click="confirm" type="default" size="large">
                        {{$t('确定')}}
                    </Button>
                </Col>
            </Row>
        </div>
    </Modal>
</template>

<script>
    import DataDictLabel from "../../../../components/ui/DataDictLabel";
    export default {
        components: {DataDictLabel},
        mixins: [componentMixin],
        data() {
            return {
                formItem: {
                    repositoryId: null,
                    pageIndex: 1,
                    pageSize: 50,
                },
                repositoryList: [],
                tableData: [],
                isSelectAll: false,
                multipart:true
            };
        },
        computed: {
            hasSelected() {
                return this.tableData.findIndex(item => item.isSelect === true) > -1;
            },
        },
        watch: {
            isSelectAll(val) {
                this.tableData.forEach(item => {
                    this.$set(item, 'isSelect', val);
                });
            },
        },
        methods: {
            pageLoad() {
                this.multipart = !this.args.single;
                this.loadVersionRepository();
            },
            debounceLoadData() {
                clearTimeout(this.loadDateTimeoutId);
                this.loadDateTimeoutId = setTimeout(() => {
                    this.loadData();
                }, 250);
            },
            loadVersionRepository() {
                app.invoke('BizAction.getCompanyVersionRepositoryList', [app.token, {
                    pageIndex: 1,
                    pageSize: 1000,
                }], (res) => {
                    if (!res || !Array.isArray(res.list)) {
                        this.repositoryList = [];
                        this.repositoryId = null;
                        return;
                    }
                    this.repositoryList = res.list;
                });
            },
            onVersionRepositoryChange() {
                this.debounceLoadData();
            },
            loadData() {
                app.invoke('BizAction.getCompanyVersionList', [app.token, {
                    ...this.formItem,
                }], (res) => {
                    if (!res || !Array.isArray(res.list)) {
                        this.tableData = [];
                        this.formItem.total = 0;
                        return;
                    }
                    this.tableData = res.list;
                    this.formItem.total = res.count;
                });
            },
            confirm: function () {
                const list = [];
                this.tableData.map(item => {
                    if (item.isSelect) {
                        list.push(item);
                    }
                });
                if (this.args.callback) {
                    this.args.callback(list);
                }
                this.showDialog = false;
            },
            selectOne(selected, itemId) {
                //多选时走原来的逻辑，不作处理
                if (!this.multipart) {
                    if (selected) {
                        for (let i = 0; i < this.tableData.length; i++) {
                            if (this.tableData[i].id != itemId) {
                                this.$set(this.tableData[i], 'isSelect', false);
                            }
                        }
                    }
                }
            },
        },
    };
</script>
