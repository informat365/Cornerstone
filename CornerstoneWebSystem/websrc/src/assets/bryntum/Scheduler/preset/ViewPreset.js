import Model from '../../Core/data/Model.js';
import IdHelper from '../../Core/helper/IdHelper.js';

// eslint-disable-next-line import/no-named-default
import { default as DH } from '../../Core/helper/DateHelper.js';
import StringHelper from '../../Core/helper/StringHelper.js';

/**
 * @module Scheduler/preset/ViewPreset
 */

/**
 * A ViewPreset describes the granularity of the timeline view of a {@link Scheduler.view.Scheduler}
 * and the layout and subdivisions of the timeline header.
 *
 * A sample preset looks like:
 * ```javascript
 * hourAndDay : {
 *   tickWidth           : 60,       // Time column width in horizontal mode
 *   tickHeight          : 30,       // Time column height in vertical mode
 *
 *   displayDateFormat   : 'HH:mm',  // Controls how dates will be displayed in tooltips etc
 *
 *   shiftIncrement      : 1,        // Controls how much time to skip when calling shiftNext and shiftPrevious.
 *   shiftUnit           : 'day',    // Valid values are 'millisecond', 'second', 'minute', 'hour', 'day', 'week', 'month', 'quarter', 'year'.
 *   defaultSpan         : 12,       // By default, if no end date is supplied to a view it will show 12 hours
 *
 *   timeResolution      : {         // Dates will be snapped to this resolution
 *       unit        : 'minute',     // Valid values are 'millisecond', 'second', 'minute', 'hour', 'day', 'week', 'month', 'quarter', 'year'.
 *       increment   : 15
 *   },
 *
 *   headers             : [         // This defines your header rows from top to bottom
 *       {                           // For each row you can define 'unit', 'increment', 'dateFormat', 'renderer', 'align', and 'thisObj'
 *         unit        : 'day',
 *         dateFormat  : 'ddd DD/MM'
 *       },
 *       {
 *         unit        : 'hour',
 *         dateFormat  : 'HH:mm'
 *       }
 *   },
 *
 *   columnLinesFor      : 1        // Defines header level column lines will be drawn for. Defaults to the last level.
 * }
 * ```
 * See the {@link Scheduler.preset.PresetManager} for the list of base presets. You may add your own
 * presets to this list, or add them on an individual basis to Scheduler instances.
 *
 * ## Duration units
 *
 * There are a number of configs for duration units ({@link #field-shiftUnit} or `unit` in {@link #field-timeResolution}, {@link #field-headers} configs).
 *
 * When registering a preset using {@link Scheduler.preset.PresetManager#function-registerPreset} method you can provide their values using short constants names ('day', 'week' etc.):
 * ```javascript
 * PresetManager.registerPreset('hour', {
 *   displayDateFormat : 'HH:mm',
 *   shiftIncrement    : 1,
 *   shiftUnit         : 'day',
 *   tickWidth         : 150,
 *   timeResolution    : {
 *     unit      : 'minute',
 *     increment : 5
 *   },
 *   headers      : [
 *     {
 *       unit       : 'day',
 *       dateFormat : 'ddd DD/MM'
 *     },
 *     {
 *       unit       : 'hour',
 *       dateFormat : 'HH:mm'
 *     }
 *   ]
 * });
 * ```
 * @extends Core/data/Model
 */
