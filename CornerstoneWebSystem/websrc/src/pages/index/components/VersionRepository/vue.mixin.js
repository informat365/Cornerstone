(() => {
    const typeOfObj = (obj) => {
        const toString = Object.prototype.toString;
        const map = {
            '[object Boolean]': 'boolean',
            '[object Number]': 'number',
            '[object String]': 'string',
            '[object Function]': 'function',
            '[object Array]': 'array',
            '[object Date]': 'date',
            '[object RegExp]': 'regExp',
            '[object Undefined]': 'undefined',
            '[object Null]': 'null',
            '[object Object]': 'object',
        };
        return map[toString.call(obj)];
    };

    /**
     * 判断对象是否是给定类型
     * @param object
     * @param type
     * @returns {boolean}
     */
    Object.isTypeOf = (object, type) => typeOfObj(object) === type;
    /**
     * 获取对象类型字符串
     * @param object
     * @returns {*}
     */
    Object.typeOf = (object) => typeOfObj(object);
    /**
     * 判断对象是否是Undefined值
     * @param object
     * @returns {boolean}
     */
    Object.isUndefined = object => typeOfObj(object) === 'undefined';
    /**
     * 判断对象是否是Null值
     * @param object
     * @returns {boolean}
     */
    Object.isNull = object => object === null;
    /**
     * 判断对象是否是Boolean类型
     * @param object
     * @returns {boolean}
     */
    Object.isBoolean = object => typeOfObj(object) === 'boolean';
    /**
     * 判断对象是否是Number类型
     * @param object
     * @returns {boolean}
     */
    Object.isNumber = object => typeOfObj(object) === 'number';
    /**
     * 判断对象是否是Date类型
     * @param object
     * @returns {boolean}
     */
    Object.isDate = object => typeOfObj(object) === 'date';
    /**
     * 判断对象是否是RegExp类型
     * @param object
     * @returns {boolean}
     */
    Object.isRegExp = object => typeOfObj(object) === 'regExp';
    /**
     * 判断对象是否是string类型
     * @param object
     * @returns {boolean}
     */
    Object.isString = object => typeOfObj(object) === 'string';
    /**
     * 判断对象是一个function类型
     * @param object
     * @returns {boolean}
     */
    Object.isFunction = object => typeOfObj(object) === 'function';
    /**
     * 判断对象是Object类型
     * @param object
     * @returns {boolean}
     */
    Object.isObject = object => typeOfObj(object) === 'object';
    /**
     * 判断对象不是null对象
     * @param object
     * @returns {boolean|boolean}
     */
    Object.isNotNullObject = object => typeOfObj(object) === 'object';
    /**
     * 判断对象是否是数组
     * @param object
     * @returns {boolean}
     */
    Object.isArray = object => typeOfObj(object) === 'array';
    /**
     * 判断对象是否是不可枚举对象(即Object.keys无法获取数据)
     * @param object
     * @returns {boolean}
     */
    Object.isNoEnumerable = (object) => {
        return ['boolean', 'number', 'string', 'function', 'date', 'regExp', 'undefined', 'null'].indexOf(typeOfObj(object)) > -1;
    };
    /**
     * 判断对象是否是空值
     * @param object
     * @returns {boolean|*}
     */
    Object.isEmpty = object => {
        const typeOf = Object.typeOf(object);
        if (['undefined', 'null'].indexOf(typeOf) > -1) {
            return true;
        }
        if (typeOf === 'string' && String.isEmpty(object)) {
            return true;
        }
        if (Object.isNoEnumerable(object)) {
            return false;
        }
        return Array.isEmpty(Object.keys(object));
    };
// deepCopy
    Object.deepCopy = (data) => {
        const t = typeOfObj(data);
        let o;
        if (t === 'array') {
            o = [];
        } else if (t === 'object') {
            o = {};
        } else {
            return data;
        }
        if (t === 'array') {
            for (let i = 0; i < data.length; i++) {
                o.push(Object.deepCopy(data[i]));
            }
        } else if (t === 'object') {
            for (let i in data) {
                o[i] = Object.deepCopy(data[i]);
            }
        }
        return o;
    };

    Object.methods = (object) => {
        const methods = [];
        Object.keys(object).forEach((key) => {
            if (object[key] && object[key].constructor && object[key].call && object[key].apply) {
                methods.push(key);
            }
        });
        return methods;
    };
// eslint-disable-next-line func-names
    Date.prototype.format = function (fmt) {
        let newFmt = fmt;
        const o = {
            'M+': this.getMonth() + 1, // 月份
            'd+': this.getDate(), // 日
            'h+': this.getHours(), // 小时
            'm+': this.getMinutes(), // 分
            's+': this.getSeconds(), // 秒
            'q+': Math.floor((this.getMonth() + 3) / 3), // 季度
            S: this.getMilliseconds(), // 毫秒
        };
        if (/([y|Y]+)/.test(newFmt)) {
            newFmt = newFmt.replace(RegExp.$1, (String(this.getFullYear()))
            .substr(4 - RegExp.$1.length));
        }
        Object.keys(o).forEach((k) => {
            if (new RegExp(`(${ k })`).test(newFmt)) {
                newFmt = newFmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k])
                    : ((`00${ o[k] }`).substr((String(o[k])).length)));
            }
        });
        return newFmt;
    };
