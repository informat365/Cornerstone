<style scoped lang="less">

    .upload-title {
        width: 100%;
        display: inline-block;
        text-align: left;
        padding: 3px;
    }

    .upload-box {
        min-width: 300px;
        width: 100%;
        display: block;
        text-align: left;
        padding: 10px;
    }

    .upload-box::-webkit-scrollbar,
    .upload-box::-webkit-scrollbar {
        width: 0; /* remove scrollbar space */
        background: transparent; /* optional: just make scrollbar invisible */
    }


    .chunk-upload {

        &-btn {
            padding: 20px 0;
            border: 1px dashed #ccc;
            border-radius: 5px;
            text-align: center;
            cursor: pointer;

            &:hover {
                border: 1px dashed #3399ff;
            }
        }

        &-tip {
            display: inline-block;
            font-size: 12px;
            padding: 3px 0;
            color: #b2b2b2;
            margin-top: 5px;
        }

    }

    .upload-chunk-container {
        margin: 5px 10px;
        font-size: 13px;
        color: #b2b2b2;

        label {
            color: #0b77cf;
            cursor: pointer;
            margin: 0 3px;
        }
    }

    #global-uploader {
        position: relative;
        display: block;
        /*z-index: 20;*/
        /*right: 15px;*/
        /*bottom: 15px;*/
        margin: 10px;

        .uploader-app {
            width: 100%;
        }

        .file-panel {
            background-color: #fff;
            /*border: 1px solid #e2e2e2;*/
            /*border-radius: 7px 7px 0 0;*/
            /*min-height: 300px;*/
            /*box-shadow: 0 0 10px rgba(0, 0, 0, .2);*/

            .file-title {
                display: flex;
                height: 40px;
                line-height: 40px;
                padding: 0 15px;
                border-bottom: 1px solid #ddd;

                .operate {
                    flex: 1;
                    text-align: right;
                }
            }

            .file-list {
                position: relative;
                height: auto;
                overflow-x: hidden;
                overflow-y: auto;
                background-color: #fff;

                > li {
                    background-color: #fff;
                }

                /deep/ .uploader-file-name i {
                    display: none !important;
                }

                /deep/ .uploader-file {
                    border-bottom: none !important;
                    height: 36px !important;
                    line-height: 36px !important;
                }

                /deep/ .uploader-file-progress {
                    background-color: #fff !important;
                }
            }

            &.collapse {
                .file-title {
                    background-color: #E7ECF2;
                }
            }
        }

        .no-file {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            font-size: 16px;
        }

        /* /deep/ .uploader-file-icon {
             &:before {
                 content: '' !important;
             }

             &[icon=image] {
                 background: url(./images/image-icon.png);
             }

             &[icon=video] {
                 background: url(./images/video-icon.png);
             }

             &[icon=document] {
                 background: url(./images/text-icon.png);
             }
         }*/

        /deep/ .uploader-file-actions > span {
            margin-right: 6px;
        }
    }

    /* 隐藏上传按钮 */
    #global-uploader-btn {
        position: absolute;
        clip: rect(0, 0, 0, 0);
    }

</style>
<i18n>
    {
    "en": {
    "点击或者将文件拖动到这里上传":"Click or drag the file to upload here",
    "文件格式错误,可选格式为":"File format is incorrect. The optional format is ",
    "最大可上传100M的文件":"Up to 100M files can be uploaded",
    "点击选择将文件上传":"Click here to choose file for upload",
    "上传错误":"Upload error"
    },
    "zh_CN": {
    "点击或者将文件拖动到这里上传":"点击或者将文件拖动到这里上传",
    "点击选择将文件上传":"点击选择将文件上传",
    "文件格式错误,可选格式为":"文件格式错误,可选格式为:",
    "最大可上传100M的文件":"最大可上传100M的文件",
    "上传错误":"上传错误"
    }
    }
