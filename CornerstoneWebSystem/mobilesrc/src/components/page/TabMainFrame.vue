
<style scoped>
    .route-view{
        margin-bottom:50px;
    }
    .weui-bar__item_on .tabbar-icon{
         fill:#0097F7;
    }
    .weui-tabbar__item.weui-bar__item_on .weui-tabbar__label {
        color:#0097F7 !important;
    }
    .tabbar-icon{
        fill:#888888;
    }
</style>
<template>
     <div>
        <router-view class="route-view" v-if="loginSuccess"></router-view>
        <tabbar style="position:fixed;z-index:100">
            <tabbar-item link="todo" :selected="currentPage=='todo'">
                 <x-icon class="tabbar-icon" slot="icon" type="ios-list" size="23"></x-icon>
                <span slot="label">待办</span>
            </tabbar-item>
            <tabbar-item link="project_list"  :selected="currentPage=='project_list'">
                <x-icon class="tabbar-icon"  slot="icon" type="android-apps" size="26"></x-icon>
                <span slot="label">项目</span>
            </tabbar-item>
            <tabbar-item  link="calendar"  :selected="currentPage=='calendar'">
                 <x-icon class="tabbar-icon"  slot="icon" type="calendar" size="25"></x-icon>
                <span slot="label">日历</span>
            </tabbar-item>
            <tabbar-item link="home"  :selected="currentPage=='home'">
                 <x-icon class="tabbar-icon"  slot="icon" type="person" size="25"></x-icon>
                 <span slot="label">我</span>
            </tabbar-item>
        </tabbar>
     </div>
</template>
<script>
import { Tabbar, TabbarItem } from 'vux'
export default {
    components: {Tabbar, TabbarItem},
    mixins:[componentMixin],
    data () {
        return {
            currentPage:null,
            loginSuccess:false,
        }
    },
    watch:{
        '$route' (to, from) {
            this.setup();
        },
    },
    mounted(){
        this.setup();
        if(this.$route.query["_token"]!=null){
            app.setCookie('token',this.$route.query["_token"])
        }
        app.getLoginInfo(()=>{
            this.loginSuccess=true
        })
    },
    methods:{
        setup(){
            this.currentPage=this.$route.params.page;
        },
    }
}
</script>
