//TODO: Handle vertical resize, add/remove row elements?

import Rectangle from '../../Core/helper/util/Rectangle.js';
import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import Row from './Row.js';
import ObjectHelper from '../../Core/helper/ObjectHelper.js';
import { roundPx } from '../../Core/helper/DomHelper.js';

/**
 * @module Grid/row/RowManager
 * @private
 */

/**
 * Virtual representation of the grid, using {@link Grid.row.Row} to represent rows. Plugs into {@link Grid.view.Grid}
 * and exposes the following functions on grid itself:
 * * {@link #function-getRecordCoords()}
 * * {@link #function-getRowById()}
 * * {@link #function-getRow()}
 * * {@link #function-getRowFor()}
 * * {@link #function-getRowFromElement()}
 *
 * @example
 * let row = grid.getRowById(1);
 * @private
 */
export default class RowManager extends InstancePlugin {
    //region Config

    // Plugin configuration.
    static get pluginConfig() {
        return {
            chain : [
                'getRowById', 'topRow', 'bottomRow', 'getRecordCoords', 'getRow', 'getRowFor', 'getRowFromElement', 'destroy'
            ],
            assign : [
                'rowHeight'
            ]
        };
    }

    static get defaultConfig() {
        return {
            /**
             * Number of rows to render above current viewport
             * @config {Number}
             * @default
             */
            prependRowBuffer : 5,

            /**
             * Number of rows to render below current viewport
             * @config {Number}
             * @default
             */
            appendRowBuffer : 5,

            /**
             * Default row height, assigned from Grid at construction (either from config
             * {@link Grid.view.Grid#config-rowHeight} or CSS). Can be set from renderers
             * @config {Number}
             * @default
             */
            rowHeight : null,

            idMap         : {},
            rowScrollMode : 'move',
            autoHeight    : false,
            // TODO: investigate if topIndex can to built away, since topRow is always first in array and has dataIndex??
            topIndex      : 0,
            lastScrollTop : 0,
            _rows         : []
        };
    }

    //endregion

    //region Init

    construct(config) {
        config.grid._rowManager = this;
        this.scrollTargetRecordId = null;
        this.refreshDetails = {
            topRowIndex : 0,
            topRowTop   : 0
        };
        super.construct(config.grid, config);
    }

    // Chained to grids doDestroy
    doDestroy() {
        // To remove timeouts
        this._rows.forEach(row => row.destroy());

        super.doDestroy();
    }

    /**
     * Initializes the RowManager with Rows to fit specified height.
     * @param {Number} height
     * @private
     * @category Init
     */
    initWithHeight(height, isRendering = false) {
        const me = this;

        // no valid height, make room for all rows
        if (me.autoHeight) {
            height = me.store.allCount * me.rowOffsetHeight;
        }

        me.viewHeight = height;
        me.calculateRowCount(isRendering);

        return height;
    }

    /**
     * Releases all elements (not from dom), calculates how many are needed, creates those and renders
     */
    reinitialize(returnToTop = false) {
        const
            me  = this,
            top = me.topRow && !returnToTop ? me.topRow.top : 0;

        me.scrollTargetRecordId = null;

        if (returnToTop) {
            me.topIndex = me.lastScrollTop = 0;
        }

        // Calculate and correct the amount of rows needed (without triggering render)
        // Rows which are found to be surplus are destroyed.
        me.calculateRowCount(false, true, true);

        const { topRow } = me;

        if (topRow) {
            // Ensure rendering from the topRow starts at the correct position
            topRow.dataIndex = me.topIndex;
            topRow.setTop(top);
        }

        // Need to estimate height in case we have Grid using autoHeight
        me.estimateTotalHeight();

        me.renderFromRow(topRow);
    }

    //endregion

    //region Rows

    /**
     * Add or remove rows to fit row count
     * @private
     * @category Rows
     */
    matchRowCount(skipRender = false) {
        const me      = this,
            rows    = me._rows,
            numRows = rows.length,
            delta   = numRows - me.rowCount,
            grid    = me.grid;

        if (delta) {
            if (delta < 0) {
                const newRows = [];

                // add rows
                for (let index = numRows, dataIndex = numRows ? rows[numRows - 1].dataIndex + 1 : 0; index < me.rowCount; index++, dataIndex++) {
                    newRows.push(new Row({
                        rowManager : me,
                        grid,
                        index,
                        dataIndex
                    }));
                }
                rows.push.apply(rows, newRows);
                // and elements (by triggering event used by SubGrid to add elements)
                me.trigger('addRows', { rows : newRows });

                if (!skipRender) {
                    // render
                    me.renderFromRow(rows[Math.max(0, numRows - 1)]);
                }
            }
            else {
                // remove rows from bottom
                const removedRows = rows.splice(numRows - delta, delta);

                // trigger event in case some feature needs to cleanup when removing (widget column might be interested)
                me.trigger('removeRows', { rows : removedRows });

                removedRows.forEach(row => row.destroy());

                // no need to rerender or such when removing from bottom. all is good :)
            }
        }
    }

