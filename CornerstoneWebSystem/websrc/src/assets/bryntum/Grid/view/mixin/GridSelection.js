import Base from '../../../Core/Base.js';
import Collection from '../../../Core/util/Collection.js';
import Model from '../../../Core/data/Model.js';
import ColumnStore from '../../data/ColumnStore.js';

const
    validIdTypes = {
        string : 1,
        number : 1
    },
    isSelectAction = {
        dataset : 1,
        batch   : 1
    };

/**
 * @module Grid/view/mixin/GridSelection
 */

/**
 * A mixin for Grid that handles row and cell selection. See {@link #config-selectionMode} for details on how to control what should be selected (rows or cells)
 *
 * @example
 * // select a row
 * grid.selectedRow = 7;
 *
 * // select a cell
 * grid.selectedCell = { id: 5, columnId: 'column1' }
 *
 * // select a record
 * grid.selectedRecord = grid.store.last;
 *
 * // select multiple records by ids
 * grid.selectedRecords = [1, 2, 4, 6]
 *
 * @mixin
 */
export default Target => class GridSelection extends (Target || Base) {
    static get defaultConfig() {
        return {
            /**
             * The selection settings, where you can set these boolean flags to control what is selected:
             * - `row` - select rows
             * - `cell` - select cells
             * - `rowCheckboxSelection` - select rows only when clicking in the checkbox column
             * - `multiSelect` - Allow multiple selection
             * - `checkbox` - true to add a checkbox selection column to the grid
             * @config {Object} selectionMode
             * @default
             * @category Selection
             */
            selectionMode : {
                row                  : true,
                cell                 : true,
                rowCheckboxSelection : false,
                multiSelect          : true,
                checkbox             : false
            },

            recordCollection : {}
        };
    }

    //region Init

    afterConfigure() {
        const me = this;

        // Inject our CheckColumn into the ColumnStore
        if (me.selectionMode.checkbox) {
            const checkColumnClass = ColumnStore.getColumnClass('check');

            if (!checkColumnClass) {
                throw new Error('CheckColumn must be imported for checkbox selection mode to work');
            }

            const col = me.checkboxColumn = new checkColumnClass({
                id       : `${me.id}-selection-column`,
                width    : '4em',
                minWidth : '4em', // Misaligned on IE11 without, since 4em is below columns default minWidth
                field    : null,
                cellCls  : 'b-checkbox-selection',
                // Always put the checkcolumn in the first region
                region   : Object.keys(me.subGridConfigs).sort()[0]
            }, me.columns);

            col.meta.depth = 0;
            // This is assigned in Column.js for normal columns
            col._grid      = me;

            // Override renderer to inject the rendered record's selected status into the value
            const checkboxRenderer = col.renderer;
            col.renderer           = renderData => {
                renderData.value = me.isSelected(renderData.record);
                checkboxRenderer.call(col, renderData);
            };

            col.on({
                toggle  : 'onCheckChange',
                thisObj : me
            });

            // Insert the checkbox after any rownumber column. If not there, -1 means in at 0.
            const insertIndex = me.columns.indexOf(me.columns.findRecord('type', 'rownumber')) + 1;

            me.columns.insert(insertIndex, col);
        }
        super.afterConfigure();

        me.store.on('idChange', me.onRecordIdChange, me);
    }

    //endregion

    //region Events

    onRecordIdChange({ record, oldValue, value }) {
        const { recordCollection } = this,
            item = recordCollection.get(oldValue);

        // having the record registered by the oldValue means we need to rebuild indices
        if (item === record) {
            recordCollection.rebuildIndices();
        }
    }

    /**
     * The selection has been changed.
     * @event selectionchange
     * @param {String} action `'select'`/`'deselect'`
     * @param {String} mode `'row'`/`'cell'`
     * @param {Grid.view.Grid} source
     * @param {Core.data.Model[]|Object} deselected The records or cells (depending on the `mode`) deselected in this operation.
     * @param {Core.data.Model[]|Object} selected The records or cells (depending on the `mode`) selected in this operation.
     * @param {Core.data.Model[]|Object} selection  The records or cells (depending on the `mode`) in the new selection.
     */

    /**
     * Responds to mutations of the underlying storage Collection
     * @param {Object} event
     * @private
     */
    onRecordCollectionChange({ source : recordCollection, action, added = [], removed }) {
        const me = this;

        if (me._selectedCell && !me.isSelectable(me._selectedCell)) {
            me.deselectCell(me._selectedCell);
        }

        // Filter out unselectable rows
        added = added.filter(row => me.isSelectable(row));

        me.triggerChangeEvent({
            mode       : 'row',
            action     : added.length ? 'select' : 'deselect',
            selection  : me.selectedRecords,
            selected   : added,
            deselected : removed
        }, me.silent);
    }

    onCheckChange({ source : column, checked, record }) {
        if (checked) {
            this.selectRow({
                record,
                column,
                addToSelection : true,
                scrollIntoView : false
            });
        }
        else {
            this.deselectRow(record);
        }
    }

    onElementKeyDown(event) {
        const me = this,
            { focusedCell, checkboxColumn } = me;

        super.onElementKeyDown(event);

        // SPACE key on our CheckColumn toggles it
        if (focusedCell && event.key === ' ' && checkboxColumn && me.columns.getById(focusedCell.columnId) === checkboxColumn) {
            const checkbox = me.getCell(focusedCell).widget;

            if (checkbox) {
                checkbox.toggle();
            }
        }
    }

    //endregion

    //region Selection collection

    set recordCollection(recordCollection) {
        if (!(recordCollection instanceof Collection)) {
            recordCollection = new Collection(recordCollection);
        }
        this._recordCollection = recordCollection;

        // Fire row change events from onRecordCollectionChange
        recordCollection.on({
            change  : 'onRecordCollectionChange',
            thisObj : this
        });
    }

    get recordCollection() {
        return this._recordCollection;
    }

    /**
     * Removes and adds records to/from the selection at the same time. Analogous
     * to the `Array` `splice` method.
     *
     * Note that if items that are specified for removal are also in the `toAdd` array,
     * then those items are *not* removed then appended. They remain in the same position
     * relative to all remaining items.
     *
     * @param {Number} index Index at which to remove a block of items. Only valid if the
     * second, `toRemove` argument is a number.
     * @param {Object[]|Number} toRemove Either the number of items to remove starting
     * at the passed `index`, or an array of items to remove (If an array is passed, the `index` is ignored).
     * @param  {Object[]|Object} toAdd An item, or an array of items to add.
     */
    spliceSelectedRecords(index, toRemove, toAdd) {
        this._recordCollection.splice(index, toRemove, toAdd);
    }

    //endregion

    //region Cell & row

    /**
     * Checks whether or not a cell or row is selected.
     * @param {Object|string|Number|Core.data.Model} cellSelectorOrId Cell selector { id: x, column: xx } or row id, or record
     * @returns {Boolean} true if cell or row is selected, otherwise false
     * @category Selection
     */
    isSelected(cellSelectorOrId) {
        const me = this;

        // A record passed
        if (cellSelectorOrId instanceof Model) {
            cellSelectorOrId = cellSelectorOrId.id;
        }

        if (validIdTypes[typeof cellSelectorOrId]) {
            return me.recordCollection.includes(cellSelectorOrId);
        }
        else {
            return me._selectedCell && me._selectedCell.id == cellSelectorOrId.id &&
                me._selectedCell.columnId === cellSelectorOrId.columnId;
        }
    }

    /**
     * Checks whether or not a cell or row can be selected.
     * @param recordCellOrId Record or cell or record id
     * @returns {Boolean} true if cell or row cane be selected, otherwise false
     * @category Selection
     */
    isSelectable(recordCellOrId) {
        // Selection disabled for undefined record, special row (group header / footer) and
        // record which was already removed from store

        if (!recordCellOrId) {
            return false;
        }
        const row = this.store.getById(recordCellOrId.id || recordCellOrId);
        return row && !(row.meta && row.meta.specialRow);
    }

    /**
     * Cell selector for selected cell, set to select a cell or use {@link #function-selectCell()}.
     * @property {Object}
     * @category Selection
     */
    get selectedCell() {
        return this._selectedCell;
    }

    set selectedCell(cellSelector) {
        this.selectCell(cellSelector);
    }

    set selectionMode(mode) {
        if (mode && mode.rowCheckboxSelection) {
            mode.row = mode.checkbox = true;
            mode.cell = false;
        }

        this._selectionMode = mode;
    }

    get selectionMode() {
        return this._selectionMode;
    }

    /**
     * The last selected record. Set to select a row or use Grid#selectRow. Set to null to
     * deselect all
     * @property {Core.data.Model}
     * @category Selection
     */
    get selectedRecord() {
        return this.recordCollection.last || null;
    }

    set selectedRecord(record) {
        this.selectRow({ record });
    }

    /**
     * Selected records.
     * Can be set as array of ids.
     * ```
     * grid.selectedRecords = [1, 2, 4, 6]
     * ```
     *
     * @property {Core.data.Model[]|Number[]}
     * @category Selection
     */
    get selectedRecords() {
        return this.recordCollection.values;
    }

    set selectedRecords(selectedRecords) {
        const { recordCollection, store } = this,
            toSelect                    = [];

        if (selectedRecords && selectedRecords.length) {
            for (let record of selectedRecords) {
                record = store.getById(record);
                if (record) {
                    toSelect.push(record);
                }
            }
        }

        // Replace the entire selected collection with the new record set
        recordCollection.splice(0, recordCollection.count, toSelect);
    }

    /**
     * CSS selector for the currently selected cell. Format is "[data-index=index] [data-column-id=column]".
     * @type {String}
     * @category Selection
     * @readonly
     */
    get selectedCellCSSSelector() {
        const me   = this,
            cell = me._selectedCell,
            row  = cell && me.getRowById(cell.id);

        if (!cell || !row) return '';

        return `[data-index=${row.dataIndex}] [data-column-id=${cell.columnId}]`;
    }

    /**
     * Selects a row (without selecting a cell).
     * @param {Object} options
     * @param (Core.data.Model|String) options.record Record or record id, specifying null will deselect all
     * @param {Column} [Grid.column.Column|String] The column to scroll into view if `scrollIntoView` is not specified as `false`. Defaults to the grid's first column.
     * @param {Boolean} [options.scrollIntoView] Specify `false` to prevent row from being scrolled into view
     * @param {Boolean} [options.addToSelection] Specify `true` to add to selection, defaults to `false` which replaces
     * @fires selectionchange
     * @category Selection
     */
    selectRow({
        record,
        column = this.columns.bottomColumns[0],
        scrollIntoView = true,
        addToSelection = false
    }) {
        // TODO: Remove this backward compatibility code in 3.0
        if (typeof arguments[0] !== 'object') {
            const args = arguments;

            record = args[0];
            scrollIntoView = args.length > 1 ? args[1] : true;
            addToSelection = args[2] || false;
        }
        const me = this,
            targetRecord = me.store.getById(record);

        if (record) {
            me.selectCell({ id : targetRecord.id, column }, scrollIntoView, addToSelection);
        }
        else {
            me.deselectAll();
        }
    }

    /**
     * Selects a cell and/or its row (depending on selectionMode)
     * @param {Object} cellSelector { id: rowId, columnId: 'columnId' }
     * @param {Boolean} scrollIntoView Specify false to prevent row from being scrolled into view
     * @param {Boolean} addToSelection Specify `true` to add to selection, defaults to `false` which replaces
     * @param {Boolean} silent Specify `true` to not trigger any events when selecting the cell
     * @returns {Object} Cell selector
     * @fires selectionchange
     * @category Selection
     */
    selectCell(cellSelector, scrollIntoView = false, addToSelection = false, silent = false) {
        const
            me                                  = this,
            { recordCollection, selectionMode } = me,
            selector                            = me.normalizeCellContext(cellSelector),
            record                              = selector.record || me.store.getById(selector.id);

        // Clear selection if row is not selectable
        if (!me.isSelectable(record)) {
            this.deselectAll();
            return;
        }

        if (scrollIntoView) {
            me.scrollRowIntoView(selector.id, {
                column : selector.columnId
            });
        }

        // Row selection (both sides if locked columns)
        if (selectionMode.row) {
            if (silent) {
                me.silent = (me.silent || 0) + 1;
            }
            if (addToSelection) {
                recordCollection.add(record);
            }
            // Clear all others
            else {
                recordCollection.splice(0, recordCollection.count, record);
            }
            if (silent) {
                me.silent--;
            }

            // When starting a selection, register the start cell
            if (me.recordCollection.count === 1) {
                me.startCell = selector;
                me.lastRange = null;
            }
        }

        // Cell selection
        if (selectionMode.cell && (selector.columnId || selector.column) && !me.isSelected(selector)) {
            let deselected = (me._selectedCell) ? [me._selectedCell] : [];

            //Remember
            me._selectedCell = selector;

            me.triggerChangeEvent({
                mode      : 'cell',
                action    : 'select',
                selected  : [selector],
                deselected,
                selection : [selector]
            }, silent);
        }

        return selector;
    }

    /**
     * Deselects all selected rows and cells
     * @category Selection
     */
    deselectAll() {
        const me = this;
        me.recordCollection.clear();
        if (me._selectedCell) {
            me.deselectCell(me._selectedCell);
        }
    }

    /**
     * Deselect a row
     * @param {Core.data.Model|String|Number} recordOrId Record or an id for a record
     * @category Selection
     */
    deselectRow(recordOrId) {
        const record = recordOrId instanceof Model ? recordOrId : this.store.getById(recordOrId);
        record && this.recordCollection.remove(record);
    }

    /**
     * Deselect a cell/row, depending on settings in Grid#selectionMode
     * @param {Object} cellSelector
     * @returns {Object} Normalized cell selector
     * @category Selection
     */
    deselectCell(cellSelector) {
        const me           = this,
            selector     = me.normalizeCellContext(cellSelector),
            selMode      = me.selectionMode,
            record       = selector.record || me.store.getById(selector.id),
            selectedCell = me._selectedCell;

        // Row selection (both sides if locked columns)
        if (selMode.row) {
            me.recordCollection.remove(record);
        }

        // Cell selection
        if (selMode.cell && selector.columnId && selectedCell) {
            if (selectedCell.id === selector.id && selectedCell.columnId === selector.columnId) {
                me._selectedCell = null;

                me.triggerChangeEvent({
                    mode       : 'cell',
                    action     : 'deselect',
                    selected   : [],
                    deselected : [selector],
                    selection  : []
                });
            }
        }

        return selector;
    }

    //endregion

    //region Record

    /**
     * Selects rows corresponding to a range of records (from fromId to toId)
     * @param {String|Number} fromId
     * @param {String|Number} toId
     * @category Selection
     */
    selectRange(fromId, toId) {
        const
            { store, recordCollection } = this,
            fromIndex                   = store.indexOf(fromId),
            toIndex                     = store.indexOf(toId),
            startIndex                  = Math.min(fromIndex, toIndex),
            endIndex                    = Math.max(fromIndex, toIndex);

        if (startIndex === -1 || endIndex === -1) {
            throw new Error('Record not found in selectRange');
        }

        recordCollection.splice(0, recordCollection.count, store.getRange(startIndex, endIndex + 1, false));
    }

    /**
     * Triggered from Grid view when records get removed from the store.
     * Deselects all records which have been removed.
     * @private
     * @category Selection
     */
    onStoreRemove(event) {
        // If the next mixin up the inheritance chain has an implementation, call it
        super.onStoreRemove && super.onStoreRemove(event);

        this.recordCollection.remove(event.records);
    }

    /**
     * Triggered from Grid view when the store changes. This might happen
     * if store events are batched and then resumed.
     * Deselects all records which have been removed.
     * @private
     * @category Selection
     */
    onStoreDataChange({ action, source : store }) {
        // If the next mixin up the inheritance chain has an implementation, call it
        super.onStoreDataChange && super.onStoreDataChange(...arguments);

        if (isSelectAction[action]) {
            const selectedRecords = this.recordCollection,
                toRemove = [];

            selectedRecords.forEach(record => {
                if (!store.includes(record)) {
                    toRemove.push(record);
                }
            });

            // Remove in one go to fire a single selectionChange event
            selectedRecords.remove(toRemove);
        }
    }

    /**
     * Triggered from Grid view when all records get removed from the store.
     * Deselects all records.
     * @private
     * @category Selection
     */
    onStoreRemoveAll() {
        // If the next mixin up the inheritance chain has an implementation, call it
        super.onStoreRemoveAll && super.onStoreRemoveAll();

        this.deselectAll();
    }

    //endregion

    //region Handle multiSelect

    /**
     * Handles multi selection using the mouse. Called from GridElementEvents on mousedown in a cell and
     * simultaneously pressing a modifier key.
     * @param cellData
     * @param event
     * @private
     * @category Selection
     */
    handleMouseMultiSelect(cellData, event) {
        const me = this,
            id = cellData.id;

        function mergeRange(fromId, toId) {
            const
                { store, recordCollection } = me,
                fromIndex                   = store.indexOf(fromId),
                toIndex                     = store.indexOf(toId),
                startIndex                  = Math.min(fromIndex, toIndex),
                endIndex                    = Math.max(fromIndex, toIndex);

            if (startIndex === -1 || endIndex === -1) {
                throw new Error('Record not found in selectRange');
            }

            const newRange = store.getRange(startIndex, endIndex + 1, false).filter(row => me.isSelectable(row));
            recordCollection.splice(0, me.lastRange || 0, newRange);
            me.lastRange = newRange;
        }

        if ((event.metaKey || event.ctrlKey) && me.isSelected(id)) {
            // ctrl/cmd deselects row if selected
            me.deselectRow(id);
        }
        else if (me.selectionMode.multiSelect) {
            if (event.shiftKey && me.startCell) {
                // shift appends selected range (if we have previously focused cell)
                mergeRange(me.startCell.id, id);
            }
            else if (event.ctrlKey || event.metaKey) {
                // ctrl/cmd adds to selection if using multiselect (and not selected)
                me.selectRow({
                    record         : id,
                    scrollIntoView : false,
                    addToSelection : true
                });
            }
        }
    }

    //endregion

    //region Navigation

    /**
     * Triggered from GridNavigation when focus is moved to another cell within the grid. Selects the cell unless
     * modifier keys are pressed, in which case it has already been handled
     * @private
     * @category Selection
     */
    onCellNavigate(me, fromCellSelector, toCellSelector, event, doSelect = true) {
        // CheckColumn events are handled by the CheckColumn itself.
        if (me.columns.getById(toCellSelector.columnId) === me.checkboxColumn || me.selectionMode.rowCheckboxSelection) {
            return;
        }

        // 1.do not affect selection if focus is returning to the grid from some widget
        // 2. don't select when clicking expander icon in a tree
        if (!doSelect || me.returningFocus || (event && event.target.classList.contains('b-tree-expander'))) {
            return;
        }

        const
            isSameRecord = fromCellSelector && toCellSelector.id === fromCellSelector.id,
            isMouse = event && event.type === 'mousedown',
            isMouseCtrl  = isMouse && event.ctrlKey;

        // SHIFT for keyboard / mouse and CTRL for mouse events indicate multiselect
        if (event && (!event.button || event.button === 2) && (event.shiftKey || isMouseCtrl)) {
            me.handleMouseMultiSelect(toCellSelector, event);
        }
        else {
            me.selectCell(toCellSelector, false, (isSameRecord && (!event || event.shiftKey || event.ctrlKey)) ||
                (event && (isMouseCtrl || (event.button === 2 && me.isSelected(toCellSelector.id)))));
        }

        // Remember last cell with ctrl pressed
        if (!me.startCell || isMouseCtrl) {
            me.startCell = toCellSelector;
            me.lastRange = null;
        }

    }

    /**
     * Keeps the UI synced with the selectionchange event before firing it out.
     * Event is not fired if the `silent` parameter is truthy.
     * @param {Object} selectionChangeEvent The change event to sync the UI to, and to possibly fire.
     * @param {Boolean} silent Specify `true` to not trigger any the passed.
     * @private
     * @category Selection
     */
    triggerChangeEvent(selectionChangeEvent, silent) {
        const me = this,
            {
                mode,
                selected,
                deselected
            }  = selectionChangeEvent;

        let i, len, row, cell;

        // Keep the UI up to date with the triggered changes.
        // A mode: 'row' change selects and/or deselects records.
        if (mode === 'row') {
            for (i = 0, len = selected.length; i < len; i++) {
                row = me.getRowFor(selected[i]);
                if (row) {
                    row.addCls('b-selected');
                    if (me.checkboxColumn && !selected[i].meta.specialRow) {
                        row.getCell(me.checkboxColumn.id).widget.checked = true;
                    }
                }
            }
            for (i = 0, len = deselected.length; i < len; i++) {
                row = me.getRowFor(deselected[i]);
                if (row) {
                    row.removeCls('b-selected');
                    if (me.checkboxColumn && !deselected[i].meta.specialRow) {
                        row.getCell(me.checkboxColumn.id).widget.checked = false;
                    }
                }
            }
        }
        // A mode: 'cell' change selects and/or deselects *one* cell right now.
        // But we always use an array for future-proofing.
        else if (mode === 'cell') {
            for (i = 0, len = selected.length; i < len; i++) {
                cell = me.getCell(selected[i]);
                if (cell) {
                    cell.classList.add('b-selected');
                }
            }
            for (i = 0, len = deselected.length; i < len; i++) {
                cell = me.getCell(deselected[i]);
                if (cell) {
                    cell.classList.remove('b-selected');
                }
            }
        }

        if (!silent) {
            me.trigger('selectionChange', selectionChangeEvent);
        }
    }

    //endregion

    //region Getters/setters

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}

    //endregion

};
