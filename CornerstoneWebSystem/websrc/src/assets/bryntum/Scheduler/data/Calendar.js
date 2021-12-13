import AjaxStore from '../../Core/data/AjaxStore.js';
import Store from '../../Core/data/Store.js';
import ArrayHelper from '../../Core/helper/ArrayHelper.js';
import DateHelper from '../../Core/helper/DateHelper.js';
import CalendarDayModel from '../model/CalendarDayModel.js';
import TimeSpan from '../model/TimeSpan.js';

/**
 * @module Scheduler/data/Calendar
 */

/**
 * A class representing a customizable calendar with weekends, holidays and availability information for any day.
 * Internally, it's just a subclass of the AjaxStore class which should be loaded with a collection
 * of {@link Scheduler.model.CalendarDayModel} instances. Additionally, calendars may have parent-child relations,
 * allowing "child" calendars to "inherit" all special dates from its "parent" and add its own.
 * See {@link #property-parent} property for details.
 *
 * A calendar can be instantiated like this:
 * ```
 * let calendar = new Scheduler.data.Calendar({
 *     data : [
 *         {
 *             date            : new Date(2010, 0, 13),
 *             cls             : 'national-holiday'
 *         },
 *         {
 *             date            : new Date(2010, 1, 1),
 *             cls             : 'company-holiday'
 *         },
 *         {
 *             date            : new Date(2010, 0, 16),
 *             isWorkingDay    : true
 *         }
 *     ]
 * });
 * ```
 * Please refer to the {@link Scheduler.model.CalendarDayModel} class to learn the data model used for the calendar.
 */
export default class Calendar extends AjaxStore {
    static get defaultConfig() {
        return {
            modelClass : CalendarDayModel,

            /**
             * Number of days per month. Will be used when converting the big duration units like month/year to days.
             *
             * @config {Number}
             * @default
             */
            daysPerMonth : 30,

            /**
             * Number of days per week. Will be used when converting the duration in weeks to days.
             *
             * @config {Number}
             * @default
             */
            daysPerWeek : 7,

            /**
             * Number of hours per day. Will be used when converting the duration in days to hours.
             *
             * **Please note**, that this config is used for duration conversion and not anything else. If you need to change
             * the number of working hours in the day, update the {@link #config-defaultAvailability}
             *
             * @config {Number}
             * @default
             */
            hoursPerDay : 24,

            unitsInMs : null,

            defaultNonWorkingTimeCssCls : 'b-nonworkingtime',

            /**
             * Setting this option to `true` will treat *all* days as working days. Default value is `false`.
             * @config {Boolean}
             * @default
             */
            weekendsAreWorkdays : false,

            /**
             * The index of the first day in a weekend, 0 for Sunday, 1 for Monday, 2 for Tuesday, and so on. '
             * Default value is 6 - Saturday
             * @config {Number}
             * @default
             */
            weekendFirstDay : 6,

            /**
             * The index of the second day in weekend, 0 for Sunday, 1 for Monday, 2 for Tuesday, and so on.
             * Default value is 0 - Sunday
             * @config {Number}
             * @default
             */
            weekendSecondDay : 0,

            holidaysCache              : null,
            availabilityIntervalsCache : null,
            daysIndex                  : null,

            // a "cached" array of WEEKDAY days
            weekAvailability : null,

            // the "very default" availability array, calculated based on `defaultAvailability` property
            defaultWeekAvailability : null,

            nonStandardWeeksByStartDate : null,
            nonStandardWeeksStartDates  : null,

            /**
             * The unique id for the calendar. Providing a `calendarId` will register this calendar in the calendars
             * registry and it can be retrieved later with {@link #function-getCalendar-static}. Generally only required if want to use
             * {@link #property-parent parent-child relations} between the calendars, or assign this calendar to a particular
             * task or resource.
             *
             * @config {String}
             */
            calendarId : null,

            /**
             * The parent calendar. Can be provided as the calendar id or calendar instance itself. If this property is
             * provided or set with {@link #property-parent} property, this calendar becomes a "child" of the specified
             * calendar. This means that it will "inherit" all day overrides, week days and week day overrides from its
             * "parent". In the same time, special days, defined in this calendar take priority over the ones from the
             * "parent".
             *
             * You can use this feature if you'd like to create a single "main" calendar for the whole project, and then
             * allow some task or resource to have slightly different calendar (with an additional day off for example).
             * You will not have to re-create all special days in the calendar of such task/resource - just set the
             * "main" calendar as a "parent" for it.
             *
             * @config {String|Scheduler.data.Calendar}
             */
            parent : null,

            /**
             * The array of default availability intervals (in the format of the
             * `Availability` field in the {@link Scheduler.model.CalendarDayModel}) for each working weekday (Monday-Friday). Defaults
             * to whole day (00-24) for backward compatibility.
             * @config {String[]}
             * @default
             */
            defaultAvailability : ['00:00-24:00'],

            /**
             * The name of this calendar
             * @config {String}
             */
            name : null,

            suspendCacheUpdate : 0,

            /**
             * Maximum number of days to search for calendar availability intervals.
             * Used in various calculations requiring to respect working time.
             * In these cases the system iterates through the working time day by day. This option determines the maximum distance
             * to iterate. Prevents against infinite loop in case of wrong calendar configuration.
             * @config {Number}
             * @default
             */
            availabilitySearchLimit : 1825 //5*365
        };
    }

