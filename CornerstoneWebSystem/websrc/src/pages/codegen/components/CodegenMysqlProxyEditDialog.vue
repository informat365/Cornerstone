<style scoped>
   .table-row{
       padding:8px;
       font-size:13px;
       font-weight: bold;
   }
</style>
<i18n>
{
	"en": {
        "编辑MySQL代理": "编辑MySQL代理",
		"名称": "名称",
		"代理端口": "代理端口",
		"本地监听地址": "本地监听地址",
		"主机": "主机",
		"数据库主机地址": "数据库主机地址",
		"端口": "端口",
		"数据库端口": "数据库端口",
		"用户名": "用户名",
		"数据库用户名": "数据库用户名",
		"密码": "密码",
		"数据库密码": "数据库密码",
		"以下角色可以登录": "以下角色可以登录",
		"以下成员可以登录": "以下成员可以登录",
		"输入成员登录用户名": "输入成员登录用户名，多个成员用,隔开",
		"确定": "确定",
		"操作成功": "操作成功"
    },
	"zh_CN": {
		"编辑MySQL代理": "编辑MySQL代理",
		"名称": "名称",
		"代理端口": "代理端口",
		"本地监听地址": "本地监听地址",
		"主机": "主机",
		"数据库主机地址": "数据库主机地址",
		"端口": "端口",
		"数据库端口": "数据库端口",
		"用户名": "用户名",
		"数据库用户名": "数据库用户名",
		"密码": "密码",
		"数据库密码": "数据库密码",
		"以下角色可以登录": "以下角色可以登录",
		"以下成员可以登录": "以下成员可以登录",
		"输入成员登录用户名": "输入成员登录用户名，多个成员用,隔开",
		"确定": "确定",
		"操作成功": "操作成功"
	}
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" title="编辑MySQL代理" width="600" >
    <div>
                    
               <Form ref="form" label-position="right" :rules="formRule" :model="formItem" :label-width="120">
                    <FormItem :label="$t('名称')" prop="name">
                        <Input v-model.trim="formItem.name"></Input>
                    </FormItem>
                    <FormItem :label="$t('代理端口')" prop="proxyPort">
                        <Input v-model.trim="formItem.proxyPort" :placeholder="$t('本地监听地址')"></Input>
                    </FormItem>
                    <FormItem :label="$t('主机')" prop="host">
                        <Input v-model.trim="formItem.host" :placeholder="$t('数据库主机地址')"></Input>
                    </FormItem>
                    <FormItem :label="$t('端口')" prop="port">
                        <Input v-model.trim="formItem.port" :placeholder="$t('数据库端口')"></Input>
                    </FormItem>
                    <FormItem :label="$t('用户名')" prop="dbUser">
                        <Input v-model.trim="formItem.dbUser" :placeholder="$t('数据库用户名')"></Input>
                    </FormItem>
                    <FormItem :label="$t('密码')" prop="dbPassword">
                        <Input v-model.trim="formItem.dbPassword" type="password" :placeholder="$t('数据库密码')"></Input>
                    </FormItem>
                    <FormItem :label="$t('以下角色可以登录')">
                         <CheckboxGroup v-model="formItem.roles">
                            <Checkbox v-for="role in roleList" :key="'role'+role.id" :label="role.id">{{role.name}}</Checkbox>
                        </CheckboxGroup>
                    </FormItem>
                     <FormItem :label="$t('以下成员可以登录')" prop="members">
                        <Input type="textarea" :rows="3" v-model="formItem.members" :placeholder="$t('输入成员登录用户名')"></Input>
                    </FormItem>
                </Form>      

    </div>
    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{$t('确定')}}</Button></Col>
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
                dbPassword:null,
                name: null,
                dbUser:null,
                host:null,
                port:3306,
                proxyPort:4000,
                roles:[],
                members:null,
            },
            formRule:{
                name:[vd.req,vd.name],
                dbUser:[vd.req],
                dbPassword:[vd.req],
                proxyPort:[vd.req],
                host:[vd.req],
                port:[vd.req],
                members:[vd.name2_500]
            },
            roleList:[],
        }
    },
    methods: {
        pageLoad(){
            if(this.args.id){
                app.invoke('DesignerAction.getDesignerMysqlProxyInstanceById',[app.token,this.args.id],(obj)=>{
                    this.formItem=obj;  
                });
            }
            this.getRoleList();
        },
        getRoleList(){
            app.invoke('BizAction.getRoleInfoList',[app.token,2],(list)=>{
                this.roleList=list;
            })
        },
        confirm:function(){
            this.$refs["form"].validate((r)=>{
                if(r){ this.confirmEdit();}
            });
        },
        confirmEdit:function(){
             app.invoke(this.formItem.id?"DesignerAction.updateDesignerMysqlProxyInstance":"DesignerAction.addDesignerMysqlProxyInstance",
                [app.token,this.formItem],()=>{
                app.toast(this.$t("操作成功"));
                app.postMessage('dbproxy-edit');
                this.showDialog=false;
            });
           
        },
    }
}
</script>