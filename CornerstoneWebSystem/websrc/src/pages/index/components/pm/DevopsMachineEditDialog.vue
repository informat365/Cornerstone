<style scoped>
.public-key-label{
    word-wrap: break-word;
}
</style>
<i18n>
{
    "en": {
        "主机管理":"Machine",
        "名称":"Name",
        "分组":"Group",
        "分组名称":"group name",
        "主机名称":"machine name",
        "IP地址或者域名":"IP or domain name",
        "地址":"Address",
        "端口":"Port",
        "只读模式":"Readonly",
        "执行命令":"Execute command",
        "连接成功后自动执行命令":"Execute command after connected",
        "备注":"Remark",
        "备注信息":"remark",
        "登录方式":"Login type",
        "用户名":"Username",
        "密码":"Password",
        "SSH公钥":"SSH public key",
        "重新生成公钥":"Generate public key",
        "将公钥内容添加到":"Add the public key content to the end of the ~/.ssh/authorized_keys file",
        "复制":"Copy",
        "访问权限":"Access permission",
        "所有项目成员都可以登录主机":"All project members can log in to the machine",
        "只有以上成员角色才能登录主机":"Only the above member roles can log in to the machine",
        "删除":"Delete",
        "复制到其它项目":"Copy to other projects",
        "继续创建下一个":"Continue to create next",
        "保存":"Save",
        "创建":"Create",
        "复制成功":"Success",
        "执行完成":"Success",
        "保存成功":"Success",
        "创建成功":"Success",
        "确认要删除主机":"Are you sure you want to delete the machine [{0}]?",
        "CMDB复制":"CMDB copy"
    },
    "zh_CN": {
        "主机管理":"主机管理",
        "名称":"名称",
        "分组":"分组",
        "分组名称":"分组名称",
        "主机名称":"主机名称",
        "IP地址或者域名":"IP地址或者域名",
        "地址":"地址",
        "端口":"端口",
        "只读模式":"只读模式",
        "执行命令":"执行命令",
        "连接成功后自动执行命令":"连接成功后自动执行命令",
        "备注":"备注",
        "备注信息":"备注信息",
        "登录方式":"登录方式",
        "用户名":"用户名",
        "密码":"密码",
        "SSH公钥":"SSH公钥",
        "重新生成公钥":"重新生成公钥",
        "将公钥内容添加到":"将公钥内容添加到 ~/.ssh/authorized_keys 文件的末尾",
        "复制":"复制",
        "访问权限":"访问权限",
        "所有项目成员都可以登录主机":"所有项目成员都可以登录主机",
        "只有以上成员角色才能登录主机":"只有以上成员角色才能登录主机",
        "删除":"删除",
        "复制到其它项目":"复制到其它项目",
        "继续创建下一个":"继续创建下一个",
        "保存":"保存",
        "创建":"创建",
        "复制成功":"复制成功",
        "执行完成":"执行完成",
        "保存成功":"保存成功",
        "创建成功":"创建成功",
        "确认要删除主机":"确认要删除主机【{0}】吗？",
        "CMDB复制":"CMDB复制"
    }
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('主机管理')+(formItem.cmdbMachineId>0?'-'+$t('CMDB复制'):'')" width="700"  @on-ok="confirm">

    <Form ref="form" :rules="formRule" :model="formItem" label-position="top" style="height:600px;padding:15px">
         <Row>
             <Col span="18">
                <FormItem  :label="$t('名称')" prop="name">
                    <Input :disabled="formItem.cmdbMachineId>0" v-model.trim="formItem.name" :placeholder="$t('主机名称')">
                    </Input>
                </FormItem>

             </Col>
             <Col span="6" style="padding-left:10px">
                <FormItem  :label="$t('分组')" prop="group">
                    <Input v-model.trim="formItem.group" :placeholder="$t('分组名称')">
                    </Input>
                </FormItem>
             </Col>
         </Row>

        <Row>
            <Col span="11" >
                <FormItem :label="$t('地址')" prop="host">
                    <Input :disabled="formItem.cmdbMachineId>0" v-model.trim="formItem.host" :placeholder="$t('IP地址或者域名')"></Input>
                </FormItem>
            </Col>

            <Col offset="2" span="5">
                <FormItem :label="$t('端口')" prop="port">
                    <InputNumber :disabled="formItem.cmdbMachineId>0" v-model.trim="formItem.port" :placeholder="$t('登录端口')"></InputNumber>
                </FormItem>
            </Col>
            <Col offset="1" span="5">
                <FormItem :label="$t('只读模式')">
                    <i-Switch v-model="formItem.readonlyMode"></i-Switch>
                </FormItem>
            </Col>
        </Row>
         <FormItem v-if="formItem.loginType==1||formItem.loginType==2" :label="$t('执行命令')" prop="cmd">
            <Input type="textarea"  :rows="1" :placeholder="$t('连接成功后自动执行命令')"  v-model.trim="formItem.cmd"></Input>
        </FormItem>
         <FormItem :label="$t('备注')" prop="remark">
            <Input type="textarea" :placeholder="$t('备注信息')"  v-model.trim="formItem.remark"></Input>
        </FormItem>

         <FormItem :label="$t('登录方式')">
             <DataDictRadio :disabled="formItem.cmdbMachineId>0" v-model="formItem.loginType"  type="Machine.loginType"></DataDictRadio>
        </FormItem>

        <Row>
            <Col span="11">
                <FormItem :label="$t('用户名')" prop="userName">
                    <input
                        name="username"
                        tabindex="-1"
                        style="position: fixed;left: -1000px;top: -1000px;width: 10px;"
                        type="password" />
                    <Input :disabled="formItem.cmdbMachineId>0" v-model.trim="formItem.userName" placeholder=""></Input>
                </FormItem>
            </Col>

            <Col offset="2" span="11">
                <input
                    name="password"
                    tabindex="-1"
                    style="position: fixed;left: -1000px;top: -1000px;width: 10px;"
                    type="password" />
                <FormItem v-if="formItem.loginType!=2" :label="$t('密码')"  prop="password">
                    <Input :disabled="formItem.cmdbMachineId>0" type="password" v-model.trim="formItem.password" placeholder=""></Input>
                </FormItem>

                <FormItem v-if="formItem.loginType==2" :label="$t('SSH公钥')">
                    <Button @click="genKey" type="default">{{$t('重新生成公钥')}}</Button>
                </FormItem>
            </Col>

        </Row>

        <Row v-if="formItem.loginType==2" style="background-color: #F7F7F7;border:1px solid #DEDEDE;padding:10px;margin-bottom:15px">
            <Col span="24">
                <div class="public-key-label">{{publicKey}}</div>
                <div style="color:#666;font-size:12px;margin-top:10px;margin-bottom:10px">
                    {{$t('将公钥内容添加到')}}
                    <Button  v-clipboard:copy="publicKey" v-clipboard:success="copySuccess" type="default" size="small">{{$t('复制')}}</Button>
                </div>
            </Col>
        </Row>
        <FormItem :label="$t('访问权限')">
            <i-Switch v-model="formItem.enableRole"></i-Switch>
            <span v-if="formItem.enableRole==false" style="margin-left:10px">{{$t('所有项目成员都可以登录主机')}} </span>
        </FormItem>

        <FormItem v-if="formItem.enableRole">
            <CheckboxGroup v-model="formItem.roles" >
                    <Checkbox v-for="item in roleList" :key="item.id" :label="item.id">{{item.name}}</Checkbox>
            </CheckboxGroup>
            <div style="color:#666;font-size:12px">{{$t('只有以上成员角色才能登录主机')}} </div>
        </FormItem>

         <FormItem label="" v-if="formItem.id>0">
            <Button @click="deleteItem" type="error" size="large" >{{$t('删除')}}</Button>
            <Button style="margin-left:10px" @click="copyItem" type="default" size="large" >{{$t('复制到其它项目')}}</Button>
        </FormItem>

    </Form>


    <div slot="footer">
        <Row>
            <Col span="12" style="text-align:left;padding-top:5px"> <Checkbox v-model="continueCreate" v-if="formItem.id==0"  size="large">{{$t('继续创建下一个')}}</Checkbox></Col>
            <Col span="12" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{formItem.id>0?$t('保存'):$t('创建')}}</Button></Col>
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
                    group:null,
                    projectId:null,
                    publicKey:null,
                    password:null,
                    roles:[],
                    enableRole:false,
                },
                formRule:{
                    name:[vd.req,vd.name],
                    group:[vd.name],
                    host:[vd.req,vd.name2_100],
                    port:[vd.req,vd.port],
                    remark:[vd.desc],
                    cmd:[vd.desc],
                    userName:[vd.req,vd.name],
                    password:[vd.req,vd.name],
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
                 this.formItem.projectId=this.args.projectId;
                 this.loadRole();
                 if(this.args.id){
                    this.loadData(this.args.id);
                 }
            },
            loadData(id){
                app.invoke('BizAction.getMachineById',[app.token,id],(info)=>{
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
            loadRole(){
                app.invoke('BizAction.getRoleInfoList',[app.token,1],(list)=>{
                    this.roleList=list;
                })
            },
            deleteItem(){
                app.confirm(this.$t('确认要删除主机',[this.formItem.name]),()=>{
                    app.invoke('BizAction.deleteMachine',[app.token,this.formItem.id],(info)=>{
                        app.postMessage('machine.edit')
                        this.showDialog=false;
                    })
                })
            },
            copyItem(){
                app.showDialog(CopySelectMyProjectDialog,{
                    callback:this.confirmCopy
                })
            },
            confirmCopy(projectList){
                 app.showDialog(MultiOperateDialog,{
                            title:"复制主机",
                            runCallback:this.copyAction,
                            finishCallback:this.finishCopyRun,
                            itemList:projectList,
                })
            },
            finishCopyRun(){
                app.postMessage('machine.edit');
                app.toast('执行完成');
            },
            copyAction(item,success,error){
                this.formItem.projectId=item;
                this.formItem.id=-1;
                app.invoke('BizAction.createMachine',[app.token,this.formItem],(info)=>{
                    success();
                },error)
            },
            confirm(){
                this.$refs.form.validate((r)=>{
                    if(r){this.confirmForm()}
                });
            },
            confirmForm(){
                var action=this.formItem.id>0?'BizAction.updateMachine':'BizAction.createMachine';
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.toast(this.formItem.name+" "+(this.formItem.id>0? this.$t('保存成功'):this.$t('创建成功')))
                    app.postMessage('machine.edit')
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
