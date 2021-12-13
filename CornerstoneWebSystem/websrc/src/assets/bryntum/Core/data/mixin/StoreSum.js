import Base from '../../Base.js';

/**
 * @module Core/data/mixin/StoreSum
 */

/**
 * Mixin for Store that handles summaries.
 *
 * @mixin
 */
export default Target => class StoreSum extends (Target || Base) {
    /**
     * Returns sum calculated by adding value of specified field for specified records. Defaults to using all records
     * in store
     * @param {String} field Field to summarize by
     * @param {Core.data.Model[]} records Records to summarize, uses all records if unspecified.
     * @returns {Number}
     */
    sum(field, records = this.storage.values) {
        if (!records) return 0;

        return records.reduce((sum, record) => {
            if (record.meta.specialRow) return sum;
            const v = Number(record[field]);

            return isNaN(v) ? sum : sum + v;
        }, 0);
    }

    /**
     * Returns min value for the specified field. Defaults to look through all records in store
     * @param {String} field Field to find min value for
     * @param {Core.data.Model[]} records Records to process, uses all records if unspecified
     * @returns {Number}
     */
    min(field, records = this.storage.values) {
        if (!records || !records.length) return 0;

        return records.reduce((min, record) => {
            if (record[field] < min) min = record[field];
            return min;
        }, records[0][field]);
    }

    /**
     * Returns max value for the specified field. Defaults to look through all records in store
     * @param {String} field Field to find max value for
     * @param {Core.data.Model[]} records Records to process, uses all records if unspecified
     * @returns {Number}
     */
    max(field, records = this.storage.values) {
        if (!records || !records.length) return 0;

        return records.reduce((max, record) => {
            if (record[field] > max) max = record[field];
            return max;
        }, records[0][field]);
    }

    /**
     * Returns the average value for the specified field. Defaults to look through all records in store
     * @param {String} field Field to calculate average value for
     * @param {Core.data.Model[]} records Records to process, uses all records if unspecified
     * @returns {Number}
     */
    average(field, records = this.storage.values) {
        if (!records || !records.length) return 0;

        let count = 0,
            sum = records.reduce((sum, record) => {
                if (record.meta.specialRow) return sum;
                const v = parseFloat(record[field]);

                if (!isNaN(v)) {
                    count++;
                    return sum + v;
                }
                else {
                    return sum;
                }
            }, 0);

        return sum / count;
    }

    /**
     * Returns sum by adding value of specified field for records in the group with the specified groupValue.
     * @param groupValue Group to summarize
     * @param {String} field Field to summarize by
     * @returns {Number} Sum or null if store not grouped
     */
    groupSum(groupValue, field) {
        return this.sum(field, this.getGroupRecords(groupValue));
    }
};
