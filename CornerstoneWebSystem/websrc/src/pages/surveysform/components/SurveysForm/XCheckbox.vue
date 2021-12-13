<template>
  <div
    v-if="field"
    class="survey-form-static-checkbox">
    <CheckboxGroup
      :disabled="disabled"
      v-model="field.value">
      <CellGroup>
        <template v-for="(item,index) in options">
          <Cell
            :key="index">
            <Checkbox
              shape="square"
              :name="item.key">
              {{ item.value }}
            </Checkbox>
          </Cell>
        </template>
      </CellGroup>
    </CheckboxGroup>
  </div>
</template>

<script>

  import { Cell, CellGroup, Checkbox, CheckboxGroup } from 'vant';

  export default {
    name: 'XCheckbox',
    components: { Cell, CellGroup, Checkbox, CheckboxGroup },
    props: {
      field: {
        type: Object,
      },
      disabled: {
        type: Boolean,
        default: false,
      },
    },
    computed: {
      options() {
        if (!this.field.optionList || !Array.isArray(this.field.optionList)) {
          return [];
        }
        const options = [];
        this.field.optionList.forEach(_ => {
          options.push({
            key: _.value,
            value: _.value,
          });
        });
        return options;
      },
    },
  };
</script>

<style lang="less" scoped>
  .survey-form-static-checkbox {

    /deep/ .van-cell {
      padding: 10px 0;
    }
  }
</style>
