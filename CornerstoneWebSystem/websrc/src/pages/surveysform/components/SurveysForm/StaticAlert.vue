<template>
  <div
    v-if="field"
    class="survey-form-static-alert"
    :class="alertClass">
    {{ field.remark }}
  </div>
</template>

<script>
  export default {
    name: 'StaticAlert',
    props: {
      field: {
        type: Object,
      },
    },
    computed: {
      alertClass() {
        if (!this.field) {
          return {};
        }
        return ['survey-form-static-alert-' + this.field.alertType];
      },
    },
  };
</script>

<style lang="less" scoped>
  .survey-form-static-alert {
    border: 1px solid #abdcff;
    background-color: #f0faff;
    font-size: 15px;
    padding: 10px 15px;

    &&-success {
      border: 1px solid #8ce6b0;
      background-color: #edfff3;
    }

    &&-warning {
      border: 1px solid #ffd77a;
      background-color: #fff9e6;
    }

    &&-error {
      border: 1px solid #ffb08f;
      background-color: #ffefe6;
    }

    &&-info {
      border: 1px solid #abdcff;
      background-color: #f0faff;
    }
  }
</style>