    /**
     * Returns an array of all registered calendars.
     *
     * @return {Scheduler.data.Calendar[]}
     */
    static get allCalendars() {
        return (Store.stores || []).filter(store => store instanceof Calendar);
    }

    /**
     * Get/set the calendarId of the current calendar, also registers it in the calendar registry.
     * @property {String}
     */
    get calendarId() {
        return this._calendarId;
    }

    /**
     * Sets the {@link #property-parent} for this calendar. Pass `null` to remove the parent.
     *
     * @param {String|Scheduler.data.Calendar} parentOrId String with {@link #config-calendarId} value or calendar instance itself.
     */
    set parent(parentOrId) {
        const me     = this,
            parent = Calendar.getCalendar(parentOrId);

        if (parentOrId && !parent) throw new Error('Invalid parent specified for the calendar');

        if (me.parent != parent) {
            const listeners = {
                calendarchange : me.clearCache,
                destroy        : me.onParentDestroy,
                thisObj        : me
            };

            const oldParent = me.parent;

            if (oldParent) oldParent.un(listeners);

            me._parent = parent;

            if (parent) parent.on(listeners);

            me.params = Object.assign(me.params || {}, { parentId : parent ? parent.calendarId : null });

            me.clearCache();

            /**
             * Triggered when a calender is assigned to a new parent calendar.
             * @event parentChange
             *
             * @param {Scheduler.data.Calendar} source The calendar which parent has changed
             * @param {Scheduler.data.Calendar} newParent The new parent of this calendar (can be `null` if parent is being removed)
             * @param {Scheduler.data.Calendar} oldParent The old parent of this calendar (can be `null` if there was no parent)
             */
            me.trigger('parentChange', { newParent : parent, oldParent });
        }
    }

    construct(config) {
        const me = this;

        super.construct(config);

        // TODO: This will be from static get properties() when https://app.assembla.com/spaces/bryntum/tickets/5165 is done
        me.unitsInMs = {
            MILLI   : 1,
            SECOND  : 1000,
            MINUTE  : 60 * 1000,
            HOUR    : 60 * 60 * 1000,
            DAY     : me.hoursPerDay * 60 * 60 * 1000,
            WEEK    : me.daysPerWeek * me.hoursPerDay * 60 * 60 * 1000,
            MONTH   : me.daysPerMonth * me.hoursPerDay * 60 * 60 * 1000,
            QUARTER : 3 * me.daysPerMonth * 24 * 60 * 60 * 1000,
            YEAR    : 4 * 3 * me.daysPerMonth * 24 * 60 * 60 * 1000
        };

        me.defaultWeekAvailability = me.getDefaultWeekAvailability();

        // traditional "on-demand" caching seems to be not so efficient for calendar (in theory)
        // calculating any cached property, like, "weekAvailability" or "nonStandardWeeksStartDates" will require full calendar scan each time
        // so we update ALL cached values on any CRUD operations
        me.on({
            // TODO ignore changes of "name/cls" field?
            change  : me.clearCache,
            thisObj : me
        });

        me.clearCache();
    }

    /**
     * Returns the registered calendar with the given id.
     *
     * @param {String} id The calendar id
     * @return {Scheduler.data.Calendar}
     */
    static getCalendar(id) {
        if (id instanceof Calendar) return id;

        return Store.getStore(id);
    }

    set calendarId(id) {
        const me = this;

        me._calendarId = id;

        if (id != null) {
            me.storeId = 'GNT_CALENDAR:' + id;
        }
        else {
            me.storeId = null;
        }

        me.params = Object.assign(me.params || {}, { calendarId : id });
    }

    getDefaultWeekAvailability() {
        let availability     = this.defaultAvailability,
            weekendFirstDay  = this.weekendFirstDay,
            weekendSecondDay = this.weekendSecondDay,
            res              = [];

        for (let i = 0; i < 7; i++) {
            res.push(
                this.weekendsAreWorkdays || i != weekendFirstDay && i != weekendSecondDay
                    ? new this.modelClass({
                        type         : 'WEEKDAY',
                        weekday      : i,
                        availability : availability && availability.slice() || [],
                        isWorkingDay : true
                    })
                    :                    new this.modelClass({ type : 'WEEKDAY', weekday : i, availability : [] })
            );
        }

        return res;
    }

    /**
     * Destroys all registered calendars.
     *
     * @return {Scheduler.data.Calendar[]}
     */
    removeAll() {
        Calendar.allCalendars.forEach(calendar => {
            calendar.storeId = null; //unregisters from Store map
            calendar.destroy();
        });
    }

