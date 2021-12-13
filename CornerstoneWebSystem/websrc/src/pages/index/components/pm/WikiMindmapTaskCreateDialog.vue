<style scoped>
    .other-field{
        border-top:1px solid #eee;
        padding-top:20px;
    }
    .other-field-item{
        display: inline-block;
        width:314px;
        padding-right:30px;
    }
</style>

<i18n>
    {
    "en": {
        "创建": "Create",
        "责任人": "Owner",
        "待认领": "none",
        "状态": "Status",
        "优先级": "Priority",
        "分类": "Category",
        "项目": "Project",
        "迭代": "Iteration",
        "起始时间": "Start",
        "未设置": "none",
        "截止时间": "End",
        "关联子系统": "System",
        "关联Release": "Release",
        "预计工时": "Expect time",
        "创建": "Create",
        "创建成功": "Success",
        "子节点": "Only generate task for leaf nodes"
    },
    "zh_CN": {
        "创建": "创建",
        "责任人": "责任人",
        "待认领": "待认领",
        "状态": "状态",
        "优先级": "优先级",
        "分类": "分类",
        "项目": "项目",
        "迭代": "迭代",
        "起始时间": "起始时间",
        "未设置": "未设置",
        "截止时间": "截止时间",
        "关联子系统": "关联子系统",
        "关联Release": "关联Release",
        "预计工时": "预计工时",
        "创建": "创建",
        "创建成功": "创建成功",
        "子节点": "仅生成子节点任务"
    }
    }
