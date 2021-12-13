<style scoped>
.page{
    padding:10px;
}
.pipeline-name{
    color:#444;
    font-weight: bold;
    font-size: 15px;
}
.pipeline-timeline{
    font-size:13px;
    color:#555;
    margin-top:10px;
    border-top:1px solid #eee;
    border-bottom:1px solid #eee;
    background-color: #F7F7F7;
    padding:10px;
    display: flex;
    align-items: center;
}
.pipeline-timeline-run-stage{
    font-size:16px;
    font-weight: bold;
}
.pipeline-timeline-run-step{
    position: relative;
}
.pipeline-timeline-run-step-time{
    position:absolute;
    top:-10px;
    font-size:12px;
    left:0;
    color:#999;
}
.pipeline-timeline-run-step-success{
    color:#0097F7;
}
.pipeline-timeline-run-step-error{
    color:#C7000B;
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
    font-weight: 700;
}
.run-box-type-2{
    color:red;
    font-weight: 700;
}
.run-box-type-3{
    color:#98F568;
    font-weight: 700;
}

.run-log-box{
    height: calc(100vh - 270px);
    border:1px solid #ccc;
    overflow: auto;
    padding:10px;
    background-color: #000;
}
.run-log-stage{
    color:#ccc;
    display: inline-block;
    margin-right:5px;
}
.run-log-time{
    color:#ccc;
    margin-left:5px;
}
.history-box{
    height: calc(100vh - 270px);
    overflow: auto;
}
.spin-icon-load{
    animation: ani-demo-spin 3s linear infinite;
    color:#fff;
}
.option-bar{
    position: absolute;
    top:140px;
    right:25px;
    width:200px;
    text-align: right;
    z-index:999;
}
.stage-box{
    display: inline-flex;
    align-items: center;
}
.stage-name{
    font-size:16px;
    font-weight: bold;
}
.step-name{
    font-size:12px;
}
.stage-arrow{
    margin-left:10px;
    margin-right:10px;
}
.current-run{
    color:#0097F7;
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
.step-name{
    display: inline-block;
    text-align: center;
}
</style>
<i18n>
{
    "en": {
        "执行Pipeline": "Run Pipeline",
        "开始执行":"Run",
        "停止执行":"Stop",
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
        "执行成功":"Success",
        "执行失败":"Fail"
    },
    "zh_CN": {
        "执行Pipeline": "执行Pipeline",
        "开始执行":"开始执行",
        "停止执行":"停止执行",
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
        "执行成功":"执行成功",
        "执行失败":"执行失败"
    }
}
</i18n>
 <template>
    <Modal
        ref="dialog" class="full-modal" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('执行Pipeline')" width="1200" :footer-hide="true">
        <div class="page">
            <Row>
                <Col span="12" class="pipeline-name">
                   {{formItem.name}}&nbsp;
                </Col>
                <Col  span="12" style="text-align:right">
                    <div class="create-user-label">
                        <template v-if="formItem.runLog.id>0"> 
                            <span style="margin-right:10px">
                                {{formItem.runLog.createTime|fmtDeltaTime}} 
                                <AvatarImage  size="small" :name="formItem.lastRunAccountName" :value="formItem.lastRunAccountImageId" type="label"></AvatarImage>
                                 </span>
                            <DataDictLabel style="margin-right:10px" type="ProjectPipelineRunLog.status" :value="formItem.runLog.status"></DataDictLabel>
                            <Button v-if="formItem.runLog.status==1||formItem.runLog.status==2" @click="stopRun" type="error">{{$t('停止执行')}}</Button>
                        </template>
                        <template v-if="(formItem.runLog.status==0||formItem.runLog.status==3||formItem.runLog.status==4)">
                             <Button @click="startRun" type="default">{{$t('开始执行')}}</Button>
                        </template>
                    </div>
                </Col>
            </Row>
            <div v-if="formItem.pipelineDefine" class="pipeline-timeline">
                <div class="pipeline-timeline-run-step" 
                     :class="{  'pipeline-timeline-run-step-success':item.success==true,
                                'pipeline-timeline-run-step-error':item.success==false,
                                'pipeline-timeline-run-stage':item.type=='stage',
                            }"
                    v-for="(item,idx) in runStepList" :key="'runs'+idx">
                     {{item.name}} <Icon v-if="idx<runStepList.length-1" type="md-arrow-round-forward" />
                    <div v-if="item.time!=null" class="pipeline-timeline-run-step-time">{{item.time}}s</div>
                </div>
            </div>
             <Tabs value="run" :animated="false" style="margin-top:15px">
                <TabPane :label="$t('输出')" name="run">
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
        
                    <Spin ref="loadingBox" v-show="formItem.runLog!=null&&(formItem.runLog.status==1||formItem.runLog.status==2)">
                        <Icon type="ios-loading" size=18 class="spin-icon-load"></Icon>
                    </Spin>
                       
                    </div>
                </TabPane>
                <TabPane :label="$t('历史记录')+'('+historyListCount+')'" name="history">
                    <div class="history-box scrollbox">
                         <table class="table-content table-content-small" style="margin-top:10px">
                        <tbody>
                            <tr v-for="item in historyList" :key="'his_'+item.id" class="table-row table-row-small">
                                <td style="width:150px">
                                    <AvatarImage  size="small" :name="item.createAccountName" :value="item.createAccountImageId" type="label"></AvatarImage>
                                </td>
                                <td>
                                    {{item.createTime|fmtDeltaTime}}
                                </td>     
                                 <td >
                                    <template v-if="item.useTime">{{$t('用时')}}:{{item.useTime}}</template>
                                </td> 
                                <td style="text-align:right;width:200px">
                                    
                                    <span class="table-row-hover-hide">
                                       <DataDictLabel  type="ProjectPipelineRunLog.status" :value="item.status"></DataDictLabel>
                         
                                    </span>
                                    <span class="table-row-opt">
                                        <IconButton @click="showHistoryDialog(item)" icon="ios-list" :tips="$t('查看日志')"></IconButton>
                                    </span>
                                </td>
                            </tr>
                        </tbody>
                        </table>
                    </div>
                </TabPane>

                <TabPane :label="$t('统计')" name="chart">
                         <v-chart :autoresize="true" :options="chartOptions" style="width:100%;height:400px"/>
                </TabPane>


            </Tabs>
            

        </div>
    </Modal>