    /**
     * Calculates how many rows fit in the available height (view height)
     * @private
     * @category Rows
     */
    calculateRowCount(skipMatchRowCount = false, allowRowCountShrink = true, skipRender = false) {
        // TODO: replace prependRowBuffer, appendXX with bufferSize
        const me                = this,
            store             = me.store,
            visibleRowCount   = Math.ceil(me.viewHeight / me.rowOffsetHeight), // Want whole rows
            maxRenderRowCount = visibleRowCount + me.prependRowBuffer + me.appendRowBuffer;

        // If RowManager is reinitialized in a hidden state the view might not have a height
        if (!me.grid.columns.count || isNaN(visibleRowCount)) {
            me.rowCount = 0;
            return 0;
        }

        // when for example jumping we do not want to remove excess rows,
        // since we know they are needed at other scroll locations
        if (maxRenderRowCount < me.rowCount && !allowRowCountShrink) return me.rowCount;

        me.visibleRowCount = visibleRowCount;
        me.rowCount = Math.min(store.count, maxRenderRowCount); // No need for more rows than data

        if (me.rowScrollMode === 'all') {
            me.rowCount = store.allCount;
        }

        // If the row count doesn't match the calculated, ensure it matches,
        if (!skipMatchRowCount) {
            if (me._rows && me.rowCount !== me._rows.length) {
                me.matchRowCount(skipRender);
            }
            else if (!me.rowCount) {
                me.trigger('changeTotalHeight', { totalHeight : me.totalHeight });
            }
            me.grid.toggleEmptyText();
        }

        return me.rowCount;
    }

    removeAllRows() {
        // remove rows from bottom
        const
            me         = this,
            { topRow } = me,
            result     = topRow ? (me.refreshDetails = {
                topRowIndex : topRow.dataIndex,
                topRowTop   : topRow.top
            }) : me.refreshDetails,
            removedRows = me._rows.slice();

        // trigger event in case some feature needs to cleanup when removing (widget column might be interested)
        me.trigger('removeRows', { rows : removedRows });

        me._rows.forEach(row => row.destroy());
        me._rows.length = 0;
        me.idMap = {};

        // We return a descriptor of the last rendered block before the remove.
        // This is primarily for a full GridBase#renderContents to be able to perform a correct refresh.
        return result;
    }

    setPosition(refreshDetails) {
        // Sets up the rendering position for the next call to reinitialize
        const
            { topRow }                 = this,
            { topRowIndex, topRowTop } = refreshDetails;

        topRow.setTop(topRowTop);
        topRow.dataIndex = topRowIndex;
    }

    //endregion

    //region Rows - Getters

    get store() {
        return this.client.store;
    }

    /**
     * Get all Rows
     * @property {Grid.row.Row[]}
     * @readonly
     * @category Rows
     */
    get rows() {
        return this._rows;
    }

    /**
     * Get the Row at specified index. Returns `undefined` if the row index is not rendered.
     * @param {Number} index
     * @returns {Grid.row.Row}
     * @category Rows
     */
    getRow(index) {
        return this.rowCount && this.rows[index - this.topIndex];
    }

    /**
     * Get Row for specified record id
     * @param {Core.data.Model|String|Number} recordOrId Record id (or a record)
     * @returns {Grid.row.Row} Found Row or null if record not rendered
     * @category Rows
     */
    getRowById(recordOrId) {
        if (!(typeof recordOrId === 'string' || typeof recordOrId === 'number')) {
            recordOrId = recordOrId.id;
        }
        // Don't use ===, want to match 1 == '1' since id is stored on rowElement in dataset (html attribute, always string)
        return this.idMap[recordOrId];// || this.rows.find(row => row.id == recordOrId);
    }

    /**
     * Get a Row from an HTMLElement
     * @param {HTMLElement} element
     * @returns {Grid.row.Row} Found Row or null if record not rendered
     * @category Rows
     */
    getRowFromElement(element) {
        element = element.closest('.b-grid-row');
        return element && this.getRow(element.dataset.index);
    }

