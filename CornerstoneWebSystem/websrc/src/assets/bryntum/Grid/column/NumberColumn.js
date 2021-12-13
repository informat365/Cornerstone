import Column from './Column.js';
import ColumnStore from '../data/ColumnStore.js';

/**
 * @module Grid/column/NumberColumn
 */

/**
 * A column for showing/editing numbers
 *
 * @extends Grid/column/Column
 * @example
 * new Grid({
 *     appendTo : document.body,
 *
 *     columns : [
 *         { type: 'number', min: 0, max : 100, field: 'score' }
 *     ]
 * });
 *
 * @classType number
 * @externalexample column/NumberColumn.js
 */
export default class NumberColumn extends Column {
    //region Config

    static get type() {
        return 'number';
    }

    // Type to use when auto adding field
    static get fieldType() {
        return 'number';
    }

    static get fields() {
        return [
            /**
             * The minimum value for the field used during editing.
             * @config {Number} min
             * @category Common
             */
            'min',

            /**
             * The maximum value for the field used during editing.
             * @config {Number} max
             * @category Common
             */
            'max',

            /**
             * Step size for the field used during editing. Specify a fractional step size to allow entering decimal
             * numbers.
             * @config {Number} step
             * @category Common
             */
            'step',

            /**
             * Unit to append to displayed value.
             * @config {String} unit
             * @category Common
             */
            'unit'
        ];
    }

    static get defaults() {
        return {
            filterType : 'number'
        };
    }

    constructor(config, store) {
        super(...arguments);

        this.internalCellCls = 'b-number-cell';
    }

    //endregion

    //region Init

    get defaultEditor() {
        return {
            name : this.field,
            type : 'numberfield',
            max  : this.max,
            min  : this.min,
            step : this.step
        };
    }

    /**
     * Renderer that displays value + optional unit in the cell
     * @private
     */
    defaultRenderer({ value = 0 }) {
        if (this.unit) {
            return `${value}${this.unit}`;
        }

        return value;
    }
}

ColumnStore.registerColumnType(NumberColumn, true);
NumberColumn.exposeProperties();
