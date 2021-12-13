<style scoped>
</style>
<i18n>
{
    "en": {
        "录入工时":"Add Work Log",
        "工作事项":"Items",
        "开始时间":"Start",
        "这段时间内做的事情":"What to do during this period of time",
        "耗费时间":"Time(Hour)",
        "删除":"Delete",
        "当天已累计录入工时":"Accumulated working hours have been entered on that day",
        "继续录入下一个":"Continue to create next",
        "确定":"OK",
        "确认要删除此工时记录吗":"Are you sure you want to delete this man-hour record?"
    },
    "zh_CN": {
        "录入工时":"录入工时",
        "工作事项":"工作事项",
        "开始时间":"开始时间",
        "这段时间内做的事情":"这段时间内做的事情",
        "耗费时间":"耗费时间(小时)",
        "删除":"删除",
        "当天已累计录入工时":"当天已累计录入工时",
        "继续录入下一个":"继续录入下一个",
        "确定":"确定",
        "确认要删除此工时记录吗":"确认要删除此工时记录吗？"
    }   
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('录入工时')" width="700"  @on-ok="confirm">

    <Form  ref="form" :rules="formRule" :model="formItem"  label-position="top" style="height:400px;padding:15px">
        <FormItem prop="content" :label="$t('工作事项')">
                <Input v-model.trim="formItem.content" type="textarea" :cols="3" :placeholder="$t('这段时间内做的事情')"></Input>
        </FormItem>

        <FormItem prop="startTime" :label="$t('开始时间')">
            <div><ExDatePicker transfer @on-change="loadWorktimeHours" v-model="formItem.startTime" ></ExDatePicker></div>
            <div style="margin-top:5px;color:#666">{{$t('当天已累计录入工时')}} <span style="color:#009AF4;font-weight:bold">{{totalHours}}h</span></div>
        </FormItem>

        <FormItem prop="hour" :label="$t('耗费时间')">
                <InputNumber v-model="formItem.hour"></InputNumber>
        </FormItem>

        <FormItem label="" v-if="formItem.id>0">
            <Button @click="deleteItem" type="error" size="large" >{{$t('删除')}}</Button>
        </FormItem> 

    </Form>
   <div slot="footer">
        <Row>
            <Col span="12" style="text-align:left;padding-top:5px"> <Checkbox v-if="formItem.id==0" v-model="continueCreate" size="large">{{$t('继续录入下一个')}}</Checkbox></Col>
            <Col span="12" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{$t('确定')}}</Button></Col>
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
                    taskId:0,
                    startTime:null,
                    hour:8,
                    content:null,
                },
                formRule:{
                    content:[vd.req,vd.desc],
                    startTime:[vd.req],
                    hour:[vd.req],
                },
                totalHours:0
            }
        },
        
        methods: {
            pageLoad(){
                this.formItem.taskId=this.args.taskId;
                if(this.args.item){
                    this.formItem.id=this.args.item.id;
                    this.formItem.taskId=this.args.item.taskId;
                    this.formItem.hour=this.args.item.hour;
                    this.formItem.startTime=this.args.item.startTime;
                    this.formItem.content=this.args.item.content;
                }else{
                    this.formItem.startTime=new Date().getTime();
                }
                this.loadWorktimeHours();
            },
            loadWorktimeHours(){
                app.invoke('BizAction.getTaskWorkHours',[app.token,
                    this.formItem.startTime],(info)=>{
                    this.totalHours=info;
                })
            },
            deleteItem(){
                app.confirm(this.$t('确认要删除此工时记录吗'),()=>{
                    app.invoke('BizAction.deleteTaskWorkTimeLog',[app.token,this.formItem.id],(info)=>{
                        this.showDialog=false;
                        this.args.callback();
                    })
                })
            },
            confirm(){
                this.$refs.form.validate((r)=>{
                    if(r)this.confirmForm()
                });
            },
            confirmForm(){
                var action=this.formItem.id>0?"BizAction.updateTaskWorkTimeLog":"BizAction.addTaskWorkTimeLog"
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