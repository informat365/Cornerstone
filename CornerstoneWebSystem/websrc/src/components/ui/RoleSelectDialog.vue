<style scoped>
.role-box{
    min-height: 400px;
}
</style>
  
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" title="选择角色" width="500"  @on-ok="confirm">
    <div class="role-box">

        <Form label-position="top">
            <FormItem label="角色选择">
                 <CheckboxGroup v-model="selectedRoles" style="margin-top:10px" >
                    <Checkbox v-for="item in roleList" :key="item.id" :label="item.id">{{item.name}}</Checkbox>
                </CheckboxGroup>
            </FormItem>
            <FormItem v-if="projectList.length>0" label="项目选择">
                <Select filterable transfer  style="width:100%" placeholder="选择项目" v-model="selectedProject" >
                    <Option  v-for="item in projectList" :key="item.id" :value="item.id">{{item.name}}</Option>
                </Select>    
            </FormItem>
         </Form>
    </div>
    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> <Button @click="confirm" type="default" size="large" >确定</Button></Col>
        </Row>
    </div>
    </Modal>
</template>


<script>
export default {
        mixins: [componentMixin],
        data () {
            return {
                selectedRoles:[],
                roleList:[],
                projectList:[],
                selectedProject:null,
            }
        },
        methods: {
            pageLoad(){
                this.loadData();
            },
            loadData(){
                app.invoke('BizAction.getRoleInfoList',[app.token,this.args.roleType],(list)=>{
                    this.roleList=list;
                })
                if(this.args.roleType==1){
                    this.loadProjectList();
                }
            },
            getFromList(list,id){
                for(var i=0;i<list.length;i++){
                    var t=list[i];
                    if(t.id==id){
                        return t;
                    }
                }
                return null;
            },
            loadProjectList(){
                var query={
                    pageIndex:1,
                    pageSize:1000
                }
                app.invoke('BizAction.getAllProjectList',[app.token,query],(info)=>{
                    this.projectList=info.list;
                })   
            },
            confirm:function(){
                if(this.args.roleType==1&&this.selectedProject==null){
                    app.toast('请选择项目');
                    return;
                }
                if(this.selectedRoles.length==0){
                    app.toast('请选择角色');
                    return;
                }
                var roleList=[];
                this.selectedRoles.forEach(item=>{
                    var role=this.getFromList(this.roleList,item);
                    roleList.push(role);
                })
                var project=this.getFromList(this.projectList,this.selectedProject)
                this.showDialog=false;
                if(this.args.roleType==1){
                    roleList.forEach(item=>{
                        item.id=project.id+"-"+item.id;
                        item.name="【"+project.name+"】"+item.name;
                    })
                    this.args.callback(roleList)
                }else{
                    this.args.callback(roleList)
                }
            }
        }
    }
</script>