<style scoped>
</style>
<i18n>
{
    "en": {
        "正在执行": "Running",
        "跳过":"Skip",
        "取消":"Cancel"
    },
    "zh_CN": {
        "正在执行": "正在执行",
        "跳过":"跳过",
        "取消":"取消"
    }
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="false" :mask-closable="false"
        :loading="false" :title="args.title" width="500">
        
        <div style="padding:10px;font-size:14px;height:50px">
            {{$t('正在执行')}} {{runCount}}/{{totalCount}} 
            <template v-if="currentIndex>0">{{itemList[currentIndex].name}}</template>
        </div>
        <div>
            <Progress :percent="parseInt(runCount*100/totalCount)" status="active" />
        </div>
        <div slot="footer">
            <Row>
                <Col span="24" style="text-align:right"> 
                    <Button v-if="pauseRun" @click="resume" type="default" size="large" > {{$t('跳过')}}</Button>
                    <Button @click="cancelRun" type="default" size="large" > {{$t('取消')}}</Button>
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
               message:null,
               totalCount:1,
               runCount:0,
               currentIndex:-1,
               pauseRun:false,
               isCancel:false,
               errorMessage:null,
               itemList:[],
            }
        },
        methods: {
            pageLoad(){
                this.itemList=this.args.itemList;
                this.totalCount=this.args.itemList.length;
                this.currentIndex=-1;
                this.runNextItem();
            },
            resume(){
                this.pauseRun=false;
                this.runNextItem();
            },
            cancelRun(){
                this.isCancel=true;
                this.runNextItem();
            },
            runNextItem(){
                if(this.isCancel){
                    this.args.finishCallback();
                    this.showDialog=false;
                    return;
                }
                this.runCount++;
                this.currentIndex++;
                if(this.currentIndex>this.args.itemList.length-1){
                    this.showDialog=false;
                    this.args.finishCallback();
                    return;
                }
                var item=this.args.itemList[this.currentIndex];
                this.args.runCallback(item,this.runNextItem,this.errorCallback)
            },
            errorCallback(message){
                this.errorMessage=message;
                this.pauseRun=true;
            }
        }
    }
</script>