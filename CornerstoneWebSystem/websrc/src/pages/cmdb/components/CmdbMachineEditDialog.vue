<style scoped>
.public-key-label{
    word-wrap: break-word;
}
</style>
<i18n>
{
	"en": {
        "主机管理": "主机管理",
		"名称": "名称",
		"主机名称": "主机名称",
		"分组": "分组",
		"外网地址": "外网地址",
		"IP地址或者域名": "IP地址或者域名",
		"内网地址": "内网地址",
		"端口": "端口",
		"登录端口": "登录端口",
		"备注": "备注",
		"备注信息": "备注信息",
		"自定义属性": "自定义属性",
		"属性名称": "属性名称",
		"属性值": "属性值",
		"删除": "删除",
		"新增属性": "新增属性",
		"登录方式": "登录方式",
		"用户名": "用户名",
		"密码": "密码",
		"SSH公钥": "SSH公钥",
		"生成公钥": "生成公钥",
		"将公钥内容添加到文件的末尾": "将公钥内容添加到 {0} 文件的末尾",
		"复制": "复制",
		"继续创建下一个": "继续创建下一个",
		"保存": "保存",
		"创建": "创建",
		"复制成功": "复制成功",
		"确认要删除主机吗": "确认要删除主机 “{0}” 吗？",
		"删除成功": "删除成功",
		"保存成功": "保存成功",
		"创建成功": "创建成功"
    },
	"zh_CN": {
		"主机管理": "主机管理",
		"名称": "名称",
		"主机名称": "主机名称",
		"分组": "分组",
		"外网地址": "外网地址",
		"IP地址或者域名": "IP地址或者域名",
		"内网地址": "内网地址",
		"端口": "端口",
		"登录端口": "登录端口",
		"备注": "备注",
		"备注信息": "备注信息",
		"自定义属性": "自定义属性",
		"属性名称": "属性名称",
		"属性值": "属性值",
		"删除": "删除",
		"新增属性": "新增属性",
		"登录方式": "登录方式",
		"用户名": "用户名",
		"密码": "密码",
		"SSH公钥": "SSH公钥",
		"生成公钥": "生成公钥",
		"将公钥内容添加到文件的末尾": "将公钥内容添加到 {0} 文件的末尾",
		"复制": "复制",
		"继续创建下一个": "继续创建下一个",
		"保存": "保存",
		"创建": "创建",
		"复制成功": "复制成功",
		"确认要删除主机吗": "确认要删除主机 “{0}” 吗？",
		"删除成功": "删除成功",
		"保存成功": "保存成功",
		"创建成功": "创建成功"
	}
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('主机管理')" width="700"  @on-ok="confirm">

    <Form ref="form" :rules="formRule" :model="formItem" label-position="top" style="height:600px;padding:15px">
        <Row>
            <Col span="11" >
                <FormItem :label="$t('名称')" prop="name">
                    <Input v-model.trim="formItem.name" :placeholder="$t('主机名称')"></Input>
                </FormItem>
            </Col>
             <Col span="11" offset="2">
                <FormItem :label="$t('分组')" prop="group">
                    <Input v-model.trim="formItem.group" :placeholder="$t('分组')"></Input>
                </FormItem>
            </Col>
        </Row>
        
        <Row>
            <Col span="7" >
                <FormItem :label="$t('外网地址')" prop="outerHost">
                    <Input v-model.trim="formItem.outerHost" :placeholder="$t('IP地址或者域名')"></Input>
                </FormItem>
            </Col>

            <Col span="7" offset="1">
                <FormItem :label="$t('内网地址')" prop="innerHost">
                    <Input v-model.trim="formItem.innerHost" :placeholder="$t('IP地址或者域名')"></Input>
                </FormItem>
            </Col>
           
            <Col offset="2" span="7">
                <FormItem :label="$t('端口')" prop="port">
                    <InputNumber v-model.trim="formItem.port" :placeholder="$t('登录端口')"></InputNumber>
                </FormItem>
            </Col>
        </Row>
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

         <FormItem :label="$t('登录方式')">
             <DataDictRadio v-model="formItem.loginType"  type="Machine.loginType"></DataDictRadio>
        </FormItem>

        <Row>
            <Col span="11">
                <FormItem :label="$t('用户名')" prop="userName">
                    <Input v-model.trim="formItem.userName" placeholder=""></Input>
                </FormItem>
            </Col>
          
            <Col offset="2" span="11">
                <FormItem v-if="formItem.loginType!=2" :label="$t('密码')"  prop="password">
                    <Input type="password" v-model.trim="formItem.password" placeholder=""></Input>
                </FormItem>
                
                <FormItem v-if="formItem.loginType==2" :label="$t('SSH公钥')">
                    <Button type="default">{{$t('生成公钥')}}</Button>
                </FormItem>
            </Col>
            
        </Row>

        <Row v-if="formItem.loginType==2" style="background-color: #F7F7F7;border:1px solid #DEDEDE;padding:10px;margin-bottom:15px">
            <Col span="24">
                 <div class="public-key-label">{{publicKey}}</div>
                <div style="color:#666;font-size:12px;margin-top:10px;margin-bottom:10px">
                    {{$t('将公钥内容添加到文件的末尾',['~/.ssh/authorized_keys'])}}
                    <Button  v-clipboard:copy="publicKey" v-clipboard:success="copySuccess" type="default" size="small">{{$t('复制')}}</Button>
                </div>
            </Col>
        </Row>

        <FormItem label="" v-if="formItem.id>0">
            <Button @click="deleteItem" type="error" size="large" >{{$t('删除')}}</Button>
        </FormItem> 
        
    </Form>

    
    <div slot="footer">
        <Row>
            <Col span="12" style="text-align:left;padding-top:5px"> <Checkbox v-model="continueCreate" v-if="formItem.id==0"  size="large">{{$t('继续创建下一个')}}</Checkbox></Col>
            <Col span="12" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{formItem.id>0?'保存':'创建'}}</Button></Col>
        </Row>
           
    </div>

    </Modal>
