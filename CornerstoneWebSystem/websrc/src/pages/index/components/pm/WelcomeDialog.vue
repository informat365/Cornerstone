<style scoped>
   .intro{
       font-size:16px;
       font-weight: bold;
       text-align: center;
   }
</style>
<i18n>
    {
        "en": {
            "欢迎": "Welcome",
            "欢迎使用CORNERSTONE，点击开始查看操作指引": "Welcome to CORNERSTONE",
            "开始使用": "Start"
        },
        "zh_CN": {
            "欢迎": "欢迎",
            "欢迎使用CORNERSTONE，点击开始查看操作指引": "欢迎使用CORNERSTONE，点击开始查看操作指引",
            "开始使用": "开始使用"
        }
    }
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('欢迎')" width="500" :footer-hide="true">

        <div class="intro">
           <div style="padding:30px"> {{$t('欢迎使用CORNERSTONE，点击开始查看操作指引')}}</div>
           <div style="padding:30px"> <Button @click="confirm" type="default" size="large" >{{$t('开始使用')}}</Button></div>
        </div>
    </Modal>
</template>


<script>
export default {
mixins: [componentMixin],
    data () {
        return {

        }
    },

    methods: {
        pageLoad(){

        },
        confirm(){
            this.$intro().start(); //guide
            this.$intro().showHints();
            this.showDialog=false;
        }
    }
}
</script>
