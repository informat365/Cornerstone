<style scoped>
</style>
  
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" title="分享终端" width="700" :footer-hide="true">
   
        <Form  label-position="top" style="height:400px;padding:15px">
            <FormItem label="主机">
                {{name}}
            </FormItem>
        
            <FormItem label="协作模式">
               <i-Switch v-model="shareMode"></i-Switch>
            </FormItem>

            <FormItem label="访问地址">
            <Row style="background-color: #F7F7F7;border:1px solid #DEDEDE;padding:10px;margin-bottom:15px">
                <Col span="24">
                <div> {{host}}/#/pm/terminal/{{shareInfo.token}}/terminal_share</div>
                <div style="color:#666;font-size:12px;margin-top:10px;margin-bottom:10px">设置协作模式后，参与的用户均可以操作终端</div> 

                </Col>
            </Row>

            </FormItem>
          
           
        </Form>
    </Modal>
</template>


<script>
    export default {
        mixins: [componentMixin],
        data () {
            return {
                name:"",
                shareMode:false,
                shareInfo:{},
                host:"",
            }
        },
        watch:{
            shareMode(val){
                this.getToken();
            }
        },
        methods: {
            pageLoad(){
                this.name=this.args.info.machineName;
                this.host=app.getHost();
                this.getToken();
            },
            getToken(){
                var type=2;
                if(this.shareMode){
                    type=1;
                }
                app.invoke("BizAction.shareLoginMachine",[app.token,this.args.info.token,type],info => {
                   this.shareInfo=info;
                });
            },
            confirm:function(){
                this.showDialog=false;
            }
        }
    }
</script>