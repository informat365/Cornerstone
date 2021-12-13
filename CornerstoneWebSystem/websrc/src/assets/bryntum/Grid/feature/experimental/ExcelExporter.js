/*global zipcelx*/
import GridFeatureManager from '../GridFeatureManager.js';
import InstancePlugin from '../../../Core/mixin/InstancePlugin.js';
import TableExporter from '../../util/TableExporter.js';
import BooleanUnicodeSymbol from '../../util/BooleanUnicodeSymbol.js';
import DateHelper from '../../../Core/helper/DateHelper.js';
import ObjectHelper from '../../../Core/helper/ObjectHelper.js';

/**
 * @module Grid/feature/experimental/ExcelExporter
 */

/**
 * **NOTE**: This class requires a 3rd party library to operate.
 *
 * A feature that allows exporting Grid data to Excel without involving the server. It uses {@link Grid.util.TableExporter}
 * class as data provider, [zipcelx library](https://www.npmjs.com/package/zipcelx)
 * forked and adjusted to support [column width config](https://github.com/bryntum/zipcelx/tree/column-width-build)
 * and [Microsoft XML specification](https://msdn.microsoft.com/en-us/library/office/documentformat.openxml.spreadsheet.aspx).
 * Zipcelx should be either in global scope (window) or can be provided with {@link #config-zipcelx} config.
 *
 * ```
 * // Global scope
 * <script src="zipcelx.js"></script>
 *
 * // importing from package
 * import zipcelx from 'zipcelx';
 *
 * const grid = new Grid({
 *     features : {
 *         excelExporter : {
 *             zipcelx
 *         }
 *     }
 * })
 * ```
 *
 * Here is an example of how to add the feature:
 *
 * ```javascript
 * const grid = new Grid({
 *     features : {
 *         excelExporter : {
 *             // Choose the date format for date fields
 *             dateFormat : 'YYYY-MM-DD HH:mm',
 *
 *             exporterConfig : {
 *                 // Choose the columns to include in the exported file
 *                 columns : ['name', 'role']
 *             }
 *         }
 *     }
 * });
 * ```
 *
 * And how to call it:
 *
 * ```javascript
 * grid.features.excelExporter.export({
 *     filename : 'Export',
 *     columns : [
 *         { text : 'First Name', field : 'firstName', width : 90 },
 *         { text : 'Age', field : 'age', width : 40 },
 *         { text : 'Starts', field : 'start', width : 140 },
 *         { text : 'Ends', field : 'finish', width : 140 }
 *     ]
 * })
 * ```
 *
 * @extends Core/mixin/InstancePlugin
 * @demo Grid/exporttoexcel
 */
export default class ExcelExporter extends InstancePlugin {
    static get $name() {
        return 'ExcelExporter';
    }

    static get defaultConfig() {
        return {
            /**
             * Name of the exported file
             * @config {String} filename
             * @default
             */
            filename : null,

            /**
             * Defines how date in a cell will be formatted
             * @config {String} dateFormat
             * @default
             */
            dateFormat : 'YYYY-MM-DD',

            /**
             * Exporter class to use as a data provider. {@link Grid.util.TableExporter} by default.
             * @config {Grid.util.TableExporter}
             * @default
             */
            exporterClass : TableExporter,

            /**
             * Configuration object for {@link #config-exporterClass exporter class}.
             * @config {Object}
             */
            exporterConfig : null,

            /**
             * Reference to zipcelx library. If not provided, exporter will look in the global scope.
             * @config
             */
            zipcelx : null,

            /**
             * If this config is true, exporter will convert all empty values to ''. Empty values are:
             * * undefined, null, NaN
             * * Objects/class instances that do not have toString method defined and are stringified to [object Object]
             * * functions
             * @config {Boolean}
             */
            convertEmptyValueToEmptyString : true
        };
    }

    processValue(value) {
        if (
            value === undefined ||
            value === null ||
            Number.isNaN(value) ||
            typeof value === 'function' ||
            (typeof value === 'object' && String(value) === '[object Object]')
        ) {
            return '';
        }
        else {
            return value;
        }
    }

    generateExportData(config) {
        const
            me                = this,
            { rows, columns } = me.exporter.export(config.exporterConfig);

        return {
            rows : rows.map(row => {
                return row.map((value, index) => {
                    if (value instanceof Date) {
                        value = DateHelper.format(value, config.dateFormat);
                    }
                    else if (typeof value === 'boolean') {
                        value = new BooleanUnicodeSymbol(value);
                    }

                    if (me.convertEmptyValueToEmptyString) {
                        value = me.processValue(value);
                    }

                    // when number column is exported with zipcelx, excel warns that sheet is broken and asks for repair
                    // repair works, but having error on open doesn't look acceptable
                    // const type = columns[index].type === 'number' ? 'number' : 'string';
                    const type = 'string';

                    return { value, type };
                });
            }),
            columns : columns.map(col => {
                let { field, value, width, type } = col;

                // when number column is exported with zipcelx, excel warns that sheet is broken and asks for repair
                // repair works, but having error on open doesn't look acceptable
                // type = type === 'number' ? 'number' : 'string';
                type = 'string';

                return { field, value, width, type };
            })
        };
    }

    /**
     * Generate and download a .xslx file.
     * @param {Object} config Optional configuration object, which overrides initial settings of the feature/exporter.
     */
    export(config = {}) {
        const me = this;

        if (!me.zipcelx) {
            throw new Error('ExcelExporter: "zipcelx" library is required');
        }

        if (me.disabled) {
            return;
        }

        config = ObjectHelper.assign({}, me.config, config);
        
        if (!config.filename) {
            config.filename = me.client.$name;
        }

        const
            { filename }      = config,
            { rows, columns } = me.generateExportData(config);

        me.zipcelx({
            filename,
            sheet : {
                data : [columns].concat(rows),
                cols : columns
            }
        });
    }

    construct(grid, config) {
        super.construct(grid, config);

        if (!this.zipcelx) {
            if (typeof zipcelx !== 'undefined') {
                this.zipcelx = zipcelx;
            }
        }
    }

    get exporter() {
        return this._exporter || (this._exporter = new this.exporterClass(Object.assign({ target : this.client }, this.exporterConfig)));
    }
}

GridFeatureManager.registerFeature(ExcelExporter, false, 'Grid');
