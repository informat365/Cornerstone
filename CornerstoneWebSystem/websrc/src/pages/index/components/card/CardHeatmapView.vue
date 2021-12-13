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
            <HeatMap :eventList="list"></HeatMap>
        </div>
    </CardBaseView>
</template>

<script>
export default {
    mixins: [componentMixin],
    props:['card'],
    data(){
        return {
            list:[]
        }
    },  
    watch:{
        card(val){
            this.setupData();
        }
    },
    mounted(){
        this.setupData();
    },
    methods:{
        setupData(){
            var data=JSON.parse(this.card.cardData);
             this.list=data.dataList;
        }
    }
}
</script>