const mixin = {
    data() {
        return {
            serverAddr: null,
            token: null,
            surveysUuid: null,
            accountUuid: null,
        };
    },
    filters: {
        formatDate(t, fmt) {
            const newFmt = fmt || 'yyyy-MM-dd hh:mm';
            return Date.format(t, newFmt);
        },
        formatDateTime(t, fmt) {
            const newFmt = fmt || 'yyyy-MM-dd hh:mm';
            return Date.format(t, newFmt, true);
        },
        formatDateSecond(t, fmt) {
            return Date.formatDateSecond(t, fmt);
        },
        formatString(value, defaultValue) {
            return String.isEmpty(value) ? (defaultValue || '--') : value;
        },
    },
    watch: {
        accountUuid(val) {
            if (String.isEmpty(this.surveysUuid)) {
                return;
            }
            const uuidKey = 'surveys-' + this.surveysUuid;
            setCookie(uuidKey, val);
        },
    },
    created() {
        this.serverAddr = [window.location.protocol, '//', window.location.host].join('');
        this.token = getCookie('token');
        const url = new URL(window.location.href);
        const pageArgs = {};
        url.searchParams.forEach((k, v) => {
            pageArgs[v] = k;
        });
        this.surveysUuid = pageArgs['uuid'];
        if (!String.isEmpty(this.surveysUuid)) {
            const uuidKey = 'surveys-' + this.surveysUuid;
            this.accountUuid = getCookie(uuidKey);
        }
        if (typeof (this.pageCreated) === 'function') {
            this.pageCreated(pageArgs);
        }
    },
    methods: {
        formatDate(t, fmt) {
            const newFmt = fmt || 'yyyy-MM-dd hh:mm';
            return Date.format(t, newFmt);
        },
        formatString(value, defaultValue) {
            return String.isEmpty(value) ? (defaultValue || '--') : value;
        },
        request(func, args, success, error, withToken = true) {
            let newArgs = [];
            if (Array.isArray(args)) {
                newArgs = [...args];
            }
            if (withToken) {
                newArgs.unshift(this.token);
            }
            ajaxInvoke(this.serverAddr + '/p/api/invoke/', func, newArgs, success, error);
        },
    },
};

export default mixin;
