<style >
*{
    box-sizing: border-box;
}
body {
    font-family: Helvetica,"Helvetica Neue", "PingFang SC",
    "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", sans-serif;
    font-size:13px;
    font-weight:400;
    color:#2b2b2b;
    height:100%;
    line-height:1.6;
    min-height:100% !important;
    background: #fff !important;
    cursor:default;
}
.note-info{
    padding:0 15px;
    color:#999;
}
.note-title{
    color:#333;
    font-size:18px;
    font-weight: bold;
    padding:15px;
}
</style>
<template>
<div class="app-frame">
    <div class="note-title">{{wiki.name}}</div>
    <div class="note-info">{{wiki.createAccountName}} · {{wiki.updateTime|fmtDateTime}}</div>
    <div class="simditor richtext-box" style="border:none">
        <div class="simditor-body richtext-body" v-html="wiki.content"></div>
    </div>
</div>
</template>
<script>
//
import Vue from 'vue';
const formatDatetime=function(t){
    if(t==null){
        return "-";
    }
    var date=new Date(t);
    var year = date.getFullYear()>2000?date.getFullYear()-2000:date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
    var hour = date.getHours();
    var minute = date.getMinutes();
    return [year, month, day].map(formatNumber).join('-') + ' ' + [hour, minute].map(formatNumber).join(':')
};
const formatNumber=function(n){
    n = n.toString();
    return n[1] ? n : '0' + n
};
Vue.filter('fmtDateTime', function (v) {
    return formatDatetime(v);
})
export default {
    data() {
        return {
            serverAddr:"",
            wiki:{}
        }
    },
    mounted() {
        this.serverAddr = [window.location.protocol, '//', window.location.host].join('');
        var url = new URL(window.location.href);
        var pageArgs={};
        url.searchParams.forEach((k,v)=>{
            pageArgs[v]=k;
        })
        if(pageArgs['id']){
            this.loadData(pageArgs['id'])
        }
    },
    methods: {
        loadData(id){
            ajaxInvoke(this.serverAddr+'/p/api/invoke/','BizAction.getWikiPageByUuid',[id],(t)=>{
                window.document.title=t.name;
                this.wiki=t;
            })
        }
    }
};
</script>
