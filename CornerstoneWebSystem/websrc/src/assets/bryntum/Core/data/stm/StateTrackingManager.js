/**
 * @module Core/data/stm/StateTrackingManager
 */
import Base from '../../Base.js';
import Events from '../../mixin/Events.js';
import StateBase from './state/StateBase.js';
import DisabledState from './state/DisabledState.js';
import ReadyState from './state/ReadyState.js';
import RecordingState from './state/RecordingState.js';
import RestoringState from './state/RestoringState.js';
import AutoReadyState from './state/AutoReadyState.js';
import AutoRecordingState from './state/AutoRecordingState.js';
import Registry from './state/Registry.js';
import UpdateAction from './action/UpdateAction.js';
import InsertChildAction from './action/InsertChildAction.js';
import RemoveChildAction from './action/RemoveChildAction.js';
import AddAction from './action/AddAction.js';
import InsertAction from './action/InsertAction.js';
import RemoveAction from './action/RemoveAction.js';
import RemoveAllAction from './action/RemoveAllAction.js';
import { STATE_PROP, STORES_PROP, QUEUE_PROP, POS_PROP, TRANSACTION_PROP, TRANSACTION_TIMER_PROP, AUTO_RECORD_PROP } from './Props.js';

export const makeModelUpdateAction = (model, newData, oldData) => {
    return new UpdateAction({
        model,
        newData,
        oldData
    });
};

export const makeModelInsertChildAction = (parentModel, insertIndex, childModels, context) => {
    return new InsertChildAction({
        parentModel,
        childModels,
        insertIndex,
        context
    });
};

export const makeModelRemoveChildAction = (parentModel, childModels, context) => {
    return new RemoveChildAction({
        parentModel,
        childModels,
        context
    });
};

export const makeStoreModelAddAction = (store, modelList, silent) => {
    return new AddAction({
        store,
        modelList,
        silent
    });
};

export const makeStoreModelInsertAction = (store, insertIndex, modelList, context, silent) => {
    return new InsertAction({
        store,
        insertIndex,
        modelList,
        context,
        silent
    });
};

export const makeStoreModelRemoveAction = (store, modelList, context, silent) => {
    return new RemoveAction({
        store,
        modelList,
        context,
        silent
    });
};

export const makeStoreRemoveAllAction = (store, allRecords, silent) => {
    return new RemoveAllAction({
        store,
        allRecords,
        silent
    });
};

const stateTransition = (stm, event, ...args) => {
    const newState = event.call(stm[STATE_PROP], stm, ...args);

    if (typeof newState === 'string') {
        stm[STATE_PROP] = Registry.resolveStmState(newState);
    }
    else if (newState instanceof StateBase) {
        stm[STATE_PROP] = newState;
    }
    else if (Array.isArray(newState)) {
        const [state, next] = newState;

        if (typeof state === 'string') {
            stm[STATE_PROP] = Registry.resolveStmState(state);
        }
        else if (state instanceof StateBase) {
            stm[STATE_PROP] = state;
        }
        else if (state && typeof state === 'object') {
            stm = Object.assign(stm, state);
            stm[STATE_PROP] = Registry.resolveStmState(stm[STATE_PROP]);
        }

        if (typeof next === 'function') {
            stateTransition(stm, next, ...args);
        }
    }
    else if (newState && typeof newState === 'object') {
        stm = Object.assign(stm, newState);
        stm[STATE_PROP] = Registry.resolveStmState(stm[STATE_PROP]);
    }
};

/**
 * State Tracking Manager.
 *
 * When enabled tracks state of every store registered via {@link #function-addStore}.
 * Use {@link #function-undo} / {@link #function-redo} method calls to restore state to a particular
 * point in time
 *
 * @example
 *
 * stm = new StateTrackingManager({
 *     autoRecord : true,
 *     listeners  : {
 *        'recordingstop' : () => {
 *            // your custom code to update undo/redo GUI controls
 *            updateUndoRedoControls();
 *        },
 *        'restoringstop' : ({ stm }) => {
 *            // your custom code to update undo/redo GUI controls
 *            updateUndoRedoControls();
 *        }
 *    },
 *    getTransactionTitle : (transaction) => {
 *        // your custom code to analyze the transaction and return custom transaction title
 *        const lastAction = transaction.queue[transaction.queue.length - 1];
 *
 *        if (lastAction instanceof AddAction) {
 *            let title = 'Add new record';
 *        }
 *
 *        return title;
 *    }
 * });
 *
 * stm.addStore(userStore);
 * stm.addStore(companyStore);
 * stm.addStore(otherStore);
 *
 * stm.enable();
 */
export default class StateTrackingManager extends Events(Base) {

