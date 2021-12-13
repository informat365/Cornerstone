import Base from '../../Base.js';
import WalkHelper from '../../helper/WalkHelper.js';

/**
 * @module Core/data/mixin/StoreSync
 */

/**
 * Mixin that allows Store to sync a new dataset with its existing records, instead of fully replacing everything.
 * Configure Store with `syncDataOnLoad: true` to activate the functionality. Sync is performed when a new dataset
 * is loaded, either by directly assigning it to `store.data` or by loading it using Ajax (if using an AjaxStore).
 *
 * ```javascript
 * const store = new Store({
 *   syncDataOnLoad : true,
 *   data           : [
 *     { id : 1, name : 'Saitama' },
 *     { id : 2, name : 'Genos' },
 *     { id : 3, name : 'Mumen Rider' }
 *   ]
 * });
 *
 * // Sync a new dataset by assigning to data:
 * store.data = [
 *   { id : 1, name : 'Caped Baldy' },
 *   { id : 4, name : 'Horse-Bone' }
 * ];
 *
 *  // Result : Record 1 updated, record 2 & 3 removed, record 4 added
 * ```
 *
 * For more details, please see {@link #config-syncDataOnLoad}.
 *
 * @mixin
 */
export default Target => class StoreSync extends (Target || Base) {

    static get defaultConfig() {
        return {
            /**
             * Configure with `true` to sync loaded data instead of replacing existing with a new dataset.
             *
             * By default (or when configured with `false`) assigning to `store.data` replaces the entire dataset
             * with a new one, creating all new records:
             *
             * ```javascript
             * store.data = [ { id : 1, name : 'Saitama' } ];
             *
             * const first = store.first;
             *
             * store.data = [ { id : 1, name : 'One-Punch man' } ];
             *
             * store.first !== first;
             * ```
             *
             * When configured with `true` the new dataset is instead synced against the old, figuring out what was
             * added, removed and updated:
             *
             * * ```javascript
             * store.data = [ { id : 1, name : 'Saitama' } ];
             *
             * const first = store.first;
             *
             * store.data = [ { id : 1, name : 'One-Punch man' } ];
             *
             * store.first === first;
             * ```
             *
             * After the sync, any configured sorters, groupers and filters will be reapplied.
             *
             * The sync operation has a configurable threshold, above which the operation will be treated as a
             * batch/refresh and only trigger a single `refresh` event. If threshold is not reached, individual events
             * will be triggered (single `add`, `remove` and possible multiple `update`). To enable the threshold,
             * supply a config object with a `threshold` property instead of `true`:
             *
             * ```
             * const store = new Store({
             *     syncDataOnLoad : {
             *         threshold : '20%'
             *     }
             * });
             * ```
             *
             * `threshold` accepts numbers or strings. A numeric threshold means number of affected records, while a
             * string is used as a percentage of the whole dataset (appending `%` is optional). By default no threshold
             * is used.
             *
             * @config {Boolean|Object}
             * @default false
             */
            syncDataOnLoad : null
        };
    }

    /**
     * Syncs a new dataset against the already loaded one, only applying changes.
     * Not intended to be called directly, please configure store with `syncDataOnLoad: true` and assign to
     * `store.data` as usual instead.
     *
     * ```
     * const store = new Store({
     *    syncDataOnLoad : true,
     *    data : [
     *        // initial data
     *    ]
     * });
     *
     * store.data = [ // new data ]; //  Difference between initial data and new data will be applied
     * ```
     *
     * @param {Object[]} data New dataset
     * @private
     */
    syncDataset(data) {
        const
            me = this,
            { storage } = me,
            { toAdd, toRemove, updated } = me.tree ? me.syncTreeDataset(data) : me.syncFlatDataset(data);

        let { threshold } = me.syncDataOnLoad,
            surpassed = false;

        // Check if threshold is surpassed
        if (threshold) {
            // Any string is treated as a percentage
            if (typeof threshold === 'string') {
                threshold = parseInt(threshold, 10) / 100 * me.count;
            }

            surpassed = toAdd.length + toRemove.length + updated.length > threshold;
        }

        if (me.tree) {
            // Flat data is spliced into/out of the collection, but in a tree it has to be added/removed from store
            // to end up on correct parents
            if (toAdd.length) {
                // Add all new nodes in one go, will be added to correct parent using `parentId`. Triggering multiple times
                const added = me.add(toAdd, surpassed);

                // parentId was tucked on in syncTreeDataset() to allow the single flat add above, clean it out
                added.forEach(node => {
                    delete node.data.parentId;
                    delete node.originalData.parentId;
                    node.meta.modified && delete node.meta.modified.parentId;
                });
            }

            // Remove in one go, removing from each parent. Triggering multiple times
            me.remove(toRemove, surpassed);
        }
        else {
            if (surpassed) {
                storage.suspendEvents();
            }

            // Add and remove, will trigger if below threshold/no threshold
            storage.splice(me.count - toRemove.length, toRemove, toAdd);

            if (surpassed) {
                storage.resumeEvents();
            }
        }

        // Trigger updates if using threshold, but have not surpassed it. If threshold is not used, the updates
        // are triggered when data is set (avoiding another iteration)
        if (threshold && !surpassed) {
            updated.forEach(({ record, toSet, wasSet }) => me.onModelChange(record, toSet, wasSet));
        }

        // Clear change-tracking
        me.acceptChanges();

        const event = { added : toAdd, removed : toRemove, updated, thresholdSurpassed : surpassed };

        // Trigger `batch` if threshold is surpassed, more similar to a batch than a full `dataset`
        if (surpassed) {
            this.trigger('refresh', {
                action   : 'batch',
                data     : data,
                records  : storage.values,
                syncInfo : event
            });
        }

        if (me.isFiltered) {
            // Announced filter
            me.filter();
        }

        if (me.isGrouped) {
            // Announced sort
            me.group();
        }
        else if (me.isSorted) {
            // Announced sort
            me.sort();
        }

        me.trigger('loadSync', event);
    }

    // Used by syncDataset()
    syncFlatDataset(data) {
        if (!data) {
            return;
        }

        const
            me          = this,
            idField     = me.modelClass.idField,
            toRemove    = [],
            toAdd       = [],
            updated     = [],
            usedIds     = {};

        let { threshold } = me.syncDataOnLoad,
            hitCount = 0;

        data.forEach(rawData => {
            const
                id     = rawData[idField],
                record = me.getById(id);

            // Record exists, might be an update
            if (record) {
                // Update silently if using threshold, otherwise trigger away
                const wasSet = record.set(rawData, null, Boolean(threshold));
                if (wasSet) {
                    updated.push({ record, wasSet, toSet : rawData });
                }

                hitCount++;
            }
            // Does not exist, add
            else {
                toAdd.push(me.createRecord(rawData));
            }

            usedIds[id] = 1;
        });

        // Check removals, unless all records were visited above
        if (hitCount < me.allCount) {
            me.forEach(record => {
                if (!usedIds[record.id]) {
                    toRemove.push(record);
                }
            });
        }

        return { toAdd, toRemove,  updated };
    }

    // Used by syncDataset()
    syncTreeDataset(data) {
        if (!data) {
            return;
        }

        const
            me          = this,
            { idField, parentIdField } = me.modelClass,
            toRemove    = [],
            toAdd       = [],
            updated     = [],
            usedIds     = {};

        let { threshold } = me.syncDataOnLoad;

        WalkHelper.preWalkWithParent({ isRoot : true, children : data }, n => n.children, (parent, rawData) => {
            if (parent) {
                const
                    id   = rawData[idField],
                    node = me.getById(id);

                // Record exists, might be an update
                if (node) {
                    // Update silently if using threshold, otherwise trigger away
                    const wasSet = node.set(rawData, null, Boolean(threshold));
                    if (wasSet) {
                        updated.push({ record : node, wasSet, toSet : rawData });
                    }
                }
                // Does not exist, add
                else {
                    rawData[parentIdField] = parent[idField];
                    toAdd.push(rawData);
                }

                usedIds[id] = 1;
            }
        });

        me.traverse(node => {
            if (!usedIds[node.id]) {
                toRemove.push(node);
            }
        });

        return { toAdd, toRemove,  updated };
    }
};
