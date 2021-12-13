import AbstractCrudManager from '../crud/AbstractCrudManager.js';
import JsonEncoder from '../crud/encoder/JsonEncoder.js';
import AjaxTransport from '../crud/transport/AjaxTransport.js';
import ResourceStore from './ResourceStore.js';
import EventStore from './EventStore.js';
import AssignmentStore from './AssignmentStore.js';
import DependencyStore from './DependencyStore.js';
import Store from '../../Core/data/Store.js';

/**
 * @module Scheduler/data/CrudManager
 */

/**
 * The Crud Manager (or "CM") is a class implementing centralized loading and saving of data in multiple stores.
 * Loading the stores and saving all changes is done using one ajax request. This class uses AJAX as a transport
 * mechanism and JSON as the data encoding format.
 *
 * ## Scheduler stores
 *
 * The class supports Scheduler specific stores (namely: resource, event and assignment stores).
 * For these stores, the CM has separate configs ({@link #config-resourceStore}, {@link #config-eventStore},
 * {@link #config-assignmentStore}) to register them. The class can also grab them from the task store (this behavior
 * can be changed using {@link #config-addRelatedStores} config).
 *
 * ```javascript
 * let crudManager = new CrudManager({
 *   autoLoad        : true,
 *   resourceStore   : resourceStore,
 *   eventStore      : eventStore,
 *   assignmentStore : assignmentStore,
 *   transport       : {
 *     load    : {
 *       url     : 'php/read.php'
 *     },
 *     sync    : {
 *       url     : 'php/save.php'
 *     }
 *   }
 * });
 * ```
 *
 * ## AJAX request configuration
 *
 * To configure AJAX request parameters please take a look at the
 * {@link Scheduler.crud.transport.AjaxTransport AjaxTransport} docs.
 *
 * ```javascript
 * const crudManager = new CrudManager({
 *     autoLoad        : true,
 *     resourceStore   : resourceStore,
 *     eventStore      : eventStore,
 *     assignmentStore : assignmentStore,
 *     transport       : {
 *         load    : {
 *             url         : 'php/read.php',
 *             // use GET request
 *             method      : 'GET',
 *             // pass request JSON in "rq" parameter
 *             paramName   : 'rq',
 *             // extra HTTP request parameters
 *             params      : {
 *                 foo     : 'bar'
 *             },
 *             requestConfig : {
 *                 fetchOptions : {
 *                     credentials: 'include'
 *                 }
 *             }
 *         },
 *         sync    : {
 *             url     : 'php/save.php'
 *         }
 *     }
 * });
 * ```
 *
 * ## Load order
 *
 * The CM is aware of the proper load order for Scheduler specific stores so you don't need to worry about it.
 * If you provide any extra stores (using {@link Scheduler.crud.AbstractCrudManager#config-stores} config) they will be
 * added to the start of collection before the Scheduler specific stores.
 * If you a different load order, you should use {@link Scheduler.crud.AbstractCrudManager#function-addStore} method to
 * register your store:
 *
 * ```javascript
 * const crudManager = new CrudManager({
 *     resourceStore   : resourceStore,
 *     eventStore      : eventStore,
 *     assignmentStore : assignmentStore,
 *     // extra user defined stores will get to the start of collection
 *     // so they will be loaded first
 *     stores          : [ store1, store2 ],
 *     transport       : {
 *         load    : {
 *             url     : 'php/read.php'
 *         },
 *         sync    : {
 *             url     : 'php/save.php'
 *         }
 *     }
 * });
 *
 * // append store3 to the end so it will be loaded last
 * crudManager.addStore(store3);
 *
 * // now when we registered all the stores let's load them
 * crudManager.load();
 * ```
 *
 * ## Features
 * The Crud Manager can automatically add Scheduler feature stores to the tracked collection.
 * For example, it tracks TimeRanges {@link Scheduler.feature.TimeRanges#config-store}.
 * You can receive a tracked store by its id:
 * ```javascript
 * let timeRangesStore = crudManager.getStore('timeRanges');
 * ```
 *
 * @mixes Scheduler/crud/encoder/JsonEncoder
 * @mixes Scheduler/crud/transport/AjaxTransport
 * @extends Scheduler/crud/AbstractCrudManager
 */

export default class CrudManager extends JsonEncoder(AjaxTransport(AbstractCrudManager)) {

    //region Config

    static get defaultConfig() {
        return {
            resourceStoreClass   : ResourceStore,
            eventStoreClass      : EventStore,
            assignmentStoreClass : AssignmentStore,
            dependencyStoreClass : DependencyStore,

            /**
             * A store with resources (or its descriptor).
             * @config {Scheduler.data.ResourceStore|Object}
             */
            resourceStore : {},

            /**
             * A store with events (or its descriptor).
             *
             * ```
             * crudManager : {
             *      eventStore {
             *          storeClass : MyEventStore
             *      }
             * }
             * ```
             * @config {Scheduler.data.EventStore|Object}
             */
            eventStore : {},

            /**
             * A store with assignments (or its descriptor).
             * @config {Scheduler.data.AssignmentStore|Object}
             */
            assignmentStore : null,

            /**
             * A store with dependencies (or its descriptor).
             * @config {Scheduler.data.DependencyStore|Object}
             */
            dependencyStore : null,

            /**
             * When set to `true` this class will try to get the {@link #config-resourceStore} and {@link #config-assignmentStore} stores from
             * the specified {@link #config-eventStore} instance.
             * @config {Boolean}
             */
            addRelatedStores : true
        };
    }

