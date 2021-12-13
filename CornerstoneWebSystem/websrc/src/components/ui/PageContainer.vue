<style>
.page-container{
   width:100%;
}
.opt-title{
    font-size:14px;
    font-weight:bold;
    text-align: left;
    line-height: 15px;
}
.ivu-form-inline .ivu-form-item{
    margin-bottom: 0 !important;
}
.query-box{
    background-color: #F6F7FB;
    padding: 15px;
    margin-bottom: 10px;
}
.query-item{
    display: inline-block;
    margin-right:10px;
}

</style>
<template>
  <div class="page-container">
    <div class="opt-box">
        <Row>
            <i-Col span="6" class="opt-title">
                 <slot name="title"></slot>&nbsp;
            </i-Col>
            <i-Col span="18" style="text-align:right">
               <slot name="option"></slot>
            </i-Col>
        </Row>
        
    </div>
    <div class="query-box">
        <div class="query-item"><slot name="query"></slot></div>
        <div class="query-item">
            <Form inline >
                <FormItem><Button @click="queryClick" type="default">查询</Button></FormItem>
            </Form> 
        </div>
    </div>
    <div class="content-container">
        <slot name="content"></slot>
    </div>
    <div style="margin-top:15px">
        <Row>
            <i-Col span="6">
                 <slot name="summary"></slot>&nbsp;
            </i-Col>
            <i-Col span="18" style="text-align:right">
                <Page :total="modelData.totalCount" 
                :size="pagerSize"
                show-total
                :current="modelData.pageIndex" 
                :page-size="modelData.pageSize"
                :page-size-opts="[10, 20, 50, 200]"
                placement="top"
                    @on-page-size-change="changePageSize"
                    @on-change="changePage"
                show-elevator show-sizer></Page>
            </i-Col>
        </Row>
    </div>
       
  </div>
</template>
<script>
export default {
    name:"PageContainer",
    props: ['value','pager-size'],
    data: function() {
        return {
            modelData: this.value,
        };
    },
    mounted (){
        var thList=this.$el.querySelectorAll(".content-container table th")
        this.resizeTableColumn(thList)
    },
    watch: {
        modelData: function(val) {
            this.$emit("change", val);
        },
        value: function(val) {
            this.modelData = val;
        }
    },
    methods: {
        queryClick:function(){
            this.modelData.pageIndex=1;
            this.$emit('change')
        },
        changePage: function (page) {
            this.modelData.pageIndex = page;
            this.$emit('change')
        },
        changePageSize: function (size) {
            this.modelData.pageSize = size;
            this.$emit('change')
        },
        resizeTableColumn:function(thList){
            var thElm;
            var startOffset;
            for(var i=0;i<thList.length;i++){
                var th=thList[i]
                th.style.position = 'relative';
                var grip = document.createElement('div');
                grip.innerHTML = "&nbsp;";
                grip.style.top = 0;
                grip.style.right = 0;
                grip.style.bottom = 0;
                grip.style.width = '5px';
                //grip.style.backgroundColor="#ccc";
                grip.style.position = 'absolute';
                grip.style.cursor = 'col-resize';
                grip.addEventListener('mousedown', function (e) {
                    thElm = th;
                    startOffset = th.offsetWidth - e.pageX;
                });

                th.appendChild(grip);
            }
            this.$el.addEventListener('mousemove', function (e) {
                if (thElm) {
                    thElm.style.width = startOffset + e.pageX + 'px';
                }
            });

            this.$el.addEventListener('mouseup', function () {
                thElm = undefined;
            });
        },
    }
}
</script>