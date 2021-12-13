import NumberColumn from './NumberColumn.js';
import ColumnStore from '../data/ColumnStore.js';

// TODO: resuse icon elements

/**
 * @module Grid/column/RatingColumn
 */

/**
 * A column that displays a star rating. Click a start to set a value, shift+click to unset a single start from the end.
 * Clicking the first and only star toggles it.
 *
 * @extends Grid/column/NumberColumn
 *
 * @example
 * new Grid({
 *     appendTo : document.body,
 *
 *     columns : [
 *         { type: 'rating', max : 10, field: 'rating' }
 *     ]
 * });
 *
 * @classType percent
 * @externalexample column/RatingColumn.js
 */
export default class RatingColumn extends NumberColumn {
    static get type() {
        return 'rating';
    }

    // Type to use when auto adding field
    static get fieldType() {
        return 'number';
    }

    static get fields() {
        return ['emptyIcon', 'filledIcon', 'editable'];
    }

    static get defaults() {
        return {
            min : 0,
            max : 5,

            /**
             * The empty rating icon to show
             * @config {String}
             * @category Rendering
             */
            emptyIcon : 'b-icon b-icon-star',

            /**
             * The filled rating icon to show
             * @config {String}
             * @category Rendering
             */
            filledIcon : 'b-icon b-icon-star',

            /**
             * Allow user to click an icon to change the value
             * @config {Boolean}
             * @category Interaction
             */
            editable : true,

            filterType   : 'number',
            searchable   : false,
            width        : '11.2em',
            htmlEncode   : false,
            autoSyncHtml : true,
            minWidth     : '11.2em',
            editor       : false
        };
    }

    constructor(config, store) {
        super(...arguments);

        this.internalCellCls = 'b-rating-cell';
    }

    /**
     * Renderer that displays a number of stars in the cell. Also adds CSS class 'b-rating-cell' to the cell.
     * @private
     */
    renderer({ value }) {
        let html = `<div class="b-rating-cell-inner ${!this.editable ? 'b-not-editable' : ''}">`;

        for (let i = 0; i < this.max; i++) {
            let filled = i < value;
            html += `<i class="b-rating-icon ${filled ? 'b-filled ' + this.filledIcon : 'b-empty ' + this.emptyIcon}"></i>`;
        }

        html += '</div>';

        return html;
    }

    onCellClick({ grid, column, record, cellSelector, target, event }) {
        if (target.classList.contains('b-rating-icon') && !grid.readOnly && column.editable) {
            let starIndex = [].indexOf.call(target.parentNode.childNodes, target);

            if (target.classList.contains('b-filled') && (event.metaKey || event.shiftKey)) {
                starIndex = starIndex - 1;
            }

            // Clicking first star when it is only one removes it
            if (record.get(column.field) === 1 && starIndex === 0) {
                starIndex = -1;
            }

            record.set(column.field, starIndex + 1);
        }
    }
}

ColumnStore.registerColumnType(RatingColumn, true);
RatingColumn.exposeProperties();
