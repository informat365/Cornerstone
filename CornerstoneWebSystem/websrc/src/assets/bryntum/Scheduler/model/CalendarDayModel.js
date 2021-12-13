import DateHelper from '../../Core/helper/DateHelper.js';
import Model from '../../Core/data/Model.js';

/**
 * @module Scheduler/model/CalendarDayModel
 */

/**
 * A model representing a single day in the calendar. Depending on the `type` field, day may be a concrete day per se (2012/01/01),
 * a certain weekday (all Thursdays), or an override for all certain weekdays in the timeframe
 * (all Fridays between 2012/01/01 - 2012/01/15, inclusive).
 *
 * A collection of CalendarDayModel instances to be provided for the {@link Scheduler.data.Calendar calendar}
 *
 * @extends Core/data/Model
 */
export default class CalendarDayModel extends Model {
    static get fields() {
        return [

            /**
             * The id of the date. Can be an arbitrary unique value, assigned by the server
             * @field {String|Number} id
             */

            /**
             * The date for this day in the ISO 8601 format. Any time information in this field will be cleared. If this
             * instance represents a weekday or week override, this field will be ignored.
             * @field {String|Date} date
             */
            {
                name    : 'date',
                type    : 'date',
                format  : 'YYYY-MM-DD',
                persist : true
            },

            /**
             * The index of the week day (0 - Sunday, 1 - Monday and so on) if this instance contains information about the week day (applicable for `WEEKDAY` and `WEEKDAYOVERRIDE`).
             * Should be set to -1 for the "main" instance of the week overrides.
             * @field {Number} weekday
             */
            { name : 'weekday', type : 'int' },

            /**
             * The start date of the timespan for week day override.
             * @field {Date} overrideStartDate
             */
            {
                name       : 'overrideStartDate',
                type       : 'date',
                dateFormat : 'YYYY-MM-DD'
            },

            /**
             * The end date of the timespan for week day override.
             * @field {Date} overrideEndDate
             */
            {
                name       : 'overrideEndDate',
                type       : 'date',
                dateFormat : 'YYYY-MM-DD'
            },

            /**
             * The type of this calendar day. Can be one of the following `DAY`, `WEEKDAY`, `WEEKDAYOVERRIDE`:
             * - Default value is `DAY` meaning this day represents a "real" day in the calendar (2012/01/01 for example) and contains availability information for that particular day only.
             * The date is stored in the `Date` field.
             * - The `WEEKDAY` value means calendar day contains information about all weekdays with the index, given in the `Weekday` field (0 - Sunday, 1 - Monday and so on).
             * For example - all Fridays. `Date` field is ignored.
             * - <p>The `WEEKDAYOVERRIDE` value means calendar day contains information about all weekdays within certain timespan. For example - all Fridays between 2012/01/01 - 2012/01/15.
             * Week day index should be stored in the `Weekday` field again, beginning of the timespan - in the `OverrideStartDate` field and the end of timespan - in the `OverrideEndDate`.
             * </p>
             * <p>
             * A single day instance contains the override for a single week day. So, to define overrides for several days (Monday and Tuesday for example) - add an additional instance
             * to the calendar with the same `Name/OverrideStartDate/OverrideEndDate` values. There's no need to define an override for every weekday - if some day is not defined - the
             * default availability will be used.
             * </p>
             * <p>
             * * **Note** Every week override should also have a "main" calendar day instance, representing the override itself. It should have the same
             * values for `Name/OverrideStartDate/OverrideEndDate` fields and -1 for `Weekday`. Also, the timespans of all week overrides should not intersect.
             * </p>
             * <p>
             * To avoid manual creation of week overrides you can use the calendar API (for example, {@link Scheduler.data.Calendar#function-addNonStandardWeek addNonStandardWeek},
             * {@link Scheduler.data.Calendar#function-removeNonStandardWeek removeNonStandardWeek} methods).
             * </p>
             * @field {String} type
             * @default 'DAY'
             */
            {
                name         : 'type',
                defaultValue : 'DAY' // 'DAY', 'WEEKDAY', 'WEEKDAYOVERRIDE'
            },

            /**
             * Optional boolean flag, allowing you to specify exceptions - working days which falls on weekends. Default value is `false`. **Please note**, that simply setting this
             * field to "true" is not enough - you also need to specify the exact hours that are available for work with the `Availability` field (see below).
             * @field {Boolean} isWorkingDay
             * @default false
             */
            { name : 'isWorkingDay', type : 'boolean', defaultValue : false },

            /**
             * Optional name of the CSS class, which can be used by various plugins working with weekends and holidays.
             * Default value is `gnt-holiday` If a holiday lasts for several days, then all days should have the same
             * `cls` value.
             * @field {String} cls
             */
            {
                name : 'cls'//,
                //defaultValue : 'b-sch-nonworkingtime'
            },

            /**
             * Optional name of the day (holiday name for example)
             * @field {String} name
             */
            { name : 'name' },

            /**
             * Availability information for this day. Should be an array of strings or objects, containing the hourly
             * availability for this day. Strings should have the following format:
             * ```javascript
             * // two working intervals
             * [ '08:00-12:00', '13:00-17:00' ]
             *
             * // whole 24 hours are available
             * [ '00:00-24:00' ]
             * ```
             * Objects:
             * ```
             * [{
             *    startTime       : new Date(0, 0, 0, 8),
             *    endTime         : new Date(0, 0, 0, 12)
             * }]
             * ```
             * **Please note**, that this field overrides `isWorkingDay` - for example, a day with
             * "isWorkingDay : false" and "Availability : [ '08:00-12:00' ]" - will be considered a working day.
             * @field {String[]|Object[]} availability
             */
            {
                name    : 'availability',
                persist : true//,
                // convert : function(value, record) {
                //     if (value) {
                //         return typeof value === 'string' ? [value] : value;
                //     } else {
                //         return [];
                //     }
                // }
            }
        ];
    }

