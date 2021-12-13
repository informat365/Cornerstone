<style scoped>
</style>
<i18n>
{
    "en": {
        "选择项目": "Project",
        "复制到项目":"Copy to ",
        "复制":"Copy",
        "请选择要复制到的项目":"Please choose project"
    },
    "zh_CN": {
        "选择项目": "选择项目",
        "复制到项目":"复制到项目",
        "复制":"复制",
        "请选择要复制到的项目":"请选择要复制到的项目"
    }
}
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('选择项目')" width="700">

    <Form   label-position="top" style="height:500px;padding:15px">
        <FormItem :label="$t('复制到项目')">
            <Select transfer filterable multiple style="width:100%" :placeholder="$t('选择项目')" v-model="formItem.projectList" >
                <Option  v-for="item in projectList" :key="item.id" :value="item.id">{{item.name}}</Option>
            </Select>    
        </FormItem>

    </Form>
    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> <Button @click="confirmForm" type="default" size="large" >{{$t('复制')}}</Button></Col>
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
                    projectList:[],
                },
                projectList:[],
            }
        },
       
        methods: {
            pageLoad(){
                this.loadProjectList();
            },
            loadProjectList(){
                app.invoke( "BizAction.getMyProjectList",[app.token],list => {
                    this.projectList=list;
                })
            },
            confirmForm(){
                if(this.formItem.projectList.length==0){
                    app.toast(this.$t('请选择要复制到的项目'));
                    return;
                }
                this.showDialog=false;
                this.args.callback(this.formItem.projectList);
            }
        }
    }
</script>