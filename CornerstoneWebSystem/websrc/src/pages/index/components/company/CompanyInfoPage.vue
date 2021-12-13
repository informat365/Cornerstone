<style scoped>
    .logo-image {
        width: 200px;
        border: 1px solid #eee;
        margin-right: 10px;
    }

    .logo-row {
        display: flex;
        align-items: center;
    }

    .form-desc {
        font-size: 13px;
        color: #999;
        padding: 10px 0;
    }
</style>

<i18n>
    {
    "en": {
    "基本信息":"Basic info",
    "公司名称":"Company name",
    "名称":"Name",
    "重置":"Reset",
    "上传LOGO":"Upload LOGO",
    "公司简介":"Company Profile",
    "保存":"Save",
    "版本信息":"Version info",
    "版本":"Version",
    "更新License":"Update License",
    "成员数量":"Members number",
    "创建时间":"Create time",
    "到期时间":"Expiration time",
    "状态":"Status",
    "保存成功":"Save success",
    "8位密码":"The length should greater than 8",
    "包含大小写":"The password should contains upper and lower chars",
    "包含数字":"The password should contains number",
    "包含特殊字符":"The password should contain special chars like '@#$'",
    "不含用户名":"The password shouldn't contain the user name",
    "不与前两次重复":"The password cann't be repeated with the password which was input latest twice time ",
    "无限制":"No limit",
    "密码规则":"Password security rules"
    },
    "zh_CN": {
    "基本信息":"基本信息",
    "名称":"名称",
    "公司名称":"公司名称",
    "重置":"重置",
    "上传LOGO":"上传LOGO",
    "公司简介":"公司简介",
    "保存":"保存",
    "版本信息":"版本信息",
    "版本":"版本",
    "更新License":"更新License",
    "成员数量":"成员数量",
    "创建时间":"创建时间",
    "到期时间":"到期时间",
    "状态":"状态",
    "保存成功":"保存成功",
    "8位密码":"最小长度8位",
    "包含大小写":"需要带有大小写字母",
    "包含数字":"需要带有数字",
    "包含特殊字符":"需要带有特殊字符@&#等",
    "不含用户名":"不能包含登录用户名",
    "不与前两次重复":"不能与前两次设置密码重复",
    "无限制":"无限制",
    "密码规则":"密码安全规则"
    }
    }
</i18n>

<template>
    <div class="admin-page">
        <div class="info-section-row" style="margin-bottom:15px">
            <div class="info-section-header">
                {{$t('基本信息')}}
            </div>
        </div>

        <Form ref="form" :rules="formRule" :model="formItem" label-position="top" style="margin-top:15px">
            <FormItem :label="$t('名称')" prop="name">
                <Input :disabled="formItem.status==1&&formItem.version==2" style="width:500px" :placeholder="$t('公司名称')"
                       v-model.trim="formItem.name"></Input>
            </FormItem>

            <FormItem label="LOGO">
                <div class="logo-row">
                    <img v-if="formItem.imageId" :src="imageURL" class="logo-image"/>
                    <Button style="margin-right:5px" v-if="perm('company_admin_edit_company_info')&&formItem.imageId"
                            @click="formItem.imageId=null" type="default">{{$t('重置')}}
                    </Button>
                    <Button v-if="perm('company_admin_edit_company_info')" @click="uploadLogo" type="default">
                        {{$t('上传LOGO')}}
                    </Button>
                </div>
                <div class="form-desc">自定义的LOGO将会显示在主界面左上角的位置</div>
            </FormItem>

            <FormItem :label="$t('公司简介')" prop="remark">
                <Input style="width:500px" type="textarea" :rows="3" v-model.trim="formItem.remark"
                       :placeholder="$t('公司简介')"></Input>
            </FormItem>


            <FormItem :label="$t('密码规则')">
                <CheckboxGroup v-model="passwordRule.rules">
                    <Checkbox :label="1">{{$t('8位密码')}}</Checkbox>
                    <Checkbox :label="2">{{$t('包含大小写')}}</Checkbox>
                    <Checkbox :label="3">{{$t('包含数字')}}</Checkbox>
                    <Checkbox :label="4">{{$t('包含特殊字符')}}</Checkbox>
                    <Checkbox :label="5">{{$t('不含用户名')}}</Checkbox>
                    <Checkbox :label="6">{{$t('不与前两次重复')}}</Checkbox>
                </CheckboxGroup>
            </FormItem>


            <FormItem v-if="perm('company_admin_edit_company_info')" label="">
                <Button @click="updateCompany" type="default">{{$t('保存')}}</Button>
            </FormItem>
        </Form>

        <div class="info-section-row" style="margin-bottom:15px;margin-top:30px">
            <div class="info-section-header">
                {{$t('版本信息')}}
            </div>
        </div>

        <Form class="tiny-form" label-position="left" :label-width="100" style="margin-top:15px">
            <FormItem :label="$t('版本')">
                <div style="display:flex;align-items:center">
                    <div>{{formItem.version|dataDict('Company.version')}}</div>
                    <div v-if="formItem.moduleList">【{{formItem.moduleList}}】</div>
                    <Button style="margin-left:5px" size="small"
                            v-if="formItem.version==2"
                            @click="updateLicense" type="default">{{$t('更新License')}}
                    </Button>

                </div>
            </FormItem>
            <FormItem :label="$t('成员数量')">
                {{formItem.memberNum}}/{{formItem.maxMemberNum}}
            </FormItem>
            <FormItem :label="$t('创建时间')">
                {{formItem.createTime|formatFullDate}}
            </FormItem>
            <FormItem :label="$t('到期时间')">
                {{formItem.dueDate|formatFullDate}}
            </FormItem>
            <FormItem :label="$t('状态')">
                {{formItem.status|dataDict('Company.status')}}
            </FormItem>
        </Form>


        <div v-if="formItem.version==1" style="margin-top:30px;">
            <Button @click="deleteCompany" type="error">{{$t('删除企业')}}</Button>
            <div class="form-desc">一旦你删除了企业，企业内所有项目、成员，任务等信息将会被永久删除。请谨慎操作！</div>
        </div>

    </div>
