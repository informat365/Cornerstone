<style scoped>
.check{
        color:#0097F7;
    }
    .tag-color{
        display: inline-block;
        width:30px;
        height: 30px;
        border-radius: 50%;
        margin-right:10px;
        color:#fff;
        overflow: hidden;
    }
</style>
<i18n>
{
    "en": {
        "创建日程": "Create Schedule",
        "事项":"Event",
        "参与者":"Members",
        "输入提醒事项":"Event name",
        "备注":"Remark",
        "开始时间":"Start Date",
        "结束时间":"End Date",
        "未设置":"none",
        "重复":"Repeat",
        "删除":"Delete",
        "继续创建下一个":"Continue to create next",
        "保存":"Save",
        "创建":"Create",
        "确认要删除":"Are you sure you want to delete the schedule {0}?",
        "保存成功":"Success",
        "创建成功":"Success",
        "颜色标识":"Color"
    },
    "zh_CN": {
        "创建日程": "创建日程",
        "事项":"事项",
        "输入提醒事项":"输入提醒事项",
        "备注":"备注",
        "参与者":"参与者",
        "开始时间":"开始时间",
        "结束时间":"结束时间",
        "未设置":"未设置",
        "重复":"重复",
        "删除":"删除",
        "继续创建下一个":"继续创建下一个",
        "保存":"保存",
        "创建":"创建",
        "确认要删除":"确认要删除日程【{0}】吗？",
        "保存成功":"保存成功",
        "创建成功":"创建成功",
        "颜色标识":"颜色标识"
    }
}
</i18n>  
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('创建日程')" width="700"  @on-ok="confirm">

    <Form  @submit.native.prevent ref="form" :rules="formRule" :model="formItem"  label-position="top" style="height:600px;padding:25px">
        <FormItem :label="$t('事项')" prop="name">
            <Input v-model.trim="formItem.name" :placeholder="$t('输入提醒事项')"></Input>
        </FormItem>

         <FormItem :label="$t('参与者')" prop="ownerAccountIdList">
            <Select v-model="formItem.ownerAccountIdList" multiple style="width:100%">
                <Option v-for="item in accountList" :value="item.id" :key="item.id">{{ item.name }}</Option>
            </Select>
        </FormItem>

        <FormItem :label="$t('开始时间')" prop="startTime">
             <ExDatePicker type="datetime" v-model="formItem.startTime" :placeholder="$t('未设置')" ></ExDatePicker>
        </FormItem>

        <FormItem :label="$t('结束时间')" prop="endTime">
             <ExDatePicker type="datetime" v-model="formItem.endTime" :placeholder="$t('未设置')" ></ExDatePicker>
        </FormItem>
        
        <FormItem   :label="$t('颜色标识')" prop="color">
              <div style="height:50px;text-align:center">
                    <span @click="setColor(item)" v-for="item in colorArray" :key="item" 
                        class="tag-color" :style="{backgroundColor:item}">
                        <Icon size="20" v-show="item==formItem.color" type="md-checkmark" />
                    </span>
            </div>
        </FormItem>

         <FormItem :label="$t('重复')" prop="repeat">
               <DataDictRadio type='CalendarSchedule.repeat' v-model="formItem.repeat"></DataDictRadio>
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
                    startTime:null,
                    endTime:null,
                    ownerAccountIdList:[],
                    repeat:1,
                    color:"#2E94B9",
                },
                formRule:{
                    name:[vd.req,vd.name],
                    remark:[vd.desc],
                    ownerAccountIdList:[vd.req],
                    startTime:[vd.req],
                    endTime:[vd.req]
                },
                accountList:[],
                colorArray:[
                        "#2E94B9",
                        "#F0B775",
                        "#D25565",
                        "#F54EA2",
                        "#42218E",
                        "#5BE7C4",
                        "#525564",
                ]
            }
        },
        methods: {
            pageLoad(){
                this.loadCalendar(this.args.calendarId);
                this.formItem.calendarId=this.args.calendarId;
                if(this.args.id){
                    app.invoke('BizAction.getCalendarScheduleById',[app.token,this.args.id],(info)=>{
                       this.formItem=info;
                    })
                }
                if(this.args.startDate){
                    this.formItem.startTime=this.args.startDate
                }
                if(this.args.endDate){
                    this.formItem.endTime=this.args.endDate
                }
                if(this.args.accountId){
                    this.formItem.ownerAccountIdList.push(this.args.accountId)
                }
            },
            loadCalendar(calendarId){
                app.invoke('BizAction.getCalendarById',[app.token,calendarId],(info)=>{
                    this.accountList=info.memberInfos;
                })
            },
            setColor(color){
                if(this.formItem.color==color){
                    this.formItem.color=null;
                }else{
                    this.formItem.color=color;
                }
            },
            deleteItem(){
                app.confirm(this.$t('确认要删除',[this.formItem.name]),()=>{
                    app.invoke('BizAction.deleteCalendarSchedule',[app.token,this.formItem.id],(info)=>{
                        app.postMessage('schedule.edit')
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
                var action=this.formItem.id>0?'BizAction.updateCalendarSchedule':'BizAction.addCalendarSchedule';
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.toast(this.formItem.name+" "+(this.formItem.id>0?this.$t('保存成功'):this.$t('创建成功')))
                    app.postMessage('schedule.edit')
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