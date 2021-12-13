// TODO: PORT Make getters/setters?
// TODO: PORT Find some nice way of replacing @cfg in docs

import Base from '../../../Core/Base.js';
import Model from '../../../Core/data/Model.js';
import DateHelper from '../../../Core/helper/DateHelper.js';
import ModelPersistencyManager from '../util/ModelPersistencyManager.js';
import AssignmentModel from '../../model/AssignmentModel.js';

/**
 * @module Scheduler/data/mixin/EventStoreMixin
 */

/**
 * This is a mixin, containing functionality related to managing events.
 *
 * It is consumed by the regular {@link Scheduler.data.EventStore} class and the Gantt `TaskStore` classes
 * to allow data sharing between a Gantt chart and a Scheduler.
 *
 * @mixin
 */
export default Target => class EventStoreMixin extends (Target || Base) {
    //region Connected stores (Resource, Assigment & Dependency)

    /**
     * Get/set the resource store for this store
     * @property {Scheduler.data.ResourceStore}
     */
    get resourceStore() {
        return this._resourceStore;
    }

    set resourceStore(resourceStore) {
        const
            me       = this,
            oldStore = me._resourceStore;

        if (oldStore) {
            oldStore.eventStore = null;
            if (me.modelPersistencyManager) me.modelPersistencyManager.resourceStore = null;
        }

        me._resourceStore = resourceStore || null;

        if (resourceStore) {
            if (me.modelPersistencyManager) me.modelPersistencyManager.resourceStore = me._resourceStore;
            resourceStore.eventStore = me;
        }

        // If store is assigned after configuration we need to init relations
        if (!me.isConfiguring) {
            me.initRelations(true);
        }

        if ((oldStore || resourceStore) && oldStore !== resourceStore) {
            /**
             * Fires when new resource store is set via {@link #property-resourceStore} setter.
             * @event resourcestorechange
             * @param {Scheduler.data.EventStore}         this
             * @param {Scheduler.data.ResourceStore} newResourceStore
             * @param {Scheduler.data.ResourceStore} oldResourceStore
             */
            me.trigger('resourceStoreChange', { newResourceStore : resourceStore, oldResourceStore : oldStore });
        }
    }

    /**
     * Get/set assignment store this event store is using by default
     * @property {Scheduler.data.AssignmentStore}
     */
    get assignmentStore() {
        return this._assignmentStore;
    }

    set assignmentStore(assignmentStore) {
        const
            me       = this,
            oldStore = me._assignmentStore;

        if (oldStore) {
            oldStore.eventStore = null;
            if (me.modelPersistencyManager) me.modelPersistencyManager.assignmentStore = null;
        }

        me._assignmentStore = assignmentStore || null;

        if (assignmentStore) {
            if (me.modelPersistencyManager) me.modelPersistencyManager.assignmentStore = me._assignmentStore;
            assignmentStore.eventStore = me;
        }

        // If store is assigned after configuration we need to init relations
        if (!me.isConfiguring) {
            me.initRelations(true);
        }

        if ((oldStore || assignmentStore) && oldStore !== assignmentStore) {
            /**
             * Fires when new assignment store is set via {@link #property-assignmentStore} setter.
             * @event assignmentStoreChange
             * @param {Scheduler.data.EventStore}           this
             * @param {Scheduler.data.AssignmentStore} newAssignmentStore
             * @param {Scheduler.data.AssignmentStore} oldAssignmentStore
             */
            me.trigger('assignmentStoreChange',  { newAssignmentStore : assignmentStore, oldAssignmentStore : oldStore });
        }
    }

    /**
     * Get/set a dependecy store instance this event store is associated with
     * Get/set a dependecy store instance this event store is associated with
     * @property {Scheduler.data.DependencyStore}
     */
    get dependencyStore() {
        return this._dependencyStore;
    }

    set dependencyStore(dependencyStore) {
        const
            me       = this,
            oldStore = me._dependencyStore;

        if (oldStore) {
            oldStore.eventStore = null;
            if (me.modelPersistencyManager) me.modelPersistencyManager.dependencyStore = null;
        }

        me._dependencyStore = dependencyStore || null;

        if (me._dependencyStore) {
            if (me.modelPersistencyManager) me.modelPersistencyManager.dependencyStore = me._dependencyStore;
            me._dependencyStore.eventStore = me;
        }

        if ((oldStore || dependencyStore) && oldStore !== dependencyStore) {
            /**
             * Fires when new dependency store is set via {@link #property-dependencyStore} setter.
             * @event dependencystorechange
             * @param {Scheduler.data.EventStore}           this
             * @param {Scheduler.data.DependencyStore} newDependencyStore
             * @param {Scheduler.data.DependencyStore} oldDependencyStore
             */
            me.trigger('dependencyStoreChange',  { newDependencyStore : dependencyStore, oldDependencyStore : oldStore });
        }
    }

    /**
     * Provide assignment store to enable multiple connections between events and resources
     * @config {Scheduler.data.AssignmentStore} assignmentStore
     */

    //endregion

    //region Init & destroy

    construct(config) {
        const me = this;

        super.construct(config);

        Object.assign(me, {
            isEventStore            : true,
            autoTree                : true,
            modelPersistencyManager : me.createModelPersistencyManager()
        });
    }

    /**
     * Creates and returns model persistency manager
     *
     * @return {Scheduler.data.util.ModelPersistencyManager}
     * @internal
     */
    createModelPersistencyManager() {
        const me = this;
        return new ModelPersistencyManager({
            eventStore      : me,
            resourceStore   : me.resourceStore,
            assignmentStore : me.assignmentStore,
            dependencyStore : me.dependencyStore
        });
    }

    //endregion

    //region Events records, iteration etc.

    /**
     * Returns events between the supplied start and end date
     * @param {Date} start The start date
     * @param {Date} end The end date
     * @param {Boolean} allowPartial false to only include events that start and end inside of the span
     * @param {Boolean} onlyAssigned true to only include events that are assigned to a resource
     * @return {Scheduler.model.EventModel[]} the events
     * @category Events
     */
    getEventsInTimeSpan(start, end, allowPartial = true, onlyAssigned = false) {
        const events = [];

        this.forEachScheduledEvent((event, eventStart, eventEnd) => {
            if (
                (allowPartial && DateHelper.intersectSpans(eventStart, eventEnd, start, end)) ||
                (!allowPartial && eventStart - start >= 0 && end - eventEnd >= 0)
            ) {
                if (!onlyAssigned || event.resources.length > 0) {
                    events.push(event);
                }
            }
        });

        return events;
    }

    /**
     * Returns all events that starts on the specified day.
     * @param start Start date
     * @returns {Scheduler.model.EventModel[]} Events starting on specified day
     * @category Events
     */
    getEventsByStartDate(start) {
        const events = [];

        this.forEachScheduledEvent((event, eventStart, eventEnd) => {
            if (DateHelper.isEqual(eventStart, start, 'day')) {
                events.push(event);
            }
        });

        return events;
    }

    /**
     * Calls the supplied iterator function once for every scheduled event, providing these arguments
     * - event : the event record
     * - startDate : the event start date
     * - endDate : the event end date
     *
     * Returning false cancels the iteration.
     *
     * @param {Function} fn iterator function
     * @param {Object} thisObj `this` reference for the function
     * @category Events
     */
    forEachScheduledEvent(fn, thisObj = this) {
        this.forEach(event => {
            const
                eventStart = event.startDate,
                eventEnd   = event.endDate;

            if (eventStart && eventEnd) return fn.call(thisObj, event, eventStart, eventEnd);
        });
    }

    /**
     * Returns an object defining the earliest start date and the latest end date of all the events in the store.
     *
     * @return {Object} An object with 'start' and 'end' Date properties (or null values if data is missing).
     * @category Events
     */
    getTotalTimeSpan() {
        let earliest = new Date(9999, 0, 1),
            latest   = new Date(0);

        this.forEach(r => {
            if (r.startDate) earliest = DateHelper.min(r.startDate, earliest);
            if (r.endDate) latest = DateHelper.max(r.endDate, latest);
        });

        // TODO: this will fail in programs designed to work with events in the past (after Jan 1, 1970)
        earliest = earliest < new Date(9999, 0, 1) ? earliest : null;
        latest   = latest > new Date(0) ? latest : null;

        // keep last calculated value to be able to track total timespan changes
        return (this.lastTotalTimeSpan = {
            startDate : earliest || null,
            endDate   : latest || earliest || null
        });
    }

    /**
     * Checks if given event record is persistable.
     * In case assignment store is used to assign events to resources and vise versa event is considered to be always
     * persistable. Otherwise backward compatible logic is used, i.e. event is considered to be persistable when
     * resources it's assigned to are not phantom.
     *
     * @param {Scheduler.model.EventModel} event
     * @return {Boolean}
     * @category Events
     */
    isEventPersistable(event) {
        let result = true;

        if (!this.assignmentStore) {
            const
                store       = event.stores[0],
                crudManager = store && store.crudManager;

            if (store) {
                // if crud manager is used it can deal with phantom resource since it persists all records in one batch
                // if no crud manager used we have to wait till resource is persisted
                result = crudManager || !event.resource || !event.resource.hasGeneratedId;
            }
            // if we remove the record
            else {
                result = true;
            }
        }

        return result;
    }

    //endregion

    //region Resource

    /**
     * Checks if a date range is allocated or not for a given resource.
     * @param {Date} start The start date
     * @param {Date} end The end date
     * @param {Scheduler.model.EventModel} excludeEvent An event to exclude from the check (or null)
     * @param {Scheduler.model.ResourceModel} resource The resource
     * @return {Boolean} True if the timespan is available for the resource
     * @category Resource
     */
    isDateRangeAvailable(start, end, excludeEvent, resource) {
        if (excludeEvent instanceof AssignmentModel) {
            const
                currentEvent = excludeEvent.event,
                resources    = currentEvent.resources,
                allEvents    = new Set(resource.events);

            resources.forEach(resource => {
                resource.events.forEach(e => allEvents.add(e));
            });

            allEvents.delete(currentEvent);

            return !Array.from(allEvents).some(ev => DateHelper.intersectSpans(start, end, ev.startDate, ev.endDate));
        }
        return !this.getEventsForResource(resource).some(ev =>
            !(excludeEvent === ev || !DateHelper.intersectSpans(start, end, ev.startDate, ev.endDate))
        );
    }

    /**
     * Filters the events associated with a resource, based on the function provided. An array will be returned for those
     * events where the passed function returns true.
     * @param {Scheduler.model.ResourceModel} resource
     * @param {Function} fn The function
     * @param {Object} [thisObj] `this` reference for the function
     * @return {Scheduler.model.EventModel[]} the events in the time span
     * @private
     * @category Resource
     */
    filterEventsForResource(resource, fn, thisObj = this) {
        // `getEvents` method of the resource will use either `indexByResource` or perform a full scan of the event store
        return resource.getEvents(this).filter(fn.bind(thisObj));
    }

    // This method provides a way for the store to append a new record, and the consuming class has to implement it
    // since Store and TreeStore don't share the add API.
    //append(record) {
    //    throw 'Must be implemented by consuming class';
    //}

    /**
     * Returns all resources assigned to an event.
     *
     * @param {Scheduler.model.EventModel|String|Number} event
     * @return {Scheduler.model.ResourceModel[]}
     * @category Resource
     */
    getResourcesForEvent(event) {
        if (this.assignmentStore) {
            return this.assignmentStore.getResourcesForEvent(event);
        }

        event = this.getById(event);

        return event.resource && !event.resource.placeHolder ? [event.resource] : [];
    }

    /**
     * Returns all events assigned to a resource
     *
     * @param {Scheduler.model.ResourceModel|String|Number} resource Resource or resource id
     * @return {Scheduler.model.EventModel[]}
     * @category Resource
     */
    getEventsForResource(resource) {
        if (this.assignmentStore) {
            return this.assignmentStore.getEventsForResource(resource);
        }

        resource = Model.asId(resource);

        // Could be changed to use resource.events, but that would require getting model by id. This way is a bit faster
        const cache = this.relationCache.resource && this.relationCache.resource[resource];
        // Slice to be safe from outside manipulation of the array
        return cache ? cache.slice() : [];
    }

    //endregion

    //region Assignment

    /**
     * Returns all assignments for a given event.
     * Works only if {@link #property-assignmentStore} is defined, otherwise returns an empty array.
     *
     * @param {Scheduler.model.EventModel|String|Number} event
     * @return {Scheduler.model.AssignmentModel[]}
     * @category Assignment
     */
    getAssignmentsForEvent(event) {
        return this.assignmentStore && this.assignmentStore.getAssignmentsForEvent(event) || [];
    }

    /**
     * Returns all assignments for a given resource.
     * Works only if {@link #property-assignmentStore} is defined, otherwise returns an empty array.
     *
     * @param {Scheduler.model.ResourceModel|String|Number} resource
     * @return {Scheduler.model.AssignmentModel[]}
     * @category Assignment
     */
    getAssignmentsForResource(resource) {
        return this.assignmentStore && this.assignmentStore.getAssignmentsForResource(resource) || [];
    }

    /**
     * Creates and adds assignment record for a given event and a resource.
     *
     * @param {Scheduler.model.EventModel|String|number} event
     * @param {Scheduler.model.ResourceModel|String|number|Scheduler.model.ResourceModel[]|String[]|number[]} resource The resource(s) to assign to the event
     * @privateparam {Boolean} [removeExistingAssignments] true to first remove existing assignments
     * @category Assignment
     */
    assignEventToResource(event, resource, removeExistingAssignments = false) {
        if (this.assignmentStore) {
            this.assignmentStore.assignEventToResource(event, resource, undefined, removeExistingAssignments);
        }
        else {
            event    = this.getById(event);

            if (Array.isArray(resource)) {
                resource = resource[0];
            }

            if (event) {
                event.resourceId = Model.asId(resource);
            }
        }
    }

    /**
     * Removes assignment record for a given event and a resource.
     *
     * @param {Scheduler.model.EventModel|String|Number} event
     * @param {Scheduler.model.ResourceModel|String|Number} resource
     * @category Assignment
     */
    unassignEventFromResource(event, resource) {
        if (this.assignmentStore) {
            this.assignmentStore.unassignEventFromResource(event, resource);
        }
        else {
            event    = this.getById(event);
            resource = Model.asId(resource);
            if (event && event.resourceId == resource) {
                event.resourceId = null;
            }
        }
    }

    /**
     * Reassigns an event from an old resource to a new resource
     *
     * @param {Scheduler.model.EventModel}    event    An event or id of the event to reassign
     * @param {Scheduler.model.ResourceModel|Scheduler.model.ResourceModel[]} oldResource A resource or id to unassign from
     * @param {Scheduler.model.ResourceModel|Scheduler.model.ResourceModel[]} newResource A resource or id to assign to
     * @category Assignment
     */
    reassignEventFromResourceToResource(event, oldResource, newResource) {
        const
            me                  = this,
            { assignmentStore } = me,
            newResourceId       = Model.asId(newResource),
            oldResourceId       = Model.asId(oldResource);

        if (assignmentStore) {
            const assignment = assignmentStore.getAssignmentForEventAndResource(event, oldResource);

            if (assignment) {
                assignment.resourceId = newResourceId;
            }
            else {
                assignmentStore.assignEventToResource(event, newResource);
            }
        }
        else {
            event = me.getById(event);
            if (event.resourceId == oldResourceId) {
                event.resourceId = newResourceId;
            }
        }
    }

    /**
     * Checks whether an event is assigned to a resource.
     *
     * @param {Scheduler.model.EventModel|String|Number} event
     * @param {Scheduler.model.ResourceModel|String|Number} resource
     * @return {Boolean}
     * @category Assignment
     */
    isEventAssignedToResource(event, resource) {
        if (this.assignmentStore) {
            return this.assignmentStore.isEventAssignedToResource(event, resource);
        }

        event    = this.getById(event);
        resource = Model.asId(resource);
        return event && (event.resourceId == resource) || false;
    }

    /**
     * Removes all assignments for given event
     *
     * @param {Scheduler.model.EventModel|String|Number} event
     * @category Assignment
     */
    removeAssignmentsForEvent(event) {
        if (this.assignmentStore) {
            this.assignmentStore.removeAssignmentsForEvent(event);
        }
        else {
            event = this.getById(event);
            if (event) {
                // This will update resource events cache via 'update' event
                event.resourceId = null;
            }
        }
    }

    /**
     * Removes all assignments for given resource
     *
     * @param {Scheduler.model.ResourceModel|String|Number} resource
     * @category Assignment
     */
    removeAssignmentsForResource(resource) {
        const { assignmentStore, resourceStore } = this;

        if (assignmentStore) {
            assignmentStore.removeAssignmentsForResource(resource);
        }
        else if (resourceStore) {
            resource = resourceStore.getById(resource);

            // TODO: change to use model cache
            //resource && me.resourceEventsCache.get(resource).forEach(event => {
            //    event.resourceId = null; // This will update resource events cache via 'update' event
            //});
        }
        else {
            resource = Model.asId(resource); // resource id might be 0 thus we use ? operator
            this.forEach(event =>
                event.resourceId == resource && (event.resourceId = null)
            );
        }
    }

    //endregion
};