    set date(date) {
        if (date) date = DateHelper.startOf(date, 'day');

        this.set('date', date);
    }

    get date() {
        return this.get('date');
    }

    /**
     * Clears the date for this day
     */
    clearDate() {
        this.set('date', null);
    }

    // needed since you cannot override setter only
    get availability() {
        return this.get('availability');
    }

    set availability(intervals) {
        // clear cache
        this.availabilityCache = null;

        this.set('availability', this.stringifyIntervals(intervals));

        // to trigger the `verifyAvailability`
        this.getAvailability();
    }

    /**
     * This method returns the availability for this day. By default it will decode an array of strings '08:00-12:00' to
     * an array of objects like:
     * ```javascript
     * {
     *    startTime       : new Date(0, 0, 0, 8),
     *    endTime         : new Date(0, 0, 0, 12)
     * }
     * ```
     * You can pass the "asString" flag to disable that and just return strings.
     *
     * @param {Boolean} asString Whether to just return an array of strings, instead of objects.
     * @return {Object[]|String[]} Array of objects with "startTime", "endTime" properties.
     */
    getAvailability(asString) {
        const me = this;

        // Return the raw availability array with strings
        if (asString) return me.get('availability');

        if (me.availabilityCache) return me.availabilityCache;

        const parsed = me.get('availability').map(value =>
            typeof value === 'string' ? me.parseInterval(value) : value
        );

        me.verifyAvailability(parsed);

        return me.availabilityCache = parsed;
    }

    verifyAvailability(intervals) {
        const me = this;

        intervals.sort((a, b) => a.startTime - b.startTime);

        intervals.forEach((interval, i) => {
            if (interval.startTime > interval.endTime) {
                throw new Error(`Start time ${DateHelper.format(interval.startTime, 'HH:mm')} is greater than end time ${DateHelper.format(interval.endTime, 'HH:mm')}`);
            }

            if (i > 0 && intervals[i - 1].endTime > interval.startTime) {
                throw new Error(`Availability intervals should not intersect: [${me.stringifyInterval(intervals[i - 1])}] and [${me.stringifyInterval(interval)}]`);
            }
        });
    }

    //prependZero(value) {
    //    return value < 10 ? '0' + value : value;
    //}

    stringifyInterval(interval) {
        const startTime = interval.startTime,
            endTime   = interval.endTime;

        return DateHelper.format(startTime, 'HH:mm') + '-' + DateHelper.format(endTime, 'HH:mm');

        //return this.prependZero(startTime.getHours()) + ':' + this.prependZero(startTime.getMinutes()) + '-' +
        //    (endTime.getDate() == 1 ? 24 : this.prependZero(endTime.getHours())) + ':' + this.prependZero(endTime.getMinutes());
    }