export default class ViewPreset extends Model {
    static get fields() {
        return [
            /**
             * The height of the row in horizontal orientation
             * @field {Number} rowHeight
             * @default
             */
            {
                name         : 'rowHeight',
                defaultValue : 24
            },

            /**
             * The width of the time tick column in horizontal orientation
             * @field {Number} tickWidth
             * @default
             */
            {
                name         : 'tickWidth',
                defaultValue : 50
            },

            /**
             * The height of the time tick column in vertical orientation
             * @field {Number} tickHeight
             * @default
             */
            {
                name         : 'tickHeight',
                defaultValue : 50
            },

            /**
             * Defines how dates will be formatted in tooltips etc
             * @field {String} displayDateFormat
             * @default
             */
            {
                name         : 'displayDateFormat',
                defaultValue : 'HH:mm'
            },

            /**
             * The unit to shift when calling shiftNext/shiftPrevious to navigate in the chart.
             * Valid values are "millisecond", "second", "minute", "hour", "day", "week", "month", "quarter", "year".
             * @field {String} shiftUnit
             * @default
             */
            {
                name         : 'shiftUnit',
                defaultValue : 'hour'
            },

            /**
             * The amount to shift (in shiftUnits)
             * @field {Number} shiftIncrement
             * @default
             */
            {
                name         : 'shiftIncrement',
                defaultValue : 1
            },

            /**
             * The amount of time to show by default in a view (in the unit defined by the middle header)
             * @field {Number} defaultSpan
             * @default
             */
            {
                name         : 'defaultSpan',
                defaultValue : 12
            },

            /**
             * An object containing a unit identifier and an increment variable. Example:
             * ```javascript
             * timeResolution : {
             *   unit      : "hour",  //Valid values are "millisecond", "second", "minute", "hour", "day", "week", "month", "quarter", "year".
             *   increment : 1
             * }
             * ```
             * @field {Object} timeResolution
             */
            'timeResolution',

            /**
             * An array containing one or more {@link Scheduler.preset.ViewPresetHeaderRow} config objects, each of which defines a level of headers for the scheduler.
             * The 'main' unit will be the last header's unit, but this can be changed using the `mainHeaderLevel` field.
             * @field {Object} headers
             */
            'headers',

            /**
             * Defines which {@link #field-headers header} level defines the 'main' header. Defaults to the bottom header.
             * @field {Number} mainHeaderLevel
             */
            'mainHeaderLevel',

            /**
             * Defines which {@link #field-headers header} level the column lines will be drawn for. See {@link Scheduler.feature.ColumnLines}.
             * Defaults to the bottom header.
             * @field {Number} columnLinesFor
             */
            'columnLinesFor'
        ];
    }

    construct(data) {
        // Process legacy columnLines config into a headers array.
        if (!data.headers) {
            this.normalizeHeaderConfig(data);
        }

        super.construct(...arguments);
        this.normalizeUnits();
    }

    generateId(owner) {
        const
            me          = this,
            {
                headers
            }           = me,
            parts       = [];

        // If we were subclassed from a base, use that id as the basis of oure.
        let result = Object.getPrototypeOf(me.data).id;

        if (!result) {
            for (let { length } = headers, i = length - 1; i >= 0; i--) {
                parts.push(i ? headers[i].unit : StringHelper.capitalizeFirstLetter(headers[i].unit));
            }

            // Use upwards header units at so eg "monthAndYear"
            result = parts.join('And');
        }

        // If duplicate, make it "hourAndDay-50by80"
        if (owner.includes(result)) {
            result += `-${me.tickWidth}by${me.tickHeight || me.tickWidth}`;
            // If still duplicate use increment
            if (owner.includes(result)) {
                result += `-${me.bottomHeader.increment}`;
                // And if STILL duplicate, make it unique with a suffix
                if (owner.includes(result)) {
                    result = IdHelper.generateId(`${result}-`);
                }
            }
        }

        return result;
    }

    normalizeUnits() {
        const
            me                          = this,
            { timeResolution, headers } = me;

        // Make sure date "unit" constant specified in the preset are resolved
        for (let i = 0, { length } = headers; i < length; i++) {
            const header = headers[i];

            header.unit = DH.normalizeUnit(header.unit);
            if (header.splitUnit) {
                header.splitUnit = DH.normalizeUnit(header.splitUnit);
            }
            if (!('increment' in header)) {
                headers[i] = Object.assign({
                    increment : 1
                }, header);
            }
        }

        if (timeResolution) {
            timeResolution.unit = DH.normalizeUnit(timeResolution.unit);
        }

        if (me.shiftUnit) {
            me.shiftUnit = DH.normalizeUnit(me.shiftUnit);
        }
    }

