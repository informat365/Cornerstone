<style scoped>
   .code-result-box{
       height: calc(100vh - 190px);
       
   }
</style>
<i18n>
{
	"en": {
        "生成结果": "生成结果",
		"文件": "文件"
    },
	"zh_CN": {
		"生成结果": "生成结果",
		"文件": "文件"
	}
}
</i18n>
 <template>
    <Modal
        ref="dialog" class="full-modal" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('生成结果')" width="1000" :footer-hide="true" >
    <div style="width:100%;">
         <Form style="padding:10px;padding-bottom:0" ref="form" label-position="left"  :label-width="40">
                    <FormItem :label="$t('文件')">
                            <RadioGroup v-model="formItem.file" type="button">
                                    <Radio v-for="item in responseList" :key="item.fileName" :label="item.fileName"></Radio>
                            </RadioGroup>
                    </FormItem>
        </Form>    

        <div class="code-result-box">
            <MonacoEditor style="height:calc(100vh - 200px)" ref="editor"  v-model="fileContent" :mode="editMode" ></MonacoEditor>
         </div>  

    </div>
   
    </Modal>
</template>


<script>

export default {
    mixins: [componentMixin],
    data () {
        return {
            formItem: {
                file:null,
            },
            responseList:[],
            fileContent:"",
            editMode:"plaintext"
        }
    },
   watch:{
        "formItem.file":function(val){
            for(var i=0;i<this.responseList.length;i++){
                var t=this.responseList[i];
                if(t.fileName==val){
                    this.fileContent=(t.content);
                    if(t.language==1){
                        this.editMode=("java");
                    }
                    if(t.language==2||t.language==6){
                        this.editMode=("csharp");
                    }
                    if(t.language==3){
                        this.editMode=("javascript");
                    }
                    if(t.language==4){
                        this.editMode=("sql");
                    }
                    if(t.language==5){
                        this.editMode=("html");
                    }
                }
            }
    },
   },
    methods: {
        pageLoad(){
            var req={
                designerDatabaseId:this.args.databaseId,
                tableName:this.args.tableId,
                designerTemplateId:this.args.templateId
            }
            app.invoke('DesignerAction.generateCode',[app.token,req],(obj)=>{
                this.responseList=obj;
                this.formItem.file=this.responseList[0].fileName;
            });
        }
    }
}
</script>