<i18n>
    {
    "en": {
    "总计条数据":"total {0} items",
    "每页条":"{0} per page",
    "第页":"{0}/{1}"
    },
    "zh_CN": {
    "总计条数据":"总计 {0} 条数据",
    "每页条":"每页 {0} 条",
    "第页":"第 {0}/{1} 页"
    }
    }
</i18n>
<template>
    <Poptip
        v-model="poptipShow"
        transfer
        class="page-poptip-select"
        popper-class="page-poptip-select-popper"
        placement="bottom"
        width="300">
        <div class="page-poptip-select-trigger" :style="triggerStyle">
            <div class="label" :class="{ placeholder: String.isEmpty(modelValue) }">
                <template v-if="String.isEmpty(modelValue)">
                    请选择
                </template>
                <template v-else>
                    {{ modelLabel }}
                </template>
            </div>
            <Icon color="icon" type="ios-arrow-down" size="16" slot="suffix-slot" />
        </div>
        <div class="page-poptip-select-popper-wrap" slot="content">
            <div class="page-poptip-select-popper-list scrollbox">
                <div class="page-poptip-select-popper-nodata" v-show="Array.isEmpty(list)">暂无数据</div>
                <template v-for="item in list">
                    <div
                        class="page-poptip-select-popper-item"
                        :class="{ selected: modelValue === item[vkey] }"
                        :key="item[vkey]"
                        @click.stop="onClickItem(item)">
                        <slot name="item" :data="item"></slot>
                        <template v-if="!hasItemSlot">
                            {{ item[vkey] }}
                        </template>
                    </div>
                </template>
            </div>
            <div class="page-poptip-select-popper-page" v-if="query.totalPage > 1">
                <div class="vcenter totalspan">{{$t('总计条数据',[query.totalCount])}}</div>
                <div class="vcenter pagespan">{{$t('每页条',[query.pageSize])}}</div>
                <IconButton
                    @click="onChangePage(-1)" :disabled="query.pageIndex===1" :size="15" icon="md-arrow-round-back" />
                <div class="vcenter">{{$t('第页',[query.pageIndex,query.totalPage])}}</div>
                <IconButton
                    @click="onChangePage(1)"
                    :disabled="query.pageIndex===query.totalPage"
                    :size="15"
                    icon="md-arrow-round-forward" />
            </div>
        </div>
    </Poptip>
</template>

<script>
    import VueMixin from '../vue.mixin';

    export default {
        name: 'PagePoptipSelect',
        mixins: [VueMixin],
        props: {
            value: {
                type: [String, Number],
            },
            width: {
                type: [String, Number],
                default: 200,
            },
            vlabel: {
                type: String,
                default: 'name',
            },
            vkey: {
                type: String,
                default: 'id',
            },
            list: {
                type: Array,
                default() {
                    return [];
                },
            },
            query: {
                type: Object,
                default() {
                    return {
                        totalCount: 0,
                        totalPage: 0,
                    };
                },
            },
        },
        data() {
            return {
                poptipShow: false,
                modelValue: null,
                modelLabel: null,
            };
        },
        computed: {
            hasItemSlot() {
                return !this.$slots.item;
            },
            triggerStyle() {
                return {
                    width: `${ this.width }px`,
                };
            },
        },
        watch: {
            value: {
                immediate: true,
                handler(val) {
                    this.modelValue = val;
                    this.compValue();
                },
            },
            modelValue(val) {
                this.$emit('input', val);
            },
        },
        methods: {
            visible(val) {
                this.poptipShow = val === true;
            },
            compValue() {
                if (Array.isEmpty(this.list)) {
                    return;
                }
                if (String.isEmpty(this.modelValue)) {
                    this.modelValue = this.list[0][this.vkey];
                    this.modelLabel = this.list[0][this.vlabel];
                    return;
                }
                const obj = this.list.find(item => item[this.vkey] === this.modelValue);
                if (!obj) {
                    return;
                }
                this.modelValue = obj[this.vkey];
                this.modelLabel = obj[this.vlabel];
            },
            onChangePage(pageAddition) {
                this.$emit('on-page-change', pageAddition);
            },
            onClickItem(item) {
                if (!this.poptipShow) {
                    return;
                }
                this.modelValue = item[this.vkey];
                this.modelLabel = item[this.vlabel];
                this.poptipShow = false;
                this.$emit('on-change', this.modelValue);
            },
        },
    };
</script>

<style lang="less" scoped></style>
