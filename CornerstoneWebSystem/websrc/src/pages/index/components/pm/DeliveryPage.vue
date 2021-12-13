<style scoped lang="less">
    .page {
        min-height: 400px;
    }

    .no-data {
        margin: 15px 0;

        &:after {
            font-weight: bold;
            font-size: 16px;
            content: '无数据';
        }
    }

    .delivery-container {
        position: relative;
        width: 1024px;
        margin: 10px auto;
        padding: 20px;
        display: flex;
        align-items: center;
        justify-content: space-around;

        .delivery-add {
            border: 1px solid #ccc;
            display: flex;
            justify-content: center;
            align-items: center;
            width: 300px;
            height: 100px;
            border-radius: 5px;
            margin: 20px;

            &:hover {
                box-shadow: 1px 1px 2px 2px #C1C1C1;
            }
        }

        .delivery {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: space-around;
            padding: 8px;
            border-radius: 5px;
            border: 1px solid #ccc;
            width: 360px;
            height: 100px;
            margin-right: 10px;

            &:hover {
                box-shadow: 1px 1px 2px 2px #C1C1C1;
            }

            .delivery-remark {
                text-align: center;
                width: 100%;
                font-size: 13px;
                color: #2b2b2b;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                padding-left: 15px;

            }

            .delivery-main {
                width: 100%;
                display: flex;
                justify-content: space-around;
                align-items: center;

                .delivery-item {
                    text-align: center;

                    &-name {
                        font-weight: bold;
                        font-size: 15px;
                    }

                    &-time {
                        font-size: 13px;
                    }

                    &-status {
                        font-size: 10px;
                        border: 2px solid #00a8c6;
                        color: #00a8c6;
                        padding: 3px 6px;
                        border-radius: 13px !important;

                        &-delivery {
                            border: 2px solid #0AB169;
                            color: #0AB169;
                        }
                    }
                }
            }

        }
    }

    .task-more-search-content {
        padding-bottom: 20px;
        max-height: calc(100vh - 150px);
        overflow: auto;
        white-space: normal;
        text-align: center;

        .tip-item {
            margin-top: 15px;
            cursor: pointer;
        }
    }
</style>

<i18n>
    {
    "en": {
    "编辑": "Edit",
    "创建": "Create Delivery Version",
    "查询": "Query",
    "交付版本名称": "Delivery version name",
    "交付起始时间": "Delivery date start",
    "交付截止时间": "Delivery date end",
    "更新对象": "Update Object",
    "修订记录": "Versions",
    "已关联对象": "Rel",
    "最后一次修改了此页面": " edited",
    "分享地址已复制到剪切板中": "share address Copied to Clipboard",
    "确定要删除页面吗？": "Are you sure you want to delete the page {0}?",
    "操作成功": "Success",
    "确定要将此修订版本设置为当前版本吗？": "Are you sure you want to set this revised version to the current version?",
    "更新成功": "Success",
    "创建了此页面": " created "
    },
    "zh_CN": {
    "创建": "创建",
    "查询": "查询",
    "交付版本名称": "交付版本名称",
    "交付起始时间": "交付起始时间",
    "交付截止时间": "交付截止时间",
    "最后一次修改了此页面": "最后一次修改了此页面",
    "分享地址已复制到剪切板中": "分享地址已复制到剪切板中",
    "确定要删除页面吗？": "确定要删除页面“{0}”吗？",
    "操作成功": "操作成功",
    "确定要将此修订版本设置为当前版本吗？": "确定要将此修订版本设置为当前版本吗？",
    "更新成功": "更新成功",
    "创建了此页面": "创建了此页面"
    }
    }
</i18n>