    //endregion

    construct(config = {}) {
        if (config.scheduler) {
            this.scheduler = config.scheduler;

            // The effects of this class's initialization are so wide ranging that they may need to
            // access the Scheduler's CrudManager, so ensure it's available immediately.
            this.scheduler._crudManager = this;
        }

        super.construct(config);
    }

    afterConstruct() {
        const me = this,
            scheduler = me.scheduler;

        if (scheduler) {
            // Inject the scheduler stores into the Scheduler.
            // The resourceStore becomes the primary store.
            if (scheduler.isVertical || scheduler.isHorizontal && !scheduler.store) {
                scheduler.resourceStore = me.resourceStore;
                scheduler.eventStore = me.eventStore;
            }

            // Features self initialize if not already initialized.
            // This must be done after the _crudManager is assigned because it may access this.crudManager
            // to get its eventStore.
            const { dependencies, timeRanges, resourceTimeRanges } = scheduler.features;

            // Special handling of dependency store
            if (dependencies) {
                me.dependencyStore = dependencies.dependencyStore;
            }

            if (timeRanges) {
                me.addCrudStore(timeRanges.store);
                me._timeRangesStore = timeRanges.store;
            }

            if (resourceTimeRanges) {
                me.addCrudStore(resourceTimeRanges.store);
                me._resourceTimeRangesStore = resourceTimeRanges.store;
            }
        }

        super.afterConstruct();
    }

    //region Stores

    /**
     * Returns store associated with timeRanges feature, if feature is enabled.
     * @property {Core.data.Store}
     * @readonly
     */
    get timeRangesStore() {
        return this._timeRangesStore;
    }

    // Adds configured scheduler stores to the store collection ensuring correct order
    // unless they're already registered.
    addFeaturedStore(store) {
        this.addPrioritizedStore(store);
    }

    static getEventStoreInfo(eventStore, config) {
        if (!(eventStore instanceof EventStore)) {
            if (typeof eventStore === 'string') {
                eventStore = Store.getStore(eventStore);
            }
            else {
                eventStore = eventStore.store;
            }
        }
        let result          = {},
            assignmentStore = config.assignmentStore,
            resourceStore   = config.resourceStore,
            dependencyStore = config.dependencyStore;

        !assignmentStore && (result.assignmentStore = eventStore.assignmentStore);
        !resourceStore && (result.resourceStore = eventStore.resourceStore);
        !dependencyStore && (result.dependencyStore = eventStore.dependencyStore);

        return result;
    }

    /**
     * Get/set the resource store bound to the CRUD manager.
     * @property {Scheduler.data.ResourceStore}
     */
    get resourceStore() {
        return this._resourceStore && this._resourceStore.store;
    }

    set resourceStore(store) {
        const me = this;

        me.setFeaturedStore('_resourceStore', store, me.resourceStoreClass);

        me.eventStore.resourceStore = me._resourceStore && me._resourceStore.store;
    }

    /**
     * Get/set the event store bound to the CRUD manager.
     * @property {Scheduler.data.EventStore}
     */
    get eventStore() {
        return this._eventStore && this._eventStore.store;
    }

    set eventStore(store) {
        const me = this;

        me.setFeaturedStore('_eventStore', store, me.eventStoreClass);

        store = me._eventStore && me._eventStore.store;

        // If we're configuring, retrieve stores registered on the provided taskStore
        if (me.isConfiguring && store && me.addRelatedStores !== false) {
            let extracted = CrudManager.getEventStoreInfo(store, me.initialConfig),
                assignmentStore = extracted.assignmentStore,
                resourceStore = extracted.resourceStore,
                dependencyStore = extracted.dependencyStore;

            if (assignmentStore) {
                me.assignmentStore = assignmentStore;
            }
            if (resourceStore) {
                me.resourceStore = resourceStore;
            }
            if (dependencyStore) {
                me.dependencyStore = dependencyStore;
            }
        }
    }

    /**
     * Get/set the assignment store bound to the CRUD manager.
     * @property {Scheduler.data.AssignmentStore}
     */
    get assignmentStore() {
        return this._assignmentStore && this._assignmentStore.store;
    }

    set assignmentStore(store) {
        this.setFeaturedStore('_assignmentStore', store, this.assignmentStoreClass);
    }

    /**
     * Get/set the dependency store bound to the CRUD manager.
     * @property {Scheduler.data.DependencyStore}
     */
    get dependencyStore() {
        return this._dependencyStore && this._dependencyStore.store;
    }

    set dependencyStore(store) {
        this.setFeaturedStore('_dependencyStore', store, this.dependencyStoreClass);
    }

    setFeaturedStore(property, store, storeClass) {
        const me = this,
            oldStore = me[property];

        store = Store.getStore(store, store && store.storeClass || storeClass);

        if (oldStore) {
            me.removeStore(oldStore);
        }

        me[property] = store && { store } || null;

        me.addFeaturedStore(me[property]);

        return me[property];
    }

    //endregion
};