// !!TODO 屏蔽JSON.stringify时间格式化的问题
    Date.prototype.toJSON = function () {
        return this.format('yyyy-MM-dd hh:mm:ss');
    };
    Date.isDate = date => (date instanceof Date && !Number.isNaN(date.valueOf()));
    Date.format = (date, fmt) => {
        const newFmt = fmt || 'yyyy-MM-dd hh:mm:ss';
        if (Date.isDate(date)) {
            return date.format(newFmt);
        }
        if (Number.isNaN(parseInt(date, 10))) {
            return '--';
        }
        return Date.parser(date).format(newFmt);
    };
    Date.parser = (date) => {
        if (Object.isEmpty(date)) {
            return null;
        }
        if (Object.isDate(date)) {
            return date;
        }
        if (Object.isNumber(date)) {
            return new Date(date);
        }
        if (date.indexOf && date.indexOf('T') > -1) {
            return new Date(date);
        }
        return new Date(date.replaceAll('-', '/'));
    };
    Date.formatDateSecond = (jsondate, fmt) => {
        let newJsonDate = jsondate;
        if (Number.isNaN(parseInt(newJsonDate, 10))) {
            return '--';
        }
        newJsonDate *= 1000;
        return Date.format(newJsonDate, fmt);
    };
    Date.getHour = (nowDate, toDate) => {
        if (Number.isNaN(parseInt(toDate, 10))) {
            return 0;
        }
        return Number.parseInt((nowDate.getTime() - toDate) / (1000 * 60 * 60), 10);
    };
    Date.nowString = (radix) => {
        let radixs = parseInt(radix, 10);
        if (Number.isNaN(radixs)) {
            radixs = 36;
        }
        return Date.now().toString(radixs);
    };

    let uqId = 1;
    Date.uqId = (radix) => {
        let newRadix = parseInt(radix, 10);
        if (Number.isNaN(newRadix)) {
            newRadix = 36;
        }
        uqId += 1;
        return Date.now().toString(newRadix).concat(uqId.toString(newRadix));
    };
    Date.isSameDay = (date, compDate) => {
        if (!Date.isDate(date) || !Date.isDate(compDate)) {
            return false;
        }
        return date.getFullYear() === compDate.getFullYear() && date.getMonth() === compDate.getMonth() && date.getDate() === compDate.getDate();
    };
    Date.isSameMonth = (date, compDate) => {
        if (!Date.isDate(date) || !Date.isDate(compDate)) {
            return false;
        }
        return date.getFullYear() === compDate.getFullYear() && date.getMonth() === compDate.getMonth();
    };
    Date.inRangeDate = (date, startDate, endDate) => {
        if (!Date.isDate(date) || !Date.isDate(startDate) || !Date.isDate(endDate)) {
            return false;
        }
        return date.getTime() >= startDate.getTime() && date.getTime() <= endDate.getTime();
    };
    Date.diffDay = (startTime, endTime) => {
        if (!Date.isDate(startTime) || !Date.isDate(endTime)) {
            return -1;
        }
        const newStartDate = new Date(startTime.getFullYear(), startTime.getMonth(), startTime.getDate());
        const newEndDate = new Date(endTime.getFullYear(), endTime.getMonth(), endTime.getDate());
        return Math.floor(Math.abs(newEndDate.getTime() - newStartDate.getTime()) / 86400000);
    };
    Date.gt=(start,end)=>{
        if(!start){
            return false;
        }
        if(!end){
            return true;
        }
        return new Date(start).getTime()>new Date(end).getTime();
    }
