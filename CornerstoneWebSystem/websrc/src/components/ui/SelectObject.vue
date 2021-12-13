<style scoped>
    .info-box {
        margin-right:5px;
        display: inline-block;
        width:170px;
        height: 32px;
        line-height: 1.5;
        padding: 6px 7px;
        font-size: 12px;
        border: 1px solid #dddee1;
        color: #495060;
        background-color: #fff;
        background-image: none;
        cursor: text;
    }
    .text-no-wrap{
        text-overflow:ellipsis;
        overflow: hidden;
        white-space: nowrap;
    }
</style>
<i18n>
{
    "en": {
        "请选择":"Select"
    },
    "zh_CN": {
        "请选择":"请选择"
    }
}
</i18n>
<template>
    <Row>
        <i-Col span="20" class="info-box text-no-wrap">
            <span v-if="objectName==null" style="color:#C2C5CA">{{$t('请选择')}}</span>
            <span v-if="objectName!=null">{{objectName}}</span>
        </i-Col>
        <i-Col>
            <i-button v-if="readonly==false||readonly==null" @click="chooseObject" type="default" size="small" icon="ios-search"></i-button>
        </i-Col>
    </Row>
</template>
<script>
export default {
    name:"SelectObject",
    props: ['value','oname','domain','query','readonly'],
    data:function(){
        return {
            objectId:this.value,
            objectName:this.oname,
        }
    },
    watch:{
        objectId:function(val){
            this.$emit('input', val);
        },
        value:function(val){
            this.objectId=val;
        },
        oname:function(val){
            this.objectName=val;
        },
        objectName:function(val){
            this.$emit('change',val)
        }
    },
    methods:{
        chooseObject:function(e){
           app.showDialog(SelectObjectDialog,{
               callback:this.chooseCallback,
               multiple:false,
               domain:this.domain,
               query:this.query
           }); 
        },
        chooseCallback:function(list){
            var t=list[0];
            this.objectId=t.id;
            this.objectName=t.name;
        }
    }
}
</script>