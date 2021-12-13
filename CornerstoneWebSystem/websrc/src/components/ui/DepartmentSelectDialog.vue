<style scoped>
    .search-box {
        position: absolute;
        top: 32px;
        left: 0;
        width: 422px;
        max-height: 400px;
        overflow: auto;
        background-color: #fff;
        box-shadow: 0 1px 6px rgba(0, 0, 0, .2);
    }

    .search-item {
        padding: 10px;
        font-size: 13px;
        border-bottom: 1px solid #eee;
        cursor: pointer;
    }

    .search-item:hover {
        background-color: #ebf7ff;
    }

    .search-no-data {
        text-align: center;
        padding: 20px;
    }
</style>
<i18n>
    {
    "en": {
    "选择部门":"Choose Department",
    "清除所有":"Clear all",
    "清除":"Clear",
    "找不到匹配的数据":"No data",
    "搜索部门":"Search department",
    "确定":"OK",
    "请选择部门":"Select at least one department"
    },
    "zh_CN": {
    "选择部门":"选择部门",
    "清除所有":"清除所有",
    "清除":"清除",
    "找不到匹配的数据":"找不到匹配的数据",
    "搜索部门":"搜索部门",
    "确定":"确定",
    "请选择部门":"请选择部门"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('选择部门')" width="700" @on-ok="confirm">
        <div style="height:500px;padding:10px">
            <div style="margin-top:10px;">
                <treeselect
                    v-model="selectedDepartments"
                    :clear-all-text="$t('清除所有')"
                    :clear-value-text="$t('清除')"
                    :flat="true"
                    :no-results-text="$t('找不到匹配的数据')"
                    :search-prompt-text="$t('搜索部门')"
                    :placeholder="$t('搜索部门')"
                    no-options-text=""
                    value-format="object"
                    :match-keys='["pinyin","label"]'
                    :show-count="true" value-consists-of="LEAF_PRIORITY"
                    :always-open="true"
                    :max-height="400"
                    :searchable="true"
                    :default-expand-level="1"
                    :multiple="multiple"
                    :options="departmentList">

                    <label slot="option-label"
                           slot-scope="{ node, shouldShowCount, count, labelClassName, countClassName }"
                           :class="labelClassName">
                        <Icon v-if="node.raw.type==1" type="ios-people"/>
                        <Icon v-if="node.raw.type==2" type="md-person"/>
                        {{ node.label }}
                        <span v-if="shouldShowCount" :class="countClassName">({{ count }})</span>
                    </label>
                </treeselect>
            </div>

        </div>
        <div slot="footer">
            <Row>
                <Col span="24" style="text-align:right">
                    <Button @click="confirm" type="default" size="large">{{$t('确定')}}</Button>
                </Col>
            </Row>
        </div>
    </Modal>
</template>


<script>
    import Treeselect from '@riophae/vue-treeselect'
    import '@riophae/vue-treeselect/dist/vue-treeselect.css'

    export default {
        mixins: [componentMixin],
        components: {Treeselect},
        props: {
            multiple: {
                type: Boolean,
                default: true,
            }
        },
        data() {
            return {
                treeData: [],
                selectedDepartments: [],
                departmentList: []
            }
        },
        methods: {
            pageLoad() {
                if (this.args.multiple == false) {
                    this.multiple = false;
                }
                this.getDepartmentList();
            },
            getDepartmentList() {
                app.invoke('BizAction.getDepartmentTree', [app.token, false], (info) => {
                    travalTree(info[0], (item) => {
                        item.label = item.title;
                        if (item.children.length == 0) {
                            delete item.children;
                        }
                    })
                    this.departmentList = info;

                })
            },
            confirm: function () {
                if (this.selectedDepartments.length == 0) {
                    app.toast(this.$t('请选择部门'));
                    return;
                }
                this.showDialog = false;
                this.args.callback(this.selectedDepartments)
            }
        }
    }
</script>
