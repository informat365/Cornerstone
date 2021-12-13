import Base from '../Base.js';
import Events from '../mixin/Events.js';
import CollectionFilter from './CollectionFilter.js';
import CollectionSorter from './CollectionSorter.js';
import FunctionHelper from '../helper/FunctionHelper.js';

/**
 * @module Core/util/Collection
 */

const return0 = () => 0,
    reverseNumericSortFn = (a, b) => b - a,
    filteredIndicesProperty = Symbol('filteredIndicesProperty'),
    emptyArray = Object.freeze([]),
    sortEvent = Object.freeze({
        action   : 'sort',
        added    : emptyArray,
        removed  : emptyArray,
        replaced : emptyArray
    }),
    filterEvent = Object.freeze({
        action   : 'filter',
        added    : emptyArray,
        removed  : emptyArray,
        replaced : emptyArray
    }),
    keyTypes = {
        string : 1,
        number : 1
    },
    nonPrimitives = new WeakMap(),
    safeIndexKey = (value) => {
        if (value && typeof value === 'object') {
            let substitute = nonPrimitives.get(value);
            if (substitute === undefined) {
                substitute = Symbol('bscik'); // Bryntum safe collection index key
                nonPrimitives.set(value, substitute);
            }
            value = substitute;
        }

        return value;
    };

/**
 * A class which encapsulates a {@link #function-get keyed},
 * {@link #function-addFilter filterable}, {@link #function-addSorter sortable}
 * collection of objects. Entries may not be atomic data types such as `string` or `number`.
 *
 * The entries are keyed by their `id` which is determined by interrogating the {@link #config-idProperty}.
 *
 * To filter a Collection, add a {@link Core.util.CollectionFilter CollectionFilter}
 * using the {@link #function-addFilter} method. A Filter config object may be specified here
 * which will be promoted to a CollectionFilter instance.
 *
 * To sort a Collection, add a {@link Core.util.CollectionSorter CollectionSorter}
 * using the {@link #function-addSorter} method. A Sorter config object may be specified here
 * which will be promoted to a CollectionSorter instance.
 */
export default class Collection extends Events(Base) {
    static get defaultConfig() {
        return {
            /**
             * Specify the name of the property of added objects which provides the lookup key
             * @config {String}
             * @default
             */
            idProperty : 'id',

            /**
             * Specify the names of properties which are to be indexed for fast lookup.
             * @config {String[]}
             */
            extraKeys : null,

            /**
             * Automatically apply filters on item add.
             * @config {String[]}
             * @default
             */
            autoFilter : true
        };
    }

    construct(config) {
        /**
         * A counter which is incremented whenever the Collection is mutated in a meaningful way.
         *
         * If a {@link #function-splice} call results in no net replacement, removal or addition,
         * then the `generation` will not be incremented.
         * @property {Number}
         * @readonly
         */
        this.generation = 0;
        this._values = [];

        super.construct(config);
    }

    doDestroy() {
        super.doDestroy();

        this._values.length = 0;

        if (this.isFiltered) {
            this._filteredValues.length = 0;
            this.filters.destroy();
        }

        this._sorters && this._sorters.destroy();
    }

    get isCollection() {
        return true;
    }

    /**
     * Clears this collection.
     */
    clear() {
        const me = this,
            removed = this._values.slice();

        if (me.totalCount) {
            me._values.length = 0;
            if (me._filteredValues) {
                me._filteredValues.length = 0;
            }
            me._indicesInvalid = true;

            // Indicate to obervers that data has changed.
            me.generation++;
            me.trigger('change', {
                action : 'clear',
                removed
            });
        }
    }

    /**
     * Replaces the internal values array with the passed array. Note that this takes ownership of the array, and the array
     * must not be mutated by outside code.
     *
     * This is an internal utility method, not designed for use by application code.
     * @internal
     * @param {Object[]} values The new values array
     */
    replaceValues(values, silent, isNewDataset) {
        const me = this;

        let removed;

        // The isNewDataset flag is passed by store#loadData to indicate that it's
        // a new data load, and that local filters can be applied.
        // Other use cases are for purely local updates of an existing dataset such as
        // refreshing the visible data with a values array containing group headers.
        if (me.isFiltered && !isNewDataset) {
            removed = me._filteredValues;
            me._filteredValues = values.slice();
        }
        else {
            removed = me._values;
            me._values = values;

            if (me.isFiltered && isNewDataset && me.autoFilter) {
                me._filterFunction = null;
                me._filteredValues = me._values.filter(me.filterFunction);
                me._indicesInvalid = true;
            }
            else {
                if (me._filteredValues) {
                    me._filteredValues.length = 0;
                }
            }
        }
        me._indicesInvalid = true;

        // Indicate to obervers that data has changed.
        me.generation++;

        if (!silent) {
            me.trigger('change', {
                action   : 'replaceValues',
                removed,
                values,
                replaced : []
            });
        }
    }

