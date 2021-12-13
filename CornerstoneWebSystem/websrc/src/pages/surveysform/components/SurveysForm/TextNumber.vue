<template>
  <div
    v-if="field"
    class="survey-form-static-text-number ">
    <Stepper
      :disabled="disabled"
      v-model="field.value"
      :min="field.minValue"
      :max="field.maxValue"
      input-width="80px"
      button-size="32px" />
  </div>
</template>

<script>
  import { Stepper } from 'vant';

  export default {
    name: 'TextNumber',
    components: { Stepper },
    props: {
      field: {
        type: Object,
      },
      disabled: {
        type: Boolean,
        default: false,
      },
    },
  };
</script>

<style lang="less" scoped>
  .survey-form-static-text-number {

    /deep/ .van-field {
      padding: 0;

      .van-field__control {
        height: 44px;
      }
    }
  }
</style>
