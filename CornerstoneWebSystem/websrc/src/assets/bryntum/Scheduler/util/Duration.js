import DateHelper from '../../Core/helper/DateHelper.js';

/**
 * @module Scheduler/util/Duration
 */

/**
 * Class which represents a duration (having `unit` and `magnitude`)
 * @private
 */
export default class Duration {

    constructor(config) {
        Object.assign(this, config);
    }

    toString() {
        if (typeof this.magnitude !== 'number') {
            return '';
        }

        return this.magnitude + ' ' + DateHelper.getLocalizedNameOfUnit(this.unit, this.magnitude !== 1);
    }
};
