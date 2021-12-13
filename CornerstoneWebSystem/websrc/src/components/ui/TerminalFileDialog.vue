<style scoped>
.file-box{
    height:500px;  
    display: flex;
    flex-direction: column;
}
.opt-box{
    height:40px;
    display: flex;
    align-items: center;
    padding:10px;
    border-bottom: 1px solid #eee;
    background-color: #f7f7f7;
}
.opt-left{
    display: flex;
    align-items: center;
    flex:1;
}
.opt-right{
    width:150px;
    display: flex;
    align-items: center;
    flex-direction: row-reverse;
    align-content:space-between;
}
.info-box{
    height:30px;
    display: flex;
    align-items: center;
    padding:5px 10px ;
    border-top: 1px solid #eee;
    background-color: #f7f7f7;
    color:#666;
    font-weight: bold;
}
.table-box{
    flex:1;
    overflow:auto;
}
.folder-icon{
    color:#59CEF7;
}
</style>
<template>
    <Modal
        ref="dialog" class="nopadding-modal" v-model="showDialog" :closable="true"
         :mask-closable="false" 
         :loading="false" 
         :title="title" 
         width="900px"  
         :footer-hide="true">
        <div class="file-box">
            <div class="opt-box">
                <div class="opt-left"> 
                    <IconButton @click="cdUp" icon="md-arrow-round-up"  tips="返回上一层" ></IconButton> 
                    <IconButton @click="loadInfo" icon="md-refresh"  tips="刷新" ></IconButton> 
                    <Poptip ref="uploadPoptip" placement="bottom-start">
                        <IconButton  icon="md-cloud-upload"  tips="上传文件" ></IconButton>
                        <ScpFileUploadView ref="uploadView" style="padding:10px;width:500px" 
                        @change="uploadSingleSuccess" slot="content"> </ScpFileUploadView>
                    </Poptip>
                    <IconButton @click="mkdir" icon="md-folder"  tips="创建文件夹" ></IconButton> 
                </div>
                <div class="opt-right"> 
                    <i-Switch v-model="showHiddenFile" size="small"/>
                    <span style="margin-right:5px">显示隐藏文件</span>
                </div>
            </div>
            <div class="table-box">
                <table class="table-content"  style="table-layout:fixed">
                <thead>
                    <tr>
                        <th style="width:40px;text-align:center"></th>
                        <th>名称</th>
                        <th style="width:100px;">大小</th>
                        <th style="width:150px;">修改时间</th>
                        <th style="width:150px;"></th>
                    </tr>     
                  </thead>
                    <tbody>
                        <tr v-for="(item,idx) in fileList" :key="idx"  class="table-row">
                            <td>
                                <Icon class="folder-icon" :size="20" v-if="item.attrs.dir" type="ios-folder" />
                                <Icon class="folder-icon" :size="20" v-if="item.attrs.dir==false" type="ios-document" />
                            </td>
                            <td @click="clickFile(item)" class="text-no-wrap clickable">
                               {{item.filename}}
                            </td>
                             <td class="text-no-wrap">
                              <template v-if="item.attrs.dir">--</template>
                              <template v-if="item.attrs.dir==false">
                                  {{item.attrs.size|fmtBytes}}
                              </template>
                            </td>
                            <td >
                               {{item.attrs.mTime*1000|fmtDateTime}}
                            </td>
                            <td>
                                 <IconButton @click="removeFile(item)" icon="md-trash"  tips="删除" ></IconButton>
                                 <IconButton @click="download(item)" v-if="item.attrs.dir==false" icon="md-cloud-download"  tips="下载文件" ></IconButton> 
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div class="info-box">
                <Row style="width:100%">
                    <Col span="18" class="text-no-wrap">
                        当前路径: {{currentInfo.pwd}}
                    </Col>
                    <Col span="6">
                        <template >文件数：{{fileList.length}}</template>
                    </Col>
                </Row>
            </div>
        </div>
    </Modal>
</template>
<script>
    export default {
        name:"TerminalFileDialog",
        mixins: [componentMixin],
        data () {
            return {
               filePath:null,
               title:"",
               connectionStatus:false,
               currentInfo:{
                   fileList:[]
               },
               showHiddenFile:false
            }
        },
        computed:{
            fileList(){
                if(this.showHiddenFile){
                    return this.currentInfo.fileList;
                }
                var list=[];
                this.currentInfo.fileList.forEach(item=>{
                    if(!item.filename.startsWith('.')){
                        list.push(item)
                    }
                })
                return list;
            }
        },
        watch:{
            connectionStatus(val){
                if(val){
                    this.title=this.args.info.machineName+" 已连接"
                }else{
                    this.title=this.args.info.machineName+" 未连接"
                }
            }
        },
        methods:{
            pageLoad(){
                this.title=this.args.info.machineName;
                this.connect();  
            },
            pageMessage(type, content) {
                if (type == "sftp.dir") {
                    this.loadInfo();
                }
            },
            
            connect(){
                app.invoke('BizAction.startSftp',[app.token,this.args.info.token],(info)=>{
                    this.connectionStatus=true;
                    this.loadInfo();
			    })
            },
            updateUploadAddr(){
                var query={
                    token:app.token,
                    machineLoginToken:this.args.info.token,
                    filePath:this.currentInfo.pwd,
                }
                //
                var queryString=JSON.stringify(query);
                var encoded=Base64.encode(queryString);
                var addr= app.serverAddr+'/p/file/sftp_upload?arg='+encoded;
                
                this.$refs.uploadView.setAddr(addr);
            },
            uploadSingleSuccess(obj){
                app.toast('上传成功');
                this.$refs.uploadPoptip.ok();
                this.loadInfo();
            },
            download(item){
                var query={
                    token:app.token,
                    machineLoginToken:this.args.info.token,
                    filePath:this.currentInfo.pwd+"/"+item.filename,
                    fileSize:item.attrs.size
                }
                //
                var queryString=JSON.stringify(query);
                var encoded=Base64.encode(queryString);
                window.open('/p/file/sftp_download?arg='+encodeURIComponent(encoded))
            },
            loadInfo(){
                app.invoke('BizAction.getSftpInfo',[app.token,this.args.info.token],(info)=>{
                    this.currentInfo=info;
                    this.updateUploadAddr();
			    })  
            },
            mkdir(){
                app.showDialog(TerminalDirCreateDialog,{
                    info:this.args.info
                })
            },
            cdUp(){
                app.invoke('BizAction.sftpCd',[app.token,this.args.info.token,".."],(info)=>{
                    this.loadInfo();
			    })
            },
            clickFile(item){
                if(!item.attrs.dir){
                    return;
                }
                app.invoke('BizAction.sftpCd',[app.token,this.args.info.token,item.filename],(info)=>{
                    this.loadInfo();
			    })
            },
            removeFile(item){
                var action=item.attrs.dir?'BizAction.sftpRmdir':"BizAction.sftpRm";
                app.confirm('确认要删除【'+item.filename+'】?',()=>{
                    app.invoke(action,[app.token,this.args.info.token,item.filename],(info)=>{
                        app.toast('删除成功')
                        this.loadInfo();
                    })
                })
            }
        }
    }
</script>