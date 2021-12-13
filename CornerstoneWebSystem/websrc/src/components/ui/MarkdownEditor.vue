<style scoped>
.editor{
    width:100%;
    height: 100%;
}
</style>
<template>
<div>
    <textarea  class="editor"></textarea>
</div>
</template>
<script>
export default {
        name:"MarkdownEditor",
        props:['value','placeholder','readonly'],
        data (){
            return{
                editerValue:this.value,
            }
        },
        mounted(){
            this.$nextTick(()=>{
                var t=this.$el.getElementsByClassName('editor')[0];
                var editor=new Editor({
                    element: t
                })
                editor.render();
                this.editor=editor;
            })
        },
        methods: {
            getValue(){
                return this.editor.codemirror.getValue();
            },
            setValue(value){
                this.editor.codemirror.setValue(value);
            }
        }
    }
</script>