    /**
     * Returns `true` or `false` depending whether the given time span intersects with one of the defined week day overrides.
     *
     * @param {Date} startDate The start date of the time span
     * @param {Date} endDate The end date of the time span
     *
     * @return {Boolean}
     */
    intersectsWithCurrentWeeks(startDate, endDate) {
        let result = false;

        this.forEachNonStandardWeek(week => {
            const weekStartDate = week.startDate,
                weekEndDate   = week.endDate;

            if (weekStartDate <= startDate && startDate < weekEndDate || weekStartDate < endDate && endDate <= weekEndDate) {
                result = true;

                // stop the iteration
                return false;
            }
        });

        return result;
    }

    // will scan through all calendar days in the store and save references to special ones to the properties, for speedup
    clearCache() {
        const me = this;

        if (me.suspendCacheUpdate > 0) return;

        me.holidaysCache              = {};
        me.availabilityIntervalsCache = {};

        const daysIndex = me.daysIndex = {},
            weekAvailability = me.weekAvailability = [],
            nonStandardWeeksStartDates = me.nonStandardWeeksStartDates = [],
            nonStandardWeeksByStartDate = me.nonStandardWeeksByStartDate = {};

        me.forEach(function(calendarDay) {
            // backward compat
            let id            = calendarDay.id,
                overrideMatch = /^(\d)-(\d\d\d\d\/\d\d\/\d\d)-(\d\d\d\d\/\d\d\/\d\d)$/.exec(id),
                weekDayMatch  = /^WEEKDAY:(\d+)$/.exec(id),
                type          = calendarDay.type,
                weekDay       = calendarDay.weekday;

            if (type == 'WEEKDAYOVERRIDE' || overrideMatch) {
                let startDate, endDate;

                if (type == 'WEEKDAYOVERRIDE') {
                    startDate = calendarDay.overrideStartDate;
                    endDate   = calendarDay.overrideEndDate;
                }

                // backward compat
                if (overrideMatch) {
                    startDate = DateHelper.parse(overrideMatch[2], 'YYYY/MM/DD');
                    endDate   = DateHelper.parse(overrideMatch[3], 'YYYY/MM/DD');
                    weekDay   = overrideMatch[1];
                }

                // allow partially defined days - they will not be included in calculations
                if (startDate && endDate && weekDay != null) {
                    const startDateNum = startDate - 0;

                    if (!nonStandardWeeksByStartDate[startDateNum]) {
                        nonStandardWeeksByStartDate[startDateNum] = {
                            startDate        : new Date(startDate),
                            endDate          : new Date(endDate),
                            name             : calendarDay.getName(),
                            weekAvailability : [],
                            // main day representing the week override itself - for example for overrides w/o any re-defined availability
                            mainDay          : null
                        };

                        nonStandardWeeksStartDates.push(startDateNum);
                    }

                    if (weekDay >= 0) {
                        nonStandardWeeksByStartDate[startDateNum].weekAvailability[weekDay] = calendarDay;
                    }
                    else {
                        nonStandardWeeksByStartDate[startDateNum].mainDay = calendarDay;
                    }
                }
            }
            else if (type == 'WEEKDAY' || weekDayMatch) {
                if (weekDayMatch) weekDay = weekDayMatch[1];

                // again - only fully defined records will be taken into account
                if (weekDay != null) {
                    if (weekDay < 0 || weekDay > 6) {
                        throw new Error('Incorrect week day index');
                    }

                    weekAvailability[weekDay] = calendarDay;
                }
            }
            else {
                const date = calendarDay.date;

                if (date) daysIndex[date - 0] = calendarDay;
            }
        });

        // Numeric sort, can't use default JS sort which is string based
        nonStandardWeeksStartDates.sort((a, b) => a - b);

        /**
         * Triggered on changes to the calendar.
         * @event calendarChange
         * @param {Scheduler.data.Calendar} source
         */
        me.trigger('calendarChange');
    }

    /**
     * Adds a week day override ("non-standard" week) to the calendar. As a reminder, week day override consists from up to 7 days,
     * that re-defines the default week days availability only within certain time span.
     *
     * @param {Date} startDate The start date of the time span
     * @param {Date} endDate The end date of the time span
     * @param {Scheduler.model.CalendarDayModel[]|String[]} weekAvailability The array indexed from 0 to 7, containing items for week days.
     * Index 0 corresponds to Sunday, 1 to Monday, etc. Some items can be not defined or set to `null`, indicating that override does not
     * change this week day. Item can be - an instance of {@link Scheduler.model.CalendarDayModel} (only `Availability` field needs to be set), or
     * an array of strings, defining the availability (see the description of the `Availability` field in the {@link Scheduler.model.CalendarDayModel}).
     * @param {String} name The name of this week day override
     */
    addNonStandardWeek(startDate, endDate, weekAvailability, name) {
        startDate = DateHelper.clearTime(startDate);
        endDate   = DateHelper.clearTime(endDate);

        if (this.intersectsWithCurrentWeeks(startDate, endDate)) {
            throw new Error('Can not add intersecting week');
        }

        const DayModel = this.modelClass,
            days     = [];

        weekAvailability.forEach((day, index) => {
            if (day instanceof CalendarDayModel) {
                day.type              = 'WEEKDAYOVERRIDE';
                day.overrideStartDate = startDate;
                day.overrideEndDate   = endDate;
                day.weekday           = index;
                day.name              = name || 'Week override';

                days.push(day);
            }
            else if (Array.isArray(day)) {
                const newDay = new DayModel();

                newDay.type              = 'WEEKDAYOVERRIDE';
                newDay.overrideStartDate = startDate;
                newDay.overrideEndDate   = endDate;
                newDay.weekday           = index;
                newDay.name              = name || 'Week override';
                newDay.setAvailability(day);

                days.push(newDay);
            }
        });

        const mainDay = new DayModel();

        mainDay.type              = 'WEEKDAYOVERRIDE';
        mainDay.overrideStartDate = startDate;
        mainDay.overrideEndDate   = endDate;
        mainDay.weekday           = -1;
        mainDay.name              = name || 'Week override';

        days.push(mainDay);

        this.add(days);
    }

