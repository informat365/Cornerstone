<template>
   <Select :disabled="disabled" :multiple="multiple" :clearable="clearable" @on-change="valueChange" :placeholder="placeholder" v-model="modelData" style="width:150px">
        <Option  :key="item.id" v-for="item in tableData" :value="item.id">{{item.name}}</Option>
    </Select>
</template>
<script>
export default {
    name:"ObjectSelect",
    props: ['value', 'domain','query','disabled','clearable', 'multiple', 'placeholder', 'type', 'change'],
    data: function() {
        return {
            tableData:[],
            modelData: this.value,
        };
    },
    mounted (){
        var query={
            pageIndex:1,
            pageSize:100,
        }
        if(this.query){
            query=this.query;
        }
        app.invoke("BossAction.get"+this.domain+"List",[app.token,query],(obj)=>{
            this.tableData=obj.list;
        });
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
        valueChange: function() {
            if (this.change) {
                setTimeout(this.change, 200);
            }
        }
    }
}
</script>