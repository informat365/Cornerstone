import DateHelper from './DateHelper.js';
import Base from '../Base.js';

/**
 * @module Core/helper/ObjectHelper
 */

const whiteSpaceRe = /\s+/,
    typeOf = value => {
        const baseType = typeof value;

        // If not atomic type, we handle date or null
        if (baseType === 'object') {
            if (value === null) {
                return 'null';
            }
            if (Object.prototype.toString.call(value) === '[object Date]') {
                return 'date';
            }
        }
        return baseType;
    };

// Detect if browser has bad implementation of toFixed()
const toFixedFix = (1.005).toFixed(2) === '1.01' ? null : function(number, fractionDigits) {
    const split = number.toString().split('.'),
        newNumber = +(!split[1] ? split[0] : split.join('.') + '1');

    return number.toFixed.call(newNumber, fractionDigits);
};

/**
 * Helper for Object manipulation.
 */
export default class ObjectHelper {
    /**
     * Checks if two values are equal. Basically === but special handling of dates.
     * @param a First value
     * @param b Second value
     * @returns {*} true if values are equal, otherwise false
     */
    static isEqual(a, b, useIsDeeply = false) {
        // Eliminate null vs undefined mismatch
        if (
            (a === null && b !== null) ||
            (a === undefined && b !== undefined) ||
            (b === null && a !== null) ||
            (b === undefined && a !== undefined)
        ) {
            return false;
        }

        // Covers undefined === undefined and null === null, since mismatches are elminated above
        if (a == null && b == null) {
            return true;
        }

        // The same instance should equal itself.
        if (a === b) {
            return true;
        }

        const
            typeA = typeof a,
            typeB = typeof b;

        if (typeA === typeB) {
            switch (typeA) {
                case 'number':
                case 'string':
                case 'boolean':
                    return a === b;
            }

            switch (true) {
                case a instanceof Date && b instanceof Date:
                    // faster than calling DateHelper.isEqual
                    // https://jsbench.me/3jk2bom2r3/1
                    return a.getTime() === b.getTime();

                case Array.isArray(a) && Array.isArray(b):
                    return a.length === b.length ? a.every((v, idx) => this.isEqual(v, b[idx], useIsDeeply)) : false;

                case typeA === 'object' && a.constructor.prototype === b.constructor.prototype:
                    return useIsDeeply ? this.isDeeplyEqual(a, b, useIsDeeply) : JSON.stringify(a) === JSON.stringify(b);
            }
        }

        return String(a) === String(b);
    }

    /**
     * Checks if two objects are deeply equal
     * @param {Object} a
     * @param {Object} b
     * @param {Object} [options] Additional comparison options
     * @param {Object} [options.ignore] Map of property names to ignore when comparing
     * @param {Function} [options.shouldEvaluate] Function used to evaluate if a property should be compared or not.
     * Return false to prevent comparision
     * @param {Function} [options.evaluate] Function used to evaluate equality. Return `true`/`false` as evaluation
     * result or anything else to let `isEqual` handle the comparision
     * @returns {Boolean}
     */
    static isDeeplyEqual(a, b, options = {}) {
        // TODO : When syncing to DOM, it first checks the outermost config for equality with the previously used one
        //  and then drills downs for each task. If lets say the third task was changed, the two prior have already been
        //  determined to be equal before drilling down. That check could be persisted on the incoming object (a) to
        //  skip comparing it again on the more detailed level. Since (a) in this case is regenerated on ech draw there
        //  is no risk of polluting the next render

        // Same object, equal :)
        if (a === b) {
            return true;
        }

        // Nothing to compare, not equal
        if (!a || !b) {
            return false;
        }

        // Property names excluding ignored
        const
            aKeys = this.keys(a, options.ignore),
            bKeys = this.keys(b, options.ignore);

        // Property count differs, not equal
        if (aKeys.length !== bKeys.length) {
            return false;
        }

        for (let i = 0; i < aKeys.length; i++) {
            const aKey = aKeys[i];
            const bKey = bKeys[i];

            // Property name differs, not equal
            if (aKey !== bKey) {
                return false;
            }

            const aVal = a[aKey];
            const bVal = b[bKey];

            // Allow caller to determine if property values should be evaluated or not
            // TODO: Not currently used
            if (options.shouldEvaluate) {
                if (options.shouldEvaluate(
                    aKey,
                    {
                        value  : aVal,
                        object : a
                    }, {
                        value  : bVal,
                        object : b
                    }
                ) === false) {
                    continue;
                }
            }

            // Allow caller to determine equality of properties
            if (options.evaluate) {
                const result = options.evaluate(aKey, {
                    value  : aVal,
                    object : a
                }, {
                    value  : bVal,
                    object : b
                });

                // Not equal
                if (result === false) {
                    return false;
                }

                // Equal, skip isEqual call below
                if (result === true) {
                    continue;
                }
            }

            // Values differ, not equal (also digs deeper)
            if (!this.isEqual(aVal, bVal, options)) {
                return false;
            }
        }

        // Found to be equal
        return true;
    }

