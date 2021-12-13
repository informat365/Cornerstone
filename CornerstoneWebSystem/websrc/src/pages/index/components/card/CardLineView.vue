<style scoped>
    .card-main-content{
        flex:1;
        position: relative;
        width:100%;
    }
</style>
<template>
    <CardBaseView :card="card" :desc="desc">
        <div slot="content" class="card-main-content">
            <v-chart :autoresize="true" :options="chartOptions" style="width:100%;height:280px"/>
        </div>
    </CardBaseView>
</template>

<script>
export default {
    mixins: [componentMixin],
    props:['card'],
    data(){
        return {
            desc:"",
            chartOptions:{
                tooltip: {
                    trigger: 'axis'
                },
                xAxis: {
                    data:[],
                    type: 'category',
                },
                yAxis: {
                    type: 'value'
                },
                legend: {
                    data:['数量'],
                    show:false,
                },
                series: [{
                    name: '数量',
                    data: [],
                    type: 'line',
                    itemStyle: {
                        normal: {
                            color: function(params) { 
                                return chartColorList[params.dataIndex] 
                            }
                        },
                    },
                }]
            },
        }
    },  
    mounted(){
        this.setupData();
    },
    methods:{
        setupData(){
            var data=JSON.parse(this.card.cardData);
            this.chartOptions.xAxis.data=[];
             this.chartOptions.series[0].data=[];
            for(var i=0;i<data.dataList.length;i++){
                var t=data.dataList[i];
                this.chartOptions.xAxis.data.push(t.name);
                this.chartOptions.series[0].data.push({
                     name:t.name,
                     value:t.value
                })
            }
        }
    }
}
</script>