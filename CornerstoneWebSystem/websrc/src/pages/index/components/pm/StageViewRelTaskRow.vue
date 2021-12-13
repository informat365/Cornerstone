<style scoped>
    .associate-item {
        width: 100%;
        display: inline-flex;
        align-items: center;
        margin: 6px 0;
    }

    .asso-item-star {
        width: 30px;
    }

    .asso-item-owner {
        width: 100px;
    }

    .asso-item-status {
        width: 80px;
    }

    .asso-item-type {
        /*width: 60px;*/
        padding: 0 3px;
        background-color: #ddd;
        border-radius: 3px;
        margin-right: 2px;
    }

    .asso-item-name {
        margin-left: 5px;
        cursor: pointer;
        font-weight: 600;
        width: 250px;
        text-overflow: ellipsis;
        overflow: hidden;
        white-space: nowrap;
    }

    .asso-item-time {
        width: 200px;
        font-weight: 600;
    }

    .no-data {
        text-align: center;
        padding: 20px;
    }

    .no-data:after {
        content: '暂无关联里程碑和事件';
    }
</style>
<i18n>
    {
    "en": {
    "未设置":"none",
    "未分类":"none",
    "天":"Day"
    },
    "zh_CN": {
    "未设置":"未设置",
    "未分类":"未分类",
    "天":"天"
    }
    }
</i18n>
<template>
    <div>
        <div class="associate-item" v-for="(asso,idx) in datas" :key="'asso_'+asso.id" v-if="asso.type===1||(asso.type===2&&asso.landmarkId>0)">
            <template v-if="asso.type===1">
                <div class="asso-item-owner">
                    <TaskOwnerView :value="asso"/>
                </div>
                <div class="asso-item-status">
                    <TaskStatus
                        v-if="asso.taskStatusName"
                        :label="asso.taskStatusName"
                        :color="asso.taskStatusColor"/>
                </div>
                <div class="asso-item-type">{{asso.objectTypeName}}</div>
                <div @click="showTaskDialog(asso)" class="asso-item-name" :title="asso.taskName">#{{asso.taskSerialNo}}
                    {{asso.taskName}}
                </div>
                <div
                    class="asso-item-time"
                    v-if="!!asso.taskCreateTime || !!asso.taskEndTime">
                    {{asso.taskCreateTime|fmtDate}}
                    <Icon type="md-arrow-round-forward"/>
                    {{asso.taskEndTime|fmtDate}}
                    <template v-if="asso.taskEndTime">
                        <OverdueText
                            :is-finish="asso.isFinish"
                            v-if="asso.taskEndTime"
                            style="margin-left:5px"
                            :value="asso.taskEndTime"/>
                    </template>
                </div>
            </template>

            <template v-if="asso.type===2&&asso.landmarkId>0">
                <div class="asso-item-star">
                    <Icon type="ios-star" title="里程碑事件"/>
                </div>
                <div @click="showLandmarkInfo(asso)" class="asso-item-name" :title="asso.landmarkName">
                    {{asso.landmarkName}}
                </div>
                <div
                    class="asso-item-time">
                    {{asso.landmarkStartDate|fmtDate}}
                    <Icon type="md-arrow-round-forward"/>
                    {{asso.landmarkEndDate|fmtDate}}
                </div>
            </template>

        </div>
        <div v-if="datas.length===0" class="no-data"></div>
    </div>
</template>

<script>

    export default {
        mixins: [componentMixin],
        props: {
            datas: {
                type: Array,
            },
        },
        data() {
            return {
                title: 'StageViewRelTaskRow',
            };
        },
        mounted() {
        },
        methods: {
            showTaskDialog(task) {
                app.showDialog(TaskDialog, {
                    taskId: task.taskUuid
                })
            },
            showLandmarkInfo(item){
                app.showDialog(LandmarkInfoDialog,{
                    id:item.landmarkId
                });
            }
            /*  onClickRelTaskName(item) {
                  this.$emit('on-rel-task-show', item);
              },
              isTableFieldHide(field) {
                  if (!this.viewSetting.hideTableFieldList) {
                      return false;
                  }
                  return this.viewSetting.hideTableFieldList.findIndex(item => item.id === field.id) > -1;
              },*/
        },
    };
</script>
