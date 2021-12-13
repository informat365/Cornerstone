/**
 * @module Core/data/stm/action/RemoveAction
 */
import ActionBase from './ActionBase.js';
import Store from '../../Store.js';
import Model from '../../Model.js';

const STORE_PROP = Symbol('STORE_PROP');
const MODEL_LIST_PROP = Symbol('MODEL_LIST_PROP');
const CONTEXT_PROP = Symbol('CONTEXT_PROP');

/**
 * Action to record the fact of models removed from a store.
 */
export default class RemoveAction extends ActionBase {

    static get defaultConfig() {
        return {
            /**
             * Reference to a store models have been removed from.
             *
             * @config {Core.data.Store}
             * @default
             */
            store : undefined,

            /**
             * List of models removed from the store.
             *
             * @config {Core.data.Model[]}
             * @default
             */
            modelList : undefined,

            /**
             * Models removing context.
             *
             * @config {Object}
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
        return 'RemoveAction';
    }

    //<debug>
    afterConfig() {
        super.afterConfig();

        console.assert(
            this.store instanceof Store &&
            Array.isArray(this.modelList) &&
            this.modelList.length &&
            this.modelList.every(m => m instanceof Model) &&
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

    get context() {
        return this[CONTEXT_PROP];
    }

    set context(context) {
        //<debug>
        console.assert(
            !this[CONTEXT_PROP] &&
            context instanceof Map &&
            Array.from(context.entries()).every(([k, v]) => k instanceof Model && typeof v === 'number'),
            "Can't set removal context, removal context is required, it should be Map with Model instances as keys and numeric values, and it can be set only once!"
        );
        //</debug>

        this[CONTEXT_PROP] = context;
    }

    undo() {
        const { store, context, modelList, silent } = this;

        // Let's sort models by index such that models with lesser index
        // were inserted back first, thus making valid index of models following.
        modelList.sort((lhs, rhs) => {
            const lhsIndex = context.get(lhs),
                rhsIndex = context.get(rhs);

            // Here, in contrast to InsertAction, index is always present
            return lhsIndex - rhsIndex;
        });

        modelList.forEach(m => {
            const index = context.get(m);

            // Insert at previous index
            store.insert(index, m, silent);
        });
    }

    redo() {
        const me = this;
        me.store.remove(me.modelList, me.silent);
    }
}
