import DateHelper from '../../Core/helper/DateHelper.js';
import ObjectHelper from '../../Core/helper/ObjectHelper.js';
import ViewPreset from './ViewPreset.js';
import PresetStore from './PresetStore.js';

// No module tag here. That stops the singleton from being included by the docs.

/**
 * Provides a registry of the possible view presets that any instance of Scheduler can use.
 *
 * See the {@link Scheduler.preset.ViewPreset} and {@link Scheduler.preset.ViewPresetHeaderRow} classes for a description of the view preset properties.
 *
 * Predefined presets are:
 *
 * - `secondAndMinute` - creates 2 level header - minute and seconds within it:
 * {@inlineexample scheduler/viewpresets/secondAndMinute.js}
 * - `minuteAndHour` - creates 2 level header - hour and minutes within it:
 * {@inlineexample scheduler/viewpresets/minuteAndHour.js}
 * - `hourAndDay` - creates 2 level header - day and hours within it:
 * {@inlineexample scheduler/viewpresets/hourAndDay.js}
 * - `dayAndWeek` - creates 2 level header - week and days within it:
 * {@inlineexample scheduler/viewpresets/dayAndWeek.js}
 * - `weekAndDay` - just like `dayAndWeek` but with different formatting:
 * {@inlineexample scheduler/viewpresets/weekAndDay.js}
 * - `weekAndDayLetter` - creates 2 level header - with weeks and day letters within it:
 * {@inlineexample scheduler/viewpresets/weekAndDayLetter.js}
 * - `weekAndMonth` - creates 2 level header - month and weeks within it:
 * {@inlineexample scheduler/viewpresets/weekAndMonth.js}
 * - `weekDateAndMonth` - creates 2 level header - month and weeks within it (weeks shown by first day only):
 * {@inlineexample scheduler/viewpresets/weekDateAndMonth.js}
 * - `monthAndYear` - creates 2 level header - year and months within it:
 * {@inlineexample scheduler/viewpresets/monthAndYear.js}
 * - `year` - creates 2 level header - year and quarters within it:
 * {@inlineexample scheduler/viewpresets/year.js}
 * - `manyYears` - creates 2 level header - 5-years and year within it:
 * {@inlineexample scheduler/viewpresets/manyYears.js}
 *
 * You can register your own preset with the {@link Scheduler/preset/PresetManager#function-registerPreset} call or pass a preset configuration in the scheduler panel.
 * @singleton
 * @extends Scheduler/preset/PresetStore
 */
