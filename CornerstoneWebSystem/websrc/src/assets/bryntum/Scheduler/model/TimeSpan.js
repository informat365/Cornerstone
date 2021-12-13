import Model from '../../Core/data/Model.js';
import DomClassList from '../../Core/helper/util/DomClassList.js';
import { default as DH } from '../../Core/helper/DateHelper.js';
import Duration from '../util/Duration.js';

/**
 * @module Scheduler/model/TimeSpan
 */

/**
 * This class represent a simple date range. It is being used in various subclasses and plugins which operate on date ranges.
 *
 * Its a subclass of  {@link Core.data.Model}.
 * Please refer to documentation of those classes to become familar with the base interface of this class.
 *
 * A TimeSpan has the following fields:
 *
 * - `startDate`    - start date of the task in the ISO 8601 format
 * - `endDate`      - end date of the task in the ISO 8601 format (not inclusive)
 * - `duration`     - duration, time between start date and end date
 * - `durationUnit` - unit used to express the duration
 * - `name`         - an optional name of the range
 * - `cls`          - an optional CSS class to be associated with the range.
 *
 * The data source of any field can be customized in the subclass. Please refer to {@link Core.data.Model} for details. To specify
 * another date format:
 *
 * ```javascript
 * class MyTimeSpan extends TimeSpan {
 *   static get fields() {
 *      { name: 'startDate', type: 'date', dateFormat: 'DD/MM/YY' }
 *   }
 * }
 * ```
 *
 * @extends Core/data/Model
 */
export default class TimeSpan extends Model {
    //region Field definitions

    static get fields() {
        return [
            /**
             * The start date of a time span (or Event / Task).
             *
             * Uses {@link Core/helper/DateHelper#property-defaultFormat-static DateHelper.defaultFormat} to convert a
             * supplied string to a Date. To specify another format, either change that setting or subclass TimeSpan and
             * change the dateFormat for this field.
             *
             * @field {String|Date} startDate
             */
            { name : 'startDate', type : 'date' },

            /**
             * The end date of a time span (or Event / Task).
             *
             * Uses {@link Core/helper/DateHelper#property-defaultFormat-static DateHelper.defaultFormat} to convert a
             * supplied string to a Date. To specify another format, either change that setting or subclass TimeSpan and
             * change the dateFormat for this field.
             *
             * @field {String|Date} endDate
             */
            { name : 'endDate', type : 'date' },

            /**
             * The numeric part of the timespan's duration (the number of units).
             * @field {Number} duration
             */
            { name : 'duration', type : 'number', allowNull : true },

            /**
             * The unit part of the TimeSpan duration, defaults to "d" (days). Valid values are:
             *
             * - "ms" (milliseconds)
             * - "s" (seconds)
             * - "m" (minutes)
             * - "h" (hours)
             * - "d" (days)
             * - "w" (weeks)
             * - "M" (months)
             * - "y" (years)
             *
             * This field is readonly after creation, to change durationUnit use #setDuration().
             * @field {String} durationUnit
             */
            {
                name         : 'durationUnit',
                type         : 'string',
                defaultValue : 'd'
            },

            {
                name : 'fullDuration'
            },

            /**
             * An encapsulation of the CSS classes to add to the rendered time span element.
             * @field {Core.helper.util.DomClassList|String} cls
             *
             * This may be accessed as a string, but for granular control of adding and
             * removing individual classes, it is recommended to use the
             * {@link Core.helper.util.DomClassList DomClassList} API.
             */
            {
                name         : 'cls',
                defaultValue : ''
            },

            /**
             * A CSS style string (applied to `style.cssText`) or object (applied to `style`)
             * ```
             * record.style = 'color: red;font-weight: 800';
             * ```
             *
             * @field {String} style
             */
            {
                name : 'style',
                type : 'object'
            },

            /**
             * The name of the time span (or Event / Task)
             * @field {String} name
             */
            { name : 'name', type : 'string' }
        ];
    }

    //endregion

    //region Init

    afterConstruct() {
        super.afterConstruct();

        // This should probably be a property setter of some mandatory config, then we would not need an afterConfigure implementation.
        this.normalize();
    }

    normalize() {
        const me                                             = this,
            { startDate, endDate, duration, durationUnit } = me,
            hasDuration                                    = duration != null;

        // need to calculate duration (checking first since seemed most likely to happen)
        if (startDate && endDate && !hasDuration) {
            me.setData('duration', DH.diff(startDate, endDate, durationUnit, true));
        }
        // need to calculate endDate?
        else if (startDate && !endDate && hasDuration) {
            me.setData('endDate', DH.add(startDate, duration, durationUnit));
        }
        // need to calculate startDate
        else if (!startDate && endDate && hasDuration) {
            me.setData('startDate', DH.add(endDate, -duration, durationUnit));
        }

        this.clearCachedValues();
    }

    //endregion

    //region Getters & Setters

