<style scoped>
.dialog-wrap{
    height:calc(100vh - 48px);
    display: flex;
    flex-direction: column;
    overflow: hidden;
    border-radius: 3px;
}
.dialog-form-content{
    flex:1;
    overflow: auto;
}
.form-number{
    font-size:16px;
    font-weight: bold;
    color:#666;
    position:absolute;
    top:15px;
    left:10px;
    display: flex;
    align-items: center;
}
.top-opt-btn{
    padding-top:2px;
    padding-bottom:2px;
}
.form-item-box{
    padding-bottom:40px;
    border-top:1px solid #eee;
}
.opt{
    display: flex;
    align-items:center;
    position:absolute;
    top:-26px;
    left:0px;
    width:750px;
}
.opt-gap{
    flex:1;
}
.mr{
    margin-right: 5px;
}
.ml{
    margin-left: 5px;
}

.form-dialog-header{
    position: relative;
    height:48px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #F7F7F7;
    border-bottom: 1px solid #ccc;
    padding: 0 30px;
}
.form-dialog-header-color{
    width:10px;
    height:10px;
    border-radius: 50%;
    display: inline-block;
    margin-right:5px;
}


.time-range-box{
    font-size: 12px;
    color: #999;
    padding: 5px 30px;
}
</style>


<i18n>
    {
        "en": {
    
        },
        "zh_CN": {
            "提交成功":"提交成功",
            "保存成功":"保存成功"
        }
    }
</i18n>

 <template>
    <Modal
        ref="dialog"
        v-model="showDialog"
        :closable="true"
        :mask-closable="false"
        :loading="false"
        width="750"
        class="workflow-dialog"
        :footer-hide="true" >
        <div  class="dialog-wrap " :class="{'blur':loading}">
            <div class="opt">
                <Button  @click="save(false)" class="mr top-opt-btn" icon="ios-cloud-upload-outline" type="default">暂存</Button>
                <Button  @click="save(true)" class="mr top-opt-btn" icon="md-arrow-round-forward" type="primary">提交</Button>
            </div>

            <div class="form-dialog-header" >
                <div v-if="define" style="font-size:16px;" class="text-no-wrap">
                    {{define.name}}
                </div>
            </div>

            <div v-if="form.id" class="dialog-form-content scrollbox">
                <Row>
                    <Col span="12">
                        <div class="time-range-box" v-if="define.startTime||define.endTime">
                        有效期：{{define.startTime|fmtDateTime}} ~  {{define.endTime|fmtDateTime}}
                        </div>&nbsp;
                    </Col>
                    <Col span="12" style="text-align:right">
                        
                        <div class="time-range-box" v-if="instance&&instance.status==2">
                        {{instance.updateTime|fmtDateTime}}提交
                        </div>
                        &nbsp;
                    </Col>
                </Row>
               
                <SurveysForm :id="'form-'+form.id" ref="form" v-if="form.id" :form="form" :value="formFieldValue" :formFieldList="formFieldList"></SurveysForm>
            </div>
        </div>
    </Modal>
</template>


<script>
export default {
    mixins: [componentMixin],
    data() {
        return {
            form:{},
            define:null,
            instance:{},
            formFieldValue:{},
            loading:false,
            formFieldList:[],
            editable:true
        };
    },
    methods: {
        pageLoad() {
            this.loadData();
        },
        loadData() {
            this.loading=true;
            app.invoke('SurveysAction.getSurveysSubmitInfo',[app.token,this.args.uuid],(info)=>{
                this.form=info.formDefine;
                this.define=info.surveysDefine;
                this.instance=info.instance;
                if(info.instance&&info.instance.status==2){
                    this.editable=false;
                }
                if(this.define.submitEdit){
                    this.editable=true;
                }
                this.setupValue();
                this.loading=false;
            })
        },
        setupValue(){
            var formFieldValue={};
            if(this.instance&&this.instance.formData){
                formFieldValue=JSON.parse(this.instance.formData);
            }
            //设置permission.formFieldList的字段类型 columnList
            var formFieldList=JSON.parse(this.form.fieldList);
            this.formFieldList=formFieldList;
           
            //设置系统字段和其它字段的默认值
            formFieldList.forEach(f=>{
                f.editable=this.editable;
                if(f.type=='date'){
                    var dateValue=formFieldValue[f.id];
                    if(dateValue!=null){
                        var time=new Date(dateValue);
                        formFieldValue[f.id]=time;
                    }
                }
               
                if(f.type=='text-number'){
                    var oldValue=formFieldValue[f.id]
                    if(oldValue==null){
                        if(f.minValue){
                            formFieldValue[f.id]=f.minValue;
                        }
                        if(formFieldValue[f.id]==null){
                            formFieldValue[f.id]=1;
                        }
                    }

                }
                if(f.type=='select'||f.type=='radio'){//从optionList选择默认选中的值
                    if(formFieldValue[f.id]==null){
                        var selectedOption=null;
                        f.optionList.forEach(option=>{
                            if(option.checked){
                                selectedOption=option.value;
                            }
                        })
                        if(selectedOption){
                            formFieldValue[f.id]=selectedOption;
                        }
                    }
                }
                if(f.type=='checkbox'){
                    if(formFieldValue[f.id]==null){
                        var selectedOptionList=[];
                            f.optionList.forEach(option=>{
                                if(option.checked){
                                    selectedOptionList.push(option.value);
                                }
                            })
                        if(selectedOptionList.length>0){
                            formFieldValue[f.id]=selectedOptionList;
                        }
                    }
                }
            })
            //
            this.formFieldValue=formFieldValue;
        },
        save(submit){
             if(submit){
                 this.$refs.form.validateForm(()=>{
                    app.confirm('确定要提交吗？',()=>{
                         this.confirmSave(submit);
                    })
                 })
            }else{
                this.confirmSave(submit);
            }
        },
        confirmSave(submit){
            var submitValue={};
            this.formFieldList.forEach(item=>{
                submitValue[item.id]=this.formFieldValue[item.id];
            })
            var formData=JSON.stringify(submitValue);
            var info={
                surveysDefineId:this.define.id,
                submit:submit,
                formData:formData
            }
            app.invoke('SurveysAction.submitSurveysInstance',[app.token,info],(info)=>{
               if(submit){
                   app.toast(this.$t('提交成功'));
                   this.showDialog=false;
                   app.postMessage('surveys.edit')
               }else{
                   app.toast(this.$t('保存成功'));
               }
            })
        }
    }
};
</script>
