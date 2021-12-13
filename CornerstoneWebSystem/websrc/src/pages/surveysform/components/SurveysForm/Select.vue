<template>
  <div
    v-if="field"
    class="survey-form-static-select ">
    <Field
      readonly
      clickable
      placeholder="请选择"
      :value="field.value"
      @click="onShow">
      <Icon
        class="right-icon"
        slot="right-icon"
        :name="showIcon"
        size="20"
        @click.stop="onClickIcon"
      />
    </Field>
    <Popup
      v-model="show"
      get-container="body"
      position="bottom">
      <Picker
        show-toolbar
        :columns="columns"
        @cancel="onPickerCancel"
        @confirm="onPickerConfirm"
      />
    </Popup>
  </div>
</template>

<script>

  import { DatetimePicker, Field, Icon, Picker, Popup } from 'vant';

  export default {
    name: 'Select',
    components: {
      DatetimePicker,
      Field,
      Icon,
      Picker,
      Popup,
    },
    props: {
      field: {
        type: Object,
      },
      disabled: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        show: false,
      };
    },
    computed: {
      showIcon() {
        if (String.isEmpty(this.field.value) || this.disabled) {
          return 'arrow';
        }
        return 'clear';
      },
      columns() {
        if (!this.field) {
          return [];
        }
        const list = [];
        let defaultIndex = 0;
        this.field.optionList.forEach((_, index) => {
          list.push({
            text: _.value,
            value: _.value,
          });
          if (_.value === this.field.value) {
            defaultIndex = index;
          }
        });
        return [
          {
            values: list,
            defaultIndex,
          },
        ];
      },
    },
    methods: {
      onShow() {
        if (this.disabled) {
          return;
        }
        this.show = true;
      },
      onClickIcon() {
        if (this.showIcon !== 'clear') {
          return;
        }
        this.field.value = null;
      },
      onPickerCancel() {
        this.show = false;
      },
      onPickerConfirm(section) {
        if (Array.isEmpty(section) || Object.isEmpty(section[0])) {
          return;
        }
        this.field.value = section[0].value;
        this.show = false;
      },
    },
  };
</script>

<style lang="less" scoped>
  .survey-form-static-select {
    /deep/ .van-cell {
      padding: 10px 0;
    }

    .right-icon {
      color: #c8c9cc;
    }
  }
</style>
