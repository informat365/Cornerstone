import TimeSpan from '../TimeSpan.js';
import RecurrenceModel from '../RecurrenceModel.js';
import DateHelper from '../../../Core/helper/DateHelper.js';

function convertExceptionDatesValue(value) {
    if (value) {
        const dateFormat = this.dateFormat;

        value = typeof value == 'string' ? value.split(',') : value;

        value = value.map(item => {
            if (typeof item == 'string') {
                item = DateHelper.parse(item, dateFormat);
            }

            return item;
        });
    }

    return value;
}

/**
 * @module Scheduler/model/mixin/RecurringTimeSpan
 */

/**
 * This mixin class provides recurrence related fields and methods to a {@link Scheduler.model.TimeSpan timespan model}.
 *
 * The mixin introduces two types of timespans: __recurring timespan__ and its __occurrences__.
 * __Recurring timespan__ is a timespan having {@link #field-recurrenceRule recurrence rule} specified and its __occurrences__ are "fake" dynamically generated timespans.
 * Their set depends on the scheduler visible timespan and changes upon the timespan change.
 *
 * There are few methods allowing to distinguish a recurring event and an occurrence: {@link #property-isRecurring}, {@link #property-isOccurrence}
 * and {@link #property-recurringTimeSpan} (returns the event this record is an occurrence of).
 *
 * The {@link #field-recurrenceRule recurrence rule} defined for the event is parsed and
 * represented with {@link Scheduler.model.RecurrenceModel} class (can be changed by setting {@link #property-recurrenceModel} property) instance.
 * See: {@link #property-recurrence} property.
 * @mixin
 */
