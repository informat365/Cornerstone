<style scoped>
    .left-info{
        font-size:16px;
        color:#333;
    }
    .left-day{
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
    .card-over{
        color:#F43B1F !important;
    }
</style>
<template>
    <CardBaseView :card="card" :desc="desc">
        <div slot="content" class="card-main-content" >
                <span class="left-info">{{diffStr}}</span>
                <span class="left-day numberfont" :class="{'card-over':isOver}">{{day}}</span>
                <span class="left-info">天</span>
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
            day:"",
            diffStr:"",
            isOver:false
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
            var diff = window.getLeftDays(this.card.dueDate);
            if(diff>=0){
                this.desc="距离"+window.formatDate(this.card.dueDate)+"的天数";
                this.diffStr="还剩余";
                this.isOver=false;
            }else{
                this.desc="超过"+window.formatDate(this.card.dueDate)+"的天数";
                this.diffStr="已过去";
                this.isOver=true;
            }
            this.day=Math.abs(diff);
        }
    }
}
</script>