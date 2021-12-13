import Base from '../../Base.js';

/**
 * @module Core/data/mixin/StoreSort
 */

/**
 * Mixin for Store that handles simple sorting as well as multi-level sorting.
 *
 * ```javascript
 * // single sorter
 * store.sort('age');
 *
 * // single sorter as object, descending order
 * store.sort({ field : 'age', ascending : false });
 *
 * // multiple sorters
 * store.sort(['age', 'name']);
 *
 * // using locale specific sort (slow)
 * store.sort({ field : 'name', useLocaleSort : 'sv-SE' });
 * ```
 *
 * @mixin
 */
export default Target => class StoreSort extends (Target || Base) {
    //region Config

    static get defaultConfig() {
        return {
            /**
             * Default sorters, format is [{ field: '', ascending: false }, ...]
             * @config {Object[]|string[]}
             * @category Common
             */
            sorters : [],

            /**
             * Use `localeCompare()` when sorting, which lets the browser sort in a locale specific order. Set to `true`,
             * a locale string or a locale config to enable.
             *
             * Enabling this has big negative impact on sorting
             * performance. For more info on `localeCompare()`, see [MDN](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/localeCompare).
             *
             * Examples:
             *
             * ```javascript
             * const store = new Store({
             *     // Swedish sorting
             *     useLocaleSort : 'sv-SE'
             * });
             *
             * const store = new Store({
             *     // Swedish sorting with custom casing order
             *     useLocaleSort : {
             *         locale    : 'sv-SE',
             *         caseFirst : 'upper'
             *     }
             * });
             * ```
             *
             * Can also be configured on a per sorter basis:
             *
             * ```javascript
             * store.sort({ field: 'name', useLocaleSort : 'sv-SE' });
             * ```
             *
             * @config {Boolean|String|Object}
             * @default false
             */
            useLocaleSort : null
        };
    }

    //endregion

    //region Events

    /**
     * Fired before sorting
     * @event beforeSort
     * @param {Core.data.Store} source This Store
     * @param {Object[]} sorters Sorter configs
     * @param {Core.data.Model[]} records Records to sort
     */

    /**
     * Fired after sorting
     * @event sort
     * @param {Core.data.Store} source This Store
     * @param {Object[]} sorters Sorter configs
     * @param {Core.data.Model[]} records Sorted records
     */

    //endregion

    //region Properties

    /**
     * Currently applied sorters
     * @member {Object[]} sorters
     * @readonly
     * @category Sort, group & filter
     */

    /**
     * Is store sorted?
     * true
     * @property {Boolean}
     * @readonly
     */
    get isSorted() {
        return Boolean(this.sorters.length) || this.isGrouped;
    }

    //endregion

    //region Add & remove sorters

    /**
     * Sort records, either by replacing current sorters or by adding to them. A sorter can specify a custom sorter
     * function which will be called with arguments (ascending, recordA, recordB). Works in the same way as a standard
     * array sorter, except that returning null triggers the stores normal sorting routine.
     * ```
     * store.sort('age');
     * store.sort(['age', 'name']);
     * store.sort({ field: 'age', fn: (a, b, dir) => sorting logic... }
     * store.sort({ field : 'name', useLocaleSort : 'sv-se' });
     * ```
     * @param {String|Object} field Field to sort by (can also be an array of sorters or a config containing a custom sort fn)
     * @param {Boolean} [ascending] Sort order (used only if field specified as string)
     * @param {Boolean} [add] Add a sorter or use only this sorter (used only if field specified as string)
     * @param {Boolean} [silent] Set as true to not fire events
     * @category Sort, group & filter
     * @fires beforeSort
     * @fires sort
     * @fires refresh
     */
    sort(field, ascending, add = false, silent = false) {
        const
            me = this,
            records = me.allRecords,
            currentSorters = me.sorters ? me.sorters.slice() : [];

        let currentDir = null,
            curSort;

        if (field) {
            if (Array.isArray(field)) {
                me.sorters = field.map(sorter => {
                    if (typeof sorter === 'string') return { field : sorter };
                    return sorter;
                });
            }
            else {
                // extract field name if sorting by config object
                const fieldName = (typeof field === 'object') ? field.field : field;

                // check if currently sorted by this field
                curSort = me.sorters.find(sorter => sorter.field === fieldName);

                // sort in opposite direction if not specified and already sorted, default to sorting ascending
                if (ascending === undefined || ascending === null) {
                    let sameField = curSort && ((typeof field === 'string' && curSort.field === field) || (typeof field === 'object' && curSort.field === field.field));

                    ascending = sameField ? !curSort.ascending : true;
                }

                const sorter = {
                    field     : fieldName,
                    ascending : ascending
                };

                if (typeof field === 'object') {
                    sorter.fn = field.fn;
                    sorter.useLocaleSort = field.useLocaleSort;
                }

                if (add) {
                    // Field already among sorters? change sort direction instead of adding new sorter
                    if (curSort) {
                        currentDir = curSort.ascending;
                        curSort.ascending = ascending;
                    }
                    else {
                        me.sorters.push(sorter);
                    }
                }
                else {
                    me.sorters = [sorter];
                }
            }
        }

        if (!silent && me.trigger('beforeSort', { sorters : me.sorters, records, currentSorters }) === false) {
            // Restore sorters
            me.sorters = currentSorters;

            // Restore sorting direction if toggled
            if (currentDir !== null) {
                curSort.ascending = currentDir;
            }

            return;
        }

        return me.performSort(silent);
    }

    /**
     * Add a sorting level (a sorter).
     * @param {String|Object} field Field to sort by (can also be an array of sorters or a config containing a custom sort fn)
     * @param {Boolean} ascending Sort order (used only if field specified as string)
     * @category Sort, group & filter
     */
    addSorter(field, ascending = true) {
        this.sort(field, ascending, true);
    }

    /**
     * Remove a sorting level (a sorter)
     * @param field Stop sorting by this field
     * @category Sort, group & filter
     */
    removeSorter(field) {
        let me          = this,
            sorterIndex = me.sorters.findIndex(sorter => sorter.field == field);
        if (sorterIndex > -1) {
            me.sorters.splice(sorterIndex, 1);
            me.sort();
        }
    }

    /**
     * Removes all sorters, turning store sorting off.
     * @category Sort, group & filter
     */
    clearSorters() {
        const me = this;

        me.sorters.length = 0;

        me.sort();
    }

    //region

    //region Sorting logic

    /**
     * Creates a function used with Array#sort when sorting the store. Override to use your own custom sorting logic.
     * @param sorters
     * @returns {Function}
     * @category Sort, group & filter
     */
    createSorterFn(sorters) {
        const storeLocaleSort = this.useLocaleSort;

        return (lhs, rhs) => {
            // Cannot use `for ... of` here, breaks a test in IE11 were this fn is called from transpiled test code
            // which leads to iterator symbol mismatch
            for (let i = 0; i < sorters.length; i++) {
                const
                    { field, ascending = true, fn = null, useLocaleSort = storeLocaleSort } = sorters[i],
                    direction = ascending ? 1 : -1;

                if (fn) {
                    const val = fn(lhs, rhs);
                    if (val !== null) {
                        return val * direction;
                    }
                }

                const
                    lhsValue = lhs[field],
                    rhsValue = rhs[field];

                if (lhsValue == null) {
                    return -direction;
                }
                if (rhsValue == null) {
                    return direction;
                }

                if (useLocaleSort) {
                    // Use systems locale
                    if (useLocaleSort === true) {
                        return String(lhsValue).localeCompare(rhsValue) * direction;
                    }

                    // Use specified locale
                    if (typeof useLocaleSort === 'string') {
                        return String(lhsValue).localeCompare(rhsValue, useLocaleSort) * direction;
                    }

                    // Use locale config
                    if (typeof useLocaleSort === 'object') {
                        return String(lhsValue).localeCompare(rhsValue, useLocaleSort.locale, useLocaleSort) * direction;
                    }
                }

                if (lhsValue > rhsValue) {
                    return direction;
                }
                if (lhsValue < rhsValue) {
                    return -direction;
                }
            }

            return 0;
        };
    }

    /**
     * Perform sorting according to the {@link #config-sorters} configured.
     * This is the internal implementation which is overridden in {@link Core.data.AjaxStore} and
     * must not be overridden.
     * @private
     * @category Sort, group & filter
     */
    performSort(silent) {
        const
            me = this,
            { rootNode, storage, sorters } = me,
            sorter = me.createSorterFn(me.isGrouped ? me.groupers.concat(sorters) : sorters);

        // Temporarily remove group headers, will be re-added after sort
        if (me.isGrouped) {
            // Remember which groups are collapsed
            if (me.storeCollapsedGroups()) {
                storage.replaceValues(me.removeHeadersAndFooters(storage.values), true);
            }
        }

        if (me.tree) {
            rootNode.traverse(node => {
                if (node.isLoaded && node.isParent) {
                    node.children = node.children.sort(sorter);
                    // Since child nodes change order their parentIndex needs to be updated
                    node.updateChildrenIndex(node.children);
                }
            });
            storage.replaceValues(me.collectDescendants(rootNode).visible, true);
        }
        else {
            storage.replaceValues(storage.values.sort(sorter), true);
        }

        me.afterPerformSort(silent);
    }

    afterPerformSort(silent) {
        const
            me = this,
            { storage } = me;

        me._idMap = null;

        // Apply grouping
        if (me.isGrouped) {
            storage.replaceValues(me.prepareGroupRecords(storage.values), true);

            // Re-collapse the groups
            me.restoreCollapsedGroups();
        }

        if (!silent) {
            const event = {
                action  : 'sort',
                sorters : me.sorters,
                records : me.allRecords
            };
            me.trigger('sort',    event);

            // Only fire this event if it's a local sort.
            // If we are configured with sortParamName, the loadData will fire it.
            if (!me.sortParamName) {
                me.trigger('refresh', event);
            }
        }
    }

    //endregion
};
