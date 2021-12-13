import Localizable from '../../Core/localization/Localizable.js';
import GridTableExporter from '../../Grid/util/TableExporter.js';
import Scheduler from '../view/Scheduler.js';

/**
 * @module Scheduler/util/ScheduleTableExporter
 */

/**
 * This class transforms scheduler component into two arrays: rows and columns. Columns array contains objects with
 * meta information about column: field name, column name, width and type of the rendered value, rows array contains
 * arrays of cell values.
 *
 * ```javascript
 * const exporter = new ScheduleTableExporter({ target : scheduler });
 * exporter.export()
 *
 * // Output
 * {
 *     columns : [
 *         { field : 'name',      value : 'First name', type : 'string',  width : 100 },
 *         { field : 'name',      value : 'Task',       type : 'string',  width : 100, eventColumn : true },
 *         { field : 'startDate', value : 'Starts',     type : 'date',    width : 100, eventColumn : true },
 *         { field : 'endDate',   value : 'Ends',       type : 'date',    width : 100, eventColumn : true }
 *     ],
 *     rows : [
 *         ['Michael', 'Hand out dundies',      Date, Date],
 *         ['Michael', 'Buy condo',             Date, Date],
 *         ['Jim',     'Close sale to library', Date, Date]
 *     ]
 * }
 * ```
 *
 * ## How data is exported
 *
 * Data is exported as in the base class with minor addition: every event is exported on a separate row, like
 * demonstrated above.
 *
 * In case there are unassigned events, by default they will be exported as well
 *
 * ```javascript
 * // output
 * {
 *     rows : [
 *         ['Michael', 'Hand out dundies',      Date, Date],
 *         ['Michael', 'Buy condo',             Date, Date],
 *         ['Jim',     'Close sale to library', Date, Date],
 *         ['',        'No resource assigned'],
 *         ['',        'Halloween prep',        Date, Date],
 *         ['',        'New year prep',         Date, Date]
 *     ]
 * }
 * ```
 *
 * @extends Grid/util/TableExporter
 * @mixes Core/localization/Localizable
 */
export default class ScheduleTableExporter extends Localizable(GridTableExporter) {
    static get defaultConfig() {
        return {
            /**
             * Set to `false` to not include unassigned events in the export. `true` by default.
             * @config {Boolean} includeUnassigned
             * @default
             */
            includeUnassigned : true,

            /**
             * An array of Event columns configuration used to specify columns width, headers name, and column fields to get the data from.
             * 'field' config is required. If 'text' is missing, the 'field' config will be used instead.
             *
             * For example:
             * ```javascript
             * eventColumns    : [
             *     { text : 'Task', field : 'name' },
             *     { text : 'Starts', field : 'startDate', width : 140 },
             *     { text : 'Ends', field : 'endDate', width : 140 }
             * ]
             * ```
             *
             * @config {String[]|Object[]} eventColumns
             * @default
             */
            eventColumns : [
                { text : 'Task', field : 'name' },
                { text : 'Starts', field : 'startDate', width : 140 },
                { text : 'Ends', field : 'endDate', width : 140 }
            ]
        };
    }

    normalizeColumns(config) {
        super.normalizeColumns(config);

        config.eventColumns = config.eventColumns.map(col => {
            if (typeof col === 'string') {
                return { field : col };
            }
            else {
                return col;
            }
        });
    }

    generateExportData(config) {
        const
            me                 = this,
            isScheduler        = this.target instanceof Scheduler,
            resourceColumns    = me.generateColumns(config),
            eventColumns       = isScheduler ? me.generateEventColumns(config) : [],
            columns            = resourceColumns.concat(eventColumns),
            rows               = me.generateRows(config);

        return { columns, rows };
    }

    generateEventColumns(config) {
        return config.eventColumns.map(column => this.processEventColumn(column, config));
    }

    processEventColumn(column, config) {
        //<debug>
        if (!column.field) {
            // Without field on scheduler column we have nothing to export
            console.warn('ExcelExporter: "field" config is required for event columns');
        }
        //</debug>

        const
            { width, minWidth }    = column,
            { defaultColumnWidth } = config;

        return {
            field       : column.field,
            value       : column.text,
            width       : Math.max(width || defaultColumnWidth, minWidth || defaultColumnWidth),
            eventColumn : true,
            type        : this.getColumnType(column, this.target.eventStore)
        };
    }

    generateRows(config) {
        const
            me         = this,
            { target } = me;

        let result;

        if (!(target instanceof Scheduler)) {
            result = super.generateRows(config);
        }
        else {
            result = [];

            // forEach skips group records, summary records etc
            target.resourceStore.map(resourceRecord => {
                // Get all events for resource (including assignment store)
                const events = resourceRecord.events || [];

                // Set dummy event to have resource info printed without events
                if (!events.length) {
                    events.push('');
                }

                events.forEach(eventRecord => result.push(me.getRowData(config, resourceRecord, eventRecord)));
            });

            if (config.includeUnassigned && config.eventColumns.length) {
                const notAssignedEvents = target.eventStore.query(eventRecord => {
                    return !eventRecord.resources.length &&
                        // this extra check is needed until eventRecord.resources skips grouped and collapsed resources
                        // checked by ExcelExport.t.js when it can be removed
                        !target.resourceStore.isAvailable(eventRecord);
                });

                if (notAssignedEvents.length) {
                    // Use offset to match first event column
                    const cells = new Array(config.columns.length).fill('');

                    cells.push(me.L('No resource assigned'));

                    result.push(cells);

                    // Set dummy resource to have event info printed without resource
                    notAssignedEvents.forEach(eventRecord => result.push(me.getRowData(config, null, eventRecord)));
                }
            }

            // filter out empty rows
            result = result.filter(cells => cells.length);
        }

        return result;
    }

    getRowData(config, resource, event) {
        const
            {
                columns,
                eventColumns
            }     = config,
            cells = [];

        cells.push(...this.processRecord(resource, columns, config));

        if (!resource || !resource.meta.specialRow) {
            cells.push(...this.processRecord(event, eventColumns, config));
        }

        return cells;
    }
}
