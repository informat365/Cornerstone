<style scoped>
    .owner-view {
        padding: 5px;
        font-size: 13px;
        width: 320px;
    }
</style>
<i18n>
    {
    "en": {
    "确认":"confirm"
    },
    "zh_CN": {
    "确认":"确认"
    }
    }
</i18n>
<template>
    <div class="owner-view">
        <div style="text-align: center;">
            <RadioGroup v-model="selecttype">
                <Radio :label="item.type" v-for="item in selections" :key="item.type">{{item.name}}</Radio>
            </RadioGroup>
        </div>
        <div style="text-align: left;padding-left: 5px;margin-top:5px;border-top: 1px solid #eee;">
            <FileUploadView v-if="selecttype=='local'"  @change="uploadSuccess"></FileUploadView>
            <Tree v-if="selecttype=='file'" @on-select-change="selectFile($event,'file')" :data="fileTreeData"></Tree>
            <Tree v-if="selecttype=='wiki'" @on-select-change="selectFile($event,'wiki')" :data="wikiTreeData"></Tree>
        </div>
        <div class="opt" v-if="btnEnable">
            <Button  type="text" size="small" @click="confirm">{{$t('确认')}}</Button>
            <Button style="margin-left: 8px;"  type="text" size="small" @click="cancel">{{$t('取消')}}</Button>
        </div>
    </div>
</template>
<script>
    export default {
        name: "TaskSelectAttachmentView",
        props:["projectId"],
        data() {
            return {
                selections: [{
                    type: 'local',
                    name: "本地上传"
                }, {
                    type: 'file',
                    name: "关联文件库"
                }, {
                    type: 'wiki',
                    name: "关联wiki"
                }],
                fileTreeData: [],
                wikiTreeData: [],
                selecttype: null,
                btnEnable:false,
                asso:null
            };
        },
        watch: {
            selecttype(val) {
                console.log("selecttype---->", val)
                if(val=='file'){
                    this.loadFullFileTree();
                }else if(val=='wiki'){
                    this.loadFullWikiTree();
                }
            }
        },
        methods: {
            loadFullFileTree() {
                app.invoke("BizAction.getProjectFullFileTree", [app.token, this.projectId], info => {
                    this.fileTreeData = info;
                })
            },
            loadFullWikiTree() {
                app.invoke("BizAction.getProjectFullWikiTree", [app.token, this.projectId], info => {
                    this.wikiTreeData = info;
                })
            },
            selectFile(e,type) {
                console.log("select file--->",e,type)
                if(!e[0]){
                    return;
                }
                if(e[0]&&e[0].isDirectory){
                    return;
                }
                this.asso={name:e[0].title,uuid:e[0].path,type:type};
                this.$emit("on-select",this.asso);
                this.btnEnable =true;
            },
            uploadSuccess(e) {
                this.asso={name:e.name,uuid:e.uuid,type:'local'};
                this.$emit("on-select",this.asso);
                this.btnEnable =true;
            },
            confirm(){
                if(this.asso&&this.asso.uuid){
                    this.$emit("on-confirm");
                    this.btnEnable =false;
                }else{
                    app.toast("请选定需要关联的文件或wiki");
                    return;
                }
            },
            cancel(){
                this.$emit("on-cancel");
            }
        }
    };
</script>
