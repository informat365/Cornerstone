<style scoped>
.opt-bar{
  background-color: #F1F4F5;
  margin-top: 0;
}
</style>

<i18n>
    {
    "en": {
    "操作人":"Operator",
    "操作内容":"Content",
    "开始时间":"Start",
    "结束时间":"End",
    "查询":"Query",
    "备注":"Remark",
    "操作日期":"Opt Date"
    },
    "zh_CN": {
    "操作人":"操作人",
    "操作内容":"操作内容",
    "开始时间":"开始时间",
    "结束时间":"结束时间",
    "查询":"查询",
    "备注":"备注",
    "操作日期":"操作日期"
    }
    }
</i18n>

<template>
    <div class="page">

        <Row class="opt-bar opt-bar-light">
          <Col span="24" class="opt-left">
               <Form inline @submit.native.prevent>
                    <FormItem >
                      <Input @on-change="loadData(true)" type="text" v-model="formItem.accountName" :placeholder="$t('操作人')"></Input>
                    </FormItem>
                     <FormItem >
                        <Input @on-change="loadData(true)" type="text" v-model="formItem.event" :placeholder="$t('操作内容')"></Input>
                    </FormItem>
                    <FormItem >
                       <ExDatePicker @on-change="loadData(true)" type="date" style="width:130px" v-model="formItem.createTimeStart" :placeholder="$t('开始时间')"></ExDatePicker>
                    </FormItem>
                    <FormItem >
                       <ExDatePicker @on-change="loadData(true)" :day-end="true" type="date" style="width:130px" v-model="formItem.createTimeEnd" :placeholder="$t('结束时间')"></ExDatePicker>
                    </FormItem>
                    <FormItem>
                        <Button @click="loadData(true)" type="default">{{$t('查询')}}</Button>
                    </FormItem>
             </Form>
          </Col>

       </Row>
        <div style="padding:20px">
         <BizTable :fixed="true" @change="loadData" :page="pageQuery" :value="tableData" >
            <template slot="thead">
                    <tr>
                    <th style="width:150px">{{$t('操作人')}}</th>
                    <th>{{$t('操作内容')}}</th>
                    <th>{{$t('备注')}}</th>
                    <th style="width:130px;">{{$t('操作日期')}}</th>
                    </tr>
            </template>
            <template slot="tbody">
                <tr v-for="item in tableData" :key="item.id" class="table-row">
                    <td>{{item.accountName}}</td>
                    <td>{{item.event}}</td>
                    <td>{{item.remark}}</td>
                    <td>{{item.createTime|fmtDateTime}}</td>

                </tr>

            </template>
        </BizTable>
        </div>
    </div>
</template>

<script>

export default {
    mixins: [componentMixin],
    data(){
        return {
            pageQuery:{
                pageIndex:1,
                pageSize:20,
            },
            formItem:{
                name:null,
                status:null,
                period:null,
            },
            tableData:[],
            }
    },

    methods:{
        pageLoad(){
            this.loadData();
        },
        loadData(resetPage){
            if(resetPage){
                this.pageQuery.pageIndex=1;
            }
            var query=copyObject(this.pageQuery,this.formItem)
            app.invoke('BizAction.getOptLogList',[app.token,query],(info)=>{
                this.tableData=info.list;
                this.pageQuery.total=info.count;
            })
        }
    }
}
</script>
