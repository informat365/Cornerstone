<style scoped>
.page{
    padding:10px;
    height: 700px;
    position: relative;
}
</style>
<i18n>
{
	"en": {
        "表结构变更对比": "表结构变更对比",
		"分栏模式": "分栏模式"
    },
	"zh_CN": {
		"表结构变更对比": "表结构变更对比",
		"分栏模式": "分栏模式"
	}
}
</i18n>
 <template>
    <Modal
        ref="dialog" class="nopadding-modal" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('表结构变更对比')" width="90%" :footer-hide="true">
         <p slot="header">
            <span>{{$t('表结构变更对比')}}</span>
            <span style="margin-left:10px;color:#999">{{$t('分栏模式')}}：</span>
            <i-Switch v-model="spliteMode" size="small"  @on-change="switchChanged" />
        </p>
    <div class="page">
      
    </div>
    </Modal>
</template>


<script>
export default {
        mixins: [componentMixin],
        data () {
            return {
                  spliteMode:true
            }
        },
        methods: {
            pageLoad(){
                app.invoke('DesignerAction.getDesignerTableChangeLogById',[app.token,this.args.id],(obj)=>{
                    if(this.args.ddl){
                        this.diffUsingJS(this.args.ddl,obj.ddl);
                    }
                    if(this.args.dml){
                        this.diffUsingJS(this.args.dml,obj.dml);
                    }
                });
            },
            switchChanged(){
                this.diffEditor.updateOptions({
                    "renderSideBySide":this.spliteMode  
                })
            },
            diffUsingJS(oldValue,newValue) {
                var t=this.$el.getElementsByClassName('page')[0];
                this.diffEditor = monaco.editor.createDiffEditor(t,{
                    enableSplitViewResizing: false,
	                renderSideBySide: true
                });
                this.diffEditor.setModel({
                    original: monaco.editor.createModel(oldValue,"sql"),
                    modified: monaco.editor.createModel(newValue,"sql")
                })
            }
        }
    }
</script>