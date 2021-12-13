/**
 * @module Core/data/stm/action/RemoveAllAction
 */
import ActionBase from './ActionBase.js';
import Store from '../../Store.js';
import Model from '../../Model.js';

const STORE_PROP       = Symbol('STORE_PROP');
const ALL_RECORDS_PROP = Symbol('ALL_RECORDS_PROP');

/**
 * Action to record store remove all operation.
 */
export default class RemoveAllAction extends ActionBase {

    static get defaultConfig() {
        return {
            /**
             * Reference to a store cleared.
             *
             * @config {Core.data.Store}
             * @default
             */
            store : undefined,

            /**
             * All store records removed
             *
             * @config {Core.data.Model[]}
             * @default
             */
            allRecords : undefined,

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
        return 'RemoveAllAction';
    }

    //<debug>
    afterConfig() {
        super.afterConfig();

        console.assert(
            this.store instanceof Store,
            Array.isArray(this.allRecords) &&
            this.allRecords.length &&
            this.allRecords.every(m => m instanceof Model),
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

    get allRecords() {
        return this[ALL_RECORDS_PROP];
    }

    set allRecords(records) {
        //<debug>
        console.assert(
            !this[ALL_RECORDS_PROP] &&
            Array.isArray(records) &&
            records.length,
            records.every(m => m instanceof Model),
            "Can't all records list, all records list is required it should be array of Models and it can be set only once!"
        );
        //</debug>

        this[ALL_RECORDS_PROP] = records.slice(0);
    }

    undo() {
        const { store, allRecords, silent } = this;
        store.add(allRecords, silent);
    }

    redo() {
        const me = this;
        me.store.removeAll(me.silent);
    }
}
