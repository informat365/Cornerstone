/**
 * @module Core/data/stm/Transaction
 */
import Base from '../../Base.js';

const ACTION_QUEUE_PROP = Symbol('ACTION_QUEUE_PROP');

/**
 * STM transaction class, holds list of actions constituting a transaction.
 *
 * A transaction can be undone and redone. Upon undo all the actions being held
 * are undone in reverse order. Upon redo all the actions being held are redone
 * in forward order.
 */
export default class Transaction extends Base {

    get defaultConfig() {
        return {
            /**
             * Transaction title
             *
             * @config {String}
             * @default
             */
            title : null
        };
    }

    construct(...args) {
        this[ACTION_QUEUE_PROP] = [];

        super.construct(...args);
    }

    /**
     * Gets transaction's actions queue
     *
     * @property {Core.data.stm.action.ActionBase[]}
     */
    get queue() {
        return this[ACTION_QUEUE_PROP].slice(0);
    }

    /**
     * Gets transaction's actions queue length
     *
     * @property {Number}
     */
    get length() {
        return this[ACTION_QUEUE_PROP].length;
    }

    /**
     * Adds an action to the transaction.
     *
     * @param {Core.data.stm.action.ActionBase|Object} action
     */
    addAction(action) {
        //<debug>
        console.assert(
            action && typeof action.undo == 'function' && typeof action.redo == 'function',
            "Can't add action to a STM transaction, action must have `undo` and `redo` methods, inheriting from `ActionBase` might help!"
        );
        //</debug>

        this[ACTION_QUEUE_PROP].push(action);
    }

    /**
     * Undoes actions held
     */
    undo() {
        const queue = this[ACTION_QUEUE_PROP];

        for (let i = queue.length - 1; i >= 0; --i) {
            queue[i].undo();
        }
    }

    /**
     * Redoes actions held
     */
    redo() {
        const queue = this[ACTION_QUEUE_PROP];

        for (let i = 0, len = queue.length; i < len; ++i) {
            queue[i].redo();
        }
    }
}