    /**
     * Get the row at the specified Y coordinate, which is by default viewport-based.
     * @param {Number} y The `Y` coordinate to find the Row for.
     * @param {Boolean} [local=false] Pass `true` if the `Y` coordinate is local to the SubGrid's element.
     * @returns {Grid.row.Row} Found Row or null if no row is rendered at that point.
     */
    getRowAt(y, local = false) {
        // Make it local.
        if (!local) {
            y -= Rectangle.from(this.grid.bodyContainer, null, true).top;

            // Adjust for scrolling
            y += this.grid.scrollable.y;
        }
        y = roundPx(y);

        return this.rows.find(r => y >= r.top && y < r.bottom);
    }

    /**
     * Get a Row for either a record, a record id or an HTMLElement
     * @param {HTMLElement|Core.data.Model|String|Number} recordOrId Record or record id or HTMLElement
     * @returns {Grid.row.Row} Found Row or null if record not rendered
     * @category Rows
     */
    getRowFor(recordOrId) {
        if (recordOrId instanceof HTMLElement) return this.getRowFromElement(recordOrId);
        return this.getRowById(recordOrId);
    }

    /**
     * Gets the Row following the specified Row (by index or object). Wraps around the end.
     * @param {Number|Grid.row.Row} indexOrRow index or Row
     * @returns {Grid.row.Row}
     * @category Rows
     */
    getNextRow(indexOrRow) {
        const me    = this,
            index = typeof indexOrRow === 'number' ? indexOrRow : indexOrRow.index;
        return me.getRow((index + 1) % me.rowCount);
    }

    /**
     * Get the Row that is currently displayed at top.
     * @property {Grid.row.Row}
     * @readonly
     * @category Rows
     */
    get topRow() {
        return this._rows[0];
    }

    /**
     * Get the Row currently displayed furthest down.
     * @property {Grid.row.Row}
     * @readonly
     * @category Rows
     */
    get bottomRow() {
        const me       = this,
            // TODO: remove when ticket on making sure rowCount is always up to date is fixed
            rowCount = Math.min(me.rowCount, me.store.count);

        return me.rows[rowCount - 1];
    }

    /**
     * Calls offset() for each Row passing along offset parameter
     * @param {Number} offset Pixels to translate Row elements.
     * @private
     * @category Rows
     */
    offsetRows(offset) {
        if (offset !== 0) {
            const rows   = this.rows,
                length = rows.length;

            for (let i = 0; i < length; i++) {
                rows[i].offset(offset);
            }
        }
        
        this.trigger('offsetRows', { offset });
    }

    //endregion

    //region Row height

    // TODO: should support setting rowHeight in em and then convert internally to pixels. 1em = font-size. Not needed for 1.0
    /**
     * Set a fixed row height (can still be overridden by renderers) or get configured row height. Setting refreshes all rows
     * @category Rows
     */
    get rowHeight() {
        return this._rowHeight;
    }

    set rowHeight(height) {
        const
            me = this,
            { grid } = me,
            oldRowHeight = me._rowHeight;

        ObjectHelper.assertNumber(height, 'rowHeight');

        me.trigger('beforeRowHeight', { height });

        me._rowHeight = height;

        me.prependBufferHeight = me.prependRowBuffer * me.rowOffsetHeight;
        me.appendBufferHeight = me.appendRowBuffer * me.rowOffsetHeight;

        if (me.rows.length) {
            let average = me.averageRowHeight;
            const
                oldAverage = average,
                oldY       = grid.scrollable.y,
                topRow     = me.getRowAt(oldY, true),
                edgeOffset = topRow.top - oldY;

            // Estimate a new averageRowHeight
            average -= grid._rowBorderHeight;
            average *= height / oldRowHeight;
            me.averageRowHeight = average += grid._rowBorderHeight;

            // Adjust number of rows, since it is only allowed to shrink in refresh()
            me.calculateRowCount(false, true, true);

            // Reposition the top row since it is used to position the rest
            me.topRow.setTop(me.topRow.dataIndex * average);

            me.refresh();

            const newY = oldY * (average / oldAverage);

            // Scroll top row to the same position.
            if (newY !== oldY) {
                grid.scrollRowIntoView(topRow.id, {
                    block : 'start',
                    edgeOffset
                });
            }
        }

        me.trigger('rowHeight', { height });
    }

    /**
     * Get actually used row height, which includes any border and might be an average if using variable row height.
     * @property {Number}
     */
    get rowOffsetHeight() {
        return Math.floor(this.averageRowHeight) || (this._rowHeight + this.grid._rowBorderHeight);
    }

    //endregion

    //region Calculations

