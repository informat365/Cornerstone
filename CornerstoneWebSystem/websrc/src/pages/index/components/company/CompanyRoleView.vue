<style lang="less" scoped>
    .admin-page {
        position: relative;
        display: flex;
        flex-direction: column;
        height: calc(100vh - 50px);
        padding: 15px 30px;
        width: 100%;
        overscroll-behavior: contain;
        overflow-scrolling: touch;
        scroll-behavior: smooth;
        overflow: hidden;
    }

    .admin-page-actions {
        height: 50px;
    }

    .admin-page-wrap {
        flex: 1;
        display: flex;
        flex-direction: column;
        overflow: hidden;
        border: solid 1px #eee;
    }

    .permission-header {
        position: relative;
        height: 48px;
        display: flex;
        overflow: hidden;
        border-bottom: 1px solid #eee;
    }

    .permission-content {
        flex: 1;
        display: flex;
        overflow: hidden;
    }

    .permission-header,
    .permission-content {
        .permission-left {
            width: 180px;
            overflow: hidden;
        }

        .permission-right {
            flex: 1;
            overflow: hidden;
        }
    }

    .permission-content {
        .permission-right {
            overflow: auto;
        }
    }

    td {
        height: 60px;
    }

    .sub-permission {
        padding-left: 15px;
        font-weight: 500;
    }

    .role-table td {
        width: 100px;
        text-align: center;
    }

    .check {
        color: #0097f7;
    }

    .check-icon {
        font-size: 20px;
    }

    .permission-row {
        width: 100%;
        overflow: auto;
        border-bottom: 1px solid #eee;
    }

    .permission-group {
        color: #666;
        border-bottom: 1px solid #eee;
        user-select: none;
        background-color: #f9f9f9;
    }

    .permission-cell {
        display: inline-flex;
        align-content: center;
        align-items: center;
        width: 180px;
        padding: 0 15px;
        height: 48px;
        user-select: none;
        vertical-align: top;
        border-right: 1px solid #eee;
    }

    .permission-cell-center {
        justify-content: center;
    }

    .permission-name {
        width: 180px;
        cursor: pointer;
        padding-left: 10px;
        font-weight: bold;
    }
</style>
<i18n>
    {
    "en": {
    "保存":"Save",
    "创建角色":"Create role",
    "编辑":"Edit",
    "项目的团队成员将会被赋予的角色，这些角色用于控制成员在项目中的权限":"The team members of the project will be assigned roles that are used to control the permissions of members in the project.",
    "这些角色用于控制成员的全局权限":" These roles are used to control the global permissions of members",
    "权限":"Permissions",
    "全部展开":" Expand all",
    "全部收起":" Collapse all",
    "系统":"System",
    "保存成功":"Save success"
    },
    "zh_CN": {
    "保存":"保存",
    "创建角色":"创建角色",
    "编辑":"编辑",
    "项目的团队成员将会被赋予的角色，这些角色用于控制成员在项目中的权限":"项目的团队成员将会被赋予的角色，这些角色用于控制成员在项目中的权限",
    "这些角色用于控制成员的全局权限":"这些角色用于控制成员的全局权限",
    "权限":"权限",
    "全部展开":"全部展开",
    "全部收起":"全部收起",
    "系统":"系统",
    "保存成功":"保存成功"
    }
    }
