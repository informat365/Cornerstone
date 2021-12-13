<i18n>
  {
  "en":{
  "清除所有":"Clear all",
  "清除":"Clear",
  "找不到匹配的数据":"No matching data found",
  "搜索部门":"Search department"
  },
  "zh_CN": {
  "清除所有":"清除所有",
  "清除":"清除",
  "找不到匹配的数据":"找不到匹配的数据",
  "搜索部门":"搜索部门"
  }
  }
</i18n>
<template>
  <treeselect
    class="department-select"
    v-model="modelValue"
    :clear-all-text="$t('清除所有')"
    :clear-value-text="$t('清除')"
    :no-results-text="$t('找不到匹配的数据')"
    :search-prompt-text="$t('搜索部门')"
    :placeholder="$t('搜索部门')"
    no-options-text=""
    value-format="id"
    :match-keys='["pinyin","label"]'
    show-count
    searchable
    :clearable="clearable"
    value-consists-of="LEAF_PRIORITY"
    :max-height="250"
    :default-expand-level="1"
    :multiple="multiple"
    :flat="multiple"
    :options="departmentList"
    @select="onSelect"
    @deselect="onDeSelect">
    <label
      slot="option-label"
      slot-scope="{ node, shouldShowCount, count, labelClassName, countClassName }"
      :class="labelClassName">
      <Icon
        v-if="node.raw.type===1" type="ios-people" />
      <Icon
        v-if="node.raw.type===2" type="md-person" />
      {{ node.label }}
      <span
        v-if="shouldShowCount" :class="countClassName">({{ count }})</span>
    </label>
  </treeselect>
</template>

<script>
  import Treeselect from '@riophae/vue-treeselect';
  import '@riophae/vue-treeselect/dist/vue-treeselect.css';

  export default {
    name: 'DepartmentTreeSelect',
    components: {
      Treeselect,
    },
    props: {
      value: {
        type: [String, Number, Array],
      },
      multiple: {
        type: Boolean,
        default: false,
      },
      clearable: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        departmentList: [],
        modelValue: null,
        selectNodes: [],
      };
    },
    watch: {
      value: {
        immediate: true,
        handler(val) {
          this.modelValue = val;
        },
      },
      modelValue(val) {
        this.$emit('input', val);
        setTimeout(() => {
          this.$emit('on-change', val);
        }, 0);
      },
      selectNodes(val) {
        let value = val;
        if (Array.isArray(val)) {
          if (!this.multiple) {
            value = val[0];
          }
        }
        setTimeout(() => {
          this.$emit('on-select-change', value);
        }, 0);
      },
    },
    created() {
      this.getDepartmentList();
    },
    methods: {
      getDepartmentList() {
        app.invoke('BizAction.getDepartmentTree', [app.token, false], (info) => {
          travalTree(info[0], (item) => {
            item.label = item.title;
            if (item.children.length === 0) {
              delete item.children;
            }
          });
          this.departmentList = info;
        });
      },
      onSelect(node) {
        const index = this.selectNodes.findIndex(item => item.id === node.id);
        if (index > -1) {
          return;
        }
        this.selectNodes.push({
          ...node,
        });
      },
      onDeSelect(node) {
        const index = this.selectNodes.findIndex(item.id === node.id);
        if (index === -1) {
          return;
        }
        this.selectNodes.splice(index, 1);
      },
    },
  };
</script>

<style lang="less" scoped>
  .department-select {
    /deep/ .vue-treeselect__control {
      height: 32px;
      border-radius: 0;

      .vue-treeselect__placeholder {
        line-height: 32px;
      }
    }

    /deep/ .vue-treeselect__menu-container {
      .vue-treeselect__menu {
        display: block;
        overflow-x: auto !important;
        max-width: 300px;

        /deep/ .vue-treeselect__label{
          display: inline-block !important;
          text-overflow: inherit !important;
          overflow: inherit !important;
        }

        &::-webkit-scrollbar {
          width: 4px;
          height: 6px;
          background-color: transparent;
          -ms-overflow-style: -ms-autohiding-scrollbar;
        }

        &::-webkit-scrollbar-track {
          border-radius: 0;
          background-color: transparent;
        }

        &::-webkit-scrollbar-thumb {
          border-radius: 0;
          background-color: #ddd;
        }
      }
    }
  }
</style>
