<style scoped>
.form-desc{
    font-size:12px;
    color:#666;
    margin-bottom:10px;
}
.side-box-inner{
    padding:10px;
}
</style>
<i18n>
{
	"en": {
        "操作": "操作",
		"执行动作": "执行动作",
		"调用URL": "调用URL",
		"发送邮件": "发送邮件",
		"webAPI地址": "webAPI地址",
		"使用表单中的字段作为发送邮件的参数": "使用表单中的字段作为发送邮件的参数，只支持单行文本和多行文本格式的字段",
		"邮件标题": "邮件标题",
		"收件人": "收件人",
		"抄送": "抄送",
		"正文": "正文",
		"流转规则": "流转规则",
		"执行后直接进入下一节点": "执行后直接进入下一节点",
		"执行后等待": "执行后等待"
    },
	"zh_CN": {
		"操作": "操作",
		"执行动作": "执行动作",
		"调用URL": "调用URL",
		"发送邮件": "发送邮件",
		"webAPI地址": "webAPI地址",
		"使用表单中的字段作为发送邮件的参数": "使用表单中的字段作为发送邮件的参数，只支持单行文本和多行文本格式的字段",
		"邮件标题": "邮件标题",
		"收件人": "收件人",
		"抄送": "抄送",
		"正文": "正文",
		"流转规则": "流转规则",
		"执行后直接进入下一节点": "执行后直接进入下一节点",
		"执行后等待": "执行后等待"
	}
}
</i18n>
<template>
    <Tabs :animated="false" value="op" >
    <TabPane :label="$t('操作')" name="op">
            <div class="side-box-inner">
                <Form label-position="top">
                    <FormItem :label="$t('执行动作')">
                        <Select @on-change="fireChanged" transfer v-model="formItem.action">
                            <Option value="webApi">{{$t('调用URL')}}</Option>
                            <Option value="email">{{$t('发送邮件')}}</Option>
                        </Select>
                    </FormItem>
                    <FormItem v-if="formItem.action=='webApi'" :label="$t('webAPI地址')">
                        <Input @on-change="fireChanged" type="text" v-model="formItem.webApiSetting.url"/>
                    </FormItem>
                    <template v-if="formItem.action=='email'">
                        <div class="form-desc">{{$t('使用表单中的字段作为发送邮件的参数')}}</div>
                        <FormItem  :label="$t('邮件标题')">
                            <Select @on-change="fireChanged" multiple transfer v-model="formItem.emailSetting.title">
                                <Option v-for="item in fieldList" :key="+'w-'+item.id" :value="item.id">{{item.name}}</Option>
                            </Select>
                        </FormItem>
                        <FormItem  :label="$t('收件人')">
                            <Select @on-change="fireChanged" multiple transfer v-model="formItem.emailSetting.to">
                                <Option v-for="item in fieldList" :key="+'t-'+item.id" :value="item.id">{{item.name}}</Option>
                            </Select>
                        </FormItem>
                        <FormItem  :label="$t('抄送')">
                            <Select @on-change="fireChanged" multiple transfer v-model="formItem.emailSetting.cc">
                                <Option v-for="item in fieldList" :key="+'c-'+item.id" :value="item.id">{{item.name}}</Option>
                            </Select>
                        </FormItem>
                         <FormItem  :label="$t('正文')">
                            <Select @on-change="fireChanged" multiple transfer v-model="formItem.emailSetting.content">
                                <Option v-for="item in fieldList" :key="+'content-'+item.id" :value="item.id">{{item.name}}</Option>
                            </Select>
                        </FormItem>
                    </template>
                   
                </Form>
            </div>
        </TabPane>
        <TabPane :label="$t('流转规则')" name="owner">
            <div class="side-box-inner">
                <Form label-position="top">
                     <FormItem :label="$t('流转规则')">
                        <Select @on-change="fireChanged" transfer v-model="formItem.forwardRule">
                            <Option value="next">{{$t('执行后直接进入下一节点')}}</Option>
                            <Option value="wait">{{$t('执行后等待')}}</Option>
                        </Select>
                    </FormItem>
                </Form>
            </div>
        </TabPane>

    </Tabs>
</template>
<script>
export default {
    props: ["value",'graph','workflowFormFieldList'],
    data() {
        return {
            fieldList:[],
            host:"",
            formItem:{
                
            }
        };
    },
    watch:{
        value(val){
            this.setupValue();
        }
    },
    mounted(){
        this.host=app.getHost();
        this.setupValue();
        this.setupFieldList();
    },
    methods: {
        fireChanged(){
            this.$emit('input',this.formItem)
        },
        setupValue(){
            this.formItem=this.value;
            if(this.formItem.emailSetting==null){
                this.formItem.emailSetting={};
            }
            if(this.formItem.webApiSetting==null){
                this.formItem.webApiSetting={
                    to:[],
                    cc:[],
                    title:[],
                    content:[]
                };
            }
            if(this.formItem.forwardRule==null){
                this.formItem.forwardRule="next";
            }
        },
        setupFieldList(){
           for(var i=0;i<this.workflowFormFieldList.length;i++){
                var wf=this.workflowFormFieldList[i];
                if(wf.type=='text-single'||wf.type=='text-area'){
                    this.fieldList.push(wf);
                }
            }  
        },
    }
};
</script>