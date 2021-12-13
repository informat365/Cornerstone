//TODO: Expand function?
//TODO: Collapse function?
//TODO: Sorting breaks grouping if some groups are collapsed
//TODO: Grouping via context menu doesn't work

import DomDataStore from '../../Core/data/DomDataStore.js';
import DomHelper from '../../Core/helper/DomHelper.js';
import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../feature/GridFeatureManager.js';

/**
 * @module Grid/feature/Group
 */

/**
 * Enables rendering and handling of row groups. The actual grouping is done in the store, but triggered by [shift] +
 * clicking headers or by using two finger tap (one on header, one anywhere on grid). Groups can be expanded/collapsed
 * by clicking on the group row or pressing [space] when group row is selected.
 *
 * This feature is <strong>enabled</strong> by default.
 *
 * @example
 * // grouping turned on but no default grouper
 * let grid = new Grid({
 *   features: {
 *     group: true
 *   }
 * });
 *
 * // default grouper, group by field 'name'
 * let grid = new Grid({
 *   features: {
 *     group: 'name'
 *   }
 * });
 *
 * // default grouper and custom renderer, which will be applied to each cell except the "group" cell
 * let grid = new Grid({
 *   features: {
 *     group: {
 *       field: 'name',
 *       ascending: false,
 *       renderer: () => 'Text'
 *     }
 *   }
 * });
 *
 * @extends Core/mixin/InstancePlugin
 *
 * @demo Grid/grouping
 * @classtype group
 * @externalexample feature/Group.js
 */
export default class Group extends InstancePlugin {

    static get $name() {
        return 'Group';
    }

    static get defaultConfig() {
        return {
            /**
             * The name of the record field to group by.
             * @config {String}
             * @default
             */
            field : null,

            /**
             * A function which yields the HTML for a group header when passed a data block.
             * The function is called in the context of this Group feature object.
             * The data block passed contains the following properties:
             *  - `groupRowFor` The value of the {@link #config-field} for the group.
             *  - `groupColumn` Optionally, the column under which the {@link #config-field} is shown.
             *  - `record` The group record representing the group, containing a `groupChildren` property which contains the child records for the group.
             *
             * The default renderer displays the `groupRowFor` and `record.meta.childCount`
             * @config {Function}
             * @default
             */
            renderer : null
        };
    }

    //region Init

    construct(grid, config) {
        const me = this;

        if (grid.features.tree) {
            return;
        }

        // groupSummary feature needs to be initialized first, if it is used
        me._thisIsAUsedExpression(grid.features.groupSummary);

        // process initial config into an actual config object
        config = me.processConfig(config);

        Object.assign(me, {
            grid       : grid,
            rowManager : grid.rowManager,
            store      : grid.store
        });

        super.construct(grid, config);

        me.store.on({
            group       : me.onStoreGroup,
            groupingoff : me.onStoreGroupingOff,
            thisObj     : me
        });

        me.rowManager.on({
            rendercell : me.renderCell,
            thisObj    : me
        });
    }

    // Group feature handles special config cases, where user can supply a string or a group config object
    // instead of a normal config object
    processConfig(config) {
        if (typeof config === 'string') {
            return {
                field     : config,
                ascending : null
            };
        }

        return config;
    }

    // override setConfig to process config before applying it (used mainly from ReactGrid)
    setConfig(config) {
        if (config === null) {
            this.store.clearGroupers();
        }
        else {
            super.setConfig(this.processConfig(config));
        }
    }

    set renderer(renderer) {
        this.groupRenderer = renderer;
    }

    set field(field) {
        this._field = field;
        this.store.group(field, this.ascending);
    }

    get field() {
        return this._field;
    }

    doDestroy() {
        super.doDestroy();
    }

    doDisable(disable) {
        const { store } = this;

        // Grouping mostly happens in store, need to clear groupers there to remove headers.
        // Use configured groupers as first sorters to somewhat maintain the order
        if (disable && store.isGrouped) {
            const { sorters } = store;
            sorters.unshift(...store.groupers);
            store.clearGroupers();
            store.sort(sorters);
        }

        super.doDisable(disable);
    }

    //endregion

    //region Plugin config

    // Plugin configuration. This plugin chains some of the functions in Grid.
    static get pluginConfig() {
        return {
            assign : ['collapseAll', 'expandAll'],
            chain  : ['renderHeader', 'getHeaderMenuItems',
                'getColumnDragToolbarItems', 'onElementTouchStart',
                'onElementClick', 'onElementKeyDown'],
            override : [
                'onStoreDataChange'
            ]
        };
    }

    //endregion

    //region Expand/collapse

