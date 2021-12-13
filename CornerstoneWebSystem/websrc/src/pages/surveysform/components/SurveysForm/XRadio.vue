<template>
  <div
    v-if="field"
    class="survey-form-static-radio">
    <RadioGroup
      :disabled="disabled"
      v-model="field.value">
      <CellGroup>
        <template v-for="(item,index) in options">
          <Cell
            :key="index">
            <Radio
              :name="item.key">
              {{ item.value }}
            </Radio>
          </Cell>
        </template>
      </CellGroup>
    </RadioGroup>
  </div>
</template>

<script>
  import { Cell, CellGroup, Radio, RadioGroup } from 'vant';

  export default {
    name: 'XRadio',
    components: { Cell, CellGroup, Radio, RadioGroup },
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
  .survey-form-static-radio {

    /deep/ .van-cell {
      padding: 10px 0;
    }
  }
</style>
