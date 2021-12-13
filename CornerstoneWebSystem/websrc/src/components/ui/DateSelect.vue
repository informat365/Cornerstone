<style scoped>

</style>
<template>
<span>
    <span v-if="disabled==true">
        <span class="placeholder-label" v-if="dateValue == null||dateValue==''">{{placeholder}}</span>
        <template v-else>
            <span v-if="showTimeField!=true" style="color:#111;font-weight:bold">{{ dateValue|fmtDate }}</span>
            <span v-if="showTimeField" style="color:#111;font-weight:bold">{{ dateValue|fmtDateTime }}</span>
        </template>
    </span>
    <DatePicker
        transfer
        v-if="disabled!==true"
        :open="visible"
        :options="options"
        confirm
        @on-change="handleChange"
        @on-clear="handleClear"
        @on-ok="handleOk"
        @on-clickoutside="onClickoutside"
        :placeholder="placeholder"
        :format="format"
        :type="showTimeField===true?'datetime':'date'"
        :placement="placement"
        :disabled="disabled"
        :clearable="clearable"
        :readonly="readonly"
        v-model="dateValue">
     <span class=" popup-select" @click.stop="handleClick">
           <span class="placeholder-label" v-if="dateValue == null||dateValue===''">{{placeholder}}</span>
           <template v-else>
                <span v-if="showTimeField!==true">{{ dateValue|fmtDate }}</span>
                <span v-if="showTimeField">{{ dateValue|fmtDateTime }}</span>
            </template>
           <Icon style="margin-left:10px" class="popup-select-arrow" type="ios-arrow-down"
           ></Icon>
    </span>
    </DatePicker>
</span>
</template>
<script>
    export default {
        name: 'DateSelect',
        props: ['value', 'format', 'start', 'end',
            'options', 'placement',
            'placeholder',
            'disabled',
            'showTimeField',
            'clearable',
            'readonly'],
        data: function () {
            return {
                visible: false,
                dateValue: null,
                hasDefaultValue: false,
                defShowDate: null,
                hasDateChange: false
            };
        },
        mounted() {
            this.setupValue();
        },
        watch: {
            dateValue: function (val) {
                var changedValue = null;
                if (val != '') {
                    changedValue = val == null ? null : val.getTime();
                    if (this.value != changedValue) {
                        this.$emit('input', changedValue);
                    }
                } else {
                    this.$emit('input', null);
                }
            },
            value: {
                immediate: true,
                handler(val) {
                    if (this.value) {
                        this.dateValue = new Date(this.value);
                    } else {
                        this.dateValue = null;
                    }
                }
            },
            visible: function (val) {
                // console.log('visible-->', val)
                if (val === false) {
                    let t = null;
                    if (this.dateValue !== '' && null != this.dateValue) {
                        t = this.dateValue.getTime();
                    }
                    this.$emit('input', t);
                    this.$emit('change', t);
                } else {
                    this.hasDefaultValue = !!this.defShowDate && !this.dateValue;
                    // console.log(this.hasDefaultValue, this.dateValue, this.defShowDate)
                    if (this.hasDefaultValue) {
                        this.$nextTick(() => {
                            this.dateValue = this.defShowDate;
                        })
                    }
                }
            },
            end: {
                immediate: true,
                handler(val) {
                    // console.log("end change", val)
                    if (val) {
                        // if(this.defShowDate&&Date.isSameDay(this.defShowDate,val)){}
                        this.defShowDate = val;

                    } else {
                        this.defShowDate = null;
                    }
                }

            },
            start: {
                immediate: true,
                handler(val) {
                    // console.log("start change", val)
                    if (val) {
                        this.defShowDate = val;
                    } else {
                        this.defShowDate = null;
                    }
                }
            },
        },
        methods: {
            setupValue() {
                if (this.value) {
                    this.dateValue = new Date(this.value);
                } else {
                    this.dateValue = null;
                }
            },
            onClickoutside() {
                if (this.hasDefaultValue) {
                    this.dateValue = null;
                }
                this.visible = false;
            },
            handleClick() {
                // console.log("handleClick", this.visible)
                if (this.visible && this.hasDefaultValue && !this.hasDateChange) {
                    this.dateValue = null;
                }
                this.visible = !this.visible;
            },
            handleChange(date) {
                // console.log("handleChange")
                this.hasDateChange = true;
                //this.visible = false;
            },
            handleClear() {
                this.dateValue = null;
                this.visible = false;
            },
            handleOk() {
                this.visible = false;
            },
        },
    };
</script>
