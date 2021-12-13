<style scoped>
    .sub-permission{
        padding-left:10px;
        font-weight:500;
    }
</style>

<i18n>
    {
    "zh_CN": {
    "配置管理数据库维护公司主机，系统信息，提供统一的配置管理功能":"配置管理数据库维护公司主机，系统信息，提供统一的配置管理功能"
    }
    }
</i18n>

<template>
    <div class="admin-page">
         <BizTable :fixed="true" >
            <template slot="tbody">
                <tr v-for="item in [1,2,3]" :key="item" class="table-row">
                    <td style="width:100px"><ServerImage></ServerImage></td>
                    <td>
                        <div style="font-size:16px;">CMDB</div>
                        <div style="margin-top:10px;color:#999">{{$t('配置管理数据库维护公司主机，系统信息，提供统一的配置管理功能')}}</div>
                    </td>
                    <td style="width:100px">
                        <i-Switch></i-Switch>
                    </td>
                </tr>
            </template>
        </BizTable>

    </div>
</template>

<script>

export default {
  mixins: [componentMixin],
  data(){
    return {
    }
  },

  methods:{
      pageLoad(){

      }
  }
}
</script>
