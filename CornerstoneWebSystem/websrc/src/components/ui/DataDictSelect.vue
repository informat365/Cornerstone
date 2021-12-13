<template>
   <Select :disabled="disabled" :multiple="multiple" :clearable="clearable" @on-change="valueChange" :placeholder="placeholder" v-model="modelData" style="width:100px">
        <Option  v-if="filterValue(item.value)" :key="item.value" v-for="item in dict[type]" :value="item.value">{{item.name}}</Option>
    </Select>
</template>
<script>
export default {
    name:"DataDictSelect",
    props: ['value', 'disabled','clearable', 'multiple', 'placeholder', 'type', 'change', 'values'],
    data: function() {
        return {
            dict:app.dataDicts,
            modelData: this.value,
        };
    },
    watch: {
        modelData: function(val) {
            this.$emit("input", val);
        },
        value: function(val) {
            this.modelData = val;
        }
    },
    methods: {
        filterValue: function(v) {
            if (this.values == null) {
                return true;
            }
            for (var i = 0; i < this.values.length; i++) {
                if (this.values[i] == v) {
                    return true;
                }
            }
            return false;
        },
        valueChange: function() {
            this.$nextTick(()=>{
                this.$emit("change");
            });
        }
    }
}
</script>

