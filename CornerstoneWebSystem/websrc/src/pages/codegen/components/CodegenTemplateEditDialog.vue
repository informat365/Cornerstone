<style scoped>
   .dialog-wrap{
       display: flex;
       flex-direction: column;
       height: calc(100vh - 150px);
   }

</style>
<i18n>
{
	"en": {
        "编辑模板": "编辑模板",
		"名称": "名称",
		"语言": "语言",
		"确定": "确定",
		"保存成功": "保存成功",
		"操作成功": "操作成功"
    },
	"zh_CN": {
		"编辑模板": "编辑模板",
		"名称": "名称",
		"语言": "语言",
		"确定": "确定",
		"保存成功": "保存成功",
		"操作成功": "操作成功"
	}
}
</i18n>
 <template>
    <Modal 
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('编辑模板')" class="fullscreen-title-modal"  fullscreen >
    <div>
    <div class="dialog-wrap">
         <Form ref="form" label-position="left" :rules="formRule" :model="formItem" :label-width="60">
                    <FormItem :label="$t('名称')" prop="name">
                        <Input v-model="formItem.name"></Input>
                    </FormItem>
                    <FormItem :label="$t('语言')" prop="language">
                        <DataDictRadio v-model="formItem.language"  type="DesignerTemplate.language"></DataDictRadio>
                    </FormItem>
        </Form> 
        <div style="flex:1">
            <MonacoEditor style="height:calc(100vh - 260px)" ref="editor"  v-model="formItem.content" mode="plaintext" ></MonacoEditor>
        </div>
    </div>                
    </div>
    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> 
                <Button @click="confirm" type="default" size="large" >{{$t('确定')}}</Button>
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
            formItem: {
                language:1,
                name: '',
                content:'',
            },
            formRule:{
                name:[vd.req,vd.name],
                language:[vd.req]
            },
        }
    },
    methods: {
        pageLoad(){
             if(this.args.id){
                app.invoke('DesignerAction.getDesignerTemplateById',[app.token,this.args.id],(obj)=>{
                    this.formItem=obj;  
                });
            }
        },
        confirm:function(){
            this.$refs["form"].validate((r)=>{
                if(r){ this.confirmEdit();}
            });
        },
        saveTemplate:function(){
            this.formItem.content=this.$refs.editor.getValue();
            if(this.formItem.id){
                app.invoke("DesignerAction.updateDesignerTemplate",
                    [app.token,this.formItem],()=>{
                        app.toast(this.$t("保存成功"));
                });
            }
        },
        confirmEdit:function(){
            this.formItem.content=this.$refs.editor.getValue();
             app.invoke(this.formItem.id?"DesignerAction.updateDesignerTemplate":"DesignerAction.addDesignerTemplate",
                [app.token,this.formItem],()=>{
                app.toast(this.$t("操作成功"));
                this.showDialog=false;
                app.postMessage('template-edit');
            });
        },
    }
}
</script>