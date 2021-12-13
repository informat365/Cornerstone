<style scoped>

</style>
<i18n>
{
    "en": {
        "日历": "Calendar",
        "成员角色":"Role",
        "清除所有":"Clear all",
        "清除":"Clear",
        "找不到匹配的用户":"No Data",
        "搜索成员":"Search",
        "添加":"Add",
        "请选择成员角色":"Choose role",
        "请选择用户":"Choose member",
        "添加成功":"Success",
        "确定":"OK"
    },
    "zh_CN": {
        "日历": "日历",
        "成员角色":"成员角色",
        "清除所有":"清除所有",
        "清除":"清除",
        "找不到匹配的用户":"找不到匹配的用户",
        "搜索成员":"搜索成员",
        "添加":"添加",
        "请选择成员角色":"请选择成员角色",
        "请选择用户":"请选择用户",
        "添加成功":"添加成功",
        "确定":"确定"
    }
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('日历')" width="700"  @on-ok="confirm">
    <div style="height:500px;padding:10px">

         <Form  @submit.native.prevent ref="form" :rules="formRule" :model="formItem"  label-position="top" style="height:600px;padding:10px">
            <FormItem :label="'名称'" prop="name">
                 <Input v-model.trim="formItem.name" placeholder="名称"></Input>
            </FormItem>
            <FormItem :label="'参与者'">
                <div>
                    <Tag v-for="item in memberList" :key="item.accountId" :name="item.name" closable @on-close="removeMember(item)">{{item.name}}</Tag>
                    <Button icon="ios-add" type="dashed" size="small" @click="handleAdd">添加</Button>
                </div>
            </FormItem>

             <FormItem label="'创建人'" prop="name">
                 {{formItem.createAccountName}} 于 {{formItem.createTime|fmtDateTime}}创建
             </FormItem>

            <FormItem label="" v-if="formItem.id>0">
                <Button @click="deleteItem" type="error" size="large" >{{$t('删除')}}</Button>
            </FormItem>

        </Form>

    </div>1
    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{$t('确定')}}</Button></Col>
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
                    id:0,
                    name:null,
                    members:[]
                },
                formRule:{
                    name:[vd.req,vd.name]
                },
                memberList:[]
            }
        },
        methods: {
            pageLoad(){
                if(this.args.id){
                    this.loadData();
                }
            },
            loadData(){
                 app.invoke('BizAction.getCalendarById',[app.token,this.args.id],(info)=>{
                    this.formItem=info;
                    info.memberInfos.forEach(item=>{
                        this.memberList.push({
                            accountId:item.id,
                            name:item.name
                        })
                    })
                });
            },
            removeMember(item){
                for(var i=0;i<this.memberList.length;i++){
                    var t=this.memberList[i];
                    if(t.accountId==item.accountId){
                        this.memberList.splice(i,1);
                        return;
                    }
                }
            },
            addToList(item,list){
                for(var i=0;i<list.length;i++){
                    var t=list[i];
                    if(t.accountId==item.accountId){
                        return;
                    }
                }
                list.push(item)
            },
            handleAdd(){
                app.showDialog(MemberSelectDialog,{
                    callback:(e)=>{
                        e.forEach(item=>{
                            this.addToList({accountId:item.accountId,name:item.title},this.memberList)
                        })
                    }
                })
            },
            deleteItem(){
                app.confirm('确认要删除此日历吗？关联的日程也会被一并删除',()=>{
                    app.invoke('BizAction.deleteCalendar',[app.token,this.formItem.id],(info)=>{
                        this.showDialog=false;
                        app.postMessage('calendar.delete')
                    })
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
                this.formItem.members=[];
                this.memberList.forEach(item=>{
                    this.formItem.members.push(item.accountId)
                })
                //
                var action=this.formItem.id==0?'BizAction.addCalendar':"BizAction.updateCalendar";
                app.invoke(action,[app.token,this.formItem],(info)=>{
                   app.toast('操作成功');
                   app.postMessage('calendar.edit')
                   this.showDialog=false;
                })
            }
        }
    }
</script>
