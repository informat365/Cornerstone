import ResourceStoreMixin from './mixin/ResourceStoreMixin.js';
import ResourceModel from '../model/ResourceModel.js';
import AjaxStore from '../../Core/data/AjaxStore.js';

/**
 * @module Scheduler/data/ResourceStore
 */

/**
 * This is a class holding the collection the {@link Scheduler.model.ResourceModel resources} to be rendered into a
 * {@link Scheduler.view.Scheduler scheduler}.
 *
 * @mixes Scheduler/data/mixin/ResourceStoreMixin
 * @extends Core/data/AjaxStore
 */
export default class ResourceStore extends ResourceStoreMixin(AjaxStore) {
    static get defaultConfig() {
        return {
            /**
             * CrudManager must load stores in the correct order. Lowest first.
             * @private
             */
            loadPriority : 200,
            /**
             * CrudManager must sync stores in the correct order. Lowest first.
             * @private
             */
            syncPriority : 100,
            modelClass   : ResourceModel,
            storeId      : 'resources',
            autoTree     : true
        };
    }

    construct(config) {
        super.construct(config);

        if (this.modelClass !== ResourceModel && !(this.modelClass.prototype instanceof ResourceModel)) {
            throw new Error('Model for ResourceStore must subclass ResourceModel');
        }
    }

    remove(recordsOrIds) {
        recordsOrIds      = Array.isArray(recordsOrIds) ? recordsOrIds : [recordsOrIds];

        for (let r of recordsOrIds) {
            const record = this.getById(r);
            // Unassign flagged as part of removal, to let UI make intelligent (?) decisions about what to update
            record.unassignAll(true);
        }

        super.remove(recordsOrIds);
    }

    removeAll() {
        this.traverse(resourceRecord => resourceRecord.unassignAll(true));

        super.removeAll();
    }
}