class PresetManager extends PresetStore {
    static get defaultConfig() {
        return {
            // To not break CSP demo
            preventSubClassingModel : true,

            basePresets : {
                secondAndMinute : {
                    name              : 'Seconds',
                    tickWidth         : 30,   // Time column width
                    tickHeight        : 40,
                    displayDateFormat : 'll LTS', // Controls how dates will be displayed in tooltips etc
                    shiftIncrement    : 10,     // Controls how much time to skip when calling shiftNext and shiftPrevious.
                    shiftUnit         : 'minute', // Valid values are "millisecond", "second", "minute", "hour", "day", "week", "month", "quarter", "year".
                    defaultSpan       : 24,    // By default, if no end date is supplied to a view it will show 24 hours
                    timeResolution    : {      // Dates will be snapped to this resolution
                        unit      : 'second',  // Valid values are "millisecond", "second", "minute", "hour", "day", "week", "month", "quarter", "year".
                        increment : 5
                    },
                    // This defines your header rows.
                    // For each row you can define "unit", "increment", "dateFormat", "renderer", "align", and "thisObj"
                    headers : [
                        {
                            unit       : 'minute',
                            dateFormat : 'llll'
                        },
                        {
                            unit       : 'second',
                            increment  : 10,
                            dateFormat : 'ss'
                        }
                    ]
                },
                minuteAndHour : {
                    name              : 'Minutes',
                    tickWidth         : 60,    // Time column width
                    tickHeight        : 60,
                    displayDateFormat : 'll LT', // Controls how dates will be displayed in tooltips etc
                    shiftIncrement    : 1,     // Controls how much time to skip when calling shiftNext and shiftPrevious.
                    shiftUnit         : 'hour', // Valid values are "MILLI", "SECOND", "minute", "HOUR", "DAY", "WEEK", "MONTH", "QUARTER", "YEAR".
                    defaultSpan       : 24,    // By default, if no end date is supplied to a view it will show 24 hours
                    timeResolution    : {      // Dates will be snapped to this resolution
                        unit      : 'minute',  // Valid values are "MILLI", "SECOND", "minute", "HOUR", "DAY", "WEEK", "MONTH", "QUARTER", "YEAR".
                        increment : 30
                    },
                    headers : [
                        {
                            unit       : 'hour',
                            dateFormat : 'ddd MM/DD, hA'
                        },
                        {
                            unit       : 'minute',
                            increment  : 30,
                            dateFormat : 'mm'
                        }
                    ]
                },
                hourAndDay : {
                    name              : 'Hours',
                    tickWidth         : 70,
                    tickHeight        : 40,
                    displayDateFormat : 'll LT',
                    shiftIncrement    : 1,
                    shiftUnit         : 'day',
                    defaultSpan       : 24,
                    timeResolution    : {
                        unit      : 'minute',
                        increment : 30
                    },
                    headers : [
                        {
                            unit       : 'day',
                            dateFormat : 'ddd DD/MM' //Mon 01/10
                        },
                        {
                            unit       : 'hour',
                            dateFormat : 'LT'
                        }
                    ]
                },
                day : {
                    name              : 'Hours 2',
                    displayDateFormat : 'LT',
                    shiftIncrement    : 1,
                    shiftUnit         : 'day',
                    defaultSpan       : 1,
                    timeResolution    : {
                        unit      : 'minute',
                        increment : 30
                    },
                    mainHeaderLevel : 0,
                    headers         : [
                        {
                            unit       : 'day',
                            dateFormat : 'ddd DD/MM', // Mon 01/02
                            splitUnit  : 'day'
                        },
                        {
                            unit : 'hour',
                            renderer(value) {
                                return `
                                    <div class="b-sch-calendarcolumn-ct"><span class="b-sch-calendarcolumn-hours">${DateHelper.format(value, 'HH')}</span>
                                    <span class="b-sch-calendarcolumn-minutes">${DateHelper.format(value, 'mm')}</span></div>
                                `;
                            }
                        }
                    ]
                },
                week : {
                    name              : 'Week/hours',
                    displayDateFormat : 'LT',
                    shiftIncrement    : 1,
                    shiftUnit         : 'week',
                    defaultSpan       : 24,
                    timeResolution    : {
                        unit      : 'minute',
                        increment : 30
                    },
                    mainHeaderLevel : 0,
                    headers         : [
                        {
                            unit       : 'week',
                            dateFormat : 'D d',
                            splitUnit  : 'day'
                        },
                        {
                            unit       : 'hour',
                            dateFormat : 'LT',    // will be overridden by renderer
                            renderer(value) {
                                return `
                                    <div class="sch-calendarcolumn-ct">
                                    <span class="sch-calendarcolumn-hours">${DateHelper.format(value, 'HH')}</span>
                                    <span class="sch-calendarcolumn-minutes">${DateHelper.format(value, 'mm')}</span>
                                    </div>
                                `;
                            }
                        }
                    ]
                },
                dayAndWeek : {
                    name              : 'Days 2',
                    tickWidth         : 100,
                    tickHeight        : 80,
                    displayDateFormat : 'll LT',
                    shiftUnit         : 'day',
                    shiftIncrement    : 1,
                    defaultSpan       : 5,
                    timeResolution    : {
                        unit      : 'hour',
                        increment : 1
                    },
                    headers : [
                        {
                            unit : 'week',
                            renderer(start) {
                                return DateHelper.getShortNameOfUnit('week') + '.' + DateHelper.format(start, 'WW MMM YYYY');
                            }
                        },
                        {
                            unit       : 'day',
                            dateFormat : 'dd DD'
                        }
                    ]
                },
                weekAndDay : {
                    name              : 'Days',
                    tickWidth         : 100,
                    tickHeight        : 80,
                    displayDateFormat : 'll hh:mm A',
                    shiftUnit         : 'week',
                    shiftIncrement    : 1,
                    defaultSpan       : 1,
                    timeResolution    : {
                        unit      : 'day',
                        increment : 1
                    },
                    mainHeaderLevel : 0,
                    headers         : [
                        {
                            unit       : 'week',
                            dateFormat : 'YYYY MMMM DD' // 2017 January 01
                        },
                        {
                            unit       : 'day',
                            increment  : 1,
                            dateFormat : 'DD MMM'
                        }
                    ]
                },
                weekAndMonth : {
                    name              : 'Weeks',
                    tickWidth         : 100,
                    tickHeight        : 105,
                    displayDateFormat : 'll',
                    shiftUnit         : 'week',
                    shiftIncrement    : 5,
                    defaultSpan       : 6,
                    timeResolution    : {
                        unit      : 'day',
                        increment : 1
                    },
                    headers : [
                        {
                            unit       : 'month',
                            dateFormat : 'MMM YYYY' //Jan 2017
                        },
                        {
                            unit       : 'week',
                            dateFormat : 'DD MMM'
                        }
                    ]
                },
                weekAndDayLetter : {
                    name              : 'Weeks 2',
                    tickWidth         : 20,
                    tickHeight        : 50,
                    displayDateFormat : 'll',
                    shiftUnit         : 'week',
                    shiftIncrement    : 1,
                    defaultSpan       : 10,
                    timeResolution    : {
                        unit      : 'day',
                        increment : 1
                    },
                    mainHeaderLevel : 0,
                    headers         : [
                        {
                            unit                : 'week',
                            dateFormat          : 'ddd DD MMM YYYY',
                            verticalColumnWidth : 115
                        },
                        {
                            unit                : 'day',
                            dateFormat          : 'd1',
                            verticalColumnWidth : 25
                        }
                    ]
                },
                weekDateAndMonth : {
                    name              : 'Weeks 3',
                    tickWidth         : 30,
                    tickHeight        : 40,
                    displayDateFormat : 'll',
                    shiftUnit         : 'week',
                    shiftIncrement    : 1,
                    defaultSpan       : 10,
                    timeResolution    : {
                        unit      : 'day',
                        increment : 1
                    },
                    headers : [
                        {
                            unit       : 'month',
                            dateFormat : 'YYYY MMMM'
                        },
                        {
                            unit       : 'week',
                            dateFormat : 'DD'
                        }
                    ]
                },
                monthAndYear : {
                    name              : 'Months',
                    tickWidth         : 110,
                    tickHeight        : 110,
                    displayDateFormat : 'll',
                    shiftIncrement    : 3,
                    shiftUnit         : 'month',
                    defaultSpan       : 12,
                    timeResolution    : {
                        unit      : 'day',
                        increment : 1
                    },
                    headers : [
                        {
                            unit       : 'year',
                            dateFormat : 'YYYY' //2017
                        },
                        {
                            unit       : 'month',
                            dateFormat : 'MMM YYYY' //Jan 2017
                        }
                    ]
                },
                year : {
                    name                : 'Years',
                    tickWidth           : 100,
                    tickHeight          : 100,
                    resourceColumnWidth : 100,
                    displayDateFormat   : 'll',
                    shiftUnit           : 'year',
                    shiftIncrement      : 1,
                    defaultSpan         : 1,
                    timeResolution      : {
                        unit      : 'month',
                        increment : 1
                    },
                    headers : [
                        {
                            unit       : 'year',
                            dateFormat : 'YYYY'
                        },
                        {
                            unit : 'quarter',
                            renderer(start, end, cfg) {
                                return DateHelper.getShortNameOfUnit('quarter').toUpperCase() + (Math.floor(start.getMonth() / 3) + 1);
                            }
                        }
                    ]
                },
                manyYears : {
                    name              : 'Years 2',
                    tickWidth         : 40,
                    tickHeight        : 50,
                    displayDateFormat : 'll',
                    shiftUnit         : 'year',
                    shiftIncrement    : 1,
                    defaultSpan       : 1,
                    timeResolution    : {
                        unit      : 'year',
                        increment : 1
                    },
                    mainHeaderLevel : 0,
                    headers         : [
                        {
                            unit       : 'year',
                            dateFormat : 'YYYY',
                            increment  : 5
                        },
                        {
                            unit       : 'year',
                            dateFormat : 'YY',
                            increment  : 1
                        }
                    ]
                }
            },

            // This is a list of bryntum-supplied preset adjustments used to create the Scheduler's
            // default initial set of ViewPresets.
            defaultPresets : [
                // Years over years
                'manyYears',
                { width : 80, increment : 1, resolution : 1, preset : 'manyYears', resolutionUnit : 'YEAR' },

                // Years over quarters
                'year',
                { width : 30,  increment : 1, resolution : 1, preset : 'year', resolutionUnit : 'MONTH' },
                { width : 50,  increment : 1, resolution : 1, preset : 'year', resolutionUnit : 'MONTH' },
                { width : 200, increment : 1, resolution : 1, preset : 'year', resolutionUnit : 'MONTH' },

                // Years over months
                'monthAndYear',

                // Months over weeks
                'weekDateAndMonth',

                // Months over weeks
                'weekAndMonth',

                // Months over weeks
                'weekAndDayLetter',

                // Weeks over days
                'weekAndDay',
                { width : 54, increment : 1, resolution : 1, preset : 'weekAndDay', resolutionUnit : 'HOUR' },

                // Days over hours
                'hourAndDay',
                { width : 64,  increment : 6, resolution : 30, preset : 'hourAndDay', resolutionUnit : 'MINUTE' },
                { width : 100, increment : 6, resolution : 30, preset : 'hourAndDay', resolutionUnit : 'MINUTE' },
                { width : 64,  increment : 2, resolution : 30, preset : 'hourAndDay', resolutionUnit : 'MINUTE' },

                // Hours over minutes
                'minuteAndHour',
                { width : 30,  increment : 15, resolution : 5, preset : 'minuteAndHour' },
                { width : 130, increment : 15, resolution : 5, preset : 'minuteAndHour' },
                { width : 60,  increment : 5,  resolution : 5, preset : 'minuteAndHour' },
                { width : 100, increment : 5,  resolution : 5, preset : 'minuteAndHour' },

                // Minutes over seconds
                'secondAndMinute',
                { width : 60,  increment : 10, resolution : 5, preset : 'secondAndMinute' },
                { width : 130, increment : 5,  resolution : 5, preset : 'secondAndMinute' }
            ],

            listeners : {
                locale : 'updateLocalization'
            }
        };
    }

