<style scoped>
    .desc-info {
        padding-left: 10px;
        font-size: 12px;
        color: #999;
    }
</style>
<i18n>
    {
    "en": {
    "归档": "Archive",
    "归档项目":"Archive project",
    "项目归档后":"After the project is archived, the project members will not be visible until the administrator reopens  them.",
    "重新打开":"Reopen",
    "复制项目":"Copy",
    "复制":"Copy",
    "以此项目为模板":"Using this project as a template, copy custom fields, workflow, priority, data permissions",
    "删除项目":"Delete",
    "删除":"Delete",
    "永久删除此项目":"Permanent deletion of all information on this project is unrecoverable. Please operate with caution.",
    "确认要删除项目":"Are you sure you want to delete the item {0}? Project deletion will not be restored, please be careful!",
    "操作成功":"Success"
    },
    "zh_CN": {
    "归档": "归档",
    "归档项目":"归档项目",
    "项目归档后":"项目归档后，将对项目成员不可见，需要管理员重新打开后才可见",
    "重新打开":"重新打开",
    "复制项目":"复制项目",
    "复制":"复制",
    "以此项目为模板":"以此项目为模板，复制自定义字段、工作流、优先级、数据权限",
    "删除项目":"删除项目",
    "删除":"删除",
    "永久删除此项目":"永久删除此项目的所有信息，不可恢复，请谨慎操作",
    "确认要删除项目":"确认要删除项目【{0}】？项目删除后将不可以恢复，请谨慎操作!",
    "操作成功":"操作成功"
    }
    }
</i18n>
<template>
    <div>
        <Form label-position="top">
            <FormItem v-if="project.status==1" :label="$t('归档')">
                <Button @click="archiveProject()" type="default">{{$t('归档项目')}}</Button>
                <span class="desc-info">{{$t('项目归档后')}}</span>
            </FormItem>

            <FormItem v-if="project.status==2" :label="$t('重新打开')">
                <Button @click="openProject()" type="default">{{$t('重新打开')}}</Button>
            </FormItem>

            <FormItem :label="$t('复制')" v-if="perm('company_create_project')">
                <Button @click="copyProject" type="default">{{$t('复制项目')}}</Button>
                <span class="desc-info">{{$t('以此项目为模板')}}</span>
            </FormItem>

            <FormItem v-if="project.isTemplate==false&&perm('company_delete_project')" :label="$t('删除')">
                <Button @click="deleteProject()" type="error">{{$t('删除项目')}}</Button>
                <span class="desc-info">{{$t('永久删除此项目')}}</span>
            </FormItem>

        </Form>

    </div>
</template>

<script>
    export default {
        mixins: [componentMixin],
        props: ['project', 'switchHome'],
        data() {
            return {
                repeatProjectName: null
            }
        },
        methods: {
            archiveProject() {
                let _this = this;
                this.twiceFactorValid(() => {
                    app.invoke('BizAction.archiveProject', [app.token, _this.project.id], () => {
                        app.postMessage('project.edit')
                        app.postMessage('project.archive')
                        app.toast(this.$t('操作成功'));
                    })
                })
            },
            openProject() {
                app.invoke('BizAction.openProject', [app.token, this.project.id], () => {
                    app.postMessage('project.edit')
                    app.toast(this.$t('操作成功'));
                })
            },
            copyProject() {
                app.showDialog(ProjectCreateDialog, {
                    projectId: this.project.id,
                    isTemplate: this.project.isTemplate,
                })
            },
            deleteProject() {
                let _this = this;
                this.twiceFactorValid(() => {
                    app.confirm(this.$t('确认要删除项目', [_this.project.name]), () => {
                        app.invoke('BizAction.deleteProject', [app.token, _this.project.id], () => {
                            app.postMessage('project.delete');
                            if (_this.switchHome) {
                                app.loadPage('/pm/index/dashboard')
                            } else {
                                this.$emit('on-close');
                            }
                        })
                    })
                })
            },
            twiceFactorValid(callback) {
                let _this = this;
                _this.$Modal.confirm({
                    render: h => {
                        return h("Input", {
                            props: {
                                value: _this.repeatProjectName,
                                autoFocus: true,
                                placeholder: "请输入项目名称以进行确认"
                            },
                            on: {
                                input: val => {
                                    _this.repeatProjectName = val;
                                }
                            }
                        });
                    },
                    onOk: () => {
                        if (_this.repeatProjectName) {
                            if (_this.repeatProjectName !== this.project.name) {
                                app.toast("输入项目名称不匹配");
                                _this.repeatProjectName = null;
                            } else {
                                if (callback) {
                                    callback.apply();
                                }
                            }
                        }
                    },
                    onCancel: () => {
                        _this.repeatProjectName = null;
                    }
                })
            }

        }
    }
</script>
