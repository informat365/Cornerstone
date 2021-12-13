import { Toast } from 'vant';
import Vue from 'vue';
import App from './surveysform';
import './surveysform.less';
import mixin from './surveysform.mixin';

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
        if (['undefined', 'null'].indexOf(Object.typeOf(object)) > -1) {
            return true;
        }
        if (Object.isNoEnumerable(object)) {
            return false;
        }
        return Array.isEmpty(Object.keys(object));
    };
    // eslint-disable-next-line func-names
    String.prototype.replaceAll = function (target, replacement) {
        if (String.isEmpty(this)) {
            return this;
        }
        return this.split(target).join(replacement);
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
        Object.keys(o)
        .forEach((k) => {
            if (new RegExp(`(${ k })`).test(newFmt)) {
                newFmt = newFmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k])
                    : ((`00${ o[k] }`).substr((String(o[k])).length)));
            }
        });
        return newFmt;
    };
    Date.isDate = date => (date instanceof Date && !Number.isNaN(date.valueOf()));
    Date.format = (date, fmt, skipSameYear = false) => {
        let newFmt = fmt || 'yyyy-MM-dd hh:mm';
        const now = new Date();
        if (Date.isDate(date)) {
            if (now.getFullYear() === date.getFullYear() && skipSameYear) {
                newFmt = newFmt.replace(/y{1,4}-/, '');
            }
            return date.format(newFmt);
        }
        if (Number.isNaN(parseInt(date, 10))) {
            return '--';
        }
        const newDate = Date.parser(date);
        if (now.getFullYear() === newDate.getFullYear() && skipSameYear) {
            newFmt = newFmt.replace('yyyy-', '');
        }
        return newDate.format(newFmt);
    };
    Date.parser = (date) => {
        if (String.isEmpty(date)) {
            return null;
        }
        if (Number.check(date)) {
            return new Date(date);
        }
        if (date.indexOf && date.indexOf('T') > -1) {
            return new Date(date);
        }
        return new Date(date.replaceAll('-', '/'));
    };
    if (!Date.now) {
        Date.now = () => new Date().getTime();
    }

    Date.gt=(start,end)=>{
        if(!start){
            return false;
        }
        if(!end){
            return true;
        }
        return new Date(start).getTime()>new Date(end).getTime();
    }
    Number.isNumeric = value => !Number.isNaN(parseFloat(value)) && !Number.isNaN(value - parseFloat(value));
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
    String.isEmpty = (val) => {
        let value = val;
        if (Object.isNumber(value)) {
            value = `${ value }`;
        }
        return !value || value.toString().trim().length === 0;
    };
    String.trim = (s) => {
        if (String.isEmpty(s)) {
            return s;
        }
        return s.replace(/(^\s*)|(\s*$)/g, '');
    };
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
    String.isIdCard = (idcard) => {
        const reg = new RegExp('[1-9](\\d{14}$|\\d{17}$|\\d{16}(\\d|X|x))');
        return reg.test(idcard);
    };
    if (!Array.isArray) {
        Array.isArray = arg => Object.prototype.toString.call(arg) === '[object Array]';
    }
    Array.isEmpty = array => !array || !Array.isArray(array) || array.length === 0;
})();
Vue.use(Toast);
/**
 * 注入通用方法
 */
Vue.mixin(mixin);
//
Object.defineProperties(Vue, {
    $toast: {
        value: Toast,
    },
});
//
new Vue({
    el: '#app',
    render: h => h(App),
});
