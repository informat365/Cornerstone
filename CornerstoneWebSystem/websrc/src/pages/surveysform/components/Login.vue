<template>
  <div class="login">
    <div class="header">
      <img class="logo" src="/image/logo.png">
    </div>
    <CellGroup class="form">
      <Field
        class="form-item"
        label-width="60px"
        label="用户名"
        placeholder="用户名/手机号码"
        v-model.trim="formItem.userName" />
      <Field
        class="form-item"
        label-width="60px"
        label="密码"
        type="password"
        placeholder="登录密码"
        v-model.trim="formItem.password" />
    </CellGroup>
    <div class="error-message" v-if="errMsg">
      {{ errMsg }}
    </div>
    <div style="padding:20px">
      <Button
        block
        :loading="logining"
        loading-text="登录中"
        :disabled="this.formItem.userName==null ||formItem.password==null"
        @click="onLogin"
        type="primary">登录
      </Button>
    </div>
  </div>
</template>

<script>
  import { Button, CellGroup, Field } from 'vant';

  export default {
    name: 'Login',
    components: { Button, CellGroup, Field },
    data() {
      return {
        formItem: {
          userName: null,
          password: null,
        },
        logining: false,
        errMsg: null,
      };
    },
    methods: {
      onLogin: function () {
        this.logining = true;
        this.request('BizAction.login', [this.formItem.userName, this.formItem.password], (result) => {
          if (result.errCode != 0) {
            this.errMsg = result.errMsg;
            this.logining = false;
            return;
          }
          setCookie('token', result.token);
          this.$emit('on-success', result.token);
          this.logining = false;
        }, () => {
          this.logining = false;
        }, false);
      },
    },
  };
</script>

<style lang="less" scoped>
  .login {
    position: relative;
    padding: 40px 15px;

    .header {
      position: relative;
      display: -webkit-box;
      display: -webkit-flex;
      display: flex;
      -webkit-box-orient: vertical;
      -webkit-box-direction: normal;
      -webkit-flex-direction: column;
      flex-direction: column;
      -webkit-box-align: center;
      -webkit-align-items: center;
      align-items: center;
      -webkit-box-pack: center;
      -webkit-justify-content: center;
      justify-content: center;

      .logo {
        width: 130px;
        margin: 40px 0;
      }
    }

    .form {
      &-item {
        padding: 0 16px;

        /deep/ .van-field__label {
          position: relative;
          height: 44px;
          line-height: 44px;
        }

        /deep/ .van-field__control {
          height: 44px;
        }
      }
    }

    .error-message {
      padding-top: 5px;
      color: #e64340;
      text-align: center;
    }
  }
</style>
