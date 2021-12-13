<style scoped>
.note-tag{
    display: inline-block;
    padding: .25em .6em;
    font-size: 12px;
    font-weight: 600;
    border-radius: 1rem;
    line-height: 1;
    text-align: center;
    white-space: nowrap;
    vertical-align: middle;
    background-color: transparent;
    border:1px solid #666;
    color: #666;
    margin-right:7px;
    margin-top:5px;
    transition: all 0.3s;
}
.note-tag-selected{
    border:1px solid #009cf1;
    color: #009cf1;
}
.delete-tag-btn{
    color:#FF604B;
    margin:0 10px;
    cursor: pointer;
}
.setting-btn{
    cursor: pointer;
}
</style>
<template>
    <span class="note-tag" :class="{'note-tag-selected':selected}">
        <template v-if="!isEditing">{{value.name}}</template>
        <input @keyup.enter="toggleEdit" v-if="isEditing" v-model.trim="value.name"></input>
        <Icon class="delete-tag-btn" v-if="isEditing" @click.native.stop="deleteTag" type="md-trash" />
        <Icon class="setting-btn" v-if="enableEdit" @click.native.stop="toggleEdit" type="ios-settings-outline" />
    </span>
</template>
<script>
export default {
    name:"NoteTag",
    props: ['value','selected','enableEdit'],
    data (){
        return{
            isEditing:false,
        }
    },
    methods:{
        toggleEdit(){
            this.isEditing=!this.isEditing;
            if(this.isEditing==false){
                this.$emit('edit',this.value)
            }
        },
        deleteTag(){
             this.$emit('deleteTag',this.value)
        }
    }
}
</script>