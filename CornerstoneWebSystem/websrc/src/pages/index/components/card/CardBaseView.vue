<style scoped>
    .card-name{
        font-size:15px;
        font-weight: bold;
    }
    .card-desc{
        font-size:12px;
        color:#999;
        padding-bottom:5px;
        padding-top:3px;
    }
    .card-content{
        flex:1; 
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        overflow: auto;
        padding:0 15px;
    }
    .card-body{
        display: flex;
        flex-direction: column;
        width:100%;
        flex:1;
        position: relative;
    }
    .card-opt{
        position:absolute;
        top:15px;
        right:15px;
    }
    .opt-more-btn{
        cursor: pointer;
    }
    .opt-more-btn:hover{
        color:#0094FB;
    }
    .card-icon{
        width:25px;
        display: inline-block;
    }
</style>
<template>
    <div class="card-body">
        <Dropdown v-if="editable" class="card-opt" transfer>
            <Icon class="opt-more-btn" size="20" type="ios-more"></Icon>
            <DropdownMenu slot="list">
                <DropdownItem @click.native="refreshCard">刷新数据</DropdownItem>
                <DropdownItem @click.native="editCard">编辑卡片</DropdownItem>
                <DropdownItem @click.native="deleteCard">删除卡片</DropdownItem>
            </DropdownMenu>
        </Dropdown>
        <div style="padding:15px;padding-bottom:0">
            <div class="card-name text-no-wrap">{{card.name}}</div>
            <div class="card-desc text-no-wrap">{{descString}}</div>
        </div>
        <div class="card-content scrollbox">
            <slot name="content"></slot>
        </div>
    </div>
</template>

<script>
export default {
    mixins: [componentMixin],
    props:['desc','card'],
    data(){
        return {
            
        }
    },  
    computed:{
        descString(){
            if(this.desc==null||this.desc==""){
                var t= "";
                if(this.card.projectName){
                    t+=this.card.projectName;
                }
                if(this.card.projectIdList&&this.card.projectIdList.length>0){
                    t+=this.card.projectIdList.length+"个项目";
                }
                if(this.card.iterationName){
                    t+= " · "+this.card.iterationName
                }
                if(this.card.objectTypeName){
                    t+= " · "+this.card.objectTypeName
                }
                t+= " · "+formatDatetime(this.card.updateTime);
                return t;
            }
            return this.desc;
        },
        editable(){
            return this.card.createAccountId==app.account.id;
        }
    },
    methods:{
        deleteCard(){
            app.confirm('确定要删除「'+this.card.name+'」?',()=>{
                app.invoke("BizAction.deleteDashboardCard",[app.token,this.card.id],list => {
                    app.postMessage('card.edit');
                })
            })
        },
        editCard(){
            app.showDialog(CardEditDialog,{
                id:this.card.id
            })
        },
        refreshCard(){
            app.invoke("BizAction.refreshDashboardCard",[app.token,this.card.id],list => {
                app.toast('数据已刷新');
                app.postMessage('card.edit');
            })
        }
    }
}
</script>