/**
 * @module Core/data/Store
 */

import Base from '../Base.js';
import Pluggable from '../mixin/Pluggable.js';
import Events from '../mixin/Events.js';
import State from '../mixin/State.js';
import Model from './Model.js';
import ObjectHelper from '../helper/ObjectHelper.js';
import { base } from '../helper/MixinHelper.js';
import StoreBag from './StoreBag.js';
import Collection from '../util/Collection.js';

import StoreCRUD from './mixin/StoreCRUD.js';
import StoreFilter from './mixin/StoreFilter.js';
import StoreGroup from './mixin/StoreGroup.js';
import StoreRelation from './mixin/StoreRelation.js';
import StoreSum from './mixin/StoreSum.js';
import StoreSearch from './mixin/StoreSearch.js';
import StoreSort from './mixin/StoreSort.js';
import StoreChained from './mixin/StoreChained.js';
import StoreState from './mixin/StoreState.js';
import StoreTree from './mixin/StoreTree.js';
import StoreSync from './mixin/StoreSync.js';
import StoreStm from './stm/mixin/StoreStm.js';
import IdHelper from '../helper/IdHelper.js';
import BrowserHelper from '../helper/BrowserHelper.js';

/**
 * The Store represents a data container which holds flat data or tree structures. An item in the Store is often called a ´record´ and it is simply an instance of the
 * {@link Core.data.Model} (or any subclass thereof). Typically you load data into a store to display it in a Grid or a ComboBox. The Store is the backing data component for any component that is showing data in a list style UI.
 *
 * * {@link Grid.view.Grid}
 * * {@link Grid.view.TreeGrid}
 * * {@link Core.widget.List}
 * * {@link Core.widget.Combo}
 *
 * <h3>Data format</h3>
 * Data is store in a JSON array the Store offers an API to edit, filter, group and sort the records.
 *
 * <h3>Store with flat data</h3>
 * To create a flat store simply provide an array of objects that describe your records
 *
 * ```javascript
 * let store = new Store({
 *   data : [
 *     { id : 1, name : 'ABBA', country : 'Sweden' },
 *     { id : 2, name : 'Beatles', country : 'UK' }
 *   ]
 * });
 *
 * // retrieve record by id
 * let beatles = store.getById(2);
 * ```
 *
 * <h3>Store with tree data</h3>
 * To create a tree store use `children` property for descendant records
 *
 * ```javascript
 * let store = new Store({
 *   tree: true,
 *   data : [
 *     { id : 1, name : 'ABBA', country : 'Sweden', children: [
 *       { id: 2, name: 'Agnetha' },
 *       { id: 3, name: 'Bjorn' },
 *       { id: 4, name: 'Benny' },
 *       { id: 5, name: 'Anni-Frid' }
 *     ]},
 *   ]
 * });
 *
 * // retrieve record by id
 * let benny = store.getById(4);
 * ```
 *
 * <h3>Sharing stores</h3>
 * You cannot directly share a Store between widgets, but the data in a Store can be shared. There are two different
 * approaches depending on your needs, sharing data and chaining stores:
 *
 * <h4>Shared data</h4>
 * To create 2 widgets that share data, you can create 2 separate stores and pass records of the first store as the
 * dataset of the second store.
 *
 * ```javascript
 * let combo1 = new Combo({
 *     appendTo : document.body,
 *     store    : new Store({
 *         data : [
 *             { id : 1, name : 'ABBA', country : 'Sweden' },
 *             { id : 2, name : 'Beatles', country : 'UK' }
 *         ]
 *     }),
 *     valueField   : 'id',
 *     displayField : 'name'
 * });
 *
 * let combo2 = new Combo({
 *     appendTo : document.body,
 *     store    : new Store({
 *         data : combo1.store.records
 *     }),
 *     valueField   : 'id',
 *     displayField : 'name'
 * });
 *
 * combo1.store.first.name = 'foo';
 * combo2.store.first.name; // "foo"
 * ```
 *
 * <h4>Chained stores</h4>
 * Another more powerful option to share data between widgets is to create {@link Core.data.mixin.StoreChained chained stores}.
 * The easiest way to create a chained store is to call {@link #function-chain} function.
 *
 * ```javascript
 * let combo1 = new Combo({
 *     appendTo : document.body,
 *     store    : new Store({
 *         data : [
 *             { id : 1, name : 'ABBA', country : 'Sweden' },
 *             { id : 2, name : 'Beatles', country : 'UK' }
 *         ]
 *     }),
 *     valueField   : 'id',
 *     displayField : 'name'
 * });
 *
 * let combo2 = new Combo({
 *     appendTo : document.body,
 *     store    : combo1.store.chain(),
 *     valueField   : 'id',
 *     displayField : 'name'
 * });
 *
 * combo1.store.first.name = 'foo';
 * combo2.store.first.name; // "foo"
 * ```
 *
 * A chained store can optionally be created with a filtering function, to only contain a subset of the records from
 * the main store. In addition, the chained store will reflect record removals/additions to the master store, something
 * the shared data approach will not.
 *
 * @mixes Core/data/mixin/StoreChained
 * @mixes Core/data/mixin/StoreCRUD
 * @mixes Core/data/mixin/StoreFilter
 * @mixes Core/data/mixin/StoreGroup
 * @mixes Core/data/mixin/StoreRelation
 * @mixes Core/data/mixin/StoreSearch
 * @mixes Core/data/mixin/StoreSort
 * @mixes Core/data/mixin/StoreState
 * @mixes Core/data/mixin/StoreSum
 * @mixes Core/data/mixin/StoreTree
 * @mixes Core/mixin/Events
 * @mixes Core/data/stm/mixin/StoreStm
 *
 * @extends Core/Base
 */
