import Base from '../../Base.js';
import ObjectHelper from '../../helper/ObjectHelper.js';

/**
 * @module Core/data/mixin/StoreSearch
 */

const stringFound = (value, find) => String(value).toLowerCase().indexOf(find) !== -1,
    comparisons = {
        string  : stringFound,
        number  : stringFound,
        boolean : stringFound,
        date    : (value, find) => {
            if (value instanceof Date && find instanceof Date) {
                return String(value) === String(find);
            }
            return String(value.getMonth() + 1).indexOf(find) !== -1 ||
                String(value.getDate()).indexOf(find) !== -1 ||
                String(value.getFullYear()).indexOf(find) !== -1;
        }
    };

/**
 * Mixin for Store that handles searching (multiple records) and finding (single record).
 *
 * @example
 * // find all records that has a field containing the string john
 * let hits = store.search('john');
 *
 * @mixin
 */
export default Target => class StoreSearch extends (Target || Base) {
    //region Search (multiple hits)

    /**
     * Find all hits.
     * @param find Value to search for
     * @param {Object[]} fields Fields to search value in
     * @returns {*} Array of hits, in the format { index: x, data: record }
     */
    search(find, fields = null) {
        const records    = this.storage.values,
            len        = records.length,
            found      = [];

        if (find == null) {
            return null;
        }

        if (typeof find === 'string') {
            find = String(find).toLowerCase();
        }

        let i,
            record,
            value,
            valueType,
            comparison;

        for (i = 0; i < len; i++) {
            record = records[i];
            for (let key of fields || record.fieldNames) {
                value = record[key];
                valueType = (value instanceof Date) ? 'date' : typeof value;
                comparison = comparisons[valueType];
                if (value && comparison && comparison(value, find)) {
                    found.push({
                        index : i,
                        data  : record,
                        field : key,
                        id    : record.id
                    });
                }
            }
        }

        return found;
    }

    /**
     * Find all hits in a column
     * @param field The store field to search in
     * @param value Value to search for
     * @returns {*} Array of hits, in the format { index: x, data: record }
     */
    findByField(field, value) {
        let records = this.storage.values,
            i,
            len     = records.length,
            record,
            found   = [],
            fieldValue;

        if (value !== null && value !== undefined) {
            value = String(value).toLowerCase();
        }

        for (i = 0; i < len; i++) {
            record      = records[i];
            fieldValue  = record[field];

            let type = fieldValue instanceof Date ? 'date' : typeof fieldValue;

            let comparison = {
                'date'      : () => Boolean(fieldValue) && fieldValue.toLocaleString().includes(value),
                'string'    : () => Boolean(fieldValue) && fieldValue.toLowerCase().includes(value),
                'number'    : () => typeof fieldValue === 'number' && fieldValue.toString().includes(value),
                'object'    : () => fieldValue === value, // typeof null === object
                'undefined' : () => fieldValue === value
            };

            if (((value === null || value === undefined) && fieldValue === value) || value && comparison[type]()) {
                found.push({
                    id    : record.id,
                    index : i,
                    data  : record
                });
            }
        }

        return found;
    }

    //endregion

    //region Find (single hit)

    /**
     * Finds the first record for which the specified function returns true
     * @param {Function} fn Comparison function, called with record as parameter
     * @returns {Core.data.Model} Record or null if none found
     *
     * @example
     * store.find(record => record.color === 'blue');
     */
    find(fn) {
        return this.storage.values.find(fn);
    }

    /**
     * Finds the first record for which the specified field has the specified value
     * @param {String} fieldName Field name
     * @param {*} value Value to find
     * @returns {Core.data.Model} Record or null if none found
     */
    findRecord(fieldName, value, searchAllRecords = false) {
        const matchFn = r => ObjectHelper.isEqual(r[fieldName], value);

        if (this.tree) {
            return this.query(matchFn)[0];
        }
        return (searchAllRecords ? this.storage.allValues : this.storage.values).find(matchFn);
    }

    /**
     * Searches the Store records using the passed function.
     * @param fn A function that is called for each record. Return true to indicate a match
     * @returns {Core.data.Model[]} An array of the matching Records
     */
    query(fn) {
        if (this.isTree) {
            const matches = [];

            this.traverse((node) => {
                if (fn(node)) {
                    matches.push(node);
                }
            });
            return matches;
        }

        return this.storage.values.filter(fn);
    }
    //endregion

    //region Others

    /**
     * Returns true if the supplied function returns true for any record in the store
     * @param fn
     * @returns {Boolean}
     *
     * @example
     * store.some(record => record.age > 95); // true if any record has age > 95
     */
    some(fn) {
        return this.storage.values.some(fn);
    }

    //endregion
};
