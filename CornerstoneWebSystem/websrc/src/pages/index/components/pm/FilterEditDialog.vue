<style scoped>

</style>
<i18n>
{
    "en": {
        "过滤器": "Filter",
        "过滤条件": "Filter",
        "过滤器名称": "filter name",
        "名称":"Name",
        "继续创建下一个":"Continue to create next",
        "保存":"Save",
        "创建":"Create",
        "确认要删除过滤器":"Are you sure you want to delete the filter {0}?"
    },
    "zh_CN": {
        "过滤器": "过滤器",
        "过滤条件": "过滤条件",
        "过滤器名称": "过滤器名称",
        "名称":"名称",
        "继续创建下一个":"继续创建下一个",
        "保存":"保存",
        "创建":"创建",
        "确认要删除过滤器":"确认要删除过滤器【{0}】吗？"
    }
}
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('过滤器')" width="700"  @on-ok="confirm">

    <Form  @submit.native.prevent ref="form" :rules="formRule" :model="formItem" label-position="top" style="height:400px;padding:15px;margin-bottom:200px">
        <FormItem :label="$t('名称')" prop="name">
            <Input v-model.trim="formItem.name" :placeholder="$t('过滤器名称')"></Input>
        </FormItem>
        <FormItem label="" v-if="formItem.id>0">
            <Button @click="deleteItem" type="error" size="large" >{{$t('删除')}}</Button>
        </FormItem>
        <FormItem :label="$t('过滤条件')">
            <div class="step-box">
                <FilterStep :query-info="queryInfo" :data="query"></FilterStep>
            </div>
        </FormItem>
    </Form>
    <div slot="footer">
        <Row>
            <Col span="12" style="text-align:left;padding-top:5px"> <Checkbox v-if="formItem.id==0" v-model="continueCreate" size="large">{{$t('继续创建下一个')}}</Checkbox>&nbsp;</Col>
            <Col span="12" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{formItem.id>0?$t('保存'):$t('创建')}}</Button></Col>
        </Row>

    </div>

    </Modal>
</template>
<script>
    export default {
        name:"FilterEditDialog",
        mixins: [componentMixin],
        data () {
            return {
                continueCreate:false,
                formItem:{
                    id:0,
                    name:null,
                    projectId:null,
                    objectType:null,
                },
                formRule:{
                    name:[vd.req,vd.name],
                },
                queryInfo:{},
                query:{},
            }
        },
        methods: {
            pageLoad(){
                this.formItem.projectId=app.projectId;
                this.formItem.objectType=this.args.objectType;
                this.loadQueryInfo();
            },
            loadFilter(){
                 app.invoke("BizAction.getFilterInfoById",[app.token,this.args.id],info => {
                    this.formItem=info;
                    this.query=info.condition;
                 });
            },
            loadQueryInfo(){
                 app.invoke("BizAction.getFilterEditTaskInfo",[app.token,
                            app.projectId,this.args.objectType],info => {
                    this.queryInfo=info;
                    if(this.args.id){
                        this.loadFilter();
                    }else{
                        this.query={type:2,children:[
                                {type:0,operator:1},
                                {type:0,operator:1},
                        ]}
                    }
                 });
            },
            deleteItem(){
                app.confirm(this.$t('确认要删除过滤器',[this.formItem.name]),()=>{
                    app.invoke('BizAction.deleteFilter',[app.token,this.formItem.id],(info)=>{
                        app.postMessage('filter.edit')
                        this.showDialog=false;
                    })
                })
            },
            confirm(){
                this.$refs.form.validate((r)=>{if(r)this.confirmForm()});
            },
            confirmForm(){
                this.formItem.condition=this.query;
                var action=this.formItem.id==0?"BizAction.createFilter":"BizAction.updateFilter";
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.postMessage('filter.edit')
                    if(!this.continueCreate){
                        this.showDialog=false;
                    }else{
                        this.$refs.form.resetFields();
                    }
			    })
            }
        }
    }
</script>
