import AjaxStore from '../../Core/data/AjaxStore.js';
import Model from '../../Core/data/Model.js';
import AssignmentModel from '../model/AssignmentModel.js';

/**
 * @module Scheduler/data/AssignmentStore
 */

/**
 * A class representing a collection of assignments between events in the {@link Scheduler.data.EventStore} and resources
 * in the {@link Scheduler.data.ResourceStore}.
 *
 * Contains a collection of {@link Scheduler.model.AssignmentModel} records.
 *
 * @extends Core/data/Store
 */
export default class AssignmentStore extends AjaxStore {
    static get defaultConfig() {
        return {
            /**
             * CrudManager must load stores in the correct order. Lowest first.
             * @private
             */
            loadPriority : 300,
            /**
             * CrudManager must sync stores in the correct order. Lowest first.
             * @private
             */
            syncPriority : 300,

            modelClass : AssignmentModel
        };
    }

    //region Init & destroy

    doDestroy() {
        const me = this;
        me.eventStoreDetacher && me.eventStoreDetacher();
        me.resourceStoreDetacher && me.resourceStoreDetacher();
        super.doDestroy();
    }

    //endregion

    //region Stores

    /**
     * Get/set the associated event store instance.  Usually it is configured automatically, by the event store itself.
     *
     * @param {Scheduler.data.EventStore}
     */
    get eventStore() {
        return this._eventStore;
    }

    set eventStore(eventStore) {
        const me       = this,
            oldStore = me._eventStore;

        me._eventStore = eventStore || null;

        me.attachToEventStore(me._eventStore);

        if ((oldStore || eventStore) && oldStore !== eventStore) {
            /**
             * Fires when new event store is set via {@link #property-eventStore} method.
             * @event eventstorechange
             * @param {Scheduler.data.AssignmentStore} this
             * @param {Scheduler.data.EventStore} newEventStore
             * @param {Scheduler.data.EventStore} oldEventStore
             */
            me.trigger('eventStoreChange', { newEventStore : eventStore, oldEventStore : oldStore });
        }
    }

    attachToEventStore(eventStore) {
        const me = this;

        me.eventStoreDetacher && me.eventStoreDetacher();

        if (eventStore) {
            me.eventStoreDetacher = eventStore.on({
                'remove'              : me.onEventRemove,
                'resourcestorechange' : me.onEventStoreResourceStoreChange,
                thisObj               : me,
                prio                  : 200 // higher then in cache, we need those handlers to do their job before cache update
            });
        }

        // If store is assigned after configuration we need to init relations
        if (!me.isConfiguring) {
            me.initRelations(true);
        }

        me.attachToResourceStore(eventStore && eventStore.resourceStore);
    }

    attachToResourceStore(resourceStore) {
        const me = this;

        me.resourceStore = resourceStore;

        me.resourceStoreDetacher && me.resourceStoreDetacher();

        if (resourceStore) {
            me.resourceStoreDetacher = resourceStore.on({
                remove    : me.onResourceRemove,
                removeAll : me.onResourceRemoveAll,
                thisObj   : me,
                prio      : 200 // higher then in cache
            });

            // If store is assigned after configuration we need to init relations
            if (!me.isConfiguring) {
                me.initRelations(true);
            }
        }
    }

    onEventStoreResourceStoreChange({ newResourceStore }) {
        this.attachToResourceStore(newResourceStore);
    }

    //endregion

    //region Event & resource events

    // TODO: We have no isMove in our stores
    onEventRemove({ records, isMove, isCollapse }) {
        if (!isMove && !isCollapse) {
            const assignments = [];

            records.forEach(record => {
                // traversing in a flat structure will only call fn on self, no need to handle tree case differently
                record.traverse(eventRecord => {
                    assignments.push(...eventRecord.assignments);
                });
            });

            // Flag that remove is caused by removing events, to prevent getting stuck in removal loop in SchedulerStores
            this.isRemovingEvent = true;
            assignments.length && this.remove(assignments);
            this.isRemovingEvent = false;
        }
    }