    set values(values) {
        this.splice(0, this._values.length, values);
    }

    /**
     * The set of values of this Collection. If this Collection {@link #property-isFiltered},
     * this yields the filtered data set.
     *
     * Setting this property replaces the data set.
     * @property {Object[]}
     */
    get values() {
        return this.isFiltered ? this._filteredValues : this._values;
    }

    /**
     * Iterator that allows you to do for (let item of collection)
     */
    [Symbol.iterator]() {
        return this.values[Symbol.iterator]();
    }

    /**
     * Executes the passed function for each item in this Collection, passing in the item,
     * ths index, and the full item array.
     * @param {Function} fn The function to execute.
     * @param {Boolean} [ignoreFilters=false] Pass `true` to include all items, bypassing filters.
     */
    forEach(fn, ignoreFilters = false) {
        (this.isFiltered && !ignoreFilters ? this._filteredValues : this._values).forEach(fn);
    }

    /**
     * Extracts ths content of this Collection into an array based upon the passed
     * value extraction function.
     * @param {Function} fn A function, which, when passed an item, returns a value to place into the resulting array.
     * @param {Boolean} [ignoreFilters=false] Pass `true` to process an item even if it is filtered out.
     * @returns {Object[]} An array of values extracted from this Collection.
     */
    map(fn, ignoreFilters = false) {
        return (this.isFiltered && !ignoreFilters ? this._filteredValues : this._values).map(fn);
    }

    /**
     * Returns the first item in this Collection which elicits a *truthy* return value from the passed funtion.
     * @param {Function} fn A function, which, when passed an item, returns `true` to select it as the item to return.
     * @param {Boolean} [ignoreFilters=false] Pass `true` to include filtered out items.
     * @returns {Object} The matched item, or `undefined`.
     */
    find(fn, ignoreFilters = false) {
        return (this.isFiltered && !ignoreFilters ? this._filteredValues : this._values).find(fn);
    }

    get first() {
        return this.values[0];
    }

    get last() {
        return this.values[this.count - 1];
    }

    /**
     * The set of all values of this Collection regardless of filters applied.
     * @readonly
     * @property {Object[]}
     */
    get allValues() {
        return this._values;
    }

    /**
     * Adds items to this Collection. Multiple new items may be passed.
     *
     * By default, new items are appended to the existing values.
     *
     * Any {@link #property-sorters} {@link #property-sorters} present are re-run.
     *
     * Any {@link #property-filters} {@link #property-filters} present are re-run.
     *
     * *Note that if application functionality requires add and remove, the
     * {@link #function-splice} operation is preferred as it performs both
     * operations in an atomic manner*
     * @param  {...Object} items The item(s) to add.
     */
    add(...items) {
        if (items.length === 1) {
            this.splice(this._values.length, null, ...items);
        }
        else {
            this.splice(this._values.length, null, items);
        }
    }

    /**
     * Removes items from this Collection. Multiple items may be passed.
     *
     * Any {@link #property-sorters} {@link #property-sorters} present are re-run.
     *
     * Any {@link #property-filters} {@link #property-filters} present are re-run.
     *
     * *Note that if application functionality requires add and remove, the
     * {@link #function-splice} operation is preferred as it performs both
     * operations in an atomic manner*
     * @param  {...Object} items The item(s) to remove.
     */
    remove(...items) {
        if (items.length === 1) {
            this.splice(0, ...items);
        }
        else {
            this.splice(0, items);
        }
    }

