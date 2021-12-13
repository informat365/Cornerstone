<template>
    <div class="action-dropdown" :class="{disabled}">
        <template v-if="disabled">
            <template v-if="disabledShowOperaText">
                <div class="action-dropdown-name">
                    <span>
                        {{ operaText }}
                    </span>
                </div>
            </template>
        </template>
        <template v-else>
            <Dropdown
                trigger="click"
                placement="bottom-start"
                transfer-class-name="action-dropdown-wrap"
                transfer
                @on-click="onClickDropdown">
                <div class="action-dropdown-actions">
                    <slot name="prefix-slot"></slot>
                    <span class="status">{{ operaText }}</span>
                    <slot name="suffix-slot"></slot>
                </div>
                <DropdownMenu slot="list">
                    <template v-for="action in actions">
                        <DropdownItem
                            :name="action.name"
                            :disabled="action.disabled"
                            :divided="action.divided"
                            :selected="action.selected">
                            <div class="action-dropdown-item">
                                <div>
                                    {{ action.label }}
                                </div>
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
        name: 'ActionDropdown',
        props: {
            disabled: {
                type: Boolean,
                default: false,
            },
            disabledShowOperaText: {
                type: Boolean,
                default: true,
            },
            operaText: {
                type: String,
                default: '操作',
            },
            /**
             * [
             *      {
             *          name:'delete',
             *          label:"删除发布内容",
             *          disabled:false,
             *          divided:false,
             *          selected:false,
             *      },
             * ]
             */
            actions: {
                type: Array,
                default() {
                    return [];
                },
            },
        },
        methods: {
            onClickDropdown(name) {
                this.$emit('on-click', name);
            },
        },
    };
</script>

<style scoped>

</style>
