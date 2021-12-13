<template>
<div>
    <textarea ref="editorBox" v-if="disabled!=true"></textarea>
    <RichtextLabel v-if="disabled==true" :value="value" placeholder="未设置"/>
</div>

</template>
<script>
export default {
    name:"RichtextEditor",
    props: ['value','mention','cleanNode','disabled'],
    data: function () {
        return {
            content:this.value
        }
    },
    mounted:function(){
    	if(this.disabled!=true){
            this.setupEditor();
        }
    },
    beforeDestroy(){
        if(this.editor){
            this.editor.destroy();
        }
    },
    watch:{
        value: function (val) {
            this.content = val;
            this.$emit("on-change")
            if(this.editor.getValue()!=val){
                this.editor.setValue(val);
            }
        }
    },
    methods:{
        setupEditor(){
            var tabBarFloat=false;
            var toolbar = ['title', 'bold', 'italic', 'underline', 'strikethrough', 'fontScale', 'color','mark','checklist',
            '|', 'ol', 'ul', 'blockquote', 'code', 'table',
            '|', 'link', 'image', 'hr',
            '|', 'indent', 'outdent', 'alignment'];
            var editorConfig={
                textarea: this.$refs.editorBox,
                toolbar:toolbar,
                upload:true,
                toolbarFloat:tabBarFloat,
                pasteImage: true,
                upload:{
                    url: app.serverAddr+'/p/file/upload?token='+app.token,
                    fileKey: 'file',
                    connectionCount: 3
                }
            }
            if(this.mention){
                editorConfig.mention={items:this.mention};
            }
            var editor = new Simditor(editorConfig);
            if(this.cleanNode){
                editor.cleanNodeFormat=true;
            }
            editor.on('blur', (e, src)=>{
                this.content=this.editor.getValue();
                this.$emit('input', this.content);
            })
            //
            this.editor=editor;
            if(this.value){
                this.editor.setValue(this.value);
            }
        },
        isUploading:function(){
            if(this.editor){
                return this.editor.uploader.uploading;
            }
            return false;
        },
        focus(){
            if(this.editor){
                this.editor.focus();
            }
        },
        blur(){
            if(this.editor){
                this.editor.blur();
            }
        },
        getValue(){
            if(this.editor==null){
                return this.content;
            }else{
                return this.editor.getValue();
            }
        },
        setValue(v){
            if(this.editor){
                this.editor.setValue(v);
            }
        },
        sync(){
            this.content=this.editor.getValue();
            this.$emit('input', this.content);
        }
    }
}
</script>