</i18n>
<template>
    <div style="min-height: 230px;">
        <div class="upload-title" v-if="showMultiUpload">
            <RadioGroup v-model="viewType">
                <Radio key="normal" label="normal">普通上传</Radio>
                <Radio key="chunk" label="chunk">分片上传</Radio>
            </RadioGroup>
        </div>

        <div class="upload-box" v-if="viewType==='normal'">
            <Upload
                    :format="format"
                    :multiple="multiple"
                    :show-upload-list="true"
                    :on-success="handleUploadSuccess"
                    :max-size="maxSize"
                    :on-error="onError"
                    :on-format-error="handleUploadFormatError"
                    :on-exceeded-size="handleUploadMaxSize"
                    :action="uploadServerAddr"
                    type="drag"
            >
                <div style="padding: 20px 0">
                    <Icon type="ios-cloud-upload" size="52" style="color: #3399ff"></Icon>
                    <p>{{$t('点击或者将文件拖动到这里上传')}}</p>
                </div>
            </Upload>
        </div>


        <div class="upload-box" v-if="viewType==='chunk'">
            <div class="chunk-upload-btn" @click="clickChunkBtn">
                <Icon type="ios-cloud-upload" size="52" style="color: #3399ff"></Icon>
                <p>{{$t('点击选择将文件上传')}}</p>
            </div>
            <span class="chunk-upload-tip">大文件(>100MB)上传时，建议使用分片上传功能</span>

            <div id="global-uploader">

                <!-- 上传 -->
                <uploader
                        ref="uploader"
                        :options="options"
                        :autoStart="false"
                        @file-added="onFileAdded"
                        @file-success="onFileSuccess"
                        @file-progress="onFileProgress"
                        @file-error="onFileError"
                        class="uploader-app">
                    <uploader-unsupport></uploader-unsupport>

                    <uploader-btn class="chunk-select-btn" id="global-uploader-btn" ref="uploadBtn">选择文件</uploader-btn>

                    <uploader-list v-show="panelShow">
                        <div class="file-panel" slot-scope="props" :class="{'collapse': collapse}">
                            <!--                            <div class="file-title">-->
                            <!--                                <h2>文件列表</h2>-->
                            <!--                                <div class="operate">-->
                            <!--                                    <Button @click="fileListShow" type="text" :title="collapse ? '展开':'折叠' ">-->
                            <!--                                        <i class="iconfont" :class="collapse ? 'inuc-fullscreen': 'inuc-minus-round'"></i>-->
                            <!--                                    </Button>-->
                            <!--                                    <Button @click="close" type="text" title="关闭">-->
                            <!--                                        <Icon type="ios-close" size="20"></Icon>-->
                            <!--                                    </Button>-->
                            <!--                                </div>-->
                            <!--                            </div>-->

                            <ul class="file-list" v-show="showFileList">
                                <li v-for="file in props.fileList" :key="file.id">
                                    <uploader-file :class="'file_' + file.id" ref="files" :file="file" :list="true"></uploader-file>
                                </li>
                                <!--                                <div class="no-file" v-if="!props.fileList.length"><i class="iconfont icon-empty-file"></i> 暂无待上传文件</div>-->
                            </ul>
                        </div>
                    </uploader-list>

                </uploader>

            </div>
        </div>
    </div>
