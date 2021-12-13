<style scoped>

</style>
<i18n>
    {
    "en": {
    "选择成员": "Choose member",
    "清除所有":"Clear all",
    "清除":"Clear",
    "找不到匹配的用户":"No matching",
    "搜索成员":"Search member",
    "确定":"OK"
    },
    "zh_CN": {
    "选择成员": "选择成员",
    "清除所有":"清除所有",
    "清除":"清除",
    "找不到匹配的用户":"找不到匹配的用户",
    "搜索成员":"搜索成员",
    "确定":"确定"
    }
    }
</i18n>
<template>
    <Modal
        ref="dialog"
        v-model="showDialog"
        :closable="true"
        :mask-closable="false"
        :loading="false"
        :title="$t('选择成员')"
        width="700">
        <div style="min-height:500px">
            <treeselect
                v-model="selectedUsers"
                :clear-all-text="$t('清除所有')"
                :clear-value-text="$t('清除')"
                :no-results-text="$t('找不到匹配的用户')"
                :search-prompt-text="$t('搜索成员')"
                :placeholder="placeholder"
                no-options-text=""
                value-format="object"
                :match-keys='["pinyinName","label","userName"]'
                :show-count="true"
                value-consists-of="LEAF_PRIORITY"
                :always-open="true"
                :max-height="400"
                :disable-branch-nodes="multiple !== true"
                :searchable="true"
                :default-expand-level="1"
                :multiple="multiple"
                :options="treeData">

                <label
                    slot="option-label"
                    slot-scope="{ node, shouldShowCount, count, labelClassName, countClassName }"
                    :class="labelClassName">
                    <Icon v-if="node.raw.type===1" type="ios-people"/>
                    <Icon v-if="node.raw.type===2" type="md-person"/>
                    {{ node.label }}
                    <span v-if="shouldShowCount" :class="countClassName">({{ count }})</span>
                </label>
            </treeselect>
        </div>
        <div slot="footer">
            <Row>
                <Col span="24" style="text-align:right">
                    <Button @click="confirmSelect" type="default" size="large">{{$t('确定')}}</Button>
                </Col>
            </Row>
        </div>

    </Modal>
</template>


<script>
    import Treeselect from '@riophae/vue-treeselect';
    import '@riophae/vue-treeselect/dist/vue-treeselect.css';

    export default {
        mixins: [componentMixin],
        components: {Treeselect},
        props: {
            multiple: {
                type: Boolean,
                default: true,
            },
        },
        data() {
            return {
                treeData: [],
                selectedUsers: [],
                placeholder: "搜索成员"
            };
        },
        methods: {
            pageLoad() {
                if (this.args.accountList) {
                    this.selectUsers = this.args.accountList;
                }
                if(this.args.placeholder){
                    this.placeholder = this.args.placeholder;
                }
                this.loadUsers();
            },
            loadUsers() {
                app.invoke('BizAction.getDepartmentTree', [app.token, true], (info) => {
                    travalTree(info[0], (item) => {
                        item.label = item.title;
                        if (item.type == 1 && item.children.length == 0) {
                            item.isDisabled = true;
                        }
                        if (item.children.length == 0) {
                            delete item.children;
                        }

                    });
                    this.treeData = info;
                });
            },
            confirmSelect() {
                this.showDialog = false;
                if (this.args.callback) {
                    this.args.callback(this.selectedUsers);
                }
            },
        },
    };
</script>
