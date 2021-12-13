<style scoped>
    .notify-item{
        font-size:14px;
        color:#666;
    }
    .notify-row{
        margin-bottom: 20px;
       
    }
    .notify-item-row{
        color:#333 !important;
        font-size:14px;
    }
</style>
<i18n>
{
    "en": {
        "保存": "Save",
        "保存成功":"Data saved"
    },
    "zh_CN": {
        "保存": "保存",
        "保存成功":"保存成功"
    }
}
</i18n> 
<template>
    <div style="padding:30px">
        
        <Row @click.native="selectLanguage(item)" v-for="item in languageList" :key="item.id" class="notify-row">
            <Col span="8" class="notify-item-row">{{item.name}}</Col>
            <Col span="12">
                <Icon v-if="selectedLanguage==item.id" size="20" style="color:#0094FB" type="md-checkmark" />
            </Col>
        </Row>

        <div>
            <Button @click="saveConfig" type="default">{{$t('保存')}}</Button>
        </div>
    </div>
</template>

<script>
export default {
    mixins: [componentMixin],
    data(){
        return {
            languageList:[
                {name:"简体中文",id:"zh_CN",},
                {name:"English",id:"en",}
            ],
            selectedLanguage:null,
        }
    },  
    mounted(){
        this.loadData();
    },
    methods:{
        loadData(){
            var t=window.localStorage['user.language']
            if(t==null){
                this.selectedLanguage='zh_CN'
            }else{
                this.selectedLanguage=t;
            }
        },
        selectLanguage(item){
            this.selectedLanguage=item.id;
        },
        saveConfig(){
            window.localStorage['user.language']=this.selectedLanguage;
            app.i18n.locale=this.selectedLanguage;
            app.toast(this.$t('保存成功'));
        }
    }
}
</script>