    /**
     * Collapses or expands a group depending on its current state
     * @param recordOrId Record or records id for a group row to collapse or expand
     * @param collapse Force collapse (true) or expand (true)
     * @fires togglegroup
     */
    toggleCollapse(recordOrId, collapse) {
        this.internalToggleCollapse(recordOrId, collapse);
    }

    /**
     * Collapses or expands a group depending on its current state
     * @param recordOrId Record or records id for a group row to collapse or expand
     * @param collapse Force collapse (true) or expand (true)
     * @param skipRender True to not render rows
     * @internal
     * @fires togglegroup
     */
    internalToggleCollapse(recordOrId, collapse, skipRender = false) {
        //<debug>
        if (recordOrId == null) {
            throw new Error('Group#toggleCollapse must be passed a record');
        }
        //</debug>

        const
            me          = this,
            store       = me.store,
            groupRecord = store.getById(recordOrId),
            meta        = groupRecord.meta;

        if (meta.groupRowFor === undefined) return;

        collapse = collapse === undefined ? !meta.collapsed : collapse;

        if (collapse) {
            store.excludeGroupRecords(groupRecord);
        }
        else {
            store.includeGroupRecords(groupRecord);
        }

        // note to self, all this could be done in collapsed setter on GroupModel if desired...
        meta.collapsed = collapse;

        /**
         * Group expanded or collapsed
         * @event toggleGroup
         * @param groupRecord Group record
         * @param {Boolean} collapse Collapsed (true) or expanded (false)
         */
        me.grid.trigger('toggleGroup', { groupRecord, collapse });

        if (!skipRender) {
            // render from grouprecord and down, no need to touch those above
            me.rowManager.renderFromRecord(groupRecord);
        }
    }

    /**
     * Collapse all groups. This function is exposed on Grid and can thus be called as `grid.collapseAll()`
     */
    collapseAll() {
        const me = this;
        if (me.store.isGrouped && !me.disabled) {
            me.store.groupRecords.forEach(r => me.internalToggleCollapse(r, true, true));
            me.grid.refreshRows(true);
        }
    }

    /**
     * Expand all groups. This function is exposed on Grid and can thus be called as `grid.expandAll()`
     */
    expandAll() {
        const me = this;
        if (me.store.isGrouped && !me.disabled) {
            me.store.groupRecords.forEach(r => me.internalToggleCollapse(r, false, true));
            me.grid.refreshRows();
        }
    }

    //endregion

    //region Rendering

    /**
     * Called when a cell is rendered, styles the group rows first cell.
     * @private
     */
    renderCell(renderData) {
        const me = this;

        // no need to run the code below if not grouping or of feature is disabled
        if (!me.store.isGrouped || me.disabled) {
            return;
        }

        const
            {
                cellElement,
                rowElement,
                column
            }                   = renderData,
            grid                = me.grid,
            meta                = renderData.record.meta,
            firstColumn         = grid.columns.visibleColumns[0],
            firstSubGridColumns = grid.subGrids[grid.regions[0]].columns.visibleColumns;

        if (meta.hasOwnProperty('groupRowFor')) {
            // Let column clear the cell, in case it needs to do some cleanup
            column.clearCell(cellElement);

            // this is a group row, add css classes

            // IE11 doesnt support this
            //rowElement.classList.toggle('b-grid-group-collapsed', meta.collapsed === undefined ? false : meta.collapsed);
            rowElement.classList[meta.collapsed ? 'add' : 'remove']('b-grid-group-collapsed');

            rowElement.classList.add('b-group-row');

            if (
                (firstColumn.type === 'rownumber' && column === firstSubGridColumns[1]) ||
                (firstColumn.type !== 'rownumber' && column === firstSubGridColumns[0])
            ) {
                cellElement.classList.add('b-group-title');
            }

            me.buildGroupHeader(renderData);
        }
        else {
            // not a group row, remove css classes
            // TODO: use dom query to remove before render instead?
            if (cellElement === rowElement.firstElementChild) {
                // IE11 doesnt support this
                //rowElement.classList.remove('b-group-row', 'b-grid-group-collapsed');
                rowElement.classList.remove('b-group-row');
                rowElement.classList.remove('b-grid-group-collapsed');
                cellElement.classList.remove('b-group-title');
            }
        }
    }

