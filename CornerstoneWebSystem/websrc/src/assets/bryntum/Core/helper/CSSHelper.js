import DomHelper from './DomHelper.js';

/**
 * @module Core/helper/CSSHelper
 */

/**
 * Provides methods to add and manipulate CSS style rules.
 *
 * Note that this class is incompatible with [CSP](https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP)
 *
 * ```
 * this.criticalRule = CSSHelper.insertRule(`#${this.id} .b-sch-event.critical {background-color:${this.criticalColor}}`);
 * ```
 */
export default class CSSHelper {
    /**
     * Inserts a CSS style rule based upon the passed text
     * @param {String} cssText The text of the rule including selector and rule body just as it would
     * be specified in a CSS file.
     * @returns {CSSRule} The resulting CSS Rule object if the add was successful.
     */
    static insertRule(cssText) {
        const
            { styleSheet } = this,
            oldCount = styleSheet.cssRules.length;

        styleSheet.insertRule(cssText, 0);

        // Only return element zero if the add was successful.
        if (styleSheet.cssRules.length > oldCount) {
            return styleSheet.cssRules[0];
        }
    }

    /**
     * Looks up the first rule which matched the passed selector.
     * @param {String/Function} selector Either the selector string to exactly match or a function which
     * when passed a required selector, returns `true`.
     * @returns {CSSRule} The first matching CSS Rule object if any found.
     */
    static findRule(selector) {
        let result,
            isFn = typeof selector === 'function';

        // Array#find will stop when the function returns true, stop when the inner
        // find call yields a value from the search string.
        // Array#find better: to http://www.andygup.net/fastest-way-to-find-an-item-in-a-javascript-array/
        Array.prototype.find.call(document.head.querySelectorAll('link[rel=stylesheet],style[type*=css]'), element => {
            result = Array.prototype.find.call(element.sheet.rules || element.sheet.cssRules, r => {
                return isFn ? selector(r) : r.selectorText === selector;
            });
            if (result) {
                return true;
            }
        });

        return result;
    }

    static get styleSheet() {
        if (!this._stylesheet) {
            this._stylesheet = DomHelper.createElement({
                tag    : 'style',
                id     : 'bryntum-private-styles',
                type   : 'text/css',
                parent : document.head
            }).sheet;
        }
        return this._stylesheet;
    }
}
