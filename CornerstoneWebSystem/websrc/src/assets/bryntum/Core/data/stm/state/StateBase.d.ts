import Model from '../../Model.js';
import Store from '../../Store.js';
import StateTrackingManager from '../StateTrackingManager.js';

export type TransitionResult = StateBase | String | Partial<StateTrackingManager> | [StateBase | String | Partial<StateTrackingManager>, (stm : StateTrackingManager) => TransitionResult]

export default class StateBase {
    canUndo() : boolean
    canRedo() : boolean

    onUndo(stm : StateTrackingManager) : TransitionResult
    onRedo(stm : StateTrackingManager) : TransitionResult

    onStartTransaction(stm : StateTrackingManager, title? : String) : TransitionResult
    onStopTransaction(stm : StateTrackingManager, title? : String) : TransitionResult
    onStopTransactionDelayed(stm : StateTrackingManager) : TransitionResult

    onEnable(stm : StateTrackingManager) : TransitionResult
    onDisable(stm : StateTrackingManager) : TransitionResult

    onAutoRecordOn(stm : StateTrackingManager) : TransitionResult
    onAutoRecordOff(stm : StateTrackingManager) : TransitionResult

    onResetQueue(stm : StateTrackingManager) : TransitionResult

    onModelUpdate(stm : StateTrackingManager, model : Model, newData : Object, oldDate : Object) : TransitionResult
    onModelInsertChild(stm : StateTrackingManager, parentModel : Model, index : number, childModels : Model[], context : Object) : TransitionResult
    onModelRemoveChild(stm : StateTrackingManager, parentModel : Model, childModels : Model[], context : Object) : TransitionResult

    onStoreModelAdd(stm : StateTrackingManager, store : Store, models : Model[], context : Object, silent : boolean) : TransitionResult
    onStoreModelInsert(stm : StateTrackingManager, store : Store, index : number, models : Model[], context : Object, silent : boolean) : TransitionResult
    onStoreModelRemove(stm : StateTrackingManager, store : Store, models : Model[], context : Object, silent : boolean) : TransitionResult
    onStoreRemoveAll(stm : StateTrackingManager, store : Store, allRecords : Model[], silent : boolean) : TransitionResult
}
