<style scoped>
    .menu-item-group{
        font-size:12px;
        color:#999;
        padding:8px 0px;
        margin-top:20px;
    }
    .search-input{
        border:none;
        border-bottom: 1px solid #eee;
        outline: none;
        width:100%;
    }
</style>
<i18n>
{
    "zh_CN":{
        "编号":"编号",
        "搜索编号":"搜索编号",
        "标题":"标题",
        "搜索标题":"搜索标题",
        "类型":"类型",
        "搜索流程类型":"搜索流程类型",
        "提交人":"提交人",
        "搜索提交人":"搜索提交人"
    },
    "en": {
        "编号":"#NO",
        "搜索编号":"search NO.",
        "标题":"Title",
        "搜索标题":"search title",
        "类型":"Type",
        "搜索流程类型":"search type",
        "提交人":"Creater",
        "搜索提交人":"search creater"
    }
}
</i18n>
<template>
<div>
  
   <div class="menu-item-group">{{$t('编号')}}</div>
    <div >
        <input class="search-input" :placeholder="$t('搜索编号')" type="text" @change="reloadData()" v-model.trim="filters.serialNo"/>
    </div>

    <div class="menu-item-group">{{$t('标题')}}</div>
    <div >
        <input class="search-input" :placeholder="$t('搜索标题')" type="text" @change="reloadData()" v-model.trim="filters.title"/>
    </div>

    <div class="menu-item-group">{{$t('类型')}}</div>
    <div >
        <input class="search-input" :placeholder="$t('搜索流程类型')" type="text" @change="reloadData()" v-model.trim="filters.workflowDefineName"/>
    </div>

     <div class="menu-item-group">{{$t('提交人')}}</div>
    <div >
        <input class="search-input" :placeholder="$t('搜索提交人')" type="text" @change="reloadData()" v-model.trim="filters.createAccountName"/>
    </div>

</div>
</template>

<script>
export default {
    mixins: [componentMixin],
    data(){
        return {
            filters:{
                serialNo:null,
                title:null,
                createAccountName:null,
                workflowDefineName:null
            },
        }
    },  
    methods:{
        reloadData(){
            this.$emit('change',this.filters)
        }
    }
}
</script>