    /**
     * Returns an object describing a week day override ("non-standard" week), that starts at the given date or `null` if there's no any.
     *
     * @param {Date} startDate The start date of the week day override
     *
     * @return {Object} An object with the following properties
     * @return {Object} return.name A "Name" field of the week days in the override
     * @return {Date} return.startDate An "OverrideStartDate" field of the week days in the override
     * @return {Date} return.endDate An "OverrideEndDate" field of the week days in the override
     * @return {Scheduler.model.CalendarDayModel[]} return.weekAvailability An array with the week days, defined by this override. May be filled only partially if
     * week day override does not contain all days.
     * @return {Scheduler.model.CalendarDayModel} return.mainDay A "main" day instance for this override
     */
    getNonStandardWeekByStartDate(startDate) {
        return this.nonStandardWeeksByStartDate[DateHelper.clearTime(startDate) - 0] || null;
    }

    /**
     * Removes all calendar day instances, that forms a week day override ("non-standard" week) with the given start date.
     *
     * @param {Date} startDate The start date of the week day override
     */
    removeNonStandardWeek(startDate) {
        startDate = DateHelper.clearTime(startDate) - 0;

        const week = this.getNonStandardWeekByStartDate(startDate);

        if (!week) return;

        this.remove(ArrayHelper.clean(week.weekAvailability).concat(week.mainDay));
    }

    /**
     * Returns an object describing a week day override ("non-standard" week), that contains the given date or `null` if there's no any.
     *
     * @param {Date} timeDate The date that falls within some of the week day overrides
     *
     * @return {Object} An object describing week day override. See {@link #function-getNonStandardWeekByStartDate} method for details.
     */
    getNonStandardWeekByDate(timeDate) {
        timeDate = DateHelper.clearTime(timeDate) - 0;

        const nonStandardWeeksStartDates  = this.nonStandardWeeksStartDates,
            nonStandardWeeksByStartDate = this.nonStandardWeeksByStartDate;

        for (let i = 0; i < nonStandardWeeksStartDates.length; i++) {
            const week = nonStandardWeeksByStartDate[nonStandardWeeksStartDates[i]];

            // since `nonStandardWeeksStartDates` are sorted inc and week overrides do not intersect
            // we can shorcut in this case
            if (week.startDate > timeDate) break;

            if (week.startDate <= timeDate && timeDate <= week.endDate) {
                return week;
            }
        }

        return null;
    }

    /**
     * Updates the default availability information based on the value provided.
     *
     * @param {Boolean} value true if weekends should be regarded as working time.
     */
    setWeekendsAreWorkDays(value) {
        const me = this;

        if (value !== me.weekendsAreWorkdays) {
            me.weekendsAreWorkdays = value;

            // Must generate new defaultWeekAvailability
            me.defaultWeekAvailability = me.getDefaultWeekAvailability();

            me.clearCache();
        }
    }

    /**
     * Returns true if weekends are regarded as working time.
     *
     * @return {Boolean} true if weekends should be regarded as working time.
     */
    areWeekendsWorkDays() {
        return this.weekendsAreWorkdays;
    }

    /**
     * Iterator for each week day override, defined in this calendar.
     *
     * @param {Function} func The function to call for each override. It will receive a single argument - object, describing the override.
     * See {@link #function-getNonStandardWeekByStartDate} for details. Returning `false` from the function stops the iterator.
     * @param {Object} thisObj `this` reference for the function
     *
     * @return {Boolean} `false` if any of the function calls have returned `false`
     */
    forEachNonStandardWeek(func, thisObj) {
        const me                          = this,
            nonStandardWeeksStartDates  = this.nonStandardWeeksStartDates,
            nonStandardWeeksByStartDate = this.nonStandardWeeksByStartDate;

        for (let i = 0; i < nonStandardWeeksStartDates.length; i++) {
            if (func.call(thisObj || me, nonStandardWeeksByStartDate[nonStandardWeeksStartDates[i]]) === false) return false;
        }
    }

