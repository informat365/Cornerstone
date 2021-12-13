<style scoped>
.side-menu{
    height:550px;
    overflow: auto;
    background-color: #404352;
    color:#fff;
    padding-top:20px;
}
.side-menu-item{
    padding:10px 20px;
    font-size:14px;
    cursor: pointer;
    position: relative;
    color:#999;
}
.side-menu-item:hover{
    background-color: #2E3341;
    color:#fff;
}
.side-menu-item-active{
    background-color: #2E3341;
}
.side-menu-item-active::before{
    content: '';
    position: absolute;
    top:0px;
    left:0;
    height:40px;
    width:3px;
    background-color: #1989FA;
}
.main-content{
    height:550px;
    overflow: auto;
    padding:25px;
}

</style>
<i18n>
{
    "en": {
        "账号设置": "Account Settings",
        "个人信息": "Information",
        "账号密码": "Account & Password",
        "消息通知": "Notification",
        "语言设置": "Language"
    },
    "zh_CN": {
        "账号设置": "账号设置",
        "个人信息": "个人信息",
        "账号密码": "账号密码",
        "消息通知": "消息通知",
        "语言设置": "语言设置"
    }
}
</i18n>    
 <template>
    <Modal
        ref="dialog" class="nopadding-modal" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('账号设置')" width="800"  :footer-hide="true">
        <Row>
            <Col span="6" class="side-menu">
                <div style="text-align:center"><AvatarImage :name="account.name" size="verylarge" :value="account.imageId" type="tips"/></div>
                <div  style="text-align:center;margin-bottom:20px;margin-top:8px;font-size:15px">{{account.name}}</div>
                <div @click="clickMenuItem(item)" 
                    v-for="item in menuList" :key="item.id" class="side-menu-item" 
                        :class="{'side-menu-item-active':currentMenu==item.id}">{{item.name}}</div>
            </Col>
            <Col span="18" class="main-content scrollbox">
                <AccountSettingInfoView v-if="currentMenu=='1'"></AccountSettingInfoView>
                <AccountSettingPasswordView v-if="currentMenu=='2'"></AccountSettingPasswordView>
                <AccountSettingNotifyView v-if="currentMenu=='3'"></AccountSettingNotifyView>
                <AccountSettingLanguageView v-if="currentMenu=='4'"></AccountSettingLanguageView>
            </Col>
        </Row>
       
    </Modal>
</template>


<script>
    export default {
        mixins: [componentMixin],
        data () {
            return {
                currentMenu:"1",
                menuList:[
                    {name:this.$t("个人信息"),id:"1"},
                    {name:this.$t("账号密码"),id:"2"},
                    {name:this.$t("消息通知"),id:"3"},
                    {name:this.$t("语言设置"),id:"4"},
                ],
                account:{},
            }
        },
        methods: {
            pageLoad(){
                this.account=app.account;
            },
            pageMessage(type){
                if(type=='account.edit'){
                    this.account=app.account;
                }
            },
            clickMenuItem(item){
                this.currentMenu=item.id
            }
        }
    }
</script>