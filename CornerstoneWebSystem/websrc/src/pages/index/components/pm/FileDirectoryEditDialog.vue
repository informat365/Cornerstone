<style scoped>
.desc-info{
    font-size:12px;
    color:#999;
}
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
}
</style>
<i18n>
{
    "en": {
        "文件夹设置": "Directory",
        "名称": "Name",
        "颜色标识":"Color",
        "访问权限":"Permission",
        "所有项目成员都可以查看":"All project members can view",
        "只允许拥有以上角色的成员查看":"Only members with the above roles are allowed to view",
        "继续创建下一个":"Continue create",
        "创建":"Create",
        "保存":"Save",
        "操作成功":"Success"
    },
    "zh_CN": {
        "文件夹设置": "文件夹设置",
        "名称": "名称",
        "颜色标识":"颜色标识",
        "访问权限":"访问权限",
        "所有项目成员都可以查看":"所有项目成员都可以查看",
        "只允许拥有以上角色的成员查看":"只允许拥有以上角色的成员查看",
        "继续创建下一个":"继续创建下一个",
        "创建":"创建",
        "保存":"保存",
        "操作成功":"操作成功"
    }
}
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('文件夹设置')" width="500"  @on-ok="confirm">

    <Form @submit.native.prevent ref="form" :rules="formRule" :model="formItem" label-position="top" style="height:400px;padding:25px">
         <FormItem :label="$t('名称')" prop="name">
            <Input v-model.trim="formItem.name" placeholder=""></Input>
        </FormItem>

         <FormItem  :label="$t('颜色标识')" >
              <div style="height:50px;text-align:center">
                    <span @click="setColor(item)" v-for="item in colorArray" :key="item" 
                        class="tag-color" :style="{backgroundColor:item}">
                        <Icon size="20" v-show="item==formItem.color" type="md-checkmark" />
                    </span>
            </div>
        </FormItem>

        <FormItem :label="$t('访问权限')">
            <i-Switch v-model="formItem.enableRole"></i-Switch>
            <span v-if="formItem.enableRole==false" style="margin-left:10px">{{$t('所有项目成员都可以查看')}}</span>
        </FormItem>

         <FormItem v-if="formItem.enableRole">
            <CheckboxGroup v-model="formItem.roles">
                    <Checkbox v-for="item in roleList" :key="item.id" :label="item.id">{{item.name}}</Checkbox>
            </CheckboxGroup>
            <div style="color:#666;font-size:12px">{{$t('只允许拥有以上角色的成员查看')}}</div>
        </FormItem> 

    </Form>
    
    <div slot="footer">
        <Row>
            <Col span="12" style="text-align:left;padding-top:5px"> <Checkbox v-if="formItem.id==0" v-model="continueCreate" size="large">{{$t('继续创建下一个')}}</Checkbox> &nbsp;</Col>
            <Col span="12" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{formItem.id==0?$t("创建"):$t("保存")}}</Button></Col>
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
                    id:0,
                    name:null,
                    enableRole:false,
                    roles:[],
                    color:null,
                },
                formRule:{
                    name:[vd.req,vd.name],
                },
                roleList:[],
                colorArray:[
                        "#2E94B9",
                        "#F0B775",
                        "#D25565",
                        "#F54EA2",
                        "#42218E",
                        "#5BE7C4",
                        "#525564",
                ]
                
            }
        },
        methods: {
            pageLoad(){
                this.loadRole();
                if(this.args.item){
                    this.formItem.id=this.args.item.id;
                    this.formItem.name=this.args.item.name;
                    this.formItem.enableRole=this.args.item.enableRole;
                    this.formItem.roles=this.args.item.roles;
                    this.formItem.color=this.args.item.color;
                }else{
                    this.formItem.parentId=this.args.parentId;
                    this.formItem.projectId=this.args.projectId;
                }
            },
            loadRole(){
                app.invoke('BizAction.getRoleInfoList',[app.token,1],(list)=>{
                    this.roleList=list;
                })
            },
            setColor(color){
                if(this.formItem.color==color){
                    this.formItem.color=null;
                }else{
                    this.formItem.color=color;
                }
            },
            confirm(){
                this.$refs.form.validate((r)=>{
                    if(r){
                        this.confirmForm()
                    }
                });
            },
            confirmForm(){
                var action=this.formItem.id==0?'BizAction.addDirectory':"BizAction.updateDirectory";
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.toast(this.$t('操作成功'))
                    app.postMessage('dir.edit');
                    if(!this.continueCreate){
                        this.showDialog=false;
                    }else{
                        this.$refs.form.resetFields();
                    }
			    })
            }
        }
    }
</script>