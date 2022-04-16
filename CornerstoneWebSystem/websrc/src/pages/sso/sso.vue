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
            var args=new Object();
            //获取查询串
            var query=location.search.substring(1);
            //在逗号处断开
            var pairs=query.split(",");
            for(var i=0;i<pairs.length;i++){
                //查找name=value
                var pos=pairs[i].indexOf('=');
                //如果没有找到就跳过
                if(pos==-1){
                    continue;
                }
                //提取name
                var argname=pairs[i].substring(0,pos);
                //提取value
                var value=pairs[i].substring(pos+1);
                //存为属性
                args[argname]=unescape(value);
            }
            //返回对象
            return args;
        },
        ssoLogin(callbackInfo) {
            ajaxInvoke(
                this.serverAddr + '/p/api/invoke/',
                'BizAction.ssoLogin',
                [callbackInfo],
                result => {
                    if (result.errCode == 0) {
                        setCookie('token', result.token);
                        window.location.href = '/#/';
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
        // this.errorMsg = '测试一下';
        this.ssoLogin(params.callbackInfo);


    },
}

</script>
