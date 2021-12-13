<style scoped>
    .report-content-row{
        padding:5px 0;
    }
    .report-content-title{
        font-size:15px;
        font-weight: bold;
    }
    .report-content-desc{
        font-size:13px;
        color:#999;
    }
    .line{
        border-top:1px solid #eee;
        margin-top:15px;
        margin-bottom:15px;
    }
    .rule-desc{
        margin-bottom:20px;
        color:#999;
    }
</style>

<i18n>
    {
    "en": {
    "此模板已禁用，将不会生成新的汇报":"This template is disabled and will not generate new reports",
    "汇报模板":"Report template",
    "模板名称":"Name",
    "汇报周期":"Period",
    "提醒时间":"Reminder time",
    "请选择":"Choose",
    "提醒日期":"Reminder Date",
    "日报将会在提醒日期的提醒时间生成":"The daily report will be generated at the reminder time of the reminder date.",
    "周报将会在每周五的提醒时间生成":"Weekly report will be generated every Friday reminder time",
    "月报将会在每月最后一天的提醒时间生成":"The monthly report will be generated on the reminder time of the last day of each month.",
    "选择项目":"Select project",
    "关联项目":"Associated project",
    "由谁提交":"Submitter",
    "由谁审核":"Reviewer",
    "汇报内容":"Report content",
    "新增成员":"New member",
    "编辑":"Edit",
    "删除":"Delete",
    "新增内容":"Add content",
    "删除此模板":"Delete template",
    "禁用此模板":"Disable template",
    "启用此模板":"Enable template",
    "继续创建下一个":"Continue to create the next ",
    "保存":"Save",
    "创建":"Create",
    "周一":"Monday",
    "周二":"Tuesday",
    "周三":"Wednesday",
    "周四":"Thursday",
    "周五":"Friday",
    "周六":"Saturday",
    "周日":"Sunday",
    "删除成功":"Delete success",
    "操作成功":"Success",
    "请选择提交人":"Select Submitter",
    "请选择审核人":"Select Reviewer",
    "请添加汇报内容":"Please add report content",
    "确认要删除吗？":"Are you sure you want to delete “{0}”?"
    },
    "zh_CN": {
    "此模板已禁用，将不会生成新的汇报":"此模板已禁用，将不会生成新的汇报",
    "汇报模板":"汇报模板",
    "模板名称":"模板名称",
    "汇报周期":"汇报周期",
    "提醒时间":"提醒时间",
    "请选择":"请选择",
    "提醒日期":"提醒日期",
    "日报将会在提醒日期的提醒时间生成":"日报将会在提醒日期的提醒时间生成",
    "周报将会在每周五的提醒时间生成":"周报将会在每周五的提醒时间生成",
    "月报将会在每月最后一天的提醒时间生成":"月报将会在每月最后一天的提醒时间生成",
    "选择项目":"选择项目",
    "关联项目":"关联项目",
    "由谁提交":"由谁提交",
    "由谁审核":"由谁审核",
    "汇报内容":"汇报内容",
    "新增成员":"新增成员",
    "编辑":"编辑",
    "删除":"删除",
    "新增内容":"新增内容",
    "删除此模板":"删除此模板",
    "禁用此模板":"禁用此模板",
    "启用此模板":"启用此模板",
    "继续创建下一个":"继续创建下一个",
    "保存":"保存",
    "创建":"创建",
    "周一":"周一",
    "周二":"周二",
    "周三":"周三",
    "周四":"周四",
    "周五":"周五",
    "周六":"周六",
    "周日":"周日",
    "删除成功":"删除成功",
    "操作成功":"操作成功",
    "请选择提交人":"请选择提交人",
    "请选择审核人":"请选择审核人",
    "请添加汇报内容":"请添加汇报内容",
    "确认要删除吗？":"确认要删除 “{0}” 吗？"
    }
    }
