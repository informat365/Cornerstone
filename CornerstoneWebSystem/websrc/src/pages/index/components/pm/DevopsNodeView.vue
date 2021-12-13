<style scoped>
   .table-box{
        background-color: #fff;
        padding:30px;
        padding-top:10px;
        box-shadow: 0px 2px 10px 0px rgba(225,225,225,0.5);
        border: 1px solid rgba(216,216,216,1);
   }
   
    .table-info{
       color:#999;
       text-align: center;
   }
   .table-count{
       background-color: #E8E8E8;
       color:#666;
       padding:3px 5px;
       border-radius: 3px;
   }
   
   .table-col-name{
       display: inline-block;
       vertical-align: middle;
   }
   .table-remark{
       color:#999;
       margin-top:5px;
   }
    .nodata{
       padding:60px;
       font-size:20px;
       color:#999;
       text-align: center;
   }
   .table-info-row{
       display: flex;
       align-items: center;
   }
  
   .card-box{
       display: block;
   }


.machine-card{
    width:260px;
    text-align: left;
    display: inline-block;
    margin-right:10px;
    margin-top:10px;
    cursor: pointer;
    background: #fff !important;
    border:1px solid #eee;
}
.machine-card-name{
    font-size:14px;
    color:#333333;
}
.login-type{
  font-size:12px;
  color:teal;
  padding-right:10px;
}
  .right-opt{
    display: flex;
    align-items: center;
    flex-direction: row-reverse;
}


</style>
<i18n>
{
    "en": {
        "卡片视图": "Card view",
        "列表视图":"Table view",
        "创建主机":"Create",
        "个主机":"{0} items",
        "无备注":"none",
        "设置属性":"Edit",
        "暂无数据":"No Data",
        "名称":"Name",
        "分组":"Group",
        "登录类型":"Type",
        "地址":"Address",
        "创建人":"Created by",
        "备注":"Remark",
        "设置":"Edit",
        "暂无数据":"No Data",
        "未分组":"Unclassified",
        "您没有权限登录主机":"You do not have permission to login to the machine"
    },
    "zh_CN": {
        "卡片视图": "卡片视图",
        "列表视图":"列表视图",
        "创建主机":"创建主机",
        "个主机":"{0}个主机",
        "无备注":"无备注",
        "设置属性":"设置属性",
        "暂无数据":"暂无数据",
        "名称":"名称",
        "分组":"分组",
        "登录类型":"登录类型",
        "地址":"地址",
        "创建人":"创建人",
        "备注":"备注",
        "设置":"设置",
        "暂无数据":"暂无数据",
        "未分组":"未分组",
        "您没有权限登录主机":"您没有权限登录主机"
    }
}
</i18n>
<template>
<div v-if="list" style="padding:20px">
     <Row>
        <Col span="6">&nbsp;</Col>
        <Col span="12">
             <div  class="table-info">
                <span class="table-count">{{$t('个主机',[list.length])}}</span>
            </div>
        </Col>
        <Col span="6" class="right-opt">
             <IconButton v-if="viewType=='table'" @click="changeViewType('card')" :title="$t('卡片视图')"></IconButton>
             <IconButton v-if="viewType=='card'" @click="changeViewType('table')" :title="$t('列表视图')"></IconButton>
            <IconButton v-if="prjPerm('devops_edit_machine')"  icon="md-add" @click="showCreateMachineDialog()" :title="$t('创建主机')"></IconButton>
        
        </Col>
    </Row>
    
    <div v-if="viewType=='card'" class="card-box">
            <template v-for="group in groupList">
            <Divider :key="'g'+group.name" orientation="left">
                {{group.name}} <span class="grp-cnt">{{group.list.length}}</span>
            </Divider>
            <Card class="machine-card" v-for="item in group.list" :key="item.id" @click.native="loginMachine(item)">
                  <div class="machine-card-name text-no-wrap"><Icon v-if="item.enableRole" type="ios-lock-outline" />{{item.name}}</div>
                  <div class="text-no-wrap" style="color:#999;font-size:12px;margin-top:5px;"><span class="login-type">{{item.loginType|dataDict('Machine.loginType')}}</span>{{item.userName}}@{{item.host}}</div>
                  <div class="text-no-wrap" style="color:#999;font-size:12px;margin-top:5px;">
                      <template v-if="item.remark">{{item.remark}}</template>
                      <template v-if="item.remark==null">{{$t('无备注')}}</template>
                  </div>
                  <div class="card-opt">
                      <IconButton  v-if="prjPerm('devops_edit_machine')" @click.stop="showCreateMachineDialog(item)" :tips="$t('设置属性')" icon="ios-settings-outline"></IconButton>
                  </div>
            </Card>

        </template>
        <div class="nodata" v-if="list.length==0">
            {{$t('暂无数据')}}
        </div>
    </div>

    <div v-if="viewType=='table'" class="table-box">
        <table class="table-content "  style="table-layout:fixed">
                <thead>
                    <tr>
                        <th>{{$t('名称')}}</th>
                        <th style="width:100px;">{{$t('分组')}}</th>
                        <th style="width:100px;">{{$t('登录类型')}}</th>
                        <th style="width:300px;">{{$t('地址')}}</th>
                        <th style="width:100px">{{$t('创建人')}}</th>
                        <th style="width:150px">{{$t('备注')}}</th>
                        <th style="width:80px">{{$t('设置')}}</th>
                    </tr>     
                  </thead>
                    <tbody>
                        <template v-for="group in groupList">
                        <tr :key="'grp_'+group.name" class="grp-tr">
                            <td colspan="7">{{group.name}} <span class="grp-cnt">{{group.list.length}}</span></td>
                        </tr>
                        <tr @click="loginMachine(item)" v-for="item in group.list" :key="'prj_'+item.id" class="table-row clickable">
                            <td class="text-no-wrap">
                                {{item.name}}<Icon v-if="item.enableRole" type="ios-lock-outline" />
                            </td>
                             <td class="text-no-wrap">
                               <template v-if="item.group">{{item.group}}</template>
                               <template v-if="item.group==null">{{$t('未分组')}}</template>
                            </td>
                            <td >
                                <span class="login-type">{{item.loginType|dataDict('Machine.loginType')}}</span>
                            </td>
                            <td class="text-no-wrap">
                                {{item.userName}}@{{item.host}}
                            </td>
                            <td>{{item.createAccountName}}</td>
                            <td class="text-no-wrap">
                               {{item.remark}}
                            </td>
                            <td>
                                <IconButton  v-if="prjPerm('devops_edit_machine')" @click.stop="showCreateMachineDialog(item)" tips="设置属性" icon="ios-settings-outline"></IconButton>
                            </td>
                        </tr>
                         </template>
                    </tbody>
        </table>
         <div v-if="list.length==0" class="table-nodata">
            <div>{{$t('暂无数据')}}</div>
        </div>

    </div>
