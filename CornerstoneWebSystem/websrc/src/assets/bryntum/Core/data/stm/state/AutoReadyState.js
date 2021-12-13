/**
 * @module Core/data/stm/state/AutoReadyState
 */
import { throwInvalidMethodCall } from './StateBase.js';
import Transaction from '../Transaction.js';
import { ReadyStateClass } from './ReadyState.js';
import { STATE_PROP, TRANSACTION_PROP, AUTO_RECORD_PROP } from '../Props.js';
import Registry from './Registry.js';

/**
 * STM transaction autostart ready state class.
 *
 * @internal
 */
export class AutoReadyStateClass extends ReadyStateClass {

    onAutoRecordOn() {
        throwInvalidMethodCall();
    }

    onAutoRecordOff() {
        return {
            [STATE_PROP]       : 'readystate',
            [AUTO_RECORD_PROP] : false
        };
    }

    onStartTransaction(stm, title) {
        const transaction = new Transaction({ title : title });

        return [{
            [STATE_PROP]       : 'autorecordingstate',
            [TRANSACTION_PROP] : transaction
        }, () => {
            stm.notifyStoresAboutStateRecordingStart(transaction);
            stm.stopTransactionDelayed();
        }];
    }

    onModelUpdate(stm, model, newData, oldData) {
        stm.startTransaction();
        stm.onModelUpdate(model, newData, oldData);
    }

    onModelInsertChild(stm, parentModel, index, childModels, context) {
        stm.startTransaction();
        stm.onModelInsertChild(parentModel, index, childModels, context);
    }

    onModelRemoveChild(stm, parentModel, childModels, context) {
        stm.startTransaction();
        stm.onModelRemoveChild(parentModel, childModels, context);
    }

    onStoreModelAdd(stm, store, models, silent) {
        stm.startTransaction();
        stm.onStoreModelAdd(store, models, silent);
    }

    onStoreModelInsert(stm, store, index, models, context, silent) {
        stm.startTransaction();
        stm.onStoreModelInsert(store, index, models, silent);
    }

    onStoreModelRemove(stm, store, models, context, silent) {
        stm.startTransaction();
        stm.onStoreModelRemove(store, models, context, silent);
    }

    onStoreRemoveAll(stm, store, allRecords, silent) {
        stm.startTransaction();
        stm.onStoreRemoveAll(store, allRecords, silent);
    }
}

/**
 * STM transaction autostart ready state.
 *
 * @internal
 */
const AutoReadyState = new AutoReadyStateClass();
export default AutoReadyState;

Registry.registerStmState('autoreadystate', AutoReadyState);
