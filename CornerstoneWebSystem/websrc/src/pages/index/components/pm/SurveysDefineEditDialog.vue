<style scoped>
    .form-desc{
        color:#999;
        font-size:12px;
    }
</style>
<i18n>
{
    "en": {
        
        "保存":"Save",
        "创建":"Create",
        "保存成功":"Success",
        "创建成功":"Success"
    },
    "zh_CN": {
        
        "保存":"保存",
        "创建":"创建",
        "保存成功":"保存成功",
        "创建成功":"创建成功",
        "输入标题":"输入标题",
        "标题":"标题",
        "创建问卷调查":"创建问卷调查",
        "允许匿名":"允许匿名",
        "允许重新编辑":"允许重新编辑",
        "开始时间":"开始时间",
        "结束时间":"结束时间",
        "未设置":"未设置",
        "编辑后生成新的记录":"编辑后生成新的记录"
    }
}
</i18n>  
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('创建问卷调查')" width="700"  @on-ok="confirm">

    <Form  @submit.native.prevent ref="form" :rules="formRule" :model="formItem"  label-position="top" style="height:600px;padding:25px">
        <FormItem :label="$t('标题')" prop="name">
            <Input v-model.trim="formItem.name" :placeholder="$t('输入标题')"></Input>
        </FormItem>

        <FormItem :label="$t('允许匿名')" >
             <i-Switch v-model="formItem.anonymous"></i-Switch>
             <div class="form-desc">允许匿名的表单不需要登录就可以提交</div>
        </FormItem>

         <FormItem :label="$t('允许重新编辑')" >
             <i-Switch v-model="formItem.submitEdit"></i-Switch>
             <div class="form-desc">提交后允许重新编辑修改</div>
        </FormItem>


        <FormItem :label="$t('开始时间')" prop="startTime">
             <ExDatePicker type="datetime" v-model="formItem.startTime" :placeholder="$t('未设置')" ></ExDatePicker>
        </FormItem>

        <FormItem :label="$t('结束时间')" prop="endTime">
            <ExDatePicker type="datetime" v-model="formItem.endTime" :placeholder="$t('未设置')" ></ExDatePicker>
            <div class="form-desc">设置了开始时间或截止时间的问卷调查只允许在这个时间段内提交</div>
        </FormItem>
        

    </Form>

    
    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"><Button @click="confirm" type="default" size="large" >{{formItem.id>0?$t('保存'):$t('创建')}}</Button></Col>
        </Row>
           
    </div>

    </Modal>
</template>


<script>
    export default {
        mixins: [componentMixin],
        data () {
            return {
                continueCreate:false,
                formItem:{
                    id:0,
                    name:null,
                    startTime:null,
                    endTime:null,
                    anonymous:false,
                    submitEdit:false
                },
                formRule:{
                    name:[vd.req,vd.name],
                }
            }
        },
        methods: {
            pageLoad(){
               
            },
            confirm(){
                this.$refs.form.validate((r)=>{
                    if(r)this.confirmForm()
                });
            },
            confirmForm(){
                var action='SurveysAction.addSurveysDefine';
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.toast(this.formItem.name+" "+this.$t('创建成功'))
                    app.postMessage('surveys.edit')
                    this.showDialog=false;
                    window.open('/surveyseditor.html#/'+info+'/info')
			    })
            }
        }
    }
</script>  