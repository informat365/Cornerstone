<style scoped>
    .check {
        color: #0097f7;
    }

    .tag-color {
        display: inline-block;
        width: 30px;
        height: 30px;
        border-radius: 50%;
        margin-right: 10px;
        color: #fff;
        overflow: hidden;
        padding-top: 3px;
    }

    .filter {
        padding: 12px 20px;
    }

    .list-nodata {
        text-align: center;
        padding-top: 30px;
        padding-bottom: 10px;
        color: #999;
        font-size: 14px;
        font-weight: bold;
    }

    .popup-select-light{
        background-color: #fff;

    }
</style>
<i18n>
    {
    "en": {
    "未分类":"Uncategorized",
    "搜索分类": "Search",
    "暂无数据": "No Data"
    },
    "zh_CN": {
    "未分类": "未分类",
    "搜索分类": "搜索分类",
    "暂无数据":"暂无数据"
    }
    }
</i18n>
<template>
<span>
    <span v-if="disabled===true">
        <TaskCategory v-for="item in selectedItems" :key="item.id" :color="item.color" :label="item.name" />
        <span class="placeholder-label" v-if="selectedItems==null||selectedItems.length===0">{{ $t('未分类') }}</span>
    </span>

     <Poptip
         transfer v-if="disabled==null||disabled==false" class="poptip-full" v-model="visible" :placement="placement">
        <span class=" popup-select" :class="{'popup-select-light':light}">
            <TaskCategory v-for="item in selectedItems" :key="item.id" :color="item.color" :label="item.name" />
            <span class="placeholder-label" v-if="selectedItems==null||selectedItems.length===0">{{ $t('未分类') }}</span>
            <Icon style="margin-left:10px" class="popup-select-arrow" type="ios-arrow-down"></Icon>
        </span>
        <div slot="content" class="popup-box scrollbox" style="padding-top:10px;padding-bottom:10px;width:300px">
            <div class="filter">
                <Input
                    v-model="categoryKeyword" :placeholder="$t('搜索分类')" suffix="ios-search" />
            </div>
            <div v-if="categoryList==null||categoryList.length==0" class="list-nodata">{{ $t('暂无数据') }}</div>
             <TaskCategoryTreeNode
                 :edit="false"
                 :change="categorySelectChanged"
                 v-if="setup"
                 v-for="category in categories"
                 :value="category"
                 :key="'cate'+category.id" />
         </div>
    </Poptip>

</span>

</template>
<script>
    export default {
        mixins: [componentMixin],
        name: 'CategorySelect',
        props: ['placement', 'value', 'categoryList', 'disabled','light'],
        data() {
            return {
                visible: false,
                categoryKeyword: null,
                selectedItems: [],
                setup: false,
            };
        },
        watch: {
            value(val) {
                this.getSelectedItems();
            },
            categoryList(val) {
                this.getSelectedItems();
            },
            visible: function (val) {
                if (val == false) {
                    this.$emit('change');
                }
            },
        },
        computed: {
            categories() {
                if (!this.categoryKeyword) {
                    return this.categoryList;
                }
                const t = JSON.parse(JSON.stringify(this.categoryList));
                return this.deepFilterCategory(t, this.categoryKeyword);
            },
        },
        mounted() {
            this.getSelectedItems();
        },
        methods: {
            deepFilterCategory(list, keyword) {
                if (!Array.isArray(list) || list.length === 0) {
                    return [];
                }
                return list.filter((item) => {
                    if (item.children) {
                        item.children = this.deepFilterCategory(item.children, keyword);
                    }
                    return item.name.indexOf(keyword) > -1 || (Array.isArray(item.children) && item.children.length > 0);
                });
            },
            getSelectedItems() {
                this.selectedItems = [];
                if (this.value && this.categoryList) {
                    for (var i = 0; i < this.categoryList.length; i++) {
                        travalTree(this.categoryList[i], (item) => {
                            for (var i = 0; i < this.value.length; i++) {
                                if (this.value[i] === item.id) {
                                    this.selectedItems.push(item);
                                    item.selected = true;
                                }
                            }
                        });
                    }
                }
                this.setup = true;
            },
            categorySelectChanged(item) {
                if (Array.isArray(this.selectedItems)) {
                    const index = this.selectedItems.findIndex(_ => _.id === item.id);
                    if (index > -1) {
                        this.selectedItems.splice(index, 1);
                    } else {
                        this.selectedItems.push(item);
                    }
                } else {
                    this.selectedItems = [item];
                }
                const idList = this.selectedItems.map(_ => _.id);
                this.$emit('input', idList);
            },
        },
    };
</script>
