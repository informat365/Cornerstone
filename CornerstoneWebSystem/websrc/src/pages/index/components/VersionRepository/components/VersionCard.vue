<template>
    <div
        v-if="version" class="version-card">
        <div class="header">
            <div class="title">
                {{ version.name }}
            </div>
            <div class="actions" @click.stop>
                <VersionStatusSelect
                    :disabled="!hasPermissionUpdate" :value="version.status" @on-change="onStatusChange">
                    <Icon type="md-time" size="16" slot="prefix-slot" />
                </VersionStatusSelect>
            </div>
        </div>
        <div class="version" v-if="version.versionNo">
            <span >{{version.versionNo}}</span>
            <span class="no">
                <span v-if="version.ownerAccountList&&version.ownerAccountList.length>0">{{version.ownerAccountList.map(k=>k.name).join("、")}}</span>
            </span>
        </div>
        <div class="time" :style="versionTimeStyle">
            <template v-if="version.endTime > 0">
                预计发布时间：{{ version.endTime | fmtDateTime }}
            </template>
        </div>
        <div class="progress">
            <Progress :percent="progress" :stroke-width="8" />
        </div>
    </div>
</template>

<script>
    import VueMixin from '../vue.mixin';
    import VersionStatusSelect from './VersionStatusSelect';

    export default {
        name: 'VersionCard',
        components: { VersionStatusSelect },
        mixins: [VueMixin, componentMixin],
        props: {
            version: {
                type: Object,
                required: true,
            },
        },
        computed: {
            progress() {
                if (!this.version || this.version.totalTaskNum === 0) {
                    return 0;
                }
                return Number.parseFloat((this.version.finishTaskNum / this.version.totalTaskNum * 100).toFixed(1));
            },
            hasPermissionUpdate() {
                return this.perm('version_add');
            },
            versionTimeStyle() {
                if (!this.version || !this.version.endTime) {
                    return {};
                }
                const endTime = new Date(this.version.endTime);
                if (Number.isNaN(endTime.getTime())) {
                    return {};
                }
                const now = new Date();
                if (now.getTime() > this.version.endTime) {
                    return {
                        color: '#ff4f3e',
                    };
                }
                if (this.version.endTime - now.getTime() < 24 * 60 * 60 * 1000) {
                    return {
                        color: '#ffaf38',
                    };
                }
                return {};
            },
        },
        data() {
            return {
                loading: true,
                endTime: null,
            };
        },
        methods: {
            onStatusChange(status) {
                if (this.version.status === status) {
                    return;
                }
                if (status !== 3) {
                    this.updateStatus(status);
                    return;
                }
                this.endTime = null;
                this.$Modal.confirm({
                    title: '请选择版本发布时间',
                    closable: true,
                    render: (h) => {
                        return h('DatePicker', {
                            props: {
                                type: 'datetime',
                                transfer: true,
                                autofocus: true,
                                placeholder: '请选择版本发布时间',
                                options: {
                                    disabledDate(date) {
                                        return date && date.getTime() >= new Date().getTime();
                                    },
                                },
                            },
                            style: {
                                width: '100%',
                            },
                            on: {
                                'on-change': (val) => {
                                    this.endTime = val;
                                },
                            },
                        });
                    },
                    onOk: () => {
                        const endTime = Date.parser(this.endTime);
                        if (!Date.isDate(endTime)) {
                            return;
                        }
                        this.endTime = null;
                        this.updateStatus(3, endTime);
                    },
                });
            },
            updateStatus(status, endTime) {
                this.loading = true;
                const merge = {
                    status: Number.parseInt(status, 10),
                };
                if (Date.isDate(endTime)) {
                    merge.endTime = endTime;
                }
                this.request('BizAction.updateCompanyVersion', [{
                    ...this.version,
                    ...merge,
                }], () => {
                    app.toast('更新状态成功');
                    this.version = {
                        ...this.version,
                        ...merge,
                    };
                    this.loading = false;
                }, (code, message) => {
                    app.toast(`更新状态失败，${ message || '' }`);
                    this.loading = false;
                });
            },
        },
    };
</script>

<style lang="less" scoped>
    .version-card {
        position: relative;
        background: #fff;
        box-shadow: 0 2px 6px 0 hsla(0, 0%, 83.1%, .45);
        border-radius: 4px;
        border: 1px solid #eee;
        padding: 15px;
        margin-top: 20px;
        width: 320px;
        height: 134px;
        cursor: pointer;

        .header {
            display: flex;
            align-items: center;
            align-content: center;
            height: 28px;
            overflow: hidden;

            .title {
                flex: 1;
                overflow: hidden;
                text-overflow: ellipsis;
                display: -webkit-box;
                -webkit-line-clamp: 1;
                -webkit-box-orient: vertical;
            }

            .actions {
                min-width: 82px;
                height: 28px;
                border-radius: 4px;
                background: #f5f5f5;
                padding: 4px 8px;
                display: inline-flex;
                justify-content: center;
                align-items: center;
                color: #383838;
                transition: all 0.2s;

                .status {
                    padding-left: 5px;
                }
            }
        }

        .version {
            width: 100%;
            height: 28px;
            border-radius: 4px;
            padding: 4px 0;
            display: inline-flex;
            justify-content: space-between;
            align-items: center;
            color: #383838;
            transition: all 0.2s;

            .no {
                margin-right: 20px;
            }
        }

        .time {
            position: relative;
            height: 32px;
            line-height: 32px;
            font-size: 12px;
            color: #009f87;
        }

        .progress {
        }
    }
</style>
