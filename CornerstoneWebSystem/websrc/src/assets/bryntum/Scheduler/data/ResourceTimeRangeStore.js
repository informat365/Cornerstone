import AjaxStore from '../../Core/data/AjaxStore.js';
import ResourceTimeRangeModel from '../model/ResourceTimeRangeModel.js';

/**
 * @module Scheduler/data/ResourceTimeRangeStore
 */

/**
 * A class representing a collection of resource time ranges.
 * Contains a collection of {@link Scheduler.model.ResourceTimeRangeModel} records.
 *
 * @extends Core/data/AjaxStore
 */
export default class ResourceTimeRangeStore extends AjaxStore {
    static get defaultConfig() {
        return {
            /**
             * CrudManager must load stores in the correct order. Lowest first.
             * @private
             */
            loadPriority : 500,

            /**
             * CrudManager must sync stores in the correct order. Lowest first.
             * @private
             */
            syncPriority : 500,

            /**
             * This store should be linked to a ResourceStore to link the time ranges to resources
             * @config {Scheduler.data.ResourceStore}
             */
            resourceStore : null,

            modelClass : ResourceTimeRangeModel,
            storeId    : 'resourceTimeRanges'
        };
    }

    set resourceStore(store) {
        this._resourceStore = store;

        // If store is assigned after configuration we need to init relations
        if (!this.isConfiguring) {
            this.initRelations(true);
        }
    }

    get resourceStore() {
        return this._resourceStore;
    }

    // Matching signature in EventStore to allow reusage of SchedulerStores#onInternalEventStoreChange()
    getResourcesForEvent(resourceTimeRange) {
        return [resourceTimeRange.resource];
    }
}
