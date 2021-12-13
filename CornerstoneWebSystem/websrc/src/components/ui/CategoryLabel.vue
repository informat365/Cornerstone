<style scoped>
    .check{
        color:#0097F7;
    }
    .tag-color{
        display: inline-block;
        width:30px;
        height: 30px;
        border-radius: 50%;
        margin-right:10px;
        color:#fff;
        overflow: hidden;
        padding-top:3px;
    }
    .nodata-label{
        line-height: 1;
        vertical-align: middle;
        font-size:12px;
    }
</style>
<template>
<span>
    <span>
        <TaskCategory  v-for="item in selectedItems" :key="item.id" :color="item.color" :label="item.name"/>
        <span class="nodata-label" v-if="selectedItems==null||selectedItems.length===0">{{placeholder}}</span>
    </span>
</span>

</template>
<script>
export default {
    mixins: [componentMixin],
    name:"CategorySelect",
    props: ['value','categoryList','placeholder'],
    data (){
        return{
            visible:false,
            // selectedItems:[],
        }
    },
    computed:{
        selectedItems(){
            if(!Array.isArray(this.value) || this.value.length === 0 || !Array.isArray(this.categoryList) || this.categoryList.length === 0){
                return [];
            }
            return this.categoryList.filter(item => this.value.indexOf(item.id) > -1);
        }
    },
}
</script>
