
<style scoped>
    .report-content-row{
        padding:5px 0;
    }
</style>

<i18n>
    {
    "en": {
    "汇报内容":"Report Content",
    "请输入":"Type content",
    "详细描述":"Description",
    "保存":"Save"
    },
    "zh_CN": {
    "汇报内容":"汇报内容",
    "请输入":"请输入",
    "详细描述":"详细描述",
    "保存":"保存"
    }
    }
</i18n>


 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('汇报内容')" width="700" >

    <Form  ref="form" :rules="formRule" :model="formItem" label-position="top" style="min-height:600px;padding:15px">

        <FormItem :label="$t('汇报内容')" prop="title">
                <Input :placeholder="$t('请输入')" v-model.trim="formItem.title" ></Input>
        </FormItem>

        <FormItem :label="$t('详细描述')">
             <RichtextEditor ref="editor" v-model="formItem.content"></RichtextEditor>
        </FormItem>


    </Form>

    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right">
                <Button @click="confirm" type="default" size="large" >{{$t('保存')}}</Button>
            </Col>
        </Row>

    </div>

    </Modal>
</template>

<script>
    export default {
        mixins: [componentMixin],

        data () {
            return {
                formItem:{
                    id:0,
                    title:null,
                    content:null,
                },
                formRule:{
                    title:[vd.req,vd.name],
                },
            }
        },
        methods: {
            pageLoad(){
                if(this.args.item){
                    this.formItem=this.args.item;
                }
            },
            confirm(){
                this.formItem.content=this.$refs.editor.getValue();
                this.$refs.form.validate((r)=>{if(r)this.confirmForm()});
            },
            confirmForm(){
                if(this.args.callback){
                    this.args.callback(this.formItem)
                }
                this.showDialog=false;
            }
        }
    }
</script>