    static get defaultConfig() {
        return {
            /**
             * Default manager disabled state
             *
             * @config {Boolean}
             * @default
             */
            disabled : true,

            /**
             * Whether to start transaction recording automatically in case the Manager is enabled.
             *
             * In the auto recording mode, the manager waits for the first change in any store being managed and starts a transaction, i.e.
             * records any changes in its monitored stores. The transaction lasts for {@link #config-autoRecordTransactionStopTimeout} and
             * afterwards creates one undo/redo step, including all changes in the stores during that period of time.
             *
             * In non auto recording mode you have to call {@link #function-startTransaction} / {@link #function-stopTransaction} to start and end
             * a transaction.
             *
             * @config {Boolean}
             * @default
             */
            autoRecord : false,

            /**
             * The transaction duration (in ms) for the auto recording mode {@link #config-autoRecord}
             *
             * @config {Number}
             * @default
             */
            autoRecordTransactionStopTimeout : 100,

            /**
             * Store model update action factory
             *
             * @config {Function}
             * @default
             * @private
             */
            makeModelUpdateAction : makeModelUpdateAction,

            /**
             * Store insert child model action factory.
             *
             * @config {Function}
             * @default
             * @private
             */
            makeModelInsertChildAction : makeModelInsertChildAction,

            /**
             * Store remove child model action factory.
             *
             * @config {Function}
             * @default
             * @private
             */
            makeModelRemoveChildAction : makeModelRemoveChildAction,

            /**
             * Store add model action factory.
             *
             * @config {Function}
             * @default
             * @private
             */
            makeStoreModelAddAction : makeStoreModelAddAction,

            /**
             * Store insert model action factory.
             *
             * @config {Function}
             * @default
             * @private
             */
            makeStoreModelInsertAction : makeStoreModelInsertAction,

            /**
             * Store remove model action factory.
             *
             * @config {Function}
             * @default
             * @private
             */
            makeStoreModelRemoveAction : makeStoreModelRemoveAction,

            /**
             * Store remove all models action factory.
             *
             * @config {Function}
             * @default
             * @private
             */
            makeStoreRemoveAllAction : makeStoreRemoveAllAction,

            /**
             * Function to create a transaction title if none is provided.
             *
             * The function receives a transaction and should return a title.
             *
             * @config {Function}
             * @default
             */
            getTransactionTitle : null
        };
    }

    construct(...args) {
        const me = this;

        Object.assign(me, {
            [STATE_PROP]             : ReadyState,
            [STORES_PROP]            : [],
            [QUEUE_PROP]             : [],
            [POS_PROP]               : 0,
            [TRANSACTION_PROP]       : null,
            [TRANSACTION_TIMER_PROP] : null,
            [AUTO_RECORD_PROP]       : false
        });

        super.construct(...args);
    }

    doDestroy() {
        super.doDestroy();
        this.destroyProperties(STATE_PROP);
    }

    /**
     * Gets current state of the manager
     *
     * @return {Core.data.stm.state.StateBase}
     */
    get state() {
        return this[STATE_PROP];
    }

    /**
     * Gets current undo/redo queue position
     *
     * @return {Number}
     */
    get position() {
        return this[POS_PROP];
    }

    /**
     * Gets current undo/redo queue length
     *
     * @return {Number}
     */
    get length() {
        return this[QUEUE_PROP].length;
    }

    /**
     * Gets all the stores registered in STM
     *
     * @return {Core.data.Store[]}
     */
    get stores() {
        return Array.from(this[STORES_PROP]);
    }

    /**
     * Checks if a store has been added to the manager
     *
     * @param  {Core.data.Store} store
     * @return {Boolean}
     */
    hasStore(store) {
        return this[STORES_PROP].includes(store);
    }

    /**
     * Adds a store to the manager
     *
     * @param {Core.data.Store} store
     */
    addStore(store) {
        //<debug>
        console.assert(
            !this.hasStore(store),
            'Can\'t add store to the STM manager, store is already added into the STM manager!'
        );
        //</debug>

        if (!this.hasStore(store)) {

            this[STORES_PROP].push(store);

            store.stm = this;
        }
    }

    /**
     * Removes a store from the manager
     *
     * @param {Core.data.Store} store
     */
    removeStore(store) {
        //<debug>
        console.assert(
            this.hasStore(store),
            'Can\'t remove store from the STM manager, store isn\'t registered in the STM manager!'
        );
        //</debug>

        if (this.hasStore(store)) {
            this[STORES_PROP] = this[STORES_PROP].filter(s => s !== store);
            store.stm = null;
        }
    }

    /**
     * Returns previously added store by it's id or undefined if store with the given id isn't added.
     *
     * @param  {String|Number} id
     * @return {Core.data.Store}
     * @deprecated 2.1
     */
    getStoreById(id) {
        return this[STORES_PROP].find(s => s.id === id);
    }