    /**
     * Moves an individual item to another location.
     * @param {Object} item The item to move.
     * @param {Object} [beforeItem] the item to insert the first item before. If omitted, the `item`
     * is moved to the end of the Collection.
     * @returns {Number} The new index of the `item`.
     */
    move(item, beforeItem) {
        const me = this,
            { _values } = me,
            itemIndex = me.indexOf(item, true);

        let beforeIndex = beforeItem ? me.indexOf(beforeItem, true) : _values.length;

        if (itemIndex === -1 || beforeIndex === -1) {
            throw new Error('Collection move parameters must be present in Collection');
        }

        if (itemIndex !== beforeIndex && itemIndex !== beforeIndex - 1) {
            if (itemIndex < beforeIndex) {
                beforeIndex--;
            }
            _values.splice(itemIndex, 1);
            _values.splice(beforeIndex, 0, item);
            me._indicesInvalid = true;

            me.trigger('change', {
                action : 'move',
                item,
                from   : itemIndex,
                to     : beforeIndex
            });
        }
        return beforeIndex;
    }

    /**
     * The core data set mutation method. Removes and adds at the same time. Analogous
     * to the `Array` `splice` method.
     *
     * Note that if items that are specified for removal are also in the `toAdd` array,
     * then those items are *not* removed then appended. They remain in the same position
     * relative to all remaning items.
     *
     * @param {Number} index Index at which to remove a block of items. Only valid if the
     * second, `toRemove` argument is a number.
     * @param {Object[]|Number} [toRemove] Either the number of items to remove starting
     * at the passed `index`, or an array of items to remove (If an array is passed, the `index` is ignored).
     * @param  {Object[]|Object} [toAdd] An item, or an array of items to add.
     */
    splice(index = 0, toRemove, ...toAdd) {
        const me = this,
            idProperty = me.idProperty,
            values = me._values,
            newIds = {},
            removed = [],
            replaced = [],
            oldCount = me.totalCount;

        let added,
            mutated;

        // Create an "newIds" map of the new items so remove ops know if it's really a replace
        // {
        //     1234 : true
        // }
        // And an "added" array of the items that need adding (there was not already an entry for the id)
        //
        if (toAdd) {
            if (toAdd.length === 1 && Array.isArray(toAdd[0])) {
                toAdd = toAdd[0];
            }

            // Check for replacements if we contain any data
            if (oldCount && toAdd.length) {
                // Only risk rebuilding the indices if we are adding
                const idIndex = me.indices[idProperty];

                added = [];

                for (let i = 0; i < toAdd.length; i++) {
                    const newItem = toAdd[i],
                        id = safeIndexKey(newItem[idProperty]),
                        existingIndex = idIndex[id];

                    // Register incoming id so that removal leaves it be
                    newIds[id] = true;

                    // Incoming id is already present.
                    // Replace it in place.
                    if (existingIndex != null) {
                        // If incoming is the same object, it's a no-op
                        if (values[existingIndex] !== newItem) {
                            replaced.push([values[existingIndex], newItem]);
                            values[existingIndex] = newItem;
                        }
                    }
                    else {
                        added.push(newItem);
                    }
                }
            }
            // Empty Collection, we simply add what we're passed
            else {
                added = toAdd;
            }
        }

        if (toRemove) {
            // We're removing a chunk starting at index
            if (typeof toRemove === 'number') {
                for (let removeIndex = index; toRemove; --toRemove) {
                    const id = safeIndexKey(values[removeIndex][idProperty]);

                    // If the entry here is being replaced, skip the insertion index past it
                    if (newIds[id]) {
                        index++;
                        removeIndex++;
                    }
                    // If the id is not among incoming items, remove it
                    else {
                        removed.push(values[removeIndex]);
                        values.splice(removeIndex, 1);
                        me._indicesInvalid = mutated = true;
                    }
                }
            }
            // We are removing an item/items
            else {
                let contiguous = added.length === 0,
                    lastIdx;

                if (!Array.isArray(toRemove)) {
                    toRemove = [toRemove];
                }
                // Create array of index points to remove.
                // They must be in reverse order so that removal leaves following remove indices stable
                const removeIndices = toRemove.reduce((result, item) => {
                    const isNumeric = typeof item === 'number',
                        idx = isNumeric ? item : me.indexOf(item, true);

                    // Drop out of contiguous mode if we find a non-contiguous record, or a remove *index*
                    if (contiguous && (lastIdx != null && idx !== lastIdx + 1 || isNumeric)) {
                        contiguous = false;
                    }

                    // Do not include indices out of range in our removeIndices
                    if (idx >= 0 && idx < oldCount) {
                        result.push(idx);
                    }
                    lastIdx = idx;
                    return result;
                }, []).sort(reverseNumericSortFn);

                // If it's a pure remove of contiguous items with no adds, fast track it.
                if (contiguous) {
                    // If reduced to zero by being asked to remove items we do not contain
                    // then this is a no-op
                    if (removeIndices.length) {
                        removed.push.apply(removed, toRemove);
                        values.splice(removeIndices[removeIndices.length - 1], removeIndices.length);
                        me._indicesInvalid = mutated = true;
                    }
                }
                else {
                    // Loop through removeIndices splicing each index out of the values
                    // unless there's an incoming identical id.
                    for (let i = 0; i < removeIndices.length; i++) {
                        const removeIndex = removeIndices[i];

                        if (removeIndex !== -1) {
                            const id = safeIndexKey(values[removeIndex][idProperty]);

                            // If the id is not among incoming items, remove it
                            if (!newIds[id]) {
                                removed.unshift(values[removeIndex]);
                                values.splice(removeIndex, 1);
                                me._indicesInvalid = mutated = true;
                            }
                        }
                    }
                }
            }
        }

        // If we collected genuinely new entries, insert them at the splice index
        if (added.length) {
            values.splice(Math.min(index, values.length), 0, ...added);
            me._indicesInvalid = mutated = true;
        }

        // Ensure order of values matches the sorters
        if (me.isSorted) {
            me.onSortersChanged();
        }
        // The sort will also recreate the filteredValues so that it can be in correct sort order
        else if (me.isFiltered) {
            if (me.autoFilter) {
                me.onFiltersChanged();
            }
            else {
                me._filteredValues.push(...added);
            }
        }

        // If we either added or removed items, or we did an in-place replace operation
        // then inform all interested parties.
        if (mutated || replaced.length) {
            // Indicate to obervers that data has changed.
            me.generation++;

            /**
             * Fired when items are added, replace or removed
             * @event change
             * @param {String} action The underlying operation which caused data change.
             * May be `'splice'` (meaning an atomic add/remove operation, `'sort'` or `'filter'`)
             * @param {Core.util.Collection} source This Collection.
             * @param {Object[]} removed An array of removed items.
             * @param {Object[]} added An array of added items.
             * @param {Object[]} replaced An array of replacements, each entry of which contains `[oldValue, newValue]`.
             * @param {Number} oldCount The number of items in the full, untiltered collection prior to the splice operation.
             */
            me.trigger('change', {
                action : 'splice',
                removed,
                added,
                replaced,
                oldCount
            });
        }
        else {
            /**
             * Fired when a {@link #function-splice} operation is requested but the operation
             * is a no-op and has caused no change to this Collection's dataset. The splice
             * method's parameters are passed for reference.
             * @event noChange
             * @param {Number} index Index at which to remove a block of items.
             * @param {Object[]|Number} [toRemove] Either the number of items to remove starting
             * at the passed `index`, or an array of items to remove (If an array is passed, the `index` is ignored).
             * @param  {Object[]|Object} [toAdd] An item, or an array of items to add.
             */
            me.trigger('noChange', {
                index,
                toRemove,
                toAdd
            });
        }
    }

