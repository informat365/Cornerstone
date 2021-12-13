<style scoped>
    .desc-info{
        font-size:12px;
        color:#999;
    }
    .check{
        color:#78B48C;
        font-size:18px;
    }

    .table-wrap{
        width: 550px;
        overflow-y:auto;
    }

    .role-td{
        width:90px;
        max-width: 90px;
        text-align: center;
    }

</style>
<i18n>
{
    "en": {
        "对象类型": "Object type",
        "设置不同处理人的操作权限":"Setting the operation permissions.",
        "权限":"Permission",
        "权限设置":"Permission Setting",
        "保存成功":"Success",
        "保存":"Save",
        "责任人":"Owner",
        "创建人":"Creater"
    },
    "zh_CN": {
        "对象类型": "对象类型",
        "设置不同处理人的操作权限":"设置不同处理人的操作权限,数据权限需要在成员权限获得的情况下才生效",
        "权限":"权限",
        "权限设置":"权限设置",
        "保存成功":"保存成功",
        "保存":"保存",
        "责任人":"责任人",
        "创建人":"创建人"
    }
}
</i18n>
<template>
    <div>
      <Form label-position="top">
            <FormItem :label="$t('对象类型')">
            <div>
                <RadioGroup v-model="objectType">
                    <Radio v-for="item in moduleList"
                        v-if="item.objectType>0" :key="item.objectType" :label="item.objectType">{{item.name}}</Radio>
                </RadioGroup>
            </div>
            <div class="desc-info">{{$t('设置不同处理人的操作权限')}}</div>
        </FormItem>

        <FormItem :label="$t('权限设置')">
            <div style="display:flex">
             <table class="table-content table-grid table-content-small table-scroll-no-h"
             style="margin-top:10px;border-top:1px solid #eee;width:140px;border-right:1px solid #eee;display: table">
                    <tbody>
                    <tr class="table-row table-row-small">
                            <td>{{$t('权限')}}</td>
                     </tr>

                    <tr v-for="item in permissionList" :key="item.id" class="table-row table-row-small">
                        <td style="width:100px;height:53px;text-align:left">
                            {{item.permissionName}}
                        </td>
                    </tr>
                </tbody>
            </table>
                <div style="overflow:auto;">
                    <table :style="{width:(roleList.length*90)+'px'}" class="table-content table-grid table-content-small table-scroll-h" style="margin-left:-2px;margin-top:10px;border-top:1px solid #eee;">
                        <tbody>
                        <tr class="table-row table-row-small">
                                <td class="text-no-wrap role-td" v-for="role in roleList" :key="role.id">{{role.name}}</td>
                        </tr>
                        <tr v-for="permission in permissionList" :key="permission.id" class="table-row table-row-small">
                            <td style="height:53px;text-align:center" @click="togglePermission(permission,role)" v-for="role in roleList" :key="role.id">
                                <Icon v-if="hasPermission(permission,role)" type="md-checkmark" class="check"/>
                            </td>
                        </tr>
                    </tbody>
                    </table>
                </div>
            </div>

        </FormItem>

         <FormItem label="" v-if="!disabled">
           <Button @click="savePermission" type="default">{{$t('保存')}}</Button>
        </FormItem>


    </Form>

    </div>
</template>

<script>
export default {
  mixins: [componentMixin],
  props:['project'],
  data(){
    return {
        permissionList:[],
        roleList:[],
        moduleList:[],
        objectType:null,
        disabled:false
    }
  },
  mounted(){
      this.loadModuleList();
      this.disabled = this.project&&this.project.isFinish;
  },
    watch:{
        "objectType":function(val){
            this.loadData();
        }
    },
  methods:{
    loadData(){
        app.invoke('BizAction.getProjectDataPermissionInfoList',[app.token,this.project.id,this.objectType],(list)=>{
            this.permissionList=list;
        })
        //
         app.invoke('BizAction.getRoleInfoList',[app.token,1],(list)=>{
            this.roleList=list;
            this.roleList.splice(0,0,{
                id:0,
                name:this.$t("责任人")
            })
            this.roleList.splice(0,0,{
                id:-1,
                name:this.$t("创建人")
            })
        })
    },
    loadModuleList(){
            app.invoke('BizAction.getProjectModuleInfoList',[app.token,this.project.id],(list)=>{
                this.moduleList=list;
                for(var i=0;i<this.moduleList.length;i++){
                   var t=this.moduleList[i];
                   if(t.objectType>0){
                       this.objectType=t.objectType;
                       break;
                   }
                }
            })
        },
    hasPermission(permission,role){
        for(var i=0;i<permission.ownerList.length;i++){
            if(permission.ownerList[i]==role.id){
                return true;
            }
        }
        return false;
    },
    togglePermission(permission,role){
        if(this.disabled){
            return;
        }
        for(var i=0;i<permission.ownerList.length;i++){
            if(permission.ownerList[i]==role.id){
                permission.ownerList.splice(i,1);
                return;
            }
        }
        permission.ownerList.push(role.id);
    },
    savePermission(){
         app.invoke('BizAction.saveProjectDataPermissionInfoList',[app.token,this.permissionList],(list)=>{
           app.toast(this.$t('保存成功'));
        })
    }
  }
}
</script>
