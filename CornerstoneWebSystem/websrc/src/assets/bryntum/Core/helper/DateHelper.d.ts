export default class DateHelper {

    static parse (dateString : string | Date, format? : string) : Date

    static add(date : string | Date, amount : number, unit? : string) : Date

    static normalizeUnit(unit : string) : string

    static startOf(date : Date, unit? : string, clone? : boolean) : Date

    static clearTime(date : Date, clone? : boolean) : Date

    static set(date : Date, unit : string, amount : number) : Date

    static getStartOfNextDay(date : Date, clone : boolean, noNeedToClearTime : boolean) : Date

    static getEndOfPreviousDay(date : Date, noNeedToClearTime : boolean) : Date

    static getNext(date : Date, unit : string, increment? : number, weekStartDate? : number) : Date
}
