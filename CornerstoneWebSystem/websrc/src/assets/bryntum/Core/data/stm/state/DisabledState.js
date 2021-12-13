/**
 * @module Core/data/stm/state/DisabledState
 */
import StateBase, { throwInvalidMethodCall } from './StateBase.js';
import { AUTO_RECORD_PROP } from '../Props.js';
import Registry from './Registry.js';
import { resetQueue } from '../Helpers.js';

/**
 * STM disabled state class.
 *
 * @internal
 */
export class DisabledStateClass extends StateBase {

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

    onEnable(stm) {
        return stm.autoRecord ? 'autoreadystate' : 'readystate';
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

    onResetQueue(stm, options) {
        return resetQueue(stm, options);
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
 * STM disabled state.
 *
 * @internal
 */
const DisabledState = new DisabledStateClass();
export default DisabledState;

Registry.registerStmState('disabledstate', DisabledState);
