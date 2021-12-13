import WidgetHelper from '../../Core/helper/WidgetHelper.js';
import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../feature/GridFeatureManager.js';

/**
 * @module Grid/feature/FilterBar
 */

/**
 * Feature that allows filtering of the grid by entering filters on column headers.
 * The actual filtering is done by the store.
 * For info on programmatically handling filters, see {@link Core.data.mixin.StoreFilter StoreFilter}.
 *
 * This feature is <strong>disabled</strong> by default.
 *
 * The individual filterability of columns is defined by a `filterable`
 * property on the column which defaults to `true`. If `false`, that column
 * is not filterable.
 *
 * The property value may also be a custom filter function.
 *
 * The property value may also be an object which may contain the following two properties:
 *  - **filterFn** : `Function` A custom filtering function
 *  - **filterField** : `Object` A config object for the filter value input field.
 *
 * @extends Core/mixin/InstancePlugin
 *
 * @example
 * // filtering turned on but no default filter
 * let grid = new Grid({
 *   features: {
 *     filterBar : true
 *   }
 * });
 *
 * @example
 * // using default filter
 * let grid = new Grid({
 *   features : {
 *     filterBar : { filter: { property : 'city', value : 'Gavle' } }
 *   }
 * });
 *
 * @example
 * // Custom filtering function for a column
 * let grid = new Grid({
 *   features : {
 *     filter : true
 *   },
 *
 *   columns: [
 *      {
 *        field      : 'age',
 *        text       : 'Age',
 *        type       : 'number',
 *        // Custom filtering function that checks "greater than"
 *        filterable : ({ record, value }) => record.age > value
 *      },
 *        field : 'name',
 *        // Filterable may specify a filterFn and a config for the filtering input field
 *        filterable : {
 *          filterFn : ({ record, value }) => record.name.toLowerCase().indexof(value.toLowerCase()) !== -1,
 *          filterField : {
 *            emptyText : 'Filter name'
 *          }
 *        }
 *      }
 *   ]
 * });
 *
 * @demo Grid/filterbar
 * @classtype filterBar
 * @externalexample feature/FilterBar.js
 */
export default class FilterBar extends InstancePlugin {
    //region Default config
    static get $name() {
        return 'FilterBar';
    }

    static get defaultConfig() {
        return {
            /**
             * The delay in milliseconds to wait after the last keystroke before applying filters.
             * Set to 0 to not trigger filtering from keystrokes, requires pressing ENTER instead
             * @config {Number}
             * @default
             */
            keyStrokeFilterDelay : 300,

            // Destroying data level filters when we hiding UI is supposed to be optional someday. So far this flag is private
            clearStoreFiltersOnHide : true
        };
    }

    //endregion

    //region Init

    construct(grid, config) {
        const me = this;

        Object.assign(me, {
            filterFieldCls           : 'b-filter-bar-field',
            filterFieldInputCls      : 'b-filter-bar-field-input',
            filterableColumnCls      : 'b-filter-bar-enabled',
            filterFieldInputSelector : '.b-filter-bar-field-input',
            filterableColumnSelector : '.b-filter-bar-enabled',
            filterParseRegExp        : /^\s*([<>=*])?(.*)$/,
            storeTrackingSupended    : 0,
            store                    : grid.store,
            grid                     : grid
        });

        me.onColumnFilterFieldChange = me.onColumnFilterFieldChange.bind(me);

        super.construct(grid, Array.isArray(config) ? {
            filter : config
        } : config);

        me.store.on({
            filter  : me.onStoreFilter,
            thisObj : me
        });

        if (me.filter) {
            me.store.filter(me.filter);
        }

        me.gridDetacher = grid.on('beforeelementclick', me.onBeforeElementClick, me);
    }

    doDestroy() {
        const me = this;

        me.destroyFilterBar();
        me.gridDetacher && me.gridDetacher();

        super.doDestroy();
    }

    doDisable(disable) {
        const { columns } = this.grid;

        // hide the fields, each silently - no updating of the store's filtered state until the end
        columns && columns.forEach(column => {
            const widget = this.getColumnFilterField(column);
            if (widget) {
                widget.disabled = disable;
            }
        });

        super.doDisable(disable);
    }

