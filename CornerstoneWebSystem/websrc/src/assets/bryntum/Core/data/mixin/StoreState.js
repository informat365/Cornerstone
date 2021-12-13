import Base from '../../Base.js';

/**
 * @module Core/data/mixin/StoreState
 */

/**
 * Mixin for Store that handles store states.
 *  * sorters
 *  * groupers
 *  * filters
 * @private
 * @mixin
 */
export default Target => class StoreState extends (Target || Base) {
    /**
     * Get store state. Used by State-plugin to serialize state
     * @private
     * @returns {{ sorters, groupers }}
     */
    getState() {
        const me    = this,
            state = {};

        if (me.sorters && me.sorters.length) state.sorters = me.sorters.slice();
        if (me.groupers && me.groupers.length) state.groupers = me.groupers.slice();
        if (me.filters && me.filters.values.length) state.filters = me.filterState;

        return state;
    }

    /**
     * Apply store state. Used by State-plugin to restore a previously serialized state
     * @private
     * @param {{ sorters, groupers }} state
     */
    applyState(state) {
        const me = this;
        if ('sorters' in state) me.sorters = state.sorters.slice();
        if ('groupers' in state) me.groupers = state.groupers.slice();
        if ('sorters' in state || 'groupers' in state) me.sort();

        if ('filters' in state) {
            me.filters = state.filters.slice();
            me.filter();
        }
    }
};