    /**
     * Calls `fn` for each store registered in STM.
     *
     * @param {Function} fn (store, id) => ...
     */
    forEachStore(fn) {
        this[STORES_PROP].forEach(s => fn(s, s.id));
    }

    /**
     * Get/set manager disabled state
     *
     * @property {Boolean}
     */
    get disabled() {
        return this.state === DisabledState;
    }

    set disabled(val) {
        const me = this;

        if (me.disabled != val) {
            if (val) {
                stateTransition(me, me.state.onDisable, me);
            }
            else {
                stateTransition(me, me.state.onEnable, me);
            }
        }
    }

    /**
     * Enables manager
     */
    enable() {
        this.disabled = false;
    }

    /**
     * Disables manager
     */
    disable() {
        this.disabled = true;
    }

    /**
     * Checks manager ready state
     *
     * @return {Boolean}
     */
    get isReady() {
        return this.state === ReadyState || this.state === AutoReadyState;
    }

    /**
     * Checks manager recording state
     *
     * @return {Boolean}
     */
    get isRecording() {
        return this.state === RecordingState || this.state === AutoRecordingState;
    }

    /**
     * Gets/sets manager auto record option
     *
     * @property {Boolean}
     */
    get autoRecord() {
        return this[AUTO_RECORD_PROP];
    }

    set autoRecord(value) {
        const me = this;

        if (me.autoRecord != value) {
            if (value) {
                stateTransition(me, me.state.onAutoRecordOn, me);
            }
            else {
                stateTransition(me, me.state.onAutoRecordOff, me);
            }
        }
    }

    /**
     * Starts undo/redo recording transaction.
     *
     * @param {String} [title=nul]
     */
    startTransaction(title = null) {
        stateTransition(this, this.state.onStartTransaction, title);
    }

    /**
     * Stops undo/redo recording transaction
     *
     * @param {String} [title=null]
     */
    stopTransaction(title = null) {
        stateTransition(this, this.state.onStopTransaction, title);
    }

    /**
     * Stops undo/redo recording transaction after {@link #config-autoRecordTransactionStopTimeout} delay.
     *
     * @private
     */
    stopTransactionDelayed() {
        stateTransition(this, this.state.onStopTransactionDelayed);
    }

    /**
     * Rejects currently recorded transaction.
     */
    rejectTransaction() {
        stateTransition(this, this.state.onRejectTransaction);
    }

    /**
     * Gets currently recording STM transaction.
     *
     * @return {Core.data.stm.Transaction}
     */
    get transaction() {
        return this[TRANSACTION_PROP];
    }

    /**
     * Gets titles of all recorded undo/redo transactions
     *
     * @return {String[]}
     */
    get queue() {
        return this[QUEUE_PROP].map((t) => t.title);
    }
    
    //<debug>
    get rawQueue() {
        return this[QUEUE_PROP];
    }
    //</debug>

    /**
     * Gets manager restoring state.
     *
     * @return {Boolean}
     */
    get isRestoring() {
        return this.state === RestoringState;
    }

    /**
     * Checks if the manager can undo.
     *
     * @return {Boolean}
     */
    get canUndo() {
        return this.state.canUndo(this);
    }

    /**
     * Checks if the manager can redo.
     *
     * @return {Boolean}
     */
    get canRedo() {
        return this.state.canRedo(this);
    }

    /**
     * Undoes current undo/redo transaction.
     *
     * @param {Number} [steps=1]
     */
    undo(steps = 1) {
        stateTransition(this, this.state.onUndo, steps);
    }

    /**
     * Undoes all transactions.
     */
    undoAll() {
        this.undo(this.length);
    }

    /**
     * Redoes current undo/redo transaction.
     *
     * @param {Number} [steps=1]
     */
    redo(steps = 1) {
        stateTransition(this, this.state.onRedo, steps);
    }

    /**
     * Redoes all transactions.
     */
    redoAll() {
        this.redo(this.length);
    }

    /**
     * Resets undo/redo queue.
     */
    resetQueue(/* private */options = { undo : true, redo : true }) {
        stateTransition(this, this.state.onResetQueue, options);
    }

    /**
     * Resets undo queue.
     */
    resetUndoQueue() {
        this.resetQueue({ undo : true });
    }

    /**
     * Resets redo queue.
     */
    resetRedoQueue() {
        this.resetQueue({ redo : true });
    }

    notifyStoresAboutStateRecordingStart(transaction) {
        this.forEachStore((store) => {
            store.onStmRecordingStart && store.onStmRecordingStart(this, transaction);
        });
        /**
         * Fired upon state recording operation starts.
         *
         * @event recordingStart
         * @param {Core.data.stm.StateTrackingManager} stm
         * @param {Core.data.stm.Transaction} transaction
         */
        this.trigger('recordingStart', { stm : this, transaction });
    }

