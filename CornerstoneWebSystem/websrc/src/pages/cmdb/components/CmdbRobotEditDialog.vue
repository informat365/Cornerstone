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
        "Robot名称": "Robot名称",
		"备注": "备注",
		"备注信息": "备注信息",
		"cron表达式": "cron表达式",
		"定时执行主机": "定时执行主机",
		"选择主机": "选择主机",
		"删除": "删除",
		"保存": "保存",
		"创建": "创建",
		"确认要删除Robot吗": "确认要删除Robot “{0}” 吗？",
		"删除成功": "删除成功",
		"保存成功": "保存成功",
		"创建成功": "创建成功"
    },
	"zh_CN": {
        "名称": "名称",
        "Robot名称": "Robot名称",
		"备注": "备注",
		"备注信息": "备注信息",
		"cron表达式": "cron表达式",
		"定时执行主机": "定时执行主机",
		"选择主机": "选择主机",
		"删除": "删除",
		"保存": "保存",
		"创建": "创建",
		"确认要删除Robot吗": "确认要删除Robot “{0}” 吗？",
		"删除成功": "删除成功",
		"保存成功": "保存成功",
		"创建成功": "创建成功"
	}
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" title="CMDB Robot" width="90%" >

    <Form  ref="form"  :rules="formRule" :model="formItem"  label-position="top" style="height:calc(100vh - 200px);padding:15px">
        

         <Row>
            <Col span="11">
                <FormItem :label="$t('名称')" prop="name">
                    <Input :placeholder="$t('Robot名称')" v-model.trim="formItem.name" style="width:100%"></Input>
                     </FormItem>
            </Col>
            <Col span="11" offset="2">
               <FormItem :label="$t('备注')" prop="remark">
                    <Input :placeholder="$t('备注信息')" v-model.trim="formItem.remark" style="width:100%"></Input>
                </FormItem>
            </Col>
        </Row>

          <Row>
            <Col span="11">
                <FormItem label="cron" prop="cron">
                    <Input :placeholder="$t('cron表达式')" v-model.trim="formItem.cron" style="width:100%"></Input>
                     </FormItem>
            </Col>
            <Col span="11" offset="2">
               <FormItem :label="$t('定时执行主机')">
                    <Select clearable multiple filterable v-model="formItem.machineList" :placeholder="$t('选择主机')" style="width:100%">
                            <Option v-for="item in machineList" :value="item.id" :key="'prj'+item.id">{{ item.name }}</Option>
                        </Select>
                </FormItem>
            </Col>
        </Row>

          <FormItem label="" v-if="formItem.id>0">
            <Button @click="deleteItem" type="error" size="large" >{{$t('删除')}}</Button>
        </FormItem> 


        <FormItem label="Robot Code" >
            <div >
                 <MonacoEditor style="height:calc(100vh - 260px)" ref="editor"  v-model="formItem.code" mode="javascript" ></MonacoEditor>
            </div>
        </FormItem>

    </Form>

    
    <div slot="footer">
        <Row>
             <Col span="24" style="text-align:right"> 
                <Button @click="confirm" type="default" size="large" >{{$t(formItem.id>0?'保存':'创建')}}</Button>
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
               
                formItem:{
                    id:0,
                    name:null,
                    remark:null,
                    code:null,
                    cron:null,
                    machineList:[],
                },
                formRule:{
                    name:[vd.req,vd.name],
                    remark:[vd.desc],
                    cron:[vd.desc],
                },
                machineList:[],
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
                this.loadMachineList();
                if(this.args.id){
                    this.loadData(this.args.id);
                    this.addKeyListener();
                }
            },
            loadMachineList(){
                var query={
                    pageIndex:1,
                    pageSize:1000,
                }
                app.invoke('BizAction.getCmdbMachineList',[app.token,query],(info)=>{
                    this.machineList=info.list;
                })   
            }, 
            loadData(id){
                 app.invoke('BizAction.getCmdbRobotById',[app.token,id],(info)=>{
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
            deleteItem(){
                app.confirm(this.$t('确认要删除Robot吗',[this.formItem.name]),()=>{
                    app.invoke('BizAction.deleteCmdbRobot',[app.token,this.formItem.id],(info)=>{
                        app.toast(this.$t('删除成功'))
                        app.postMessage('cmdrobot.edit')
                        this.showDialog=false;
                    })
                })
            },
            confirm(){
                this.$refs.form.validate((r)=>{if(r)this.confirmForm()});
            },
            confirmForm(){
                this.formItem.code=this.$refs.editor.getValue();
                var action=this.formItem.id>0?'BizAction.updateCmdbRobot':'BizAction.addCmdbRobot';
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.toast(this.formItem.name+' '+this.$t(this.formItem.id>0?'保存成功':'创建成功'));
                    app.postMessage('cmdrobot.edit')
                    if(this.formItem.id==0){
                        this.showDialog=false;
                    }
			    })
            }
        }
    }
</script>