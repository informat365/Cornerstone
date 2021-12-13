<style >
*{
    box-sizing: border-box;
}
.app-frame{
    display: flex;
    width:100%;
    height:100vh;
    flex-direction: column;
}
.app-top{
    height:48px;
    border-bottom: 1px solid #eee;
    display: flex;
    align-items: center;
}
.app-main{
    flex:1;
    overflow: auto;
    max-height: calc(100vh -48px);
    max-width: 100vw;
}
.scrollbox::-webkit-scrollbar{
    width:6px;
    height:6px;
    background-color:transparent;
    -ms-overflow-style: -ms-autohiding-scrollbar;
}
.scrollbox::-webkit-scrollbar-track{
    border-radius:0;
    background-color:transparent;
}
.scrollbox::-webkit-scrollbar-thumb{
    border-radius:0;
    background-color:#ddd;
}
.top-left{
    width:200px;
    padding:0 20px;
    display: flex;
    align-items: center;
}
.top-center{
    flex:1;
    text-align: center;
    font-size:16px;
    font-weight: bold;
}
.top-right{
    width:200px;
    padding:0 20px;
    display: flex;
    align-items: center;
    flex-direction: row-reverse;
}
.scale-text{
    margin:0 10px;
    font-size:16px;
}
.page-item{
    padding:10px;
    font-size:14px;
}
.side-box{
    position:fixed;
    top:48px;
    left:0;
    width:200px;
    height:calc(100vh - 48px);
    background-color: #fff;
    border-right:1px solid #eee;
}
</style>
<template>
<div class="app-frame">
    <div class="app-top">
        <div class="top-left">
            <Icon @click.native="setScale(-10)" size="20" type="md-remove-circle" />
            <span class="scale-text">{{viewScale}}%</span>
            <Icon  @click.native="setScale(10)" size="20" type="md-add-circle" />
        </div>
        <div class="top-center">{{file.name}}</div>
        <div class="top-right">
            <FileUpload @change="onFileChange"></FileUpload>
        </div>
    </div>
    <div  class="app-main scrollbox">
        <SketchBoard :value="sketchData" :scale="viewScale"></SketchBoard>
    </div>
</div>
</template>
<script>
import Vue from 'vue'
import iView from 'iview';
import 'iview/dist/styles/iview.css';
Vue.use(iView);
//
import FileUpload from './components/FileUpload'
import SketchBoard from './components/SketchBoard'
import sketchapi from './sketchapi'
//
export default {
    components:{
        FileUpload,SketchBoard
    },
    data() {
        return {
            serverAddr:"",
            file:{
                name:null,
                size:0
            },
            sketchData:{

            },
            viewScale:100
        }
    },
    mounted() {
        this.serverAddr = [window.location.protocol, '//', window.location.host].join('');
        var url = new URL(window.location.href);
        var pageArgs={};
        url.searchParams.forEach((k,v)=>{
            pageArgs[v]=k;
        })
        if(pageArgs['id']){
            this.loadRemoteFile(pageArgs['id'])
        }
    },
    methods: {
        onFileChange (file) {
            this.file.name=file.name;
            this.file.size=file.size;
            sketchapi.readfile(file,()=>{
                this.sketchData=sketchapi.data;
            })
        },
        loadRemoteFile(id){
            var fileURL=this.serverAddr+"/p/file/get_file/"+id;
            var xhr = new XMLHttpRequest();
            xhr.open('GET', fileURL, true);
            xhr.responseType = 'blob';
            xhr.onload = (e) =>{
                if (e.loaded == e.total) {
                    var blob = xhr.response;
                    sketchapi.readZipData(blob,()=>{
                        this.sketchData=sketchapi.data;
                    })
                }
            };
            xhr.send();
        },
        setScale(delta){
            this.viewScale+=delta;
        }
    }
};
</script>
