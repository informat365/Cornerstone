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
    <div class="mobile" v-if="isMobile === true">
      <div class="content">
        <div class="logo">
          <img src="/image/logo.png" class="logo-image">
        </div>
        <div class="text">请在PC端配置</div>
      </div>
    </div>
    <div class="main-box" v-else>
      <div class="logo">
        <img src="/image/logo.png" class="logo-image">
      </div>
      <div class="title">
        是否授权以下应用访问你的<span class="cornerstone">CORNERSTONE</span> 账号？
      </div>
      <div class="authorize-content">
        <div class="authorize-apps">
          <div class="authorize-apps-cornerstone">
            <div class="icon"></div>
            <div class="text">CORNERSTONE</div>
          </div>
          <div class="authorize-apps-exchange">
            <div class="icon"></div>
          </div>
          <div class="authorize-apps-feishu">
            <div class="icon"></div>
            <div class="text">
              <template v-if="larkResult">
                {{ larkResult.name || '飞书' }}
              </template>
              <template v-else>
                飞书
              </template>
            </div>
          </div>
        </div>
        <div class="authorize-alert">
          <span class="feishu font-strong">飞书</span>为第三方应用，授权即视为允许通过飞书账号直接访问您<span class="cornerstone font-strong">CORNERSTONE</span>账户中的相关内容。
        </div>
        <div class="authorize-tips" v-show="authState.code === 0">
          <div class="authorize-permission">
            <div>
              <strong>
                同意授权即允许<span class="feishu font-strong">飞书</span>获得以下访问权限：
              </strong>
            </div>
            <ul>
              <li>获取你填写的用户名及个人信息</li>
              <li>获取你的<span class="cornerstone">CORNERSTONE</span>项目信息</li>
              <li>获取您的项目动态推送</li>
            </ul>
          </div>
          <div class="authorize-actions">
            <button class="btn btn-text" @click="onClickPrivacyla">
              隐私政策
            </button>
            <div class="">
              <button
                class="btn btn-outline-default"
                @click="onClickDisGrant"
                :disabled="authState.authing || authState.looping">
                拒绝授权
              </button>
              <button
                class="btn btn-primary" @click="onClickGrant" :disabled="authState.authing || authState.looping">
                同意授权
              </button>
            </div>
          </div>
        </div>
        <div class="authorize-state" v-show="authState.code !== 0">
          <div class="authorize-state-tips">
            <template v-if="authState.code === 2">
              授权<span class="cornerstone">CORNERSTONE</span> 成功，{{ second }}秒后自动跳转
            </template>
            <template v-else-if="authState.code === 3">
              授权<span class="cornerstone">CORNERSTONE</span> 成功，{{ authState.second }}秒后自动跳转
            </template>
            <template v-else-if="authState.code === 4">
              授权<span class="cornerstone">CORNERSTONE</span> 成功,请完成账号注册，{{ authState.second }}秒后自动跳转
            </template>
            <template v-else>
              授权<span class="cornerstone">CORNERSTONE</span> 失败，请稍后重试
            </template>
          </div>
          <div class="authorize-state-actions">
            <template v-if="authState.code === 2 || authState.code === 3">
              <button class="btn btn-danger" @click="onClickEnter">
                立即进入应用
              </button>
            </template>
            <template v-else-if="authState.code === 4">
              <button class="btn btn-outline" @click="onClickRegister">
                注册并绑定
              </button>
            </template>
            <template v-else-if="authState.code === 1">
              <button class="btn btn-outline" @click="onClickGrant">
                重新授权
              </button>
            </template>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>

  import bowser from 'bowser';
  import { Base64 } from 'js-base64';

  export default {
    data() {
      return {
        larkResult: null,
        larkData: null,
        bowserInfo: {},
        isMobile: true,
        authState: {
          authing: false,
          looping: false,
          code: 0,
          second: 0,
        },
        jumpIntervalId: 0,
      };
    },
    created() {
      this.bowserInfo = JSON.stringify(bowser);
      this.isMobile = bowser.mobile === true;
      serverAddr = [window.location.protocol, '//', window.location.host].join('');
      const url = new URL(window.location.href);
      const pageArgs = {};
      url.searchParams.forEach((k, v) => {
        pageArgs[v] = k;
      });
      this.stopLoop();
      this.authState = {
        authing: false,
        looping: false,
        code: 0,
        second: 3,
      };
      if (pageArgs.data) {
        try {
          this.larkData = pageArgs.data;
          this.larkResult = JSON.parse(Base64.decode(pageArgs.data));
        } catch (e) {
          this.authState.code = 1;
        }
      } else {
        this.authState.code = 1;
      }
    },
    beforeDestroy() {
      this.authState.code = 0;
      this.stopLoop();
    },
    methods: {
      startLoop() {
        this.stopLoop();
        this.authState.authing = true;
        this.jumpIntervalId = setInterval(() => {
          if (this.authState.second <= 1) {
            this.stopLoop();
            if (this.authState.code === 2 || this.authState.code === 3) {
              this.onClickEnter();
              return;
            }
            if (this.authState.code === 4) {
              this.onClickRegister();
              return;
            }
            return;
          }
          this.authState.second -= 1;
        }, 1000);
      },
      stopLoop() {
        clearInterval(this.jumpIntervalId);
        this.authState = {
          ...this.authState,
          ...{
            authing: false,
            looping: false,
            second: 3,
          },
        };
      },
      onClickRegister() {
        const url = [serverAddr, '/register.html'];
        url.push('?data=');
        url.push(encodeURIComponent(this.larkData));
        window.location.href = url.join('');
      },
      onClickEnter() {
        window.location.href = '/#/';
      },
      onClickPrivacyla() {
        window.open(serverAddr + '/privacyla.html');
      },
      onClickDisGrant() {
        window.location.href = serverAddr;
      },
      onClickGrant() {
        const token = getCookie('token') || '';
        this.authState.authing = true;
        ajaxInvoke(serverAddr + '/p/api/invoke/', 'BizAction.larkAuthorize', [token, this.larkData],
            (res) => {
              if (!res) {
                this.authState.authing = false;
                this.authState.code = 1;
                return;
              }
              this.authState.code = res.code;
              if (this.authState.code === 2 || this.authState.code === 3) {
                if (res.token) {
                  setCookie('token', res.token);
                }
              }
              this.startLoop();
            },
            () => {
              this.authState.authing = false;
              this.authState.code = 1;
            },
        );
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


  .mobile {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: #f3f3f3;

    .content {
      position: absolute;
      top: 50%;
      left: 0;
      margin-top: -200px;
      width: 100%;
      overflow: hidden;

      .logo {
        text-align: center;

        img {
          display: inline-block;
          width: 200px;
        }
      }

      .text {
        text-align: center;
        padding: 50px 0;
        font-size: 20px;
      }
    }
  }

  .authorize {
    font-size: 14px;
    text-align: center;

    .main-box {
      display: inline-block;
      text-align: left;
      width: 500px;
      margin-top: 100px;
    }

    .logo {
      text-align: center;
      padding-bottom: 20px;

      img {
        display: inline-block;
        width: 200px;
      }
    }

    .title {
      font-size: 20px;
      font-weight: 500;
      padding: 10px 0;
      text-align: center;
    }

    &-content {
    }

    &-apps {
      display: flex;
      align-items: center;
      align-content: center;
      justify-content: center;
      padding: 20px 0;

      .icon {
        position: relative;
        display: inline-block;
        height: 60px;
        width: 60px;
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
          width: 60px;
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

    &-alert {
      border: 1px solid #ffb08f;
      background-color: #ffefe6;
      padding: 10px;
    }

    &-permission {
      margin-top: 30px;
      border: 1px solid #ffd77a;
      background-color: #fff9e6;
      padding: 10px;
    }

    &-state {

      &-tips {
        font-size: 18px;
        font-weight: 500;
        padding: 50px 0;
        text-align: center;
      }

      &-actions {
        display: flex;
        align-items: center;
        align-content: center;
        justify-content: space-around;

        .btn {
          margin-right: 10px;
          width: 200px;
        }
      }
    }

    &-actions {
      display: flex;
      align-items: center;
      align-content: center;
      justify-content: space-between;

      .btn {
        margin-right: 10px;
      }
    }
  }
</style>
