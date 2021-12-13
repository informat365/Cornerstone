import Vue from 'vue';

var rtrim = /^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g;
String.prototype.trim = function () {
    return this.replace(rtrim, '');
};
window.chartColorList = ['#0094fb', '#efe42a', '#64bd3d', '#ee9201', '#29aae3', '#b74ae5', '#0aaf9f', '#e89589', '#16a085', '#4a235a', '#C39BD3 ', '#f9e79f', '#ba4a00', '#ecf0f1', '#616a6b', '#eaf2f8', '#4a235a', '#3498db'];
window.encodeHTML = function (html) {
    var temp = document.createElement('div');
    (temp.textContent != null) ? (temp.textContent = html) : (temp.innerText = html);
    var output = temp.innerHTML;
    temp = null;
    return output;
};
window.requestFullscreen = function () {
    if (document.documentElement.requestFullscreen) {
        document.documentElement.requestFullscreen();
    } else if (document.documentElement.msRequestFullscreen) {
        document.documentElement.msRequestFullscreen();
    } else if (document.documentElement.mozRequestFullScreen) {
        document.documentElement.mozRequestFullScreen();
    } else if (document.documentElement.webkitRequestFullscreen) {
        document.documentElement.webkitRequestFullscreen(Element.ALLOW_KEYBOARD_INPUT);
    } else {
        app.toast('当前浏览器不支持全屏');
    }
};
window.exitFullscreen = function () {
    if (document.exitFullscreen) {
        document.exitFullscreen();
    } else if (document.webkitExitFullscreen) {
        document.webkitExitFullscreen();
    } else if (document.mozCancelFullScreen) {
        document.mozCancelFullScreen();
    } else if (document.msExitFullscreen) {
        document.msExitFullscreen();
    }
};
window.formatByteSize = function (value) {
    if (value == null) {
        return '';
    }
    if (value < 1024) {
        return value + 'B';
    }
    if (value < 1024 * 1024) {
        return (parseFloat(value / 1024)).toFixed(2) + 'KB';
    }
    if (value < 1024 * 1024 * 1024) {
        return (parseFloat(value / 1024 / 1024)).toFixed(2) + 'MB';
    }
    var q = parseFloat(value / (1024 * 1024 * 1024));
    return q.toFixed(2) + 'GB';
};
//公共函数
window.formatNumber = function (n) {
    n = n.toString();
    return n[1] ? n : '0' + n;
};
window.fmtDeltaTime = function (t) {
    if (t == null) {
        return '-';
    }
    var date = new Date(t);
    var now = new Date();
    if (window.dateSameDay(now, date)) {
        return window.formatTime(t);
    }
    if (now.getFullYear() == date.getFullYear()) {
        return window.formatMonthDatetime(t);
    }
    return window.formatDatetime(t);
};
window.formatTime = function (t) {
    if (t == null) {
        return '-';
    }
    var date = new Date(t);
    var hour = date.getHours();
    var minute = date.getMinutes();
    return [hour, minute].map(window.formatNumber).join(':');
};
window.formatUTCDate = function (t) {
    if (t == null || t == '') {
        return '-';
    }
    var time = new Date(t);
    //var current=new Date();
    //var date=new Date(time.getTime()-current.getTimezoneOffset()*60000)
    return window.formatDate(time);
};
window.formatUTCDatetime = function (t) {
    if (t == null || t == '') {
        return '-';
    }
    var time = new Date(t);
    //var current=new Date();
    //var date=new Date(time.getTime()-current.getTimezoneOffset()*60000)
    return window.formatFullDatetime(time);
};
window.formatWeekDay = function (t) {
    if (t == null) {
        return '-';
    }
    var date = new Date(t);
    var result = '';
    switch (date.getDay()) {
        case 0:
            result = '周日';
            break;
        case 1:
            result = '周一';
            break;
        case 2:
            result = '周二';
            break;
        case 3:
            result = '周三';
            break;
        case 4:
            result = '周四';
            break;
        case 5:
            result = '周五';
            break;
        case 6:
            result = '周六';
            break;
        default:
            result = '';
    }
    return result;
};
window.formatMonthDatetime = function (t) {
    if (t == null) {
        return '-';
    }
    var date = new Date(t);
    var month = date.getMonth() + 1;
    var day = date.getDate();
    var hour = date.getHours();
    var minute = date.getMinutes();
    return [month, day].map(window.formatNumber).join('-') + ' ' + [hour, minute].map(window.formatNumber).join(':');
};
window.formatMonth = function (t) {
    if (t == null) {
        return '-';
    }
    var date = new Date(t);
    var month = date.getMonth() + 1;
    var day = date.getDate();
    return [month, day].map(window.formatNumber).join('-');
};
window.formatDatetime = function (t) {
    if (t == null) {
        return '-';
    }
    var date = new Date(t);
    var year = date.getFullYear() > 2000 ? date.getFullYear() - 2000 : date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
    var hour = date.getHours();
    var minute = date.getMinutes();
    return [year, month, day].map(window.formatNumber).join('-') + ' ' + [hour, minute].map(window.formatNumber).join(':');
};
window.formatFullDatetime = function (t) {
    if (t == null) {
        return '-';
    }
    var date = new Date(t);
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
    var hour = date.getHours();
    var minute = date.getMinutes();
    return [year, month, day].map(window.formatNumber).join('-') + ' ' + [hour, minute].map(window.formatNumber).join(':');
};
window.formatDate = function (t) {
    if (t == null) {
        return '-';
    }
    var date = new Date(t);
    var year = date.getFullYear() > 2000 ? date.getFullYear() - 2000 : date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
    var now = new Date();
    if (now.getFullYear() == date.getFullYear()) {
        return [month, day].map(window.formatNumber).join('-');
    }
    return [year, month, day].map(window.formatNumber).join('-');
};
window.formatFullDate = function (t) {
    if (t == null) {
        return '-';
    }
    var date = new Date(t);
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
    var now = new Date();
    /*if (now.getFullYear() == date.getFullYear()) {
        return [month, day].map(window.formatNumber).join('-');
    }*/
    return [year, month, day].map(window.formatNumber).join('-');
};

