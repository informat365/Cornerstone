
<style scoped>
    .chart-box{
        padding:10px;
    }
</style>

<template>
<div class="chart-box">
     <v-chart :autoresize="true" :options="chartOptions" style="width:100%;" :style="{height:chartData.height+'px'}"/>
</div>
</template>

<script>
/*
chartData:{
                title:'图表标题',
                height:400,
                swapAxis:true,
                cellRange:{
                    x1:0,
                    y1:1,
                    x2:1,
                    y2:5
                },
                xAxis:{
                    index:1,
                    type: 'category',
                },
                yAxis:{
                    type: 'value'
                },
                series:[
                    {
                        index:0,
                        name:'数量1',
                        type: 'pie',
                    },
                    {
                        index:0,
                        name:'数量2',
                        type: 'line',
                        area:false
                    }
                ]
}
*/
export default {
    name: 'DatatableChart',
    props: ['chart','table','tableVersion'],
    components: {
    },
    data(){
        return{
            chartOptions:{
                title: {
                    left:'center',
                    text: '',
                },
                tooltip: {
                    trigger: 'axis'
                }, 
                toolbox: {
                    show : true,
                    feature : {
                        dataView: {readOnly: true},
                        saveAsImage : {show: true}
                    }
                },
                xAxis: {
                    data:[],
                    axisLabel:{
                        'interval':0,
                         rotate:20
                    },
                },
                yAxis: {

                },
                series: []
            },
            chartData:this.chart
        }
    },
    watch:{
        table(val){
            this.setupChartOptions();
        },
        chart(val){
            this.chartData=val;
            this.setupChartOptions();
        },
        tableVersion(val){
            this.setupChartOptions();
        }
    },
    created(){
       
    }, 
    mounted(){
       this.setupChartOptions();
    },
    methods:{
        setupChartOptions(){
            if(this.chartData==null){
                return;
            }
            this.chartOptions.title.text=this.chartData.title;
            this.chartOptions.xAxis.type=this.chartData.xAxis.type;
            this.chartOptions.xAxis.data=[];
            this.chartOptions.yAxis.type=this.chartData.yAxis.type;
            //
            this.chartOptions.series=[];
            this.chartData.series.forEach(item=>{
                var series={
                    name: item.name,
                    data: [],
                    type: item.type,
                    barWidth : 20,
                    radius : '55%',
                    itemStyle: {
                        normal: {
                            color: function(params) { 
                                return chartColorList[params.dataIndex] 
                            }
                        },
                        emphasis: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    },
                    label : {
                        normal : {
                            formatter: '{b}:{c}: ({d}%)',
                            textStyle : {
                            fontWeight : 'normal',
                            fontSize : 12
                            }
                        }
                    },
                }
                if(item.area){
                    series.itemStyle={normal: {areaStyle: {type: 'default'}}};
                    series.smooth=true;
                }
                this.chartOptions.series.push(series)
            })
            //
            var range=this.chartData.cellRange;
            var axisChartCount=0;
            for(var rowIndex=range.y1;rowIndex<=range.y2;rowIndex++){
                var row=this.table.body[rowIndex];
                var xAxisColumnIndex = this.chartData.xAxis.index + range.x1;
                //xAxis data
                this.chartOptions.xAxis.data.push(row.cells[xAxisColumnIndex].value);
                //yAxis series
                var seriesIndex=0;
                this.chartData.series.forEach(series=>{
                    var seriesColumnIndex = series.index + range.x1;
                    if(series.type=='bar'||series.type=='line'){
                        this.chartOptions.series[seriesIndex].data.push({
                            name:series.name,
                            value:row.cells[seriesColumnIndex].value
                        })
                        axisChartCount++;
                    }
                    if(series.type=='pie'){//饼图取x轴作为name 
                        var xValue=row.cells[xAxisColumnIndex].value
                        this.chartOptions.series[seriesIndex].data.push({
                            name:xValue,
                            value:row.cells[seriesColumnIndex].value
                        })
                    }

                    seriesIndex++;
                })
            }
            if(axisChartCount==0){
                this.chartOptions.xAxis=null;
                this.chartOptions.yAxis=null;
            }
            //
            if(this.chartData.swapAxis){
                var temp=this.chartOptions.xAxis;
                this.chartOptions.xAxis=this.chartOptions.yAxis;
                this.chartOptions.yAxis=temp;
            }
            //
            //console.log(this.chartOptions)
        },
    }
}
</script>

