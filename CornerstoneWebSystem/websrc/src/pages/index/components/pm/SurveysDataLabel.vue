<style scoped>
.tag{
    display: inline-block;
    padding: 1px 7px;
    border-radius: 10px;
    background-color: #3C97F0;
    color: #fff;
    font-size: 12px;
    line-height: 1;
    margin-right: 5px;
    text-overflow:ellipsis;
	overflow: hidden;
    white-space: nowrap;
    word-break: break-all;
    min-width:40px;
    text-align: center;
    line-height: 1;
}
.pre-box{
    white-space:pre-wrap;
}
</style>
<template>
<div>
    <template v-if="formData[field.id]">
        <template v-if="field.type=='text-single'">
        {{formData[field.id]}}
        </template>
         <template v-if="field.type=='system-value'">
        {{formData[field.id]}}
        </template>
        <div class="pre-box" v-if="field.type=='text-area'">{{formData[field.id]}}</div>
        <template v-if="field.type=='text-number'">
        {{formData[field.id]}}
        </template>
        <template v-if="field.type=='date'">
        {{formData[field.id]|fmtUTCDateTime}}
        </template>
        <template v-if="field.type=='time'">
        {{formData[field.id]}}
        </template>

        <span class="tag" v-if="field.type=='select'">
        {{formData[field.id]}}
        </span>

        <span class="tag" v-if="field.type=='radio'">
        {{formData[field.id]}}
        </span>

        <template v-if="field.type=='checkbox'">
            <span v-for="(item,itemIdx) in formData[field.id]" :key="itemIdx" class="tag" >
                {{item}}
            </span>
        </template>

        <template v-if="field.type=='attachment'">
            <span @click="showAttachment(item)" v-for="(item,itemIdx) in formData[field.id]" :key="itemIdx" class="tag clickable" >
                {{item.name}}
            </span>
        </template>
                       

    </template>
</div>
</template>

<script>
export default {
    props:['field','formData'],
    data(){
        return {
          
        }
    },  
    mounted(){  
    },
    methods:{
        showAttachment(item){
            var canPreivew=app.previewFile(item.id,true);
            if(canPreivew==false){
                app.downloadFile(item.id);
            }
        }
    }
}
</script>