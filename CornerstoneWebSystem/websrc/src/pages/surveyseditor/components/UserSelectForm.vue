<style scoped>
.page {
    display: flex;
    align-items: center;
    background-color: #f1f4f5;
    padding: 10px;
    flex-direction: column;
}
.page-form {
    width: 700px;
    background-color: #fff;
    border: 1px solid #eee;
    border-radius: 3px;
    padding: 30px 20px;
}
.form-desc {
    font-size: 12px;
    color: #666;
    margin-bottom: 5px;
}
.form-value-tag-select {
    padding-top: 0px;
}
.choose-tag {
    margin-right: 5px;
    margin-bottom: 5px;
    margin-top:5px;
}
</style>
<i18n>
{
	"en": {
        "提交人": "提交人",
		"成员": "成员",
		"添加成员": "添加成员",
		"部门成员": "部门成员",
		"添加部门": "添加部门",
		"部门负责人": "部门负责人",
		"企业角色": "企业角色",
		"添加角色": "添加角色",
		"项目角色": "项目角色",
		"表单值": "表单值",
		"表单中填写的用户角色部门": "表单中填写的用户、角色、部门"
    },
	"zh_CN": {
		"提交人": "提交人",
		"成员": "成员",
		"添加成员": "添加成员",
		"部门成员": "部门成员",
		"添加部门": "添加部门",
		"部门负责人": "部门负责人",
		"企业角色": "企业角色",
		"添加角色": "添加角色",
		"项目角色": "项目角色",
		"表单值": "表单值",
		"表单中填写的用户角色部门": "表单中填写的用户、角色、部门"
	}
}
</i18n>
<template>
    <Form :model="formItem" :label-width="80">
        <FormItem :label="$t('提交人')">
            <div>
                <i-Switch @on-change="fireChanged" v-model="formItem.submitter"></i-Switch>
            </div>
        </FormItem>
        <FormItem :label="$t('成员')">
            <div class="form-value-tag-select">
                <ColorTag
                    class="choose-tag"
                    v-for="item in formItem.userList"
                    :key="'o'+item.id"
                    :closable="true"
                    @on-close="removeFromList(formItem.userList,item)"
                >{{item.name}}</ColorTag>
                <Button icon="ios-add" type="dashed" size="small" @click="selectUser">{{$t('添加成员')}}</Button>
            </div>
        </FormItem>
        <FormItem :label="$t('部门成员')">
            <div class="form-value-tag-select">
                <ColorTag
                    class="choose-tag"
                    v-for="item in formItem.departmentList"
                    :key="'o'+item.id"
                    :closable="true"
                    @on-close="removeFromList(formItem.departmentList,item)"
                >{{item.name}}</ColorTag>
                <Button icon="ios-add" type="dashed" size="small" @click="selectDepartment">{{$t('添加部门')}}</Button>
            </div>
        </FormItem>

        <FormItem :label="$t('部门负责人')">
            <ColorTag
                v-for="item in formItem.departmentOwnerList"
                    :key="'od'+item.id"
                    :closable="true" @on-close="removeFromList(formItem.departmentOwnerList,item)">{{item.name}}</ColorTag>
            <Button icon="ios-add" type="dashed" size="small" @click="selectDepartmentOwner()">{{$t('添加部门')}}</Button>
        </FormItem>

        <FormItem :label="$t('企业角色')">
            <div class="form-value-tag-select">
                <ColorTag
                    class="choose-tag"
                    v-for="item in formItem.companyRoleList"
                    :key="'cr'+item.id"
                    :closable="true"
                    @on-close="removeFromList(formItem.companyRoleList,item)"
                >{{item.name}}</ColorTag>
                <Button icon="ios-add" type="dashed" size="small" @click="selectCompanyRole">{{$t('添加角色')}}</Button>
            </div>
        </FormItem>
        <FormItem :label="$t('项目角色')">
            <div class="form-value-tag-select">
                <ColorTag
                    class="choose-tag"
                    v-for="item in formItem.projectRoleList"
                    :key="'pr'+item.id"
                    :closable="true"
                    @on-close="removeFromList(formItem.projectRoleList,item)"
                >{{item.name}}</ColorTag>
                <Button icon="ios-add" type="dashed" size="small" @click="selectProjectRole()">{{$t('添加角色')}}</Button>
            </div>
        </FormItem>

        <FormItem :label="$t('表单值')">
            <div class="form-desc">{{$t('表单中填写的用户角色部门')}}</div>
                <div>
                    <Select @on-change="fireChanged" transfer multiple v-model="formItem.formItemList">
                        <Option v-for="item in ownerFieldList" :value="item.id" :key="item.id">{{item.name}}</Option>
                    </Select>
                </div>
        </FormItem>
        
    </Form>
