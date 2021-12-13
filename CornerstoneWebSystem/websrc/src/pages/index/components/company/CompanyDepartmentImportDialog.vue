<style scoped>
</style>
<i18n>
{
    "en": {
   
    },
    "zh_CN": {
        "导入组织架构":"导入组织架构",
        "导入":"导入",
        "请上传包含有":"请下载导入模板，填写好模板后上传导入"
    }
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('导入组织架构')" width="700" >
        <div  style="text-align:center">
            <div style="font-size:16px;color:#999;font-weight:bold;margin-bottom:20px;margin-top:20px">
                {{$t('请上传包含有')}}
            </div>
            <FileUploadView :format="['xlsx']" @change="uploadSuccess"></FileUploadView>
        </div>
    <div slot="footer">
        <Row>
            <Col span="12" style="text-align:left;padding-top:5px"><a href="/template/org_template.xlsx">下载模板</a></Col>
            <Col span="12" style="text-align:right"> 
                <Button :disabled="fileUUID==null" @click="confirm" type="default" size="large" >{{$t('导入')}}</Button>
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
                fileUUID:null,
            }
        },
        methods: {
            pageLoad(){
               
            },
            confirm(){
                app.invoke('BizAction.importDepartmentFromExcel',[app.token,this.fileUUID],(info)=>{
                    app.toast('导入成功');
                    this.showDialog=false;
                    app.postMessage('department.edit')
                })
            },
            uploadSuccess(uuid){
                app.toast('上传成功');
                this.fileUUID=uuid.uuid;
            }
        }
    }
</script>
