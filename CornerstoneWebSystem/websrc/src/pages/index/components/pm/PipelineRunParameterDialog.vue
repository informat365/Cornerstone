<style scoped>
   
</style>
<i18n>
{
    "en": {
        "输入参数": "Parameter",
        "取消":"Cancel",
        "确定":"OK"
    },
    "zh_CN": {
        "输入参数": "输入参数",
        "取消":"取消",
        "确定":"确定"
    }
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="false" :mask-closable="false"
        :loading="false" @on-visible-change="visibleChange" :title="$t('输入参数')" width="500"  @on-ok="confirm">

        <div style="padding:20px;font-size:16px;font-weight:bold">
                {{parameter.message}}
        </div>

        <Form   ref="form" :rules="formRule" :model="formItem" label-position="top" style="padding:15px;margin-bottom:200px">
            <FormItem v-if="parameter.type=='string'" :label="parameter.name" prop="stringValue">
                <Input v-model.trim="formItem.stringValue"></Input>
            </FormItem>
            <FormItem v-if="parameter.type=='array'" :label="parameter.name" prop="listValue">
                <Select multiple clearable filterable v-model="formItem.listValue">
                    <Option v-for="item in parameter.list" :value="item" :key="item">{{ item }}</Option>
                </Select>
            </FormItem>
        </Form>
    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> 
                <Button v-if="parameter.type=='bool'" @click="cancel" type="text" size="default" >{{$t('取消')}}</Button>
                <Button @click="confirm" type="primary" size="large" >{{$t('确定')}}</Button>
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
                    stringValue:null,
                    booleanValue:null,
                    listValue:[],
                },
                formRule:{
                    stringValue:[vd.req],
                    listValue:[vd.req],
                },
                parameter:{},
                pipeline:{}
            }
        },
        
        methods: {
            pageLoad(){
                this.pipeline=this.args.pipeline;
                var runlog=this.pipeline.runLog;
                for(var i=0;i<this.pipeline.pipelineDefine.stages.length;i++){
                    var stage=this.pipeline.pipelineDefine.stages[i];
                    if(stage.name==runlog.stage){
                       
                        for(var j=0;j<stage.parameters.length;j++){
                            var p=stage.parameters[j];
                            if(p.name==runlog.parameter){
                                this.parameter=p;
                            }
                        }
                    }
                }
            },
            visibleChange(val){
                if(val==false){
                    app.postMessage('pipeline.parameter.hide')
                }
            },
            confirm(){
                if(this.parameter.type=='bool'){
                    this.confirmForm();
                    return;
                }
                this.$refs.form.validate((r)=>{if(r)this.confirmForm()});
            },
            cancel(){ 
                this.formItem.booleanValue=false;
                app.invoke('BizAction.setProjectPipelineRunLogParameter'
                    ,[app.token,this.pipeline.runId,this.formItem],(info)=>{
                    this.showDialog=false;
                    app.postMessage('pipeline.parameter.edit')
                })
            },
            confirmForm(){
                if(this.parameter.type=='bool'){
                    this.formItem.booleanValue=true;             
                }
                app.invoke('BizAction.setProjectPipelineRunLogParameter'
                    ,[app.token,this.pipeline.runId,this.formItem],(info)=>{
                    this.showDialog=false;
                    app.postMessage('pipeline.parameter.edit')
                })
            }
        }
    }
</script>