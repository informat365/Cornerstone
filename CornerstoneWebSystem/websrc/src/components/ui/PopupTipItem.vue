<style scoped>
    .check{
        color:#2391ff;
    }
    .popuptip-item-name{
        font-size:14px;
        color:#2f2e2e;
        padding:10px 20px;
        font-weight: 400;
        min-width: 250px;
    }
    .popuptip-item-name:first-child{
        margin-top:10px;
    }
    .popuptip-item-name-divide{
        border-top:1px solid #eee;
    }
    .popuptip-item-name:hover{
        background-color:#f3f3f3;
    }
</style>
<template>
 <Row @click.native="clickItem()" class="popuptip-item-name">
        <Col span="21" class="name-row"><div class="text-no-wrap">{{name}}</div></Col>
        <Col span="3" style="text-align:right">
            <Icon v-if="isSelected" class="check" type="md-checkmark"/> &nbsp;
        </Col>
</Row>
</template>
<script>
export default {
    name:"PopupTipItem",
    props: ['name','selected'],
    data (){
        return{
            isSelected:this.selected
        }
    },
    watch:{
        selected(val){
            this.isSelected=val;
        }
    },
    methods:{
        clickItem(e){
            this.$emit('click')
        }
    }
}
</script>