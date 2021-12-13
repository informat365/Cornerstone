<style scoped>
.page{
  min-height: 400px;
}
.opt-bar{
  background-color: #F1F4F5;
  margin-top: 0;
  height: 54px;
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
.main-container{
    width:100%;
    height: calc(100vh - 105px);
    display: flex;
}
.change-list-box{
    width:300px;
    border-right: 1px solid #eee;
    height: calc(100vh - 105px);
    background-color: #fff;
    overflow: auto;
}
.change-info-box{
    flex:1;
    height: calc(100vh - 103px);
    overflow: auto;
    background-color: #fff;
    padding:10px;
}

.change-item{
    padding:10px 20px;
    border-bottom: 1px solid #eee;
    cursor: pointer;
}
.change-item-selected{
    border-right:3px solid #009AF4;
}
</style>
<i18n>
{
	"en": {
        "数据库表结构变更": "数据库表结构变更",
        "DDL对比": "DDL对比",
        "DML对比": "DML对比",
		"表": "表"
    },
	"zh_CN": {
		"数据库表结构变更": "数据库表结构变更",
        "DDL对比": "DDL对比",
        "DML对比": "DML对比",
		"表": "表"
	}
}
</i18n>
<template>
    <div class="page">
         <Row class="opt-bar">
          <Col span="18" class="opt-left">
               {{$t('数据库表结构变更')}}
          </Col>
          <Col span="6" class="opt-right">
            <Form inline>
              <FormItem v-if="selectedItem!=null">
                    <Dropdown placement="bottom-end" >
                        <Button type="default">{{$t('DDL对比')}}
                                <Icon style="margin-left:5px" type="ios-arrow-down"></Icon>
                            </Button>
                        <DropdownMenu slot="list" style="text-align:left">
                            <DropdownItem @click.native="compareDdlCode(template.id)" v-for="template in changeList"
                            :key="'t'+template.id">{{template.createTime|fmtDateTime}}</DropdownItem>
                        </DropdownMenu>
                    </Dropdown>
              </FormItem>

            <FormItem v-if="selectedItem!=null">
               <Dropdown placement="bottom-end" >
                  <Button type="default">{{$t('DML对比')}}
                         <Icon style="margin-left:5px" type="ios-arrow-down"></Icon>
                    </Button>
                <DropdownMenu slot="list" style="text-align:left">
                    <DropdownItem @click.native="compareDmlCode(template.id)" v-for="template in changeList"
                     :key="'t'+template.id">{{template.createTime|fmtDateTime}}</DropdownItem>
                </DropdownMenu>
                </Dropdown>
            </FormItem>
             </Form>
            </Col>
       </Row>
     
    <div class="main-container">
        <div class="change-list-box">
            <div @click="selectItem(item)" v-for="item in changeList" :key="'log_'+item.id" class="change-item" :class="{'change-item-selected':item==selectedItem}">
                <div style="font-weight:bold">{{item.createTime|fmtDateTime}} 
                    【{{item.tableCount}}{{$t('表')}}】
                    <template v-if="item.isDdlChanged">【DDL】</template>
                    <template v-if="item.isDmlChanged">【DML】</template>
                </div>
                <div style="color:#999">{{item.databaseName}} {{item.databaseInstanceId}}</div>
            </div>
        </div>
        <div class="change-info-box">
            <div  v-if="changeInfo">
                <div>
                    <RadioGroup v-model="changeType" >
                        <Radio v-for="item in changeTypeList" :key="item" :label="item"></Radio>
                    </RadioGroup>
                </div>
                <MonacoEditor style="height:calc(100vh - 160px)" v-model="currentChangeContent" mode="sql" ></MonacoEditor>
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
           changeList:[],
           selectedItem:null,
           changeInfo:null,
           changeTypeList:["DDL","DML"],
           changeType:null,
        }
    },
    computed:{
        currentChangeContent(){
            if(this.changeType=='DDL'){
                return this.changeInfo.ddl;
            }
            if(this.changeType=='DML'){
                return this.changeInfo.dml;
            }
            return "";
        }
    },
    methods:{
        pageLoad(){
            this.loadData();
        },
        loadData(){
            var query={
                pageIndex:1,
                pageSize:100,  
                designerDatabaseId:this.args.id
            }
            app.invoke('DesignerAction.getDesignerTableChangeLogList',[app.token,query],(obj)=>{
                this.changeList=obj.list; 
            });
        },
        compareDdlCode(id){
            //
            if(this.changeInfo==null){
                return;
            }
            app.showDialog(DatabaseDiffDialog,{
                id:id,
                ddl:this.changeInfo.ddl
            })
        },
        compareDmlCode(id){
            //
            if(this.changeInfo==null){
                return;
            }
            app.showDialog(DatabaseDiffDialog,{
                id:id,
                dml:this.changeInfo.dml
            })
        },
        selectItem(item){
            this.selectedItem=item;
            this.loadItem(item);
        },
        loadItem(item){
            app.invoke('DesignerAction.getDesignerTableChangeLogById',[app.token,item.id],(obj)=>{
                this.changeInfo=obj; 
                this.changeTypeList=[];
                if(this.changeInfo.ddl){
                    this.changeTypeList.push('DDL');
                }
                if(this.changeInfo.dml){
                    this.changeTypeList.push('DML');
                }
                this.changeType=this.changeTypeList[0];
            });
        }
    }
}
</script>