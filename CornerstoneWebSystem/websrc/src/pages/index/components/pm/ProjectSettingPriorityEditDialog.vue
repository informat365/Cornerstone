<style scoped>
.desc-info{
        font-size:12px;
        color:#999;
}
</style>
<i18n>
{
    "en": {
        "优先级": "Priority",
        "名称":"Name",
        "备注":"Remark",
        "默认状态":"Default",
        "创建对象时的默认优先级":"Default priority",
        "删除此优先级":"Delete",
        "继续创建下一个":"Continue to create next",
        "创建":"Create",
        "保存":"Save",
        "确定要删除优先级":"Are you sure you want to delete priority [{0}]? If there is data in this priority, please reset the priority of these data first!"
    },
    "zh_CN": {
        "优先级": "优先级",
        "名称":"名称",
        "备注":"备注",
        "默认状态":"默认状态",
        "创建对象时的默认优先级":"创建对象时的默认优先级",
        "删除此优先级":"删除此优先级",
        "继续创建下一个":"继续创建下一个",
        "创建":"创建",
        "保存":"保存",
        "确定要删除优先级":"确定要删除优先级【{0}】吗？如果有数据处在这个优先级，请先重新设置这些数据的优先级!"
    }
}
</i18n> 
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('优先级')" width="700"  @on-ok="confirm">

    <Form ref="form" :rules="formRule" :model="formItem" label-position="top" style="height:500px;padding:25px">
        <Row>
            <Col span="11">
                <FormItem :label="$t('名称')" prop="name">
                    <Input :disabled="disabled" v-model.trim="formItem.name" placeholder=""></Input>
                </FormItem>
            </Col>
            <Col offset="2" span="11">
                 <FormItem prop="remark" :label="$t('备注')">
                    <Input  :disabled="disabled"  v-model.trim="formItem.remark" placeholder=""></Input>
                </FormItem>
            </Col>
         </Row>


        <Row>
            <Col span="11">
                <FormItem :label="$t('默认状态')">
                    <i-Switch :disabled="disabled" v-model="formItem.isDefault"></i-Switch>
                    <div class="desc-info">创建对象时的默认优先级</div>
            </FormItem>
            </Col>
            <Col offset="2" span="11">
                 <FormItem :label="$t('颜色')" prop="color">
                    <ColorPicker :disabled="disabled" :recommend="true" :colors="colorArray" v-model="formItem.color"/>
                </FormItem>
            </Col>
         </Row>

        <FormItem v-if="formItem.id>0&&!disabled"  label="">
           <Button @click="deleteField" type="error">{{$t('删除此优先级')}}</Button>
        </FormItem>
         


    </Form>
    
    <div slot="footer">
        <Row v-if="!disabled">
            <Col span="12" style="text-align:left;padding-top:5px"> <Checkbox v-model="continueCreate" size="large">{{$t('继续创建下一个')}}</Checkbox></Col>
            <Col span="12" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{formItem.id==0?$t("创建"):$t("保存")}}</Button></Col>
        </Row>
    </div>

    </Modal>
</template>


<script>
    export default {
        name:"ProjectSettingPriorityEditDialog",
        mixins: [componentMixin],
        data () {
            return {
                continueCreate:false,
                formItem:{
                    id:0,
                    name:null,
                    isDefault:false,
                    remark:null,
                    color:'#2E94B9',
                },
                formRule:{
                    name:[vd.req,vd.name1_10],
                    remark:[vd.desc],
                    color:[vd.req],
                },
                colorArray:[
                    "#2E94B9",
                    "#FFFDC0",
                    "#F0B775",
                    "#D25565",
                    "#F54EA2",
                    "#42218E",
                    "#5BE7C4",
                    "#525564",
                ],
                disabled:false
            }
        },
        methods: {
             pageLoad(){
                if(this.args.item){
                    this.formItem=this.args.item;
                }else{
                    this.formItem.objectType=this.args.objectType;
                    this.formItem.projectId=this.args.project.id;
                }
                this.disabled = this.args.project&&this.args.project.isFinish;
            },
            deleteField(){
                app.confirm(this.$t('确定要删除优先级',[this.formItem.name]),()=>{
                     app.invoke('BizAction.deleteProjectPriorityDefine',[app.token,this.formItem.id],(info)=>{
                        this.args.callback();
                        this.showDialog=false;
                    })
                })
            },
            confirm(){
                this.$refs.form.validate((r)=>{
                    if(r){
                        this.confirmForm();
                    }
                });
            },
            confirmForm(){
                var action=this.formItem.id==0?'BizAction.addProjectPriorityDefine':"BizAction.updateProjectPriorityDefine";
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    this.args.callback();
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