    /**
     * Change the id of an existing member by mutating its {@link #config-idProperty}.
     * @param {String|Number|Object} item The item or id of the item to change.
     * @param {String|Number} newId The id to set in the existing member.
     */
    changeId(item, newId) {
        const me           = this,
            { idProperty } = me,
            oldId          = keyTypes[typeof item] ? item : item[idProperty],
            member         = me.get(oldId);

        if (member) {
            const existingMember = me.get(newId);

            if (existingMember && member !== existingMember) {
                throw new Error(`Attempt to set item ${oldId} to already existing member's id ${newId}`);
            }

            member[idProperty] = newId;

            // If indices are valid, keep the id index correct
            if (!me._indicesInvalid) {
                const idIndex = me.indices.id,
                    memberIndex = idIndex[oldId];

                delete idIndex[oldId];
                idIndex[newId] = memberIndex;
            }
        }
    }

    /**
     * Returns the item with the passed `id`. By default, filtered are honoured, and
     * if the item with the requested `id` is filtered out, nothing will be returned.
     *
     * To return the item even if it has been filtered out, pass the second parameter as `true`.
     * @param {*} id The `id` to find.
     * @param {Boolean} [ignoreFilters=false] Pass `true` to return an item even if it is filtered out.
     * @returns {Object} The found item, or `undefined`.
     */
    get(id, ignoreFilters = false) {
        return this.getBy(this.idProperty, id, ignoreFilters);
    }

