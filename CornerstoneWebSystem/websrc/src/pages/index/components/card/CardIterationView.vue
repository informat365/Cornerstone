<style scoped>
    .card-main-content{
        width:100%;
        flex:1;
        align-items: center;
        justify-items: center;
        display: flex;
    }
</style>
<template>
    <CardBaseView :card="card">
        <div slot="content" class="card-main-content">
            <v-chart :autoresize="true" :options="chartOptions" style="width:100%;height:280px"/>
        </div>
    </CardBaseView>
</template>

<script>
import { graphic } from 'echarts/lib/export'
export default {
    mixins: [componentMixin],
    props:['card'],
    data(){
        return {
           chartOptions:{
                color:['#0094FB','#EC0023'],
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
                    data:['预期',"实际"]
                },
                series: [
                    {
                        name: '预期',
                        data: [],
                        type: 'line',
                        areaStyle: {
                            color: new graphic.LinearGradient(0, 0, 0, 1, [{
                                offset: 0,
                                color: '#0094FB'
                            }, {
                                offset: 1,
                                color: '#CCCCCC'
                            }])
                        },
                    },
                    {   
                        name: '实际',
                        data: [],
                        type: 'line'
                    }
                ]
            },
        }
    },  
    mounted(){
        this.setupData();
    },
    watch:{
        card(val){
            this.setupData();
        }
    },
    methods:{
        setupData(){
            var data=JSON.parse(this.card.cardData);
            var list=data.dataList;
            this.chartOptions.series[0].data=[];
            this.chartOptions.series[1].data=[];
            this.chartOptions.xAxis.data=[];
            for(var i=0;i<list.length;i++){
                    var t = list[i];
                    this.chartOptions.xAxis.data.push(formatDate(t.statDate));
                    this.chartOptions.series[0].data.push(t.expectUnfinishNum);
                    if(t.unfinishNum>=0){
                        this.chartOptions.series[1].data.push(t.unfinishNum);
                    }
            } 
        }
    }
}
</script>