    /**
     * Returns top and bottom for rendered row or estimated coordinates for unrendered.
     * @param {Core.data.Model|string|Number} recordOrId Record or record id
     * @param {Boolean} [local] Pass true to get relative record coordinates
     * @returns {Core.helper.util.Rectangle} Record bounds with format { x, y, width, height, bottom, right }
     * @category Calculations
     */
    getRecordCoords(recordOrId, local = false) {
        const
            me  = this,
            id  = typeof recordOrId === 'string' || typeof recordOrId === 'number' ? recordOrId : recordOrId.id,
            row = me.getRowById(recordOrId);

        let scrollingViewport = me.client._bodyRectangle;

        // _bodyRectangle is not updated on page/containing element scroll etc. Need to make sure it is correct in case
        // that has happend. This if-statement should be removed when fixing
        // https://app.assembla.com/spaces/bryntum/tickets/6587-cached-_bodyrectangle-should-be-updated-on--quot-external-quot--scroll/details
        if (!local) {
            scrollingViewport = me.client._bodyRectangle = Rectangle.client(me.client.bodyContainer);
        }

        // Rendered? Then we know position for certain
        if (row) {
            return new Rectangle(
                scrollingViewport.x,
                local ? Math.round(row.top) : Math.round(row.top + scrollingViewport.y - me.client.scrollable.y),
                scrollingViewport.width,
                row.offsetHeight
            );
        }

        return me.getRecordCoordsByIndex(me.store.indexOf(id), local);
    }

    /**
     * Returns estimated top and bottom coordinates for specified row.
     * @param {Number} recordIndex Record index
     * @returns {Core.helper.util.Rectangle} Estimated record bounds with format { x, y, width, height, bottom, right }
     * @category Calculations
     */
    getRecordCoordsByIndex(recordIndex, local = false) {
        const
            me                = this,
            scrollingViewport = me.client._bodyRectangle,
            // Not using rowOffsetHeight since it floors the value and that rounding might give big errors far down
            height            = me.averageRowHeight || (me._rowHeight + me.grid._rowBorderHeight),
            currentTopIndex = me.topRow.dataIndex,
            currentBottomIndex = me.bottomRow.dataIndex,
            // Instead of estimating top from the very top, use closest known coordinate. Makes sure a coordinate is not
            // estimated on wrong side of rendered rows, needed to correctly draw dependencies where one event is located
            // on a unrendered row
            calculateFrom =
                // bottomRow is closest, calculate from it
                recordIndex > currentBottomIndex
                    ? { index : recordIndex - currentBottomIndex - 1, y : me.bottomRow.bottom, from : 'bottomRow' }
                    //  closer to topRow than 0, use topRow
                    : recordIndex > currentTopIndex / 2
                        ? { index : recordIndex - currentTopIndex, y : me.topRow.top, from : 'topRow' }
                        // closer to the very top, use it
                        : { index : recordIndex, y : 0, from : 'top' },
            top               = Math.floor(calculateFrom.y + calculateFrom.index * height),
            //top               = Math.floor(recordIndex * height),
            result            = new Rectangle(
                scrollingViewport.x,
                local ? top : top + scrollingViewport.y - me.client.scrollable.y,
                scrollingViewport.width,
                height
            );

        // Signal that it's not based on an element, so is only approximate.
        // Grid.scrollRowIntoView will have to go round again using the block options below to ensure it's correct.
        result.virtual = true;

        // When the block becomes visible, scroll it to the logical position using the scrollIntoView's block
        // option. If it's above, use block: 'start', if below, use block: 'end'.
        result.block = result.bottom < scrollingViewport.y ? 'start' : (result.y > scrollingViewport.bottom ? 'end' : 'nearest');

        return result;
    }

    /**
     * Total estimated grid height (used for scroller)
     * @property {Number}
     * @readonly
     * @category Calculations
     */
    get totalHeight() {
        return Math.floor(this.store.count * this.rowOffsetHeight);
    }

    //endregion

    //region Iteration etc.

    /**
     * Calls a function for each Row
     * @param {Function} fn Function that will be called with Row as first parameter
     * @category Iteration
     */
    forEach(fn) {
        this.rows.forEach(fn);
    }

    /**
     * Iterator that allows you to do for (let row of rowManager)
     * @category Iteration
     */
    [Symbol.iterator]() {
        return this.rows[Symbol.iterator]();
    }

    //endregion

    //region Scrolling & rendering

    /**
     * Renders from the top of the grid, also resetting scroll to top. Used for example when collapsing all groups.
     * @category Scrolling & rendering
     */
    returnToTop() {
        const me = this;

        me.topIndex = 0;
        me.lastScrollTop = 0;
        me.topRow.dataIndex = 0;

        // Force the top row to the top of the scroll range
        me.topRow.setTop(0);

        me.refresh();

        // Rows rendered from top, make sure grid is scrolled to top also
        me.grid.scrollable.y = 0;
    }

