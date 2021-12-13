<template>
    <div :class="{'disabled': readonly}" class="company-user-select" @click="onClickUserSelect">
        <div class="placeholder" v-show="!Array.isArray(accountList) || accountList.length === 0">
            {{ $t(placeholder||'用户') }}
        </div>
        <div
            class="selection"
            :class="{multiple}"
            v-show="Array.isArray(accountList) && accountList.length > 0">
            <template v-for="item in accountList">
                <template v-if="multiple">
                    <Tag
                        :key="item.id" :closable="!readonly" @on-close="onRemoveUser(item.id)">{{item.name}}
                    </Tag>
                </template>
                <template v-else>
                    {{item.name}}
                </template>
            </template>
        </div>
        <template v-if="clearable && Array.isArray(accountList) && accountList.length > 0">
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
        name: 'CompanyUserSelect',
        props: {
            multiple: {
                type: Boolean,
                default: false,
            },
            clearable: {
                type: Boolean,
                default: false,
            },
            placeholder: {
                type: String,
                default: null
            },
            defaultList: {
                type: Array
            },
            readonly: {
                type: Boolean,
                default: false
            }
        },
        data() {
            return {
                accountList: [],
            };
        },
        watch: {
            defaultList: {
                immediate: true,
                handler(val) {
                    this.accountList = val;
                }
            },
            accountList(val) {
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
            onRemoveUser(accountId) {
                if (this.readonly) {
                    return;
                }
                const index = this.accountList.findIndex(item => item.id === accountId);
                if (index === -1) {
                    return;
                }
                this.accountList.splice(index, 1);
                const accountIds = [];
                this.accountList.forEach(item => {
                    accountIds.push(item.accountId);
                });
                this.modelValue = accountIds;
            },
            onClickUserSelect() {
                if (this.readonly) {
                    return;
                }
                app.showDialog(CompanyUserSelectDialog, {
                        placeholder: this.placeholder,
                        callback: (res) => {
                            if (!this.multiple) {
                                if (!res) {
                                    return;
                                }
                                this.accountList = [{
                                    id: res.accountId,
                                    name: res.title,
                                    userName: res.userName,
                                }];
                                return;
                            }
                            if (!Array.isArray(res) || res.length === 0) {
                                this.accountList = [];
                                return;
                            }
                            const accountList = [];
                            const accountIds = [];
                            res.forEach(item => {
                                if (accountIds.indexOf(item.accountId) > -1) {
                                    return;
                                }
                                accountList.push({
                                    id: item.accountId,
                                    name: item.title,
                                    userName: item.userName,
                                });
                                accountIds.push(item.accountId);
                            });
                            this.accountList = accountList;
                        },
                    },
                    {multiple: this.multiple});
            },
            onClickClear() {
                if (this.readonly) {
                    return;
                }
                this.accountList = [];
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
            /deep/ .ivu-tag {
                cursor: not-allowed;
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
