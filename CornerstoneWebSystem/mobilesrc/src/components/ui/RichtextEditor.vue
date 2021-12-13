<template>
    <textarea></textarea>
</template>
<script>
export default {
    name:"RichtextEditor",
    props: ['value','mention'],
    data: function () {
        return {
            content:this.value
        }
    },
    mounted:function(){
    	var tabBarFloat=false;
        var toolbar = ['title', 'bold', 'italic', 'underline', 'strikethrough', 'fontScale', 'color'];
        var editorConfig={
	          textarea: this.$el,
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
             editorConfig.mention={items:app.mentionList};
        }
        var editor = new Simditor(editorConfig);
        this.editor=editor;
    },
    watch:{
    	content: function (val) {
            //this.$emit('input', val);
        },
        value: function (val) {
            this.content = val;
            if(this.editor.getValue()!=val){
                this.editor.setValue(val);    	
            }
        }
    },
    methods:{
        isUploading:function(){
        	return this.editor.uploader.uploading;
        },
        focus(){
            this.editor.focus();
        },
        blur(){
            this.editor.blur();
        },
        getValue(){
           return this.editor.getValue();
        },
        setValue(v){
            this.editor.setValue(v);
        },
        sync(){
            this.content=this.editor.getValue();
            this.$emit('input', this.content);
        }
    }
}
</script>