window.dateSameDay = function (date1, date2) {
    return window.formatDate(date1) == window.formatDate(date2);
};
window.dateSameWeek = function (date1, date2) {
    var oneDayTime = 1000 * 60 * 60 * 24;
    var old_count = parseInt(date1.getTime() / oneDayTime);
    var now_other = parseInt(date2.getTime() / oneDayTime);
    return parseInt((old_count + 4) / 7) == parseInt((now_other + 4) / 7);
};
window.getWeekNum = function (date) {
    var target = new Date(date.valueOf()),
        dayNumber = (date.getUTCDay() + 6) % 7,
        firstThursday;
    target.setUTCDate(target.getUTCDate() - dayNumber + 3);
    firstThursday = target.valueOf();
    target.setUTCMonth(0, 1);
    if (target.getUTCDay() !== 4) {
        target.setUTCMonth(0, 1 + ((4 - target.getUTCDay()) + 7) % 7);
    }
    return Math.ceil((firstThursday - target) / (7 * 24 * 3600 * 1000)) + 1;
};
window.copyObject = function (a, b) {
    var options = Object.assign({}, a, b);
    return options;
};
//
//表单验证
window.vd = {
    req: { required: true, message: ' ' },
    rangeDate: { type: 'array', length: 2, required: true, message: ' ' },
    name: { type: 'string', min: 2, max: 50, message: '长度为2-50个字' },
    name2_80: { type: 'string', min: 2, max: 80, message: '长度为2-80个字' },
    name2_100: { type: 'string', min: 2, max: 100, message: '长度为2-100个字' },
    name2_500: { type: 'string', min: 2, max: 500, message: '长度为2-500个字' },
    name2_10: { type: 'string', min: 2, max: 10, message: '长度为2-10个字' },
    name2_20: { type: 'string', min: 2, max: 20, message: '长度为2-20个字' },
    name2_15: { type: 'string', min: 2, max: 15, message: '长度为2-15个字' },
    name2_30: { type: 'string', min: 2, max: 30, message: '长度为2-30个字' },
    name1_10: { type: 'string', min: 1, max: 10, message: '长度为1-10个字' },
    desc: { type: 'string', min: 2, max: 200, message: '长度为2-200个字' },
    intValue: { type: 'integer', min: 1, message: '必须为正整数' },
    port: { type: 'integer', min: 1, max: 65535, message: '端口范围1~65535' },
    mobile: { type: 'string', length: 11, message: '手机号格式错误' },
    email: { type: 'email', message: '邮箱格式错误' },
    password: { type: 'string', min: 8, max: 12, message: '密码为8-12个数字和字母的组合' },
};
//
//
window.travalTree = function (node, callback) {
    callback(node);
    if (node.children != null) {
        for (var i = 0; i < node.children.length; i++) {
            window.travalTree(node.children[i], callback);
        }
    }
},
    window.dateDiff = function (dt1, dt2) {
        return Math.floor((Date.UTC(dt2.getFullYear(), dt2.getMonth(), dt2.getDate())
            - Date.UTC(dt1.getFullYear(), dt1.getMonth(), dt1.getDate())) / (1000 * 60 * 60 * 24));
    },
    window.getLeftDays = function (value) {
        if (value == null) {
            return 0;
        }
        return window.dateDiff(new Date(), new Date(value));
    },
    Vue.filter('fmtSeconds', function (num) {
        num = parseInt(num / 1000);
        var s = num % 60;
        num = parseInt(num / 60);
        var m = num % 60;
        num = parseInt(num / 60);
        var h = num % 24;
        num = parseInt(num / 24);
        var d = num;
        var t = [];
        if (d > 0) {
            t.push(d + '天');
        }
        if (h > 0) {
            t.push(h + '时');
        }
        if (m > 0) {
            t.push(m + '分');
        }
        if (s > 0) {
            t.push(s + '秒');
        }
        return t.join('');
    });