    stringifyIntervals(intervals) {
        const me = this;

        return intervals.map(interval =>
            typeof interval === 'string' ? interval : me.stringifyInterval(interval)
        );
    }

    parseInterval(string) {
        const match = /(\d\d):(\d\d)-(\d\d):(\d\d)/.exec(string);

        if (!match) throw new Error(`Invalid format for availability string: ${String}. It should have exact format: hh:mm-hh:mm`);

        return {
            startTime : new Date(0, 0, 0, match[1], match[2]),
            endTime   : new Date(0, 0, 0, match[3], match[4])
        };
    }

    /**
     * Returns the total length of all availability intervals for this day in hours.
     *
     * @property {Number}
     * @readonly
     */
    get totalHours() {
        return this.getTotalMS() / 1000 / 60 / 60;
    }

    /**
     * Returns the total length of all availability intervals for this day in milliseconds.
     *
     * @property {Number}
     * @readonly
     */
    get totalMS() {
        return this.getAvailability().reduce((totalMS, interval) => totalMS += interval.endTime - interval.startTime, 0);
    }

    /**
     * Adds a new availability interval to this day. Both arguments should have the same format.
     *
     * @param {Date|String} startTime Start time of the interval. Can be a Date object (new Date(0, 0, 0, 8)) or just a plain string: '08'
     * @param {Date|String} endTime End time of the interval. Can be a Date object (new Date(0, 0, 0, 12)) or just a plain string: '12'
     */
    addAvailabilityInterval(startTime, endTime) {
        let interval;

        if (startTime instanceof Date) {
            interval = {
                startTime : startTime,
                endTime   : endTime
            };
        }
        else {
            interval = this.parseInterval(startTime + (endTime ? '-' + endTime : ''));
        }

        const intervals = this.getAvailability().concat(interval);

        this.verifyAvailability(intervals);

        this.setAvailability(intervals);
    }

    /**
     * Removes the availability interval by its index.
     *
     * @param {Number} index Ordinal position of the interval to be removed
     */
    removeAvailabilityInterval(index) {
        const intervals = this.getAvailability();

        intervals.splice(index, 1);

        this.setAvailability(intervals);
    }

    /**
     * Applies the availability intervals to a concrete day. For example the availability intervals [ '08:00-12:00', '13:00-17:00' ],
     * applied to a day 2012/01/01 will return the following result:
     * ```javascript
     * [
     * {
     *     startDate       : new Date(2012, 0, 1, 8),
     *     endDate         : new Date(2012, 0, 1, 12)
     * },
     * {
     *     startDate       : new Date(2012, 0, 1, 13),
     *     endDate         : new Date(2012, 0, 1, 17)
     * }
     * ]
     * ```
     * @param {Date} timeDate The date to apply the intervals to
     * @returns {Object[]} Array of objects with "startDate / endDate" properties.
     */
    getAvailabilityIntervalsFor(timeDate) {
        timeDate = typeof timeDate === 'number' ? new Date(timeDate) : timeDate;

        const year  = timeDate.getFullYear(),
            month = timeDate.getMonth(),
            date  = timeDate.getDate();

        return this.getAvailability().map(interval => {
            const endDate = interval.endTime.getDate();

            return {
                startDate : new Date(year, month, date, interval.startTime.getHours(), interval.startTime.getMinutes()),
                endDate   : new Date(year, month, date + (endDate == 1 ? 1 : 0), interval.endTime.getHours(), interval.endTime.getMinutes())
            };
        });
    }

    /**
     * Returns the earliest available time for the given date. If this day has no availability intervals it returns `null`.
     *
     * @param {Date} timeDate The date to get the earliest availability time for.
     * @return {Date}
     */
    getAvailabilityStartFor(timeDate) {
        const intervals = this.getAvailabilityIntervalsFor(timeDate);

        if (!intervals.length) return null;

        return intervals[0].startDate;
    }

    /**
     * Returns the latest available time for the given date. If this day has no availability intervals, it returns `null`.
     *
     * @param {Date} timeDate The date to get the latest availability time for.
     * @return {Date}
     */
    getAvailabilityEndFor(timeDate) {
        const intervals = this.getAvailabilityIntervalsFor(timeDate);

        if (!intervals.length) return null;

        return intervals[intervals.length - 1].endDate;
    }
}
