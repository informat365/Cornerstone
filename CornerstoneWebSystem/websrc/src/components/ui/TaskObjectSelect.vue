<style scoped lang="less">

    .check {
        color: #0097f7;
    }

    .list-nodata {
        text-align: center;
        padding-top: 10px;
        padding-bottom: 10px;
        color: #999;
    }

    .item-label {
        position: relative;
        max-width: 200px;
    }

    .tag-label-input {
        /deep/ input {
            padding: 0;
            outline: none !important;
            background: transparent !important;
            border: none !important;
            box-shadow: none !important;

            &:focus, &::selection, &::-moz-selection {
                outline: none !important;
                background: transparent !important;
                border: none !important;
                box-shadow: none !important;
            }
        }
    }

</style>
<i18n>
    {
    "en": {
    "未设置":"none",
    "暂无数据":"No Data"
    },
    "zh_CN": {
    "未设置":"未设置",
    "暂无数据":"暂无数据"
    }
    }
</i18n>
<template>

    <span>
        <span v-if="disabled==true">
                <span style="color:#111;font-weight:bold" v-if="selectedValue">{{selectedObject.name}}</span>
                <span class="placeholder-label" v-if="selectedValue==null||selectedValue==0">{{$t('未设置')}}</span>
        </span>

        <Poptip
            transfer
            v-if="disabled==null||disabled==false"
            class="poptip-full"
            v-model="visible"
            @on-popper-hide="handleHide"
            :placement="placement"
            style="text-align:left">
            <span class="tag-label popup-select" @click.stop="openFilter">
                <span v-if="selectedValue&&!filterable">{{selectedObject.name}}</span>
                <Input autofocus @on-clear="handleClear" @input="handleFilter" class="tag-label-input"
                       v-if="filterable" v-model.trim="searchKeyword"></Input>
                <span class="placeholder-label" v-if="(selectedValue==null||selectedValue==0)&&!filterable">{{$t('未设置')}}</span>
                <Icon v-if="!filterable" style="margin-left:10px" class="popup-select-arrow"
                      type="ios-arrow-down"></Icon>
            </span>
            <div slot="content" class="popup-box scrollbox">
                <div class="list-nodata" v-if="filteredDataList.length==0">{{$t('暂无数据')}}</div>
                <Row
                    @click.native="selectItem(item)"
                    class="popup-item-name"
                    v-for="item in filteredDataList"
                    :key="item.id">
                    <Col span="22">
                        <div
                            class="item-label" v-tooltip="item.name" v-text-overflow="item.name"></div>
                    </Col>
                    <Col span="2" style="text-align:right">
                            <Icon v-if="item.id==selectedValue" type="md-checkmark" class="check"/> &nbsp;
                        </Col>
                </Row>
            </div>
        </Poptip>
    </span>


</template>
<script>

    import {TextOverflow, Tooltip} from '../../assets/vue.directives';

    export default {
        name: 'TaskObjectSelect',
        props: ['placement', 'value', 'objectList', 'nodata', 'disabled'],
        directives: {
            TextOverflow,
            Tooltip,
        },
        data() {
            return {
                visible: false,
                selectedValue: this.value,
                objectValueList: [],
                filterable: false,
                searchKeyword: null,
                filteredDataList: [],
                beforeSelect:null
            };
        },
        mounted() {
            this.setupObjectValue();
        },
        watch: {
            objectList(val) {
                this.setupObjectValue();
            },
            selectedValue: function (val) {
                this.$emit('input', val);
            },
            visible: function (val) {
                if (val == false) {
                    this.$emit('change', this.selectedValue);
                }
            },
            value: function (val) {
                this.selectedValue = val;
            },
        },
        computed: {
            selectedObject: function () {
                if (this.filteredDataList == null) {
                    return {};
                }
                for (var i = 0; i < this.filteredDataList.length; i++) {
                    if (this.filteredDataList[i].id == this.selectedValue) {
                        return this.filteredDataList[i];
                    }
                }
                return {};
            },
            /*  filteredDataList: function () {
                  if (this.searchKeyword) {
                      if (this.objectValueList && this.objectValueList.length > 0) {
                          return this.objectValueList.filter(k => k.name.indexOf(this.searchKeyword) > -1);
                      }
                  } else {
                      return this.objectValueList;
                  }
                  return [];
              }*/
        },
        methods: {
            handleClear(){
                this.searchKeyword = null;
                this.handleFilter();
            },
            handleHide(){
                this.searchKeyword = null;
                this.filterable = false;
            },
            handleFilter() {
                if (this.searchKeyword) {
                    this.filteredDataList = this.objectValueList.filter(k => k.name.indexOf(this.searchKeyword) > -1);
                } else {
                    this.filteredDataList = this.objectValueList;
                }
            },
            openFilter() {
                this.filterable = true;
                this.visible = true;
                this.handleFilter();
                if (this.selectedObject&&!this.searchKeyword) {
                    this.searchKeyword = this.selectedObject.name;
                }

            },
            selectItem(item) {
                if (this.selectedValue == item.id) {
                    this.selectedValue = null;
                } else {
                    this.selectedValue = item.id;
                }
                // this.beforeSelect = this.select
                this.filterable = false;
                this.searchKeyword = null;
                this.visible = false;
            },
            setupObjectValue() {
                this.objectValueList = [];
                if (this.nodata) {
                    this.objectValueList.push({
                        id: -1,
                        name: '无',
                    });
                }
                if (this.objectList) {
                    for (var i = 0; i < this.objectList.length; i++) {
                        this.objectValueList.push(this.objectList[i]);
                    }
                }
                this.handleFilter();
            },
        },
    };
</script>