    getAt(index, ignoreFilters = false) {
        if (this.isFiltered && !ignoreFilters) {
            return this._filteredValues[index];
        }
        else {
            return this._values[index];
        }
    }

    /**
     * Returns the item with passed property name equal to the passed value. By default,
     * filtered are honoured, and if the item with the requested `id` is filtered out,
     * nothing will be returned.
     *
     * To return the item even if it has been filtered out, pass the third parameter as `true`.
     * @param {String} propertyName The property to test.
     * @param {*} value The value to find.
     * @param {Boolean} [ignoreFilters=false] Pass `true` to return an item even if it is filtered out.
     * @returns {Object} The found item, or `undefined`.
     */
    getBy(propertyName, value, ignoreFilters = false) {
        const me = this;

        if (me.isFiltered && ignoreFilters) {
            const index = me.findIndex(propertyName, value, true);
            if (index !== -1) {
                return me._values[index];
            }
        }
        else {
            const index = me.findIndex(propertyName, value);
            if (index !== -1) {
                return me.values[index];
            }
        }
    }

    /**
     * The number of items in this collection. Note that this honours filtering.
     * See {@link #property-totalCount};
     * @property {Number}
     * @readonly
     */
    get count() {
        return this.values.length;
    }

    /**
     * The number of items in this collection regardless of filtering.
     * @property {Number}
     * @readonly
     */
    get totalCount() {
        return this._values.length;
    }

    set idProperty(idProperty) {
        this._idProperty = idProperty;
        this.addIndex(idProperty);
    }

    /**
     * The property name used to extract item `id`s from added objects.
     * @property {String}
     */
    get idProperty() {
        return this._idProperty;
    }

    set extraKeys(extraKeys) {
        if (!Array.isArray(extraKeys)) {
            extraKeys = [extraKeys];
        }
        for (let i = 0; i < extraKeys.length; i++) {
            this.addIndex(extraKeys[i]);
        }
    }

    /**
     * The Collection of {@link Core.util.CollectionSorter Sorters} for this Collection.
     * @property {Core.util.Collection}
     * @readonly
     */
    get sorters() {
        if (!this._sorters) {
            this._sorters = new Collection({
                listeners : {
                    change  : 'onSortersChanged',
                    thisObj : this
                }
            });
        }
        return this._sorters;
    }

    /**
     * Adds a Sorter to the Collection of Sorters which are operating on this Collection.
     *
     * A Sorter may be an specified as an instantiated {@link Core.util.CollectionSorter
     * CollectionSorter}, or a config object for a CollectionSorter of the form
     *
     *     {
     *         property  : 'age',
     *         direction : 'desc'
     *     }
     *
     * Note that by default, a Sorter *replaces* a Sorter with the same `property` to make
     * it easy to change existing Sorters. A Sorter's `id` is its `property` by default. You
     * can avoid this and add multiple Sorters for one property by configuring Sorters with `id`s.
     *
     * A Sorter may also be specified as a function which compares two objects eg:
     *
     *     (lhs, rhs) => lhs.customerDetails.age - rhs.customerDetails.age
     *
     * @param {Object} sorter A Sorter of Sorter configuration object to add to the Collection
     * of Sorters operating on this Collection.
     * @returns {Core.util.CollectionSorter} The resulting Sorter to make it easy to remove Sorters.
     */
    addSorter(sorter) {
        const result = (sorter instanceof CollectionSorter) ? sorter : new CollectionSorter(sorter);

        this.sorters.add(result);

        return result;
    }

    /**
     * A flag which is `true` if this Collection has active {@link #property-sorters}.
     * @property {Boolean}
     * @readonly
     */
    get isSorted() {
        return Boolean(this._sorters && this._sorters.count);
    }

    onSortersChanged() {
        const me = this;

        delete me._sortFunction;

        me._values.sort(me.sortFunction);
        me._indicesInvalid = true;

        me.trigger('change', sortEvent);
    }

