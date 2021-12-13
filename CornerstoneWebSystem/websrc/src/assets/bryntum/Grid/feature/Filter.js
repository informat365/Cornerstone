//TODO: Format value in header filter tooltip (see date)

import DomHelper from '../../Core/helper/DomHelper.js';
import WidgetHelper from '../../Core/helper/WidgetHelper.js';
import Tooltip from '../../Core/widget/Tooltip.js';
import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../feature/GridFeatureManager.js';

/**
 * @module Grid/feature/Filter
 */

/**
 * Feature that allows filtering of the grid by settings filters on columns. The actual filtering is done by the store.
 * For info on programmatically handling filters, see {@link Core.data.mixin.StoreFilter StoreFilter}.
 *
 * This feature is <strong>disabled</strong> by default.
 *
 * @extends Core/mixin/InstancePlugin
 *
 * @example
 * // Filtering turned on but no default filter
 * let grid = new Grid({
 *   features : {
 *     filter : true
 *   }
 * });
 *
 * // Using default filter
 * let grid = new Grid({
 *   features : {
 *     filter : { property : 'city', value : 'Gavle' }
 *   }
 * });
 *
 * // Custom filtering function for a column
 * let grid = new Grid({
 *    features : {
 *        filter : true
 *    },
 *
 *    columns: [
 *        {
 *          field      : 'age',
 *          text       : 'Age',
 *          type       : 'number',
 *          // Custom filtering function that checks "greater than" no matter which field user filled in :)
 *          filterable : ({ record, value, operator }) => record.age > value
 *        }
 *    ]
 * });
 *
 * @demo Grid/filtering
 * @classtype filter
 * @externalexample feature/Filter.js
 */
export default class Filter extends InstancePlugin {
    //region Init

    static get $name() {
        return 'Filter';
    }

    // Plugin configuration. This plugin chains some of the functions in Grid.
    static get pluginConfig() {
        return {
            chain : ['renderHeader', 'getCellMenuItems', 'getHeaderMenuItems', 'onElementClick']
        };
    }

    construct(grid, config) {
        const me = this;

        me.grid = grid;
        me.store = grid.store;
        me.closeFilterEditor = me.closeFilterEditor.bind(me);

        super.construct(grid, config);

        me.store.on({ filter : me.onStoreFilter }, me);

        if (config && typeof config === 'object') {
            me.store.filter(config, null, me.client.isConfiguring);
        }
    }

    //endregion

    //region Plugin config

    doDestroy() {
        const me = this;

        me.filterTip && me.filterTip.destroy();
        me.filterEditorPopup && me.filterEditorPopup.destroy();

        me.store.un({ sort : me.onStoreFilter }, me);

        super.doDestroy();
    }

    //endregion

    //region Refresh headers

    /**
     * Update headers to match stores filters. Called on store load and grid header render.
     * @param reRenderRows Also refresh rows?
     * @private
     */
    refreshHeaders(reRenderRows) {
        const
            me      = this,
            grid    = me.grid,
            element = grid.headerContainer;

        if (element) {
            // remove .latest from all filters, will be applied to actual latest
            DomHelper.children(element, '.b-filter-icon.b-latest').forEach(iconElement => iconElement.classList.remove('b-latest'));

            if (!me.filterTip) {
                me.filterTip = new Tooltip({
                    forElement  : element,
                    forSelector : '.b-filter-icon',
                    getHtml({ forElement }) {
                        return forElement.dataset.filterText;
                    }
                });
            }

            for (const column of grid.columns) {
                if (column.filterable !== false) {
                    const
                        filter   = me.store.filters.getBy('property', column.field),
                        headerEl = column.element;

                    if (headerEl) {
                        const textEl = column.textWrapper;

                        let filterIconEl = textEl && textEl.querySelector('.b-filter-icon'),
                            filterText;

                        if (filter) {
                            filterText = me.L('filter') + ': ' + (typeof filter === 'string'
                                ? filter
                                : `${filter.operator} ${filter.displayValue || filter.value || ''}`);
                            //TODO: filter.value needs to be formatted using column format or something
                        }
                        else {
                            filterText = me.L('applyFilter');
                        }

                        if (!filterIconEl) {
                            // putting icon in header text to have more options for positioning it
                            filterIconEl = DomHelper.createElement({
                                parent    : textEl,
                                tag       : 'div',
                                className : 'b-filter-icon',
                                dataset   : {
                                    filterText : filterText
                                }
                            });
                            headerEl.classList.add('b-filterable');
                        }
                        else {
                            filterIconEl.dataset.filterText = filterText;
                        }

                        // latest applied filter distinguished with class to enable highlighting etc.
                        if (column.field === me.store.latestFilterField) filterIconEl.classList.add('b-latest');

                        headerEl.classList[filter ? 'add' : 'remove']('b-filter');
                        // When IE11 support is dropped
                        // headerEl.classList.toggle('b-filter', !!filter);
                    }

                    column.meta.isFiltered = !!filter;
                }
            }

            if (reRenderRows) {
                grid.refreshRows();
            }
        }
    }

