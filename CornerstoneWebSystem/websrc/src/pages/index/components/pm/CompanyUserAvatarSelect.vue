<template>
    <div class="company-user-select" @click="onClickUserSelect">
        <div class="placeholder" v-show="!Array.isArray(accountList) || accountList.length === 0">
            {{ $t(placeholder) }}
        </div>
        <div
            class="selection"
            :class="{multiple}"
            v-show="Array.isArray(accountList) && accountList.length > 0">
            <template v-for="item in accountList">
                <template v-if="multiple">
                    <AvatarImage :value="item.imageId" :name="item.name" size="small"></AvatarImage>
                    <!--          <Tag-->
                    <!--            :key="item.id" closable @on-close="onRemoveUser(item.id)">{{item.name}}-->
                    <!--          </Tag>-->
                </template>
                <template v-else>
                    {{item.name}}
                </template>
            </template>
        </div>
        <template v-if="clearable && Array.isArray(accountList) && accountList.length > 0">
            <Icon class="icon" type="ios-close-circle" @click.stop.native="onClickClear"/>
        </template>
        <template v-else>
            <Icon class="icon" type="ios-arrow-down"/>
        </template>
    </div>
</template>

<i18n>
    {
    "en": {
    "用户":"Member"
    },
    "zh_CN": {
    "用户":"用户"
    }
    }
</i18n>
<script>
    import AvatarImage from "../../../../components/ui/AvatarImage";

    export default {
        name: 'CompanyUserSelect',
        components: {AvatarImage},
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
                default: '用户'
            },
            initAccountList: {
                type: Array,
                default:()=>[]
            }
        },
        data() {
            return {
                accountList: this.initAccountList || [],
            };
        },
        watch: {
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
                app.showDialog(CompanyUserSelectDialog, {
                        accountList:this.accountList,
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

        &:hover {
            border-color: #57a3f3
        }


        .placeholder {
            display: block;
            height: 30px;
            line-height: 30px;
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
            cursor: pointer;
            background-color: #fff;
            min-height: 30px;
            max-height: 120px;
            overflow: auto;
            padding-top: 2px;
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