export default class Store extends base(Base).mixes(
    Events,
    Pluggable,
    State,
    StoreFilter,
    StoreCRUD,
    StoreRelation,
    StoreSum,
    StoreSearch,
    StoreSort,
    StoreGroup,
    StoreChained,
    StoreState,
    StoreTree,
    StoreStm,
    StoreSync
) {
    //region Config & properties

    static get properties() {
        return {
            relationCache         : {},
            dependentStoreConfigs : new Map()
        };
    }

    static get defaultConfig() {
        return {
            /**
             * Deprecated in favour of {@link #config-id}
             * @config {String|Number}
             * @deprecated 2.0.0
             */
            storeId : null,

            /**
             * Store's unique identifier. When set the store is added to a store map accessible through Store#getStore(id)
             * @config {String|Number}
             * @category Common
             */
            id : true,

            /**
             * An array of field definitions used to create a Model (modelClass) subclass. Optional. If the Model
             * already has fields defined, these fields will extend those.
             * @config {Object[]}
             * @category Common
             */
            fields : null,

            /**
             * Automatically detect from set data if used as tree store or flat store
             * @config {Boolean}
             * @default
             * @category Tree
             */
            autoTree : true,

            /**
             * Class used to represent records
             * @config {Core.data.Model}
             * @default
             * @category Common
             * @typings { new(data: object): Model }
             */
            modelClass : Model,

            /**
             * Raw data to load initially
             * @config {Object[]}
             * @category Common
             */
            data : null,

            /**
             * `true` to act as a tree store.
             * @config {Boolean}
             * @category Tree
             */
            tree : false,

            callOnFunctions : true,

            /**
             * A {@link Core.util.Collection Collection}, or Collection config object
             * to use to contain this Store's constituent records.
             * @config {Core.util.Collection|Object}
             */
            storage : null,

            /**
             * Retools the loaded data objects instead of making shallow copies of them. This increases performance but
             * pollutes the incoming data and does not allow remapping of fields (dataSource).
             *
             * Also allows disabling certain steps in data loading, to further improve performance. Either accepts an
             * object with the params described below or `true` which equals `disableDuplicateIdCheck` and
             * `disableTypeConversion`.
             *
             * ```javascript
             * // No duplicate id checking, no type conversions
             * new Store({ useRawData : true });
             *
             * new Store({
             *   // No type conversions only
             *   useRawData : {
             *     disableTypeConversion : true
             *   }
             * });
             * ```
             *
             * @config {Boolean|Object}
             * @param {Boolean} [disableDuplicateIdCheck] Data must not contain duplicate ids, check is bypassed.
             * @param {Boolean} [disableDefaultValue] Default values will not be applied to record fields.
             * @param {Boolean} [disableTypeConversion] No type conversions will be performed on record data.
             * @category Advanced
             */
            useRawData : false,

            /**
             * Specify `false` to prevent loading records without ids, a good practise to enforce when syncing with a
             * backend. By default Store allows loading records without ids, in which case a generated id will be
             * assigned.
             * @config {Boolean}
             * @default true
             * @category Advanced
             */
            allowNoId : true,

            /**
             * Prevent dynamically subclassing the modelClass. It does so by default to not pollute it when exposing
             * properties. Should rarely need to be used.
             * @config {Boolean}
             * @default false
             * @private
             * @category Advanced
             */
            preventSubClassingModel : null
        };
    }

    //endregion

    //region Events

    /**
     * Fired when the id of a record has changed
     * @event idChange
     * @param {Core.data.Store} source This Store
     * @param {Core.data.Model} record Modified record
     * @param {String|Number} oldValue Old id
     * @param {String|Number} value New id
     */

    /**
     * Fired before record is modified in this store.
     * Modification may be vetoed by returning `false` from a handler.
     * @event beforeUpdate
     * @param {Core.data.Store} source This Store
     * @param {Core.data.Model} record Modified record
     * @param {Object} toSet Modification data
     */

    /**
     * Fired when a record is modified
     * @event update
     * @param {Core.data.Store} source This Store
     * @param {Core.data.Model} record Modified record
     * @param {Object} toSet Modification data
     */

    /**
     * Fired when the root node is set
     * @event rootChange
     * @param {Core.data.Store} source This Store
     * @param {Core.data.Model} oldRoot The old root node.
     * @param {Core.data.Model} rootNode The new root node.
     */

    /**
     * Data in the store was changed. This is a catch-all event which is fired for all changes
     * which take place to the store's data.
     *
     * This includes mutation of individual records, adding and removal of records, as well as
     * setting a new data payload using the {@link #property-data} property, sorting, filtering,
     * and calling {@link Core.data.mixin.StoreCRUD#function-removeAll}.
     *
     * Simple databound widgets may use to the `change` event to refresh their UI without having to add multiple
     * listeners to the {@link #event-update}, {@link Core.data.mixin.StoreCRUD#event-add}, {@link Core.data.mixin.StoreCRUD#event-remove}, {@link #event-refresh}
     * and {@link Core.data.mixin.StoreCRUD#event-removeAll} events.
     *
     * A more complex databound widget such as a grid may use the more granular events to perform less
     * destructive updates more appropriate to each type of change. The properties will depend upon the value of the `action` property.
     * @event change
     * @param {Core.data.Store} source This Store.
     * @param {String} action Name of action which triggered the change. May be one of:
     * * `'remove'`
     * * `'removeAll'`
     * * `'add'`
     * * `'updatemultiple'`
     * * `'clearchanges'`
     * * `'filter'`
     * * `'sort'`
     * * `'update'`
     * * `'dataset'`
     * * `'replace'`
     */

    /**
     * Data in the store has completely changed, such as by a filter, or sort or load operation.
     * @event refresh
     * @param {Core.data.Store} source This Store.
     * @param {Boolean} batch Flag set to `true` when the refresh is triggered by ending a batch
     * @param {String} action Name of action which triggered the change. May be one of:
     * * `'dataset'`
     * * `'sort'`
     * * `'filter'`
     * * `'create'`
     * * `'update'`
     * * `'delete'`
     * * `'group'`.
     */

    //endregion

    //region Init

    construct(config = {}) {
        const me = this;

        Object.assign(me, {
            added              : new StoreBag(),
            removed            : new StoreBag(),
            modified           : new StoreBag(),
            idRegister         : {},
            internalIdRegister : {}
        });

        super.construct(config);

        me.initRelations();
    }

    doDestroy() {
        const
            me = this,
            allRecords = me.registeredRecords;

        for (let i = allRecords.length - 1, rec; i >= 0; i--) {
            rec = allRecords[i];
            if (rec && !rec.isDestroyed) {
                rec.unJoinStore(me);
            }
        }

        me.storage.destroy();
        delete Store.storeMap[me.id];

        // Remove from STM if added there
        if (me.stm) {
            me.stm.removeStore(me);
        }

        // Events superclass fires destroy event.
        super.doDestroy();
        //TODO abort any ongoing loads
    }

    /**
     * Stops this store from firing events until {@link #function-endBatch} is called. Multiple calls to `beginBatch`
     * stack up, and will require an equal number of `endBatch` calls to resume events.
     *
     * Upon call of {@link #function-endBatch}, a {@link #event-refresh} event is triggered to allow UIs to
     * update themselves based upon the new state of the store.
     *
     * This is extremely useful when making a large number of changes to a store. It is important not to trigger
     * too many UI updates for performance reasons. Batching the changes ensures that UIs attached to this
     * store are only updated once at the end of the updates.
     */
    beginBatch() {
        this.suspendEvents();
    }

    /**
     * Ends event suspension started by {@link #function-beginBatch}. Multiple calls to {@link #function-beginBatch}
     * stack up, and will require an equal number of `endBatch` calls to resume events.
     *
     * Upon call of `endBatch`, a {@link #event-refresh} event with `action: batch` is triggered to allow UIs to update
     * themselves based upon the new state of the store.
     *
     * This is extremely useful when making a large number of changes to a store. It is important not to trigger
     * too many UI updates for performance reasons. Batching the changes ensures that UIs attached to this
     * store are only updated once at the end of the updates.
     */
    endBatch() {
        if (this.resumeEvents()) {
            this.trigger('refresh', {
                action  : 'batch',
                data    : this.storage.values,
                records : this.storage.values
            });
        }
    }

    set storage(storage) {
        const me = this;

        if (storage && storage.isCollection) {
            me._storage = storage;
        }
        else {
            me._storage = new Collection(storage);
        }
        me._storage.autoFilter = me.reapplyFilterOnAdd;

        // Join all the constituent records to this Store
        for (const r of me._storage) {
            r.joinStore(me);
        }
        me._storage.on({
            change  : 'onDataChange',
            thisObj : me
        });
    }

    get storage() {
        if (!this._storage) {
            this.storage = {};
        }
        return this._storage;
    }

    get allRecords() {
        const me = this;

        if (me.isTree) {
            const result = me.collectDescendants(me.rootNode).all;

            if (me.rootVisible) {
                result.unshift(me.rootNode);
            }
            return result;
        }
        else {
            return me.isGrouped
                ? me.collectGroupRecords()
                : me.storage.allValues;
        }
    }

    /**
     * Responds to mutations of the underlying storage Collection
     * @param {Object} event
     * @private
     */
    onDataChange({ source : storage, action, added, removed, replaced, oldCount, item, from, to }) {
        const
            me = this,
            addedCount = added && added.length,
            removedCount = removed && removed.length;

        let record, filtersWereReapplied;

        me._idMap = null;

        if (addedCount) {
            added.forEach(added => {
                added.joinStore(me);
            });
        }

        replaced && replaced.forEach(([oldRecord, newRecord]) => {
            oldRecord.unJoinStore(me);
            newRecord.joinStore(me);
        });

        // Allow mixins to mutate the storage before firing events.
        // StoreGroup does this to introduce group records into the mix.
        super.onDataChange(...arguments);

        // Join/unjoin incoming/outgoing records unless its as a result of TreeNode operations.
        // If we are a tree, joining is done when nodes are added/removed
        // as child nodes of a joined parent.
        if (!me.isTree) {
            if (addedCount) {
                for (record of added) {
                    record.joinStore(me);
                }

                me.added.add(added);
                me.removed.remove(added);

                // Re-evaluate the current *local* filter set silently so that the
                // information we are broadcasting below is up to date.
                filtersWereReapplied = !me.filterParamName && me.filtered && me.reapplyFilterOnAdd;
                if (filtersWereReapplied) {
                    me.filter({
                        silent : true
                    });
                }

            }
            if (removedCount) {
                for (record of removed) {
                    record.unJoinStore(me);

                    // If was newly added, remove from added list
                    if (me.added.includes(record)) {
                        me.added.remove(record);
                    }
                    // Else add to removed list
                    else {
                        me.removed.add(record);
                    }
                }
                me.modified.remove(removed);

                // Re-evaluate the current *local* filter set silently so that the
                // information we are broadcasting below is up to date.
                filtersWereReapplied = !me.filterParamName && me.filtered;
                if (filtersWereReapplied) {
                    me.filter({
                        silent : true
                    });
                }
            }
        }

        switch (action) {
            case 'clear':
                // Clear our own relationCache, since we will be empty
                me.relationCache = {};

                // Signal to stores that depend on us
                me.updateDependentStores('removeall');

                me.trigger('removeAll');
                me.trigger('change', {
                    action : 'removeall'
                });
                break;

            case 'splice':
                if (addedCount) {
                    me.updateDependentStores('add', added);

                    const
                        // Collection does not handle moves, figure out if and where a record was moved from by checking
                        // previous index value stored in meta
                        oldIndex = added.reduce((lowest, record) => {
                            const { previousIndex } = record.meta;
                            if (previousIndex > -1 && previousIndex < lowest) lowest = previousIndex;
                            return lowest;
                        }, added[0].meta.previousIndex),

                        index = storage.indexOf(added[0], !storage.autoFilter),

                        params = {
                            records : added,
                            index
                        };

                    // Only include param oldIndex when used
                    if (oldIndex > -1) {
                        params.oldIndex = oldIndex;
                    }

                    me.trigger('add', params);

                    me.trigger('change', Object.assign({ action : 'add' }, params));

                    if (filtersWereReapplied) {
                        me.triggerFilterEvent({ action : 'filter', filters : me.filters, oldCount, records : me.storage.allValues });
                    }
                }

                if (removed.length) {
                    me.updateDependentStores('remove', removed);

                    me.trigger('remove', {
                        records : removed
                    });
                    me.trigger('change', {
                        action  : 'remove',
                        records : removed
                    });
                }

                if (replaced.length) {
                    // TODO: Remove in 2.2 if no problems til then
                    // me.trigger('updateMultiple', {
                    //     records : removed,
                    //     all     : me.records.length === replaced.length
                    // });
                    me.trigger('replace', {
                        records : replaced,
                        all     : me.records.length === replaced.length
                    });
                    me.trigger('change', {
                        action : 'replace',
                        replaced,
                        all    : me.records.length === replaced.length
                    });
                }
                break;

            case 'filter':
                // Reapply grouping/sorting to make sure unfiltered records get sorted correctly
                if (me.isGrouped) {
                    me.group();
                }
                else if (me.isSorted) {
                    me.performSort();
                }
                break;

            case 'move':
                // update parentIndex of records affected
                for (let allRecords = me.storage.allValues, i = Math.min(from, to); i <= Math.max(from, to); i++) {
                    allRecords[i].parentIndex = i;
                }

                /**
                 * Fired when a record has been moved within this Store
                 * @event move
                 * @param {Core.data.Store} source This Store
                 * @param {Core.data.Model} record The record moved.
                 * @param {Number} from The index from which the record was removed.
                 * @param {Number} to The index at which the record was inserted.
                 */
                me.trigger('move', {
                    record : item,
                    from,
                    to
                });
                me.trigger('change', {
                    action,
                    record : item,
                    from,
                    to
                });
        }
    }

    /**
     * This is called from Model after mutating any fields so that Stores can take any actions necessary
     * at that point, and distribute mutation event information through events.
     * @param {Core.data.Model} record The record which has just changed
     * @param {Object} toSet A map of the field names and values that were passed to be set
     * @param {Object} wasSet A map of the fields that were set. Each property is a field name, and
     * the property value is an object containing two properties: `oldValue` and `value` eg:
     * ```javascript
     *     {
     *         name {
     *             oldValue : 'Rigel',
     *             value : 'Nigel'
     *         }
     *     }
     *
     * @param {Boolean} silent Do not trigger events
     * @param {Boolean} fromRelationUpdate Update caused by a change in related model
     * @private
     */
    onModelChange(record, toSet, wasSet, silent, fromRelationUpdate) {
        const
            me      = this,
            idField = record.constructor.idField,
            event   = {
                record,
                changes : wasSet,
                // Cannot use isBatching, since change is triggered when batching has reached 0
                // (but before it is set to null)
                batch   : record.batching != null,
                fromRelationUpdate
            };

        // Add or remove from our modified Bag
        if (record.isModified) {
            if (!me.modified.includes(record) && !me.added.includes(record)) {
                me.modified.add(record);
                if (me.autoCommit) {
                    me.commit();
                }
            }
        }
        else {
            me.modified.remove(record);
        }

        if (!silent) {
            if (idField in wasSet) {
                const { oldValue, value } = toSet[idField];

                me.updateDependentRecordIds(oldValue, value);

                me.onRecordIdChange(record, oldValue, value);

                me.trigger('idChange', {
                    store : me,
                    record,
                    oldValue,
                    value
                });
            }

            me.onUpdateRecord(record, wasSet);

            me.trigger('update', event);
            me.trigger('change', Object.assign({ action : 'update' }, event));
        }
    }

    get idMap() {
        const me               = this,
            processedRecords = me.storage.values,
            needsRebuild   = !me._idMap,
            idMap          = me._idMap || (me._idMap = {});

        if (needsRebuild) {
            for (let record, index = 0, visibleIndex = 0; index < processedRecords.length; index++) {
                record = processedRecords[index];
                idMap[record.id] = { index, visibleIndex, record };
                if (!record.meta.specialRow) {
                    visibleIndex++;
                }
            }
        }
        return idMap;
    }

    /**
     * Class used to represent records. Defaults to class Model.
     * @property {Core.data.Model}
     * @category Records
     * @typings { new(data: object): Model }
     */
    get modelClass() {
        return this._modelClass;
    }

    set modelClass(ClassDef) {
        const fields = this.fields;

        // noinspection JSRedeclarationOfBlockScope
        let ClassDefEx = null;

        // Ensure our modelClass is exchanged for an extended of modelClass decorated with any configured fields.
        if (fields && fields.length) {
            ClassDefEx = class extends ClassDef {
                static get fields() {
                    return fields;
                }
            };

            ClassDefEx.exposeProperties();
        }
        // If we expose properties on Model we will pollute all other models, use internal subclass instead
        else if (!this.preventSubClassingModel) {
            ClassDefEx = class extends ClassDef {};
            //<debug>
            // The hack below is not allowed with CSP
            if (!window.bryntum.CSP && BrowserHelper.isChrome) {
                // This workaround makes Chrome output a readable class name in DevTools when you inspect
                // (turns TaskModel -> TaskModelEx etc. instead of ClassDef for all)
                // eslint-disable-next-line no-eval
                eval(`ClassDefEx = class ${ClassDef.$name || ClassDef.name}Ex extends ClassDef {};`);
            }
            //</debug>
        }
        else {
            ClassDefEx = ClassDef;
        }

        // Need to properly expose relations on this new subclass
        ClassDefEx.exposeRelations();

        this._modelClass = ClassDefEx;
    }

    //endregion

    //region Store id & map

    // Deprecated.
    // TODO: Remove in 2.0 when all references have been removed from Scheduler and Gantt
    set storeId(storeId) {
        this.id = storeId;
    }

    get storeId() {
        return this.id;
    }

    /**
     * Get/set id, an unique identifier for the store.
     * Used to build a store map, use Store#getStore() to retrieve a store from the map.
     * @property {String|Number}
     * @category Store
     */
    set id(id) {
        const me = this;

        if (me._id) {
            delete Store.storeMap[me._id];
        }
        me._id = id === true ? IdHelper.generateId('store-') : id;
        if (id) {
            Store.storeMap[id] = me;
        }
    }

    get id() {
        return this._id;
    }

    get tree() {
        return this._tree;
    }

    set tree(tree) {
        this._tree = tree;

        if (tree && !this.rootNode) {
            this.rootNode = this.buildRootNode();
        }
    }

    // a hook to build a customized root node
    buildRootNode() {
        return {};
    }

    /**
     * Get a store from the store map by id.
     * @param {String|Number|Object[]} id The id of the store to retrieve, or an array of objects
     * from which to create the contents of a new Store.
     * @returns {Core.data.Store} The store with the specified id
     */
    static getStore(id, storeClass) {
        if (id instanceof Store) {
            return id;
        }
        if (this.storeMap[id]) {
            return this.storeMap[id];
        }
        if (Array.isArray(id)) {
            let storeModel;

            const storeData = id.map(item => {
                if (item instanceof Model) {
                    storeModel = item.constructor;
                }
                else if (typeof item === 'string') {
                    item = {
                        text : item
                    };
                }
                else {
                    //<debug>
                    if (item.constructor.name !== 'Object') {
                        throw new Error('getStore must be passed an array of Objects');
                    }
                    //</debug>
                }
                return item;
            });

            id = {
                autoCreated : true,
                data        : storeData,
                modelClass  : storeModel || class extends Model {},
                allowNoId   : true // String items have no id and are not guaranteed to be unique
            };
            if (!storeClass) {
                storeClass = Store;
            }
        }
        if (storeClass) {
            return new storeClass(id);
        }
    }

    /**
     * Get all registered stores
     * @returns {Core.data.Store[]}
     */
    static get stores() {
        return Object.values(this.storeMap);
    }

    //endregion

    //region Data

    /**
     * The invisible root node of this tree.
     * @property {Core.data.Model}
     * @readonly
     */
    get rootNode() {
        return this.masterStore ? this.masterStore.rootNode : this._rootNode;
    }

    set rootNode(rootNode) {
        const me = this,
            oldRoot = me._rootNode;

        // No change
        if (rootNode === oldRoot) {
            return;
        }

        if (oldRoot) {
            me.clear(false);
        }
        if (rootNode instanceof Model) {
            // We insist that the rootNode is expanded otherwise no children will be added
            rootNode.instanceMeta(me).collapsed = false;

            me._rootNode = rootNode;
        }
        else {
            me._rootNode = rootNode = new me.modelClass(Object.assign({
                expanded                : true,
                [me.modelClass.idField] : `${me.id}-rootNode`
            }, rootNode), me, null, true);
            rootNode.isAutoRoot = true;
        }
        me._tree = true;
        rootNode.isRoot = true;
        rootNode.joinStore(me);

        // If there are nodes to be inserted into the flat storage
        // then onNodeAddChild knows how to do that and what events
        // to fire based upon rootNode.isLoading.
        if (rootNode.children && rootNode.children.length || me.rootVisible) {
            rootNode.isLoading = true;
            me.onNodeAddChild(rootNode, rootNode.children || [], 0);
            rootNode.isLoading = false;
        }

        me.trigger('rootChange', { oldRoot, rootNode });
    }

    /**
     * Sets data in the store, called on initialization if data is in config otherwise call it yourself after
     * ajax call etc. Can also be used to get the raw original data.
     * @property {Object[]}
     * @fires refresh
     * @fires change
     * @category Records
     */
    set data(data) {
        const me = this;

        // Make sure that if the plugins have not been processed yet, we call
        // the temporary property getter which configuration injects to
        // process plugins at this point. Some plugins are required to
        // operate on incoming data.
        me._thisIsAUsedExpression(me.plugins);

        // In case someone is listening for load
        me.processConfiguredListeners();

        // Convert to being a tree store if any of the new rows have a children property
        me.tree = !me.isChained && (me.tree || Boolean(me.autoTree && data && data.some(r => r[me.modelClass.childrenField])));

        // Always load a new dataset initially
        if (!me.syncDataOnLoad || !me._data) {
            me._data = data;
            // This means load the root node
            if (me.tree) {
                const root = me.rootNode;

                // Should signal stores to clear which clears UIs
                root.clearChildren();

                root.isLoading = true;
                // Append child will detect that this is a dataset operation and trigger sort + events needed
                root.appendChild(data);

                me.updateDependentStores('dataset', [root]);

                root.isLoading = false;
            }
            else {
                me.loadData(data);
            }

            // loading the store discards all tracked changes
            me.added.clear();
            me.removed.clear();
            me.modified.clear();
        }
        // Sync dataset if configured to do so
        else {
            me.syncDataset(data);
        }
    }

    loadData(data, action = 'dataset') {
        const
            me                     = this,
            { storage, allowNoId } = me,
            idField                = me.modelClass.fieldMap.id.dataSource;

        if (me.allCount) {
            // clear without marking as removed
            me.clear(false);
        }

        me._idMap = null;

        if (data) {
            // Having any of groups collapsed at the time of data reloading using the same dataset
            // makes new data look differ comparing to the data which is already in the store.
            // So here we expand all groups and save the state to collapse them later.
            // This might be expensive, but it helps to prevent Id collision failure for now.
            me.storeCollapsedGroups();

            const isRaw = !(data[0] instanceof Model);

            if (isRaw) {
                me.modelClass.exposeProperties(data[0]);

                const
                    count   = data.length,
                    records = new Array(count);

                for (let i = 0; i < count; i++) {
                    const recordData = data[i];

                    if (!allowNoId && recordData[idField] == null) {
                        throw new Error(`Id required but not found on row ${i}`);
                    }

                    records[i] = me.createRecord(recordData, true);
                    records[i].parentIndex = i;
                }
                // Allow Collection's own filters to work on the Collection by
                // passing the isNewDataset param as true.
                // The storage Collection may have been set up with its own filters
                // while we are doing remote filtering. An example is ComboBox
                // with filterSelected: true. Records which are in the selection are
                // filtered out of visibility using a filter directly in the Combobox's
                // Store's Collection.
                storage.replaceValues(records, true, true);
            }
            else {
                storage.replaceValues(data.slice(), true, true);
            }

            me.storage.allValues.forEach(r => {
                r.joinStore(me);
            });

            // Need to update group records info (headers and footers)
            me.prepareGroupRecords();

            // Restore collapsed state
            me.restoreCollapsedGroups();

            // The three operations below, filter, store and sort, all are passed
            // the "silent" parameter meaning they do not fire their own events.
            // The 'refresh' and 'change' events after are used to update UIs.
            if (!me.filterParamName && me.isFiltered) {
                me.filter({
                    silent : true
                });
            }

            // TODO: groupers must just be promoted to be the primary sorters.
            if (me.isGrouped) {
                me.group(null, null, false, !me.sorters.length, true);
            }
            if (!me.sortParamName && me.sorters.length) {
                me.sort(null, null, false, true);
            }

            // Check for duplicate ids, unless user guarantees data validity
            if (!me.useRawData.disableDuplicateIdCheck) {
                const idMap = me.idMap;
                if (Object.keys(idMap).length < me.storage.values.length) {
                    // idMap has fewer entries than expected, a duplicate id was used. pick idMap apart to find out which
                    const collisions = [];
                    me.storage.values.forEach(r => idMap[r.id] ? delete idMap[r.id] : collisions.push(r));

                    throw new Error(`Id collision on ${collisions.map(r => r.id)}`);
                }
            }

            const event = { action, data, records : me.storage.values };

            me.updateDependentStores(event.action, event.records);

            me.trigger('refresh', event);
            me.trigger('change', event);
        }
        else {
            me._data = null;
        }
    }

    get data() {
        return this._data;
    }

    /**
     * Creates an array of records from this store from the `start` to the `end' - 1
     * @param {Number} [start] The index of the first record to return
     * @param {Number} [end] The index *after* the last record to return `(start + length)`
     * @return {Core.data.Model[]} The requested records.
     * @category Records
     */
    getRange(start, end, all = true) {
        return (all ? this.storage.allValues : this.storage.values).slice(start, end);
    }

    /**
     * Creates a model instance, used internally when data is set/added. Override this in a subclass to do your own custom
     * conversion from data to record.
     * @param data Json data
     * @param skipExpose Supply true when batch setting to not expose properties multiple times
     * @category Records
     */
    createRecord(data, skipExpose = false) {
        return new this.modelClass(data, this, null, skipExpose);
    }

    refreshData() {
        this.filter();
        this.sort();
    }

    onRecordIdChange(record, oldValue, value) {
        const me = this,
            idMap = me._idMap,
            { idRegister } = me;

        me.storage._indicesInvalid = true;

        if (idMap) {
            delete idMap[oldValue];
            idMap[value] = record;
        }
        me.added.changeId(oldValue, value);
        me.removed.changeId(oldValue, value);
        me.modified.changeId(oldValue, value);

        delete idRegister[oldValue];
        idRegister[value] = record;

        record.index = me.storage.indexOf(record);
    }

    onUpdateRecord(record, changes) {
        const { internalId } = changes,
            { internalIdRegister } = this;

        if (internalId) {
            this.storage._indicesInvalid = true;
            delete internalIdRegister[internalId.oldValue];
            internalIdRegister[internalId.value] = record;
        }

        // Reapply filters when records change?
        if (this.reapplyFilterOnUpdate && this.isFiltered) {
            this.filter();
        }
    }

    get useRawData() {
        return this._useRawData;
    }

    set useRawData(options) {
        if (options === true) {
            this._useRawData = {
                enabled                 : true,
                disableDuplicateIdCheck : true,
                disableTypeConversion   : true,
                disableDefaultValue     : false
            };
        }
        else {
            this._useRawData = options ? Object.assign(options, { enabled : true }) : { enabled : false };
        }
    }

    //endregion

    //region Count

    /**
     * Number of records in the store
     * @param countProcessed Count processed (true) or real records (false)
     * @returns {Number} Record count
     * @category Records
     */
    getCount(countProcessed = true) {
        return countProcessed ? this.count : this.originalCount;
    }

    /**
     * Record count, for data records. Not including records added for group headers etc.
     * @property {Number}
     * @readonly
     * @category Records
     */
    get originalCount() {
        return this.storage.totalCount;
    }

    /**
     * Record count, including records added for group headers etc.
     * @property {Number}
     * @readonly
     * @category Records
     */
    get count() {
        return this.storage.count;
    }

    /**
     * Returns the complete dataset size regardless of tree node collapsing or filtering
     * @property {Number}
     * @readonly
     */
    get allCount() {
        return this.isTree ? this.rootNode.descendantCount : this.storage.totalCount;
    }

    //endregion

    //region Get record(s)

    /**
     * Returns all "visible" records
     * @property {Core.data.Model[]}
     * @readonly
     * @category Records
     */
    get records() {
        return this.storage.values;
    }

    /**
     * Get the first record in the store.
     * @property {Core.data.Model}
     * @readonly
     * @category Records
     */
    get first() {
        return this.storage.values[0];
    }

    /**
     * Get the last record in the store.
     * @property {Core.data.Model}
     * @readonly
     * @category Records
     */
    get last() {
        return this.storage.values[this.storage.values.length - 1];
    }

    /**
     * Get the record at the specified index
     * @param {Number} index Index for the record
     * @returns {Core.data.Model} Record at the specified index
     * @category Records
     */
    getAt(index, all = false) {
        // all means include filtered out records
        return this.storage.getAt(index, all);
    }

    // These are called by Model#join and Model#unjoin
    // register a record as a findable member keyed by id and internalId
    register(record) {
        const
            me          = this,
            // Test for duplicate IDs on register only when a tree store.
            // loadData does it in the case of a non-tree
            existingRec = me.isTree && me.idRegister[record.id];

        if (existingRec && existingRec !== record) {
            throw new Error(`Id collision on ${record.id}`);
        }
        me.idRegister[record.id] = record;
        me.internalIdRegister[record.internalId] = record;
    }

    unregister(record) {
        delete this.idRegister[record.id];
        delete this.internalIdRegister[record.internalId];
    }

    get registeredRecords() {
        return Object.values(this.idRegister);
    }

    /**
     * Get a record by id. Find the record even if filtered out, part of collapsed group or collapsed node
     * @param {Core.data.Model|String|Number} id Id of record to return.
     * @returns {Core.data.Model} A record with the specified id
     * @category Records
     */
    getById(id) {
        // In case `id` is a record, we use its ID to try to find the record in the store,
        // because if the record is removed from the store it shouldn't be found.
        // if (id instanceof Model) {
        //     id = id.id;
        // }

        if (id && id.isModel) {
            return id;
        }

        //return this.tree ? this.idRegister[id] : this.storage.get(id);
        return this.idRegister[id];
    }

    /**
     * Checks if a record is visible, in the sense that it is not filtered out,
     * hidden in a collapsed group or in a collapsed node.
     * Deprecated in 2.2.3, please use {@link #function-isAvailable} instead
     * @param {Core.data.Model|String|Number} recordOrId Record to check
     * @returns {Boolean}
     * @deprecated 2.2.3
     */
    isVisible(recordOrId) {
        return this.isAvailable(recordOrId);
    }

    /**
     * Checks if a record is available, in the sense that it is not filtered out,
     * hidden in a collapsed group or in a collapsed node.
     * @param {Core.data.Model|String|Number} recordOrId Record to check
     * @returns {Boolean}
     */
    isAvailable(recordOrId) {
        const record = this.getById(recordOrId);

        return record && this.storage.includes(record) || false;
    }

    /**
     * Get a record by internalId.
     * @param {Number} internalId The internalId of the record to return
     * @returns {Core.data.Model} A record with the specified internalId
     * @category Records
     */
    getByInternalId(internalId) {
        return this.internalIdRegister[internalId];
    }

    /**
     * Checks if the specified record is contained in the store
     * @param {Core.data.Model|String|Number} recordOrId Record, or `id` of record
     * @returns {Boolean}
     * @category Records
     */
    includes(recordOrId) {
        if (this.isTree) {
            return this.idRegister[Model.asId(recordOrId)] != null;
        }

        return this.indexOf(recordOrId) > -1;
    }

    //endregion

    //region Get index

    /**
     * Returns the index of the specified record/id, or `-1` if not found.
     * @param {Core.data.Model|String|Number} recordOrId Record, or `id` of record to return the index of.
     * @param {Boolean} [visibleRecords] Pass `true` to find the visible index.
     * as opposed to the dataset index. This omits group header records.
     * @returns {Number} Index for the record/id, or `-1` if not found.
     * @category Records
     */
    indexOf(recordOrId, visibleRecords = false) {
        const id = Model.asId(recordOrId);

        if (id == null) {
            return -1;
        }

        // When a tree, indexOf is always in the visible records - filtering is different in trees.
        if (this.isTree) {
            return this.storage.indexOf(id);
        }

        const found = this.idMap[id];

        return found ? found[visibleRecords ? 'visibleIndex' : 'index'] : -1;
    }

    allIndexOf(recordOrId) {
        if (this.isTree) {
            const record = this.getById(recordOrId);
            let result = -1;

            // Use the tree structure to get the index in tree walk order
            if (record) {
                record.bubble(n => {
                    if (n.parent) {
                        result += n.parentIndex + 1;
                    }
                    else if (n === this.rootNode && this.rootVisible) {
                        result += 1;
                    }
                });
            }
            return result;
        }
        else {
            return this.storage.indexOf(recordOrId, true);
        }
    }

    //endregion

    //region Get values

    /**
     * Gets distinct values for the specified field.
     * @param field Field to extract values for
     * @returns {Array} Array of values
     * @category Values
     */
    getDistinctValues(field) {
        const me     = this,
            values = [],
            keys   = {};
        let value;

        me.forEach(r => {
            if (!r.meta.specialRow && !r.isRoot) {
                value = r.get(field);
                if (!keys[value]) {
                    values.push(value);
                    keys[value] = 1;
                }
            }
        });

        return values;
    }

    /**
     * Counts how many times specified value appears in the store
     * @param field Field to look in
     * @param value Value to look for
     * @returns {Number} Found count
     * @category Values
     */
    getValueCount(field, value) {
        let me    = this,
            count = 0;

        me.forEach(r => {
            if (ObjectHelper.isEqual(r.get(field), value)) count++;
        });

        return count;
    }

    //endregion

    //region JSON & console

    get json() {
        return JSON.stringify(this, null, 4);
    }

    toJSON() {
        // extract entire structure.
        // If we're a tree, then that consists of the payload of the rootNode.
        return (this.isTree ? this.rootNode.children : this).map(record => record.toJSON());
    }

    //<debug>
    showJSON() {
        window.open().document.body.innerHTML = `<pre>${JSON.stringify(this, null, 2)}</pre>`;
    }

    downloadJSON(filename) {
        const a = document.createElement('a');
        a.textContent = 'Download';
        a.download = `${filename || this.id || 'store'}.json`;
        a.href = 'data:text/csv;charset=utf-8,' + escape(this.json);
        a.style.cssText = 'position: absolute; top: 0; left: 0; z-index: 100';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
    }

    toTable() {
        console.table(this.records.map(r => r.data));
    }

    //</debug>

    //endregion

    //region Iteration & traversing

    /**
     * Iterates over all normal records in store. Omits group header and footer records
     * if this store is grouped.
     * @param {Function} fn A function that is called for each record. Returning false from that function cancels iteration
     * @param {Object} thisObj `this` reference for the function
     * @category Iteration
     */
    forEach(fn, thisObj = this) {
        const callback = (r, i) => {
            if (!r.isRoot && !r.meta.specialRow) {
                return fn.call(thisObj, r, i);
            }
        };

        if (this.isTree) {
            this.rootNode.traverseWhile(callback);
        }
        else {
            // native forEach cannot be aborted by returning false, have to loop "manually"
            const records = this.storage.values;
            for (let i = 0; i < records.length; i++) {
                if (callback(records[i], i) === false) {
                    return;
                }
            }
        }
    }

    /**
     * Equivalent to Array.map(). Creates a new array with the results of calling a provided function on every record
     * @param {Function} fn
     * @returns {Array}
     * @category Iteration
     */
    map(fn, thisObj = this) {
        return this.storage.values.map(fn, thisObj);
    }

    /**
     * Equivalent to Array.reduce(). Applies a function against an accumulator and each record (from left to right) to
     * reduce it to a single value.
     * @param {Function} fn
     * @param initialValue
     * @returns {*}
     * @category Iteration
     */
    reduce(fn, initialValue = [], thisObj = this) {
        if (thisObj !== this) {
            fn = fn.bind(thisObj);
        }

        return this.storage.values.reduce(fn, initialValue, thisObj);
    }

    /**
     * Iterator that allows you to do for (let record of store)
     * @category Iteration
     */
    [Symbol.iterator]() {
        return this.storage.values[Symbol.iterator]();
    }

    /**
     * Traverse all tree nodes
     * @param {Function} fn The function to call on visiting each node.
     * @param {Core.data.Model} [topNode=this.rootNode] The top node to start the traverse at.
     * @param {Boolean} [skipTopNode] Pass true to not call `fn` on the top node, but proceed directly to its children.
     * @category Traverse
     */
    traverse(fn, topNode = this.rootNode, skipTopNode = topNode === this.rootNode) {
        const me = this;

        if (me.isTree) {
            // Allow store.traverse(fn, true) to start from rootNode
            if (typeof topNode === 'boolean') {
                skipTopNode = topNode;
                topNode = me.rootNode;
            }
            if (me.isChained) {
                const passedFn = fn;

                fn = node => {
                    if (me.chainedFilterFn(node)) {
                        passedFn(node);
                    }
                };
            }
            topNode.traverse(fn, skipTopNode);
        }
        else {
            for (const record of me.storage) {
                record.traverse(fn);
            }
        }
    }

    /**
     * Traverse all tree nodes while the passed `fn` returns true
     * @param {Function} fn The function to call on visiting each node. Returning `false` from it stops the traverse.
     * @param {Core.data.Model} [topNode=this.rootNode] The top node to start the traverse at.
     * @param {Boolean} [skipTopNode] Pass true to not call `fn` on the top node, but proceed directly to its children.
     * @category Traverse
     */
    traverseWhile(fn, topNode = this.rootNode, skipTopNode = topNode === this.rootNode) {
        const me = this;

        if (me.isTree) {
            // Allow store.traverse(fn, true) to start from rootNode
            if (typeof topNode === 'boolean') {
                skipTopNode = topNode;
                topNode = me.rootNode;
            }
            if (me.isChained) {
                const passedFn = fn;

                fn = node => {
                    if (me.chainedFilterFn(node)) {
                        passedFn(node);
                    }
                };
            }
            topNode.traverseWhile(fn, skipTopNode);
        }
        else {
            for (const record of me.storage) {
                if (record.traverse(fn) === false) {
                    break;
                }
            }
        }
    }

    /**
     * Finds the next record.
     * @param recordOrId Current record or its id
     * @param {Boolean} wrap Wrap at start/end or stop there
     * @returns {Core.data.Model} Next record or null if current is the last one
     * @param {Boolean} [skipSpecialRows=false] True to not return specialRows like group headers
     * @category Traverse
     */
    getNext(recordOrId, wrap = false, skipSpecialRows = false) {
        let me      = this,
            records = me.storage.values,
            idx     = me.indexOf(recordOrId);

        if (idx >= records.length - 1) {
            if (wrap) {
                idx = -1;
            }
            else {
                return null;
            }
        }

        const record = records[idx + 1];

        // Skip the result if it's a specialRow and we are told to skip them
        if (skipSpecialRows && record && record.meta.specialRow) {
            return me.getNext(records[idx + 1], wrap, true);
        }

        return record;
    }

    /**
     * Finds the previous record.
     * @param recordOrId Current record or id
     * @param {Boolean} wrap Wrap at start/end or stop there
     * @returns {Core.data.Model} Previous record or null if current is the last one
     * @param {Boolean} [skipSpecialRows=false] True to not return specialRows like group headers
     * @category Traverse
     */
    getPrev(recordOrId, wrap = false, skipSpecialRows = false) {
        let me      = this,
            records = me.storage.values,
            idx     = me.indexOf(recordOrId);

        if (idx === 0) {
            if (wrap) {
                idx = records.length;
            }
            else {
                return null;
            }
        }

        const record = records[idx - 1];

        // Skip the result if it's a specialRow and we are told to skip them
        if (skipSpecialRows && record && record.meta.specialRow) {
            return me.getNext(records[idx + 1], wrap, true);
        }

        return record;
    }

    /**
     * Gets the next or the previous record. Optionally wraps from first -> last and vice versa
     * @param {String|Model} recordOrId Record or records id
     * @param {Boolean} next Next (true) or previous (false)
     * @param {Boolean} wrap Wrap at start/end or stop there
     * @param {Boolean} [skipSpecialRows=false] True to not return specialRows like group headers
     * @returns {Core.data.Model}
     * @category Traverse
     * @internal
     */
    getAdjacent(recordOrId, next = true, wrap = false, skipSpecialRows = false) {
        return next ? this.getNext(recordOrId, wrap, skipSpecialRows) : this.getPrev(recordOrId, wrap, skipSpecialRows);
    }

    /**
     * Finds the next record among leaves (in a tree structure)
     * @param recordOrId Current record or its id
     * @param {Boolean} wrap Wrap at start/end or stop there
     * @returns {Core.data.Model} Next record or null if current is the last one
     * @category Traverse
     * @internal
     */
    getNextLeaf(recordOrId, wrap = false) {
        let me      = this,
            records = me.leaves,
            record  = me.getById(recordOrId),
            idx     = records.indexOf(record);

        if (idx >= records.length - 1) {
            if (wrap) {
                idx = -1;
            }
            else {
                return null;
            }
        }

        return records[idx + 1];
    }

    /**
     * Finds the previous record among leaves (in a tree structure)
     * @param recordOrId Current record or id
     * @param {Boolean} wrap Wrap at start/end or stop there
     * @returns {Core.data.Model} Previous record or null if current is the last one
     * @category Traverse
     * @internal
     */
    getPrevLeaf(recordOrId, wrap = false) {
        let me      = this,
            records = me.leaves,
            record  = me.getById(recordOrId),
            idx     = records.indexOf(record);

        if (idx === 0) {
            if (wrap) {
                idx = records.length;
            }
            else {
                return null;
            }
        }

        return records[idx - 1];
    }

    /**
     * Gets the next or the previous record among leaves (in a tree structure). Optionally wraps from first -> last and
     * vice versa
     * @param {String/Model} recordOrId Record or records id
     * @param {Boolean} next Next (true) or previous (false)
     * @param {Boolean} wrap Wrap at start/end or stop there
     * @returns {Core.data.Model}
     * @category Traverse
     * @internal
     */
    getAdjacentLeaf(recordOrId, next = true, wrap = false) {
        return next ? this.getNextLeaf(recordOrId, wrap) : this.getPrevLeaf(recordOrId, wrap);
    }

    //endregion

    //region Chained store

    /**
     * Creates a chained store, a new Store instance that contains a subset of the records from current store.
     * Which records is determined by a filtering function, which is reapplied when data in the base store changes.
     *
     * If this store is a {@link Core.data.mixin.StoreTree#property-isTree tree} store, then the resulting
     * chained store will be a tree store sharing the same root node, but only child nodes which pass
     * the `chainedFilterFn` will be considered when iterating the tree through the methods such as
     * {@link #function-traverse} or {@link #function-forEach}.
     *
     * @param {Function} chainedFilterFn Function called for each records that determines if it should be included
     * (return true) or not (return false). Defaults to including all records (fn always returning true)
     * @param {String[]} chainedFields Array of fields that trigger filtering when they are updated
     * @param {Object} config Additional chained store configuration
     * @returns {Core.data.Store}
     * @example
     * let oldies = store.makeChained(record => record.age > 40);
     */
    makeChained(chainedFilterFn = () => true, chainedFields, config) {
        return new this.constructor(Object.assign(config || {}, {
            tree        : false,
            autoTree    : false,
            masterStore : this,
            chainedFilterFn,
            chainedFields
        }));
    }

    /**
     * Alias for {@link #function-makeChained}
     * @param {Function} chainedFilterFn Function called for each records that determines if it should be included
     * (return true) or not (return false). Defaults to including all records (fn always returning true)
     * @param {String[]} chainedFields Array of fields that trigger filtering when they are updated
     * @param {Object} config Additional chained store configuration
     * @returns {Core.data.Store}
     */
    chain() {
        return this.makeChained(...arguments);
    }

    //endregion
}

Store.storeMap = {};
