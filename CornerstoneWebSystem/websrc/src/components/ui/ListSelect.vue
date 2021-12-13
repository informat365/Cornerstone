<style scoped>
    .member-label {
        font-size: 13px;
        vertical-align: middle;
        font-weight: bold;
        width: 150px;
        border: 1px solid #ddd;
        height: 32px;
        line-height: 32px;
        /*margin-left: 15px;*/
        overflow: hidden;
    }

    .member-label-tag {
        margin-left: 5px;
    }

    .vcenter-icom {
        position: absolute;
        top: 10px;
        right: 6px;
    }

    .check {
        color: #0097f7;
    }

    .main-owner {
        font-size: 13px;
        color: #999;
    }

    .list-nodata {
        padding: 10px;
        color: #999;
        text-align: center;
    }

    .popup-box {
        max-height: 320px;
    }

    .member-tags {
        padding: 5px;
        box-shadow: 0 0 4px 0 rgba(0, 0, 0, 0.1);
    }

</style>
<i18n>
    {
    "en": {
    "未设置标签":"No labels set",
    "没有匹配的数据":"No matching user",
    "搜索":"Search",
    "主要":"Primary",
    "最多只能选择20个成员":"Only select up to 20 members"
    },
    "zh_CN": {
    "未设置标签":"未设置标签",
    "没有匹配的数据":"没有匹配的数据",
    "搜索":"搜索",
    "主要":"主要",
    "最多只能选择20个成员":"最多只能选择20个成员"
    }
    }
</i18n>
<template>
    <Poptip
            ref="poptip"
            transfer
            class="poptip-full"
            v-model="visible"
            :placement="placement">
        <span class="member-label popup-select" :title="selectNames">
            <span
                    class="member-label-tag"
                    v-for="id in selectedValue"
                    :key="'member_'+id"
            >{{getItem(id).name}}</span>
            <span v-if="selectedValue==null||selectedValue.length==0">
                <span class="vcenter placeholder-label" style="padding-left:7px">{{placeholder}}</span>
            </span>
             <Icon color="#ddd" class="vcenter-icom" v-if="selectedValue==null||selectedValue.length==0" type="ios-search"/>
        </span>
        <div slot="content">
            <div style="padding:10px;padding-bottom:0">
                <Input
                        v-model="searchText" :placeholder="$t('搜索')" icon="ios-search"></Input></div>
            <div class="list-nodata" v-if="filteredList.length==0">{{$t('没有匹配的数据')}}</div>
            <div class="popup-box scrollbox">
                <Row
                        @click.native="selectItem(item)"
                        class="popup-item-name"
                        v-for="item in filteredList"
                        :key="item.id">
                    <Col span="21">
                        <span>{{item.name||item.title}}</span></Col>
                    <Col span="3" style="text-align:right">
                        <Icon
                                v-if="isChecked(item)" class="check" type="md-checkmark"/> &nbsp;
                    </Col>
                </Row>
            </div>
        </div>
    </Poptip>
</template>
<script>
    export default {
        name: 'ListSelect',
        props: ['placement', 'value', 'dataList', 'multiple', 'disabled', 'placeholder'],
        data() {
            return {
                visible: false,
                searchText: null,
                selectedValue: null,
                theDataList: [],
            }
        },
        watch: {
            visible: function (val) {
                if (val == false) {
                    this.emitEvents();
                }
            },
            value: function (val) {
                this.setupValue();
            },
            dataList: function (val) {
                this.setupDataList();

            }
        },
        mounted() {
            this.setupValue();
        },
        computed: {
            filteredList() {
                var t = [];
                if (this.searchText == null || this.searchText == '') {
                    return this.theDataList;
                }
                for (var i = 0; i < this.theDataList.length; i++) {
                    var q = this.theDataList[i];
                    var name = q.title || q.name;
                    if (name.indexOf(this.searchText) != -1) {
                        t.push(q);
                    }
                }
                return t;
            },
            selectNames(){
                var t=[];
                if(Array.isArray(this.selectedValue)){
                    for (let i = 0; i < this.selectedValue.length; i++) {
                        t.push(this.getItem(this.selectedValue[i]).name)
                    }
                }
                return t.join("、");
            }
        }
        ,
        methods: {
            emitEvents() {
                if (!this.isSameArray(this.selectedValue, this.value)) {
                    this.$emit('input', this.selectedValue);
                    this.$emit('on-change', this.selectedValue);
                }
            }
            ,
            isSameArray(a, b) {
                if (a == null || b == null) {
                    return false;
                }
                if (a.length != b.length) {
                    return false;
                }
                for (var i = 0; i < a.length; i++) {
                    if (a[i] != b[i]) {
                        return false;
                    }
                }
                return true;
            },
            setupDataList(){
                this.theDataList = [];
                if (this.dataList) {
                    for (var i = 0; i < this.dataList.length; i++) {
                        this.theDataList.push(this.dataList[i]);
                    }
                }
            },
            setupValue() {
                this.selectedValue = [];
                if (this.multiple == true) {
                    if (this.value != null) {
                        for (var i = 0; i < this.value.length; i++) {
                            this.selectedValue.push(this.value[i]);
                        }
                    }
                } else {
                    if (this.value != null) {
                        this.selectedValue.push(this.value);
                    }
                }
            }
            ,
            getItem(id) {
                for (var i = 0; i < this.theDataList.length; i++) {
                    if (this.theDataList[i].id == id) {
                        return this.theDataList[i];
                    }
                }
                return {};
            }
            ,
            isChecked(item) {
                if (this.selectedValue == null) {
                    return false;
                }
                for (var i = 0; i < this.selectedValue.length; i++) {
                    if (this.selectedValue[i] == item.id) {
                        return true;
                    }
                }
                return false;
            },
            selectItem(item) {
                if (this.selectedValue == null) {
                    this.selectedValue = [];
                }
                for (var i = 0; i < this.selectedValue.length; i++) {
                    if (this.selectedValue[i] == item.id) {
                        this.selectedValue.splice(i, 1);
                        return;
                    }
                }
                if (this.selectedValue.length >= 20) {
                    app.toast(this.$t('最多只能选择20个'));
                    return;
                }
                this.selectedValue.push(item.id);
            }
        }
    }
</script>
