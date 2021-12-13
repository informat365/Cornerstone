<style scoped>
    .page {
        padding: 10px;
        height: 600px;
    }
</style>
<i18n>
    {
    "en": {
    "未分类":"None",
    "所有分类":"All {0} category",
    "分类":"categories",
    "选择项目":"Select Project",
    "选择对象":"Select Object",
    "类型":"Type",
    "名称":"Name",
    "查询":"Query",
    "责任人":"Owner",
    "创建人":"Creator",
    "创建":"Create",
    "取消":"Cancel",
    "确定":"OK",
    "开始时间":"Start date",
    "截止时间":"End date"
    },
    "zh_CN": {
    "未分类":"未分类",
    "所有分类":"所有 {0}",
    "分类":"分类",
    "选择项目":"选择项目",
    "选择对象":"选择对象",
    "类型":"类型",
    "名称":"名称",
    "查询":"查询",
    "责任人":"责任人",
    "创建人":"创建人",
    "创建":"创建",
    "取消":"取消",
    "确定":"确定",
    "开始时间":"开始时间",
    "截止时间":"截止时间"
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
        :title="$t('选择对象')"
        width="1000">
        <div class="page">
            <Form inline>
                <FormItem>
                    <Input
                        @on-change="debounceLoadData"
                        type="text"
                        v-model.trim="formItem.name"
                        :placeholder="$t('名称')"></Input>
                </FormItem>
                <FormItem>
                    <DataDictSelect v-model="formItem.status" type="Stage.status"></DataDictSelect>
                </FormItem>
                <FormItem>
                    <Button style="margin: 0 5px;" @click="debounceLoadData" type="default">{{$t('查询')}}</Button>
                </FormItem>
            </Form>
            <BizTable
                ref="table"
                @change="loadData"
                :value="tableData"
                :page="formItem"
                style="border-top:1px solid #eee;margin-top:20px">
                <template slot="thead">
                    <tr>
                        <th style="width:40px">
                        </th>
                        <th style="width:200px">{{$t('名称')}}</th>
                        <th style="width:100px">{{$t('状态')}}</th>
                        <th style="width:100px">{{$t('开始时间')}}</th>
                        <th style="width:100px">{{$t('截止时间')}}</th>
                        <th style="width:80px">{{$t('创建人')}}</th>
                    </tr>
                </template>
                <template slot="tbody">

                    <tr v-for="item in tableData" :key="item.id" class="table-row">
                        <td>
                            <Checkbox @on-change="selectOne($event,item.id)" v-model="item.isSelect"></Checkbox>
                        </td>
                        <td class="text-no-wrap">
                           {{item.name}}
                        </td>
                        <td >
                           <DataDictLabel v-model="item.status" type="Stage.status"/>
                        </td>
                        <td>
                            {{item.startDate|fmtDate}}
                        </td>
                        <td>
                            {{item.endDate|fmtDate}}
                        </td>
                        <td>
                            {{item.createAccountName}}
                        </td>
                    </tr>
                </template>
            </BizTable>


        </div>

        <div slot="footer">
            <Row>
                <Col span="24" style="text-align:right">
                    <Button @click="showDialog=false" type="text" size="large">{{$t('取消')}}</Button>
                    <Button :disabled="getSelectedCount()==0" @click="confirm" type="default" size="large">
                        {{$t('确定')}}
                    </Button>
                </Col>
            </Row>

        </div>
    </Modal>
</template>

<script>

    import DataDictLabel from "../../../../components/ui/DataDictLabel";
    import DataDictSelect from "../../../../components/ui/DataDictSelect";
    export default {
        components: {DataDictSelect, DataDictLabel},
        mixins: [componentMixin],
        data() {
            return {
                formItem: {
                    projectId: null,
                    name: null,
                    status: null,
                    pageIndex: 1,
                    pageSize: 50,
                    idNotInList:[]
                },
                tableData: [],
                isSelectAll: false,
                moduleList: [],
                singleSelect: true,
                stageId:null
            };
        },
        watch: {
            'isSelectAll': function (val) {
                this.tableData.map(item => {
                    item.isSelect = val;
                });
            },
        },
        methods: {
            pageLoad() {
                this.formItem.projectId = this.args.projectId;
                if(!!this.args.id){
                    this.formItem.idNotInList=[this.args.id]
                }
                this.loadData();
            },
            debounceLoadData() {
                clearTimeout(this.loadDateTimeoutId);
                this.loadDateTimeoutId = setTimeout(() => {
                    this.loadData();
                }, 250);
            },
            loadData() {
                const formItem = JSON.parse(JSON.stringify(this.formItem));
                app.invoke('BizAction.getStageList', [app.token, formItem], info => {
                    this.tableData = info;
                });
            },
            getSelectedCount() {
                var count = 0;
                this.tableData.map(item => {
                    if (item.isSelect) {
                        count++;
                    }
                });
                return count;
            },
            confirm: function () {
                var list = [];
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
                if (this.singleSelect) {
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
