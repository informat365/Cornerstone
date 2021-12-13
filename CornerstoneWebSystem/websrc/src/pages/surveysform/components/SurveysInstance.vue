<template>
  <div class="survey-instance">
    <div class="survey-form">
      <div class="survey-define" v-if="[1,5].indexOf(surveysState)> -1">
        <div class="survey-define-name">
          {{ formDefine.title }}
        </div>
        <div class="survey-define-time" v-if="surveysDefine.startTime > 0 || surveysDefine.endTime > 0">
          有效期：
          <template v-if="surveysDefine.startTime > 0 ">
            {{ surveysDefine.startTime | formatDate }}
          </template>
          <template v-if="surveysDefine.endTime > 0 ">
            ~ {{ surveysDefine.endTime | formatDate }}
          </template>
        </div>
        <div class="survey-define-time" v-if="formInstance">
          {{ surveysDefine.lastSubmitTime | formatDate }}提交
        </div>
      </div>
      <div
        class="survey-form-fields"
        v-show="showSurveysSuccess !== true">
        <template v-for="field in formFields">
          <Cell
            class="form-field"
            :class="[{ 'error': field.hasError }, 'form-field-'+field.compoment]"
            :border="false"
            :key="field.id">
            <div
              class="title"
              v-if="formFieldType.indexOf(field.compoment) > -1">
              <span class="name">{{ field.name }}</span>
              <span class="required" v-if="field.required">*</span>
              <span class="remark">
                  {{ field.remark }}
              </span>
            </div>
            <component
              :key="field.id"
              :disabled="formReadonly"
              :define-uuid="uuid"
              :is="field.compoment"
              :field="field" />
            <div class="message">
              {{ field.errorMsg }}
            </div>
          </Cell>
        </template>
      </div>
      <div
        v-if="formReadonly===false"
        class="survey-actions">
        <Button
          :disabled="Array.isEmpty(formFields)"
          block
          :loading="submiting"
          loading-text="提交中"
          type="info"
          @click="onSubmitSurveys">提交
        </Button>
      </div>
      <div class="survey-error" v-if="[2,3,4,6].indexOf(surveysState)> -1">
        <div class="survey-error-logo">
          <img src="/image/logo.png">
        </div>
        <div class="survey-success-icon">
          <Icon name="close" size="48px" color="#ee0a24" />
        </div>
        <div class="survey-error-text">
          <template v-if="surveysState === 2">
            问卷不存在
          </template>
          <template v-if="surveysState === 3">
            加载失败，{{ surveysStateMessage }}
          </template>
          <template v-if="surveysState === 4">
            问卷【{{ formDefine.title }}】已过期
          </template>
          <template v-if="surveysState === 6">
            问卷【{{ formDefine.title }}】已被禁用
          </template>
        </div>
      </div>
      <transition name="fade">
        <div
          class="survey-success"
          v-if="[1,5].indexOf(surveysState)> -1"
          v-show="showSurveysSuccess === true">
          <div class="survey-success-logo">
            <img src="/image/logo.png">
          </div>
          <div class="survey-success-icon">
            <Icon name="passed" size="48px" color="#07c160" />
          </div>
          <div class="survey-success-text">
            问卷【{{ formDefine.title }}】信息已提交，谢谢您的配合
          </div>
          <div class="survey-success-actions">
            <Button
              block
              type="info"
              @click="onClickSurveysView">
              {{ surveysDefine.submitEdit ? '编辑' : '查看'}}已填写内容
            </Button>
          </div>
        </div>
      </transition>
      <div class="survey-footer">
        由CORNERSTONE提供技术支持
      </div>
    </div>
  </div>
</template>

