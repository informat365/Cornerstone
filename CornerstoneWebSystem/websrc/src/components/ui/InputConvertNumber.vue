<template>
   <InputNumber  style="width:200px" v-model="convertValue"></InputNumber>
</template>
<script>
export default {
     name:"InputConvertNumber",
    props: ['value','ratio'],
    data: function () {
        return {
            convertValue:(this.value==null?0:this.value)/this.ratio
        }
    },
    watch:{
        convertValue: function (val) {
            this.$emit('input', val*this.ratio);
        },
        value: function (val) {
            this.convertValue = val/this.ratio;
        }
    }
}
</script>