    /**
     * Returns a corresponding {@link Scheduler.model.CalendarDayModel} instance for the given date. First, this method checks for {@link #function-getOverrideDay day overrides}
     * (either in this or parent calendars), then for week days (again, in this or parent calendars) and finally fallbacks to the
     * calendar day with the {@link #config-defaultAvailability} availability.
     *
     * @param {Date} timeDate A date (can contain time portion which will be ignored)
     *
     * @return {Scheduler.model.CalendarDayModel}
     */
    getCalendarDay(timeDate) {
        timeDate = typeof timeDate == 'number' ? new Date(timeDate) : timeDate;

        return this.getOverrideDay(timeDate) || this.getWeekDay(timeDate.getDay(), timeDate) || this.getDefaultCalendarDay(timeDate.getDay());
    }

    /**
     * Returns a day override corresponding to the given date (possibly found in the parent calendars) or `null` if the given date
     * has no overrides in this calendar and all its parents.
     *
     * @param {Date} timeDate The date to check for day overrides for
     * @return {Scheduler.model.CalendarDayModel}
     */
    getOverrideDay(timeDate) {
        return this.getOwnCalendarDay(timeDate) || this.parent && this.parent.getOverrideDay(timeDate) || null;
    }

    /**
     * Returns an "own" day override corresponding to the given date. That is - day override defined in the current calendar only.
     *
     * @param {Date} timeDate The date to check for day overrides for
     * @return {Scheduler.model.CalendarDayModel}
     */
    getOwnCalendarDay(timeDate) {
        timeDate = typeof timeDate == 'number' ? new Date(timeDate) : timeDate;

        return this.daysIndex[DateHelper.clearTime(timeDate) - 0];
    }

    /**
     * Returns a "special" week day corresponding to the given date. Under "special" week day we mean a calendar day with the `Type = WEEKDAY` or `WEEKDAYOVERRIDE`.
     * See the {@link Scheduler.model.CalendarDayModel} class for details. If the concrete date is given as 2nd argument, this method will
     * first check for any week overrides passing on it.
     *
     * If not found in current calendar, this method will consult parent. If no "special" week day found neither in this calendar, no parents - it returns `null`.
     *
     * @param {Number} weekDayIndex The index of the week day to retrieve (0-Sunday, 1-Monday, etc)
     * @param {Date} [timeDate] The date for which the week day is being retrieved.
     * @return {Scheduler.model.CalendarDayModel}
     */
    getWeekDay(weekDayIndex, timeDate) {
        // if 2nd argument is provided then try to search in non-standard weeks first
        if (timeDate) {
            const week = this.getNonStandardWeekByDate(timeDate);

            if (week && week.weekAvailability[weekDayIndex]) return week.weekAvailability[weekDayIndex];
        }

        return this.weekAvailability[weekDayIndex] || this.parent && this.parent.getWeekDay(weekDayIndex, timeDate) || null;
    }

    /**
     * Returns a boolean indicating whether a passed date falls on the weekend or holiday.
     *
     * @param {Date} timeDate A given date (can contain time portion)
     *
     * @return {Boolean}
     */
    isHoliday(timeDate) {
        const secondsSinceEpoch = timeDate - 0,
            holidaysCache     = this.holidaysCache;

        if (holidaysCache[secondsSinceEpoch] != null) {
            return holidaysCache[secondsSinceEpoch];
        }

        timeDate = typeof timeDate == 'number' ? new Date(timeDate) : timeDate;

        const day = this.getCalendarDay(timeDate);

        if (!day) throw new Error("Can't find day for " + timeDate);

        return holidaysCache[secondsSinceEpoch] = !day.isWorkingDay;
    }

    /**
     * Returns a "default" calendar day instance, corresponding to the one, generated from {@link #config-defaultAvailability}. By default all working days in the week
     * corresponds to the day with {@link #config-defaultAvailability} set in the `Availability` field and non-working days has empty `Availability`.
     *
     * @param {Number} weekDayIndex The index of the "default" week day to retrieve (0-Sunday, 1-Monday, etc)
     * @return {Scheduler.model.CalendarDayModel}
     */
    getDefaultCalendarDay(weekDayIndex) {
        if (!this.hasOwnProperty('defaultAvailability') && !this.hasOwnProperty('weekendsAreWorkdays') && this.parent) {
            return this.parent.getDefaultCalendarDay(weekDayIndex);
        }

        return this.defaultWeekAvailability[weekDayIndex];
    }

    /**
     * Returns a boolean indicating whether a passed date is a working day.
     *
     * @param {Date} date A given date (can contain time portion which will be ignored)
     *
     * @return {Boolean}
     */
    isWorkingDay(date) {
        return !this.isHoliday(date);
    }

    /**
     * Returns `true` if given date passes on the weekend and `false` otherwise. Weekend days can be re-defined with the {@link #config-weekendFirstDay} and {@link #config-weekendSecondDay} options.
     *
     * @param {Date} timeDate The date to check
     * @return {Boolean}
     */
    isWeekend(timeDate) {
        const dayIndex = timeDate.getDay();
        return dayIndex === this.weekendFirstDay || dayIndex === this.weekendSecondDay;
    }

    /**
     * Convert the duration given in milliseconds to a given unit. Uses the {@link #config-daysPerMonth} configuration option.
     *
     * @param {Number} durationInMs Duration in milliseconds
     * @param {String} unit Duration unit to which the duration should be converted
     *
     * @return {Number} converted value
     */
    convertMSDurationToUnit(durationInMs, unit) {
        return durationInMs / this.unitsInMs[DateHelper.getUnitByName(unit)];
    }

