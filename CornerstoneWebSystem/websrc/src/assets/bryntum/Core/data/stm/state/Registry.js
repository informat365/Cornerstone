import StateBase from './StateBase.js';

/**
 * @module Core/data/stm/state/Registry
 *
 * Provides map of registered STM states.
 *
 * Needed to remove states circular dependency.
 *
 * @internal
 */
const registry = new Map();

/**
 * Registers STM state class with the given name.
 *
 * @private
 *
 * @param {string} name
 * @param {Core.data.stm.state.StateBase} state
 */
export const registerStmState = (name, state) => {
    //<debug>
    console.assert(
        state instanceof StateBase,
        `Can't register STM state ${name}, invalid state class provided!`
    );
    //</debug>

    registry.set(name, state);
};

/**
 * Resolves STM state class with the given name.
 *
 * @private
 *
 * @param {string} name
 * @return {Core.data.stm.state.StateBase} state
 */
export const resolveStmState = (state) => {
    if (typeof state === 'string') {
        state = registry.get(state);
    }

    //<debug>
    console.assert(
        state instanceof StateBase,
        `Can't resolve STM state ${state}, state class hasn't been registered!`
    );
    //</debug>

    return state;
};

// UMD/module compatible export
// NOTE: the most compatible way of exporting is:
//       import registry from './Registry.js';
//       { registerStmState, resolveStmState } = registry;
//          or
//       registry.registerStmState(...);
export default {
    registerStmState,
    resolveStmState
};
