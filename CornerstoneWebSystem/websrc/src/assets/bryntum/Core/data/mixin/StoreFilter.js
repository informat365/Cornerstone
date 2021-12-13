import Base from '../../Base.js';
import Filter from '../../util/CollectionFilter.js';
import FunctionHelper from '../../helper/FunctionHelper.js';
import Collection from '../../util/Collection.js';
import ObjectHelper from '../../helper/ObjectHelper.js';

/**
 * @module Core/data/mixin/StoreFilter
 */

/**
 * Mixin for Store that handles filtering.
 * Filters are instances of {@link Core.util.CollectionFilter CollectionFilter} class.
 *
 * - Adding a filter for the same property will replace the current one (unless a unique {@link Core.util.CollectionFilter#config-id Id} is specified),
 * but will not clear any other filters.
 * - Adding a filter through the {@link #function-filterBy} function is ultimate.
 * It will clear all the property based filters and replace the current filterBy function if present.
 * - Removing records from the store does not remove filters!
 * The filters will be reapplied if {@link #config-reapplyFilterOnAdd}/{@link #config-reapplyFilterOnUpdate} are true and you add new records or update current.
 *
 * ```
 * // Add a filter
 * store.filter({
 *     property : 'score',
 *     value    : 10,
 *     operator : '>'
 * });
 *
 * // Replace any filter set with new filters
 * store.filter({
 *     filters : {
 *         property : 'score',
 *         value    : 10,
 *         operator : '>'
 *     },
 *     replace : true
 * });
 *
 * // Reapply filters
 * store.filter();
 *
 * // Reapply filters without firing an event.
 * // Use if making multiple data mutations with the
 * // intention of updating UIs when all finished.
 * store.filter({
 *     silent : true
 * });
 * ```
 *
 * @mixin
 */
