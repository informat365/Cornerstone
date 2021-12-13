<template>
    <div :class="{'disabled': readonly}" class="company-user-select" @click="onClickDepartmentSelect">
        <div class="placeholder" v-show="!Array.isArray(departmentList) || departmentList.length === 0">
            {{ $t(placeholder||'部门') }}
        </div>
        <div
            class="selection"
            :class="{multiple}"
            v-show="Array.isArray(departmentList) && departmentList.length > 0">
            <template v-for="item in departmentList">
                <template v-if="multiple">
                    <Tag
                        :key="item.id" :closable="!readonly" @on-close="onRemoveDepartment(item.id)">{{item.name}}
                    </Tag>
                </template>
                <template v-else>
                    {{item.name}}
                </template>
            </template>
        </div>
        <template v-if="clearable && Array.isArray(departmentList) && departmentList.length > 0">
            <Icon v-if="!readonly" class="icon" type="ios-close-circle" @click.stop.native="onClickClear"/>
        </template>
        <template v-else>
            <Icon class="icon" type="ios-arrow-down"/>
        </template>
    </div>
</template>

<i18n>
    {
    "en": {
    "用户":"Member",
    "汇报人":"Reportor"
    },
    "zh_CN": {
    "用户":"用户",
    "汇报人":"汇报人"
    }
    }
</i18n>
<script>
    export default {
        name: 'CompanyDepartmentSelect',
        props: {
            multiple: {
                type: Boolean,
                default: false,
            },
            clearable: {
                type: Boolean,
                default: false,
            },
            readonly: {
                type: Boolean,
                default: false,
            },
            placeholder: {
                type: String,
                default: null
            },
            defaultList: {
                type: Array,
            }
        },
        data() {
            return {
                departmentList: [],
            };
        },
        watch: {
            defaultList: {
                immediate: true,
                handler(val) {
                    this.departmentList = val;
                }
            },
            departmentList(val) {
                if (val) {
                    const values = val.map(item => item.id);
                    if (this.multiple) {
                        this.$emit('input', values);
                    } else {
                        this.$emit('input', values[0]);
                    }
                }
                setTimeout(() => {
                    this.$emit('on-change', val);
                });
            },
        },
        methods: {

            onRemoveDepartment(deptId) {
                if (this.readonly) {
                    return;
                }
                const index = this.departmentList.findIndex(item => item.id === deptId);
                if (index === -1) {
                    return;
                }
                this.departmentList.splice(index, 1);
                const departmentIds = [];
                this.departmentList.forEach(item => {
                    departmentIds.push(item.id);
                });
                this.modelValue = departmentIds;
            },
            onClickDepartmentSelect() {
                if (this.readonly) {
                    return;
                }
                app.showDialog(DepartmentSelectDialog, {
                    multiple: this.multiple,
                    callback: (res) => {
                        if (!this.multiple) {
                            if (!res) {
                                return;
                            }
                            this.departmentList = [{
                                id: res.id,
                                name: res.title,
                            }];
                            return;
                        }
                        if (!Array.isArray(res) || res.length === 0) {
                            this.departmentList = [];
                            return;
                        }
                        const departmentList = [];
                        const departmentIds = [];
                        res.forEach(item => {
                            if (departmentIds.indexOf(item.accountId) > -1) {
                                return;
                            }
                            departmentList.push({
                                id: item.id,
                                name: item.title,
                            });
                            departmentIds.push(item.id);
                        });
                        this.departmentList = departmentList;
                    },
                });
            },
            onClickClear() {
                if (this.readonly) {
                    return;
                }
                this.departmentList = [];
            },
        },
    };
</script>

<style lang="less" scoped>

    .company-user-select {
        position: relative;
        display: inline-block;
        width: 100%;
        color: #515a6e;
        font-size: 14px;
        cursor: pointer;
        background-color: #fff;
        border: 1px solid #dcdee2;
        outline: 0;
        -webkit-user-select: none;
        -moz-user-select: none;
        -ms-user-select: none;
        user-select: none;
        box-sizing: border-box;
        vertical-align: middle;

        &.disabled {
            background-color: #f3f3f3;
            cursor: not-allowed;
            &:hover {
                border-color: #dcdee2;
            }
        }

        &:hover {
            border-color: #57a3f3
        }


        .placeholder {
            display: block;
            height: 32px;
            line-height: 32px;
            font-size: 12px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            padding-left: 8px;
            padding-right: 24px;
            color: #c5c8ce;
        }

        /deep/ .disabled .ivu-tag {
            cursor: not-allowed;
        }

        .selection {
            position: relative;
            display: block;
            outline: 0;
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            user-select: none;
            min-height: 32px;
            max-height: 120px;
            overflow: auto;
            padding-top: 4px;
            padding-left: 4px;
            padding-right: 24px;

            &.multiple {
                padding-top: 2px;
            }
        }

        .icon {
            position: absolute;
            top: 50%;
            right: 8px;
            line-height: 1;
            transform: translateY(-50%);
            font-size: 14px;
            color: #808695;
        }
    }
</style>
