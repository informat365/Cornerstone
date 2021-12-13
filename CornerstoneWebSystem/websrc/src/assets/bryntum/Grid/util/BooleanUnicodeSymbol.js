export default class BooleanUnicodeSymbol {
    constructor(value) {
        this._value = value;
    }
    
    get value() {
        return this._value;
    }
    
    toString() {
        return Boolean(this.value) ? '✓' : '';
    }
}
