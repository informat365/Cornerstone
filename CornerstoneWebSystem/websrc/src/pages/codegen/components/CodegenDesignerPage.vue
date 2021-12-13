<style scoped>
.page{
  min-height: 400px;
  overflow: hidden;
}
.opt-bar{
  background-color: #F1F4F5;
  margin-top: 0;
}
.opt-right{
  text-align: right;
}
.table-info-bar{
  font-size:12px;
  font-weight:bold;
  color:#999999;
  text-align:right;
}

    .table-box{
        width:220px;
        border-right:1px solid #eee;
        height: calc(100vh - 103px);
        overflow-y:auto;
    }
    .column-box{
        height: 100%;
        overflow-y:auto;
        height: calc(100vh - 103px);
        flex:1;
    }
    .main-box{
        display: flex;
        background-color: #fff;
        height: calc(100vh - 103px);
        overflow: hidden;
    }
   
    .th-edit{
        color:#3292DF;
    }
    .table-item-selected{
        background-color: #FFFCDF;
    }
    .display-name input{
        color:#3292DF;
    }
    .table-subtitle{
        color:#999;
    }
    .table-item-name{
        display: flex;
        align-items: center;
    }
</style>
<i18n>
{
	"en": {
        "数据库": "数据库",
		"重新加载数据表": "重新加载数据表",
		"关系图": "关系图",
		"生成": "生成",
		"表名": "表名",
		"搜索表": "搜索表",
		"暂无数据": "No Data",
		"列名": "列名",
		"显示名称": "显示名称",
		"表现方式": "表现方式",
		"基础属性": "基础属性",
		"展示与排序": "展示与排序",
		"取值范围": "取值范围",
		"数据字典": "数据字典",
		"排序": "排序",
		"外键": "外键",
		"一对多关联": "一对多关联",
		"多对多关联": "多对多关联",
		"关联": "关联",
		"分组名称/表现方式": "分组名称/表现方式",
		"是否必选": "是否必选",
		"可否编辑": "可否编辑",
		"查询条件": "查询条件",
		"区间查询": "区间查询",
		"列表显示": "列表显示",
		"详情显示": "详情显示",
		"可否排序": "可否排序",
		"刷新成功": "刷新成功",
		"请选择数据库": "请选择数据库",
		"请选择表": "请选择表",
		"新增成功": "新增成功",
		"确认要删除此数据列吗": "确认要删除此数据列吗？",
		"保存成功": "保存成功"
    },
	"zh_CN": {
		"数据库": "数据库",
		"重新加载数据表": "重新加载数据表",
		"关系图": "关系图",
		"生成": "生成",
		"表名": "表名",
		"搜索表": "搜索表",
		"暂无数据": "暂无数据",
		"列名": "列名",
		"显示名称": "显示名称",
		"表现方式": "表现方式",
		"基础属性": "基础属性",
		"展示与排序": "展示与排序",
		"取值范围": "取值范围",
		"数据字典": "数据字典",
		"排序": "排序",
		"外键": "外键",
		"一对多关联": "一对多关联",
		"多对多关联": "多对多关联",
		"关联": "关联",
		"分组名称/表现方式": "分组名称/表现方式",
		"是否必选": "是否必选",
		"可否编辑": "可否编辑",
		"查询条件": "查询条件",
		"区间查询": "区间查询",
		"列表显示": "列表显示",
		"详情显示": "详情显示",
		"可否排序": "可否排序",
		"刷新成功": "刷新成功",
		"请选择数据库": "请选择数据库",
		"请选择表": "请选择表",
		"新增成功": "新增成功",
		"确认要删除此数据列吗": "确认要删除此数据列吗？",
		"保存成功": "保存成功"
	}
}
</i18n>
<template>
    <div class="page">
         <Row class="opt-bar">
          <Col span="18" class="opt-left">
               <Form inline>
                  <FormItem>
                       <Select style="width:200px" filterable @on-change="loadTable" :clearable="true" v-model="formItem.databaseId" :placeholder="$t('数据库')">
                          <Option v-for="item in dbList" :key="'db_'+item.id" :value="item.id">{{item.name}}</Option>
                      </Select>
                    </FormItem>
                   
                    <FormItem>
                        <Button @click="reloadTable()" type="default" style="margin-right:8px;">{{$t('重新加载数据表')}}</Button>
                        <Button :disabled="formItem.databaseId==null" @click="showRelation()" type="success">{{$t('关系图')}}</Button>
                        
                    </FormItem>
             </Form>

          </Col>
          <Col span="6" class="opt-right">
            <Form inline>
             


              <Dropdown placement="bottom-end">
                <FormItem>
                    <Button type="default" >{{$t('生成')}}
                         <Icon style="margin-left:5px" type="ios-arrow-down"></Icon>
                    </Button>
                </FormItem>
                <DropdownMenu slot="list" style="text-align:left">
                    <DropdownItem @click.native="genCode(template.id)" v-for="template in templateList"
                     :key="'t'+template.id">{{template.name}}</DropdownItem>
                </DropdownMenu>
            </Dropdown>

             </Form>
            </Col>
       </Row>
     
    <div class="main-box">
        <div class="table-box">
            <table class="table-content">
                <thead>
                        <tr >
                          <th>{{$t('表名')}}</th>
                        </tr>
                      </thead>
                <tr v-if="tableList.length>0">
                    <td>              
                        <Input :placeholder="$t('搜索表')" style="width:100%" v-model="searchTableName"></Input>
                    </td>
                </tr>
                <tr class="table-row" v-for=" item in filtedTableList"  @click="selectTable(item)" :key="item.name">
                    <td :class="{'table-item-selected':formItem.tableId==item.name}">
                        <div class="table-item-name" >
                                <div class="text-no-wrap" style="max-width:150px;display:inline-block">{{item.name}}</div>
                                <Button v-if="formItem.tableId==item.name" @click="editTable(item)" size="small" icon="ios-settings"></Button>
                        </div>
                        <div class="table-subtitle text-no-wrap" style="max-width:150px">{{item.displayName}}</div>
                    </td>
                </tr>
            </table>
            <div class="table-nodata" v-if="tableList.length==0">
                {{$t('暂无数据')}}
            </div>
        </div>
            <div class="column-box">
            
            <table class="table-content">
                <thead >
                        <tr>
                          <th style="min-width:120px">{{$t('列名')}}</th>
                          <th  style="width:140px">{{$t('显示名称/表现方式')}}</th>
                          
                          <th  style="width:170px">{{$t('基础属性')}}</th>
                          <th  style="width:170px">{{$t('展示与排序')}}</th>
                          
                          <th  style="width:100px">{{$t('取值范围')}}</th>
                          <th  style="min-width:100px">{{$t('数据字典')}}</th>
                          <th @click="resortColumn" style="width:60px;text-align:right;color:#0091EA;">{{$t('排序')}}</th>
                        </tr>
                      </thead>
                <tbody>
                <tr class="table-row" v-for="(item,index) in columnDataList" :key="item.id">
                    <td>
                        <div v-if="item.fieldType==1">
                            <div >
                                <Button v-if="item.foreignTableName" @click="addFkRow(item)" size="small" type="default" shape="circle" icon="md-add"></Button>
                               
                                <Button v-if="intDbType(item)" @click="addRelRow(item)" size="small" type="success" shape="circle" icon="ios-link"></Button>
                      			<Button v-if="intDbType(item)" @click="addMulRelRow(item)" size="small" type="success" shape="circle" icon="md-link"></Button>
                      
                                <span style="font-weight:700;font-size:14px">{{item.columnName}}</span>
                            </div>
                            <div class="table-subtitle">
                                    {{item.dbType}} {{item.dbSize}} 
                                    <span v-if="item.dbNullable=='YES'">NULLABLE</span>
                                    <span v-if="item.dbNullable=='NO'">NOTNULL</span>
                            </div>
                        </div>
                        <div v-if="item.fieldType==2">
                            <div >
                                <Button @click="deleteItem(item)" size="small" type="error" shape="circle" icon="ios-trash"></Button>
                                <span>{{item.relationDesignerColumnName}} {{$t('外键')}} {{item.foreignTableName}}.{{item.foreignColumnName}}</span>
                            </div>
                            <div class="table-subtitle">
                                {{item.domainName}}
                            </div>
                            <div class="table-subtitle">
                                    {{item.dbType}} {{item.dbSize}} 
                                    <span v-if="item.dbNullable=='YES'">NULLABLE</span>
                                    <span v-if="item.dbNullable=='NO'">NOTNULL</span>
                            </div>
                        </div>
                         <div v-if="item.fieldType==3">
                            <div >
                                <Button @click="deleteItem(item)" size="small" type="error" shape="circle" icon="ios-trash"></Button>
                                <span>{{item.relationDesignerColumnName}} {{$t('一对多关联')}} </span>
                            </div>
                            <div class="table-subtitle">
                                {{item.domainName}}
                            </div>
                        </div>
                        <div v-if="item.fieldType==4">
                            <div >
                                <Button @click="deleteItem(item)" size="small" type="error" shape="circle" icon="ios-trash"></Button>
                                <span>{{item.relationDesignerColumnName}} {{$t('多对多关联')}} </span>
                            </div>
                            <div class="table-subtitle">
                                {{item.domainName}}
                            </div>
                        </div>


                          <div style="border-top:1px solid #eee;margin-top:8px;padding-top:8px" v-if="item.fieldType==1||item.fieldType==3||item.fieldType==4">
                            <span style="font-weight:bold;margin-right:10px;color:#999">{{$t('外键')}}/{{$t('关联')}}</span>
                            <span style="margin-right:10px" v-if="item.foreignTableName">{{item.foreignTableName}}.{{item.foreignColumnName}}</span>
                            <span style="color:#EE6B46" v-if="item.relationTableName">{{item.relationTableName}}.{{item.relationColumnName}}</span>
                            <span style="color:#EE6B46" v-if="item.targetTableName"> &lt;-&gt;{{item.relationRightColumnName}} {{$t('关联')}} {{item.targetTableName}}.{{item.targetColumnName}}</span>
                            <Button v-if="intDbType(item)&&item.fieldType==1" @click="editFk(item,index)" size="small" type="text" shape="circle" icon="ios-link"></Button>
                        </div>

                        
                    </td>
                    <td>
                       

                        <div><Input :placeholder="$t('显示名称')" class="display-name" style="width:100%;" v-model="item.displayName"  @on-blur="saveItem(item)"></Input></div>
                        <div><Input :placeholder="$t('分组名称')"  style="width:100%;margin-top:5px" v-model="item.sectionName"  @on-blur="saveItem(item)"></Input></div>
                        
                        <div style="margin-top:5px">
                            
                            <Select :placeholder="$t('表现形式')"  v-model="item.showType" @on-change="saveItem(item)">
                                    <Option :key="item.value" v-for="item in dataDict['DesignerColumn.showType']" :value="item.value">{{item.name}}</Option>
                            </Select>

                        </div>
                    
                    </td>
                    
                    <td>
                    	<div>{{$t('是否必选')}}:<i-Switch style="margin-left:15px" v-model="item.isRequire" @on-change="saveItem(item)"></i-Switch></div>
                    	<div style="margin-top:5px">{{$t('可否编辑')}}:<i-Switch style="margin-left:15px" v-model="item.isCanModify"  @on-change="saveItem(item)"></i-Switch></div>
               
                    </td>
                    
					<td>
                    <div v-if="item.fieldType!=3&&item.fieldType!=4">{{$t('查询条件')}}:<i-Switch style="margin-left:15px" v-model="item.isQuery" @on-change="saveItem(item)"></i-Switch></div>
                    
                    <div v-if="item.fieldType!=3&&item.fieldType!=4&&rankQueryType(item)" style="margin-top:5px">{{$t('区间查询')}}:<i-Switch style="margin-left:15px" v-model="item.isRangeQuery" @on-change="saveItem(item)"></i-Switch></div>
                    
                    <div  v-if="item.fieldType!=3&&item.fieldType!=4" style="margin-top:5px">{{$t('列表显示')}}:<i-Switch style="margin-left:15px" v-model="item.isShowInList" @on-change="saveItem(item)"></i-Switch></div>
                    <div  style="margin-top:5px">{{$t('详情显示')}}:<i-Switch style="margin-left:15px" v-model="item.isShowInDetailPage" @on-change="saveItem(item)"></i-Switch></div>
                    <div  v-if="item.fieldType==1" style="margin-top:5px">{{$t('可否排序')}}:<i-Switch style="margin-left:15px" v-model="item.isCanOrder"  @on-change="saveItem(item)"></i-Switch></div>
                    </td>
                    <td>
                        <div><Input style="width:100%" v-model="item.minValue" v-if="item.fieldType!=3&&item.fieldType!=4" @on-blur="saveItem(item)"></Input></div>
                        <div style="margin-top:5px"><Input style="width:100%" v-model="item.maxValue" v-if="item.fieldType!=3&&item.fieldType!=4"  @on-blur="saveItem(item)"></Input></div>
                    </td>
                    
                    <td><Input type="textarea" style="width:100%" v-model="item.dataDict" v-if="item.fieldType==1" @on-blur="saveItem(item)"></Input></td>
                    <td style="text-align:right">
                        <div><Button @click="moveItem(item,-1)" size="small" type="text" icon="md-arrow-round-up"></Button></div>
                        <div><Button @click="moveItem(item,1)" size="small" type="text" icon="md-arrow-round-down"></Button></div>
                        
                    </td>
                </tr>
                   
                </tbody>
               
            </table>
            <div class="table-nodata" v-if="columnDataList.length==0">
                    暂无数据
            </div>
           
        </div>
        </div> 
    </div>
