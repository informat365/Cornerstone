/**
 * @module Core/data/stm/state/RestoringState
 */
import StateBase, { throwInvalidMethodCall } from './StateBase.js';
import { AUTO_RECORD_PROP } from '../Props.js';
import Registry from './Registry.js';

/**
 * STM restoring state class.
 *
 * @internal
 */
export class RestoringStateClass extends StateBase {

    canUndo() {
        return false;
    }

    canRedo() {
        return false;
    }

    onUndo() {
        throwInvalidMethodCall();
    }

    onRedo() {
        throwInvalidMethodCall();
    }

    onEnable() {
        throwInvalidMethodCall();
    }

    onDisable() {
        throwInvalidMethodCall();
    }

    onAutoRecordOn() {
        return {
            [AUTO_RECORD_PROP] : true
        };
    }

    onAutoRecordOff() {
        return {
            [AUTO_RECORD_PROP] : false
        };
    }

    onStartTransaction() {
        throwInvalidMethodCall();
    }

    onStopTransaction()  {
        throwInvalidMethodCall();
    }

    onStopTransactionDelayed() {
        throwInvalidMethodCall();
    }

    onRejectTransaction() {
        throwInvalidMethodCall();
    }

    onQueueReset() {
        throwInvalidMethodCall();
    }

    onModelUpdate()      {}
    onModelInsertChild() {}
    onModelRemoveChild() {}
    onStoreModelAdd()    {}
    onStoreModelInsert() {}
    onStoreModelRemove() {}
    onStoreRemoveAll()   {}
}

/**
 * STM restoring state.
 *
 * @internal
 */
const RestoringState = new RestoringStateClass();
export default RestoringState;

Registry.registerStmState('restoringstate', RestoringState);