    /**
     * Checks if value B is partially equal to value A.
     * @param a First value
     * @param b Second value
     * @returns {Boolean} true if values are partially equal, false otherwise
     */
    static isPartial(a, b) {
        a = String(a).toLowerCase();
        b = String(b).toLowerCase();

        return a.indexOf(b) != -1;
    }

    /**
     * Checks if value a is smaller than value b.
     * @param a First value
     * @param b Second value
     * @returns {Boolean} true if a < b
     */
    static isLessThan(a, b) {
        if (a instanceof Date && b instanceof Date) {
            return DateHelper.isBefore(a, b);
        }
        return a < b;
    }

    /**
     * Checks if value a is bigger than value b.
     * @param a First value
     * @param b Second value
     * @returns {Boolean} true if a > b
     */
    static isMoreThan(a, b) {
        if (a instanceof Date && b instanceof Date) {
            return DateHelper.isAfter(a, b);
        }
        return a > b;
    }

    /**
     * Used by the Base class to make deep copies of defaultConfig blocks
     * @private
     */
    static fork(obj) {
        var ret, key, value;

        if (obj && obj.constructor === Object) {
            ret = Object.setPrototypeOf({}, obj);

            for (key in obj) {
                value = obj[key];

                if (value) {
                    if (value.constructor === Object) {
                        ret[key] = this.fork(value);
                    }
                    else if (value instanceof Array) {
                        ret[key] = value.slice();
                    }
                }
            }
        }
        else {
            ret = obj;
        }

        return ret;
    }

    static assign(dest, ...sources) {
        var i = 0,
            ln = sources.length,
            source, key;

        for (; i < ln; i++) {
            source = sources[i];

            for (key in source) {
                dest[key] = source[key];
            }
        }
        return dest;
    }

    static clone(source) {
        if (source == null) {
            return source;
        }

        var type = source.constructor.name,
            result = source,
            i, key;

        // Date
        if (type === 'Date') {
            return new Date(source.getTime());
        }

        // Array
        if (type === 'Array') {
            i = source.length;

            result = [];

            while (i--) {
                result[i] = this.clone(source[i]);
            }
        }
        // Object
        else if (type === 'Object') {
            result = {};

            for (key in source) {
                result[key] = this.clone(source[key]);
            }
        }

        return result;
    }

    static merge(dest, ...sources) {
        var i = 0,
            ln = sources.length,
            source, key, value, sourceKey;

        for (; i < ln; i++) {
            source = sources[i];

            for (key in source) {
                value = source[key];
                if (value && value.constructor === Object) {
                    sourceKey = dest[key];
                    if (sourceKey && sourceKey.constructor.name === 'Object') {
                        this.merge(sourceKey, value);
                    }
                    else {
                        dest[key] = this.clone(value);
                    }
                }
                else {
                    dest[key] = value;
                }
            }
        }

        return dest;
    }

    /**
     * Copies the named properties from the `source` parameter into the `dest` parameter.
     * @param {Object} dest The destination into which properties are copied.
     * @param {Object} source The source from which properties are copied.
     * @param {String[]} props The list of property names.
     * @returns The `dest` object.
     */
    static copyProperties(dest, source, props) {
        let prop, i;
        for (i = 0; i < props.length; i++) {
            prop = props[i];
            if (prop in source) {
                dest[prop] = source[prop];
            }
        }
        return dest;
    }

    /**
     * Copies the named properties from the `source` parameter into the `dest` parameter
     * unless the property already exists in the `dest`.
     * @param {Object} dest The destination into which properties are copied.
     * @param {Object} source The source from which properties are copied.
     * @param {String[]} props The list of property names.
     * @returns The `dest` object.
     */
    static copyPropertiesIf(dest, source, props) {
        for (const prop of props) {
            if (!(prop in dest)) {
                dest[prop] = source[prop];
            }
        }
        return dest;
    }

