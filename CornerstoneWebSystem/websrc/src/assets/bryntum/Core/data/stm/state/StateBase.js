/**
 * @module Core/data/stm/state/StateBase
 */
import Base from '../../../Base.js';

export const throwAbstractMethodCall = () => {
    throw new Error('Abstract method call!');
};

export const throwInvalidMethodCall = () => {
    throw new Error('Method cannot be called at this state!');
};

/**
 * Abstract class for STM states
 *
 * Every on* method should return a state for the STM which it should switch to
 * or throw an exception that this call at this state is illegal.
 *
 * Interface of this class mirrors interface of {@link Core.data.stm.StateTrackingManager}.
 *
 * @abstract
 */
export default class StateBase extends Base {

    canUndo(stm) {
        throwAbstractMethodCall();
    }

    canRedo(stm) {
        throwAbstractMethodCall();
    }

    onUndo(stm) {
        throwAbstractMethodCall();
    }

    onRedo(stm) {
        throwAbstractMethodCall();
    }

    onStartTransaction(stm) {
        throwAbstractMethodCall();
    }

    onStopTransaction(stm) {
        throwAbstractMethodCall();
    }

    onStopTransactionDelayed(stm) {
        throwAbstractMethodCall();
    }

    onRejectTransaction(stm) {
        throwAbstractMethodCall();
    }

    onEnable(stm) {
        throwAbstractMethodCall();
    }

    onDisable(stm) {
        throwAbstractMethodCall();
    }

    onAutoRecordOn(stm) {
        throwAbstractMethodCall();
    }

    onAutoRecordOff(stm) {
        throwAbstractMethodCall();
    }

    onResetQueue(stm) {
        throwAbstractMethodCall();
    }

    onModelUpdate(stm) {
        throwAbstractMethodCall();
    }

    onStoreModelAdd(stm) {
        throwAbstractMethodCall();
    }

    onStoreModelInsert(stm) {
        throwAbstractMethodCall();
    }

    onStoreModelRemove(stm) {
        throwAbstractMethodCall();
    }

    onStoreModelRemoveAll(stm) {
        throwAbstractMethodCall();
    }

    onModelInsertChild(stm) {
        throwAbstractMethodCall();
    }

    onModelRemoveChild(stm) {
        throwAbstractMethodCall();
    }
}
