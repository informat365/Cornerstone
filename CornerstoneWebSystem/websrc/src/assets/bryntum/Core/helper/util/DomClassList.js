import ObjectHelper from '../ObjectHelper.js';

/**
 * @module Core/helper/util/DomClassList
 */

/**
 * This class encapsulates a list of CSS classes which can be set as the `className`
 * on an `HTMLElement`.
 *
 * Properties names set on this class equate to *adding* a class if the property's value
 * is _truthy_, or removing a class if the value is _falsy_.
 *
 * ```javascript
 * const myClassList = new DomClassList('b-test-button');
 *
 * myClassList.add('test-class');
 * myClassList.important = 1;
 *
 * myHtmlElement.className = myClassList; // Sets it to "b-test-button test-class important"
 * ```
 */
export default class DomClassList {
    constructor(...classes) {
        if (typeof classes[0] === 'object') {
            Object.assign(this, classes[0]);
        }
        else {
            this.process(1, classes);
        }

        // String value needs recalculating
        this[dirtySymbol] = true;
    }

    /**
     * Returns a clone of this DomClassList with all the same keys set.
     * @returns {Core.helper.util.DomClassList} A clone of this DomClassList.
     */
    clone() {
        return new DomClassList(this);
    }

    // An instance of this class may be assigned directly to an element's className
    // it will be coerced to a string value using this method.
    toString() {
        // Adding space at the end if there is content to make concatenation code simpler in renderers.
        return this.length ? `${this.value} ` : '';
    }

    /**
     * Analogous to string.trim, returns the string value of this `DomClassList` with no trailing space.
     * @returns {String} A concatenated string value of all the class names in this `DomClassList`
     * separated by spaces.
     */
    trim() {
        return this.value;
    }

    /**
     * Compares this ClassList to another ClassList (or class name string of space separated classes).
     * If the *same class names, regardless of order* are present, the two are considered equal.
     *
     * So `new DomClassList('foo bar bletch').isEqual('bletch bar foo')` would return `true`
     * @param {Core.helper.util.DomClassList|String} other The `DomClassList` or string of classes to compare to.
     * @returns {Boolean} `true` if the two contain the same class names.
     */
    isEqual(other) {
        if (typeof other === 'string') {
            testClassList.value = other;
            other = testClassList;
        }

        if (this.length === other.length) {
            const otherClasses = ObjectHelper.getTruthyKeys(other);

            for (let i = 0, len = otherClasses.length; i < len; i++) {
                if (!this[otherClasses[i]]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    get value() {
        const me = this;

        if (me[dirtySymbol]) {
            const keys = ObjectHelper.getTruthyKeys(me);
            me[lengthSymbol] = keys.length;
            me[valueSymbol] = keys.join(' ');
            me[dirtySymbol] = false;
        }
        return me[valueSymbol];
    }

    set value(value) {
        const
            me = this,
            keys = Object.keys(me),
            len = keys.length;

        for (let i = 0; i < len; i++) {
            delete me[keys[i]];
        }

        if (value) {
            me.process(1, [value]);
        }
        else {
            // String value needs recalculating
            me[dirtySymbol] = true;
        }
    }

    get length() {
        // Maintainer: We MUST access the value getter to force
        // the value to be calculated if it's currently dirty.
        return this.value ? this[lengthSymbol] : 0;
    }

    process(value, classes) {
        const len = classes.length;
        for (let i = 0; i < len; i++) {
            if (classes[i]) {
                const cls = classes[i],
                    splitClasses = cls.values ? Array.from(cls.values()) : (cls.item ? Array.from(cls) : cls.split(whiteSpaceRe)),
                    len = splitClasses.length;
                for (let i = 0; i < len; i++) {
                    if (splitClasses[i]) {
                        //<debug>
                        if (splitClasses[i].indexOf(' ') !== -1) {
                            throw new Error('CSS class names added to DomClassList may not contain space');
                        }
                        //</debug>
                        this[splitClasses[i]] = value;
                    }
                }
            }
        }

        // String value needs recalculating
        this[dirtySymbol] = true;
    }

    /**
     * Add CSS class(es)
     * ```
     * myClassList.add('bold', 'small');
     * ```
     * @param {String} classes CSS classes to add
     */
    add(...classes) {
        this.process(1, classes);
    }

    /**
     * Remove CSS class(es)
     * ```
     * myClassList.remove('bold', 'small');
     * ```
     * @param {String} classes CSS classes to remove
     */
    remove(...classes) {
        this.process(0, classes);
    }

    /**
     * Analogous to the `String#split` method, but with no delimiter
     * parameter. This method returns an array containing the individual
     * CSS class names set.
     * @returns {String[]} The individual class names in this `DomClassList`
     */
    split() {
        return ObjectHelper.getTruthyKeys(this);
    }

    forEach(fn) {
        return ObjectHelper.getTruthyKeys(this).forEach(fn);
    }

    // To gain some speed in DomHelper.sync(), faster than instanceof etc
    get isDomClassList() {
        return true;
    }
};

const
    whiteSpaceRe  = /\s+/,
    valueSymbol   = Symbol('value'),
    lengthSymbol  = Symbol('length'),
    dirtySymbol   = Symbol('dirty'),
    testClassList = new DomClassList();
