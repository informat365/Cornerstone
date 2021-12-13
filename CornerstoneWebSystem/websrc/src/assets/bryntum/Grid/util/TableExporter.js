import DomHelper from '../../Core/helper/DomHelper.js';
import Base from '../../Core/Base.js';
import ObjectHelper from '../../Core/helper/ObjectHelper.js';
import DateColumn from '../column/DateColumn.js';

/**
 * @module Grid/util/TableExporter
 */

/**
 * This class transforms grid component into two arrays: rows and columns. Columns array contains objects with
 * meta information about column: field name, column name, width and type of the rendered value, rows array contains
 * arrays of cell values.
 *
 * ```javascript
 * const exporter = new TableExporter({ target : grid });
 * exporter.export()
 *
 * // Output
 * {
 *     columns : [
 *         { field : 'name',     value : 'First name', type : 'string',  width : 100 },
 *         { field : 'surname',  value : 'Last name',  type : 'string',  width : 100 },
 *         { field : 'age',      value : 'Age',        type : 'number',  width : 50  },
 *         { field : 'married',  value : 'Married',    type : 'boolean', width : 50  },
 *         { field : 'children', value : 'Children',   type : 'object',  width : 100 }
 *     ],
 *     rows : [
 *         ['Michael', 'Scott',   40, false, []],
 *         ['Jim',     'Halpert', 30, true,  [...]]
 *     ]
 * }
 * ```
 *
 * ## How data is exported
 *
 * Exporter iterates over store records and processes each record for each column being exported. Exporter uses same
 * approach to retrieve data as column: reading record field, configured on the column, or calling renderer function
 * if one is provided. This means data can be of any type: primitives or objects. So children array in the above code
 * snippet may contain instances of child record class.
 *
 * ## Column renderers
 *
 * Column renderers are commonly used to style the cell, or even render more HTML into it, like {@link Grid.column.WidgetColumn}
 * does. This is not applicable in case of export. Also, given grid uses virtual rendering (only renders visible rows) and
 * exporter iterates over all records, not just visible ones, we cannot provide all data necessary to the renderer. Some
 * arguments, like cellElement and row, wouldn't exist. Thus renderer is called with as much data we have: value,
 * record, column, grid, other {@link Grid.column.Column#config-renderer documented arguments} would be undefined.
 *
 * Exporter adds one more flag for renderer function: isExport. When renderer receives this flag it knows
 * data is being exported and can skip DOM work to return simpler value. Below snippet shows simplified code of the
 * widget column handling export:
 *
 * ```javascript
 * renderer({ isExport }) {
 *     if (isExport) {
 *         return null;
 *     }
 *     else {
 *         // widget rendering routine
 *         ...
 *     }
 * }
 * ```
 *
 * ## Column types
 *
 * Column types are not actually a complete list of JavaScript types (you can get actual type of the cell using typeof) it
 * is a simple and helpful meta information.
 *
 * Available column types are:
 *  * string
 *  * number
 *  * boolean
 *  * date
 *  * object
 *
 * Everything which is not primitive like string/number/bool (or a date) is considered an object. This includes null, undefined,
 * arrays, classes, functions etc.
 *
 * ## Getting column type
 *
 * If existing grid column is used, column type first would be checked with {@link Grid.column.Column#config-exportedType exportedType}
 * config. If exportedType is undefined or column does not exist in grid, type is read from a record field definition.
 * If the field is not defined, object type is used.
 *
 * Configuring exported type:
 *
 * ```javascript
 * new Grid({
 *     columns : [
 *         {
 *             name         : 'Name',
 *             field        : 'name',
 *             exportedType : 'object',
 *             renderer     : ({ value, isExport }) => {
 *                 if (isExport) {
 *                     return { value }; // return value wrapped into object
 *                 }
 *             }
 *     ]
 * })
 * ```
 *
 * @extends Core/Base
 */
export default class TableExporter extends Base {
    static get defaultConfig() {
        return {
            /**
             * Target grid instance to export data from
             * @config {Grid} target
             */
            target : null,

            /**
             * Specifies a default column width if no width specified
             * @config {Number} defaultColumnWidth
             * @default
             */
            defaultColumnWidth : 100,

            /**
             * Set to false to export date as it is displayed by Date column formatter
             * @config {Boolean}
             * @default
             */
            exportDateAsInstance : true,

            /**
             * If true and the grid is grouped, shows the grouped value in the first column. True by default.
             * @config {Boolean} showGroupHeader
             * @default
             */
            showGroupHeader : true,

            /**
             * An array of columns configuration used to specify columns width, headers name, and column fields to get the data from.
             * 'field' config is required. If 'text' is missing, it will try to get it retrieved from the grid column or the 'field' config.
             * If 'width' is missing, it will try to get it retrieved from the grid column or {@link #config-defaultColumnWidth} config.
             * If no columns provided the config will be generated from the grid columns.
             *
             * For example:
             * ```javascript
             * columns : [
             *     'firstName', // field
             *     'age', // field
             *     { text : 'Starts', field : 'start', width : 140 },
             *     { text : 'Ends', field : 'finish', width : 140 }
             * ]
             * ```
             *
             * @config {String[]|Object[]} columns
             * @default
             */
            columns : null,

            /**
             * When true and tree is being exported, node names are indented with {@link #config-indentationSymbol}
             * @config {Boolean}
             * @default
             */
            indent : true,

            /**
             * This symbol (four spaces by default) is used to indent node names when {@link #config-indent} is true
             * @config {String}
             * @default
             */
            indentationSymbol : '\u00A0\u00A0\u00A0\u00A0'
        };
    }

