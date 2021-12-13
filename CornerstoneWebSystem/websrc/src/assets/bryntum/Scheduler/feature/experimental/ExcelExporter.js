/*eslint no-undef: "error"*/
import GridFeatureManager from '../../../Grid/feature/GridFeatureManager.js';
import GridExcelExporter from '../../../Grid/feature/experimental/ExcelExporter.js';
import ScheduleTableExporter from '../../util/ScheduleTableExporter.js';

/**
 * @module Scheduler/feature/experimental/ExcelExporter
 */

/**
 * **NOTE**: This class requires a 3rd party library to operate.
 *
 * A plugin that allows exporting Scheduler data to Excel without involving the server. It uses {@link Scheduler.util.ScheduleTableExporter}
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
 * const scheduler = new Scheduler({
 *     features : {
 *         excelExporter : {
 *             // Choose the date format for date fields
 *             dateFormat : 'YYYY-MM-DD HH:mm',
 *
 *             // Choose the Resource fields to include in the exported file
 *             columns : [{ text : 'Staff', field : 'name' }],
 *
 *             // Choose the Event fields to include in the exported file
 *             eventColumns    : [
 *                 { text : 'Task', field : 'name' },
 *                 { text : 'Starts', field : 'startDate', width : 140 },
 *                 { text : 'Ends', field : 'endDate', width : 140 }
 *             ]
 *         }
 *     }
 * });
 * ```
 *
 * And how to call it:
 *
 * ```javascript
 * scheduler.features.excelExporter.export({
 *     filename : 'Export'
 * })
 * ```
 *
 * @extends Grid/feature/experimental/ExcelExporter
 * @demo Scheduler/exporttoexcel
 * @typings Grid/feature/experimental/ExcelExporter -> Grid/feature/experimental/GridExcelExporter
 */
export default class ExcelExporter extends GridExcelExporter {
    static get $name() {
        return 'ExcelExporter';
    }

    static get defaultConfig() {
        return {
            /**
             * Exporter class. Must subclass {@link Scheduler.util.ScheduleTableExporter}
             * @config {Scheduler.util.ScheduleTableExporter}
             * @default
             */
            exporterClass : ScheduleTableExporter,

            /**
             * Configuration object for {@link #config-exporterClass exporter class}.
             * @config {Object}
             */
            exporterConfig : null
        };
    }
}

GridFeatureManager.registerFeature(ExcelExporter, false, 'Scheduler');
