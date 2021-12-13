//TODO: Allow multisort using multitouch?
//TODO: UI sort of broken with grouped headers, take a look at groupedheaders demo

import DomHelper from '../../Core/helper/DomHelper.js';
import DomClassList from '../../Core/helper/util/DomClassList.js';
import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../feature/GridFeatureManager.js';

/**
 * @module Grid/feature/Sort
 */

/**
 * Allows sorting of grid by clicking (or tapping) headers, also displays which columns grid is sorted by (numbered if
 * using multisort). Use modifier keys for multisorting: ctrl + click to add sorter, ctrl + alt + click to remove sorter.
 * The actual sorting is done by the store.
 *
 * For info on programmatically handling sorting, see {@link Core.data.mixin.StoreSort StoreSort}.
 *
 * This feature is <strong>enabled</strong> by default.
 *
 * @extends Core/mixin/InstancePlugin
 *
 * @example
 * // use initial sorting
 * let grid = new Grid({
 *   features: {
 *     sort: 'name'
 *   }
 * });
 *
 * // can also be specified on store
 * let grid = new Grid({
 *   store: {
 *     sorters: [
 *       { field: 'name', ascending: false }
 *     ]
 *   }
 * });
 *
 * @demo Grid/sorting
 * @classtype sort
 * @externalexample feature/Sort.js
 */
export default class Sort extends InstancePlugin {
    //region Config

    static get $name() {
        return 'Sort';
    }

    static get defaultConfig() {
        return {
            /**
             * Enable multi sort
             * @config {Boolean}
             * @default
             */
            multiSort : true,

            ignoreRe : new RegExp([
                // Stop this feature from having to know the internals of two other optional features.
                'b-grid-header-resize-handle',
                'b-filter-icon'
            ].join('|')),

            sortableCls   : 'b-sortable',
            sortedCls     : 'b-sort',
            sortedAscCls  : 'b-asc',
            sortedDescCls : 'b-desc'
        };
    }

    //endregion

    //region Init

    construct(grid, config) {
        const me = this;

        // process initial config into an actual config object
        config = me.processConfig(config);

        me.store = grid.store;
        me.grid = grid;

        me.store.on({
            sort    : me.syncHeaderSortState,
            thisObj : me
        });

        super.construct(grid, config);
    }

    // Sort feature handles special config cases, where user can supply a string or an array of sorters
    // instead of a normal config object
    processConfig(config) {
        if (typeof config === 'string' || Array.isArray(config)) {
            return {
                field     : config,
                ascending : null
            };
        }

        return config;
    }

    // override setConfig to process config before applying it
    setConfig(config) {
        super.setConfig(this.processConfig(config));
    }

    set field(field) {
        // Use columns sortable config for initial sorting if it is specified
        const column = this.grid.columns.get(field);
        if (column && typeof column.sortable === 'object') {
            // Normalization of Store & CollectionSorter differences
            column.sortable.field = column.sortable.property || field;
            field = column.sortable;
        }

        this.store.sort(field, this.ascending);
    }

    doDestroy() {
        super.doDestroy();
    }

    //endregion

    //region Plugin config

    // Plugin configuration. This plugin chains some of the functions in Grid.
    static get pluginConfig() {
        return {
            chain : ['onElementClick', 'getHeaderMenuItems', 'getColumnDragToolbarItems', 'renderHeader']
        };
    }

    //endregion

    //region Headers

