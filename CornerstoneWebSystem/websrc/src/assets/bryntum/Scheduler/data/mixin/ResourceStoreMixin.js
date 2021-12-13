import Base from '../../../Core/Base.js';

/**
 * @module Scheduler/data/mixin/ResourceStoreMixin
 */

/**
 * This is a mixin for the ResourceStore functionality. It is consumed by the {@link Scheduler.data.ResourceStore}.
 *
 * @mixin
 */
export default Target => class ResourceStoreMixin extends (Target || Base) {
    /**
     * Get/set the associated event store instance
     *
     * @property {Scheduler.data.EventStore}
     */
    get eventStore() {
        return this._eventStore;
    }

    set eventStore(eventStore) {
        const me = this;

        if (me._eventStore !== eventStore) {
            const oldStore = me._eventStore;
            me._eventStore = eventStore || null;

            if (eventStore && !eventStore.resourceStore) {
                eventStore.resourceStore = me;
            }

            /**
             * Fires when new event store is set via {@link #property-eventStore} method.
             * @event eventstorechange
             * @param {Scheduler.data.ResourceStore}   this
             * @param {Scheduler.data.EventStore} newEventStore
             * @param {Scheduler.data.EventStore} oldEventStore
             */
            me.trigger('eventStoreChange', { newEventStore : eventStore, oldEventStore : oldStore });
        }
    }

    getScheduledEventsInTimeSpan(start, end, eventStore = this.eventStore) {
        // TODO: PORT check if correct
        return this.reduce((events, resource) => {
            events.concat(eventStore.getEventsForResource(resource).reduce((events, event) => {
                if (event.intersectsRange(start, end)) events.push(event);
            }));
        });
    }
};
