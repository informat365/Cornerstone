<style scoped>
.form-desc{
    font-size:12px;
    color:#666;
    margin-bottom:5px;
    margin-left:7px;
}
.side-box-inner{
    padding:10px;
}
</style>
<i18n>
{
	"en": {
        "字段": "字段",
		"设置流程流转到此步骤时表单字段的显示和是否可编辑": "设置流程流转到此步骤时表单字段的显示和是否可编辑",
		"显示": "显示",
		"编辑": "编辑",
		"操作": "操作",
		"提交": "提交",
		"提交按钮显示的文本": "提交按钮显示的文本",
		"允许回退": "允许回退",
		"是否显示回退按钮": "是否显示回退按钮",
		"请选择可回退到节点": "请选择可回退到节点",
		"允许暂存": "允许暂存",
		"是否显示暂存按钮": "是否显示暂存按钮",
		"允许终止流程": "允许终止流程",
		"是否显示终止流程按钮": "是否显示终止流程按钮",
		"终止按钮显示的文本": "终止按钮显示的文本",
		"允许转交": "允许转交",
		"允许将负责人转交给他人": "允许将负责人转交给他人",
		"流转规则": "流转规则",
		"任意一个负责人通过后流转到下一节点": "任意一个负责人通过后流转到下一节点",
		"所有负责人通过后流转到下一节点": "所有负责人通过后流转到下一节点",
		"负责人": "负责人",
		"抄送": "抄送"
    },
	"zh_CN": {
		"字段": "字段",
		"设置流程流转到此步骤时表单字段的显示和是否可编辑": "设置流程流转到此步骤时表单字段的显示和是否可编辑",
		"显示": "显示",
		"编辑": "编辑",
		"操作": "操作",
		"提交": "提交",
		"提交按钮显示的文本": "提交按钮显示的文本",
		"允许回退": "允许回退",
		"是否显示回退按钮": "是否显示回退按钮",
		"请选择可回退到节点": "请选择可回退到节点",
		"允许暂存": "允许暂存",
		"是否显示暂存按钮": "是否显示暂存按钮",
		"允许终止流程": "允许终止流程",
		"是否显示终止流程按钮": "是否显示终止流程按钮",
		"终止按钮显示的文本": "终止按钮显示的文本",
		"允许转交": "允许转交",
		"允许将负责人转交给他人": "允许将负责人转交给他人",
		"流转规则": "流转规则",
		"任意一个负责人通过后流转到下一节点": "任意一个负责人通过后流转到下一节点",
		"所有负责人通过后流转到下一节点": "所有负责人通过后流转到下一节点",
		"负责人": "负责人",
		"抄送": "抄送"
	}
}
</i18n>
<template>
    <Tabs value="field" :animated="false">
        <TabPane :label="$t('字段')" name="field">
            <div style="padding:10px" class="form-desc">{{$t('设置流程流转到此步骤时表单字段的显示和是否可编辑')}}</div>
            <table
                class="table-content table-content-small"
                style="table-layout:fixed;border-top:1px solid #eee">
                <thead>
                    <tr class="table-row table-row-small">
                        <th>{{$t('字段')}}</th>
                        <th style="width:70px">{{$t('显示')}}</th>
                        <th style="width:70px">{{$t('编辑')}}</th>
                    </tr>
                </thead>
                <tbody>
                    <tr class="table-row table-row-small">
                        <td class="text-no-wrap"></td>
                        <td><i-Switch v-model="visibleSelectAll" size="small"></i-Switch></td>
                        <td><i-Switch v-model="editableSelectAll" size="small"></i-Switch> </td>
                    </tr>
                    <tr v-for="item in value.fieldList" :key="'f'+item.id" class="table-row table-row-small">
                        <td class="text-no-wrap">{{item.name}}</td>
                        <td><i-Switch @on-change="changeItemVisible(item)" v-model="item.visible" size="small"></i-Switch></td>
                        <td><i-Switch v-if="item.type&&item.type.indexOf('static')==-1" v-model="item.editable" size="small"></i-Switch> </td>
                    </tr>
                </tbody>
            </table>
        </TabPane>
        <TabPane :label="$t('操作')" name="op">
            <div class="side-box-inner">
                <Form label-position="top">
                    <FormItem :label="$t('提交')">
                        <div>
                            <Input style="width:100px" v-model="value.submitButtonText"/>
                            <span class="form-desc">{{$t('提交按钮显示的文本')}}</span>
                        </div>
                    </FormItem>

                    <FormItem :label="$t('允许回退')">
                        <div>
                            <i-Switch v-model="value.enableBackword"></i-Switch>
                            <span class="form-desc">{{$t('是否显示回退按钮')}}</span>
                        </div>
                        <div v-if="value.enableBackword">
                            <Select :placeholder="$t('请选择可回退到节点')" multiple transfer v-model="value.backwordNodeList">
                                <template v-for="item in graph.nodes">
                                    <Option  v-if="item.type=='nodeEvent'" :key="item.id" :value="item.id">{{item.name}}</Option>
                                </template>
                            </Select>
                        </div>
                    </FormItem>

                    <FormItem :label="$t('允许暂存')">
                       
                        <div>
                            <i-Switch v-model="value.enableSave"></i-Switch>
                             <span class="form-desc">{{$t('是否显示暂存按钮')}}</span>
                        </div>
                    </FormItem>

                    <FormItem :label="$t('允许终止流程')">
                        <div>
                            <i-Switch v-model="value.enableTerminal"></i-Switch>
                             <span class="form-desc">{{$t('是否显示终止流程按钮')}}</span>
                        </div>
                        <div v-if="value.enableTerminal">           
                            <Input style="width:100px" v-model="value.terminalButtonText"/>
                            <span class="form-desc">{{$t('终止按钮显示的文本')}}</span>
                        </div>
                    </FormItem>

                    <FormItem :label="$t('允许转交')">
                        <div>
                            <i-Switch v-model="value.enableTransferTo"></i-Switch>
                            <span class="form-desc">{{$t('允许将负责人转交给他人')}}</span>
                        </div>
                    </FormItem>

                    <FormItem :label="$t('流转规则')">
                        <Select transfer v-model="value.forwardRule">
                            <Option value="any">{{$t('任意一个负责人通过后流转到下一节点')}}</Option>
                            <Option value="all">{{$t('所有负责人通过后流转到下一节点')}}</Option>
                        </Select>
                    </FormItem>
                </Form>
            </div>
        </TabPane>
        <TabPane :label="$t('负责人')" name="owner">
            <div class="side-box-inner">
                <UserSelectForm v-model="value.owner" :ownerFieldList="ownerFieldList" ></UserSelectForm>
            </div>
        </TabPane>

         <TabPane :label="$t('抄送')" name="cc">
            <div class="side-box-inner">
                <UserSelectForm v-model="value.cc" :ownerFieldList="ownerFieldList" ></UserSelectForm>
            </div>
        </TabPane>
    </Tabs>
