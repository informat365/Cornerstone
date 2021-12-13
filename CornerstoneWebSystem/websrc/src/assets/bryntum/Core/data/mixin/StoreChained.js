import Base from '../../Base.js';
// TODO: turn into plugin instead?

/**
 * @module Core/data/mixin/StoreChained
 */
const returnTrue = () => true;

/**
 * A chained Store contains a subset of records from a master store. Which records to include is determined by a
 * filtering function, {@link #config-chainedFilterFn}.
 *
 * @example
 * masterStore.makeChained(record => record.percent < 10);
 *
 * // or
 *
 * new Store({
 *   masterStore     : masterStore,
 *   chainedFilterFn : record => record.percent < 10
 * });
 *
 * @mixin
 */
export default Target => class StoreChained extends (Target || Base) {
    //region Config

    static get defaultConfig() {
        return {
            /**
             * Function used to filter records in the masterStore into a chained store
             * @config {Function}
             * @default () => true
             * @category Chained store
             */
            chainedFilterFn : null,

            /**
             * Array of fields that should trigger filtering of chained store when the fields are updated.
             * @config {String[]}
             * @category Chained store
             */
            chainedFields : null,

            /**
             * Master store that a chained store gets its records from.
             * @config {Store}
             * @category Chained store
             */
            masterStore : null,

            /**
             * Method names calls to which should be relayed to master store.
             * @config {String[]}
             * @category Chained store
             */
            doRelayToMaster : ['add', 'remove', 'insert', 'removeAll'],

            /**
             * Method names calls to which shouldn't be relayed to master store.
             * @config {String}
             * @category Chained store
             */
            dontRelayToMaster : [],

            /**
             * Flag showing whether to keep added/removed uncommitted records when filling the store from master.
             * @config {Boolean}
             * @category Chained store
             */
            keepUncommittedChanges : false
        };
    }

    //endregion

    construct(config) {
        const me = this;

        super.construct(config);

        if (me.masterStore) {
            me.methodNamesToRelay.forEach(fnName => me[fnName] = (...params) => me.relayToMaster(fnName, params));

            // TODO: prevent other functions?

            me.masterStore.on({
                change  : me.onMasterDataChanged,
                prio    : 1,
                thisObj : me
            });

            if (!me.masterStore.chainedStores) {
                me.masterStore.chainedStores = [];
            }
            me.masterStore.chainedStores.push(me);

            me.fillFromMaster();
        }
    }

    //region Properties

    /**
     * Is this a chained store?
     * @property {Boolean}
     * @readonly
     * @category Store
     */
    get isChained() {
        return Boolean(this.masterStore);
    }

    set chainedFilterFn(chainedFilterFn) {
        this._chainedFilterFn = this.thisObj ? chainedFilterFn.bind(this.thisObj) : chainedFilterFn;
    }

    get chainedFilterFn() {
        return this._chainedFilterFn || returnTrue;
    }

    get methodNamesToRelay() {
        const doIsArray = Array.isArray(this.doRelayToMaster),
            dontIsArray = Array.isArray(this.dontRelayToMaster);

        return doIsArray && this.doRelayToMaster.filter(name => !dontIsArray || !this.dontRelayToMaster.includes(name)) || [];
    }

    //endregion

    //region Internal

    updateChainedStores() {
        if (this.chainedStores) {
            this.chainedStores.forEach(store => store.fillFromMaster());
        }
    }

    /**
     * Updates records available in a chained store by filtering the master store records using
     * {@link #config-chainedFilterFn}
     * @internal
     */
    fillFromMaster() {
        const
            me = this,
            { masterStore } = me;

        if (!me.isChained) {
            throw new Error('fillFromMaster only allowed on chained store');
        }

        if (me.keepUncommittedChanges) {
            if (me.isTree) {
                throw new Error('Cannot use keepUncommittedChanges on a chained tree store');
            }
            me.data = [].concat(
                me.added.values.filter(r => !me.removed.includes(r)),
                masterStore.allRecords.filter(r => !me.removed.includes(r) && !me.added.includes(r) && me.chainedFilterFn(r))
            );
        }
        else {
            if (masterStore.isTree) {
                // All nodes will be registered
                me.idRegister = {};
                me.internalIdRegister = {};

                // *all* owned records have to join, as they would have done if they'd all gone through
                // the appendChild route for this store.
                masterStore.allRecords.filter(me.chainedFilterFn).forEach(r => {
                    if (r.stores.includes(me)) {
                        me.register(r);
                    }
                    else {
                        r.joinStore(me);
                    }
                });

                // But the flat storage must reflect the initial expanded status.
                me.data = me.doIncludeExclude(me.getChildren(me.masterStore.rootNode), true);
            }
            else {
                me.data = masterStore.allRecords.filter(me.chainedFilterFn);
            }
        }
    }

    /**
     * Commits changes back to master.
     * - the records deleted from chained store and present in master will be deleted from master
     * - the records added to chained store and missing in master will added to master
     * Internally calls {Store#function-commit commit()}.
     * @returns {Object} Changes, see Store#changes
     * @internal
     */
    commitToMaster() {
        const
            me = this,
            master = me.masterStore;

        if (!me.isChained) {
            throw new Error('commitToMaster only allowed on chained store');
        }

        master.beginBatch();
        master.remove(me.removed.values);
        master.add(me.added.values);
        master.endBatch();

        return me.commit();
    }

    /**
     * Relays some function calls to the master store
     * @private
     */
    relayToMaster(fnName, params) {
        if (fnName === 'remove' && params.length === 4 && params[3] === true) return;
        return this.masterStore[fnName](...params);
    }

    /**
     * Handles changes in master stores data. Updates the chained store accordingly
     * @private
     */
    onMasterDataChanged({ action, changes }) {
        const me = this;

        if (action === 'update') {
            // if a field not defined in chainedFields is changed, ignore the change.
            // there is no need to refilter the store in such cases, the change will be available anyhow since data is
            // shared
            const refilter = me.chainedFields && me.chainedFields.some(field => field in changes);

            if (!refilter) return;
        }

        me.fillFromMaster();
    }

    //endregion
};
