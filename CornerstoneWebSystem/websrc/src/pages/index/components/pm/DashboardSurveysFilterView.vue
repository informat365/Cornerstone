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
        "搜索标题":"搜索标题"
    },
    "en": {
        "编号":"#NO.",
        "搜索编号":"Search NO.",
        "标题":"Title",
        "搜索标题":"Search title"
    }
}
</i18n>
<template>
<div>
  
   
    <div class="menu-item-group">{{$t('标题')}}</div>
    <div >
        <input class="search-input" :placeholder="$t('搜索标题')" type="text" @change="reloadData()" v-model.trim="filters.name"/>
    </div>

</div>
</template>

<script>
export default {
    mixins: [componentMixin],
    data(){
        return {
            filters:{
                name:null
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