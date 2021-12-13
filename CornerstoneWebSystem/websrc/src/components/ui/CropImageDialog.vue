<style scoped>
    .upload-btn{
        display: inline-block;
        margin-bottom: 0;
        font-weight: 400;
        text-align: center;
        vertical-align: middle;
        touch-action: manipulation;
        cursor: pointer;
        background-image: none;
        border: 1px solid transparent;
        white-space: nowrap;
        line-height: 1.5;
        -webkit-user-select: none;
        -moz-user-select: none;
        -ms-user-select: none;
        user-select: none;
        padding: 5px 15px 6px;
        font-size: 12px;
        border-radius: 4px;
        transition: color .2s linear,background-color .2s linear,border .2s linear,box-shadow .2s linear;
        color: #515a6e;
        background-color: #fff;
        border-color: #dcdee2;
        padding: 8px 10px;
        cursor: pointer;
        margin-right: 10px;
    }
</style>
<i18n>
{
    "en": {
        "上传图片":"Upload image",
        "保存":"OK",
        "图片类型必":"accept image types: gif,jpeg,jpg,png,bmp "
    },
    "zh_CN": {
        "上传图片":"上传图片",
        "保存":"保存",
        "图片类型必":"图片类型必须是.gif,jpeg,jpg,png,bmp中的一种"
    }
}
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true"
         :mask-closable="false" 
         :loading="false" 
         :title="$t('上传图片')" width="700" >
        
        <div  style="height:400px">
            <vue-cropper ref="cropper" 
                :img="option.img" 
                :output-size="option.size" 
                :output-type="option.outputType" 
                :info="true" 
                :full="option.full"
                :can-move="option.canMove" 
                :can-move-box="option.canMoveBox" 
                :fixed-box="option.fixedBox" 
                :original="option.original"
                :auto-crop="option.autoCrop" 
                :auto-crop-width="option.autoCropWidth"
                :auto-crop-height="option.autoCropHeight" 
                :center-box="option.centerBox"
                :high="option.high"></vue-cropper>
        </div>
         
         <div slot="footer">
            <Row>
                <Col span="24" style="text-align:right"> 

                    <label class="upload-btn" for="uploads">{{$t('上传图片')}}</label>
                    <input type="file" id="uploads" style="position:absolute; clip:rect(0 0 0 0);" 
                        accept="image/png, image/jpeg, image/gif, image/jpg"
                        @change="uploadImg($event)">


                    <Button :disabled="option.img==''" @click="confirm" type="default" size="large" >保存</Button>
                </Col>
            </Row>   
        </div>
    </Modal>
</template>
<script>
    import { VueCropper }  from 'vue-cropper'
    export default {
        name:"CropImageDialog",
        mixins: [componentMixin],
        components: {
           VueCropper,
        },
        data () {
            return {
               option: {
                    img: "",
                    size: 1,
                    full: false,
                    outputType: 'png',
                    canMove: true,
                    fixedBox: true,
                    original: false,
                    canMoveBox: true,
                    autoCrop: true,
                    autoCropWidth: 230,
                    autoCropHeight: 96,
                    centerBox: false,
                    high: true
                },
            }
        },
        methods:{
            pageLoad(){
                if(this.args.url){
                    this.option.img=this.args.url;
                }
            },
            uploadImg(e) {
                var file = e.target.files[0]
                if (!/\.(gif|jpg|jpeg|png|bmp|GIF|JPG|PNG)$/.test(e.target.value)) {
                    app.toast(this.$t('图片类型必'))
                    return false
                }
                var reader = new FileReader()
                reader.onload = (e) => {
                    let data
                    if (typeof e.target.result === 'object') {
                         data = window.URL.createObjectURL(new Blob([e.target.result]))
                    } else {
                        data = e.target.result
                    }
                    this.option.img = data
                }
                reader.readAsArrayBuffer(file)
            },
            confirm(){
                this.$refs.cropper.getCropBlob((data) => {
                    this.showDialog=false
                    if(this.args.callback){
                        this.args.callback(data)
                    }
                });
            }
        }
    }
</script>