</template>


<script>
import qrcode from '@xkeshi/vue-qrcode';
import AnsiUp from 'ansi_up';

export default {
        name:"PipelineRunDialog",
        mixins: [componentMixin],
        components: {
            qrcode
        },
        data () {
            return {
                chartOptions:{
                 color:['#0094FB','#CCCCCC'],
                    tooltip: {
                        trigger: 'axis'
                    },
                    xAxis: {
                        data:[],
                        type: 'category',
                    },
                    yAxis: {
                        type: 'value'
                    },
                    legend: {
                        data:[this.$t('时间')]
                    },
                    series: [{
                        name: this.$t('时间'),
                        data: [],
                        type: 'bar',
                        itemStyle: {
                        normal: {
                                color: (params)=>{ 
                                    var data=this.historyList[params.dataIndex];
                                    if(data==null){
                                        return null;
                                    }
                                    var colorList = ['#C33531','#EFE42A','#64BD3D','#0094FB','#F82733']; 
                                    return colorList[data.status] 
                                }
                            },
                        },
                    }]
                },
                lastLogId:0,
                formItem:{
                    runLog:{}
                },
                logList:[],
                lastStatus:0,
                historyList:[],
                runStepList:[],
                historyListCount:0,
                parameterDialogShow:false,
            }
        },
        created(){
            window.ansi_up =  new AnsiUp;
        },
        filters:{
           
        },
        beforeDestroy(){
            clearInterval(this.refreshInterval);
        },
        methods: {
            pageLoad(){
                this.loadData();
                this.loadHistory();
                this.startRefresh();
            },
            pageMessage(type){
                if(type=='pipeline.parameter.edit'){
                    this.loadData();
                }
                if(type=='pipeline.parameter.hide'){
                    this.parameterDialogShow=false;
                }
            },
            ansiup(v){
                return ansi_up.ansi_to_html(v);
            },
            getStepTime(step){
                if(this.formItem.runLog==null){
                    return null;
                }
                if(this.formItem.runLog.stepInfo==null){
                    return null;
                }
                var list=this.formItem.runLog.stepInfo;
                for(var i=0;i<list.length;i++){
                    var item=list[i];
                    if(item.step==step){
                        return item;
                    }
                }
                return null;
            },
            setupRunLog(){
                var runStepList=[];
                this.formItem.pipelineDefine.stages.forEach(stage=>{
                    runStepList.push({
                        name:stage.name,
                        type:'stage'
                    })
                    stage.steps.forEach(step=>{
                        var stepTime=this.getStepTime(step);
                        var stepItem={
                            name:step,
                            type:'step',
                        }
                        if(stepTime){
                            stepItem.time=parseInt(stepTime.time/1000);
                            stepItem.success=stepTime.success;
                        }
                        runStepList.push(stepItem)
                    })
                })
                this.runStepList=runStepList;
                console.log(runStepList)
            },
            loadData(){
                app.invoke('BizAction.getProjectPipelineInfoById',[app.token,this.args.id],(info)=>{
                    this.formItem=info;
                    this.setupRunLog();
                    if(info.runId>0){
                        this.loadLog();
                    }
                    if(this.lastStatus!=0&&this.lastStatus!=this.formItem.runLog.status){
                        app.postMessage('pipeline.runstatus');
                        if(this.formItem.runLog.status==3){
                            app.toast(this.$t('执行成功'));
                            new Audio('/audio/pipeline-finish.mp3').play() //成功
                        }
                        if(this.formItem.runLog.status==4){
                            app.toast(this.$t('执行失败'));
                            new Audio('/audio/pipeline-error.mp3').play() //失败
                        }
                    }
                    this.lastStatus=this.formItem.runLog.status;
                    //
                    if(this.formItem.runLog.status==1){
                        if(this.parameterDialogShow==true){
                            return;
                        }
                        this.parameterDialogShow=true;
                        app.showDialog(PipelineRunParameterDialog,{
                            pipeline:this.formItem
                        })
                    }
                },()=>{},true)
            },
            loadHistory(){
                var historyQuery={
                    pageIndex:1,
                    pageSize:50,
                    pipelineId:this.args.id,
                }
                app.invoke('BizAction.getProjectPipelineRunLogInfoList',[app.token,historyQuery],(info)=>{
                   this.historyList=info.list;
                   this.historyListCount=info.count;
                   //
                   for(var i=0;i<info.list.length;i++){
                        var t = info.list[i];
                        this.chartOptions.xAxis.data.push(formatDate(t.createTime));
                        var seconds=(t.endTime-t.startTime)/1000;
                        this.chartOptions.series[0].data.push(seconds);
                    } 
                   //
                })
            },
            downloadArchive(item){
                window.open(item.url)
            },
            loadLog(){
                app.invoke('BizAction.getProjectPipelineRunDetailLogList',[app.token,
                    this.formItem.runId,this.lastLogId],(list)=>{
                    if(list.length>0&&this.logList.length>0){
                        if(list[0].id==this.logList[this.logList.length-1].id){
                            this.logList.splice(this.logList.length-1,1);
                        }
                    }
                    for(var i=0;i<list.length;i++){
                        var log=list[i];
                        if(log.type==4){
                            log.archiveObject=JSON.parse(log.message);
                            log.archiveObject.url=app.getHost()+'/p/webapi/download_artifact/'+log.archiveObject.uuid;
                        }
                        this.logList.push(log)
                    }
                    if(list.length>0){
                        this.lastLogId=list[list.length-1].id;
                    }
                    this.$nextTick(()=>{
                        this.$refs.loadingBox.$el.scrollIntoView();
                    })
                },()=>{},true)
            },
            startRefresh(){
                this.refreshInterval=setInterval(()=>{
                    if(this.formItem.runLog!=null&&(this.formItem.runLog.status==1||this.formItem.runLog.status==2)){
                         this.loadData();
                    }
                },5000)
            },
           
            startRun(){
                this.logList=[];
                this.parameterDialogShow=false;
                app.invoke('BizAction.runProjectPipeline',[app.token,this.args.id],(info)=>{
                    this.loadData();
                    app.postMessage('pipeline.edit')
                })
            },
            stopRun(){
                app.invoke('BizAction.stopProjectPipeline',[app.token,this.args.id],(info)=>{
                    this.loadData();
                    app.postMessage('pipeline.edit')
                })
            },
            showHistoryDialog(item){
                app.showDialog(PipelineRunHistoryDialog,{
                    runId:item.id
                })
            },
            confirm:function(){
                this.showDialog=false;
            }
        }
    }
</script>