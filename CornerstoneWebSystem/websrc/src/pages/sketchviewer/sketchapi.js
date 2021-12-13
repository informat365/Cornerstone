import JSZip from 'jszip'

export default {
    data:{
        pages:[],
        document:null,
        imageData:null,
        image:null,
        imageWidth:null,
        imageHeight:null
    },
    readfile(file,callback){
        this.data.pages=[];
        var reader = new FileReader()
        reader.onload=(e)=>{
            var data=e.target.result;
            this.readZipData(data,callback)
        }
        reader.readAsArrayBuffer(file)
    },
    readZipData(data,callback){
        JSZip.loadAsync(data).then( (zip)=>{
            zip.forEach((relativePath, zipEntry)=>{
              if (relativePath === 'previews/preview.png') {
                zipEntry.async('base64').then((content)=>{
                    var imageData = 'data:image/png;base64,' + content
                    this.data.imageData=imageData;
                    var image = new Image()
                    image.onload = ()=>{
                        this.data.imageWidth = image.width
                        this.data.imageHeight = image.height
                    }
                    image.src = imageData
                    this.data.image=image;
                    if(callback){
                        callback();
                    }
                },
                (e)=>{
                  console.log(e)
                })
              } else if (relativePath.startsWith('pages/')) {
                    zipEntry.async('string').then((content)=>{
                        var page = JSON.parse(content)
                        this.data.pages.push(page);
                    },
                    (e)=>{
                        console.log(e)
                    })
              }else if (relativePath.startsWith('document.json')) {
                    zipEntry.async('string').then((content)=>{
                        var document = JSON.parse(content)
                        this.data.document=document;
                    },
                    (e)=>{
                        console.log(e)
                    })
              }
            })
          }, (e)=> {
            console.log(e)
        })
    }
}