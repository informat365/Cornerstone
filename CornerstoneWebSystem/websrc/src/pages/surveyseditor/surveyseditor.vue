<style>
</style>
<template>
    <div>
        <router-view></router-view>
    </div>
</template>
<script>
export default {
    data() {
        return {
            login: false
        };
    },
    mounted() {
        var loading = document.getElementById("loading-box");
        if (loading) {
            loading.parentNode.removeChild(loading);
        }
    },
    methods: {}
};
</script>