    set basePresets(basePresets) {
        const presetCache = this._basePresets = {};

        for (const id in basePresets) {
            basePresets[id].id = id;
            presetCache[id] = this.createRecord(basePresets[id]);
        }
    }

    get basePresets() {
        return this._basePresets;
    }

    set defaultPresets(defaultPresets) {
        for (let i = 0, { length } = defaultPresets; i < length; i++) {
            const
                presetAdjustment   = defaultPresets[i],
                isBase             = typeof presetAdjustment === 'string',
                baseType           = isBase ? presetAdjustment : presetAdjustment.preset;

            let preset;

            // The default was just a string, so it's an unmodified instance of a base type.
            if (isBase) {
                preset = this.basePresets[baseType];
            }
            // If it's an object, it's an adjustment to a base tyope
            else {
                const
                    config             = Object.setPrototypeOf(ObjectHelper.clone(this.basePresets[baseType].data), { id : baseType }),
                    { timeResolution } = config,
                    bottomHeader       = config.headers[config.headers.length - 1];

                config.id = undefined;
                if ('width' in presetAdjustment) {
                    config.tickWidth = presetAdjustment.width;
                }
                if ('height' in presetAdjustment) {
                    config.tickHeight = presetAdjustment.height;
                }
                if ('increment' in presetAdjustment) {
                    bottomHeader.increment = presetAdjustment.increment;
                }
                if ('resolution' in presetAdjustment) {
                    timeResolution.increment = presetAdjustment.resolution;
                }
                if ('resolutionUnit' in presetAdjustment) {
                    timeResolution.unit = DateHelper.getUnitByName(presetAdjustment.resolutionUnit);
                }

                preset = this.createRecord(config);
            }
            this.add(preset);
        }
    }

