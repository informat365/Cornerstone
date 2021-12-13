import GridRowModel from '../../Grid/data/GridRowModel.js';

/**
 * @module Scheduler/model/ResourceModel
 */

/**
 * This class represent a single Resource in the scheduler chart. It's a subclass of  {@link Core.data.Model}.
 * Please refer to the documentation for that class to become familiar with the base interface of the resource.
 *
 * A Resource has only 2 mandatory fields - `id` and `name`. If you want to add more fields with meta data describing your resources then you should subclass this class:
 *
 * ```javascript
 * class MyResource extends ResourceModel {
 *
 *   static get fields() {
 *     [
 *       // `Id` and `Name` fields are already provided by the superclass
 *       { name: 'company', type : 'string' }
 *     ];
 *   }
 *
 *   getCompany() {
 *     return this.company;
 *   }
 *   ...
 * });
 * ```
 * If you want to use other names in your data for the id and name fields you can configure them as seen below:
 * ```javascript
 * class MyResource extends ResourceModel {
 *
 *   static get fields() {
 *     return [
 *        { name: 'name', dataSource: 'userName' }
 *     ];
 *   },
 *   ...
 * });
 * ```
 * Please refer to {@link Core.data.Model} for details.
 *
 * @extends Grid/data/GridRowModel
 */
export default class ResourceModel extends GridRowModel {
    //region Fields

    static get fields() {
        return [
            /**
             * Unique identifier
             * @field {String|Number} id
             */

            /**
             * Get or set resource name
             * @field {String} name
             */
            { name : 'name', type : 'string', persist : true },

            /**
             * Controls the primary color used for events assigned to this resource. Can be overridden per event using
             * EventModels {@link Scheduler/model/EventModel#field-eventColor eventColor config}. See Schedulers
             * {@link Scheduler.view.mixin.TimelineEventRendering#config-eventColor eventColor config} for available
             * colors.
             * @field {String} eventColor
             */
            'eventColor',

            /**
             * Controls the style used for events assigned to this resource. Can be overridden per event using
             * EventModels {@link Scheduler/model/EventModel#field-eventStyle eventStyle config}. See Schedulers
             * {@link Scheduler.view.mixin.TimelineEventRendering#config-eventStyle eventStyle config} for available
             * options.
             * @field {String} eventStyle
             */
            'eventStyle',

            /**
             * Image URL, used by `ResourceInfoColumn` and vertical modes `ResourceHeader` to display a miniature image
             * for the resource.
             * @field {String} imageUrl
             */
            'imageUrl'
        ];
    }

    // ResoureModel#assignments is created by a relation defined in AssignmentModel
    /**
     * Returns all assignments for the resource. Resource must be part of the store for this method to work.
     * @member {Scheduler.model.AssignmentModel[]} assignments
     */

    //endregion

    //region Stores

    /**
     * Returns a resource store this resource is part of. Resource must be part
     * of a resource store to be able to retrieve resource store.
     *
     * @return {Scheduler.data.ResourceStore}
     * @readonly
     */
    get resourceStore() {
        return this.stores && this.stores[0];
    }

    /**
     * Returns an event store this resource uses as default. Resource must be part
     * of a resource store to be able to retrieve event store.
     *
     * @return {Scheduler.data.EventStore}
     * @readonly
     */
    get eventStore() {
        const resourceStore = this.resourceStore;
        // TODO: this.parentNode... is not ported
        return resourceStore && resourceStore.eventStore || this.parentNode && this.parentNode.eventStore;
    }

    /**
     * Returns as assignment store this resources uses as default. Resource must be part
     * of a resource store to be able to retrieve default assignment store.
     *
     * @return {Scheduler.data.AssignmentStore}
     * @readonly
     */
    get assignmentStore() {
        const eventStore = this.eventStore;
        return eventStore && eventStore.assignmentStore;
    }

    //endregion

    //region Getters

    /**
     * Get associated events
     * @returns {Scheduler.model.EventModel[]}
     * @readonly
     */
    get events() {
        // Cannot use relation here, since it wont work in mult assignment
        // TODO: Investigate making relations handle many-to-many using intermediate store? To have it cached
        return this.eventStore && this.eventStore.getEventsForResource(this);
    }

    // /**
    //  * Returns all assignments for the resource. Resource must be part of the store for this method to work.
    //  *
    //  * @return {Scheduler.model.AssignmentModel[]}
    //  * @readonly
    //  */
    // get assignments() {
    //     const me         = this,
    //         eventStore = me.eventStore;
    //
    //     return eventStore && eventStore.getAssignmentsForResource(me);
    // }

    /**
     * Returns an array of events, associated with this resource
     *
     * @param {Scheduler.data.EventStore} eventStore (optional) The event store to get events for (if a resource is bound to multiple stores)
     * @return {Scheduler.model.TimeSpan[]}
     */
    // TODO: Needed?
    getEvents(eventStore = this.eventStore) {
        return eventStore && eventStore.getEventsForResource(this) || [];
    }

    /**
     * Returns true if the Resource can be persisted.
     * In a flat store resource is always considered to be persistable, in a tree store resource is considered to
     * be persitable if it's parent node is persistable.
     *
     * @return {Boolean} true if this model can be persisted to server.
     * @readonly
     */
    get isPersistable() {
        const parent = this.parentNode;
        // TODO: not ported yet
        return !parent || !parent.phantom || (parent.isRoot && parent.isRoot());
    }

    //endregion

    /**
     * Returns true if this resource model is above the passed resource model
     * @param {Scheduler.model.ResourceModel} otherResource
     * @returns {Boolean}
     */
    isAbove(otherResource) {
        let me     = this,
            store  = me.resourceStore,
            current, myAncestors, otherAncestors, commonAncestorsLength, lastCommonAncestor;

        //<debug>
        if (!store) throw new Error('Resource must be added to a store to be able to check if it above of an other resource');
        //</debug>

        if (me === otherResource) return false;

        if (store.tree) {
            //TODO: not ported

            // Getting self ancestors this node including
            current = me;
            myAncestors = [];
            while (current) {
                myAncestors.push(current);
                current = current.parentNode;
            }

            // Getting other ancestors other node including
            current = otherResource;
            otherAncestors = [];
            while (current) {
                otherAncestors.push(current);
                current = current.parentNode;
            }

            // Getting common ancestors sequence length
            commonAncestorsLength = 0;
            while (
                commonAncestorsLength < myAncestors.length - 1 &&
            commonAncestorsLength < otherAncestors.length - 1 &&
            myAncestors[commonAncestorsLength] == otherAncestors[commonAncestorsLength]
            ) {
                ++commonAncestorsLength;
            }

            // Getting last common ancesstor
            lastCommonAncestor = myAncestors[commonAncestorsLength];

            // Here the next ancestor in myAncestors and next ancesstor in otherAncestors are siblings and
            // thus designate which node is above
            me = myAncestors[commonAncestorsLength + 1];
            otherResource = otherAncestors[commonAncestorsLength + 1];

            return lastCommonAncestor.indexOf(me) < lastCommonAncestor.indexOf(otherResource);
        }

        return store.indexOf(me) < store.indexOf(otherResource);
    }

    /**
     * Unassigns this Resource from all its Events
     */
    unassignAll(removingResource) {
        this.events && this.events.slice().forEach(event => event.unassign(this, removingResource));
    }
}

ResourceModel.exposeProperties();