    /**
     * Renders from specified records row and down (used for example when collapsing a group, does not affect rows above).
     * @param {Core.data.Model} record Record of first row to render
     * @category Scrolling & rendering
     */
    renderFromRecord(record) {
        const row = this.getRowById(record.id);
        if (row) {
            this.renderFromRow(row);
        }
    }

    /**
     * Renders from specified row and down (used for example when collapsing a group, does not affect rows above).
     * @param {Grid.row.Row} fromRow First row to render
     * @category Scrolling & rendering
     */
    renderFromRow(fromRow = null) {
        const me = this,
            { _rows, store } = me,
            storeCount = store.count;

        // Calculate row count, adding rows if needed, but do not rerender - we are going to do that below.
        // Bail out if no rows. Allow removing rows if we have more than store have rows
        if (me.calculateRowCount(false, storeCount < _rows.length, true) === 0) {
            return;
        }

        let // render from this row
            fromRowIndex  = fromRow ? _rows.indexOf(fromRow) : 0,
            // starting either from its specified dataIndex or from its index (happens on first render, no dataIndex yet)
            dataIndex     = fromRow ? fromRow.dataIndex : _rows[0].dataIndex,
            // amount of records after this one in store
            recordsAfter  = storeCount - dataIndex - 1,
            // render to this row, either the last row or the row which will hold the last record available
            toRowIndex    = Math.min(_rows.length - 1, fromRowIndex + recordsAfter),
            // amount of rows which wont be rendered below last record (if we have fewer records than topRow + row count)
            leftOverCount = _rows.length - toRowIndex - 1,
            // Start with top correctly just below the previous row's bottom
            top           = fromRowIndex > 0 ? _rows[fromRowIndex - 1].bottom : _rows[fromRowIndex].top,
            row;

        // _rows array is ordered in display order, just iterate to the end
        for (let i = fromRowIndex; i <= toRowIndex; i++) {
            row = _rows[i];
            // Needed in scheduler when translating events, happens before render
            row.dataIndex = dataIndex;
            row.setTop(top);
            row.render(dataIndex, store.getAt(dataIndex++), false);
            top += row.offsetHeight;
        }

        // if number for records to display has decreased, for example by collapsing a node, we might get unused rows
        // below bottom. move those to top to not have unused rows laying around
        while (leftOverCount-- > 0) {
            me.displayRecordAtTop();
        }

        if (me.averageRowHeight) {
            // Adjust average, subtracting the rendered block's estimated height and then adding the actual height
            me.averageRowHeight = (me.averageRowHeight * (storeCount - me.rowCount) + (top - me.topRow.top)) / storeCount;
        }
        else {
            me.averageRowHeight = (top - me.topRow.top) / me.rowCount;
        }

        // Reestimate total height
        me.estimateTotalHeight();

        me.trigger('renderDone');
    }

    /**
     * Renders the passed array (or [Set](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Set)) of {@link Grid.row.Row rows}
     * @param {Grid.row.Row[]|Set} rows The rows to render
     * @category Scrolling & rendering
     */
    renderRows(rows) {
        let oldHeight,
            heightChanged = false;

        rows = Array.from(rows);

        // Render the requested rows.
        for (const row of rows) {
            oldHeight = row.height;

            // Pass updatingSingleRow as false, so that it does not shuffle following
            // rows downwards on each render. We do that once here after the rows are all refreshed.
            row.render(null, null, false);
            heightChanged |= row.height !== oldHeight;
        }

        // If this caused a height change, shuffle following rows.
        if (heightChanged) {
            this.translateFromRow(rows[0], true);

            // Reestimate total height
            this.estimateTotalHeight();
        }

        this.trigger('renderDone');
    }

    /**
     * Translates all rows after the specified row. Used when a single rows height is changed and the others should
     * rearrange. (Called from Row#render)
     * @param {Grid.row.Row} fromRow
     * @private
     * @category Scrolling & rendering
     */
    translateFromRow(fromRow, batch = false) {
        let me         = this,
            top        = fromRow.bottom,
            storeCount = me.store.count,
            row, index;

        for (index = fromRow.dataIndex + 1, row = me.getRow(index); row; row = me.getRow(++index)) {
            row.setTop(top);
            top += row.offsetHeight;
        }

        // Adjust average, subtracting the rendered block's estimated height and then adding the actual height
        me.averageRowHeight = (me.averageRowHeight * (storeCount - me.rowCount) + (me.bottomRow.bottom - me.topRow.top)) / storeCount;

        // Reestimate total height
        if (!batch) {
            me.estimateTotalHeight();
        }
    }

