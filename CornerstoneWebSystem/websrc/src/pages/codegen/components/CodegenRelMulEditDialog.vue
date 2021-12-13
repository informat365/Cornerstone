<style scoped>
   .table-row{
       padding:8px;
       font-size:13px;
       font-weight: bold;
   }
</style>
<i18n>
{
	"en": {
        "多对多关联": "多对多关联",
		"列名": "列名",
		"中间表": "中间表",
		"中间左列": "中间左列",
		"中间右列": "中间右列",
		"目标表": "目标表",
		"目标列": "目标列",
		"确定": "确定"
    },
	"zh_CN": {
		"多对多关联": "多对多关联",
		"列名": "列名",
		"中间表": "中间表",
		"中间左列": "中间左列",
		"中间右列": "中间右列",
		"目标表": "目标表",
		"目标列": "目标列",
		"确定": "确定"
	}
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('多对多关联')" width="600" >
    <div>
                 <Form ref="form" label-position="right" :rules="formRule" :model="formItem" :label-width="80">
                    <FormItem :label="$t('列名')" >
                        {{formItem.tableId}}.{{formItem.columnId}}
                    </FormItem>
                    <FormItem :label="$t('中间表')" prop="midTableId">
                            <Select transfer filterable :clearable="true" v-model="formItem.midTableId" >
                                    <Option  :key="item.name" v-for="item in tableList" :value="item.name">{{item.name}}</Option>
                            </Select>
                    </FormItem>
                    <FormItem :label="$t('中间左列')" prop="midLeftColumnId">
                            <Select transfer  filterable :clearable="true" v-model="formItem.midLeftColumnId" >
                                    <Option  :key="item.name" v-for="item in filterMidColumnList" :value="item.name">{{item.name}}</Option>
                                </Select>
                    </FormItem>
                      <FormItem :label="$t('中间右列')" prop="midRightColumnId">
                            <Select transfer  filterable :clearable="true" v-model="formItem.midRightColumnId" >
                                    <Option  :key="item.name" v-for="item in filterMidColumnList" :value="item.name">{{item.name}}</Option>
                             </Select>
                    </FormItem>
                    
                     <FormItem :label="$t('目标表')" prop="targetTableId">
                            <Select transfer filterable :clearable="true" v-model="formItem.targetTableId" >
                                    <Option  :key="item.name" v-for="item in tableList" :value="item.name">{{item.name}}</Option>
                            </Select>
                    </FormItem>
                    <FormItem :label="$t('目标列')" prop="targetColumnId">
                            <Select transfer filterable :clearable="true" v-model="formItem.targetColumnId" >
                                    <Option  :key="item.name" v-for="item in filterTargetColumnList" :value="item.name">{{item.name}}</Option>
                                </Select>
                    </FormItem>
                    
                </Form>      
    </div>
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
            formItem: {
                columnId:null,
                midTableId:null,
                midLeftColumnId:null,
                midRightColumnId:null,
                
                targetTableId:null,
                targetColumnId:null,
            
            },
            formRule:{
                name:[vd.req],
                midLeftColumnId:[vd.req],
                midRightColumnId:[vd.req],
                midTableId:[vd.req],
                targetColumnId:[vd.req],
                targetTableId:[vd.req],
            },
            tableList:[],
            columnList:[],
        }
    },
    computed:{
         filterMidColumnList:function(){
            var t=[];
            for(var i=0;i<this.columnList.length;i++){
                var q=this.columnList[i];
                if(q.tableName==this.formItem.midTableId){
                    t.push(q);
                }
            }
            return t;
        },
        filterTargetColumnList:function(){
            var t=[];
            for(var i=0;i<this.columnList.length;i++){
                var q=this.columnList[i];
                if(q.tableName==this.formItem.targetTableId){
                    t.push(q);
                }
            }
            return t;
        }
    },
    methods: {
        pageLoad(){
            this.tableList=this.args.tableList;
            this.columnList=this.args.columnList;
            this.formItem.columnId=this.args.columnId;
            this.formItem.tableId=this.args.tableId;
        },
        confirm:function(){
            this.$refs["form"].validate((r)=>{
                if(r){ this.confirmEdit();}
            });
        },
        confirmEdit:function(){
            this.showDialog=false;
            if(this.args.callback){
                this.args.callback(this.formItem);
            }
           
        },
    }
}
</script>