</template>
<script>
    import SparkMD5 from 'spark-md5';

    export default {
        name: "FileUploadView",
        props: ['multiple', 'format', 'tempfile', 'object'],
        data() {
            return {
                uploadServerAddr: app.serverAddr + '/p/file/upload_file?token=' + app.token,
                //kb
                maxSize: 1024 * (app.uploadFileSize || 100),
                chunkSize: '20480000',
                chunkable: true,
                options: {
                    target: app.serverAddr + '/p/file/upload_chunk_file?token=' + app.token,
                    chunkSize: 20480000,
                    fileParameterName: 'file',
                    maxChunkRetries: 3,
                    testChunks: true,   //是否开启服务器分片校验
                    // 服务器分片校验函数，秒传及断点续传基础
                    checkChunkUploadedByResponse: function (chunk, message) {
                        console.log("checkChunkUploadedByResponse", chunk, message)
                        let objMessage = JSON.parse(message);
                        if (objMessage.skipUpload) {
                            return true;
                        }
                        return (objMessage.uploaded || []).indexOf(chunk.offset + 1) >= 0
                    },
                    headers: {
                        token: app.token
                    },
                    query() {
                    }
                },
                panelShow: false,   //选择文件后，展示上传panel
                collapse: false,
                showFileList: true,
                viewType: 'normal',
                showMultiUpload: app.showMultiUpload

            }
        },
        mounted() {
            if (this.tempfile == true) {
                this.uploadServerAddr = app.serverAddr + '/p/file/upload_temp_file?token=' + app.token;
            }
        },
        computed: {
            //Uploader实例
            uploader() {
                return this.$refs.uploader.uploader;
            }
        },
        methods: {
            handleUploadMaxSize: function () {
                app.toast(this.$t("最大可上传" + (app.uploadFileSize || 100) + "M的文件"));
            },
            handleUploadSuccess: function (obj,file,fileList) {
                // console.log(obj,fileList)
                if (obj && obj.hasOwnProperty("size") && obj.size <= 0) {
                    app.toast("请勿上传空文件");
                    fileList.pop();
                    return;
                }
                var uuid = {
                    id: obj.attachment.id,
                    uuid: obj.attachment.uuid,
                    name: obj.attachment.name
                }
                if (this.object) {
                    this.$emit('change', {
                        object: this.object,
                        uuid: uuid
                    })
                } else {
                    this.$emit('change', uuid)
                }

            },
            handleUploadFormatError: function () {
                app.toast(this.$t('文件格式错误,可选格式为') + this.format)
            },
            onError: function (e) {
                console.error("error---->", e)
                app.toast(this.$t('上传错误'))
            },
            deleteObject: function (idx) {
                this.uuid.splice(idx, 1);
            },
            //---------------------分片上传---------------------------
            clickChunkBtn() {
                this.$el.getElementsByClassName("chunk-select-btn")[0].click();
                // this.$refs.uploadBtn.click();
            },
            onFileAdded(file) {
                this.panelShow = true;
                this.computeMD5(file);
                this.$emit('chunk-fileAdded');
            },
            onFileProgress(rootFile, file, chunk) {
                console.log(`上传中 ${file.name}，chunk：${chunk.startByte / 1024 / 1024} ~ ${chunk.endByte / 1024 / 1024}`)
            },
            onFileSuccess(rootFile, file, response, chunk) {
                let res = JSON.parse(response);
                // if (!res.success) {
                //     app.toast("上传失败");
                //     return;
                // }
                // 如果服务端返回需要合并
                if (res.needMerge) {
                    // 文件状态设为“合并中”
                    this.statusSet(file.id, 'merging');
                    ajaxInvoke(app.serverAddr, `/p/file/merge_chunk_file?token=${app.token}&filename=${rootFile.name}&identifier=${rootFile.uniqueIdentifier}&totalChunks=${rootFile.chunks.length}&chunkNumber=${rootFile.chunks.length}&totalSize=${rootFile.size}`, [app.token], res => {
                        // 文件合并成功
                        this.$emit('chunk-fileSuccess');
                        this.statusRemove(file.id);
                        let uuid = res.attachment;
                        if (!uuid) {
                            app.toast("上传失败");
                        } else {
                            app.toast("上传完成");
                        }
                        if (this.object) {
                            this.$emit('change', {
                                object: this.object,
                                uuid: uuid
                            })
                        } else {
                            this.$emit('change', uuid)
                        }
                    });
                } else {
                    // // 不需要合并
                    if (file.completed) {
                        this.$emit('chunk-fileSuccess');
                        let uuid = res.attachment;
                        if (!uuid) {
                            app.toast("上传失败");
                        } else {
                            // app.toast("上传完成");
                        }
                        if (this.object) {
                            this.$emit('change', {
                                object: this.object,
                                uuid: uuid
                            })
                        } else {
                            this.$emit('change', uuid)
                        }
                    }
                }
            },
            onFileError(rootFile, file, response, chunk) {
                console.log("onFileError--->", file, response, chunk)
                app.toast(JSON.parse(response).message);
            },
            /**
             * 计算md5，实现断点续传及秒传
             * @param file
             */
            computeMD5(file) {
                let fileReader = new FileReader();
                let time = new Date().getTime();
                let blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice;
                let currentChunk = 0;
                const chunkSize = this.chunkSize;
                let fileSize = file.size;
                if (fileSize <= 0) {
                    app.toast("请勿上传空文件");
                    file.cancel();
                    return;
                }
                let chunks = Math.ceil(file.size / chunkSize);
                let spark = new SparkMD5.ArrayBuffer();
                // 文件状态设为"计算MD5"
                this.statusSet(file.id, 'md5');
                file.pause();
                loadNext();
                fileReader.onload = (e => {
                    spark.append(e.target.result);
                    if (currentChunk < chunks) {
                        currentChunk++;
                        loadNext();
                        // 实时展示MD5的计算进度
                        this.$nextTick(() => {
                            var rate = ((currentChunk / chunks) * 100).toFixed(0) + '%';
                            this.statusSet(file.id, 'md5', rate);
                        })
                    } else {
                        let md5 = spark.end();
                        this.computeMD5Success(md5, file);
                        console.log(`MD5计算完毕：${file.name} \nMD5：${md5} \n分片：${chunks} 大小:${file.size} 用时：${new Date().getTime() - time} ms`);
                    }
                });
                fileReader.onerror = function () {
                    app.toast(`文件${file.name}读取出错，请检查该文件`)
                    file.cancel();
                };

                function loadNext() {
                    let start = currentChunk * chunkSize;
                    let end = ((start + chunkSize) >= file.size) ? file.size : start + chunkSize;
                    fileReader.readAsArrayBuffer(blobSlice.call(file.file, start, end));
                }
            },
            computeMD5Success(md5, file) {
                if (file.size > (app.uploadFileSize || 100) * 1024 * 1024) {
                    app.toast("文件超出上传限制");
                    file.cancel();
                    return false;
                }
                // 将自定义参数直接加载uploader实例的opts上
                // Object.assign(this.uploader.opts, {
                //     query: {
                //         ...this.params,
                //     }
                // })
                file.uniqueIdentifier = md5;
                file.resume();
                this.statusRemove(file.id);
            },
            close() {
                this.uploader.cancel();
                this.panelShow = false;
            },
            /**
             * 新增的自定义的状态: 'md5'、'transcoding'、'failed'
             * @param id
             * @param status
             */
            statusSet(id, status, rate) {
                let statusMap = {
                    md5: {
                        text: 'md5校验中' + (rate || ''),
                        bgc: '#fff'
                    },
                    merging: {
                        text: '分片合并中',
                        bgc: '#e2eeff'
                    },
                    transcoding: {
                        text: '转码中',
                        bgc: '#e2eeff'
                    },
                    failed: {
                        text: '上传失败',
                        bgc: '#e2eeff'
                    }
                }
                this.$nextTick(() => {
                    $(`<p class="myStatus_${id}"></p>`).appendTo(`.file_${id} .uploader-file-status`).css({
                        'position': 'absolute',
                        'top': '0',
                        'left': '0',
                        'right': '0',
                        'bottom': '0',
                        'zIndex': '1',
                        'backgroundColor': statusMap[status].bgc
                    }).text(statusMap[status].text);
                })
            },
            statusRemove(id) {
                this.$nextTick(() => {
                    $(`.myStatus_${id}`).remove();
                })
            },
        }
    }
</script>
