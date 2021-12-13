<style scoped>
.info{
    font-size:16px;
    color:#666;
}
</style>
<i18n>
{
    "en": {
        "订阅日历": "Subscribe",
        "你可以在":"You can view CORNERSTONE calendars in iCal-enabled software such as Apple Calendar.",
        "如果你不希望":"If you don't want others to see the calendar content, don't share subscription links with them. If the link is leaked, you can ",
        "地址重新生成后原有的链接将不可用":"The original link will not be available after the address is regenerated",
        "重新生成地址":"Regenerate link",
        "点击订阅":"Subscribe",
        "确定要重新生成日历地址吗":"Are you sure you want to regenerate the calendar address? The original link will not be available after rebuilding"
    },
    "zh_CN": {
        "订阅日历": "订阅日历",
        "你可以在":"你可以在 苹果日历 等支持 iCal协议的软件中查看 CORNERSTONE 的日历。",
        "如果你不希望":"如果你不希望其他人看到日历内容，请不要与他们共享订阅链接。如果泄露了链接您可以",
        "地址重新生成后原有的链接将不可用":"地址重新生成后原有的链接将不可用",
        "重新生成地址":"重新生成地址",
        "点击订阅":"点击订阅",
        "确定要重新生成日历地址吗":"确定要重新生成日历地址吗？重新生成后原有链接将不可用"
    }
}
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('订阅日历')" width="550" :footer-hide="true">
    <div style="padding:15px;height:500px;">
        <div class="info">
            {{$t('你可以在')}}<br>
            {{$t('如果你不希望')}}
            <a @click="resetAddr" href="#">{{$t('重新生成地址')}}</a>，{{$t('地址重新生成后原有的链接将不可用')}}
        </div>
        <div style="text-align:center;padding:30px">
            <Button @click="subscribe" type="default" size="large">{{$t('点击订阅')}}</Button>
        </div>
    </div>

    </Modal>
</template>


<script>
    export default {
        mixins: [componentMixin],
        data () {
            return {
               uuid:null,
            }
        },
        methods: {
            pageLoad(){
                this.loadData();
            },
            loadData(){
                app.invoke('BizAction.getAccountCalUuid',[app.token],(info)=>{
                    this.uuid=info;
                })
            },
            subscribe(){
                var host=window.location.host;
                window.open("webcal://"+host+"/p/webapi/subscribe_cal/"+this.uuid);
            },
            resetAddr(){
                app.confirm(this.$t('确定要重新生成日历地址吗'),()=>{
                    app.invoke('BizAction.refreshAccountCalUuid',[app.token],(info)=>{
                        this.uuid=info;
                    })
                })
            },

        }
    }
</script>