export default Target => class RecurringTimeSpan extends (Target || TimeSpan) {

    /**
     * Returns `true` indicating the timespan supports recurring.
     * @property {Boolean} true
     */
    get supportsRecurring() {
        return true;
    }

    static get fields() {
        return [
            /**
             * Identifier of the "main" timespan this model is an occurrence of.
             * **Applicable to occurrences only.**
             * @field {String|Number} recurringTimeSpanId
             */
            { name : 'recurringTimeSpanId' },
            /**
             * The timespan recurrence rule. A string in [RFC-5545](https://tools.ietf.org/html/rfc5545#section-3.3.10) described format ("RRULE" expression).
             * @field {String} recurrenceRule
             */
            { name : 'recurrenceRule' },
            /**
             * The timespan exception dates. The dates that must be skipped when generating occurrences for a repeating timespan.
             * This is used to modify only individual occurrences of the timespan so the further regenerations
             * won't create another copy of this occurrence again.
             * Use {@link #function-addExceptionDate} method to add an individual entry to the dates array:
             *
             * ```javascript
             * // let the main timespan know that this date should be skipped when regenerating the occurrences
             * occurrence.recurringTimeSpan.addExceptionDate( occurrence.startDate );
             *
             * // cut the main event cord
             * occurrence.recurringTimeSpanId = null;
             *
             * // now the occurrence is an individual record that can be changed & persisted freely
             * occurrence.setStartEndDate(new Date(2018, 6, 2), new Date(2018, 6, 3));
             * ```
             * **Note:** The dates in this field get automatically removed when the event changes its {@link Scheduler.model.TimeSpan#field-startDate start date}.
             *
             * @field {Date[]} exceptionDates
             */
            { name : 'exceptionDates', convert : convertExceptionDatesValue }
        ];
    }

    /**
     * Name of the class representing the recurrence model.
     * @property {String} [recurrenceModel=Scheduler.model.RecurrenceModel]
     */
    get recurrenceModel() {
        return this._recurrenceModel || RecurrenceModel;
    }

    set recurrenceModel(model) {
        this._recurrenceModel = model;
    }

    /**
     * Sets a recurrence for the timespan with a given frequency, interval, and end.
     * @param {String/Object/Scheduler.model.RecurrenceModel} frequency The frequency of the recurrence, configuration object or the recurrence model. The frequency can be `DAILY`, `WEEKLY`, `MONTHLY`, or `YEARLY`.
     *
     * ```javascript
     * // repeat the event every other week till Jan 2 2039
     * event.setRecurrence("WEEKLY", 2, new Date(2039, 0, 2));
     * ```
     *
     * Also a {@link Scheduler.model.RecurrenceModel recurrence model} can be provided as the only argument for this method:
     *
     * ```javascript
     * const recurrence = new RecurrenceModel({ frequency : 'DAILY', interval : 5 });
     *
     * event.setRecurrence(recurrence);
     * ```
     *
     * @param {Number} [interval] The interval between occurrences (instances of this recurrence). For example, a daily recurrence with an interval of 2 occurs every other day. Must be greater than 0.
     * @param {Number|Date} [recurrenceEnd] The end of the recurrence. The value can be specified by a date or by a maximum count of occurrences (has to greater than 1, since 1 means the event itself).
     */
    setRecurrence(frequency, interval, recurrenceEnd) {
        const me = this;

        let recurrence,
            recurrenceRule;

        if (frequency) {
            if (frequency.isRecurrenceModel) {
                recurrence = frequency;
            }
            else if (typeof frequency == 'string') {
                recurrence = new this.recurrenceModel();

                recurrence.frequency = frequency;
                if (interval) {
                    recurrence.interval = interval;
                }

                // if the recurrence is limited
                if (recurrenceEnd) {
                    if (recurrenceEnd instanceof Date) {
                        recurrence.endDate = recurrenceEnd;
                    }
                    else {
                        recurrence.count = recurrenceEnd;
                    }
                }
            }
            else {
                recurrence = new this.recurrenceModel(frequency);
            }

            recurrence.timeSpan = me;

            recurrenceRule = recurrence.rule;
        }

        me.recurrence     = recurrence;
        me.recurrenceRule = recurrenceRule;
    }

    /**
     * The recurrence model used for the timespan.
     * @property {Scheduler.model.RecurrenceModel}
     */
    get recurrence() {
        const
            me = this,
            rule = me.recurrenceRule;

        if (!me._recurrence && rule) {
            me._recurrence = new me.recurrenceModel({ rule, timeSpan : me });
        }

        return me._recurrence;
    }

    set recurrence(recurrence) {
        const me = this;

        let previousRecurrence;

        // If this is an occurrence - turn it into an event first
        if (me.isOccurrence) {
            const recurringTimeSpan = me.recurringTimeSpan;
            previousRecurrence      = recurringTimeSpan && recurringTimeSpan.recurrence;
            me.recurringTimeSpanId  = null;
        }

        if (recurrence) {
            // if we set recurrence on an occurrence model
            // we stop previous main recurrence
            if (previousRecurrence) {
                previousRecurrence.endDate = new Date(me.startDate - 1);
            }
        }

        me._recurrence = recurrence;

        if (recurrence) {
            // bind recurrence instance to the model
            recurrence.timeSpan = me;
            me.recurrenceRule   = recurrence.rule;
        }
        else {
            me.recurrenceRule = null;
        }
    }

    /**
     * Indicates if the timespan is recurring.
     * @property {Boolean}
     * @readonly
     */
    get isRecurring() {
        return this.recurrence && !this.isOccurrence;
    }

    /**
     * Indicates if the timespan is an occurrence of another recurring timespan.
     * @property {Boolean}
     * @readonly
     */
    get isOccurrence() {
        return Boolean(this.recurringTimeSpanId);
    }

    /**
     * The "main" timespan this model is an occurrence of. For non-occurrences returns `null`.
     * @property {Scheduler.model.TimeSpan}
     * @readonly
     */
    get recurringTimeSpan() {
        const masterEventId = this.recurringTimeSpanId,
            store           = this.stores[0];

        return masterEventId && store && store.getById(masterEventId);
    }

    getOccurrenceByStartDate(startDate) {
        let result, occurrences;

        if (startDate) {
            occurrences = this.occurrences;

            for (let i = 0; i < occurrences.length; i++) {
                if (occurrences[i].startDate - startDate === 0) {
                    result = occurrences[i];
                    break;
                }
            }
        }

        return result;
    }

    /**
     * List of this recurring timespan occurrences or `null` if the timespan is not recurring.
     * @property {Scheduler.model.TimeSpan[]}
     * @readonly
     */
    get occurrences() {
        const store = this.stores[0];

        return store && store.getOccurrencesForTimeSpans && store.getOccurrencesForTimeSpans(this);
    }

    /**
     * Removes this recurring timespan occurrences.
     */
    removeOccurrences() {
        const store = this.stores[0];

        return store && store.removeOccurrencesForTimeSpans && store.removeOccurrencesForTimeSpans(this);
    }

    /**
     * The method is triggered when the timespan recurrence settings get changed.
     * It updates the {@link #field-recurrenceRule} field in this case.
     * @protected
     */
    onRecurrenceChanged() {
        this.recurrenceRule = this.recurrence && this.recurrence.rule || null;
    }

    /**
     * Builds this record occurrence by cloning the timespan data.
     * The method is used internally by the __recurring events__ feature.
     * Override it if you need to customize the generated occurrences.
     * @param  {Date} startDate The occurrence start date.
     * @return {Scheduler.model.TimeSpan} The occurrence.
     * @protected
     */
    buildOccurrence(startDate) {
        const copy = this.copy(null);

        copy.beginBatch();
        copy.setStartDate(startDate);
        copy.recurringTimeSpanId = this.id;
        copy.endBatch();

        return copy;
    }

    afterChange(toSet, wasSet, silent, ...args) {
        // reset cached recurrence instance in case "recurrenceRule" is changed
        if ('recurrenceRule' in wasSet) {
            this._recurrence = null;
        }

        return super.afterChange(toSet, wasSet, silent, ...args);
    }

    /**
     * Adds an exception date that should be skipped when generating occurrences for the timespan.
     * The methods adds an entry to the array kept in {@link #field-exceptionDates} field.
     * @param {Date} date Exception date.
     */
    addExceptionDate(date) {
        const me  = this,
            dates = me.exceptionDates || [];

        if (date) {
            me.exceptionDates = dates.concat(date);
        }
    }

    beforeStartDateChange() {
        this._startDateValue = this.startDate;
    }

    afterStartDateChange() {
        if (this._startDateValue - this.startDate && this.exceptionDates) {
            this.exceptionDates = null;
        }
    }

};
