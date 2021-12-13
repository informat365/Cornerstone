import Model from '../../Core/data/Model.js';

/**
 * @module Scheduler/model/AssignmentModel
 */

/**
 * This class represent a single assignment of a resource to an event in scheduler.
 * It is a subclass of {@link Core.data.Model} class.
 * Please refer to the documentation for that class to become familiar with the base interface of this class.
 *
 * An Assignment has the following fields:
 * - `id` - The id of the assignment
 * - `resourceId` - The id of the resource assigned
 * - `eventId` - The id of the event to which the resource is assigned
 *
 * The data source for these fields can be customized by subclassing this class.
 *
 * @extends Core/data/Model
 */
export default class AssignmentModel extends Model {
    //region Fields
    static get fields() {
        return [
            /**
             * Id for the resource assigned
             * @field {String|Number} resourceId
             */
            { name : 'resourceId' },

            /**
             * Id for the event assigned
             * @field {String|Number} eventId
             */
            { name : 'eventId' }
        ];
    }

    static get relationConfig() {
        return [
            /**
             * Resource assigned
             * @member {Scheduler.model.ResourceModel} resource
             */
            { relationName : 'resource', fieldName : 'resourceId', store : 'resourceStore', collectionName : 'assignments' },

            /**
             * Event assigned
             * @member {Scheduler.model.EventModel} event
             */
            { relationName : 'event', fieldName : 'eventId', store : 'eventStore', collectionName : 'assignments' }
        ];
    }
    //endregion

    //region Stores

    /**
     * Returns an assigment store this assignment is part of. Assignment must be part of an assigment store
     * to be able to retrieve it.
     *
     * @return {Scheduler.data.AssignmentStore}
     */
    get assignmentStore() {
        return this.stores && this.stores[0];
    }

    /**
     * Returns an event store this assignment uses as default event store. Assignment must be part
     * of an assignment store to be able to retrieve default event store.
     *
     * @return {Scheduler.data.EventStore}
     */
    get eventStore() {
        const { assignmentStore } = this;
        return assignmentStore && assignmentStore.eventStore;
    }

    /**
     * Returns a resource store this assignment uses as default resource store. Assignment must be part
     * of an assignment store to be able to retrieve default resource store.
     *
     * @return {Scheduler.data.ResourceStore}
     */
    get resourceStore() {
        const { eventStore } = this;
        return eventStore && eventStore.resourceStore;
    }

    //endregion

    //region Event & resource

    /**
     * Convenience property to get the name of the associated event.
     * @property {String}
     * @readonly
     */
    get eventName() {
        return this.event && this.event.name;
    }

    /**
     * Convenience property to get the name of the associated resource.
     * @property {String}
     * @readonly
     */
    get resourceName() {
        return this.resource && this.resource.name;
    }

    // /**
    //  * Returns an event associated with this assignment.
    //  *
    //  * @privateparam  {Scheduler.data.EventStore} [eventStore]
    //  * @return {Scheduler.model.TimeSpan} Event instance
    //  */
    // getEvent(eventStore = this.eventStore) {
    //     // removed assignment will not have "this.joined" so we are providing a way to get an event via provided
    //     // event store
    //     return eventStore && eventStore.getById(this.eventId);
    // }

    /**
     * Returns the resource associated with this assignment.
     *
     * @privateparam {Scheduler.data.ResourceStore} [resourceStore]
     * @return {Scheduler.model.ResourceModel} Instance of resource
     */
    getResource(resourceStore = this.resourceStore) {
        // removed assignment will not have "this.joined" so we are providing a way to get a resource via provided
        // resource store
        return resourceStore && resourceStore.getById(this.resourceId);
    }

    // /**
    //  * Convenience method to get a name of the associated event.
    //  *
    //  * @privateparam  {Scheduler.data.EventStore} [eventStore]
    //  * @return {String} name
    //  */
    // getEventName(eventStore) {
    //     const evnt = this.getEvent(eventStore);
    //     return evnt && evnt.name || '';
    // }

    // /**
    //  * Convenience method to get a name of the associated resource.
    //  *
    //  * @privateparam {Scheduler.data.ResourceStore} [resourceStore]
    //  * @return {String} name
    //  */
    // getResourceName(resourceStore) {
    //     const resource = this.getResource(resourceStore);
    //     return resource && resource.name || '';
    // }

    //endregion

    // Convenience getter to not have to check `instanceof AssignmentModel`
    get isAssignment() {
        return true;
    }

    /**
     * Returns true if the Assignment can be persisted (e.g. task and resource are not 'phantoms')
     *
     * @return {Boolean} true if this model can be persisted to server.
     */
    get isPersistable() {
        const
            { event, resource, stores, unjoinedStores } = this,
            crudManager = stores[0] && stores[0].crudManager;

        let store = stores[0],
            result;

        if (store) {
            // if crud manager is used it can deal with phantom event/resource since it persists all records in one batch
            // if no crud manager used we have to wait till event/resource are persisted
            result = this.isValid && (crudManager || !event.hasGeneratedId && !resource.hasGeneratedId);
        }
        // if we remove the record
        else {
            result = Boolean(unjoinedStores[0]);
        }

        return result;
    }

    get isValid() {
        return this.resource != null && this.event != null;
    }

    fullCopy() {
        // NOT IMPLEMENTED
        //return this.copy.apply(this, arguments);
        throw new Error('Not implemented');
    }

    // private
    get eventResourceCompositeKey() {
        return AssignmentModel.makeAssignmentEventResourceCompositeKey(
            this.eventId,
            this.resourceId
        );
    }

    static makeAssignmentEventResourceCompositeKey(eventId, resourceId) {
        return `event(${eventId})-resource(${resourceId})`;
    }
}

AssignmentModel.exposeProperties();
