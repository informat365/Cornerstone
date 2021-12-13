import Column from './Column.js';
import ColumnStore from '../data/ColumnStore.js';
import DateHelper from '../../Core/helper/DateHelper.js';

/**
 * @module Grid/column/DateColumn
 */

/**
 * A column that displays a date in the specified format (see {@link Core.helper.DateHelper#function-format-static} for
 * formatting options), with a date picker as default editor.
 *
 * If no `format` is specified, {@link Core.helper.DateHelper#property-defaultFormat-static DateHelper.defaultFormat} is
 * used. Please note that by default the `L` format is used.
 *
 * @extends Grid/column/Column
 *
 * @example
 * new Grid({
 *     appendTo : document.body,
 *
 *     columns : [
 *          { type: 'date', text: 'Start date', format: 'YYYY-MM-DD', data: 'start' }
 *     ]
 * });
 *
 * @classType date
 * @externalexample column/DateColumn.js
 */
export default class DateColumn extends Column {

    //region Config

    static get type() {
        return 'date';
    }

    // Type to use when auto adding field
    static get fieldType() {
        return 'date';
    }

    static get fields() {
        return ['format', 'pickerFormat', 'step'];
    }

    static get defaults() {
        return {
            /**
             * Date format
             * @config {String}
             * @category Common
             */
            format : 'L',

            /**
             * Time increment duration value. See {@link Core.widget.DateField#config-step} for more information
             * @config {String|Number|Object}
             * @category Common
             */
            step : 1,

            minWidth : 85,

            filterType : 'date'
        };
    }

    //endregion

    //region Init

    constructor(config, store) {
        super(...arguments);

        this.internalCellCls = 'b-date-cell';
    }

    //endregion

    //region Display

    /**
     * Renderer that displays the date with the specified format. Also adds cls 'date-cell' to the cell.
     * @private
     */
    defaultRenderer({ value }) {
        return value ? this.formatValue(value) : '';
    }

    /**
     * Group renderer that displays the date with the specified format.
     * @private
     */
    groupRenderer({ cellElement, groupRowFor }) {
        cellElement.innerHTML = this.formatValue(groupRowFor);
    }

    //endregion

    //region Formatter

    /**
     * Used by both renderer and groupRenderer to do the actual formatting of the date
     * @private
     * @param value
     * @returns {String}
     */
    formatValue(value) {
        // Ideally we should be served a date, but if not make it easier for the user by parsing
        if (typeof value === 'string') {
            value = DateHelper.parse(value, this.format || undefined); // null does not use default format
        }
        return DateHelper.format(value, this.format || undefined);
    }

    //endregion

    //region Getters/setters

    /**
     * Get/Set format for date displayed in cell and editor (see {@link Core.helper.DateHelper#function-format-static} for formatting options)
     * @property {String}
     */
    set format(value) {
        const me = this,
            editor = me.editor;
        me.set('format', value);
        if (editor) {
            editor.format = me.format;
        }
    }

    get format() {
        return this.get('format');
    }

    get defaultEditor() {
        const me = this;

        return {
            name                 : me.field,
            type                 : 'date',
            calendarContainerCls : 'b-grid-cell-editor-related',
            format               : me.format,
            step                 : me.step
        };
    }

    //endregion

}

ColumnStore.registerColumnType(DateColumn, true);
DateColumn.exposeProperties();