</template>
<script>
export default {
    props: ["value",'graph','workflowFormFieldList'],
    data() {
        return {
            ownerFieldList:[],
            visibleSelectAll:false,
            editableSelectAll:false
        };
    },
    watch:{
        value(val){
            this.setupFieldList();
        },
        visibleSelectAll(val){
            this.value.fieldList.forEach(item=>{
                item.visible=val;
                this.changeItemVisible(item);
            })
        },
        editableSelectAll(val){
            this.value.fieldList.forEach(item=>{
                item.editable=val;
            })
        },
    },
    mounted(){
        this.setupFieldList();
    },
    methods: {
        //
        setupFieldList(){
            var allValueFields={}
            for(var i=0;i<this.value.fieldList.length;i++){
                var t=this.value.fieldList[i];
                allValueFields[t.id]=t;
            }
            this.ownerFieldList=[];
            var computedFieldList=[];
            for(var i=0;i<this.workflowFormFieldList.length;i++){
                var wf=this.workflowFormFieldList[i];
                var newField={
                    id:wf.id,
                    name:wf.name,
                    type:wf.type,
                    order:i,
                    editable:false,
                    visible:false
                }
                computedFieldList.push(newField);
                var vf=allValueFields[wf.id];
                if(vf){
                   newField.visible=vf.visible;
                   newField.editable=vf.editable;
                }
                //
                if(wf.type=='user-select'||wf.type=='department-select'||wf.type=='role-company-select'){
                    this.ownerFieldList.push(wf);
                }
            } 
            //
            var t=this.value.fieldList.length;
            this.value.fieldList.splice(0,t);
            computedFieldList.forEach(item=>{
                this.value.fieldList.push(item);
            })
        },      
        changeItemVisible(item){
            if(!item.visible){
                item.editable=false;
            }
        }
    }
};
</script>