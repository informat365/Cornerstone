import Base from "../Base.js";
import Store from "./Store.js";

export default class Model extends Base {
    id                  : string | number

    internalId          : string | number

    static fields?      : any[]

    fields              : any[]

    stores              : Store[]

    data                : object

    parent              : this
    children            : this[]

    childLevel          : number

    isRoot              : boolean

    isBatchUpdating     : boolean

    isDestroying        : boolean


    configure (config : object) : void

    constructor (...args : any[])

    construct (data? : object, store? : Store, meta? : object, skipExpose? : boolean, processingTree? : boolean) : void

    afterConstruct () : void
    afterConfigure () : void


    get (fieldName : string) : any
    set (fieldName : string | object, value? : any, silent? : boolean) : object


    joinStore (store : Store) : void
    unJoinStore (store : Store) : void

    appendChild<T extends Model> (child : T|T[]) : T|T[]
    insertChild<T extends Model> (child : T|T[], before? : T) : T|T[]

    remove () : void

    traverse (fn : (node : this) => void) : void

    getFieldDefinition (fieldName : string) : object

    copy(newId : this[ 'id' ]) : this

    beginBatch() : void
    endBatch(silent? : Boolean): void

    afterSet(field : string | object, value : any, silent : Boolean, fromRelationUpdate : Boolean, preResult : any[], wasSet : any) : void
}
