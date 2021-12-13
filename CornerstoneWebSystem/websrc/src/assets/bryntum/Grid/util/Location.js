import Base from '../../Core/Base.js';

/**
 * This class encapsulates a reference to a specific grid cell based upon the record and column.
 *
 * TODO: This is not used yet in preference to hand coded raw objects which is not safe, and not
 * flexible. A Location may be created with less internal knowledge, such as by simple
 * row index and column index. It can use a robust internal representation and can
 * encapsulate knowledge such as how to retrieve DOM, and how to navigate.
 * https://app.assembla.com/spaces/bryntum/tickets/5160
 *
 * @internal
 */
export default class Location extends Base {
    get recordIndex() {
        return this._recordIndex;
    }

    set record(recordIndex) {
        this._record = recordIndex;
    }

    get column() {
        return this._column;
    }

    set column(column) {
        this._column = column;
    }

    get cell() {

    }
}