    // Process legacy columnLines config into a headers array.
    normalizeHeaderConfig(data) {
        const
            { headerConfig, columnLinesFor } = data,
            headers = data.headers           = [];

        if (headerConfig.top) {
            if (columnLinesFor == 'top') {
                data.columnLinesFor = 0;
            }
            headers[0] = headerConfig.top;
        }
        if (headerConfig.middle) {
            if (columnLinesFor == 'middle') {
                data.columnLinesFor = headers.length;
            }
            headers.push(headerConfig.middle);
        }
        else {
            throw new Error('ViewPreset.headerConfig must be configured with a middle');
        }
        if (headerConfig.bottom) {
            // Main level is middle when using headerConfig object.
            data.mainHeaderLevel = headers.length - 1;

            // There *must* be a middle above this bottom header
            // so that is the columnLines one by default.
            if (columnLinesFor == null) {
                data.columnLinesFor = headers.length - 1;
            }
            else if (columnLinesFor == 'bottom') {
                data.columnLinesFor = headers.length;
            }

            headers.push(headerConfig.bottom);
        }
    }

    // These are read-only once configured.
    set() {}
    inSet() {}

    get columnLinesFor() {
        return ('columnLinesFor' in this.data) ? this.data.columnLinesFor : this.headers.length - 1;
    }

    get tickSize() {
        return this._tickSize || this.tickWidth;
    }

    get tickWidth() {
        return ('tickWidth' in this.data) ? this.data.tickWidth : 50;
    }

    get tickHeight() {
        return ('tickHeight' in this.data) ? this.data.tickHeight : 50;
    }

    get headerConfig() {
        // Configured in the legacy manner, just return the configured value.
        if (this.data.headerConfig) {
            return this.data.headerConfig;
        }

        // Rebuild the object based upon the configured headers array.
        const
            result = {},
            { headers } = this,
            { length } = headers;

        switch (length) {
            case 1 :
                result.middle = headers[0];
                break;
            case 2:
                if (this.mainHeaderLevel === 0) {
                    result.middle = headers[0];
                    result.bottom = headers[1];
                }
                else {
                    result.top = headers[0];
                    result.middle = headers[1];
                }
                break;
            case 3:
                result.top = headers[0];
                result.middle = headers[1];
                result.bottom = headers[2];
                break;
            default:
                throw new Error('headerConfig object not supported for >3 header levels');
        }

        return result;
    }

    set mainHeaderLevel(mainHeaderLevel) {
        this.data.mainHeaderLevel = mainHeaderLevel;
    }

    get mainHeaderLevel() {
        if ('mainHeaderLevel' in this.data) {
            return this.data.mainHeaderLevel;
        }

        // 3 headers, then it's the middle
        if (this.data.headers.length === 3) {
            return 1;
        };
         
        // Assume it goes top, middle.
        // If it's middle, top, use mainHeaderLevel : 0
        return this.headers.length - 1;
    }

    get mainHeader() {
        return this.headers[this.mainHeaderLevel];
    }

    get bottomHeader() {
        return this.headers[this.headers.length - 1];
    }

    get leafUnit() {
        return this.bottomHeader.unit;
    }

    get mainUnit() {
        return this.mainHeader;
    }

    get msPerPixel() {
        const { bottomHeader } = this;

        return Math.round(DH.asMilliseconds(bottomHeader.increment || 1, bottomHeader.unit) / this.tickWidth);
    }

    get isValid() {
        const me = this;

        let valid = true;

        // Make sure all date "unit" constants are valid
        for (const header of me.headers) {
            valid = valid && Boolean(DH.normalizeUnit(header.unit));
        }

        if (me.timeResolution) {
            valid = valid && DH.normalizeUnit(me.timeResolution.unit);
        }

        if (me.shiftUnit) {
            valid = valid && DH.normalizeUnit(me.shiftUnit);
        }

        return valid;
    }
}
