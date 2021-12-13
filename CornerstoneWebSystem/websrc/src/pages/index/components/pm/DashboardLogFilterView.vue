<style scoped>
.menu-item-group {
    font-size: 12px;
    color: #999;
    padding: 8px 0px;
    margin-top: 20px;
}
.search-input {
    border: none;
    border-bottom: 1px solid #eee;
    outline: none;
    width: 100%;
}
</style>
<i18n>
{
    "en": {
        "项目": "Project",
        "用户": "Member",
        "按照项目过滤": "Filter by project",
        "按照用户过滤": "Filter by member",
        "开始时间": "Start",
        "结束时间": "End"
    },
    "zh_CN": {
        "项目": "项目",
        "用户": "用户",
        "按照项目过滤": "按照项目过滤",
        "按照用户过滤": "按照用户过滤",
        "开始时间": "开始时间",
        "结束时间": "结束时间"
    }
}
</i18n>
<template>
    <div>
        <div class="menu-item-group">{{$t('项目')}}</div>
        <div>
            <input
                class="search-input"
                :placeholder="$t('按照项目过滤')"
                type="text"
                @change="reloadData()"
                clearable
                v-model.trim="formItem.projectName"
            >
        </div>

        <div class="menu-item-group">{{$t('用户')}}</div>
        <div>
            <input
                class="search-input"
                :placeholder="$t('按照用户过滤')"
                clearable
                type="text"
                @change="reloadData()"
                v-model.trim="formItem.createAccountName"
            >
        </div>

        <div class="menu-item-group">{{$t('开始时间')}}</div>
        <ExDatePicker
            @on-change="reloadData"
            type="date"
            clearable
            v-model="formItem.createTimeStart"
            :placeholder="$t('开始时间')"
        ></ExDatePicker>
        <div></div>

        <div class="menu-item-group">{{$t('结束时间')}}</div>
        <ExDatePicker
            @on-change="reloadData"
            type="date"
            clearable
            :day-end="true"
            v-model="formItem.createTimeEnd"
            :placeholder="$t('结束时间')"
        ></ExDatePicker>
        <div></div>
    </div>
</template>

<script>
export default {
    mixins: [componentMixin],
    data() {
        return {
            formItem: {
                projectName: null,
                createAccountName: null,
                createTimeStart: null,
                createTimeEnd: null
            }
        };
    },
    methods: {
        reloadData() {
            this.$emit("change", this.formItem);
        }
    }
};
</script>
