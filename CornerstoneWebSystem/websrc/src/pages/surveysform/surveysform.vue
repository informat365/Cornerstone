<template>
  <div class="surveys">
    <transition name="fade">
      <Login
        v-if="showType === 'login'"
        @on-success="onLoginSuccess" />
    </transition>
    <transition name="fade">
      <SurveysInstance
        :uuid="uuid"
        :loading.sync="loading"
        @on-change-type="onChangeType"
        v-if="showType === 'instance'" />
    </transition>
    <transition name="fade">
      <div class="loading" v-show="loading">
        <div class="loading-box">
          <img src="/image/logo.png">
        </div>
        <div class="loading-tips">
          <div class="loading-tips-text">
            加载中
          </div>
          <Loading type="spinner" size="16" />
        </div>
      </div>
    </transition>
  </div>
</template>
<script>
  import { Loading } from 'vant';
  import Login from './components/Login';
  import SurveysInstance from './components/SurveysInstance';

  export default {
    components: {
      SurveysInstance,
      Login,
      Loading,
    },
    data() {
      return {
        showType: null,
        loading: false,
        uuid: null,
      };
    },
    mounted() {
      this.showType = 'instance';
    },
    methods: {
      pageCreated(options) {
        this.uuid = options.uuid;
        this.loading = true;
      },
      onChangeType(type) {
        this.showType = type;
      },
      onLoginSuccess() {
        this.showType = 'instance';
      },
    },
  };
</script>
<style lang="less" scoped>
  .surveys {
    position: relative;
    height: 100%;
    width: 100%;
    overflow: hidden;
  }

  .loading {
    position: absolute;
    top: 0;
    left: 0;
    height: 100%;
    width: 100%;
    overflow: hidden;
    background-color: #fff;

    &-box {
      margin-top: 300px;
      font-size: 30px;
      color: #333;
      font-weight: bold;
      text-align: center;

      img {
        width: 150px;
      }
    }

    &-tips {
      position: relative;
      display: -webkit-box;
      display: -webkit-flex;
      display: flex;
      -webkit-box-align: center;
      -webkit-align-items: center;
      align-items: center;
      -webkit-box-pack: center;
      -webkit-justify-content: center;
      justify-content: center;

      &-text {
        padding: 10px 5px;
        color: #969799;
      }
    }
  }
</style>
