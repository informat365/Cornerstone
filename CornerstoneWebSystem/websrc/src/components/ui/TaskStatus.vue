<style scoped>
    .task-status {
        display: inline-block;
        padding: .25em .6em;
        font-size: 12px;
        font-weight: 600;
        border-radius: 1rem;
        line-height: 1;
        text-align: center;
        white-space: nowrap;
        vertical-align: middle;

        background-color: transparent;
        border: 1px solid #aaa;
        color: #8897AA;
    }

</style>
<template>
    <span class="task-status" :title="label&&label.length>6?label:''" :style="{color:color,borderColor:color}">{{label}}</span>
</template>
<script>
    export default {
        name: "TaskStatus",
        props: ['label', 'color'],
        data() {
            return {}
        }
    }
</script>