    // renderData.cellElement is required
    buildGroupHeader(renderData) {
        const
            me                  = this,
            {
                record,
                cellElement,
                column,
                persist
            }                   = renderData,
            grid                = me.grid,
            meta                = record.meta,
            // Need to adjust count if group summary is used
            // TODO remove this when grouping has been refactored to not store group headers/footers in the Store
            count               = meta.childCount - (grid.features.groupSummary ? 1 : 0),
            firstVisibleColumn  = grid.columns.visibleColumns[0],
            firstSubGridColumns = column && grid.subGrids[grid.regions[0]].columns.visibleColumns,
            isFirstColumn       =
                (firstVisibleColumn.type === 'rownumber' && column === firstSubGridColumns[1]) ||
                (firstVisibleColumn.type !== 'rownumber' && column === firstSubGridColumns[0]);

        let html         = null,
            applyDefault = true;

        if (persist || column) {
            const groupColumn = grid.columns.get(meta.groupField);
            // First try using columns groupRenderer (might not even have a column if grouping programmatically)
            if (groupColumn && groupColumn.groupRenderer) {
                if (isFirstColumn) {
                    // groupRenderer could return nothing and just apply changes directly to DOM element
                    html = groupColumn.groupRenderer(Object.assign({}, renderData, {
                        groupRowFor  : meta.groupRowFor,
                        groupRecords : record.groupChildren,
                        groupColumn,
                        count
                    }));

                    applyDefault = false;
                }
            }
            // Secondly use features groupRenderer, if configured with one
            else if (me.groupRenderer) {
                // groupRenderer could return nothing and just apply changes directly to DOM element
                html = me.groupRenderer(Object.assign({}, renderData, {
                    groupRowFor  : meta.groupRowFor,
                    groupRecords : record.groupChildren,
                    groupColumn,
                    count,
                    isFirstColumn
                }));
            }

            // Third, just display unformatted value and child count (also applied for features groupRenderer that do
            // not output any html of their own)
            if (isFirstColumn && html == null && applyDefault) {
                html = `${meta.groupRowFor == null ? '' : meta.groupRowFor} (${count})`;
            }
        }
        else if (me.groupRenderer) {
            // groupRenderer could return nothing and just apply changes directly to DOM element
            html = me.groupRenderer(renderData);
        }

        // Renderers could return nothing and just apply changes directly to DOM element
        if (typeof html === 'string') {
            cellElement.innerHTML = html;
        }

        // If groupRenderer added elements to the cell, we need to remember that to clear it on re-usage as a normal cell
        if (cellElement.childElementCount > 0) {
            cellElement._hasHtml = true;
        }

        return cellElement.innerHTML;
    }

    /**
     * Called when an header is rendered, adds grouping icon if grouped by that column.
     * @private
     * @param headerContainerElement
     */
    renderHeader(headerContainerElement) {
        const { store, grid } = this;

        if (store.isGrouped) {
            // Sorted from start, reflect in rendering
            for (const groupInfo of store.groupers) {
                // Might be grouping by field without column, which is valid
                const
                    column = grid.columns.get(groupInfo.field),
                    header = column && grid.getHeaderElement(column.id);
                // IE11 doesnt support this
                //header && header.classList.add('b-group', groupInfo.ascending ? 'b-asc' : 'b-desc');
                if (header) {
                    header.classList.add('b-group');
                    header.classList.add(groupInfo.ascending ? 'b-asc' : 'b-desc');
                }
            }
        }
    }

    //endregion

    //region Context menu

    /**
     * Supply items for headers context menu.
     * @private
     * @param column Header for this column
     * @param items Add items to this array
     * @returns {Object[]} Modified items array
     */
    getHeaderMenuItems(column, items) {
        const
            me    = this,
            store = me.store;

        if (column.groupable !== false) {
            items.push({
                text     : me.L('groupAscending'),
                icon     : 'b-fw-icon b-icon-group-asc',
                name     : 'groupAsc',
                cls      : 'b-separator',
                weight   : 110,
                disabled : me.disabled,
                onItem   : ({ item : { column } }) => store.group(column.field, true)
            }, {
                text     : me.L('groupDescending'),
                icon     : 'b-fw-icon b-icon-group-desc',
                name     : 'groupDesc',
                weight   : 110,
                disabled : me.disabled,
                onItem   : ({ item : { column } }) => store.group(column.field, false)
            });
        }

        if (store.isGrouped) {
            items.push({
                text     : me.L('stopGrouping'),
                icon     : 'b-fw-icon b-icon-clear',
                name     : 'groupRemove',
                cls      : column.groupable ? '' : 'b-separator',
                weight   : 110,
                disabled : me.disabled,
                onItem   : ({ item : { column } }) => store.clearGroupers()
            });
        }

    }

