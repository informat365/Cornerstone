import Base from '../../../Core/Base.js';

// TODO: remove this API from simple Scheduler.
//       Simple scheduler always works in sync mode
/**
 * @module Scheduler/data/api/BatchAPI
 */

/**
 * Propagation batch API mixin.
 *
 * For the basic Scheduler methods in this mixin are noop, they are overriden in Scheduler Pro batch API mixin, where batching makes sense.
 *
 * @mixin
 */
export default Target => class BatchAPI extends (Target || Base) {

    construct(config) {
        this._propagationBatchPromise = Promise.resolve();
        super.construct(config);
    }

    /**
     * Starts batch of operations which naturally will end with propagation process. This makes sence only in Scheduler Pro
     * environment for the basic Scheduler this operation is noop.
     */
    beginPropagationBatch() {}

    /**
     * Ends batch of operations (which naturally will end with propagation process each) with single finalizing
     * call to projects propagate method. This makes sense only in Scheduler Pro/Gantt environment for the basic
     * Scheduler this operation is noop.
     *
     * @return {Promise} For the basic Scheduler the promise will be initially resolved
     */
    endPropagationBatch() {
        return this._propagationBatchPromise;
    }
};
