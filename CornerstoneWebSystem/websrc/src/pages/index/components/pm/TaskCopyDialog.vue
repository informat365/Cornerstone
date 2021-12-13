<style scoped></style>
<i18n>
    {
    "en": {
    "复制": "Copy",
    "复制到项目":"Copy to project",
    "选择项目":"Choose project",
    "迭代":"Iteration",
    "选择迭代":"Choose iteration",
    "类型":"Type",
    "类型之间转换可能会丢失自定义字段":"Conversion between types may lose custom fields",
    "复制后删除原有的对象":"Delete the original object after replication",
    "已选择个待复制":"{0} ",
    "复制":"Copy",
    "复制并编辑":"Copy and edit",
    "复制评论":"Copy Comments",
    "复制父子对象":"Copy Pub sub Objects",
    "复制变更记录":"Copy Changelog",
    "复制关联对象":"Copy Relation"
    },
    "zh_CN": {
    "复制": "复制",
    "复制到项目":"复制到项目",
    "选择项目":"选择项目",
    "迭代":"迭代",
    "选择迭代":"选择迭代",
    "类型":"类型",
    "类型之间转换可能会丢失自定义字段":"类型之间转换可能会丢失自定义字段",
    "复制后删除原有的对象":"复制后删除原有的对象",
    "已选择个待复制":"已选择 {0} 个待复制",
    "复制":"复制",
    "复制并编辑":"复制并编辑",
    "复制评论":"复制评论",
    "复制父子对象":"复制父子对象",
    "复制变更记录":"复制变更记录",
    "复制关联对象":"复制前后关联对象"
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
        :title="$t('复制')"
        width="700">
        <Form label-position="top" style="height:560px;padding:0 15px">
            <FormItem :label="$t('复制到项目')">
                <Select
                    transfer
                    style="width:300px"
                    @on-change="loadIterationList"
                    :placeholder="$t('选择项目')"
                    v-model="formItem.projectId">
                    <Option v-for="item in projectList" :key="item.id" :value="item.id">{{item.name}}</Option>
                </Select>
            </FormItem>

            <FormItem v-if="iterationList.length>0" :label="$t('迭代')">
                <Select transfer style="width:300px" :placeholder="$t('选择迭代')" v-model="formItem.iterationId" clearable>
                    <Option v-for="item in iterationList" :key="item.id" :value="item.id">{{item.name}}</Option>
                </Select>
            </FormItem>
            <FormItem :label="$t('类型')">
                <Select transfer style="width:200px" v-model="formItem.objectType">
                    <Option v-for="item in moduleList" v-if="item.objectType>0" :value="item.objectType" :key="item.id">
                        {{ item.name }}
                    </Option>
                </Select>
                <div style="color:#999">{{$t('类型之间转换可能会丢失自定义字段')}}</div>
            </FormItem>

            <FormItem :label="$t('复制后删除原有的对象')">
                <i-Switch v-model="formItem.deleteOrigin"/>
            </FormItem>

            <FormItem :label="$t('复制评论')">
                <i-Switch v-model="formItem.copyComments"/>
            </FormItem>

            <FormItem :label="$t('复制变更记录')">
                <i-Switch v-model="formItem.copyChangeLogs"/>
            </FormItem>
            <FormItem :label="$t('复制关联对象')">
                <i-Switch v-model="formItem.copyAssociatedTasks"/>
            </FormItem>
            <FormItem :label="$t('复制父子对象')">
                <i-Switch  v-model="formItem.copyParentTasks"/>
                <span style="font-size: 12px;color: #bbbbbb;">请同时勾选父子对象</span>
            </FormItem>

        </Form>
        <div slot="footer">
            <Row>
                <Col span="12">
                    <div style="color:#999;text-align: left;">
                        {{$t('已选择个待复制',[itemList.length])}}{{objectType|dataDict('Task.objectType')}}
                    </div>
                </Col>
                <Col span="12" style="text-align:right">
                    <Button @click="confirmForm" type="default" size="large">{{$t('复制')}}</Button>
                    <Button @click="confirmForm(true)" type="default" size="large">{{$t('复制并编辑')}}</Button>
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
                formItem: {
                    projectId: null,
                    iterationId: null,
                    objectType: null,
                    deleteOrigin: false,
                    copyComments: false,
                    copyChangeLogs: false,
                    copyAssociatedTasks: false,
                    copyParentTasks: false,
                    copySubTasks: false
                },
                withEdit: false,
                editItemList: [],
                projectList: [],
                iterationList: [],
                moduleList: [],
                itemList: [],
                objectType: 0,
                idMap: new Map(),
                parentMap:new Map()
            };
        },
        watch: {
            'formItem.projectId': function (val) {
                this.loadIterationList();
                this.loadProjectModule();
                if (val != this.args.projectId) {
                    this.formItem.copyParentTasks = false;
                    this.formItem.copySubTasks = false;
                }
            },
        },
        methods: {
            pageLoad() {
                //console.log('xxxxxx',this.args)
                this.itemList = this.args.itemList;
                this.itemList.forEach(item=>{
                    this.parentMap.set(item.id,item.parentId)
                })
                this.objectType = this.args.objectType;
                this.formItem.objectType = this.args.objectType;
                this.formItem.projectId = this.args.projectId;
                this.formItem.iterationId = this.args.iterationId;
                this.loadProjectList();
            },
            loadProjectList() {
                app.invoke('BizAction.getMyProjectList', [app.token], list => {
                    this.projectList = list;
                });
            },
            loadIterationList() {
                app.invoke('BizAction.getProjectIterationInfoList', [app.token, this.formItem.projectId], list => {
                    this.iterationList = list;
                    this.formItem.iterationId = null;
                    if (list.length > 0) {
                        this.formItem.iterationId = list[0].id;
                    }
                });
            },
            loadProjectModule() {
                app.invoke('BizAction.getProjectModuleInfoList', [app.token, this.formItem.projectId], (list) => {
                    this.moduleList = list;

                });
            },
            confirmForm(withEdit) {
                this.withEdit = withEdit === true;
                this.editItemList = [];
                app.showDialog(MultiOperateDialog, {
                    title: this.$t('复制'),
                    runCallback: this.copyAction,
                    finishCallback: this.finishRun,
                    itemList: this.itemList,
                });
            },
            finishRun() {
                if (this.formItem.copyParentTasks && this.itemList.length > 1) {
                    let relations = [];
                    this.parentMap.forEach((pid,id)=>{
                        var newId= this.idMap.get(id);
                        var newParentId = this.idMap.get(pid);
                        if(newId&&newParentId){
                            relations.push({
                                id: newId,
                                parentId: newParentId
                            })
                        }
                    })
                    if(relations.length>0){
                        app.invoke('BizAction.setCopyTaskPubsubRelation', [app.token, relations], (res) => {
                            this.afterFinish();
                        });
                    }else{
                        this.afterFinish();
                    }
                } else {
                    this.afterFinish();
                }
            },
            afterFinish() {
                if (this.withEdit && Array.isArray(this.editItemList) && this.editItemList.length > 0) {
                    if (this.editItemList.length > 1) {
                        app.showDialog(TaskEditDialog, {
                            itemList: [
                                ...this.editItemList,
                            ],
                            objectType: this.formItem.objectType,
                            projectId: this.formItem.projectId,
                        });
                    } else {
                        const editItem = this.editItemList[0];
                        app.showDialog(TaskDialog, {
                            taskId: editItem.uuid,
                            showTopBar: true,
                        });
                    }
                }
                app.postMessage('task.edit');
                this.editItemList = [];
                this.withEdit = false;
                this.showDialog = false;
            },
            copyAction(item, success, error) {
                app.invoke('BizAction.batchCopyTask', [app.token,
                    [item.id],
                    this.formItem.projectId,
                    this.formItem.iterationId,
                    this.formItem.objectType,
                    this.formItem.deleteOrigin,
                    this.formItem.copyComments,
                    this.formItem.copyChangeLogs,
                    this.formItem.copyAssociatedTasks,
                    this.formItem.copyParentTasks,
                    this.formItem.copySubTasks,
                ], (list) => {
                    if (!Array.isArray(list) || list.length === 0) {
                        success();
                        return;
                    }
                    this.idMap.set(item.id, list[0].id);
                    this.editItemList.push(list[0]);
                    success();
                }, error);
            },
        },
    };
</script>
