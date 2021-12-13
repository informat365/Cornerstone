import LocaleManager from '../localization/LocaleManager.js';
import DateHelper from '../helper/DateHelper.js';

/**
 * @module Core/util/Month
 */

/**
 * A class which encapsulates a calendar view of a month, and offers information about 
 * the weeks and days within that calendar view.
 * ```
 *   const m = new Month({
 *       date         : '2018-12-01',
 *       weekStartDay : 1
 *   }) // December 2018 using Monday as week start
 *   m.eachWeek((week, dates) => console.log(dates.map(d => d.getDate())))
 * ```
 */
export default class Month {
    /**
     * Constructs a Month from a config object which *must* contain a `date` property.
     * @param {Object} config An object containing initialization properties.
     * @param {Date|String|Number} config.date The date which the month should encapsulate. May be
     * a `Date` object, or a value, which, when passed to the `Date` constructor yields a
     * valid `Date` object. Mutating a passed `Date` after initializing a `Month` object
     * has no effect on the `Month` object.
     * @param {Number} [config.weekStartDay] Optional. Week start day override. Defaults to the
     * locale's {@link Core.helper.DateHelper#property-weekStartDay-static}.
     * @param {Boolean} [config.sixWeeks] Optional. Pass `true` to always have the month encapsulate six weeks.
     * This is ueful for UIs which must be a fixed height.
     * @function constructor
     */
    constructor(config) {
        let { date, weekStartDay, sixWeeks } = config;

        this.constructor.updateDayNumbers(weekStartDay);
        this.sixWeeks = sixWeeks;
        this.date = date;
    }

    set date(date) {
        const isString = typeof date === 'string';

        date = new Date(date);
 
        // Edge parses YYYY-MM-DD as UTC, not local, so in other locales, the value
        // may end up not being 00:00:00 in the date, so remove any time zone offset.
        if (isString) {
            if (isNaN(date)) {
                throw new Error('CalendarPanel date ingestion must be passed a Date, or a valid argument to the Date constructor');
            }
            date = DateHelper.add(date, date.getTimezoneOffset(), 'minute');
        }

        const me = this,
            monthStart    = DateHelper.getFirstDateOfMonth(date),
            monthEnd      = DateHelper.getLastDateOfMonth(monthStart),
            startWeekDay  = me.dayNumbers[monthStart.getDay()],
            endWeekDay    = me.dayNumbers[monthEnd.getDay()];

        me._date = date;
    
        // These comments assume ISO standard of Monday as week start day.
        //
        // This is the date of month that is the beginning of the first week row.
        // So this may be -ve. Eg: for Dec 2018, Monday 26th Nov is the first
        // cell on the calendar which is the -4th of December. Note that the 0th
        // of December was 31st of November, so needs -4 to get back to the 26th.
        me.startDayOfMonth = 1 - startWeekDay;
            
        // This is the date of month that is the end of the last week row.
        // So this may be > month end. Eg: for Dec 2018, Sunday 6th Jan is the last
        // cell on the calendar which is the 37th of December.
        me.endDayOfMonth = monthEnd.getDate() + (6 - endWeekDay);

        if (me.sixWeeks) {
            while (me.weekCount < 6) {
                me.endDayOfMonth += 7;
            }
        }

        const jan1 = new Date(me.year, 0, 1),
            dec31 = new Date(me.year, 1, 31),
            january = me.month ? new Month({
                date         : jan1,
                weekStartDay : me.weekStartDay
            }) : me;

        // First 7 days are in last week of previous year if the year
        // starts after our 4th day of week.
        if (me.dayNumbers[jan1.getDay()] > 3) {
            // Week base is calculated from the year start
            me.weekBase = january.startDate;
        }
        // First 7 days are in week 1 of this year
        else {
            // Week base is the start of week before
            me.weekBase = new Date(me.year, 0, january.startDayOfMonth - 7);
        }

        // Our year only has a 53rd week if the year ends before our week's 5th day
        me.has53weeks = dec31.getDay() < 4;
    }

    set year(year) {
        this.date.setFullYear(year);
        this.date = this.date;
    }

    get year() {
        return this.date.getFullYear();
    }

    set month(month) {
        this.date.setMonth(month);
        this.date = this.date;
    }

    get month() {
        return this.date.getMonth();
    }

    get date() {
        return this._date;
    }

