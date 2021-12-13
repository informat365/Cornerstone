<style scoped>
.editor{
    position:absolute;
    top:0;
    left:0;
    width:100%;
    height: 100%;
}
.editor-wrap{
    position: relative;    
    height: 300px;
    border:1px solid #ccc;
}
</style>
<template>
<div class="editor-wrap">
    <div class="editor"></div>
</div>
</template>
<script>
    export default {
        name:"MonacoEditor",
        props:['value','placeholder','readonly','mode'],
        data (){
            return{
            }
        },
        mounted(){
            setTimeout(()=>{
                 this.$nextTick(this.setupEditor)
            },200)
        },
        beforeDestroy(){

        },
        watch:{
            value(val){
                if(this.editor){
                    if(val==null){
                        this.editor.getModel().setValue("");
                    }else{
                        this.editor.getModel().setValue(val);
                    }
                }
            },
            mode(val){
                if(this.editor){
                    var model = this.editor.getModel(); 
                    monaco.editor.setModelLanguage(model, val)
                }
            }
        },
        methods: {
            setupEditor(){
                var t=this.$el.getElementsByClassName('editor')[0];
                monaco.languages.typescript.javascriptDefaults.setCompilerOptions({ noLib: true, allowNonTsExtensions: true });
                var mode="plaintext";
                if(this.mode!=null){
                    mode=this.mode;
                }
                this.editor = monaco.editor.create(t, {
                    value: this.value,
                    language:mode
                });
            },
            resize(){
                this.editor.layout();
            },
            setValue(val){
                this.editor.getModel().setValue(val);
            },
            getValue(){
                return this.editor.getModel().getValue();
            }
        }
    }
</script>