    /**
     * Update headers to match stores sorters (displays sort icon in correct direction on them)
     * @private
     */
    syncHeaderSortState() {
        const me = this,
            sorterMap = {};

        if (!me.grid.isConfiguring) {
            let storeSorters = me.store.sorters,
                sorterCount = storeSorters.length,
                classList = new DomClassList(),
                sorter;

            // Key sorters object by field name so we can find them.
            for (let sortIndex = 0; sortIndex < sorterCount; sortIndex++) {
                const sorter = storeSorters[sortIndex];
                if (sorter.field) {
                    sorterMap[sorter.field] = {
                        ascending : sorter.ascending,
                        sortIndex : sortIndex + 1
                    };
                }
            }

            // Sync the sortable, sorted, and sortIndex state of each leaf header element
            for (const leafColumn of me.grid.columns.bottomColumns) {
                const leafHeader = leafColumn.element;

                if (leafHeader) {
                    // TimeAxisColumn in Scheduler has no textWrapper, since it has custom rendering,
                    // but since it cannot be sorted by anyway lets just ignore it
                    const dataset = leafColumn.textWrapper && leafColumn.textWrapper.dataset;

                    // data-sortIndex is 1-based, and only set if there is > 1 sorter.
                    // iOS Safari throws a JS error if the requested delete property is not present.
                    dataset && dataset.sortIndex && delete dataset.sortIndex;

                    classList.value = leafHeader.classList;

                    if (leafColumn.sortable !== false) {
                        classList.add(me.sortableCls);
                        sorter = sorterMap[leafColumn.field];
                        if (sorter) {
                            if (sorterCount > 1 && dataset) {
                                dataset.sortIndex = sorter.sortIndex;
                            }
                            classList.add(me.sortedCls);
                            if (sorter.ascending) {
                                classList.add(me.sortedAscCls);
                                classList.remove(me.sortedDescCls);
                            }
                            else {
                                classList.add(me.sortedDescCls);
                                classList.remove(me.sortedAscCls);
                            }
                        }
                        else {
                            classList.remove(me.sortedCls);
                            // Not optimal, but easiest way to make sure sort feature does not remove needed classes.
                            // Better solution would be to use different names for sorting and grouping
                            if (!classList['b-group']) {
                                classList.remove(me.sortedAscCls);
                                classList.remove(me.sortedDescCls);
                            }
                        }
                    }
                    else {
                        classList.remove(me.sortableCls);
                    }

                    // Update the element's classList
                    DomHelper.syncClassList(leafHeader, classList);
                }
            }
        }
    }

    //endregion

    //region Context menu

    /**
     * Adds sort menu items to header context menu.
     * @param column
     * @param items
     * @returns {Object[]}
     * @private
     */
    getHeaderMenuItems(column, items) {
        const
            me = this,
            { disabled } = me;

        if (column.sortable !== false) {
            items.push({
                text   : me.L('sortAscending'),
                icon   : 'b-fw-icon b-icon-sort-asc',
                name   : 'sortAsc',
                cls    : 'b-separator',
                weight : 105,
                disabled,
                onItem : ({ item : { column } }) => me.store.sort(column.field, true)
            });
            items.push({
                text   : me.L('sortDescending'),
                icon   : 'b-fw-icon b-icon-sort-desc',
                name   : 'sortDesc',
                weight : 105,
                disabled,
                onItem : ({ item : { column } }) => me.store.sort(column.field, false)
            });

            if (me.multiSort && me.grid.columns.records.filter(col => col.sortable).length > 1) {
                const sorter = this.grid.store.sorters.find(s => s.field === column.field);

                items.push({
                    text   : me.L('multiSort'),
                    icon   : 'b-fw-icon b-icon-sort',
                    name   : 'multiSort',
                    weight : 105,
                    disabled,
                    menu   : [{
                        text     : sorter ? me.L('toggleSortAscending') : me.L('addSortAscending'),
                        icon     : 'b-fw-icon b-icon-sort-asc',
                        name     : 'addSortAsc',
                        disabled : sorter && sorter.ascending,
                        weight   : 105,
                        onItem   : ({ item : { column } }) => me.store.addSorter(column.field, true)
                    }, {
                        text     : sorter ? me.L('toggleSortDescending') : me.L('addSortDescending'),
                        icon     : 'b-fw-icon b-icon-sort-desc',
                        name     : 'addSortDesc',
                        disabled : sorter && !sorter.ascending,
                        weight   : 105,
                        onItem   : ({ item : { column } }) => me.store.addSorter(column.field, false)
                    }, {
                        text     : me.L('removeSorter'),
                        icon     : 'b-fw-icon b-icon-remove',
                        name     : 'remove-sorter',
                        weight   : 105,
                        disabled : !sorter,
                        onItem   : ({ item : { column } }) => me.store.removeSorter(column.field)
                    }]
                });
            }
        }
        return items;
    }

