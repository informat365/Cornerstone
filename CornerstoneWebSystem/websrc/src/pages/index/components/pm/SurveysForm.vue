<style scoped>
.workflow-form{
    padding:15px;
    padding-right:5px;
}
.form-title{
    font-size:18px;
    font-weight: bold;
    color:#333;
    margin-top:10px;
    text-align: center;
}
.form-subtitle{
    font-size:13px;
    color:#666;
    margin-top:10px;
    text-align: center;
}
.form-item-box{
    padding:20px;
    display: flex;
    flex-wrap: wrap;
}

.form-item-holder-width-25{
    width:25%;
}
.form-item-holder-width-50{
    width:50%;
}
.form-item-holder-width-75{
    width:75%;
}
.form-item-holder-width-100{
    width:100%;
}
.form-item-holder{
    display: inline-block;
    margin-bottom:10px;
    padding-right:15px;
}
.form-item-title{
    font-size:14px;
    font-weight: bold;
    color:#666;
}
.form-item-desc{
    font-size:12px;
    color:#999;
    margin-left:5px;
    display: inline-block;
}
.form-item-holder-static-groupitem{
    font-weight: bold;
    color:#333;
    font-size:16px;
    margin: 15px 0;
    padding-left:7px;
    border-bottom:1px solid #eee;
    position: relative;
    padding-bottom:5px;
}
.form-item-holder-static-groupitem::before{
    height:15px;
    width:4px;
    display: block;
    background:  rgba(35,145,255,1);
    content: '';
    position: absolute;
    top:4px;
    left:0;
}
.form-item-holder-static-labelitem{
    font-size:13px;
    color:#333;
}
.required{
    color:#C7000B;
    margin:0 3px;
}
.form-item-holder-static-alert-success{
    border: 1px solid #8ce6b0;
    background-color: #edfff3;
    font-size:13px;
    padding:10px;
}
.form-item-holder-static-alert-warning{
    border: 1px solid #ffd77a;
    background-color: #fff9e6;
    font-size:13px;
    padding:10px;
}
.form-item-holder-static-alert-error{
    border: 1px solid #ffb08f;
    background-color: #ffefe6;
    font-size:13px;
    padding:10px;
}
.form-item-holder-static-alert-info{
    border: 1px solid #abdcff;
    background-color: #f0faff;
    font-size:13px;
    padding:10px;
}
.form-form{
    margin-top:7px;
}

.ivu-form-item-error .workflow-form-richtext-box{
    border:1px solid #ed4014;
}
.ivu-form-item-error .ivu-radio-group{
    border:1px solid #ed4014;
}
.ivu-form-item-error .ivu-checkbox-group{
    border:1px solid #ed4014;
}
</style>


<i18n>
    {
    "en": {
        "请选择": "none",
        "未设置": "none",
        "格式不正确": "format error",
        "长度为个字": "length {0}~{1}",
        "邮箱格式不正确": "bad email",
        "url格式不正确": "bad url",
        "手机号格式不正确": "bad mobile",
        "身份证号码格式不正确": "bad idcard",
        "取值范围": "range value",
        "表单中有的字段没有按照要求填写，请检查": "Some fields in the form are not filled in as required. Please check them."
    },
    "zh_CN": {
        "请选择": "请选择",
        "未设置": "未设置",
        "格式不正确": "格式不正确",
        "长度为个字": "长度为{0}~{1}个字",
        "邮箱格式不正确": "邮箱格式不正确",
        "url格式不正确": "url格式不正确",
        "手机号格式不正确": "手机号格式不正确",
        "身份证号码格式不正确": "身份证号码格式不正确",
        "取值范围": "取值范围",
        "表单中有的字段没有按照要求填写，请检查": "表单中有的字段没有按照要求填写，请检查"
        }
    }
