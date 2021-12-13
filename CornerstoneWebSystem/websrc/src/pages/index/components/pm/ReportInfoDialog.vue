<style scoped>
.content-box{
    padding:10px 20px;
    width:100%;
    min-height: calc(100vh - 200px);
}
.report-title{
    font-size:25px;
    font-weight: bold;
    width:100% !important;
    border:none;
    text-align: center;
}
.report-content-title{
    font-size:20px;
    font-weight: bold;
    color:#2b2b2b;
    display: flex;
    align-items: center;
    margin-bottom:10px;
}
.report-content-item{
    margin-top:25px;
}
.info-line{
    display: flex;
    align-items: center;
    color:#999;
    margin-top:10px;
    font-size:13px;
}
.reply-box{
    position:absolute;
    bottom: 0;
    left:0;
    width:100%;
    height: calc(100vh - 180px);
    overflow: auto;
    background-color: #fff;
    border-top:1px solid #eee;
    padding:25px 35px;
    z-index:999;
    box-shadow: rgba(0, 0, 0, 0.15) 0px 0px 8px 0px  !important;
}
.reply-user{
    display: flex;
    flex-direction: row;
    margin-bottom:7px;
}
.reply-item{
    margin-bottom:15px;
}
</style>
<i18n>
{
    "en": {
        "汇报人": "Reporter",
        "汇报周期":"Period ",
        "审核人":"Auditor",
        "关联项目":"Project",
        "编辑":"Edit",
        "评论":"Comment",
        "取消":"Cancel",
        "保存":"Save",
        "回复":"Reply",
        "保存草稿":"Save",
        "提交审核":"Submit",
        "请输入回复内容":"Please input content",
        "回复汇报":"Reply",
        "保存成功":"Success",
        "审核成功":"Success",
        "导出":"Export",
        "确定要提交此汇报吗？":"Are you sure you want to submit this report?"
    },
    "zh_CN": {
        "汇报人": "汇报人",
        "汇报周期":"汇报周期",
        "审核人":"审核人",
        "关联项目":"关联项目",
        "编辑":"编辑",
        "评论":"评论",
        "取消":"取消",
        "保存":"保存",
        "回复":"回复",
        "保存草稿":"保存草稿",
        "提交审核":"提交审核",
        "请输入回复内容":"请输入回复内容",
        "回复汇报":"回复汇报",
        "保存成功":"保存成功",
        "审核成功":"审核成功",
        "导出":"导出",
        "确定要提交此汇报吗？":"确定要提交此汇报吗"
    }
}
</i18n>
<template>
    <Modal
        ref="dialog"  v-model="showDialog" :closable="true"
         :mask-closable="false"
         :loading="false"
         width="800">
        <div class="content-box">
        <div v-if="formItem.id>0">
            <div class="report-title" >
                 {{formItem.name}}
            </div>
            <div class="info-line" style="justify-content: center;">
                  {{$t('汇报人')}}：{{formItem.submitterName}}
                  <span style="margin-left:10px;margin-right:10px">{{$t('汇报周期')}}：{{formItem.reportTime}} </span>
                  <DataDictLabel type="Report.status" :value="formItem.status"/>
            </div>

            <div class="info-line">
                 {{$t('审核人')}}：
                     <template v-if="formItem.auditorList.length==1">
                                        <AvatarImage  size="small" :name="formItem.auditorList[0].name" :value="formItem.auditorList[0].imageId" type="label"></AvatarImage>
                                    </template>
                                    <template v-if="formItem.auditorList.length>1">
                                        <AvatarImage  v-for="acc in formItem.auditorList" :key="formItem.id+'_acc'+acc.id" size="small" :name="acc.name" :value="acc.imageId" type="tips"></AvatarImage>
                                </template>
            </div>
            <div class="info-line" v-if="formItem.projectName">
                {{$t('关联项目')}}：{{formItem.projectName}}
            </div>

        </div>

            <div>
                <div  v-for="(item,idx) in contentList" :key="'rp'+item.id"  class="report-content-item">
                    <div class="report-content-title">{{idx+1}}.{{item.title}}
                        <IconButton v-if="hasEditPermission()" style="font-size:13px" @click="answerItem(idx,item)" :title="$t('编辑')"/>
                    </div>
                    <RichtextLabel :value="item.content"></RichtextLabel>
                </div>
            </div>

            <div v-if="replyList.length>0" style="margin-top:30px">
                <div class="report-content-title">{{$t('评论')}}</div>
                <div class="reply-item" v-for="item in replyList" :key="'rp'+item.id">
                    <div class="reply-user">
                        <AvatarImage :name="item.createAccountName" :value="item.createAccountImageId" type="tips"/>
                        <div style="padding-left:10px">
                            <div  style="">{{item.createAccountName}}</div>
                            <div  style="color:#999">{{item.createTime|fmtDeltaTime}}</div>
                        </div>
                    </div>
                    <div class="reply-content">
                        <RichtextLabel :value="item.content"></RichtextLabel>
                    </div>
                </div>
            </div>

        </div>
        <div v-show="editItem.isShow" class="reply-box">
            <div class="report-content-title">{{editItem.title}}</div>
            <RichtextEditor  ref="editor" v-model="editItem.content"></RichtextEditor>
             <Row>
                <Col span="24" style="text-align:right;padding-top:10px">
                    <Button ghost type="text"   @click="cancelEdit"  style="color:#333;margin-right:10px" size="large">{{$t('取消')}}</Button>
                    <Button v-if="hasEditPermission()" icon="md-checkmark" @click="confirmEdit" type="default" size="large" >{{$t('保存')}}</Button>
                    <Button v-if="hasReplyPermission()" icon="md-checkmark" @click="confirmReply" type="default" size="large" >{{$t('回复')}}</Button>
                </Col>
            </Row>
        </div>
         <div slot="footer" >
            <Row>
                <Col span="12" style="text-align:left">
                    <Button @click="exportReport" size="large" type="default">{{$t('导出')}}</Button>
                </Col>
                <Col span="12" style="text-align:right" v-if="hasEditPermission()||hasReplyPermission()">
                    <Button v-if="hasEditPermission()" @click="saveDraft" type="success"  style="margin-right:10px" size="large">{{$t('保存草稿')}}</Button>
                    <Button v-if="hasEditPermission()" icon="md-checkmark" @click="confirm" type="default" size="large" >{{$t('提交审核')}}</Button>
                    <Button v-if="hasReplyPermission()" icon="md-checkmark" @click="reply" type="default" size="large" >{{$t('回复')}}</Button>
                    <template v-if="formItem.status === 2 && hasReplyPermission">
                        <Button icon="md-checkmark" @click="audit(false)" type="warning" size="large" >{{$t('拒绝')}}</Button>
                        <Button icon="md-checkmark" @click="audit(true)" type="success" size="large" >{{$t('通过')}}</Button>
                    </template>
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
                    auditorList:[],
                    reportContentList:[]
                },
                currentIndex:null,
                editItem:{
                    isShow:false,
                    title:null,
                    content:null
                }
            }
        },
        computed:{
            contentList(){
                return this.filterList(1);
            },
            replyList(){
                return this.filterList(2);
            }
        },
        methods:{
            pageLoad(){
               this.loadData();
            },
            hasReplyPermission(){
                if(this.formItem.id==null||this.formItem.status==1){
                    return false;
                }
                for(var i=0;i<this.formItem.auditorIds.length;i++){
                    var t=this.formItem.auditorIds[i];
                    if(t==app.account.id){
                        return true;
                    }
                }
                return false;
            },
            hasEditPermission(){
                if(this.formItem.id==null||this.formItem.status!=1){
                    return false;
                }
                return app.account.id==this.formItem.submitterId;
            },
            filterList(type){
                var list=[];
                for(var i=0;i<this.formItem.reportContentList.length;i++){
                    var t=this.formItem.reportContentList[i];
                    if(t.type==type){
                        list.push(t);
                    }
                }
                return list;
            },
            loadData(){
                 app.invoke("BizAction.getReportById",[app.token,this.args.id],info => {
                    this.formItem=info;
                });
            },
            saveDraft(){
                this.save(false);
            },
            answerItem(idx,item){
                this.currentIndex=idx;
                this.editItem.isShow=true;
                this.editItem.title=item.title;
                this.editItem.content=item.content;
                this.$refs.editor.setValue(item.content);
            },
            cancelEdit(){
                 this.editItem.isShow=false;
            },
            confirmEdit(){
                var t=this.formItem.reportContentList[this.currentIndex];
                t.content=this.$refs.editor.getValue();
                this.$set(this.formItem.reportContentList,this.currentIndex,t);
                this.editItem.isShow=false;
            },
            confirmReply(){
                var bean={
                    reportId:this.formItem.id,
                    content:this.$refs.editor.getValue()
                }
                if(bean.content.trim().length==0){
                    app.toast(this.$t('请输入回复内容'));
                    return;
                }
                app.invoke("BizAction.replyReport",[app.token,bean],info => {
                    this.editItem.isShow=false;
                    app.postMessage('report.edit')
                    this.loadData();
                });
            },
            reply(){
                this.editItem.isShow=true;
                this.editItem.title=this.$t("回复汇报");
                this.editItem.content="";
                this.$refs.editor.setValue("");
            },
            audit(pass) {
                const url = pass ? 'BizAction.auditPassReport' : 'BizAction.auditRejectReport';
                app.invoke(url, [app.token,this.formItem.id], () => {
                    this.showDialog=false;
                    app.postMessage('report.edit');
                    app.toast(this.$t('审核成功'));
                });
            },
            save(isSubmit){
                app.invoke("BizAction.saveReport",[app.token,
                        this.formItem,
                        isSubmit],info => {
                    if(isSubmit){
                        this.showDialog=false;
                        app.postMessage('report.edit')
                    }else{
                        app.toast(this.$t('保存成功'));
                    }
                });
            },
            confirm(){
                app.confirm(this.$t('确定要提交此汇报吗'),()=>{
                    this.save(true);
                })
            },
            exportReport(){
                const url = [app.serverAddr];
                url.push('/p/file/download_report_pdf/');
                url.push(this.args.id);
                url.push('?token=');
                url.push(app.token);
                window.open(url.join(''));
            }
        }
    }
</script>
