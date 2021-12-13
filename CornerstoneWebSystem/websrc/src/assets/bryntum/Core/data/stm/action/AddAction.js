import ActionBase from './ActionBase.js';
import Store from '../../Store.js';
import Model from '../../Model.js';

/**
 * @module Core/data/stm/action/AddAction
 */
const STORE_PROP      = Symbol('STORE_PROP');
const MODEL_LIST_PROP = Symbol('MODEL_LIST_PROP');

/**
 * Action to record the fact of models adding to a store.
 */
export default class AddAction extends ActionBase {

    static get defaultConfig() {
        return {
            /**
             * Reference to a store models have been added into.
             *
             * @config {Core.data.Store}
             * @default
             */
            store : undefined,

            /**
             * List of models added into the store.
             *
             * @config {Core.data.Model[]}
             * @default
             */
            modelList : undefined,

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
        return 'AddAction';
    }

    //<debug>
    afterConfig() {
        super.afterConfig();

        console.assert(
            this.store instanceof Store &&
            Array.isArray(this.modelList) &&
            this.modelList.length &&
            this.modelList.every(m => m instanceof Model),
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
            list.length,
            list.every(m => m instanceof Model),
            "Can't set model list, model list is required it should be array of Models and it can be set only once!"
        );
        //</debug>

        this[MODEL_LIST_PROP] = list.slice(0);
    }

    undo() {
        const me = this;
        me.store.remove(me.modelList, me.silent);
    }

    redo() {
        const me = this;
        me.store.add(me.modelList, me.silent);
    }
}
