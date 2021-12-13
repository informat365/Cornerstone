<template>
   <RadioGroup v-model="modelData" v-on:on-change="valueChange">
        <Radio :disabled="disabled" :key="item.value" v-for="item in dict[type]" :label="item.value" >{{item.name}}</Radio>
    </RadioGroup>
</template>
<script>
export default {
    name:"DataDictRadio",
    props: ['value', 'disabled',  'type'],
    data: function() {
        return {
            dict:app.dataDicts,
            modelData: this.value,
        };
    },
    methods: {
        valueChange: function (args) {
            this.$emit('changed', {  args })
        }
    },
    watch: {
        modelData: function (val) {
            this.$emit('input', val);
        },
        value: function (val) {
            this.modelData = val;
        }
    }
}
</script>