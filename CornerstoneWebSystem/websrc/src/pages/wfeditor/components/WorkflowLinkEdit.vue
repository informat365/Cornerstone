<style scoped>
.form-desc{
    font-size:12px;
    color:#666;
    margin-bottom:5px;
}
.side-box-inner{
    padding:10px;
}
.side-box-note{
    margin: 5px 0;
    color:#666;
}
.rule-item{
    display: flex;
    align-items: center;
    margin-bottom: 5px;
}
.rule-item .field{
    flex:1;
}
.rule-item .op{
    width:90px;
    margin-left:5px;
}
.rule-item .value{
    width:100px;
    margin-left: 5px;
}
.rule-item .del{
    width:30px;
}
.top-note{
    padding:10px;
    color:#666;
    font-weight: bold;
}
.del-btn{
   color:#FF604B;
   cursor: pointer;
}
</style>
<i18n>
{
	"en": {
        "可使用组合条件或者表达式设置流程的跳转条件": "可使用组合条件或者表达式设置流程的跳转条件。如果组合条件或者表达式中描述的规则符合，流程将使用此条件跳转到下一节点。",
		"组合条件": "组合条件",
		"类型": "类型",
		"满足所有条件": "满足所有条件",
		"满足任意一个条件": "满足任意一个条件",
		"条件": "条件",
		"等于": "等于",
		"不等于": "不等于",
		"包含": "包含",
		"大于": "大于",
		"小于": "小于",
		"大于等于": "大于等于",
		"小于等于": "小于等于",
		"添加": "添加",
		"表达式": "表达式",
		"表达式可使用表单中字段进行逻辑和数学运算": "表达式可使用表单中字段进行逻辑和数学运算",
		"条件表达式": "条件表达式"
    },
	"zh_CN": {
		"可使用组合条件或者表达式设置流程的跳转条件": "可使用组合条件或者表达式设置流程的跳转条件。如果组合条件或者表达式中描述的规则符合，流程将使用此条件跳转到下一节点。",
		"组合条件": "组合条件",
		"类型": "类型",
		"满足所有条件": "满足所有条件",
		"满足任意一个条件": "满足任意一个条件",
		"条件": "条件",
		"等于": "等于",
		"不等于": "不等于",
		"包含": "包含",
		"大于": "大于",
		"小于": "小于",
		"大于等于": "大于等于",
		"小于等于": "小于等于",
		"添加": "添加",
		"表达式": "表达式",
		"表达式可使用表单中字段进行逻辑和数学运算": "表达式可使用表单中字段进行逻辑和数学运算",
		"条件表达式": "条件表达式"
	}
}
</i18n>
<template>
<div> 
    <div class="top-note">
        {{$t('可使用组合条件或者表达式设置流程的跳转条件')}}
    </div>
    <Tabs :animated="false" value="op" >
        <TabPane :label="$t('组合条件')" name="op">
        <div class="side-box-inner">
                <Form label-position="top">
                    <FormItem :label="$t('类型')">
                        <Select transfer v-model="value.ruleType">
                            <Option value="and">{{$t('满足所有条件')}}</Option>
                            <Option value="or">{{$t('满足任意一个条件')}}</Option>
                        </Select>
                    </FormItem>
                    <FormItem  :label="$t('条件')">
                        <div v-for="(rule,ruleIdx) in value.ruleList" :key="'r'+ruleIdx" class="rule-item">
                             <Select @on-change="fieldChange(rule)" class="field" transfer v-model="rule.field">
                                <Option v-for="item in fieldList" :key="ruleIdx+'-'+item.id" :value="item.id">{{item.name}}</Option>
                            </Select>
                            <Select class="op" transfer v-model="rule.op">
                                <Option value="=">{{$t('等于')}}</Option>
                                <Option value="!=">{{$t('不等于')}}</Option>
                                <Option value="like">{{$t('包含')}}</Option>
                                <Option v-if="rule.type=='text-number'" value=">">{{$t('大于')}}</Option>
                                <Option v-if="rule.type=='text-number'" value="<">{{$t('小于')}}</Option>
                                <Option v-if="rule.type=='text-number'" value=">=">{{$t('大于等于')}}</Option>
                                <Option v-if="rule.type=='text-number'" value="<=">{{$t('小于等于')}}</Option>
                               
                               
                            </Select>
                            <Input v-model="rule.value" class="value"/>
                            <div class="del">
                                <Icon @click.native="deleteRule(ruleIdx)" class="del-btn" size="20" type="md-trash"></Icon>
                            </div>
                        </div>

                         <Button
                            icon="ios-add"
                            type="dashed"
                            size="small"
                            @click="addRule()"
                        >{{$t('添加')}}</Button>
                    </FormItem>
                </Form>
            </div>
        </TabPane>
        <TabPane :label="$t('表达式')" name="expression">
            <div class="side-box-inner">
                <div class="side-box-note">{{$t('表达式可使用表单中字段进行逻辑和数学运算')}}</div>
                <Input v-model.trim="value.express" type="textarea" :rows="3" :placeholder="$t('条件表达式')"/>
            </div>
        </TabPane>

    </Tabs>
</div>
</template>
<script>
export default {
    props: ["value",'graph','workflowFormFieldList'],
    data() {
        return {
            fieldList:[],
        };
    },
    mounted(){
        this.setupValue();
    },
    methods: {
        setupValue(){
            this.setupFieldList();
        },
        //
        setupFieldList(){
           for(var i=0;i<this.workflowFormFieldList.length;i++){
                var wf=this.workflowFormFieldList[i];
                if(wf.type.indexOf('static')==-1
                    &&wf.type!='table'
                    &&wf.type!='text-rich'
                    &&wf.type!='attachment'
                ){
                    this.fieldList.push(wf);
                }
            } 
            //
            if(this.value.ruleList==null){
                this.value.ruleList=[];
            }
        },
        fieldChange(rule){
            var f=null;
            for(var i=0;i<this.fieldList.length;i++){
                var field=this.fieldList[i];
                if(field.id==rule.field){
                    f=field;
                    break;
                }
            }
            if(f!=null){
                rule.type=f.type;
            }
        },
        addRule(){
            this.value.ruleList.push({
                field:null,
                op:'=',
                value:null,
                type:null
            })
        },
        deleteRule(idx){
            this.value.ruleList.splice(idx,1)
        }
    }
};
</script>