    static get pluginConfig() {
        return {
            before : ['onElementKeyDown'],
            chain  : ['renderHeader', 'getHeaderMenuItems']
        };
    }

    //endregion

    //region FilterBar

    destroyFilterBar() {
        this.grid.columns && this.grid.columns.forEach(this.destroyColumnFilterField, this);
    }

    /**
     * Hides the filtering fields.
     */
    hideFilterBar() {
        const
            me      = this,
            columns = me.grid.columns;

        // we don't want to hear back store "filter" event while we're resetting store filters
        me.clearStoreFiltersOnHide && me.suspendStoreTracking();

        // hide the fields, each silently - no updating of the store's filtered state until the end
        columns && columns.forEach(col => me.hideColumnFilterField(col, true));

        // Now update the filtered state
        me.grid.store.filter();

        me.clearStoreFiltersOnHide && me.resumeStoreTracking();

        me.hidden = true;
    }

    /**
     * Shows the filtering fields.
     */
    showFilterBar() {
        this.renderFilterBar();

        this.hidden = false;
    }

    /**
     * Toggles the filtering fields visibility.
     */
    toggleFilterBar() {
        const me = this;

        if (me.hidden) {
            me.showFilterBar();
        }
        else {
            me.hideFilterBar();
        }
    }

    /**
     * Renders the filtering fields for filterable columns.
     * @private
     */
    renderFilterBar() {
        this.grid.columns.visibleColumns.forEach(column => this.renderColumnFilterField(column));
        this.rendered = true;
    }

    //endregion

    //region FilterBar fields

    /**
     * Renders text field filter in the provided column header.
     * @param {Grid.column.Column} column Column to render text field filter for.
     * @private
     */
    renderColumnFilterField(column) {
        const
            me         = this,
            grid       = me.grid,
            filterable = me.getColumnFilterable(column);

        // we render fields for filterable columns only
        if (filterable && !column.hidden) {
            const headerEl = column.element;

            let widget = me.getColumnFilterField(column);

            // if we don't haven't created a field yet
            // we build it from scratch
            if (!widget) {
                const
                    filter = grid.store.filters.getBy('property', column.field),
                    type   = `${column.filterType || 'text'}field`;

                widget = WidgetHelper.append(Object.assign({
                    type,
                    owner                : me.grid,
                    clearable            : true,
                    column               : column,
                    name                 : column.field,
                    value                : filter && me.buildFilterString(filter),
                    cls                  : me.filterFieldCls,
                    inputCls             : me.filterFieldInputCls,
                    keyStrokeChangeDelay : me.keyStrokeFilterDelay,
                    onChange             : me.onColumnFilterFieldChange,
                    onClear              : me.onColumnFilterFieldChange,
                    disabled             : me.disabled
                }, filterable.filterField), headerEl)[0];

                me.setColumnFilterField(column, widget);
            }
            // if we have one..
            else {
                // re-apply widget filter
                me.onColumnFilterFieldChange({ source : widget, value : widget.value });
                // re-append the widget to its parent node (in case the column header was redrawn (happens when resizing columns))
                widget.render(headerEl);
                // show widget in case it was hidden
                widget.show();
            }

            headerEl.classList.add(me.filterableColumnCls);
        }
    }

    /**
     * Fills in column filter fields with values from the grid store filters.
     * @private
     */
    updateColumnFilterFields() {
        const
            me   = this,
            grid = me.grid;

        let field, filter;

        for (const column of grid.columns) {
            field = me.getColumnFilterField(column);
            if (field) {
                filter = grid.store.filters.getBy('property', column.field);
                field.value = filter && me.buildFilterString(filter) || '';
            }
        }
    }

    getColumnFilterable(column) {
        if (!column.isRoot && column.filterable !== false && column.field) {
            if (typeof column.filterable === 'function') {
                column.filterable = {
                    filterFn : column.filterable
                };
            }
            return column.filterable;
        }
    }

