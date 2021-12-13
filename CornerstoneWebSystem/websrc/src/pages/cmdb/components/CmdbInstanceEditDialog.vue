<style scoped>
</style>
<i18n>
{
	"en": {},
	"zh_CN": {
		"批量修改": "批量修改",
		"实例管理": "实例管理",
		"名称": "名称",
		"实例名称": "实例名称",
		"分组": "分组",
		"主机": "主机",
		"应用": "应用",
		"用户名": "用户名",
		"密码": "密码",
		"端口": "端口",
		"登录端口": "登录端口",
		"备注": "备注",
		"备注信息": "备注信息",
		"自定义属性": "自定义属性",
		"属性名称": "属性名称",
		"属性值": "属性值",
		"删除": "删除",
		"新增属性": "新增属性",
		"复制": "复制",
		"继续创建下一个": "继续创建下一个",
		"保存": "保存",
		"创建": "创建",
		"确认要删除实例吗": "确认要删除实例 “{0}” 吗？",
		"删除成功": "删除成功",
		"保存成功": "保存成功",
		"创建成功": "创建成功"
	}
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="batchEdit?'批量修改':'实例管理'" width="700"  @on-ok="confirm">

    <Form ref="form" :rules="formRule" :model="formItem" label-position="top" style="height:600px;padding:15px">
        <Row>
            <Col span="11" >
                <FormItem :label="$t('名称')" prop="name">
                    <Input v-model.trim="formItem.name" :placeholder="$t('实例名称')"></Input>
                </FormItem>
            </Col>
             <Col span="11" offset="2">
                <FormItem :label="$t('分组')" prop="group">
                    <Input v-model.trim="formItem.group" :placeholder="$t('分组')"></Input>
                </FormItem>
            </Col>
        </Row>
       <Row>
            <Col span="11" >
                <FormItem :label="$t('主机')"  prop="machineId">
                    <Select filterable  v-model="formItem.machineId" style="width:100%">
                        <Option v-for="item in allMachineList" :value="item.id" :key="item.name">{{ item.name }}</Option>
                    </Select>
                </FormItem>
            </Col>
             <Col span="11" offset="2">
               <FormItem :label="$t('应用')"  prop="applicationId">
                    <Select filterable  v-model="formItem.applicationId" style="width:100%">
                        <Option v-for="item in allApplicationList" :value="item.id" :key="item.name">{{ item.name }}</Option>
                    </Select>
                </FormItem>
            </Col>
        </Row>
        <Row>
            <Col span="11" >
                <FormItem :label="$t('用户名')">
                    <Input v-model.trim="formItem.user"></Input>
                </FormItem>
            </Col>
             <Col span="11" offset="2">
                <FormItem :label="$t('密码')">
                    <Input type="password" v-model.trim="formItem.password" ></Input>
                </FormItem>
            </Col>
        </Row>
         <FormItem :label="$t('端口')" prop="port">
            <InputNumber v-model.trim="formItem.port" :placeholder="$t('登录端口')" style="width:200px"></InputNumber>
        </FormItem>

         <FormItem :label="$t('备注')" prop="remark">
            <Input type="textarea" :placeholder="$t('备注信息')"  v-model.trim="formItem.remark"></Input>
        </FormItem>

        <FormItem :label="$t('自定义属性')">
            <Row style="margin-bottom:5px" v-for="(item,idx) in propList" :key="'prop'+idx">
            <Col span="6"><Input style="width:100%" v-model.trim="item.name" :placeholder="$t('属性名称')"></Input></Col>
            <Col span="14" offset="1"><Input style="width:100%" v-model.trim="item.value"  :placeholder="$t('属性值')"></Input></Col>
            <Col span="3"><IconButton @click="removeProp(idx)" icon="ios-trash" :title="$t('删除')"/></Col>
            </Row>

            <div>
                <IconButton @click="addProp" icon="ios-add" :title="$t('新增属性')"/>
            </div>
        
        </FormItem>

        <FormItem label="" v-if="formItem.id>0">
            <Button @click="deleteItem" type="error" size="large" >{{$t('删除')}}</Button>
            <Button style="margin-left:10px" @click="copyItem" type="default" size="large" >{{$t('复制')}}</Button>
        </FormItem> 
        
    </Form>

    
    <div slot="footer">
        <Row>
            <Col span="12" style="text-align:left;padding-top:5px"> <Checkbox v-model="continueCreate" v-if="batchEdit==false&&formItem.id==0"  size="large">{{$t('继续创建下一个')}}</Checkbox></Col>
            <Col span="12" style="text-align:right"> <Button @click="confirm" type="default" size="large" >
              <template v-if="batchEdit==false">  {{$t(formItem.id>0?'保存':'创建')}} </template>
              <template v-if="batchEdit==true">  {{$t('批量修改')}} </template>
              
            </Button></Col>
        </Row>
           
    </div>

    </Modal>
</template>


<script>
    export default {
        mixins: [componentMixin],
        data () {
            return {
                allApplicationList:[],
                allMachineList:[],
                continueCreate:false,
                formItem:{
                    id:0,
                    port:null,
                },
                propList:[],
                formRule:{
                    name:[vd.req,vd.name],
                    type:[vd.req,vd.name2_100],
                    remark:[vd.desc],
                    port:[vd.req],
                    group:[vd.req],
                    machineId:[vd.req],
                    applicationId:[vd.req],
                },
                batchEdit:false,
            }
        },
        methods: {
            pageLoad(){
                if(this.args.id){
                    this.loadData(this.args.id);
                }
                if(this.args.editCallback){
                    this.batchEdit=true;
                }
                this.loadApplication();
                this.loadMachine();
            },
            loadData(id){
                 app.invoke('BizAction.getCmdbInstanceById',[app.token,id],(info)=>{
                     for(var k in info.properties){
                         this.propList.push({
                             name:k,
                             value:info.properties[k]
                         })
                     }
                    this.formItem=info;
                    if(this.args.copy){
                        this.formItem.id=0;
                        this.formItem.password=null;
                    }
                })
            },
            loadApplication(){
                var query={
                    pageIndex:1,
                    pageSize:9999
                }
                app.invoke('BizAction.getCmdbApplicationList',[app.token,query],(info)=>{
                    this.allApplicationList=info.list;
                })  
            },
            loadMachine(){
                var query={
                    pageIndex:1,
                    pageSize:9999
                }
                app.invoke('BizAction.getCmdbMachineList',[app.token,query],(info)=>{
                    this.allMachineList=info.list;
                })  
            },
            removeProp(idx){
                this.propList.splice(idx,1);
            },
            addProp(){
                this.propList.push({
                    name:"",
                    value:""
                })
            },
            copyItem(){
                this.showDialog=false;
                app.showDialog(CmdbInstanceEditDialog,{
                    id:this.args.id,
                    copy:true
                })
            },
            deleteItem(){
                app.confirm(this.$t('确认要删除实例吗',[this.formItem.name]),()=>{
                    app.invoke('BizAction.deleteCmdbInstance',[app.token,this.formItem.id],(info)=>{
                        app.toast(this.$t('删除成功'))
                        app.postMessage('cmdbinstance.edit')
                        this.showDialog=false;
                    })
                })
            },
            confirm(){
                this.$refs.form.validate((r)=>{if(r)this.confirmForm()});
            },
            confirmForm(){
                this.formItem.properties={};
                for(var i=0;i<this.propList.length;i++){
                    var t=this.propList[i];
                    if(t.name!=null&&t.name!=''&&t.value!=null&&t.value!=''){
                        this.formItem.properties[t.name]=t.value;
                    }
                }
                if(this.args.editCallback){
                    this.args.editCallback(this.formItem);
                    this.showDialog=false;
                    return;
                }
                var action=this.formItem.id>0?'BizAction.updateCmdbInstance':'BizAction.addCmdbInstance';
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.toast(this.formItem.name+' '+this.$t(this.formItem.id>0?'保存成功':'创建成功'));
                    app.postMessage('cmdbinstance.edit')
                    if(!this.continueCreate){
                        this.showDialog=false;
                    }else{
                        this.$refs.form.resetFields();
                    }
			    })
            }
        }
    }
</script>