</template>

<script>

    export default {
        mixins: [componentMixin],
        data() {
            return {
                formItem: {
                    name: null,
                    remark: null,
                    imageId: null,
                },
                formRule: {
                    name: [vd.req, vd.name],
                    remark: [vd.desc],
                },
                passwordRule: {
                    rules: []
                }
            }
        },
        computed: {
            imageURL: function () {
                return app.serverAddr + "/p/file/get_file/" + this.formItem.imageId + "?token=" + app.token;
            },
        },
        methods: {
            pageLoad() {
                this.loadCompanyInfo();
                this.loadCompanyPasswordRule();
            },
            pageMessage(type) {
                if (type == 'company.update') {
                    this.loadCompanyInfo();
                }
            },
            loadCompanyInfo() {
                app.invoke('BizAction.getCompanyInfoByUuid', [app.token, app.companyUUID], (info) => {
                    if (info.imageId == undefined) {
                        info.imageId = null;
                    }
                    this.formItem = info;
                })
            },
            loadCompanyPasswordRule() {
                app.invoke('BizAction.getPasswordRule', [app.token], (info) => {
                    if (!!info) {
                        this.passwordRule = info;
                    }
                })
            },
            updateLicense() {
                app.showDialog(UpdateLicenseDialog, {
                    id: this.formItem.id
                })
            },
            uploadLogo() {
                app.showDialog(CropImageDialog, {
                    callback: (blob) => {
                        this.uploadBlob(blob)
                    }
                })
            },
            uploadBlob(blob) {
                var fd = new FormData();
                fd.append('fname', 'logo.png');
                fd.append('file', blob);
                var vm = this;
                $.ajax({
                    type: 'POST',
                    url: app.serverAddr + '/p/file/upload_file?token=' + app.token,
                    data: fd,
                    processData: false,
                    contentType: false
                }).done(function (data) {
                    var t = JSON.parse(data);
                    vm.formItem.imageId = t.attachment.uuid;
                });
            },
            updateCompany() {
                this.$refs.form.validate((r) => {
                    if (r) {
                        this.confirmForm()
                    }
                });
            },
            confirmForm() {
                app.invoke('BizAction.updateCompany', [app.token, this.formItem], (info) => {
                    app.postMessage('company.update')
                    app.toast(this.formItem.name + this.$t('保存成功'))
                })
                app.invoke(this.passwordRule.id > 0 ? 'BizAction.updatePasswordRule' : "BizAction.addPasswordRule", [app.token, this.passwordRule], (info) => {
                })
            },
            deleteCompany() {
                app.confirm('确认要删除企业吗？一旦你删除了企业，企业内所有项目、成员，任务等信息将会被永久删除。请谨慎操作！', () => {
                    app.invoke('BizAction.deleteCompany', [app.token, this.formItem.id], (info) => {
                        window.location.href = "https://cornerstone365.cn"
                    })
                })
            }
        }
    }
</script>