    //endregion

    //region Filter

    applyFilter(config) {
        const
            { store } = this,
            column    = this.grid.columns.get(config.property);

        if (typeof column.filterable === 'function') {
            store.filter({
                filterBy     : record => column.filterable(Object.assign({}, config, { record })),
                // To be able to retrieve the value next time filtering popup is shown, not actually used by the filter
                value        : config.value,
                property     : config.property,
                operator     : config.operator,
                displayValue : config.displayValue
            });
        }
        else {
            store.filter(config);
        }
    }

    // TODO: break out as own views, registering with Filter the same way columns register with ColumnManager

    getPopupDateItems(fieldType, filter, initialValue, field, store, changeCallback, closeCallback) {
        const
            me      = this,
            onClose = changeCallback,
            onClear = closeCallback;

        function onChange({ source, value }) {
            if (value == null) {
                closeCallback();
            }
            else {
                me.applyFilter({ property : field, operator : source.operator, value, displayValue : source._value });
                changeCallback();
            }
        }

        return [{
            type        : 'date',
            ref         : 'on',
            placeholder : me.L('on'),
            clearable   : true,
            label       : '<i class="b-fw-icon b-icon-filter-equal"></i>',
            value       : filter && filter.operator === '=' ? filter.value : initialValue,
            operator    : '=',
            onChange,
            onClose,
            onClear
        }, {
            type        : 'date',
            ref         : 'before',
            placeholder : me.L('before'),
            clearable   : true,
            label       : '<i class="b-fw-icon b-icon-filter-before"></i>',
            value       : filter && filter.operator === '<' ? filter.value : null,
            operator    : '<',
            onChange,
            onClose,
            onClear
        }, {
            type        : 'date',
            ref         : 'after',
            cls         : 'b-last-row',
            placeholder : me.L('after'),
            clearable   : true,
            label       : '<i class="b-fw-icon b-icon-filter-after"></i>',
            value       : filter && filter.operator === '>' ? filter.value : null,
            operator    : '>',
            onChange,
            onClose,
            onClear
        }];
    }

    getPopupNumberItems(fieldType, filter, initialValue, field, store, changeCallback, closeCallback) {
        const
            me      = this,
            onEsc   = changeCallback,
            onClear = closeCallback;

        function onChange({ source, value }) {
            if (value == null) {
                closeCallback();
            }
            else {
                me.applyFilter({ property : field, operator : source.operator, value });
                changeCallback();
            }
        }

        return [{
            type        : 'number',
            placeholder : me.L('equals'),
            clearable   : true,
            label       : '<i class="b-fw-icon b-icon-filter-equal"></i>',
            value       : filter && filter.operator === '=' ? filter.value : initialValue,
            operator    : '=',
            onChange,
            onEsc,
            onClear
        }, {
            type        : 'number',
            placeholder : me.L('lessThan'),
            clearable   : true,
            label       : '<i class="b-fw-icon b-icon-filter-less"></i>',
            value       : filter && filter.operator === '<' ? filter.value : null,
            operator    : '<',
            onChange,
            onEsc,
            onClear
        }, {
            type        : 'number',
            cls         : 'b-last-row',
            placeholder : me.L('moreThan'),
            clearable   : true,
            label       : '<i class="b-fw-icon b-icon-filter-more"></i>',
            value       : filter && filter.operator === '>' ? filter.value : null,
            operator    : '>',
            onChange,
            onEsc,
            onClear
        }];
    }