    destroyColumnFilterField(column) {
        const me     = this,
            widget = me.getColumnFilterField(column);

        if (widget) {
            me.hideColumnFilterField(column);
            // destroy filter UI field
            widget.destroy();
            // remember there is no field bound anymore
            me.setColumnFilterField(column, undefined);
        }
    }

    hideColumnFilterField(column, silent) {
        const me       = this,
            store    = me.grid.store,
            columnEl = column.element,
            widget   = me.getColumnFilterField(column);

        if (widget) {
            // hide field
            widget.hide();

            if (me.clearStoreFiltersOnHide && column.field) {
                store.removeFieldFilter(column.field, silent);
            }

            columnEl.classList.remove(me.filterableColumnCls);
        }
    }

    getColumnFilterField(column) {
        return this._columnFilters && this._columnFilters[column.data.id];
    }

    setColumnFilterField(column, widget) {
        this._columnFilters = this._columnFilters || {};

        this._columnFilters[column.data.id] = widget;
    }

    //endregion

    //region Filters

    parseFilterValue(value) {
        const match = String(value).match(this.filterParseRegExp);

        return {
            operator : match[1] || '*',
            value    : match[2]
        };
    }

    buildFilterString(filter) {
        let result;

        if (filter && !filter.initialConfig.filterBy) {
            result = (filter.operator === '*' ? '' : filter.operator) + filter.value;
        }

        return result;
    }

    //endregion

    // region Events

    /**
     * Fires when store gets filtered. Refreshes field values in column headers.
     * @private
     */
    onStoreFilter() {
        if (!this.storeTrackingSupended && this.rendered) {
            this.updateColumnFilterFields();
        }
    }

    suspendStoreTracking() {
        this.storeTrackingSupended++;
    }

    resumeStoreTracking() {
        this.storeTrackingSupended--;
    }

    /**
     * Called after headers are rendered, make headers match stores initial sorters
     * @private
     */
    renderHeader() {
        this.renderFilterBar();
    }

    onElementKeyDown(event) {
        const me = this;

        // flagging event with handled = true used to signal that other features should probably not care about it
        if (event.handled) return;

        // if we are pressing left/right arrow keys while being in a filter editor
        // we set event.handled flag (otherwise other features prevent the event)
        if (event.target.matches(me.filterFieldInputSelector)) {
            switch (event.key) {
                case 'ArrowLeft':
                case 'ArrowRight':
                    event.handled = true;
            }
        }
    }

    onBeforeElementClick({ event }) {
        const me = this;

        // prevent other features reacting when clicking a filter field (or any element inside it)
        if (event.target.closest('.' + me.filterFieldCls)) {
            return false;
        }
    }

    /**
     * Called when a column text filter field value is changed by user.
     * @param  {TextField} field Filter text field.
     * @param  {String} value New filtering value.
     * @private
     */
    onColumnFilterFieldChange({ source : field, value }) {
        const me   = this,
            store = me.grid.store,
            filterable = me.getColumnFilterable(field.column);

        // we don't want to hear back store "filter" event
        // so we suspend store tracking
        me.suspendStoreTracking();

        if (value == null || value === '') {
            // remove filter if setting to empty
            store.removeFieldFilter(field.name);
        }
        else if (filterable.filterFn) {
            store.filter({
                filterBy : record => filterable.filterFn({ property : field.name, value, record }),
                // To be able to clear the filter
                property : field.name
            });
        }
        else {
            store.filter(Object.assign({
                property : field.name
            }, me.parseFilterValue(value)));
        }

        me.resumeStoreTracking();
    }

    //endregion

    //region Menu items

    /**
     * Adds a menu item to toggle filter bar visibility.
     * @param column Column
     * @param items Menu items
     * @returns {Object[]} Menu items.
     * @private
     */
    getHeaderMenuItems(column, items) {
        const me = this;

        items.push({
            text   : me.L(me.hidden ? 'enableFilterBar' : 'disableFilterBar'),
            name   : 'toggleFilterBar',
            icon   : 'b-fw-icon b-icon-filter',
            cls    : 'b-separator',
            onItem : () => me.toggleFilterBar()
        });
    }

    //endregion
}

FilterBar.featureClass = 'b-filter-bar';

GridFeatureManager.registerFeature(FilterBar);
