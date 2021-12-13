/**
 * @module Core/helper/BrowserHelper
 */

/**
 * Static helper class that does browser/feature detection.
 * @internal
 */
export default class BrowserHelper {

    static cacheFlags(platform = navigator.platform, userAgent = navigator.userAgent) {
        const me = this;

        // os
        me._isLinux = Boolean(platform.match(/Linux/));
        me._isMac = Boolean(platform.match(/Mac/));
        me._isWindows = Boolean(platform.match(/Win32/));

        // browser
        me._isIE11 = Boolean(userAgent.match(/rv:11/));

        me._edgeVersion = me.getVersion(userAgent, /Edge\/(\d+)\./);
        me._isEdge = me._edgeVersion > 0;

        // Edge user agent contain webkit too
        me._isWebkit = Boolean(userAgent.match(/WebKit/)) && !me._isEdge;

        me._firefoxVersion = me.getVersion(userAgent, /Firefox\/(\d+)\./);
        me._isFirefox = me._firefoxVersion > 0;

        me._chromeVersion = !me._isEdge ? me.getVersion(userAgent, /Chrom(?:e|ium)\/(\d+)\./) : 0;
        me._isChrome = me._chromeVersion > 0;

        me._isSafari = Boolean(userAgent.match(/Safari/)) && !me._isChrome && !me._isEdge;
        me._isMobileSafari = Boolean(userAgent.match(/Mobile.*Safari/));

        me._isAndroid = Boolean(userAgent.match(/Android/g));

        try {
            document.querySelector(':scope');
            // Scoped queries are not supported for custom element polyfill in firefox
            // https://app.assembla.com/spaces/bryntum/tickets/6781
            me.supportsQueryScope = !me._isFirefox;
        }
        catch (e) {
            me.supportsQueryScope = false;
        }

        me._supportsPassive = false;
        try {
            // If the browser asks the options object to yield its passive
            // property, we know it supports the object form options object
            // and passive listeners.
            document.addEventListener('__notvalid__', null, {
                get passive() {
                    me._supportsPassive = true;
                }
            });
        }
        catch (e) {}

        //document.addEventListener("DOMContentLoaded", me.onDocumentReady);
    }

    // NOTE: Not allowed with CSP, moved to Grid#render()
    // /**
    //  * Feature and browser detection which requires the document to be loaded.
    //  * @private
    //  */
    // static onDocumentReady() {
    //     // Firefox includes a part of the Y scroller jutting up which is not needed.
    //     // Also need to "mitre" the horizontal scrollbar to leave the gap at the end.
    //     if (BrowserHelper.isFirefox) {
    //         CSSHelper.insertRule(`.b-virtual-scroller {height: ${DomHelper.scrollBarWidth}px;}`);
    //         CSSHelper.insertRule(`.b-virtual-scrollers {padding-right: ${DomHelper.scrollBarWidth}px;}`);
    //     }
    // }

    /**
     * Returns matched version for userAgent
     * @param String versionRe version match regular expression
     * @returns {Number} matched version
     * @readonly
     * @private
     */
    static getVersion(userAgent, versionRe) {
        const match = userAgent.match(versionRe);
        return match ? parseInt(match[1]) : 0;
    }

    /**
     * Tries to determine if the user is using a touch device
     * @returns {Boolean}
     * @readonly
     */
    static get isTouchDevice() {
        if ('_isTouchDevice' in this) return this._isTouchDevice;

        return (('ontouchstart' in window) ||
            // edge tends to always have this with a value 2
            (!this.isEdge && navigator.maxTouchPoints > 0) ||
            // but if env is actually touchable, then window has this class present
            (this.isEdge && window.TouchEvent) ||
            (navigator.msMaxTouchPoints > 0));
    }

    // Since touch screen detection is unreliable we should allow client to configure it, or detect first touch
    static set isTouchDevice(value) {
        this._isTouchDevice = value;
    }