    get cls() {
        if (!this._cls) {
            this._cls = new DomClassList(super.get('cls'));
        }
        return this._cls;
    }

    set cls(cls) {
        const me = this;

        if (me._cls) {
            me._cls.value = cls;
        }
        else {
            me._cls = new DomClassList(cls);
        }
        me.set('cls', me._cls.value);
    }

    get startDate() {
        return this.get('startDate');
    }

    set startDate(date) {
        this.setStartDate(date);
    }

    get endDate() {
        return this.get('endDate');
    }

    set endDate(date) {
        this.setEndDate(date);
    }

    get duration() {
        return this.get('duration');
    }

    set duration(duration) {
        this.setDuration(duration, this.durationUnit);
    }

    get durationUnit() {
        return this.get('durationUnit');
    }

    /**
     * Sets duration and durationUnit in one go. Only allowed way to change durationUnit, the durationUnit field is
     * readonly after creation
     * @param {Number} duration Duration value
     * @param {String} durationUnit Unit for specified duration value, see {@link #field-durationUnit} for valid values
     */
    setDuration(duration, durationUnit = this.durationUnit) {
        // Must be a number
        duration = parseFloat(duration);

        const toSet = {
            duration,
            durationUnit
        };

        if (this.startDate) {
            toSet.endDate = DH.add(this.startDate, duration, durationUnit);
        }
        else if (this.endDate) {
            toSet.startDate = DH.add(this.endDate, -duration, durationUnit);
        }

        this.set(toSet);
    }

    /**
     * Property which encapsulates the duration's magnitude and units.
     */
    get fullDuration() {
        // Used for formatting during export
        return new Duration({
            unit      : this.durationUnit,
            magnitude : this.duration
        });
    }

    set fullDuration(duration) {
        if (typeof duration === 'string') {
            duration = DH.parseDuration(duration, true, this.durationUnit);
        }

        this.setDuration(duration.magnitude, duration.unit);
    }

    /**
     * Sets the range start date
     *
     * @param {Date} date The new start date
     * @param {Boolean} keepDuration Pass `true` to keep the duration of the task ("move" the event), `false` to change the duration ("resize" the event).
     * Defaults to `true`
     */
    setStartDate(date, keepDuration = true) {
        const me       = this,
            toSet = {
                startDate : date
            };

        if (date) {
            let calcEndDate;

            if (keepDuration) {
                calcEndDate = me.duration != null;
            }
            else {
                if (me.endDate) {
                    toSet.duration = DH.diff(date, me.endDate, me.durationUnit, true);

                    if (toSet.duration < 0) throw new Error('Negative duration');
                }
                else {
                    calcEndDate = this.duration != null;
                }
            }

            if (calcEndDate) {
                toSet.endDate = DH.add(date, me.duration, me.durationUnit);
            }
        }
        else {
            toSet.duration = null;
        }

        me.set(toSet);
    }

    /**
     * Sets the range end date
     *
     * @param {Date} date The new end date
     * @param {Boolean} keepDuration Pass `true` to keep the duration of the task ("move" the event), `false` to change the duration ("resize" the event).
     * Defaults to `false`
     */
    setEndDate(date, keepDuration = false) {
        const me        = this,
            toSet = {
                endDate : date
            };

        if (date) {
            let calcStartDate;

            if (keepDuration === true) {
                calcStartDate = me.duration != null;
            }
            else {
                if (me.startDate) {
                    toSet.duration = DH.diff(me.startDate, date, me.durationUnit, true);

                    if (toSet.duration < 0) throw new Error('Negative duration');
                }
                else {
                    calcStartDate = this.duration != null;
                }
            }

            if (calcStartDate) {
                toSet.startDate = DH.add(date, -me.duration, me.durationUnit);
            }
        }

        me.set(toSet);
    }

    /**
     * Sets the event start and end dates
     *
     * @param {Date} start The new start date
     * @param {Date} end The new end date
     */
    setStartEndDate(start, end) {
        this.set({
            startDate : start,
            endDate   : end
        });
    }

    /**
     * Returns an array of dates in this range. If the range starts/ends not at the beginning of day, the whole day will be included.
     * @return {Date[]}
     */
    get dates() {
        const dates     = [],
            startDate = DH.startOf(this.startDate, 'day'),
            endDate   = this.endDate;

        for (let date = startDate; date < endDate; date = DH.add(date, 1, 'day')) {
            dates.push(date);
        }

        return dates;
    }

    /**
     * Returns the duration of this Event in milliseconds.
     * @private
     */
    get durationMS() {
        if (this.endDate && this.startDate) {
            return this.endDateMS - this.startDateMS;
        }
        else {
            return DH.asMilliseconds(this.duration || 0, this.durationUnit);
        }
    }

    // Caching isMilestone, startDate and endDate ms conversion since it costs a bit during rendering
    clearCachedValues() {
        this._startDateMS = null;
        this._endDateMS = null;
        this._isMilestone = null;
    }

