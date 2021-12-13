<style scoped>
    .check {
        color: #0097f7;
    }

    .tag-color {
        display: inline-block;
        width: 30px;
        height: 30px;
        border-radius: 50%;
        margin-right: 10px;
        color: #fff;
        overflow: hidden;
    }
</style>
<i18n>
    {
    "en": {
    "名称": "Name",
    "添加":"Add"
    },
    "zh_CN": {
    "名称": "名称",
    "添加":"添加"
    }
    }
</i18n>
<template>
    <div>
        <div style="margin-bottom: 20px;">
            <span style="font-size: 10px;color: #ddd;">勾选文件模板后可进行文件树配置</span>
            <Tree expand-node :data="files" :render="renderContent" class="demo-tree-render" @on-select-change="selectNode"></Tree>
        </div>
        <Button :disabled="disabled" type="default" @click="saveTemplateFileTree">{{$t('保存')}}</Button>


    </div>
</template>

<script>

    export default {
        name: "ProjectSettingTemplateFileTreeView",
        mixins: [componentMixin],
        props: ['project'],
        data() {
            return {
                disabled:false,
                buttonProps: {
                    type: 'default',
                    size: 'small',
                },
                files: [
                    {
                        title: '文件树',
                        expand: true,
                        render: (h, {root, node, data}) => {
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
                                            type: 'default',
                                            disabled:this.disabled
                                        }),
                                        style: {
                                            width: '64px'
                                        },
                                        on: {
                                            click: () => {
                                                this.append(data)
                                            }
                                        }
                                    })
                                ])
                            ]);
                        },
                    }
                ],
            };
        },
        mounted() {
            this.disabled = this.project&&this.project.isFinish;
           this.loadTemplateFileTree();
        },
        methods: {
            loadTemplateFileTree(){
                app.invoke('BizAction.getDirectoryNode', [app.token, this.project.id], (info) => {
                    if(!Array.isEmpty(info)){

                        this.files = [{
                            title:'文件树',
                            expand:true,
                            nodeKey:0,
                            children:info
                        }];
                    }
                });
            },
            selectNode(node){
                console.log(node)
            },
            saveTemplateFileTree(){
                if(this.disabled){
                    return;
                }
                app.invoke('BizAction.saveTemplateProjectFileList', [app.token, this.project.id,this.files], (info) => {
                    app.toast("保存成功");
                });
            },
            renderContent(h, {root, node, data}) {
                return h('span', {
                    style: {
                        display: 'inline-block',
                        width: '100%'
                    }
                }, [
                    h('span', [
                        h('Icon', {
                            props: {
                                type: 'ios-paper-outline'
                            },
                            style: {
                                marginRight: '8px'
                            }
                        }),
                        h('span',{
                            style:{
                                color:data.color
                            }
                        }, data.title)
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
                                disabled:this.disabled
                            }),
                            style: {
                                marginRight: '8px'
                            },
                            on: {
                                click: () => {
                                    this.append(data)
                                }
                            }
                        }),
                        h('Button', {
                            props: Object.assign({}, this.buttonProps, {
                                icon: 'ios-remove',
                                disabled:this.disabled||data.title=='文件树',
                            }),
                            style: {
                                marginRight: '8px'
                            },
                            on: {
                                click: () => {
                                    this.remove(root, node, data)
                                }
                            }
                        }),
                        h('Button', {
                            props: Object.assign({}, this.buttonProps, {
                                icon: 'ios-brush',
                                disabled:this.disabled||data.title=='文件树',
                            }),
                            on: {
                                click: () => {
                                    this.edit(root, node, data)
                                }
                            }
                        }),
                    ])
                ]);
            },
            append(data) {
                let _this =this;
                const children = data.children || [];
                app.showDialog(FileDirectoryCreateDialog,{
                    callback:function (res) {
                        children.push({
                            title: res.name,
                            color:res.color,
                            expand: true
                        });
                        _this.$set(data, 'children', children);
                    }
                });
            },
            remove(root, node, data) {
                if (data.nodeKey==0)return;
                const parentKey = root.find(el => el === node).parent;
                const parent = root.find(el => el.nodeKey === parentKey).node;
                const index = parent.children.indexOf(data);
                parent.children.splice(index, 1);
            },
            edit(root,node,data){
                let _this =this;
                if (data.nodeKey==0)return;
                console.log("root:"+root,"node:"+node,"data:"+JSON.stringify(data))
                app.showDialog(FileDirectoryCreateDialog,{
                    item:data,
                    callback:function (res) {
                        _this.$set(data,'color',res.color);
                        _this.$set(data,'title',res.name);
                    }
                });
            }

        },
    };
</script>