</i18n>
<template>
    <div class="admin-page">
        <div ref="adminPageActions" class="admin-page-actions">
            <Button
                @click="showCreateDialog()"
                type="default"
                icon="md-add"
                class="mr15"
            >{{ $t("创建角色") }}</Button
            >
            <Button :type="edit ? 'success' : 'default'" @click="clickEdit">{{
                edit ? $t("保存") : $t("编辑")
                }}</Button>
            <span v-if="type === 1" style="color:#999;padding-left:20px">
                {{
                    $t(
                        "项目的团队成员将会被赋予的角色，这些角色用于控制成员在项目中的权限"
                    )
                }}
            </span>
            <span v-if="type === 2" style="color:#999;padding-left:20px">
                {{ $t("这些角色用于控制成员的全局权限") }}
            </span>
        </div>
        <div v-if="permissionList.length > 0" class="admin-page-wrap">
            <div class="permission-header">
                <div class="permission-left">
                    <div class="permission-row">
                        <div
                            @click="toggleAll()"
                            class="permission-cell permission-name"
                        >
                            <div>{{ $t("权限") }}</div>
                            <div
                                style="font-size:12px;color:#999;margin-left:5px"
                                v-if="isShowAll == false"
                            >
                                {{ $t("全部展开") }}
                            </div>
                            <div
                                style="font-size:12px;color:#999;margin-left:5px"
                                v-if="isShowAll"
                            >
                                {{ $t("全部收起") }}
                            </div>
                        </div>
                    </div>
                </div>
                <div ref="permissionRightHeader" class="permission-right">
                    <div
                        class="permission-row"
                        :style="permissionHeaderWidthStyle"
                    >
                        <div
                            class="permission-cell permission-cell-center"
                            v-for="item in roleList"
                            :key="item.id"
                        >
                            <IconButton
                                v-if="false === item.isSystemRole"
                                icon="ios-settings-outline"
                                @click="showCreateDialog(item)"
                            ></IconButton>
                            <div
                                style="color:#999;font-size:12px;margin-right:5px"
                                v-else
                            >
                                {{ $t("系统") }}
                            </div>
                            <div style="text-align:center;">
                                {{ item.name }}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="permission-content">
                <div ref="permissionLeftContent" class="permission-left">
                    <div
                        v-for="permission in permissionList"
                        :key="'permission_' + permission.name"
                        class="permission-row"
                    >
                        <div class="permission-group">
                            <div
                                @click="permission.open = !permission.open"
                                class="permission-cell  permission-name"
                            >
                                {{ permission.name }}
                                <Icon
                                    v-if="permission.open === false"
                                    type="ios-arrow-forward"
                                />
                                <Icon
                                    v-if="permission.open"
                                    type="ios-arrow-down"
                                />
                            </div>
                        </div>
                        <template v-for="pitem in permission.children">
                            <div
                                v-if="permission.open"
                                :key="pitem.id"
                                class="permission-row"
                            >
                                <div
                                    class="permission-cell permission-cell-center permission-name"
                                    style="padding-left:15px;"
                                >
                                    {{ pitem.name }}
                                </div>
                            </div>
                        </template>
                    </div>
                    <div style="height: 20px;"></div>
                </div>
                <div class="permission-right scrollbox" @scroll="handleScroll">
                    <div
                        v-for="permission in permissionList"
                        :key="'permission_' + permission.name"
                        class="permission-row"
                        :style="permissionWidthStyle"
                    >
                        <div class="permission-group">
                            <div
                                @click="selectAllRole(permission, item)"
                                style="text-align:center;"
                                class="permission-cell permission-cell-center"
                                v-for="item in roleList"
                                :key="item.id"
                            >
                                <template v-if="edit">
                                    <Icon
                                        v-if="isSelectAllRole(permission, item)"
                                        type="md-checkmark-circle"
                                        class="check-icon check"
                                    />
                                    <Icon
                                        v-if="
                                            isSelectAllRole(
                                                permission,
                                                item
                                            ) !== true
                                        "
                                        class="check-icon"
                                        type="md-radio-button-off"
                                    />
                                </template>
                                <template v-if="edit === false">
                                    <Icon
                                        v-if="isSelectAllRole(permission, item)"
                                        type="md-checkmark"
                                        class="check-icon check"
                                    />
                                </template>
                            </div>
                        </div>
                        <template v-for="pitem in permission.children">
                            <div
                                v-if="permission.open"
                                :key="pitem.id"
                                class="permission-row"
                            >
                                <div
                                    @click="
                                        togglePermission(
                                            role,
                                            pitem,
                                            permission
                                        )
                                    "
                                    v-for="role in roleList"
                                    :key="role.id + '-' + pitem.id"
                                    style="text-align:center;"
                                    class="permission-cell-center permission-cell ivu-icon check-icon"
                                    :class="getCellClass(role, pitem)"
                                ></div>
                            </div>
                        </template>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import { off, on } from "../../../../assets/vue.assist";

    export default {
        mixins: [componentMixin],
        props: ["type"],
        data() {
            return {
                permissionList: [],
                roleList: [],
                edit: false,
                isShowAll: false,
                pageWidth: 0
            };
        },
        computed: {
            permissionWidthStyle() {
                if (!Array.isArray(this.roleList) || this.roleList.length === 0) {
                    return {
                        width: `${this.pageWidth}px`
                    };
                }
                const width = (this.roleList.length + 1) * 180 + 40;
                if (width < this.pageWidth) {
                    return {
                        width: `${this.pageWidth}px`
                    };
                }
                return {
                    width: `${width}px`
                };
            },
            permissionHeaderWidthStyle() {
                if (!Array.isArray(this.roleList) || this.roleList.length === 0) {
                    return {
                        width: `${this.pageWidth + 20}px`
                    };
                }
                const width = (this.roleList.length + 1) * 180 + 60;
                if (width < this.pageWidth) {
                    return {
                        width: `${this.pageWidth + 20}px`
                    };
                }
                return {
                    width: `${width + 20}px`
                };
            }
        },
        beforeDestroy() {
            off(window, "resize", this.refreshPageWidth);
        },
        created() {
            on(window, "resize", this.refreshPageWidth);
        },
        mounted() {
            this.loadData();
            this.refreshPageWidth();
        },
        methods: {
            getCellClass(role, pitem) {
                const hasPermission = this.hasPermission(role, pitem);
                const classList = [];
                if (hasPermission) {
                    classList.push("check");
                    if (this.edit) {
                        classList.push("ivu-icon-md-checkmark-circle");
                    } else {
                        classList.push("ivu-icon-md-checkmark");
                    }
                } else if (this.edit) {
                    classList.push("ivu-icon-md-radio-button-off");
                }
                return classList;
            },
            refreshPageWidth() {
                this.pageWidth = this.$refs.adminPageActions.getBoundingClientRect().width;
            },
            pageMessage(type) {
                if (type == "role.edit") {
                    this.loadRoles();
                }
            },
            handleScroll(event) {
                this.$refs.permissionRightHeader.scrollLeft =
                    event.target.scrollLeft;
                this.$refs.permissionLeftContent.scrollTop = event.target.scrollTop;
            },
            loadData() {
                app.invoke(
                    "BizAction.getPermissionList",
                    [app.token, this.type],
                    list => {
                        for (let i = 0; i < list.length; i++) {
                            list[i].open = false;
                        }
                        this.permissionList = list;
                        this.loadRoles();
                    }
                );
            },
            loadRoles() {
                app.invoke(
                    "BizAction.getRoleInfoList",
                    [app.token, this.type],
                    list => {
                        this.roleList = list;
                    }
                );
            },
            selectAllRole(permission, role) {
                if (this.edit == false) {
                    return;
                }
                if (this.isSelectAllRole(permission, role)) {
                    this.removeSubPermission(role, permission);
                    return;
                }
                this.addSubPermission(role, permission);
            },
            isSelectAllRole(permission, role) {
                const childrenIds = permission.children.map(item => item.id);
                const rolePermissionIds = role.permissionIds.filter(
                    item => childrenIds.indexOf(item) >= 0
                );
                return childrenIds.length === rolePermissionIds.length;
            },
            toggleAll() {
                this.isShowAll = !this.isShowAll;
                for (let i = 0; i < this.permissionList.length; i++) {
                    const permission = this.permissionList[i];
                    permission.open = this.isShowAll;
                }
            },
            clickEdit() {
                if (this.edit) {
                    this.edit = false;
                    this.savePermissionList();
                } else {
                    this.edit = true;
                }
            },
            togglePermission(role, permission, parentPermission) {
                if (this.edit == false) {
                    return;
                }
                for (let i = 0; i < role.permissionIds.length; i++) {
                    if (role.permissionIds[i] == permission.id) {
                        role.permissionIds.splice(i, 1);
                        return;
                    }
                }
                role.permissionIds.push(permission.id);
            },
            addSubPermission(role, permission) {
                const childrenIds = permission.children.map(item => item.id);
                childrenIds.forEach(item => {
                    if (role.permissionIds.indexOf(item) < 0) {
                        role.permissionIds.push(item);
                    }
                });
            },
            removeSubPermission(role, permission) {
                const childrenIds = permission.children.map(item => item.id);
                role.permissionIds = role.permissionIds.filter(
                    item => childrenIds.indexOf(item) < 0
                );
            },
            hasPermission(role, permission) {
                return role.permissionIds.indexOf(permission.id) >= 0;
            },
            showCreateDialog(item) {
                app.showDialog(window.CompanyRoleCreateDialog, {
                    type: this.type,
                    item: item
                });
            },
            savePermissionList() {
                app.invoke(
                    "BizAction.saveRoleInfoList",
                    [app.token, this.roleList],
                    list => {
                        app.toast(this.$t("保存成功"));
                    }
                );
            }
        }
    };
</script>
