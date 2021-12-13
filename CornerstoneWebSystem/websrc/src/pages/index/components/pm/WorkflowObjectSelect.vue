<style scoped>
.workflow-object-select{
    display: flex;
    align-items: center;
    flex-wrap: wrap;
}
.ivu-form-item-error .workflow-object-select{
    border:1px solid #ed4014;
}
.not-set-label{
    font-size: 14px;
    color: rgb(153, 153, 153);
}
</style>

<i18n>
    {
    "en": {
        "添加": "Add",
        "只能选择一条数据": "Only one item can be selected",
        "未设置": "none"
    },
    "zh_CN": {
    "添加": "添加",
    "只能选择一条数据": "只能选择一条数据",
    "未设置": "未设置"
    }
    }
</i18n>

<template>
<div class="workflow-object-select">
    <ColorTag style="margin-bottom:5px"
        v-for="item in list"
        :key="'o'+item.id"
        :closable="!disabled" @on-close="removeFromList(list,item)">{{item.name}}</ColorTag>
    <Button v-if="!disabled" icon="ios-add" type="dashed" size="small" @click="selectObject(list)">{{$t('添加')}}</Button>
    <span class="not-set-label" v-if="disabled==true&&list.length==0">{{$t('未设置')}}</span>
</div>
</template>
<script>
export default {
    props:['value','countType','disabled','type'],
    data() {
        return {
            list:[]
        };
    },
    watch:{
        value(val){
            this.setupValue();
        }
    },
    mounted(){
        this.setupValue();
    },
    methods: {
        setupValue(){
            this.list=[];
            if(this.value!=null){
                this.value.forEach(item=>{
                    this.list.push(item);
                })
            }
        },
        selectObject(targetList){
            if(this.countType=='single'&&this.list.length>=1){
                app.toast(this.$t('只能选择一条数据'))
                return;
            }
            if(this.type=='user'){
                app.showDialog(MemberSelectDialog,{
                    countType:this.countType,
                    callback:(list)=>{
                        list.forEach(item=>{
                            var user={
                                id:item.accountId,
                                name:item.title
                            }
                            this.addToList(targetList,user);
                        })
                    }
                })
            }
            if(this.type=='department'){
                app.showDialog(DepartmentSelectDialog,{
                    countType:this.countType,
                    callback:(list)=>{
                        list.forEach(item=>{
                            var user={
                                id:item.id,
                                name:item.title
                            }
                            this.addToList(targetList,user);
                        })
                    }
                })
            }
            if(this.type=='companyRole'){
                app.showDialog(RoleSelectDialog,{
                    roleType:2,
                    callback:(list)=>{
                        for(var i=0;i<list.length;i++){
                            var t=list[i];
                            var item={
                                id:t.id,
                                name:t.name
                            }
                            this.addToList(targetList,item)
                        }
                    }
                })
            }
            if(this.type=='projectRole'){
                app.showDialog(RoleSelectDialog,{
                    roleType:1,
                    callback:(list)=>{
                        for(var i=0;i<list.length;i++){
                            var t=list[i];
                            var item={
                                id:t.id,
                                name:t.name
                            }
                            this.addToList(targetList,item)
                        }
                    }
                })
            }

        },
        addToList(list,item){
            if(this.countType=='single'&&(this.list.length+list.length)>1){
                return;
            }
            for(var i=0;i<list.length;i++){
                var t=list[i];
                if(t.id==item.id){
                    return;
                }
            }
            list.push(item)
            this.$emit('input',this.list)
        },
        removeFromList(list,item){
             for(var i=0;i<list.length;i++){
                var t=list[i];
                if(t.id==item.id){
                   list.splice(i,1);
                   this.$emit('input',this.list)
                   return;
                }
            }
        },
    }
};
</script>
