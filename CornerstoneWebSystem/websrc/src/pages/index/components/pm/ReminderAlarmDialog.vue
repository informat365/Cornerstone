<style scoped>
</style>
<i18n>
{
    "en": {
        "提醒": "Reminder",
        "等会再提醒我":"Wait a minute",
        "我知道了":"OK"
    },
    "zh_CN": {
        "提醒": "提醒",
        "等会再提醒我":"等会再提醒我",
        "我知道了":"我知道了"
    }
}
</i18n> 
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="false" :mask-closable="false"
        :loading="false" :title="$t('提醒')" width="500">
    <div style="padding:20px;height:200px">
        <div v-for="item in list" :key="item.id" style="margin-top:10px">
            <div style="font-size:16px;font-weight:bold">{{item.name}}</div>
            <div style="font-size:13px;color:#999;margin-top:10px">{{item.remindTime|fmtDeltaTime}} {{item.remark}}</div>
        </div>
    </div>
    
    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> 
                <Button @click="sendLater()" type="success" size="large" icon="ios-alarm" class="mr10">{{$t('等会再提醒我')}}</Button>
                <Button @click="confirm()" type="default" size="large" icon="checkmark" >{{$t('我知道了')}}</Button>
            </Col>
        </Row>
    </div>
    </Modal>
</template>


<script>
    export default {
        mixins: [componentMixin],
        data () {
            return {
                list:[]
            }
        },
        methods: {
            pageLoad(){
                this.list=this.args.list;
                new Audio('/audio/alarm.mp3').play()
            },
            confirm:function(){
               var ids=this.list.map(item=>{return item.notificationId})
                app.invoke('BizAction.readNotificationList',[app.token,ids],(info)=>{
                    this.showDialog=false;
                    app.reminderAlarmDialog=null;
                })
            },
            sendLater(){
                var ids=this.list.map(item=>{return item.notificationId})
                app.invoke('BizAction.sendNotificationsLater',[app.token,ids],(info)=>{
                    this.showDialog=false;
                    app.reminderAlarmDialog=null;
                })
            },
            updateList(list){
                this.list=list;
            }
        }
    }
</script>