import StringHelper from '../StringHelper.js';

/**
 * @module Core/helper/util/Fullscreen
 */

/**
 * Encapsulates the functionality related to switching cross-browser to full screen view and back.
 */
export default class Fullscreen {
    static init() {
        const fnNames  = ['fullscreenEnabled', 'requestFullscreen', 'exitFullscreen', 'fullscreenElement'],
            // turns fnNames into function calls to prefixed functions, fullscreenEnabled -> document.mozFullscreenEnabled
            prefixFn = prefix => fnNames.map(fn => {
                let result = prefix + StringHelper.capitalizeFirstLetter(fn);

                // fullscreenEnabled in Firefox is called fullScreenEnabled
                if (prefix === 'moz') {
                    result = result.replace('screen', 'Screen');
                    
                    // #6555 - Crash when clicking full screen button twice
                    // firefox doesn't support exitFullScreen method
                    if ('mozCancelFullScreen' in document && fn === 'exitFullscreen') {
                        result = 'mozCancelFullScreen';
                    }
                }

                return result;
            });

        this.functions = (
            ('fullscreenEnabled' in document && fnNames) ||
            ('webkitFullscreenEnabled' in document && prefixFn('webkit')) ||
            ('mozFullScreenEnabled' in document && prefixFn('moz')) ||
            ('msFullscreenEnabled' in document && prefixFn('ms')) ||
            []
        );

        const eventNames   = [
                'fullscreenchange',
                'fullscreenerror'
            ],
            msEventNames = [
                'MSFullscreenChange',
                'MSFullscreenError'
            ],
            prefixEvt    = prefix => eventNames.map(eventName => prefix + StringHelper.capitalizeFirstLetter(eventName));

        this.events = (
            ('fullscreenEnabled' in document && eventNames) ||
            ('webkitFullscreenEnabled' in document && prefixEvt('webkit')) ||
            ('mozFullscreenEnabled' in document && prefixEvt('moz')) ||
            ('msFullscreenEnabled' in document && msEventNames) ||
            []
        );
    }

    /**
     * True if the fullscreen mode is supported and enabled, false otherwise
     * @property {Boolean}
     */
    static get enabled() {
        return document[this.functions[0]];
    }

    /**
     * Request entering the fullscreen mode. 
     * @param {HTMLElement} element Element to be displayed fullscreen
     */
    static request(element) {
        return element[this.functions[1]](element);
    }

    /**
     * Exit the previously entered fullscreen mode.
     */
    static exit() {
        return document[this.functions[2]]();
    }

    /**
     * True if fullscreen mode is currently active, false otherwise
     * @return {Boolean}
     */
    static get isFullscreen() {
        return !!document[this.functions[3]];
    }

    /**
     * Installs the passed listener to fullscreenchange event
     * @param {Function} fn The listener to install
     */
    static onFullscreenChange(fn) {
        document.addEventListener(this.events[0], fn);
    }

    /**
     * Uninstalls the passed listener from fullscreenchange event
     * @param {Function} fn 
     */
    static unFullscreenChange(fn) {
        document.removeEventListener(this.events[0], fn);
    }
}

Fullscreen.init();