    /**
     * A sorter function which encapsulates the {@link Core.util.CollectionSorter Sorters}
     * for this Collection.
     * @property {Function}
     * @readonly
     */
    get sortFunction() {
        if (!this._sortFunction) {
            if (this.isSorted) {
                this._sortFunction = CollectionSorter.generateSortFunction(this.sorters.values);
            }
            else {
                this._sortFunction = return0;
            }
        }

        return this._sortFunction;
    }

    /**
     * The Collection of {@link Core.util.CollectionFilter Filters} for this Collection.
     * @property {Core.util.Collection}
     * @readonly
     */
    get filters() {
        if (!this._filters) {
            this._filters = new Collection({
                listeners : {
                    change  : 'onFiltersChanged',
                    thisObj : this
                }
            });
        }
        return this._filters;
    }

    /**
     * Adds a Filter to the Collection of Filters which are operating on this Collection.
     *
     * A Filter may be an specified as an instantiated {@link Core.util.CollectionFilter
     * CollectionFilter}, or a config object for a CollectionFilter of the form
     *
     *     {
     *         property : 'age',
     *         operator : '>=',
     *         value    : 21
     *     }
     *
     * Note that by default, a Filter *replaces* a Filter with the same `property` to make
     * it easy to change existing Filters. A Filter's `id` is its `property` by default. You
     * can avoid this and add multiple Filters for one property by configuring Filters with `id`s.
     *
     * A Filter may also be specified as a function which filters candidate objects eg:
     *
     *     candidate => candidate.customerDetails.age >= 21
     *
     * @param {Object} filter A Filter or Filter configuration object to add to the Collection
     * of Filters operating on this Collection.
     * @returns {Core.util.CollectionFilter} The resulting Filter to make it easy to remove Filters.
     */
    addFilter(filter) {
        const result = (filter instanceof CollectionFilter) ? filter : new CollectionFilter(filter);

        this.filters.add(result);

        return result;
    }

    /**
     * A flag which is `true` if this Collection has active {@link #property-filters}.
     * @property {Boolean}
     * @readonly
     */
    get isFiltered() {
        return Boolean(this._filters && this._filters.count);
    }

    onFiltersChanged() {
        const me = this;

        me._filterFunction = null;
        me._filteredValues = me._values.filter(me.filterFunction);
        me._indicesInvalid = true;

        me.trigger('change', filterEvent);
    }

    /**
     * A filter function which encapsulates the {@link Core.util.CollectionFilter Filters}
     * for this Collection.
     * @property {Function}
     * @readonly
     */
    get filterFunction() {
        if (!this._filterFunction) {
            if (this.isFiltered) {
                this._filterFunction = CollectionFilter.generateFiltersFunction(this.filters.values);
            }
            else {
                this._filterFunction = FunctionHelper.returnTrue;
            }
        }

        return this._filterFunction;
    }

    /**
     * Adds a lookup index for the passed property name. The index is built lazily when
     * an index is serched,
     * @internal
     * @param {String} indexProperty The property name to add an index for.
     */
    addIndex(indexProperty) {
        (this._indices || (this._indices = {}))[indexProperty] = {};

        // Indices need a rebuild now.
        this._indicesInvalid = true;

        /**
         * this.indices is keyed by the property name, and contains the keys linked to the index in the _values array.
         * So collection.add({id : foo, name : 'Nige'}, {id : 'bar', name : 'Faye'}) where collection has had an index
         * added for the "name" property would result in:
         *
         * {
         *     id : {
         *         foo : 0,
         *         bar : 1
         *     },
         *     name : {
         *         Nige : 0,
         *         Faye : 1
         *     }
         * }
         */
    }

