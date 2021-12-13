<style scoped>
.form-image-4{
    height:120px;
    height:120px;
    margin-right:10px;
}
.form-image-5{
  height:80px;
  height:80px;
  margin-right:10px;
}
.form-image-6{
  height:40px;
  width:40px;
  margin-right:10px;
}
</style>
<template>
  <img @click="previewImage" :src="value|imgurl" :class="'form-image-'+size"/>
</template>
<script>
export default {
   name:"ServerImage",
    props: {
      'value':String,
      'size':{
        type:[String,Number],
        default:4,
      }
    },
    methods:{
      previewImage(){
        if(this.value==null||this.value==""){
          return;
        }
        app.showDialog(window["PreviewImageDialog"],{
          uuid:this.value
        })
      }
    }
}
</script>