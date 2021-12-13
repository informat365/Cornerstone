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
        "编辑数据库": "编辑数据库",
		"名称": "名称",
		"数据库ID": "数据库ID",
		"数据库类型": "数据库类型",
		"主机": "主机",
		"端口": "端口",
		"用户名": "用户名",
		"密码": "密码",
		"包名": "包名",
		"DML表": "DML表",
		"以下角色可以使用": "以下角色可以使用",
		"以下成员可以使用": "以下成员可以使用",
		"输入成员登录用户名": "输入成员登录用户名，多个成员用,隔开",
		"确定": "确定",
		"操作成功": "操作成功"
    },
	"zh_CN": {
		"编辑数据库": "编辑数据库",
		"名称": "名称",
		"数据库ID": "数据库ID",
		"数据库类型": "数据库类型",
		"主机": "主机",
		"端口": "端口",
		"用户名": "用户名",
		"密码": "密码",
		"包名": "包名",
		"DML表": "DML表",
		"以下角色可以使用": "以下角色可以使用",
		"以下成员可以使用": "以下成员可以使用",
		"输入成员登录用户名": "输入成员登录用户名，多个成员用,隔开",
		"确定": "确定",
		"操作成功": "操作成功"
	}
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('编辑数据库')" width="600" >
    <div>
                    
               <Form ref="form" label-position="right" :rules="formRule" :model="formItem" :label-width="120">
                    <FormItem :label="$t('名称')" prop="name">
                        <Input v-model.trim="formItem.name"></Input>
                    </FormItem>
                    <FormItem :label="$t('数据库ID')" prop="instanceId">
                        <Input v-model.trim="formItem.instanceId"></Input>
                    </FormItem>
                    <FormItem :label="$t('数据库类型')" prop="dbType">
                        <DataDictRadio v-model="formItem.dbType" 
                         type="DesignerDatabase.dbType"></DataDictRadio>
                    </FormItem>
                    <FormItem :label="$t('主机')" prop="host">
                        <Input v-model.trim="formItem.host"></Input>
                    </FormItem>
                    <FormItem :label="$t('端口')" prop="port">
                        <Input v-model.trim="formItem.port"></Input>
                    </FormItem>
                    <FormItem :label="$t('用户名')" prop="dbUser">
                        <Input v-model.trim="formItem.dbUser"></Input>
                    </FormItem>
                    <FormItem :label="$t('密码')" prop="dbPassword">
                        <Input type="password" v-model.trim="formItem.dbPassword"></Input>
                    </FormItem>

                    <FormItem :label="$t('包名')" prop="packageName">
                            <Input v-model.trim="formItem.packageName"></Input>
                    </FormItem>
                    <FormItem :label="$t('DML表')" prop="dmlTables">
                            <Input type="textarea" :rows="5" v-model.trim="formItem.dmlTables"></Input>
                    </FormItem>

                     <FormItem :label="$t('以下角色可以使用')">
                         <CheckboxGroup v-model="formItem.roles">
                            <Checkbox v-for="role in roleList" :key="'role'+role.id" :label="role.id">{{role.name}}</Checkbox>
                        </CheckboxGroup>
                    </FormItem>
                     <FormItem :label="$t('以下成员可以使用')" prop="members">
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
                dbType:null,
                instanceId:null,
                host:null,
                port:3306,
                packageName:null,
                roles:[],
                members:null,
            },
            formRule:{
                name:[vd.req,vd.name],
                dbUser:[vd.req],
                dbPassword:[vd.req],
                dbType:[vd.req],
                instanceId:[vd.req],
                host:[vd.req],
                port:[vd.req],
                packageName:[vd.req],
            },
            roleList:[],
        }
    },
    methods: {
        pageLoad(){
            this.getRoleList();
            if(this.args.id){
                app.invoke('DesignerAction.getDesignerDatabaseById',[app.token,this.args.id],(obj)=>{
                    this.formItem=obj;  
                });
            }
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
             app.invoke(this.formItem.id?"DesignerAction.updateDesignerDatabase":"DesignerAction.addDesignerDatabase",
                [app.token,this.formItem],()=>{
                app.toast(this.$t("操作成功"));
                app.postMessage('db-edit');
                this.showDialog=false;
            });
           
        },
    }
}
</script>