<style scoped>
.dialog-wrap{
    height:calc(100vh - 48px);
    display: flex;
    flex-direction: column;
    overflow: hidden;
    border-radius: 3px;
}
.dialog-form-content{
    flex:1;
    overflow: auto;
}
.form-number{
    font-size:16px;
    font-weight: bold;
    color:#666;
    position:absolute;
    top:15px;
    left:10px;
    display: flex;
    align-items: center;
}

.form-item-box{
    padding-bottom:40px;
    border-top:1px solid #eee;
}
.opt{
    display: flex;
    align-items:center;
    position:absolute;
    top:-26px;
    left:0px;
    width:750px;
}
.opt-gap{
    flex:1;
}
.mr{
    margin-right: 5px;
}
.ml{
    margin-left: 5px;
}
.form-dialog-header{
    position: relative;
    height:48px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #F7F7F7;
    border-bottom: 1px solid #ccc;
    padding: 0 30px;
}
.form-dialog-header-color{
    width:10px;
    height:10px;
    border-radius: 50%;
    display: inline-block;
    margin-right:5px;
}

</style>

 <template>
    <Modal
        ref="dialog"
        v-model="showDialog"
        :closable="true"
        :mask-closable="false"
        :loading="false"
        width="750"
        class="workflow-dialog"
        :footer-hide="true" >
        <div  class="dialog-wrap " :class="{'blur':loading}">

        <div class="form-dialog-header" >
            <div style="font-size:16px;" class="text-no-wrap">
                <div class="form-dialog-header-color" :style="headerStyle"></div>
                <WorkflowNameLabel :id="instance.serialNo"
                                :name="instance.title"
                                :finishType="instance.finishType"
                                :finishText="instance.finishText"
                                :isDone="instance.isFinished"></WorkflowNameLabel>
               <template v-if="instance.currNodeName"> - {{instance.currNodeName}}</template>
            </div>
        </div>

        <div v-if="form.id" class="dialog-form-content scrollbox">
            <WorkflowForm :id="'form-'+form.id" ref="form" v-if="form.id"
                :form="form" :value="formFieldValue"
                :formFieldList="fieldList"></WorkflowForm>
        </div>

        </div>
    </Modal>
</template>


<script>
export default {
    mixins: [componentMixin],
    data() {
        return {
            form:{},
            define:null,
            instance:{},
            fieldList:[],
            formFieldValue:{},
            graphItemProp:{},
            loading:false,
            headerStyle:{
                backgroundColor:'#fff'
            }
        };
    },
    methods: {
        pageLoad() {
            this.loadData();
        },
        loadData() {
            this.loading=true;
            app.invoke('WorkflowAction.getWorkflowInstanceData',[app.token,this.args.id],(info)=>{
                this.form=info.form;
                this.define=info.define;
                this.instance=info.instance;
                info.formFieldList.forEach(item=>{
                    item.editable=false;
                })
                this.fieldList=info.formFieldList;
                this.headerStyle={
                    backgroundColor:info.define.color
                }
                //
                this.setupValue();
                this.loading=false;
            })
        },
        setupValue(){
            if(this.instance.formData){
                this.formFieldValue=JSON.parse(this.instance.formData);
            }
        }
    }
};
</script>
