<style scoped>
    .desc-info {
        font-size: 12px;
        color: #999;
    }

    .value-row {
        padding-top: 7px;
        padding-bottom: 7px;
        cursor: move;
    }

    .value-row:hover {
        background-color: #ebf7ff;
    }
</style>
<i18n>
    {
    "en": {
    "功能开关": "Switch",
    "拖动调整功能的显示顺序":"Drag to reorder",
    "自定义模块":"Custom",
    "保存":"Save",
    "保存成功":"Success"
    },
    "zh_CN": {
    "功能开关": "功能开关",
    "拖动调整功能的显示顺序":"拖动调整功能的显示顺序",
    "自定义模块":"自定义模块",
    "保存":"保存",
    "保存成功":"保存成功"
    }
    }
</i18n>
<template>
    <div>
        <Form label-position="top">
            <FormItem :label="$t('功能开关')">
                <div class="desc-info">{{$t('拖动调整功能的显示顺序')}}

                </div>
                <draggable v-model="moduleList" :options="{draggable:'.value-row'}">
                    <Row class="value-row" v-for="item in moduleList" :key="item.id">
                        <Col span="20">{{item.name}}</Col>
                        <Col span="4">
                            <i-Switch :disabled="disabled" v-model="item.isEnable"></i-Switch>
                        </Col>
                    </Row>
                </draggable>

            </FormItem>

            <FormItem label="" v-if="!disabled">
                <Button @click="save" type="default">{{$t('保存')}}</Button>
                <IconButton
                    @click="showModuleSelect"
                    style="margin-left:20px"
                    icon="ios-create-outline"
                    :title="$t('自定义模块')"></IconButton>
            </FormItem>
        </Form>

    </div>
</template>

<script>
    import draggable from 'vuedraggable';

    export default {
        mixins: [componentMixin],
        props: ['project'],
        components: {
            draggable,
        },
        mounted() {
            this.loadData();
            this.disabled = this.project&&this.project.isFinish;
        },
        data() {
            return {
                moduleList: [],
                disabled:false
            };
        },
        methods: {
            loadData() {
                app.invoke('BizAction.getAllProjectModuleInfoList', [app.token, this.project.id], (list) => {
                    this.moduleList = list;
                });
            },
            pageMessage(type) {
                if (type == 'projectmodule.edit') {
                    this.loadData();
                }
            },
            save() {
                app.invoke('BizAction.saveProjectModuleList', [app.token, this.moduleList], (list) => {
                    app.toast(this.$t('保存成功'));
                    app.postMessage('project.edit');
                });
            },
            showModuleSelect() {
                app.showDialog(ProjectSettingModuleSelectDialog, {
                    projectId: this.project.id,
                    projectTemplateUuid: this.project.templateUuid,
                    list: this.moduleList,
                });
            },
        },
    };
</script>
