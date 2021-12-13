<style scoped>
   

</style>
<template>
<div v-transfer-dom>
    <popup v-model="showPopup" max-height="90%">
        <popup-header
        left-text="取消"
        right-text="确定"
        title=""
        :show-bottom-border="false"
        @on-click-left="showPopup = false"
        @on-click-right="confirm"></popup-header>
        <group gutter="0">
          <Checklist v-model="selectedValue" :max="max" :min="min" :options="list"></Checklist>
        </group>
      </popup>
</div>
</template>
<script>
import {TransferDom, PopupHeader, Popup, Group, Checklist } from 'vux'
export default {
    props: ['value','list','selectedList','multiple'],
    components: {
        PopupHeader,
        Popup,
        Group,
        Checklist
    },
    directives: {
        TransferDom
    },
    data (){
        return{
           showPopup:this.value,
           max:this.multiple==false?1:null,
           min:this.multiple==false?1:null,
           selectedValue:this.selectedList,
        }
    },
    watch:{
        showPopup(val){
            this.$emit('input',val);
        },
        value(val){
            this.showPopup=val;
        },
        selectedList(val){
            this.selectedValue=val;
        },
        multiple(val){
            this.max=this.multiple==false?1:null;
            this.min=this.multiple==false?1:null;
        }
    },
    methods:{
        confirm(){
            this.showPopup = false
            this.$emit('change',this.selectedValue)
        },
    }
}
</script>