import Bag from '../util/Bag.js';

/**
 * @module Core/data/StoreBag
 */

export default class StoreBag extends Bag {

    add(...toAdd) {
        if (toAdd.length === 1 && Array.isArray(toAdd[0])) {
            toAdd = toAdd[0];
        }

        // ignore unpersistable records
        return super.add(...toAdd.filter(record => record.isPersistable));
    }

};