    /**
     * Supply items to ColumnDragToolbar
     * @private
     */
    getColumnDragToolbarItems(column, items) {
        const
            me                  = this,
            { store, disabled } = me;

        if (column.sortable !== false) {
            items.push({
                text   : me.L('sortAscendingShort'),
                group  : me.L('Sort'),
                icon   : 'b-icon b-icon-sort-asc',
                name   : 'sortAsc',
                cls    : 'b-separator',
                weight : 105,
                disabled,
                onDrop : ({ column }) => store.sort(column.field, true)
            });
            items.push({
                text   : me.L('sortDescendingShort'),
                group  : me.L('Sort'),
                icon   : 'b-icon b-icon-sort-desc',
                name   : 'sortDesc',
                weight : 105,
                disabled,
                onDrop : ({ column }) => store.sort(column.field, false)
            });

            const sorter = store.sorters.find(s => s.field === column.field);

            Array.prototype.push.apply(items, [
                {
                    text     : me.L('addSortAscendingShort'),
                    group    : me.L('Multisort'),
                    icon     : 'b-icon b-icon-sort-asc',
                    name     : 'multisortAddAsc',
                    disabled : disabled || (sorter && sorter.ascending),
                    weight   : 105,
                    onDrop   : ({ column }) => store.addSorter(column.field, true)
                }, {
                    text     : me.L('addSortDescendingShort'),
                    group    : me.L('Multisort'),
                    icon     : 'b-icon b-icon-sort-desc',
                    name     : 'multisortAddDesc',
                    disabled : disabled || (sorter && !sorter.ascending),
                    weight   : 105,
                    onDrop   : ({ column }) => store.addSorter(column.field, false)
                }, {
                    text     : me.L('removeSorterShort'),
                    group    : me.L('Multisort'),
                    icon     : 'b-icon b-icon-remove',
                    name     : 'multisortRemove',
                    weight   : 105,
                    disabled : disabled || !sorter,
                    onDrop   : ({ column }) => store.removeSorter(column.field)
                }
            ]);
        }
        return items;
    }

    //endregion

    //region Events

    /**
     * Clicked on header, sort Store.
     * @private
     */
    onElementClick(event) {
        const
            me     = this,
            store  = me.store,
            target = event.target,
            header = DomHelper.up(target, '.b-grid-header.b-sortable'),
            field  = header && header.dataset.column;

        if (me.ignoreRe.test(target.className) || me.disabled) return;

        //Header
        if (header && field) {
            const column = me.grid.columns.getById(header.dataset.columnId),
                columnGrouper = store.isGrouped && store.groupers.find(g => g.field === field);

            // The Group feature will handle the change of the grouper's direction
            if (columnGrouper && !event.shiftKey) {
                return;
            }

            if (column.sortable && !event.shiftKey) {
                if (event.ctrlKey && event.altKey) {
                    store.removeSorter(column.field);
                }
                else {
                    let sortBy = column.field;

                    if (typeof column.sortable === 'function') {
                        sortBy = {
                            field : column.field,
                            fn    : column.sortable
                        };
                    }
                    else if (typeof column.sortable === 'object') {
                        sortBy = column.sortable;
                        // Handle mismatch between Store#sort and CollectionSorter (field/property)
                        if (!sortBy.field) {
                            sortBy.field = sortBy.property || column.field;
                        }
                    }
                    store.sort(sortBy, null, event.ctrlKey);
                }
            }
        }
    }

    /**
     * Called when grid headers are rendered, make headers match current sorters.
     * @private
     */
    renderHeader() {
        this.syncHeaderSortState();
    }

    //endregion
}

Sort.featureClass = 'b-sort';

GridFeatureManager.registerFeature(Sort, true);
