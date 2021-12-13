import Base from '../../../Core/Base.js';
import DomHelper from '../../../Core/helper/DomHelper.js';
import Rectangle from '../../../Core/helper/util/Rectangle.js';

/**
 * @module Grid/view/mixin/GridNavigation
 */

const
    defaultFocusOptions = Object.freeze({
        doSelect : true
    }),
    disableScrolling = Object.freeze({
        x : false,
        y : false
    });

/**
 * Mixin for Grid that handles cell to cell navigation.
 *
 * @mixin
 */
export default Target => class GridNavigation extends (Target || Base) {
    //region Cell

    /**
     * User navigated to a grid cell
     * @event navigate
     * @param {Grid.view.Grid} grid
     * @param {Object} last focused location
     * @param {Object} location focus moved to
     * @param {Event} [event] The UI event which caused navigation.
     */

    /**
     * Cell selector for focused cell, set to focus a cell or use {@link #function-focusCell}.
     * @property {Object}
     */
    get focusedCell() {
        const result = this._focusedCell;

        if (result && this.getCell(result)) {
            return result;
        }
        this.clearFocus();
    }

    /**
     * This property is `true` if an element _within_ a cell is focused.
     * @property {Boolean}
     * @readonly
     */
    get isActionableLocation() {
        const focusedCell = this._focusedCell;
        return Boolean(focusedCell && focusedCell.element && this.getCell(focusedCell).contains(focusedCell.element));
    }

    set focusedCell(cellSelector) {
        this.focusCell(cellSelector, {
            doSelect : false
        });
    }

    get focusedRecord() {
        if (this._focusedCell) {
            return this.store.getById(this._focusedCell.id);
        }
    }

    /**
     * CSS selector for currently focused cell. Format is "[data-index=index] [data-column-id=columnId]".
     * @property {String}
     * @readonly
     */
    get cellCSSSelector() {
        const me   = this,
            cell = me._focusedCell,
            row  = cell && me.getRowById(cell.id);

        if (!cell || !row) return '';

        return `[data-index=${row.index}] [data-column-id=${cell.columnId}]`;
    }

    /**
     * Grid element focus, selects first row and cell when grid is focused if none is selected (otherwise user cannot
     * see that grid is focused).
     * Chain this function it features to handle the dom event.
     * @param event
     * @category Other events
     * @internal
     */
    onGridElementFocus(event) {
        // Select first row if none is selected when grid is focused (to show that it has focus)
        const me = this,
            focusOptions = {
                doSelect : false,
                event
            };

        if (me._focusedCell) {
            me.focusCell(me._focusedCell, focusOptions);
        }
        else {
            // If they just clicked in blank space, do not refocus the last focused cell.
            // We focus cell 0, 0 on that gesture.
            const targetContext =  me.element.contains(event.relatedTarget) ? null : me.lastFocusedCell;

            if (targetContext) {
                const cell = me.getCell(targetContext);

                // If we're using lastFocusedCell and the cell is no longer in existence, or not visible
                // or the previously focused record has since been removed.
                // Fallback to reverting to where focus entered the grid from.
                // TODO: CellContext should store the record index
                // to fall back to if the actual record is no longer present.
                if (!cell || !DomHelper.isVisible(cell) || !me.store.getById(targetContext.id)) {
                    me.revertFocus();
                    return;
                }

                // flag to not affect selection
                me.returningFocus = true;
                me.focusCell(targetContext, focusOptions);
                me.returningFocus = false;
            }
            else if (!me.skipFocusSelection && me.store.first) {
                me.focusCell({
                    id       : me.store.first.id,
                    columnId : me.columns.visibleColumns[0].id
                }, focusOptions);
            }
        }
        me.skipFocusSelection = false;
    }

    onFocusOut(event) {
        super.onFocusOut(event);

        this.clearFocus();
    }

    /**
     * Checks whether or not a cell is focused.
     * @param {Object|string|Number} cellSelector Cell selector { id: x, columnId: xx } or row id
     * @returns {Boolean} true if cell or row is focused, otherwise false
     */
    isFocused(cellSelector) {
        return Boolean(this._focusedCell) && this.isLocationEqual(cellSelector, this._focusedCell);
    }

    /**
     * Navigates to a cell and/or its row (depending on selectionMode)
     * @param {Object} cellSelector { id: rowId, columnId: 'columnId' }
     * @param {Object} options Modifier options for how to deal with focusing the cell. These
     * are used as the {@link Core.helper.util.Scroller#function-scrollTo} options.
     * @param {Boolean} [options.doSelect=true] Optionally pass `false` to not continue to selection.
     * @param {Event} [options.event] Optionally pass the UI event which instigated the focus request.
     * @param {Object|boolean} [options.scroll=true] Pass `false` to not scroll the cell into view, or a
     * scroll options object to affact the scroll.
     * @returns {Object} Cell selector
     * @fires navigate
     */
    focusCell(cellSelector, options = defaultFocusOptions) {
        // If we're being passed a context object (as opposed to a record), clone in case we were
        // passed the _focusedCell, and after refresh isLocationEqual needs to detect a difference.
        cellSelector = this.normalizeCellContext(cellSelector === Object ? Object.assign({}, cellSelector) : cellSelector);

        const
            me              = this,
            doSelect        = options.doSelect !== false,
            {
                event,
                scroll
            }              = options,
            lastFocusedCell = me.lastFocusedCell = me._focusedCell,
            isNotMove       = me.isLocationEqual(cellSelector, lastFocusedCell),
            lastCell        = lastFocusedCell && me.getCell(lastFocusedCell),
            subGrid         = me.getSubGridFromColumn(cellSelector.columnId);

        let cell = me.getCell(cellSelector);

        // If we're focusing due to a mousedown on a focusable element *within* a cell
        // then we do not "navigate" to the cell - we allow the element to be focused.
        // For example a CheckColumn or WidgetColumn.
        if (event && event.type === 'mousedown') {
            for (let target = event.target; target !== cell; target = target.parentElement) {
                if (DomHelper.isFocusable(target)) {
                    cellSelector.element = target;
                    return;
                }
            }
        }

        // No navigation takes place, but the selection still needs to know if a UI event happened.
        if (isNotMove) {
            if (event) {
                me.onCellNavigate && me.onCellNavigate(me, null, me._focusedCell, event, doSelect);
            }
            return cellSelector;
        }

        if (lastCell) {
            lastCell.classList.remove('b-focused');
        }

        const
            testCell = cell || me.getCell({
                row      : me.rowManager.topIndex,
                columnId : cellSelector.columnId
            }),
            subGridRect = Rectangle.from(subGrid.element),
            cellRect = Rectangle.from(testCell).moveTo(null, subGridRect.y);

        if (scroll === false) {
            options = Object.assign({}, options, disableScrolling);
        }
        else {
            options = Object.assign({}, options, scroll);

            // If the test cell is larger than the subGrid, in any dumension, disable scrolling
            if (cellRect.width > subGridRect.width || cellRect.height > subGridRect.height) {
                options.x = options.y = false;
            }
            // Else ask for the column to be scrolled into view
            else {
                options.column = cellSelector.columnId;
            }

            me.scrollRowIntoView(cellSelector.id, options);
        }

        // Get the newly visible cell *after* it has been scrolled into view.
        // With buffered rendering, it may not have existed before the scroll.
        cell = me.getCell(cellSelector);
        if (cell) {
            cellSelector.element = cell;
            cell.classList.add('b-focused');
        }

        //Remember
        me._focusedCell = cellSelector;

        me.onCellNavigate && me.onCellNavigate(me, lastFocusedCell, me._focusedCell, event, doSelect);

        me.trigger('navigate', { lastFocusedCell, focusedCell : me._focusedCell, event });
        //TODO: should be able to cancel selectcell from listeners

        return cellSelector;
    }

    isLocationEqual(cellSelector, otherCellSelector) {
        return (
            cellSelector && otherCellSelector &&
            cellSelector.id === otherCellSelector.id &&
            cellSelector.columnId === otherCellSelector.columnId &&
            // Normalized cellSelectors are not required to have an element, only compare elements if both selectors
            // have it specified
            (
                !cellSelector.element ||
                !otherCellSelector.element ||
                cellSelector.element === otherCellSelector.element
            )
        );
    }

    blurCell(cellSelector) {
        const me   = this,
            cell = me.getCell(cellSelector);

        if (cell) {
            cell.classList.remove('b-focused');
        }
    }

    clearFocus() {
        const me = this;

        if (me._focusedCell) {
            // set last to have focus return to previous cell when alt tabbing
            me.lastFocusedCell = me._focusedCell;

            me.blurCell(me._focusedCell);
            me._focusedCell = null;
        }
    }

    /**
     * Selects the cell before or after currently focused cell.
     * @private
     * @param next Specify true to select the next cell, false to select the previous
     * @param {Event} [event] Optionally, the UI event which caused navigation.
     * @returns {Object} Used cell selector
     */
    internalNextPrevCell(next = true, event) {
        let me           = this,
            cellSelector = me._focusedCell;

        if (cellSelector) {
            return me.focusCell({
                id       : cellSelector.id,
                columnId : me.columns.getAdjacentVisibleLeafColumn(cellSelector.columnId, next, true).id
            }, {
                doSelect : true,
                event
            });
        }
        return null;
    }

    /**
     * Select the cell after the currently focused one.
     * @param {Event} [event] Optionally, the UI event which caused navigation.
     * @returns {Object} Cell selector
     */
    navigateRight(event) {
        return this.internalNextPrevCell(true, event);
    }

    /**
     * Select the cell before the currently focused one.
     * @param {Event} [event] Optionally, the UI event which caused navigation.
     * @returns {Object} Cell selector
     */
    navigateLeft(event) {
        return this.internalNextPrevCell(false, event);
    }

    //endregion

    //region Row

    /**
     * Selects the next or previous record in relation to the current selection. Scrolls into view if outside.
     * @private
     * @param next Next record (true) or previous (false)
     * @param {Boolean} skipSpecialRows True to not return specialRows like headers
     * @param {Event} [event] Optionally, the UI event which caused navigation.
     * @returns {Object/Boolean} Selection context for the focused row (& cell) or false if no selection was made
     */
    internalNextPrevRow(next, skipSpecialRows = true, event) {
        const me = this,
            cell = me._focusedCell;

        if (!cell) return false;

        const record = me.store[`get${next ? 'Next' : 'Prev'}`](cell.id, false, skipSpecialRows);

        if (!record) return false;

        return me.focusCell({
            id       : record.id,
            columnId : cell.columnId,
            scroll   : {
                x : false
            }
        }, {
            doSelect : true,
            event
        });
    }

    /**
     * Navigates to the cell below the currently focused cell
     * @param {Event} [event] Optionally, the UI event which caused navigation.
     * @returns {Object} Selector for focused row (& cell)
     */
    navigateDown(event) {
        return this.internalNextPrevRow(true, false, event);
    }

    /**
     * Navigates to the cell above the currently focused cell
     * @param {Event} [event] Optionally, the UI event which caused navigation.
     * @returns {Object} Selector for focused row (& cell)
     */
    navigateUp(event) {
        return this.internalNextPrevRow(false, false, event);
    }

    //endregion

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}
};