</i18n>

 <template>
    <div class="workflow-form">
        <div class="form-title">{{form.title}}</div>

        <Form class="form-form" ref="form" :model="value" :rules="formRule">
            <div class="form-item-box">
                <template v-for="item in fieldList">
                    <div class="form-item-holder" :class="'form-item-holder-width-'+item.width" :key="item.id">
                        <FormItem v-if="item.type.indexOf('static')==-1" :prop="item.id">
                            <div class="form-item-title">{{item.name}} <span class="required" v-if="item.required&&item.editable">*</span>
                                <span class="form-item-desc">{{item.remark}}</span>
                            </div>
                            <div class="form-item-value">

                                <Input :disabled="!item.editable"  v-if="item.type=='text-single'" v-model="value[item.id]" ></Input>
                                <Input :disabled="!item.editable"  v-if="item.type=='text-area'" type="textarea" :autosize="{minRows:3}" v-model="value[item.id]" ></Input>
                                <InputNumber :disabled="!item.editable"  v-if="item.type=='text-number'"  v-model="value[item.id]" ></InputNumber>

                                <template v-if="item.type=='date'">
                                    <DatePicker v-if="item.dateFormat=='datetime'" transfer :disabled="!item.editable"
                                        format="yyyy-MM-dd HH:mm"
                                        :type="item.dateFormat" v-model="value[item.id]"></DatePicker>

                                    <DatePicker v-else transfer :disabled="!item.editable"
                                        :type="item.dateFormat" v-model="value[item.id]"></DatePicker>

                                </template>

                                 <TimePicker transfer :disabled="!item.editable"
                                v-if="item.type=='time'" v-model="value[item.id]"></TimePicker>

                                <Select :disabled="!item.editable" :placeholder="item.editable?$t('请选择'):$t('未设置')"
                                    transfer
                                    :multiple="item.countType=='multiple'" clearable
                                    v-if="item.type=='select'" v-model="value[item.id]">
                                    <Option v-for="(selectOption,selectOptionIdx) in item.optionList" :value="selectOption.value"
                                        :key="item.id+'_o_'+selectOptionIdx">{{ selectOption.value }}</Option>
                                </Select>

                                <RadioGroup :vertical="item.optionLayout=='vertical'" v-if="item.type=='radio'" v-model="value[item.id]">
                                    <Radio  :disabled="!item.editable" v-for="(selectOption,selectOptionIdx) in item.optionList"
                                        :key="item.id+'_o_'+selectOptionIdx"
                                        :label="selectOption.value"></Radio>
                                </RadioGroup>

                                <CheckboxGroup  v-if="item.type=='checkbox'" v-model="value[item.id]">
                                    <template v-for="(selectOption,selectOptionIdx) in item.optionList">
                                        <Checkbox  :disabled="!item.editable" 
                                            :key="item.id+'_o_'+selectOptionIdx"
                                            :label="selectOption.value"></Checkbox>
                                        <br :key="item.id+'_k_'+selectOptionIdx" v-if="item.optionLayout=='vertical'"/>
                                    </template>
                                   
                                </CheckboxGroup>

                               

                                <WorkflowAttachment v-if="item.type=='attachment'"  :disabled="!item.editable"
                                    :countType="item.countType" v-model="value[item.id]" ></WorkflowAttachment>

                            </div>
                        </FormItem>

                        <template v-if="item.type=='static-group'">
                            <div class="form-item-holder-static-groupitem">{{item.name}}</div>
                        </template>
                        <template v-if="item.type=='static-label'">
                            <div class="form-item-holder-static-labelitem">
                                <RichtextLabel :value="item.content"></RichtextLabel>
                            </div>
                        </template>
                         <template v-if="item.type=='static-alert'">
                            <div :class="'form-item-holder-static-alert-'+item.alertType">{{item.remark}}</div>
                        </template>

                    </div>
                 </template>
            </div>
        </Form>
    </div>
</template>


<script>
export default {
    props:['form','value','formFieldList'],
    data() {
        return {
            fieldList:[],
            formRule:{}
        };
    },
    mounted(){
        this.setupValue();
    },
    methods: {
        setupValue(){
            var fList=JSON.parse(this.form.fieldList);
            var allGraphFields={};
            console.log(this.formFieldList)
            this.formFieldList.forEach(item=>{
                allGraphFields[item.id]=item;
            })
            var filteredList=[];
            var formRule={}
            fList.forEach(item=>{
                var gItem=allGraphFields[item.id];
                if(gItem){
                        item.editable=gItem.editable;
                        filteredList.push(item);
                        formRule[item.id]=[];
                        //form rule
                        if(item.editable){
                            if(item.required){
                                formRule[item.id].push({required:true,message:" "})
                            }
                            if(item.type=='text-single'||item.type=='text-area'){
                                formRule[item.id].push({
                                     type: 'string',
                                     min:item.minLength,
                                     max:item.maxLength,
                                     message:this.$t('长度为个字',[item.minLength,item.maxLength])
                                })
                            }
                            if(item.type=='text-single'){
                                if(item.textFormat=='email'){
                                    formRule[item.id].push({
                                        type: 'email',
                                        message:this.$t('邮箱格式不正确'),
                                        trigger:'blur'
                                    })
                                }
                                if(item.textFormat=='url'){
                                    formRule[item.id].push({
                                        type: 'url',
                                        message:this.$t('url格式不正确'),
                                        trigger:'blur'
                                    })
                                }
                                if(item.textFormat=='mobile'){
                                    formRule[item.id].push({
                                        type: 'string',
                                        min:11,
                                        max:11,
                                        message:this.$t('手机号格式不正确'),
                                        trigger:'blur'
                                    })
                                }
                                if(item.textFormat=='idcard'){
                                    formRule[item.id].push({
                                        type: 'string',
                                        pattern:/(^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$)|(^[1-9]\d{5}\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}$)/,
                                        message:this.$t('身份证号码格式不正确'),
                                        trigger:'blur'
                                    })
                                }

                            }
                            if(item.type=='text-number'){
                                formRule[item.id].push({
                                     type: 'number',
                                     min:item.minValue,
                                     max:item.maxValue,
                                     message:this.$t('取值范围')+item.minValue+"~"+item.maxValue+""
                                })
                            }
                        }
                }
            })
            this.formRule=formRule;
            this.fieldList=filteredList
            //console.log(filteredList)
        },
        validateForm(callback){
            //
            this.$refs.form.validate((r)=>{
                if(r){
                    callback();
                }else{
                    app.toast(this.$t('表单中有的字段没有按照要求填写，请检查'))
                }
            });
        }
    }
};
</script>