    /**
     * Rerender all rows
     * @category Scrolling & rendering
     */
    refresh() {
        const
            me         = this,
            { topRow } = me;

        // too early
        if (!topRow) {
            return;
        }

        me.idMap = {};

        me.renderFromRow(topRow);

        me.trigger('refresh');
    }

    /**
     * Makes sure that specified record is displayed in view
     * @param newScrollTop Top of visible section
     * @param [forceRecordIndex] Index of record to display at center
     * @private
     * @category Scrolling & rendering
     */
    jumpToPosition(newScrollTop, forceRecordIndex) {
        // There are two very different requirements here.
        // If there is a forceRecordIndex, that takes precedence to get it into the center of the
        // viewport, and wherever we render the calculated row block, we may then *adjust the scrollTop*
        // to get that row to the center.
        //
        // If there's no forceRecordIndex, then the scroll position is the primary objective and
        // we must render what we calculate to be correct at that viewport position.
        const
            me          = this,
            rowHeight   = me.rowOffsetHeight,
            storeCount  = me.store.count,
            // Calculate index of the top of the rendered block.
            // If we are targeting the scrollTop, this will be the top index at the scrollTop minus prepend count.
            // If we are targeting a recordIndex, this will attempt to place that in the center of the rendered block.
            targetIndex = forceRecordIndex == null ? Math.floor(newScrollTop / rowHeight) - me.prependRowBuffer : forceRecordIndex - Math.floor(me.rowCount / 2),
            startIndex  = Math.max(Math.min(targetIndex, storeCount - me.rowCount), 0),
            viewportTop = me.client.scrollable.y,
            viewportBottom = Math.min(me.client._bodyRectangle.height + viewportTop + me.appendBufferHeight, me.totalHeight);

        me.lastScrollTop = newScrollTop;
        me.topRow.dataIndex = me.topIndex = startIndex;
        me.topRow.setTop(startIndex * rowHeight, false);

        // render entire buffer
        me.refresh();

        // TODO: It is likely the approach below will be needed for scrolling in opposite direction also, although no
        //   problem encountered yet

        // Not filled all the way down?
        if (me.bottomRow.bottom < viewportBottom) {
            // Fill with available rows (might be available above buffer because of var row height), stop if we run out of records :)
            while (me.bottomRow.bottom < viewportBottom && me._rows[me.prependRowBuffer].top < viewportTop && me.bottomRow.dataIndex < storeCount - 1) {
                me.displayRecordAtBottom();
            }

            // Average row height in rendered block
            const blockHeight = me.bottomRow.bottom - me.topRow.top;

            // Adjust average, subtracting the rendered block's estimated height and then adding the actual height
            me.averageRowHeight = (me.averageRowHeight * (storeCount - me.rowCount) + blockHeight) / storeCount;

            // TODO: Block below was not needed for current tests, but if row height in one block is enough smaller
            //  than average row height then we will need to add more rows

            // Still not filled all the way down? Need more rows
            // if (me.bottomRow.bottom < viewportBottom) {
            //     //const localAverage = blockHeight / me.rowCount;
            //     while (me.bottomRow.bottom < viewportBottom) {
            //        me.addRecordAtBottom();
            //     }
            // }

        }

        me.estimateTotalHeight();

        // If the row index is our priority, then scroll it into the center
        if (forceRecordIndex != null) {
            const targetRow      = me.getRow(forceRecordIndex),
                // When coming from a block of high rowHeights to one with much lower we might still miss the target...
                // TODO: Jump again in these cases?
                rowCenter      = targetRow && Rectangle.from(targetRow._elementsArray[0]).center.y,
                viewportCenter = me.grid.scrollable.viewport.center.y;

            // Scroll the targetRow into the center of the viewport
            if (targetRow) {
                me.grid.scrollable.y = newScrollTop = Math.floor(me.grid.scrollable.y + (rowCenter - viewportCenter));
            }
        }

        return newScrollTop;
    }

