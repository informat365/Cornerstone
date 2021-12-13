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
        "选项值": "选项值",
		"新增": "新增"
    },
	"zh_CN": {
		"选项值": "选项值",
		"新增": "新增"
	}
}
</i18n>
<template> 
    <div>
        <draggable v-model="list" :options="{draggable:'.option-item'}">
            <div v-for="(item,idx) in list" :key="'op'+idx" class="option-item">
                <Checkbox @on-change="selectOption(item)" v-model="item.checked"></Checkbox>
                <Input v-model.trim="item.value" class="input" :placeholder="$t('选项值')"/>
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
    props:['value','single'],
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
                checked:false,
                value:null
            })
        },
        removeOption(idx){
            this.list.splice(idx,1);
        },
        selectOption(item){
            if(this.single==true){
                if(item.checked){
                    for(var i=0;i<this.list.length;i++){
                        var t=this.list[i];
                        if(t!=item){
                            t.checked=false;
                        }
                    }
                }
            }
        }
    }
}
</script>