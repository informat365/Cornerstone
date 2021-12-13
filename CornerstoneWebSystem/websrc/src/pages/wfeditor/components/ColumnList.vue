<style scoped>
    .option-item{
        display: flex;
        padding:5px 0;
        align-items: center;
    }
    .input{
        flex:1;
    }
    .remove-btn{
        margin-left:5px;
        color:#C7000B;
        cursor: pointer;
    }
</style>
<i18n>
{
	"en": {
        "名称": "名称",
		"宽度": "宽度",
		"新增": "新增"
    },
	"zh_CN": {
		"名称": "名称",
		"宽度": "宽度",
		"新增": "新增"
	}
}
</i18n>
<template>
    <div>
        <draggable v-model="list" :options="{draggable:'.option-item'}">
            <div v-for="(item,idx) in list" :key="'op'+idx" class="option-item">
                <Input v-model.trim="item.name" class="input" :placeholder="$t('名称')"/>
                <InputNumber style="margin-left:5px" v-model="item.width" class="input" :placeholder="$t('宽度')"/>
                <Icon @click.native="removeOption(idx)" class="remove-btn" size="20" type="md-remove-circle" />
            </div>
            <div>
                <Button @click="addOption" icon="ios-add" type="dashed" size="small">{{$t('新增')}}</Button>
            </div>
        </draggable>
    </div>
</template>
<script>
import draggable from 'vuedraggable'
export default {
    props:['value'],
    components:{
        draggable
    },
    data(){
        return {
            list:this.value
        }
    },
    watch:{
        value(val){
            this.list=val;
        },
        list(val){
            this.$emit('input',val)
        }
    },
    methods:{
        addOption(){
            this.list.push({
                width:100,
                name:null
            })
        },
        removeOption(idx){
            this.list.splice(idx,1);
        }
    }
}
</script>