<template>
    <div
        class="flag-title" :class="[size]">
        <div
            class="label" :style="labelStyle" @click="onClickLabel">
            {{label}}
        </div>
        <div class="content">
            <slot></slot>
        </div>
        <div class="right">
            <slot name="right" />
        </div>
        <div class="clearfix" />
    </div>
</template>

<script>
    export default {
        name: 'FlagTitle',
        props: {
            label: {
                type: [String, Number],
                required: true,
            },
            clickable: {
                type: Boolean,
                default: false,
            },
            size: {
                type: String,
                validator(value) {
                    return ['default', 'medium', 'large'].indexOf(value) > -1;
                },
                default: 'default',
            },
            flagStyle: {
                type: Object,
            },
        },
        computed: {
            labelStyle() {
                let style = this.flagStyle;
                if (Object.isEmpty(style)) {
                    style = {};
                }
                if (this.clickable) {
                    style.cursor = 'pointer';
                }
                return style;
            },
        },
        methods: {
            onClickLabel() {
                if (!this.clickable) {
                    return;
                }
                this.$emit('on-click-label');
            },
        },
    };
</script>

<style lang="less" scoped>

    .clearfix {
        clear: both;
    }

    .flag-title {
        padding: 8px 0;

        .label {
            position: relative;
            float: left;
            height: 20px;
            line-height: 20px;
            font-weight: bold;
            font-size: 14px;
            color: #333;
            padding-left: 8px;

            &::before {
                content: "";
                position: absolute;
                top: 50%;
                left: 0;
                margin-top: -10px;
                height: 20px;
                width: 3px;
                border-left: solid 3px #2391ff;
            }
        }

        .content {
            float: left;
            height: 20px;
            line-height: 20px;
            font-size: 12px;
        }

        .right {
            float: right;
            height: 20px;
            line-height: 20px;
            font-size: 12px;
            color: #021c2c;
        }

        &.medium {
            .label {
                height: 32px;
                line-height: 32px;
            }

            .content {
                height: 32px;
                line-height: 32px;
            }

            .right {
                height: 32px;
                line-height: 32px;
            }
        }

        &.large {
            .label {
                height: 48px;
                line-height: 48px;
            }

            .content {
                height: 48px;
                line-height: 48px;
            }

            .right {
                height: 48px;
                line-height: 48px;
            }
        }
    }
</style>
