<template>
  <div
    v-if="field"
    class="survey-form-static-text-area ">
    <Field
      v-model="fieldValue"
      rows="2"
      clearable
      :readonly="disabled"
      :maxlength="field.maxLength"
      show-word-limit
      autosize
      type="textarea"
      placeholder="请输入" />
  </div>
</template>

<script>
  import { Field } from 'vant';

  export default {
    name: 'Textarea',
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
  .survey-form-static-text-area {

    /deep/ .van-field {
      padding: 0;
    }
  }
</style>
