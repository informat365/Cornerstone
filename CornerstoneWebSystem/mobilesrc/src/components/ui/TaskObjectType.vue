<template>
  <div class="object-type-label">
    {{name}}
  </div>
</template>

<script>
export default {
  props: ['name']
}
</script>

<style scoped>
 .object-type-label{
        background-color: #EEEEEE;
        color:#999;
        font-size:12px;
        padding:2px 5px;
        text-align: center;
        display: inline-block;
	      white-space: nowrap;
        border-radius: 3px;
        margin-right: 5px;
        font-weight: bold;
  }
</style>