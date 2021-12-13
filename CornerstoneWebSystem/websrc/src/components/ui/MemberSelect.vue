<style scoped>
    .member-label {
        font-size: 13px;
        vertical-align: middle;
        font-weight: bold;
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
    "搜索团队成员":"Search member",
    "主要":"Primary",
    "最多只能选择20个成员":"Only select up to 20 members"
    },
    "zh_CN": {
    "未设置标签":"未设置标签",
    "没有匹配的数据":"没有匹配的数据",
    "搜索团队成员":"搜索团队成员",
    "主要":"主要",
    "最多只能选择20个成员":"最多只能选择20个成员"
    }
    }
</i18n>
<template>
    <span>
     <span v-if="disabled==true" class="member-label">
            <AvatarImage
                v-for="accountId in selectedValue"
                :key="'member_'+accountId"
                size="small"
                :type="selectedValue.length>1?'tips':'label'"
                :value="getAccount(accountId).accountImageId"
                :name="getAccount(accountId).accountName" />
            <template v-if="selectedValue==null||selectedValue.length==0">
                <AvatarImage size="small" />
                <span class="vcenter placeholder-label" style="padding-left:7px">{{placeholder}}</span>
            </template>
    </span>
    <Poptip
        ref="poptip"
        transfer
        v-if="disabled==null||disabled==false"
        class="poptip-full"
        v-model="visible"
        :placement="placement">
        <span class="member-label popup-select">
            <AvatarImage
                v-for="accountId in selectedValue"
                :key="'member_'+accountId"
                size="small"
                :type="selectedValue.length>1?'tips':'label'"
                :value="getAccount(accountId).accountImageId"
                :name="getAccount(accountId).accountName" />
            <span v-if="selectedValue==null||selectedValue.length==0">
                <AvatarImage size="small" /> <span class="vcenter placeholder-label" style="padding-left:7px">{{placeholder}}</span>
            </span>
        </span>
        <div slot="content">
            <div style="padding:10px;padding-bottom:0"><Input
                v-model="searchText" :placeholder="$t('搜索团队成员')" icon="ios-search"></Input></div>
            <div class="list-nodata" v-if="filteredList.length==0">{{$t('没有匹配的数据')}}</div>
            <div class="popup-box scrollbox">
                <Row
                    @click.native="selectMember(item)"
                    class="popup-item-name"
                    v-for="item in filteredList"
                    :key="item.accountId">
                    <Col span="16"><AvatarImage
                        size="small" v-model="item.accountImageId" :name="item.accountName" type="label" /></Col>
                    <Col span="3" style="text-align:right"><Icon
                        v-if="isMember(item)" class="check" type="md-checkmark" /> &nbsp;</Col>
                    <Col @click.native.stop="selectMainOwner(item)" span="5" style="text-align:center">
                        <Icon v-if="item.accountId!=mainOwnerId" type="ios-radio-button-off" />
                        <span v-if="item.accountId==mainOwnerId" class="main-owner">{{$t('主要')}}</span>
                    </Col>
                </Row>
           </div>
           <div class="member-tags" v-if="memberTags && memberTags.length > 0">
               <template v-for="(item) in memberTags">
                   <Tag
                       :color="selectedMemberTags.indexOf(item) > -1 ? 'primary' : 'default'"
                       :key="item"
                       @click.native="onClickMemberTags(item)">{{ item }}</Tag>
               </template>
           </div>
        </div>
    </Poptip>

    </span>