    /**
     * Returns an array containing all enumerable property names from every prototype level for the object.
     * @param {Object} object Object to retrieve property names from
     * @param {Object} [ignore] Optional map of names to ignore
     * @returns {String[]} All keys from every prototype level.
     */
    static keys(object, ignore = null) {
        const result = [];

        for (const p in object) {
            if (!ignore || !ignore[p]) {
                result.push(p);
            }
        }
        return result;
    }

    /**
     * Tests whether a passed object has any enumerable properties.
     * @param {Object} object
     * @returns {Boolean} `true` if the passed object has no enumerable properties.
     */
    static isEmpty(object) {
        for (const p in object) { // eslint-disable-line no-unused-vars
            return false;
        }
        return true;
    }

    /**
     * Gathers the names of properties which have truthy values into an array.
     *
     * This is useful when gathering CSS class names for complex element production.
     * Instead of appending to an array or string which may already contain the
     * name, and instead of contending with space separation and concatenation
     * and conditional execution, just set the properties of an object:
     *
     *     cls = {
     *         [this.selectedCls] : this.isSelected(thing),
     *         [this.dirtyCls] : this.isDirty(thing)
     *     };
     *
     * @param {Object} source Source of keys to gather into an array.
     * @returns {String[]} The keys which had a truthy value.
     */
    static getTruthyKeys(source) {
        const keys = Object.keys(source);

        for (let i = 0; i < keys.length;) {
            if (source[keys[i]]) {
                i++;
            }
            else {
                keys.splice(i, 1);
            }
        }

        return keys;
    }

    /**
     * Gathers the values of properties which are truthy into an array.
     * @param {Object} source Source of values to gather into an array.
     * @returns {String[]} The truthy values from the passed object.
     */
    static getTruthyValues(source) {
        const keys = Object.keys(source);

        for (let i = 0; i < keys.length;) {
            if (source[keys[i]]) {
                keys[i] = source[keys[i++]];
            }
            else {
                keys.splice(i, 1);
            }
        }

        return keys;
    }

    /**
     * Converts a list of names, either an a space separated string, or
     * from an array, into a series of properties in an object with truthy
     * values. The Converse of {@link #function-getTruthyKeys-static}
     * @param {String|String[]} The list of names to convert to object form.
     */
    static createTruthyKeys(source) {
        if (typeof source === 'string') {
            source = source.split(whiteSpaceRe);
        }
        const result = {};

        for (const key of source) {
            if (key.length) {
                result[key] = 1;
            }
        }

        return result;
    }

    /**
     * Returns an array of a given object's properties names including properties in
     * all superclasses, in the same order as we get with a normal loop.
     * @param {Object} source An object which may have properties in a prototype
     * chain, such as a configuration (These are chained because of inheritance).
     * @returns {String[]} The property names.
     */
    static allKeys(object) {
        const result = [];

        for (object; object; object = Object.getPrototypeOf(object)) {
            result.push(...Object.keys(object));
        }

        return result;
    }

    /**
     * Checks if a given path exists in an object
     * @param {Object} object Object to check path on
     * @param {String} path Dot-separated path, e.g. 'object.childObject.someKey'
     * @returns {Boolean} Returns `true` if path exists or `false` if it does not
     */
    static pathExists(object, path) {
        const properties = path.split('.');

        return properties.every(property => {
            if (!(property in object)) {
                return false;
            }
            object = object[property];
            return true;
        });
    }

    /**
     * Returns value for a given path in the object
     * @param {Object} object Object to check path on
     * @param {String} path Dot-separated path, e.g. 'object.childObject.someKey'
     * @returns {*} Value associated with passed key
     */
    static getPath(object, path) {
        return path.split('.').reduce((result, key) => {
            return (result || {})[key];
        }, object);
    }

    /**
     * Sets value for a given path in the object
     * @param {Object} object Target object
     * @param {String} path Dot-separated path, e.g. 'object.childObject.someKey'
     * @param {*} value Value for a given path
     */
    static setPath(object, path, value) {
        path.split('.').reduce((result, key, index, array) => {
            const isLast = index === array.length - 1;

            if (isLast) {
                return result[key] = value;
            }
            else if (!(result[key] instanceof Object)) {
                result[key] = {};
            }

            return result[key];
        }, object);
    }

