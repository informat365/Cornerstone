<style scoped>
.itit-box{
    position: absolute;
    right:10px;
    bottom: 10px;
    font-size:12px;
    font-weight: bold;
    color:#999;
}
.user-role{
    font-size:12px;
    background-color: #EEEEEE;
    border-radius: 3px;
    padding:4px 7px;
    margin-right:5px;
    display: inline-block;
    line-height: 1;
}
</style>
<i18n>
{
    "en": {
        "头像": "Avatar",
        "用户名": "Username",
        "角色": "Role",
        "姓名": "Name",
        "保存": "Save",
        "保存成功":"Operation completed"
    },
    "zh_CN": {
        "头像": "头像",
        "用户名": "用户名",
        "角色": "角色",
        "姓名": "姓名",
        "保存": "保存",
        "保存成功":"保存成功"
    }
}
</i18n>   
<template>
    <div style="padding:30px">
      <Form   label-position="top">
        <FormItem :label="$t('头像')">
            <UploadImage v-model="account.imageId"  style="margin-right:10px"/>
        </FormItem>

         <FormItem :label="$t('用户名')">
            {{account.userName}}
        </FormItem>

         <FormItem :label="$t('角色')">
            <div><span v-for="role in account.roles" :key="'role'+role.id" class="user-role">{{role.name}}</span></div>
        </FormItem>

        <FormItem :label="$t('姓名')">
            <Input placeholder="" v-model.trim="account.name" :maxlength="15" style="width:200px"></Input>
        </FormItem>

        

        <FormItem label="">
           <Button @click="saveAccount" type="default">{{$t('保存')}}</Button>
        </FormItem>
    </Form>
    </div>
</template>

<script>
export default {
  mixins: [componentMixin],
  data(){
    return {
        account:{},
        version:""
    }
  },  
  mounted(){
        this.loadData();
  },
  methods:{
      loadData(){
            this.version=app.version;
            app.invoke("BizAction.getAccountInfo",[app.token],info => {
                this.account=info;
            });
      },
      saveAccount(){
         app.invoke("BizAction.updateAccountInfo",[app.token,this.account],info => {
            app.toast(this.$t('保存成功'));
            app.postMessage('account.edit');
        });
      }
  }
}
</script>