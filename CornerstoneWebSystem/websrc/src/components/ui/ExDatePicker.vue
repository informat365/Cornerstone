<style scoped>

</style>
<template>
    <DatePicker
        :type="type"
        :placeholder="placeholder"
        :format="format"
        :placement="placement"
        :disabled="disabled"
        :clearable="clearable"
        :readonly="readonly"
        :transfer="transfer"
        :options="options"
        :start-date="startDate"
        :editable="editable"
        v-model="dateValue"
        @on-change="onChangeEvent"></DatePicker>
</template>
<script>

    export default {
        name: 'ExDatePicker',
        props: {
            type: {
                type: String,
            },
            format: {
                type: String,
            },
            placement: {
                type: String,
            },
            placeholder: {
                type: String,
            },
            transfer: {
                type: Boolean,
                default: true,
            },
            disabled: {
                type: Boolean,
                default: false,
            },
            clearable: {
                type: Boolean,
                default: false,
            },
            readonly: {
                type: Boolean,
                default: false,
            },
            startDate: {
                type: Date,
            },
            options: {
                type: Object,
            },
            value: {
                type: [String, Date, Number],
            },
            dayEnd: {
                type: Boolean,
                default: false
            },
            editable:{
                type:Boolean,
                default:true
            }
        },
        data: function () {
            return {
                dateValue: null,
            };
        },
        mounted() {
            if (this.value) {
                this.dateValue = new Date(this.value);
            } else {
                this.dateValue = null;
            }
        },
        watch: {
            dateValue: function (val) {

                var changedValue = null;
                if (val != '') {
                    changedValue = (val == null ? null : val.getTime());
                    if (this.value != changedValue) {
                        if (this.dayEnd) {
                            if (changedValue && this.type === 'date') {
                                this.$emit('input', changedValue + 24 * 3600 * 1000 - 1);
                            } else {
                                this.$emit('input', changedValue);
                            }
                        } else {
                            this.$emit('input', changedValue);
                        }
                    }
                } else {
                    this.$emit('input', null);
                }
            },
            value: function (val) {
                if (val) {
                    this.dateValue = new Date(val);
                } else {
                    this.dateValue = null;
                }
            },
        },
        methods: {
            onChangeEvent(e) {
                this.$emit('on-change');
            },
        },
    };
</script>
