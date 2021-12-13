import Events from '../../../Core/mixin/Events.js';
import Delayable from '../../../Core/mixin/Delayable.js';
import StringHelper from '../../../Core/helper/StringHelper.js';

/**
 * @module Scheduler/data/util/DelayedCallsManager
 */

/**
 * This class delays a function call in the same way {@link Core.helper.FunctionHelper#function-createBuffered-static createBuffered} does, plus it:
 *
 * - accepts a function call as a configuration object
 * - collects arguments of the wrapped function calls
 * - provides a hook that allows to process/combine the collected call arguments
 * - triggers wrapping `delayed${Id}Start`, `delayed${Id}End` events (where `${Id}` is capitalized `id` of the delayed call).
 *
 * @private
 */
export default class DelayedCallsManager extends Delayable(Events()) {

    get delayedCallTimeout() {
        return !isNaN(this._delayedCallTimeout) ? this._delayedCallTimeout : 100;
    }

    set delayedCallTimeout(value) {
        this._delayedCallTimeout = value;
    }

    cancel(...callIds) {
        const { delayedCalls } = this;

        if (delayedCalls) {
            const ids = callIds.length ? callIds : Object.keys(delayedCalls);

            for (let i = ids.length - 1; i >= 0; i--) {
                const id        = ids[i],
                    delayedCall = delayedCalls[id];

                if (delayedCall && delayedCall.timer) {
                    this.clearTimeout(delayedCall.timer);
                    delayedCall.timer = null;
                }
            }
        }
    }

    invoke(delayedCall) {
        const { id, scope, beforeFn } = delayedCall,
            Id = StringHelper.capitalizeFirstLetter(id);

        this.trigger(`delayed${Id}Start`, delayedCall);

        beforeFn && beforeFn.call(scope, delayedCall);

        // get entries after beforeFn call (it could be processed by the call)
        const { afterFn, fn, entries } = delayedCall;

        let args, result = [];

        while ((args = entries.shift())) {
            result.push(fn.call(scope, delayedCall, ...args));
        }

        afterFn && afterFn.call(scope, delayedCall);

        this.trigger(`delayed${Id}End`, delayedCall);

        delayedCall.delete();

        return result;
    }

    /**
     * Schedules a delayed call.
     * @param {Object}   config Call configuration object:
     * @param {String}   config.id Call identifier. This is used as part of triggered `delayed${Id}Start`, `delayed${Id}End` events
     * @param {Function} config.fn Function that should be called
     * @param {Object[]} config.args `fn` function arguments
     * @param {Object}   config.scope `this` object for `fn` function call
     * @param {Function} config.beforeFn Function that should be called before real invoking of the `fn` function
     * @param {Object}   config.beforeFn.delayedCall Object containing the delayed call config plus additional property:
     * @param {Array[]}  config.beforeFn.delayedCall.entries Array of collected call arguments
     * @returns {Promise}
     */
    execute(config = {}) {
        return new Promise((resolve, reject) => {
            const me = this;

            me.delayedCalls = me.delayedCalls || {};

            const { id, args, timeout } = config;

            // get this specific group of delayed calls
            if (!me.delayedCalls[id]) {
                me.delayedCalls[id] = Object.assign({
                    delete  : () => delete me.delayedCalls[id],
                    entries : [],
                    scope   : this,
                    id
                }, config);

                delete me.delayedCalls[id].args;
            }

            const delayedCall = me.delayedCalls[id];

            // reset previously set timer (if any)
            me.cancel(id);

            // record this call into entries array
            delayedCall.entries.push(args || []);

            // Setup timer to delay the call
            delayedCall.timer = me.setTimeout(() => {
                delayedCall.results = me.invoke(delayedCall);
                resolve(delayedCall);
            }, timeout || me.delayedCallTimeout);
        });
    }
}