    /**
     * Jumps to a position if it is far enough from current position. Otherwise does nothing.
     * @private
     * @category Scrolling & rendering
     */
    warpIfNeeded(newScrollTop) {
        const me     = this,
            result = { newScrollTop, deltaTop : newScrollTop - me.lastScrollTop };

        // if gap to fill is large enough, better to jump there than to fill row by row
        if (Math.abs(result.deltaTop) > (me.rowCount * me.rowOffsetHeight) * 3) {
            // no specific record targeted
            let index;

            // Specific record specified as target of scroll?
            if (me.scrollTargetRecordId) {
                index = me.store.indexOf(me.scrollTargetRecordId);

                // since scroll is happening async record might have been removed after requesting scroll,
                // in that case we rely on calculated index (as when scrolling without target)
            }

            // perform the jump and return results
            result.newScrollTop = me.jumpToPosition(newScrollTop, index);
            result.deltaTop = 0; // no extra filling needed
        }

        return result;
    }

    /**
     * Handles virtual rendering (only visible rows + buffer are in dom) for rows
     * @param {Number} newScrollTop The `Y` scroll position for which to render rows.
     * @param {Boolean} [force=false] Pass `true` to update the rendered row block even if the scroll position has not changed.
     * @return {Number} Adjusted height required to fit rows
     * @private
     * @category Scrolling & rendering
     */
    updateRenderedRows(newScrollTop, force, ignoreError) {
        const me         = this,
            clientRect = me.client._bodyRectangle;

        // Might be triggered after removing all records, should not crash
        if (me.rowCount === 0) {
            return 0;
        }

        let result = me.estimatedTotalHeight;

        if (
            force ||
            // Only react if we have scrolled by more than one row
            Math.abs(newScrollTop - me.lastScrollTop) > me.rowOffsetHeight ||
            // or if we have a gap at top/bottom (#9375)
            me.topRow.top > newScrollTop ||
            me.bottomRow.bottom < newScrollTop + clientRect.height
        ) {
            // If scrolled by a large amount, jump instead of rendering each row
            const posInfo = me.warpIfNeeded(newScrollTop);

            me.scrollTargetRecordId = null;

            // Cache the last correct render scrollTop before fill.
            // it can be adjusted to hide row position corrections.
            me.lastScrollTop = posInfo.newScrollTop;

            if (posInfo.deltaTop > 0) {
                // Scrolling down
                me.fillBelow(posInfo.newScrollTop);
            }
            else if (posInfo.deltaTop < 0) {
                // Scrolling up
                me.fillAbove(posInfo.newScrollTop);
            }

            // Calculate the new height based on new content
            result = me.estimateTotalHeight();

            // If it's a temporary scroll, we can be told to ignore the drift.
            // Apart from that, we must correct keep the rendered block position correct.
            // Otherwise, when rolling upwards after a teleport, we may not be able to reach
            // the top. Some rows may end up at -ve positions.
            if (!ignoreError) {
                // Only correct the rendered block position if we are in danger of running out of scroll space.
                // That is if we are getting towards the top or bottom of the scroll range.
                if (
                    // Scrolling up within top zone
                    (posInfo.deltaTop < 0 && newScrollTop < clientRect.height * 2) ||
                    // Scrolling down within bottom zone
                    (posInfo.deltaTop > 0 && newScrollTop > me.estimatedTotalHeight - clientRect.height * 2 - 3)
                ) {
                    // Correct the rendered block position if it's not at the calculated position.
                    // Keep the visual position correct by adjusting the scrollTop by the same amount.
                    // When variable row heights are used, this will keep the rendered block top correct.
                    // TODO: Calc could be eased more, using distance left to have less effect the further away from top/bottom
                    const error = me.topRow.top - me.topIndex * me.rowOffsetHeight;
                    if (error) {
                        me.offsetRows(-error);
                        me.grid.scrollable.y = me.lastScrollTop = me.grid.scrollable.y - error;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Moves as many rows from the bottom to the top that are needed to fill to current scroll pos.
     * @param newTop Scroll position
     * @private
     * @category Scrolling & rendering
     */
    fillAbove(newTop) {
        const me         = this,
            fillHeight = newTop - me.topRow.top - me.prependBufferHeight;

        let accumulatedHeight = 0;

        while (accumulatedHeight > fillHeight && me.topIndex > 0) {
            // We want to show prev record at top of rows
            accumulatedHeight -= me.displayRecordAtTop();
        }

        me.trigger('renderDone');
    }

    /**
     * Moves as many rows from the top to the bottom that are needed to fill to current scroll pos.
     * @param newTop Scroll position
     * @private
     * @category Scrolling & rendering
     */
    fillBelow(newTop) {
        const me          = this,
            fillHeight  = newTop - me.topRow.top - me.prependBufferHeight,
            recordCount = me.store.count,
            rowCount    = me.rowCount;

        let accumulatedHeight = 0;

        // Repeat until we have filled empty height
        while (
            accumulatedHeight < fillHeight &&         // fill empty height
            me.topIndex + rowCount < recordCount &&   // as long as we have records left
            me.topRow.top + me.topRow.offsetHeight < newTop // and do not move top row fully into view (can happen with var row height)
        ) {
            // We want to show next record at bottom of rows
            accumulatedHeight += me.displayRecordAtBottom();
        }

        me.trigger('renderDone');
    }

    /**
     * Estimates height needed to fit all rows, based on average row height. Also offsets rows if needed to not be above
     * the reachable area of the view.
     * @returns {Number}
     * @private
     * @category Scrolling & rendering
     */
    estimateTotalHeight() {
        let me            = this,
            recordCount   = me.store.count,
            // When used to estimate initial height in a grid with autoHeight there is no averageRowHeight yet
            estimate      = Math.floor(recordCount * (me.averageRowHeight || me.rowOffsetHeight)),
            { bottomRow } = me;

        if (me.grid.renderingRows) {
            return;
        }

        // No bottomRow yet if estimating initial height in autoHeight grid
        if (bottomRow) {
            const bottom = bottomRow.bottom;
            //top                   = topRow.top;

            // To low estimate or reached the end with scroll left, adjust to fit current bottom
            if (bottom > estimate || (me.topIndex + me.rowCount >= recordCount && estimate > bottom)) {
                estimate = bottom;

                // estimate all the way down
                if (bottomRow.dataIndex < recordCount - 1) {
                    estimate += (recordCount - 1 - bottomRow.dataIndex) * me.rowOffsetHeight;
                }
            }
        }

        if (estimate != me.estimatedTotalHeight) {
            if (me.trigger('changeTotalHeight', { totalHeight : estimate }) !== false) {
                me.estimatedTotalHeight = estimate;
            }
        }

        return estimate;
    }

    /**
     * Moves a row from bottom to top and renders the corresponding record to it.
     * @returns {Number} New row height
     * @private
     * @category Scrolling & rendering
     */
    displayRecordAtTop() {
        const me           = this,
            recordIndex  = me.topIndex - 1,
            record       = me.store.getAt(recordIndex),
            recordCount  = me.store.count,
            // Row currently rendered at the bottom, the row we want to move
            bottomRow    = me.bottomRow,
            bottomRowTop = bottomRow.top;

        me.trigger('beforeTranslateRow', {
            row       : bottomRow,
            newRecord : record
        });

        // estimated top, for rendering that depends on having top
        bottomRow._top = me.topRow.top - me.rowOffsetHeight;
        bottomRow.estimatedTop = true;

        // Render row
        bottomRow.render(recordIndex, record, false);

        // Move it to top. Restore top so that the setter won't reject non-change
        // if the estimate happened to be correct.
        bottomRow._top = bottomRowTop;
        bottomRow.setBottom(me.topRow.top);
        bottomRow.estimatedTop = false;

        // Prev row is now at top
        me.topIndex--;

        // move to start of array (bottomRow becomes topRow)
        me._rows.unshift(me._rows.pop());

        me.averageRowHeight = (me.averageRowHeight * (recordCount - 1) + bottomRow.offsetHeight) / recordCount;

        return bottomRow.offsetHeight;
    }

    /**
     * Moves a row from top to bottom and renders the corresponding record to it.
     * @returns {Number} New row height
     * @private
     * @category Scrolling & rendering
     */
    displayRecordAtBottom() {
        const me          = this,
            //scrollMode  = me.rowScrollMode,
            recordIndex = me.topIndex + me.rowCount,
            record      = me.store.getAt(recordIndex),
            recordCount = me.store.count,
            // Row currently rendered on the top, the row we want to move
            topRow      = me.topRow;

        // if (scrollMode === 'dom') {
        //     // only for performance evaluation, not to be used
        //
        //     // remove divs
        //     topRow.removeElements();
        //
        //     // add new divs
        //     me.grid.regions.forEach(region => {
        //         let div = me.grid.getSubGrid(region).addNewRowElement();
        //         topRow.addElement(region, div);
        //     });
        // }

        me.trigger('beforeTranslateRow', {
            row       : topRow,
            newRecord : record
        });

        topRow.dataIndex = recordIndex;

        // Move it to bottom
        topRow.setTop(me.bottomRow.bottom);
        // Render row
        topRow.render(recordIndex, record, false);

        // Next row is now at top
        me.topIndex++;

        // move to end of array (topRow becomes bottomRow)
        me._rows.push(me._rows.shift());

        me.averageRowHeight = (me.averageRowHeight * (recordCount - 1) + topRow.offsetHeight) / recordCount;

        return topRow.offsetHeight;
    }

    //endregion
}