    notifyStoresAboutStateRecordingStop(transaction, reason) {
        this.forEachStore((store) => {
            store.onStmRecordingStop && store.onStmRecordingStop(this, transaction, reason);
        });
        /**
         * Fired upon state recording operation stops.
         *
         * @event recordingStop
         * @param {Core.data.stm.StateTrackingManager} stm
         * @param {Core.data.stm.Transaction} transaction
         * @param {Object} reason Transaction stop reason
         * @param {Boolean} reason.stop Transaction recording has been stopped in a normal way.
         * @param {Boolean} reason.disabled Transaction recording has been stopped due to STM has been disabled.
         * @param {Boolean} reason.rejected Transaction recording has been stopped due to transaction has been rejected.
         */
        this.trigger('recordingStop', { stm : this, transaction, reason });
    }

    notifyStoresAboutStateRestoringStart() {
        this.forEachStore((store) => {
            store.onStmRestoringStart && store.onStmRestoringStart(this);
        });
        /**
         * Fired upon state restoration operation starts.
         *
         * @event restoringStart
         * @param {Core.data.stm.StateTrackingManager} stm
         */
        this.trigger('restoringStart', { stm : this });
    }

    notifyStoresAboutStateRestoringStop() {
        this.forEachStore((store) => {
            store.onStmRestoringStop && store.onStmRestoringStop(this);
        });
        /**
         * Fired upon state restoration operation stops.
         *
         * @event restoringStop
         * @param {Core.data.stm.StateTrackingManager} stm
         */
        this.trigger('restoringStop', { stm : this });
    }

    notifyStoresAboutQueueReset(options) {
        this.forEachStore((store) => {
            store.onStmQueueReset && store.onStmQueueReset(this, options);
        });
        /**
         * Fired upon state undo/redo queue reset.
         *
         * @event queueReset
         * @param {Core.data.stm.StateTrackingManager} stm
         */
        this.trigger('queueReset', { stm : this, options });
    }

    /**
     * Method to call from model STM mixin upon model update
     *
     * @param {Core.data.Model} model
     * @param {Object} newData
     * @param {Object} oldData
     *
     * @private
     */
    onModelUpdate(model, newData, oldData) {
        stateTransition(this, this.state.onModelUpdate, model, newData, oldData);
    }

    /**
     * Method to call from model STM mixin upon tree model child insertion
     *
     * @param {Core.data.Model} parentModel Parent model
     * @param {Number} index Insertion index
     * @param {Core.data.Model[]} childModels Array of models inserted
     * @param {Map} context Map with inserted models as keys and objects with previous parent,
     *                      and index at previous parent.
     *
     * @private
     */
    onModelInsertChild(parentModel, index, childModels, context) {
        stateTransition(this, this.state.onModelInsertChild, parentModel, index, childModels, context);
    }

    /**
     * Method to call from model STM mixin upon tree model child removal
     *
     * @param {Core.data.Model} parentModel
     * @param {Core.data.Model[]} childModels
     * @param {Map} context
     *
     * @private
     */
    onModelRemoveChild(parentModel, childModels, context) {
        stateTransition(this, this.state.onModelRemoveChild, parentModel, childModels, context);
    }

    /**
     * Method to call from store STM mixin upon store models adding
     *
     * @param {Core.data.Store} store
     * @param {Core.data.Model[]} models
     * @param {Boolean} silent
     *
     * @private
     */
    onStoreModelAdd(store, models, silent) {
        stateTransition(this, this.state.onStoreModelAdd, store, models, silent);
    }

    /**
     * Method to call from store STM mixin upon store models insertion
     *
     * @param {Core.data.Store} store
     * @param {Number} index
     * @param {Core.data.Model[]} models
     * @param {Map} context
     * @param {Boolean} silent
     *
     * @private
     */
    onStoreModelInsert(store, index, models, context, silent) {
        stateTransition(this, this.state.onStoreModelInsert, store, index, models, context, silent);
    }

    /**
     * Method to call from store STM mixin upon store models removal
     *
     * @param {Core.data.Store} store
     * @param {Core.data.Model[]} models
     * @param {Object} context
     * @param {Boolean} silent
     *
     * @private
     */
    onStoreModelRemove(store, models, context, silent) {
        stateTransition(this, this.state.onStoreModelRemove, store, models, context, silent);
    }

    /**
     * Method to call from store STM mixin upon store clear
     *
     * @param {Core.data.Store} store
     * @param {Core.data.Model[]} allRecords
     * @param {Boolean} silent
     *
     * @private
     */
    onStoreRemoveAll(store, allRecords, silent) {
        stateTransition(this, this.state.onStoreRemoveAll, store, allRecords, silent);
    }
}
