<style lang="less" scoped>
    @media print {
        .noprint {
            display: none
        }
    }
    .pdf-frame {
        position: relative;
        height: 100%;
        width: 100%;
        overflow: hidden;
    }

    .split-line {
        width: 1px;
        height: 12px;
        background-color: #a2a2a2;
        margin-left: 7px;
        margin-right: 7px;
    }

    .preview-office {
        /deep/ .ivu-modal {
            position: relative;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;

            &-content {
                position: relative;
                height: 100%;
                width: 100%;
                overflow: hidden;
            }

            &-header {
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                z-index: 101;
                background-color: #fff;
            }

            &-body {
                position: relative;
                height: 100%;
                width: 100%;
                padding: 0;
                top: 0;
                max-height: none;
                overflow: hidden;
            }
        }

        &-header {
            display: flex;
            align-items: center;
            align-content: center;

            &-title {
                flex: 1;
                overflow: hidden;
                text-overflow: ellipsis;
                display: -webkit-box;
                -webkit-line-clamp: 1;
                -webkit-box-orient: vertical;
                font-weight: bold;
                font-size: 16px;
            }

            &-actions {
                display: flex;
                align-items: center;
                align-content: center;
            }
        }
    }

    .file-box-unsupport {
        flex: 1;
        display: flex;
        align-items: center;
        align-content: center;
        justify-content: center;
        flex-direction: column;

        .el-icon-document {
            font-size: 140px;
            color: #b2b2b2;
        }

        .tips {
            padding-top: 60px;
            font-size: 18px;
        }

        .file-name {
            color: #3d5afe;
            font-weight: bold;
        }
    }
</style>
<i18n>
    {
    "en": {
    "预览":"Preview",
    "下载文件":"Download"
    },
    "zh_CN": {
    "预览":"预览",
    "下载文件":"下载文件"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog" class="preview-office" v-model="showDialog" :closable="false" mask-closable fullscreen footer-hide>
        <div slot="close"></div>
        <div class="preview-office-header " slot="header">
            <div class="preview-office-header-title">
                {{ $t('预览') }}
            </div>
            <div class="preview-office-header-actions">
                <IconButton icon="md-download" @click="onClickVersion" :title="$t('下载文件')"></IconButton>
                <div class="split-line"></div>
                <IconButton icon="md-close" @click="showDialog=false"></IconButton>
            </div>
        </div>
        <iframe v-if="filePath!=null"
                width="100%"
                allowusermedia
                allowfullscreen
                :src="filePath"
                class="pdf-frame noprint"
                frameborder="0"
                @load="onIframeLoad"></iframe>
        <div class="file-box-unsupport" v-else>
            <i class="el-icon-document"></i>
            <div class="tips">此格式文档暂不支持预览</div>
        </div>
    </Modal>
</template>
<script>
    import {Base64} from 'js-base64';

    export default {
        name: 'PreviewOfficeDialog',
        mixins: [componentMixin],
        data() {
            return {
                filePath: null,
            };
        },
        created() {
            window.onPageOfficeOpenWindowModeless = () => {
                console.log('-------->window.onPageOfficeOpenWindowModeless invoke');
                setTimeout(() => {
                    this.$emit('on-close', 'onPageOfficeOpenWindowModeless');
                }, 10000);
            };
            window.onSaveCompeleted = function () {
                console.log("---onSaveCompeleted--")
                app.toast("保存成功");
            }
        },
        beforeDestroy() {
            window.onPageOfficeOpenWindowModeless = null;
        },
        methods: {
            pageLoad() {
                if( !app.officeType){
                    app.officeType =  "wps";
                }

                if (app.officeType === 'wps') {
                    this.filePath = app.serverAddr + '/v1/3rd/preview/?token=' + app.token + '&fileId=' + this.args.uuid;
                } else if (app.officeType === 'pageoffice') {
                    this.filePath = `${app.serverAddr}/p/po/info?fileId=${this.args.uuid}&token=${app.token}`;
                } else if (app.officeType === 'office') {
                    this.filePath = app.owaUrl + "/op/embed.aspx?src=" + app.serverAddr + '/p/file/get_file/' + this.args.uuid + '?token=' + app.token;
                } else if (app.officeType === 'kkfileview') {
                    let previewUrl = `${app.serverAddr}/p/file/get_file/${this.args.uuid}?token=${app.token}`;
                    this.filePath = `${app.owaUrl}?url=${encodeURIComponent(Base64.encode(previewUrl))}`
                }
                console.log(app.officeType,this.filePath)
            },
            onClickVersion() {
                window.open(app.serverAddr + '/p/file/get_file_ex/' + this.args.uuid + '?token=' + app.token);
            },
            onIframeLoad() {
                if (app.officeType!== 'pageoffice') {
                    return false;
                }
            }
        },
    };
</script>
