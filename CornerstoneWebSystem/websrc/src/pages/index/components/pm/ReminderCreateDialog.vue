<style scoped>
</style>
<i18n>
{
    "en": {
        "创建提醒": "Create Reminder",
        "事项":"Event",
        "输入提醒事项":"Event name",
        "备注":"Remark",
        "在以下日期提醒我":"Reminder date",
        "未设置":"none",
        "重复":"Repeat",
        "删除":"Delete",
        "继续创建下一个":"Continue to create next",
        "保存":"Save",
        "创建":"Create",
        "确认要删除提醒":"Are you sure you want to delete the reminder {0}?",
        "保存成功":"Success",
        "创建成功":"Success"
    },
    "zh_CN": {
        "创建提醒": "创建提醒",
        "事项":"事项",
        "输入提醒事项":"输入提醒事项",
        "备注":"备注",
        "在以下日期提醒我":"在以下日期提醒我",
        "未设置":"未设置",
        "重复":"重复",
        "删除":"删除",
        "继续创建下一个":"继续创建下一个",
        "保存":"保存",
        "创建":"创建",
        "确认要删除提醒":"确认要删除提醒【{0}】吗？",
        "保存成功":"保存成功",
        "创建成功":"创建成功"
    }
}
</i18n>  
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('创建提醒')" width="700"  @on-ok="confirm">

    <Form  @submit.native.prevent ref="form" :rules="formRule" :model="formItem"  label-position="top" style="height:600px;padding:25px">
        <FormItem :label="$t('事项')" prop="name">
            <Input v-model.trim="formItem.name" :placeholder="$t('输入提醒事项')"></Input>
        </FormItem>
         <FormItem :label="$t('备注')" prop="remark">
            <Input type="textarea" v-model.trim="formItem.remark" placeholder=""></Input>
        </FormItem>
      
        <FormItem :label="$t('在以下日期提醒我')" prop="remindTime">
             <ExDatePicker type="datetime" v-model="formItem.remindTime" :placeholder="$t('未设置')" ></ExDatePicker>
        </FormItem>

         <FormItem :label="$t('重复')" prop="repeat">
               <DataDictRadio type='Remind.repeat' v-model="formItem.repeat"></DataDictRadio>
        </FormItem>
         <FormItem label="" v-if="formItem.id>0">
            <Button @click="deleteItem" type="error" size="large" >{{$t('删除')}}</Button>
        </FormItem> 

    </Form>

    
    <div slot="footer">
        <Row>
            <Col span="12" style="text-align:left;padding-top:5px"> <Checkbox v-if="formItem.id==0" v-model="continueCreate" size="large">{{$t('继续创建下一个')}}</Checkbox></Col>
            <Col span="12" style="text-align:right"><Button @click="confirm" type="default" size="large" >{{formItem.id>0?$t('保存'):$t('创建')}}</Button></Col>
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
                    name:null,
                    remark:null,
                    remindTime:null,
                    repeat:1,
                },
                formRule:{
                    name:[vd.req,vd.name],
                    remark:[vd.desc],
                    remindTime:[vd.req]
                },
                
            }
        },
        methods: {
            pageLoad(){
               
                if(this.args.remindTime){
                    console.log('yyyy',this.args.remindTime,this.args.remindTime.getTime())
                    this.formItem.remindTime=this.args.remindTime.getTime();
                }
                if(this.args.id){
                    app.invoke('BizAction.getRemindInfoById',[app.token,this.args.id],(info)=>{
                       this.formItem=info;
                    })
                }
            },
            deleteItem(){
                app.confirm(this.$t('确认要删除提醒',[this.formItem.name]),()=>{
                    app.invoke('BizAction.deleteRemind',[app.token,this.formItem.id],(info)=>{
                        app.postMessage('remind.edit')
                        this.showDialog=false;
                    })
                })
            },
            confirm(){
                this.$refs.form.validate((r)=>{
                    if(r)this.confirmForm()
                });
            },
            confirmForm(){
                //
                if(this.formItem.projectId==0){
                    this.formItem.projectId=this.args.projectId;
                }
                var action=this.formItem.id>0?'BizAction.updateRemind':'BizAction.addRemind';
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.toast(this.formItem.name+" "+(this.formItem.id>0?this.$t('保存成功'):this.$t('创建成功')))
                    app.postMessage('remind.edit')
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