</div>
</template>

<script>
export default {
    mixins: [componentMixin],
    data(){
        return {
            viewType:'card',
            list:[]
        }
    },  
    computed:{
        groupList(){
            var list=[];
            var groupMap={};
            for(var i=0;i<this.list.length;i++){
                var t=this.list[i];
                if(t.group==null||t.group==''){
                    t.group=this.$t("未分组");
                }
                if(groupMap[t.group]==null){
                    groupMap[t.group]={
                        name:t.group,
                        list:[]
                    }
                    list.push(groupMap[t.group])
                }
                groupMap[t.group].list.push(t);
            }
            return list;
        }
    },
    mounted(){
        var t=app.loadObject('DevOps.node.viewType');
        if(t!=null){
            this.viewType=t;
        }
        this.loadData();
    },
    methods:{
        loadData(){
            var query={
                projectId:app.projectId
            }
            app.invoke("BizAction.getMachineInfoList",[app.token,query],list => {
                this.list=list;
            });
        },
        changeViewType(t){
            this.viewType=t;
            app.saveObject('DevOps.node.viewType',t)
        },
        showCreateMachineDialog(item){
            app.showDialog(DevopsMachineEditDialog,{
                projectId:app.projectId,
                id:item==null?null:item.id
            })
        },
        loginMachine(item){
            app.invoke("BizAction.loginMachine",[app.token,item.id],info => {
                this.showTerminalDialog(item,info);
            });
        },
        showTerminalDialog(item,info){
            if(item.loginType==3){
                app.showDialog(VncDialog,{
                    info:info
                });
            }else{
                app.showDialog(TerminalWindow,{
                    info:info,
                })
            }
        }
    }
}
</script>