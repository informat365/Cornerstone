import Model from '../../Core/data/Model.js';
import RecurringTimeSpan from './mixin/RecurringTimeSpan.js';

/**
 * @module Scheduler/model/EventModel
 */

/**
 * This class represent a single event in your schedule. It is a subclass of the {@link Scheduler.model.TimeSpan}, which is in turn subclass of {@link Core.data.Model}.
 * Please refer to documentation of that class to become familiar with the base interface of the event.
 *
 * The Event model has a few predefined fields as seen below. If you want to add new fields or change the options for the existing fields,
 * you can do that by subclassing this class (see example below).
 *
 * Subclassing the Event model class
 * --------------------
 * ```
 * class MyEvent extends EventModel {
 *
 *     static get fields() {
 *         return [
 *            // Add new field
 *            { name: 'myField', type : 'number', defaultValue : 0 }
 *         ];
 *     },
 *
 *     myCheckMethod() {
 *         return this.myField > 0
 *     },
 *
 *     ...
 * });
 * ```
 * If you in your data want to use other names for the startDate, endDate, resourceId and name fields you can configure
 * them as seen below:
 * ```
 * class MyEvent extends EventModel {
 *
 *     static get fields() {
 *         return [
 *            { name: 'startDate', dataSource 'taskStart', type: 'date', format: 'YYYY-MM-DD' },
 *            { name: 'endDate', dataSource 'taskEnd', type: 'date', format: 'YYYY-MM-DD' },
 *            { name: 'resourceId', dataSource 'userId' },
 *            { name: 'name', dataSource 'taskTitle' },
 *         ];
 *     },
 *     ...
 * });
 * ```
 * Please refer to {@link Core.data.Model} for additional details.
 *
 * @extends Scheduler/model/TimeSpan
 * @mixes Scheduler/model/mixin/RecurringTimeSpan
 */
export default class EventModel extends RecurringTimeSpan() {
    //region Fields

    // TODO: handle persist? defaultValue?
    static get fields() {
        return [
            /**
             * The unique identifier of a task (mandatory)
             * @field {String|Number} id
             */

            /**
             * CSS class specifying an icon to apply to the event
             * @field {String} iconCls
             */
            { name : 'iconCls' },

            /**
             * Id of the resource this event is associated with (only usable for single assignments)
             * @field {String|Number} resourceId
             */
            { name : 'resourceId' },

            /**
             * Specify false to prevent the event from being dragged (if EventDrag feature is used)
             * @field {Boolean} draggable
             * @default true
             */
            { name : 'draggable', type : 'boolean', persist : false, defaultValue : true },   // true or false

            /**
             * Specify false to prevent the event from being resized (if EventResize feature is used). You can also
             * specify 'start' or 'end' to only allow resizing in one direction
             * @field {boolean|String} resizable
             * @default true
             */
            { name : 'resizable', persist : false, defaultValue : true },                    // true, false, 'start' or 'end'

            /**
             * Controls this events appearance, see Schedulers
             * {@link Scheduler.view.mixin.TimelineEventRendering#config-eventStyle eventStyle config} for
             * available options.
             * @field {String} eventStyle
             */
            'eventStyle',

            /**
             * Controls the primary color of the event, see Schedulers
             * {@link Scheduler.view.mixin.TimelineEventRendering#config-eventColor eventColor config} for
             * available colors.
             * @field {String} eventColor
             */
            'eventColor',

            /**
             * Width (in px) to use for this milestone when using Scheduler#milestoneLayoutMode 'data'.
             * @field {Number} milestoneWidth
             */
            'milestoneWidth'
        ];
    }

    // EventModel#assignments is created by a relation defined in AssignmentModel
    /**
     * Returns all assignments for the event. Event must be part of the store for this method to work.
     * @member {Scheduler.model.AssignmentModel[]} assignments
     */

    /**
     * Returns the assigned resource. Only valid when not using an AssignmentStore (single assignment)
     * @member {Scheduler.model.ResourceModel} resource
     */

    static get relationConfig() {
        return [
            { relationName : 'resource', fieldName : 'resourceId', store : 'resourceStore', collectionName : 'events' }
        ];
    }

    //endregion

    //region Stores

    /**
     * Returns the event store this event is part of.
     *
     * @return {Scheduler.data.EventStore}
     * @readonly
     */
    get eventStore() {
        const me = this;

        if (!me._eventStore) {
            me._eventStore = me.stores && me.stores.find(s => s.isEventStore);
        }
        return me._eventStore;
    }

    /**
     * Returns the resource store this event uses as its default resource store. Event must be part
     * of an event store to be able to retrieve default resource store.
     *
     * @return {Scheduler.data.ResourceStore}
     * @readonly
     */
    get resourceStore() {
        const eventStore = this.eventStore;
        return eventStore && eventStore.resourceStore;
    }

    /**
     * Returns the assigment store this event uses as its default assignment store. Event must be part
     * of an event store to be able to retrieve default assignment store.
     *
     * @return {Scheduler.data.AssignmentStore}
     * @readonly
     */
    get assignmentStore() {
        const eventStore = this.eventStore;
        return eventStore && eventStore.assignmentStore;
    }

