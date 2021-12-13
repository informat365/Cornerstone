<style scoped>
.desc-info{
        font-size:12px;
        color:#999;
        line-height: 1.5;
}
</style>
 <i18n>
{
    "en": {
        "状态设置": "Status Setting",
        "名称":"Name",
        "备注":"Remark",
        "类型":"Type",
        "开始状态只能有一个":"There can only be one start state, which is given by default when an alteration is created.",
        "结束状态表示对象流转已完成":"The end state indicates that the alteration flow has been completed and the alteration in the end state can no longer be edited.",
        "颜色":"Color",
        "只允许以下成员操作":"Only the following member operations are allowed",
        "当变更为此状态时将负责人设置为":"Change auditor to ",
        "检查字段已经设置":"Check field ",
        "删除此状态":"Delete",
        "继续创建下一个":"Continue to create next",
        "创建":"Create",
        "保存":"Save",
        "确定要删除状态":"Are you sure you want to delete the status {0}? If the data is in this state, please reset the state of the data first!",
        "包含特殊字符":"The name contains some special characters,please modify"
    },
    "zh_CN": {
        "状态设置": "状态设置",
        "名称":"名称",
        "备注":"备注",
        "类型":"类型",
        "只允许以下成员操作":"只允许以下成员操作",
        "开始状态只能有一个":"开始状态只能有一个，变更创建时会默认赋予这个状态",
        "失败结束状态":"失败状态表示变更流转已完成，本次变更不通过，结束状态的变更不能再进行编辑",
        "成功结束状态":"成功状态表示变更流转已完成，本次变更通过，结束状态的变更不能再进行编辑",
        "颜色":"颜色",
        "当变更为此状态时将负责人设置为":"当变更为此状态时将审批人设置为",
        "检查字段已经设置":"检查字段已经设置",
        "删除此状态":"删除此状态",
        "继续创建下一个":"继续创建下一个",
        "创建":"创建",
        "保存":"保存",
        "确定要删除状态":"确定要删除状态【{0}】吗？如果有数据处在这个状态，请先重新设置这些数据的状态!",
        "包含特殊字符":"名称包含特殊字符，请修改"
    }
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('状态设置')" width="700"  @on-ok="confirm">

    <Form @submit.native.prevent  ref="form" :rules="formRule" :model="formItem" label-position="top" style="height:500px;padding:25px">
        <Row>
            <Col span="11">
                <FormItem :label="$t('名称')" prop="name">
                    <Input :disabled="disabled" v-model.trim="formItem.name" placeholder="请勿包含特殊字符"></Input>
                </FormItem>
            </Col>
            <Col offset="2" span="11">
                 <FormItem prop="remark" :label="$t('备注')">
                    <Input  :disabled="disabled" v-model.trim="formItem.remark" placeholder=""></Input>
                </FormItem>
            </Col>
         </Row>

         <Row>
            <Col span="11">
                <FormItem :label="$t('类型')">
                    <RadioGroup :disabled="disabled" v-model="formItem.type">
                        <Radio    :label="1" >开始状态</Radio>
                        <Radio    :label="2" >进行状态</Radio>
                        <Radio    :label="4" >成功状态</Radio>
                        <Radio    :label="3" >失败状态</Radio>
                    </RadioGroup>
            <div v-if="formItem.type==1" class="desc-info">{{$t('开始状态只能有一个')}}</div>
            <div v-if="formItem.type==3" class="desc-info">{{$t('失败结束状态')}}</div>
            <div v-if="formItem.type==4" class="desc-info">{{$t('成功结束状态')}}</div>
        </FormItem>
            </Col>
            <Col offset="2" span="11">
                 <FormItem :label="$t('颜色')" prop="color">
                    <ColorPicker :disabled="disabled" :recommend="true" :colors="colorArray" v-model="formItem.color"/>
                </FormItem>
            </Col>
         </Row>

<!--        <FormItem :label="$t('只允许以下成员操作')">-->
<!--            <Select transfer v-if="changeOwnerListLoaded" v-model="formItem.permissionOwnerList" multiple clearable style="width:100%">-->
<!--                <Option v-for="item in changeOwnerList"  :value="item.id" :key="'m'+item.id">{{ item.name }}</Option>-->
<!--            </Select>-->
<!--        </FormItem>-->

        <FormItem :label="$t('当变更为此状态时将负责人设置为')">
            <Select :disabled="disabled" transfer v-if="changeOwnerListLoaded" v-model="formItem.setOwnerList" multiple clearable style="width:100%">
                <Option v-for="item in changeOwnerList"  :value="item.id" :key="'m'+item.id">{{ item.name }}</Option>
            </Select>
        </FormItem>

        <FormItem v-if="formItem.id>0&&!disabled"  label="">
           <Button @click="deleteField" type="error">{{$t('删除此状态')}}</Button>


        </FormItem>



    </Form>

    <div slot="footer">
        <Row v-if="!disabled">
            <Col span="12" style="text-align:left;padding-top:5px"> &nbsp;<Checkbox v-if="formItem.id==0" v-model="continueCreate" size="large">{{$t('继续创建下一个')}}</Checkbox></Col>
            <Col span="12" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{formItem.id==0?$t("创建"):$t("保存")}}</Button></Col>
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
                    type:2,
                    remark:null,
                    color:'#2E94B9',
                    setOwnerList:[],
                    permissionOwnerList:[],
                    checkFieldList:[],
                },
                formRule:{
                    name:[vd.req,vd.name2_10],
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
                changeOwnerListLoaded:false,
                changeOwnerList:[{
                    id:"creater",
                    name:"创建人"
                },{
                    id:"owner",
                    name:"责任人"
                },
                {
                    id:"lastOwner",
                    name:"上一个状态的责任人"
                },
                {
                    id:"firstOwner",
                    name:"最初的责任人"
                },
                {
                    id:"emptyOwner",
                    name:"空"
                }],
                fieldList:[],
                disabled:false
            }
        },
        methods: {
            pageLoad(){
                if(this.args.item){
                    this.formItem=this.args.item;
                    console.log(this.formItem.type)
                }else{
                    this.formItem.objectType=this.args.objectType;
                    this.formItem.projectId=this.args.project.id;
                }
                this.loadRoleList();
                // this.loadFieldList();
                this.disabled = this.args.project&&this.args.project.isFinish;
                console.log(this.formItem.setOwnerList)
            },
            loadRoleList(){
                app.invoke('BizAction.getRoleInfoList',[app.token,1],(list)=>{
                    for(var i=0;i<list.length;i++){
                        var item=list[i]
                        this.changeOwnerList.push({
                            id:"role_"+item.id,
                            name:"[角色]"+item.name,
                        })
                    }
                    this.loadMemberList();
                })
            },
            loadMemberList(){
                app.invoke('BizAction.getProjectMemberInfoList',[app.token,this.formItem.projectId],(list)=>{
                    for(var i=0;i<list.length;i++){
                        var item=list[i]
                        this.changeOwnerList.push({
                            id:"member_"+item.accountId,
                            name:"[成员]"+item.accountName,
                        })
                    }
                    this.changeOwnerListLoaded=true;
                })
            },
            deleteField(){
                app.confirm(this.$t('确定要删除流程节点',[this.formItem.name]),()=>{
                     app.invoke('TaskAlterationAction.deleteTaskAlterationDefine',[app.token,this.formItem.id],(info)=>{
                        app.toast(this.$t('操作成功'))
                        this.args.callback();
                        this.showDialog=false;
                    })
                })
            },
            confirm(){
                //名称特殊字符校验（某些情况下会导致流程图无法渲染）
                var pattern = new RegExp("[`\\-\\\\~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]");
                var rs = "";
                var s = this.formItem.name;
                for (var i = 0; i < s.length; i++) {
                    rs = rs + s.substr(i, 1).replace(pattern, '');
                }
                if(rs!==this.formItem.name){
                    app.toast(this.$t('包含特殊字符'));
                        return;
                }
                this.$refs.form.validate((r)=>{
                    if(r){
                        this.confirmForm()
                    }
                });
            },
            confirmForm(){
                var action=this.formItem.id==0?'TaskAlterationAction.addTaskAlterationDefine':"TaskAlterationAction.updateTaskAlterationDefine";
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