</template>
<script>
    export default {
        name: 'MemberSelect',
        props: ['placement', 'value', 'memberList', 'valueList', 'multiple', 'disabled', 'placeholder'],
        data() {
            return {
                visible: false,
                searchText: null,
                selectedValue: null,
                mainOwnerId: null,
                theMemberList: [],
                selectedMemberTags: [],
            };
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
        },
        mounted() {
            this.setupValue();
        },
        computed: {
            filteredList() {
                var t = [];
                if (this.searchText == null || this.searchText == '') {
                    return this.theMemberList;
                }
                for (var i = 0; i < this.theMemberList.length; i++) {
                    var q = this.theMemberList[i];
                    if (q.accountName.indexOf(this.searchText) != -1
                        || q.accountUserName.indexOf(this.searchText) != -1
                        || (q.accountPinyinName&&q.accountPinyinName.indexOf(this.searchText) != -1)) {
                        t.push(q);
                    }
                }
                return t;
            },
            memberTags() {
                if (!Array.isArray(this.theMemberList) || this.theMemberList.length === 0) {
                    return [];
                }
                const tags = [];
                this.theMemberList.forEach(item => {
                    if (!Array.isArray(item.tag)) {
                        return;
                    }
                    item.tag.forEach(tag => {
                        if (tags.indexOf(tag) > -1) {
                            return;
                        }
                        tags.push(tag);
                    });
                });
                if (tags.length === 0) {
                    return tags;
                }
                tags.push(this.$t('未设置标签'));
                return tags;
            },
        },
        methods: {
            hideView() {
                if (this.visible) {
                    this.emitEvents();
                }
            },
            confirm() {
                this.emitEvents();
            },
            emitEvents() {
                if (!this.isSameArray(this.selectedValue, this.value)) {
                    this.$emit('input', this.selectedValue);
                    this.$emit('change', this.selectedValue);
                }
            },
            onClickMemberTags(tag) {
                const index = this.selectedMemberTags.indexOf(tag);
                const memberList = this.theMemberList.filter(item => {
                    if (tag === this.$t('未设置标签') && !Array.isArray(item.tag)) {
                        return true;
                    }
                    if (!Array.isArray(item.tag)) {
                        return false;
                    }
                    return item.tag.indexOf(tag) > -1;
                });
                if (index > -1) {
                    // 从当前选择的用户中移除当前选中的标签用户
                    memberList.filter(item => {
                        if (!Array.isArray(this.selectedValue)) {
                            return false;
                        }
                        return this.selectedValue.indexOf(item.accountId) > -1;
                    }).forEach(item => {
                        this.selectMember(item);
                    });
                    this.selectedMemberTags.splice(index, 1);
                } else {
                    // 从当前选择的用户中追加当前选中的标签用户
                    const changeList = memberList.filter(item => {
                        if (!Array.isArray(this.selectedValue)) {
                            return true;
                        }
                        return this.selectedValue.indexOf(item.accountId) === -1;
                    });
                    const selectedLen = this.selectedValue ? this.selectedValue.length : 0;
                    if (changeList.length + selectedLen > 20) {
                        app.toast(this.$t('最多只能选择20个成员'));
                        return;
                    }
                    changeList.forEach(item => {
                        this.selectMember(item);
                    });
                    this.selectedMemberTags.push(tag);
                }
            },
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
            containsAccount(id) {
                for (var i = 0; i < this.theMemberList.length; i++) {
                    if (this.theMemberList[i].accountId == id) {
                        return true;
                    }
                }
                return false;
            },
            setupValue() {
                this.theMemberList = [];
                if (this.memberList) {
                    for (var i = 0; i < this.memberList.length; i++) {
                        this.theMemberList.push(this.memberList[i]);
                    }
                }
                if (this.valueList) {
                    for (var j = 0; j < this.valueList.length; j++) {
                        var t = this.valueList[j];
                        if (!this.containsAccount(t.id)) {
                            this.theMemberList.push({
                                accountId: t.id,
                                accountName: t.name,
                                accountImageId: t.imageId,
                            });
                        }
                    }
                }
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
                // 设置当前选择的标签
                const selectedMemberTags = [];
                this.theMemberList.filter(item => this.selectedValue.indexOf(item.accountId) > -1).forEach(item => {
                    let tags = item.tag;
                    if (!Array.isArray(tags)) {
                        tags = [this.$t('未设置标签')];
                    }
                    tags.forEach(tag => {
                        if (selectedMemberTags.indexOf(tag) > -1) {
                            return;
                        }
                        selectedMemberTags.push(tag);
                    });
                });
                this.selectedMemberTags = selectedMemberTags;
                if (this.value != null && this.value.length > 0) {
                    this.mainOwnerId = this.value[0];
                } else {
                    this.mainOwnerId = 0;
                }
            },
            getAccount(accountId) {
                for (var i = 0; i < this.theMemberList.length; i++) {
                    if (this.theMemberList[i].accountId == accountId) {
                        return this.theMemberList[i];
                    }
                }
                return {};
            },
            isMember(item) {
                if (this.selectedValue == null) {
                    return false;
                }
                for (var i = 0; i < this.selectedValue.length; i++) {
                    if (this.selectedValue[i] == item.accountId) {
                        return true;
                    }
                }
                return false;
            },
            selectMainOwner(item) {
                this.mainOwnerId = item.accountId;
                var contains = false;
                for (var i = 0; i < this.selectedValue.length; i++) {
                    if (this.selectedValue[i] == item.accountId) {
                        contains = true;
                    }
                }
                if (!contains) {
                    this.selectedValue.push(item.accountId);
                }
                this.reOrderSelectValue();
            },
            selectMember(item) {
                if (this.selectedValue == null) {
                    this.selectedValue = [];
                }
                for (var i = 0; i < this.selectedValue.length; i++) {
                    if (this.selectedValue[i] == item.accountId) {
                        this.selectedValue.splice(i, 1);
                        if (this.mainOwnerId == item.accountId) {
                            this.mainOwnerId = 0;
                            if (this.selectedValue.length > 0) {
                                this.mainOwnerId = this.selectedValue[0];
                                this.reOrderSelectValue();
                            }
                        }
                        return;
                    }
                }
                if (this.selectedValue.length >= 20) {
                    app.toast(this.$t('最多只能选择20个成员'));
                    return;
                }
                this.selectedValue.push(item.accountId);
                if (this.mainOwnerId == 0 || this.mainOwnerId == null) {
                    this.mainOwnerId = item.accountId;
                    this.reOrderSelectValue();
                }
            },
            reOrderSelectValue() {
                var list = [];
                for (var i = 0; i < this.selectedValue.length; i++) {
                    if (this.selectedValue[i] == this.mainOwnerId) {
                        list.push(this.selectedValue[i]);
                        break;
                    }
                }
                //
                for (var i = 0; i < this.selectedValue.length; i++) {
                    if (this.selectedValue[i] != this.mainOwnerId) {
                        list.push(this.selectedValue[i]);
                    }
                }
                this.selectedValue = list;
            },
        },
    };
</script>