    //endregion

    //region Resources

    /**
     * Returns all resources assigned to an event.
     *
     * @return {Scheduler.model.ResourceModel[]}
     * @readonly
     */
    get resources() {
        return this.eventStore && this.eventStore.getResourcesForEvent(this) || [];
    }

    /**
     * Iterate over all associated resources
     * @private
     */
    forEachResource(fn, thisObj = this) {
        for (let resource of this.resources) {
            if (fn.call(thisObj, resource) === false) return;
        }
    }

    /**
     * Returns either the resource associated with this event (when called w/o `resourceId`) or resource
     * with specified id.
     *
     * @param {String} resourceId (optional)
     * @return {Scheduler.model.ResourceModel}
     */
    getResource(resourceId = this.resourceId) {
        let me            = this,
            eventStore    = me.eventStore,
            resourceStore = eventStore && eventStore.resourceStore;

        if (eventStore && resourceId) {
            let result = eventStore.getResourcesForEvent(me);

            if (result.length == 1) return result[0];

            if (result.length > 1) throw new Error('Event::getResource() is not applicable for events with multiple assignments, please use Event::resources instead.');

            return null;
        }

        if (resourceStore) return resourceStore ? resourceStore.getById(resourceId) : null;
    }

    /**
     * Sets the resource which the event should belong to.
     *
     * @param {Scheduler.model.ResourceModel|String|Number} resource The new resource
     */
    // set resource(resource) {
    //     const me         = this,
    //           eventStore = me.eventStore;
    //
    //     eventStore && eventStore.removeAssignmentsForEvent(me);
    //
    //     me.assign(resource);
    // }

    //endregion

    //region Is

    // Used internally to differentiate between Event and ResourceTimeRange
    get isEvent() {
        return true;
    }

    /**
     * Returns true if event can be drag and dropped
     * @return {Boolean} The draggable state for the event.
     * @readonly
     */
    get isDraggable() {
        return this.draggable;
    }

    /**
     * Returns true if event can be resized, but can additionally return 'start' or 'end' indicating how this event can be resized.
     * @return {*} true, false, 'start' or 'end'
     * @readonly
     */
    get isResizable() {
        return !this.isMilestone && this.resizable;
    }

    /**
     * Returns false if a linked resource is a phantom record, i.e. it's not persisted in the database.
     *
     * @return {Boolean} true if persistable
     * @readonly
     */
    get isPersistable() {
        const me         = this,
            eventStore = me.eventStore;
        return eventStore && eventStore.isEventPersistable(me);
    }

    //endregion

    //region Assignment

    /**'
     * Assigns this event to the specified resource.
     *
     * @param {Scheduler.model.ResourceModel|String|Number} resource A new resource for this event, either as a full Resource record or an id (or an array of such).
     */
    assign(resource) {
        const me         = this,
            eventStore = me.eventStore;

        resource = Model.asId(resource);

        if (eventStore) {
            eventStore.assignEventToResource(me, resource);
        }
        else {
            me.resourceId = resource;
        }
    }

    /**
     * Unassigns this event from the specified resource
     *
     * @param {Scheduler.model.ResourceModel|String|Number|Array} [resource] The resource to unassign from.
     */
    unassign(resource, removingResource) {
        const me         = this,
            eventStore = me.eventStore;

        resource = Model.asId(resource);

        // If unassigned is caused by removing the resource the UI should be able to find out to not do extra redraws etc.
        me.meta.removingResource = removingResource;

        if (eventStore) {
            eventStore.unassignEventFromResource(me, resource);
        }
        else if (me.resourceId == resource) {
            me.resourceId = null;
        }

        me.meta.removingResource = null;
    }

    /**
     * Reassigns an event from an old resource to a new resource
     *
     * @param {Scheduler.model.ResourceModel|String|Number} oldResourceId A resource to unassign from or its id
     * @param {Scheduler.model.ResourceModel|String|Number} newResourceId A resource to assign to or its id
     */
    reassign(oldResourceId, newResourceId) {
        const me         = this,
            eventStore = me.eventStore;

        oldResourceId = Model.asId(oldResourceId);
        newResourceId = Model.asId(newResourceId);

        if (eventStore) {
            eventStore.reassignEventFromResourceToResource(me, oldResourceId, newResourceId);
        }
        else {
            me.resourceId = newResourceId;
        }
    }

    /**
     * Returns true if this event is assigned to a certain resource.
     *
     * @param {Scheduler.model.ResourceModel|String|Number} resource The resource to query for
     * @return {Boolean}
     */
    isAssignedTo(resource) {
        let me         = this,
            eventStore = me.eventStore;

        resource = Model.asId(resource);

        if (eventStore) return eventStore.isEventAssignedToResource(me, resource);

        return me.resourceId == resource;
    }

    //endregion

    /**
     * The "main" event this model is an occurrence of.
     * Returns `null` for non-occurrences.
     * @property {Scheduler.model.EventModel}
     * @alias #property-recurringTimeSpan
     * @readonly
     */
    get recurringEvent() {
        return this.recurringTimeSpan;
    }

}

EventModel.exposeProperties();
