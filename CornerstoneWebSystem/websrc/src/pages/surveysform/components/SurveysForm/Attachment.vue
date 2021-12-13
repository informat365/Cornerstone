<template>
  <div
    v-if="field"
    class="survey-form-static-attachment ">
    <div class="survey-form-static-attachment-wrap">
      <div
        class="survey-form-static-attachment-wrap-item"
        :key="item.field"
        v-for="(item,index) in fileList">
        <div class="uploader-block">
          <Icon name="description" size="20" />
          <div class="text">{{ item.name }}</div>
          <div class="mask" v-if="item.status === 'uploading'">
            <Loading type="spinner" color="#fafafa" />
          </div>
          <template v-else>
            <template v-if="disabled !== true">
              <Icon
                class="remove"
                name="clear"
                size="20"
                @click.native="onClickDelete(index)" />
            </template>
          </template>
        </div>
      </div>
      <template v-if="fileList.length < fileCount && disabled !== true">
        <Uploader
          accept="*"
          result-type="file"
          :max-size="1024*1024*100"
          :after-read="onFileUpload">
          <div class="uploader-block">
            <Icon name="plus" size="20" />
            <div class="text">上传文件</div>
          </div>
        </Uploader>
      </template>
    </div>
  </div>
</template>

<script>
  import { Button, Icon, Loading, Uploader } from 'vant';
  import upload from './upload';

  export default {
    name: 'Attachment',
    components: { Button, Loading, Icon, Uploader },
    props: {
      defineUuid: {
        type: String,
      },
      field: {
        type: Object,
      },
      disabled: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        uploadUrl: null,
        fileList: [],
      };
    },
    watch: {
      fileList(val) {
        if (Array.isEmpty(val)) {
          this.field.value = [];
          return;
        }
        this.field.value = [
          ...val.filter(_ => _.status !== 'uploading'),
        ];
      },
    },
    computed: {
      fileCount() {
        if (!this.field) {
          return 1;
        }
        if (this.field.countType === 'multiple') {
          return 5;
        }
        return 1;
      },
    },
    methods: {
      pageCreated() {
        this.uploadUrl = this.serverAddr + '/p/file/upload_surveys_file?token=' + this.token + '&surveysDefineUuid=' + this.defineUuid;
        this.uploadId = 1;
        if (this.field && Array.isArray(this.field.value)) {
          this.fileList = [
            ...this.field.value,
          ];
        }
      },
      onFileUpload(dataFile) {
        const file = dataFile.file;
        const id = this.uploadId++;
        this.fileList.push({
          id: id,
          name: file.name,
          status: 'uploading',
        });
        upload({
          withCredentials: true,
          file: file,
          filename: 'file',
          action: this.uploadUrl,
          onProgress: () => {
          },
          onSuccess: (res) => {
            const fileInfo = {
              id: res.attachment.uuid,
              name: file.name,
            };
            const index = this.fileList.findIndex(_ => _.id === id);
            if (index > -1) {
              this.fileList.splice(index, 1, fileInfo);
            } else {
              this.fileList.push(fileInfo);
            }
            this.$toast.success('上传成功');
          },
          onError: () => {
            const index = this.fileList.findIndex(_ => _.id === id);
            this.fileList.splice(index, 1);
            this.$toast.fail('上传失败,请稍后重试');
          },
        });
      },
      onClickDelete(index) {
        this.fileList.splice(index, 1);
      },
    },
  };
</script>

<style lang="less" scoped>
  .survey-form-static-attachment {

    /deep/ .van-cell {
      padding: 10px 0;
    }

    .uploader-block {
      position: relative;
      display: -webkit-box;
      display: -webkit-flex;
      display: flex;
      -webkit-box-orient: vertical;
      -webkit-box-direction: normal;
      -webkit-flex-direction: column;
      flex-direction: column;
      -webkit-box-align: center;
      -webkit-align-items: center;
      align-items: center;
      -webkit-box-pack: center;
      -webkit-justify-content: center;
      justify-content: center;
      width: 80px;
      height: 80px;
      margin: 0 8px 8px 0;
      background-color: #fff;
      border: 1px dashed #ebedf0;
      border-radius: 4px;

      .mask {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        display: -webkit-box;
        display: -webkit-flex;
        display: flex;
        -webkit-box-orient: vertical;
        -webkit-box-direction: normal;
        -webkit-flex-direction: column;
        flex-direction: column;
        -webkit-box-align: center;
        -webkit-align-items: center;
        align-items: center;
        -webkit-box-pack: center;
        -webkit-justify-content: center;
        justify-content: center;
      }

      .text {
        width: 100%;
        margin-top: 8px;
        padding: 0 4px;
        color: #646566;
        font-size: 12px;
        text-align: center;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
      }

      .remove {
        position: absolute;
        top: -8px;
        right: -8px;
        color: #969799;
        font-size: 18px;
        background-color: #fff;
        border-radius: 100%;
      }
    }

    &-wrap {
      display: -webkit-box;
      display: -webkit-flex;
      display: flex;
      -webkit-flex-wrap: wrap;
      flex-wrap: wrap;
    }
  }
</style>
