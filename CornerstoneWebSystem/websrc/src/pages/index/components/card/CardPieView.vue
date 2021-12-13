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
            <v-chart :autoresize="true" :options="chartOptions" style="width:100%;height:260px"/>
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
                    trigger: 'item',
                    formatter: '{a} <br/>{b} : {c} ({d}%)'
                },
                legend: {
                    data:[],
                    show:false,
                },
                series: [{
                    name: '数量',
                    data: [],
                    type: 'pie',
                    radius : '60%',
                    itemStyle: {
                        normal: {
                            color: function(params) {
                                return chartColorList[params.dataIndex]
                            }
                        },
                    },
                    label : {
                        normal : {
                            formatter: '{b}:{c}: ({d}%)',
                            textStyle : {
                            fontWeight : 'normal',
                            fontSize : 12
                            }
                        }
                    }
                }]
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
            this.chartOptions.legend.data=[];
            this.chartOptions.series[0].data=[];
            var data=JSON.parse(this.card.cardData);
            for(var i=0;i<data.dataList.length;i++){
                var t=data.dataList[i];
                this.chartOptions.legend.data.push(t.name);
                this.chartOptions.series[0].data.push({
                     name:t.name,
                     value:t.value
                })
            }

        }
    }
}
</script>
