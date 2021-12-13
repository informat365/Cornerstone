<style scoped>

    .popup-item-name {
        cursor: pointer;
    }

    .popup-item-name:hover .edit-btn {
        visibility: visible;
    }

    .edit-btn {
        visibility: hidden;
        margin-right: 10px;
    }

    .draggable {
        cursor: pointer;
    }
</style>
<i18n>
    {
    "en": {
    "使用默认字段顺序":"Use default field order",
    "显示全部字段":"All Fields",
    "只显示系统字段":"Only System Fields"
    },
    "zh_CN": {
    "使用默认字段顺序":"使用默认字段顺序",
    "显示全部字段":"显示全部字段",
    "只显示系统字段":"只显示系统字段"
    }
    }
</i18n>
<template>
    <Poptip class="poptip-full" v-model="visible" :placement="placement">
        <slot name="popup-name"></slot>
        <div slot="content" style="text-align:left;width:250px;max-height:400px;overflow:auto">
            <draggable
                v-model="tableFieldList" :options="{draggable:'.popup-item-name'}">
                <Row
                    @click.native="toggleField(field)"
                    class="popup-item-name draggable"
                    vv-if="field.isShow"
                    v-for="field in tableFieldList"
                    :key="field.id">
                    <Col span="18">
                        <Icon type="md-more" />
                        {{field.name}}
                    </Col>
                    <Col span="6" style="text-align:right">
                        <Icon v-if="isShow(field)" type="md-checkmark" />&nbsp;
                    </Col>
                </Row>
            </draggable>
            <Row @click.native="onResetFieldsOrder" class="popup-item-name popup-item-name-divide">
                {{$t('使用默认字段顺序')}}
            </Row>
            <Row @click.native="showAllField" class="popup-item-name popup-item-name-divide">
                {{$t('显示全部字段')}}
            </Row>
            <Row
                v-show="!noSystemFields" @click.native="showSystemField" class="popup-item-name popup-item-name-divide">
                {{$t('只显示系统字段')}}
            </Row>
        </div>
    </Poptip>

</template>
<script>
    import draggable from 'vuedraggable';

    export default {
        name: 'TaskTableHeaderSelect',
        props: {
            placement: {
                type: String,
            },
            fieldList: {
                type: Array,
            },
            value: {
                type: Array,
            },
            noSystemFields: {
                type: Boolean,
                default: false,
            },
        },
        components: {
            draggable,
        },
        data() {
            return {
                visible: false,
                hideValue: this.value,
                tableFieldList: [],
                tableFieldChangeId: 0,
            };
        },
        watch: {
            value(val) {
                this.hideValue = val;
            },
            fieldList: {
                immediate: true,
                handler(val) {
                    if (!Array.isArray(val)) {
                        this.tableFieldList = [];
                        return;
                    }
                    this.tableFieldList = val;
                },
            },
            tableFieldList(val) {
                clearTimeout(this.tableFieldChangeId);
                this.tableFieldChangeId = setTimeout(() => {
                    this.$emit('on-field-list-sort', val);
                }, 100);
            },
            visible(val) {
                if (val === false) {
                    this.$emit('input', this.hideValue);
                    this.$emit('change', this.hideValue);
                }
            },
        },
        methods: {
            toggleField(field) {
                if (this.hideValue == null) {
                    this.hideValue = [];
                }
                const index = this.hideValue.findIndex(item => field.id === item);
                if (index > -1) {
                    this.hideValue.splice(index, 1);
                    return;
                }
                this.hideValue.push(field.id);
            },
            isShow(field) {
                if (this.hideValue == null) {
                    return true;
                }
                return this.hideValue.findIndex(item => field.id === item) === -1;
            },
            onResetFieldsOrder() {
                this.$emit('on-reset-fields-order');
            },
            showAllField() {
                this.hideValue = [];
            },
            showSystemField() {
                this.hideValue = [];
                for (var i = 0; i < this.fieldList.length; i++) {
                    var t = this.fieldList[i];
                    if (!t.isSystemField) {
                        this.hideValue.push(t.id);
                    }
                }
            },
        },
    };
</script>
