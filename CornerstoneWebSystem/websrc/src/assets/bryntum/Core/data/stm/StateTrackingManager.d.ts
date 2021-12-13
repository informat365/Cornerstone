import Model from '../Model.js';
import Store from '../Store.js';
import StateBase from './state/StateBase.js';
import Transaction from './Transaction.js'
import Base from '../../Base.js';

export default class StateTrackingManager extends Base {
    state : StateBase
    position : number
    length : number
    disabled : boolean
    isReady : boolean
    isRecording : boolean
    isRestoring : boolean
    autoRecord : boolean
    canUndo : boolean
    canRedo : boolean
    stores : Store[]
    transaction : Transaction
    queue : [string]

    constructor(config? : Partial<StateTrackingManager>)

    hasStore(store : Store) : boolean
    addStore(store : Store) : void
    removeStore(store : Store) : void
    getStoreById(id : string | number) : Store
    forEachStore(fn : (store : Store) => void) : void

    enable() : void
    disable() : void

    startTransaction(title? : string) : void
    stopTransaction(title? : string) : void
    stopTransactionDelayed() : void
    rejectTransaction() : void

    undo(steps? : number) : void
    undoAll() : void

    redo(steps? : number) : void
    redoAll() : void

    resetQueue() : void
    resetUndoQueue() : void
    resetRedoQueue() : void

    onModelUpdate(model : Model, newData : object, oldDate : object) : void
    onModelInsertChild(parentModel : Model, index : number, childModels : Model[], context : object) : void
    onModelRemoveChild(parentModel : Model, childModels : Model[], context : object) : void

    onStoreModelAdd(store : Store, models : Model[], silent : boolean) : void
    onStoreModelInsert(store : Store, index : number, models : Model, silent : boolean) : void
    onStoreModelRemove(store : Store, models : Model[], context : object, silent : boolean) : void

    onStoreRemoveAll(store : Store, allRecords : Model[], silent : boolean) : void
}
