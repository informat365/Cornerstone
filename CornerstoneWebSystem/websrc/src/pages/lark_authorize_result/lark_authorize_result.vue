<i18n>
  {
  "en": {
  },
  "zh_CN": {
  }
  }
</i18n>
<template>
  <div class="app authorize">
    <div class="main-box">
      <div class="logo">
        <img src="/image/logo.png" class="logo-image">
      </div>
      <div class="authorize-content">
        <div class="authorize-apps">
          <template v-if="result">
            <div class="authorize-apps-cornerstone">
              <div class="icon"></div>
              <div class="text">{{ result.name }}</div>
            </div>
            <div class="authorize-apps-exchange">
              <div class="icon"></div>
            </div>
          </template>
          <div class="authorize-apps-feishu">
            <div class="icon"></div>
            <div class="text">飞书</div>
          </div>
        </div>
        <div class="authorize-state">
          <template v-if="result">
            <template v-if="result.code === 2">
              授权<span class="cornerstone">CORNERSTONE</span> 成功，{{ second }}秒后自动跳转
            </template>
            <template v-else-if="result.code === 3">
              授权<span class="cornerstone">CORNERSTONE</span> 成功，{{ second }}秒后自动跳转
            </template>
            <template v-else-if="result.code === 4">
              授权<span class="cornerstone">CORNERSTONE</span> 成功,请完成账号注册，{{ second }}秒后自动跳转
            </template>
            <template v-else>
              授权<span class="cornerstone">CORNERSTONE</span> 失败，{{ result.message }}
            </template>
          </template>
          <template v-else>
            授权<span class="cornerstone">CORNERSTONE</span> 失败，请稍后重试
          </template>
        </div>
        <div class="authorize-actions">
          <template v-if="result">
            <template v-if="result.code === 2 || result.code === 3">
              <button class="btn btn-danger" @click="onClickEnter">
                立即进入应用
              </button>
            </template>
            <template v-if="result.code === 4">
              <button class="btn btn-outline" @click="onClickRegister">
                注册并绑定
              </button>
            </template>
          </template>
          <template v-if="!result || result.code === 1">
            <button class="btn btn-outline" @click="onClickRegrant">
              重新授权
            </button>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
  import { Base64 } from 'js-base64';

  export default {
    data() {
      return {
        result: null,
        second: 0,
        jumpIntervalId: 0,
      };
    },
    created() {
      serverAddr = [window.location.protocol, '//', window.location.host].join('');
      const url = new URL(window.location.href);
      const pageArgs = {};
      url.searchParams.forEach((k, v) => {
        pageArgs[v] = k;
      });
      if (pageArgs.result) {
        try {
          this.result = JSON.parse(Base64.decode(pageArgs.result));
        } catch (e) {
        }
      }
      this.stopLoop();
      if (this.result && (this.result.code === 2 || this.result.code === 3 || this.result.code === 4)) {
        if (this.result.token) {
          setCookie('token', this.result.token);
        }
        this.startLoop();
      }
    },
    beforeDestroy() {
      this.stopLoop();
    },
    mounted() {
    },
    methods: {
      startLoop() {
        this.stopLoop();
        this.jumpIntervalId = setInterval(() => {
          if (this.second <= 1) {
            this.stopLoop();
            if (this.result.code === 2 || this.result.code === 3) {
              this.onClickEnter();
              return;
            }
            if (this.result.code === 4) {
              this.onClickRegister();
              return;
            }
            return;
          }
          this.second -= 1;
        }, 1000);
      },
      stopLoop() {
        clearInterval(this.jumpIntervalId);
        this.second = 3;
      },
      onClickRegister() {
        const url = [serverAddr, '/register.html'];
        url.push('?r=');
        url.push(Math.random());
        if (this.result) {
          url.push('&larkOpenId=');
          url.push(this.result.openId || '');
          url.push('&larkTenantKey=');
          url.push(this.result.tenantKey || '');
        }
        window.location.href = url.join('');
      },
      onClickRegrant() {
        window.location.href = '/lark_authorize.html';
      },
      onClickEnter() {
        window.location.href = '/#/';
      },
    },
  };
</script>
<style lang="less" scoped>

  .btn-danger {
    color: #fff;
    background-color: #ff3632;
    border-color: #ff3632;

    &:hover {
      background-color: #c12e2a;
      border-color: #c12e2a;
    }
  }

  .btn-text {
    color: #888;
    background-color: transparent;
    border-color: transparent;

    &:hover {
      color: #333;
      text-decoration: underline;
    }
  }

  .btn-outline-default {
    color: #888;
    background-color: transparent;
    border-color: #dddee1;

    &:hover {
      color: #333;
    }
  }


  .font-strong {
    font-weight: bold;
  }

  .feishu,
  .cornerstone {
    padding: 0 5px;
  }

  .feishu {
    color: #006aff;
  }

  .cornerstone {
    color: #009df1;
  }


  .authorize {
    font-size: 14px;
    text-align: center;

    .main-box {
      display: inline-block;
      text-align: left;
      width: 360px;
      margin-top: 100px;
    }

    .logo {
      text-align: center;
      padding-bottom: 40px;

      img {
        display: inline-block;
        width: 200px;
      }
    }

    &-apps {
      display: flex;
      align-items: center;
      align-content: center;
      justify-content: center;
      padding: 30px 0;

      .icon {
        position: relative;
        display: inline-block;
        height: 40px;
        width: 40px;
        background: url("/image/feishu/icon-cornerstone.png") center no-repeat #fff;
        background-size: contain;
      }

      .text {
        padding-top: 10px;
        text-align: center;
      }

      &-cornerstone {
        text-align: center;

        .icon {
          background-image: url("/image/feishu/icon-cornerstone.png");
        }
      }

      &-exchange {
        text-align: center;

        .icon {
          height: 30px;
          width: 40px;
          margin: 0 80px;
          background-image: url("/image/feishu/icon-exchange.png");
        }
      }

      &-feishu {
        text-align: center;

        .icon {
          background-image: url("/image/feishu/icon-feishu.png");
        }
      }
    }

    &-state {
      font-size: 18px;
      font-weight: 500;
      padding-bottom: 50px;
      text-align: center;
    }

    &-actions {
      display: flex;
      align-items: center;
      align-content: center;
      justify-content: space-around;

      .btn {
        margin-right: 10px;
        width: 150px;
      }
    }
  }
</style>