    /**
     * Return the index of the item with the specified key having the specified value.
     * By default, filtering is taken into account and this returns the index in the filtered
     * dataset if present. To bypass this, pass the third parameter as `true`.
     * @param {String} propertyName The name of the property to test.
     * @param {*} value The value to test for.
     * @param {Boolean} [ignoreFilters=false] Pass `true` to return the index in
     * the original data set if the item is filtered out.
     * @returns {Number} The index of the item, or `-1` if not found.
     */
    findIndex(propertyName, value, ignoreFilters = false) {
        const me = this,
            isFiltered = me.isFiltered,
            index = isFiltered && !ignoreFilters ? me.indices[filteredIndicesProperty][propertyName] : me.indices[propertyName];

        if (index) {
            const itemIndex = index[safeIndexKey(value)];

            if (itemIndex != null) {
                return itemIndex;
            }
        }
        else {
            // Search the filtered values if we are filtered and not ignoreing filters
            const values = isFiltered && !ignoreFilters ? me._filteredValues : me._values,
                count = values.length;

            for (let i = 0; i < count; i++) {
                if (values[i][propertyName] == value) {
                    return i;
                }
            }
        }

        // Not found
        return -1;
    }

    /**
     * Returns the index of the item with the same `id` as the passed item.
     *
     * By default, filtering is honoured, so if the item in question has been added,
     * but is currently filtered out of visibility, `-1` will be returned.
     *
     * To find the index in the master, unfiltered dataset, pass the second parameter as `true`;
     * @param {Object|String|Number} item The item to find, or an `id` to find.
     * @param {Boolean} [ignoreFilters=false] Pass `true` to find the index in the master, unfiltered data set.
     * @returns {Number} The index of the item, or `-1` if not found.
     */
    indexOf(item, ignoreFilters = false) {
        return this.findIndex(this.idProperty, keyTypes[typeof item] ? item : item[this.idProperty], ignoreFilters);
    }

    /**
     * Returns `true` if this Collection includes an item with the same `id` as the passed item.
     *
     * By default, filtering is honoured, so if the item in question has been added,
     * but is currently filtered out of visibility, `false` will be returned.
     *
     * To query inclusion in the master, unfiltered dataset, pass the second parameter as `true`;
     * @param {Object|String|Number} item The item to find, or an `id` to find.
     * @param {Boolean} [ignoreFilters=false] Pass `true` to find the index in the master, unfiltered data set.
     * @returns {Boolean} True if the passed item is found.
     */
    includes(item, ignoreFilters = false) {
        return this.indexOf(item, ignoreFilters) !== -1;
    }

    get indices() {
        if (this._indicesInvalid) {
            this.rebuildIndices();
        }
        return this._indices;
    }

    /**
     * Called when the Collection is mutated and the indices have been flagged as invalid.
     *
     * Rebuilds the indices object to allow lookup by keys.
     * @internal
     */
    rebuildIndices() {
        const me = this,
            isFiltered = me.isFiltered,
            indices = (me._indices || (me._indices = {})),
            keyProps = Object.keys(indices),
            indexCount = keyProps.length,
            values = me._values,
            count = values.length;

        let i, j;

        // First, clear indices.
        if (isFiltered) {
            indices[filteredIndicesProperty] = {};
        }
        for (i = 0; i < indexCount; i++) {
            indices[keyProps[i]] = {};
            if (isFiltered) {
                indices[filteredIndicesProperty][keyProps[i]] = {};
            }
        }

        /*
         * Rebuild the indices object.
         * Loop through all items adding an entry for each one to each index.
         * So collection.add({id : foo, name : 'Nige'}, {id : 'bar', name : 'Faye'}) where collection has had an index
         * added for the "name" property would result in:
         *
         * {
         *     id : {
         *         foo : 0,
         *         bar : 1
         *     },
         *     name : {
         *         Nige : 0,
         *         Faye : 1
         *     }
         * }
         */
        for (i = 0; i < count; i++) {
            const item = values[i];

            for (j = 0; j < indexCount; j++) {
                const keyProp = keyProps[j];
                // This does indices.name['Nige'] = 0
                indices[keyProp][safeIndexKey(item[keyProp])] = i;
            }
        }

        // Create a parallel lookup structure into the _filteredValues
        if (isFiltered) {
            const values = me._filteredValues,
                count = values.length,
                indices = me._indices[filteredIndicesProperty];

            for (i = 0; i < count; i++) {
                const item = values[i];

                for (j = 0; j < indexCount; j++) {
                    const keyProp = keyProps[j];
                    // This does indices[filteredIndicesProperty].name['Nige'] = 0
                    indices[keyProp][safeIndexKey(item[keyProp])] = i;
                }
            }
        }

        me._indicesInvalid = false;
    }
}

// These are used by Bag for the same purpose
export  { safeIndexKey, keyTypes };