Vue.filter('leftDays', function (v) {
    return window.getLeftDays(v);
});
//
Vue.filter('fmtPercent', function (v) {
    return parseFloat(v.toFixed(2));
});
Vue.filter('fmtPercentStr', function (v1, v2) {
    if (v1 < 0) {
        return '--';
    }
    if (v2 == 0) {
        return '--';
    }
    return (v1 * 100 / v2).toFixed(1) + '%';
});
Vue.filter('fmtMoney', function (v) {
    var t = Number(v) / 100;
    return parseFloat(t.toFixed(2));
});
Vue.filter('fmtDateTime', function (v) {
    return window.formatDatetime(v);
});

Vue.filter('fmtUTCDateTime', function (v) {
    return window.formatUTCDatetime(v);
});
Vue.filter('fmtUTCDate', function (v) {
    return window.formatUTCDate(v);
});

Vue.filter('fmtDeltaTime', function (v) {
    return window.fmtDeltaTime(v);
});
Vue.filter('fmtDate', function (v) {
    return window.formatDate(v);
});
Vue.filter('formatFullDate', function (v) {
    return window.formatFullDate(v);
});

Vue.filter('fmtMonth', function (v) {
    return window.formatMonth(v);
});
Vue.filter('fmtWeekDay', function (v) {
    return window.formatWeekDay(v);
});

Vue.filter('dateDiff', function (v) {
    if (v == 0) {
        return '-';
    }
    var t2 = new Date().getTime();
    var t1 = v;
    var r = parseInt((t2 - t1) / (24 * 3600 * 1000));
    if (r == 0) {
        return 1;
    }
    return r;
});
Vue.filter('fmtBool', function (t) {
    return t == true ? '是' : '否';
});
Vue.filter('imgurl', function (t) {
    if (t == null || t == '') {
        return './image/common/placeholder.png';
    }
    return app.serverAddr + '/p/file/get_file/' + t;
});
Vue.filter('userimgurl', function (t) {
    if (t == null || t == '') {
        return './image/common/account-placeholder.png';
    }
    return app.serverAddr + '/p/file/get_file/' + t;
});
Vue.filter('dataDict', function (value, type) {
    if (value == null) {
        return '';
    }
    return app.dataDictValue(type, value);
});
Vue.filter('fmtBytes', function (value) {
    return formatByteSize(value);
});
//
Vue.filter('objectTypeName', function (t) {
    return app.dataDictValue('Task.objectType', t);
});
//
Vue.filter('trim', function (t) {
    return t.trim();
});
//
Vue.directive('focus', {
    inserted: function (el) {
        el.focus();
    },
});

Vue.directive('click-outside', {
    bind(el, binding, vnode) {
        function documentHandler(e) {
            if (el.contains(e.target)) {
                return false;
            }
            if (binding.expression) {
                binding.value(e);
            }
        }

        el.__vueClickOutside__ = documentHandler;
        document.addEventListener('click', documentHandler);
    },
    update() {

    },
    unbind(el, binding) {
        document.removeEventListener('click', el.__vueClickOutside__);
        delete el.__vueClickOutside__;
    },
});
//

