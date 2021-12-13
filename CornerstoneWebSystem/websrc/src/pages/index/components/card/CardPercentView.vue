<style scoped>
    .card-main-content{
        flex:1;
        position: relative;
    }
</style>
<template>
    <CardBaseView :card="card" desc="">
        <div slot="content" class="card-main-content">
             <v-chart :autoresize="true" :options="chartOptions" style="width:350px;height:200px"/>
        </div>
    </CardBaseView>
</template>

<script>
export default {
    mixins: [componentMixin],
    props:['card'],
    data(){
    return {
        labelName:"",
        chartOptions :{
            color:['#0094FB','#CCCCCC'],
            tooltip: {
                trigger: 'item',
                formatter: "{a} <br/>{b}: {c} ({d}%)"
            },
            legend: {
                data:['数量','剩余'],
                show:false,
            },
            series: [
                {
                    name:'数据',
                    type:'pie',
                    radius: ['50%', '70%'],
                    label : {
                        normal : {
                            formatter: '{b}:{c}: ({d}%)',
                            textStyle : {
                            fontWeight : 'normal',
                            fontSize : 12
                            }
                        }
                    },
                    data:[
                        {value:0, name:'数量', 
                        label: {
                            normal: {
                                    show: true,
                                    position: 'center',
                                    formatter :  (params)=>{
                                        return params.percent + '%'+'\n'+this.labelName
                                    },
                                    textStyle: {
                                        fontSize: '20',
                                        fontWeight: 'bold'
                                    }
                                },
                            },
                        },
                        {value:0, name:'剩余'},
                    ]
                }
            ]}
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
            if(data.value==-1){
                data.value=0;
            }
            this.labelName=data.name;
            this.chartOptions.series[0].data[0].value=data.value;
            this.chartOptions.series[0].data[1].value=data.total-data.value;
            
        }
    }
}
</script>