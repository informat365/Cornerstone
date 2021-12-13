<style scoped>
</style>
  
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false" :loading="false" title="选择" width="700"  @on-ok="confirm">
    <PageContainer style="height:600px" v-model="pageQuery" @change="loadData" pager-size="small">
    <Form inline slot="query"  @submit.native.prevent>
    <FormItem><Input style="width:100px" placeholder="ID" v-model="queryItem.id" @keyup.enter.native="loadData(true)"/></FormItem>
    <FormItem><Input style="width:100px" placeholder="名称" v-model="queryItem.name" @keyup.enter.native="loadData(true)"/></FormItem>
    </Form>
    <div slot="content">
     <table  class="table-content">
            <thead>
                    <tr>
            <th style="width:40px"><Checkbox v-if="args.multiple" v-model="selectAll"></Checkbox></th>
            <TableHeader v-model="queryItem.idSort" @change="loadData(true)"  :sortable="true" name="ID" style="width:80px"/>
            <TableHeader v-model="queryItem.nameSort" @change="loadData(true)"  :sortable="true" name="名称" style=""/>
           
            <TableHeader  v-model="queryItem.createTimeSort" :sortable="true"  @change="loadData(true)" name="创建时间" style="width:120px"/>
           
                    </tr>     
                  </thead>
            <tr @click="singleSelect(item)" @dblclick="dbClickRow" v-for="item in tableData" :key="item.id" class="table-row">
                <td>
                    <Checkbox  v-if="args.multiple" v-model="item.isSelect"></Checkbox>
                    <span v-if="!args.multiple&&currentSelectItem!=null">
                        <Icon v-if="item.id==currentSelectItem.id" type="checkmark" style="color:#0091EA"/>    
                    </span>
                </td>
                <td>{{item.id}} </td>
                <td>{{item.name}} </td>
                
                <td>
                    <div>{{item.createUserName}}</div>
                    <div>{{item.createTime|fmtDateTime}}</div>
                </td>
            </tr>
        </table>
        <div class="table-nodata" v-if="tableData.length==0">
            暂无数据
        </div>
    </div>
  </PageContainer>
    </Modal>
</template>


<script>
    export default {
        name:"SelectObjectDialog",
       mixins: [componentMixin],
       data () {
            return {
                queryItem:{},
                selectAll:false,
                currentSelectItem:null,
            }
        },
        watch:{
            "selectAll":function(val){
                for(var i=0;i<this.tableData.length;i++){
                    this.tableData[i].isSelect=val;
                }
            }
        },
        methods: {
            pageLoad(){
                if(this.args.query){
                    for(var k in this.args.query){
                        this.queryItem[k]=this.args.query[k];
                    }
                }
                this.loadData(true)
            },
            loadData(resetPageIndex){
                this.queryList('BossAction.get'+this.args.domain+'List',resetPageIndex,()=>{
                    if(this.args.list!=null){
                        for(var i=0;i<this.tableData.length;i++){
                            var item=this.tableData[i];
                            item.isSelect=this.containsInList(this.args.list,item)
                        }
                    }
                });
            },
            containsInList(list,item){
                for(var i=0;i<list.length;i++){
                    if(list[i].id==item.id){
                        return true
                    }
                }
                return false;
            },
            singleSelect:function(item){
                this.currentSelectItem=item;
            },
            dbClickRow:function(){
                if(this.args.multiple){
                    return;
                }
                this.confirm();
            },
            confirm:function(){
                var list=[];
                if(this.args.multiple){
                    for(var i=0;i<this.tableData.length;i++){
                        if(this.tableData[i].isSelect){
                            list.push(this.tableData[i]);
                        }
                    }
                }else{
                    if(this.currentSelectItem!=null){
                        list.push(this.currentSelectItem);
                    }
                }
                if(this.args.callback&&list.length>0){
                    this.args.callback(list);
                }
                this.showDialog=false;
            }
        }
    }
</script>