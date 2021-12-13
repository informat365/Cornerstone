import Column from './Column.js';
import ColumnStore from '../data/ColumnStore.js';

/**
 * @module Grid/column/PercentColumn
 */

/**
 * A column that display a basic progress bar
 *
 * @extends Grid/column/Column
 *
 * @example
 * new Grid({
 *     appendTo : document.body,
 *
 *     columns : [
 *         { type: 'percent', text: 'Progress', data: 'progress' }
 *     ]
 * });
 *
 * @classType percent
 * @externalexample column/PercentColumn.js
 */
export default class PercentColumn extends Column {
    static get type() {
        return 'percent';
    }

    // Type to use when auto adding field
    static get fieldType() {
        return 'number';
    }

    static get fields() {
        return ['lowThreshold'];
    }

    static get defaults() {
        return {
            /**
             * PercentColumn uses a {@link Core.widget.NumberField} configured with an allowed interval 0 - 100 as
             * its default editor.
             * @config {Object|String}
             * @default Core.widget.NumberField
             * @category Misc
             */
            editor : {
                type : 'number',
                min  : 0,
                max  : 100
            },

            /**
             * When below this percentage the bar will have `b-low` CSS class added. By default it turns the bar red.
             * @config {Number}
             * @category Rendering
             */
            lowThreshold : 20,

            filterType      : 'number',
            htmlEncode      : false,
            searchable      : false,
            summaryRenderer : sum => `${sum}%`
        };
    }

    constructor(config, store) {
        super(...arguments);

        this.internalCellCls = 'b-percent-bar-cell';
    }

    /**
     * Renderer that displays a progress bar in the cell.
     * @private
     */
    renderer({ value }) {
        value = value || 0;

        return {
            className : 'b-percent-bar-outer',
            children  : [
                {
                    tag       : 'div',
                    className : {
                        'b-percent-bar' : 1,
                        'b-zero'        : value === 0,
                        'b-low'         : value < this.lowThreshold
                    },
                    style : {
                        width : value + '%'
                    },
                    html : value + '%'
                }
            ]
        };
    }

    // Overrides base implementation to cleanup the checkbox, for example when a cell is reused as part of group header
    clearCell(cellElement) {
        if (cellElement.percentBarElement) {
            cellElement.percentBarElement = null;
        }
        super.clearCell(cellElement);
    }

    // Null implementation because the column width drives the width of its content.
    // So the concept of sizing to content is invalid here.
    resizeToFitContent() {}
}

PercentColumn.sum = 'average';

ColumnStore.registerColumnType(PercentColumn, true);
