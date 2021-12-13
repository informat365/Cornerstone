<template>
  <div
    v-if="field"
    class="survey-form-static-date-time">
    <Field
      readonly
      clickable
      placeholder="选择时间"
      :value="currValue"
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
      <template v-if="['date','time','datetime'].indexOf(dateType) > -1">
        <DatetimePicker
          :value="currentDate"
          :type="dateType"
          :min-date="minDate"
          :max-date="maxDate"
          :formatter="formatter"
          @cancel="onPickerCancel"
          @confirm="onDateConfirm"
        />
      </template>
      <template v-else>
        <Picker
          show-toolbar
          :columns="columns"
          @cancel="onPickerCancel"
          @confirm="onPickerConfirm"
        />
      </template>
    </Popup>
  </div>
</template>

<script>
  import { DatetimePicker, Field, Icon, Picker, Popup } from 'vant';

  export default {
    name: 'DateTime',
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
        currValue: '',
        dateIcon: 'calender-o',
        currentDate: null,
        minDate: null,
        maxDate: null,
        dateType: 'datetime',
        dateFmt: 'yyyy-MM-dd hh:mm',
        columns: [],
        show: false,
      };
    },
    computed: {
      showIcon() {
        if (String.isEmpty(this.currValue) || this.disabled) {
          return this.dateIcon;
        }
        return 'clear';
      },
    },
    methods: {
      pageCreated() {
        this.compData();
      },
      onClickIcon() {
        if (this.showIcon !== 'clear') {
          return;
        }
        this.currValue = '';
        this.field.value = null;
      },
      compData() {
        this.currentDate = new Date();
        this.minDate = new Date(1900, 1, 1);
        this.maxDate = new Date(this.currentDate.getFullYear() + 50, 11, 31);
        if (!this.field) {
          return;
        }
        if (this.field.dateFormat) {
          this.dateType = this.field.dateFormat;
        }
        if (this.field.type === 'time') {
          this.dateType = 'time';
        }
        if (this.dateType === 'date') {
          this.dateFmt = 'yyyy-MM-dd';
          this.dateIcon = 'calender-o';
        } else if (this.dateType === 'datetime') {
          this.dateFmt = 'yyyy-MM-dd hh:mm';
          this.dateIcon = 'calender-o';
        } else if (this.dateType === 'time') {
          this.dateFmt = 'hh:mm';
          this.dateIcon = 'clock-o';
        } else if (this.dateType === 'year') {
          this.dateFmt = 'YYYY年';
          this.dateIcon = 'arrow';
        } else if (this.dateType === 'month') {
          this.dateFmt = 'MM月';
          this.dateIcon = 'arrow';
        }
        if (this.dateType === 'time') {
          this.currentDate = '';
          if (!String.isEmpty(this.field.value)) {
            this.currentDate = this.field.value;
            this.currValue = this.field.value;
          }
        } else {
          const value = Date.parser(this.field.value);
          if (Date.isDate(value)) {
            this.currentDate = value;
            this.currValue = Date.format(value, this.dateFmt);
          }
        }
        if (this.dateType === 'year' || this.dateType === 'month') {
          const list = [];
          let start = 0;
          let end = 0;
          let suffix = '';
          let defaultIndex = 0;
          if (this.dateType === 'year') {
            const year = this.currentDate.getFullYear();
            start = 1900;
            end = year + 101;
            suffix = '年';
            defaultIndex = year - 1900;
          } else if (this.dateType === 'month') {
            start = 1;
            end = 13;
            suffix = '月';
            defaultIndex = this.currentDate.getMonth();
          }
          for (let i = start; i < end; i++) {
            list.push({
              text: i + suffix,
              value: i,
            });
          }
          this.columns = [
            {
              values: list,
              defaultIndex,
            },
          ];
        }
      },
      formatter(type, val) {
        if (type === 'year') {
          return `${ val }年`;
        }
        if (type === 'month') {
          return `${ val }月`;
        }
        if (type === 'day') {
          return `${ val }日`;
        }
        if (type === 'hour') {
          return `${ val }时`;
        }
        if (type === 'minute') {
          return `${ val }分`;
        }
        return val;
      },
      onShow() {
        if (this.disabled) {
          return;
        }
        this.show = true;
      },
      onPickerCancel() {
        this.show = false;
      },
      onDateConfirm(date) {
        let value = date;
        if (Date.isDate(value)) {
          if (this.dateType === 'date') {
            value = new Date(value.getFullYear(), value.getMonth(), value.getDate());
          } else if (this.dateType === 'datetime') {
            value = new Date(value.getFullYear(), value.getMonth(), value.getDate(), value.getHours(), value.getMinutes());
          }
          this.currValue = Date.format(value, this.dateFmt);
        } else {
          this.currValue = value;
        }
        this.currentDate = value;
        this.field.value = value;
        this.show = false;
      },
      onPickerConfirm(section) {
        this.show = false;
        if (Array.isEmpty(section) || Object.isEmpty(section[0])) {
          return;
        }
        const now = new Date();
        let value = now;
        if (this.dateType === 'year') {
          value = new Date(section[0].value, 0, 1);
        } else if (this.dateType === 'month') {
          value = new Date(now.getFullYear(), section[0].value, 1);
        }
        this.currentDate = value;
        this.field.value = value;
        this.currValue = Date.format(value, this.dateFmt);
      },
    },

  };
</script>

<style lang="less" scoped>
  .survey-form-static-date-time {
    /deep/ .van-cell {
      padding: 10px 0;
    }

    .right-icon {
      color: #c8c9cc;
    }
  }
</style>
