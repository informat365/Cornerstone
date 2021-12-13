import Base from '../../../Core/Base.js';
import DelayedCallsManager from '../util/DelayedCallsManager.js';
import DailyRecurrenceIterator from '../util/recurrence/DailyRecurrenceIterator.js';
import WeeklyRecurrenceIterator from '../util/recurrence/WeeklyRecurrenceIterator.js';
import MonthlyRecurrenceIterator from '../util/recurrence/MonthlyRecurrenceIterator.js';
import YearlyRecurrenceIterator from '../util/recurrence/YearlyRecurrenceIterator.js';

/**
 * @module Scheduler/data/mixin/RecurringTimeSpansMixin
 */

/**
 * This mixin class provides recurring timespans functionality to a store of {@link Scheduler.model.TimeSpan} models.
 * @mixin
 */
export default Target => class RecurringTimeSpansMixin extends (Target || Base) {

    static get $name() {
        return 'RecurringTimeSpansMixin';
    }

    /**
     * Indicates that the store supports recurring timespans.
     * @default true
     * @property {Boolean}
     * @readonly
     */
    get supportsRecurringTimeSpans() {
        return true;
    }

    /**
     * Timeout in milliseconds during which to collect calls for generating occurrences related methods.
     * @property {Number}
     * @default 100
     */
    get delayedCallTimeout() {
        return !isNaN(this._delayedCallTimeout) ? this._delayedCallTimeout : 100;
    }

    set delayedCallTimeout(value) {
        this._delayedCallTimeout = value;
    }

    /**
     * Returns delayed calls manager
     *
     * @return {Scheduler.data.util.DelayedCallsManager}
     * @internal
     */
    get delayedCallsManager() {
        this._delayedCallsManager = this._delayedCallsManager || new DelayedCallsManager({
            delayedCallTimeout : this.delayedCallTimeout
        });

        return this._delayedCallsManager;
    }

    setupRecurringTimeSpans() {
        const me = this;

        me.recurrenceIterators = me.recurrenceIterators || [];

        me.addRecurrenceIterators(
            DailyRecurrenceIterator,
            WeeklyRecurrenceIterator,
            MonthlyRecurrenceIterator,
            YearlyRecurrenceIterator
        );

        me.relayEvents(
            me.delayedCallsManager,
            [
                'delayedRegenerateOccurrencesStart',
                'delayedRegenerateOccurrencesEnd',
                'delayedGenerateOccurrencesStart',
                'delayedGenerateOccurrencesEnd'
            ]
        );

        me.delayedCallsManager.on({
            'delayedRegenerateOccurrencesEnd' : me.onDelayedRegenerateOccurrencesFinish,
            'delayedGenerateOccurrencesEnd'   : me.onDelayedGenerateOccurrencesFinish,

            thisObj : me
        });
    }

    doDestroy() {
        this.delayedCallsManager.destroy();
    }

    addRecurrenceIterators(...iterators) {
        iterators.forEach(iterator => this.recurrenceIterators[iterator.frequency] = iterator);
    }

    getRecurrenceIteratorForTimeSpan(timeSpan) {
        return this.recurrenceIterators[timeSpan.recurrence.frequency];
    }

    /**
     * Builds the provided timespan occurrences for the provided timespan.
     * @private
     */
    buildOccurrencesForTimeSpan(timeSpan, startDate, endDate, skipExisting) {
        const occurrences = [];

        // is recurring
        if (timeSpan.isRecurring && timeSpan.startDate) {
            const
                me         = this,
                recurrence = timeSpan.recurrence,
                iterator   = me.getRecurrenceIteratorForTimeSpan(timeSpan);

            //<debug>
            if (!iterator) {
                throw new Error(`Can't find iterator for ${recurrence.frequency} frequency`);
            }
            //</debug>

            const hasExceptionOnDate = timeSpan.exceptionDates ? timeSpan.exceptionDates.reduce((map, date) => {
                map[date.getTime()] = true;
                return map;
            }, {}) : {};

            iterator.forEachDate({
                recurrence,
                startDate,
                endDate,
                fn(date) {
                    // when it's told we don't generate occurrences if we already have ones on the calculated dates
                    if (!hasExceptionOnDate[date.getTime()] && (!skipExisting || !timeSpan.getOccurrenceByStartDate(date))) {
                        occurrences.push(timeSpan.buildOccurrence(date));
                    }
                }
            });
        }

        return occurrences;
    }

    mergeDelayedCallEntries(delayedCall) {
        let entries = delayedCall.entries,
            byTimeSpanId = {},
            startDate,
            endDate,
            timeSpans,
            timeSpan,
            args;

        // first get the largest range for each requested timeSpan
        for (let i = 0; i < entries.length; i++) {
            args      = entries[i];
            [timeSpans, startDate, endDate] = args;

            // Go over the timeSpans and merge this call and other ones arguments
            // so start date will be the minimal start date requested
            // and the end date the maximal end date requested
            // TODO: need to handle cases when ranges don't intersect
            for (let j = 0; j < timeSpans.length; j++) {
                timeSpan = timeSpans[j];

                const savedArgs = byTimeSpanId[timeSpan.id];

                // if we already met this timeSpan -> adjust its start/end date arguments
                if (savedArgs) {
                    if (savedArgs[1] > startDate) savedArgs[1] = startDate;
                    if (savedArgs[2] < endDate) savedArgs[2] = endDate;
                }
                // Arguments are:
                // 1) array of timeSpans
                // 2) start date
                // 3) end date
                // ...
                else {
                    byTimeSpanId[timeSpan.id] = [[timeSpan]].concat(args.slice(1));
                }
            }

        }

        // ranges are grouped by timeSpan id
        entries = Object.values(byTimeSpanId);

        // let's try to combine calls having the same ranges
        const combinedEntries = {};

        for (let i = 0; i < entries.length; i++) {
            args      = entries[i];

            [timeSpan, startDate, endDate] = args;

            const key = (startDate ? startDate.getTime() : '') + '-' + (endDate ? endDate.getTime() : '');

            // if this range is already met
            if (combinedEntries[key]) {
                // add timeSpan to the first argument
                combinedEntries[key][0] = combinedEntries[key][0].concat(timeSpan);

            // if this range isn't met yet
            // remember we met it using that call arguments
            }
            else {
                combinedEntries[key] = args;
            }
        }

        // use combined entries
        delayedCall.entries = Object.values(combinedEntries);
    }

    /**
     * Schedules regenerating (removing and building back) the occurrences of the provided recurring timespans in the provided time interval.
     * The method waits for {@link #property-delayedCallTimeout} milliseconds timeout during which it collects repeating calls.
     * Every further call restarts the timeout. After the timeout the method processes the collected calls trying to merge startDate/endDate ranges
     * to reduce the number of calls and then generates new occurrences and removes the previous ones.
     * @param   {Scheduler.model.TimeSpan[]} timeSpans Timespans to build occurrences for.
     * @param   {Date}                       startDate Time interval start.
     * @param   {Date}                       endDate   Time interval end.
     * @returns {Promise}
     * @async
     */
    regenerateOccurrencesForTimeSpansBuffered(timeSpans, startDate, endDate) {
        const me = this;

        if (!Array.isArray(timeSpans)) {
            timeSpans = [timeSpans];
        }

        // make sure we deal w/ recurring timeSpans only
        timeSpans = timeSpans.filter(timeSpan => timeSpan.isRecurring);

        if (timeSpans.length) {
            return new Promise(async(resolve, reject) => {
                const delayedCall = await me.delayedCallsManager.execute({
                    id       : 'regenerateOccurrences',
                    beforeFn : me.mergeDelayedCallEntries,
                    args     : [timeSpans, startDate, endDate],
                    fn(delayedCall, timeSpans, startDate, endDate) {
                        // Collect old occurrences we'll remove them later by using a single store.remove() call
                        const toRemove = delayedCall.toRemove = delayedCall.toRemove || [];

                        toRemove.push(...me.getOccurrencesForTimeSpans(timeSpans));

                        // add new occurrences
                        delayedCall.added = me.generateOccurrencesForTimeSpans(timeSpans, startDate, endDate, false);
                    },
                    // remove previous occurrences (if we have any)
                    afterFn(delayedCall) {
                        const { toRemove } = delayedCall;
                        if (toRemove.length) {
                            delayedCall.removed = me.remove(toRemove);
                        }
                    }
                });

                resolve({
                    added   : delayedCall.added,
                    removed : delayedCall.removed
                });
            });
        }

        return Promise.resolve();
    }

    /**
     * Schedules generating the occurrences of the provided recurring timespans in the provided time interval.
     * The method waits for {@link #property-delayedCallTimeout} milliseconds timeout during which it collects repeating calls.
     * Every further call restarts the timeout. After the timeout the method processes the collected calls trying to merge startDate/endDate ranges
     * to reduce the number of calls and then generates occurrences.
     * @param   {Scheduler.model.TimeSpan[]} timeSpans          Timespans to build occurrences for.
     * @param   {Date}                       startDate          Time interval start.
     * @param   {Date}                       endDate            Time interval end.
     * @param   {Boolean}                    [preserveExisting] `false` to generate occurrences even if there is already an existing one on a calculated date.
     * @returns {Promise}
     * @async
     */
    generateOccurrencesForTimeSpansBuffered(timeSpans, startDate, endDate, preserveExisting = true) {
        const me = this;

        if (!Array.isArray(timeSpans)) {
            timeSpans = [timeSpans];
        }

        // make sure we deal w/ recurring timeSpans only
        timeSpans = timeSpans.filter(timeSpan => timeSpan.isRecurring);

        if (timeSpans.length) {
            return new Promise(async(resolve) => {
                const delayedCall = await me.delayedCallsManager.execute({
                    id       : 'generateOccurrences',
                    beforeFn : me.mergeDelayedCallEntries,
                    args     : [timeSpans, startDate, endDate, preserveExisting],
                    fn(delayedCall, ...args) {
                        // add new occurrences
                        delayedCall.added = me.generateOccurrencesForTimeSpans(...args);
                    }
                });

                resolve({
                    added : delayedCall.added
                });
            });
        }

        return Promise.resolve();
    }

    /**
     * Generates occurrences of the provided recurring timespans in the provided time interval.
     * @param  {Scheduler.model.TimeSpan[]} timeSpans          Timespans to build occurrences for.
     * @param  {Date}                       startDate          Time interval start.
     * @param  {Date}                       endDate            Time interval end.
     * @param  {Boolean}                    [preserveExisting] `false` to generate occurrences even if there is already an existing one on a calculated date.
     * @private
     */
    generateOccurrencesForTimeSpans(timeSpans, startDate, endDate, preserveExisting = true) {
        const allOccurrences = [];

        if (timeSpans) {
            const me = this;

            let occurrences = [];

            if (!Array.isArray(timeSpans)) {
                timeSpans = [timeSpans];
            }

            if (timeSpans.length) {

                for (let i = 0; i < timeSpans.length; i++) {

                    let timeSpan = timeSpans[i],
                        firstOccurrenceStartDate,
                        firstOccurrence,
                        eventStartDate;

                    if ((occurrences = me.buildOccurrencesForTimeSpan(timeSpan, startDate, endDate, preserveExisting))) {

                        eventStartDate = timeSpan.startDate;

                        // If requested [startDate, endDate] range starts before or matches the timespan starts
                        // we treat the first built occurrence as the timespan itself
                        // and if the occurrence start doesn't match the timespan start
                        // we move the timespan accordingly
                        if (startDate <= eventStartDate) {
                            // get 1st occurrence
                            if ((firstOccurrence = occurrences.shift())) {
                                firstOccurrenceStartDate = firstOccurrence.startDate;
                                // compare its start date with the event one and shift the event if needed
                                if (firstOccurrenceStartDate - eventStartDate) {
                                    timeSpan.setStartEndDate(firstOccurrenceStartDate, firstOccurrence.endDate);
                                    // Since we've changed the event start date the recurrence "Days"/"MonthDays"/"Months"
                                    // might get redundant in case the event start date matches the fields values
                                    // Calling recurrence sanitize() will clean the fields in this case.
                                    timeSpan.recurrence.sanitize();
                                }
                            }
                        }

                        allOccurrences.push(...occurrences);
                    }
                }

                if (allOccurrences.length) {
                    me.add(allOccurrences);
                }
            }
        }

        return allOccurrences;
    }

    /**
     * Generates occurrences for all the existing recurring timespans in the provided time interval.
     * @param  {Date}    startDate          Time interval start.
     * @param  {Date}    endDate            Time interval end.
     * @param  {Boolean} [preserveExisting] `true` to not generate occurrences if there are already existing ones on the calculated dates.
     * @private
     */
    generateOccurrencesForAll(startDate, endDate, preserveExisting = false) {
        const me   = this,
            timeSpans = me.getRecurringTimeSpans();

        let result = [];

        if (timeSpans.length) {
            me.trigger('generateOccurrencesAllStart', { timeSpans, startDate, endDate, preserveExisting });

            result = me.generateOccurrencesForTimeSpans(timeSpans, startDate, endDate, preserveExisting);

            me.trigger('generateOccurrencesAllEnd', { timeSpans, startDate, endDate, preserveExisting });
        }

        return result;
    }

    /**
     * Returns all the recurring timespans.
     * @return {Scheduler.model.TimeSpan[]} Array of recurring events.
     */
    getRecurringTimeSpans() {
        return this.query(record => record.supportsRecurring && record.isRecurring);
    }

    /**
     * Returns occurrences of the provided recurring timespans.
     * @param  {Scheduler.model.TimeSpan|Scheduler.model.TimeSpan[]} records Recurring timespans which occurrences should be retrieved.
     * @return {Scheduler.model.TimeSpan[]} Array of the provided timespans occurrences.
     */
    getOccurrencesForTimeSpans(records) {
        const result = [];

        if (!Array.isArray(records)) {
            records = [records];
        }

        if (records.length) {
            for (let i = 0; i < records.length; i++) {
                const recordId = records[i].id;

                // TODO: cache
                result.push(...this.query(record => record.supportsRecurring && record.recurringTimeSpanId == recordId));
            }
        }

        return result;
    }

    /**
     * Returns occurrences of all the existing recurring timespans.
     * @return {Scheduler.model.TimeSpan[]} Array of the occurrences.
     */
    getOccurrencesForAll() {
        return this.query(record => record.supportsRecurring && record.isOccurrence);
    }

    /**
     * Removes occurrences of the provided recurring timespans.
     * @param {Scheduler.model.TimeSpan|Scheduler.model.TimeSpan[]} timeSpans Recurring timespans which occurrences should be removed.
     */
    removeOccurrencesForTimeSpans(timeSpans) {
        return this.remove(this.getOccurrencesForTimeSpans(timeSpans));
    }

    /**
     * Removes occurrences of all the existing recurring events.
     */
    removeOccurrencesForAll() {
        return this.remove(this.getOccurrencesForAll());
    }

    onDelayedRegenerateOccurrencesFinish() {
        /**
         * Fires when occurrences building is done. This happens:
         *
         * - after panel got rendered;
         * - on timespans store `refresh`, `add`, `update` and `remove` events;
         * - on visible timespan change.
         * @event occurrencesReady
         * @param {Core.data.Store} source Timespans store.
         */
        this.trigger('occurrencesReady');
    }

    onDelayedGenerateOccurrencesFinish() {
        this.trigger('occurrencesReady');
    }

};