</i18n>

 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('创建')+typeName" width="700">

    <Form  ref="form" :rules="formRule" :model="formItem" label-position="top" style="height:600px;padding:15px">
        <FormItem :label="$t('子节点')">
            <i-switch v-model="onlyChildrenNode"  />
        </FormItem>
        <Row>
            <Col span="6" v-if="hasField('ownerAccountName')">
                <FormItem :label="$t('责任人')">
                    <MemberSelect :placeholder="$t('待认领')" :multiple="true" :member-list="editInfo.memberList" v-model="formItem.ownerAccountIdList" placement="bottom-start"></MemberSelect>
                </FormItem>
            </Col>
            <Col span="6" v-if="hasField('statusName')">
                <FormItem :label="$t('状态')">
                    <TaskStatusSelect :status-list="editInfo.statusList" v-model="formItem.status" placement="bottom-start"></TaskStatusSelect>
                </FormItem>
            </Col>
            <Col span="6" v-if="hasField('priorityName')">
               <FormItem :label="$t('优先级')">
                    <TaskPrioritySelect :priority-list="editInfo.priorityList" v-model="formItem.priority" placement="bottom-start"></TaskPrioritySelect>
                </FormItem>
            </Col>

             <Col span="6">
                <FormItem :label="$t('分类')">
                    <CategorySelect :category-list="editInfo.categoryNodeList" v-model="formItem.categoryIdList" placement="bottom"></CategorySelect>
                </FormItem>
            </Col>

        </Row>
        <Row class="other-field">
             <Col span="12">
               <FormItem  :label="$t('项目')">
                    <template v-if="editInfo.project">{{editInfo.project.name}}</template>
            </FormItem>
            </Col>

             <Col span="12" v-if="hasField('iterationName')">
                <FormItem  :label="$t('迭代')">
                       <TaskObjectSelect :object-list="editInfo.iterationList" v-model="formItem.iterationId" placement="bottom-start"></TaskObjectSelect>
                </FormItem>
            </Col>

        </Row>

        <div class="other-field">
             <div class="other-field-item"  v-for="item in editInfo.fieldList" :key="item.id"
                v-if="isSystemOtherField(item)">
                <template v-if="item.isSystemField">

                    <FormItem v-if="item.field=='startDate'" :label="$t('起始时间')">
                        <DateSelect type="date" v-model="formItem.startDate" :placeholder="$t('未设置')" placement="bottom-start" ></DateSelect>
                    </FormItem>

                    <FormItem v-if="item.field=='endDate'" :label="$t('截止时间')">
                        <DateSelect type="date" v-model="formItem.endDate" :placeholder="$t('未设置')" placement="bottom-start" ></DateSelect>
                    </FormItem>

                    <FormItem v-if="item.field=='subSystemName'" :label="$t('关联子系统')">
                        <TaskObjectSelect :object-list="editInfo.subSystemList" v-model="formItem.subSystemId" placement="bottom-start"></TaskObjectSelect>
                    </FormItem>

                    <FormItem v-if="item.field=='releaseName'" :label="$t('关联Release')">
                        <TaskObjectSelect :object-list="editInfo.releaseList" v-model="formItem.releaseId" placement="bottom-start"></TaskObjectSelect>
                    </FormItem>

                    <FormItem v-if="item.field=='expectWorkTime'" :label="$t('预计工时')">
                        <input type="number" v-model.number="formItem.expectWorkTime" ></input>
                    </FormItem>

                </template>
                 <template v-if="item.isSystemField==false">

                     <FormItem  :label="item.name" style="padding-right:20px" :prop="'customFields.field_'+item.id">
                        <Input type="textarea" autoresize :maxlength="500" v-if="item.type==1" v-model.trim="formItem.customFields['field_'+item.id]" :placeholder="item.remark"></Input>
                        <input type="number" style="width:100%" v-if="item.type==8" v-model.number="formItem.customFields['field_'+item.id]" :placeholder="item.remark"></input>
                        <DateSelect type="date" v-if="item.type==7" v-model="formItem.customFields['field_'+item.id]" :placeholder="$t('未设置')"></DateSelect>
                        <TaskCustomSelect :multiple="true" v-if="item.type==3" v-model="formItem.customFields['field_'+item.id]" :object-list="item.valueRange" placement="bottom-start"></TaskCustomSelect>
                        <TaskCustomSelect :multiple="false" v-if="item.type==4" v-model="formItem.customFields['field_'+item.id]" :object-list="item.valueRange" placement="bottom-start"></TaskCustomSelect>

                        <MemberSelect :placeholder="$t('未设置')" v-if="item.type==6"
                            :member-list="editInfo.memberList"
                            :multiple="true"
                            v-model="formItem.customFields['field_'+item.id]"
                            placement="bottom-start"></MemberSelect>
                    </FormItem>

                 </template>

            </div>
        </div>

    </Form>


    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{$t('创建')}}</Button></Col>
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
                editInfo:{},
                formItem:{
                    ownerAccountIdList:[],
                    status:null,
                    priority:null,
                    categoryIdList:[],
                    customFields:{},
                    startDate:new Date(),
                },
                formRule:{
                    name:[vd.req,vd.name],
                },
                onlyChildrenNode:true
            }
        },
        computed:{
            typeName:function(){
               return app.dataDictValue('Task.objectType',this.args.type);
            }
        },
        methods: {

            pageLoad(){
                this.formItem.projectId=this.args.projectId;
                this.formItem.iterationId=this.args.iterationId;
                this.formItem.objectType=this.args.type;
                this.formItem.parentId=this.args.parentId;
                this.formItem.status=this.args.status;
                if(this.args.startDate){
                    this.formItem.startDate=this.args.startDate;
                }
                if(this.args.endDate){
                    this.formItem.endDate=this.args.endDate;
                }
                this.loadEditInfo();
            },
            pageMessage(type){
                if(type=='category.edit'){
                    this.loadEditInfo();
                }
            },
            hasField(name){
                if(this.editInfo.fieldList){
                    for(var i=0;i<this.editInfo.fieldList.length;i++){
                        var f=this.editInfo.fieldList[i];
                        if(f.field==name){
                            return true;
                        }
                    }
                }
                return false;
            },
            isSystemOtherField(item){
                var excludesList=[
                    "workTime",
                    "startDays",
                    "endDays",
                    "iterationName",
                    "categoryIdList",
                    "name",
                    "ownerAccountName",
                    "createAccountName",
                    "priorityName",
                    "statusName",
                    "createTime",
                    "updateTime",
                ]
                for(var i=0;i<excludesList.length;i++){
                    var t=excludesList[i];
                    if(t==item.field){
                        return false;
                    }
                }
                return true;
            },
            loadEditInfo(){
                app.invoke( "BizAction.getEditTaskInfo",[app.token,this.args.projectId,this.args.type],info => {
                    this.editInfo=info;
                    //
                    this.editInfo.priorityList.map(item=>{
                        if(item.isDefault){
                            this.formItem.priority=item.id;
                        }
                    })
                    this.editInfo.statusList.map(item=>{
                        if(item.type==1&&this.args.status==null){
                            this.formItem.status=item.id;
                        }
                    })
                    //
                    this.editInfo.fieldList.map(item=>{
                        if(item.isSystemField==false&&item.isRequired){
                            this.formRule['customFields.field_'+item.id]=[vd.req]
                        }
                    })
                    //
                    if(this.formItem.iterationId==null&&info.iterationList.length>0){
                        this.formItem.iterationId=info.iterationList[0].id;
                    }
                });
            },
            confirm(){
                this.confirmForm();
            },
            confirmForm(){
                app.invoke(this.args.wikiPageType == 5 ? 'BizAction.createSeniorWikiTask' : 'BizAction.createWikiTask',[app.token,this.formItem,this.args.wikiPageId,this.args.nodeId,this.onlyChildrenNode],()=>{
                    app.toast(this.$t('创建成功'))
                    app.postMessage('task.edit')
                    if(this.args.callback){
                        this.args.callback();
                    }
                    this.showDialog=false;
			    })
            }
        }
    }
</script>
