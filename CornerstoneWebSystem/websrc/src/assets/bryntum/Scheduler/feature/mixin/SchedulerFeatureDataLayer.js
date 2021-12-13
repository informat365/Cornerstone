import Base from '../../../Core/Base.js';
import GridFeatureDataLayer from '../../../Grid/feature/mixin/GridFeatureDataLayer.js';

/**
 * @module Scheduler/feature/mixin/SchedulerFeatureDataLayer
 */

/**
 * Scheduler feature data layer mixin, provides methods and properties
 * to get scheduler stores, attach to their events and react gracefully on their change.
 *
 * @mixin
 * @private
 */
export default Target => class SchedulerFeatureDataLayer extends GridFeatureDataLayer(Target || Base) {

    static get defaultConfig() {
        return {
            dataLayerStores : ['resourceStore', 'dependencyStore', 'assignmentStore', 'eventStore']
        };
    }

    obtainResourceStore(scheduler, config) {
        return scheduler.store;
    }

    obtainDependencyStore(scheduler, config) {
        return scheduler.dependencyStore;
    }

    obtainAssignmentStore(scheduler, config) {
        return scheduler.assignmentStore;
    }

    obtainEventStore(scheduler, config) {
        return scheduler.eventStore;
    }
};