</template>

<script>

export default {
    mixins: [componentMixin],
    data(){
        return {
            formItem: {
                templateId: null,
                databaseId: null,
                tableId:null,
            },
            templateId:null,
            columnDataList:[],
            tableList:[],
            columnList:[],
            currentEditRow:null,
            currentEditRowIndex:null,
            searchTableName:null,
            dbList:[],
            templateList:[],
            dataDict:app.dataDicts
        }
    },
    computed:{
        filtedTableList:function(){
            var r=[];
            if(this.searchTableName==null||this.searchTableName==""){
                return this.tableList;
            }
            for(var i=0;i<this.tableList.length;i++){
                var t=this.tableList[i];
                var q=t.name+""+t.remarks;
                if(q.indexOf(this.searchTableName)!=-1){
                    r.push(t);
                }
            }
            return r;
        }
    },
    methods:{
        pageLoad(){
            this.loadDatabase();
            this.loadTemplate();
        },
        pageUpdate(){
        
        },
        pageMessage:function(type,content){
            if(type=='table-edit'){
                this.loadTable();
            }
            if(type=='designer-column-edit'){
            	this.loadColumn(false);
            }
        },
        rankQueryType:function(item){
            if(!item.isQuery){
                return false;
            }
            var t=item.dbType.toUpperCase();
            return t.indexOf('INT')!=-1||t.indexOf('DATE')!=-1||t.indexOf('TIME')!=-1||t.indexOf('FLOAT')!=-1||t.indexOf('DOUBLE')!=-1;
        },
        intDbType:function(item){
            return item.dbType.toUpperCase().indexOf('INT')!=-1;
        },
        loadDatabase(){
            app.invoke('DesignerAction.getMyDesignerDatabases',[app.token],(list)=>{
                this.dbList=list;      
            });
        },
        loadTemplate(){
            var query={pageIndex:1,pageSize:9999}
            app.invoke('DesignerAction.getDesignerTemplates',[app.token,query],(obj)=>{
                this.templateList=obj.list;      
            });
        },
        loadTable:function(){
            this.tableList=[];
            if(this.formItem.databaseId){
                app.invoke('DesignerAction.getTableInfos',[app.token,this.formItem.databaseId],(obj)=>{
                    this.tableList=obj.tableInfos;
                    this.columnList=obj.columnInfos;     
                });
            }
        },
        reloadTable:function(){
            if(this.formItem.databaseId){
                app.invoke('DesignerAction.refreshDesignerDatabase',[app.token,this.formItem.databaseId],(obj)=>{
                    toast(this.$t("刷新成功"))
                });
            }
            
        },
        resortColumn:function(){
        	if(this.columnDataList==null){
        		return;
        	}
        	app.showDialog(CodegenReorderDialog,{
                databaseId:this.formItem.databaseId,
                tableName:this.formItem.tableId,
                columnList:this.columnDataList
            });
        },
        selectTable:function(item){
            this.formItem.tableId=item.name;
            this.loadColumn(true);
        },
        editTable:function(item){
            app.showDialog(CodegenTableEditDialog,{
                table:item,
                callback:this.confirmEditTable
            });
        },
        confirmEditTable:function(item){
            app.invoke('DesignerAction.updateDesignerTable',[app.token,item],(obj)=>{  
                this.loadColumn(false);
            });
        },
        loadColumn:function(loadMataData){
            app.invoke('DesignerAction.getDesignerColumns',[app.token,
                this.formItem.databaseId,
                this.formItem.tableId,loadMataData],(obj)=>{
                    this.columnDataList=obj; 
            });
        },
        showRelation:function(){
            if(this.formItem.databaseId==null){
                toast(this.$t("请选择数据库"))
                return;
            }
            app.showDialog(CodegenRelVizDialog,{
                id:this.formItem.databaseId
            });
        },
        genCode:function(templateId){
            if(this.formItem.databaseId==null){
                toast(this.$t("请选择数据库"))
                return;
            }
            if(this.formItem.tableId==null){
                toast(this.$t("请选择表"))
                return;
            }
            this.formItem.templateId=templateId;
            app.showDialog(CodegenResultDialog,this.formItem);
        },
        addFkRow:function(item){
            this.currentEditRow=item;
            app.showDialog(CodegenFKRowEditDialog,{
                    tableId:this.formItem.tableId,
                    columnId:item.columnName,
                    fkTableId:item.foreignTableName,
                    tableList:this.tableList,
                    columnList:this.columnList,
                    callback:this.confirmAddFkRow
                }
        	);
        },
        confirmAddFkRow:function(item){
            var target={
                relationDesignerColumnId:this.currentEditRow.id,
                domainName:item.name,
                foreignColumnName:item.fkColumnId,
            }
            app.invoke('DesignerAction.addForeignColumn',[app.token,target],(obj)=>{
                toast(this.$t("新增成功"))  
                this.loadColumn(false);
            });
        },
        moveItem:function(item,delta){
            app.invoke('DesignerAction.moveDesignerColumn',[app.token,item.id,delta],(obj)=>{
                this.loadColumn(false);
            });
        },
        deleteItem:function(item){
            app.confirm(this.$t('确认要删除此数据列吗'),()=>{
                    app.invoke('DesignerAction.deleteDesignerColumn',[app.token,item.id],(obj)=>{
                        this.loadColumn(false);
                    });
            });
        },
        editFk:function(item,index){
            this.currentEditRow=item;
            this.currentEditRowIndex=index;
            app.showDialog(CodegenFKEditDialog,{
                    tableId:this.formItem.tableId,
                    columnId:item.columnName,
                    tableList:this.tableList,
                    columnList:this.columnList,
                    callback:this.confirmEditFk
                }
        	);
        },
        confirmEditFk:function(item){
            this.currentEditRow.foreignTableName=item.fkTableId;
            this.currentEditRow.foreignColumnName=item.fkColumnId;
            this.saveItem(this.currentEditRow,()=>{
                this.loadColumn(false);
            });  
        },
        addMulRelRow:function(item){
            this.currentEditRow=item;
            app.showDialog(CodegenRelMulEditDialog,{
                    tableId:this.formItem.tableId,
                    columnId:item.columnName,
                    tableList:this.tableList,
                    columnList:this.columnList,
                    callback:this.confirmAddMulRel
                }
        	);
        },
        confirmAddMulRel:function(item){
            var target={
                    relationDesignerColumnId:this.currentEditRow.id,
                    relationTableName:item.midTableId,
                    relationColumnName:item.midLeftColumnId,
                    relationRightColumnName:item.midRightColumnId,
                    
                    targetTableName:item.targetTableId,
                    targetColumnName:item.targetColumnId,
            }
            app.invoke('DesignerAction.addRelationColumn',[app.token,target],(obj)=>{
               toast(this.$t("新增成功"))  
               this.loadColumn(false);
            });
        },
        addRelRow:function(item){
            this.currentEditRow=item;
            app.showDialog(CodegenRelEditDialog,{
                    tableId:this.formItem.tableId,
                    columnId:item.columnName,
                    tableList:this.tableList,
                    columnList:this.columnList,
                    callback:this.confirmAddRel
                }
        	);
        },
        confirmAddRel:function(item){
            var target={
                    relationDesignerColumnId:this.currentEditRow.id,
                    relationTableName:item.fkTableId,
                    relationColumnName:item.fkColumnId,
            }
            app.invoke('DesignerAction.addRelationColumn',[app.token,target],(obj)=>{
               toast(this.$t("新增成功"))  
               this.loadColumn(false);
            });
        },
        saveItem:function(item,callback){
            app.invoke('DesignerAction.updateDesignerColumn',[app.token,item],(obj)=>{
                toast(this.$t("保存成功"))  
                if(callback){
                    callback();
                }
            });
        },
  }
}
</script>