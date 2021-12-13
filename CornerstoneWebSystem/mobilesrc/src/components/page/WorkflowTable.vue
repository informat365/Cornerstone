<style scoped>
.form-table th{
    border-right:1px solid #555;
    padding: 3px 5px;
    font-weight: bold;
    position: relative;
    font-size:13px;
    word-break: break-all;
    height:24px;
    background-color: #CCCCCC;
}
.form-table th:first-child{
    border-left:1px solid #555;       
}
.form-table td{
    border-right:1px solid #555;
    font-size:13px;
    padding: 3px 5px;
    word-break: break-all;
    height:24px;
}
.form-table td:first-child{
    border-left:1px solid #555;
}
.form-table tr{
    border-bottom:1px solid #555;
}
.form-table{
    width:100%;
    border-collapse: collapse;
    border-spacing: 0;
    text-align: left;
    table-layout: fixed;
    border-top:1px solid #555;
}
.remove-btn{
    color:#C7000B;
    cursor: pointer;
}
</style>
  
<template>        
<div>                      
    <table class="form-table">
        <thead>
            <tr>
                <th v-for="(col,colIdx) in columnList" :key="'col'+colIdx" :style="{'width':col.width+'px'}">{{col.name}}</th>    
            </tr>
        </thead>
        <tbody>
            <tr v-for="(row,rowIndex) in list" :key="'row'+rowIndex">
                <td v-for="(col,colIdx) in columnList" :key="'col'+colIdx" >
                    <template >{{row[colIdx]}}</template>
                </td>  
            </tr>
        </tbody>
    </table>
</div>
</template>
<script>
export default {
    props:['value','columnList'],
    data() {
        return {
            list:[]
        };
    },
    watch:{
        value(val){
            this.setupValue();
        }
    },
    mounted(){
        this.setupValue();
    },
    methods: {
        setupValue(){
            this.list=[];
            if(this.value!=null){
                this.value.forEach(item=>{
                    this.list.push(item);
                })
            }
            if(this.list.length==0){
                this.list.push({})
            }
        }
    }
};
</script>