</i18n>

 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('汇报模板')" width="700"  @on-ok="confirm">

    <Form  ref="form" :rules="formRule" :model="formItem" label-position="top" style="min-height:600px;padding:15px">
        <div v-if="loaded">


         <Alert v-if="formItem.status==2">
         {{$t('此模板已禁用，将不会生成新的汇报')}}
        </Alert>
        <FormItem :label="$t('名称')" prop="name">
                <Input :placeholder="$t('模板名称')" v-model.trim="formItem.name" ></Input>
        </FormItem>

        <Row>
            <Col span="11">
                 <FormItem :label="$t('汇报周期')" prop="period">
                 <DataDictRadio v-model="formItem.period"
                         type="ReportTemplate.period"></DataDictRadio>
                </FormItem>
            </Col>
            <Col span="11" offset="2">
                 <FormItem :label="$t('提醒时间')" prop="remindTime">
                     <TimePicker format="HH:mm" v-model="formItem.remindTime" :placeholder="$t('请选择')" style="width: 112px"></TimePicker>
                </FormItem>
            </Col>
        </Row>
        <FormItem v-if="formItem.period==1" :label="$t('提醒日期')">
           <CheckboxGroup v-model="formItem.periodSetting">
                <Checkbox v-for="item in periodSettingArray" :key="'p'+item.value" :label="item.value">
                    <span>{{item.name}}</span>
                </Checkbox>
           </CheckboxGroup>

        </FormItem>

        <div v-if="formItem.period==1" class="rule-desc">{{$t('日报将会在提醒日期的提醒时间生成')}}</div>
        <div v-if="formItem.period==2" class="rule-desc">{{$t('周报将会在每周五的提醒时间生成')}}</div>
        <div v-if="formItem.period==3" class="rule-desc">{{$t('月报将会在每月最后一天的提醒时间生成')}}</div>


        <FormItem :label="$t('关联项目')">
            <Select filterable transfer clearable  style="width:100%" :placeholder="$t('选择项目')" v-model="formItem.projectId" >
                <Option  v-for="item in projectList" :key="item.id" :value="item.id">{{item.name}}</Option>
            </Select>
        </FormItem>

        <FormItem :label="$t('由谁提交')">
            <Tag v-for="item in formItem.submitterList" :key="'s'+item.id" closable @on-close="removeUser('selectSubmitUsers',item)">{{item.name}}</Tag>
            <Button type="default" size="small" icon="md-add" @click="selectCompanyUser('selectSubmitUsers')">{{$t('新增成员')}}</Button>
        </FormItem>

        <FormItem :label="$t('由谁审核')">
            <Tag v-for="item in formItem.auditorList" :key="'s'+item.id" closable @on-close="removeUser('selectApproveUsers',item)">{{item.name}}</Tag>
            <Button type="default" size="small"  icon="md-add" @click="selectCompanyUser('selectApproveUsers')">{{$t('新增成员')}}</Button>
        </FormItem>

        <div class="line"></div>
        <FormItem :label="$t('汇报内容')">
             <Row class="report-content-row" v-for="(item,itemIdx) in formItem.content" :key="'rep'+itemIdx">
                <Col span="21">
                    <div class="report-content-title">{{item.title}}</div>
                    <div class="report-content-desc">
                        <RichtextLabel v-model="item.content"></RichtextLabel>
                    </div>
                </Col>
                <Col span="3">
                    <IconButton @click="editReportContent(item)" icon="ios-settings" :tips="$t('编辑')"/>
                    <IconButton @click="deleteReportContent(item)" icon="ios-trash" :tips="$t('删除')"/>
                </Col>
             </Row>
             <div class="report-content-row">
                 <Button size="small" icon="md-add" type="default" @click="addReportContent">{{$t('新增内容')}}</Button>
             </div>
        </FormItem>

        <div class="line"></div>
        <FormItem v-if="formItem.id>0"  label="">
           <Button @click="deleteItem" type="error">{{$t('删除此模板')}}</Button>
           <Button v-if="formItem.status==1" style="margin-left:10px" @click="setEnable(false)" type="default">{{$t('禁用此模板')}}</Button>
           <Button v-if="formItem.status==2" style="margin-left:10px" @click="setEnable(true)" type="default">{{$t('启用此模板')}}</Button>
        </FormItem>
     </div>
    </Form>

    <div slot="footer">
        <Row>
            <Col span="12" style="text-align:left;padding-top:5px"> <Checkbox v-if="formItem.id==0" v-model="continueCreate" size="large">{{$t('继续创建下一个')}}</Checkbox>&nbsp;</Col>
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
                loaded:false,
                continueCreate:false,
                formItem:{
                    id:0,
                    name:null,
                    period:1,
                    remindTime:null,
                    projectId:null,
                    submitterList:[],
                    auditorList:[],
                    content:[],
                    periodSetting:[
                        1,2,3,4,5
                    ],
                },
                formRule:{
                    name:[vd.req,vd.name],
                    remindTime:[vd.req],
                },
                projectList:[],
                periodSettingArray:[
                    {name:this.$t('周一'),value:1},
                    {name:this.$t('周二'),value:2},
                    {name:this.$t('周三'),value:3},
                    {name:this.$t('周四'),value:4},
                    {name:this.$t('周五'),value:5},
                    {name:this.$t('周六'),value:6},
                    {name:this.$t('周日'),value:7}
                ]
            }
        },
        methods: {
            pageLoad(){
                if(this.args.id){
                    this.loadData(this.args.id);
                }else if(this.args.copyId){
                    this.loadData(this.args.copyId,true);
                }else{
                    this.loaded=true;
                }
                this.loadProjectList();
            },
            loadData(id,copy){
                app.invoke('BizAction.getReportTemplateById',[app.token,id],(info)=>{
                    this.formItem=info;
                    this.setupUser();
                    this.loaded=true;
                    if(copy){
                        this.formItem.id=0;
                    }
                })
            },
            setupUser(){
                this.formItem.auditorIds=[];
                this.formItem.submitterIds=[];
                this.formItem.auditorList.forEach((item)=>{
                    this.formItem.auditorIds.push(item.id)
                })
                this.formItem.submitterList.forEach((item)=>{
                    this.formItem.submitterIds.push(item.id)
                })
            },
            loadProjectList(){
                var query={
                    pageIndex:1,
                    pageSize:1000
                }
                app.invoke('BizAction.getReportAllProjectList',[app.token,query],(info)=>{
                    this.projectList=info.list;
                })
            },
            addToList(list,t){
                for(var i=0;i<list.length;i++){
                    var q=list[i];
                    if(q.id==t.accountId){
                        return;
                    }
                }
                list.push({
                    id:t.accountId,
                    name:t.title
                })
            },
            removeFromList(list,item){
                for(var i=0;i<list.length;i++){
                    var t=list[i];
                    if(t==item){
                        list.splice(i,1);
                        return;
                    }
                }
            },
            removeUser(type,item){
                if(type=='selectApproveUsers'){
                    this.removeFromList(this.formItem.auditorList,item)
                }
                if(type=='selectSubmitUsers'){
                    this.removeFromList(this.formItem.submitterList,item)
                }
            },
            selectCompanyUser(userList){
                app.showDialog(CompanyUserSelectDialog,{
                    callback:(t)=>{
                        if(userList=='selectApproveUsers'){
                            for(var i=0;i<t.length;i++){
                                this.addToList(this.formItem.auditorList,t[i])
                            }
                        }
                        if(userList=='selectSubmitUsers'){
                           for(var i=0;i<t.length;i++){
                                this.addToList(this.formItem.submitterList,t[i])
                            }
                        }
                    }
                })
            },
            addReportContent(){
                app.showDialog(CompanyReportContentEditDialog,{
                    callback:(item)=>{
                        this.formItem.content.push(item);
                    }
                })
            },
            deleteReportContent(item){
                for(var i=0;i<this.formItem.content.length;i++){
                    var t=this.formItem.content[i];
                    if(t==item){
                        this.formItem.content.splice(i,1);
                        return;
                    }
                }
            },
            editReportContent(item){
                app.showDialog(CompanyReportContentEditDialog,{
                   item:item
                })
            },
            deleteItem(){
                app.confirm(this.$t('确认要删除吗？',[this.formItem.name]),()=>{
                    app.invoke('BizAction.deleteReportTemplate',[app.token,this.formItem.id],(info)=>{
                        app.toast(this.$t('删除成功'))
                        app.postMessage('report.template.edit')
                        this.showDialog=false;
                    })
                })
            },
            setEnable(enable){
                var status=enable?1:2;
                app.invoke('BizAction.updateReportTemplateStatus',[app.token,this.formItem.id,status],(info)=>{
                    app.toast(this.$t('操作成功'))
                    app.postMessage('report.template.edit')
                    this.showDialog=false;
                })
            },
            confirm(){
                this.$refs.form.validate((r)=>{if(r)this.confirmForm()});
            },
            confirmForm(){
                this.setupUser();

                if(this.formItem.submitterIds.length==0){
                    app.toast(this.$t('请选择提交人'));
                    return;
                }
                if(this.formItem.auditorIds.length==0){
                    app.toast(this.$t('请选择审核人'));
                    return;
                }
                if(this.formItem.content.length==0){
                    app.toast(this.$t('请添加汇报内容'));
                    return;
                }

                var action=this.formItem.id>0?'BizAction.updateReportTemplate':'BizAction.addReportTemplate';
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.toast(this.$t('操作成功'))
                    app.postMessage('report.template.edit')
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
