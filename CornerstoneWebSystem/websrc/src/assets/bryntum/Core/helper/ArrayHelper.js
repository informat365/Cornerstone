/**
 * @module Core/helper/ArrayHelper
 */

/**
 * Helper with useful functions for handling Arrays
 * @internal
 */
export default class ArrayHelper {
    static clean(array) {
        return array.reduce((res, item) => {
            if (item !== null && item !== undefined && !(Array.isArray(item) && item.length === 0) && item !== '') res.push(item);
            return res;
        }, []);
    }

    /**
     * Remove one or more items from an array
     * @param {Array} array Array to remove from
     * @param {Object} items One or more items to remove
     * @returns {boolean} Returns true if any item was removed
     */
    static remove(array, ...items) {
        let index,
            item,
            removed = false;

        for (let i = 0; i < items.length; i++) {
            item = items[i];
            if ((index = array.indexOf(item)) !== -1) {
                array.splice(index, 1);
                removed = true;
            }
        }

        return removed;
    }

    /*
     * Calculates the insertion index of a passed object into the passed Array according
     * to the passed comparator function. Note that the passed Array *MUST* already be ordered.
     * @param {Object} item The item to calculate the insertion index for.
     * @param {Array} The array into which the item is to be inserted.
     * @param {Function} comparatorFn The comparison function. Must return -1 or 0 or 1.
     * @param {Object} comparatorFn.lhs The left object to compare.
     * @param {Object} comparatorFn.rhs The right object to compare.
     * @param {Number} index The possible correct index to try first before a binary
     * search is instigated.
     */
    static findInsertionIndex(item, array, comparatorFn = this.lexicalCompare, index) {
        const len = array.length;
        let beforeCheck, afterCheck;

        if (index < len) {
            beforeCheck = index > 0 ? comparatorFn(array[index - 1], item) : 0;
            afterCheck = index < len - 1 ? comparatorFn(item, array[index]) : 0;
            if (beforeCheck < 1 && afterCheck < 1) {
                return index;
            }
        }

        return this.binarySearch(array, item, comparatorFn);
    }

    /**
     * This method returns the index that a given item would be inserted into the
     * given (sorted) `array`. Note that the given `item` may or may not be in the
     * array. This method will return the index of where the item *should* be.
     *
     * For example:
     *
     *      var array = [ 'A', 'D', 'G', 'K', 'O', 'R', 'X' ];
     *      var index = ArrayHelper.binarySearch(array, 'E');
     *
     *      console.log('index: ' + index);
     *      // logs "index: 2"
     *
     *      array.splice(index, 0, 'E');
     *
     *      console.log('array : ' + array.join(''));
     *      // logs "array: ADEGKORX"
     *
     * @param {Object[]} array The array to search.
     * @param {Object} item The item that you want to insert into the `array`.
     * @param {Number} [begin=0] The first index in the `array` to consider.
     * @param {Number} [end=array.length] The index that marks the end of the range
     * to consider. The item at this index is *not* considered.
     * @param {Function} [compareFn] The comparison function that matches the sort
     * order of the `array`. The default `compareFn` compares items using less-than
     * and greater-than operators.
     * @return {Number} The index for the given item in the given array based on
     * the passed `compareFn`.
     */
    static binarySearch(array, item, begin = 0, end = array.length, compareFn = this.lexicalCompare) {
        const length = array.length;
        let middle, comparison;

        if (begin instanceof Function) {
            compareFn = begin;
            begin = 0;
        }
        else if (end instanceof Function) {
            compareFn = end;
            end = length;
        }

        --end;

        while (begin <= end) {
            middle = (begin + end) >> 1;
            comparison = compareFn(item, array[middle]);
            if (comparison >= 0) {
                begin = middle + 1;
            }
            else if (comparison < 0) {
                end = middle - 1;
            }
        }

        return begin;
    }

    magnitudeCompare(lhs, rhs) {
        return (lhs < rhs) ? -1 : ((lhs > rhs) ? 1 : 0);
    }

    lexicalCompare(lhs, rhs) {
        lhs = String(lhs);
        rhs = String(rhs);

        return (lhs < rhs) ? -1 : ((lhs > rhs) ? 1 : 0);
    }

    /**
     * Similar to Array.prototype.fill(), but constructs a new array with the specified item count and fills it with
     * clones of the supplied item.
     * @param {Number} count Number of entries to create
     * @param {Object|Array} itemOrArray Item or array of items to clone (uses object spread to create shallow clone)
     * @param {Function} [fn] An optional function that is called for each item added, to allow processing
     * @returns {Array} A new populated array
     */
    static fill(count, itemOrArray = {}, fn = null) {
        const
            result = [],
            items  = Array.isArray(itemOrArray) ? itemOrArray : [ itemOrArray ];

        for (let i = 0; i < count; i++) {
            for (let item of items) {
                // Using object spread here forces us to use more babel plugins and will make
                // react_typescript demo very difficult to setup
                const processedItem = Object.assign({}, item);

                if (fn) {
                    fn(processedItem, i);
                }

                result.push(processedItem);
            }
        }
        return result;
    }

    /**
     * Populates an array with the return value from `fn`.
     * @param {Number} count Number of entries to create
     * @param {Function} fn A function that is called `count` times, return value is added to array
     * @param {Number} fn.index Current index in the array
     * @returns {Array} A new populated array
     */
    static populate(count, fn) {
        const items = [];
        for (let i = 0; i < count; i++) {
            items.push(fn(i));
        }
        return items;
    }

    /**
     * Pushes `item` on to the `array` if not already included
     * @param {Array}  array Array to push to
     * @param {Object} item Item to push if not already included
     */
    static include(array, item) {
        if (!array.includes(item)) {
            array.push(item);
        }
    }

    // Wanted to create an indexer on Stores, based on this. But turns out Proxy cannot be transpiled/polyfill for IE11
    // Keeping it for future reference
    static allowNegative(array) {
        // From https://github.com/sindresorhus/negative-array
        return new Proxy(array, {
            get(target, name, receiver) {
                if (typeof name !== 'string') {
                    return Reflect.get(target, name, receiver);
                }

                const index = Number(name);

                if (Number.isNaN(index)) {
                    return Reflect.get(target, name, receiver);
                }

                return target[index < 0 ? target.length + index : index];
            },
            set(target, name, value, receiver) {
                if (typeof name !== 'string') {
                    return Reflect.set(target, name, value, receiver);
                }

                const index = Number(name);

                if (Number.isNaN(index)) {
                    return Reflect.set(target, name, value, receiver);
                }

                target[index < 0 ? target.length + index : index] = value;

                return true;
            }
        });
    }

    static delta(a, b) {
        // Nicer syntax but about 40% slower (an extra iteration)
        // const
        //     onlyInA = a.filter(item => !b.includes(item)),
        //     onlyInB = b.filter(item => !a.includes(item)),
        //     inBoth  = a.filter(item => b.includes(item));

        // Quick bailout for nonexisting target array
        if (!b) {
            return { onlyInA : a, onlyInB : [], inBoth : [] };
        }

        const
            onlyInA = [],
            onlyInB = [],
            inBoth  = [];

        for (let i = 0; i < a.length; i++) {
            const item = a[i];

            if (b.includes(item)) {
                inBoth.push(item);
            }
            else {
                onlyInA.push(item);
            }
        }

        for (let i = 0; i < b.length; i++) {
            const item = b[i];

            if (!inBoth.includes(item)) {
                onlyInB.push(item);
            }
        }

        return { onlyInA, onlyInB, inBoth };
    }
}
