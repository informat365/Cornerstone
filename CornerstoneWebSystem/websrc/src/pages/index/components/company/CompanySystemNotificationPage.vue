<style scoped>
    .opt-bar {
        background-color: #f1f4f5;
        margin-top: 0;
    }
</style>

<i18n>
    {
    "en": {
    "名称":"Name",
    "周期":"Period",
    "状态":"Status",
    "查询":"Query",
    "创建模板":"Create notification",
    "创建人":"Creater",
    "创建日期":"Create time",
    "复制":"Copy",
    "设置":"Settings"
    },
    "zh_CN": {
    "名称":"名称",
    "周期":"周期",
    "状态":"状态",
    "查询":"查询",
    "创建通知":"创建通知",
    "创建人":"创建人",
    "创建日期":"创建日期",
    "复制":"复制",
    "设置":"设置"
    }
    }
</i18n>

<template>
    <div class="page">

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
                            :placeholder="$t('周期')"
                            type="SystemNotification.period"
                            v-model="formItem.period"></DataDictSelect>
                    </FormItem>
                    <FormItem>
                        <DataDictSelect
                            @change="loadData(true)"
                            clearable
                            :placeholder="$t('状态')"
                            type="SystemNotification.status"
                            v-model="formItem.status"></DataDictSelect>
                    </FormItem>
                    <FormItem>
                        <Button @click="loadData(true)" type="default">{{$t('查询')}}</Button>
                    </FormItem>
                </Form>
            </Col>
            <Col span="12" style="text-align:right">
                <Form inline>
                    <FormItem>
                        <Button @click="showEditTemplateDialog" type="default" icon="md-add" class="mr15">
                            {{$t('创建通知')}}
                        </Button>
                    </FormItem>
                </Form>
            </Col>
        </Row>
        <div style="padding:20px">
            <BizTable :fixed="true" @change="loadData" :page="pageQuery" :value="tableData">
                <template slot="thead">
                    <tr>
                        <th>{{$t('名称')}}</th>
                        <th style="width:100px">{{$t('周期')}}</th>
                        <th style="width:100px">{{$t('状态')}}</th>
                        <th style="width:100px">{{$t('创建人')}}</th>
                        <th style="width:130px;">{{$t('创建日期')}}</th>
                        <th style="width:180px;"></th>
                    </tr>
                </template>
                <template slot="tbody">
                    <tr v-for="item in tableData" :key="item.id" class="table-row">
                        <td>{{item.name}}</td>
                        <td>
                            <DataDictLabel type="SystemNotification.period" :value="item.period" />
                        </td>
                        <td>
                            <DataDictLabel type="Common.status" :value="item.status" />
                        </td>
                        <td>{{item.createAccountName}}</td>
                        <td>{{item.createTime|fmtDateTime}}</td>
                        <td style="text-align:right">
                            <Button style="margin-right:5px" @click="copyItem(item)" type="default">{{$t('复制')}}
                            </Button>
                            <Button @click="settingItem(item)" type="default">{{$t('设置')}}</Button>
                        </td>
                    </tr>

                </template>
            </BizTable>
        </div>
    </div>
</template>

<script>

    export default {
        mixins: [componentMixin],
        data() {
            return {
                pageQuery: {
                    pageIndex: 1,
                    pageSize: 20,
                },
                formItem: {
                    name: null,
                    status: null,
                    period: null,
                },
                tableData: [],
            };
        },

        methods: {
            pageLoad() {
                this.loadData();
            },
            pageMessage(type) {
                if (type === 'report.notification.edit') {
                    this.loadData();
                }
            },
            loadData(resetPage) {
                if (resetPage) {
                    this.pageQuery.pageIndex = 1;
                }
                var query = copyObject(this.pageQuery, this.formItem);
                app.invoke('BizAction.getSystemNotificationList', [app.token, query], (info) => {
                    this.tableData = info.list;
                    this.pageQuery.total = info.count;
                });
            },
            showEditTemplateDialog() {
                app.showDialog(CompanySystemNotificationEditDialog);
            },
            settingItem(item) {
                app.showDialog(CompanySystemNotificationEditDialog, {
                    id: item.id,
                });
            },
            copyItem(item) {
                app.showDialog(CompanySystemNotificationEditDialog, {
                    copyId: item.id,
                });
            },
        },
    };
</script>