    get endDateMS() {
        const me = this;

        if (me._endDateMS == null) {
            me._endDateMS = me.endDate && me.endDate.getTime();
        }

        return me._endDateMS;
    }

    get startDateMS() {
        const me = this;

        if (me._startDateMS == null) {
            me._startDateMS = me.startDate && me.startDate.getTime();
        }

        return me._startDateMS;
    }

    get isMilestone() {
        const me = this;

        if (me._isMilestone == null) {
            me._isMilestone = me.endDateMS === me.startDateMS;
        }

        return me._isMilestone;
    }

    inSetNormalize(field) {
        if (typeof field !== 'string') {
            // If user is updating multiple properties in one go using an object, we help out
            // by filling out missing schedule related data
            field = Object.assign({}, field);

            if ('duration' in field) {
                if (field.startDate && !field.endDate) {
                    field.endDate = DH.add(field.startDate, field.duration, field.durationUnit || this.durationUnit, true, true);
                }

                if (!field.startDate && field.endDate) {
                    field.startDate = DH.add(field.endDate, -field.duration, field.durationUnit || this.durationUnit, true, true);
                }
            }
            else if (field.startDate && field.endDate) {
                field.duration = DH.diff(field.startDate, field.endDate, field.durationUnit || this.durationUnit, true);
            }
            return field;
        }
    }

    inSet(field, value, silent, fromRelationUpdate) {
        this.clearCachedValues();
        field = this.inSetNormalize(field) || field;
        return super.inSet(field, value, silent, fromRelationUpdate);
    }

    //endregion

    //region Iteration

    /**
     * Iterates over the {@link #property-dates}
     * @param {Function} func The function to call for each date
     * @param {Object} thisObj `this` reference for the function
     */
    forEachDate(func, thisObj) {
        return this.dates.forEach(func.bind(thisObj));
    }

    //endregion

    /**
     * Checks if the range record has both start and end dates set and start <= end
     *
     * @return {Boolean}
     */
    get isScheduled() {
        const me = this;
        return Boolean(me.startDate && me.endDate && me.hasValidDates);
    }

    // Simple check if end date is greater than start date
    get isValid() {
        let me     = this,
            result = true; //super.isValid(),

        if (result) {
            let start = me.startDate,
                end   = me.endDate;
            result = !start || !end || (end - start >= 0);
        }

        return result;
    }

    // Simple check if just end date is greater than start date
    get hasValidDates() {
        let me    = this,
            start = me.startDateMS,
            end   = me.endDateMS;

        return !start || !end || (end - start >= 0);
    }

    /**
     * Shift the dates for the date range by the passed amount and unit
     * @param {String} unit The unit to shift by, see {@Core.helper.DateHelper} for more information on valid formats.
     * @param {Number} amount The amount to shift
     */
    shift(amount, unit = this.durationUnit) {
        // TODO REMOVE FOR 2.0
        if (typeof amount === 'string') {
            const u = amount;

            amount = unit;
            unit = u;
        }

        this.setStartDate(DH.add(this.startDate, amount, unit, true), true);
    }

    /**
     * Returns the WBS code of this model (only relevant when it's part of a tree store).
     * @return {String} The WBS code string
     * @private
     */
    get wbsCode() {
        return this.indexPath.join('.');
    }

    fullCopy() {
        //NOT PORTED

        return this.copy.apply(this, arguments);
    }

    intersects(timeSpan) {
        return this.intersectsRange(timeSpan.startDate, timeSpan.endDate);
    }

    intersectsRange(start, end) {
        let myStart = this.startDate,
            myEnd   = this.endDate;

        return myStart && myEnd && DH.intersectSpans(myStart, myEnd, start, end);
    }

    /**
     * Splits this event into two pieces at the desired position.
     *
     * @param {Number} splitPoint A number greater than 0 and less than 1, indicating how this event will be split. 0.5 means cut it in half
     * @return {Scheduler.model.TimeSpan} The newly created split section of the timespan
     */
    split(splitPoint = 0.5) {
        const me              = this,
            clone           = this.copy(),
            eventStore      = me.firstStore,
            assignmentStore = eventStore && eventStore.assignmentStore,
            ownNewDuration  = me.duration * splitPoint,
            cloneDuration   = me.duration - ownNewDuration;

        if (splitPoint <= 0 || splitPoint >= 1) {
            throw new Error('Split point must be > 0 and < 1');
        }

        me.duration = ownNewDuration;
        clone.startDate = me.endDate;
        clone.duration = cloneDuration;

        if (eventStore) {
            eventStore.add(clone);
        }

        if (assignmentStore) {
            assignmentStore.add(
                me.assignments.map((assignment) => {
                    const clonedData   = Object.assign({}, assignment.data, { eventId : clone.id });
                    delete clonedData.id;

                    return clonedData;
                })
            );
        }

        return clone;
    }
}
