import Base from '../../Base.js';
import ObjectHelper from '../../helper/ObjectHelper.js';
import StringHelper from '../../helper/StringHelper.js';

/**
 * @module Core/data/mixin/StoreGroup
 */

const resortActions = {
    add     : 1,
    replace : 1
};

/**
 * Mixin for Store that handles grouping.
 *
 * @example
 * store.group('city');
 *
 * @mixin
 */
export default Target => class StoreGroup extends (Target || Base) {
    //region Config

    static get defaultConfig() {
        return {
            /**
             * Initial groupers, specify to have store grouped automatically after initially setting data
             * @config {Object[]}
             * @category Common
             */
            groupers : null
        };
    }

    //endregion

    //region Events

    /**
     * Fired when grouping is stopped
     * @event groupingOff
     * @param {Core.data.Store} source This Store
     */

    //endregion

    //region Init

    construct(config) {
        super.construct(config);

        // For handling record mutation, *not* add/remove of records.
        // Sorts dataset if necessary.
        this.on('change', this.onDataChanged);
    }

    /**
     * Currently used groupers
     * @property {Object[]}
     * @category Sort, group & filter
     */
    get groupers() {
        return this._groupers;
    }

    set groupers(groupers) {
        const me = this;

        if (groupers && groupers.length) {
            me._groupers = groupers;
        }
        else if (me.groupers) {
            delete me._groupers;

            me.groupRecords.forEach(r => {
                if (r.meta.collapsed) {
                    me.includeGroupRecords(r);
                }
            });
            me.storage.replaceValues(me.removeHeadersAndFooters(me.registeredRecords), true);
            me.group(null, null, null, false);
            if ('groupRecords' in me) {
                delete me.groupRecords;
            }
        }
    }

    storeCollapsedGroups() {
        const me = this;

        me.collapsedGroupsHash = {};

        // Remember which groups are collapsed
        if (me.groupRecords && me.groupRecords.length) {
            me.groupRecords.forEach(rec => {
                if (me.expand(rec)) {
                    me.collapsedGroupsHash[rec.id] = true;
                }
            });

            return true;
        }

        return false;
    }

    restoreCollapsedGroups() {
        const me = this;

        Object.keys(me.collapsedGroupsHash).forEach(id => me.collapse(me.getById(id)));
    }

    onDataChange({ source : storage, action, removed }) {
        const
            me           = this,
            { groupers } = me;

        // Only do grouping transformations if we have groupers to apply.
        // In stores which never use grouping, this code is superfluous and will reduce performance.
        // The else side will simply replace the ungrouped data with itself.
        if (groupers) {
            const records = storage.values;

            // When records are added or removed, re-evaluate the group records
            // so that when the events are fired by the super call, the group
            // records are in place.
            if (groupers.length) {
                if (action === 'splice' && removed && removed.length) {
                    // Remember which groups are collapsed
                    if (me.storeCollapsedGroups()) {
                        me.storage.replaceValues(me.removeHeadersAndFooters(records), true);
                    }

                    me.storage.replaceValues(me.prepareGroupRecords(records), true);

                    // Re-collapse the groups
                    me.restoreCollapsedGroups();
                }
            }
            // Remove all group headers and footers
            else {
                storage.replaceValues(me.removeHeadersAndFooters(records), true);
            }
        }

        super.onDataChange && super.onDataChange(...arguments);
    }

    // private function that collapses on the data level
    // TODO: make public and trigger events for grid to react to?
    collapse(groupRecord) {
        if (groupRecord && !groupRecord.meta.collapsed) {
            this.excludeGroupRecords(groupRecord);
            groupRecord.meta.collapsed = true;
            return true;
        }
        return false;
    }

    // private function that expands on the data level
    // TODO: make public and trigger events for grid to react to?
    expand(groupRecord) {
        if (groupRecord && groupRecord.meta.collapsed) {
            this.includeGroupRecords(groupRecord);
            groupRecord.meta.collapsed = false;
            return true;
        }
        return false;
    }

    removeHeadersAndFooters(records) {
        const me = this;

        return records.filter(r => {
            if (r.meta.specialRow) {
                me.unregister(r);
                return false;
            }
            else {
                return true;
            }
        }, true);
    }

    prepareGroupRecords(records = this.registeredRecords) {
        const me = this;

        records = me.removeHeadersAndFooters(records);

        if (!me.isGrouped) {
            return records;
        }

        const groupedRecords = [],
            field          = me.groupers[0].field,
            groupRecords   = [];

        let curGroup       = null,
            curGroupRecord = null,
            childCount     = 0;

        function addFooter() {
            const
                val    = curGroupRecord.meta.groupRowFor,
                id     = `group-footer-${typeof val === 'number' ? val : StringHelper.createId(val)}`,
                footer = me.getById(id) || new me.modelClass({ id }, me, {
                    specialRow     : true,
                    groupFooterFor : val,
                    groupRecord    : curGroupRecord
                });

            me.register(footer);
            footer.groupChildren = curGroupRecord.groupChildren;
            groupRecords.push(footer);
            groupedRecords.push(footer);
            me.allRecords.push(footer);
            curGroupRecord.groupChildren.push(footer);
            childCount++;
            return footer;
        }

        records.forEach((record) => {
            const val = record[field],
                id  = `group-header-${typeof val === 'number' ? val : StringHelper.createId(val)}`;

            // A group header or footer record of an empty group.
            // Remove from the data
            if (record.groupChildren && !record.groupChildren.length) {
                me.unregister(record);
                return;
            }

            if (!ObjectHelper.isEqual(val, curGroup)) {
                if (curGroupRecord) {
                    // also add group footer? used by GroupSummary feature
                    if (me.useGroupFooters) {
                        addFooter(curGroupRecord);
                    }

                    curGroupRecord.meta.childCount = childCount;
                }

                curGroupRecord = me.getById(id) || new me.modelClass({ id }, me, {
                    specialRow  : true,
                    groupRowFor : val,
                    groupField  : field
                });

                me.register(curGroupRecord);
                curGroupRecord.groupChildren = [];
                groupedRecords.push(curGroupRecord);
                me.allRecords.push(curGroupRecord);
                groupRecords.push(curGroupRecord);
                curGroup = val;
                childCount = 0;
            }

            record.instanceMeta(me.id).groupParent = curGroupRecord;
            groupedRecords.push(record);
            curGroupRecord.groupChildren.push(record);
            childCount++;
        });

        // misses for last group without this
        if (curGroupRecord) {
            curGroupRecord.meta.childCount = childCount;

            // footer for last group
            if (me.useGroupFooters) {
                addFooter();
            }
        }

        me.groupRecords = groupRecords;

        return groupedRecords;
    }

    //endregion

    //region Group and ungroup

    /**
     * Is store currently grouped?
     * @property {Boolean}
     * @readonly
     * @category Sort, group & filter
     */
    get isGrouped() {
        return Boolean(this.groupers && this.groupers.length);
    }

    /**
     * Group records.
     * @param {String} field Field to group by
     * @param {Boolean} ascending Group direction
     * @param {Boolean} add Add grouper (true) or use only this grouper (false)
     * @param {Boolean} performSort Trigger sort directly, which does the actual grouping
     * @param {Boolean} silent True to not fire events
     * @fires group
     * @fires refresh
     * @category Sort, group & filter
     */
    group(field, ascending, add = false, performSort = true, silent = false) {
        const me = this;

        let newGrouper;

        if (add) {
            me.groupers.push(newGrouper = {
                field          : field,
                ascending      : ascending,
                complexMapping : field.includes('.')
            });
        }
        else if (field) {
            if (ascending == undefined) {
                ascending = me.groupInfo && me.groupInfo.field === field ? !me.groupInfo.ascending : true;
            }

            me.groupInfo = newGrouper = {
                field          : field,
                ascending      : ascending,
                complexMapping : field.includes('.')
            };

            me.groupers = [me.groupInfo];
        }

        if (newGrouper) {
            const { prototype } = me.modelClass;

            // Create a getter for complex field names like "get resource.city"
            if (newGrouper.complexMapping && !prototype.hasOwnProperty(field)) {
                Object.defineProperty(prototype, field, {
                    get() {
                        return ObjectHelper.getPath(this, field);
                    }
                });
            }
        }

        // as far as the store is concerned, grouping is just more sorting. so trigger sort
        if (performSort !== false) {
            me.sort(null, null, false, true);
        }

        if (!silent) {
            /**
             * Fired when grouping changes
             * @event group
             * @param {Core.data.Store} source This Store
             * @param {Object[]} groupers Applied groupers
             * @param {Core.data.Model[]} records Grouped records
             */
            me.trigger('group', { isGrouped : me.isGrouped, groupers : me.groupers, records : me.storage.values });
            me.trigger('refresh', { action : 'group', isGrouped : me.isGrouped, groupers : me.groupers, records : me.storage.values });
        }
    }

    // Internal since UI does not support multi grouping yet
    /**
     * Add a grouping level (a grouper).
     * @param {String} field Field to group by
     * @param {Boolean} ascending Group direction
     * @category Sort, group & filter
     * @internal
     */
    addGrouper(field, ascending = true) {
        this.group(field, ascending, true);
    }

    // Internal since UI does not support multi grouping yet
    /**
     * Removes a grouping level (a grouper)
     * @param {String} field Grouper to remove
     * @category Sort, group & filter
     * @internal
     */
    removeGrouper(field) {
        const me    = this,
            index = me.groupers.findIndex(grouper => grouper.field === field);
        if (index > -1) {
            me.groupers.splice(index, 1);

            me.group();
        }
    }

    /**
     * Removes all groupers, turning store grouping off.
     * @fires groupingOff
     * @category Sort, group & filter
     */
    clearGroupers() {
        this.groupers = null;
    }

    //endregion

    //region Get and check

    /**
     * Check if a record belongs to a certain group (only for the first grouping level)
     * @param {Core.data.Model} record Record
     * @param groupValue Groups value
     * @returns {Boolean} True if the record belongs to the group, otherwise false
     * @category Sort, group & filter
     */
    isRecordInGroup(record, groupValue) {
        if (!this.isGrouped) return null;

        const me         = this,
            groupField = me.groupers[0] && me.groupers[0].field;

        return record[groupField] === groupValue && !record.meta.specialRow;
    }

    isInCollapsedGroup(record) {
        const parentGroupRec = record.instanceMeta(this).groupParent;

        return parentGroupRec && parentGroupRec.meta.collapsed;
    }

    /**
     * Returns all records in the group with specified groupValue.
     * @param groupValue
     * @returns {Core.data.Model[]} Records in specified group or null if store not grouped
     * @category Sort, group & filter
     */
    getGroupRecords(groupValue) {
        const me = this;

        if (!me.isGrouped) return null;

        return me.storage.values.filter(record => me.isRecordInGroup(record, groupValue));
    }

    /**
     * Get all group titles.
     * @returns {String[]} Group titles
     * @category Sort, group & filter
     */
    getGroupTitles() {
        const me = this;

        if (!me.isGrouped) return null;

        return me.getDistinctValues(me.groupers[0] && me.groupers[0].field);
    }

    //endregion

    onDataChanged(event) {
        if (
            this.isGrouped && (
                // If an action flagged as requiring resort is performed...
                (!event.changes && resortActions[event.action]) ||
                // ...or if the group field has changes...
                (event.changes && this.groupers.some(grouper => grouper.field in event.changes))
            )
        ) {
            // ...then resort
            this.sort();
        }
    }

    /**
     * Adds or removes records in a group from storage. Used when expanding/collapsing groups.
     * @private
     * @param {Core.data.Model} groupRecord Group which records should be added or removed
     * @param {Boolean} include Include (true) or exclude (false) records
     * @category Grouping
     */
    internalIncludeExcludeGroupRecords(groupRecord, include) {
        const me    = this,
            index = me.indexOf(groupRecord),
            mapId = me.id;

        // Skip if group record is not found, otherwise it removes records from wrong position
        if (index === -1) {
            return;
        }

        // Prevent removing from already collapsed and vice versa
        if ((groupRecord.meta.collapsed && !include) || (!groupRecord.meta.collapsed && include)) {
            return;
        }

        groupRecord.groupChildren.forEach(child =>
            child.instanceMeta(mapId).hiddenByCollapse = !include
        );

        if (include) {
            // Avoid adding record duplicates which may already have been reinserted by clearing filters
            const groupChildren = groupRecord.groupChildren.filter(r => !me.isAvailable(r));
            me.storage.values.splice(index + 1, 0, ...groupChildren);
        }
        else {
            me.storage.values.splice(index + 1, groupRecord.groupChildren.length);
        }
        me.storage._indicesInvalid = true;
        me._idMap = null;
    }

    /**
     * Removes records in a group from storage. Used when collapsing a group.
     * @private
     * @param groupRecord Group which records should be removed
     * @category Grouping
     */
    excludeGroupRecords(groupRecord) {
        this.internalIncludeExcludeGroupRecords(groupRecord, false);
    }

    /**
     * Adds records in a group to storage. Used when expanding a group.
     * @private
     * @param groupRecord Group which records should be added
     * @category Grouping
     */
    includeGroupRecords(groupRecord) {
        this.internalIncludeExcludeGroupRecords(groupRecord, true);
    }

    /**
     * Collects all group headers + children, whether expanded or not
     * @private
     * @returns {Core.data.Model[]}
     */
    collectGroupRecords() {
        return this.records.reduce((records, record) => {
            if (record.meta.specialRow) {
                records.push(record);

                if ('groupRowFor' in record.meta) {
                    records.push.apply(records, record.groupChildren);
                }
            }

            return records;
        }, []);
    }
};