    // TODO: We have no isMove in our stores
    onResourceRemove({ records, isMove, isCollapse }) {
        if (!isMove && !isCollapse) {
            const assignments = [];

            records.forEach(record => {
                // traversing in a flat structure will only call fn on self, no need to handle tree case differently
                record.traverse(resourceRecord => {
                    assignments.push(...resourceRecord.assignments);
                });
            });

            // TODO: Make resource removal behaviour configurable
            assignments.length && this.remove(assignments);
        }
    }

    onResourceRemoveAll() {
        // TODO: Make resource removal behaviour configurable
        this.removeAll();
    }

    //endregion

    //region Mapping

    /**
     * Maps over event assignments.
     *
     * @param {Scheduler.model.EventModel} event
     * @param {Function} [fn]
     * @param {Function} [filterFn]
     * @return {Array}
     */
    mapAssignmentsForEvent(event, fn, filterFn) {
        const me          = this,
            fnSet       = Boolean(fn),
            filterFnSet = Boolean(filterFn),
            eventId     = Model.asId(event),
            assignmentCache = me.relationCache.event && me.relationCache.event[eventId];

        if (!assignmentCache) return [];

        fn       = fn || (a => a);
        filterFn = filterFn || (() => true);

        if (fnSet || filterFnSet) {
            return assignmentCache.reduce((result, assignment) => {
                const mapResult = fn(assignment);

                if (filterFn(mapResult)) {
                    //result = result.concat([mapResult]);
                    result.push(mapResult);
                }

                return result;
            }, []);
        }

        return assignmentCache;
    }

    /**
     * Maps over resource assignments.
     *
     * @param {Scheduler.model.ResourceModel|Number|String} resource
     * @param {Function} [fn]
     * @param {Function} [filterFn]
     * @return {Scheduler.model.ResourceModel[]}
     */
    mapAssignmentsForResource(resource, fn, filterFn) {
        const me          = this,
            fnSet       = Boolean(fn),
            filterFnSet = Boolean(filterFn),
            resourceId  = Model.asId(resource),
            assignmentCache = me.relationCache.resource && me.relationCache.resource[resourceId];

        if (!assignmentCache) return [];

        fn       = fn || (a => a);
        filterFn = filterFn || (() => true);

        if (fnSet || filterFnSet) {
            return assignmentCache.reduce((result, assignment) => {
                const mapResult = fn(assignment);

                if (filterFn(mapResult)) {
                    //result = result.concat([mapResult]);
                    result.push(mapResult);
                }

                return result;
            }, []);
        }

        return assignmentCache;
    }

    /**
     * Returns all assignments for a given event.
     *
     * @param {Scheduler.model.TimeSpan} event
     * @return {Scheduler.model.AssignmentModel[]}
     */
    getAssignmentsForEvent(event) {
        return event.assignments;
    }

    /**
     * Removes all assignments for given event
     *
     * @param {Scheduler.model.TimeSpan|Object} event
     */
    removeAssignmentsForEvent(event) {
        return this.remove(event.assignments);
    }

    /**
     * Returns all assignments for a given resource.
     *
     * @param {Scheduler.model.ResourceModel|Object} event
     * @return {Scheduler.model.TimeSpan[]}
     */
    getAssignmentsForResource(resource) {
        return this.mapAssignmentsForResource(resource);
    }

    /**
     * Removes all assignments for given resource
     *
     * @param {Scheduler.model.ResourceModel|*} resource
     */
    removeAssignmentsForResource(resource) {
        this.remove(this.getAssignmentsForResource(resource));
    }

    /**
     * Returns all resources assigned to an event.
     *
     * @param {Scheduler.model.EventModel} event
     * @return {Scheduler.model.ResourceModel[]}
     */
    getResourcesForEvent(event) {
        const me = this;

        // TODO: cache event -> resource
        //if (!me.relationCache.events) return []; //return me.eventResourceCache.get(event);

        return me.mapAssignmentsForEvent(
            event,
            assignment => assignment.resource,
            resource => Boolean(resource)
        );
    }

