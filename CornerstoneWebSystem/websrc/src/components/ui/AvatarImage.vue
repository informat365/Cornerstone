<style scoped>
    .avatar-image{
        width:30px;
        height:30px;
        border-radius: 50%;
        background-color: #3F4351;
        display: inline-flex;
        color: #fff;
        font-size:14px;
        text-align: center;
        vertical-align: middle;
        justify-content: center;
        align-items: center;
        color: #fff;
        line-height: 30px;
        border:1px solid #eee;
        font-weight: bold;
    }
    .avatar-image-img{
        background-color: #ccc;
    }
    .avatar-image-small{
        width:20px;
        height:20px;
        line-height: 18px;
        font-size:12px;
    }
    .avatar-image-large{
        width:40px;
        height:40px;
        line-height: 40px;
        font-size:20px;
    }

    .avatar-image-verylarge{
        width:60px;
        height:60px;
        line-height: 60px;
        font-size:20px;
    }

    .avatar-span{
        display: inline;
        line-height: 1;
        font-size:13px;
    }
    .name-span{
        vertical-align: middle;
        display: inline-block;
        max-width: 140px;
        padding-left:4px;
    }
</style>
<template>
   <span class="avatar-span">
       <template v-if="type=='tips'">
            <Tooltip  :content="name==null?placeholder:name">
                <div v-if="value!=null||name==null" :class="'avatar-image-'+size" :style="bgImageStyle" class="avatar-image avatar-image-img"/>
                <span v-if="value==null&&name!=null" class="avatar-image"
                :style="{backgroundColor:bgColor}"
                :class="'avatar-image-'+size">{{firstLetter}}</span>
            </Tooltip>
       </template>
        <template v-if="type=='none'">
            <div v-if="value!=null||name==null" :class="'avatar-image-'+size" :style="bgImageStyle" class="avatar-image avatar-image-img"/>
            <span v-if="value==null&&name!=null"
            :style="{backgroundColor:bgColor}"
            class="avatar-image" :class="'avatar-image-'+size">{{firstLetter}}</span>
       </template>
       <template v-if="type=='label'">
           <template  v-if="value!=null||name==null">
                <div :class="'avatar-image-'+size"  :style="bgImageStyle" class="avatar-image avatar-image-img"/>
            </template>
            <span v-if="value==null&&name!=null"  class="avatar-image"
            :style="{backgroundColor:bgColor}"
            :class="'avatar-image-'+size">{{firstLetter}}</span>
            <span class="name-span text-no-wrap" v-if="type=='label'">
                   <template :title="name" v-if="name"> {{name}}</template>
                   <template v-if="name==null">{{placeholder}}</template>
            </span>
       </template>
        <template v-if="type==null">
            <div :class="'avatar-image-'+size"  :style="bgImageStyle" class="avatar-image avatar-image-img"/>
        </template>
   </span>
</template>
<script>
export default {
    name:"AvatarImage",
    props: ['value','name',"size","type","placeholder"],
    data (){
        this.colorMap={
        'A':'#fff1ac',
        'B':'#f9bcdd',
        'C':'#08ffc8',
        'D':'#584ED0',
        'E':'#204969',
        'F':'#515bd4',
        'G':'#8134af',
        'H':'#dd2a7b',
        'I':'#feda77',
        'G':'#001871',
        'K':'#ff585d',
        'L':'#ffb549',
        'M':'#41b6e6',
        'N':'#05445c',
        'O':'#f2317f',
        'P':'#5c4f74',
        'Q':'#252A34',
        'R':'#FF2E63',
        'S':'#EC7700',
        'T':'#2BBBD8',
        'U':'#1F6ED4',
        'V':'#00a03e',
        'W':'#0087cb',
        'X':'#D74B4B',
        'Y':'#A3CD39',
        'Z':'#235789',
        '=':'#79bd9a',
        '0':'#C73B0B',
        '1':'#6F684E',
        '2':'#283654',
        '3':'#F37C3D',
        '4':'#47b8e0',
        '5':'#47b8e0',
        '6':'#3A405A',
        '7':'#03359D',
        '8':'#207ba1',
        '9':'#58355E',
        '0':'#558ad8',
        };
        return{
            bgImageStyle:{
                backgroundSize:"cover",
                backgroundImage: "url('/image/common/account-placeholder.png')"
            },
        }
    },
    mounted(){
        this.setupValue();
    },
    watch:{
        value(val){
            this.setupValue();
        }
    },
    methods:{
        setupValue(){
            if(this.value){
                this.bgImageStyle={
                    backgroundSize:"cover",
                    backgroundImage: "url('"+app.serverAddr+"/p/file/get_file/"+this.value+"')"
                }
            }
        }
    },
    computed:{
        bgColor(){
            var code=btoa(encodeURIComponent(this.name));
            var colorCode= code.substring(code.length-1,code.length);
            return this.colorMap[colorCode.toUpperCase()];
        },
        firstLetter(){
            var t=this.name;
            if(t!=null){
                return t.substring(0,1);
            }
            return "";
        }
    }
}
</script>
