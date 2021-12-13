<style scoped>
.opt-bar{
  background-color: #F1F4F5;
  margin-top: 0;
}
</style>
<i18n>
    {
    "en":{
    "参数名":"Parameter name",
    "查询":"Query",
    "类型":"Type",
    "参数值":"Parameter value",
    "保存":"Save",
    "编辑":"Edit",
    "保存成功":"Save success"
    },
    "zh_CN": {
    "参数名":"参数名",
    "查询":"查询",
    "类型":"类型",
    "参数值":"参数值",
    "保存":"保存",
    "编辑":"编辑",
    "保存成功":"保存成功"
    }
    }
</i18n>
<template>
    <div class="page">

        <Row class="opt-bar opt-bar-light">
          <Col span="12" class="opt-left">
               <Form inline @submit.native.prevent>
                    <FormItem >
                      <Input @on-change="loadData(true)" type="text" v-model="formItem.name" :placeholder="$t('参数名')"></Input>
                    </FormItem>
                    <FormItem>
                        <Button @click="loadData(true)" type="default">{{$t('查询')}}</Button>
                    </FormItem>
             </Form>
          </Col>
          <Col span="12" style="text-align:right">

        </Col>
       </Row>
        <div style="padding:20px">
         <BizTable  :value="tableData" >
            <template slot="thead">
                    <tr>
                    <th style="width:350px">{{$t('参数名')}}</th>
                    <th style="width:100px">{{$t('类型')}}</th>
                    <th >{{$t('参数值')}}</th>
                    <th style="width:120px;"></th>
                    </tr>
            </template>
            <template slot="tbody">
                <tr v-for="item in tableData" :key="item.id" class="table-row">
                    <td >
                        <div>{{item.name}}</div>
                        <div style="color:#999;font-size:12px;margin-top:5px">{{item.description}}</div>
                    </td>
                    <td>{{item.valueType}}</td>
                    <td>
                        <Input v-if="item.edit" v-model="item.value" style="width:100%"></Input>
                        <template v-if="item.edit==false">{{item.value}}</template>
                    </td>
                    <td style="text-align:right">
                        <Button v-if="item.edit" @click="saveConfig(item)" type="default">{{$t('保存')}}</Button>
                        <Button v-if="item.edit==false" @click="item.edit=true" type="default">{{$t('编辑')}}</Button>
                    </td>
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
        formItem:{
            name:null,
        },
        tableData:[],
        }
    },

    methods:{
        pageLoad(){
            this.loadData();
        },
        loadData(){
            app.invoke('BizAction.getConfigList',[app.token,this.formItem],(list)=>{
                for(var i=0;i<list.length;i++){
                    list[i].edit=false;
                }
                this.tableData=list;
            })
        },
        saveConfig(item){
            app.invoke('BizAction.updateConfig',[app.token,item],()=>{
                item.edit=false;
                app.toast(this.$t('保存成功'));
            })
        }
    }
}
</script>