    /**
     * Returns all events assigned to a resource
     *
     * @param {Scheduler.model.ResourceModel|*} resource
     * @return {Scheduler.model.TimeSpan[]}
     */
    getEventsForResource(resource) {
        const me = this;

        //if (me.resourceEventsCache) return me.resourceEventsCache.get(resource);

        return me.mapAssignmentsForResource(
            resource,
            assignment => assignment.event,
            event => !!event
        );
    }

    /**
     * Creates and adds assignment record(s) for a given event and resource.
     *
     * @param {Scheduler.model.TimeSpan|*} event
     * @param {Scheduler.model.ResourceModel|Scheduler.model.ResourceModel[]} resource The resource(s) to assign to the event
     * @privateparam {Function} [assignmentSetupFn]
     * @privateparam {Boolean} [removeExistingAssignments] true to first remove existing assignments
     * @return {Scheduler.model.AssignmentModel[]} An array with the created assignment(s)
     */
    assignEventToResource(event, resource, assignmentSetupFn = o => o, removeExistingAssignments = false) {
        const me          = this,
            resources   = Array.isArray(resource) ? resource : [resource];

        let assignments = [];

        me.beginBatch();

        if (removeExistingAssignments) {
            me.removeAssignmentsForEvent(event);
        }

        resources.forEach(resource => {
            if (!me.isEventAssignedToResource(event, resource)) {
                let assignment = new me.modelClass({
                    eventId    : Model.asId(event),
                    resourceId : Model.asId(resource)
                });

                assignment = assignmentSetupFn(assignment);

                assignments.push(assignment);
            }
        });

        assignments = me.add(assignments);
        me.endBatch();

        return assignments;
    }

    /**
     * Removes assignment record for a given event and resource.
     *
     * @param {Scheduler.model.TimeSpan|String|Number} event
     * @param {Scheduler.model.ResourceModel|String|Number} [resource] The resource to unassign the event from. If omitted, all resources of the events will be unassigned
     * @return {Scheduler.model.AssignmentModel|Scheduler.model.AssignmentModel[]}
     */
    unassignEventFromResource(event, resource) {
        const me = this;

        if (!resource) return me.removeAssignmentsForEvent(event);

        if (me.isEventAssignedToResource(event, resource)) {
            const assignment = me.getAssignmentForEventAndResource(event, resource);
            me.remove(assignment);
            return assignment;
        }

        return null;
    }

    /**
     * Checks whether an event is assigned to a resource.
     *
     * @param {Scheduler.model.EventModel|String|Number} event Event record or id
     * @param {Scheduler.model.ResourceModel|String|Number} resource Resource record or id
     * @return {Boolean}
     */
    isEventAssignedToResource(event, resource) {
        const me       = this,
            records    = me.getResourcesForEvent(event),
            resourceId = Model.asId(resource);

        // noinspection EqualityComparisonWithCoercionJS
        return records.some(res => res.id == resourceId);
    }

    /**
     * Returns an assignment record for a given event and resource
     *
     * @param {Scheduler.model.EventModel|String|Number} event The event or its id
     * @param {Scheduler.model.ResourceModel|String|Number} resource The resource or its id
     * @return {Scheduler.model.AssignmentModel}
     */
    getAssignmentForEventAndResource(event, resource) {
        const me = this;

        event    = me.eventStore.getById(event);
        resource = me.resourceStore.getById(resource);

        //const key = me.modelClass.makeAssignmentEventResourceCompositeKey(event, resource);

        //TODO: PORT key map
        //return me.records.find(a => key == me.modelClass.makeAssignmentEventResourceCompositeKey(a.eventId, a.resourceId));

        // noinspection EqualityComparisonWithCoercionJS
        return me.records.find(assignment => assignment.event == event && assignment.resource == resource);
    }

    //endregion
}
