import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';

/**
 * @module Core/helper/IdHelper
 */

function isInstanceOfClass(instance, type) {
    const classHierarchy = instance.classHierarchy();

    for (let i = 0; i < classHierarchy.length; i++) {
        if (classHierarchy[i].$name.toLowerCase() === type) {
            return true;
        }
    }

    return false;
}

/**
 * IdHelper keeps an internal map of objects and ids, allowing easy retrieval by id. It can generate and assign
 * unique ids (in IdHelper context, not guaranteed to be unique on page), either explicitly or when registering objects
 * in its map.
 *
 * IdHelper also supplies some useful functions for querying Widgets.
 *
 * This class is not intended for application use, it is used internally by the Brytum infrastructure.
 * @internal
 * @private
 */
export default class IdHelper {
    /**
     * Generate a new id, using IdHelpers internal counter and a prefix
     * @param {String} prefix Id prefix
     * @returns {String} Generated id
     */
    static generateId(prefix = 'generatedId') {
        return prefix + (++IdHelper.idCounter);
    }

    /**
     * Register an object with IdHelper, assigning it a generated id if it has none. Throws if objects id is already
     * in use.
     * @param {Object} obj Any object
     * @param {String} prefix Prefix for generated id
     */
    static register(obj, prefix = '') {
        let objId = obj.id;

        if (objId == null) {
            objId = obj.id = IdHelper.generateId(prefix);
            obj.hasGeneratedId = true;
        }

        // Code editor sets `disableThrow` to not get conflicts when loading the same module again
        if (objId in IdHelper.idMap && !this.disableThrow) {
            throw new Error('Id ' + objId + ' already in use');
        }

        IdHelper.idMap[objId] = obj;
    }

    /**
     * Unregister from IdHelper, normally done on destruction
     * @param {Object} obj Object to unregister
     */
    static unregister(obj) {
        // Have to check for identity in case another instance by the same id has been created.
        if (IdHelper.idMap[obj.id] === obj) {
            delete IdHelper.idMap[obj.id];
        }
    }

    /**
     * Get an object using id
     * @param {String} id Object id
     * @returns {*} Object or undefined if none found
     */
    static get(id) {
        return IdHelper.idMap[id];
    }

    /**
     * Analogous to document.querySelector, finds the first Bryntum widget matching the passed
     * selector. Right now, only class name (lowercased) selector strings, or
     * a filter function which returns `true` for required object are allowed:
     * ```
     * bryntum.query('grid').destroy();
     * ```
     * @param {String|Function} selector A lowercased class name, or a filter function.
     * @param {Boolean} [deep] Specify `true` to search the prototype chain (requires supplying a string `selector`). For
     *   example 'widget' would then find a Grid
     * @return {Core.widget.Widget} The first matched widget if any.
     */
    static query(selector, deep = false) {
        const idMap = IdHelper.idMap;

        for (let id in idMap) {
            if (widgetMatches(idMap[id], selector, deep)) {
                return idMap[id];
            }
        }
        return null;
    }

    /**
     * Analogous to document.querySelectorAll, finds all Bryntum widgets matching the passed
     * selector. Right now, only registered widget `type` strings, or a filter function which
     * returns `true` for required object are allowed:
     * ```
     * let allFields = bryntum.queryAll('field', true);
     * ```
     * @param {String|Function} selector A lowercased class name, or a filter function.
     * @param {Boolean} [deep] Specify `true` to search the prototype chain (requires supplying a string `selector`). For
     *   example 'widget' would then find a Grid
     * @return {Core.widget.Widget[]} The first matched widgets if any - an empty array will be returned
     * if no matches are found.
     */
    static queryAll(selector, deep = false) {
        const
            idMap  = IdHelper.idMap,
            result = [];

        for (let id in idMap) {
            if (widgetMatches(idMap[id], selector, deep)) {
                result.push(idMap[id]);
            }
        }
        return result;
    }

    /**
     * Returns the Widget which owns the passed element (or event).
     * @param {HTMLElement|Event} element The element or event to start from
     * @param {String|Function} [type] The type of Widget to scan upwards for. The lowercase
     * class name. Or a filter function which returns `true` for the required Widget.
     * @param {HTMLElement|Number} [limit] The number of components to traverse upwards to find a
     * match of the type parameter, or the element to stop at.
     * @return {Widget} The found Widget or null.
     */
    static fromElement(element, type, limit) {
        const typeOfType = typeof type;

        // Event passed
        if (element && element instanceof Event) {
            element = element.target;
        }

        if (typeOfType === 'number' || type && type.nodeType === 1) {
            limit = type;
            type = null;
        }

        let target = element,
            cache = IdHelper.idMap,
            depth = 0,
            topmost, cmpId, cmp;

        if (typeof limit !== 'number') {
            topmost = limit;
            limit = Number.MAX_VALUE;
        }
        if (typeOfType === 'string') {
            type = type.toLowerCase();
        }

        while (target && target.nodeType === 1 && depth < limit && target !== topmost) {
            cmpId = (target.dataset && target.dataset.ownerCmp) || target.id;

            if (cmpId) {
                cmp = cache[cmpId];
                if (cmp) {
                    if (type) {
                        if (typeOfType === 'function') {
                            if (type(cmp)) {
                                return cmp;
                            }
                        }
                        else if (widgetMatches(cmp, type) || isInstanceOfClass(cmp, type)) {
                            return cmp;
                        }
                    }
                    else {
                        return cmp;
                    }
                }

                // Increment depth on every *Widget* found
                depth++;
            }

            target = target.parentNode;
        }

        return null;
    }
}

export function widgetMatches(candidate, selector, deep) {
    if (selector === '*') {
        return true;
    }
    if (typeof selector === 'function') {
        return selector(candidate);
    }
    return BryntumWidgetAdapterRegister.isType(candidate, selector, deep);
}

IdHelper.idCounter = 0;
IdHelper.idMap = {};

// Simplify querying widgets by exposing fns in bryntum ns
(window.bryntum || (window.bryntum = {})).get = IdHelper.get;
window.bryntum.IdHelper = IdHelper;
window.bryntum.query = IdHelper.query;
window.bryntum.queryAll = IdHelper.queryAll;
window.bryntum.fromElement = IdHelper.fromElement;
