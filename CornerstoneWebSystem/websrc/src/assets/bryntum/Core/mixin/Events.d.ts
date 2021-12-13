import Base from "../Base.js";

// to avoid dependency on `chronograph`
type AnyConstructor<A = any>   = new (...input: any[]) => A

export declare class EventsMixin {
    on (...args) : any

    un (...args) : any

    trigger (...args) : any

    hasListener (eventName : string) : boolean
}

declare const Events : <T extends AnyConstructor<Base>>(base : T) => AnyConstructor<InstanceType<T> & EventsMixin>

export default Events