<script>
  import { Button, Cell, Icon } from 'vant';
  import {
    Attachment,
    DateTime,
    Select,
    StaticAlert,
    StaticGroup,
    StaticLabel,
    Textarea,
    TextNumber,
    TextSingle,
    XCheckbox,
    XRadio,
  } from './SurveysForm';

  export default {
    name: 'SurveysInstance',
    components: {
      Button,
      Cell,
      Icon,
      'vx-attachment': Attachment,
      'vx-checkbox': XCheckbox,
      'vx-date': DateTime,
      'vx-time': DateTime,
      'vx-radio': XRadio,
      'vx-select': Select,
      'vx-static-alert': StaticAlert,
      'vx-static-group': StaticGroup,
      'vx-static-label': StaticLabel,
      'vx-text-area': Textarea,
      'vx-text-number': TextNumber,
      'vx-text-single': TextSingle,
    },
    props: {
      uuid: {
        type: String,
      },
      loading: {
        type: Boolean,
      },
    },
    data() {
      return {
        /**
         * 0: 未加载
         * 1: 已加载且存在
         * 2: 已加载且不存在
         * 3: 加载失败
         * 4: 已加载且已过期
         * 5: 已加载且已写过已过期
         * 6: 已加载且禁用
         */
        surveysState: 0,
        surveysStateMessage: null,
        surveysDefine: null,
        formDefine: {},
        formInstance: null,
        formFields: [],
        noCellBorder: ['vx-checkbox', 'vx-radio', 'vx-static-label', 'vx-static-group', 'vx-static-alert'],
        formFieldType: ['vx-attachment', 'vx-checkbox', 'vx-date', 'vx-time', 'vx-radio', 'vx-select', 'vx-text-area', 'vx-text-number', 'vx-text-single'],
        dataLoading: this.loading,
        showSurveysSuccess: false,
        submiting: false,
      };
    },
    watch: {
      dataLoading(val) {
        this.$emit('update:loading', val);
      },
    },
    computed: {
      formReadonly() {
        if (!this.formDefine || !this.surveysDefine) {
          return true;
        }
        if (this.surveysState !== 0 && this.surveysState !== 1) {
          return true;
        }
        return !this.surveysDefine.submitEdit && !!this.formInstance;
      },
    },
    methods: {
      pageCreated() {
        if (!this.uuid) {
          return;
        }
        if (this.token) {
          this.loadInfo(true);
          return;
        }
        this.loadDefine(true, true);
      },
      loadDefine(fullscreenLoading, loadInfo) {
        this.dataLoading = fullscreenLoading === true;
        this.request('SurveysAction.getSurveysDefineByUuid', [this.uuid], (result) => {
          this.dataLoading = false;
          this.surveysDefine = result;
          if (!this.surveysDefine) {
            this.surveysState = 2;
            return;
          }
          this.formDefine = {
            title: this.surveysDefine.name,
          };
          document.title = this.formDefine.title;
          if (this.surveysDefine.status !== 1) {
            this.surveysState = 6;
            return;
          }
          if (this.surveysDefine.endTime) {
              this.surveysState = this.surveysDefine.endTime < Date.now() ? 4 : 1;
          } else {
              this.surveysState = 1;
          }
          if (this.surveysDefine.anonymous === true) {
            if (this.surveysState === 4) {
              return;
            }
            if (loadInfo) {
              this.loadInfo(fullscreenLoading);
            }
            return;
          }
          if (String.isEmpty(this.token)) {
            this.$nextTick(() => {
              this.$emit('on-change-type', 'login');
            });
          }
        }, (code, message) => {
          this.surveysState = 3;
          this.surveysStateMessage = message || '问卷不存在或已过期';
          this.dataLoading = false;
        });
      },
      loadInfo(fullscreenLoading) {
        this.dataLoading = fullscreenLoading === true;
        this.request('SurveysAction.getSurveysSubmitInfo', [this.uuid, this.accountUuid], (result) => {
          this.dataLoading = false;
          this.surveysDefine = result.surveysDefine;
          if (!this.surveysDefine) {
            this.surveysState = 2;
            return;
          }
          this.formDefine = result.formDefine;
          this.formInstance = result.instance;
          if (!this.formDefine) {
            this.formDefine = {
              title: this.surveysDefine.name,
            };
            document.title = this.formDefine.title;
          }
          document.title = this.formDefine.title;
          this.showSurveysSuccess = !Object.isEmpty(this.formInstance);
          if (this.surveysDefine.endTime < Date.now()) {
            this.surveysState = Object.isEmpty(this.formInstance) ? 4 : 5;
          } else {
            this.surveysState = this.surveysDefine.status !== 1 ? 6 : 1;
          }
          if (this.surveysState === 4 || this.surveysState === 6) {
            return;
          }
          this.compFormFields();
        }, (code, message) => {
          this.surveysState = 3;
          this.surveysStateMessage = message || '问卷不存在或已过期';
          this.dataLoading = false;
        });
      },
      compFormFields() {
        if (!this.formDefine) {
          return;
        }
        let formFields = [];
        try {
          formFields = JSON.parse(this.formDefine.fieldList);
        } catch (e) {
        }
        if (!this.formInstance) {
          formFields.forEach(item => {
            item.compoment = 'vx-' + item.type;
            item.hasError = false;
            item.errorMsg = null;
          });
          this.formFields = formFields;
          return;
        }
        let formData = {};
        try {
          formData = JSON.parse(this.formInstance.formData);
        } catch (e) {
        }
        formFields.forEach(item => {
          item.compoment = 'vx-' + item.type;
          item.hasError = false;
          item.errorMsg = null;
          item.value = formData[item.id];
        });
        this.formFields = formFields;
      },
      onSubmitSurveys() {
        const valid = this.surveysFormValidate();
        if (!valid) {
          this.$toast.fail('表单中有内容不符合要求，请检查');
          return;
        }
        const formData = {};
        this.formFields.forEach(field => {
          //不在form表单项或跳过
          if (this.formFieldType.indexOf(field.compoment) === -1) {
            return;
          }
          formData[field.id] = field.value;
        });
        const submitInfo = {
          surveysDefineId: this.surveysDefine.id,
          formData: JSON.stringify(formData),
          userAgent: window.navigator.userAgent,
          cookieEnabled: window.navigator.cookieEnabled,
          accountUuid: this.accountUuid,
          submit: true,
        };
        this.submiting = true;
        this.$toast.loading({
          duration: 0,
          forbidClick: true,
          message: '提交中',
        });
        this.request('SurveysAction.submitSurveysInstance', [submitInfo], (res) => {
          console.log(res);
          this.submiting = false;
          setTimeout(() => {
            this.$toast.clear();
            this.$toast.success('提交成功');
          }, 500);
          this.accountUuid = res.accountUuid;
          submitInfo.accountUuid = this.accountUuid;
          if (Object.isEmpty(this.formInstance)) {
            this.formInstance = {
              ...submitInfo,
            };
          }
          this.loadDefine();
          this.showSurveysSuccess = true;
        }, (code, message) => {
          this.submiting = false;
          this.$toast.clear();
          if (String.isEmpty(message)) {
            this.$toast.fail('提交失败');
          } else {
            this.$toast.fail('提交失败,' + message);
          }
        });
      },
      surveysFormValidate() {
        let valid = true;
        this.formFields.forEach(field => {
          const state = this.surveysFormValidateField(field);
          if (!state || state.error === false) {
            field.hasError = false;
            field.errorMsg = null;
            return;
          }
          field.hasError = true;
          field.errorMsg = state.errorMsg;
          valid = false;
        });
        return valid;
      },
      surveysFormValidateField(field) {
        //不在form表单项或跳过
        if (this.formFieldType.indexOf(field.compoment) === -1) {
          return {
            error: false,
          };
        }
        if (Object.isEmpty(field.value) && field.required) {
          return {
            error: true,
            errorMsg: field.name + '是必填项',
          };
        }
        if (field.compoment === 'vx-text-single') {
          if (field.textFormat === 'email' && !String.isEmail(field.value)) {
            return {
              error: true,
              errorMsg: field.name + '不符合邮箱格式',
            };
          }
          if (field.textFormat === 'url' && !String.isUrl(field.value)) {
            return {
              error: true,
              errorMsg: field.name + '不符合URL地址格式',
            };
          }
          if (field.textFormat === 'mobile' && !String.isMobile(field.value)) {
            return {
              error: true,
              errorMsg: field.name + '不符合手机号格式',
            };
          }
          if (field.textFormat === 'idcard' && !String.isIdCard(field.value)) {
            return {
              error: true,
              errorMsg: field.name + '不符合身份证格式',
            };
          }
        }
        return {
          error: false,
        };
      },
      onClickSurveysView() {
        this.showSurveysSuccess = false;
      },
    },
  };
