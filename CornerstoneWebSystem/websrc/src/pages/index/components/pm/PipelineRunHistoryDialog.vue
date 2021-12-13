<style scoped>
.page{
    padding:10px;
}
.pipeline-name{
    color:#444;
    font-weight: bold;
    font-size: 15px;
}
.pipeline-desc{
    font-size:13px;
    color:#666;
    margin-top:10px;
}
.create-user-label{
    color:#999999;
    font-size:12px;
}
.run-box{
    font-size:12px;
    font-family: 'Courier New', Courier, monospace;
    white-space: pre;
    margin-top:5px;
    margin-bottom:5px;
    color:#fff;
  
    font-weight: 500;
}
.run-box-type-1{
    color:#fff;
     font-weight: bold;
}
.run-box-type-2{
    color:red;
     font-weight: bold;
}
.run-box-type-3{
    color:#98F568;
    font-weight: bold;
}

.run-log-box{
    height: calc(100vh - 190px);
    border:1px solid #ccc;
    overflow: auto;
    padding:10px;
    background-color: #000;
}
.run-log-stage{
    color:#fff;
    display: inline-block;
    margin-right:5px;
}
.run-log-time{
    color:#fff;
    margin-left:5px;
}
.spin-icon-load{
    animation: ani-demo-spin 3s linear infinite;
}
.run-box-archive{
    font-weight: bold;
    border-top:1px solid #fff;
    border-bottom:1px solid #fff;
    padding-top:15px;
    padding-bottom:15px;
    margin-bottom:15px;
}

.qrcode-box canvas{
    position: relative;
    margin-top:10px;
}

</style>
<i18n>
{
    "en": {
        "Pipeline执行历史": "Pipeline History",
        "运行记录":"Log",
        "输出":"Log",
        "交付物":"Artifact",
        "版本":"Version",
        "大小":"Size",
        "MD5":"MD5",
        "下载地址":"Link",
        "二维码":"QRCode",
        "历史记录":"History",
        "用时":"Time",
        "查看日志":"Log",
        "统计":"Stat",
        "时间":"Time",
        "脚本":"Script"
    },
    "zh_CN": {
        "Pipeline执行历史": "Pipeline执行历史",
        "运行记录":"运行记录",
        "输出":"输出",
        "交付物":"交付物",
        "版本":"版本",
        "大小":"大小",
        "MD5":"MD5",
        "下载地址":"下载地址",
        "二维码":"二维码",
        "历史记录":"历史记录",
        "用时":"用时",
        "查看日志":"查看日志",
        "统计":"统计",
        "时间":"时间",
        "脚本":"脚本"
    }
}
</i18n>
 <template>
    <Modal
        ref="dialog" class="full-modal"  v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('Pipeline执行历史')" width="1200" :footer-hide="true">
        <div class="page" >
             <Tabs value="run" :animated="false" style="margin-top:15px">
                <TabPane :label="$t('运行记录')" name="run">
                     <div class="run-log-box scrollbox">
                         <div class="run-log-item" v-for="log in logList" :key="'log_'+log.id">
                            <div v-if="log.type!=4" class="run-box" :class="'run-box-type-'+log.type">
                                <div class="run-log-stage">[{{log.step}}<span class="run-log-time">{{log.createTime|fmtDeltaTime}}]</span></div>
                                    <div v-html="ansiup(log.message)"></div>
                             </div>
                            <div v-if="log.type==4" class="run-box run-box-archive">
                                <div>{{$t('交付物')}}：{{log.archiveObject.name}} </div>
                                <div>{{$t('版本')}}：{{log.archiveObject.version}} </div>
                                <div>{{$t('大小')}}：{{log.archiveObject.size|fmtBytes}} </div>
                                <div>MD5：{{log.archiveObject.md5}} </div>
                                <div>{{$t('下载地址')}}：<a href="#" @click="downloadArchive(log.archiveObject)">{{log.archiveObject.url}}</a></div>
                                <div class="qrcode-box">{{$t('二维码')}}： <qrcode :value="log.archiveObject.url" :options="{ size: 200 }"></qrcode></div>
                            </div> 
                            <div v-if="log.type==5" class="run-box run-box-archive">
                                <div class="qrcode-box">{{$t('二维码')}}： <qrcode :value="log.message" :options="{ size: 200 }"></qrcode></div>
                            </div>
                        </div>                    
                           
                       
                    </div>
                </TabPane>
                 <TabPane  :label="$t('脚本')" name="script">
                     <pre>{{formItem.script}}</pre>
                </TabPane>
            </Tabs>
            

        </div>
    </Modal>
</template>


<script>
import AnsiUp from 'ansi_up';
import qrcode from '@xkeshi/vue-qrcode';
    export default {
        mixins: [componentMixin],
        components: {
            qrcode
        },
        data () {
            return {
              formItem:{
                  script:''
              },
              logList:[],
            }
        },
        created(){
            window.ansi_up =  new AnsiUp;
        },
        methods: {
            pageLoad(){
                this.loadData();
                this.loadLog();
            },
            loadData(){
                app.invoke('BizAction.getProjectPipelineRunLogInfoById',[app.token,this.args.runId],(info)=>{
                    this.formItem=info;
                })
            },
            ansiup(v){
                return ansi_up.ansi_to_html(v);
            },
            downloadArchive(item){
                window.open(item.url)
            },
            loadLog(){
                app.invoke('BizAction.getProjectPipelineRunDetailLogList',[app.token,
                    this.args.runId,0],(list)=>{
                    this.logList=[];
                    for(var i=0;i<list.length;i++){
                        var log=list[i];
                        if(log.type==4){
                            log.archiveObject=JSON.parse(log.message);
                            log.archiveObject.url=app.getHost()+'/p/webapi/download_artifact/'+log.archiveObject.uuid;
                        }
                        this.logList.push(log)
                    }
                })
            },
            confirm:function(){
                this.showDialog=false;
            }
        }
    }
</script>