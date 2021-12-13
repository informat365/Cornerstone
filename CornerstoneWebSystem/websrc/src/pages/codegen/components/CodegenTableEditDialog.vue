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
        "表属性": "表属性",
		"表名称": "表名称",
		"显示名称": "显示名称",
		"编辑": "编辑",
		"显示详情": "显示详情",
		"列表可选": "列表可选",
		"列表排序": "列表排序",
		"表单inline": "表单inline",
		"确定": "确定"
    },
	"zh_CN": {
		"表属性": "表属性",
		"表名称": "表名称",
		"显示名称": "显示名称",
		"编辑": "编辑",
		"显示详情": "显示详情",
		"列表可选": "列表可选",
		"列表排序": "列表排序",
		"表单inline": "表单inline",
		"确定": "确定"
	}
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('表属性')" width="500" >
    <div>
        <Form ref="form" label-position="right" :rules="formRule" :model="formItem" :label-width="80">
                    <FormItem :label="$t('表名称')" >
                        {{formItem.name}}
                    </FormItem>
                    <FormItem :label="$t('显示名称')" prop="displayName">
                        <i-input v-model="formItem.displayName"></i-Input>
                    </FormItem>
                     <FormItem :label="$t('编辑')" prop="isCanUpdate">
                        <i-Switch v-model="formItem.isCanUpdate" ></i-Switch>
                    </FormItem>
                     <FormItem :label="$t('显示详情')" prop="isShowDetailPage">
                        <i-Switch v-model="formItem.isShowDetailPage" ></i-Switch>
                    </FormItem>
                    <FormItem :label="$t('列表可选')" prop="isShowSelect">
                        <i-Switch v-model="formItem.isShowSelect" ></i-Switch>
                    </FormItem>
                    <FormItem :label="$t('列表排序')" prop="isSortable">
                        <i-Switch v-model="formItem.isSortable" ></i-Switch>
                    </FormItem>
                     <FormItem :label="$t('表单inline')" prop="isFormInline">
                        <i-Switch v-model="formItem.isFormInline" ></i-Switch>
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
            displayName:null,
            tableId:null,
            },
            formRule:{
                displayName:[vd.req],
                isCanUpdate:[vd.req],
                isShowDetailPage:[vd.req],
                isShowSelect:[vd.req],
                isFormInline:[vd.req],
                isSortable:[vd.req],       
            },
        }
    },
    
    methods: {
        pageLoad(){
             this.formItem=this.args.table;
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