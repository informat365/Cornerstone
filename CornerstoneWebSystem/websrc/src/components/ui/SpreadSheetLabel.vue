<style scoped>
    .sheet-box{
        height:calc(100vh - 330px)
    }
</style>

<template>
   <div ref="container" class="sheet-box"></div>
</template>
<script>
import { Spreadsheet} from "dhx-spreadsheet";
export default {
    name:"SpreadSheetLabel",
    props: ['value'],
    data: function () {
        return {
           
        }
    },
    watch:{
        value(val){
            this.parse();
        }
    },
    mounted:function(){
        this.spreadsheet = new Spreadsheet(this.$refs.container, {
			readonly: true
	    });
        this.parse();
    },
    methods:{
        parse(){
            if(this.value){
                var data=JSON.parse(this.value);
                this.spreadsheet.parse(data);
            }
        }
    }
}
</script>