    /**
     * Exports grid data according to provided config
     * @param {Object} config
     * @returns {{ rows : Object[][], columns : Object[] }}
     */
    export(config = {}) {
        const me = this;

        config = ObjectHelper.assign({}, me.config, config);

        me.normalizeColumns(config);

        return me.generateExportData(config);
    }

    generateExportData(config) {
        const
            me      = this,
            columns = me.generateColumns(config),
            rows    = me.generateRows(config);

        return { rows, columns };
    }

    normalizeColumns(config) {
        // In case columns are provided we need to use normalized config. If those are not provided, we are going
        // to use real columns, possible invoking renderers (we need to pass column instance to the renderer to
        // avoid breaking API too much)
        const columns = config.columns || this.target.columns.visibleColumns.filter(rec => rec.exportable !== false);

        config.columns = columns.map(col => {
            if (typeof col === 'string') {
                return this.target.columns.find(column => column.field === col) || { field : col };
            }
            else {
                return col;
            }
        });
    }

    generateColumns(config) {
        return config.columns.map(col => this.processColumn(col, config));
    }

    generateRows(config) {
        const { columns } = config;

        if (!columns.length) {
            return [];
        }

        const
            me         = this,
            { target } = me;

        return target.store
            // although columns are taken from config, it is convenient to provide them as a separate argument
            // becuase that allows to override set of columns to process
            .map(record => me.processRecord(record, columns, config))
            // filter out empty rows
            .filter(cells => cells.length);
    }

    getColumnType(column, store = this.target.store) {
        let result = column.exportedType || 'object';

        if (column.exportedType === undefined) {
            if (column.field) {
                const fieldDefinition = store.modelClass.getFieldDefinition(column.field);

                if (fieldDefinition && fieldDefinition.type) {
                    result = fieldDefinition.type;
                }
            }
        }

        return result;
    }

    /**
     * Extracts export data from the column instance
     * @param {Column} column
     * @param {Object} config
     * @private
     * @returns {Object}
     */
    processColumn(column, config) {
        const
            me                     = this,
            { target }             = me,
            { defaultColumnWidth } = config;

        let { field, text : value, width, minWidth } = column;

        // If column is not configured with field, field is generated (see Column.js around line 514).
        // In export we want empty string there
        if (!(field in target.store.modelClass.fieldMap)) {
            field = '';
        }

        // If name or width is missing try to retrieve them from the grid column and the field, or use default values.
        if (!value || !width) {
            const gridColumn = target.columns.find(col => col.field === field);

            if (!value) {
                value = gridColumn && gridColumn.text || field;
            }

            // null or undefined
            if (width == null) {
                width = gridColumn && gridColumn.width || defaultColumnWidth;
            }
        }

        width = Math.max(width || defaultColumnWidth, minWidth || defaultColumnWidth);

        return { field, value, width, type : me.getColumnType(column) };
    }

    /**
     * Extracts export data from the record instance reading supplied column configs
     * @param {Model|null} record If null is passed, all columns will be filled with empty strings
     * @param {Column[]} columns
     * @param {Object} config
     * @private
     * @returns {Object[]}
     */
    processRecord(record, columns, config) {
        const
            { target } = this,
            {
                showGroupHeader,
                indent,
                indentationSymbol
            }  = config;

        let cells;

        if (!record) {
            cells = columns.map(() => '');
        }
        else if (record.meta.specialRow) {
            if (showGroupHeader && record.meta.groupRowFor) {
                cells = columns.map(column => {
                    return target.features.group.buildGroupHeader({
                        // Create dummy element to get html from
                        cellElement : DomHelper.createElement(),
                        grid        : target,
                        record,
                        column
                    });
                });
            }
        }
        else {
            cells = columns.map(column => {
                let value       = record[column.field],
                    useRenderer = column.renderer || column.defaultRenderer;

                if (useRenderer && !(value && column instanceof DateColumn && config.exportDateAsInstance)) {
                    value = useRenderer.call(column, {
                        value,
                        record,
                        column,
                        grid     : target,
                        isExport : true
                    });
                }

                if (indent && column.tree) {
                    value = `${indentationSymbol.repeat(record.childLevel)}${value}`;
                }

                return value;
            });
        }

        return cells;
    }
}