// 扩充函数
    if (!Date.now) {
        Date.now = () => new Date().getTime();
    }
    Number.integer = {
        min: -2147483648,
        max: 2147483647,
    };
    /**
     * 随机数字
     * @param min
     * @param max
     * @returns {*}
     */
    Number.randomInt = (min = 0, max = 0) => Math.floor(Math.random() * (max - min + 1)) + min;
    /**
     * 数字检查
     *
     * @param value
     * @param min
     *            option
     * @param max
     *            option
     */
    Number.check = (value, min, max) => {
        let ret = false;
        if (!Number.isNumeric(value)) {
            return ret;
        }
        const newValue = parseFloat(value);
        ret = true;
        const newMin = parseFloat(min);
        const newMax = parseFloat(max);
        if (!Number.isNaN(newMin)) {
            if (newMin.toString() !== min.toString()) {
                return false;
            }
            ret = newValue >= newMin;
            if (!ret) {
                return false;
            }
        }
        if (!Number.isNaN(newMax)) {
            if (newMax.toString() !== max.toString()) {
                return false;
            }
            ret = newValue <= newMax;
        }
        return ret;
    };
    Number.isNumeric = value => !Number.isNaN(parseFloat(value)) && !Number.isNaN(value - parseFloat(value));
    Number.parseIntWithDefault = (value, vdefault) => {
        let newDdefault = parseInt(vdefault, 10);
        if (Number.isNaN(newDdefault)) {
            newDdefault = 0;
        }
        let newValue = parseInt(value, 10);
        if (Number.isNaN(newValue)) {
            newValue = newDdefault;
        }
        return newValue;
    };
    Number.parseIntRate = (value, rate) => {
        if (!Number.isNumeric(value)) {
            return 0;
        }
        if (!Number.isNumeric(rate)) {
            return parseInt(rate, 10);
        }
        return parseInt((value * rate).toPrecision(12), 10);
    };
    Number.parseIntRatio = (value, ratio) => {
        const newRatio = Number.parseIntWithDefault(ratio, 0);
        if (newRatio === 0) {
            throw new Error('ratio can\'t be zero!!');
        }
        return parseInt((value / newRatio).toPrecision(12), 10);
    };
    Number.parseFloatWithDefault = (value, vdefault, toFixed) => {
        let newDefault = parseFloat(vdefault);
        if (Number.isNaN(newDefault)) {
            newDefault = 0;
        }
        let newToFixed = parseInt(toFixed, 10);
        if (Number.isNaN(toFixed)) {
            newToFixed = -1;
        }
        let newValue = parseFloat(value);
        if (Number.isNaN(newValue)) {
            newValue = newDefault;
        }
        newValue = parseFloat(newValue.toPrecision(12));
        if (newToFixed > -1) {
            return parseFloat(newValue.toFixed(newToFixed));
        }
        return value;
    };
    Number.pad = (number, len) => (Array(len).join(0) + number).slice(-len);
    Number.formatPenny = (value) => {
        let newValue = parseFloat(value);
        if (Number.isNaN(newValue)) {
            newValue = 0;
        }
        if (newValue % 100 === 0) {
            return Number(newValue / 100);
        }
        return Number(Number((value / 100).toPrecision(12)).toFixed(2));
    };
    Number.formatRate = (value, rate, toFixed) => {
        const newValue = parseFloat(value);
        let newRate = parseInt(rate, 10);
        const newToFixed = parseInt(toFixed, 10);
        if (Number.isNaN(newToFixed)) {
            return '--';
        }
        if (Number.isNaN(value)) {
            return '--';
        }
        if (Number.isNaN(newRate)) {
            newRate = 1;
        }
        if (value % rate === 0) {
            return Number(newValue / newRate);
        }
        return Number(Number((newValue / rate).toPrecision(12)).toFixed(toFixed));
    };
