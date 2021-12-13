<style scoped>
</style>
<i18n>
{
    "en": {
        "迭代管理": "Iteration",
        "名称":"Name",
        "详细描述":"Remark",
        "状态":"Status",
        "删除":"Delete",
        "阶段":"Stage",
        "阶段名称":"Stage name",
        "选择迭代周期":"Choose stage period",
        "删除阶段":"Delete",
        "继续创建下一个":"Continue to create next",
        "保存":"Save",
        "创建":"Create",
        "确认要删除迭代":"Are you sure you want to delete iteration【{0}】?"
    },
    "zh_CN": {
        "迭代管理": "迭代管理",
        "名称":"名称",
        "详细描述":"详细描述",
        "状态":"状态",
        "删除":"删除",
        "阶段":"阶段",
        "阶段名称":"阶段名称",
        "选择迭代周期":"选择迭代周期",
        "删除阶段":"删除阶段",
        "继续创建下一个":"继续创建下一个",
        "保存":"保存",
        "创建":"创建",
        "确认要删除迭代":"确认要删除迭代【{0}】吗？"
    }
}
</i18n> 
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('迭代管理')" width="700" >

    <Form  ref="form" :rules="formRule" :model="formItem"  label-position="top" style="height:400px;padding:15px;margin-bottom:300px">
         <FormItem :label="$t('名称')" prop="name">
            <Input :disabled="projectDisabled" v-model.trim="formItem.name" :placeholder="$t('迭代名称')"></Input>
        </FormItem>
        <FormItem :label="$t('详细描述')" prop="description">
            <Input  :disabled="projectDisabled" v-model.trim="formItem.description" type="textarea" :rows="3" ></Input>
        </FormItem>

        <FormItem :label="$t('状态')">
             <DataDictRadio :disabled="projectDisabled" v-model="formItem.status" type="ProjectIteration.status"/>
                
        </FormItem>

         <FormItem label="" v-if="formItem.id>0">
            <Button :disabled="projectDisabled" @click="deleteItem" type="error" size="large" >{{$t('删除')}}</Button>
        </FormItem> 

         <FormItem :label="$t('阶段')">
            <table class="table-content table-content-small">
            <tbody>
                <tr v-for="item in formItem.stepList" :key="item.id" class="table-row table-row-small">
                    <td>
                       <Input :placeholder="$t('阶段名称')" v-model.trim="item.name"></Input>
                    </td>
                    <td style="width:300px">
                        <DatePicker type="daterange" style="width:300px" v-model="item.dateRange" :placeholder="$t('选择迭代周期')" ></DatePicker>
                    </td>
                   
                    <td style="text-align:right;width:100px;color:#999">
                        <span class="table-row-opt">
                            <IconButton :disabled="projectDisabled" @click="removeStepItem(item)" icon="ios-trash" :tips="$t('删除阶段')"></IconButton>
                        </span>
                    </td>
                </tr>
             </tbody>
            </table>
            <div class="mt10">
                <IconButton  v-if="!projectDisabled" @click="addStep" icon="ios-add" :title="$t('增加阶段')"></IconButton>
            </div>
        </FormItem>

    </Form>

    
    <div slot="footer">
        <Row v-if="!projectDisabled">
            <Col span="12" style="text-align:left;padding-top:5px"> <Checkbox v-if="formItem.id==0" v-model="continueCreate" size="large">{{$t('继续创建下一个')}}</Checkbox></Col>
            <Col span="12" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{formItem.id>0?$t('保存'):$t('创建')}}</Button></Col>
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
                    projectId:0,
                    name:null,
                    description:null,
                    status:1,
                    stepList:[
                        {id:1,name:null,startDate:null,endDate:null,dateRange:[]},
                        {id:2,name:null,startDate:null,endDate:null,dateRange:[]},
                        {id:3,name:null,startDate:null,endDate:null,dateRange:[]},
                    ]
                },
                formRule:{
                    name:[vd.req,vd.name],
                    description:[vd.desc],
                },
                
            }
        },
        methods: {
            pageLoad(){
                if(this.args.item){
                    var t=this.args.item;
                    t.stepList.map(item=>{
                        item.dateRange=[new Date(item.startDate),new Date(item.endDate)]
                    })
                    this.formItem=copyObject(this.args.item);
                }
            },
            deleteItem(){
                app.confirm(this.$t('确认要删除迭代',[this.formItem.name]),()=>{
                    app.invoke('BizAction.deleteProjectIteration',[app.token,this.formItem.id],(info)=>{
                        app.toast('删除成功')
                        app.postMessage('iteration.delete')
                        app.postMessage('iteration.edit')
                        this.showDialog=false;
                    })
                })
            },
            removeStepItem(item){
                this.formItem.stepList.remove(item);
            },
            addStep(){
                this.formItem.stepList.push({
                    name:null,
                    dateRange:[],
                });
            },
            confirm(){
                this.$refs.form.validate((r)=>{
                    if(r){this.confirmForm()}
                });
            },
            confirmForm(){
                //
                if(this.formItem.projectId==0){
                    this.formItem.projectId=this.args.projectId;
                }
                this.formItem.stepList.map(item=>{
                    item.startDate=item.dateRange[0];
                    item.endDate=item.dateRange[1];
                })
                var action=this.formItem.id>0?'BizAction.updateProjectIteration':'BizAction.createProjectIteration';
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.postMessage('iteration.edit')
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