</template>

<script>
export default {
    props: ["value",'ownerFieldList'],
    data() {
        return {
            formItem: {
                submitter:false,
                userList: [],
                companyRoleList: [],
                projectRoleList: [],
                departmentList: [],
                departmentOwnerList:[],
                formItemList:[]
            }
        };
    },
    watch: {
        value(val) {
            this.setupValue(val);
        }
    },
    mounted() {
        this.setupValue(this.value);
    },
    methods: {
        concatList(list,addList){
            if(addList==null||addList==undefined){
                return;
            }
            if(list.length>0){
                list.splice(0,list.length)
            }
            addList.forEach(item=>{
                list.push(item)
            })
        },
        setupValue(value){
            this.formItem.submitter=false;
            this.formItem.userList=[];
            this.formItem.companyRoleList=[];
            this.formItem.projectRoleList=[];
            this.formItem.departmentList=[];
            this.formItem.formItemList=[];
            if(value==null){   
                return;
            }
            this.formItem.submitter=value.submitter;
            this.concatList(this.formItem.userList,value.userList)
            this.concatList(this.formItem.companyRoleList,value.companyRoleList)
            this.concatList(this.formItem.projectRoleList,value.projectRoleList)
            this.concatList(this.formItem.departmentList,value.departmentList)
            this.concatList(this.formItem.departmentOwnerList,value.departmentOwnerList)
            this.concatList(this.formItem.formItemList,value.formItemList)
        },
        fireChanged() {
            var copy=JSON.parse(JSON.stringify(this.formItem))
            this.$emit("input",copy);
        },
        addToList(list, item) {
            for (var i = 0; i < list.length; i++) {
                var t = list[i];
                if (t.id == item.id) {
                    return;
                }
            }
            list.push(item);
            this.fireChanged();
        },
        removeFromList(list, item) {
            for (var i = 0; i < list.length; i++) {
                var t = list[i];
                if (t.id == item.id) {
                    list.splice(i, 1);
                    this.fireChanged();
                    return;
                }
            }
        },
        selectUser() {
            app.showDialog(MemberSelectDialog, {
                callback: list => {
                    list.forEach(item => {
                        var user = {
                            id: item.accountId,
                            name: item.title
                        };
                        this.addToList(this.formItem.userList, user);
                    });
                }
            });
        },
        selectDepartment() {
            app.showDialog(DepartmentSelectDialog, {
                callback: list => {
                    list.forEach(item => {
                        var user = {
                            id: item.id,
                            name: item.title
                        };
                        this.addToList(this.formItem.departmentList, user);
                    });
                }
            });
        },
        selectDepartmentOwner() {
            app.showDialog(DepartmentSelectDialog, {
                callback: list => {
                    list.forEach(item => {
                        var user = {
                            id: item.id,
                            name: item.title
                        };
                        this.addToList(this.formItem.departmentOwnerList, user);
                    });
                }
            });
        },
        selectCompanyRole() {
            app.showDialog(RoleSelectDialog, {
                roleType: 2,
                callback: list => {
                    for (var i = 0; i < list.length; i++) {
                        var t = list[i];
                        var item = {
                            id: t.id,
                            name: t.name
                        };
                        this.addToList(this.formItem.companyRoleList, item);
                    }
                }
            });
        },
        selectProjectRole() {
            app.showDialog(RoleSelectDialog, {
                roleType: 1,
                callback: list => {
                    for (var i = 0; i < list.length; i++) {
                        var t = list[i];
                        var item = {
                            id: t.id,
                            name: t.name
                        };
                        this.addToList(this.formItem.projectRoleList, item);
                    }
                }
            });
        }
    }
};
</script>