<style scoped>
</style>
<i18n>
{
    "en": {
        "卡片": "Card",
        "类型": "Type",
        "名称": "Name",
        "卡片数据": "Card data",
        "请选择": "Choose",
        "项目列表":"Project list",
        "选择项目列表":"Choose project list",
        "项目":"Project",
        "选择项目":"Choose project",
        "对象类型":"Object type",
        "选择对象类型":"Choose object type",
        "数据报表":"Data chart",
        "迭代":"Iteration",
        "选择迭代":"Choose iteration",
        "过滤器":"Filter",
        "选择对象过滤器":"Choose filter",
        "统计到日期":"Date stat",
        "继续创建下一个":"Continue create next",
        "保存":"Save",
        "操作成功":"Success"
    },
    "zh_CN": {
        "卡片": "卡片",
        "类型": "类型",
        "名称": "名称",
        "卡片数据": "卡片数据",
        "请选择": "请选择",
        "项目列表":"项目列表",
        "选择项目列表":"选择项目列表",
        "项目":"项目",
        "选择项目":"选择项目",
        "对象类型":"对象类型",
        "选择对象类型":"选择对象类型",
        "数据报表":"数据报表",
        "迭代":"迭代",
        "选择迭代":"选择迭代",
        "过滤器":"过滤器",
        "选择对象过滤器":"选择对象过滤器",
        "统计到日期":"统计到日期",
        "继续创建下一个":"继续创建下一个",
        "保存":"保存",
        "操作成功":"操作成功"
    }
}
</i18n> 
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('卡片')" width="600">

    <Form  @submit.native.prevent ref="form" :rules="formRule" :model="formItem" 
        label-position="top" style="min-height:600px;padding:15px">
        <Row>
            <Col span="11">
               <FormItem :label="$t('名称')" prop="name">
                <Input v-model.trim="formItem.name" ></Input> 
                </FormItem>
            </Col>
            <Col span="11" offset="2">
                 <FormItem :label="$t('类型')" prop="type">
                    <DataDictSelect style="width:100%" :placeholder="$t('请选择')" type="DashboardCard.type" v-model="formItem.type"></DataDictSelect>
                </FormItem>
            </Col>
            
        </Row>

        <Divider v-if="formItem.type>0"  >{{$t('卡片数据')}}</Divider>
     

        <FormItem v-if="formItem.type==3" prop="projectIdList" :label="$t('项目列表')">
            <Select transfer multiple filterable  style="width:100%" :placeholder="$t('选择项目列表')" v-model="formItem.projectIdList" >
                <Option  v-for="item in projectList" :key="item.id" :value="item.id">{{item.name}}</Option>
            </Select>   
        </FormItem>


        <FormItem v-if="formItem.type==1||formItem.type==2||formItem.type==6||formItem.type==5" :label="$t('项目')">
             <Select transfer filterable  style="width:100%" :placeholder="$t('选择项目')" v-model="formItem.projectId" >
                <Option  v-for="item in projectList" :key="item.id" :value="item.id">{{item.name}}</Option>
            </Select>  
        </FormItem>

        
        
        <Row>
            <Col span="11">
                  <FormItem v-if="formItem.type==1||formItem.type==2" prop="objectType" :label="$t('对象类型')">
                        <Select transfer filterable  style="width:100%" :placeholder="$t('选择对象类型')" v-model="formItem.objectType" >
                            <Option  v-for="item in objectTypeList"  :key="item.id" :value="item.id">{{item.name}}</Option>
                        </Select>  
                    </FormItem>
            </Col>
            <Col span="11" offset="2">
                    <FormItem v-if="formItem.type==1" prop="chartId" :label="$t('数据报表')">
                        <DataDictSelect transfer style="width:100%" :placeholder="$t('请选择')" type="DashboardCard.chartId" v-model="formItem.chartId"></DataDictSelect>  
                    </FormItem>
            </Col>
        </Row>

         <Row>
            <Col span="11">
                  <FormItem v-if="formItem.type==1||formItem.type==2||formItem.type==5" :label="$t('迭代')">
                        <Select transfer filterable clearable style="width:100%" :placeholder="$t('选择迭代')" v-model="formItem.iterationId" >
                            <Option  v-for="item in iterationList"  :key="item.id" :value="item.id">{{item.name}}</Option>
                        </Select>  
                    </FormItem>
            </Col>
            <Col span="11" offset="2">
                     <FormItem v-if="formItem.type==1||formItem.type==2" :label="$t('过滤器')">
                        <Select transfer filterable clearable style="width:100%" :placeholder="$t('选择对象过滤器')" v-model="formItem.filterId" >
                            <Option  v-for="item in filterList" :key="item.id" :value="item.id">{{item.name}}</Option>
                        </Select>  
                    </FormItem>
            </Col>
        </Row>
  
     
        <FormItem v-if="formItem.type==4" prop="dueDate" :label="$t('统计到日期')">
              <ExDatePicker transfer type="date" style="width:130px" v-model="formItem.dueDate" :placeholder="$t('请选择')"></ExDatePicker>  
        </FormItem>


    </Form>
    <div slot="footer">
        <Row>
            <Col span="12" style="text-align:left;padding-top:5px"> <Checkbox v-model="continueCreate" v-if="formItem.id==0"  size="large">{{$t('继续创建下一个')}}</Checkbox></Col>
            <Col span="12" style="text-align:right"> 
                <Button @click="confirm" type="default" size="large">{{$t('保存')}}</Button></Col>
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
                    type:null,
                    chartId:null,
                    projectId:null,
                    objectType:null,
                    iterationId:null,
                    projectIdList:[]
                },
                formRule:{
                    name:[vd.req,vd.name],
                    dueDate:[vd.req],
                    objectType:[vd.req],
                    chartId:[vd.req]
                },
                cardWidth:{
                    "1":2,//数据报表
                    "2":2,//数字指标
                    "3":4,//项目列表
                    "4":2,//日期统计
                    "5":4,//迭代概览
                    "6":4,//项目活动图
                },
                projectList:[],
                objectTypeList:[],
                filterList:[],
                iterationList:[]
            }
        },
        watch:{
            "formItem.projectId":function(val){
                this.loadObjectTypeList();
                this.loadFilterList();
                this.loadIterationList();
            }
        },
        methods: {
            pageLoad(){
                if(this.args.dashboardId){
                     this.formItem.dashboardId=this.args.dashboardId;
                }
                if(this.args.id){
                    this.loadCard(this.args.id);
                }
                this.loadProjectList();
            },
            loadCard(id){
                 app.invoke( "BizAction.getDashboardCardById",[app.token,id],info => {
                    this.formItem=info;
                })
            },
            loadProjectList(){
                app.invoke( "BizAction.getMyProjectList",[app.token],list => {
                    this.projectList=list;
                })
            },
            loadIterationList(){
                if(this.formItem.projectId==null||this.formItem.projectId==0){
                    this.iterationList=[];
                    return;
                }
                app.invoke( "BizAction.getProjectIterationInfoList",[app.token,this.formItem.projectId],list => {
                    this.iterationList=list;
                })
            },
            loadFilterList(){
                if(this.formItem.projectId==null||this.formItem.projectId==0){
                    this.filterList=[];
                    return;
                }
                var query={
                    projectId:this.formItem.projectId,
                    objectType:this.formItem.objectType
                }
                app.invoke( "BizAction.getFilterInfoList",[app.token,query],list => {
                    this.filterList=list;
                })
            },
            loadObjectTypeList(){
                if(this.formItem.projectId==null||this.formItem.projectId==0){
                    this.objectTypeList=[];
                    return;
                }
                this.objectTypeList=[];
                app.invoke( "BizAction.getProjectModuleInfoList",[app.token,this.formItem.projectId],list => {
                    for(var i=0;i<list.length;i++){
                        var t=list[i];
                        if(t.objectType>0&&t.isEnable){
                            this.objectTypeList.push({
                                id:t.objectType,
                                name:t.name
                            })
                        }
                    }
                })
            },
            confirm(){
                this.$refs.form.validate((r)=>{
                    if(r){
                        this.confirmForm()
                    }
                });
            },
            confirmForm(){
                if(this.formItem.id==0){
                    this.formItem.width=this.cardWidth[this.formItem.type+""];
                    this.formItem.height=6;
                }
                var action=this.formItem.id==0?'BizAction.addDashboardCard':"BizAction.updateDashboardCard";
                app.invoke(action,[app.token,this.formItem],(info)=>{
                    app.toast(this.$t('操作成功'))
                    app.postMessage('card.edit');
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