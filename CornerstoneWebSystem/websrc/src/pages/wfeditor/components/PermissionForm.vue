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
.form-desc{
    font-size:12px;
    color:#666;
    margin-bottom:5px;
}
.form-value-tag-select{
    padding-top:0;
}
.choose-tag{
    margin-right:5px;
    margin-bottom:5px;
    margin-top:5px;
}
</style>
<i18n>
{
	"en": {
        "成员": "成员",
		"添加成员": "添加成员",
		"部门": "部门",
		"添加部门": "添加部门",
		"企业角色": "企业角色",
		"添加角色": "添加角色",
		"项目角色": "项目角色"
    },
	"zh_CN": {
		"成员": "成员",
		"添加成员": "添加成员",
		"部门": "部门",
		"添加部门": "添加部门",
		"企业角色": "企业角色",
		"添加角色": "添加角色",
		"项目角色": "项目角色"
	}
}
</i18n>
<template>
<Form :model="formItem" :label-width="80">
                    <FormItem :label="$t('成员')">
                        <div class="form-value-tag-select">
                        <ColorTag class="choose-tag"
                            v-for="item in formItem.userList"
                            :key="'o'+item.id"
                            :closable="true" @on-close="removeFromList(formItem.userList,item)">{{item.name}}</ColorTag>
                        <Button icon="ios-add" type="dashed" size="small" @click="selectUser">{{$t('添加成员')}}</Button>
                        </div>
                    </FormItem>
                    <FormItem :label="$t('部门')">
                         <div class="form-value-tag-select">
                        <ColorTag class="choose-tag"
                            v-for="item in formItem.departmentList"
                            :key="'o'+item.id"
                             :closable="true" @on-close="removeFromList(formItem.departmentList,item)">{{item.name}}</ColorTag>
                        <Button icon="ios-add" type="dashed" size="small" @click="selectDepartment">{{$t('添加部门')}}</Button>
                        </div>
                    </FormItem>

                    <FormItem :label="$t('企业角色')">
                        <div class="form-value-tag-select">
                        <ColorTag class="choose-tag"
                            v-for="item in formItem.companyRoleList"
                            :key="'cr'+item.id"
                             :closable="true" @on-close="removeFromList(formItem.companyRoleList,item)">{{item.name}}</ColorTag>
                        <Button
                            icon="ios-add"
                            type="dashed"
                            size="small"
                            @click="selectCompanyRole"
                        >{{$t('添加角色')}}</Button>
                        </div>
                    </FormItem>
                    <FormItem :label="$t('项目角色')">
                         <div class="form-value-tag-select">
                        <ColorTag class="choose-tag"
                            v-for="item in formItem.projectRoleList"
                            :key="'pr'+item.id"
                             :closable="true" @on-close="removeFromList(formItem.projectRoleList,item)">{{item.name}}</ColorTag>
                        <Button
                            icon="ios-add"
                            type="dashed"
                            size="small"
                            @click="selectProjectRole()"
                        >{{$t('添加角色')}}</Button>
                         </div>
                    </FormItem>
            </Form>
</template>

<script>
export default {
    props:['value'],
    data() {
        return {
            formItem: {
                userList:[],
                companyRoleList:[],
                projectRoleList:[],
                departmentList:[],
            }
        };
    },
    watch:{
        value(val){
            this.formItem=this.value;
        }
    },
    mounted(){
        this.formItem=this.value;
    },
    methods: {
        concatList(list,addList){
            addList.forEach(item=>{
                list.push(item)
            })
        },
        fireChanged(){
            this.$emit('input',this.formItem)
        },
        addToList(list,item){
            for(var i=0;i<list.length;i++){
                var t=list[i];
                if(t.id==item.id){
                    return;
                }
            }
            list.push(item)
            this.fireChanged();
        },
        removeFromList(list,item){
            for(var i=0;i<list.length;i++){
                var t=list[i];
                if(t.id==item.id){
                    list.splice(i,1);
                    this.fireChanged();
                   return;
                }
            }
        },
        selectUser(){
            app.showDialog(MemberSelectDialog,{
                callback:(list)=>{
                    list.forEach(item=>{
                        var user={
                            id:item.accountId,
                            name:item.title
                        }
                        this.addToList(this.formItem.userList,user);
                    })
                }
            })
        },
        selectDepartment(){
            app.showDialog(DepartmentSelectDialog,{
                callback:(list)=>{
                    list.forEach(item=>{
                        var user={
                            id:item.id,
                            name:item.title
                        }
                        this.addToList(this.formItem.departmentList,user);
                    })
                }
            })
        },
        selectCompanyRole(){
            app.showDialog(RoleSelectDialog,{
                roleType:2,
                callback:(list)=>{
                    for(var i=0;i<list.length;i++){
                        var t=list[i];
                        var item={
                            id:t.id,
                            name:t.name
                        }
                        this.addToList(this.formItem.companyRoleList,item)
                    }
                }
            })
        },
        selectProjectRole(){
            app.showDialog(RoleSelectDialog,{
                roleType:1,
                callback:(list)=>{
                    for(var i=0;i<list.length;i++){
                        var t=list[i];
                        var item={
                            id:t.id,
                            name:t.name
                        }
                        this.addToList(this.formItem.projectRoleList,item)
                    }
                }
            })
        }
    }
};
</script>