    /**
     * The number of days in the calendar for this month. This will always be
     * a multiple of 7, because this represents the number of calendar cells
     * occupied by this month.
     * @property {Number}
     * @readonly
     */
    get dayCount() {
        // So for the example month, Dec 2018 has 42 days, from Mon 26th Nov (-4th Dec) 2018
        // to Sun 6th Jan (37th Dec) 2019
        return (this.endDayOfMonth + 1) - this.startDayOfMonth;
    }

    /**
     * The number of weeks in the calendar for this month.
     * @property {Number}
     * @readonly
     */
    get weekCount() {
        return this.dayCount / 7;
    }

    /**
     * The date of the first cell in the calendar view of this month.
     * @property {Date}
     * @readonly
     */
    get startDate() {
        return new Date(this.year, this.month, this.startDayOfMonth);
    }

    /**
     * The date of the last cell in the calendar view of this month.
     * @property {Date}
     * @readonly
     */
    get endDate() {
        return new Date(this.year, this.month, this.endDayOfMonth);
    }

    /**
     * Iterates through all calendar cells in this month, calling the passed function
     * for each date.
     * @param {Function} fn The function to call.
     * <h4>Parameters</h4>
     *  - date [Date](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date) The date for the cell.
     */
    eachDay(fn) {
        const me = this;

        for (let dayOfMonth = me.startDayOfMonth; dayOfMonth <= me.endDayOfMonth; dayOfMonth++) {
            fn(new Date(me.year, me.month, dayOfMonth));
        }
    }

    /**
     * Iterates through all weeks in this month, calling the passed function
     * for each week. The function is passed the following parameters:
     * @param {Function} fn The function to call.
     * <h4>Parameters</h4>
     *  - week [Number[]](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number) An array containing `[year, weekNumber]`.
     *  - date [Date[]](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date) The dates for the week.
     */
    eachWeek(fn) {
        const me = this,
            { weekCount } = me;

        for (let dayOfMonth = me.startDayOfMonth, week = 0; week < weekCount; week++) {
            const weekDates  = [],
                weekOfYear = me.getWeekNumber(new Date(me.year, me.month, Math.max(dayOfMonth, 1)));

            for (let day = 0; day < 7; day++, dayOfMonth++) {
                weekDates.push(new Date(me.year, me.month, dayOfMonth));
            }
            fn(weekOfYear, weekDates);
        }
    }

    getWeekNumber(date) {
        const me = this,
            { weekStartDay } = me;

        date = DateHelper.clearTime(date);

        // If it's a date that our known year does not contain
        // create a new Month to find the answer.
        if (date < me.startDate || date > me.endDate) {
            return new Month({
                date,
                weekStartDay
            }).getWeekNumber(date);
        }

        let weekNo = Math.floor(((date - me.weekBase) / 86400000) / 7),
            year = date.getFullYear();

        // No week 0. It's the last week of last year
        if (!weekNo) {
            const lastDec31 = new Date(me.year, 0, 0);

            // Week is the week of last year's 31st Dec
            return new Month({
                date : lastDec31,
                weekStartDay
            }).getWeekNumber(lastDec31);
        }
        // Only week 53 if year ends before our week's 5th day
        else if (weekNo === 53 && !me.has53weeks) {
            weekNo = 1;
            year++;
        }

        // Return array of year and week number
        return [year, weekNo];
    }

    static applyLocale() {
        this.updateDayNumbers();
    }

    static updateDayNumbers(weekStartDay = DateHelper.weekStartDay) {
        const me = this.prototype,
            dayNumbers = me.dayNumbers = [],
            canonicalDayNumbers = me.canonicalDayNumbers = [];

        me.weekStartDay = weekStartDay;

        // So, if they set weekStartDay to 1 meaning Monday which is ISO standard, we will
        // have mapping of internal day number to canonical day number (as used by Date class)
        // and to abbreviated day name like this:
        // canonicalDayNumbers = [1, 2, 3, 4, 5, 6, 0] // Use for translation from our day number to Date class's day number
        // dayNumbers          = [6, 0, 1, 2, 3, 4, 5] // Use for translation from Date object's day number to ours
        for (let i = 0; i < 7; i++) {
            const canonicalDay = (weekStartDay + i) % 7;

            canonicalDayNumbers[i] = canonicalDay;
            dayNumbers[canonicalDay] = i;
        }
    }
}

// Update when changing locale
LocaleManager.on({
    locale  : 'applyLocale',
    thisObj : Month
});
