<style scoped>
   .wrap-box{
       display: flex;
       height:calc(100vh - 220px);
       padding:15px;
       margin-bottom:10px;
       flex-direction: row;
   }
   .left{
       width:250px;
       padding-right:10px;
   }
   .right{
       flex:1;
   }
   .address{
        word-break: break-all;
        color:#666;
        font-weight: bold;
   }
   .reset-btn{
       cursor: pointer;
       margin-left:5px;
       color:#3C97F0;
   }
</style>


<i18n>
    {
    "en": {
    "创建WebApi":"Create WebApi",
    "名称":"Name",
    "备注":"Remark",
    "删除":"Delete",
    "禁用":"Disable",
    "启用":"Enable",
    "调用地址：":"Api address：",
    "重置":"Reset",
    "脚本":"Script",
    "继续创建下一个":"Continue to create next",
    "保存":"Save",
    "创建":"Create",
    "地址已保存到剪切板中":"The address has been saved to the clipboard",
    "确认要重置调用地址吗？重置后原有地址将不能使用。":"Are you sure you want to reset the address? The original address will not be available after reset.",
    "确认要删除WebApi吗？":"Are you sure you want to delete WebApi “{0}” ？",
    "删除成功":"Delete success",
    "保存成功":"Save success",
    "创建成功":"Create success",
    "操作成功":"Success"
    },
    "zh_CN": {
    "创建WebApi":"创建WebApi",
    "名称":"名称",
    "备注":"备注",
    "删除":"删除",
    "禁用":"禁用",
    "启用":"启用",
    "调用地址：":"调用地址：",
    "重置":"重置",
    "脚本":"脚本",
    "继续创建下一个":"继续创建下一个",
    "保存":"保存",
    "创建":"创建",
    "确认要重置调用地址吗？重置后原有地址将不能使用。":"确认要重置调用地址吗？重置后原有地址将不能使用。",
    "地址已保存到剪切板中":"地址已保存到剪切板中",
    "确认要删除WebApi吗？":"确认要删除WebApi “{0}” 吗？",
    "删除成功":"删除成功",
    "保存成功":"保存成功",
    "创建成功":"创建成功",
    "操作成功":"操作成功"
    }
    }
</i18n>

 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('创建WebApi')" width="1200"  @on-ok="confirm">
    <div class="wrap-box">
        <Form  class="left"  @submit.native.prevent ref="form" :rules="formRule" :model="formItem" label-position="top" >

            <FormItem :label="$t('名称')" prop="name">
                <Input v-model.trim="formItem.name" :placeholder="$t('名称')"></Input>
            </FormItem>
            <FormItem :label="$t('备注')" prop="remark">
                <Input v-model.trim="formItem.remark" :placeholder="$t('备注')"></Input>
            </FormItem>

            <FormItem label="" v-if="formItem.id>0">
                <Button @click="deleteItem" type="error" >{{$t('删除')}}</Button>
                <Button v-if="formItem.status==1" style="margin-left:10px" @click="setEnable(false)" type="default">{{$t('禁用')}}</Button>
                <Button v-if="formItem.status==2" style="margin-left:10px" @click="setEnable(true)" type="default">{{$t('启用')}}</Button>
            </FormItem>
        </Form>

        <Form class="right">
            <div class="address" >
               <span v-clipboard:copy="''+apiAddress+'/p/webapi/run_web_api/'+formItem.apiKey" v-clipboard:success="copySuccess">{{$t('调用地址：')}}{{apiAddress}}/p/webapi/cmdb_api/{{formItem.apiKey}}</span>
               <span @click="resetApiKey" class="reset-btn">{{$t('重置')}}</span>
            </div>
            <FormItem :label="$t('脚本')"  prop="script">
                <div>
                    <MonacoEditor :style="editorStyle" ref="editor" mode="javascript"  v-model="formItem.script"></MonacoEditor>
                </div>
            </FormItem>
        </Form>
    </div>
    <div slot="footer">
        <Row>
            <Col span="12" style="text-align:left;padding-top:5px"> <Checkbox v-if="formItem.id==0" v-model="continueCreate" size="large">{{$t('继续创建下一个')}}</Checkbox></Col>
            <Col span="12" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{formItem.id>0?$t('保存'):$t('创建')}}</Button></Col>
         </Row>

    </div>

    </Modal>
</template>


<script>
var demoCode=
`var webApi={

}
`
export default {
        mixins: [componentMixin],
        data () {
            return {
                continueCreate:false,
                apiAddress:"",
                formItem:{
                    id:0,
                    name:null,
                    remark:null,
                    script:demoCode
                },
                editorFullScreen:false,
                editorStyle:"height:calc(100vh - 270px)",
                formRule:{
                    name:[vd.req,vd.name],
                    group:[vd.name],
                    remark:[vd.desc],
                }
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
                this.loadData();
            },
            loadData(){
                if(this.args.id){
                    this.addKeyListener();
                    app.invoke('WebApiAction.getWebApiById',[app.token,this.args.id],(info)=>{
                        this.formItem=info;
                    })
                }
            },
            copySuccess(){
                app.toast(this.$t('地址已保存到剪切板中'));
            },
            setEnable(enable){
                var status=enable?1:2;
                app.invoke('WebApiAction.updateWebApiStatus',[app.token,this.formItem.id,status],(info)=>{
                    app.toast(this.$t('操作成功'))
                    app.postMessage('webapi.edit')
                    this.showDialog=false;
                })
            },
            resetApiKey(){
                 app.confirm(this.$t('确认要重置调用地址吗？重置后原有地址将不能使用。'),()=>{
                     this.resetApiKey0();
                 });
            },
            resetApiKey0(){
                app.invoke('WebApiAction.refreshWebApiKey',[app.token,this.formItem.id],(info)=>{
                    app.toast(this.$t('操作成功'))
                    this.loadData();
                })
            },
            deleteItem(){
                app.confirm(this.$t('确认要删除WebApi吗？',[this.formItem.name]),()=>{
                    app.invoke('WebApiAction.deleteWebApi',[app.token,this.formItem.id],(info)=>{
                        app.toast(this.$t('删除成功'))
                        app.postMessage('webapi.edit')
                        this.showDialog=false;
                    })
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
            confirm(){
                this.$refs.form.validate((r)=>{
                    if(r)this.confirmForm()
                });
            },
            confirmForm(){
                this.formItem.script=this.$refs.editor.getValue();
                var action=this.formItem.id>0?'WebApiAction.updateWebApi':'WebApiAction.addWebApi';
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.toast(this.formItem.name+(this.formItem.id>0?this.$t('保存成功'):this.$t('创建成功')))
                    app.postMessage('webapi.edit')
                    if(this.formItem.id==0){
                        if(!this.continueCreatet){
                            this.showDialog=false;
                        }else{
                            this.$refs.form.resetFields();
                        }
                    }
			    })
            }
        }
    }
</script>
