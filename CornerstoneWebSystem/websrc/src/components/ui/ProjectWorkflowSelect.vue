<style scoped>
    .check {
        color: #0097f7;
    }

    .workflow-status {
        /*display: inline-block;*/
        padding: .25em .6em;
        font-size: 12px;
        font-weight: 600;
        border-radius: 1rem;
        text-align: center;
        white-space: nowrap;
        vertical-align: middle;
        max-width: 200px;
        border: 1px solid #aaa;
        color: #8897aa;
        text-overflow: ellipsis;
        overflow: hidden;
        white-space: nowrap;
    }

    .workflow-status-line {
        display: inline-block;
        padding: .25em .6em;
        font-size: 12px;
        font-weight: 600;
        border-radius: 1rem;
        text-align: center;
        white-space: nowrap;
        vertical-align: middle;
        max-width: 200px;
        border: 1px solid #aaa;
        color: #8897aa;
        text-overflow: ellipsis;
        overflow: hidden;
        white-space: nowrap;
    }
</style>
<i18n>
    {
    "en": {
    "未设置":"none",
    "暂无数据":"No Data"
    },
    "zh_CN": {
    "未设置":"未设置",
    "暂无数据":"暂无数据"
    }
    }
</i18n>
<template>
    <div>
        <div v-if="disabled===true">
            <span
                v-if="selectedStatus"
                class="workflow-status"
                :style="{color:selectedStatus.color,borderColor:selectedStatus.color}">
                {{selectedStatus.name}}
            </span>
            <span v-else class="placeholder-label">{{$t('未设置')}}</span>
        </div>
        <Poptip
            v-else transfer class="poptip-full" v-model="visible" :placeholder="placeholder" style="text-align:left">
            <div class="popup-select">
                <span
                    v-if="selectedStatus"
                    class="workflow-status"
                    :style="{color:selectedStatus.color,borderColor:selectedStatus.color}">
                    {{selectedStatus.name}}
                </span>
                <span v-else class="placeholder-label">{{$t('未设置')}}</span>
                <Icon style="margin-left:10px" class="popup-select-arrow" type="ios-arrow-down"></Icon>
            </div>
            <div slot="content" class="popup-box scrollbox">
                <Row
                    @click.native="onClickRow(item)"
                    class="popup-item-name"
                    v-for="item in transferFlowList"
                    :key="item.id">
                    <Col span="18">
                        <div
                            class="workflow-status-line" :style="{color:item.color,borderColor:item.color}">
                            {{item.name}}
                        </div>
                    </Col>
                    <Col span="6" style="text-align:right">
                        <Icon v-if="item.id===selectedValue" type="md-checkmark" class="check"/> &nbsp;
                    </Col>
                </Row>
            </div>
        </Poptip>

    </div>
</template>
<script>
    export default {
        name: 'ProjectWorkflowSelect',
        props: {
            placeholder: {
                type: String,
            },
            value: {
                type: Number,
            },
            disabled: {
                type: Boolean,
                default: false,
            },
            projectId: {
                type: Number,
                required: true,
            },
        },
        data() {
            return {
                selectedValue: null,
                visible: false,
                flowList: [],
            };
        },
        watch: {
            value: {
                immediate: true,
                handler(val) {
                    if (!val) {
                        return;
                    }
                    this.selectedValue = val;
                },
            },
            selectedValue(val) {
                this.$emit('input', val);
            },
            projectId: {
                immediate: true,
                handler() {
                    this.loadData();
                },
            },
            visible(val) {
                if (val === false) {
                    this.$emit('change', this.selectedValue);
                }
            },
        },
        computed: {
            selectedStatus() {
                if (!Array.isArray(this.flowList) || !this.selectedValue) {
                    return null;
                }
                return this.flowList.find(_ => _.id === this.selectedValue);
            },
            transferFlowList() {
                if (this.selectedStatus) {
                    return this.flowList.filter(_ => {
                        return this.selectedStatus.transferTo.indexOf(_.id) > -1;
                    });
                }
                return this.flowList;
            },
        },
        methods: {
            loadData() {
                if (!this.projectId) {
                    this.flowList = [];
                    return;
                }
                app.invoke('BizAction.getProjectStatusDefineInfoList', [app.token, this.projectId, 0], (list) => {
                    this.flowList = list;
                });
            },
            onClickRow(item) {
                if (!this.visible) {
                    return;
                }
                this.selectedValue = item.id;
                this.visible = false;
            },
        },
    };
</script>
