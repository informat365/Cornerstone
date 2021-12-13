<template>
  <div
    v-if="field"
    class="survey-form-static-group ">
    {{ field.name }}
  </div>
</template>

<script>
  export default {
    name: 'StaticGroup',
    props: {
      field: {
        type: Object,
      },
    },
  };
</script>

<style lang="less" scoped>
  .survey-form-static-group {
    font-weight: 700;
    color: #333;
    font-size: 16px;
    padding: 0 15px;
    position: relative;

    &:before {
      height: 16px;
      width: 4px;
      display: block;
      background: #2391ff;
      content: "";
      position: absolute;
      top: 4px;
      left: 0;
    }
  }
</style>