</template>


<script>
    export default {
        mixins: [componentMixin],
        data () {
            return {
                roleList:[],
                continueCreate:false,
                formItem:{
                    id:0,
                    loginType:1,
                    port:22,
                    privateKey:null,
                    publicKey:null,
                },
                propList:[],
                formRule:{
                    name:[vd.req,vd.name],
                    innerHost:[vd.req,vd.name2_100],
                    outerHost:[vd.req,vd.name2_100],
                    port:[vd.req,vd.port],
                    remark:[vd.desc],
                    userName:[vd.req,vd.name],
                    password:[vd.req,vd.name],
                    group:[vd.req]
                },
                publicKey:null,
            }
        },
        watch:{
            "formItem.loginType":function(val){
                if(this.formItem.id==0){
                    if(val==1||val==2){
                        this.formItem.port=22;
                    }
                    if(val==3){
                        this.formItem.port=5901;
                    }
                }
                if(val==2&&this.formItem.publicKey==null){
                    this.genKey();
                }
            }
        },
        methods: {
            pageLoad(){
                if(this.args.id){
                    this.loadData(this.args.id);
                }
            },
            loadData(id){
                 app.invoke('BizAction.getCmdbMachineById',[app.token,id],(info)=>{
                     for(var k in info.properties){
                         this.propList.push({
                             name:k,
                             value:info.properties[k]
                         })
                     }
                    this.formItem=info;
                })
            },
            genKey(){
                app.invoke('BizAction.genKeyPair',[app.token],(info)=>{
                    this.formItem.privateKey=info.privateKeyString;
                    this.formItem.publicKey=info.publicKeyString;
                    this.publicKey=info.publicKeyString;
                })
            },
            copySuccess(){
                app.toast(this.$t('复制成功'));
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
            deleteItem(){
                app.confirm(this.$t('确认要删除主机吗',[this.formItem.name]),()=>{
                    app.invoke('BizAction.deleteCmdbMachine',[app.token,this.formItem.id],(info)=>{
                        app.toast(this.$t('删除成功'))
                        app.postMessage('cmdbmachine.edit')
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
                var action=this.formItem.id>0?'BizAction.updateCmdbMachine':'BizAction.addCmdbMachine';
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.toast(this.formItem.name+' '+this.$t(this.formItem.id>0?'保存成功':'创建成功'));
                    app.postMessage('cmdbmachine.edit')
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