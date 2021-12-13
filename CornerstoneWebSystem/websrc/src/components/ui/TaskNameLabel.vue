<style scoped>

    @-webkit-keyframes spinner {
        0% {
            -webkit-transform: rotate(0deg);
            transform: rotate(0deg);
        }
        100% {
            -webkit-transform: rotate(359deg);
            transform: rotate(359deg);
        }
    }

    @keyframes spinner {
        0% {
            -webkit-transform: rotate(0deg);
            transform: rotate(0deg);
        }
        100% {
            -webkit-transform: rotate(359deg);
            transform: rotate(359deg);
        }
    }


    .task-name-label {
    }

    .numberfont {
        color: #999;
        padding-top: 5px;
        padding-right: 5px;
        font-weight: bold
    }

    .gray {
        color: #999;
        padding-right: 5px;
        text-decoration: line-through !important;
    }

    .link {
        padding-right: 5px;
        text-decoration: none;
    }

    .link1 {
        display: inline-block;
        padding-right: 5px;
        text-decoration: none;
        overflow: hidden;
        resize:horizontal;
        margin-bottom: -6px;
    }

    .light {
        color: #0097f7;
        font-weight: bold;
    }

    .object-type-label {
        background-color: #eee;
        color: #555;
        font-size: 12px;
        padding: 2px 5px;
        text-align: center;
        display: inline-block;
        border-radius: 3px;
        margin-right: 5px;
        font-weight: bold;
    }

    .task-expand {
        display: inline-flex;
        width: 20px;
        align-items: center;
        align-content: center;
        color: #8c8c8c;
    }

    .task-expand .icon-loading {
        -webkit-animation: spinner 1s infinite linear;
        animation: spinner 1s infinite linear;
    }

    .task-expand .icon-expand {
        transition: transform 0.2s ease-in-out;
    }

    .task-expand .icon-expand:hover {
        color: #57a3f3;
    }

    .task-expand.expand .icon-expand {
        transform: rotate(90deg);
    }
</style>
<i18n>
    {
    "en": {
    "前置":"PRE",
    "后置":"AFT",
    "关联":"REL",
    "子":"SUB",
    "父":"PAT"
    },
    "zh_CN": {
    "前置":"前置",
    "后置":"后置",
    "关联":"关联",
    "子":"子",
    "父":"父"
    }
    }
</i18n>
<template>
    <span class="task-name-label">
        <span
            class="task-expand" :class="{expand:expand}" v-show="withExpand">
            <template v-if="canExpand">
                <template v-if="loading">
                    <Icon class="icon-loading" type="ios-loading" />
                </template>
                <template v-else>
                    <Icon
                        class="icon-expand" type="md-play" @click.native.stop="onToggleExpand" />
                </template>
            </template>
        </span>
        <span v-if="objectType" class="object-type-label">
        <template v-if="sub==true">{{$t('子')}}</template>
        <template v-if="parent==true">{{$t('父')}}</template>
        <template v-if="rel==0">{{$t('关联')}}</template>
        <template v-if="rel==1"><Icon type="md-arrow-round-back" />{{$t('前置')}}</template>
        <template v-if="rel==2"><Icon type="md-arrow-round-forward" />{{$t('后置')}}</template>
        <span>{{objectType}}</span>
        </span>
        <span class="numberfont">#{{id}}</span>
        <abbr :title="name" class="link" :class="{'gray':isDone,'light':light}">{{name}}</abbr>
    </span>
</template>
<script>
    export default {
        name: 'TaskNameLabel',
        props: {
            id: {
                type: [String, Number],
            },
            name: {
                type: [String, Number],
            },
            type: {
                type: [String, Number],
            },
            objectType: {
                type: String,
            },
            isDone: {
                type: Boolean,
                default: false,
            },
            light: {
                type: Boolean,
                default: false,
            },
            sub: {
                type: Boolean,
                default: false,
            },
            rel: {
                type: Number,
            },
            parent: {
                type: Boolean,
                default: false,
            },
            withExpand: {
                type: Boolean,
                default: false,
            },
            canExpand: {
                type: Boolean,
                default: false,
            },
            expand: {
                type: Boolean,
                default: false,
            },
            loading: {
                type: Boolean,
                default: false,
            },
        },
        methods: {
            onToggleExpand() {
                this.$emit('on-task-expand', !this.expand);
            },
        },
    };
</script>
