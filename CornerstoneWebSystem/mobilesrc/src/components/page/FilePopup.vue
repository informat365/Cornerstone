<style scoped>
    .file-popup{
        z-index:501;
        position: fixed;
        top:0;
        left:0;
        width:100vw;
        height: 100vh;
        overflow: auto;
        background-color: #fff;
        -webkit-overflow-scrolling:touch
    }
    .nodata{
        text-align: center;
        font-size:20px;
        font-weight: bold;
        color:#999;
        margin-top:100px;
    }
    .pdf-frame{
        width:100%;
        height: 100vh;
    }
    .close-btn{
        position: fixed;
        top:10px;
        right:10px;
    }
    .file-image{
        width:100%;
        height:auto;
    }
</style>
<template>
 <div class="file-popup">
     <div class="nodata" v-if="fileType==null">
         不支持预览
     </div>
      <icon @click.native="closePopup" class="close-btn" type="clear"></icon>
    <img class="file-image" :src="filePath" v-if="fileType=='image'">

    <iframe v-if="fileType=='office'" :src="filePath" class="pdf-frame" frameborder="0"></iframe>
    <iframe v-if="fileType=='pdf'" scrolling="auto"  :src="filePath" class="pdf-frame" frameborder="0"></iframe>
 </div>
</template>

<script>
import {Icon} from 'vux'
export default {
    components: {Icon},
    props:['uuid'],
    data () {
        return {
           showPopup:true,
           filePath:null,
           fileType:null,
        }
    },
    watch:{
        uuid(val){
           this.loadData();
        },
    },
    mounted(){
       this.loadData();
    },
    methods:{
        loadData(){
            var uuid=this.uuid;
            this.filePath=app.serverAddr+'/p/file/get_file_ex/'+uuid+"?token="+app.token;
            var name=uuid.toLowerCase();
            if(name.indexOf('.png')!=-1||
                name.indexOf('.jpg')!=-1||
                name.indexOf('.jpeg')!=-1||
                name.indexOf('.svg')!=-1||
                name.indexOf('.gif')!=-1){
                this.fileType="image"
                return
            }
              if(name.indexOf('.pdf')!=-1){
                this.filePath="./static/pdfjs/web/viewer.html?file="+encodeURIComponent(this.filePath);
                this.fileType="pdf"
                return
            }
            //"&inline=true
            if(
			name.indexOf('.ppt')!=-1||
			name.indexOf('.pptx')!=-1||
			name.indexOf('.pps')!=-1||
			name.indexOf('.ppsx')!=-1||
			name.indexOf('.pptm')!=-1||
			name.indexOf('.ppam')!=-1||
			name.indexOf('.potx')!=-1||
			name.indexOf('.ppsm')!=-1||
			
			name.indexOf('.doc')!=-1||
			name.indexOf('.docx')!=-1||
			name.indexOf('.dotm')!=-1||
			name.indexOf('.dotx')!=-1||
			
			name.indexOf('.xls')!=-1||
			name.indexOf('.xlsx')!=-1||
			name.indexOf('.xlsb')!=-1||
			name.indexOf('.xlsm')!=-1){
                this.filePath="https://view.officeapps.live.com/op/embed.aspx?src="+encodeURIComponent(this.filePath);
                this.fileType="office";
               return;
            }

        },
        closePopup(){
            this.$emit('close')
        }
    }
}
</script>


