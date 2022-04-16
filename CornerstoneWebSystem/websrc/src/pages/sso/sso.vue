<template>
    <div>
        <div v-if="errorMsg">
            <Alert type="error">{{ errorMsg }}</Alert>
        </div>
    </div>
</template>
<script>
export default {
    data() {
        return {
            errorMsg: '',
            serverAddr: window.serverAddr,
        }
    },
    methods: {
        getUrlArgObject() {
            const uri = location.href;
            const questionMarkIdx = uri.indexOf("?");
            const item = {};
            if (0 > questionMarkIdx) {
                return item;
            }
            //获取查询串
            const query = uri.substring(questionMarkIdx + 1);

            console.log('query---->', query);

            //并且符号分组
            const args = query.split("&");
            for(let i=0;i < args.length; i++){
                const arg = args[i];
                const eqMarkIdx = arg.indexOf('=');
                // 没有等于符号跳过
                if(eqMarkIdx < 0){
                    continue;
                }
                //提取name
                const name = arg.substring(0, eqMarkIdx);
                //提取value
                const value= arg.substring(eqMarkIdx + 1);
                //存为属性
                item[name]=unescape(value);
            }
            //返回对象
            return item;
        },
        ssoLogin(callbackInfo, redirectUri) {
            ajaxInvoke(
                this.serverAddr + '/p/api/invoke/',
                'BizAction.ssoLogin',
                [callbackInfo],
                result => {
                    if (result.errCode == 0) {
                        setCookie('token', result.token);
                        if (redirectUri) {
                            if (!redirectUri.startsWith('/')) {
                                redirectUri = '/' + redirectUri;
                            }
                            if (redirectUri.startsWith('/#')) {
                                window.location.href = redirectUri;
                            } else {
                                window.location.href = '/#' + redirectUri;
                            }
                        } else {
                            window.location.href = '/#/';
                        }

                        this.saveLoginType(1)
                    } else {
                        console.log('result.errMsg ----->', result.errMsg);
                        this.errorMsg = result.errMsg;
                    }
                },
                (code, msg) => {
                    this.errorMsg = msg;
                },
            );
        }
    },
    mounted() {
        const params = this.getUrlArgObject();
        console.log('params----->', params);
        this.ssoLogin(params.callbackInfo, params.redirectUri);


    },
}

</script>
