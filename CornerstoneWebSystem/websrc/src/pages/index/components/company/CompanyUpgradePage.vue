<style scoped>

</style>

<i18n>
    {
    "zh_CN": {
    "升级完成后需要管理员重启系统":"升级完成后需要管理员重启系统",
    "当前版本":"当前版本",
    "选择升级方式":"选择升级方式",
    "在线自动升级":"在线自动升级",
    "本地升级":"本地升级",
    "在线自动升级":"在线自动升级",
    "系统：":"系统：",
    "数据库：":"数据库：",
    "在线自动升级":"在线自动升级",
    "开始升级":"开始升级",
    "本地升级":"本地升级",
    "请上传本地安装包":"请上传本地安装包",
    "上传":"上传",
    "升级日志":"升级日志",
    "在线自动升级需要连接到install.cornerstone365.cn，请确保网络畅通":"在线自动升级需要连接到install.cornerstone365.cn，请确保网络畅通"
    }
    }
</i18n>

<template>
    <div class="admin-page">
         <Alert>
                {{$t('升级完成后需要管理员重启系统')}}
            </Alert>
          <Form label-position="top">
                    <FormItem  :label="$t('当前版本')">
                        <div>{{$t('系统：')}}v1.0.3 {{$t('数据库：')}} v1.0.2</div>
                    </FormItem>
                    <FormItem :label="$t('选择升级方式')">
                        <div>
                            <RadioGroup v-model="upgradeType">
                                <Radio label="web">{{$t('在线自动升级')}}</Radio>
                                <Radio label="local">{{$t('本地升级')}}</Radio>
                            </RadioGroup>
                        </div>
                    </FormItem>

                    <FormItem v-show="upgradeType=='web'" :label="$t('在线自动升级')">
                        <div>{{$t('在线自动升级需要连接到install.cornerstone365.cn，请确保网络畅通')}}</div>
                        <div>
                            <Button @click="startAutoUpgrade()" type="default">{{$t('开始升级')}}</Button>
                        </div>
                    </FormItem>

                    <FormItem v-show="upgradeType=='local'" label="本地升级">
                        <div>{{$t('请上传本地安装包')}}</div>
                        <div>
                            <Button @click="startAutoUpgrade()" type="default">{{$t('上传')}}</Button>
                        </div>
                    </FormItem>

                    <FormItem :label="$t('升级日志')">
                        <div>xxxx</div>
                    </FormItem>

            </Form>

    </div>
</template>

<script>

export default {
    mixins: [componentMixin],
    data(){
        return {
            upgradeType:"web"
        }
    },
    methods:{
        pageLoad(){

        },
        startAutoUpgrade(){

        }
    }
}
</script>