    getPopupStringItems(fieldType, filter, initialValue, field, store, changeCallback, closeCallback) {
        const me = this;

        return [{
            type        : fieldType,
            cls         : 'b-last-row',
            placeholder : me.L('filter'),
            clearable   : true,
            label       : '<i class="b-fw-icon b-icon-filter-equal"></i>',
            value       : filter ? filter.value || filter : initialValue,
            onChange    : ({ value }) => {
                if (value === '') {
                    closeCallback();
                }
                else {
                    me.applyFilter({ property : field, value });
                    changeCallback();
                }
            },
            onClose : changeCallback,
            onClear : closeCallback
        }];
    }

    /**
     * Get fields to display in filter popup.
     * @param fieldType Type of field, number, date etc.
     * @param filter Current filter filter
     * @param initialValue
     * @param field Column
     * @param store Grid store
     * @param changeCallback Callback for when filter has changed
     * @param closeCallback Callback for when editor should be closed
     * @returns {*}
     * @private
     */
    getPopupItems(fieldType, filter, initialValue, field, store, changeCallback, closeCallback) {
        switch (fieldType) {
            case 'date':
                return this.getPopupDateItems(...arguments);
            case 'number':
                return this.getPopupNumberItems(...arguments);
            default:
                return this.getPopupStringItems(...arguments);
        }
    }

    /**
     * Shows a popup where a filter can be edited.
     * @param {Grid.column.Column} column Column to show filter editor for
     * @param {*} value Value to init field with
     */
    showFilterEditor(column, value) {
        const
            me        = this,
            { store } = me,
            col       = typeof column === 'string' ? me.grid.columns.getById(column) : column,
            headerEl  = col.element,
            field     = store.modelClass.fieldMap[col.field],
            filter    = store.filters.getBy('property', col.field),
            type      = field && field.type || col.filterType || col.type || 'string',
            fieldType = {
                'string' : 'text',
                'number' : 'number',
                'date'   : 'date'
            }[type] || 'text';

        if (col.filterable === false) {
            return;
        }

        // Destroy previous filter popup
        me.closeFilterEditor();

        me.filterEditorPopup = WidgetHelper.openPopup(headerEl, {
            owner        : this.grid,
            width        : '16em',
            cls          : 'b-filter-popup',
            scrollAction : 'realign',
            items        : me.getPopupItems(
                fieldType,
                filter,
                value,
                col.field,
                me.store,
                me.closeFilterEditor,
                () => {
                    me.store.removeFieldFilter(col.field);
                    me.closeFilterEditor();
                }
            )
        });
    }

    /**
     * Close the filter editor.
     */
    closeFilterEditor() {
        const me = this;

        // Must defer the destroy because it may be closed by an event like a "change" event where
        // there may be plenty of code left to execute which must not execute on destroyed objects.
        me.filterEditorPopup && me.filterEditorPopup.setTimeout(me.filterEditorPopup.destroy);
        me.filterEditorPopup = null;
    }

    //endregion

    //region Context menu

    //TODO: break out together with getPopupXXItems() (see comment above)

    getMenuDateItems(column, record) {
        const
            me     = this,
            value  = record[column.field],
            filter = operator => {
                me.applyFilter({
                    property     : column.field,
                    operator,
                    value,
                    displayValue : column.formatValue ? column.formatValue(value) : value
                });
            };

        return [{
            text     : me.L('on'),
            icon     : 'b-fw-icon b-icon-filter-equal',
            cls      : 'b-separator',
            name     : 'filterDateEquals',
            disabled : me.disabled,
            onItem   : () => filter('=')
        }, {
            text     : me.L('before'),
            icon     : 'b-fw-icon b-icon-filter-before',
            name     : 'filterDateBefore',
            disabled : me.disabled,
            onItem   : () => filter('<')
        }, {
            text     : me.L('after'),
            icon     : 'b-fw-icon b-icon-filter-after',
            name     : 'filterDateAfter',
            disabled : me.disabled,
            onItem   : () => filter('>')
        }];
    }