// eslint-disable-next-line func-names
    String.prototype.replaceAll = function (target, replacement) {
        if (String.isEmpty(this)) {
            return this;
        }
        return this.split(target).join(replacement);
    };
    String.isEmpty = (val) => {
        let value = val;
        if (Object.isNumber(value)) {
            value = `${ value }`;
        }
        return !value || value.toString().trim().length === 0;
    };
    String.replaceAll = (s, target, replacement) => {
        if (String.isEmpty(s)) {
            return s;
        }
        return s.split(target).join(replacement);
    };
    String.trim = (s) => {
        if (String.isEmpty(s)) {
            return s;
        }
        return s.replace(/(^\s*)|(\s*$)/g, '');
    };
    String.ltrim = (s) => {
        if (String.isEmpty(s)) {
            return s;
        }
        return s.replace(/(^\s*)/g, '');
    };
    String.rtrim = (s) => {
        if (String.isEmpty(s)) {
            return s;
        }
        return s.replace(/(\s*$)/g, '');
    };
    String.startsWith = (s, searchString, position) => {
        if (String.isEmpty(s)) {
            return false;
        }
        const newPosition = Number.parseInt(position, 10);
        if (newPosition) {
            return false;
        }
        return s.indexOf(searchString, newPosition) === newPosition;
    };
    if (!String.prototype.startWith) {
        Object.defineProperty(String.prototype, 'startWith', {
            value: function (search, pos) {
                // eslint-disable-next-line no-param-reassign
                pos = !pos || pos < 0 ? 0 : +pos;
                return this.substring(pos, pos + search.length) === search;
            },
        });
    }
    if (!String.prototype.endWith) {
        // eslint-disable-next-line func-names
        String.prototype.endWith = function (search, thisLen) {
            // eslint-disable-next-line no-undefined
            if (thisLen === undefined || thisLen > this.length) {
                // eslint-disable-next-line no-param-reassign
                thisLen = this.length;
            }
            return this.substring(thisLen - search.length, thisLen) === search;
        };
    }
    String.isMobile = (mobile) => {
        const reg = /^1[0-9]{10}$/;
        return reg.test(mobile);
    };
    String.isEmail = (email) => {
        const reg = new RegExp('(([^<>()\\[\\]\\.,;:\\s@\\"]+(\\.[^<>()\\[\\]\\.,;:\\s@\\"]+)*)|(\\".+\\"))@(([^<>()[\\]\\.,;:\\s@\\"]+\\.)+[^<>()[\\]\\.,;:\\s@\\"]{2,})', 'i');
        return reg.test(email);
    };
    String.isUrl = (url) => {
        const reg = new RegExp('https?://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]');
        return reg.test(url);
    };
    String.trim = () => this.replace(/^\s+|\s+$/g, '');
    if (!Array.isArray) {
        Array.isArray = arg => Object.prototype.toString.call(arg) === '[object Array]';
    }
    Array.isEmpty = array => !array || !Array.isArray(array) || array.length === 0;
    Array.deleteElement = (array, callback) => {
        if (!Array.isArray(array) || array.length === 0) {
            return array;
        }
        if (!Object.isTypeOf(callback, 'function')) {
            throw new Error('callback can not be null');
        }
        const newArray = [];
        array.forEach((item, index) => {
            if (callback(item, index)) {
                return;
            }
            newArray.push(item);
        });
        return newArray;
    };
    Array.random = (array, count) => {
        if (Array.isEmpty(array) || !Number.check(count, 1)) {
            return [];
        }
        const newArray = array.map(item => {
            return item;
        });
        const len = newArray.length;
        newArray.sort(() => {
            return Math.random() * len - Math.random() * len;
        });
        return newArray.splice(0, count);
    };
    /**
     * 判断数组中是否存在某一值
     * @param array
     * @param value
     * @param callback
     * @returns {boolean}
     */
    Array.contains = (array, value, callback = item => item) => {
        if (Array.isEmpty(array)) {
            return false;
        }
        return !Object.isEmpty(array.find(item => callback(item) === value));
    };
    /**
     * 判断数组中是否包含另一数组某一值
     * @param array1
     * @param array2
     * @param callback
     * @returns {boolean}
     */
    Array.containsAny = (array1, array2, callback = (a, b) => a === b) => {
        if (Array.isEmpty(array1) || Array.isEmpty(array2)) {
            return false;
        }
        for (let i = 0; i < array1.length; i += 1) {
            for (let j = 0; j < array2.length; j += 1) {
                if (callback.apply(null, [array1[i], array2[j]]) === true) {
                    return true;
                }
            }
        }
        return false;
    };

    /**
     * 数组合并
     * 如果callback不传递则使用偶人callback
     * @param callback      识别两个值是一样的回调
     * @param arrays        变长数组(可以传递任意类型)
     * @returns {*|*[]|[]}
     */
    Array.merge = (callback, ...arrays) => {
        if (Array.isEmpty(arrays)) {
            return;
        }
        let mergeArray = [];
        let keyCallback = item => item;
        if (Object.isFunction(callback)) {
            keyCallback = callback;
        } else if (!Object.isEmpty(callback)) {
            if (Object.isArray(callback)) {
                mergeArray = mergeArray.concat(callback);
            } else {
                mergeArray.push(callback);
            }
        }
        arrays.forEach(item => {
            if (Array.isArray(item)) {
                mergeArray = mergeArray.concat(item);
                return;
            }
            if (!Object.isEmpty(item)) {
                mergeArray.push(item);
            }
        });
        const mergeObject = {};
        mergeArray.forEach(item => {
            mergeObject['mo_' + keyCallback(item)] = item;
        });
        const result = [];
        Object.keys(mergeObject).forEach(key => {
            result.push(mergeObject[key]);
        });
        return result;
    };
})();

const mixin = {
    data() {
        return {
            serverAddr: null,
            token: null,
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
