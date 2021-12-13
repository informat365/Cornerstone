<template>
    <div class="version-status" :class="{disabled}">
        <template v-if="disabled">
            <div class="version-status-name">
                <slot name="prefix-slot"></slot>
                <div>
                    {{ statusName }}
                </div>
                <slot name="suffix-slot"></slot>
            </div>
        </template>
        <template v-else>
            <Dropdown
                :disabled="loading"
                trigger="click"
                placement="bottom-start"
                transfer-class-name="version-status-dropdown"
                transfer
                @on-click="onStatusClick">
                <div class="version-status-dropdown-actions">
                    <slot name="prefix-slot"></slot>
                    <span class="status">{{ statusName }}</span>
                    <slot name="suffix-slot"></slot>
                </div>
                <DropdownMenu slot="list">
                    <template v-for="item in versionStatus">
                        <DropdownItem :key="item.value" :name="item.value">
                            <div class="version-status-item">
                                <div>{{ item.name }}</div>
                                <Icon type="md-checkmark" v-show="modelValue === item.value" />
                            </div>
                        </DropdownItem>
                    </template>
                </DropdownMenu>
            </Dropdown>
        </template>
    </div>
</template>

<script>
    export default {
        name: 'VersionStatusSelect',
        props: {
            value: {
                type: Number,
            },
            disabled: {
                type: Boolean,
                default: false,
            },
        },
        computed: {
            statusName() {
                if (!this.modelValue || !Array.isArray(this.versionStatus)) {
                    return '';
                }
                const status = this.versionStatus.find(item => item.value === this.modelValue);
                if (!status) {
                    return '';
                }
                return status.name;
            },
        },
        data() {
            return {
                modelValue: null,
                versionStatus: [],
                loading: true,
            };
        },
        watch: {
            value: {
                immediate: true,
                handler(val) {
                    this.modelValue = val;
                },
            },
        },
        created() {
            this.versionStatus = app.dataDicts['CompanyVersion.status'];
        },
        methods: {
            onStatusClick(status) {
                this.$emit('on-change', status);
            },
        },
    };
</script>

<style lang="less" scoped>
    .version-status {
        &-name {
            display: flex;
            align-items: center;
            align-content: center;
            justify-content: space-between;
            padding: 0 8px;
        }
    }
</style>
