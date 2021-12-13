<style scoped>
.toggle-btn{
    position: absolute;
    right:0;
    top:-30px;
   
}
</style>
<i18n>
{
	"en": {
		"名称": "名称",
        "API名称": "API名称",
		"备注": "备注",
		"备注信息": "备注信息",
		"删除": "删除",
		"调用地址": "调用地址",
		"重新生成": "重新生成",
		"保存": "保存",
		"创建": "创建",
        "确定要重新生成吗": "确定要重新生成api key吗？重新生成后使用原有api key将不能访问",
        "确认要删除吗": "确认要删除API “{0}” 吗？",
		"删除成功": "删除成功",
		"保存成功": "保存成功",
		"创建成功": "创建成功"
	},
	"zh_CN": {
        "名称": "名称",
        "API名称": "API名称",
		"备注": "备注",
		"备注信息": "备注信息",
		"删除": "删除",
		"调用地址": "调用地址",
		"重新生成": "重新生成",
		"保存": "保存",
		"创建": "创建",
        "确定要重新生成吗": "确定要重新生成api key吗？重新生成后使用原有api key将不能访问",
        "确认要删除吗": "确认要删除API “{0}” 吗？",
		"删除成功": "删除成功",
		"保存成功": "保存成功",
		"创建成功": "创建成功"
	}
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" title="CMDB API" width="90%" >

    <Form  ref="form"  :rules="formRule" :model="formItem"  label-position="top" style="height:calc(100vh - 200px);padding:15px">
        <div >

         <Row>
            <Col span="11">
                <FormItem :label="$t('名称')" prop="name">
                    <Input :placeholder="$t('API名称')" v-model.trim="formItem.name" style="width:100%"></Input>
                     </FormItem>
            </Col>
            <Col span="11" offset="2">
               <FormItem :label="$t('备注')" prop="remark">
                    <Input :placeholder="$t('备注信息')" v-model.trim="formItem.remark" style="width:100%"></Input>
                </FormItem>
            </Col>
        </Row>

          <FormItem label="" v-if="formItem.id>0">
            <Button @click="deleteItem" type="error" size="large" >{{$t('删除')}}</Button>
        </FormItem> 

        <FormItem v-if="formItem.id>0" label="API Key">
             <Alert>
                <div>{{$t('调用地址')}}:{{apiAddress}}/p/webapi/cmdb_api/{{formItem.apiKey}}
                     <Button style="margin-left:10px" @click="reloadKey" type="default">{{$t('重新生成')}}</Button>
                </div>
             </Alert>
           
        </FormItem>
        </div>

        <FormItem label="API Code" >
            <div >
                <MonacoEditor style="height:calc(100vh - 260px)" ref="editor"  v-model="formItem.code" mode="javascript" ></MonacoEditor>
            </div>
        </FormItem>

    </Form>

    
    <div slot="footer">
        <Row>
             <Col span="24" style="text-align:right"> 
                <Button @click="confirm" type="default" size="large" >{{formItem.id>0?$t('保存'):$t('创建')}}</Button>
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
                apiAddress:"",
                formItem:{
                    id:0,
                    name:null,
                    remark:null,
                    code:null
                },
                formRule:{
                    name:[vd.req,vd.name],
                    remark:[vd.desc],
                },
            }
        },
        watch:{
            showDialog(val){
                if(val==false){
                    this.removeKeyListener();
                }
            }
        },
        methods: {
            pageLoad(){
                this.apiAddress=app.getHost();
                if(this.args.id){
                    this.loadData(this.args.id);
                    this.addKeyListener();
                }
            },
          
            loadData(id){
                 app.invoke('BizAction.getCmdbApiById',[app.token,id],(info)=>{
                    this.formItem=info;
                })
            },
            addKeyListener(){
                this.$el.addEventListener("keydown",this.keyHandler, false);
            },
            removeKeyListener(){
                this.$el.removeEventListener("keydown",this.keyHandler)
            },
            keyHandler(e){
                if (e.keyCode == 83 && (navigator.platform.match("Mac") ? e.metaKey : e.ctrlKey)) {
                    e.preventDefault();
                    this.confirm();
                }
            },
            reloadKey(){
                app.confirm(this.$t('确定要重新生成吗'),()=>{
                    app.invoke('BizAction.reloadCmdbApiKey',[app.token,this.formItem.id],(info)=>{
                        this.loadData(this.formItem.id);
                    })
                })
            },
            deleteItem(){
                let confirmMsg = this.$t('确认要删除吗',[this.formItem.name]);
                app.confirm(confirmMsg,()=>{
                    app.invoke('BizAction.deleteCmdbApi',[app.token,this.formItem.id],(info)=>{
                        app.toast(this.$t('删除成功'))
                        app.postMessage('cmdapi.edit')
                        this.showDialog=false;
                    })
                })
            },
            confirm(){
                this.$refs.form.validate((r)=>{if(r)this.confirmForm()});
            },
            confirmForm(){
                this.formItem.code=this.$refs.editor.getValue();
                var action=this.formItem.id>0?'BizAction.updateCmdbApi':'BizAction.addCmdbApi';
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.toast(this.formItem.name+' '+this.$t(this.formItem.id>0?'保存成功':'创建成功'));
                    app.postMessage('cmdapi.edit')
                    if(this.formItem.id==0){
                        this.showDialog=false;
                    }
                })
            }
        }
    }
</script>