<template>
    <div class="page">
        <Row class="opt-bar">
            <Col span="24" class="opt-left">
                <Form inline>
                    <FormItem>
                        <Input type="text" v-model="queryItem.name" :placeholder="$t('交付版本名称')" style="width: 200px;"/>
                    </FormItem>
                    <FormItem>
                        <ExDatePicker clearable @on-change="loadData(true)" type="date" style="width:200px"
                                      v-model="queryItem.deliveryDateStart" :placeholder="$t('交付起始时间')"></ExDatePicker>
                    </FormItem>
                    <FormItem>
                        <ExDatePicker :day-end="true" clearable @on-change="loadData(true)" type="date" style="width:200px"
                                      v-model="queryItem.deliveryDateEnd" :placeholder="$t('交付截止时间')"></ExDatePicker>
                    </FormItem>
                    <FormItem v-if="!projectDisabled">
                        <Button type="default" @click="loadData">{{$t('查询')}}</Button>
                        <Button style="margin-left: 10px;" type="default" @click="addDelivery">{{$t('创建')}}</Button>
                    </FormItem>
                </Form>
            </Col>
        </Row>
        <div class="delivery-container">
            <div class="delivery" v-for="item in deliveryList" :key="item.id" @click="showDetail(item)">
                <div class="delivery-main">
                    <div class="delivery-item">
                        <div class="delivery-item-name">{{item.name}}</div>
                    </div>
                    <div class="delivery-item">
                        <div class="delivery-item-time">{{item.deliveryDate|fmtDateTime}}</div>
                        <div class="delivery-item-status" :class="{'delivery-item-status-delivery':item.status==2}">
                            {{item.status==2?'已交付':'未交付'}}
                        </div>
                        <!--                    <DataDictLabel :value="item.status" type="DeliveryVersion.status"/>-->
                    </div>
                    <!-- <Poptip transfer placement="bottom" width="100" class="poptip-gray nowrap-poptip">
                           <span class="more-query-btn">
                             <Icon type="ios-more" size="18"/>
                           </span>
                         <div slot="content" class="task-more-search-content scrollbox">
                             <div class="tip-item" v-if="item.status==1" @click="updateStatus(item,2)">交付</div>
                             <div class="tip-item" v-if="item.status==1" @click="editItem(item)">编辑</div>
                             <div class="tip-item" v-if="item.status==1" @click="deleteItem(item)">删除</div>
                         </div>
                     </Poptip>-->
                </div>
                <div class="delivery-remark">{{item.remark}}</div>
            </div>
            <div v-if="Array.isEmpty(deliveryList)" class="no-data"></div>
        </div>

        <DrawerDeliveryVersion ref="detailDrawer"></DrawerDeliveryVersion>
    </div>
</template>

<script>
    import DataDictLabel from "../../../../components/ui/DataDictLabel";
    import DrawerDeliveryVersion from "../pm/DrawerDeliveryVersion";

    export default {
        mixins: [componentMixin],
        components: {
            DataDictLabel, DrawerDeliveryVersion
        },
        data() {
            return {
                title: 'DeliveryPage',
                queryItem: {
                    name: null,
                    pageIndex: 1,
                    pageSize: -1,
                    projectId: 0,
                    deliveryDateStart: null,
                    deliveryDateEnd: null,
                },
                deliveryList: [],
            };
        },
        watch: {},
        mounted() {
            this.queryItem.projectId = app.projectId;
        },
        methods: {
            pageLoad() {
                this.loadData();
            },
            loadData() {
                app.invoke('BizAction.getDeliveryList', [app.token, this.queryItem], (res) => {
                    this.deliveryList = res;
                });
            },
            addDelivery() {
                app.showDialog(DeliveryEditDialog);
            },
            pageMessage(type, value) {
                if (type == 'delivery.edit') {
                    this.loadData();
                }
            },
            showDetail(item) {
                this.$refs.detailDrawer.show(item.id);
            },
            updateStatus(item, status) {
                item.status = status;
                app.invoke('BizAction.updateDeliveryStatus', [app.token, item], (res) => {
                    app.toast(this.$t('操作成功'));
                });
            },
            editItem(item) {
                app.showDialog(DeliveryEditDialog, {
                    id: item.id
                });
            },
            deleteItem(item) {
                let _this = this;
                app.confirm(this.$t('确认要删除此交付版本吗'), () => {
                    app.invoke('BizAction.deleteDelivery', [app.token, item.id], (res) => {
                        app.toast(_this.$t('操作成功'));
                    });
                })
            }

        },
    };
</script>