    /**
     * Convert the duration given in some unit to milliseconds. Uses the {@link #config-daysPerMonth} configuration option.
     *
     * @param {Number} durationInMs
     * @param {String} unit
     *
     * @return {Number} converted value
     */
    convertDurationToMs(duration, unit) {
        return duration * this.unitsInMs[DateHelper.getUnitByName(unit)];
    }

    /**
     * This an iterator that passes through the all availability intervals (working time intervals) in the given date range.
     *
     * For example if the default availability in this calendar is [ '09:00-13:00', '14:00-18:00' ] and this function is called, like this:
     *
     *      calendar.forEachAvailabilityInterval(
     *           //             midnight  Friday                 midnight Tuesday
     *          { startDate : new Date(2013, 1, 8), endDate : new Date(2013, 1, 12) },
     *          function (startDate, endDate) { ... }
     *      )
     * then the provided function will be called 4 times with the following arguments:
     *
     *      startDate : new Date(2013, 1, 8, 9),    endDate : new Date(2013, 1, 8, 13)
     *      startDate : new Date(2013, 1, 8, 14),   endDate : new Date(2013, 1, 8, 18)
     *      startDate : new Date(2013, 1, 11, 9),   endDate : new Date(2013, 1, 11, 13)
     *      startDate : new Date(2013, 1, 11, 14),  endDate : new Date(2013, 1, 11, 18)
     *
     *
     * @param {Object} options An object with the following properties:
     * @param {Date} options.startDate A start date of the date range. Can be omitted, if `isForward` flag is set to `false`. In this case iterator
     * will not stop until the call to `func` will return `false`.
     * @param {Date} options.endDate An end date of the date range. Can be omitted, if `isForward` flag is set to `true`. In this case iterator
     * will not stop until the call to `func` will return `false`.
     * @param {Boolean} [options.isForward=true] A flag, defining the direction, this iterator advances in. If set to `true` iterations
     * will start from the `startDate` option and will advance in date increasing direction. If set to `false` iterations will start from the `endDate`
     * option and will advance in date decreasing direction.
     * @param {Function} func A function to call for each availability interval, in the given date range. It receives 2 arguments - the start date
     * of the availability interval and the end date.
     * @param {Object} thisObj `this` reference for the function
     *
     * @return {Boolean} `false` if any of the calls to `func` has returned `false`
     */
    forEachAvailabilityInterval(options, func, thisObj) {
        thisObj         = thisObj || this;
        let me        = this,
            startDate = options.startDate,
            endDate   = options.endDate,

            // isForward by default
            isForward = options.isForward !== false;

        if (isForward ? !startDate : !endDate) {
            throw new Error('At least `startDate` or `endDate` is required, depending from the `isForward` option');
        }

        let cursorDate = new Date(isForward ? startDate : endDate),
            DATE       = DateHelper;

        // if no boundary we still have to specify some limit
        if (isForward) {
            if (!endDate) {
                endDate = DATE.add(startDate, options.availabilitySearchLimit || me.availabilitySearchLimit || 5 * 365, 'day');
            }
        }
        else {
            if (!startDate) {
                startDate = DATE.add(endDate, -(options.availabilitySearchLimit || me.availabilitySearchLimit || 5 * 365), 'day');
            }
        }

        // the clearTime() method is called a lot during this method (like 200k times for 2k tasks project)
        // sometimes w/o real need for it since we always advance to the next day's boundary
        // this optimization brings it down to ~10k, ~10% speed up
        let noNeedToClearTime = false;

        while (isForward ? cursorDate < endDate : cursorDate > startDate) {
            // - 1 for backward direction ensures that we are checking correct day,
            // since the endDate is not inclusive - 02/10/2012 means the end of 02/09/2012
            // for backward direction we always clear time, because intervals are cached by the beginning of the day
            let intervals = me.getAvailabilityIntervalsFor(cursorDate - (isForward ? 0 : 1), isForward ? noNeedToClearTime : false);

            // the order of processing is different for forward / backward processing
            for (let i = isForward ? 0 : intervals.length - 1; isForward ? i < intervals.length : i >= 0; isForward ? i++ : i--) {
                let interval          = intervals[i],
                    intervalStartDate = interval.startDate,
                    intervalEndDate   = interval.endDate;

                // availability interval is out of [ startDate, endDate )
                if (intervalStartDate >= endDate || intervalEndDate <= startDate) continue;

                let countingFrom = intervalStartDate < startDate ? startDate : intervalStartDate,
                    countingTill = intervalEndDate > endDate ? endDate : intervalEndDate;

                if (func.call(thisObj, countingFrom, countingTill) === false) return false;
            }

            cursorDate = isForward ? DATE.getStartOfNextDay(cursorDate, false, noNeedToClearTime) : DATE.getEndOfPreviousDay(cursorDate, noNeedToClearTime);

            noNeedToClearTime = true;
        }
    }

