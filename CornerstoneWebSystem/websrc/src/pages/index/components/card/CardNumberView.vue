<style scoped>
    .number{
        font-size:80px;
        color:#333;
        font-weight: bold;
        text-align: center;
        margin-left:10px;
        margin-right:10px;
        padding-bottom: 30px;
    }
    .card-main-content{
        flex:1;
        display: flex;
        align-items: center;
        justify-content: center;
        padding-bottom:30px;
    }
</style>
<template>
    <CardBaseView :card="card" >
        <div slot="content" class="card-main-content" >
                <span class="number numberfont">{{showNumber}}</span>
           </div>
    </CardBaseView>
</template>

<script>
export default {
    mixins: [componentMixin],
    props:['card'],
    data(){
        return {
            showNumber:10
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
            this.showNumber=data.value;
        }
    }
}
</script>