if (!Array.indexOf) {
    Array.prototype.indexOf = function (elem) {
        var i = 0,
            length = this.length;
        for (; i < length; i++) {
            if (this[i] === elem) {
                return i;
            }
        }
        return -1;
    };
}
// Add remove method to Array.
Array.prototype.remove = function (elem) {
    return this.splice(this.indexOf(elem), 1);
};
Array.prototype.contains = function (value) {
    var contains = false;
    this.map(item => {
        if (item == value) {
            contains = true;
        }
    });
    return contains;
};
Array.prototype.containsKey = function (key, value) {
    var contains = false;
    this.map(item => {
        if (item[key] == value) {
            contains = true;
        }
    });
    return contains;
};
//
Array.prototype.containsId = function (id) {
    var contains = false;
    this.map(item => {
        if (item.id == id) {
            contains = true;
        }
    });
    return contains;
};
//
window.componentMixin = {
    data() {
        return {
            title: 'undefined',
            args: {},
            showDialog: false,
            showDialogFullscreen: false,
        };
    },
    computed: {
        // 项目集项目排除模块
        projectSetObjectTypeSystemNames() {
            const objectTypeSystemNames = [];
            if (process.env.VUE_APP_PROJECT_SET_OBJECT_TYPE_SYSTEM_NAME_PRO) {
                objectTypeSystemNames.push(process.env.VUE_APP_PROJECT_SET_OBJECT_TYPE_SYSTEM_NAME_PRO);
            }
            if (process.env.VUE_APP_PROJECT_SET_OBJECT_TYPE_SYSTEM_NAME_POS) {
                objectTypeSystemNames.push(process.env.VUE_APP_PROJECT_SET_OBJECT_TYPE_SYSTEM_NAME_POS);
            }
            return objectTypeSystemNames;
        },
        /**
         * 项目是否结束，结束状态的项目在归档前不可再编辑
         * @returns {boolean}
         */
        projectDisabled() {
            var disabled=false;
            if (!!app.projectList) {
                let project = app.projectList.filter(item => item.id === app.projectId)[0]
                disabled = project && project.isFinish;
            }
            return disabled;
        }
    },
    mounted() {
        this.$addEventListener();
    },
    beforeDestroy() {
        this.$removeEventListener();
    },
    watch: {
        showDialog(val) {
            if (val == false) {
                this.$el.remove();
                this.$destroy();
            }
        },
    },
    methods: {
        isProjectDisabled(projectId){
            var disabled=false;
            if(!projectId){
                console.error("<------ isProjectDisabled no projectId input --->")
                return false;
            }
            if (!!app.projectList) {
                let project = app.projectList.filter(item => item.id === projectId)[0]
                disabled = project && project.isFinish;
            }
            return disabled;
        },
        isNotProjectSet(templateUuid) {
            return templateUuid !== process.env.VUE_APP_PROJECT_SET_TEMPLATE_UUID;
        },
        $addEventListener() {
            app.onMessage('AppEvent', this.$receiveMessage);
        },
        $removeEventListener() {
            app.offMessage('AppEvent', this.$receiveMessage);
        },
        $receiveMessage(t) {
            if (this.pageMessage) {
                console.log('pageMessage', t.type, this.title);
                this.pageMessage(t.type, t.content);
            }
        },
        savePageStatus(key) {
            //
            app.saveObject(key, {
                formItem: this.formItem,
                pageQuery: this.pageQuery,
            });
        },
        loadPageStatus(key) {
            var t = app.loadObject(key);
            if (t) {
                if (t.pageQuery) {
                    this.pageQuery = t.pageQuery;
                }
                if (t.formItem) {
                    this.formItem = t.formItem;
                }
            }
        },
        perm(list) {
            return app.perm(list);
        },
        prjPerm(list) {
            return app.prjPerm(list);
        },
        isProjectMember(projectUuid){
            if(app.account.superBoss>0){
                return true;
            }
            if(app.projectList&&Array.isArray(app.projectList)){
                for (let i = 0; i < app.projectList.length; i++) {
                    if(app.projectList[i].uuid==projectUuid){
                        return true;
                    }
                }
            }
            return false;
        },
        gotoProject(uuid,callback){
            if (this.isProjectMember(uuid)) {
                if(callback){
                    callback.apply(null)
                }
                app.loadPage('/pm/project/' + uuid + '/project');
            }else{
                app.toast("您不是项目成员，无法查看项目详情");
            }
        }
    },
};

