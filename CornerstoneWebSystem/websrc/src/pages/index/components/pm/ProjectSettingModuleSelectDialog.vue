<style scoped>
    .desc-info {
        font-size: 12px;
        color: #999;
    }

    .value-row {
        padding-top: 7px;
        padding-bottom: 7px;
    }

    .value-row:hover {
        background-color: #ebf7ff;
    }
</style>
<i18n>
    {
    "en": {
    "自定义模块": "Custom Module",
    "公共模块":"Common",
    "对象模块":"Object",
    "项目阶段与时间计划":"Stage",
    "保存":"Save",
    "保存成功":"Success",
    "文件":"File",
    "敏捷":"Agile"
    },
    "zh_CN": {
    "自定义模块": "自定义模块",
    "公共模块":"公共模块",
    "对象模块":"对象模块",
    "项目阶段与时间计划":"项目阶段与时间计划",
    "保存":"保存",
    "保存成功":"保存成功",
    "文件":"文件",
    "敏捷":"敏捷"
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
        :title="$t('自定义模块')"
        width="500">
        <Form ref="form" label-position="top" style="height:500px;padding:15px">
            <FormItem :label="$t('公共模块')">
                <template v-for="item in moduleList">
                    <Row class="value-row" v-if="item.objectType==0" :key="item.url">
                        <Col span="20">{{item.name}}</Col>
                        <Col span="4">
                            <i-Switch v-model="item.isSelected"></i-Switch>
                        </Col>
                    </Row>
                </template>
            </FormItem>

            <FormItem :label="$t('对象模块')">
                <template v-for="item in moduleList">
                    <Row class="value-row" v-if="item.objectType>0" :key="item.field">
                        <Col span="20">
                            <div>{{item.name}}
                                <span style="margin-left:10px;color:#999;font-size:12px;">{{item.group}}</span></div>
                        </Col>
                        <Col span="4">
                            <i-Switch v-model="item.isSelected"></i-Switch>
                        </Col>
                    </Row>
                </template>
            </FormItem>


        </Form>


        <div slot="footer">
            <Row>
                <Col span="24" style="text-align:right">
                    <Button @click="confirm" type="default" size="large">{{$t('保存')}}</Button>
                </Col>
            </Row>

        </div>

    </Modal>
</template>


<script>

    export default {
        mixins: [componentMixin],
        data() {
            return {
                moduleList: [],
            };
        },
        methods: {
            pageLoad() {
                this.moduleList = [
                    { url: 'file', name: this.$t('文件'), objectType: 0 },
                    { url: 'wiki', name: 'WIKI', objectType: 0 },
                    { url: 'devops', name: 'DevOps', objectType: 0 },
                    { url: 'agile', name: this.$t('敏捷'), objectType: 0 },
                    { url: 'stage', name: this.$t('项目阶段与时间计划'), objectType: 0 },
                    { url: 'landmark', name: this.$t('里程碑'), objectType: 0 },
                    { url: 'delivery', name: this.$t('交付版本'), objectType: 0 },
                ];
                this.loadObjectTypeList();
            },
            loadObjectTypeList() {
                var query = {
                    pageIndex: 1,
                    pageSize: 200,
                };
                app.invoke('BizAction.getObjectTypeList', [app.token, query], (info) => {
                    if (!info || !Array.isArray(info.list)) {
                        return;
                    }
                    info.list.filter(item => this.projectSetObjectTypeSystemNames.indexOf(item.systemName) === -1).forEach(item => {
                        this.moduleList.push({
                            objectType: item.id,
                            name: item.name,
                            remark: item.remark,
                            url: 'task' + item.id,
                        });
                    });
                    this.checkShow();
                });
            },
            checkShow() {
                for (var i = 0; i < this.moduleList.length; i++) {
                    var t = this.moduleList[i];
                    if (t.objectType > 0) {
                        t.isSelected = this.isSelectedObjectType(t.objectType);
                    } else {
                        t.isSelected = this.isSelectedURL(t.url);
                    }
                }
            },
            isSelectedObjectType(objectType) {
                for (var i = 0; i < this.args.list.length; i++) {
                    var t = this.args.list[i];
                    if (t.objectType == objectType) {
                        return t.isEnable;
                    }
                }
                return false;
            },
            isSelectedURL(url) {
                for (var i = 0; i < this.args.list.length; i++) {
                    var t = this.args.list[i];
                    if (t.url == url) {
                        return t.isEnable;
                    }
                }
                return false;
            },
            confirm() {
                var list = [];
                for (var i = 0; i < this.moduleList.length; i++) {
                    var t = this.moduleList[i];
                    if (t.isSelected) {
                        list.push(t);
                    }
                }
                app.invoke('BizAction.updateProjectModuleList', [
                    app.token, this.args.projectId, list], () => {
                    app.postMessage('projectmodule.edit');
                    app.postMessage('project.edit');
                    app.toast(this.$t('保存成功'));
                    this.showDialog = false;
                });
            },
        },
    };
</script>
