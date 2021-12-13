<style scoped>

</style>
<i18n>
{
    "en": {
        "拖动调整分类的结构和顺序": "Drag to reorder",
        "分类":"Category",
        "保存":"Save",
        "保存成功":"Success",
        "所有分类":"All Category",
        "选择项目":"Choose project",
        "选择类型":"Choose Type",
        "当前项目":"Current Project"
    },
    "zh_CN": {
        "拖动调整分类的结构和顺序": "拖动调整分类的结构和顺序",
        "分类":"分类",
        "保存":"保存",
        "保存成功":"保存成功",
        "所有分类":"所有分类",
        "选择项目":"选择项目",
        "选择类型":"选择类型",
        "当前项目":"当前项目"
    }
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('分类')" width="700"  @on-ok="confirm">
        <div style="padding:10px;color:#999;font-size:13px">
            {{$t('拖动调整分类的结构和顺序')}}
        </div>
        <div style="min-height:500px;padding:10px">

            <SortableTree :data="treeData">
                <template slot-scope="{item}">
                    <span style="font-size:13px;padding-left:5px;color:#333">{{item.name}}</span>
                </template>
            </SortableTree>

        </div>
    <div slot="footer">
        <Row>
            <Col span="12" style="text-align:left">
                <span>复制到</span>
                <Select
                    transfer
                    style="width:100px;margin-left:10px;"
                    :placeholder="$t('选择项目')"
                    v-model="copyToProjectId">
                    <Option v-for="item in projectList" :key="item.id" :value="item.id">{{item.name}}</Option>
                </Select>
                <Select transfer :placeholder="$t('选择类型')" style="width:100px;margin-left:10px;margin-right:10px" v-model="copyToObjectType" >
                    <template v-for="item in moduleList" >
                        <Option v-if="item.objectType>0&&(item.objectType!=copyFromObjectType || copyToProjectId != args.projectId)" :value="item.objectType" :key="item.id">{{ item.name }}</Option>
                    </template>
                </Select>
                <Button :disabled="copyToObjectType==null || copyToProjectId==null" @click="confirmCopy" type="default" size="large" >复制</Button>
            </Col>
            <Col span="12" style="text-align:right"> 
                <Button @click="confirm" type="default" size="large" >{{$t('保存')}}</Button>
            </Col>
        </Row>
    </div>

    </Modal>
</template>


<script>
    export default {
        mixins: [componentMixin],
        data () {
            return {
                treeData:{},
                copyFromObjectType:null,
                copyToProjectId:null,
                copyToObjectType:null,
                moduleList:[],
                projectList: [],
            }
        },
        watch: {
            'copyToProjectId': function (val) {
                this.loadProjectModule();
            },
        },
        methods: {
            pageLoad(){
                this.copyFromObjectType=this.args.objectType;
                app.invoke("BizAction.getCategoryNodeList",[app.token,this.args.projectId,
                this.args.objectType],list => {
                    this.treeData={
                        name:this.$t("所有分类"),
                        children: list
                    }
                });
                this.copyToProjectId = this.args.projectId;
                this.loadProjectList();
                // this.loadProjectModule();
            },   
            loadProjectModule(){
                app.invoke('BizAction.getProjectModuleInfoList',[app.token,this.copyToProjectId],(list)=>{
                    
                    this.moduleList=list;
                })
            },
            loadProjectList() {
                app.invoke('BizAction.getMyProjectList', [app.token], list => {
                    const index = list.findIndex(item => item.id === this.args.projectId);
                    if(index < 0) {
                        list.push({ id: this.args.projectId, name: this.$t('当前项目') });
                    }
                    this.projectList = list;
                });
            },
            confirm(){
                app.invoke('BizAction.updateCategorySortWeightAndRelation',[app.token,this.treeData.children],(info)=>{
                    app.toast(this.$t('保存成功'))
                    app.postMessage('category.edit');
                    this.showDialog=false;
                })
            },
            confirmCopy(){
                app.confirm('确认复制所有分类吗？',()=>{
                    app.invoke('BizAction.batchCopyCategories',[app.token,
                        this.args.projectId,
                        this.copyFromObjectType,
                        this.copyToProjectId,
                        this.copyToObjectType],()=>{
                        app.toast('复制成功');
                    })
                })
            }
        }
    }
</script>