/**
 * @module Core/data/stm/state/AutoRecordingState
 */
import Delayable from '../../../mixin/Delayable.js';
import { throwInvalidMethodCall } from './StateBase.js';
import { RecordingStateClass } from './RecordingState.js';
import { STATE_PROP, TRANSACTION_PROP, TRANSACTION_TIMER_PROP, QUEUE_PROP, POS_PROP, AUTO_RECORD_PROP } from '../Props.js';
import Registry from './Registry.js';

/**
 * STM recording state class.
 *
 * @internal
 */
export class AutoRecordingStateClass extends Delayable(RecordingStateClass) {

    onDisable(stm) {
        const transaction = stm[TRANSACTION_PROP];
        const timer       = stm[TRANSACTION_TIMER_PROP];

        if (timer) {
            this.clearTimeout(timer);
        }

        stm.notifyStoresAboutStateRecordingStop(transaction, { disabled : true });

        return {
            [STATE_PROP]             : 'disabledstate',
            [TRANSACTION_PROP]       : null,
            [TRANSACTION_TIMER_PROP] : null
        };
    }

    onAutoRecordOn(stm) {
        throwInvalidMethodCall();
    }

    onAutoRecordOff(stm) {
        const timer       = stm[TRANSACTION_TIMER_PROP];

        if (timer) {
            this.clearTimeout(timer);
        }

        return {
            [STATE_PROP]             : 'recordingstate',
            [AUTO_RECORD_PROP]       : false,
            [TRANSACTION_TIMER_PROP] : null
        };
    }

    onStopTransaction(stm, title) {
        const transaction = stm[TRANSACTION_PROP];
        const timer       = stm[TRANSACTION_TIMER_PROP];
        let position      = stm[POS_PROP];
        let queue         = stm[QUEUE_PROP];

        if (timer) {
            this.clearTimeout(timer);
        }

        if (transaction.length) {

            if (!transaction.title && !title && stm.getTransactionTitle) {
                transaction.title = stm.getTransactionTitle(transaction);
            }
            else if (title) {
                transaction.title = title;
            }

            queue[position] = transaction;
            queue.length    = ++position;
        }

        return [{
            [STATE_PROP]             : 'autoreadystate',
            [POS_PROP]               : position,
            [TRANSACTION_PROP]       : null,
            [TRANSACTION_TIMER_PROP] : null
        }, () => {
            stm.notifyStoresAboutStateRecordingStop(transaction, { stop : true });
        }];
    }

    onStopTransactionDelayed(stm) {
        let timer = stm[TRANSACTION_TIMER_PROP];

        if (timer) {
            this.clearTimeout(timer);
        }

        timer = this.setTimeout(
            () => {
                stm.stopTransaction();
            },
            stm.autoRecordTransactionStopTimeout
        );

        return {
            [STATE_PROP]             : AutoRecordingState,
            [TRANSACTION_TIMER_PROP] : timer
        };
    }

    onRejectTransaction(stm) {
        const transaction = stm[TRANSACTION_PROP],
            timer = stm[TRANSACTION_TIMER_PROP];

        if (timer) {
            this.clearTimeout(timer);
        }

        return [{
            [STATE_PROP]             : 'restoringstate',
            [TRANSACTION_PROP]       : null,
            [TRANSACTION_TIMER_PROP] : null
        }, () => {

            if (transaction.length) {
                transaction.undo();
            }

            return [
                'autoreadystate',
                () => {
                    stm.notifyStoresAboutStateRecordingStop(transaction, { rejected : true });
                }
            ];
        }];
    }

    onModelUpdate(stm, ...rest) {
        super.onModelUpdate(stm, ...rest);
        stm.stopTransactionDelayed();
    }

    onModelInsertChild(stm, ...rest) {
        super.onModelInsertChild(stm, ...rest);
        stm.stopTransactionDelayed();
    }

    onModelRemoveChild(stm, ...rest) {
        super.onModelRemoveChild(stm, ...rest);
        stm.stopTransactionDelayed();
    }

    onStoreModelAdd(stm, ...rest)    {
        super.onStoreModelAdd(stm, ...rest);
        stm.stopTransactionDelayed();
    }

    onStoreModelInsert(stm, ...rest) {
        super.onStoreModelInsert(stm, ...rest);
        stm.stopTransactionDelayed();
    }

    onStoreModelRemove(stm, ...rest) {
        super.onStoreModelRemove(stm, ...rest);
        stm.stopTransactionDelayed();
    }

    onStoreRemoveAll(stm, ...rest)   {
        super.onStoreRemoveAll(stm, ...rest);
        stm.stopTransactionDelayed();
    }
}

/**
 * STM recording state.
 *
 * @internal
 */
const AutoRecordingState = new AutoRecordingStateClass();
export default AutoRecordingState;

Registry.registerStmState('autorecordingstate', AutoRecordingState);
