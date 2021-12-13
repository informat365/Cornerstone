<style scoped>
   .fixed-header-table{
       background-color: #fff;
   }
   .biz-table{
       table-layout: fixed;
   }
</style>
<i18n>
{
    "en": {
        "暂无数据": "No Data"
    },
    "zh_CN": {
        "暂无数据": "暂无数据"
    }
}
</i18n>
<template>
<div>
    <div  style="width:100%;overflow-y:auto">
        <table class="table-content content-table table-color biz-table" :style="{width:tableWidth}">
            <thead> <slot name="thead"></slot></thead>
            <tbody > <slot name="tbody"></slot></tbody>
        </table>
        <div class="table-nodata" v-if="value==null||value.length==0">
                暂无数据
        </div>
    </div>
   
     <Row class="table-footer-bar">
            <Col span="8" class="table-footer-bar-left">
                <slot name="footer-left"></slot> &nbsp;
            </Col>
            <Col span="16" class="table-footer-bar-right">
               <Page v-if="page!=null" :total="page.total" 
                :current="page.pageIndex" 
                :page-size="page.pageSize"
                :page-size-opts="[10,15, 20, 50, 200]"
                placement="top"
                show-sizer
                show-total
                @on-page-size-change="changePageSize"
                @on-change="changePage"></Page>
            </Col>
    </Row>
</div>
</template>
<script>

export default {
    name:"BizTable",
    props: ['value','auto','page','fixwidth'],
    mounted:function(){
        if(this.auto==true||this.auto==null){
           this.setup();
        }
    },
    data:function(){
        return {
            tableWidth:"100%",
        }
    },
    beforeDestroy(){

    },
    methods:{
        changePage: function (page) {
            this.page.pageIndex = page;
            this.$emit('change')
        },
        changePageSize: function (size) {
            this.page.pageSize = size;
            this.$emit('change')
        },
        setup:function(){
           
        }
    }
}
</script>