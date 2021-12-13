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
        "一对多关联": "一对多关联",
		"列名": "列名",
		"关联表": "关联表",
		"关联列": "关联列",
		"确定": "确定"
    },
	"zh_CN": {
		"一对多关联": "一对多关联",
		"列名": "列名",
		"关联表": "关联表",
		"关联列": "关联列",
		"确定": "确定"
	}
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('一对多关联')" width="600" >
    <div>
                    <Form ref="form" label-position="right" :rules="formRule" :model="formItem" :label-width="80">
                    <FormItem :label="$t('列名')" >
                        {{formItem.tableId}}.{{formItem.columnId}}
                    </FormItem>
                    <FormItem :label="$t('关联表')" prop="fkTableId">
                            <Select transfer filterable :clearable="true" v-model="formItem.fkTableId" >
                                    <Option  :key="item.name" v-for="item in tableList" :value="item.name">{{item.name}}</Option>
                            </Select>
                    </FormItem>
                    <FormItem :label="$t('关联列')" prop="fkColumnId">
                            <Select transfer filterable :clearable="true" v-model="formItem.fkColumnId" >
                                    <Option  :key="item.name" v-for="item in filterColumnList" :value="item.name">{{item.name}}</Option>
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
                fkTableId:null,
                fkColumnId:null,
            },
            formRule:{
                name:[vd.req],
                fkColumnId:[vd.req],
                fkTableId:[vd.req],
            },
            tableList:[],
            columnList:[],
        }
    },
    computed:{
         filterColumnList:function(){
            var t=[];
            for(var i=0;i<this.columnList.length;i++){
                var q=this.columnList[i];
                if(q.tableName==this.formItem.fkTableId){
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
            if(this.args.callback){
                this.args.callback(this.formItem);
            }
           this.showDialog=false;
        },
    }
}
</script>