<style scoped>
    .task-priority {
        color: #8897AA;
        font-size: 13px;
        font-weight: 600;
        vertical-align: middle;
        margin-left: 3px;
    }

</style>
<template>
   <span style="color: #8897AA">
          <Icon type="md-calendar" color="#8897AA"/>
          <span class="task-priority">{{start|fmtDate}}</span>~
          <span class="task-priority">{{end|fmtDate}}</span>
      </span>
</template>
<script>
    export default {
        name: "TaskPeriod",
        props: ['start', 'end'],
        data() {
            return {}
        }
    }
</script>
