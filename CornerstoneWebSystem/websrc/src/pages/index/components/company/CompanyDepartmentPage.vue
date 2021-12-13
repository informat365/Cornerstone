<style scoped>
.opt-bar{
  background-color: #F1F4F5;
  margin-top: 0;
}
.opt-right{
  text-align: right;
}
  .create-popup{
      position: fixed;
      width:200px;
      background-color: #fff;
      box-shadow: 0 1px 6px rgba(0,0,0,.2);
      z-index:999;
    }
    .create-popup-opt{
      position: absolute;
      top:0;
      left:0;
      width:200px;
      padding-left:20px;
      color:#999;
    }
    .nodata{
        font-size:20px;
        color:#999;
        margin-top:30px;
    }
</style>

<i18n>
{
    "en": {
        "创建部门":"Create Department",
        "导入":"Import"
    },
    "zh_CN": {
        "创建部门":"创建部门",
        "导入":"导入"
    }
}
</i18n>
<template>
    <div class="page">
        <Row class="opt-bar">
          <Col span="18" class="opt-left">
           <template v-if="currentTreeNode">{{currentTreeNode.title}}</template> &nbsp;
          </Col>
          <Col span="6" class="opt-right">
            <Form inline>
              <FormItem>
                <Button style="margin-right:5px"  @click="showImportDialog" type="default" >{{$t('导入')}}</Button>
                <Button @click="showCreateDialog" type="default" icon="md-add">{{$t('创建部门')}}</Button>
              </FormItem>
             </Form>
            </Col>
       </Row>


        <div style="padding:40px">
             <Row>
             <Col span="24" style="flex:1">
                <OrgTree :data="treeData" :horizontal="true"
                @on-node-click="treeNodeClick"
                @on-edit="treeNodeEdit"
                @on-add="treeNodeAdd"
                :collapsable="true" @on-expand="treeExpend"></OrgTree>
             </Col>

            </Row>
        </div>

    </div>
</template>

<script>

export default {
  mixins: [componentMixin],
  data(){
    return {
        treeData: {},
        currentTreeNode:null,
    }
},
methods:{
    pageLoad(){
         this.loadData();
    },
    pageMessage(type){
        if(type=='department.edit'){
            this.loadData();
        }
    },
    loadData(){
        this.currentTreeNode=null;
        app.invoke('BizAction.getDepartmentTree',[app.token,false],(info)=>{
            this.treeData=info[0];
        })
    },

    treeExpend(node){
        node.expand=!node.expand;
    },
    treeNodeClick(e,node){
        this.currentTreeNode=node;

    },
    treeNodeEdit(e,node){
        this.currentTreeNode=node;
        this.showEditDialog();
    },
    treeNodeAdd(e,node){
        this.currentTreeNode=node;
        this.showCreateDialog();
    },
    showEditDialog(){
        var item={
            id:this.currentTreeNode.id,
            name:this.currentTreeNode.title,
            remark:this.currentTreeNode.remark,
            parentId:this.currentTreeNode.parentId
        }
        app.showDialog(CompanyDepartmentEditDialog,{
            id:item.id
        })
    },

    treeSelectChange(item){
        if(item==null||item.length==0){
            this.currentTreeNode=null;
        }else{
            this.currentTreeNode=item[0];
        }
    },
    showCreateDialog(){
        app.showDialog(CompanyDepartmentEditDialog,{
            parentNode:this.currentTreeNode
        })
    },
    showImportDialog(){
        app.showDialog(CompanyDepartmentImportDialog)
    }
  }
}
</script>