    getMenuNumberItems(column, record) {
        const
            me     = this,
            filter = operator => {
                me.applyFilter({
                    property : column.field,
                    operator : operator,
                    value    : record[column.field]
                });
            };

        return [{
            text     : me.L('equals'),
            icon     : 'b-fw-icon b-icon-filter-equal',
            cls      : 'b-separator',
            name     : 'filterNumberEquals',
            disabled : me.disabled,
            onItem   : () => filter('=')
        }, {
            text     : me.L('lessThan'),
            icon     : 'b-fw-icon b-icon-filter-less',
            name     : 'filterNumberLess',
            disabled : me.disabled,
            onItem   : () => filter('<')
        }, {
            text     : me.L('moreThan'),
            icon     : 'b-fw-icon b-icon-filter-more',
            name     : 'filterNumberMore',
            disabled : me.disabled,
            onItem   : () => filter('>')
        }];
    }

    getMenuStringItems(column, record) {
        return [{
            text     : this.L('equals'),
            icon     : 'b-fw-icon b-icon-filter-equal',
            cls      : 'b-separator',
            name     : 'filterStringEquals',
            disabled : this.disabled,
            onItem   : () => {
                this.applyFilter({ property : column.field, value : record[column.field] });
            }
        }];
    }

    /**
     * Add menu items for filtering, depending on filter type etc.
     * @param column
     * @param record
     * @param items
     * @returns {Object[]}
     * @private
     */
    getCellMenuItems(column, record, items) {
        const
            me    = this,
            field = record.getFieldDefinition(column.field);

        if (column.filterable !== false) {
            const filterItems = [];

            if (column.meta.isFiltered) {
                filterItems.push({
                    text     : me.L('removeFilter'),
                    icon     : 'b-fw-icon b-icon-clear',
                    cls      : 'b-separator',
                    name     : 'filterRemove',
                    disabled : me.disabled,
                    onItem() {
                        me.store.removeFieldFilter(column.field);
                    }
                });
            }

            switch (column.filterType || column.type || (field && field.type)) {
                case 'date':
                    filterItems.push(...me.getMenuDateItems(...arguments));
                    break;
                case 'number':
                    filterItems.push(...me.getMenuNumberItems(...arguments));
                    break;
                default:
                    filterItems.push(...me.getMenuStringItems(...arguments));
                    break;
            }

            // remove separator from second item if filtered
            if (column.meta.isFiltered && filterItems.length > 1) {
                filterItems[1].cls = '';
            }

            items.push(...filterItems);
        }
    }

    /**
     * Add menu item for removing filter if column is filtered.
     * @private
     * @param column Column
     * @param items Menu items
     * @returns {Object[]}
     */
    getHeaderMenuItems(column, items) {
        const me = this;

        if (column.meta.isFiltered) {
            items.push({
                text     : me.L('editFilter'),
                name     : 'editFilter',
                icon     : 'b-fw-icon b-icon-filter',
                cls      : 'b-separator',
                disabled : me.disabled,
                onItem() {
                    me.showFilterEditor(column);
                }
            });
            items.push({
                text     : me.L('removeFilter'),
                name     : 'removeFilter',
                icon     : 'b-fw-icon b-icon-remove',
                disabled : me.disabled,
                onItem() {
                    me.store.removeFieldFilter(column.field);
                }
            });
        }
        else if (column.filterable !== false) {
            items.push({
                text     : me.L('filter'),
                name     : 'filter',
                icon     : 'b-fw-icon b-icon-filter',
                cls      : 'b-separator',
                disabled : me.disabled,
                onItem() {
                    me.showFilterEditor(column);
                }
            });
        }
    }

    //endregion

    //region Events

    /**
     * Store filtered; refresh headers.
     * @private
     */
    onStoreFilter() {
        // Pass false to not refresh rows.
        // Store's refresh event will refresh the rows.
        this.refreshHeaders(false);
    }

    /**
     * Called after headers are rendered, make headers match stores initial sorters
     * @private
     */
    renderHeader() {
        this.refreshHeaders(false);
    }

    /**
     * Called when user clicks on the grid. Only care about clicks on the filter icon.
     * @param {MouseEvent} event
     * @private
     */
    onElementClick(event) {
        const target = event.target;

        if (this.filterEditorPopup) this.closeFilterEditor();

        // Checks if click is on node expander icon, then toggles expand/collapse
        if (target.classList.contains('b-filter-icon')) {
            const headerEl = DomHelper.up(target, '.b-grid-header');

            this.showFilterEditor(headerEl.dataset.columnId);
            return false;
        }
    }

    //endregion
}

GridFeatureManager.registerFeature(Filter);
