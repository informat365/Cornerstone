import Base from "../Base.js";
import Events from "../mixin/Events.js";
import Model from "./Model.js";

export default class Store extends Events(Base) {
    modelClass          : typeof Model

    modelInstanceT      : InstanceType<this[ 'modelClass' ]>

    data                : this[ 'modelInstanceT' ][] | Object[]

    // root node is intentionally typed as just `Model` and not as `this[ 'modelInstanceT' ]`
    // this is to be able to have different type for root node (in engine its ProjectMixin)
    rootNode            : Model

    count               : number
    allCount            : number

    first               : this[ 'modelInstanceT' ]
    last                : this[ 'modelInstanceT' ]


    constructor(...args : any[])

    forEach(fn : (model : InstanceType<this[ 'modelClass' ]>, index? : number) => void) : void

    map<T>(mapFn : (model : this['modelInstanceT']) => T, thisObj? : any) : T[]

    reduce<T>(reduceFn : (result : T, model : this['modelInstanceT']) => T, initialValue? : T, thisObj? : any) : T

    includes (model : this['modelInstanceT']) : boolean

    remove (records : InstanceType<this[ 'modelClass' ]> | InstanceType<this[ 'modelClass' ]>[], silent? : boolean)
        : InstanceType<this[ 'modelClass' ]>[]

    add (records : Partial<InstanceType<this[ 'modelClass' ]>> | Partial<InstanceType<this[ 'modelClass' ]>>[], silent? : boolean)
        : this[ 'modelInstanceT' ][]

    removeAll (silent? : boolean) : void

    getRange (start? : number, end? : number, all? : boolean) : this[ 'modelInstanceT' ][]

    getById (id : any) : this[ 'modelInstanceT' ]

    loadData (data : any)

    makeChained (filterFn : (model : this['modelInstanceT']) => boolean) : this

    fillFromMaster() : void

    beginBatch() : void

    endBatch() : void
}
