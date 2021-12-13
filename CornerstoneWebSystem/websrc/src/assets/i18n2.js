import Vue from 'vue'
import VueI18n from 'vue-i18n'
Vue.use(VueI18n)
//
function getLanguage(){
    var t=window.localStorage['user.language'];
    if(t==null){
        return 'zh_CN'
    }else{
        return t;
    }
}
//
const i18n = new VueI18n({
    locale: getLanguage(), 
})

//
export default i18n;