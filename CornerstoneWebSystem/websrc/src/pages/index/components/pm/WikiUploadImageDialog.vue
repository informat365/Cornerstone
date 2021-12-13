<style scoped>

</style>

<i18n>
    {
    "en": {
        "上传图片": "Upload Image",
        "确定": "OK"
    },
    "zh_CN": {
        "上传图片": "上传图片",
        "确定": "确定"
    }
    }
</i18n>

 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('上传图片')" width="500"  @on-ok="confirm">

        <div  style="text-align:center;padding:20px">
            <FileUploadView :multiple="true" :format="['jpg','jpeg','png','gif','svg','tiff']" @change="uploadSuccess"></FileUploadView>
        </div>

    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{$t('确定')}}</Button></Col>
        </Row>

    </div>

    </Modal>
</template>


<script>
    export default {
        mixins: [componentMixin],
        data () {
            return {
                uuidList:[],
            }
        },
        methods: {
            pageLoad(){

            },
            uploadSuccess(uuid){
                this.uuidList.push(uuid);
            },
            confirm(){
                if(this.args.callback){
                    this.args.callback(this.uuidList);
                }
                this.showDialog=false;
            }
        }
    }
</script>