    getById(id) {
        // Look first in the default set, and if it's one of the base types that is not imported into the
        // default set, then look at the bases.
        return super.getById(id) || this.basePresets[id];
    }

    /**
     * Registers a new view preset base to be used by any scheduler grid or tree on the page.
     * @param {String} id The unique identifier for this preset
     * @param {Object} config The configuration properties of the view preset (see {@link Scheduler.preset.ViewPreset} for more information)
     * @returns {Scheduler.preset.ViewPreset} A new ViewPreset based upon the passed configuration.
     */
    registerPreset(id, config) {
        const
            preset = this.createRecord(Object.assign({
                id
            }, config)),
            existingDuplicate = this.find(p => p.equals(preset));

        if (existingDuplicate) {
            return existingDuplicate;
        }

        if (preset.isValid) {
            this.add(preset);
        }
        else {
            throw new Error('Invalid preset, please check your configuration');
        }

        return preset;
    }

    getPreset(preset) {
        if (typeof preset === 'number') {
            preset = this.getAt(preset);
        }
        if (typeof preset === 'string') {
            preset = this.getById(preset);
        }
        else if (!(preset instanceof ViewPreset)) {
            preset = this.createRecord(preset);
        }
        return preset;
    }

    /**
     * Applies preset customizations or fetches a preset view preset using its name.
     * @param {String|Object} presetOrId Id of a predefined preset or a preset config object
     * @returns {Scheduler.preset.ViewPreset} Resulting ViewPreset instance
     */
    normalizePreset(preset) {
        let me = this;

        if (!(preset instanceof ViewPreset)) {
            if (typeof preset === 'string') {
                preset = me.getPreset(preset);
                if (!preset) {
                    throw new Error('You must define a valid view preset. See PresetManager for reference');
                }
            }
            else if (typeof preset === 'object') {
                // Look up any existing ViewPreset that it is based upon.
                if (preset.base) {
                    const base = this.getById(preset.base);

                    if (!base) {
                        throw new Error(`ViewPreset base '${preset.base}' does not exist`);
                    }
                    // The config is based upon the base's data with the new config object merged in.
                    preset = ObjectHelper.merge(ObjectHelper.clone(base.data), preset);
                }

                // Ensure the new ViewPreset has a legible, logical id which does not already
                // exxist in our store.
                if (preset.id) {
                    preset = me.createRecord(preset);
                }
                else {
                    preset = me.createRecord(ObjectHelper.assign({}, preset));
                    preset.id = preset.generateId(preset);
                }
            }
        }

        return preset;
    }

    /**
     * Deletes a view preset
     * @param {String} id The id of the preset, or the preset instance.
     */
    deletePreset(presetOrId) {
        if (typeof presetOrId === 'string') {
            presetOrId = this.getById(presetOrId);
        }
        else if (typeof presetOrId === 'number') {
            presetOrId = this.getAt(presetOrId);
        }

        if (presetOrId) {
            this.remove(presetOrId);

            // ALso remove it from our base list
            delete this.basePresets[presetOrId.id];
        }
    }
}

const pm = new PresetManager();

export { pm as default };
