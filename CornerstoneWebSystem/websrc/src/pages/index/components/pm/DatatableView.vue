
<style scoped>
    .table-wrap{
        overflow: auto;
        background-color: #fff;
        border-top:1px solid #EBEEF5;
        color:#333;
        font-weight: 500;
    }
    .header-name{
        font-size:15px;
        font-weight: bold;
        color:#333;
    }
    .cell{
        position: relative;
    }
    .cell input{
        border:none;
        padding:0;
        outline: none;
        width:100%;
    }
</style>

<template>
<div class="table-wrap">
   
    <table class="line-table">
        <col :width="col.width+'px'" v-for="(col,colIdx) in tableData.col" :key="'col'+colIdx"></col>
        <thead>
            <template v-for="(header,idx) in tableData.header" >
            <tr :key="'header'+idx">
                <template v-for="(cell,cellIdx) in header.cells" >
                    <th :style="getCellStyle(cell)" :key="'header-cell'+idx+'_'+cellIdx" 
                        :rowspan="cell.rowspan" 
                        :colspan="cell.colspan">
                        {{cell.value}}
                    </th>
                </template>
            </tr>     
            </template>
        </thead>
        <tbody ref="tbody">
            <tr v-for="(row,idx) in tableData.body" :key="'row'+idx" >
                <template v-for="(cell,cellidx) in row.cells" >
                    <td class="cell"
                        :rowspan="cell.rowspan"
                        :colspan="cell.colspan" 
                        :key="'cell'+idx+'_'+cellidx"
                        :style="getCellStyle(cell)">
                        <template v-if="cell.type==null||cell.type=='text'">{{cell.value}}</template>
                        <input v-if="cell.type=='input'" placeholder="占位符" v-model="cell.value" @change="updateCharts"></input>
                        <DatatableChart v-if="cell.type=='chart'" :chart="getChartData(cell)" :table="tableData" :tableVersion="chartDataVersion"></DatatableChart>
                    </td>
                </template>
                
            </tr>
        </tbody>
    </table>
    
</div>
</template>

<script>
export default {
    name: 'DatatableView',
    props: ['data'],
    components: {
    },
    data(){
        return{
            tableData:this.setupData(this.data),
            chartDataVersion:0,
        }
    },
    watch:{
        data(val){
            this.tableData=this.setupData(val);
        }
    },
    created(){
       
    }, 
    mounted(){
       
    },
    methods:{
        updateCharts(){
            this.chartDataVersion++;
        },
        getChartData(cell){
            if(cell.value){
                try{
                    return JSON.parse(cell.value);
                }catch(e){}
            }
            return null;
        },
        getCellStyle(cell){
            var style={};
            if(cell.color){
                style.color="#"+cell.color;
            }
            if(cell.bgcolor){
                style.backgroundColor="#"+cell.bgcolor;
            }
            if(cell.fsize){
                style.fontSize=cell.fsize+"px";
            }
            if(cell.width){
                style.width=cell.width+"px";
            }
            if(cell.align){
                style.textAlign=cell.align;
            }
            return style;
        },
        setupData(tableData){
            return tableData;
        }
    }
}
</script>

