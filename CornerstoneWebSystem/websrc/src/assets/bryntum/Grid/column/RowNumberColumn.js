import Column from './Column.js';
import ColumnStore from '../data/ColumnStore.js';
import DomHelper from '../../Core/helper/DomHelper.js';

/**
 * @module Grid/column/RowNumberColumn
 */

/**
 * A column that displays the row number in each cell
 *
 * @extends Grid/column/Column
 *
 * @example
 * let grid = new Grid({
    appendTo : targetElement,
    width    : 300,
    columns  : [
        { type : 'rownumber' }
    ]
});
 *
 * @classType rownumber
 * @externalexample column/RowNumberColumn.js
 */
export default class RowNumberColumn extends Column {
    static get defaults() {
        return {
            groupable  : false,
            sortable   : false,
            filterable : false,
            searchable : false,
            resizable  : false,
            minWidth   : 50,
            width      : 50,
            align      : 'right',
            text       : '#',
            editor     : false
        };
    }

    construct(config, store) {
        const me = this;

        super.construct(...arguments);

        // Update our width when the store mutates (tests test Columns in isolation with no grid, so we must handle that!)
        if (me.grid) {
            me.grid.store.on({
                change  : me.resizeToFitContent,
                thisObj : me
            });
            if (me.grid.store.count) {
                me.grid.on({
                    render  : me.resizeToFitContent,
                    thisObj : me,
                    once    : true
                });
            }
        }
        me.internalCellCls = 'b-row-number-cell';
    }

    static get type() {
        return 'rownumber';
    }

    /**
     * Renderer that displays the row number in the cell.
     * @private
     */
    renderer({ record, grid }) {
        return record.meta.specialRow ? '' : grid.store.indexOf(record, true) + 1;
    }

    /**
     * Resizes the column to match the widest string in it. Called when you double click the edge between column
     * headers
     */
    resizeToFitContent() {
        const grid  = this.grid,
            store = grid.store,
            count = store.count;

        if (count && !this.hidden) {
            const cellElement = grid.element.querySelector(`.b-grid-cell[data-column-id=${this.id}]`);

            // cellElement might not exist, e.g. when trial is expired
            if (cellElement) {
                const cellStyle = window.getComputedStyle(cellElement),
                    cellPadding = parseInt(cellStyle['padding-left']),
                    maxWidth    = DomHelper.measureText(count, cellElement);

                this.width = maxWidth + 2 * cellPadding;
            }
        }
    }

    set flex(f) {
        //<debug>
        if (f != null) {
            throw new Error('RowNumberer column may not be flexed');
        }
        //</debug>
    }
}

ColumnStore.registerColumnType(RowNumberColumn, true);
