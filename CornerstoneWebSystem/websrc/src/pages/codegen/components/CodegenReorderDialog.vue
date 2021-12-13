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
        "排序": "排序",
		"确定": "确定"
    },
	"zh_CN": {
		"排序": "排序",
		"确定": "确定"
	}
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('排序')" width="600" >
    <div>
        <draggable v-model="tableData" :options="{draggable:'.table-row'}">
				<div class="table-row" :key="element.id" v-for="element in tableData">
	                 {{element.columnName}} 
	                <span style="color:#999"> {{element.displayName}}</span>
	            </div>
	    </draggable>
    </div>
    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{$t('确定')}}</Button></Col>
         </Row>
    </div>
    </Modal>
</template>


<script>

import draggable from 'vuedraggable'
export default {
    mixins: [componentMixin],
    components: {
        draggable,
    },
    data () {
        return {
            tableData:[],
            tableName:null,
            designerDatabaseId:null,
        }
    },
    
    methods: {
        pageLoad(){
            this.designerDatabaseId=this.args.databaseId;
        	this.tableName=this.args.tableName;
            this.tableData=this.args.columnList;
        },
        
        confirm(){
            var list=this.tableData.map(function(item){return item.id})
        	app.invoke('DesignerAction.resetDesignerColumnListOrder',[app.token,this.designerDatabaseId,this.tableName,list],(obj)=>{
        		this.showDialog=false;
        		app.postMessage('designer-column-edit');
            });
           
        }
    }
}
</script>