    /**
     * Checks if platform is mac
     * @returns {Boolean}
     * @readonly
     */
    static get isMac() {
        return this._isMac;
    }

    /**
     * Checks if platform is windows
     * @returns {Boolean}
     * @readonly
     */
    static get isWindows() {
        return this._isWindows;
    }

    /**
     * Checks if platform is linux
     * @returns {Boolean}
     * @readonly
     */
    static get isLinux() {
        return this._isLinux;
    }

    /**
     * Checks if browser is IE11
     * @returns {Boolean}
     * @readonly
     */
    static get isIE11() {
        return this._isIE11;
    }

    /**
     * Checks if browser is Edge
     * @returns {Boolean}
     * @readonly
     */
    static get isEdge() {
        return this._isEdge;
    }

    /**
     * Find the major Edge version
     * @returns {Number} Edge version or 0 for other browsers
     * @readonly
     */
    static get edgeVersion() {
        return this._edgeVersion;
    }

    /**
     * Checks if browser is Webkit
     * @returns {Boolean}
     * @readonly
     */
    static get isWebkit() {
        return this._isWebkit;
    }

    /**
     * Checks if browser is Chrome
     * @returns {Boolean}
     * @readonly
     */
    static get isChrome() {
        return this._isChrome;
    }

    /**
     * Find the major Chrome version
     * @returns {Number} Chrome version or 0 for other browsers
     * @readonly
     */
    static get chromeVersion() {
        return this._chromeVersion;
    }

    /**
     * Checks if browser is Firefox
     * @returns {Boolean}
     * @readonly
     */
    static get isFirefox() {
        return this._isFirefox;
    }

    /**
     * Find the major Firefox version.
     * @returns {Number} Firefox version or 0 for other browsers
     * @readonly
     */
    static get firefoxVersion() {
        return this._firefoxVersion;
    }
    /**
     * Checks if browser is Safari
     * @returns {Boolean}
     * @readonly
     */
    static get isSafari() {
        return this._isSafari;
    }

    /**
     * Checks if browser is mobile Safari
     * @returns {Boolean}
     * @readonly
     */
    static get isMobileSafari() {
        return this._isMobileSafari;
    }

    static get isAndroid() {
        return this._isAndroid;
    }

    /**
     * Returns `true` if the browser supports passive event listeners.
     */
    static get supportsPassive() {
        return this._supportsPassive;
    }

    // https://developer.mozilla.org/en-US/docs/Web/API/Web_Storage_API/Using_the_Web_Storage_API
    static get storageAvailable() {
        let storage, x;

        try {
            storage = localStorage;
            x = '__storage_test__';

            storage.setItem(x, x);
            storage.removeItem(x);
            return true;
        }
        catch (e) {
            return e instanceof DOMException && (
                // everything except Firefox
                e.code === 22 ||
                // Firefox
                e.code === 1014 ||
                // test name field too, because code might not be present
                // everything except Firefox
                e.name === 'QuotaExceededError' ||
                // Firefox
                e.name === 'NS_ERROR_DOM_QUOTA_REACHED') &&
                // acknowledge QuotaExceededError only if there's something already stored
                storage.length !== 0;
        }
    }

    static setLocalStorageItem(key, value) {
        this.storageAvailable && localStorage.setItem(key, value);
    }

    static getLocalStorageItem(key) {
        return this.storageAvailable && localStorage.getItem(key);
    }

    static removeLocalStorageItem(key) {
        this.storageAvailable && localStorage.removeItem(key);
    }

    /**
     * Returns parameter value from search string by parameter name.
     * @param {String} paramName search parameter name
     * @param {*} [defaultValue] default value if parameter not found
     * @param {String} [search] search string. Defaults to `document.location.search`
     */
    static searchParam(paramName, defaultValue = null, search = document.location.search) {
        const
            re    = new RegExp(`[?&]${paramName}=([^&]*)`),
            match = search.match(re);
        return (match && match[1]) || defaultValue;
    }
}

BrowserHelper.cacheFlags();
