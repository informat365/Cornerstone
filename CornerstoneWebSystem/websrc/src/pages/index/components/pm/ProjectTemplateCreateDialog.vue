<style scoped>
    .project-type-card {
        border: 1px solid #dcdee2;
        width: 210px;
        height: 100px;
        margin-top: 15px;
        margin-right: 15px;
        display: inline-block;
        position: relative;
        overflow: hidden;
        padding: 10px;
        background-color: #fff;
    }

    .project-type-box {
        text-align: left;
        display: flex;
        flex-wrap: wrap;
    }

    .project-card-select .check {
        visibility: visible;
    }

    .check {
        visibility: hidden;
        position: absolute;
        top: 30px;
        left: 80px;
        font-size: 50px;
        color: #0097f7;
    }

    .project-type-name {
        color: #333;
        font-weight: bold;
        padding-top: 10px;
        font-size: 16px;
        text-align: center;
    }

    .project-type-desc {
        color: #666;
        font-size: 12px;
        text-align: center;
        line-height: 1.2;
        padding-bottom: 10px;
    }

</style>
<i18n>
    {
    "en": {
    "创建项目": "Create Project",
    "模板":"Template",
    "名称":"Name",
    "开始日期":"Start date",
    "截止日期":"Due date",
    "项目名称":"Project name",
    "项目描述":"Remark",
    "项目模块":"Project modules",
    "继续创建下一个":"Continue to create next",
    "创建":"Create",
    "请选择项目类型":"Please choose project type"
    },
    "zh_CN": {
    "创建项目": "创建项目",
    "模板":"模板",
    "名称":"名称",
    "开始日期":"开始日期",
    "截止日期":"截止日期",
    "项目名称":"项目名称",
    "项目描述":"项目描述",
    "项目模块":"项目模块",
    "继续创建下一个":"继续创建下一个",
    "创建":"创建",
    "请选择项目类型":"请选择项目类型"
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
        :title="'【'+$t('模板')+'】'+$t('创建项目')"
        width="750">

        <Form
            @submit.native.prevent
            ref="form"
            :rules="formRule"
            :model="formItem"
            label-position="top"
            style="height:400px;padding:15px">
            <FormItem :label="$t('名称')" prop="name">
                <Input v-model.trim="formItem.name" :placeholder="$t('项目名称')"/>
            </FormItem>
            <FormItem :label="$t('项目描述')" prop="description">
                <Input v-model.trim="formItem.description" type="textarea" :rows="3"></Input>
            </FormItem>
            <FormItem  :label="$t('项目模块')">
                <CheckboxGroup @on-change="checkModules" v-model="checkIds">
<!--                    <Checkbox v-for="item in sysObjectTypeList" :label="item.id" :key="item.id" disabled>{{item.name}}</Checkbox>-->
                    <Checkbox v-for="item in objectTypeList" :label="item.id" :key="item.id" >{{item.name}}</Checkbox>
                </CheckboxGroup>
            </FormItem>
        </Form>




        <div slot="footer">
            <Row>
                <Col span="12" style="text-align:left;padding-top:5px">
                </Col>
                <Col span="12" style="text-align:right">
                    <Button @click="confirm" type="default" size="large">{{$t('创建')}}</Button>
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
                    name: null,
                    description: null,
                },
                formRule: {
                    name: [vd.req, vd.name],
                    description: [vd.desc],
                },
                checkIds: [],
                //     agile file wiki Devops stage delivery
                sysObjectTypeList:[
                    {
                        id:-7,
                        name:'里程碑'
                    },
                    {
                        id:-6,
                        name:'敏捷'
                    },
                    {
                        id:-5,
                        name:'文件'
                    },
                    {
                        id:-4,
                        name:'WIKI'
                    },
                    {
                        id:-3,
                        name:'DevOps'
                    },
                    {
                        id:-2,
                        name:'阶段'
                    },
                    {
                        id:-1,
                        name:'交付版本'
                    }
                ],
                objectTypeList:[],
                buttonProps: {
                    type: 'default',
                    size: 'small',
                },
                files:[
                    {
                        title: '文件树',
                        expand: true,
                        render: (h, { root, node, data }) => {
                            return h('span', {
                                style: {
                                    display: 'inline-block',
                                    width: '100%'
                                }
                            }, [
                                h('span', [
                                    h('Icon', {
                                        props: {
                                            type: 'ios-folder-outline'
                                        },
                                        style: {
                                            marginRight: '8px'
                                        }
                                    }),
                                    h('span', data.title)
                                ]),
                                h('span', {
                                    style: {
                                        display: 'inline-block',
                                        float: 'right',
                                        marginRight: '32px'
                                    }
                                }, [
                                    h('Button', {
                                        props: Object.assign({}, this.buttonProps, {
                                            icon: 'ios-add',
                                            type: 'primary'
                                        }),
                                        style: {
                                            width: '64px'
                                        },
                                        on: {
                                            click: () => { this.append(data) }
                                        }
                                    })
                                ])
                            ]);
                        },
                    }
                ],
            };
        },
        methods: {
            pageLoad() {
                this.loadObjectTypeList();
            },
            loadObjectTypeList() {
                app.invoke('BizAction.getObjectTypeList', [app.token,{pageSize:1024}], (info) => {
                    if (!Array.isArray(info.list)) {
                        return;
                    }
                    let ms = info.list.filter(item=>item.id!=1001&&item.id!=1002);
                    this.objectTypeList = this.sysObjectTypeList.concat(ms);
                });
            },
            confirm() {
                this.$refs.form.validate((r) => {
                    if (r) {
                        this.confirmForm();
                    }
                });
            },
            confirmForm() {
                app.invoke('BizAction.createTemplateProject', [
                    app.token,
                    this.formItem,
                    this.checkIds], (uuid) => {
                    app.postMessage('project.edit');
                    app.toast(this.$t('创建成功'));
                    this.showDialog = false;
                });
            },
            checkModules(e){
            },
        },
    };
</script>
