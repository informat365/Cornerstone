<style >
.file-upload-btn{
    font-size:14px;
    cursor: pointer;
    padding:0 5px;
    position: relative;
}
.file-upload-btn input {
    position: absolute;
    cursor: pointer;
    top: 0px;
    left: 0;
    width: 100%;
    height: 100%;
    opacity: 0;
}
</style>
<template>
<div class="file-upload-btn">
    <span>上传</span>
    <input type="file" @change="onFileChange">
</div>
</template>
<script>
//
export default {
    data() {
        return {

        }
    },
    mounted() {
        
    },
    methods: {
        onFileChange (e) {
            var files = e.target.files || e.dataTransfer.files
            if (!files.length){
                return
            }
            var file = files[0]
            this.$emit('change',file)
        }
    }
};
</script>