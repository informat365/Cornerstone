<style scoped>
    .avatar-image{
        width:30px;
        height:30px;
        border-radius: 50%;
        vertical-align: middle;
        background-color: #ccc;
        display: inline-block;
        color: #fff;
        font-size:14px;
        text-align: center;
        vertical-align: middle;
        line-height: 30px;
    }
    .avatar-image-small{
        width:20px;
        height:20px;
        line-height: 20px;
        font-size:12px;
    }
    .avatar-image-large{
        width:50px;
        height:50px;
        line-height: 50px;
        font-size:20px;
    }
    .avatar-span{
        display: inline;
        line-height: 1;
    }
    .name-span{
        vertical-align: middle;
        display: inline-block;
        max-width: 140px;
        padding-left:2px;
        font-size:13px;
    }
</style>
<template>
   <span class="avatar-span">
       <template v-if="type=='tips'">
            <img v-if="value!=null||name==null" :class="'avatar-image-'+size" :src="imageURL" class="avatar-image"/>
            <span v-if="value==null&&name!=null" class="avatar-image" :class="'avatar-image-'+size">{{firstLetter}}</span>
       </template>
       <template v-if="type=='label'">
           <template  v-if="value!=null||name==null">
                <img :class="'avatar-image-'+size"  :src="imageURL" class="avatar-image"/>
            </template>
            <span v-if="value==null&&name!=null"  class="avatar-image" :class="'avatar-image-'+size">{{firstLetter}}</span>
            <span class="name-span text-no-wrap" v-if="type=='label'">  
                   <template v-if="name"> {{name}}</template>
                   <template v-if="name==null">{{placeholder}}</template>
            </span>
       </template>
        <template v-if="type==null">
            <img :class="'avatar-image-'+size"  src="../../assets/image/account-placeholder.png" class="avatar-image"/>
        </template>
   </span>
</template>
<script>
export default {
    name:"AvatarImage",
    props: ['value','name',"size","type","placeholder"],
    data (){
        return{
            
        }
    },
    computed:{
        imageURL:function(){
            if(this.value==null){
                return "./static/image/account-placeholder.png";
            }
            return app.serverAddr+"/p/file/get_file/"+this.value+"?token="+app.token;
        },
        firstLetter:function(){
            var t=this.name;
            if(t!=null){
                return t.substring(0,1);
            }
            return "";
        }
    }
}
</script>