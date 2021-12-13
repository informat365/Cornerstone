<style scoped>
.popup{
    position: fixed;
    z-index:100;
    width:100vw;
    top:0;
    right:0;
    height: 100vh;
    overflow: auto;
    text-align: center;
}
.search-box{
    margin-top:100px;
    display: inline-block;
    text-align: left;
    width:600px;
    background-color: #F1F4F5;
    border-radius: 5px;
    box-shadow:rgba(0, 0, 0, 0.4) 0px 1px 11px 0px;
    position: relative;
}
.search-input-box{
    padding:25px;
}
.search-input{
    font-size:17px;
    border:none;
    color:#333;
    background-color: transparent;
    outline: none;
    margin-left:5px;
    width:500px;
}
.search-result{
    border-top:1px solid #eee;
    background-color: #fff;
    padding-top:10px;
    padding-bottom:10px;
    max-height:calc(100vh - 200px);
    overflow: auto;
}
.result-item{
    padding:15px;
    padding-top:7px;
    padding-bottom:7px;
    font-size:13px;
    color:#333;
    cursor: pointer;
}
.search-remark{
    font-size:12px;
}
.result-item:hover{
    background-color: aliceblue;
}
.close-btn{
    position: absolute;
    top:20px;
    right:15px;
    cursor: pointer;
    transition: all 0.3s;
}
.close-btn:hover{
    color:#999;
}
.search-id{
    color:#999;
    margin-right:5px;
}
.search-type{
    color:#999;
    margin-right:5px;
}
.search-name{
    color:#333;
}

</style>
<i18n>
{
  "en": {
    "快速搜索": "Search",
     "创建":"created"
  },
  "zh_CN": {
     "快速搜索": "快速搜索",
     "创建":"创建"
  }
}
</i18n>
<template>
    <div class="popup" @keyup.esc="$emit('popup-close')">
        <div class="search-box">
             <Icon @click.native="clickCloseBtn" size="30" type="ios-close" class="close-btn"/>
             <div class="search-input-box">
                 <Icon size="25" type="ios-search" />
                 <input v-focus v-model.trim="searchContent" class="search-input" :placeholder="$t('快速搜索')" />
             </div>
             <div v-show="resultList.length>0" class="scrollbox search-result">
                <div @click="showResult(item)" v-for="item in resultList" :key="item.id" class="result-item text-no-wrap">
                    <div class="search-name">{{item.name}}</div>
                    <div class="search-remark"> 
                        <span class="search-id">{{getItemType(item)}} </span> 
                        <span class="search-type">{{item.serialNo}} {{item.projectName}} {{item.createAccountName}}{{$t('创建')}}</span>
                        
                    </div>
                </div>
             </div>
        </div>
    </div>
</template>

<script>
    export default {
        data () {
            return {
                searchContent:null,
                resultList:[],
            }
        },
        watch:{
            searchContent(val){
                if(val!=null&&val!=""){
                    this.debounceSearch();
                }else{
                    this.resultList=[];
                }
            }
        },  
        created(){
            this.debounceSearch=app.debounce(this.search,500)
        },
        methods:{
            search(){
                app.invoke('BizAction.searchDocumentList',[app.token,this.searchContent],(list)=>{
                    this.resultList=list;
                });
            },
            getItemType(item){
                if(item.objectType>0){
                    return app.dataDictValue('Task.objectType',item.objectType)
                }
                if(item.type==2){
                    return "WIKI"
                }
                if(item.type==3){
                    return "文件"
                }
                return "";
            },
            showResult(item){
                if(item.objectType>0){
                    app.showDialog(TaskDialog,{
                        taskId:item.uuid,
                        showTopBar:true
                    })
                }
                if(item.type==2){
                     //TODO
                     // /pm/project/d3950cd7e0f5446d88ca4a1ce79c6920/wiki?id=44&wiki=94
                }
                if(item.type==3){
                     //TODO
                }
            },
            clickCloseBtn(){
                this.$emit('popup-close');
            }
        }
    }
</script>