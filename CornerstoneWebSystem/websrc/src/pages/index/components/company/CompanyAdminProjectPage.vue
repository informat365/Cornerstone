<style scoped>
    .opt-bar {
        background-color: #F1F4F5;
        margin-top: 0;
    }
</style>
<i18n>
    {
    "en":{
    "名称":"Name",
    "状态":"Status",
    "查询":"Query",
    "创建":"Create",
    "删除":"Delete",
    "创建日期":"Create Time",
    "设置":"Settings",
    "删除成功":"Delete Success",
    "确认要删除项目":"Are you sure to delete the project template【{0}】？The operation can not revert ,be caution !"
    },
    "zh_CN": {
    "名称":"名称",
    "查询":"查询",
    "创建":"创建",
    "状态":"状态",
    "删除":"删除",
    "创建日期":"创建日期",
    "设置":"设置",
    "删除成功":"删除成功",
    "确认要删除项目":"确认要删除项目模板【{0}】？删除后将不可以恢复，请谨慎操作!"
    }
    }
</i18n>
<template>
    <div class="page">
        <Row class="opt-bar opt-bar-light">
            <Col span="12" class="opt-left">
                <Form inline @submit.native.prevent>
                    <FormItem>
                        <Input @on-change="loadData(true)" type="text" v-model="formItem.name"
                               :placeholder="$t('名称')"></Input>
                    </FormItem>
                    <FormItem>
                        <Button @click="loadData(true)" type="default">{{$t('查询')}}</Button>
                        <Button style="margin-left: 10px;" @click="createProjectTemplate" type="default">{{$t('创建')}}
                        </Button>
                    </FormItem>
                </Form>
            </Col>
            <Col span="12" style="text-align:right">

            </Col>
        </Row>
        <div style="padding:20px">
            <BizTable :fixed="true" :page="pageQuery" :value="tableData">
                <template slot="thead">
                    <tr>
                        <th>{{$t('名称')}}</th>
                        <th style="width:150px">{{$t('状态')}}</th>
                        <th style="width:140px;">{{$t('创建日期')}}</th>
                        <th style="width:170px;"></th>
                    </tr>
                </template>
                <template slot="tbody">
                    <tr v-for="item in tableData" :key="item.id" class="table-row">
                        <td>{{item.name}}</td>
                        <td>{{item.status|dataDict('Project.status')}}</td>
                        <td>{{item.createTime|fmtDateTime}}</td>
                        <td >
                            <Button @click="settingItem(item)" type="default">{{$t('设置')}}</Button>
                            <Button style="margin-left: 10px;" @click="deleteProjectTemplate(item)" type="error">{{$t('删除')}}</Button>
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
                },
                tableData: [],
            }
        },

        methods: {
            pageLoad() {
                this.loadData();
            },
            pageMessage(type) {
                if (type == 'project.edit' || type == 'project.delete') {
                    this.loadData();
                }
            },
            loadData(resetPage) {
                if (resetPage) {
                    this.pageQuery.pageIndex = 1;
                }
                var query = copyObject(this.pageQuery, this.formItem)
                app.invoke('BizAction.getTemplateProjectList', [app.token, query], (info) => {
                    this.tableData = info.list;
                    this.pageQuery.total = info.count;
                })
            },

            settingItem(item) {
                app.showDialog(ProjectSettingDialog, {
                    uuid: item.uuid,
                })
            },
            createProjectTemplate() {
                app.showDialog(ProjectTemplateCreateDialog);
            },
            deleteProjectTemplate(item) {
                app.confirm(this.$t('确认要删除项目',[item.name]),()=>{
                    app.invoke('BizAction.deleteProject',[app.token,item.id],()=>{
                        app.toast(this.$t("删除成功"));
                        this.loadData(true);
                    })
                })
            }
        }
    }
</script>