    static coerce(from, to) {
        var fromType = typeOf(from),
            toType = typeOf(to),
            isString = typeof from === 'string';

        if (fromType !== toType) {
            switch (toType) {
                case 'string':
                    return String(from);
                case 'number':
                    return Number(from);
                case 'boolean':
                    // See http://ecma262-5.com/ELS5_HTML.htm#Section_11.9.3 as to why '0'.
                    // TL;DR => ('0' == 0), so if given string '0', we must return boolean false.
                    return isString && (!from || from === 'false' || from === '0') ? false : Boolean(from);
                case 'null':
                    return isString && (!from || from === 'null') ? null : false;
                case 'undefined':
                    return isString && (!from || from === 'undefined') ? undefined : false;
                case 'date':
                    return isString && isNaN(from) ? DateHelper.parse(from) : Date(Number(from));
            }
        }
        return from;
    }

    static wrapProperty(object, propertyName, newGetter, newSetter, deep = true) {
        const newProperty = {};

        let proto = Object.getPrototypeOf(object),
            existingProperty = Object.getOwnPropertyDescriptor(proto, propertyName);

        while (!existingProperty && proto && deep) {
            proto = Object.getPrototypeOf(proto);
            if (proto) {
                existingProperty = Object.getOwnPropertyDescriptor(proto, propertyName);
            }
        }

        if (existingProperty) {
            if (existingProperty.set) {
                newProperty.set = v => {
                    existingProperty.set.call(object, v);

                    // Must invoke the getter in case "v" has been transformed.
                    newSetter && newSetter.call(object, existingProperty.get.call(object));
                };
            }
            else {
                newProperty.set = newSetter;
            }
            if (existingProperty.get) {
                newProperty.get = () => {
                    let result = existingProperty.get.call(object);
                    if (newGetter) {
                        result = newGetter.call(object, result);
                    }
                    return result;
                };
            }
            else {
                newProperty.get = newGetter;
            }
        }
        else {
            newProperty.set = v => {
                object[`_${propertyName}`] = v;
                newSetter && newSetter.call(object, v);
            };
            newProperty.get = () => {
                let result = object[`_${propertyName}`];
                if (newGetter) {
                    result = newGetter.call(object, result);
                }
                return result;
            };
        }
        Object.defineProperty(object, propertyName, newProperty);
    }

    /**
     * Finds a property descriptor for the passed object from all inheritance levels.
     * @param {Object} object The Object whos property to find.
     * @param {String} propertyName The name of the property to find.
     * @returns {Object} An ECMA property descriptor is the property was found, otherwise `null`
     */
    static getPropertyDescriptor(object, propertyName) {
        let result = null;
        for (let obj = object; !result && obj !== Base; obj = Object.getPrototypeOf(obj)) {
            result = Object.getOwnPropertyDescriptor(obj, propertyName);
        }
        return result;
    }

    /**
     * Changes the passed object and removes all null and undefined properties from it
     * @param {Object} object Target object
     * @returns {Object} Passed object
     */
    static cleanupProperties(obj) {
        Object.entries(obj).forEach(([key, value]) => value == null && delete obj[key]);
        return obj;
    }

    /**
     * Checks that the supplied value is of the specified type. Throws if it is not
     * @param {Object} value Value to check type of
     * @param {String} type Expected type
     * @param {String} name Name of the value, used in error message
     */
    static assertType(value, type, name) {
        // eslint-disable-next-line valid-typeof
        if (value != null && typeof value !== type) {
            throw new Error(`Incorrect type "${typeof value}" for ${name}, expected "${type}"`);
        }
    }

    /**
     * Checks that the supplied value is a number.  Throws if it is not
     * @param {Object} value Value to check type of
     * @param {String} name Name of the value, used in error message
     */
    static assertNumber(value, name) {
        this.assertType(value, 'number', name);
    }

    /**
     * Checks that the supplied value is a boolean.  Throws if it is not
     * @param {Object} value Value to check type of
     * @param {String} name Name of the value, used in error message
     */
    static assertBoolean(value, name) {
        this.assertType(value, 'boolean', name);
    }

    /**
     * Number.toFixed(), with polyfill for browsers that needs it
     * @param {Number} number
     * @param {Number} digits
     * @returns {String} A fixed point string representation of the passed number.
     */
    static toFixed(number, digits) {
        if (toFixedFix) {
            return toFixedFix(number, digits);
        }

        return number.toFixed(digits);
    }

    /**
     * Round the passed number to the passed number of decimals.
     * @param {Number} number The number to round.
     * @param {Number} digits The number of decimal places to round to.
     * @returns {Number} The number rounded to the passed number of decimal places.
     */
    static round(number, digits) {
        // Undefined or null means do not round. NOT round to no decimals.
        if (digits == null) {
            return number;
        }

        const factor = 10 ** digits;

        return Math.round(number * factor) / factor;
    }
}
