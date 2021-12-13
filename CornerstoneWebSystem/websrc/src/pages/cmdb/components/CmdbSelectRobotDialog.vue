<style scoped>
</style>
<i18n>
{
	"en": {
        "选择Robot": "选择Robot",
		"确定": "确定",
		"请选择要执行的Robot": "请选择要执行的Robot"
    },
	"zh_CN": {
		"选择Robot": "选择Robot",
		"确定": "确定",
		"请选择要执行的Robot": "请选择要执行的Robot"
	}
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('选择Robot')" width="700">

    <Form   label-position="top" style="height:500px;padding:15px">
        <FormItem :label="$t('选择Robot')">
            <Select filterable style="width:100%" :placeholder="$t('选择Robot')" v-model="formItem.robotId" >
                <Option  v-for="item in robotList" :key="item.id" :value="item.id">{{item.name}}</Option>
            </Select>    
        </FormItem>

    </Form>
    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> <Button @click="confirmForm" type="default" size="large" >{{$t('确定')}}</Button></Col>
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
                    robotId:null,
                },
                robotList:[],
            }
        },
       
        methods: {
            pageLoad(){
                this.loadList();
            },
            loadList(){
                var query={
                    pageIndex:1,
                    pageSize:1000
                }
                app.invoke("BizAction.getCmdbRobotList",[app.token,query],info => {
                    this.robotList=info.list;
                })
            },
            confirmForm(){
                if(this.formItem.robotId==null){
                    app.toast(this.$t('请选择要执行的Robot'));
                    return;
                }
                this.showDialog=false;
                this.args.callback(this.formItem.robotId);
            }
        }
    }
</script>