<template>
  <div
    v-if="field"
    class="survey-form-static-text-single ">
    <Field
      v-model="fieldValue"
      clearable
      :readonly="disabled"
      :maxlength="field.maxLength"
      show-word-limit
      placeholder="请输入" />
  </div>
</template>

<script>

  import { Field } from 'vant';

  export default {
    name: 'TextSingle',
    components: { Field },
    props: {
      field: {
        type: Object,
      },
      disabled: {
        type: Boolean,
        default: false,
      },
    },
    watch: {
      field: {
        immediate: true,
        handler(val) {
          this.fieldValue = val.value || '';
        },
      },
      fieldValue(val) {
        this.field.value = val;
      },
    },
    data() {
      return {
        fieldValue: '',
      };
    },
  };
</script>

<style lang="less" scoped>
  .survey-form-static-text-single {

    /deep/ .van-field {
      padding: 0;

      .van-field__control {
        height: 44px;
      }
    }
  }
</style>
