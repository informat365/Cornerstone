import ActionBase from './ActionBase.js';
import Store from '../../Store.js';
import Model from '../../Model.js';

/**
 * @module Core/data/stm/action/InsertAction
 */

const STORE_PROP        = Symbol('STORE_PROP');
const MODEL_LIST_PROP   = Symbol('MODEL_LIST_PROP');
const INSERT_INDEX_PROP = Symbol('INSERT_INDEX_PROP');
const CONTEXT_PROP      = Symbol('CONTEXT_PROP');

/**
 * Action to record the fact of models inserting into a store.
 */
export default class InsertAction extends ActionBase {

    static get defaultConfig() {
        return {
            /**
             * Reference to a store models have been inserted into.
             *
             * @config {Core.data.Store}
             * @default
             */
            store : undefined,

            /**
             * List of models inserted into the store.
             *
             * @config {Core.data.Model[]}
             * @default
             */
            modelList : undefined,

            /**
             * Index the models have been inserted at.
             *
             * @config {Number}
             * @default
             */
            insertIndex : undefined,

            /**
             * Models move context (if models has been moved), if any.
             * Map this {@link Core/data/Model} instances as keys and their
             * previous index as values
             *
             * @config {Map}
             * @default
             */
            context : undefined,

            /**
             * Flag showing if undo/redo should be done silently i.e. with events suppressed
             *
             * @config {Boolean}
             * @default
             */
            silent : false
        };
    }

    get type() {
        return 'InsertAction';
    }

    //<debug>
    afterConfig() {
        super.afterConfig();

        console.assert(
            this.store instanceof Store &&
            Array.isArray(this.modelList) &&
            this.modelList.length &&
            this.modelList.every(m => m instanceof Model) &&
            this.insertIndex !== undefined &&
            this.context instanceof Map,
            "Can't create action, bad configuration!"
        );
    }
    //</debug>

    get store() {
        return this[STORE_PROP];
    }

    set store(store) {
        //<debug>
        console.assert(
            !this[STORE_PROP] && store && store instanceof Store,
            "Can't set store, store is required and can be set only once!"
        );
        //</debug>

        this[STORE_PROP] = store;
    }

    get modelList() {
        return this[MODEL_LIST_PROP];
    }

    set modelList(list) {
        //<debug>
        console.assert(
            !this[MODEL_LIST_PROP] &&
            Array.isArray(list) &&
            list.length &&
            list.every(m => m instanceof Model),
            "Can't set model list, model list is required, it should be array of Models and it can be set only once!"
        );
        //</debug>

        this[MODEL_LIST_PROP] = list.slice(0);
    }

    get insertIndex() {
        return this[INSERT_INDEX_PROP];
    }

    set insertIndex(index) {
        //<debug>
        console.assert(
            this[INSERT_INDEX_PROP] === undefined && index !== undefined,
            "Can't set insert index, the value is required and can be set only once!"
        );
        //</debug>

        this[INSERT_INDEX_PROP] = index;
    }

    get context() {
        return this[CONTEXT_PROP];
    }

    set context(context) {
        //<debug>
        console.assert(
            !this[CONTEXT_PROP] &&
            context instanceof Map &&
            Array.from(context.entries()).every(([k, v]) => k instanceof Model && v !== undefined),
            "Can't set move context, context is required, it should be Map with keys set to Model instances and numeric values, and it can be set only once!"
        );
        //</debug>

        this[CONTEXT_PROP] = context;
    }

    undo() {
        const { store, modelList, context, silent } = this;

        // Let's sort models by index such that models with lesser index
        // were inserted back first, thus making valid index of models following.

        modelList.sort((lhs, rhs) => {
            const lhsIndex = context.get(lhs),
                rhsIndex = context.get(rhs);

            return lhsIndex !== undefined && rhsIndex !== undefined ? lhsIndex - rhsIndex : 0;
        });

        modelList.forEach(m => {
            const index = context.get(m);

            if (index !== undefined) {
                // Insert at previous index
                store.insert(index, m, silent);
            }
            else {
                // Just remove
                store.remove(m, silent);
            }
        });
    }

    redo() {
        const me = this;
        me.store.insert(me.insertIndex, me.modelList, me.silent);
    }
}