    /**
     * Supply items to ColumnDragToolbar
     * @private
     */
    getColumnDragToolbarItems(column, items) {
        const
            me    = this,
            store = me.store;

        items.push({
            text     : me.L('groupAscendingShort'),
            group    : me.L('Group'),
            icon     : 'b-icon b-icon-group-asc',
            name     : 'groupAsc',
            cls      : 'b-separator',
            weight   : 110,
            disabled : me.disabled,
            onDrop   : ({ column }) => store.group(column.field, true)
        });

        items.push({
            text     : me.L('groupDescendingShort'),
            group    : me.L('Group'),
            icon     : 'b-icon b-icon-group-desc',
            name     : 'groupDesc',
            weight   : 110,
            disabled : me.disabled,
            onDrop   : ({ column }) => store.group(column.field, false)
        });

        const grouped = store.groupers && store.groupers.some(col => col.field === column.field) && !me.disabled;
        items.push({
            text     : me.L('stopGroupingShort'),
            group    : me.L('Group'),
            icon     : 'b-icon b-icon-clear',
            name     : 'groupRemove',
            disabled : !grouped,
            weight   : 110,
            onDrop   : ({ column }) => store.removeGrouper(column.field)
        });

        return items;
    }

    //endregion

    //region Events - Store

    /**
     * Called when store grouping changes. Reflects on header and rerenders rows.
     * @private
     */
    onStoreGroup({ groupers }) {
        const
            { grid }        = this,
            { element }     = grid,
            curGroupHeaders = element && DomHelper.children(element, '.b-grid-header.b-group');

        if (element) {
            for (const header of curGroupHeaders) {
                DomHelper.removeClasses(header, ['b-group', 'b-asc', 'b-desc']);
            }

            if (groupers) {
                for (const groupInfo of groupers) {
                    const header = grid.getHeaderElementByField(groupInfo.field);
                    if (header) {
                        DomHelper.addClasses(header, ['b-group', groupInfo.ascending ? 'b-asc' : 'b-desc']);
                    }
                }
            }
        }
    }

    /**
     * Called when grouping is turned of in the store. Updates headers and rows.
     * @private
     */
    onStoreGroupingOff() {
        const curGroupHeaders = DomHelper.children(this.grid.element, '.b-grid-header.b-group');

        for (const header of curGroupHeaders) {
            DomHelper.removeClasses(header, ['b-group', 'b-asc', 'b-desc']);
        }

        this.grid.refreshRows();
    }

    //endregion

    //region Events - Grid

    /**
     * Store touches when user touches header, used in onElementTouchEnd.
     * @private
     */
    onElementTouchStart(event) {
        const
            me     = this,
            target = event.target,
            header = DomHelper.up(target, '.b-grid-header'),
            column = header && me.grid.getColumnFromElement(header);

        // If it's a multi touch, group.
        if (event.touches.length > 1 && column && column.groupable !== false && !me.disabled) {
            me.store.group(column.field);
        }
    }

    /**
     * React to click on headers (to group by that column if [alt] is pressed) and on group rows (expand/collapse).
     * @private
     * @param event
     * @returns {Boolean}
     */
    onElementClick(event) {
        const
            me     = this,
            store  = me.store,
            target = event.target,
            row    = DomHelper.up(target, '.b-group-row'),
            header = DomHelper.up(target, '.b-grid-header'),
            field  = header && header.dataset.column;

        if (target.classList.contains('b-resizer') || me.disabled) {
            return;
        }

        // Header
        if (header && field) {
            const columnGrouper = store.groupers && store.groupers.find(g => g.field === field);

            // Store has a grouper for this column's field; flip grouper order
            if (columnGrouper && !event.shiftKey) {
                store.group(field, !columnGrouper.ascending);
                return false;
            }
            // Group or ungroup
            else if (event.shiftKey) {
                const column = me.grid.columns.get(field);

                if (column.groupable !== false) {
                    if (event.altKey) {
                        store.removeGrouper(field);
                    }
                    else {
                        store.group(field);
                    }
                }
            }
            event.preventDefault();
        }

        // Anywhere on group-row
        if (row) {
            me.internalToggleCollapse(DomDataStore.get(row).id);
            return false;
        }
    }

    /**
     * Toggle groups with [space].
     * @private
     * @param event
     * @returns {*}
     */
    onElementKeyDown(event) {
        const me = this;

        // only catch space on grid element, not in editors etc...
        if (!me.disabled && me.store.isGrouped && event.target === me.client.element && event.key === ' ' && me.grid.focusedCell) {
            event.preventDefault();

            me.internalToggleCollapse(me.grid.focusedCell.id);
        }
    }

    //endregion

    //region Grid overrides

    onStoreDataChange(data) {
        const store = data.source;

        // Grouping mixin needs to process data which then makes sure UI is refreshed
        if (store.isGrouped && store.count > 0) {
            return;
        }

        this.overridden.onStoreDataChange(data);
    }

    //endregion
}

GridFeatureManager.registerFeature(Group, true, ['Grid', 'Scheduler']);
