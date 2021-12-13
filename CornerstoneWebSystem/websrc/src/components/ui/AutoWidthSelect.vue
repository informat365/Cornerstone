<style scoped>
.auto-select-select{
    min-width:150px;
    max-width:400px;
    text-overflow:ellipsis;
	overflow: hidden;
	white-space: nowrap;
}
</style>
<template>
    <Select transfer class="auto-select-select" @on-change="valueChange" 
        :style="{width:displayWidth}"
        :multiple="multiple" 
        :clearable="clearable" 
        :placeholder="placeholder"
        v-model="modelData" >
          <Option v-for="item in valueList" :key="item.id" :value="item.id">{{item.name}}</Option>
    </Select>
</template>
<script>
export default {
    name:"AutoWidthSelect",
    props: ['value', 'disabled', 'multiple','clearable','valueList','type','placeholder'],
    data: function() {
        return {
            dict:app.dataDicts,
            modelData: this.value,
            displayWidth:'150px'
        };
    },
    mounted(){
        this.$nextTick(this.computeWidth)
    },
    watch: {
        modelData: function (val) {
            this.$emit('input', val);
            this.$nextTick(this.computeWidth)
        },
        value: function (val) {
            this.modelData = val;
            this.$nextTick(this.computeWidth)
        },
    },
    methods:{
        valueChange: function (args) {
            this.$nextTick(()=>{
                this.$emit('on-change',args)
            })
        },
        computeWidth(){
            var tags=this.$el.getElementsByClassName('ivu-tag');
            var t=0;
            for(var i=0;i<tags.length;i++){
                t+=(tags[i].clientWidth+25);
            }
            this.displayWidth=t+"px";
        }
    }
}
</script>