    /**
     * Calculate the duration in the given `unit` between 2 dates, taking into account the availability/holidays information (non-working time will be excluded from the duration).
     *
     * @param {Date} startDate The start date
     * @param {Date} endDate The end date
     * @param {String} unit One of the units used by DateHelper
     *
     * @return {Number} Working time duration between given dates.
     */
    calculateDuration(startDate, endDate, unit) {
        let duration = 0;

        this.forEachAvailabilityInterval({
            startDate : startDate,
            endDate   : endDate
        }, (intervalStartDate, intervalEndDate) => {
            let dstDiff = intervalStartDate.getTimezoneOffset() - intervalEndDate.getTimezoneOffset();

            duration += intervalEndDate - intervalStartDate + dstDiff * 60 * 1000;
        });

        return this.convertMSDurationToUnit(duration, unit);
    }

    /**
     * Returns an array of ranges for non-working days between `startDate` and `endDate`. For example normally, given a
     * full month, it will return an array of 4 `Scheduler.model.TimeSpan` instances, containing ranges for the
     * weekends. If a holiday lasts for several days and all {@link Scheduler.model.CalendarDayModel} instances have
     * the same `cls` value then all days will be combined into a single range.
     *
     * @param {Date} startDate - A start date of the timeframe to extract the holidays from
     * @param {Date} endDate - An end date of the timeframe to extract the holidays from
     *
     * @return {Scheduler.model.TimeSpan[]}
     */
    getHolidaysRanges(startDate, endDate, includeWeekends) {
        if (startDate > endDate) {
            throw new Error("startDate can't be bigger than endDate");
        }

        startDate = DateHelper.clearTime(startDate);
        endDate   = DateHelper.clearTime(endDate);

        let ranges = [],
            currentRange,
            date;

        for (date = startDate; date < endDate; date = DateHelper.getNext(date, 'day', 1)) {
            if (this.isHoliday(date) || (this.weekendsAreWorkdays && includeWeekends && this.isWeekend(date))) {
                const day      = this.getCalendarDay(date),
                    cssClass = day && day.cls || this.defaultNonWorkingTimeCssCls,
                    nextDate = DateHelper.getNext(date, 'day', 1);

                // starts new range
                if (!currentRange) {
                    currentRange = new TimeSpan({
                        startDate : date,
                        endDate   : nextDate,
                        cls       : cssClass
                    });
                }
                else {
                    // checks if the range is still the same
                    if (currentRange.cls.isEqual(cssClass)) {
                        currentRange.endDate = nextDate;
                    }
                    else {
                        ranges.push(currentRange);

                        currentRange = new TimeSpan({
                            startDate : date,
                            endDate   : nextDate,
                            cls       : cssClass
                        });
                    }
                }
            }
            else {
                if (currentRange) {
                    ranges.push(currentRange);
                    currentRange = null;
                }
            }
        }

        if (currentRange) {
            ranges.push(currentRange);
        }

        return ranges;
    }

    /**
     * Calculate the end date for the given start date and duration, taking into account the availability/holidays information (non-working time will not be counted as duration).
     *
     * @param {Date} startDate The start date
     * @param {Number} duration The "pure" duration (w/o any non-working time).
     * @param {String} unit One of the units of the {@link Core.helper.DateHelper} class.
     *
     * @return {Date} The end date
     */
    calculateEndDate(startDate, duration, unit) {
        // if duration is 0 - return the same date
        if (!duration) {
            return new Date(startDate);
        }

        let DATE = DateHelper,
            endDate;

        duration = this.convertDurationToMs(duration, unit);

        let startFrom =
                // milestone case, which we don't want to re-schedule to the next business days
                // milestones should start/end in the same day as its incoming dependency
                duration === 0 && DATE.clearTime(startDate, true) - startDate === 0

                    ? DATE.add(startDate, -1, 'day')
                    :                    startDate;

        this.forEachAvailabilityInterval({ startDate : startFrom }, function(intervalStartDate, intervalEndDate) {
            let diff    = intervalEndDate - intervalStartDate,
                dstDiff = intervalStartDate.getTimezoneOffset() - intervalEndDate.getTimezoneOffset();

            if (diff >= duration) {
                endDate = new Date(intervalStartDate - 0 + duration);

                return false;
            }
            else {
                duration -= diff + dstDiff * 60 * 1000;
            }
        });

        return endDate;
    }

    /**
     * This method starts from the given `date` and moves forward/backward in time (depending from the `isForward` flag) skiping the non-working time.
     * It returns the nearest edge of the first working time interval it encounters. If the given `date` falls on the working time, then `date` itself is returned.
     *
     * For example, if this function is called with some Saturday as `date` and `isForward` flag is set, it will return the earliest working hours on following Monday.
     * If `isForward` flag will be set to `false` - it will return the latest working hours on previous Friday.
     *
     * @param {Date} date A date (presumably falling on the non-working time).
     * @param {Boolean} isForward Pass `true` to skip the non-working time in forward direction, `false` - in backward
     *
     * @return {Date} Nearest working date.
     */
    skipNonWorkingTime(date, isForward) {
        let found = false;
        // reseting the date to the earliest availability interval
        this.forEachAvailabilityInterval(
            isForward ? { startDate : date } : { endDate : date, isForward : false },

            (intervalStartDate, intervalEndDate) => {
                date  = isForward ? intervalStartDate : intervalEndDate;
                found = true;

                return false;
            }
        );

        if (!found) throw new Error('skipNonWorkingTime: Cannot skip non-working time, please ensure that this calendar has any working period of time specified');

        return new Date(date);
    }