</script>

<style lang="less" scoped>
  .survey-instance {
    position: relative;
    height: 100%;
    width: 100%;
    overflow: hidden;
  }

  .survey-define {
    padding: 30px 16px 0 16px;
    background-color: #fff;

    &-name {
      font-size: 18px;
      font-weight: bold;
      text-align: center;
    }

    &-time {
      font-size: 12px;
      color: #999;
      padding: 5px 0;
      text-align: center;
    }
  }

  .survey-error,
  .survey-success {
    &-logo {
      position: relative;
      text-align: center;

      img {
        display: inline-block;
        width: 130px;
        margin: 40px 0;
      }
    }

    &-icon {
      text-align: center;
    }

    &-text {
      font-weight: bold;
      padding: 20px 16px 40px 16px;
      text-align: center;
    }
  }

  .survey-success {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 10;
    padding: 0 16px;
    background-color: #fff;

    &-actions {
      padding: 0 10px;
    }
  }

  .survey-form {
    position: relative;
    height: 100%;
    width: 100%;
    overflow: auto;
    overflow-scrolling: touch;


    &-fields {
      background-color: #f0f0f0;
    }

    .form-field {
      border: solid 1px #fff;
      margin-bottom: 10px;

      /deep/ .van-cell {
        font-size: 15px;
      }

      &-vx-static-group {
        background-color: #f0f0f0;
        border-color: #f0f0f0;
      }

      .title {
        padding-bottom: 10px;

        .name {
          font-size: 16px;
          font-weight: 700;
          color: #333;
        }

        .remark {
          font-size: 12px;
          color: #999;
          padding-left: 5px;
        }
      }

      .message {
        padding-top: 5px;
        font-size: 12px;
        color: #e64340;
        display: none;
      }

      &.error {
        border-color: #e64340;

        .message {
          display: block;
        }
      }
    }
  }

  .survey-actions {
    padding: 20px;
  }

  .survey-footer {
    position: relative;
    height: 50px;
    line-height: 50px;
    color: #999;
    font-size: 14px;
    text-align: center;
    background-color: #f0f0f0;
  }

</style>
