<style scoped>
.form-item-holder{
    display: inline-block;
    margin-bottom:15px;
    cursor: pointer;
    padding-right:10px;
    position: relative;
    border:2px solid #fff;
}
.form-item-holder:hover{
    border:2px dashed #feda77;
}
.form-item-select{
    border:2px dashed #feda77;
}
.copy-btn{
    position:absolute;
    top:0;
    right:0;
    visibility: hidden;
}
.form-item-select .copy-btn{
    visibility:visible;
}
.form-item-holder-width-25{
    width:25%;
}
.form-item-holder-width-50{
    width:50%;
}
.form-item-holder-width-75{
    width:75%;
}
.form-item-holder-width-100{
    width:100%;
}
.form-item-holder-title{
    font-size:13px;
    color:#666;
    font-weight: bold;
}
.form-item-holder-input-box{
    min-height:30px;
    border:1px solid #eee;
    line-height: 30px;
    color:#999;
    margin-top:5px;
    padding:0 7px;
}
.required{
    color:red;
}
.form-item-holder-text-area .form-item-holder-input-box{
    height:60px;
}
.form-item-holder-text-rich .form-item-holder-input-box{
    height:60px;
}
.form-item-holder-table .form-item-holder-input-box{
    height:60px;
    border:none;
}
.form-item-holder-static-groupitem{
    font-weight: bold;
    color:#333;
    border-left:3px solid #4494F1;
    font-size:13px;
    margin: 15px 0;
    padding-left:7px;
}
.form-item-holder-static-labelitem{
    font-size:13px;
    color:#333;
}
.option-item{
    margin-right:3px;
    font-weight: bold;
    background-color: #999;
    color:#fff;
    border-radius: 2px;
    padding:0 4px;
}
.option-item-v{
    display: block;
    line-height: 1;
    margin-bottom:5px;
}
.option-item-checked{
    background-color: #4494F1;
}


    .form-table th{
        border-right:1px solid #333;
        padding: 3px 5px;
        font-weight: bold;
        position: relative;
        font-size:13px;
        word-break: break-all;
        height:24px;
    }
    .form-table th:first-child{
        border-left:1px solid #333;
       
    }
    .form-table td{
        border-right:1px solid #333;
        font-size:13px;
        padding: 3px 5px;
        word-break: break-all;
    }
    .form-table td:first-child{
        border-left:1px solid #333;
    }
    .form-table tr{
        border-bottom:1px solid #333;
    }
    .form-table{
        width:100%;
        border-collapse: collapse;
        border-spacing: 0;
        text-align: left;
        table-layout: fixed;
        border-top:1px solid #333;
    }
    .remark{
        font-size:12px;
        color:#999;
        margin-left:5px;
        font-weight: normal;
    }
.form-item-holder-static-alert-success{
    border: 1px solid #8ce6b0;
    background-color: #edfff3;
    font-size:13px;
    padding:10px;
}
.form-item-holder-static-alert-warning{
    border: 1px solid #ffd77a;
    background-color: #fff9e6;
    font-size:13px;
    padding:10px;
}
.form-item-holder-static-alert-error{
    border: 1px solid #ffb08f;
    background-color: #ffefe6;
    font-size:13px;
    padding:10px;
}
.form-item-holder-static-alert-info{
    border: 1px solid #abdcff;
    background-color: #f0faff;
    font-size:13px;
    padding:10px;
}
</style>
<i18n>
{
	"en": {
        "复制": "复制",
		"删除": "删除"
    },
	"zh_CN": {
		"复制": "复制",
		"删除": "删除"
	}
}
</i18n>
<template>
    <div class="form-item-holder" :data-id="item.id" :class="[widthClass,typeClass,selectClass]">
        <template v-if="item.type.indexOf('static')==-1">
            <div class="form-item-holder-title">
                {{item.name}} 
                <span class="required" v-if="item.required">*</span> 
                <span class="remark">{{item.remark}} </span>
            </div>
            <div class="form-item-holder-input-box text-no-wrap">
                <template v-if="item.optionList">
                    <span class="option-item" 
                    :class="{'option-item-checked':option.checked,'option-item-v':item.optionLayout=='vertical'}" 
                    v-for="(option,opIdx) in item.optionList" :key="'op'+opIdx">{{option.value}}</span>
                </template>
                <template v-if="item.columnList">
                    <table class="form-table">
                        <thead>
                            <tr>
                                <th v-for="(col,colIdx) in item.columnList" :key="'col'+colIdx" :style="{'width':col.width+'px'}">{{col.name}}</th>    
                            </tr>
                        </thead>
                    </table>
                </template>
            </div>
        </template>
        <template v-if="item.type=='static-group'">
            <div class="form-item-holder-static-groupitem">
                {{item.name}}
            </div>
        </template>
        <template v-if="item.type=='static-label'">
            <div class="form-item-holder-static-labelitem">
                <RichtextLabel :value="item.content"/>
            </div>
        </template>
        <template v-if="item.type=='static-alert'">
             <div :class="'form-item-holder-static-alert-'+item.alertType">{{item.remark}}</div>
        </template>
        <div class="copy-btn">
            <Button style="margin-right:3px" @click="clickCopy" type="success" size="small">{{$t('复制')}}</Button>
            <Button  @click="clickDelete" type="error" size="small">{{$t('删除')}}</Button>
        </div>
        
    </div>
</template>
<script>
export default {
    props:['item','select'],
    data(){
        return {
            widthClass:'form-item-holder-width-'+this.item.width,
            typeClass:'form-item-holder-'+this.item.type,
            selectClass:'form-item-select'
        }
    },
    watch:{
        "item.width"(val){
            this.widthClass='form-item-holder-width-'+this.item.width;
        },
        "item.typeClass"(val){
            this.typeClass='form-item-holder-'+this.item.type;
        },
        select(val){
            this.setupVisibe()
        }
    },
    mounted(){
       this.setupVisibe()
    },
    methods:{
        setupVisibe(){
            this.selectClass=this.select==true?'form-item-select':""
        },
        clickCopy(){
            this.$emit('copy')
        },
        clickDelete(){
            this.$emit('delete')
        }
    }
}
</script>