    /**
     * Calculate the start date for the given end date and duration, taking into account the availability/holidays information (non-working time will not be counted as duration).
     *
     * @param {Date} endDate The end date
     * @param {Number} duration The "pure" duration (w/o any non-working time).
     * @param {String} unit One of the units of the {@link Core.helper.DateHelper} class.
     *
     * @return {Date} The start date
     */
    calculateStartDate(endDate, duration, unit) {
        // if duration is 0 - return the same date
        if (!duration) {
            return new Date(endDate);
        }

        let startDate;

        duration = this.convertDurationToMs(duration, unit);

        this.forEachAvailabilityInterval({
            endDate   : endDate,
            isForward : false
        }, (intervalStartDate, intervalEndDate) => {
            const diff = intervalEndDate - intervalStartDate;

            if (diff >= duration) {
                startDate = new Date(intervalEndDate - duration);

                return false;
            }
            else {
                duration -= diff;
            }
        });

        return startDate;
    }

    /**
     * This method starts from the given `date` and moves forward/backward in time (depending from the `duration` argument).
     * It stops as soon as it skips the amount of *working* time defined by the `duration` and `unit` arguments. Skipped non-working time simply will not
     * be counted.
     *
     * **Note** that this method behaves differently from the {@link #function-skipNonWorkingTime} - that method stops as soon as it encounters the non-working time.
     * This method stops as soon as it accumulate enough skipped working time.
     *
     * @param {Date} date A starting point
     * @param {Number} duration The duration of the working time. To skip working time in backward direction pass a negative value.
     * @param {String} unit One of the units of the {@link Core.helper.DateHelper} class.
     *
     * @return {Date}
     */
    skipWorkingTime(date, duration, unit) {
        return duration >= 0 ? this.calculateEndDate(date, duration, unit) : this.calculateStartDate(date, -duration, unit);
    }

    isChildOf(calendar) {
        let parent = this,
            found  = false;

        while (parent && !found) {
            found  = parent === calendar;
            parent = parent.parent;
        }

        return found;
    }

    getParentableCalendars() {
        const me        = this,
            calendars = Calendar.getAllCalendars();

        return calendars.reduce((result, calendar) => {
            if (calendar !== me && !calendar.isChildOf(me)) {
                result.push({ id : calendar.calendarId, name : calendar.name || calendar.calendarId });
            }
            return result;
        }, []);
    }

    get parent() {
        return this._parent;
    }

    /**
     * Returns the availability intervals of a specific day. Potentially can consult a parent calendar.
     *
     * @param {Date|Number} timeDate A date or timestamp
     * @return {Object[]} Array of objects, like:

     {
         startDate       : new Date(...),
         endDate         : new Date(...)
     }
     */
    getAvailabilityIntervalsFor(timeDate, noNeedToClearTime) {
        if (noNeedToClearTime) {
            timeDate = (timeDate).valueOf();
        }
        else if (timeDate instanceof Date) {
            timeDate = (new Date(timeDate.getFullYear(), timeDate.getMonth(), timeDate.getDate())).valueOf();
        }
        else {
            timeDate = DateHelper.clearTime(new Date(timeDate)).valueOf();
        }

        return this.availabilityIntervalsCache[timeDate] = (this.availabilityIntervalsCache[timeDate] || this.getCalendarDay(timeDate).getAvailabilityIntervalsFor(timeDate));
    }

    onParentDestroy() {
        this.parent = null;
    }

    isAvailabilityIntersected(withCalendar, startDate, endDate) {
        let ownWeekDay, ownAvailability,
            testWeekDay, testAvailability;

        // first let's try to find overlapping of weeks (check daily intervals)
        // loop over week days
        for (let i = 0; i < 7; i++) {
            ownWeekDay  = this.getWeekDay(i) || this.getDefaultCalendarDay(i);
            testWeekDay = withCalendar.getWeekDay(i) || withCalendar.getDefaultCalendarDay(i);

            if (!ownWeekDay || !testWeekDay) continue;

            // get daily intervals
            ownAvailability  = ownWeekDay.getAvailability();
            testAvailability = testWeekDay.getAvailability();

            // loop over intervals to find overlapping
            for (let j = 0, l = ownAvailability.length; j < l; j++) {
                for (let k = 0, ll = testAvailability.length; k < ll; k++) {
                    if (testAvailability[k].startTime < ownAvailability[j].endTime && testAvailability[k].endTime > ownAvailability[j].startTime) {
                        return true;
                    }
                }
            }
        }

        let result = false;

        this.forEachNonStandardWeek(week => {
            if (week.startDate >= endDate) return false;

            if (startDate < week.endDate) {
                result = true;
                // stop the iteration
                return false;
            }
        });

        return result;
    }
}
