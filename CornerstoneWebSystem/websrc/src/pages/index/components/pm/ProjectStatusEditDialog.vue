<style scoped>
    .check{
        color:#0097F7;
    }
    .tag-color{
        display: inline-block;
        width:30px;
        height: 30px;
        border-radius: 50%;
        margin-right:10px;
        color:#fff;
        overflow: hidden;
        text-align: center;
    }
    .tag-item{
        display: inline-flex;
        align-items: center;
        padding:5px 10px;
    }
    .tag-box{
        display: flex;
        align-items: center;
        justify-items: center;
        padding:10px 0;
    }
</style>
<i18n>
{
    "en": {
        "编辑状态": "Edit status",
        "项目状态":"Project status",
        "进度":"Progress",
        "备注":"Remark",
        "保存":"Save",
        "进度正常":"Normal",
        "存在风险":"Rish",
        "进度失控":"Out of Control",
        "保存成功":"Success",
         "请选择项目状态":"Please choose a project status"
    },
    "zh_CN": {
        "编辑状态": "编辑状态",
        "项目状态":"项目状态",
        "进度":"进度",
        "备注":"备注",
        "保存":"保存",
        "进度正常":"进度正常",
        "存在风险":"存在风险",
        "进度失控":"进度失控",
        "保存成功":"保存成功",
        "请选择项目状态":"请选择项目状态"
    }
}
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('编辑状态')" width="500"  @on-ok="confirm">

    <Form   @submit.native.prevent ref="form" :rules="formRule" :model="formItem"
             label-position="top" style="height:450px;padding:15px">

        <FormItem :label="$t('项目状态')" >
            <div class="tag-box">

                    <div class="tag-item" v-for="item in colorArray" :key="item.value" >
                        <span @click="setColor(item)"
                                class="tag-color" :style="{backgroundColor:item.color}">
                                <Icon size="20" v-show="item.value==formItem.runStatus" type="md-checkmark" />
                        </span>
                        <span>{{item.name}}</span>
                    </div>

            </div>
        </FormItem>

        <FormItem :label="$t('进度')" prop="progress">
             <Slider v-model="formItem.progress" show-input></Slider>
        </FormItem>

        <FormItem :label="$t('备注')" prop="remark">
            <Input type="textarea" :rows="4" v-model.trim="formItem.remark" :placeholder="$t('备注')"></Input>
        </FormItem>

    </Form>


    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{$t('保存')}}</Button></Col>
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
                    projectId:0,
                    progress:0,
                    runStatus:1,
                    remark:null,
                },
                formRule:{
                    remark:[vd.req,vd.desc],
                },
                colorArray:[
                    {color:"#00BE70",name:this.$t("进度正常"),value:1},
                    {color:"#FF9725",name:this.$t("存在风险"),value:2},
                    {color:"#EC0023",name:this.$t("进度失控"),value:3}
                ]
            }
        },
        methods: {
            pageLoad(){
                if(this.args.project){
                    this.formItem.projectId=this.args.project.id;
                    this.formItem.progress=this.args.project.progress;
                    this.formItem.runStatus=this.args.project.runStatus;
                }
            },
            setColor(item){
               this.formItem.runStatus=item.value;
            },
            confirm(){
                if(this.formItem.runStatus<=0||this.formItem.runStatus>3){
                    app.toast(this.$t('请选择项目状态'));
                    return;
                }
                this.$refs.form.validate((r)=>{
                    if(r){this.confirmForm()}
                });
            },
            confirmForm(){
                var action='BizAction.addProjectRunLog'
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.toast(this.$t('保存成功'))
                    app.postMessage('project.edit')
                    this.showDialog=false;
			    })
            }
        }
    }
</script>
