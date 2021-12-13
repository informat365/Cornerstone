import Base from '../../../Core/Base.js';
import RecurringTimeSpansMixin from './RecurringTimeSpansMixin.js';

/**
 * @module Scheduler/data/mixin/RecurringEventsMixin
 */

/**
 * This mixin class provides recurring events functionality to the {@link Scheduler.data.EventStore event store}.
 * @extends Scheduler/data/mixin/RecurringTimeSpansMixin
 * @mixin
 */
export default Target => class RecurringEventsMixin extends RecurringTimeSpansMixin(Target || Base) {

    static get $name() {
        return 'RecurringEventsMixin';
    }

    /**
     * Indicates that the store supports recurring events.
     * @default true
     * @property {Boolean}
     * @readonly
     */
    get supportsRecurringEvents() {
        return true;
    }

    setupRecurringEvents(...args) {
        return this.setupRecurringTimeSpans(...args);
    }

    generateOccurrencesForEvents(...args) {
        return this.generateOccurrencesForTimeSpans(...args);
    }

    /**
     * Schedules generating the occurrences of the provided recurring events in the provided time interval.
     * The method waits for {@link Scheduler.data.mixin.RecurringTimeSpansMixin#property-delayedCallTimeout} milliseconds timeout during which it collects repeating calls.
     * Every further call restarts the timeout. After the timeout the method processes the collected calls trying to merge startDate/endDate ranges
     * to reduce the number of calls and then launches occurrences generation.
     * @param  {Scheduler.model.EventModel[]} events        Events to build occurrences for.
     * @param  {Date}              startDate                Time interval start.
     * @param  {Date}              endDate                  Time interval end.
     * @param  {Boolean}           [preserveExisting=true]  `false` to generate occurrences even if there is already an existing one on a calculated date.
     * @returns {Promise}
     * @async
     */
    generateOccurrencesForEventsBuffered(...args) {
        return this.generateOccurrencesForTimeSpansBuffered(...args);
    }

    regenerateOccurrencesForEvents(...args) {
        return this.regenerateOccurrencesForTimeSpans(...args);
    }

    /**
     * Schedules regenerating (removing and building back) the occurrences of the provided recurring events in the provided time interval.
     * The method waits for {@link Scheduler.data.mixin.RecurringTimeSpansMixin#property-delayedCallTimeout} milliseconds timeout during which it collects repeating calls.
     * Every further call restarts the timeout. After the timeout the method processes the collected calls trying to merge startDate/endDate ranges
     * to reduce the number of calls and then launches new occurrences generation and removes the previous ones.
     * @param  {Scheduler.model.EventModel[]} events    Events to build occurrences for.
     * @param  {Date}                         startDate Time interval start.
     * @param  {Date}                         endDate   Time interval end.
     * @returns {Promise}
     * @async
     */
    regenerateOccurrencesForEventsBuffered(...args) {
        return this.regenerateOccurrencesForTimeSpansBuffered(...args);
    }

    /**
     * Returns all the recurring events.
     *
     * **An alias for ** {@link Scheduler.data.mixin.RecurringTimeSpansMixin#function-getRecurringTimeSpans} method.
     *
     * @return {Scheduler.model.EventModel[]} Array of recurring events.
     */
    getRecurringEvents() {
        return this.getRecurringTimeSpans();
    }

    /**
     * Returns occurrences of the provided recurring events.
     *
     * **An alias for ** {@link Scheduler.data.mixin.RecurringTimeSpansMixin#function-getOccurrencesForTimeSpans} method.
     *
     * @param  {Scheduler.model.EventModel|Scheduler.model.EventModel[]} events Recurring events which occurrences should be retrieved.
     * @return {Scheduler.model.EventModel[]} Array of the provided events occurrences.
     */
    getOccurrencesForEvents(events) {
        return this.getOccurrencesForTimeSpans(events);
    }

    /**
     * Removes occurrences of the provided recurring events.
     *
     * **An alias for ** {@link Scheduler.data.mixin.RecurringTimeSpansMixin#function-removeOccurrencesForTimeSpans} method.
     *
     * @param {Scheduler.model.EventModel|Scheduler.model.EventModel[]} events Recurring events which occurrences should be removed.
     */
    removeOccurrencesForEvents(events) {
        return this.removeOccurrencesForTimeSpans(events);
    }

    isEventPersistable(event) {
        // occurrences are not persistable
        return super.isEventPersistable(event) && (!event.supportsRecurring || !event.isOccurrence);
    }
};
