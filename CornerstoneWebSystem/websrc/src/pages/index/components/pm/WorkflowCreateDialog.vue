<style scoped>
    .dialog-bg{
        min-height:600px;
        background-color: #F1F4F5;
        padding:20px;
    }
    .card-wrap-box{
        display: flex;
        align-items: center;
        flex-wrap: wrap;
    }
    .template-card{
        width:30%;
        border:1px solid #eee;
        background-color: #fff;
        padding:15px;
        border-radius: 5px;
        cursor: pointer;
        transition: all 0.3s;
        margin-right:10px;
    }
    .template-card:hover{
        box-shadow: 0px 2px 3px 1px rgba(0,0,0,0.28);
    }
    .template-name{
        font-size:14px;
        font-weight: bold;
    }
    .template-remark{
        font-size:12px;
        color:#666;
        margin-top:5px;
        padding-left:10px;
    }
</style>

<i18n>
{
    "en": {
        "发起流程": "Start",
        "输入名称搜索": "Search title",
        "未分组": "Ungrouped"
    },
    "zh_CN": {
        "发起流程": "发起流程",
        "输入名称搜索": "输入名称搜索",
        "未分组": "未分组"
    }
}
</i18n>

 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false" :footer-hide="true"
        :loading="false" class="nopadding-modal" :title="$t('发起流程')" width="800" >
        <div class="dialog-bg">
            <div><Input icon="ios-search" :placeholder="$t('输入名称搜索')" v-model="searchContent"/></div>
            <template v-for="group in groupProjectList">
                    <Divider :key="'div_'+group.name" orientation="left">{{group.name}}</Divider>
                    <div class="card-wrap-box" :key="'div_box_'+group.name">
                        <div
                            @click="applyWorkflow(item)"
                            v-for="item in group.list"
                            :key="'prj_'+item.id"
                            class="template-card">
                            <WorkflowDefineNameLabel style="font-size:14px;" :name="item.name" :color="item.color"></WorkflowDefineNameLabel>
                            <div v-if="item.remark" class="text-no-wrap template-remark">{{item.remark}}</div>
                        </div>
                    </div>
            </template>
        </div>
    </Modal>
</template>


<script>
    export default {
        mixins: [componentMixin],
        data () {
            return {
                templateList:[],
                searchContent:null
            }
        },
        computed: {
            groupProjectList() {
                var list = [];
                var groupMap = {};
                for (var i = 0; i < this.templateList.length; i++) {
                    var t = this.templateList[i];
                    if(this.searchContent!=null){
                        if(t.name.indexOf(this.searchContent)==-1){
                            continue;
                        }
                    }
                    if (t.group == null || t.group == "") {
                        t.group = this.$t('未分组');
                    }
                    if (groupMap[t.group] == null) {
                        groupMap[t.group] = {
                            name: t.group,
                            list: []
                        };
                        list.push(groupMap[t.group]);
                    }
                    groupMap[t.group].list.push(t);
                }
                return list;
            }
        },
        methods: {
            pageLoad(){
                this.loadTemplate();
            },
            loadTemplate(){
                app.invoke('WorkflowAction.getMyWorkflowDefineList',[app.token],(list)=>{
                    this.templateList=list;
                });
            },
            applyWorkflow(item){
                app.invoke('WorkflowAction.applyWorkflow',[app.token,item.id],(id)=>{
                    this.showDialog=false;
                    app.postMessage('workflow.edit')
                    app.showDialog(WorkflowDialog,{
                        id:id
                    })
                });
            }
        }
    }
</script>
