<style scoped>
</style>
<i18n>
{
    "en": {
        "子系统管理": "System",
        "子系统名称":"Name",
        "名称":"Name",
        "状态":"Status",
        "详细描述":"Remark",
        "删除":"Delete",
        "继续创建下一个":"Continue to create next",
        "保存":"Save",
        "创建":"Create",
        "确认要删除子系统":"Are you sure you want to delete the system {0}?",
        "保存成功":"Success",
        "创建成功":"Success"
    },
    "zh_CN": {
        "子系统管理": "子系统管理",
        "子系统名称":"子系统名称",
        "名称":"名称",
        "状态":"状态",
        "详细描述":"详细描述",
        "删除":"删除",
        "继续创建下一个":"继续创建下一个",
        "保存":"保存",
        "创建":"创建",
        "确认要删除子系统":"确认要删除子系统【{0}】吗？",
        "保存成功":"保存成功",
        "创建成功":"创建成功"
    }
}
</i18n>  
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('子系统管理')" width="700" >
 
    <Form  ref="form" :rules="formRule" :model="formItem"  label-position="top" style="height:550px;padding:15px;">
         <FormItem :label="$t('名称')" prop="name">
            <Input :disabled="projectDisabled" v-model.trim="formItem.name" :placeholder="$t('子系统名称')"></Input>
        </FormItem>
       

        <FormItem :label="$t('状态')">
             <DataDictRadio :disabled="projectDisabled" v-model="formItem.status" type="ProjectSubSystem.status"/>
                
        </FormItem>

         <FormItem :label="$t('详细描述')" prop="description">
            <Input  :disabled="projectDisabled"  v-model.trim="formItem.description" type="textarea" :rows="3" ></Input>
        </FormItem>
        
        <FormItem label="" v-if="formItem.id>0">
            <Button :disabled="projectDisabled" @click="deleteItem" type="error" size="large" >{{$t('删除')}}</Button>
        </FormItem> 
    </Form>

    
    <div slot="footer">
        <Row v-if="!projectDisabled">
            <Col span="12" style="text-align:left;padding-top:5px"> <Checkbox v-if="formItem.id==0" v-model="continueCreate" size="large">{{$t('继续创建下一个')}}</Checkbox></Col>
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
                continueCreate:false,
                formItem:{
                    id:0,
                    projectId:0,
                    name:null,
                    description:null,
                    status:1,
                },
                formRule:{
                    name:[vd.req,vd.name],
                    description:[vd.desc],
                },
                
            }
        },
        methods: {
            pageLoad(){
                if(this.args.item){
                    this.formItem=copyObject(this.args.item);
                }
            },
            deleteItem(){
                app.confirm(this.$t('确认要删除子系统',[this.formItem.name]),()=>{
                    app.invoke('BizAction.deleteProjectSubSystem',[app.token,this.formItem.id],(info)=>{
                        app.postMessage('system.edit')
                        this.showDialog=false;
                    })
                })
            },
            confirm(){
                this.$refs.form.validate((r)=>{
                    if(r){this.confirmForm()}
                });
            },
            confirmForm(){
                //
                if(this.formItem.projectId==0){
                    this.formItem.projectId=this.args.projectId;
                }
                var action=this.formItem.id>0?'BizAction.updateProjectSubSystem':'BizAction.createProjectSubSystem';
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.toast(this.formItem.name+" "+(this.formItem.id>0? this.$t('保存成功'):this.$t('创建成功')))
                    app.postMessage('system.edit')
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