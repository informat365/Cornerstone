import Base from '../../Base.js';
import ArrayHelper from '../../helper/ArrayHelper.js';
import Model from '../Model.js';

/**
 * @module Core/data/mixin/StoreRelation
 */

/**
 * Mixin for Store that handles relations with other stores (actually defined on model).
 *
 * @private
 *
 * @mixin
 */
export default Target => class StoreRelation extends (Target || Base) {
    //region Init

    /**
     * Initialized relations, called from constructor
     * @private
     */
    initRelations(reset) {
        const
            me        = this,
            relations = me.modelClass.relations;

        if (reset && me.modelRelations) {
            // reset will reinit all relations, stop listening for store events on existing ones
            me.modelRelations.forEach(relation => {
                if (relation.storeDetacher) relation.storeDetacher();
            });
        }

        if ((!me.modelRelations || me.modelRelations.length === 0 || reset) && relations) {
            me.modelRelations = [];

            // foreignKeys is filled when model exposes its properties
            relations && relations.forEach(modelRelationConfig => {
                const
                    config       = Object.assign({}, modelRelationConfig),
                    relatedStore = typeof config.store === 'string' ? me[config.store] : config.store;

                config.dependentStore = me;

                me.modelRelations.push(config);

                if (relatedStore) {
                    config.storeProperty = config.store;
                    config.store = relatedStore; // repeated from initRelationStores, needed if stored is assigned late

                    const dependentStoreConfigs = relatedStore.dependentStoreConfigs;

                    // Add link to dependent store
                    if (dependentStoreConfigs.has(me)) {
                        dependentStoreConfigs.get(me).push(config);
                    }
                    else {
                        dependentStoreConfigs.set(me, [config]);
                    }

                    // if foreign key specifies collectionName the related store should also be configured
                    if (config.collectionName) {
                        relatedStore.initRelationCollection(config, me);
                    }

                    if (relatedStore.count > 0) {
                        relatedStore.updateDependentStores('dataset', relatedStore.records);
                    }
                }
            });
        }
    }

    /**
     * Called from other end of an relation when this store should hold a collection of related records.
     * @private
     * @param config
     * @param collectionStore
     */
    initRelationCollection(config, collectionStore) {
        // TODO: parts of this should move to model?
        const
            me   = this,
            name = config.collectionName;

        if (!me.collectionStores) {
            me.collectionStores = {};
        }

        me.collectionStores[name] = {
            store  : collectionStore,
            config : config
        };

        if (!me[name + 'Store']) {
            me[name + 'Store'] = collectionStore;
        }

        if (me.count > 0) {
            me.initModelRelationCollection(name, me.records);
        }
    }

    initModelRelationCollection(name, records) {
        const me = this;
        // add collection getter to each model
        records.forEach(record => {
            // Needs to work in trees also, if not a tree traverse just calls fn on self
            record.traverse(node => {
                !(name in node) && Object.defineProperty(node, name, {
                    enumerable : true,
                    get        : function() {
                        return me.getCollection(this, name);
                    },
                    set : function(value) {
                        return me.setCollection(this, name, value);
                    }
                });
            });
        });
    }

    //TODO: Do diff update, this is called on filtering and will be heavy with lots of records

    /**
     * Updates relationCache for all records.
     * @private
     */
    resetRelationCache() {
        this.relationCache = {};
        this.forEach(record => record.initRelations());
    }

    /**
     * Caches related records from related store on the local store.
     * @private
     * @param record Local record
     * @param relations Relations to related store
     */
    updateRecordRelationCache(record, relations) {
        const me = this;

        relations && relations.forEach(relation => {
            // use related records id, or if called before "binding" is complete use foreign key
            const foreignId = relation.related ? relation.related.id : record.get(relation.config.fieldName);
            // cache on that id, removing previously cached value if any
            foreignId !== undefined && me.cacheRelatedRecord(record, foreignId, relation.config.relationName, foreignId);
        });
    }

    //endregion

    //region Getters

    /**
     * Returns records from a collection of related records. Not to be called directly, called from Model getter.
     * TODO: Move to Model?
     * @private
     * @param model
     * @param name
     * @returns {*}
     */
    getCollection(model, name) {
        const { config, store } = this.collectionStores[name];

        return (store.relationCache[config.relationName] && store.relationCache[config.relationName][model.id]) || [];
    }

    /**
     * Sets a collection of related records. Will updated the related store and trigger events from it. Not to be called
     * directly, called from Model setter.
     * @private
     */
    setCollection(model, name, records) {
        const { config, store } = this.collectionStores[name];

        if (!store.relationCache[config.relationName]) store.relationCache[config.relationName] = {};

        const old = (store.relationCache[config.relationName][model.id] || []).slice(),
            added = [],
            removed = [];

        store.suspendEvents();

        // Remove any related records not in the new collection
        old.forEach(record => {
            if (!records.includes(record)) {
                record[config.fieldName] = null;
                store.remove(record);
                removed.push(record);
            }
        });

        // Add records from the new collection not already in store
        records.forEach(record => {
            if (record instanceof Model) {
                if (!record.stores.includes(store)) {
                    store.add(record);
                    added.push(record);
                }
            }
            else {
                [record] = store.add(record);
                added.push(record);
            }

            // Init relation
            record[config.fieldName] = model.id;
        });

        store.resumeEvents();

        if (removed.length) {
            store.trigger('remove', { records : removed });
            store.trigger('change', { action : 'remove', records : removed });
        }

        if (added.length) {
            store.trigger('add', { records : added });
            store.trigger('change', { action : 'add', records : added });
        }
    }

    //endregion

    //region Caching

    /**
     * Adds a record to relation cache, optionally removing it if already there.
     * @private
     * @param record
     * @param id
     * @param name
     * @param uncacheId
     */
    cacheRelatedRecord(record, id, name, uncacheId = null) {
        const me    = this,
            cache = me.relationCache[name] || (me.relationCache[name] = {});

        if (uncacheId !== null) {
            me.uncacheRelatedRecord(record, name, uncacheId);
        }

        if (id != null) {
            // Only include of not already in relation cache, which might happen when removing and re-adding the same instance
            ArrayHelper.include(cache[id] || (cache[id] = []), record);
        }
    }

    /**
     * Removes a record from relation cache, for a specific relation (specifiy relation name and id) or for all relations
     * @private
     * @param record Record to remove from cache
     * @param name Optional, relation name
     * @param id Optional, id
     */
    uncacheRelatedRecord(record, name = null, id = null) {
        const me       = this;

        function remove(relationName, relatedId) {
            const cache    = me.relationCache[relationName],
                oldCache = cache && cache[relatedId];

            // When unjoining a record from a filtered store the relationCache will also be filtered
            // and might give us nothing, in which case we have nothing to clean up and bail out
            if (oldCache) {
                const uncacheIndex = oldCache.indexOf(record);
                uncacheIndex >= 0 && oldCache.splice(uncacheIndex, 1);

                if (oldCache.length === 0) {
                    delete cache[relatedId];
                }
            }
        }

        if (id != null) {
            remove(name, id);
        }
        else {
            if (record.meta.relationCache) {
                Object.entries(record.meta.relationCache).forEach(([relationName, relatedRecord]) => {
                    const relatedId     = relatedRecord && relatedRecord.id;

                    remove(relationName, relatedId);
                });
            }
        }
    }

    /**
     * Updates related stores when store is cleared, a record is removed or added.
     * @private
     * @param {String} action
     * @param {Core.data.Model[]} records
     */
    updateDependentStores(action, records) {
        this.dependentStoreConfigs.forEach(configs => {
            configs.forEach(config => {
                const
                    dependentStore = config.dependentStore,
                    cache = dependentStore.relationCache[config.relationName];

                if (action === 'dataset') {
                    config.collectionName && this.initModelRelationCollection(config.collectionName, records);

                    dependentStore.forEach(record => {
                        const foreign = record.initRelation(config);
                        foreign && dependentStore.cacheRelatedRecord(record, foreign.id, config.relationName, foreign.id);
                    });

                    return;
                }

                if (action === 'removeall') {
                    dependentStore.forEach(record => {
                        record.removeRelation(config);
                    });

                    delete dependentStore.relationCache[config.relationName];

                    return;
                }

                if (action === 'add') {
                    config.collectionName && this.initModelRelationCollection(config.collectionName, records);
                }

                if (action === 'add' || action === 'remove') {
                    records.forEach(record => {
                        const dependentRecords = cache && cache[record.id];

                        switch (action) {
                            case 'remove':
                                // removing related record removes from cache on model and store
                                if (dependentRecords) {
                                    dependentRecords.forEach(dependentRecord => dependentRecord.removeRelation(config));
                                    // Altered to not delete on self, simplifies taking actions on related records after remove if relation still lives
                                    //delete cache[relatedRecord.id];
                                }
                                // TODO: Should removing related set foreign key to null? (removing Team sets Player.teamId to null)
                                break;
                            case 'add':
                                // adding a new record in related store checks if any foreign keys match the new id,
                                // and if so it sets up the relation
                                dependentStore.forEach(dependentRecord => {
                                    if (dependentRecord.get(config.fieldName) == record.id) {
                                        dependentRecord.initRelation(config);
                                        dependentStore.cacheRelatedRecord(dependentRecord, record.id, config.relationName);
                                    }
                                });
                                break;
                        }
                    });
                }
            });
        });
    }

    /**
     * Updates relation cache and foreign key value when a related objects id is changed.
     * @private
     */
    updateDependentRecordIds(oldValue, value) {
        this.dependentStoreConfigs && this.dependentStoreConfigs.forEach(configs => {
            configs.forEach(config => {
                const
                    dependentStore = config.dependentStore,
                    cache        = dependentStore.relationCache[config.relationName],
                    localRecords = cache && cache[oldValue] && cache[oldValue].slice();

                localRecords && localRecords.forEach(localRecord => {
                    localRecord.set(config.fieldName, value, false, true);
                    dependentStore.cacheRelatedRecord(localRecord, value, config.relationName, oldValue);
                });
            });
        });
    }

    //endregion
};