export default Target => class StoreFilter extends (Target || Base) {
    //region Config

    static get defaultConfig() {
        return {
            /**
             * Specify a filter config to use initial filtering
             * @config {Object}
             * @category Filtering
             */
            filters : null,

            /**
             * Specify true to reapply filters when a record is added to the store.
             * @config {Boolean}
             * @default
             * @category Filtering
             */
            reapplyFilterOnAdd : false,

            /**
             * Specify true to reapply filters when a record is updated in the store.
             * @config {Boolean}
             * @default
             * @category Filtering
             */
            reapplyFilterOnUpdate : false

        };
    }

    //endregion

    //region Events

    /**
     * Fired after applying filters to the store
     * @event filter
     * @param {Core.data.Store} source This Store
     * @param {Core.util.Collection} filters Filters used by this Store
     * @param {Core.data.Model[]} records Filtered records
     */

    //endregion

    //region Properties

    /**
     * Currently applied filters. A collection of {@link Core.util.CollectionFilter} instances.
     * @type {Core.util.Collection}
     * @readonly
     * @category Sort, group & filter
     */
    set filters(filters) {
        const me         = this,
            collection = me.filters;

        collection.clear();

        // Invalidate the filtersFunction so that it has to be recalculated upon next access
        me._filtersFunction = null;

        // If we are being asked to filter, parse the filters.
        if (filters) {
            if (filters.constructor.name === 'Object') {
                for (const f of Object.entries(filters)) {
                    // Entry keys are either a field name with its value being the filter value
                    // or, there may be one filterBy property which specifies a filering function.
                    if (f[0] === 'filterBy' && typeof f[1] === 'function') {
                        collection.add(new Filter({
                            filterBy : f[1]
                        }));
                    }
                    else {
                        collection.add(new Filter(f[1].constructor.name === 'Object' ? Object.assign({
                            property : f[0]
                        }, f[1]) : {
                            property : f[0],
                            value    : f[1]
                        }));
                    }
                }
            }
            else if (Array.isArray(filters)) {
                // Make sure we are adding CollectionFilters
                collection.add(...filters.map(filterConfig => {
                    if (filterConfig instanceof Filter) {
                        return filterConfig;
                    }
                    return new Filter(filterConfig);
                }));
            }
            else if (filters.isCollection) {
                // Use supplied collection? Opting to use items from it currently
                collection.add(...filters.values);
            }
            else {
                //<debug>
                if (typeof filters !== 'function') {
                    throw new Error('Store filters must be an object whose properties are Filter configs keyed by field name, or an array of Filter configs, or a filtering function');
                }
                //</debug>
                collection.add(new Filter({
                    filterBy : filters
                }));
            }

            collection.forEach(item => item.owner = me);
        }
    }

    get filters() {
        return this._filters || (this._filters = new Collection({ extraKeys : ['property'] }));
    }

    set filtersFunction(filtersFunction) {
        this._filtersFunction = filtersFunction;
    }

    get filtersFunction() {
        const me                     = this,
            { filters, isGrouped } = me;

        if (!me._filtersFunction) {
            if (filters.count) {
                const generatedFilterFunction = Filter.generateFiltersFunction(filters);

                me._filtersFunction = candidate => {
                    // A group record is filtered in if it has passing groupChildren.
                    if (isGrouped && candidate.meta.specialRow) {
                        return candidate.groupChildren.some(generatedFilterFunction);
                    }
                    else {
                        return generatedFilterFunction(candidate);
                    }
                };
            }
            else {
                me._filtersFunction = FunctionHelper.returnTrue;
            }
        }

        return me._filtersFunction;
    }

    /**
     * Check if store is filtered
     * @returns {Boolean}
     * @readonly
     * @category Sort, group & filter
     */
    get isFiltered() {
        return this.filters.values.some(filter => !filter.disabled);
    }

    //endregion

    traverseFilter(record) {
        let me          = this,
            hitsCurrent = !record.isRoot && me.filtersFunction(record),
            hitsChild   = false,
            children    = record.unfilteredChildren || record.children;

        // leaf, bail out
        if (!children || !children.length) {
            return hitsCurrent;
        }

        if (!record.unfilteredChildren) {
            record.unfilteredChildren = record.children.slice();
        }

        record.children = record.unfilteredChildren.filter(r => {
            return me.traverseFilter(r);
        });

        if (record.children.length) hitsChild = true;

        return hitsCurrent || hitsChild;
    }

    traverseClearFilter(record) {
        const me = this;

        if (record.children) {
            record.children = record.unfilteredChildren || record.children;
            record.children.forEach(r => me.traverseClearFilter(r));
        }
    }

    // TODO: Get rid of this.
    // The Filter feature of a Grid pokes around in the Store to ask this question.
    get latestFilterField() {
        return this.filters.last ? this.filters.last.property : null;
    }

    processFieldFilter(filter, value) {
        if (typeof filter === 'string') {
            filter = {
                property : filter,
                value    : value
            };
        }

        //<debug>
        if (filter._filterBy && this.filterParamName) {
            throw new Error('Cannot filter with a function if remote filtering is being used');
        }
        //</debug>
        filter = filter instanceof Filter ? filter : new Filter(filter);

        // We want notification upon change of field, value or operator
        filter.owner = this;

        // Collection will replace any already existing filter on the field, unless it has id specified
        this.filters.add(filter);
    }

    /**
     * Filters the store by *adding* the specified filter or filters to the existing filters applied to this Store. Call without arguments to reapply filters.
     * ```
     * // Add a filter
     * store.filter({
     *     property : 'age',
     *     operator : '>',
     *     value    : 90
     * });
     *
     * // Reapply filters
     * store.filter();
     *
     * // Replace existing filters
     * store.filter({
     *     filters : {
     *         property : 'age',
     *         operator : '<',
     *         value    : 90
     *     },
     *     replace : true
     * });
     *
     * // Filter using function
     * store.filter(r => r.age < 90);
     *
     * // Replacing existing filters with function
     * store.filter({
     *     filters : r => r.age > 90,
     *     replace : true
     * });
     * ```
     * @param {Object|Object[]|function} newFilters A filter config, or array of filter configs, or a function to use for filtering
     * @fires filter
     * @fires change
     * @category Sort, group & filter
     */
    filter(newFilters) {
        const
            me          = this,
            { filters } = me;

        let silent = false;

        if (newFilters) {
            let fieldType = typeof newFilters;

            if (fieldType === 'object') {
                if (('silent' in newFilters) || ('replace' in newFilters)) {
                    silent = newFilters.silent;
                    if (newFilters.replace) {
                        filters.clear();
                    }
                    newFilters = newFilters.filters;
                    fieldType = typeof newFilters;
                }
            }

            // If it was just a config object containing no filters, this will be null
            if (newFilters) {
                // We will not be informed about Filter mutations while configuring.
                me.isConfiguring = true;

                // If we provide array of objects looking like :
                //  {
                //      property  : 'fieldName',
                //      value     : 'someValue',
                //      [operator : '>']
                //  }
                //  or ...
                //  {
                //      property : 'fieldName',
                //      filterBy : function (value, record) {
                //          return value > 50;
                //      }
                //  }
                if (Array.isArray(newFilters)) {
                    newFilters.forEach(me.processFieldFilter, me);
                }
                else if (fieldType === 'function') {
                    //<debug>
                    if (me.filterParamName) {
                        throw new Error('Cannot filter with a function if remote filtering is being used');
                    }
                    //</debug>
                    filters.add(new Filter(newFilters));
                }
                // Old signature of fieldname, value with implicit equality test.
                // Not documented, but still tested.
                else if (fieldType === 'string') {
                    me.processFieldFilter(newFilters, arguments[1]);
                }
                // An object-based filter definition
                else {
                    me.processFieldFilter(newFilters);
                }

                // Open up to recieving Filter mutation notifications again
                me.isConfiguring = false;

                // We added a disabled filter to either no filters, or all disabled filters, so no change.
                if (!me.isFiltered) {
                    return;
                }
            }
        }

        // Invalidate the filtersFunction so that it has to be recalculated upon next access
        me.filtersFunction = null;

        // Implemented here for local filtering, and AjaxStore implements for remote.
        me.performFilter(silent);
    }

    /**
     * Perform filtering according to the {@link #property-filters} Collection.
     * This is the internal implementation which is overridden in {@link Core.data.AjaxStore} and
     * must not be overridden.
     * @private
     */
    performFilter(silent) {
        const
            me          = this,
            { storage, filters, rootNode } = me,
            oldCount    = me.count;

        if (me.tree) {
            if (me.isFiltered) {
                me.traverseFilter(rootNode);
            }
            else {
                me.traverseClearFilter(rootNode);
            }
            storage.replaceValues(me.collectDescendants(rootNode).visible, true);
        }
        else {
            if (me.isFiltered) {
                storage.addFilter({
                    id       : 'primary-filter',
                    filterBy : me.filtersFunction
                });
            }
            else {
                storage.filters.clear();
            }
        }

        me.afterPerformFilter(silent ? null : {
            action  : 'filter',
            filters,
            oldCount,
            records : me.storage.values
        });
    }

    afterPerformFilter(event) {
        this.resetRelationCache();

        if (event) {
            this.triggerFilterEvent(event);
        }
    }

    get filtered() {
        return this.storage.isFiltered;
    }

    // Used from filter() and StoreCRUD when reapplying filters
    triggerFilterEvent(event) {
        this.trigger('filter', event);

        // Only fire these events if it's a local filter.
        // If we are configured with filterParamName, the loadData will fire them.
        if (!this.filterParamName) {
            this.trigger('refresh', event);
            this.trigger('change', event);
        }
    }

    /**
     * *Adds* a function used to filter the store. Alias for calling `filter(fn)`. Return `true` from the function to
     * include record in filtered set
     *
     * ```javascript
     * store.filterBy(record => record.age > 25 && record.name.startsWith('A'));
     * ```
     *
     * @param {Function} fn Function used to test records
     * @category Sort, group & filter
     */
    filterBy(fn) {
        this.filter(fn);
    }

    /**
     * Removes filtering from the specified field.
     * @param {String} field Field to not filter the store on any longer
     * @private
     * @deprecated
     * Only used by the Grid Filtering plugin which assumes one Filter per field.
     * @category Sort, group & filter
     */
    removeFieldFilter(field, silent) {
        const me     = this,
            filter = me.filters.getBy('property', field);

        // If we have such a filter, remove it.
        if (filter) {

            me.filters.remove(filter);

            // Invalidate the filtersFunction so that it has to be recalculated upon next access
            me._filtersFunction = null;

            if (!silent) {
                me.filter();
            }
        }
    }

    /**
     * Removes all filters from the store.
     * @category Sort, group & filter
     */
    clearFilters(silent) {
        this.filters.clear();
        this.filter(undefined, undefined, silent);
    }

    convertFilterToString(field) {
        let filter = this.filters.getBy('property', field),
            result = '';

        if (filter && !filter.filterBy) {
            result = String(filter);
        }

        return result;
    }

    get filterState() {
        return this.filters.values.map(filter => ObjectHelper.cleanupProperties(filter.config));
    }
};
