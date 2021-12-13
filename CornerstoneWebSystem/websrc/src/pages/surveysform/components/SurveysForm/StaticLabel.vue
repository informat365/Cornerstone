<template>
  <div
    v-if="field"
    class="survey-form-static-label ">
    <div v-html="field.content"></div>
  </div>
</template>

<script>
  export default {
    name: 'StaticLabel',
    props: {
      field: {
        type: Object,
      },
    },
  };
</script>

<style lang="less" scoped>
  .survey-form-static-label {
    font-size: 13px;
    color: #333;
  }
</style>
